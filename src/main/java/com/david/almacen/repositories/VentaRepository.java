package com.david.almacen.repositories;

import com.david.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.david.almacen.dto.ventas.VentaRequest;
import com.david.almacen.dto.ventas.VentaResponse;
import com.david.almacen.entities.Producto;
import com.david.almacen.entities.Venta;
import com.david.almacen.enums.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta,Long> {
    @Query("""
        SELECT new com.david.almacen.dto.ventas.ReporteVentasSucursalResponse(
            s.id,
            s.nombre,
            COALESCE(SUM(d.cantidadProducto * d.precioProducto), 0.0),
            CAST(COALESCE(SUM(d.cantidadProducto), 0) AS integer)
        )
        FROM Venta v
        JOIN v.sucursal s
        JOIN v.detalleVentas d
        WHERE v.estadoVenta <> 'CANCELADA'
        GROUP BY s.id, s.nombre
    """)
    List<ReporteVentasSucursalResponse> obtenerReporteVentasPorSucursal();

}
