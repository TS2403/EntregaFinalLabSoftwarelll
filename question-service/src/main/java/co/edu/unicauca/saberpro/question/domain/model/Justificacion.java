package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * Objeto de Valor inmutable con la justificacion de la respuesta correcta.
 * Regla: debe tener minimo 15 caracteres (sin contar espacios al inicio/fin).
 */
public final class Justificacion {

    public static final int LONGITUD_MINIMA = 15;

    private final String texto;

    public Justificacion(String texto) {
        if (texto == null || texto.trim().length() < LONGITUD_MINIMA) {
            throw new ReglaNegocioException(
                    "La justificacion debe tener minimo " + LONGITUD_MINIMA + " caracteres");
        }
        this.texto = texto.trim();
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Justificacion)) return false;
        return texto.equals(((Justificacion) o).texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto);
    }

    @Override
    public String toString() {
        return texto;
    }
}
