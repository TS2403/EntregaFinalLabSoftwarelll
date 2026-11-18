package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.question.domain.exception.PreguntaNoEncontradaException;
import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.model.Enunciado;
import co.edu.unicauca.saberpro.question.domain.model.Justificacion;
import co.edu.unicauca.saberpro.question.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.ConsultarPreguntasUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.EvaluarPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionContenidoPort;

import java.util.List;

/**
 * Servicio de aplicacion: orquesta los casos de uso.
 *
 * - Implementa los puertos de entrada.
 * - Depende SOLO de puertos de salida (interfaces), nunca de adaptadores concretos.
 * - No tiene anotaciones de Spring: se registra como @Bean en BeanConfiguration.
 *   Gracias a esto se puede probar con adaptadores falsos o en memoria.
 */
public class QuestionApplicationService
        implements CrearPreguntaUseCase, ConsultarPreguntasUseCase, EvaluarPreguntaUseCase {

    private final PreguntaRepositoryPort repositorio;
    private final NotificacionPort notificador;
    private final ValidacionContenidoPort validador;

    public QuestionApplicationService(PreguntaRepositoryPort repositorio,
                                      NotificacionPort notificador,
                                      ValidacionContenidoPort validador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.validador = validador;
    }

    // ------------------------- Crear -------------------------

    @Override
    public Pregunta crear(CrearPreguntaCommand comando) {
        if (comando == null) {
            throw new ReglaNegocioException("La solicitud de creacion no puede ser nula");
        }
        List<OpcionRespuesta> opciones = comando.opciones() == null ? List.of()
                : comando.opciones().stream()
                    .map(o -> new OpcionRespuesta(o.texto(), o.correcta()))
                    .toList();

        // Los objetos de valor validan sus propias reglas al construirse
        Pregunta pregunta = Pregunta.crear(
                new Enunciado(comando.enunciado()),
                new Justificacion(comando.justificacion()),
                opciones);

        // Llamada sincrona al validation-service (Pipes & Filters del Corte I)
        List<String> errores = validador.validar(pregunta);
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException("Validacion de contenido fallida: " + String.join("; ", errores));
        }

        return repositorio.guardar(pregunta);
    }

    // ------------------------- Consultar -------------------------

    @Override
    public List<Pregunta> listar() {
        return repositorio.listarTodas();
    }

    @Override
    public Pregunta buscarPorId(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new PreguntaNoEncontradaException(id));
    }

    // ------------------------- Evaluar -------------------------

    @Override
    public Pregunta enviarARevision(Long id) {
        Pregunta pregunta = buscarPorId(id);
        pregunta.enviarARevision();                 // el agregado protege sus invariantes
        Pregunta guardada = repositorio.guardar(pregunta);
        notificador.notificarEnvioARevision(guardada); // evento asincrono (RabbitMQ)
        return guardada;
    }

    @Override
    public Pregunta aprobar(Long id) {
        Pregunta pregunta = buscarPorId(id);
        pregunta.aprobar();
        return repositorio.guardar(pregunta);
    }

    @Override
    public Pregunta rechazar(Long id) {
        Pregunta pregunta = buscarPorId(id);
        pregunta.rechazar();
        return repositorio.guardar(pregunta);
    }
}
