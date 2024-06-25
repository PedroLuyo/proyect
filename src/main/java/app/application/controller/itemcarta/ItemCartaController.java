package app.application.controller.itemcarta;

import app.application.services.itemcarta.ItemCartaService;
import app.infrastructure.rest.carta.dto.ItemCarta;
import app.infrastructure.rest.carta.webclient.CartaWebclient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET,})
@RequestMapping("/api/v1/platos-carta")
public class ItemCartaController {

    @Autowired
    private CartaWebclient cartaWebclient;

    @Autowired
    private ItemCartaService itemCartaService;


    @GetMapping("/obtener")
    public Flux<ItemCarta> obtenerPlatosCarta() {
        return itemCartaService.obtenerCarta();
    }
}
