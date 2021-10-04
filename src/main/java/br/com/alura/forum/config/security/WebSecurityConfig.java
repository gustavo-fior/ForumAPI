package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AutenticacaoDeUsuarioService autenticacaoService;
	
	// Aqui, diferentemente do filtro, podemos injetar essa dependencia
	// Atributo usado para injetar dependencias no filtro
	@Autowired
	private TokenService tokenService;
	
	// Atributo usado para injetar dependencias no filtro
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	// Metodo sobrescrito para podermos ter uma instancia do authentication
	// manager (Spring nao consegue injetar)
	// @Bean para o Spring saber que o metodo devolve o Authentication Manager
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	// Configuracoes de AUTENTICACAO
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

		// Passando a classe de logica de autenticacao
		auth.userDetailsService(autenticacaoService).passwordEncoder(encoder);

	}

	// Configuracoes de AUTORIZACAO
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Permitindo todas as requisicoes do tipo GET para /topicos
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/topicos").permitAll()
			.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
			.antMatchers(HttpMethod.POST, "/auth").permitAll()
			.antMatchers(HttpMethod.GET, "/actuator").permitAll() // permitAll apenas para desenvolvimento
			.antMatchers(HttpMethod.GET, "/actuator/**").permitAll() // permitAll apenas para desenvolvimento
			.antMatchers(HttpMethod.POST, "/cadastrar").permitAll()
			.anyRequest().authenticated()
			.and()
			.csrf().disable() // Como nossa aplicacao usa o token, ela esta livre desse tipo de ataque, por isso desabilitamos
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nao cria uma sessao, deixa a autenticacao stateless
			.and()
			.addFilterBefore(new AutenticacaoViaTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class); // Passando nosso filtro antes do padrao do Spring
			

			
			// Dessa forma usariamos uma sessao para autenticacao do usuario
			// e o Spring usa seu proprio controller para a parte de autenticacao (se nao implementarmos),
			// agora temos que criar nosso controller
			//.and()
			//.formLogin();
		
	}

	// Configuracoes de RECURSOS ESTATICOS (JS, CSS, IMGS)
	// Implementacao seria dizer ao Spring Security para ignorar arquivos desse tipo
	@Override
	public void configure(WebSecurity web) throws Exception {

		// Ignorando os arquivos do Swagger
		// http://localhost:8080/swagger-ui.html
		
		web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**",
				"/swagger-resources/**");

	}

}
