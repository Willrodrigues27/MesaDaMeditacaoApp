package com.leitor_textos_api.pessoal.dto;

public class LoginDTO {
    private String email;
    private String senha;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}