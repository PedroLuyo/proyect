package app.application.controller.reports;

import app.infrastructure.reports.ReporteService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/platos")
    public ResponseEntity<byte[]> descargarReportePlatos() {
        try {
            byte[] reporte = reporteService.generarReportePlatos();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_platos.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reporte);
        } catch (JRException e) {
            // Aquí puedes manejar el error como prefieras
            return ResponseEntity.status(500).build();
        }
    }
}