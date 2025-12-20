package com.catalogo.app;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/produtos")
class ProdutoController {

    @PersistenceContext
    private EntityManager em;

    static class ProdutoCreateRequest {
        public Long lojaId;
        public String nome;
        public String categoria;
        public String descricao;
        public Double preco;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody ProdutoCreateRequest req) {
        if (req == null || req.lojaId == null || req.nome == null || req.categoria == null || req.descricao == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados incompletos");
        }

        Long lojaExists = em.createQuery("select count(l) from Loja l where l.id = :id and l.ativo = true", Long.class)
                .setParameter("id", req.lojaId)
                .getSingleResult();

        if (lojaExists == null || lojaExists == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loja não encontrada");
        }

        Produto p = new Produto();
        p.lojaId = req.lojaId;
        p.nome = req.nome.trim();
        p.categoria = req.categoria.trim();
        p.descricao = req.descricao.trim();
        p.preco = req.preco;

        em.persist(p);

        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
