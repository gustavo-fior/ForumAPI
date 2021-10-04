package br.com.alura.forum.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.modelo.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

// Classe para configuracao do SpringFox Swagger
@Configuration
public class SwaggerConfigurations {

	@Bean // (exportando esse bean do tipo Docket)
	public Docket forumApi() {
		
		// Retornando um Docket baseando a documentacao no swagger 2
		// Settando o pacote base como "br.com.alura.forum"
		// Settando os paths para a documentacao fazer analise "/**"
		// No final, para ignorar todas as url que envolvam o usuario
		// Depois configurando para o swagger adicionar o JWT na doc
		
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.alura.forum"))
				.paths(PathSelectors.ant("/**"))
				.build()
				.ignoredParameterTypes(Usuario.class)
				.globalOperationParameters(Arrays.asList( //Criando uma lista de parametros globais para o swagger apresentar em todos os endpoints
						new ParameterBuilder()
						.name("Authorization") // cabecalho do parametro
						.description("Header para Token JWT") // description do header
						.modelRef(new ModelRef("string")) // tipo do parametro
						.parameterType("header") // onde o parametro vai ser passado
						.required(false) // obrigatorio? nao
						.build()));
	}

}
