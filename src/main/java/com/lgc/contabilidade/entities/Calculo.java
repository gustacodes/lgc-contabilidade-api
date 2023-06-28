package com.lgc.contabilidade.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "calculo")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;
    private LocalTime entrada;
    private LocalTime saidaAlmoco;
    private LocalTime voltaAlmoco;
    private LocalTime saidaCasa;

}
