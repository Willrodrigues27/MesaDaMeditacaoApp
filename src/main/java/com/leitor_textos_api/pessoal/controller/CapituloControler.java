package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.service.CapituloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;

import java.util.List;

@RestController
@RequestMapping("/api/capitulos")
public class CapituloControler {

    @Autowired
    private CapituloService service;

    // Retorna ao menu
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

    // Salva um novo capítulo ou texto
    @PostMapping
    public Capitulo salvar(@RequestBody Capitulo capitulo) {
        return service.salvar(capitulo);
    }
}