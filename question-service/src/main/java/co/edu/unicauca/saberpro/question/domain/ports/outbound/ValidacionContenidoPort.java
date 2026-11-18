package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;

import java.util.List;

/**
 * Puerto de SALIDA hacia el servicio de validacion de contenido (validation-service).
 * Devuelve la lista de errores encontrados; si esta vacia, el contenido es valido.
 * Implementacion: RestValidacionAdapter (infrastructure/rest), llamada sincrona HTTP.
 */
public interface ValidacionContenidoPort {

    List<String> validar(Pregunta pregunta);
}
