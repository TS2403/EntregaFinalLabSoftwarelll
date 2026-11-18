package co.edu.unicauca.saberpro.question.api;

import java.util.List;

/**
 * DTO de entrada del API REST (JSON que llega desde Postman / Thunder Client).
 */
public record PreguntaRequest(String enunciado, String justificacion, List<OpcionRequest> opciones) {

    public record OpcionRequest(String texto, boolean correcta) {
    }
}
