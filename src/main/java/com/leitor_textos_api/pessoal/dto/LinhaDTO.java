package com.leitor_textos_api.pessoal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinhaDTO {
    private List<LinhaCifraDTO> composicao;
}