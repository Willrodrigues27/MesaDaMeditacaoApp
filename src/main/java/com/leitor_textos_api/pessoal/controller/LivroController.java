package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
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

    @GetMapping("/busca")
    public List<Livro> buscaLivros(@RequestParam String termo){
        return repository.findByTituloContainingIgnoreCase(termo);
    }

    @PostMapping("/importar")
    public Livro importarLivroComCapitulos(@RequestBody Livro livro) {
        // Vincula cada capítulo ao livro
        if (livro.getCapitulos() != null) {
            livro.getCapitulos().forEach(cap -> cap.setLivro(livro));
        }
        return repository.save(livro);
    }

    @GetMapping("/{id}/capitulos")
    public List<Capitulo> listarCapitulosDoLivro(@PathVariable Long id) {
        Livro livro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        return livro.getCapitulos();
    }
}
