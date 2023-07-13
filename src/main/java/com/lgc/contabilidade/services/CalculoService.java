package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.repositories.CalculoRepository;
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
    Duration horasExtras = Duration.ZERO;

    public Iterable<Calculo> findAll() {
        return cr.findAll();
    }

    public Calculo calculadora(Calculo calculo, Funcionario cargo) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String horaExtra = "";

        LocalTime entrada = LocalTime.parse(calculo.getEntrada(), formatter);
        LocalTime saidaAlmoco = LocalTime.parse(calculo.getSaidaAlmoco(), formatter);
        LocalTime voltaAlmoco = LocalTime.parse(calculo.getVoltaAlmoco(), formatter);
        LocalTime saidaCasa = LocalTime.parse(calculo.getSaidaCasa(), formatter);

        Duration primeiroIntervalo = Duration.between(entrada, saidaAlmoco);
        Duration segundoIntervalo = Duration.between(voltaAlmoco, saidaCasa);
        Duration total = primeiroIntervalo.plus(segundoIntervalo);

        Duration cargaHoraria = Duration.ZERO;

        if (cargo.getCargo().equalsIgnoreCase("Balconista")) {

            cargaHoraria = Duration.ofHours(7).plusMinutes(20);

            Duration horasTotaisBalconista = Duration.ofHours(total.toHours()).plusMinutes(total.toMinutes() % 60);
            calculo.setHorasTotais(horasTotaisBalconista.toHours() + ":" + horasTotaisBalconista.toMinutesPart());

            Duration totalExtraBalconista = horasTotaisBalconista.minus(cargaHoraria);

            if (horasTotaisBalconista.toHours() >= 7) {

                Duration horasExtras = Duration.ofHours(totalExtraBalconista.toHours()).plusMinutes(totalExtraBalconista.toMinutes() % 60);

                horasExtrasAcumuladas = horasExtrasAcumuladas.plus(horasExtras);
                horaExtra = horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

                calculo.setExtras(horaExtra);
                cr.save(calculo);


            } else if (horasTotaisBalconista.toHours() <= 7) {

                if (horasTotaisBalconista.toHours() == 7 && horasTotaisBalconista.toMinutesPart() < 20) {
                    horasExtras = Duration.ofHours(totalExtraBalconista.toHours()).minusMinutes(totalExtraBalconista.toMinutes() % 60);
                } else {
                    horasExtras = Duration.ofHours(totalExtraBalconista.toHours()).minusMinutes(totalExtraBalconista.toMinutes() % 60);
                }

                horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
                horaExtra = "-" + horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

                calculo.setExtras(horaExtra);
                cr.save(calculo);

            }

        } else {

            cargaHoraria = Duration.ofHours(8);

            Duration horasTotais = Duration.ofHours(total.toHours()).plusMinutes(total.toMinutes() % 60);
            calculo.setHorasTotais(horasTotais.toHours() + ":" + horasTotais.toMinutesPart());

            Duration totalExtra = horasTotais.minus(cargaHoraria);

            if (horasTotais.toHours() >= 8) {

                Duration horasExtras = Duration.ofHours(totalExtra.toHours()).plusMinutes(totalExtra.toMinutes() % 60);
                horasExtrasAcumuladas = horasExtrasAcumuladas.plus(horasExtras);
                horaExtra = horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

                calculo.setExtras(horaExtra);
                cr.save(calculo);

            } else if (horasTotais.toHours() <= 7) {

                if (horasTotais.toHours() == 7 && horasTotais.toMinutesPart() > 0) {
                    horasExtras = Duration.ofHours(7 - horasTotais.toHours()).minusMinutes(totalExtra.toMinutes() % 60);
                } else {
                    horasExtras = Duration.ofHours(8 - horasTotais.toHours()).minusMinutes(totalExtra.toMinutes() % 60);
                }

                horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
                horaExtra = "-" + horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

            }

            calculo.setExtras(horaExtra);
            cr.save(calculo);
        }

        return calculo;
    }

    public void delete(Long id) {
        cr.deleteById(id);
    }
}
