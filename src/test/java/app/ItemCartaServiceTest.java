package app;

import app.application.services.itemcarta.ItemCartaService;
import app.domain.RestaurantMenu;
import app.infrastructure.rest.carta.dto.ItemCarta;
import app.infrastructure.rest.carta.dto.ItemCarta.Categoriadetalle;
import app.infrastructure.rest.carta.dto.ItemCarta.Presentaciondetalle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ItemCartaServiceTest {

    @Mock
    private ItemCarta mockedItemCarta;

    @Mock
    private Categoriadetalle mockedCategoriaDetalle;

    @Mock
    private Presentaciondetalle mockedPresentacionDetalle;

    @InjectMocks
    private ItemCartaService itemCartaService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMapToRestaurantMenu() {
        // Mock de datos de ItemCarta
        when(mockedItemCarta.getId()).thenReturn(1);
        when(mockedItemCarta.getNombre()).thenReturn("Plato de Prueba");
        when(mockedItemCarta.getDescripcion()).thenReturn("Descripción de prueba");
        when(mockedItemCarta.getPrecio()).thenReturn(15.99);
        when(mockedItemCarta.getStock()).thenReturn(10);
        when(mockedItemCarta.getEstado()).thenReturn("A");

        when(mockedItemCarta.getCategoria_detalle()).thenReturn(mockedCategoriaDetalle);
        when(mockedCategoriaDetalle.getId()).thenReturn(1);
        when(mockedCategoriaDetalle.getNombre()).thenReturn("Categoría de Prueba");
        when(mockedCategoriaDetalle.getEstado()).thenReturn("A");

        when(mockedItemCarta.getPresentacion_detalle()).thenReturn(mockedPresentacionDetalle);
        when(mockedPresentacionDetalle.getId()).thenReturn(1);
        when(mockedPresentacionDetalle.getTipo()).thenReturn("Presentación de Prueba");
        when(mockedPresentacionDetalle.getEstado()).thenReturn("A");

        // Llamada al método a probar
        RestaurantMenu restaurantMenu = itemCartaService.mapToRestaurantMenu(mockedItemCarta);

        // Verificación de los resultados esperados
        assertEquals(1, restaurantMenu.getId_carta());
        assertEquals("Plato de Prueba", restaurantMenu.getNombre());
        assertEquals("Descripción de prueba", restaurantMenu.getDescripcion());
        assertEquals(15.99, restaurantMenu.getPrecio());
        assertEquals(10, restaurantMenu.getStock());
        assertEquals("C", restaurantMenu.getTipo());
        assertEquals("A", restaurantMenu.getEstado());

        assertEquals(1, restaurantMenu.getCategoria_detalle().getId());
        assertEquals("Categoría de Prueba", restaurantMenu.getCategoria_detalle().getNombre());
        assertEquals("A", restaurantMenu.getCategoria_detalle().getEstado());

        assertEquals(1, restaurantMenu.getPresentacion_detalle().getId());
        assertEquals("Presentación de Prueba", restaurantMenu.getPresentacion_detalle().getTipo());
        assertEquals("A", restaurantMenu.getPresentacion_detalle().getEstado());
    }
}

