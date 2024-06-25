package app.infrastructure.rest.carta.webclient;

import app.infrastructure.rest.carta.dto.ItemCarta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Component
public class CartaWebclient {

    private final WebClient webClient;

    @Autowired
    public CartaWebclient(@Qualifier("platosCartaWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ItemCarta> getCarta() {
        return webClient.get()
                .uri("/obtener")  // Actualiza la URI
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(ItemCarta.class);
    }
}


