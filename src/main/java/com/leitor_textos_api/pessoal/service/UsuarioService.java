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
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenha(dto.getSenha());
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
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(dto.getEmail());

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getSenha().equals(dto.getSenha())) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }

        Usuario u = usuarioOpt.get();
        return new UsuarioRespostaDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole());
    }

    // Validação de Acesso que considera se o livro é de seção pública
    public boolean validarAcessoAoLivro(Long usuarioId, Long livroId) {
        if (livroId == null) {
            return false;
        }

        // 1. Busca o livro para descobrir a qual seção ele pertence
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isPresent()) {
            Long secaoId = livroOpt.get().getSecaoId();

            // 2. SE FOR SEÇÃO PÚBLICA (0, 1, 5...): Liberado para qualquer leitor!
            boolean ehSecaoPublica = secaoId == null || secaoId == 0L || secaoId == 1L || secaoId == 5L;
            if (ehSecaoPublica) {
                return true;
            }
        }

        // 3. SE FOR ADMIN: Acesso liberado para tudo
        if (usuarioId != null && usuarioId > 0 && ehAdministrador(usuarioId)) {
            return true;
        }

        // 4. SE FOR SEÇÃO RESTRITA: Valida se o leitor recebeu permissão explícita no banco
        if (usuarioId == null || usuarioId <= 0) {
            return false;
        }

        return usuarioRepository.possuiAcessoAoLivro(usuarioId, livroId);
    }

    // 4. Verifica se o usuário é Administrador
    public boolean ehAdministrador(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) return false;
        return usuarioRepository.findById(usuarioId)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    // 5. Conceder permissão de um livro para um usuário
    @Transactional
    public void concederAcessoAoLivro(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        usuario.adicionarLivroPermitido(livro);
        usuarioRepository.save(usuario);
    }

    // 6. Revogar permissão de um livro de um usuário
    @Transactional
    public void revogarAcessoAoLivro(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        usuario.removerLivroPermitido(livro);
        usuarioRepository.save(usuario);
    }

    // 7. Listar todos os leitores
    public List<UsuarioRespostaDTO> listarLeitores() {
        return usuarioRepository.findByRole(Role.ROLE_LEITOR)
                .stream()
                .map(u -> new UsuarioRespostaDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole()))
                .toList();
    }
}