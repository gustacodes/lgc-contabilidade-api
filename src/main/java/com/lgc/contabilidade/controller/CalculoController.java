package com.lgc.contabilidade.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.services.CalculoService;
import com.lgc.contabilidade.services.FuncionarioServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.io.PrintWriter;
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
        mv.addObject("calculo", calculos);

        return mv;
    }

    @GetMapping("/busca")
    public ModelAndView findByCodigo(@RequestParam("codigo") Long codigo, HttpServletResponse response) throws IOException {
        ModelAndView mv = new ModelAndView("index/calculadora");
        code = codigo;
        Funcionario funcionario = funcionarioServices.findByCodigo(codigo);

        PrintWriter out = response.getWriter();

        out.println("<script>");
        out.println("var mensagem = 'Funcionário(a): " + funcionario.getNome() + "';");
        out.println("alert(mensagem);");
        out.println("</script>");

        mv.addObject("funcionario", funcionario);
        mv.addObject("funcionarioobj", new Funcionario());

        return mv;
    }

    @GetMapping("/download-relatorio")
    public void downloadPdf(HttpServletRequest request, HttpServletResponse response) throws BadElementException, IOException {
        Funcionario funcionario = funcionarioServices.findByCodigo(code);
        List<Calculo> calculos = calculoService.findAll();
        calculoService.gerarRelatorio(funcionario, calculos, request, response);
    }

    @PostMapping
    public ModelAndView registro(@ModelAttribute("novoCalculo") Calculo calculo) {
        Funcionario cargo = funcionarioServices.findByCodigo(code);
        var funcionario = cargo;
        calculos.add(calculoService.save(calculoService.calculadora(calculo, funcionario)));

        return new ModelAndView("redirect:/ac/calculo");
    }

    @DeleteMapping("/{id}")
    public RedirectView deletar(@PathVariable Long id) {
        calculoService.delete(id);
        return new RedirectView("/ac/calculo");
    }

}
