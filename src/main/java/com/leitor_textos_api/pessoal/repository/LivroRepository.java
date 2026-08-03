package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Livro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Busca padrão do Spring Data JPA
    List<Livro> findBySecaoId(Long secaoId);

    List<Livro> findByGeneroIgnoreCase(String genero);
    List<Livro> findByTituloContainingIgnoreCaseOrderByTituloAsc(String titulo);
    List<Livro> findAllByOrderByTituloAsc();
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    List<Livro> findByTituloContainingIgnoreCaseOrCapitulosConteudoContainingIgnoreCase(String titulo, String conteudo);
    List<Livro> findByRestritoTrue();

    @Transactional
    @Modifying
    @Query("UPDATE Livro l SET l.restrito = false")
    void liberarTodosLivros();
}