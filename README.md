# Banco de Preguntas Saber PRO - Taller 13 (Entrega Final, Fase II)

Laboratorio de Ingenieria de Software II - Universidad del Cauca - 2026.2

Ecosistema de microservicios con **DDD tactico**, **Arquitectura Hexagonal (puertos y adaptadores)**,
**mensajeria asincrona con RabbitMQ** y **contenerizacion con Docker / Docker Compose**.

## Microservicios

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| validation-service | 8082 | Valida el contenido de la pregunta con una tuberia de filtros (Pipes and Filters, HU03 del Corte I). |
| question-service | 8083 | Servicio principal. Dominio DDD puro + hexagonal. Crea preguntas y las envia a revision. |
| lifecycle-service | 8084 | Consume de forma asincrona los eventos de `pregunta.revisar.queue`. |
| rabbitmq | 5672 / 15672 | Broker AMQP y dashboard web (guest / guest). |

## Estructura

```
saberpro-banco-preguntas/
├── docker-compose.yml
├── build-all.sh / build-all.ps1
├── bruno/                <- Colección Bruno para pruebas automatizadas e interactivas de API
├── docs/postman/SaberPRO-Taller13.postman_collection.json
├── validation-service/   (Dockerfile, pom.xml, api/, filtros/)
├── question-service/
│   └── src/main/java/co/edu/unicauca/saberpro/question/
│       ├── domain/           <- DOMINIO PURO (sin Spring ni JPA)
│       │   ├── model/        Pregunta (raiz del agregado), Enunciado, Justificacion, OpcionRespuesta, EstadoPregunta
│       │   ├── exception/    ReglaNegocioException, PreguntaNoEncontradaException
│       │   └── ports/
│       │       ├── inbound/  CrearPreguntaUseCase, ConsultarPreguntasUseCase, EvaluarPreguntaUseCase
│       │       └── outbound/ PreguntaRepositoryPort, NotificacionPort, ValidacionContenidoPort
│       ├── application/      QuestionApplicationService
│       ├── infrastructure/
│       │   ├── jpa/          QuestionRecord, AnswerOptionRecord, QuestionDomainMapper, JpaQuestionAdapter
│       │   ├── data/         QuestionDataSeeder (poblado de datos de prueba en H2)
│       │   ├── memory/       InMemoryQuestionAdapter
│       │   ├── messaging/    RabbitMQConfig, RabbitMQPublisher
│       │   ├── rest/         RestValidacionAdapter (llamada sincrona a validation-service)
│       │   └── config/       BeanConfiguration (@Bean: aqui se intercambian los adaptadores)
│       └── api/              QuestionRestController, DTOs, GlobalExceptionHandler
└── lifecycle-service/    (Dockerfile, pom.xml, messaging/QuestionReviewListener)
```

## Reglas de negocio (invariantes del agregado Pregunta)

- `Enunciado`: no puede ser nulo ni vacio.
- `Justificacion`: minimo 15 caracteres.
- `OpcionRespuesta`: texto obligatorio + indicador de clave correcta.
- `Pregunta.enviarARevision()`: solo desde BORRADOR o RECHAZADA, exactamente **4 opciones** y exactamente **1 clave correcta**.
- `aprobar()` / `rechazar()`: solo si la pregunta esta en PENDIENTE_REVISION.

## Requisitos

- Java 17 y Maven 3.9+
- Docker Desktop (con Docker Compose v2)

## Despliegue

```bash
# 1. Generar el .jar de cada microservicio
cd validation-service && mvn clean package -DskipTests && cd ..
cd question-service   && mvn clean package -DskipTests && cd ..
cd lifecycle-service  && mvn clean package -DskipTests && cd ..

# 2. Construir imagenes y levantar todo en segundo plano
docker compose up -d --build

# 3. Verificar contenedores
docker ps

# 4. Ver logs en tiempo real
docker logs -f saberpro-question-service
docker logs -f saberpro-lifecycle-service

# Apagar
docker compose down
```

O simplemente: `./build-all.sh` (Linux/Mac) o `.\build-all.ps1` (Windows).

## Prueba de intercambiabilidad de adaptadores

En `docker-compose.yml`, servicio `question-service`:

```yaml
- SABERPRO_PERSISTENCIA=jpa      # JpaQuestionAdapter (H2)
- SABERPRO_PERSISTENCIA=memoria  # InMemoryQuestionAdapter (HashMap)
```

Despues de cambiarla:

```bash
docker compose up -d question-service
docker logs saberpro-question-service | grep "Adaptador de persistencia"
```

El log muestra cual adaptador quedo activo. El dominio y el servicio de aplicacion no cambian.
La prueba unitaria `IntercambiabilidadAdaptadoresTest` hace lo mismo sin Spring.

## Endpoints

| Metodo | URL | Respuesta esperada |
|---|---|---|
| GET | http://localhost:8083/api/preguntas | 200 OK |
| POST | http://localhost:8083/api/preguntas | 201 Created / 400 Bad Request |
| GET | http://localhost:8083/api/preguntas/{id} | 200 OK / 404 |
| POST | http://localhost:8083/api/preguntas/{id}/enviar-revision | 202 Accepted / 400 |
| POST | http://localhost:8083/api/preguntas/{id}/aprobar | 200 OK / 400 |
| POST | http://localhost:8083/api/preguntas/{id}/rechazar | 200 OK / 400 |
| GET | http://localhost:8084/api/ciclo-vida/revisiones | 200 OK (eventos consumidos) |
| POST | http://localhost:8082/api/validaciones | 200 OK `{valido, errores}` |

Ejemplo de cuerpo para crear una pregunta:

```json
{
  "enunciado": "Cual es la complejidad temporal de la busqueda binaria en un arreglo ordenado?",
  "justificacion": "En cada iteracion se descarta la mitad del espacio de busqueda, por eso es logaritmica.",
  "opciones": [
    { "texto": "O(n)", "correcta": false },
    { "texto": "O(log n)", "correcta": true },
    { "texto": "O(n log n)", "correcta": false },
    { "texto": "O(1)", "correcta": false }
  ]
}
```

La coleccion para Bruno esta disponible en la carpeta `bruno/` (abrir la carpeta directamente en la app de escritorio Bruno o usar `bru run --env Local`).
Tambien se incluye la coleccion legacy para Postman en `docs/postman/` (importar en Postman o Thunder Client).

## Monitoreo en RabbitMQ

1. Abrir http://localhost:15672 (guest / guest).
2. Pestana **Queues** -> `pregunta.revisar.queue`.
3. Enviar una pregunta a revision y ver el pico en *Message rates*.
4. Confirmar el consumo con `docker logs -f saberpro-lifecycle-service`.

## Problemas comunes

- **`openjdk:17-jdk-slim: not found`**: la imagen `openjdk` esta deprecada en Docker Hub.
  En cada Dockerfile cambiar la primera linea por `FROM eclipse-temurin:17-jdk`.
- **`COPY target/*.jar` falla**: no se ejecuto `mvn clean package -DskipTests` antes del `docker compose up`.
- **Puerto ocupado**: detener otros procesos en 8082-8084, 5672 o 15672.
- **503 al crear pregunta**: el validation-service aun esta arrancando; esperar unos segundos.
