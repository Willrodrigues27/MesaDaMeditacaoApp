package com.leitor_textos_api.pessoal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicaDTO {
    private String titulo;
    private Long secaoId;
    private String textoBruto;
    private Map<String, Object> metadados;
    private List<LinhaCifraDTO> composicao;
}