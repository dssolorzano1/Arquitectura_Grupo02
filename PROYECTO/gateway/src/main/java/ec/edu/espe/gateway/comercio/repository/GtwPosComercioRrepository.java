package ec.edu.espe.gateway.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ec.edu.espe.gateway.comercio.model.GtwPosComercio;
import ec.edu.espe.gateway.comercio.model.GtwPosComercioPK;

public interface GtwPosComercioRrepository extends JpaRepository<GtwPosComercio, GtwPosComercioPK>{
    
}
