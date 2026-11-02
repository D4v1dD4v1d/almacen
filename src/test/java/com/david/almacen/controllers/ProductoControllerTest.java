package com.david.almacen.controllers;

import com.david.almacen.dto.productos.ProductoRequest;
import com.david.almacen.dto.productos.ProductoResponse;
import com.david.almacen.exceptions.RecursoNoEncontradoException;
import com.david.almacen.services.productos.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    // @MockitoBean reemplaza al antiguo @MockBean en Spring Boot 4.
    // Registra un mock de ProductoService dentro del contexto de Spring
    // para que el controller lo reciba por inyección de dependencias.
    @MockitoBean
    private ProductoService productoService;
    @Test
    void registrar_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        // Arrange
        ProductoRequest request = new ProductoRequest(
                "Mouse Inalámbrico", "Electrónica", new BigDecimal("299.99"), 50);
        ProductoResponse response = new ProductoResponse(
                1L, "Mouse Inalámbrico", "Electrónica", new BigDecimal("299.99"), 50);

        when(productoService.registrar(any(ProductoRequest.class))).thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Inalámbrico"));
    }

    @Test
    void registrar_debeRetornar400_cuandoElNombreEsMuyCorto() throws Exception {
        // Arrange: "Ab" viola @Size(min = 5) del ProductoRequest
        ProductoRequest request = new ProductoRequest(
                "Ab", "Electrónica", new BigDecimal("299.99"), 50);

        // Act + Assert
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // La validación @Valid falla ANTES de llegar al service.
        verify(productoService, never()).registrar(any());
    }

    @Test
    void obtenerPorId_debeRetornar404_cuandoElProductoNoExiste() throws Exception {
        // Arrange
        when(productoService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado con id: 99"));

        // Act + Assert
        mockMvc.perform(get("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Producto no encontrado con id: 99"));
    }

    @Test
    void obtenerPorId_debeRetornar400_cuandoElIdEsNegativo() throws Exception {
        // El @Positive del controller rechaza esto antes de llegar al service
        mockMvc.perform(get("/api/productos/{id}", -1L))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).obtenerPorId(any());
    }
}
