package app.infrastructure.rest.carta.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class CategoriaDto {

    @Id
    private Integer id;

    private String nombre;

    private String estado;

}