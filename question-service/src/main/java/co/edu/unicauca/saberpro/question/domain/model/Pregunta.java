package co.edu.unicauca.saberpro.question.domain.model;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Raiz del Agregado Pregunta.
 *
 * Encapsula el enunciado, la justificacion y las opciones de respuesta.
 * Todas las modificaciones de estado pasan por metodos de negocio que
 * protegen las invariantes; no hay setters publicos.
 *
 * IMPORTANTE: esta clase es Java puro. No tiene @Entity, @Id ni nada de Spring.
 */
public class Pregunta {

    public static final int NUMERO_OPCIONES_REQUERIDAS = 4;

    private Long id;
    private final Enunciado enunciado;
    private final Justificacion justificacion;
    private final List<OpcionRespuesta> opciones;
    private EstadoPregunta estado;

    private Pregunta(Long id, Enunciado enunciado, Justificacion justificacion,
                     List<OpcionRespuesta> opciones, EstadoPregunta estado) {
        this.id = id;
        this.enunciado = Objects.requireNonNull(enunciado, "enunciado requerido");
        this.justificacion = Objects.requireNonNull(justificacion, "justificacion requerida");
        if (opciones == null) {
            throw new ReglaNegocioException("La pregunta debe tener una lista de opciones");
        }
        this.opciones = new ArrayList<>(opciones);
        this.estado = Objects.requireNonNull(estado, "estado requerido");
    }

    /**
     * Fabrica para crear una pregunta nueva. Siempre inicia en BORRADOR y sin id
     * (el id lo asigna el repositorio al guardar).
     */
    public static Pregunta crear(Enunciado enunciado, Justificacion justificacion,
                                 List<OpcionRespuesta> opciones) {
        return new Pregunta(null, enunciado, justificacion, opciones, EstadoPregunta.BORRADOR);
    }

    /**
     * Fabrica para reconstruir una pregunta que ya existe (la usan los adaptadores
     * de persistencia al leer de la base de datos o de memoria).
     */
    public static Pregunta reconstruir(Long id, Enunciado enunciado, Justificacion justificacion,
                                       List<OpcionRespuesta> opciones, EstadoPregunta estado) {
        return new Pregunta(id, enunciado, justificacion, opciones, estado);
    }

    // ------------------------------------------------------------------
    // Comportamiento de negocio
    // ------------------------------------------------------------------

    /**
     * Cambia el estado a PENDIENTE_REVISION.
     * Invariantes:
     *  - Solo se puede enviar desde BORRADOR o RECHAZADA.
     *  - Debe tener exactamente 4 opciones.
     *  - Debe tener exactamente 1 opcion marcada como correcta.
     */
    public void enviarARevision() {
        if (estado != EstadoPregunta.BORRADOR && estado != EstadoPregunta.RECHAZADA) {
            throw new ReglaNegocioException(
                    "Solo se puede enviar a revision una pregunta en BORRADOR o RECHAZADA (estado actual: " + estado + ")");
        }
        if (opciones.size() != NUMERO_OPCIONES_REQUERIDAS) {
            throw new ReglaNegocioException(
                    "La pregunta debe tener exactamente " + NUMERO_OPCIONES_REQUERIDAS
                            + " opciones de respuesta (tiene " + opciones.size() + ")");
        }
        long correctas = opciones.stream().filter(OpcionRespuesta::isCorrecta).count();
        if (correctas != 1) {
            throw new ReglaNegocioException(
                    "La pregunta debe tener exactamente 1 clave correcta (tiene " + correctas + ")");
        }
        this.estado = EstadoPregunta.PENDIENTE_REVISION;
    }

    /** Aprueba la pregunta. Solo es valido si esta pendiente de revision. */
    public void aprobar() {
        validarPendiente("aprobar");
        this.estado = EstadoPregunta.APROBADA;
    }

    /** Rechaza la pregunta. Solo es valido si esta pendiente de revision. */
    public void rechazar() {
        validarPendiente("rechazar");
        this.estado = EstadoPregunta.RECHAZADA;
    }

    private void validarPendiente(String accion) {
        if (estado != EstadoPregunta.PENDIENTE_REVISION) {
            throw new ReglaNegocioException(
                    "Solo se puede " + accion + " una pregunta PENDIENTE_REVISION (estado actual: " + estado + ")");
        }
    }

    /** El repositorio asigna el id una sola vez cuando la pregunta se guarda por primera vez. */
    public void asignarId(Long id) {
        if (this.id != null) {
            throw new ReglaNegocioException("La pregunta ya tiene un id asignado");
        }
        this.id = id;
    }

    // ------------------------------------------------------------------
    // Getters (solo lectura)
    // ------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public Enunciado getEnunciado() {
        return enunciado;
    }

    public Justificacion getJustificacion() {
        return justificacion;
    }

    /** Se devuelve una copia inmodificable para que nadie altere el agregado desde afuera. */
    public List<OpcionRespuesta> getOpciones() {
        return Collections.unmodifiableList(opciones);
    }

    public EstadoPregunta getEstado() {
        return estado;
    }
}
