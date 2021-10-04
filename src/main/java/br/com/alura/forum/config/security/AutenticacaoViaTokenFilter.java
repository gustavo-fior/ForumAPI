package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

// Extendemos a classe de filtrar por cada requisicao
// @Filter nao existe no Spring, precisamos configurar na classe de web configs
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	// Em filtros nao podemos injetar dependencias com @Autowired
	private TokenService tokenService;

	private UsuarioRepository usuarioRepository;

	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String token = recuperarToken(request);

		boolean isValido = tokenService.isTokenValido(token);

		if (isValido) {

			autenticarCliente(token);

		}

		// Segue o fluxo (se nao estiver autenticado, manda erro)
		chain.doFilter(request, response);

	}

	private void autenticarCliente(String token) {

		// Recuperando o id do usuario atraves do token (passamos o id quando geramos o
		// token)
		Long idUsuario = tokenService.getIdUsuario(token);
		
		Usuario usuario = usuarioRepository.findById(idUsuario).get(); // Dessa forma carrega com o relacionamento EAGER
		
		// Autenticacao do usuario (que nao importa, ja que ele ja foi autenticado
		// antes, por isso a senha = null)
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null,
				usuario.getAuthorities());

		// Settando para o usuario estar autenticado para este request (filtro)
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	// Recuperando o token passado pela requisicao do usuario
	private String recuperarToken(HttpServletRequest request) {

		String token = request.getHeader("Authorization");

		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
			return null;
		}

		return token.substring(7, token.length());
	}

}
