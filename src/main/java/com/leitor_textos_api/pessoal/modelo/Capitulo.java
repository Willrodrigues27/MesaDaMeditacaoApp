package com.leitor_textos_api.pessoal.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Capitulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ➕ Adicionado para ordenar e identificar o número do capítulo
    @Column(name = "numero_capitulo")
    private Integer numeroCapitulo;

    @Column(name = "titulo_capitulo")
    private String tituloCapitulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    @JsonBackReference
    private Livro livro;

    public void setLivro(Livro livro){
        this.livro = livro;
    }
}