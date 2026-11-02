package com.clyvoelo.painel.dto;

import com.clyvoelo.painel.model.enums.OrigemAlerta;
import com.clyvoelo.painel.model.enums.PrioridadeAlerta;
import com.clyvoelo.painel.model.enums.StatusAlerta;

public class AlertaFiltro {

    private StatusAlerta status;
    private PrioridadeAlerta prioridade;
    private OrigemAlerta origem;
    private boolean somenteAtivos = true;

    public StatusAlerta getStatus() { return status; }
    public void setStatus(StatusAlerta status) { this.status = status; }

    public PrioridadeAlerta getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeAlerta prioridade) { this.prioridade = prioridade; }

    public OrigemAlerta getOrigem() { return origem; }
    public void setOrigem(OrigemAlerta origem) { this.origem = origem; }

    public boolean isSomenteAtivos() { return somenteAtivos; }
    public void setSomenteAtivos(boolean somenteAtivos) { this.somenteAtivos = somenteAtivos; }
}
