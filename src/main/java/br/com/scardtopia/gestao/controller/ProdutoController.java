package br.com.scardtopia.gestao.controller;

import br.com.scardtopia.gestao.model.Produto;
import br.com.scardtopia.gestao.repository.ProdutoRepository;
import br.com.scardtopia.gestao.repository.VendaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository produtos;
    private final VendaRepository vendas;

    public ProdutoController(ProdutoRepository produtos, VendaRepository vendas) {
        this.produtos = produtos; this.vendas = vendas;
    }

    @InitBinder
    public void bindPreco(WebDataBinder binder) {
        // HTML type=number envia decimal com ponto; aceitar também vírgula no formulário.
        binder.registerCustomEditor(BigDecimal.class, "preco", new PropertyEditorSupport() {
            @Override public void setAsText(String valor) {
                if (valor == null || valor.isBlank()) { setValue(null); return; }
                setValue(new BigDecimal(valor.trim().replace(',', '.')));
            }
        });
    }

    @GetMapping
    public String listar(@RequestParam(required=false, defaultValue="") String busca, Model model) {
        model.addAttribute("pagina", "produtos");
        model.addAttribute("busca", busca);
        model.addAttribute("produtos", busca.isBlank() ? produtos.findAllByOrderByNomeAsc() :
                produtos.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim()));
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pagina", "produtos");
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        var p = produtos.findById(id);
        if (p.isEmpty()) { flash.addFlashAttribute("erro", "Produto não encontrado."); return "redirect:/produtos"; }
        model.addAttribute("pagina", "produtos");
        model.addAttribute("produto", p.get());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto, BindingResult resultado,
                         Model model, RedirectAttributes flash) {
        model.addAttribute("pagina", "produtos");
        if (!resultado.hasErrors()) {
            produto.setCodigo(produto.getCodigo().trim().toUpperCase());
            boolean duplicado = produto.getId() == null ? produtos.existsByCodigoIgnoreCase(produto.getCodigo()) :
                produtos.existsByCodigoIgnoreCaseAndIdNot(produto.getCodigo(), produto.getId());
            if (duplicado) resultado.rejectValue("codigo", "duplicado", "Este código já está cadastrado.");
            if (produto.getId() != null && !produtos.existsById(produto.getId())) {
                resultado.reject("invalido", "Produto não encontrado para edição.");
            }
        }
        if (resultado.hasErrors()) return "produtos/form";
        produtos.save(produto);
        flash.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes flash) {
        if (vendas.existsByProdutoId(id)) {
            flash.addFlashAttribute("erro", "Não é possível excluir produto com ordens vinculadas.");
        } else if (produtos.existsById(id)) {
            produtos.deleteById(id);
            flash.addFlashAttribute("sucesso", "Produto excluído.");
        }
        return "redirect:/produtos";
    }
}
