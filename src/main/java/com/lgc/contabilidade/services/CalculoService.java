package com.lgc.contabilidade.services;

import com.lgc.contabilidade.repositories.CalculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculoService {

    @Autowired
    private CalculoRepository cr;


}
