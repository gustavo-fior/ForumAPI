package br.com.alura.forum.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.alura.forum.modelo.Categoria;
import br.com.alura.forum.modelo.Curso;

//Especializacao do spring para testes de repository
@DataJpaTest
//Anotacao para configurar nao substituir pelo banco em memoria (por padrao ele usa banco em memoria)
@AutoConfigureTestDatabase(replace = Replace.NONE)
//Quando essa classe rodar o Spring carregara o default (nao passa pelo main -> arguments...) e aqui forcamos para o perfil test
@ActiveProfiles("test") // Carrega o application-test.properties automaticamente
public class CursoRepositoryTest {

	// Com essa anotacao, qualquer atributo recebe uma instancia
	@Autowired
	private CursoRepository cursoRepository;
	
	// Entity Manager de teste do Spring
	@Autowired
	private TestEntityManager em;

	@Test
	public void deveriaCarregarUmCursoAoBuscarPeloSeuNome() {

		
		Categoria categoria = new Categoria();
		categoria.setTitulo("Programacao");
		
		Curso html5 = new Curso();
		html5.setNome("HTML 5");
		html5.setCategoria(categoria);
		
		String nome = "HTML 5";
		
		em.persist(categoria);

		Curso curso = cursoRepository.findByNome(nome);

		assertNotNull(curso);
		assertEquals(nome, curso.getNome());
	}

	@Test
	public void naoDeveriaCarregarUmCursoAoBuscarPeloSeuNome() {

		String nome = "JPA";

		Curso curso = cursoRepository.findByNome(nome);

		assertNull(curso);
	}

}
