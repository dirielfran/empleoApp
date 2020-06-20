package com.eareiza.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eareiza.model.Usuario;
import com.eareiza.repository.UsuariosRepository;
import com.eareiza.service.IUsuariosService;

@Service
public class UsuariosServiceJPA implements IUsuariosService {
	
	@Autowired
	private UsuariosRepository usuariosRepo;

	@Override
	public void guardar(Usuario usuario) {
		usuariosRepo.save(usuario);
	}

	@Override
	public void eliminar(Integer idUsuario) {
		usuariosRepo.deleteById(idUsuario);
	}

	@Override
	public List<Usuario> buscarTodos() {
		return usuariosRepo.findAll();
	}

	@Override
	public Usuario buscarProId(Integer idUsuario) {
		Optional<Usuario> op = usuariosRepo.findById(idUsuario);
		if (op.isPresent()) {
			return op.get();
		}
		return null;
	}
	//Metodo que retorna usuario por atributo Username
	@Override
	public Usuario buscarPorUsername(String user) {
		return usuariosRepo.findByUsername(user);
	}
}
