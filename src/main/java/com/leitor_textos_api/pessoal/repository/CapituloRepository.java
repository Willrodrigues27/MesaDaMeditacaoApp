package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapituloRepository extends JpaRepository<Capitulo, Long> {
}
// Aqui já salva e busca automaticamente