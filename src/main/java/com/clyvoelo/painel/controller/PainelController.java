package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.security.UsuarioAutenticado;
import com.clyvoelo.painel.service.AreaTutorService;
import com.clyvoelo.painel.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PainelController {

    private final DashboardService dashboardService;
    private final AreaTutorService areaTutorService;

    public PainelController(DashboardService dashboardService, AreaTutorService areaTutorService) {
        this.dashboardService = dashboardService;
        this.areaTutorService = areaTutorService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String dashboard(@AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
        return switch (usuario.getPerfil()) {
            case ADMIN -> {
                model.addAttribute("painel", dashboardService.montarDashboardAdmin());
                yield "dashboard/admin";
            }
            case DOUTOR -> {
                model.addAttribute("painel", dashboardService.montarDashboardVeterinario(usuario.getId()));
                yield "dashboard/veterinario";
            }
            case TUTOR -> {
                model.addAttribute("pets", areaTutorService.resumoDosPets(usuario.getId()));
                yield "dashboard/tutor";
            }
        };
    }

    @RequestMapping("/acesso-negado")
    public String acessoNegado(Model model) {
        model.addAttribute("titulo", "Acesso não permitido");
        model.addAttribute("mensagem", "Seu perfil não tem permissão para acessar esta área do painel.");
        return "erro";
    }
}
