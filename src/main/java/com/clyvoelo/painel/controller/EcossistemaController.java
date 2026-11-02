package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.dto.StatusServicoDto;
import com.clyvoelo.painel.service.EcossistemaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class EcossistemaController {

    private final EcossistemaService ecossistemaService;
    private final String urlApiDotnet;
    private final String urlAppMobile;

    public EcossistemaController(EcossistemaService ecossistemaService,
                                 @Value("${clyvo.integracoes.api-dotnet}") String urlApiDotnet,
                                 @Value("${clyvo.integracoes.app-mobile}") String urlAppMobile) {
        this.ecossistemaService = ecossistemaService;
        this.urlApiDotnet = urlApiDotnet;
        this.urlAppMobile = urlAppMobile;
    }

    @GetMapping("/ecossistema")
    public String ecossistema(Model model) {
        model.addAttribute("urlApiDotnet", urlApiDotnet);
        model.addAttribute("urlAppMobile", urlAppMobile);
        return "ecossistema";
    }

    @GetMapping("/ecossistema/status")
    @ResponseBody
    public List<StatusServicoDto> status() {
        return ecossistemaService.verificarServicos();
    }
}
