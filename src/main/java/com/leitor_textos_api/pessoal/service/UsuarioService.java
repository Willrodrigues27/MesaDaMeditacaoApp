package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.dto.LoginDTO;
import com.leitor_textos_api.pessoal.dto.RegistroDTO;
import com.leitor_textos_api.pessoal.dto.UsuarioRespostaDTO;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.modelo.Role;
import com.leitor_textos_api.pessoal.modelo.Usuario;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    // 1. Cadastrar novo leitor
    public UsuarioRespostaDTO cadastrarLeitor(RegistroDTO dto) {
        String emailFormatado = dto.getEmail() != null ? dto.getEmail().toLowerCase().trim() : "";

        if (emailFormatado.isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }

        if (usuarioRepository.findByEmail(emailFormatado).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome() != null ? dto.getNome().trim() : "");
        novoUsuario.setEmail(emailFormatado);
        novoUsuario.setSenha(dto.getSenha() != null ? dto.getSenha().trim() : "");
        novoUsuario.setRole(Role.ROLE_LEITOR);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return new UsuarioRespostaDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }

    // 2. Autenticar usuário
    public UsuarioRespostaDTO autenticar(LoginDTO dto) {
        String emailFormatado = dto.getEmail() != null ? dto.getEmail().toLowerCase().trim() : "";
        String senhaFormatada = dto.getSenha() != null ? dto.getSenha().trim() : "";

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailFormatado);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getSenha().equals(senhaFormatada)) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        return new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    // 3. Validação de Acesso
    public boolean validarAcessoAoLivro(Long usuarioId, Long livroId) {
        if (livroId == null) return false;

        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isPresent()) {
            Long secaoId = livroOpt.get().getSecaoId();
            boolean ehSecaoPublica = secaoId == null || secaoId == 0L || secaoId == 1L || secaoId == 5L;
            if (ehSecaoPublica) return true;
        }

        if (usuarioId != null && usuarioId > 0 && ehAdministrador(usuarioId)) return true;

        if (usuarioId == null || usuarioId <= 0) return false;

        return usuarioRepository.possuiAcessoAoLivro(usuarioId, livroId);
    }

    // 4. Verifica se é Administrador
    public boolean ehAdministrador(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) return false;
        return usuarioRepository.findById(usuarioId)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    // 5. Conceder permissão
    @Transactional
    public void concederAcessoAoLivro(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        usuario.adicionarLivroPermitido(livro);
        usuarioRepository.save(usuario);
    }

    // 6. Revogar permissão
    @Transactional
    public void revogarAcessoAoLivro(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        usuario.removerLivroPermitido(livro);
        usuarioRepository.save(usuario);
    }

    // 7. Listar leitores
    public List<UsuarioRespostaDTO> listarLeitores() {
        return usuarioRepository.findByRole(Role.ROLE_LEITOR)
                .stream()
                .map(u -> new UsuarioRespostaDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole()))
                .toList();
    }

    @Transactional
    public void liberarTodosLivrosParaUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Livro> todosLivros = livroRepository.findAll();

        // Adiciona todos os livros à lista de livros permitidos do usuário
        for (Livro livro : todosLivros) {
            if (!usuario.getLivrosPermitidos().contains(livro)) {
                usuario.getLivrosPermitidos().add(livro);
            }
        }

        usuarioRepository.save(usuario);
    }
}