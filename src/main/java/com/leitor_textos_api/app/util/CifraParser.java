package com.leitor_textos_api.app.util;

import com.leitor_textos_api.app.dto.LinhaCifraDTO;
import com.leitor_textos_api.app.dto.NotaPosicaoDTO;

import java.util.ArrayList;
import java.util.List;

public class CifraParser {

    public static List<LinhaCifraDTO> converterTextoParaComposicao(String textoBruto) {
        List<LinhaCifraDTO> composicao = new ArrayList<>();
        if (textoBruto == null || textoBruto.isBlank()) return composicao;

        String[] linhas = textoBruto.split("\r?\n");
        List<NotaPosicaoDTO> cifrasPendentes = new ArrayList<>();

        for (String linha : linhas) {
            if (ehLinhaDeCifra(linha)) {
                cifrasPendentes = extrairNotasEPosicoes(linha);
            } else {
                composicao.add(new LinhaCifraDTO(linha, cifrasPendentes));
                cifrasPendentes = new ArrayList<>();
            }
        }

        if (!cifrasPendentes.isEmpty()) {
            composicao.add(new LinhaCifraDTO("", cifrasPendentes));
        }

        return composicao;
    }

    private static boolean ehLinhaDeCifra(String linha) {
        if (linha.trim().isEmpty()) return false;
        return linha.matches(".*\\b[A-G](b|#)?(m|maj|min|dim|aug|sus)?\\d*\\b.*");
    }

    private static List<NotaPosicaoDTO> extrairNotasEPosicoes(String linha) {
        List<NotaPosicaoDTO> lista = new ArrayList<>();
        int i = 0;

        while (i < linha.length()) {
            if (linha.charAt(i) != ' ') {
                int inicio = i;
                while (i < linha.length() && linha.charAt(i) != ' ') {
                    i++;
                }
                String nota = linha.substring(inicio, i);
                lista.add(new NotaPosicaoDTO(nota, inicio));
            } else {
                i++;
            }
        }
        return lista;
    }
}