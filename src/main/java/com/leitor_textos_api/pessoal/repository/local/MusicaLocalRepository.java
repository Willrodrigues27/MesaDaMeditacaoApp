package com.leitor_textos_api.pessoal.repository.local;

import com.leitor_textos_api.pessoal.modelo.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicaLocalRepository extends JpaRepository<Musica, Long> {
}