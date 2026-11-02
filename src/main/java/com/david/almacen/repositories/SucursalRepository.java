package com.david.almacen.repositories;

import com.david.almacen.entities.Sucursal;
import org.apache.catalina.Service;
import org.apache.catalina.util.ServerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    //SELECT COUNT(*) FROM SUCURSALES WHERE LOWER(NOMBRE) = LOWER(:NOMBRE)
    boolean existsByNombreIgnoreCase(String nombre);
    //SELECT COUNT(*) FROM SUCURSALES WHERE LOWER(NOMBRE) = LOWER(:NOMBRE) AND ID_SUCURSAL <> :ID
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre,Long id);

}
