package com.lgc.contabilidade.controller;

import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.services.FuncionarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/gabriela/contabilidade/funcionarios")
public class FuncionarioController {

    @Autowired
    FuncionarioServices funcionarioServices;

    @GetMapping
    public ModelAndView findAll() {
        ModelAndView mv = new ModelAndView("index/funcionarios");
        mv.addObject("funcionario", funcionarioServices.findAll());
        return mv;
    }

    @PostMapping
    public ModelAndView save(Funcionario funcionario, @RequestParam("opcao") String opcaoSelecionada) {
        ModelAndView mv = new ModelAndView("index/funcionarios");

        if(opcaoSelecionada.isEmpty()) {
            funcionario.setCargo("Outro");
        } else {
            funcionario.setCargo(opcaoSelecionada);
        }

        funcionarioServices.save(funcionario);
        mv.addObject("funcionario", funcionarioServices.findAll());
        return new ModelAndView("redirect:/gabriela/contabilidade/funcionarios");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteById(@PathVariable Long id) {
        funcionarioServices.deleteById(id);
        return new ModelAndView("redirect:/gabriela/contabilidade/funcionarios");
    }

}