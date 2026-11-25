package br.com.scardtopia.gestao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos", uniqueConstraints = @UniqueConstraint(name="uk_produto_codigo", columnNames="codigo"))
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;
    @NotBlank @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    private String codigo;
    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String categoria;
    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String edicao;
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;
    @NotNull @Min(0)
    @Column(nullable = false)
    private Integer estoque = 0;
    @Column(nullable = false)
    private boolean foil;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEdicao() { return edicao; }
    public void setEdicao(String edicao) { this.edicao = edicao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public boolean isFoil() { return foil; }
    public void setFoil(boolean foil) { this.foil = foil; }
}
