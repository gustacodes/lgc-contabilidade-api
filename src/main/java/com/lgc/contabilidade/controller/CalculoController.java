package com.lgc.contabilidade.controller;

import com.lgc.contabilidade.entities.Calculo;
import com.lgc.contabilidade.services.CalculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/lgc/calculo")
public class CalculoController {

    @Autowired
    private CalculoService cs;

    private List<Calculo> calculos = new ArrayList<>();

    @GetMapping
    public ModelAndView findAll() {

        Iterable<Calculo> calculos = cs.findAll();

        ModelAndView mv = new ModelAndView("index/index");
        mv.addObject("totalHora", Calculo.localTime);
        mv.addObject("calculo", calculos);

        return mv;
    }

    @PostMapping
    public ModelAndView registro(@ModelAttribute("novoCalculo") Calculo calculo) {
        calculos.add(cs.registro(calculo));
        return new ModelAndView("redirect:/lgc/calculo");
    }

    @DeleteMapping("/{id}")
    public RedirectView deletar(@PathVariable Long id) {
        cs.delete(id);
        return new RedirectView("/lgc/calculo");
    }

}
