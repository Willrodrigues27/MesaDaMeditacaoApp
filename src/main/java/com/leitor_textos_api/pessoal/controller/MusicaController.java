package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.dto.MusicaDTO;
import com.leitor_textos_api.pessoal.modelo.Musica;
import com.leitor_textos_api.pessoal.service.MusicaService;
import com.leitor_textos_api.pessoal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musicas")
@CrossOrigin(origins = "*")
public class MusicaController {

    @Autowired
    private MusicaService musicaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Musica>> listarTodas() {
        return ResponseEntity.ok(musicaService.listarTodas());
    }

    @GetMapping("/secao/{id}")
    public ResponseEntity<List<Musica>> listarPorSecao(@PathVariable Long id) {
        return ResponseEntity.ok(musicaService.listarPorSecao(id));
    }

    @PostMapping
    public ResponseEntity<?> salvar(
            @RequestBody MusicaDTO dto,
            @RequestHeader(value = "Usuario-Id", required = false) Long usuarioId,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long xUsuarioId) {

        Long idFinal = usuarioId != null ? usuarioId : xUsuarioId;

        if (idFinal == null || !usuarioService.ehAdministrador(idFinal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado: Apenas administradores podem cadastrar músicas.");
        }

        Musica salva = musicaService.salvarMusica(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }
}