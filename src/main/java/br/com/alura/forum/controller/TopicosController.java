package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDTO;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.dto.TopicoDTO;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

// Combinacao de @Controller e @ResponseBody
@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	// Dessa forma os parametros vem na URI, por isso a @RequestParam, dessa forma
	// o Spring entende que ele se torna obrigatorio, por isso o required = false
	// @PageableDefault para quando o usuario nao passar ordenacao
	// --------------------------------------------------------------------------------
	// @Cacheable segura o retorno do metodo em memoria e o value eh o identificador
	// (id) desse cacheable
	// O Spring sabe quando URI muda (uso de parametros) e, se necessario, faz mais
	// uma consulta
	// --------------------------------------------------------------------------------
	// Apesar de tudo, esse nao eh um bom uso do Cache, pois essa tabela eh utilizada
	// muito corriqueiramente, deveriamos colocar tabelas menos utilizadas
	@GetMapping
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDTO> listaTopicos(@RequestParam(required = false) String nomeCurso,
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) {

		// Trocamos essa linha e is parametros passados na URI pelo parametro Pageable
		// da URI
		// O Pageable recebe os mesmos parametros so que em ingles
		// localhost:8080/topicos?nomeCurso=Spring+Boot&page=0&size=2&sort=id,asc&sort=.....
		// se quiser mais de um parametro
		// Pageable paginacao = PageRequest.of(pagina, quantidade, Direction.ASC,
		// ordenacao);

		if (nomeCurso == null) {

			Page<Topico> topicos = topicoRepository.findAll(paginacao);

			return TopicoDTO.toTopicoDTO(topicos);

		} else {

			Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
			
			return TopicoDTO.toTopicoDTO(topicos);
		}

	}

	// @RequestBody avisando que os parametros para preenchermos o TopicoForm vao
	// vir no corpo da requisicao
	// ------------------------------------------------------------------------------
	// ResponseEntity recebe qual objeto volta no corpo da resposta
	// ------------------------------------------------------------------------------
	// UriComponentsBuilder vai ajudar a formar a URI de resposta
	// ------------------------------------------------------------------------------
	// @Transactional para quando a operacao for de escrita no bd (save, update,
	// delete)
	// ------------------------------------------------------------------------------
	// @CacheEvicit diz para o Spring que quando este metodo for chamado, ele deve
	// invalidar o Cache correspondente ao id passado e allEntries para limpar tudo
	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> cadastrar(@Valid @RequestBody TopicoForm topicoForm,
			UriComponentsBuilder uriBuilder) {

		Topico topico = topicoForm.toTopico(cursoRepository);
		topicoRepository.save(topico);

		// uriBuilder pega o complemento (nesse caso, localhost:8080) e adiciona ao que
		// foi passado
		// Estamos passando o id dinamicamente pegando ele do proprio objeto criado
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

		// Retornando o codigo 201 (created) que precisa de dois parametros:
		// Um cabecalho HTTP com a uri desse novo recurso
		// e uma representacao do recurso criado no corpo da resposta
		return ResponseEntity.created(uri).body(new TopicoDTO(topico));

	}

	// @RequestBody -> parametros no corpo da requisicao
	// @PathVariable -> variavel vem do path no URI
	// Sem anotacao -> parametro vem de forma tradicional (&x=20) na URI
	// Se precisarmos mudar o nome do parametro, indicamos isso na anotacao
	// ex.: @PathVariable("id") Long qlqrOutroNome
	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDTO> detalhar(@PathVariable Long id) {

		Optional<Topico> topico = topicoRepository.findById(id);

		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesDoTopicoDTO(topico.get()));
		}

		return ResponseEntity.notFound().build();

	}

	// PutMapping para "sobrescrever" todas as informacoes
	// PatchMapping para apenas um ou alguns campos
	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> atualizar(@PathVariable Long id,
			@Valid @RequestBody AtualizacaoTopicoForm atualizacaoTopicoform) {

		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {

			// topicoRepository.save(topico);
			// Nao precisamos dessa linha pois o Spring comitta sozinho se notar alguma
			// mudanca nos atributos e salva o topico alterado

			Topico topico = atualizacaoTopicoform.atualizar(id, topicoRepository);

			// Como nao criamos nada, nao precisamos passar a uri do novo topico
			// ou a representacao desse objeto no corpo da requisicao (so o objeto)
			return ResponseEntity.ok(new TopicoDTO(topico));
		}

		return ResponseEntity.notFound().build();

	}

	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<?> deletar(@PathVariable Long id) {

		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {

			topicoRepository.deleteById(id);

			return ResponseEntity.ok().build();

		}

		return ResponseEntity.notFound().build();

	}

}
