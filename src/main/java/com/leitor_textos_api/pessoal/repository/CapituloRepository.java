package com.leitor_textos_api.pessoal.repository;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CapituloRepository extends JpaRepository<Capitulo, Long> {


    List<Capitulo> findByLivroIdOrderByNumeroCapituloAscIdAsc(Long livroId);


    List<Capitulo> findByConteudoContainingIgnoreCaseOrTituloCapituloContainingIgnoreCaseOrderByNumeroCapituloAscIdAsc(
            String conteudo,
            String tituloCapitulo
    );
         // Adicione esta linha:
        List<Capitulo> findByConteudoContainingIgnoreCaseOrTituloCapituloContainingIgnoreCase(String conteudo, String tituloCapitulo);

        @Query("SELECT c FROM Capitulo c WHERE c.livro.id IN :livrosPermitidos AND (LOWER(c.conteudo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(c.tituloCapitulo) LIKE LOWER(CONCAT('%', :termo, '%'))) ORDER BY c.numeroCapitulo ASC, c.id ASC")
    List<Capitulo> pesquisarNosLivrosPermitidos(@Param("termo") String termo, @Param("livrosPermitidos") List<Long> livrosPermitidos);
}