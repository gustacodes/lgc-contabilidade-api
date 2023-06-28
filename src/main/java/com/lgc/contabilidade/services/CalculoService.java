package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.repositories.CalculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

@Service
public class CalculoService {

    @Autowired
    private CalculoRepository cr;

    public Calculo registro(Calculo calculo) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime entrada = LocalTime.parse(calculo.getEntrada(), formatter);
        LocalTime saidaAlmoco = LocalTime.parse(calculo.getSaidaAlmoco(), formatter);
        LocalTime voltaAlmoco = LocalTime.parse(calculo.getVoltaAlmoco(), formatter);
        LocalTime saidaCasa = LocalTime.parse(calculo.getSaidaCasa(), formatter);

        Duration primeiroIntervalo = Duration.between(entrada, saidaAlmoco);
        Duration segundoIntervalo = Duration.between(voltaAlmoco, saidaCasa);
        Duration total = primeiroIntervalo.plus(segundoIntervalo);

        long totalHoras = total.toHours();
        long totalMinutos = total.toMinutes() % 60;

        if (totalHoras > 8) {
            totalHoras -= 8;
        } else {
            totalHoras = 0;
        }

        LocalTime localTime = LocalTime.of((int) totalHoras, (int) totalMinutos);
        LocalTime totalH = LocalTime.of((int) total.toHours(), (int) total.toMinutes() % 60);
        String extras = localTime.format(formatter);
        String totalHo = totalH.format(formatter);
        calculo.setHorasTotais(totalHo);
        calculo.setExtras(extras);

        return calculo;
    }

}
