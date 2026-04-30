package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {
    @Autowired
    private LivroRepository repository;

    @GetMapping
    public List<Livro> listarTodos(){
        return repository.findAll();
    }

    @PostMapping
    public Livro criarLivro(@RequestBody Livro livro){
        return repository.save(livro);
    }
}
