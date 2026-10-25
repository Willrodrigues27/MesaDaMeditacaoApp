package com.leitor_textos_api.app.dto;

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
    private String introducao;
    private String textoBruto;
    private Map<String, Object> metadados;
    private List<LinhaCifraDTO> composicao;
}