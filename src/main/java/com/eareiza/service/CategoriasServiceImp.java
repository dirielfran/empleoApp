package com.eareiza.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eareiza.model.Categoria;

@Service
public class CategoriasServiceImp implements ICategoriasService{
	
	private List<Categoria> listaCateg= null;
	
	
	
	public CategoriasServiceImp() {
		super();
		listaCateg = new LinkedList<Categoria>();
		Categoria categoria1 = new Categoria();
		Categoria categoria2 = new Categoria();
		Categoria categoria3 = new Categoria();
		Categoria categoria4 = new Categoria();
		
		categoria1.setId(1);
		categoria1.setNombre("Recursos Humanos");
		categoria1.setDescripcion("Trabajos relacionados con el area de RH.");
		
		categoria2.setId(2);
		categoria2.setNombre("Ventas");
		categoria2.setDescripcion("Ofertas de trabajo relacionado con ventas.");
		
		categoria3.setId(3);
		categoria3.setNombre("Arquitectura");
		categoria3.setDescripcion("Diseño de planos en general y trabajos relacionados.");
		
		categoria4.setId(4);
		categoria4.setNombre("Desarrollo del Software");
		categoria4.setDescripcion("Trabajo para programadores.");
		
		listaCateg.add(categoria1);
		listaCateg.add(categoria2);
		listaCateg.add(categoria3);
		listaCateg.add(categoria4);
	}

	@Override
	public void guardar(Categoria categoria) {
		listaCateg.add(categoria);		
	}

	@Override
	public List<Categoria> buscarTodas() {
		return listaCateg;
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		for (Categoria categoria : listaCateg) {
			if(categoria.getId() == idCategoria) {
				return categoria;
			}
		}
		return null;
	}

	@Override
	public void eliminar(int idCategoria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Page<Categoria> buscarTodas(Pageable pagina) {
		// TODO Auto-generated method stub
		return null;
	}

}
