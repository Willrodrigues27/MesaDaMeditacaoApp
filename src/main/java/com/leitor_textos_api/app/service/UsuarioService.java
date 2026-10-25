package com.leitor_textos_api.app.service;

import com.leitor_textos_api.app.dto.LoginDTO;
import com.leitor_textos_api.app.dto.RegistroDTO;
import com.leitor_textos_api.app.dto.UsuarioRespostaDTO;
import com.leitor_textos_api.app.modelo.Livro;
import com.leitor_textos_api.app.modelo.Role;
import com.leitor_textos_api.app.modelo.Usuario;
import com.leitor_textos_api.app.repository.LivroRepository;
import com.leitor_textos_api.app.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. Cadastrar novo leitor (criptografando a senha)
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

        // Criptografa a senha antes de salvar
        String senhaPura = dto.getSenha() != null ? dto.getSenha().trim() : "";
        novoUsuario.setSenha(passwordEncoder.encode(senhaPura));
        novoUsuario.setRole(Role.ROLE_LEITOR);

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return new UsuarioRespostaDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }

    // 2. Autenticar usuário (Suporta BCrypt e Texto Puro)
    public UsuarioRespostaDTO autenticar(LoginDTO dto) {
        String emailFormatado = dto.getEmail() != null ? dto.getEmail().toLowerCase().trim() : "";
        String senhaFormatada = dto.getSenha() != null ? dto.getSenha().trim() : "";

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(emailFormatado);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        Usuario usuario = usuarioOpt.get();

        // Valida a senha usando BCrypt (se for hash) ou texto puro
        boolean senhaValida = false;
        if (usuario.getSenha().startsWith("$2a$") || usuario.getSenha().startsWith("$2b$")) {
            senhaValida = passwordEncoder.matches(senhaFormatada, usuario.getSenha());
        } else {
            senhaValida = usuario.getSenha().equals(senhaFormatada);
        }

        if (!senhaValida) {
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
            Livro livro = livroOpt.get();
            Long secaoId = livro.getSecaoId();

            boolean ehSecaoPublica = secaoId == null || secaoId != 3L;
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
    public List<Long> obterIdsLivrosPermitidos(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            return List.of();
        }
        return usuarioRepository.findLivrosPermitidosIdsByUsuarioId(usuarioId);
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

    // 7. Listar todos os usuários (leitores e administradores)
    public List<UsuarioRespostaDTO> listarLeitores() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioRespostaDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole()))
                .toList();
    }

    @Transactional
    public void liberarTodosLivrosParaUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Livro> livrosRestritos = livroRepository.findByRestritoTrue();

        for (Livro livro : livrosRestritos) {
            if (!usuario.getLivrosPermitidos().contains(livro)) {
                usuario.getLivrosPermitidos().add(livro);
            }
        }

        usuarioRepository.save(usuario);
    }
}