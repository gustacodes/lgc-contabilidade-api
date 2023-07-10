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

    Duration horasExtrasAcumuladas = Duration.ZERO;


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

        Duration cargaOito = Duration.ofHours(8);
        Duration horasTotais = Duration.ofHours(total.toHours()).plusMinutes(total.toMinutes() % 60);

        Duration totalExtra = horasTotais.minus(cargaOito);

        if (horasTotais.toHours() >= 8) {
            Duration horasExtras = Duration.ofHours( horasTotais.toHours() - 8).plusMinutes(totalExtra.toMinutes() % 60);
            horasExtrasAcumuladas = horasExtrasAcumuladas.plus(horasExtras);

        } else if (horasTotais.toHours() < 8) {
            Duration horasExtras = Duration.ofHours( 0).minusMinutes(totalExtra.toMinutes() % 60);
            horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
        }

        LocalTime localTime = LocalTime.of((int) horasExtrasAcumuladas.toHours(),(int) horasExtrasAcumuladas.toMinutesPart());
        String extras = localTime.format(formatter);
        Calculo.localTime = localTime;

        calculo.setExtras(extras);

        return calculo;
    }

    public void delete(Long id) {
        cr.deleteById(id);
    }

}
