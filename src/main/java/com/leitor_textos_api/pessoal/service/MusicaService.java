package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.dto.MusicaDTO;
import com.leitor_textos_api.pessoal.modelo.Musica;
import com.leitor_textos_api.pessoal.repository.MusicaRepository;
import com.leitor_textos_api.pessoal.util.CifraParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository;

    public Musica salvarMusica(MusicaDTO dto) {
        Musica musica = new Musica();
        musica.setTitulo(dto.getTitulo());
        musica.setSecaoId(dto.getSecaoId());
        musica.setMetadados(dto.getMetadados());

        // Se enviou texto colado, converte automaticamente em linhas/cifras
        if (dto.getTextoBruto() != null && !dto.getTextoBruto().isBlank()) {
            musica.setComposicao(CifraParser.converterTextoParaComposicao(dto.getTextoBruto()));
        } else {
            musica.setComposicao(dto.getComposicao());
        }

        return musicaRepository.save(musica);
    }

    public List<Musica> listarTodas() {
        return musicaRepository.findAll();
    }

    public List<Musica> listarPorSecao(Long secaoId) {
        return musicaRepository.findBySecaoIdOrderByTituloAsc(secaoId);
    }
}