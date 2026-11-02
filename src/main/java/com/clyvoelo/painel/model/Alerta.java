package com.clyvoelo.painel.model;

import com.clyvoelo.painel.model.enums.OrigemAlerta;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "painel_alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pet", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigemAlerta origem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeAlerta prioridade;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAlerta status;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_funcionario_responsavel")
    private Funcionario responsavel;

    @ManyToOne
    @JoinColumn(name = "id_tutor_relatou")
    private Tutor tutorRelatou;

    @Column(name = "nota_resolucao", length = 500)
    private String notaResolucao;

    @ManyToOne
    @JoinColumn(name = "id_funcionario_resolveu")
    private Funcionario funcionarioResolveu;

    @Column(name = "data_resolucao")
    private LocalDate dataResolucao;

    public static Alerta relatadoPeloTutor(Pet pet, Tutor tutor, PrioridadeAlerta prioridade, String descricao) {
        Alerta alerta = new Alerta();
        alerta.pet = pet;
        alerta.tutorRelatou = tutor;
        alerta.origem = OrigemAlerta.TUTOR;
        alerta.prioridade = prioridade;
        alerta.descricao = descricao;
        alerta.status = StatusAlerta.ABERTO;
        alerta.dataCriacao = LocalDate.now();
        return alerta;
    }

    public void assumir(Funcionario veterinario) {
        garantirQueNaoFoiResolvido();
        this.responsavel = veterinario;
        this.status = StatusAlerta.EM_ACOMPANHAMENTO;
    }

    public void resolver(Funcionario veterinario, String nota) {
        garantirQueNaoFoiResolvido();
        if (responsavel == null) {
            this.responsavel = veterinario;
        }
        this.funcionarioResolveu = veterinario;
        this.notaResolucao = nota;
        this.dataResolucao = LocalDate.now();
        this.status = StatusAlerta.RESOLVIDO;
    }

    public void garantirQueNaoFoiResolvido() {
        if (estaResolvido()) {
            throw new RegraDeNegocioException("Este alerta já foi resolvido.");
        }
    }

    public boolean estaResolvido() {
        return status == StatusAlerta.RESOLVIDO;
    }

    public long getDiasEmAberto() {
        if (dataCriacao == null) {
            return 0;
        }
        LocalDate fim = dataResolucao != null ? dataResolucao : LocalDate.now();
        return ChronoUnit.DAYS.between(dataCriacao, fim);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public OrigemAlerta getOrigem() { return origem; }
    public void setOrigem(OrigemAlerta origem) { this.origem = origem; }

    public PrioridadeAlerta getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeAlerta prioridade) { this.prioridade = prioridade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusAlerta getStatus() { return status; }
    public void setStatus(StatusAlerta status) { this.status = status; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario responsavel) { this.responsavel = responsavel; }

    public Tutor getTutorRelatou() { return tutorRelatou; }
    public void setTutorRelatou(Tutor tutorRelatou) { this.tutorRelatou = tutorRelatou; }

    public String getNotaResolucao() { return notaResolucao; }
    public void setNotaResolucao(String notaResolucao) { this.notaResolucao = notaResolucao; }

    public Funcionario getFuncionarioResolveu() { return funcionarioResolveu; }
    public void setFuncionarioResolveu(Funcionario funcionarioResolveu) { this.funcionarioResolveu = funcionarioResolveu; }

    public LocalDate getDataResolucao() { return dataResolucao; }
    public void setDataResolucao(LocalDate dataResolucao) { this.dataResolucao = dataResolucao; }
}
