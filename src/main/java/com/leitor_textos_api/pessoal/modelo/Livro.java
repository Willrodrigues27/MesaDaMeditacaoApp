package com.leitor_textos_api.pessoal.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String genero;
    private String descricao;

    public Long getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getGenero() {return genero;}
    public String getDescricao(){return descricao;}

    public void setId(Long id) {this.id = id;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setGenero(String genero) {this.genero = genero;}
    public void setDescricao(String descricao) {this.descricao = descricao;}

    public Livro() {}

    @JsonManagedReference
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Capitulo> capitulos = new ArrayList<>();

    public void adionarCapitulo (Capitulo capitulo){
        capitulos.add(capitulo);
        capitulo.setLivro(this);
    }
}