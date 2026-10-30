package com.david.almacen.dto.ventas;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Reportes de ventas de las sucursales")
public record ReporteVentasSucursalResponse(
        @Schema(description = "ID de la Sucursal", example = "1")
        Long id,
        @Schema(description = "Nombre de la sucursal", example = "Sucursal Norte")
        String nombre,
        @Schema(description = "El total de lo que ha vendido la sucursal", example = "20000.00")
        BigDecimal totalFacturado,
        @Schema(description = "El conteo de productos que ha vendido la sucursal", example = "10000")
        Integer cantidadProductosVendidos
) {

    public ReporteVentasSucursalResponse(Long id, String nombre, BigDecimal totalFacturado, Integer cantidadProductosVendidos) {
        this.id = id;
        this.nombre = nombre;
        this.totalFacturado = totalFacturado;
        this.cantidadProductosVendidos = cantidadProductosVendidos;
    }
}
