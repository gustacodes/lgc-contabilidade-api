package com.lgc.contabilidade.entities;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Relogio {
    static LocalTime somaHora = LocalTime.now();

    public static void main(String[] args) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime entrada = LocalTime.of(05, 50);
        LocalTime saidaAlmoco = LocalTime.of(11, 00);
        LocalTime voltaAlmoco = LocalTime.of(12, 00);
        LocalTime saidaCasa = LocalTime.of(15, 00);

        Duration primeiroIntervalo = Duration.between(entrada, saidaAlmoco);
        Duration segundoIntervalo = Duration.between(voltaAlmoco, saidaCasa);
        Duration total = primeiroIntervalo.plus(segundoIntervalo);

        somaHora.plusHours(total.toHours()).plusMinutes(total.toMinutes());

        long x = total.toHours();
        long y = total.toMinutes() % 60;

        LocalTime cargaOito = LocalTime.of(8,0);
        LocalTime este = LocalTime.of((int) x, (int) y);

        Duration totalExtra = Duration.between(cargaOito, este);

        long totalHoras = totalExtra.toHours();
        long totalMinutos = totalExtra.toMinutes() % 60;

        System.out.println(totalHoras + ":" + totalMinutos);

    }
}
