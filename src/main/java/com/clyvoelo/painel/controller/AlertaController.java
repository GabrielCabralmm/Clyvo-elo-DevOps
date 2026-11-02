package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.dto.AlertaFiltro;
import com.clyvoelo.painel.dto.TextoForm;
import com.clyvoelo.painel.security.UsuarioAutenticado;
import com.clyvoelo.painel.service.AlertaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public String listar(@ModelAttribute("filtro") AlertaFiltro filtro, Model model) {
        model.addAttribute("alertas", alertaService.buscar(filtro));
        return "alertas/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        return exibirDetalhe(id, new TextoForm(), new TextoForm(), model);
    }

    @PostMapping("/{id}/assumir")
    public String assumir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario,
                          RedirectAttributes redirect) {
        alertaService.assumir(id, usuario.getId());
        redirect.addFlashAttribute("mensagemSucesso", "Você agora é o responsável por este alerta.");
        return "redirect:/alertas/" + id;
    }

    @PostMapping("/{id}/anotar")
    public String anotar(@PathVariable Long id, @Valid @ModelAttribute("anotacao") TextoForm anotacao,
                         BindingResult resultado, @AuthenticationPrincipal UsuarioAutenticado usuario,
                         Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return exibirDetalhe(id, anotacao, new TextoForm(), model);
        }
        alertaService.registrarAnotacao(id, usuario.getId(), anotacao.getTexto());
        redirect.addFlashAttribute("mensagemSucesso", "Anotação registrada no acompanhamento.");
        return "redirect:/alertas/" + id;
    }

    @PostMapping("/{id}/resolver")
    public String resolver(@PathVariable Long id, @Valid @ModelAttribute("resolucao") TextoForm resolucao,
                           BindingResult resultado, @AuthenticationPrincipal UsuarioAutenticado usuario,
                           Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return exibirDetalhe(id, new TextoForm(), resolucao, model);
        }
        alertaService.resolver(id, usuario.getId(), resolucao.getTexto());
        redirect.addFlashAttribute("mensagemSucesso", "Alerta resolvido.");
        return "redirect:/alertas/" + id;
    }

    private String exibirDetalhe(Long id, TextoForm anotacao, TextoForm resolucao, Model model) {
        model.addAttribute("alerta", alertaService.buscar(id));
        model.addAttribute("acompanhamentos", alertaService.acompanhamentos(id));
        model.addAttribute("anotacao", anotacao);
        model.addAttribute("resolucao", resolucao);
        return "alertas/detalhe";
    }
}
