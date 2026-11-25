package br.com.scardtopia.gestao.controller;

import br.com.scardtopia.gestao.form.VendaForm;
import br.com.scardtopia.gestao.model.StatusVenda;
import br.com.scardtopia.gestao.repository.*;
import br.com.scardtopia.gestao.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendas")
public class VendaController {
    private final VendaRepository vendas;
    private final ProdutoRepository produtos;
    private final ClienteRepository clientes;
    private final VendaService servico;

    public VendaController(VendaRepository vendas, ProdutoRepository produtos,
                           ClienteRepository clientes, VendaService servico) {
        this.vendas=vendas; this.produtos=produtos; this.clientes=clientes; this.servico=servico;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagina", "vendas");
        model.addAttribute("vendas", vendas.findAllByOrderByCriadoEmDesc());
        return "vendas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("vendaForm", new VendaForm());
        preencherOpcoes(model);
        return "vendas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("vendaForm", servico.criarFormParaEdicao(id));
            preencherOpcoes(model);
            return "vendas/form";
        } catch(RegraNegocioException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/vendas";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("vendaForm") VendaForm form, BindingResult resultado,
                         Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) { preencherOpcoes(model); return "vendas/form"; }
        try {
            servico.salvar(form);
            flash.addFlashAttribute("sucesso", "Ordem salva e estoque atualizado.");
            return "redirect:/vendas";
        } catch(RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            preencherOpcoes(model);
            return "vendas/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        try {
            servico.excluir(id);
            flash.addFlashAttribute("sucesso", "Ordem excluída. Estoque recomposto, quando aplicável.");
        } catch(RegraNegocioException ex) {
            flash.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/vendas";
    }

    private void preencherOpcoes(Model model) {
        model.addAttribute("pagina", "vendas");
        model.addAttribute("clientes", clientes.findAllByOrderByNomeAsc());
        model.addAttribute("produtos", produtos.findAllByOrderByNomeAsc());
        model.addAttribute("statusDisponiveis", StatusVenda.values());
    }
}
