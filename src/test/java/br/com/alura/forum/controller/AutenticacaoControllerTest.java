package br.com.alura.forum.controller;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// @WebMvcTest Testes da camada controller (spring so carrega as classes mvc do projeto, por isso usamos o teste normal)
// Quando essa classe rodar o Spring carregara o default (nao passa pelo main -> arguments...) e aqui forcamos para o perfil test
@ActiveProfiles("test") // Carrega o application-test.properties automaticamente
@SpringBootTest
@AutoConfigureMockMvc // Se tivessemos injetado o @WebMvcTest nao precisariamos dessa configuracao para injetar o mock
public class AutenticacaoControllerTest {

	// Para nao precisarmos instanciar o controller, essa classe nos fornece um mock
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void deveriaDevolver400CasoDadosDeAutenticacaoEstejamIncorretos() throws Exception {

		URI uri = new URI("/auth");

		String json = "{\"email\":\"invalido@email.com\",\"senha\":\"123456}\"";

		// Mock realizando a requisicao para o controller e ja conferindo se o status volta 400
		mockMvc.perform(MockMvcRequestBuilders
				.post(uri)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers
				.status()
				.is(400));
	}

}
