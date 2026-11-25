package br.com.scardtopia.gestao.controller;

import br.com.scardtopia.gestao.model.Cliente;
import br.com.scardtopia.gestao.repository.ClienteRepository;
import br.com.scardtopia.gestao.repository.VendaRepository;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository clientes;
    private final VendaRepository vendas;

    public ClienteController(ClienteRepository clientes, VendaRepository vendas) {
        this.clientes = clientes; this.vendas = vendas;
    }

    @GetMapping
    public String listar(@RequestParam(required=false, defaultValue="") String busca, Model model) {
        model.addAttribute("pagina", "clientes");
        model.addAttribute("busca", busca);
        model.addAttribute("clientes", busca.isBlank() ? clientes.findAllByOrderByNomeAsc() :
                clientes.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim()));
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pagina", "clientes");
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable @NonNull Long id, Model model, RedirectAttributes flash) {
        var c = clientes.findById(id);
        if (c.isEmpty()) { flash.addFlashAttribute("erro", "Cliente não encontrado."); return "redirect:/clientes"; }
        model.addAttribute("pagina", "clientes");
        model.addAttribute("cliente", c.get());
        return "clientes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("cliente") @NonNull Cliente cliente, BindingResult resultado,
                         Model model, RedirectAttributes flash) {
        model.addAttribute("pagina", "clientes");
        if (!resultado.hasErrors()) {
            cliente.setEmail(cliente.getEmail().trim().toLowerCase());
            boolean duplicado = cliente.getId() == null ? clientes.existsByEmailIgnoreCase(cliente.getEmail()) :
                clientes.existsByEmailIgnoreCaseAndIdNot(cliente.getEmail(), cliente.getId());
            if (duplicado) resultado.rejectValue("email", "duplicado", "Este e-mail já está cadastrado.");
            if (cliente.getId() != null && !clientes.existsById(cliente.getId())) {
                resultado.reject("invalido", "Cliente não encontrado para edição.");
            }
        }
        if (resultado.hasErrors()) return "clientes/form";
        clientes.save(cliente);
        flash.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable @NonNull Long id, RedirectAttributes flash) {
        if (vendas.existsByClienteId(id)) {
            flash.addFlashAttribute("erro", "Não é possível excluir cliente com ordens vinculadas.");
        } else if (clientes.existsById(id)) {
            clientes.deleteById(id);
            flash.addFlashAttribute("sucesso", "Cliente excluído.");
        }
        return "redirect:/clientes";
    }
}
