package br.com.alura.forum.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@Profile("dev") // Anotacao para essa classe ser usada apenas com o perfil de dev (para ativar
				// entre em run configurations -> arguments)
public class DevWebSecurityConfig extends WebSecurityConfigurerAdapter {

	// Configuracoes de AUTORIZACAO
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Permitindo todas as requisicoes para facilitar o desenvolvimento
		http.authorizeRequests()
				.antMatchers("/**").permitAll()
				.and().csrf().disable();
		
	}

}
