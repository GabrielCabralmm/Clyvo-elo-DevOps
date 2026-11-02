package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.model.Pet;
import com.clyvoelo.painel.model.enums.Especie;
import com.clyvoelo.painel.service.AlertaService;
import com.clyvoelo.painel.service.EventoClinicoService;
import com.clyvoelo.painel.service.PetService;
import com.clyvoelo.painel.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final TutorService tutorService;
    private final EventoClinicoService eventoClinicoService;
    private final AlertaService alertaService;

    public PetController(PetService petService, TutorService tutorService,
                         EventoClinicoService eventoClinicoService, AlertaService alertaService) {
        this.petService = petService;
        this.tutorService = tutorService;
        this.eventoClinicoService = eventoClinicoService;
        this.alertaService = alertaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca,
                         @RequestParam(required = false) Especie especie, Model model) {
        model.addAttribute("pets", petService.buscar(busca, especie));
        model.addAttribute("busca", busca);
        model.addAttribute("especieSelecionada", especie);
        return "pets/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pet", petService.buscar(id));
        model.addAttribute("eventos", eventoClinicoService.historicoDoPet(id));
        model.addAttribute("alertas", alertaService.alertasDoPet(id));
        return "pets/detalhe";
    }

    @GetMapping("/novo")
    public String formNovo(@RequestParam(required = false) Long idTutor, Model model) {
        Pet pet = new Pet();
        if (idTutor != null) {
            pet.setTutor(tutorService.buscar(idTutor));
        }
        return exibirFormulario(pet, model);
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        return exibirFormulario(petService.buscar(id), model);
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Pet pet, BindingResult resultado, Model model,
                         RedirectAttributes redirect) {
        if (pet.getTutor() == null || pet.getTutor().getId() == null) {
            resultado.rejectValue("tutor", "obrigatorio", "Selecione o tutor");
        }
        if (resultado.hasErrors()) {
            return exibirFormulario(pet, model);
        }
        Pet salvo = petService.salvar(pet);
        redirect.addFlashAttribute("mensagemSucesso", salvo.getNome() + " foi salvo com sucesso.");
        return "redirect:/pets/" + salvo.getId();
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        petService.excluir(id);
        redirect.addFlashAttribute("mensagemSucesso", "Pet excluído.");
        return "redirect:/pets";
    }

    private String exibirFormulario(Pet pet, Model model) {
        model.addAttribute("pet", pet);
        model.addAttribute("tutores", tutorService.listar());
        return "pets/form";
    }
}
