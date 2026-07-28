package com.leitor_textos_api.pessoal;

import com.leitor_textos_api.pessoal.modelo.Capitulo;
import com.leitor_textos_api.pessoal.modelo.Livro;
import com.leitor_textos_api.pessoal.modelo.Role;
import com.leitor_textos_api.pessoal.modelo.Usuario;
import com.leitor_textos_api.pessoal.repository.LivroRepository;
import com.leitor_textos_api.pessoal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class PessoalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PessoalApplication.class, args);
	}

	@Bean
	CommandLineRunner init(LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
		return args -> {

			// 1. Criacao do Usuario Admin Fixo (se nao existir)
			String emailAdmin = "wilson@admin.com";
			if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
				Usuario admin = new Usuario();
				admin.setNome("Administrador");
				admin.setEmail(emailAdmin);
				admin.setSenha("admin123");
				admin.setRole(Role.ROLE_ADMIN);
				usuarioRepository.save(admin);
				System.out.println("✅ ADMIN CRIADO!");
			}

			// 2. Carga Inicial de Textos (Executa apenas se o banco de livros estiver vazio)
			if (livroRepository.count() == 0) {

				// --- TEXTO / LIVRO 1 (Seção 1 - Ensinos Bíblicos) ---
				Livro livro1 = new Livro();
				livro1.setTitulo("A Importância da Oração");
				livro1.setDescricao("Reflexões diárias sobre a vida de oração.");
				livro1.setSecaoId(1L);

				Capitulo cap1 = new Capitulo();
				cap1.setNumeroCapitulo(1);
				cap1.setTituloCapitulo("Capítulo 1 - O Poder da Constância");
				cap1.setConteudo("A oração constante fortalece o espírito e traz paz ao coração em momentos de atribulação...");
				cap1.setLivro(livro1);

				livro1.setCapitulos(List.of(cap1));


				// --- TEXTO / LIVRO 2 (Seção 3 - Ensino Casais) ---
				Livro livro2 = new Livro();
				livro2.setTitulo("O Amortecimento das Arestas no Casamento");
				livro2.setDescricao("Orientações para o diálogo e convivência a dois.");
				livro2.setSecaoId(3L);

				Capitulo cap2 = new Capitulo();
				cap2.setNumeroCapitulo(1);
				cap2.setTituloCapitulo("Capítulo 1 - A Arte da Escuta");
				cap2.setConteudo("Ouvir com atenção é o primeiro passo para compreender as necessidades do parceiro sem julgamentos prévios...");
				cap2.setLivro(livro2);

				livro2.setCapitulos(List.of(cap2));


				// --- TEXTO / LIVRO 3 (Seção 2 - Ensino Família - Restrita) ---
				Livro livro3 = new Livro();
				livro3.setTitulo("Educação e Valores na Família");
				livro3.setDescricao("Princípios essenciais para a estrutura familiar.");
				livro3.setSecaoId(2L);

				Capitulo cap3 = new Capitulo();
				cap3.setNumeroCapitulo(1);
				cap3.setTituloCapitulo("Capítulo 1 - O Exemplo Prático");
				cap3.setConteudo("Os ensinamentos mais profundos transmitidos na família são aqueles demonstrados pelas atitudes diárias...");
				cap3.setLivro(livro3);

				livro3.setCapitulos(List.of(cap3));


				// Salva todos os livros no PostgreSQL
				livroRepository.saveAll(List.of(livro1, livro2, livro3));

				System.out.println("✅ TODOS OS TEXTOS E CAPÍTULOS FORAM CARREGADOS!");
			}
		};
	}
}