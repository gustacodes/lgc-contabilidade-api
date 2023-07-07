package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.repositories.CalculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class CalculoService {

    @Autowired
    private CalculoRepository cr;

    private static boolean positivo = true;

    public Iterable<Calculo> findAll() {
        return cr.findAll();
    }

    public Calculo registro(Calculo calculo) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime entrada = LocalTime.parse(calculo.getEntrada(), formatter);
        LocalTime saidaAlmoco = LocalTime.parse(calculo.getSaidaAlmoco(), formatter);
        LocalTime voltaAlmoco = LocalTime.parse(calculo.getVoltaAlmoco(), formatter);
        LocalTime saidaCasa = LocalTime.parse(calculo.getSaidaCasa(), formatter);

        Duration primeiroIntervalo = Duration.between(entrada, saidaAlmoco);
        Duration segundoIntervalo = Duration.between(voltaAlmoco, saidaCasa);
        Duration total = primeiroIntervalo.plus(segundoIntervalo);

        String extras = "";

        long totalHoras = total.toHours();
        long totalMinutos = total.toMinutes() % 60;
        long auxi = 0;

        if (totalHoras < 8) {

            totalMinutos -= 60;
            Calculo.minutoExtra += (int) totalMinutos;

            if(Calculo.minutoExtra < 0 && positivo == false) {
                int aux = Math.abs(Calculo.minutoExtra);
                Calculo.minutoExtra = 60 - aux;
            }

            Calculo.horaExtra = (int) totalHoras;
            long positivoMinutos = Math.abs(totalMinutos);
            long positivoHoras = Math.abs(totalHoras);

            if (positivoMinutos > 59) {
                positivoMinutos %= 60;
            }

            if(Calculo.horaExtra < 0) {
                Calculo.horaExtra = 0;
            }

            LocalTime localTime = LocalTime.of((int) positivoHoras, (int) positivoMinutos);
            extras = localTime.format(formatter);
            calculo.setExtras("- " + extras);

        } else if (totalHoras > 8) {

            positivo = false;

            totalHoras -= 8;
            LocalTime localTime = LocalTime.of((int) totalHoras, (int) totalMinutos);
            Duration duration = Duration.between(LocalTime.MIDNIGHT, localTime);

            LocalTime lt = LocalTime.MIDNIGHT.plus(duration);

            Calculo.horaExtra += lt.getHour();
            Calculo.minutoExtra += lt.getMinute();

            if(Calculo.minutoExtra >= 60) {
                Calculo.minutoExtra -= 60;
                Calculo.horaExtra += 1;
            }

            if(Calculo.horaExtra > 8) {
                Calculo.horaExtra += Calculo.horaExtra - 8;
                Calculo.minutoExtra += Calculo.minutoExtra - 60;
            }

            if(Calculo.minutoExtra < 0) {
                Calculo.minutoExtra += 60;
                Calculo.horaExtra -= 1;
            }

            extras = localTime.format(formatter);
            calculo.setExtras(extras);

        } else {
            totalHoras = 0;
        }

        LocalTime totalH = LocalTime.of((int) total.toHours(), (int) total.toMinutes() % 60);

        String totalHo = totalH.format(formatter);
        calculo.setHorasTotais(totalHo);
        cr.save(calculo);

        return calculo;
    }

    public void delete(Long id) {
        cr.deleteById(id);
    }

}
