package com.clyvoelo.painel.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RegraDeNegocioException.class)
    public String tratarRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("mensagemErro", ex.getMessage());
        String origem = request.getHeader("Referer");
        boolean origemValida = origem != null && !origem.endsWith(request.getRequestURI());
        return "redirect:" + (origemValida ? origem : "/");
    }

    @ExceptionHandler({RecursoNaoEncontradoException.class, NoResourceFoundException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tratarNaoEncontrado(Exception ex, Model model) {
        model.addAttribute("titulo", "Página não encontrada");
        model.addAttribute("mensagem", ex instanceof RecursoNaoEncontradoException
                ? ex.getMessage()
                : "O endereço acessado não existe no painel.");
        return "erro";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String tratarErroInesperado(Exception ex, Model model) {
        log.error("Erro inesperado no painel", ex);
        model.addAttribute("titulo", "Algo deu errado");
        model.addAttribute("mensagem", "Não foi possível concluir a operação. Tente novamente em instantes.");
        return "erro";
    }
}
