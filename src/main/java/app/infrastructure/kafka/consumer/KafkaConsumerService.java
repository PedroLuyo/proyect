package app.infrastructure.kafka.consumer;

import app.application.services.itemcarta.ItemCartaService;
import app.application.services.itemmenu.ItemMenuService;
import app.domain.RestaurantMenu;
import app.infrastructure.item.product.repository.ProductRepository;
import app.infrastructure.rest.carta.dto.CategoriaDto;
import app.infrastructure.rest.carta.dto.ItemCarta;
import app.infrastructure.rest.carta.dto.PresentacionDto;
import app.infrastructure.rest.carta.dto.ReservationMessage;
import app.infrastructure.rest.menu.dto.ItemMenu;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;

@Service
public class KafkaConsumerService {

    private static final Logger logger = Logger.getLogger(KafkaConsumerService.class.getName());

    private static final List<ComparacionCampo> CAMPOS_COMPARAR_CARTA = List.of(
            new ComparacionCampo("nombre", "getNombre"),
            new ComparacionCampo("descripcion", "getDescripcion"),
            new ComparacionCampo("precio", "getPrecio"),
            new ComparacionCampo("stock", "getStock"),
            new ComparacionCampo("image", "getImage"),
            new ComparacionCampo("estado", "getEstado")
    );
    private static final List<ComparacionCampo> CAMPOS_COMPARAR_MENU = List.of(
            new ComparacionCampo("nombre", "getNombre"),
            new ComparacionCampo("categoria", "getCategoria"),
            //new ComparacionCampo("image", "getImage"),
            new ComparacionCampo("estado", "getEstado")
    );

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ItemCartaService itemCartaService;
    @Autowired
    private ItemMenuService itemMenuService;

    @KafkaListener(topics = "crmscreate", groupId = "my-consumer-group")
    public void consumeCreateMessage(String json) {
        try {
            if (json.contains("presentacion_detalle")) {
                mapAndSaveCarta(json);
            } else if (json.contains("menu_detalle")) {
                mapAndSaveMenu(json);
            } else {
                logger.warning("Guardar plato: Tipo de plato desconocido para el JSON: " + json);
            }
        } catch (Exception e) {
            logger.warning("Guardar plato: Error al procesar el mensaje JSON: " + json);
        }
    }

    private <T> T mapFromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.warning("Error al mapear JSON a objeto: " + e.getMessage());
            return null;
        }
    }

    private void mapAndSaveCarta(String json) {
        ItemCarta itemCarta = mapFromJson(json, ItemCarta.class);
        if (itemCarta == null) return;

        RestaurantMenu restaurantMenu = itemCartaService.mapToRestaurantMenu(itemCarta);
        restaurantMenu.setTipo("C");

        saveToMongo(restaurantMenu)
                .subscribe(
                        savedMenu -> logger.info("Plato Carta '" + restaurantMenu.getNombre() + "' guardado con éxito: " + savedMenu),
                        error -> logger.warning("Error al guardar el Plato Carta: " + error.getMessage())
                );
    }

    private void mapAndSaveMenu(String json) {
        ItemMenu itemMenu = mapFromJson(json, ItemMenu.class);
        if (itemMenu == null) return;

        RestaurantMenu restaurantMenu = itemMenuService.mapToRestaurantMenu(itemMenu);
        restaurantMenu.setTipo("M");

        saveToMongo(restaurantMenu)
                .subscribe(
                        savedMenu -> logger.info("Plato Menú '" + restaurantMenu.getNombre() + "' guardado con éxito: " + savedMenu),
                        error -> logger.warning("Error al guardar el Plato Menú: " + error.getMessage())
                );
    }

    private Mono<RestaurantMenu> saveToMongo(RestaurantMenu restaurantMenu) {
        return productRepository.save(restaurantMenu)
                .doOnSuccess(savedMenu -> logger.info("Guardado exitoso en MongoDB"))
                .doOnError(error -> logger.warning("Error al guardar en MongoDB: " + error.getMessage()));
    }

    @KafkaListener(topics = "crmsedit", groupId = "my-consumer-group")
    public void consumeEditMessage(String json) {
        try {
            if (json.contains("presentacion_detalle")) {
                ItemCarta itemCarta = mapFromJson(json, ItemCarta.class);
                editAndSaveToMongo(itemCarta, "C", CAMPOS_COMPARAR_CARTA);
            } else if (json.contains("menu_detalle")) {
                ItemMenu itemMenu = mapFromJson(json, ItemMenu.class);
                editAndSaveToMongo(itemMenu, "M", CAMPOS_COMPARAR_MENU);
            } else {
                logger.warning("Editar plato: Tipo de plato desconocido para el JSON: " + json);
            }
        } catch (Exception e) {
            logger.warning("Editar plato: Error al procesar el mensaje JSON: " + json);
        }
    }

    private <T> boolean compareAndUpdateFields(T newItem, RestaurantMenu existingMenu, List<ComparacionCampo> camposComparar) {
        boolean updated = false;

        for (ComparacionCampo campo : camposComparar) {
            String nombreCampo = campo.getNombreCampo();
            String metodoObtenerCampo = campo.getMetodoObtenerCampo();

            try {
                Object valorNuevo = newItem.getClass().getMethod(metodoObtenerCampo).invoke(newItem);
                Object valorExistente = existingMenu.getClass().getMethod(metodoObtenerCampo).invoke(existingMenu);

                if (valorNuevo != null && !valorNuevo.equals(valorExistente)) {
                    existingMenu.getClass().getMethod("set" + nombreCampo.substring(0, 1).toUpperCase() + nombreCampo.substring(1), valorNuevo.getClass()).invoke(existingMenu, valorNuevo);
                    updated = true;
                }
            } catch (Exception e) {
                logger.warning("Error al comparar y actualizar campo: " + e.getMessage());
            }
        }

        return updated;
    }

    private Mono<RestaurantMenu> updateNestedFields(RestaurantMenu restaurantMenu, ItemCarta.Presentaciondetalle newPresentacionDetalle, ItemCarta.Categoriadetalle newCategoriaDetalle) {
        Query query = new Query().addCriteria(Criteria.where("id").is(restaurantMenu.getId()));

        Update update = new Update()
                .set("presentacion_detalle", newPresentacionDetalle)
                .set("categoria_detalle", newCategoriaDetalle);

        return reactiveMongoTemplate.findAndModify(query, update, RestaurantMenu.class);
    }

    private <T> void editAndSaveToMongo(T item, String tipo, List<ComparacionCampo> camposComparar) {
        int itemId = (item instanceof ItemCarta) ? ((ItemCarta) item).getId() : ((ItemMenu) item).getId();

        Mono<RestaurantMenu> existingMenuMono = ("C".equals(tipo)) ? productRepository.findByCartaId(itemId) : productRepository.findByMenuId(itemId);

        existingMenuMono.flatMap(existingMenu -> {
            boolean updated = compareAndUpdateFields(item, existingMenu, camposComparar);

            if (updated) {
                return saveToMongo(existingMenu);
            } else if (item instanceof ItemCarta) {
                ItemCarta itemCarta = (ItemCarta) item;
                return updateNestedFields(existingMenu, itemCarta.getPresentacion_detalle(), itemCarta.getCategoria_detalle());
            } else {
                logger.info("No hay cambios en el plato, no se requiere actualización.");
                return Mono.empty();
            }
        }).subscribe(
                savedMenu -> logger.info("Plato actualizado con éxito: " + savedMenu),
                error -> logger.warning("Error al actualizar el plato: " + error.getMessage())
        );
    }

    @KafkaListener(topics = "pcms-ctg", groupId = "my-consumer-group")
    public void consumeEditCat(String json) {
        CategoriaDto categoriaDto = mapFromJson(json, CategoriaDto.class);
        String categoria = categoriaDto.getNombre();

        Query query = new Query()
                .addCriteria(Criteria.where("tipo").is("C").and("categoria_detalle.id").is(categoriaDto.getId()));

        Update update = new Update()
                .set("categoria_detalle.nombre", categoriaDto.getNombre())
                .set("categoria_detalle.estado", categoriaDto.getEstado());

        reactiveMongoTemplate.updateMulti(query, update, RestaurantMenu.class)
                .subscribe(
                        updateResult -> logger.info("Categorías '" + categoria + "' actualizadas con éxito: " + updateResult.getModifiedCount()),
                        error -> logger.warning("Error al actualizar las categorías: " + error.getMessage())
                );
    }

    @KafkaListener(topics = "pcms-prt", groupId = "my-consumer-group")
    public void consumeEditPre(String json) {
        PresentacionDto presentacionDto = mapFromJson(json, PresentacionDto.class);

        Query query = new Query()
                .addCriteria(Criteria.where("tipo").is("C").and("presentacion_detalle.id").is(presentacionDto.getId()));

        Update update = new Update()
                .set("presentacion_detalle.tipo", presentacionDto.getTipo())
                .set("presentacion_detalle.estado", presentacionDto.getEstado());

        reactiveMongoTemplate.updateMulti(query, update, RestaurantMenu.class)
                .subscribe(
                        updateResult -> logger.info("Presentaciones '" + presentacionDto.getTipo() + "' actualizadas con éxito: " + updateResult.getModifiedCount()),
                        error -> logger.warning("Error al actualizar las presentaciones: " + error.getMessage())
                );
    }

    @KafkaListener(topics = "pcmsreservations", groupId = "my-consumer-group")
    public void consumeReservationMessage(String json) {
        try {
            // Deserializar el mensaje JSON a un objeto ReservationMessage
            ReservationMessage message = mapFromJson(json, ReservationMessage.class);
            if (message == null) return;

            // Buscar el plato en MongoDB y actualizar su stock
            productRepository.findByCartaId(message.getId())
                    .flatMap(restaurantMenu -> {
                        restaurantMenu.setStock(message.getStock());
                        return productRepository.save(restaurantMenu);
                    })
                    .subscribe(
                            savedMenu -> logger.info("Stock del plato '" + savedMenu.getNombre() + "' actualizado con éxito: " + savedMenu),
                            error -> logger.warning("Error al actualizar el stock del plato: " + error.getMessage())
                    );
        } catch (Exception e) {
            logger.warning("Error al procesar el mensaje JSON: " + json);
        }
    }

    @Getter
    private static class ComparacionCampo {
        private final String nombreCampo;
        private final String metodoObtenerCampo;

        public ComparacionCampo(String nombreCampo, String metodoObtenerCampo) {
            this.nombreCampo = nombreCampo;
            this.metodoObtenerCampo = metodoObtenerCampo;
        }
    }
}
