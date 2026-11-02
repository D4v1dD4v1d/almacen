package com.david.almacen.controllers;

import com.david.almacen.docs.ProblemaDoc;
import com.david.almacen.dto.productos.ProductoRequest;
import com.david.almacen.dto.ventas.ReporteVentasSucursalResponse;
import com.david.almacen.dto.ventas.VentaRequest;
import com.david.almacen.dto.ventas.VentaResponse;
import com.david.almacen.entities.Venta;
import com.david.almacen.services.ventas.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "API Ventas",description = "Géstion de Ventas")
public class VentaController {
    private final VentaService ventaService;
    @GetMapping("/registradas")
    @Operation(summary = "Obtener un listado de todas las ventas registradas")
    @ApiResponse(responseCode = "200",description = "Ventas registradas encontradas...")
    public ResponseEntity<List<VentaResponse>> listarVentasRegistradas(){
        return ResponseEntity.ok(ventaService.listarRegistradas());
    }
    @GetMapping("/canceladas")
    @Operation(summary = "Obtener un listado de todas las ventas que han sido canceladas")
    @ApiResponse(responseCode = "200",description = "Ventas canceladas encontradas...")
    public ResponseEntity<List<VentaResponse>> listarVentaasCanceladas(){
        return ResponseEntity.ok(ventaService.listarCanceladas());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una venta por id")
    @ApiResponse(responseCode = "200",description = "Venta encontrada")
    public ResponseEntity<VentaResponse> obtenerPorId(
            @Parameter(description = "Id de la venta", example = "1")
            @PathVariable @Positive Long id){
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }
    @GetMapping("/reportes")
    @Operation(summary = "Obtener un listado de los reportes de cada sucursal")
    @ApiResponse(responseCode = "200",description = "Reportes realizados.")
    public ResponseEntity<List<ReporteVentasSucursalResponse>> obtenerReportePorSucursal(){
        return ResponseEntity.ok(ventaService.reporteVentas());
    }

    @PostMapping
    @Operation(summary = "Insertar Ventas con detalles")
    @ApiResponse(responseCode = "201",description = "Venta Realizada")
    @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    public ResponseEntity<VentaResponse> registrar(
            @Valid @RequestBody VentaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar la venta")
    @ApiResponse(responseCode = "204", description = "Venta Cancelada")
    public ResponseEntity cancelar(
            @Parameter(description = "Id de la venta", example = "1")
            @PathVariable @Positive Long id)
    {
        ventaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }




}
