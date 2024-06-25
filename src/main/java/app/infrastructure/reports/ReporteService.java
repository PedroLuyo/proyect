package app.infrastructure.reports;

import app.domain.RestaurantMenu;
import app.infrastructure.item.product.repository.ProductRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private ProductRepository productRepository;

    public byte[] generarReportePlatos() throws JRException {
        // Obtener los datos para el reporte
        List<RestaurantMenu> platos = productRepository.findAll().collectList().block();

        // Crear el data source para JasperReports
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(platos);

        // Cargar el archivo .jrxml (reemplaza "reporte_platos.jrxml" con la ruta al archivo .jrxml)
        JasperReport jasperReport = JasperCompileManager.compileReport("classpath:template/reporte_platos.jrxml");

        // Crear los parámetros para el reporte
        Map<String, Object> parameters = new HashMap<>();
        // Aquí puedes agregar parámetros que se usarán en el reporte

        // Generar el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Exportar el reporte a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}