package com.leitor_textos_api.pessoal.service;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CapituloService {

    @Autowired
    private CapituloRepository repository;

    //Listar capítulos
    public List<Capitulo> buscarTodos(){
        return repository.findAll();
    }

    //Buscar texto
    public Optional<Capitulo> buscarPorId(Long id){
        return repository.findById(id);
    }

    //Criar Regras (Não salvar capítulos sem título)
    public Capitulo salvar(Capitulo capitulo){
        if (capitulo.getTituloCapitulo() == null || capitulo.getTituloCapitulo().isEmpty()){
            throw new RuntimeException("O capítulo precisa de um título!");
        }
        return repository.save(capitulo);
    }

    //Atualizar capítulos
    public Capitulo atualizar(Long id, Capitulo capituloAtualizado){
        return repository.findById(id).map(capitulo -> {
            capitulo.setTituloCapitulo(capituloAtualizado.getTituloCapitulo());
            capitulo.setConteudo(capituloAtualizado.getConteudo());
            return repository.save(capitulo);
        }).orElseThrow(() -> new RuntimeException("Capítulo não encontrado!"));
    }

    //Deletar um capítulo
    public void deletar(Long id){
        repository.deleteById(id);
    }
}
