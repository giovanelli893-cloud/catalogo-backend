package com.catalogo.app;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/lojas")
class LojaController {

    @PersistenceContext
    private EntityManager em;

    // Termo simples por enquanto (depois a gente coloca versão, data, IP, etc.)
    public static final String TERMO_LOJISTA =
            "TERMO DE RESPONSABILIDADE DO ANUNCIANTE\n\n" +
            "Ao se cadastrar, o anunciante declara ser o responsável legal pela loja/prestador e assume total responsabilidade " +
            "pelos produtos/serviços anunciados, incluindo informações, preços, disponibilidade, qualidade, garantias e eventuais " +
            "danos ao consumidor. O aplicativo atua apenas como catálogo/divulgação e não intermedia pagamentos, entregas ou negociações.\n\n" +
            "O anunciante confirma que possui CPF/CNPJ válido e autoriza o armazenamento dos dados cadastrais para fins de gestão, " +
            "segurança, prevenção a fraudes e cumprimento legal.\n";

    static class LojaRegisterRequest {
        public String nomeFantasia;
        public String razaoSocial;
        public String documentoResponsavel; // CPF/CNPJ
        public String responsavelNome;
        public String telefone;
        public String endereco;
        public String cidade;
        public String categoria;
        public String horarioFuncionamento;
        public Boolean aceitouTermos;
    }

    @GetMapping("/termos")
    public ResponseEntity<?> termos() {
        return ResponseEntity.ok(TERMO_LOJISTA);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody LojaRegisterRequest req) {
        if (req == null ||
            req.nomeFantasia == null ||
            req.razaoSocial == null ||
            req.documentoResponsavel == null ||
            req.responsavelNome == null ||
            req.telefone == null ||
            req.endereco == null ||
            req.cidade == null ||
            req.categoria == null ||
            req.horarioFuncionamento == null ||
            req.aceitouTermos == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados incompletos");
        }

        if (!Boolean.TRUE.equals(req.aceitouTermos)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("É obrigatório aceitar os termos");
        }

        Loja l = new Loja();
        l.nomeFantasia = req.nomeFantasia.trim();
        l.razaoSocial = req.razaoSocial.trim();
        l.documentoResponsavel = req.documentoResponsavel.trim();
        l.responsavelNome = req.responsavelNome.trim();
        l.telefone = req.telefone.trim();
        l.endereco = req.endereco.trim();
        l.cidade = req.cidade.trim();
        l.categoria = req.categoria.trim();
        l.horarioFuncionamento = req.horarioFuncionamento.trim();

        em.persist(l);

        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
