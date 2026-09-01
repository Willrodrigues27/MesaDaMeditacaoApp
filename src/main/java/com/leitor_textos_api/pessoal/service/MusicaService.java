package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.dto.MusicaDTO;
import com.leitor_textos_api.pessoal.modelo.Musica;
import com.leitor_textos_api.pessoal.repository.MusicaRepository;
import com.leitor_textos_api.pessoal.repository.local.MusicaLocalRepository;
import com.leitor_textos_api.pessoal.util.CifraParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository; // Neon DB

    @Autowired(required = false)
    private MusicaLocalRepository musicaLocalRepository; // db_pessoal Local

    public Musica salvarMusica(MusicaDTO dto) {
        Musica musica = new Musica();
        musica.setTitulo(dto.getTitulo());
        musica.setIntroducao(dto.getIntroducao());
        musica.setSecaoId(dto.getSecaoId());
        musica.setMetadados(dto.getMetadados());

        if (dto.getTextoBruto() != null && !dto.getTextoBruto().isBlank()) {
            musica.setComposicao(CifraParser.converterTextoParaComposicao(dto.getTextoBruto()));
        } else {
            musica.setComposicao(dto.getComposicao());
        }

        Musica salvaNeon = musicaRepository.save(musica);
        salvarNoLocalSeguro(salvaNeon);

        return salvaNeon;
    }

    public List<Musica> listarTodas() {
        return musicaRepository.findAll();
    }

    public List<Musica> listarPorSecao(Long secaoId) {
        return musicaRepository.findBySecaoIdOrderByTituloAsc(secaoId);
    }

    public Musica atualizarMusica(Long id, MusicaDTO dto) {
        Musica musicaExistente = musicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Música não encontrada"));

        musicaExistente.setTitulo(dto.getTitulo());
        musicaExistente.setIntroducao(dto.getIntroducao());
        musicaExistente.setSecaoId(dto.getSecaoId());

        if (dto.getTextoBruto() != null && !dto.getTextoBruto().isBlank()) {
            musicaExistente.setComposicao(CifraParser.converterTextoParaComposicao(dto.getTextoBruto()));
        }

        Musica atualizadaNeon = musicaRepository.save(musicaExistente);
        salvarNoLocalSeguro(atualizadaNeon);

        return atualizadaNeon;
    }

    public void deletarMusica(Long id) {
        if (!musicaRepository.existsById(id)) {
            throw new RuntimeException("Música não encontrada");
        }
        musicaRepository.deleteById(id);

        if (musicaLocalRepository != null) {
            try {
                musicaLocalRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("Aviso [Backup Local]: Não foi possível excluir música ID " + id + ": " + e.getMessage());
            }
        }
    }

    private void salvarNoLocalSeguro(Musica musica) {
        if (musicaLocalRepository != null) {
            try {
                musicaLocalRepository.save(musica);
            } catch (Exception e) {
                System.err.println("Aviso [Backup Local]: Falha ao sincronizar música no banco pessoal: " + e.getMessage());
            }
        }
    }
}