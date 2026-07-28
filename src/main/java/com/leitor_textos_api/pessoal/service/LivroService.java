package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LivroService {

    private final LivroRepository repository;
    private final CapituloRepository capituloRepository;

    public LivroService(LivroRepository repository, CapituloRepository capituloRepository) {
        this.repository = repository;
        this.capituloRepository = capituloRepository;
    }

    public List<Livro> listarPorGenero(String genero) {
        return repository.findByGeneroIgnoreCase(genero);
    }

    // 🟢 Listar todos com ordenação natural segura no Java
    public List<Livro> listarTodos(){
        List<Livro> livros = repository.findAll();
        ordenarLivrosManualmente(livros);
        return livros;
    }

    public Livro salvar(Livro livro) {
        return repository.save(livro);
    }

    public List<Livro> buscarPorTermo(String termo) {
        return repository.findByTituloContainingIgnoreCase(termo);
    }

    public Livro importarComCapitulos(Livro livro) {
        if (livro.getCapitulos() != null && !livro.getCapitulos().isEmpty()) {
            for (int i = 0; i < livro.getCapitulos().size(); i++) {
                Capitulo cap = livro.getCapitulos().get(i);
                cap.setLivro(livro);
                if (cap.getNumeroCapitulo() == null || cap.getNumeroCapitulo() == 0) {
                    cap.setNumeroCapitulo(i + 1);
                }
            }
        }
        return repository.save(livro);
    }

    public List<Capitulo> listarCapitulos(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com o ID: " + id);
        }
        return capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(id);
    }

    // 🟢 Listar por Seção usando busca padrão JPA + ordenação natural no Java
    public List<Livro> listarPorSecao(Long secaoId) {
        List<Livro> livros = repository.findBySecaoId(secaoId);
        ordenarLivrosManualmente(livros);
        return livros;
    }

    // 🛠️ Algoritmo de Ordenação Alfanumérica/Natural em memória
    private void ordenarLivrosManualmente(List<Livro> livros) {
        livros.sort((l1, l2) -> {
            Integer num1 = extrairNumeroDoTitulo(l1.getTitulo());
            Integer num2 = extrairNumeroDoTitulo(l2.getTitulo());

            // Se ambos os títulos tiverem números, ordena pelo valor numérico
            if (num1 != null && num2 != null) {
                int comp = num1.compareTo(num2);
                if (comp != 0) return comp;
            } else if (num1 != null) {
                return -1; // Título com número tem prioridade
            } else if (num2 != null) {
                return 1;
            }

            // Desempate por ordem alfabética convencional
            return l1.getTitulo().compareToIgnoreCase(l2.getTitulo());
        });
    }

    // Extrai o primeiro número do título (Ex: "ENSINO 10..." -> 10)
    private Integer extrairNumeroDoTitulo(String titulo) {
        if (titulo == null) return null;
        Matcher matcher = Pattern.compile("\\d+").matcher(titulo);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}