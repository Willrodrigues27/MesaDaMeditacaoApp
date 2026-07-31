package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.service.LivroService;
import com.leitor_textos_api.pessoal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    private final LivroService service;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private CapituloRepository capituloRepository;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Livro> listarTodos() {
        return service.listarTodos();
    }

    @PostMapping
    public Livro criarLivro(@RequestBody Livro livro) {
        return service.salvar(livro);
    }

    @GetMapping("/busca")
    public List<Livro> buscaLivros(@RequestParam String termo) {
        return service.buscarPorTermo(termo);
    }

    @PostMapping("/importar")
    public Livro importarLivroComCapitulos(@RequestBody Livro livro) {
        return service.importarComCapitulos(livro);
    }

    // 1. Trava nos capítulos de um livro específico
    @GetMapping("/{id}/capitulos")
    public ResponseEntity<?> listarCapitulosDoLivro(
            @PathVariable Long id,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        if (!usuarioService.validarAcessoAoLivro(usuarioId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado a este conteúdo.");
        }

        List<Capitulo> capitulos = capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(id);
        return ResponseEntity.ok(capitulos);
    }

    // 2. Trava ao buscar um único capítulo diretamente pelo ID dele (ex: resultado de busca)
    @GetMapping("/capitulo/{capituloId}")
    public ResponseEntity<?> buscarCapituloPorIdUnico(
            @PathVariable Long capituloId,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        return capituloRepository.findById(capituloId)
                .map(capitulo -> {
                    if (capitulo.getLivro() == null || !usuarioService.validarAcessoAoLivro(usuarioId, capitulo.getLivro().getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Acesso negado a este capítulo.");
                    }
                    return ResponseEntity.ok(capitulo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/secao/{id}")
    public ResponseEntity<List<Livro>> buscarLivrosPorIdDaSecao(
            @PathVariable Long id,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        List<Livro> livros = service.listarPorSecaoEUsuario(id, usuarioId);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<Livro>> buscarPorSecao(@RequestParam String genero) {
        List<Livro> livros = service.listarPorGenero(genero);
        return ResponseEntity.ok(livros);
    }

    // PESQUISA PROTEGIDA: Agora recebe o Usuario-Id no header e aciona a filtragem do service
    @GetMapping("/pesquisa")
    public ResponseEntity<List<Capitulo>> pesquisarCapitulos(
            @RequestParam("termo") String termo,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        List<Capitulo> resultados = service.realizarPesquisaAvancada(termo, usuarioId);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/restritos")
    public ResponseEntity<List<Livro>> listarLivrosRestritos() {
        List<Livro> livrosRestritos = livroRepository.findByRestritoTrue();
        return ResponseEntity.ok(livrosRestritos);
    }
}