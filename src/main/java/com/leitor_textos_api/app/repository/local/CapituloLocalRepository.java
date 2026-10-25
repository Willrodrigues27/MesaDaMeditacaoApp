package com.leitor_textos_api.app.repository.local;

import com.leitor_textos_api.app.modelo.Capitulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapituloLocalRepository extends JpaRepository<Capitulo, Long> {
}