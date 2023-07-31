package com.lgc.contabilidade.repositories;

import com.lgc.contabilidade.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query("SELECT p FROM Funcionario p WHERE p.codigo = :codigo")
    Funcionario findByCodigo(Long codigo);
}
