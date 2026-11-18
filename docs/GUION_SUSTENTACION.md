# Guion de sustentacion - Taller 13 (10 minutos max)

**Antes de entrar (preparacion, no cuenta en el tiempo):**

- [ ] `mvn clean package -DskipTests` en los 3 servicios.
- [ ] `docker compose up -d --build` y verificar con `docker ps` que los 4 contenedores esten `Up`.
- [ ] Postman abierto con la coleccion `docs/postman/SaberPRO-Taller13.postman_collection.json` importada.
- [ ] Pestana del navegador en http://localhost:15672 (guest / guest), ya logueados, en **Queues**.
- [ ] Dos terminales abiertas: una para `docker logs -f saberpro-lifecycle-service` y otra libre.
- [ ] VS Code / IntelliJ abierto en `question-service/src/main/java/.../question/` con `Pregunta.java` y `BeanConfiguration.java` en pestanas.
- [ ] Hacer un `docker compose down -v && docker compose up -d` antes de entrar para que los ids empiecen en 1.

Reparto sugerido: **Persona A** = min 0-4 y 9-10, **Persona B** = min 4-9.

---

## Min 0-2 · Dominio DDD puro (Persona A)

**Mostrar:** arbol de carpetas `domain/` y luego `Pregunta.java`.

> "Empezamos por el nucleo. En el paquete domain tenemos el agregado Pregunta y sus objetos de valor:
> Enunciado, Justificacion y OpcionRespuesta. Si se fijan, ninguna clase importa Spring ni JPA, no hay
> @Entity ni @Component. Es Java puro."

> "Los objetos de valor son inmutables y se validan solos en el constructor: un Enunciado no puede ser
> vacio y una Justificacion necesita minimo 15 caracteres; si no, lanzan ReglaNegocioException."

**Senalar** el metodo `enviarARevision()`:

> "La raiz del agregado protege las invariantes. Para pasar a PENDIENTE_REVISION la pregunta debe estar
> en borrador, tener exactamente 4 opciones y exactamente 1 clave correcta. No hay setters: el estado solo
> cambia por metodos de negocio, y la lista de opciones se devuelve inmodificable."

(Opcional si sobra tiempo: mostrar `PreguntaTest` corriendo en verde.)

## Min 2-4 · Arquitectura Hexagonal (Persona A)

**Mostrar:** `domain/ports/inbound` y `outbound`, luego `QuestionApplicationService`.

> "Los puertos de entrada son los casos de uso: crear, consultar y evaluar. Los de salida son lo que el
> dominio necesita del mundo exterior: guardar, notificar y validar. El servicio de aplicacion solo
> conoce interfaces."

**Mostrar** `BeanConfiguration.java`:

> "Aqui se conectan los adaptadores. Con @Bean y @ConditionalOnProperty elegimos entre
> JpaQuestionAdapter e InMemoryQuestionAdapter segun la propiedad saberpro.persistencia."

**Demo de intercambio (rapida):**

```bash
# en docker-compose.yml cambiar SABERPRO_PERSISTENCIA=jpa  ->  memoria
docker compose up -d question-service
docker logs saberpro-question-service | grep "Adaptador de persistencia"
```

> "Cambiamos una variable de entorno, recreamos solo ese contenedor y el log ya dice
> InMemoryQuestionAdapter. No tocamos ni una linea del dominio."

(Si el tiempo es justo: tener ya una captura de ambos logs y solo mostrarla. Luego volver a `jpa`.)

## Min 4-6 · Infraestructura Docker (Persona B)

**Mostrar:** `question-service/Dockerfile`.

> "Cada microservicio tiene su Dockerfile: imagen base de Java 17, directorio /app, copiamos el .jar,
> exponemos el puerto y el ENTRYPOINT ejecuta java -jar."

**Mostrar:** `docker-compose.yml`.

> "El compose orquesta cinco cosas: RabbitMQ y los tres microservicios, todos en la red bridge
> saberpro-net. Dentro de esa red los contenedores se llaman por nombre: por eso question-service usa
> rabbitmq y validation-service como host, no localhost."

> "Agregamos un healthcheck a RabbitMQ y depends_on con service_healthy, porque depends_on solo espera
> a que el contenedor arranque, no a que Rabbit acepte conexiones."

**Terminal:** `docker ps` -> mostrar los 4 contenedores Up y los puertos 8082, 8083, 8084, 5672, 15672.

## Min 6-9 · Prueba en vivo: Postman + RabbitMQ (Persona B)

Ejecutar en orden (coleccion de Postman):

1. **01 GET /api/preguntas** -> `200 OK`, lista vacia.
2. **02 POST crear pregunta valida** -> `201 Created` con `id: 1`, estado `BORRADOR`.
   > "Antes de guardar, question-service llamo de forma sincrona al validation-service."
3. **03 POST justificacion corta** -> `400 Bad Request`.
   > "Esto lo rechazo el objeto de valor Justificacion, en el dominio."
4. **04 POST opciones repetidas** -> `400`.
   > "Este otro lo rechazo el validation-service con su tuberia de filtros."
5. **06 POST enviar-revision** -> `202 Accepted`.
   > "Responde de inmediato porque el envio es asincrono: el evento se publico en RabbitMQ."
6. **Cambiar a la terminal de logs** del lifecycle-service: se ve
   `Mensaje recibido de pregunta.revisar.queue -> pregunta id=1`.
7. **Cambiar al Dashboard de RabbitMQ**: cola `pregunta.revisar.queue`, grafica de *Message rates*
   con el pico (Ready en 0 porque ya se consumio).
8. **09 GET :8084/api/ciclo-vida/revisiones** -> el evento recibido.
9. (Si hay tiempo) **05 + 07**: pregunta con 3 opciones se crea, pero enviarla a revision da `400`
   por la invariante del agregado.

## Min 9-10 · Conclusiones y trazabilidad (Persona A)

> "Para cerrar, la trazabilidad del semestre: en el Corte I teniamos un monolito limpio con SOLID y el
> pipeline de validacion con Pipes and Filters, que hoy vive en el validation-service. En el Corte II
> separamos en microservicios con REST, RabbitMQ y patrones GoF; el State de la pregunta hoy es parte
> del agregado. Y en este corte aislamos el dominio con DDD y hexagonal, y lo desplegamos todo con
> Docker Compose."

> "Lo principal que aprendimos: con el dominio aislado las reglas se prueban sin infraestructura, los
> adaptadores se cambian con una variable, y Docker nos da un despliegue repetible con un solo comando."

---

## Preguntas que pueden hacer (y respuesta corta)

- **Por que el agregado no tiene @Entity?** Porque el dominio no debe depender de la tecnologia de
  persistencia. Usamos QuestionRecord + QuestionDomainMapper en infraestructura.
- **Que pasa si RabbitMQ esta caido al enviar a revision?** El GlobalExceptionHandler captura
  AmqpException y responde 503.
- **Que pasa si validation-service esta caido?** RestValidacionAdapter lanza
  ServicioValidacionNoDisponibleException -> 503.
- **Diferencia entre imagen y contenedor?** La imagen es la plantilla inmutable; el contenedor es la
  instancia en ejecucion creada a partir de ella.
- **Por que 202 y no 200?** Porque la solicitud fue aceptada pero se procesa despues, de forma asincrona.
- **Como sabe question-service la IP de rabbitmq?** No la necesita: Docker resuelve el nombre del
  servicio con su DNS interno dentro de saberpro-net.
- **Que es un objeto de valor?** Un objeto sin identidad propia, inmutable, que se compara por su
  contenido (equals/hashCode).
