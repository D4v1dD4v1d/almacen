package com.david.almacen.mappers;

import com.david.almacen.dto.ventas.DetalleVentaResponse;
import com.david.almacen.entities.DetalleVenta;
import com.david.almacen.entities.Producto;
import com.david.almacen.entities.Venta;
import org.springframework.stereotype.Component;

@Component
public class DetalleVentaMapper {
    public DetalleVenta requestAEntidad(Venta venta, Producto producto,Integer cantidad){
        return venta == null
                ? null
                : DetalleVenta.crear(
                        venta,
                        producto,
                        cantidad);
    }
    public DetalleVentaResponse entidadAResponse(DetalleVenta detalleVenta,Producto producto){
        return detalleVenta == null
                ? null
                : new DetalleVentaResponse(
                        producto.getId(),
                        producto.getNombre(),
                        detalleVenta.getCantidadProducto(),
                        detalleVenta.getPrecioProducto(),
                        detalleVenta.calcularSubtotal()
                        );
    }
}
