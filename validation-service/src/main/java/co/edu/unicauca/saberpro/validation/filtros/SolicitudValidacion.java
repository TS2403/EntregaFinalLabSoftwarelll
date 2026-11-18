package co.edu.unicauca.saberpro.validation.filtros;

import java.util.List;

/**
 * Datos que llegan a validar (mismo formato que envia question-service).
 */
public record SolicitudValidacion(String enunciado, String justificacion, List<Opcion> opciones) {

    public record Opcion(String texto, boolean correcta) {
    }
}
