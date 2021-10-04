package br.com.alura.forum.config.validacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Especie de interceptador para qualquer rest controller (existe tambem @ControllerAdvice)
@RestControllerAdvice
public class ErroDeValidacaoHandler {
	
	// Interface para pegar mensagens de erro de acordo com o idioma do user
	@Autowired
	private MessageSource messageSource;

	// Todas as excecoes MethodArgumentNotValidException vem para este metodo
	// Sem o response status, o Spring pensa que fizemos o tratamento e devolve 200
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroDeFormularioDTO> handle(MethodArgumentNotValidException excecao) {

		List<ErroDeFormularioDTO> dtos = new ArrayList<>();
		
		// Todos os erros do formularios entram nessa lista
		List<FieldError> fieldErrors = excecao.getBindingResult().getFieldErrors();
		
		fieldErrors.forEach(erro -> {
			
			//Pegando a mensagem passando o erro e pegando o local do usuario
			String mensagem = messageSource.getMessage(erro, LocaleContextHolder.getLocale());
			
			ErroDeFormularioDTO erroDTO = new ErroDeFormularioDTO(erro.getField(), mensagem);
			dtos.add(erroDTO);
		});
		
		return dtos;
		
	}

}
