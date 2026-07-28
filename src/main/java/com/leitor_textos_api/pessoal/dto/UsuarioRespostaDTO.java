package com.leitor_textos_api.pessoal.dto;

import com.leitor_textos_api.pessoal.modelo.Role;

public class UsuarioRespostaDTO {
    private Long id;
    private String nome;
    private String email;
    private Role role;

    public UsuarioRespostaDTO(Long id, String nome, String email, Role role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}