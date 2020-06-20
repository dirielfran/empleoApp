
package com.eareiza.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eareiza.model.Categoria;
import com.eareiza.repository.CategoriasRepository;
import com.eareiza.service.ICategoriasService;

@Service
@Primary
public class CategoriasServiceJPA implements ICategoriasService {
	
	@Autowired
	private CategoriasRepository categoriasRepo; 

	@Override
	public void guardar(Categoria categoria) {
		categoriasRepo.save(categoria);
	}

	@Override
	public List<Categoria> buscarTodas() {
		return categoriasRepo.findAll();
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		// TODO Auto-generated method stub
		Optional<Categoria> opcional = categoriasRepo.findById(idCategoria);
		if(opcional.isPresent()) {
			return opcional.get();
		}
		return null;
	}

	@Override
	public void eliminar(int idCategoria) {
		categoriasRepo.deleteById(idCategoria);		
	}

	@Override
	public void updateCategoria(Categoria categoria) {
		categoriasRepo.save(categoria);		
	}

	@Override
	public Page<Categoria> buscarTodas(Pageable pagina) {
		return categoriasRepo.findAll(pagina);
	}
}
