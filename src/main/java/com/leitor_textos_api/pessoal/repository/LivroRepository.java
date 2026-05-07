package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByGeneroIgnoreCase(String genero);

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT l FROM Livro l WHERE LOWER(l.genero) = LOWER(:genero)")
    List<Livro> buscarPorGeneroCustom(@Param("genero") String genero);

    @Query("SELECT l FROM Livro l WHERE l.secaoId = :secaoId")
    List<Livro> findBySecaoId(@Param("secaoId") Long secaoId);

    Long id(Long id);
}

