package com.catalogo.app;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String nome;

    @Column(nullable = false, unique = true, length = 180)
    public String email;

    @Column(nullable = false, length = 255)
    public String senhaHash;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();
}
