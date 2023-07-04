package com.lgc.contabilidade.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "calculo")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;
    @NotBlank(message = "Entrada obrigatoria")
    private String entrada;
    @NotBlank
    private String saidaAlmoco;
    @NotBlank
    private String voltaAlmoco;
    @NotBlank
    private String saidaCasa;
    private String horasTotais;
    private String extras;
    private String total;
    public static int hour;
    public static int minute;

}
