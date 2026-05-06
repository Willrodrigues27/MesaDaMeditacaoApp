package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {
    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }
    // Filtrar por gênero
    public List<Livro> listarPorGenero(String genero) {
        return repository.findByGeneroIgnoreCase(genero);
    }
    // Listar todos os livros
    public List<Livro> listarTodos(){
        return repository.findAll();
    }
    // Salvar um novo livro
    public Livro salvar(Livro livro) {
        return repository.save(livro);
    }
    // Buscar livros por termo
    public List<Livro> buscarPorTermo(String termo) {
        return repository.findByTituloContainingIgnoreCase(termo);
    }
    // Importar livro já com os capítulos
    public Livro importarComCapitulos(Livro livro) {
        if (livro.getCapitulos() != null) {
            livro.getCapitulos().forEach(cap -> cap.setLivro(livro));
        }
        return repository.save(livro);
    }
    // Listar capítulos de um livro específico
    public List<Capitulo> listarCapitulos(Long id) {
        Livro livro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        return livro.getCapitulos();
    }
}