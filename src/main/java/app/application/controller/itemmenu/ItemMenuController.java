package app.application.controller.itemmenu;

import app.application.services.itemmenu.ItemMenuService;
import app.infrastructure.rest.menu.dto.PlatoMenuDto;
import app.infrastructure.rest.menu.webclient.MenuWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/platos-menu")
public class ItemMenuController {

    @Autowired
    private MenuWebClient menuWebClient;

    @Autowired
    private ItemMenuService itemMenuService;

    @GetMapping("/obtener")
    public Flux<PlatoMenuDto> obtenerPlatosMenu() {
        return itemMenuService.obtenerMenu();
    }


}
