package br.com.scardtopia.gestao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
public class Venda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="produto_id", nullable=false, foreignKey=@ForeignKey(name="fk_venda_produto"))
    private Produto produto;
    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="cliente_id", nullable=false, foreignKey=@ForeignKey(name="fk_venda_cliente"))
    private Cliente cliente;
    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer quantidade;
    @NotNull
    @Column(name="preco_unitario", nullable=false, precision=12, scale=2)
    private BigDecimal precoUnitario;
    @NotNull
    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private StatusVenda status;
    @Column(name="criado_em",nullable=false,updatable=false)
    private LocalDateTime criadoEm;

    @PrePersist
    void onCreate() { criadoEm = LocalDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public StatusVenda getStatus() { return status; }
    public void setStatus(StatusVenda status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
