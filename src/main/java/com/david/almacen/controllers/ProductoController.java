package com.david.almacen.controllers;

import com.david.almacen.dto.productos.ProductoRequest;
import com.david.almacen.dto.productos.ProductoResponse;
import com.david.almacen.entities.Producto;
import com.david.almacen.services.productos.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "API productos", description = "Gestión de inventario de productos")
public class ProductoController {
    private final ProductoService productoService;
    @GetMapping
    @Operation(summary = "Listar productos", description = "Todos los filtros son opcionales")
    @ApiResponse(responseCode = "200",description = "Listado obtenido")
    public ResponseEntity<List<ProductoResponse>> listar(
           @RequestParam(required = false) String nombre,
           @RequestParam(required = false) String categoria,
           @RequestParam(required = false) BigDecimal precioMin,
           @RequestParam(required = false) BigDecimal precioMax
    ){
        return ResponseEntity.ok(productoService.listar(nombre,categoria,precioMin,precioMax));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Traer un producto", description = "Trarer un producto por su id")
    @ApiResponse(responseCode = "200",description = "Producto encontrado")
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @Parameter(description = "Id del producto", example = "1")
            @PathVariable @Positive Long id){
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }
    @PostMapping
    @Operation(summary = "Registrar", description = "Registrar un producto")
    @ApiResponse(responseCode = "201",description = "Producto Registrado")
    public ResponseEntity<ProductoResponse> registrar(@Valid @RequestBody ProductoRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.registrar(request));
    }
    @PutMapping
    @Operation(summary = "Actualizar", description = "Actualizar un producto")
    @ApiResponse(responseCode = "200", description = "Producto actualizado")
    public ResponseEntity<ProductoResponse> actualizar (
            @Valid @RequestBody ProductoRequest request,
            @Parameter(description = "ID del producto",example = "1")
            @PathVariable @Positive Long id){
        return ResponseEntity.ok(productoService.actualizar(request,id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    public ResponseEntity eliminar(@PathVariable Long id){
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
