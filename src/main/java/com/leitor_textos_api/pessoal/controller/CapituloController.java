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
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        if (idFinal == null || !usuarioService.ehAdministrador(idFinal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem cadastrar novos capítulos.");
        }

        return livroRepository.findById(livroId).map(livro -> {
            capitulo.setLivro(livro);
            return ResponseEntity.ok((Object) capituloRepository.save(capitulo));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Listar capítulos por livro
    @GetMapping("/livro/{livroId}")
    public ResponseEntity<?> listarPorLivro(
            @PathVariable Long livroId,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        boolean possuiAcesso = usuarioService.validarAcessoAoLivro(idFinal, livroId);

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

    // Salva um novo capítulo geral (Restrito a ADMIN)
    @PostMapping
    public ResponseEntity<?> salvar(
            @RequestBody Capitulo capitulo,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        if (idFinal == null || !usuarioService.ehAdministrador(idFinal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem salvar novos textos.");
        }

        Capitulo salvo = service.salvar(capitulo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // 🟢 Método para EDITAR capítulo (Restrito a ADMIN via Header)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody Capitulo capitulo,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        if (idFinal == null || !usuarioService.ehAdministrador(idFinal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem editar textos.");
        }

        try {
            return ResponseEntity.ok(service.atualizar(id, capitulo));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // Método DELETE (Restrito a ADMIN via Header)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        if (idFinal == null || !usuarioService.ehAdministrador(idFinal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem excluir textos.");
        }

        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}