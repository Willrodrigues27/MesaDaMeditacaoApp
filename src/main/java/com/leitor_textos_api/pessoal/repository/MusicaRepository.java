package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {
    List<Musica> findBySecaoIdOrderByTituloAsc(Long secaoId);
}