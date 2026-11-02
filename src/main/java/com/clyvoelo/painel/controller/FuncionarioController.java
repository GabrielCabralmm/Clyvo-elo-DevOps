package com.clyvoelo.painel.controller;

import com.clyvoelo.painel.model.Funcionario;
import com.clyvoelo.painel.model.enums.Perfil;
import com.clyvoelo.painel.security.UsuarioAutenticado;
import com.clyvoelo.painel.service.FuncionarioService;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private static final List<Perfil> PERFIS_DA_EQUIPE = List.of(Perfil.ADMIN, Perfil.DOUTOR);

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @ModelAttribute("perfisDaEquipe")
    public List<Perfil> perfisDaEquipe() {
        return PERFIS_DA_EQUIPE;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("funcionarios", funcionarioService.listar());
        return "funcionarios/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        return "funcionarios/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("funcionario", funcionarioService.buscar(id));
        return "funcionarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Funcionario funcionario, BindingResult resultado,
                         @RequestParam(required = false) String senha, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            return "funcionarios/form";
        }
        Funcionario salvo;
        try {
            salvo = funcionarioService.salvar(funcionario, senha);
        } catch (RegraDeNegocioException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            return "funcionarios/form";
        }
        redirect.addFlashAttribute("mensagemSucesso", salvo.getNome() + " foi salvo com sucesso.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario,
                          RedirectAttributes redirect) {
        funcionarioService.excluir(id, usuario.getId());
        redirect.addFlashAttribute("mensagemSucesso", "Funcionário excluído.");
        return "redirect:/funcionarios";
    }
}
