package com.clyvoelo.painel.model;

import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import com.clyvoelo.painel.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "painel_evento_clinico")
public class EventoClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pet", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEvento tipoEvento;

    @Column(length = 500)
    private String descricao;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEvento status;

    @Column(name = "observacao_atendimento", length = 500)
    private String observacaoAtendimento;

    @ManyToOne
    @JoinColumn(name = "id_funcionario_atendeu")
    private Funcionario funcionarioAtendeu;

    @Column(name = "data_atendimento")
    private LocalDate dataAtendimento;

    public static EventoClinico agendar(Pet pet, TipoEvento tipo, String descricao, LocalDate data) {
        EventoClinico evento = new EventoClinico();
        evento.pet = pet;
        evento.tipoEvento = tipo;
        evento.descricao = descricao;
        evento.dataEvento = data;
        evento.status = StatusEvento.PENDENTE;
        return evento;
    }

    public void registrarAtendimento(Funcionario veterinario, String observacao) {
        if (estaConcluido()) {
            throw new RegraDeNegocioException("Este evento já foi atendido.");
        }
        this.funcionarioAtendeu = veterinario;
        this.observacaoAtendimento = observacao;
        this.dataAtendimento = LocalDate.now();
        this.status = StatusEvento.CONCLUIDO;
    }

    public void reagendar(LocalDate novaData) {
        if (estaConcluido()) {
            throw new RegraDeNegocioException("Um evento já atendido não pode ser reagendado.");
        }
        this.dataEvento = novaData;
        this.status = StatusEvento.PENDENTE;
    }

    public boolean estaConcluido() {
        return status == StatusEvento.CONCLUIDO;
    }

    public boolean estaAtrasado() {
        return !estaConcluido() && dataEvento != null && dataEvento.isBefore(LocalDate.now());
    }

    public boolean ehHoje() {
        return !estaConcluido() && LocalDate.now().equals(dataEvento);
    }

    public StatusEvento getSituacao() {
        return estaAtrasado() ? StatusEvento.ATRASADO : status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    public String getObservacaoAtendimento() { return observacaoAtendimento; }
    public void setObservacaoAtendimento(String observacaoAtendimento) { this.observacaoAtendimento = observacaoAtendimento; }

    public Funcionario getFuncionarioAtendeu() { return funcionarioAtendeu; }
    public void setFuncionarioAtendeu(Funcionario funcionarioAtendeu) { this.funcionarioAtendeu = funcionarioAtendeu; }

    public LocalDate getDataAtendimento() { return dataAtendimento; }
    public void setDataAtendimento(LocalDate dataAtendimento) { this.dataAtendimento = dataAtendimento; }
}
