package br.com.alura.forum.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.config.validacao.UsuarioValidacao;
import br.com.alura.forum.controller.dto.UsuarioDTO;
import br.com.alura.forum.controller.form.CadastroForm;
import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

@Controller
@RequestMapping("/cadastrar")
public class CadastroController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioValidacao usuarioValidacao;

	@PostMapping
	public ResponseEntity<UsuarioDTO> cadastrarUsuario(@RequestBody @Valid CadastroForm form,
			UriComponentsBuilder uriBuilder) {

		Usuario usuario = form.toUsuario(form);

		if (!usuarioValidacao.validarEmail(usuario)) {
			
			return ResponseEntity.badRequest().build();
		}

		usuarioRepository.save(usuario);

		URI uri = uriBuilder.path("/usuario/{id}").buildAndExpand(usuario.getId()).toUri();

		return ResponseEntity.created(uri).body(new UsuarioDTO(usuario));

	}

}
