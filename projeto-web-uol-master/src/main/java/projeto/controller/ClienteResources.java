package projeto.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import projeto.models.Cliente;
import projeto.models.Clima;
import projeto.models.Geolocalizacao;
import projeto.models.Historico;
import projeto.models.Localidade;
import projeto.repository.ClienteRepository;
import projeto.repository.DataRepository;
import projeto.repository.HistoricoRepository;
import projeto.repository.LocalidadeRepository;

@RestController
@RequestMapping("/clientes")
public class ClienteResources {
	
	@Autowired
	private ClienteRepository cr;
	
	@Autowired
	private HistoricoRepository hr;
	
	@Autowired
	private LocalidadeRepository lr;
	
	@Autowired
	private DataRepository dr;
	
	@GetMapping
	public List<Cliente> listaClientes() {
		return cr.findAll();
	}
	
	@Cacheable(value = "userCache")
	@GetMapping("/{id}")
	public Optional<Cliente> buscarClienteId(@PathVariable Long id) {
		return cr.findById(id);
	}
	
	@CacheEvict(allEntries = true, value = "userCache", beforeInvocation = false)
	@PostMapping
	public Cliente salvar(@RequestBody Cliente cliente) {
		
		Historico hist = new Historico();
		
		Localidade localidade = obterLocalidade();
		
		List<Geolocalizacao> listaGeolocalizacao = obterGeolocalizacao(localidade.getData().getLatitude(), localidade.getData().getLongitude());
		
		List<Clima> listaClimas = obterClimaDia(listaGeolocalizacao);		
		
		hist.setMin_temp(listaClimas.get(0).getMin_temp());
		hist.setMax_temp(listaClimas.get(0).getMax_temp());
		
		for (Clima clima : listaClimas) {
			  
			if(Double.parseDouble(clima.getMin_temp()) < Double.parseDouble(hist.getMin_temp()))
				hist.setMin_temp(clima.getMin_temp());
			
			if(Double.parseDouble(clima.getMax_temp()) > Double.parseDouble(hist.getMax_temp()))
				hist.setMax_temp(clima.getMax_temp());		
		}
		
		Cliente c = cr.save(cliente);
		localidade.setData(dr.save(localidade.getData()));
		lr.save(localidade);
		
		hist.setCliente(c);
		hist.setLocalidade(localidade);
		
		hr.save(hist);
		
		return c;
	}
	
	@CacheEvict(allEntries = true, value = "userCache", beforeInvocation = false)
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		
		Optional<Historico> historico = hr.findById(id);
		
		if (historico.isPresent()) {
			hr.deleteById(historico.get().getId());
		}
		
		
		Optional<Localidade> localidade = lr.findById(id);
		
		if (localidade.isPresent()) {
			lr.deleteById(localidade.get().getId());
		}
		
		cr.deleteById(id);
	}
	
	@CacheEvict(allEntries = true, value = "userCache", beforeInvocation = false)
	@PutMapping("/{id}")
	public Cliente atualizar(@RequestBody Cliente cliente, @PathVariable long id) {
		Optional<Cliente> clienteDB = cr.findById(id);
		if (!clienteDB.isPresent())
			return null;
		
		cliente.setId(id);
		return cr.save(cliente);
	}
	
	@GetMapping("/historico-cliente/{id}")
	public Optional<Historico> buscarHistoricoClienteId(@PathVariable Long id) {
		return hr.findById(id);
	}
	
	public Localidade obterLocalidade(){
		URL url;
		String json = "";
		Localidade localidade = new Localidade();
		try {
			
			url = new URL("https://ipvigilante.com/");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			
			if(conn.getResponseCode() != 200){
				throw new RuntimeException("HttpResponseCode: "+ conn.getResponseCode());
			}else{
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext()) {
					json+=sc.nextLine();
				}
				sc.close();
				
				Gson gson = new Gson();
				localidade = gson.fromJson(json, Localidade.class);			
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return localidade;
	}

	public List<Geolocalizacao> obterGeolocalizacao(String latt, String longe){
		URL url;
		String json = "";
		List<Geolocalizacao> lista = new ArrayList<Geolocalizacao>();
		try {
			
			String coordenadas = "https://www.metaweather.com/api/location/search/?lattlong="+latt+","+longe;
			url = new URL(coordenadas);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			
			if(conn.getResponseCode() != 200){
				throw new RuntimeException("HttpResponseCode: "+ conn.getResponseCode());
			}else{
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext()) {
					json+=sc.nextLine();
				}
				sc.close();
				Gson gson = new Gson();
				lista = gson.fromJson(json, new TypeToken<List<Geolocalizacao>>(){}.getType());					
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<Clima> obterClimaDia(List<Geolocalizacao> geolocalizacaolist){
		URL url;
		String json = "";
		List<Clima> listaClima = new ArrayList<Clima>();
				
		String pattern = "yyyy/MM/dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String data = simpleDateFormat.format(new Date());
				
		try {
			
			for (Geolocalizacao geolocalizacao : geolocalizacaolist) {		
				
				json = "";
				String coordenadas = "https://www.metaweather.com/api/location/"+geolocalizacao.getWoeid()+"/"+ data;
				url = new URL(coordenadas);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				
				if(conn.getResponseCode() != 200){
					throw new RuntimeException("HttpResponseCode: "+ conn.getResponseCode());
				}else{
					Scanner sc = new Scanner(url.openStream());
					while(sc.hasNext()) {
						json+=sc.nextLine();
					}
					
					sc.close();
					
					Gson gson = new Gson();
					
					listaClima = gson.fromJson(json, new TypeToken<List<Clima>>(){}.getType());
					
					if(listaClima.size() > 0)
						break;
				}
											
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return listaClima;
	}
	
}
