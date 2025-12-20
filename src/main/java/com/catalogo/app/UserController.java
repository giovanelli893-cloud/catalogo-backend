package com.catalogo.app;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/users")
class UserController {

    @PersistenceContext
    private EntityManager em;

    static class RegisterRequest {
        public String nome;
        public String email;
        public String senha;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req == null || req.nome == null || req.email == null || req.senha == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados incompletos");
        }

        String email = req.email.trim().toLowerCase();
        if (email.isEmpty() || req.senha.trim().isEmpty() || req.nome.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
        }

        Long exists = em.createQuery("select count(u) from User u where u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();

        if (exists != null && exists > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já cadastrado");
        }

        User u = new User();
        u.nome = req.nome.trim();
        u.email = email;
        u.senhaHash = sha256(req.senha.trim());

        em.persist(u);

        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping
public ResponseEntity<?> list() {
    return ResponseEntity.ok(
        em.createQuery(
            "select u from User u",
            User.class
        ).getResultList()
    );
}

}
