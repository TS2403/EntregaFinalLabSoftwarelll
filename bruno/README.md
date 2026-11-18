# Colección Bruno - Banco de Preguntas Saber PRO

Colección completa para pruebas de los microservicios del ecosistema **Saber PRO** usando [Bruno](https://usebruno.com/).

## Estructura de la Colección

```
bruno/
├── bruno.json                                          # Configuración raíz de la colección
├── environments/
│   ├── Local.bru                                       # Variables para ejecución local
│   └── Docker.bru                                      # Variables para ejecución en contenedores
├── 01-Question-Service/                                # Endpoints del microservicio principal (puerto 8083)
│   ├── 01-Listar-todas-las-preguntas.bru
│   ├── 02-Consultar-pregunta-por-id.bru
│   ├── 03-Crear-pregunta-valida.bru
│   ├── 04-Error-Crear-Justificacion-corta.bru
│   ├── 05-Error-Crear-Opciones-repetidas.bru
│   ├── 06-Error-Crear-Lenguaje-ofensivo.bru
│   ├── 07-Crear-pregunta-con-3-opciones.bru
│   ├── 08-Enviar-a-revision.bru
│   ├── 09-Error-Enviar-a-revision-3-opciones.bru
│   ├── 10-Aprobar-pregunta.bru
│   ├── 11-Rechazar-pregunta.bru
│   ├── 12-Error-Aprobar-pregunta-en-borrador.bru
│   ├── 13-Consultar-pregunta-inexistente.bru
│   └── 14-Acceso-a-Consola-Web-H2.bru
├── 02-Validation-Service/                              # Endpoints de validación con Pipes & Filters (puerto 8082)
│   ├── 01-Verificar-estado-del-servicio.bru
│   ├── 02-Validar-pregunta-valida.bru
│   ├── 03-Validar-pregunta-opciones-duplicadas.bru
│   ├── 04-Validar-pregunta-lenguaje-prohibido.bru
│   └── 05-Validar-pregunta-enunciado-muy-corto.bru
└── 03-Lifecycle-Service/                               # Endpoints de ciclo de vida / eventos AMQP (puerto 8084)
    └── 01-Consultar-eventos-de-revision-consumidos.bru
```

## Cómo abrir en Bruno (Interfaz Gráfica)

1. Abre la aplicación de escritorio **Bruno**.
2. Selecciona **"Open Collection"**.
3. Selecciona la carpeta `bruno/` de este repositorio.
4. En la esquina superior derecha de Bruno, selecciona el entorno **Local** (o **Docker**).
5. Ejecuta las peticiones de forma individual o la carpeta completa con **"Run Collection"**.

## Cómo ejecutar desde la Terminal (Bruno CLI)

Si tienes instalado `@usebruno/cli`:

```bash
# Ejecutar todas las pruebas con el entorno Local
bru run --env Local

# Ejecutar únicamente las pruebas del Question Service
bru run 01-Question-Service --env Local
```
