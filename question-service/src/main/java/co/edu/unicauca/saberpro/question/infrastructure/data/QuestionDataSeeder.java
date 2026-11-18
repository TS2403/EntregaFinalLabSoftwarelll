package co.edu.unicauca.saberpro.question.infrastructure.data;

import co.edu.unicauca.saberpro.question.domain.model.Enunciado;
import co.edu.unicauca.saberpro.question.domain.model.Justificacion;
import co.edu.unicauca.saberpro.question.domain.model.OpcionRespuesta;
import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeder de datos iniciales para la base de datos H2 (o el repositorio activo).
 *
 * Puebla el sistema con preguntas de prueba en diferentes estados para facilitar
 * las validaciones desde clientes API (Bruno, Postman, etc.).
 */
@Component
@ConditionalOnProperty(name = "saberpro.seeder.enabled", havingValue = "true", matchIfMissing = true)
public class QuestionDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QuestionDataSeeder.class);

    private final PreguntaRepositoryPort repositorio;

    public QuestionDataSeeder(PreguntaRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (!repositorio.listarTodas().isEmpty()) {
            log.info("=== Seeder de preguntas omitido: ya existen {} registros en persistencia ===",
                    repositorio.listarTodas().size());
            return;
        }

        log.info("=== Inicializando Seeder: poblando datos de prueba en la base de datos H2 ===");

        // Pregunta 1: BORRADOR - Completa con 4 opciones (lista para enviar a revision)
        Pregunta p1 = Pregunta.crear(
                new Enunciado("¿Cuál es la complejidad temporal promedio de la búsqueda binaria en un arreglo ordenado de n elementos?"),
                new Justificacion("En cada iteración se descarta la mitad del espacio de búsqueda, resultando en un costo logarítmico O(log n)."),
                List.of(
                        new OpcionRespuesta("O(n)", false),
                        new OpcionRespuesta("O(log n)", true),
                        new OpcionRespuesta("O(n log n)", false),
                        new OpcionRespuesta("O(1)", false)
                )
        );
        repositorio.guardar(p1);

        // Pregunta 2: BORRADOR - Incompleta (3 opciones, ideal para probar regla de negocio de rechazo de revision)
        Pregunta p2 = Pregunta.crear(
                new Enunciado("¿Cuál comando de Docker Compose reconstruye las imágenes y levanta los servicios en segundo plano?"),
                new Justificacion("El parámetro --build fuerza la construcción de imágenes y -d inicia los contenedores en modo detached."),
                List.of(
                        new OpcionRespuesta("docker compose up -d --build", true),
                        new OpcionRespuesta("docker compose start", false),
                        new OpcionRespuesta("docker run -d --build", false)
                )
        );
        repositorio.guardar(p2);

        // Pregunta 3: PENDIENTE_REVISION (lista para probar aprobacion o rechazo)
        Pregunta p3 = Pregunta.crear(
                new Enunciado("En Domain-Driven Design (DDD), ¿cuál es el propósito de una Raíz de Agregado (Aggregate Root)?"),
                new Justificacion("La Raíz de Agregado protege las invariantes de negocio y garantiza la coherencia transaccional del agregado."),
                List.of(
                        new OpcionRespuesta("Centralizar la conexión directa a los controladores REST", false),
                        new OpcionRespuesta("Proteger las invariantes y garantizar la coherencia interna del agregado", true),
                        new OpcionRespuesta("Mapear automáticamente las entidades a tablas relacionales", false),
                        new OpcionRespuesta("Reemplazar el uso de puertos y adaptadores en la arquitectura", false)
                )
        );
        p3.enviarARevision();
        repositorio.guardar(p3);

        // Pregunta 4: APROBADA (evaluada favorablemente)
        Pregunta p4 = Pregunta.crear(
                new Enunciado("En Arquitectura Hexagonal, ¿qué componente define la interfaz que desacopla el núcleo de las tecnologías externas?"),
                new Justificacion("Los puertos definen contratos abstractos independientes de frameworks o librerías de infraestructura."),
                List.of(
                        new OpcionRespuesta("El Puerto (Port)", true),
                        new OpcionRespuesta("El Adaptador (Adapter)", false),
                        new OpcionRespuesta("El Controlador REST", false),
                        new OpcionRespuesta("El Repositorio JPA", false)
                )
        );
        p4.enviarARevision();
        p4.aprobar();
        repositorio.guardar(p4);

        // Pregunta 5: RECHAZADA (evaluada con observaciones)
        Pregunta p5 = Pregunta.crear(
                new Enunciado("¿Cuál de las siguientes afirmaciones describe mejor el patrón Pipes and Filters?"),
                new Justificacion("Divide el procesamiento de datos en etapas secuenciales independientes donde cada filtro transforma su entrada."),
                List.of(
                        new OpcionRespuesta("Divide una tarea en una secuencia de componentes independientes de procesamiento", true),
                        new OpcionRespuesta("Maneja transacciones distribuidas mediante el patrón Saga exclusivamente", false),
                        new OpcionRespuesta("Sincroniza bases de datos relacionales con colas de mensajería AMQP", false),
                        new OpcionRespuesta("Es un patrón exclusivo para el diseño de interfaces gráficas reactivas", false)
                )
        );
        p5.enviarARevision();
        p5.rechazar();
        repositorio.guardar(p5);

        log.info("=== Seeder finalizado exitosamente: {} preguntas registradas ===",
                repositorio.listarTodas().size());
    }
}
