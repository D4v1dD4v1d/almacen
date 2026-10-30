package com.david.almacen.mappers;

import com.david.almacen.dto.sucursales.SucursalResponse;
import com.david.almacen.dto.ventas.DetalleVentaResponse;
import com.david.almacen.dto.ventas.VentaResponse;
import com.david.almacen.entities.DetalleVenta;
import com.david.almacen.entities.Sucursal;
import com.david.almacen.entities.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class VentaMapper {
    public Venta requestAEntidad(List<DetalleVenta> detallesVentas, Sucursal sucursal){
        return sucursal == null
                ? null
                :Venta.crear(
                        sucursal,
                        detallesVentas
                        );
    }

    public VentaResponse entidadAResponse(Venta venta, SucursalResponse sucursalResponse, List<DetalleVentaResponse> detalles, BigDecimal total){
        return venta == null
                ? null
                : new VentaResponse(
                        venta.getId(),
                        venta.getFecha().toString(),
                        venta.getEstadoVenta().toString(),
                        sucursalResponse,
                        detalles,
                        total
        );
    }
}
