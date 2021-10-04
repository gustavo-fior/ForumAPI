package br.com.alura.forum.config.validacao;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

@Service
public class UsuarioValidacao {

	private UsuarioRepository usuarioRepostory;

	public UsuarioValidacao(UsuarioRepository usuarioRepostory) {
		this.usuarioRepostory = usuarioRepostory;
	}

	public boolean validarEmail(Usuario usuario) {

		Optional<Usuario> usuarioOptional = usuarioRepostory.findByEmail(usuario.getEmail());

		if (usuarioOptional.isPresent()) {
			return false;
		}

		return true;

	}

}
