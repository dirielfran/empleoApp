package com.eareiza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eareiza.model.Categoria;
import com.eareiza.model.Usuario;
import com.eareiza.service.IUsuariosService;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private IUsuariosService servicesUsuarios;
	
    @GetMapping("/index")
	public String mostrarIndex(Model modelo) {
    	List<Usuario> usuarios = servicesUsuarios.buscarTodos();
    	modelo.addAttribute("usuarios", usuarios);
    	return "usuarios/listUsuarios";
	}
    
    @GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") int idUsuario, RedirectAttributes attr) {		    	
    	servicesUsuarios.eliminar(idUsuario);
    	attr.addFlashAttribute("msg","El registro fue eliminado con exito.");
		return "redirect:/usuarios/index";
	}
    
    @GetMapping("/edit/{id}")
    public String editUsuario(@PathVariable("id") int idUsuario, Model modelo) {
    	Usuario usuario = servicesUsuarios.buscarProId(idUsuario); 	
    	modelo.addAttribute("usuario", usuario);
    	return "usuarios/formRegistro";
    }
    
    @GetMapping("/look/{id}")
    public String lookUsuario(@PathVariable("id")int idUsuario, RedirectAttributes attr) {
    	Usuario usuario = servicesUsuarios.buscarProId(idUsuario);
    	usuario.setEstatus(0);
    	servicesUsuarios.guardar(usuario);
    	attr.addFlashAttribute("msg","El usuario fue bloqueado.");
    	return "redirect:/usuarios/index";
    }
    
    @GetMapping("/unlook/{id}")
    public String desbloquear(@PathVariable("id")int idUsuario, RedirectAttributes attr) {
    	Usuario usuario = servicesUsuarios.buscarProId(idUsuario);
    	usuario.setEstatus(1);
    	servicesUsuarios.guardar(usuario);
    	attr.addFlashAttribute("msg","El usuario fue desbloqueado.");
    	return "redirect:/usuarios/index";
    }
}
