package co.edu.unicauca.saberpro.validation.api;

import co.edu.unicauca.saberpro.validation.filtros.PipelineValidacion;
import co.edu.unicauca.saberpro.validation.filtros.SolicitudValidacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API del validation-service. Lo consume question-service de forma sincrona
 * en POST /api/validaciones antes de guardar una pregunta.
 */
@RestController
@RequestMapping("/api/validaciones")
public class ValidationRestController {

    private static final Logger log = LoggerFactory.getLogger(ValidationRestController.class);

    private final PipelineValidacion pipeline;

    public ValidationRestController(PipelineValidacion pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping
    public ResultadoValidacion validar(@RequestBody SolicitudValidacion solicitud) {
        List<String> errores = pipeline.ejecutar(solicitud);
        log.info(">>> Validacion de contenido ejecutada. Errores encontrados: {}", errores.size());
        return new ResultadoValidacion(errores.isEmpty(), errores);
    }

    /** Endpoint simple para comprobar que el servicio esta arriba. */
    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of("servicio", "validation-service", "estado", "UP");
    }

    public record ResultadoValidacion(boolean valido, List<String> errores) {
    }
}
