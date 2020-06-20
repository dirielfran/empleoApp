package com.eareiza.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eareiza.model.Perfil;
import com.eareiza.model.Usuario;
import com.eareiza.model.Vacante;
import com.eareiza.service.ICategoriasService;
import com.eareiza.service.IUsuariosService;
import com.eareiza.service.IVacanteService;

@Controller
public class HomeController {
	
	
	@Autowired
	private ICategoriasService serviceCategorias;
	@Autowired
	private IVacanteService serviceVacantes;
	@Autowired
	private IUsuariosService servicesUsuario;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/listado")
	public String mostrarListado(Model modelo) {
		List<String> lista = new LinkedList<String>();
		lista.add("Ingeniero en Informatica");
		lista.add("Auxiliar de Contabilidad");
		lista.add("Arquitecto");
		lista.add("Vendedor");
		modelo.addAttribute("empleos", lista);
		return "listado";
	}
	
	@GetMapping("/")
	public String motrarHome(Model modelo) {
//		List<Vacante> vacantes = serviceVacantes.buscarDestacadas();
//		modelo.addAttribute("vacantes", vacantes);
		return "home";
	}
	
	
	@GetMapping("/detalle")
	public String mostrarDetalle(Model modelo) {
		Vacante vacante = new Vacante();
		vacante.setNombre("Ingeniero de Comunicaciones");
		vacante.setDescripcion("Se solicita ingeniero para dar soporte a internet");
		vacante.setFecha(new Date());
		vacante.setSalario((double) 9700);
		modelo.addAttribute("vacante", vacante);
		return "detalle";
	}
	
	@GetMapping("/tabla")
	public String mostrarTabla(Model modelo) {
		List<Vacante> listaVac = serviceVacantes.buscarTodas();
		modelo.addAttribute("vacantes", listaVac);
		return "tabla";
	}
	
	@GetMapping("/create")
    public String crearUsuario(Usuario usuario, Model modelo) {
    	return "usuarios/formRegistro";
    }
    
    @PostMapping("/save")
	public String guardarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr, Model modelo) {
    	
    	String passPlano = usuario.getPassword();
    	String passEncriptado = passwordEncoder.encode(passPlano);
    	usuario.setPassword(passEncriptado);
    	
		if(result.hasErrors()) {
			return "usuarios/formRegistro";
		}
    	usuario.setEstatus(1);
		usuario.setFechaRegistro(new Date());
		Perfil perfil = new Perfil();
		perfil.setId(3);
		
		try {
			usuario.agregar(perfil);
			servicesUsuario.guardar(usuario);	
		}catch(Exception e) {
			result.addError(new ObjectError("excepcion",e.getMessage() ));
			return "usuarios/formRegistro";
		}		
		attr.addFlashAttribute("msg", "El registro fue guardado correctamente.");
		return "redirect:/usuarios/index";
	}
    
    @GetMapping("/search")
    public String buscar(@ModelAttribute("vacanteSearch")Vacante vacante, Model modelo) {
    	System.out.println("La vacante: "+vacante);
    	//e modifica el metodo buscar para que busque la vacante con el operador 
    	//like y no con el =, con el objeto ExampleMatcher
    	ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("descripcion", ExampleMatcher.GenericPropertyMatchers.contains());
    	//Se crea obj de tipo Example
    	Example<Vacante> example = Example.of(vacante, matcher);
    	//Se crea lista utilizando el metodo buscarByExample y pasandole el obj de tipo Vacante
    	List<Vacante> listVacante = serviceVacantes.buscarByExample(example);
    	modelo.addAttribute("vacantes", listVacante);
    	return "home";
    }
    
    @GetMapping(value="/index")
    //Se le añade un parametro de tipo Authentication, que tiene metodos para recuperar inf. del usuario que inicio sesion
    public String mostrarIndex(Authentication auth, HttpSession sesion) {
    	//Se llama al metodo getName del obj Authentication para recuperar el nombre
    	String username = auth.getName();
    	System.out.println("Se ha logueado el usuario: "+username);
    	//Se recupera los perfiles del usuario con el metodo getAuthorities, que devuelve un permisos de autorizacion
    	for (GrantedAuthority item : auth.getAuthorities()) {
			System.out.println("rol: "+item.getAuthority());
		}
    	//Se valida si hay un atributo usario
    	if(sesion.getAttribute("usuario") == null) {
    		//Se recupera el usuario de la Base de Datos
    		Usuario user = servicesUsuario.buscarPorUsername(username);
    		//Se setea el pass a null para que no venga encriptada
    		user.setPassword(null);
    		//Se imprime el usuario
        	System.out.println(user.toString());
        	//Se agraga el usuario a la session
        	sesion.setAttribute("usuario", user);
    	}
    	
    	//Redireccionamiento al directorio raiz de la aplicacion
    	return "redirect:/";
    }
    
    @GetMapping("/login")
    public String mostrarLogin() {
    	return"formLogin";
    }
    
    @GetMapping("/logout")
    public String cerrarSession(HttpServletRequest request) {
    	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    	logoutHandler.logout(request, null, null);
    	return "redirect:/login";
    }
    
    @GetMapping("/encriptacion/{keyPlano}")
    //Anotacion para que redirija directamente al body de la pagina
    @ResponseBody
    //Metodo de utilidad para encriptar
    public String enciptacion(@PathVariable("keyPlano")String texto) {
    	return texto+" Encriptado en BCrypt "+passwordEncoder.encode(texto);
    }
    /*
     * InitBinder para String, si lo detecta vacio en el data Binding los setea a NULL
     * */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
    
    @ModelAttribute
	public void setGenericos(Model modelo) {
    	Vacante vacanteSerch  = new Vacante();
    	vacanteSerch.resetImagen();
		modelo.addAttribute("vacantes", serviceVacantes.buscarDestacadas());
		modelo.addAttribute("categorias", serviceCategorias.buscarTodas());
		modelo.addAttribute("vacanteSearch", vacanteSerch);
	}
} 
