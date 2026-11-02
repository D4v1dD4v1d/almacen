package com.david.almacen.services.ventas;

import com.david.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.david.almacen.dto.ventas.VentaRequest;
import com.david.almacen.dto.ventas.VentaResponse;

import java.util.List;

public interface VentaService {
    List<VentaResponse> listarRegistradas();
    List<VentaResponse> listarCanceladas();
    VentaResponse obtenerPorId(Long id);
    VentaResponse registrar(VentaRequest request);
    void cancelar (Long id);
    List<ReporteVentasSucursalResponse> reporteVentas ();
}
