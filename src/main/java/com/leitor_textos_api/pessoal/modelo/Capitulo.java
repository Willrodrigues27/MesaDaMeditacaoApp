package com.leitor_textos_api.pessoal.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Capitulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tituloCapitulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;
}
