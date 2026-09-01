package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.repository.local.LivroLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LivroService {

    private final LivroRepository repository;
    private final CapituloRepository capituloRepository;
    private final UsuarioService usuarioService;

    @Autowired(required = false)
    private LivroLocalRepository repositoryLocal;

    public LivroService(LivroRepository repository,
                        CapituloRepository capituloRepository,
                        UsuarioService usuarioService) {
        this.repository = repository;
        this.capituloRepository = capituloRepository;
        this.usuarioService = usuarioService;
    }

    public List<Livro> listarPorGenero(String genero) {
        return repository.findByGeneroIgnoreCase(genero);
    }

    public List<Livro> listarTodos(){
        List<Livro> livros = repository.findAll();
        ordenarLivrosManualmente(livros);
        return livros;
    }

    public List<Livro> buscarPorTermo(String termo) {
        return repository.findByTituloContainingIgnoreCase(termo);
    }

    public Livro salvar(Livro livro) {
        if (livro.getSecaoId() != null) {
            List<Long> secoesRestritas = List.of(3L);
            if (secoesRestritas.contains(livro.getSecaoId())) {
                livro.setRestrito(true);
            }
        }
        Livro salvoNeon = repository.save(livro);
        salvarNoLocalSeguro(salvoNeon);
        return salvoNeon;
    }

    public Livro importarComCapitulos(Livro livro) {
        if (livro.getSecaoId() != null) {
            List<Long> secoesRestritas = List.of(3L);
            if (secoesRestritas.contains(livro.getSecaoId())) {
                livro.setRestrito(true);
            }
        }

        if (livro.getCapitulos() != null && !livro.getCapitulos().isEmpty()) {
            for (int i = 0; i < livro.getCapitulos().size(); i++) {
                Capitulo cap = livro.getCapitulos().get(i);
                cap.setLivro(livro);
                if (cap.getNumeroCapitulo() == null || cap.getNumeroCapitulo() == 0) {
                    cap.setNumeroCapitulo(i + 1);
                }
            }
        }
        Livro salvoNeon = repository.save(livro);
        salvarNoLocalSeguro(salvoNeon);
        return salvoNeon;
    }

    public List<Capitulo> listarCapitulos(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com o ID: " + id);
        }
        return capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(id);
    }

    public List<Livro> listarPorSecao(Long secaoId) {
        List<Livro> livros = repository.findBySecaoId(secaoId);
        ordenarLivrosManualmente(livros);
        return livros;
    }

    public List<Livro> listarPorSecaoEUsuario(Long secaoId, Long usuarioId) {
        if (usuarioId != null && usuarioId > 0 && usuarioService.ehAdministrador(usuarioId)) {
            List<Livro> todosOsLivros = repository.findBySecaoId(secaoId);
            todosOsLivros.forEach(livro -> livro.setRestrito(false));
            ordenarLivrosManualmente(todosOsLivros);
            return todosOsLivros;
        }

        boolean ehSecaoRestrita = secaoId != null && secaoId == 3L;

        if (!ehSecaoRestrita) {
            List<Livro> livrosPublicos = repository.findBySecaoId(secaoId);
            livrosPublicos.forEach(livro -> livro.setRestrito(false));
            ordenarLivrosManualmente(livrosPublicos);
            return livrosPublicos;
        }

        List<Livro> livrosDaSecao = repository.findBySecaoId(secaoId);

        List<Livro> livrosVisiveis = livrosDaSecao.stream()
                .filter(livro -> {
                    if (usuarioId == null || usuarioId <= 0) {
                        return false;
                    }
                    return usuarioService.validarAcessoAoLivro(usuarioId, livro.getId());
                })
                .collect(Collectors.toList());

        ordenarLivrosManualmente(livrosVisiveis);
        return livrosVisiveis;
    }

    public List<Capitulo> realizarPesquisaAvancada(String termo, Long usuarioId) {
        if (termo == null || termo.trim().isEmpty()) {
            return List.of();
        }

        List<Capitulo> todosOsCapitulos = capituloRepository
                .findByConteudoContainingIgnoreCaseOrTituloCapituloContainingIgnoreCase(termo, termo);

        if (usuarioId != null && usuarioId > 0 && usuarioService.ehAdministrador(usuarioId)) {
            return todosOsCapitulos;
        }

        return todosOsCapitulos.stream()
                .filter(capitulo -> {
                    if (capitulo.getLivro() == null) {
                        return false;
                    }

                    Livro livro = capitulo.getLivro();
                    boolean ehSecaoRestrita = livro.getSecaoId() != null && livro.getSecaoId() == 3L;
                    if (!ehSecaoRestrita) {
                        return true;
                    }

                    if (usuarioId == null || usuarioId <= 0) {
                        return false;
                    }
                    return usuarioService.validarAcessoAoLivro(usuarioId, livro.getId());
                })
                .collect(Collectors.toList());
    }

    private void salvarNoLocalSeguro(Livro livro) {
        if (repositoryLocal != null) {
            try {
                repositoryLocal.save(livro);
            } catch (Exception e) {
                System.err.println("Aviso [Backup Local]: Falha ao sincronizar livro no banco pessoal: " + e.getMessage());
            }
        }
    }

    private void ordenarLivrosManualmente(List<Livro> livros) {
        livros.sort((l1, l2) -> {
            Integer num1 = extrairNumeroDoTitulo(l1.getTitulo());
            Integer num2 = extrairNumeroDoTitulo(l2.getTitulo());

            if (num1 != null && num2 != null) {
                int comp = num1.compareTo(num2);
                if (comp != 0) return comp;
            } else if (num1 != null) {
                return -1;
            } else if (num2 != null) {
                return 1;
            }

            return l1.getTitulo().compareToIgnoreCase(l2.getTitulo());
        });
    }

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