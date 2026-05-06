package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.service.LivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {
    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Livro> listarTodos(){
        return service.listarTodos();
    }

    @GetMapping("/secao/{nomeGenero}")
    public ResponseEntity<List<Livro>> buscarPorSecao(@PathVariable String nomeGenero) {
        List<Livro> livros = service.listarPorGenero(nomeGenero);
        return ResponseEntity.ok(livros);
    }

    @PostMapping
    public Livro criarLivro(@RequestBody Livro livro){
        return service.salvar(livro);
    }

    @GetMapping("/busca")
    public List<Livro> buscaLivros(@RequestParam String termo){
        return service.buscarPorTermo(termo);
    }

    @PostMapping("/importar")
    public Livro importarLivroComCapitulos(@RequestBody Livro livro) {
        return service.importarComCapitulos(livro);
    }

    @GetMapping("/{id}/capitulos")
    public List<Capitulo> listarCapitulosDoLivro(@PathVariable Long id) {
        return service.listarCapitulos(id);
    }

    @GetMapping("/teste")
    public String teste(){
        return "Servidor está ouvindo!";
    }
}