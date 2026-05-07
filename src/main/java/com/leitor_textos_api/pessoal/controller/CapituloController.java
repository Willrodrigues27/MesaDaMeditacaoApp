package com.leitor_textos_api.pessoal.controller;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.service.CapituloService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/livro/{livroId}")
    public ResponseEntity<Capitulo> adicionarCapitulo(@PathVariable Long livroId, @RequestBody Capitulo capitulo) {
        return livroRepository.findById(livroId).map(livro -> {
            capitulo.setLivro(livro);
            return ResponseEntity.ok(capituloRepository.save(capitulo));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/livro/{livroId}")
    public List<Capitulo> listarPorLivro(@PathVariable Long livroId) {
        return capituloRepository.findByLivroId(livroId);
    }

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

    // Metodo para editar
    @PutMapping("/{id}")
    public ResponseEntity<Capitulo> atualizar(@PathVariable Long id, @RequestBody Capitulo capitulo){
        try {
            return ResponseEntity.ok(service.atualizar(id, capitulo));
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    //Metodo DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}