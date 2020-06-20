package com.eareiza.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eareiza.model.Perfil;
import com.eareiza.model.Vacante;
import com.eareiza.service.ICategoriasService;
import com.eareiza.service.IVacanteService;
import com.eareiza.util.Utileria;

@Controller
@RequestMapping(value="/vacantes")
public class VacantesController {
	
	@Value("${empleosapp.ruta.imagenes}")
	private String ruta;
	
	@Autowired
	private IVacanteService serviceVacantes;
	
	@Autowired
	@Qualifier("categoriasServiceJPA")
	private ICategoriasService serviceCategorias;
	
	@GetMapping("/index")
	public String mostrarIndex(Model modelo) {
		//1.- Obtener todas las vacantes (recuperarlas con la clase de servicio)
		List<Vacante> vacantes = serviceVacantes.buscarTodas();
		//2.- Agregar al modelo el listado de vacantes
		modelo.addAttribute("vacantes", vacantes);
		//3.- Renderizar las vacantes en la vista(integrar el archivo template empleos/listVacantes.html)

		//4.- Agregar al menu una opcion llamada "Vacantes" configurando la url
		return "vacantes/listVacantes";
	}
	
	@GetMapping(value="/indexPaginado")
	public String mostrarIndexPaginado(Model modelo, Pageable pagina) {
		//Se crea objeto de tipo page que almacena la lista de vacantes
		Page<Vacante> listaVacante = serviceVacantes.buscarTodas(pagina);
		//Se garega la lista al modelo
		modelo.addAttribute("vacantes", listaVacante);
		return "vacantes/listVacantes";
	}
	
	@GetMapping("/create")
	public String crearVacante(Vacante vacante, Model modelo) {
		return "vacantes/formVacante";
	}
	
	/*@PostMapping("/save")
	public String guardar(@RequestParam("nombre")String nombre, @RequestParam("descripcion")String descripcion,
			@RequestParam("estatus")String estatus, @RequestParam("fecha")String fecha, @RequestParam("destacado")int destacado,
			@RequestParam("salario")double salario, @RequestParam("detalles")String detalles) {
		System.out.println("nombre: "+nombre);
		System.out.println("descripcion: "+descripcion);
		System.out.println("estatus: "+estatus);
		System.out.println("fecha: "+fecha);
		System.out.println("destacado: "+destacado);
		System.out.println("salario: "+salario);
		System.out.println("detalles: "+detalles);
		return "vacantes/listVacantes";
	}*/
	
	@PostMapping("/save")
	public String guardar(Vacante vacante, BindingResult result, RedirectAttributes attr, @RequestParam("archivoImagen") MultipartFile multiPart) {
		if(result.hasErrors()){
			for (ObjectError error : result.getAllErrors()) {
				System.out.println("Ocurrio un error: "+ error.getDefaultMessage());
			}
			return "vacantes/formVacante";
		}
		
		if (!multiPart.isEmpty()) {
			//String ruta = "/empleos/img-vacantes/"; // Linux/MAC
			//String ruta = "c:/empleos/img-vacantes/"; // Windows
			String nombreImagen = Utileria.guardarArchivo(multiPart, ruta);
			if (nombreImagen != null){ // La imagen si se subio
				// Procesamos la variable nombreImagen
				vacante.setImagen(nombreImagen);
			}
		}
		
		serviceVacantes.guardar(vacante);
		attr.addFlashAttribute("msg", "Registro guardado.");
		System.out.println("Vacante: "+vacante);
		return "redirect:/vacantes/index";
	}
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") int idVacante, Model modelo, RedirectAttributes attr) {
		serviceVacantes.eliminar(idVacante);
		System.out.println("Vacante eliminada con el id "+idVacante);
		attr.addFlashAttribute("msg", "Registro eliminado.");
		modelo.addAttribute("id", idVacante);
		return "redirect:/vacantes/index";
	}
	
	@GetMapping("/edit/{id}")
	public String editarVacante(@PathVariable("id") int idVacante, Model modelo) {
		Vacante vacante = serviceVacantes.buscarPorId(idVacante);
		modelo.addAttribute("vacante", vacante);
		return "/vacantes/formVacante";
	}
	
	@GetMapping(value="/view/{id}")
	public String verDetalle(@PathVariable("id")int idVacante, Model modelo) {
		Vacante vacante= serviceVacantes.buscarPorId(idVacante);
		System.out.println("Vacande: "+vacante);
		modelo.addAttribute("vacante", vacante);
		//Buscar vacantes de la BDs
		return "detalle";
	}
	

	@ModelAttribute
	public void setGenericos(Model modelo) {
		modelo.addAttribute("categorias", serviceCategorias.buscarTodas());
	}
		
	//declarar un conversor de tipos fcha para que sea manejado por el controlador antes de almacenarlo en el bean
	//Aplica para todos los tipos de variable, tipo fecha
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}
}
