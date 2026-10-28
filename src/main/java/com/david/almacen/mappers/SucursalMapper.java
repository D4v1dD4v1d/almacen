package com.david.almacen.mappers;

import com.david.almacen.dto.sucursales.SucursalRequest;
import com.david.almacen.dto.sucursales.SucursalResponse;
import com.david.almacen.entities.Sucursal;
import com.david.almacen.repositories.SucursalRepository;
import org.springframework.stereotype.Component;

@Component
public class SucursalMapper {
    public Sucursal requestAEntidad(SucursalRequest request){
        return request == null
                ? null
                : Sucursal.crear(
                    request.nombre(),
                    request.direccion()
                );
    }
    public SucursalResponse entidadAResponse(Sucursal sucursal){
        return sucursal == null
                ? null
                : new SucursalResponse(
                    sucursal.getId(),
                    sucursal.getNombre(),
                    sucursal.getDireccion()
                );
    }
}
