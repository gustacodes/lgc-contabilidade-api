package com.lgc.contabilidade.controller;

import com.lgc.contabilidade.entities.Calculo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calculo")
public class CalculoController {

    private List<Calculo> calculos = new ArrayList<>();

    @GetMapping
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index/index");
        mv.addObject("calculo", calculos);
        mv.addObject("novoCalculo", new Calculo());

        return mv;
    }

    @PostMapping
    public ModelAndView registro(@ModelAttribute("novoCalculo") Calculo calculo) {
        calculos.add(calculo);
        return new ModelAndView("redirect:/calculo");
    }


}
