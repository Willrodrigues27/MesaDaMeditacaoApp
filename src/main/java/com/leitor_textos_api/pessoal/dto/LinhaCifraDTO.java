package com.leitor_textos_api.pessoal.dto;

import java.util.List;

public class LinhaCifraDTO {
    private String letra;
    private List<NotaPosicaoDTO> cifras;

    public LinhaCifraDTO() {}

    public LinhaCifraDTO(String letra, List<NotaPosicaoDTO> cifras) {
        this.letra = letra;
        this.cifras = cifras;
    }

    public String getLetra() { return letra; }
    public void setLetra(String letra) { this.letra = letra; }

    public List<NotaPosicaoDTO> getCifras() { return cifras; }
    public void setCifras(List<NotaPosicaoDTO> cifras) { this.cifras = cifras; }
}