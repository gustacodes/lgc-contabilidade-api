package com.lgc.contabilidade.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "calculo")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date data;
    private LocalTime entrada;
    private LocalTime saidaAlmoco;
    private LocalTime voltaAlmoco;
    private LocalTime saidaCasa;

}
