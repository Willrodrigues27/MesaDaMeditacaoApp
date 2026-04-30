package com.leitor_textos_api.pessoal.modelo;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
    private List<Capitulo> capitulos;
}