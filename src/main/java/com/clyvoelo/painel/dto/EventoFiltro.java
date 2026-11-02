package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.enums.StatusEvento;
import com.clyvoelo.painel.model.enums.TipoEvento;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EventoFiltro {

    private StatusEvento status = StatusEvento.PENDENTE;
    private TipoEvento tipo;
    private Long idPet;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate inicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fim;

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    public TipoEvento getTipo() { return tipo; }
    public void setTipo(TipoEvento tipo) { this.tipo = tipo; }

    public Long getIdPet() { return idPet; }
    public void setIdPet(Long idPet) { this.idPet = idPet; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }
}
