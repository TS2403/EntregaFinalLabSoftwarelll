package co.edu.unicauca.saberpro.question.api;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.ConsultarPreguntasUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.CrearPreguntaCommand;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.CrearPreguntaUseCase.OpcionCommand;
import co.edu.unicauca.saberpro.question.domain.ports.inbound.EvaluarPreguntaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Adaptador PRIMARIO (driving adapter) REST.
 * Solo depende de los puertos de entrada (interfaces de casos de uso).
 */
@RestController
@RequestMapping("/api/preguntas")
public class QuestionRestController {

    private final CrearPreguntaUseCase crearPregunta;
    private final ConsultarPreguntasUseCase consultarPreguntas;
    private final EvaluarPreguntaUseCase evaluarPregunta;

    public QuestionRestController(CrearPreguntaUseCase crearPregunta,
                                  ConsultarPreguntasUseCase consultarPreguntas,
                                  EvaluarPreguntaUseCase evaluarPregunta) {
        this.crearPregunta = crearPregunta;
        this.consultarPreguntas = consultarPreguntas;
        this.evaluarPregunta = evaluarPregunta;
    }

    /** GET /api/preguntas -> 200 OK con la lista. */
    @GetMapping
    public List<PreguntaResponse> listar() {
        return consultarPreguntas.listar().stream().map(PreguntaResponse::desde).toList();
    }

    /** GET /api/preguntas/{id} -> 200 OK o 404. */
    @GetMapping("/{id}")
    public PreguntaResponse buscar(@PathVariable Long id) {
        return PreguntaResponse.desde(consultarPreguntas.buscarPorId(id));
    }

    /** POST /api/preguntas -> 201 Created (sincrono) o 400 si viola reglas de negocio. */
    @PostMapping
    public ResponseEntity<PreguntaResponse> crear(@RequestBody PreguntaRequest request) {
        List<OpcionCommand> opciones = request.opciones() == null ? List.of()
                : request.opciones().stream().map(o -> new OpcionCommand(o.texto(), o.correcta())).toList();

        Pregunta creada = crearPregunta.crear(
                new CrearPreguntaCommand(request.enunciado(), request.justificacion(), opciones));

        return ResponseEntity
                .created(URI.create("/api/preguntas/" + creada.getId()))
                .body(PreguntaResponse.desde(creada));
    }

    /** POST /api/preguntas/{id}/enviar-revision -> 202 Accepted (evento a RabbitMQ). */
    @PostMapping("/{id}/enviar-revision")
    public ResponseEntity<Map<String, Object>> enviarARevision(@PathVariable Long id) {
        Pregunta pregunta = evaluarPregunta.enviarARevision(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "mensaje", "Solicitud de revision despachada a la cola pregunta.revisar.queue",
                "id", pregunta.getId(),
                "estado", pregunta.getEstado().name()));
    }

    @PostMapping("/{id}/aprobar")
    public PreguntaResponse aprobar(@PathVariable Long id) {
        return PreguntaResponse.desde(evaluarPregunta.aprobar(id));
    }

    @PostMapping("/{id}/rechazar")
    public PreguntaResponse rechazar(@PathVariable Long id) {
        return PreguntaResponse.desde(evaluarPregunta.rechazar(id));
    }
}
