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
    @GetMapping("/search")
public ResponseEntity<?> search(
        @RequestParam(name = "q", required = false) String q,
        @RequestParam(name = "cidade", required = false) String cidade
) {
    String qq = (q == null) ? "" : q.trim().toLowerCase();
    String cc = (cidade == null) ? "" : cidade.trim().toLowerCase();

    return ResponseEntity.ok(
        em.createQuery(
            "select p from Produto p " +
            "where p.ativo = true " +
            "and (:qq = '' or lower(p.nome) like concat('%', :qq, '%') or lower(p.categoria) like concat('%', :qq, '%'))",
            Produto.class
        )
        .setParameter("qq", qq)
        .getResultList()
    );
}
static class ProdutoLojaResult {
    public Long produtoId;
    public String produtoNome;
    public String produtoCategoria;
    public String produtoDescricao;
    public Double produtoPreco;

    public Long lojaId;
    public String lojaNomeFantasia;
    public String lojaTelefone;
    public String lojaCidade;
    public String lojaHorario;

    public ProdutoLojaResult(Long produtoId, String produtoNome, String produtoCategoria, String produtoDescricao, Double produtoPreco,
                             Long lojaId, String lojaNomeFantasia, String lojaTelefone, String lojaCidade, String lojaHorario) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.produtoCategoria = produtoCategoria;
        this.produtoDescricao = produtoDescricao;
        this.produtoPreco = produtoPreco;
        this.lojaId = lojaId;
        this.lojaNomeFantasia = lojaNomeFantasia;
        this.lojaTelefone = lojaTelefone;
        this.lojaCidade = lojaCidade;
        this.lojaHorario = lojaHorario;
    }
}

@GetMapping("/search2")
public ResponseEntity<?> search2(
        @RequestParam(name = "q", required = false) String q,
        @RequestParam(name = "cidade", required = false) String cidade
) {
    String qq = (q == null) ? "" : q.trim().toLowerCase();
    String cc = (cidade == null) ? "" : cidade.trim().toLowerCase();

    return ResponseEntity.ok(
        em.createQuery(
            "select new com.catalogo.app.ProdutoController$ProdutoLojaResult(" +
            "p.id, p.nome, p.categoria, p.descricao, p.preco, " +
            "l.id, l.nomeFantasia, l.telefone, l.cidade, l.horarioFuncionamento" +
            ") " +
            "from Produto p, Loja l " +
            "where p.ativo = true and l.ativo = true and p.lojaId = l.id " +
            "and (l.paidUntil is not null and l.paidUntil >= current_date) " +
            "and (:qq = '' or lower(p.nome) like concat('%', :qq, '%') or lower(p.categoria) like concat('%', :qq, '%')) " +
            "and (:cc = '' or lower(l.cidade) = :cc)",
            ProdutoLojaResult.class
        )
        .setParameter("qq", qq)
        .setParameter("cc", cc)
        .getResultList()
    );
}

}
