package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.dto.RelatoAlertaForm;
import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.security.UsuarioAutenticado;
import com.clyvoelo.painel.service.AlertaService;
import com.clyvoelo.painel.service.AreaTutorService;
import com.clyvoelo.painel.service.EventoClinicoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/minha-area/pets")
public class AreaTutorController {

    private final AreaTutorService areaTutorService;
    private final EventoClinicoService eventoClinicoService;
    private final AlertaService alertaService;

    public AreaTutorController(AreaTutorService areaTutorService, EventoClinicoService eventoClinicoService,
                               AlertaService alertaService) {
        this.areaTutorService = areaTutorService;
        this.eventoClinicoService = eventoClinicoService;
        this.alertaService = alertaService;
    }

    @GetMapping("/{id}")
    public String acompanhamento(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario, Model model) {
        return exibirPet(id, usuario, new RelatoAlertaForm(), model);
    }

    @PostMapping("/{id}/relatar")
    public String relatar(@PathVariable Long id, @Valid @ModelAttribute("relato") RelatoAlertaForm relato,
                          BindingResult resultado, @AuthenticationPrincipal UsuarioAutenticado usuario,
                          Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return exibirPet(id, usuario, relato, model);
        }
        alertaService.relatarPeloTutor(id, usuario.getId(), relato);
        redirect.addFlashAttribute("mensagemSucesso", "Relato enviado. A equipe veterinária vai acompanhar e responder por aqui.");
        return "redirect:/minha-area/pets/" + id;
    }

    private String exibirPet(Long id, UsuarioAutenticado usuario, RelatoAlertaForm relato, Model model) {
        Pet pet = areaTutorService.petDoTutor(id, usuario.getId());
        model.addAttribute("pet", pet);
        model.addAttribute("eventos", eventoClinicoService.historicoDoPet(id));
        model.addAttribute("alertas", alertaService.alertasDoPet(id));
        model.addAttribute("relato", relato);
        return "tutor/pet";
    }
}
