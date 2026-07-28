package com.leitor_textos_api.pessoal.modelo;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_LEITOR;

    // 🔥 Lista de livros que este usuário tem permissão para ler
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_livros_permitidos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "livro_id")
    )
    private Set<Livro> livrosPermitidos = new HashSet<>();

    public Usuario() {}

    public Usuario(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Set<Livro> getLivrosPermitidos() { return livrosPermitidos; }
    public void setLivrosPermitidos(Set<Livro> livrosPermitidos) { this.livrosPermitidos = livrosPermitidos; }
}