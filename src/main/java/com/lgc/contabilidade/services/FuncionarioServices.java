package com.lgc.contabilidade.services;

import com.lgc.contabilidade.entities.Funcionario;
import com.lgc.contabilidade.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioServices {

    @Autowired
    private FuncionarioRepository fr;

    public Funcionario save(Funcionario funcionario) {
        return fr.save(funcionario);
    }

    public List<Funcionario> findAll() {
        return fr.findAll();
    }

    public void deleteById(Long id) {
        fr.deleteById(id);
    }

}
