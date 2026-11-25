package br.com.scardtopia.gestao.service;

import br.com.scardtopia.gestao.form.VendaForm;
import br.com.scardtopia.gestao.model.*;
import br.com.scardtopia.gestao.repository.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {
    @Mock VendaRepository vendas;
    @Mock ProdutoRepository produtos;
    @Mock ClienteRepository clientes;
    @InjectMocks VendaService servico;
    Produto produto;
    Cliente cliente;
    VendaForm form;

    @BeforeEach
    void preparar() {
        produto = new Produto(); produto.setId(1L); produto.setNome("Secret Lair x Dune: Spice");
        produto.setEstoque(5); produto.setPreco(new BigDecimal("450.00"));
        cliente = new Cliente(); cliente.setId(2L); cliente.setNome("Cliente de teste");
        form = new VendaForm(); form.setProdutoId(1L); form.setClienteId(2L);
        form.setQuantidade(2); form.setStatus(StatusVenda.PENDENTE);
    }

    @Test
    void novaVendaPendenteDeveReservarEstoqueECalcularTotal() {
        when(produtos.buscarParaAtualizar(1L)).thenReturn(Optional.of(produto));
        when(clientes.findById(2L)).thenReturn(Optional.of(cliente));
        when(vendas.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));
        Venda salva = servico.salvar(form);
        assertEquals(3, produto.getEstoque());
        assertEquals(new BigDecimal("900.00"), salva.getTotal());
        assertEquals(StatusVenda.PENDENTE, salva.getStatus());
        verify(vendas).save(any(Venda.class));
    }

    @Test
    void quantidadeMaiorQueEstoqueNaoPodeSerVendida() {
        form.setQuantidade(9);
        when(produtos.buscarParaAtualizar(1L)).thenReturn(Optional.of(produto));
        when(clientes.findById(2L)).thenReturn(Optional.of(cliente));
        assertThrows(RegraNegocioException.class, () -> servico.salvar(form));
        assertEquals(5, produto.getEstoque());
        verify(vendas, never()).save(any());
    }

    @Test
    void vendaCanceladaNaoDescontaEstoque() {
        form.setStatus(StatusVenda.CANCELADA);
        when(produtos.buscarParaAtualizar(1L)).thenReturn(Optional.of(produto));
        when(clientes.findById(2L)).thenReturn(Optional.of(cliente));
        when(vendas.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));
        servico.salvar(form);
        assertEquals(5, produto.getEstoque());
    }

    @Test
    void excluirVendaPendenteDeveReporEstoque() {
        Venda venda = new Venda(); venda.setId(10L); venda.setProduto(produto);
        venda.setQuantidade(2); venda.setStatus(StatusVenda.PENDENTE);
        when(vendas.findById(10L)).thenReturn(Optional.of(venda));
        when(produtos.buscarParaAtualizar(1L)).thenReturn(Optional.of(produto));
        servico.excluir(10L);
        assertEquals(7, produto.getEstoque());
        verify(vendas).delete(venda);
    }
}
