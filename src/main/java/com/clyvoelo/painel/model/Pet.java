package com.clyvoelo.painel.model;

import com.clyvoelo.painel.model.enums.Especie;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "painel_pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Long id;

    @NotBlank(message = "Informe o nome do pet")
    @Size(max = 80, message = "O nome pode ter no máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nome;

    @NotNull(message = "Selecione a espécie")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especie especie;

    @Size(max = 80, message = "A raça pode ter no máximo 80 caracteres")
    @Column(length = 80)
    private String raca;

    @DecimalMin(value = "0.1", message = "O peso deve ser maior que zero")
    @DecimalMax(value = "150.0", message = "O peso não pode passar de 150 kg")
    @Column(name = "peso_kg")
    private BigDecimal pesoKg;

    @PastOrPresent(message = "A data de nascimento não pode estar no futuro")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @NotNull(message = "Selecione o tutor")
    @ManyToOne
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutor tutor;

    public String getIdadeFormatada() {
        if (dataNascimento == null) {
            return "Idade não informada";
        }
        Period idade = Period.between(dataNascimento, LocalDate.now());
        if (idade.getYears() > 0) {
            return idade.getYears() + (idade.getYears() == 1 ? " ano" : " anos");
        }
        int meses = Math.max(idade.getMonths(), 0);
        return meses + (meses == 1 ? " mês" : " meses");
    }

    public String getPesoFormatado() {
        if (pesoKg == null) {
            return "Não informado";
        }
        return pesoKg.stripTrailingZeros().toPlainString().replace('.', ',') + " kg";
    }

    public boolean pertenceAo(Tutor outroTutor) {
        return tutor != null && outroTutor != null && tutor.getId().equals(outroTutor.getId());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
}
