package com.lgc.contabilidade.controller;

import com.itextpdf.text.*;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.lgc.contabilidade.services.CalculoService.horasExtrasAcumuladas;

@Controller
@RequestMapping("/gabriela/contabilidade/calculo")
public class CalculoController {

    @Autowired
    private CalculoService calculoService;

    @Autowired
    private FuncionarioServices funcionarioServices;

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
    public ModelAndView downloadPdf(HttpServletRequest request, HttpServletResponse response) throws BadElementException, IOException {
        
        ModelAndView mv = new ModelAndView("index/calculadora");
        
        Funcionario funcionario = funcionarioServices.findByCodigo(code);
        List<Calculo> calculos = calculoService.findAll();

        if(calculos.isEmpty()) {
            mv.addObject("erro", "Não há registros para salvar.");
            return mv;
        }

        calculoService.gerarRelatorio(funcionario, calculos, request, response);

        return mv;
    }

    @PostMapping
    public ModelAndView registro(@ModelAttribute("novoCalculo") Calculo calculo) {
        Funcionario cargo = funcionarioServices.findByCodigo(code);
        var funcionario = cargo;

        if(funcionario == null) {
            ModelAndView mv = new ModelAndView("index/calculadora");
            mv.addObject("erro", "Funcionário não encontrado.");
            return mv;
        }

        calculos.add(calculoService.save(calculoService.calculadora(calculo, funcionario)));

        return new ModelAndView("redirect:/gabriela/contabilidade/calculo");
    }

    @DeleteMapping("/{id}")
    public RedirectView deletar(@PathVariable Long id, @RequestParam("extras") String extras) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        String horas = CalculoService.transformarHoraNegativaEmPositiva(extras);
        LocalTime entrada = LocalTime.now();

        if (horas.equalsIgnoreCase("0:0")) {
            horas = "0:00";
            entrada = LocalTime.parse(horas, formatter);
        }

        if (extras.contains("-")) {            
            horasExtrasAcumuladas = horasExtrasAcumuladas.plusHours(entrada.getHour()).plusMinutes(entrada.getMinute());

        } else {

            if (extras.equalsIgnoreCase("0:0")) {
                extras = "0:00";
            }

            entrada = LocalTime.parse(extras, formatter);
            horasExtrasAcumuladas = horasExtrasAcumuladas.minusHours(entrada.getHour()).minusMinutes(entrada.getMinute());
        }

        calculoService.delete(id);
        return new RedirectView("/gabriela/contabilidade/calculo");
    }

}
