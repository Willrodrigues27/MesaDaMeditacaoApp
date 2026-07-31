package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
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

    // Listar todos com ordenação natural
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
            List<Long> secoesRestritas = List.of(2L, 3L, 4L);
            if (secoesRestritas.contains(livro.getSecaoId())) {
                livro.setRestrito(true);
            }
        }
        return repository.save(livro);
    }

    public Livro importarComCapitulos(Livro livro) {
        if (livro.getSecaoId() != null) {
            List<Long> secoesRestritas = List.of(2L, 3L, 4L);
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
        return repository.save(livro);
    }

    public List<Capitulo> listarCapitulos(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Livro não encontrado com o ID: " + id);
        }
        return capituloRepository.findByLivroIdOrderByNumeroCapituloAsc(id);
    }

    // Listar por Seção padrão
    public List<Livro> listarPorSecao(Long secaoId) {
        List<Livro> livros = repository.findBySecaoId(secaoId);
        ordenarLivrosManualmente(livros);
        return livros;
    }

    public List<Livro> listarPorSecaoEUsuario(Long secaoId, Long usuarioId) {
        System.out.println(">>> ------------------------------------------------");
        System.out.println(">>> REQUISIÇÃO SEÇÃO: " + secaoId + " | USUARIO_ID: " + usuarioId);

        // 1. SE FOR ADMINISTRADOR: Liberdade total
        if (usuarioId != null && usuarioId > 0 && usuarioService.ehAdministrador(usuarioId)) {
            System.out.println(">>> [ACESSO ADMIN CONFIRMADO]: Exibindo TODOS os livros da seção " + secaoId);
            List<Livro> todosOsLivros = repository.findBySecaoId(secaoId);

            todosOsLivros.forEach(livro -> livro.setRestrito(false));

            ordenarLivrosManualmente(todosOsLivros);
            return todosOsLivros;
        }

        // 2. SE FOR SEÇÃO PÚBLICA: Liberada para todos
        boolean ehSecaoRestrita = secaoId != null && (secaoId == 2L || secaoId == 3L || secaoId == 4L);

        if (!ehSecaoRestrita) {
            System.out.println(">>> [ACESSO PÚBLICO]: Seção " + secaoId + " liberada para todos.");
            List<Livro> livrosPublicos = repository.findBySecaoId(secaoId);

            livrosPublicos.forEach(livro -> livro.setRestrito(false));

            ordenarLivrosManualmente(livrosPublicos);
            return livrosPublicos;
        }

        // 3. SE FOR LEITOR EM SEÇÃO RESTRITA (2, 3 ou 4)
        System.out.println(">>> [ACESSO LEITOR RESTRITO]: Validando permissões na Seção " + secaoId);
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

    // PESQUISA AVANÇADA PROTEGIDA POR USUÁRIO
    public List<Capitulo> realizarPesquisaAvancada(String termo, Long usuarioId) {
        if (termo == null || termo.trim().isEmpty()) {
            return List.of();
        }

        // Busca todos os capítulos que batem com o termo no título ou no conteúdo
        List<Capitulo> todosOsCapitulos = capituloRepository
                .findByConteudoContainingIgnoreCaseOrTituloCapituloContainingIgnoreCase(termo, termo);

        // Se for ADMIN, libera todos os resultados encontrados
        if (usuarioId != null && usuarioId > 0 && usuarioService.ehAdministrador(usuarioId)) {
            return todosOsCapitulos;
        }

        // Se for LEITOR, filtra mantendo apenas capítulos dos livros liberados
        return todosOsCapitulos.stream()
                .filter(capitulo -> {
                    if (capitulo.getLivro() == null) {
                        return false;
                    }
                    return usuarioService.validarAcessoAoLivro(usuarioId, capitulo.getLivro().getId());
                })
                .collect(Collectors.toList());
    }

    // Ordenação Alfanumérica/Natural em memória
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

    // Extrai o primeiro número do título
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