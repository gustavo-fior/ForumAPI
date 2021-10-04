package br.com.alura.forum.controller.form;

import javax.validation.constraints.NotBlank;

import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;

public class TopicoForm {

	@NotBlank
	private String titulo;
	
	@NotBlank
	private String mensagem;
	
	@NotBlank
	private String nomeCurso;

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public Topico toTopico(CursoRepository cursoRepository) {

		Topico topico = new Topico();

		topico.setTitulo(this.titulo);
		topico.setMensagem(this.mensagem);
		topico.setCurso(cursoRepository.findByNome(nomeCurso));

		return topico;

	}

}
