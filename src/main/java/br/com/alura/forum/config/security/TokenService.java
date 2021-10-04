package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	// Injetado do application.properties
	@Value("${forum.jwt.expiration}")
	private String expiration;
	
	// Injetado do application.properties
	@Value("${forum.jwt.secret}")
	private String secret;

	public String gerarToken(Authentication authentication) {
		
		// Recuperando o usuario logado
		Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
		
		// Usa a API de datas antigas do Java
		// Identificando o momento atual
		Date hoje = new Date();
		
		// Identificando o limite para expirar
		// Pegando os milisegundos e adicionando o tempo de expiracao
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));
		
		return Jwts.builder() // Builder para o token
				.setIssuer("API do Forum da Alura") // Issuer eh quem esta criando o token
				.setSubject(usuarioLogado.getId().toString()) // Fornecendo identificacao unica para o usuario
				.setIssuedAt(hoje) // Data de geracao
				.setExpiration(dataExpiracao) // Quando vai expirar o token 
				.signWith(SignatureAlgorithm.HS256, secret) // Criptografia do token (algoritmo de criptografia e segredo)
				.compact(); // Para o token transformar em String
				
	}

	public boolean isTokenValido(String token) {

		try {

			// Retorna o token se ele existir e estiver valido
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;

		} catch (Exception e) {

			return false;
		}

	}

	public Long getIdUsuario(String token) {
		
		// Pegando o corpo do token
		Claims bodyDoToken = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		
		// Recuperando o Usuario pelo token
		String usuario = bodyDoToken.getSubject();
		
		return Long.parseLong(usuario);
	}
	
}
