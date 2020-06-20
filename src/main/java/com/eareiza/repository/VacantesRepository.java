package com.eareiza.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eareiza.model.Vacante;

public interface VacantesRepository extends JpaRepository<Vacante, Integer> {
	//select * from vacantes where estatus = ?
	List<Vacante> findByEstatus(String estatus);
	
	List<Vacante> findByDestacadoAndEstatusOrderByIdDesc(int destacado, String estado);
	
	List<Vacante> findBySalarioBetweenOrderBySalarioDesc(double s1, double s2);
	
	List<Vacante> findByEstatusIn(String[] estatus);
	
}
