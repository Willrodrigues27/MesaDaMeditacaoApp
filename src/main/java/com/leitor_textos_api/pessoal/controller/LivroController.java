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

    // ⛔ 1. TRAVA NO CONTEÚDO DOS CAPÍTULOS (AGORA ORDENADO POR NUMERO_CAPITULO)
    // Valida se o usuário tem permissão para o livro antes de retornar os capítulos
    @GetMapping("/{id}/capitulos")
    public ResponseEntity<?> listarCapitulosDoLivro(
            @PathVariable Long id,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        // Valida a permissão
        if (!usuarioService.validarAcessoAoLivro(usuarioId, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado a este conteúdo.");
        }

        // 🟢 Busca os capítulos ordenados do menor para o maior (1, 2, 3... 10)
        List<Capitulo> capitulos = capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(id);
        return ResponseEntity.ok(capitulos);
    }

    // ⛔ 2. TRAVA DE SEÇÃO NA BUSCA DE LIVROS POR SEÇÃO
    @GetMapping("/secao/{id}")
    public ResponseEntity<?> buscarLivrosPorIdDaSecao(
            @PathVariable Long id,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId) {

        // Se for leitor e tentar buscar direto a seção 2, 3 ou 4, bloqueia
        List<Long> secoesBloqueadas = List.of(2L, 3L, 4L);
        if (secoesBloqueadas.contains(id) && !usuarioService.ehAdministrador(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso restrito a esta seção.");
        }

        List<Livro> livros = service.listarPorSecao(id);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<Livro>> buscarPorSecao(@RequestParam String genero) {
        List<Livro> livros = service.listarPorGenero(genero);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/pesquisa")
    public ResponseEntity<List<Livro>> pesquisarLivros(@RequestParam("termo") String termo) {
        List<Livro> resultados = livroRepository.findByTituloContainingIgnoreCaseOrCapitulosConteudoContainingIgnoreCase(termo, termo);
        return ResponseEntity.ok(resultados);
    }
}