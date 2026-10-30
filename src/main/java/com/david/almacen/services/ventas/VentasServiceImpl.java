package com.david.almacen.services.ventas;

import com.david.almacen.dto.sucursales.SucursalResponse;
import com.david.almacen.dto.ventas.DetalleVentaResponse;
import com.david.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.david.almacen.dto.ventas.VentaRequest;
import com.david.almacen.dto.ventas.VentaResponse;
import com.david.almacen.entities.Producto;
import com.david.almacen.entities.Sucursal;
import com.david.almacen.entities.Venta;
import com.david.almacen.entities.DetalleVenta;
import com.david.almacen.enums.EstadoVenta;
import com.david.almacen.exceptions.ConflictoException;
import com.david.almacen.exceptions.DatoInvalidoException;
import com.david.almacen.exceptions.RecursoNoEncontradoException;
import com.david.almacen.mappers.DetalleVentaMapper;
import com.david.almacen.mappers.SucursalMapper;
import com.david.almacen.mappers.VentaMapper;
import com.david.almacen.repositories.DetalleVentaRepository;
import com.david.almacen.repositories.ProductoRepository;
import com.david.almacen.repositories.SucursalRepository;
import com.david.almacen.repositories.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VentasServiceImpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final SucursalMapper sucursalMapper;
    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;

    @Transactional(readOnly = true)
    @Override
    public List<VentaResponse> listarRegistradas() {
        log.info("Listando ventas...");
        List<Venta> ventas = ventaRepository.findAll().stream().
                filter(venta -> venta.getEstadoVenta().equals(EstadoVenta.REGISTRADA))
                .toList();
        log.info("Ventas encontradas");
        return ventas.stream().
                map(venta ->
                        ventaMapper.entidadAResponse(
                                venta,
                                sucursalMapper.entidadAResponse(venta.getSucursal()),
                                listaDetalleVentaResponse(venta.getDetalleVentas()),
                                calcularTotal(venta.getDetalleVentas())
                        )).toList();
    }


    @Override
    public List<VentaResponse> listarCanceladas() {
        log.info("Listando ventas canceladas...");
        List<Venta> ventas = ventaRepository.findAll().stream().
                filter(venta -> venta.getEstadoVenta().equals(EstadoVenta.CANCELADA))
                .toList();
        log.info("Ventas canceladas encontradas");
        return ventas.stream().
                map(venta ->
                        ventaMapper.entidadAResponse(
                                venta,
                                sucursalMapper.entidadAResponse(venta.getSucursal()),
                                listaDetalleVentaResponse(venta.getDetalleVentas()),
                                calcularTotal(venta.getDetalleVentas())
                        )).toList();

    }

    @Override
    public VentaResponse obtenerPorId(Long id) {
        log.info("Buscando venta con ID: {}", id);
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));
        log.info("Venta encontrada con ID: {}",id);
        return ventaMapper.entidadAResponse(
                venta,
                sucursalMapper.entidadAResponse(venta.getSucursal()),
                listaDetalleVentaResponse(venta.getDetalleVentas()),
                calcularTotal(venta.getDetalleVentas())
        );

    }

    @Override
    public VentaResponse registrar(VentaRequest request) {
        //traigo la sucursal con el id que viene en el request
        log.info("Obteniendo sucursal...");
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new RecursoNoEncontradoException("La sucursal no se encuentra, id: " + request.idSucursal()));
        log.info("Sucursal obtenida.");
        //creo una venta temporal para ir agregando sus detalles

        //creo una lista de detalles si algun detalle trae un producto que no exista se lanza la excepcion
        log.info("Obteniendo detalles...");
        List<DetalleVenta> detalles = request.productos().stream()
                .map(detalleRequest -> {
                    Producto producto = productoRepository.findById(detalleRequest.idProducto())
                            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + detalleRequest.idProducto()));

                    if(producto.getCantidad()<detalleRequest.cantidadProducto())
                        throw new ConflictoException("No hay suficientes "+producto.getNombre()+" en el inventario, venta cancelada.");
                    producto.descontarCantidad(detalleRequest.cantidadProducto());

                    return detalleVentaMapper.requestAEntidad(
                            new Venta(),
                            producto,
                            detalleRequest.cantidadProducto()
                    );
                })
                .collect(Collectors.toList());
        //creo mi venta
        log.info("Detalles obtenidos.");
        log.info("Creando Venta...");

         Venta venta = Venta.crear(sucursal, detalles);

        //un foreach para los detalles para ir agregando de uno en uno
        detalles.forEach(venta::agregarDetalle);

        //inserto los cambios en la base de datos
        Venta ventaGuardada = ventaRepository.saveAndFlush(venta);

        log.info("Venta realizada con exito!!");

        SucursalResponse sucursalResponse = sucursalMapper.entidadAResponse(sucursal);
        List<DetalleVentaResponse> detallesResponse = listaDetalleVentaResponse(detalles);
        BigDecimal total = calcularTotal(detalles);

        return ventaMapper.entidadAResponse(ventaGuardada, sucursalResponse, detallesResponse, total);
    }

    @Override
    public void cancelar(Long id) {

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));

        if (venta.getEstadoVenta().equals(EstadoVenta.CANCELADA))
            throw new ConflictoException("La venta que intentas cancelar ya ha sido cancelada antes");

        log.info("Cancelando venta con id {}...",venta.getId());
        // devolvemos a cada producto de la venta su cantidad que había sido vendida
        venta.getDetalleVentas().
                forEach(detalleVenta -> {
                            Producto producto=detalleVenta.getProducto();
                            producto.aumentarCantidad(detalleVenta.getCantidadProducto());
                        }
                );
        venta.cancelar();
        log.info("La venta con id: "+venta.getId()+". Ha sido cancelada exitosamente");
    }

    @Override
    public List<ReporteVentasSucursalResponse> reporteVentas() {
        return ventaRepository.obtenerReporteVentasPorSucursal();
    }

    public List<DetalleVentaResponse> listaDetalleVentaResponse(List<DetalleVenta> detalles){
        return detalles.stream()
                .map(detalle -> detalleVentaMapper.entidadAResponse(
                        detalle,
                        detalle.getProducto()
                ))
                .toList();
    }

    public BigDecimal calcularTotal(List<DetalleVenta> detalles){
        return detalles.stream()
                .map(DetalleVenta::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
