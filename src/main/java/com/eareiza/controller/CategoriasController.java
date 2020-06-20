package com.eareiza.controller;

import java.util.List;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eareiza.model.Categoria;
import com.eareiza.service.ICategoriasService;
import com.eareiza.service.db.CategoriasServiceJPA;

@Controller
@RequestMapping(value="/categorias")
public class CategoriasController {

	@Autowired
  	@Qualifier("categoriasServiceJPA")
	private ICategoriasService categoriasServices;
	

	
	// @GetMapping("/index")
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String mostrarIndex(Model model) {
		List<Categoria> lista = categoriasServices.buscarTodas();
		model.addAttribute("categorias", lista);
		return "categorias/listCategorias";
	}
	
	@GetMapping(value="/indexPaginado")
	public String mostrarIndexPaginado(Model modelo, Pageable pagina) {
		//Se crea objeto de tipo Page que contiene la lista de categorias
		Page<Categoria> listCategorias = categoriasServices.buscarTodas(pagina);
		//Se agrega al modelo la lista 
		modelo.addAttribute("categorias", listCategorias);
		return "categorias/listCategorias";
	}
	
	// @GetMapping("/create")
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String crear(Categoria categoria) {
		return "categorias/formCategorias";
	}
	
	// @PostMapping("/save")
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String guardar(Categoria categoria,BindingResult result ,RedirectAttributes attr) {
		if(result.hasErrors()) {
			for(ObjectError error : result.getAllErrors()) {
				System.out.println("Ocurrio un error: "+error.getDefaultMessage());;
			}
			return "categorias/formCategorias";
		}
		categoriasServices.guardar(categoria);
		attr.addFlashAttribute("msg", "Registro guardado.");
		return "redirect:/categorias/index";
	}	
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public String eliminarCategoria(@PathVariable("id") int idCategoria, RedirectAttributes attr ) {        
		try {
			categoriasServices.eliminar(idCategoria);
			attr.addFlashAttribute("msg", "Categoria eliminada.");
			System.out.println("Se elimino la categoria "+idCategoria);
		} catch (Exception e) {
			attr.addFlashAttribute("msg", "Nos puede eliminar la categoria, tiene vacantes asociadas.");
		}  
		return "redirect:/categorias/index";
	}
	
	@GetMapping("/update/{id}")
	public String editCategoria(@PathVariable("id") int idCategoria, Model modelo) {
		Categoria categoria = categoriasServices.buscarPorId(idCategoria);
		modelo.addAttribute("categoria", categoria);
		return "categorias/formCategorias";
	}
}
