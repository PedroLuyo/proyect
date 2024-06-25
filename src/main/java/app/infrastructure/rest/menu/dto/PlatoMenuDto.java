package app.infrastructure.rest.menu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class PlatoMenuDto {

    @Id
    private Integer comidaid;

    private String nombre;

    private String categoria;

    private Double precio;

    private Long menuid;

    private String estado;
}
