package com.leitor_textos_api.app.dto;

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