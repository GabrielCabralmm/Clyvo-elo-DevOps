package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.model.Tutor;
import com.clyvoelo.painel.service.PetService;
import com.clyvoelo.painel.service.TutorService;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService tutorService;
    private final PetService petService;

    public TutorController(TutorService tutorService, PetService petService) {
        this.tutorService = tutorService;
        this.petService = petService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tutores", tutorService.listar());
        model.addAttribute("petsPorTutor", tutorService.quantidadeDePetsPorTutor());
        return "tutores/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("tutor", tutorService.buscar(id));
        model.addAttribute("pets", petService.listarDoTutor(id));
        return "tutores/detalhe";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("tutor", new Tutor());
        return "tutores/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("tutor", tutorService.buscar(id));
        return "tutores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Tutor tutor, BindingResult resultado,
                         @RequestParam(required = false) String senha, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return "tutores/form";
        }
        Tutor salvo;
        try {
            salvo = tutorService.salvar(tutor, senha);
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            return "tutores/form";
        }
        redirect.addFlashAttribute("mensagemSucesso", "Tutor " + salvo.getNome() + " salvo com sucesso.");
        return "redirect:/tutores/" + salvo.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        tutorService.excluir(id);
        redirect.addFlashAttribute("mensagemSucesso", "Tutor excluído.");
        return "redirect:/tutores";
    }
}
