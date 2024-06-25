package app.application.controller;


import app.infrastructure.exceptions.NoMethodsAvailableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    @RequestMapping({"/**", ""})
    public void handleAllRequests() {
        throw new NoMethodsAvailableException("No hay métodos disponibles para usar.", "Esta intentando acceder a una ruta no definida.");
    }


}
