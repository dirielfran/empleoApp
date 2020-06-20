package com.eareiza.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eareiza.model.Usuario;

public interface UsuariosRepository extends JpaRepository<Usuario, Integer> {
	//Metodo que busca un usuario por su username
	Usuario findByUsername(String user);
	
}
