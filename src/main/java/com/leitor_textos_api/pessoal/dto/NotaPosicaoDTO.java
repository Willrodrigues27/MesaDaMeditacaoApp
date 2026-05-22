package com.leitor_textos_api.pessoal.dto;

public class NotaPosicaoDTO {
    private String nota;
    private int posicao;

    public NotaPosicaoDTO() {}

    public NotaPosicaoDTO(String nota, int posicao) {
        this.nota = nota;
        this.posicao = posicao;
    }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
}