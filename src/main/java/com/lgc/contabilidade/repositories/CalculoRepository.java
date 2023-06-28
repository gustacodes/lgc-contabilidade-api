package com.lgc.contabilidade.repositories;

import com.lgc.contabilidade.entities.Calculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculoRepository extends JpaRepository<Calculo, Long> {

}
