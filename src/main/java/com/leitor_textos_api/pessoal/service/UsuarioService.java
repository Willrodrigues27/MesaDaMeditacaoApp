package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.dto.LoginDTO;
import com.leitor_textos_api.pessoal.dto.RegistroDTO;
import com.leitor_textos_api.pessoal.dto.UsuarioRespostaDTO;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.modelo.Role;
import com.leitor_textos_api.pessoal.modelo.Usuario;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.repository.UsuarioRepository;
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
    public boolean validarAcessoAoLivro(Long usuarioId, Long livroId) {
        if (usuarioId == null || livroId == null) return false;

        // 1. Administrador tem acesso total
        if (ehAdministrador(usuarioId)) return true;

        // 2. Busca o livro para conferir a seção
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();

            if (livro.getSecaoId() != null) {
                Long secaoId = livro.getSecaoId();

                // 🟢 Seções totalmente liberadas para qualquer leitor: 0, 1 e 3
                List<Long> secoesLiberadas = List.of(0L, 1L, 3L);
                if (secoesLiberadas.contains(secaoId)) {
                    return true; // Libera a leitura sem precisar de registro na tabela de permissões
                }

                // ⛔ Seções Bloqueadas por padrão (ex: 2 e 4)
                List<Long> secoesBloqueadas = List.of(2L, 4L);
                if (secoesBloqueadas.contains(secaoId)) {
                    // Só libera se o admin concedeu permissão explícita individual no banco
                    return usuarioRepository.possuiAcessoAoLivro(usuarioId, livroId);
                }
            }
        }

        // Caso padrão para qualquer livro fora da lista acima
        return usuarioRepository.possuiAcessoAoLivro(usuarioId, livroId);
    }

    // 4. Verifica se o usuário é Administrador
    public boolean ehAdministrador(Long usuarioId) {
        if (usuarioId == null) return false;
        return usuarioRepository.findById(usuarioId)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }
}