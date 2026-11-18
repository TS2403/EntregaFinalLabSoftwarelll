package co.edu.unicauca.saberpro.validation.filtros;

import java.util.List;

/**
 * Filtro del estilo Pipes and Filters (heredado de la HU03 del Corte I).
 * Cada filtro revisa una sola cosa y devuelve los errores que encuentre.
 */
public interface FiltroValidacion {

    List<String> aplicar(SolicitudValidacion solicitud);
}
