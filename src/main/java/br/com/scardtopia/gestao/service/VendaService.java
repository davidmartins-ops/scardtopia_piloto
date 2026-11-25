package br.com.scardtopia.gestao.service;

import br.com.scardtopia.gestao.form.VendaForm;
import br.com.scardtopia.gestao.model.*;
import br.com.scardtopia.gestao.repository.*;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendaService {
    private final VendaRepository vendas;
    private final ProdutoRepository produtos;
    private final ClienteRepository clientes;

    public VendaService(VendaRepository vendas, ProdutoRepository produtos, ClienteRepository clientes) {
        this.vendas = vendas; this.produtos = produtos; this.clientes = clientes;
    }

    @Transactional
    public Venda salvar(VendaForm form) {
        if (form.getQuantidade() == null || form.getQuantidade() < 1 || form.getStatus() == null) {
            throw new RegraNegocioException("Quantidade e status obrigatórios.");
        }
        Venda venda = form.getId() == null ? new Venda() : vendas.findById(form.getId())
            .orElseThrow(() -> new RegraNegocioException("Ordem não encontrada."));

        // Recuperar saldo antigo antes de recalcular a venda; rollback automático se falhar.
        if (venda.getId() != null && venda.getStatus() != StatusVenda.CANCELADA) {
            Produto anterior = produtos.buscarParaAtualizar(venda.getProduto().getId())
                .orElseThrow(() -> new RegraNegocioException("Produto anterior não encontrado."));
            anterior.setEstoque(anterior.getEstoque() + venda.getQuantidade());
        }
        Produto produto = produtos.buscarParaAtualizar(form.getProdutoId())
            .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
        Cliente cliente = clientes.findById(form.getClienteId())
            .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));

        // PENDENTE reserva estoque; CONFIRMADA mantém reserva; CANCELADA libera estoque.
        if (form.getStatus() != StatusVenda.CANCELADA) {
            if (produto.getEstoque() < form.getQuantidade()) {
                throw new RegraNegocioException("Estoque insuficiente: disponíveis " + produto.getEstoque() + " unidade(s).");
            }
            produto.setEstoque(produto.getEstoque() - form.getQuantidade());
        }
        venda.setProduto(produto);
        venda.setCliente(cliente);
        venda.setQuantidade(form.getQuantidade());
        venda.setStatus(form.getStatus());
        venda.setPrecoUnitario(produto.getPreco());
        venda.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(form.getQuantidade())));
        return vendas.save(venda);
    }

    @Transactional
    public void excluir(Long id) {
        Venda venda = vendas.findById(id).orElseThrow(() -> new RegraNegocioException("Ordem não encontrada."));
        if (venda.getStatus() != StatusVenda.CANCELADA) {
            Produto produto = produtos.buscarParaAtualizar(venda.getProduto().getId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
            produto.setEstoque(produto.getEstoque() + venda.getQuantidade());
        }
        vendas.delete(venda);
    }

    @Transactional(readOnly=true)
    public VendaForm criarFormParaEdicao(Long id) {
        Venda venda = vendas.findById(id).orElseThrow(() -> new RegraNegocioException("Ordem não encontrada."));
        VendaForm form = new VendaForm();
        form.setId(venda.getId());
        form.setClienteId(venda.getCliente().getId());
        form.setProdutoId(venda.getProduto().getId());
        form.setQuantidade(venda.getQuantidade());
        form.setStatus(venda.getStatus());
        return form;
    }
}
