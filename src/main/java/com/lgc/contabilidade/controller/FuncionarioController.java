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
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("index/funcionarios");
        mv.addObject("funcionarios", funcionarioServices.findAll());
        mv.addObject("funcionario", new Funcionario());
        return mv;
    }

    @PostMapping("/cadastro")
    public ModelAndView save(Funcionario funcionario, @RequestParam("opcao") String opcaoSelecionada) {
        
        if(opcaoSelecionada.isEmpty()) {
            funcionario.setCargo("Outro");
        } else {
            funcionario.setCargo(opcaoSelecionada);
        }

        funcionarioServices.save(funcionario);
        return new ModelAndView("redirect:/gabriela/contabilidade/funcionarios");
        
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteById(@PathVariable Long id) {
        funcionarioServices.deleteById(id);
        return new ModelAndView("redirect:/gabriela/contabilidade/funcionarios");
    }

}