package co.edu.unicauca.saberpro.question.infrastructure.rest;

/**
 * Excepcion tecnica (de infraestructura) cuando el validation-service no responde.
 * Se traduce a HTTP 503 en el manejador global de errores.
 */
public class ServicioValidacionNoDisponibleException extends RuntimeException {

    public ServicioValidacionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
