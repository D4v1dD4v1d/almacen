package com.david.almacen.enums;

import com.david.almacen.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class CategoriaTest {
    @Test
    void obtenerCategoriaPorDescripcion_debeEncontrarCategoria_ignorandoAcentosYMayusculas() {
        // Act
        Categoria categoria = Categoria.obtenerCategoriaPorDescripcion("electronica");

        // Assert
        assertThat(categoria).isEqualTo(Categoria.ELECTRONICA);
    }
    @Test
    void obtenerCategoriaPorDescripcion_debeEncontrarCategoria_conMayusculasYTilde() {
        Categoria categoria = Categoria.obtenerCategoriaPorDescripcion("ELECTRÓNICA");

        assertThat(categoria).isEqualTo(Categoria.ELECTRONICA);
    }
    @Test
    void obtenerCategoriaPorDescripcion_debeLanzarExcepcion_cuandoNoExisteLaCategoria() {
        assertThatThrownBy(() -> Categoria.obtenerCategoriaPorDescripcion("Mueble"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessageContaining("No existe una categoría en la descripción:Mueble");
    }
    @Test
    void obtenerCategoriaPorDescripcion_debeLanzarExcepcion_cuandoLaDescripcionEsVacia() {
        assertThatThrownBy(() -> Categoria.obtenerCategoriaPorDescripcion("   "))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La descripción es requerida, no puede ser vacía");
    }
}
