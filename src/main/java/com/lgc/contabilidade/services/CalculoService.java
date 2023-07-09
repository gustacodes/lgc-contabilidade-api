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

        long horas = total.toHours();
        long minutos = total.toMinutes() % 60;

        LocalTime cargaOito = LocalTime.of(8,0);
        LocalTime horasTotais = LocalTime.of((int) horas, (int) minutos);

        Duration totalExtra = Duration.between(cargaOito, horasTotais);

        LocalTime localTime = LocalTime.of((int) totalExtra.toHours(),(int) totalExtra.toMinutes() % 60);
        String extras = localTime.format(formatter);
        Calculo.localTime = localTime;

        calculo.setExtras(extras);

        return calculo;
    }

    public void delete(Long id) {
        cr.deleteById(id);
    }

}
