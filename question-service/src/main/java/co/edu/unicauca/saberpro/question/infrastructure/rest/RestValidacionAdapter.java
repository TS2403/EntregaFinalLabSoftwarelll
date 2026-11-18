package co.edu.unicauca.saberpro.question.infrastructure.rest;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionContenidoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Adaptador secundario REST: llama de forma SINCRONA al validation-service.
 * Dentro de docker compose la URL usa el nombre del servicio como DNS:
 * http://validation-service:8082
 */
public class RestValidacionAdapter implements ValidacionContenidoPort {

    private static final Logger log = LoggerFactory.getLogger(RestValidacionAdapter.class);

    private final RestClient restClient;

    public RestValidacionAdapter(String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public List<String> validar(Pregunta pregunta) {
        ValidacionRequest request = new ValidacionRequest(
                pregunta.getEnunciado().getTexto(),
                pregunta.getJustificacion().getTexto(),
                pregunta.getOpciones().stream()
                        .map(o -> new ValidacionRequest.Opcion(o.getTexto(), o.isCorrecta()))
                        .toList());
        try {
            ValidacionResponse response = restClient.post()
                    .uri("/api/validaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ValidacionResponse.class);
            log.info(">>> [REST] validation-service respondio: valido={}", response != null && response.valido());
            return response == null || response.errores() == null ? List.of() : response.errores();
        } catch (ResourceAccessException e) {
            throw new ServicioValidacionNoDisponibleException(
                    "No fue posible contactar al validation-service: " + e.getMessage());
        }
    }

    record ValidacionRequest(String enunciado, String justificacion, List<Opcion> opciones) {
        record Opcion(String texto, boolean correcta) {
        }
    }

    record ValidacionResponse(boolean valido, List<String> errores) {
    }
}
