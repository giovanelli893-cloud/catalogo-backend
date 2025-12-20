package com.catalogo.app;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.Instant;

@RestController
@RequestMapping("/admin")
class AdminController {

    @PersistenceContext
    private EntityManager em;

    private void requireAdmin(String token) {
        String expected = System.getenv("ADMIN_TOKEN");
        if (expected == null || expected.isBlank()) {
            throw new RuntimeException("ADMIN_TOKEN não configurado");
        }
        if (token == null || !expected.equals(token)) {
            throw new RuntimeException("Acesso negado");
        }
    }

    // ======= LISTAR LOJAS =======
    @GetMapping("/lojas")
    public ResponseEntity<?> listLojas(@RequestHeader(value = "X-ADMIN-TOKEN", required = false) String token) {
        try {
            requireAdmin(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        return ResponseEntity.ok(
                em.createQuery("select l from Loja l order by l.id desc", Loja.class).getResultList()
        );
    }

    // ======= BANIR / DESATIVAR =======
    @PostMapping("/lojas/{id}/banir")
    @Transactional
    public ResponseEntity<?> banirLoja(@RequestHeader(value = "X-ADMIN-TOKEN", required = false) String token,
                                      @PathVariable("id") Long id) {
        try {
            requireAdmin(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        Loja l = em.find(Loja.class, id);
        if (l == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loja não encontrada");

        l.ativo = false;
        return ResponseEntity.ok("OK");
    }

    // ======= REATIVAR =======
    @PostMapping("/lojas/{id}/reativar")
    @Transactional
    public ResponseEntity<?> reativarLoja(@RequestHeader(value = "X-ADMIN-TOKEN", required = false) String token,
                                         @PathVariable("id") Long id) {
        try {
            requireAdmin(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        Loja l = em.find(Loja.class, id);
        if (l == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loja não encontrada");

        l.ativo = true;
        return ResponseEntity.ok("OK");
    }

    // ======= LOG SIMPLES DE AÇÕES ADMIN (por enquanto só imprime) =======
    private void adminLog(String msg) {
        System.out.println(Instant.now() + " [ADMIN] " + msg);
    }
}
