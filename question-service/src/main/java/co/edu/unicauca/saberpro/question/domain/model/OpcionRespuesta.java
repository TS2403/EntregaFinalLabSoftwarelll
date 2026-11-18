package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * Objeto de Valor inmutable: una opcion de respuesta (texto + si es la clave correcta).
 */
public final class OpcionRespuesta {

    private final String texto;
    private final boolean correcta;

    public OpcionRespuesta(String texto, boolean correcta) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException("El texto de la opcion de respuesta no puede estar vacio");
        }
        this.texto = texto.trim();
        this.correcta = correcta;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpcionRespuesta)) return false;
        OpcionRespuesta that = (OpcionRespuesta) o;
        return correcta == that.correcta && texto.equals(that.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto, correcta);
    }

    @Override
    public String toString() {
        return texto + (correcta ? " (clave)" : "");
    }
}
