package projeto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projeto.models.Historico;
import projeto.repository.HistoricoRepository;

@RestController
@RequestMapping("/historicos")
public class HistoricoResources {
	
	@Autowired
	private HistoricoRepository hr;
	
	@GetMapping
	public List<Historico> listaHistoricos() {
		return hr.findAll();
	}

}
