package com.lgc.contabilidade.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.services.CalculoService;
import com.lgc.contabilidade.services.FuncionarioServices;
import com.lowagie.text.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ac/calculo")
public class CalculoController {

    @Autowired
    private CalculoService calculoService;

    @Autowired
    private FuncionarioServices funcionarioServices;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private static Long code;

    private List<Calculo> calculos = new ArrayList<>();

    @GetMapping
    public ModelAndView findAll() {

        Iterable<Calculo> calculos = calculoService.findAll();

        ModelAndView mv = new ModelAndView("index/calculadora");
        mv.addObject("totalHora", Calculo.horasExtrasSomadas);
        mv.addObject("calculo", calculos);

        return mv;
    }

    @PostMapping("/busca")
    public ModelAndView findByCodigo(@RequestParam("codigo") Long codigo) {
        ModelAndView mv = new ModelAndView("index/calculadora");
        code = codigo;
        Funcionario funcionario = funcionarioServices.findByCodigo(codigo);
        mv.addObject("funcionario", funcionario);
        mv.addObject("funcionarioobj", new Funcionario());

        return mv;
    }

    @GetMapping("/download-pdf")
    public void downloadPdf(HttpServletRequest request, HttpServletResponse response) {

        Funcionario funcionario = funcionarioServices.findByCodigo(code);
        List<Calculo> calculos = calculoService.findAll();
        Document relatorio = new Document();

        try {

            response.setContentType("apllication/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + "Relatorio " + funcionario.getNome().toUpperCase() + ".pdf");
            PdfWriter.getInstance(relatorio, response.getOutputStream());
            relatorio.open();
            relatorio.add(new Paragraph("Relatório de horas extras"));
            relatorio.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(7);

            PdfPCell data =  new PdfPCell(new Paragraph("Data"));
            PdfPCell entrada =  new PdfPCell(new Paragraph("Entrada"));
            PdfPCell saidaAlmoco =  new PdfPCell(new Paragraph("Saída/Almoço"));
            PdfPCell voltaAlmoco =  new PdfPCell(new Paragraph("Volta/Almoço"));
            PdfPCell saida =  new PdfPCell(new Paragraph("Saída"));
            PdfPCell totalDia =  new PdfPCell(new Paragraph("Total/Dia"));
            PdfPCell extrasDia =  new PdfPCell(new Paragraph("Extras/Dia"));

            tabela.addCell(data);
            tabela.addCell(entrada);
            tabela.addCell(saidaAlmoco);
            tabela.addCell(voltaAlmoco);
            tabela.addCell(saida);
            tabela.addCell(totalDia);
            tabela.addCell(extrasDia);

            for(int i = 0; i < calculos.size(); i++) {
                tabela.addCell(String.valueOf(calculos.get(i).getData()));
                tabela.addCell(calculos.get(i).getEntrada());
                tabela.addCell(calculos.get(i).getSaidaAlmoco());
                tabela.addCell(calculos.get(i).getVoltaAlmoco());
                tabela.addCell(calculos.get(i).getSaidaCasa());
                tabela.addCell(calculos.get(i).getHorasTotais());
                tabela.addCell(calculos.get(i).getExtras());
            }

            relatorio.add(tabela);
            relatorio.close();

        } catch (Exception e) {
            System.out.println(e);
            relatorio.close();
        }
    }


    @PostMapping
    public ModelAndView registro(@ModelAttribute("novoCalculo") Calculo calculo, Funcionario cargo) {
        calculos.add(calculoService.calculadora(calculo, cargo));
        return new ModelAndView("redirect:/ac/calculo");
    }

    @DeleteMapping("/{id}")
    public RedirectView deletar(@PathVariable Long id) {
        calculoService.delete(id);
        return new RedirectView("/ac/calculo");
    }

}
