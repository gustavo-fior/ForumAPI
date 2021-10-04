package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.form.LoginForm;
import br.com.alura.forum.dto.TokenDTO;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

	// Atributo para fazermos a autenticacao do usuario (user e senha)
	// O Spring nao consegue injetar essa classe, por isso
	// fizemos a sobrescrita do metodo que cria ele na
	// classe de configs web
	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private TokenService tokenService;

	@PostMapping
	public ResponseEntity<TokenDTO> autenticar(@RequestBody @Valid LoginForm form) {

		// Criamos um metodo na DTO do formulario de login (implementado pela aplicacao
		// de front end que envia em JSON) para converter para esse objeto
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();

		try {

			// Metodo recebe apenas objetos do tipo UsernamePasswordAuthenticationToken
			// por isso convertemos acima
			// Aqui o Spring chama nossa classe de logica de validacao
			// (AutenticacaoDeUsuarioService)
			Authentication authentication = authManager.authenticate(dadosLogin);

			// Se a autenticacao der certo, geramos o token em uma classe separada
			// para o usuario com o authentication (podemos extrair o usuario do
			// authentication)
			String token = tokenService.gerarToken(authentication);

			// Retornando o token (via DTO) no corpo da requisicao e o metodo de
			// autenticacao (HTTP)
			return ResponseEntity.ok(new TokenDTO(token, "Bearer"));

		} catch (AuthenticationException e) {

			return ResponseEntity.badRequest().build();

		}

	}

}
