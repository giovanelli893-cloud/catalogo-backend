package com.catalogo.app;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public Long lojaId; // vínculo com Loja

    @Column(nullable = false, length = 180)
    public String nome;

    @Column(nullable = false, length = 120)
    public String categoria; // ex: "Alimento", "Serviço", "Eletrônico"

    @Column(nullable = false, length = 400)
    public String descricao;

    @Column(nullable = true)
    public Double preco; // pode ser null (serviço sob orçamento)

    @Column(nullable = false)
    public boolean ativo = true;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    public Long getId() { return id; }
}
