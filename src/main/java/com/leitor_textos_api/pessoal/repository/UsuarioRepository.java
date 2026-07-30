package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT l.id FROM Usuario u JOIN u.livrosPermitidos l WHERE u.id = :usuarioId")
    List<Long> findLivrosPermitidosIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    // 🟢 ADICIONE ESTA QUERY AQUI:
    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.livrosPermitidos l WHERE u.id = :usuarioId AND l.id = :livroId")
    boolean possuiAcessoAoLivro(@Param("usuarioId") Long usuarioId, @Param("livroId") Long livroId);
}