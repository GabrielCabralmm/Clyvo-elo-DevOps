package com.clyvoelo.painel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "painel_alerta_acompanhamento")
public class AcompanhamentoAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acompanhamento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_alerta", nullable = false)
    private Alerta alerta;

    @Column(nullable = false, length = 120)
    private String autor;

    @Column(nullable = false, length = 500)
    private String anotacao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    public static AcompanhamentoAlerta registrar(Alerta alerta, String autor, String anotacao) {
        AcompanhamentoAlerta acompanhamento = new AcompanhamentoAlerta();
        acompanhamento.alerta = alerta;
        acompanhamento.autor = autor;
        acompanhamento.anotacao = anotacao;
        acompanhamento.dataRegistro = LocalDateTime.now();
        return acompanhamento;
    }

    public Long getId() { return id; }
    public Alerta getAlerta() { return alerta; }
    public String getAutor() { return autor; }
    public String getAnotacao() { return anotacao; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
}
