package com.david.almacen.repositories;

import com.david.almacen.dto.productos.ProductoResponse;
import com.david.almacen.entities.Producto;
import com.david.almacen.enums.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository <Producto,Long>{
    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:categoria IS NULL OR p.categoria = :categoria) " +
            "AND (:min IS NULL OR p.precio>= :min) " +
            "AND (:max IS NULL OR p.precio <= :max )")
    List<Producto> productoFiltrado(
            @Param("nombre") String nombre,
            @Param("categoria") Categoria categoria,
            @Param("min") BigDecimal precioMin,
            @Param("max") BigDecimal precioMax);
}
