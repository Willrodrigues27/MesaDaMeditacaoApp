package com.leitor_textos_api.app.repository.local;

import com.leitor_textos_api.app.modelo.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicaLocalRepository extends JpaRepository<Musica, Long> {
}