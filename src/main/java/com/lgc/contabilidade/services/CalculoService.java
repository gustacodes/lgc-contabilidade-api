package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.repositories.CalculoRepository;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

        Duration cargaHoraria = Duration.ofHours(8);
        Duration horasTotais = Duration.ofHours(total.toHours()).plusMinutes(total.toMinutes() % 60);
        String format = DurationFormatUtils.formatDuration(horasTotais.toMillis(), "HH:mm");
        calculo.setHorasTotais(format);

        Duration totalExtra = horasTotais.minus(cargaHoraria);

        if (horasTotais.toHours() >= 8) {

            Duration horasExtras = Duration.ofHours(horasTotais.toHours() - 8).plusMinutes(totalExtra.toMinutes() % 60);
            horasExtrasAcumuladas = horasExtrasAcumuladas.plus(horasExtras);
            String horaExtra = horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
            Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

            calculo.setExtras(horaExtra);
            cr.save(calculo);

        } else if (horasTotais.toHours() <= 7) {

            Duration horasExtras = Duration.ofHours(horasTotais.toHours() - 7).minusMinutes(totalExtra.toMinutes() % 60);
            horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
            String horaExtra = horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
            Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

            calculo.setExtras(horaExtra);
            cr.save(calculo);
        }

        return calculo;
    }

    public void delete(Long id) {
        cr.deleteById(id);
    }

}
