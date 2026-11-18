package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * Objeto de Valor inmutable que representa el enunciado de una pregunta.
 * Regla: el texto no puede ser nulo ni vacio.
 */
public final class Enunciado {

    private final String texto;

    public Enunciado(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException("El enunciado no puede ser nulo ni vacio");
        }
        this.texto = texto.trim();
    }

    public String getTexto() {
        return texto;
    }

    // Los objetos de valor se comparan por su contenido, no por identidad
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enunciado)) return false;
        return texto.equals(((Enunciado) o).texto);
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
