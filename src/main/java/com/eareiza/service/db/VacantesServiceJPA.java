package com.eareiza.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eareiza.model.Vacante;
import com.eareiza.repository.VacantesRepository;
import com.eareiza.service.IVacanteService;

@Service
@Primary
public class VacantesServiceJPA implements IVacanteService {

	@Autowired
	private VacantesRepository vacantesRepo;
	
	@Override
	public List<Vacante> buscarTodas() {
		return vacantesRepo.findAll();
	}

	@Override
	public Vacante buscarPorId(Integer id) {
		Optional<Vacante> opcional = vacantesRepo.findById(id);
		if(opcional.isPresent()) {
			return opcional.get();
		}
		return null;
	}

	@Override
	public void guardar(Vacante vacante) {
		vacantesRepo.save(vacante);
	}

	@Override
	public List<Vacante> buscarDestacadas() {
		return vacantesRepo.findByDestacadoAndEstatusOrderByIdDesc(1, "Aprobada");
	}

	@Override
	public void eliminar(int idVacante) {
		vacantesRepo.deleteById(idVacante);		
	}

	@Override
	public List<Vacante> buscarByExample(Example<Vacante> example) {
		return vacantesRepo.findAll(example);
	}

	@Override
	public Page<Vacante> buscarTodas(Pageable pagina) {
		return vacantesRepo.findAll(pagina);
	}	

}
