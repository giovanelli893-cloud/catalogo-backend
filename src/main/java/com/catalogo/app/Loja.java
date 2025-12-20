package com.catalogo.app;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    public String nomeFantasia;

    @Column(nullable = false, length = 180)
    public String razaoSocial;

    @Column(nullable = false, length = 20)
    public String documentoResponsavel; // CPF ou CNPJ

    @Column(nullable = false, length = 120)
    public String responsavelNome;

    @Column(nullable = false, length = 30)
    public String telefone;

    @Column(nullable = false, length = 180)
    public String endereco;

    @Column(nullable = false, length = 120)
    public String cidade;

    @Column(nullable = false, length = 120)
    public String categoria; // ex: "Mercado", "Autopeças", "Serviço"

    @Column(nullable = false, length = 200)
    public String horarioFuncionamento; // ex: "Seg-Sex 08:00-18:00; Sab 08:00-12:00"

    @Column(nullable = false)
    public boolean ativo = true;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    public Long getId() { return id; }
}
