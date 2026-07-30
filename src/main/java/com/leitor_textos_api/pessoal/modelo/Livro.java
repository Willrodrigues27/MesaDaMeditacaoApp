package com.leitor_textos_api.pessoal.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String genero;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // 🟢 Permite nulo temporariamente no banco ou trata o valor padrão caso venha nulo
    @Column(name = "restrito", nullable = false)
    @JsonProperty("restrito")
    private Boolean restrito = false;

    @JsonProperty("secao_id")
    private Long secaoId;

    @JsonManagedReference
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<Capitulo> capitulos = new ArrayList<>();

    public Livro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getRestrito() {
        return restrito != null ? restrito : false;
    }

    public void setRestrito(Boolean restrito) {
        this.restrito = restrito != null ? restrito : false;
    }

    public Long getSecaoId() {
        return secaoId;
    }

    public void setSecaoId(Long secaoId) {
        this.secaoId = secaoId;
    }

    public List<Capitulo> getCapitulos() {
        return capitulos;
    }

    public void setCapitulos(List<Capitulo> capitulos) {
        this.capitulos = capitulos;
    }

    public void adionarCapitulo(Capitulo capitulo) {
        capitulos.add(capitulo);
        capitulo.setLivro(this);
    }

    // 🟢 ESSENCIAL para o funcionamento do HashSet no relacionamento @ManyToMany
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(id, livro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}