package com.leitor_textos_api.app.service;

import com.leitor_textos_api.app.modelo.Capitulo;
import com.leitor_textos_api.app.repository.CapituloRepository;
import com.leitor_textos_api.app.repository.local.CapituloLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CapituloService {

    @Autowired
    private CapituloRepository repository; // Neon DB

    @Autowired(required = false)
    private CapituloLocalRepository repositoryLocal; // db_pessoal Local

    public List<Capitulo> buscarTodos(){
        return repository.findAll();
    }

    public Optional<Capitulo> buscarPorId(Long id){
        return repository.findById(id);
    }

    public Capitulo salvar(Capitulo capitulo){
        if (capitulo.getTituloCapitulo() == null || capitulo.getTituloCapitulo().isEmpty()){
            throw new RuntimeException("O capítulo precisa de um título!");
        }
        Capitulo salvoNeon = repository.save(capitulo);

        // Réplica automática no banco local
        salvarNoLocalSeguro(salvoNeon);

        return salvoNeon;
    }

    public Capitulo atualizar(Long id, Capitulo capituloAtualizado){
        Capitulo atualizado = repository.findById(id).map(capitulo -> {
            capitulo.setTituloCapitulo(capituloAtualizado.getTituloCapitulo());
            capitulo.setConteudo(capituloAtualizado.getConteudo());

            if (capituloAtualizado.getNumeroCapitulo() != null && capituloAtualizado.getNumeroCapitulo() > 0) {
                capitulo.setNumeroCapitulo(capituloAtualizado.getNumeroCapitulo());
            }

            if (capituloAtualizado.getLivro() != null) {
                capitulo.setLivro(capituloAtualizado.getLivro());
            }

            return repository.save(capitulo);
        }).orElseThrow(() -> new RuntimeException("Capítulo não encontrado!"));

        salvarNoLocalSeguro(atualizado);
        return atualizado;
    }

    public void deletar(Long id){
        repository.deleteById(id);
        if (repositoryLocal != null) {
            try {
                repositoryLocal.deleteById(id);
            } catch (Exception e) {
                System.err.println("Aviso [Backup Local]: Não foi possível excluir capítulo ID " + id + ": " + e.getMessage());
            }
        }
    }

    private void salvarNoLocalSeguro(Capitulo capitulo) {
        if (repositoryLocal != null) {
            try {
                repositoryLocal.save(capitulo);
            } catch (Exception e) {
                System.err.println("Aviso [Backup Local]: Falha ao sincronizar capítulo no banco pessoal: " + e.getMessage());
            }
        }
    }
}