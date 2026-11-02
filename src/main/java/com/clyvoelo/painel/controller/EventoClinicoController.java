package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.dto.EventoFiltro;
import com.clyvoelo.painel.dto.NovoEventoForm;
import com.clyvoelo.painel.dto.TextoForm;
import com.clyvoelo.painel.model.EventoClinico;
import com.clyvoelo.painel.security.UsuarioAutenticado;
import com.clyvoelo.painel.service.EventoClinicoService;
import com.clyvoelo.painel.service.PetService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/eventos")
public class EventoClinicoController {

    private final EventoClinicoService eventoClinicoService;
    private final PetService petService;

    public EventoClinicoController(EventoClinicoService eventoClinicoService, PetService petService) {
        this.eventoClinicoService = eventoClinicoService;
        this.petService = petService;
    }

    @GetMapping
    public String listar(@ModelAttribute("filtro") EventoFiltro filtro, Model model) {
        model.addAttribute("eventos", eventoClinicoService.buscar(filtro));
        model.addAttribute("pets", petService.listar());
        return "eventos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(@RequestParam(required = false) Long idPet, Model model) {
        NovoEventoForm form = new NovoEventoForm();
        form.setIdPet(idPet);
        form.setData(LocalDate.now());
        return exibirFormulario(form, model);
    }

    @PostMapping("/novo")
    public String agendar(@Valid @ModelAttribute("form") NovoEventoForm form, BindingResult resultado, Model model,
                          RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return exibirFormulario(form, model);
        }
        EventoClinico evento = eventoClinicoService.agendar(form);
        redirect.addFlashAttribute("mensagemSucesso",
                "Evento de " + evento.getTipoEvento().getDescricao().toLowerCase() + " agendado para " + evento.getPet().getNome() + ".");
        return "redirect:/eventos/" + evento.getId();
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return exibirDetalhe(id, new TextoForm(), model);
    }

    @PostMapping("/{id}/atender")
    public String atender(@PathVariable Long id, @Valid @ModelAttribute("atendimento") TextoForm atendimento,
                          BindingResult resultado, @AuthenticationPrincipal UsuarioAutenticado usuario,
                          Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return exibirDetalhe(id, atendimento, model);
        }
        EventoClinico evento = eventoClinicoService.registrarAtendimento(id, usuario.getId(), atendimento.getTexto());
        redirect.addFlashAttribute("mensagemSucesso",
                "Atendimento de " + evento.getPet().getNome() + " registrado. O tutor já pode ver a orientação.");
        return "redirect:/eventos/" + id;
    }

    @PostMapping("/{id}/reagendar")
    public String reagendar(@PathVariable Long id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaData,
                            RedirectAttributes redirect) {
        eventoClinicoService.reagendar(id, novaData);
        redirect.addFlashAttribute("mensagemSucesso", "Evento reagendado.");
        return "redirect:/eventos/" + id;
    }

    private String exibirFormulario(NovoEventoForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("pets", petService.listar());
        return "eventos/form";
    }

    private String exibirDetalhe(Long id, TextoForm atendimento, Model model) {
        EventoClinico evento = eventoClinicoService.buscar(id);
        model.addAttribute("evento", evento);
        model.addAttribute("historico", eventoClinicoService.historicoDoPet(evento.getPet().getId()));
        model.addAttribute("atendimento", atendimento);
        return "eventos/detalhe";
    }
}
