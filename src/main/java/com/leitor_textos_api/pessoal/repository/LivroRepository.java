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

    List<Livro> findByTituloContainingIgnoreCaseOrderByTituloAsc(String titulo);

    List<Livro> findBySecaoIdOrderByTituloAsc(Long secaoId);

    List<Livro> findAllByOrderByTituloAsc();

    @Query("SELECT l FROM Livro l WHERE l.secaoId = :secaoId ORDER BY l.titulo ASC")
    List<Livro> buscarPorSecaoOrdenado(@Param("secaoId") Long secaoId);

    List<Livro> findByTituloContainingIgnoreCase(String titulo);
}