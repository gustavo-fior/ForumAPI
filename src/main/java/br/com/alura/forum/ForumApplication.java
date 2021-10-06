package br.com.alura.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
// Para habilitarmos esse modulo (mais especificamente para receber a paginacao da URI)
@EnableSpringDataWebSupport
// Para habilitarmos cache
@EnableCaching
// Para habilitarmos o SpringFox Swagger 2 (para documentacao automatica)
@EnableSwagger2
// SpringBootServletInitializer seria para fazer a build em war
public class ForumApplication /*extends SpringBootServletInitializer*/{

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}
 
	/* Para build em war
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ForumApplication.class);
	}
	 */
	
}
