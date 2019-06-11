package projeto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import projeto.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	
	Optional<Cliente> findById(Long id);

}
