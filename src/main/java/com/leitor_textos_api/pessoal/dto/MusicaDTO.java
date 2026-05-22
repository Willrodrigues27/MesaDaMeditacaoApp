package com.leitor_textos_api.pessoal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;
import com.leitor_textos_api.pessoal.dto.LinhaCifraDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicaDTO {
    private String titulo;
    private Map<String, Object> metadados;
    private List<LinhaCifraDTO> composicao;
}