package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.dto.LoginDTO;
import com.leitor_textos_api.pessoal.dto.RegistroDTO;
import com.leitor_textos_api.pessoal.dto.UsuarioRespostaDTO;
import com.leitor_textos_api.pessoal.repository.UsuarioRepository;
import com.leitor_textos_api.pessoal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody RegistroDTO dto) {
        try {
            UsuarioRespostaDTO resposta = usuarioService.cadastrarLeitor(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            UsuarioRespostaDTO resposta = usuarioService.autenticar(dto);
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{usuarioId}/livros-permitidos")
    public ResponseEntity<List<Long>> listarLivrosLiberados(@PathVariable Long usuarioId) {
        List<Long> idsLivros = usuarioRepository.findLivrosPermitidosIdsByUsuarioId(usuarioId);
        return ResponseEntity.ok(idsLivros);
    }

    // 🔒 POST: Liberar acesso do livro ao usuário (Apenas Admin)
    @PostMapping("/{usuarioId}/liberar-livro/{livroId}")
    public ResponseEntity<String> liberarLivro(
            @PathVariable Long usuarioId,
            @PathVariable Long livroId,
            @RequestHeader(value = "Usuario-Id", required = false) Long solicitanteId) {

        if (!usuarioService.ehAdministrador(solicitanteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado. Apenas administradores podem liberar livros.");
        }

        try {
            usuarioService.concederAcessoAoLivro(usuarioId, livroId);
            return ResponseEntity.ok("Acesso ao livro liberado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 🔒 DELETE: Bloquear/Revogar acesso do livro ao usuário (Apenas Admin)
    @DeleteMapping("/{usuarioId}/revogar-livro/{livroId}")
    public ResponseEntity<String> revogarLivro(
            @PathVariable Long usuarioId,
            @PathVariable Long livroId,
            @RequestHeader(value = "Usuario-Id", required = false) Long solicitanteId) {

        if (!usuarioService.ehAdministrador(solicitanteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado. Apenas administradores podem revogar livros.");
        }

        try {
            usuarioService.revogarAcessoAoLivro(usuarioId, livroId);
            return ResponseEntity.ok("Acesso ao livro revogado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/leitores")
    public ResponseEntity<List<UsuarioRespostaDTO>> listarLeitores() {
        List<UsuarioRespostaDTO> leitores = usuarioService.listarLeitores();
        return ResponseEntity.ok(leitores);
    }
}