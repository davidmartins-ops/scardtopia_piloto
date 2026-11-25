package br.com.scardtopia.gestao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(name="uk_cliente_email", columnNames="email"))
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;
    @NotBlank @Email @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @NotBlank @Size(max = 25)
    @Column(nullable = false, length = 25)
    private String telefone;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
