package co.edu.unicauca.saberpro.question.infrastructure.memory;

import co.edu.unicauca.saberpro.question.domain.model.Pregunta;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.PreguntaRepositoryPort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adaptador secundario de persistencia EN MEMORIA (HashMap).
 *
 * Sirve para la prueba de intercambiabilidad: implementa el mismo puerto que
 * JpaQuestionAdapter, asi que el dominio y el servicio de aplicacion funcionan
 * igual sin cambiar una sola linea. Tambien se usa en las pruebas unitarias.
 */
public class InMemoryQuestionAdapter implements PreguntaRepositoryPort {

    private final Map<Long, Pregunta> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public Pregunta guardar(Pregunta pregunta) {
        if (pregunta.getId() == null) {
            pregunta.asignarId(secuencia.incrementAndGet());
        }
        almacen.put(pregunta.getId(), pregunta);
        return pregunta;
    }

    @Override
    public Optional<Pregunta> buscarPorId(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<Pregunta> listarTodas() {
        List<Pregunta> lista = new ArrayList<>(almacen.values());
        lista.sort(Comparator.comparing(Pregunta::getId));
        return lista;
    }
}
