package app.service;

import app.application.services.itemmenu.ItemMenuService;
import app.domain.RestaurantMenu;
import app.infrastructure.rest.menu.dto.ItemMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenuTest {

    private ItemMenuService itemMenuService;

    @BeforeEach
    public void setup() {
        itemMenuService = new ItemMenuService();
    }

    @Test
    public void testMapToRestaurantMenu() {
        // Crear un ItemMenu de prueba
        ItemMenu itemMenu = new ItemMenu();
        itemMenu.setId(1);
        itemMenu.setNombre("Test");
        itemMenu.setCategoria("Test Category");

        ItemMenu.MenuDetalle menuDetalle = new ItemMenu.MenuDetalle();
        menuDetalle.setId(1);
        menuDetalle.setNombre("Test Menu");
        menuDetalle.setEstado("A");

        itemMenu.setMenu_detalle(menuDetalle);

        // Llamar al método que se está probando
        RestaurantMenu result = itemMenuService.mapToRestaurantMenu(itemMenu);

        // Verificar que el resultado es el esperado
        assertEquals(itemMenu.getId().intValue(), result.getId_menu());
        assertEquals(itemMenu.getNombre(), result.getNombre());
        assertEquals(itemMenu.getCategoria(), result.getCategoria());
        assertEquals(itemMenu.getMenu_detalle().getId(), result.getMenu_detalle().getId());
        assertEquals(itemMenu.getMenu_detalle().getNombre(), result.getMenu_detalle().getNombre());
        assertEquals(itemMenu.getMenu_detalle().getEstado(), result.getMenu_detalle().getEstado());
    }
}