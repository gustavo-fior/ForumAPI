package br.com.alura.forum.controller.dto;

import br.com.alura.forum.modelo.Usuario;

public class UsuarioDTO {

	private String nome;
	private String email;

	public UsuarioDTO(Usuario usuario) {
		this.nome = usuario.getNome();
		this.email = usuario.getEmail();
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}
	
	

}
