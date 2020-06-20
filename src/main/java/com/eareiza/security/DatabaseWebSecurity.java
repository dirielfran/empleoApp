package com.eareiza.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//Se agrega anotacion para que lo detecte como un archivo de configuracion
@Configuration
//Se le añade anotacion para que tenga soporte de seguridad
@EnableWebSecurity
public class DatabaseWebSecurity extends WebSecurityConfigurerAdapter {
	//En tiempo de ejecucion se realiza inyeccion de la instancia del datasource configurado en el archivo application.properties
	@Autowired
	private DataSource dataSource;
	// Se crea metodo para authenticacion en la base de datos configurada en el DataSource
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
		//Se realiza consulta a la tabla usuarios(propia del desarrollo) devolviendo username,password y estatus
		.usersByUsernameQuery("select username,password, estatus from Usuarios where username = ?")
		//Se realiza consulta a la tabla usuarios, usuarioperfil y perfil(propia del desarrollo) devolviendo username y perfil
		.authoritiesByUsernameQuery("select u.username, p.perfil from UsuarioPerfil up " +
				"inner join Usuarios u on u.id = up.idUsuario " +
				"inner join Perfiles p on p.id = up.idPerfil " +
				"where u.username = ?");
	}
	
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		//Los recursos estaticos no reuieren autenticacion
		.antMatchers("/bootstrap/**",
				"/images/**",
				"/tinymce/**",
				"/logos/**").permitAll()
		
		//Las vistas publicas no requieren autenticacion
		.antMatchers("/",
				"/signup",
				"/search",
				"/create",
				"/save",
				"/encriptacion/**",
				"/vacantes/view/**").permitAll()
		
		//Se configura acceso a las urls segun role
		.antMatchers("/vacantes/**").hasAnyAuthority("SUPERVISOR", "ADMINISTRADOR")
		.antMatchers("/categorias/**").hasAnyAuthority("SUPERVISOR", "ADMINISTRADOR")
		.antMatchers("/usuarios/**").hasAnyAuthority("ADMINISTRADOR")
		
		//Todas las demas urls de la aplacacion requieren autenticacion
		.anyRequest().authenticated()
		
		//El formulario  de Login no requiere autenticacion, se indica por medio de loginPage la pagina que renderiza el Login
		.and().formLogin().loginPage("/login").permitAll();
	}
	
	//Se agrega anotacion para en tiempo de ejecucion se cree un bean del metodo en el contexto
	@Bean
	//Se crea metodo de encriptado
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
