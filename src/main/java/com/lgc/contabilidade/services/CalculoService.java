package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.repositories.CalculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;

import static com.lgc.contabilidade.entities.Calculo.hour;

@Service
public class CalculoService {

    @Autowired
    private CalculoRepository cr;

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

        long totalHoras = total.toHours();
        long totalMinutos = total.toMinutes() % 60;

        String extras = "", somatorio = "";

        if(totalHoras < 8){

            totalHoras = 0;
            totalMinutos -= 60;
            Calculo.minute += (int) totalMinutos;
            Calculo.hour -= (int) totalHoras;
            long positivo = Math.abs(totalMinutos);
            long positivoHoras = Math.abs(totalHoras);
            LocalTime localTime = LocalTime.of((int) positivoHoras, (int) positivo);
            extras = localTime.format(formatter);
            calculo.setExtras("- " + extras);

        } else if (totalHoras >= 8) {

            totalHoras -= 8;
            LocalTime localTime = LocalTime.of((int) totalHoras, (int) totalMinutos);
            Duration duration = Duration.between(LocalTime.MIDNIGHT, localTime);

            LocalTime lt = LocalTime.MIDNIGHT.plus(duration);

            Calculo.hour += lt.getHour();
            Calculo.minute += lt.getMinute();

            if(Calculo.minute >= 60) {
                Calculo.minute -= 60;
                hour += 1;
            }

            calculo.setTotal(somatorio);
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
