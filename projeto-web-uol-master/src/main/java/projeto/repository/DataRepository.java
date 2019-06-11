package projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projeto.models.Data;

public interface DataRepository extends JpaRepository<Data, Long>{

}
