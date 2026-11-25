package br.com.scardtopia.gestao.controller;

import br.com.scardtopia.gestao.model.StatusVenda;
import br.com.scardtopia.gestao.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ProdutoRepository produtos;
    private final ClienteRepository clientes;
    private final VendaRepository vendas;

    public HomeController(ProdutoRepository produtos, ClienteRepository clientes, VendaRepository vendas) {
        this.produtos = produtos; this.clientes = clientes; this.vendas = vendas;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("pagina", "dashboard");
        model.addAttribute("totalProdutos", produtos.count());
        model.addAttribute("totalClientes", clientes.count());
        model.addAttribute("totalVendas", vendas.count());
        model.addAttribute("estoqueBaixo", produtos.countByEstoqueLessThanEqual(3));
        model.addAttribute("faturamento", vendas.somarTotalPorStatus(StatusVenda.CONFIRMADA));
        model.addAttribute("ultimasVendas", vendas.findTop5ByOrderByCriadoEmDesc());
        return "dashboard";
    }
}
