package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.model.enums.Especie;
import com.clyvoelo.painel.model.enums.OrigemAlerta;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import com.clyvoelo.painel.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AtributosGlobais {

    @ModelAttribute("usuarioLogado")
    public UsuarioAutenticado usuarioLogado(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return usuario;
    }

    @ModelAttribute("especies")
    public Especie[] especies() { return Especie.values(); }

    @ModelAttribute("tiposEvento")
    public TipoEvento[] tiposEvento() { return TipoEvento.values(); }

    @ModelAttribute("statusEvento")
    public StatusEvento[] statusEvento() { return StatusEvento.values(); }

    @ModelAttribute("prioridades")
    public PrioridadeAlerta[] prioridades() { return PrioridadeAlerta.values(); }

    @ModelAttribute("statusAlerta")
    public StatusAlerta[] statusAlerta() { return StatusAlerta.values(); }

    @ModelAttribute("origensAlerta")
    public OrigemAlerta[] origensAlerta() { return OrigemAlerta.values(); }
}
