package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.service.CapituloService;
import com.leitor_textos_api.pessoal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/capitulos")
public class CapituloController {

    @Autowired
    private CapituloRepository capituloRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private CapituloService service;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/pesquisa")
    public ResponseEntity<List<Capitulo>> pesquisarConteudo(@RequestParam("termo") String termo) {
        List<Capitulo> paginasEncontradas = capituloRepository
                .findByConteudoContainingIgnoreCaseOrTituloCapituloContainingIgnoreCaseOrderByNumeroCapituloAsc(termo, termo);
        return ResponseEntity.ok(paginasEncontradas);
    }

    @PostMapping("/livro/{livroId}")
    public ResponseEntity<?> adicionarCapitulo(
            @PathVariable Long livroId,
            @RequestBody Capitulo capitulo,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {

        if (usuarioId == null || !usuarioService.ehAdministrador(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem cadastrar novos capítulos.");
        }

        return livroRepository.findById(livroId).map(livro -> {
            capitulo.setLivro(livro);
            return ResponseEntity.ok((Object) capituloRepository.save(capitulo));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Listar capítulos por livro (Valida se o usuário tem acesso ao livro ou é Admin)
    @GetMapping("/livro/{livroId}")
    public ResponseEntity<?> listarPorLivro(
            @PathVariable Long livroId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não identificado no cabeçalho (X-Usuario-Id).");
        }

        // Valida se o usuário tem o livro liberado ou se é Admin
        boolean possuiAcesso = usuarioService.validarAcessoAoLivro(usuarioId, livroId);

        if (!possuiAcesso) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso Negado: Você não possui permissão para ler os capítulos deste livro.");
        }

        List<Capitulo> capitulos = capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(livroId);
        return ResponseEntity.ok(capitulos);
    }

    // Retorna ao menu (Geral)
    @GetMapping
    public List<Capitulo> listarTodos(){
        return service.buscarTodos();
    }

    // Busca um capítulo pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Capitulo> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔒 Salva um novo capítulo geral (Restrito a ADMIN)
    @PostMapping
    public ResponseEntity<?> salvar(
            @RequestBody Capitulo capitulo,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {

        if (usuarioId == null || !usuarioService.ehAdministrador(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem salvar novos textos.");
        }

        Capitulo salvo = service.salvar(capitulo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // Metodo para editar (Restrito a ADMIN se preferir, ou mantido)
    @PutMapping("/{id}")
    public ResponseEntity<Capitulo> atualizar(@PathVariable Long id, @RequestBody Capitulo capitulo){
        try {
            return ResponseEntity.ok(service.atualizar(id, capitulo));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // Metodo DELETE (Restrito a ADMIN se preferir, ou mantido)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}