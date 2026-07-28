package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // 🔥 Query SQL nativa: direta no PostgreSQL para evitar erros de GROUP BY ou JPQL
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM usuario_livros_permitidos " +
            "WHERE usuario_id = :usuarioId AND livro_id = :livroId",
            nativeQuery = true)
    boolean possuiAcessoAoLivro(@Param("usuarioId") Long usuarioId, @Param("livroId") Long livroId);
}