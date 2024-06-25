package app.application.controller.itemproduct;

import app.application.services.itemproduct.ItemProductService;
import app.domain.RestaurantMenu;
import app.infrastructure.item.product.repository.ProductRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*", methods = RequestMethod.GET)
@RequestMapping("/api/v1/products/")
public class ItemProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemProductService itemProductService;

    @GetMapping("/obtener")
    public Flux<RestaurantMenu> obtenerProduct() {
        return productRepository.findAll();
    }


}
