package com.lgc.contabilidade.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.repositories.CalculoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CalculoService {

    @Autowired
    private CalculoRepository cr;

    public static Duration horasExtrasAcumuladas = Duration.ZERO;
    Duration horasExtras = Duration.ZERO;

    public List<Calculo> findAll() {
        return cr.findAll();
    }

    public Calculo save(Calculo calculo) {
        return cr.save(calculo);
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

            } else if (horasTotaisBalconista.toHours() <= 7) {

                if (horasTotaisBalconista.toHours() == 7 && horasTotaisBalconista.toMinutesPart() < 20) {
                    horasExtras = Duration.ofHours(totalExtraBalconista.toHours()).minusMinutes(totalExtraBalconista.toMinutes() % 60);
                } else {
                    horasExtras = Duration.ofHours(totalExtraBalconista.toHours()).minusMinutes(totalExtraBalconista.toMinutes() % 60);
                }

                horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
                horaExtra = horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

                calculo.setExtras(horaExtra);

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

            } else if (horasTotais.toHours() <= 7) {

                if (horasTotais.toHours() == 7 && horasTotais.toMinutesPart() > 0) {
                    horasExtras = Duration.ofHours(horasTotais.toHours() - 7).minusMinutes(totalExtra.toMinutes() % 60);
                } else {
                    horasExtras = Duration.ofHours(horasTotais.toHours()).minusMinutes(totalExtra.toMinutes() % 60);
                }

                horasExtrasAcumuladas = horasExtrasAcumuladas.minus(horasExtras);
                horaExtra = "-" + horasExtras.toHours() + ":" + horasExtras.toMinutesPart();
                Calculo.horasExtrasSomadas = horasExtrasAcumuladas.toHours() + ":" + horasExtrasAcumuladas.toMinutesPart();

            }

            calculo.setExtras(horaExtra);
        }

        return calculo;
    }

    public static String transformarHoraNegativaEmPositiva(String horaNegativa) {

        String[] partes = horaNegativa.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);

        if (horas < 0 || minutos < 0) {
            int totalMinutos = Math.abs(horas) * 60 + Math.abs(minutos);
            int horasPositivas = totalMinutos / 60;
            int minutosPositivos = totalMinutos % 60;

            return String.format("%d:%02d", horasPositivas, minutosPositivos);
        }

        return horaNegativa;
    }

    public void gerarRelatorio(Funcionario funcionario, List<Calculo> calculos, HttpServletRequest request, HttpServletResponse response) throws BadElementException, IOException {

        Document relatorio = new Document();
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontCabecalho = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

        try {

            response.setContentType("apllication/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + "Relatorio " + funcionario.getNome().toUpperCase() + ".pdf");
            PdfWriter.getInstance(relatorio, response.getOutputStream());
            relatorio.open();
            relatorio.add(new Paragraph("                     GABRIELA ALENCAR - CONTABILIDADE", fontCabecalho));
            relatorio.add(new Paragraph("                         Registro de Horas Extras", fontCabecalho));
            relatorio.add(new Paragraph(" "));
            relatorio.add(new Paragraph("Funcionário(a): " + funcionario.getNome()));
            relatorio.add(new Paragraph("Cargo: " + funcionario.getCargo() + "                        Jornada: " + ((funcionario.getCargo()).equalsIgnoreCase("Balconista") ? "7:20h" : "8:00h")));
            relatorio.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(7);
            tabela.setWidthPercentage(100);

            PdfPCell data = new PdfPCell(new Paragraph("Data", font));
            data.setBackgroundColor(BaseColor.LIGHT_GRAY);
            data.setBorderColor(BaseColor.DARK_GRAY);
            data.setBorderWidth(0.5f);
            data.setPadding(5f);
            tabela.addCell(data).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell entrada = new PdfPCell(new Paragraph("Entrada", font));
            entrada.setBackgroundColor(BaseColor.LIGHT_GRAY);
            entrada.setBorderColor(BaseColor.DARK_GRAY);
            entrada.setBorderWidth(0.5f);
            entrada.setPadding(5f);
            tabela.addCell(entrada).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell saidaAlmoco = new PdfPCell(new Paragraph("Saída/Int.", font));
            saidaAlmoco.setBackgroundColor(BaseColor.LIGHT_GRAY);
            saidaAlmoco.setBorderColor(BaseColor.DARK_GRAY);
            saidaAlmoco.setBorderWidth(0.5f);
            saidaAlmoco.setPadding(5f);
            tabela.addCell(saidaAlmoco).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell voltaAlmoco = new PdfPCell(new Paragraph("Volta/Int.", font));
            voltaAlmoco.setBackgroundColor(BaseColor.LIGHT_GRAY);
            voltaAlmoco.setBorderColor(BaseColor.DARK_GRAY);
            voltaAlmoco.setBorderWidth(0.5f);
            voltaAlmoco.setPadding(5f);
            tabela.addCell(voltaAlmoco).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell saida = new PdfPCell(new Paragraph("Saída", font));
            saida.setBackgroundColor(BaseColor.LIGHT_GRAY);
            saida.setBorderColor(BaseColor.DARK_GRAY);
            saida.setBorderWidth(0.5f);
            saida.setPadding(5f);
            tabela.addCell(saida).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell totalDia = new PdfPCell(new Paragraph("Total/Dia", font));
            totalDia.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalDia.setBorderColor(BaseColor.DARK_GRAY);
            totalDia.setBorderWidth(0.5f);
            totalDia.setPadding(5f);
            tabela.addCell(totalDia).setHorizontalAlignment((Element.ALIGN_CENTER));

            PdfPCell extrasDia = new PdfPCell(new Paragraph("Extras/Dia", font));
            extrasDia.setBackgroundColor(BaseColor.LIGHT_GRAY);
            extrasDia.setBorderColor(BaseColor.DARK_GRAY);
            extrasDia.setBorderWidth(0.5f);
            extrasDia.setPadding(5f);
            tabela.addCell(extrasDia).setHorizontalAlignment((Element.ALIGN_CENTER));

            for (int i = 0; i < calculos.size(); i++) {

                tabela.addCell(String.valueOf(calculos.get(i).getData()));
                tabela.addCell(calculos.get(i).getEntrada());
                tabela.addCell(calculos.get(i).getSaidaAlmoco());
                tabela.addCell(calculos.get(i).getVoltaAlmoco());
                tabela.addCell(calculos.get(i).getSaidaCasa());
                tabela.addCell(calculos.get(i).getHorasTotais());
                tabela.addCell(calculos.get(i).getExtras());

            }

            relatorio.add(tabela);
            relatorio.add(new Paragraph(" "));
            relatorio.add(new Paragraph("Status: " + (Calculo.horasExtrasSomadas.contains("-") ? "Devendo" : "Extras á calcular")));
            relatorio.add(new Paragraph("Total/Extras: " + Calculo.horasExtrasSomadas));
            relatorio.close();
            horasExtrasAcumuladas = Duration.ZERO;
            cr.deleteAll();

        } catch (Exception e) {
            e.printStackTrace();
            relatorio.close();
        }

    }

    public void delete(Long id) {
        cr.deleteById(id);
    }
}
