package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
}
