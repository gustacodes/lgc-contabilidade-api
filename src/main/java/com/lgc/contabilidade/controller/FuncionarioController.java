package com.lgc.contabilidade.controller;

import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.services.FuncionarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("lgc/funcionarios")
public class FuncionarioController {

    @Autowired
    FuncionarioServices fs;

    @GetMapping
    public ModelAndView findAll() {
        ModelAndView mv = new ModelAndView("index/funcionarios");
        mv.addObject("funcionario", fs.findAll());
        return mv;
    }

    @PostMapping
    public ModelAndView save(@RequestBody Funcionario funcionario) {
        ModelAndView mv = new ModelAndView("index/funcionarios");
        fs.save(funcionario);
        mv.addObject("funcionario", fs.findAll());
        return new ModelAndView("redirect:/lgc/funcionarios");
    }
}
