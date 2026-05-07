package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class LivroService {
    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public List<Livro> listarPorGenero(String genero) {
        return repository.findByGeneroIgnoreCase(genero);
    }

    public List<Livro> listarTodos(){
        return repository.findAll();
    }

    public Livro salvar(Livro livro) {
        return repository.save(livro);
    }

    public List<Livro> buscarPorTermo(String termo) {
        return repository.findByTituloContainingIgnoreCase(termo);
    }

    public Livro importarComCapitulos(Livro livro) {
        if (livro.getCapitulos() != null) {
            livro.getCapitulos().forEach(cap -> cap.setLivro(livro));
        }
        return repository.save(livro);
    }

    public List<Capitulo> listarCapitulos(Long id) {
        Livro livro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        return livro.getCapitulos();
    }
    public List<Livro> listarPorSecao(Long secaoId) {
        return repository.findBySecaoId(secaoId);
    }
}