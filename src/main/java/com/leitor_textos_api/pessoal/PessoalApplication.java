package com.leitor_textos_api.pessoal;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.repository.CapituloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PessoalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PessoalApplication.class, args);
	}

	@Bean
	CommandLineRunner init(CapituloRepository repository){
		return args -> {
			Capitulo c1 = new Capitulo();
			c1.setTituloCapitulo("Chapeuzinho Vermelho");
			c1.setConteudo("Era uma vez uma menina que, ao atravessar a floresta para levar doces à avó doente, desobedece a mãe e conversa com um lobo astuto. O lobo engana a menina, chega antes na casa da avó, devora-a e se disfarça para comer a menina também, sendo salvo por um caçador no final.");

			Capitulo c2 = new Capitulo();
			c2.setTituloCapitulo("Três porquinhos");
			c2.setConteudo("Era uma vez Três porquinhos irmãos saem da casa da mãe para construir suas próprias vidas. Dois, preguiçosos, fazem casas de palha e madeira. O terceiro, trabalhador, constrói uma sólida casa de tijolos. O lobo mau derruba as duas primeiras casas soprando, mas não consegue destruir a de tijolos, e os porquinhos se salvam");

			repository.save(c1);
			repository.save(c2);

			System.out.println("Dados carregados com sucesso!");
		};
	}
}
