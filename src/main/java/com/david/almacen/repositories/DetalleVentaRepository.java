package com.david.almacen.repositories;

import com.david.almacen.entities.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta,Long> {
}
