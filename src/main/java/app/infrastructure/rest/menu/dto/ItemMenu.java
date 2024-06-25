package app.infrastructure.rest.menu.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ItemMenu {

    private Integer id;
    private String nombre;
    private String categoria;
    private Double precio;
    private String estado;
    private MenuDetalle menu_detalle;


    @Data
    @Getter
    @Setter
    public static class MenuDetalle {

        private Integer id;
        private String nombre;
        private String estado;

    }

}