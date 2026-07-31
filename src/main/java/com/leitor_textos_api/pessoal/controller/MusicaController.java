package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.dto.MusicaDTO;
import com.leitor_textos_api.pessoal.modelo.Musica;
import com.leitor_textos_api.pessoal.repository.MusicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    @Autowired
    private MusicaRepository repository;

    @PostMapping
    public Musica salvar(@RequestBody Musica musica) {
        return repository.save(musica);
    }

    @GetMapping
    public List<Musica> listarTodas() {
        return repository.findAll();
    }

    @GetMapping("/secao/{id}")
    public List<Musica> listarPorSecao(@PathVariable Long id) {
        return repository.findBySecaoIdOrderByTituloAsc(id);
    }

    @GetMapping("/teste")
    public MusicaDTO testarEstrutura() {
        MusicaDTO musica = new MusicaDTO();
        musica.setTitulo("Meus Céus");

        return musica;
    }
}