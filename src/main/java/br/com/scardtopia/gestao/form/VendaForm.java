package br.com.scardtopia.gestao.form;

import br.com.scardtopia.gestao.model.StatusVenda;
import jakarta.validation.constraints.*;

public class VendaForm {
    private Long id;
    @NotNull(message="Selecione um cliente")
    private Long clienteId;
    @NotNull(message="Selecione um produto")
    private Long produtoId;
    @NotNull @Min(value=1, message="A quantidade deve ser maior que zero")
    private Integer quantidade = 1;
    @NotNull(message="Selecione um status")
    private StatusVenda status = StatusVenda.PENDENTE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public StatusVenda getStatus() { return status; }
    public void setStatus(StatusVenda status) { this.status = status; }
}
