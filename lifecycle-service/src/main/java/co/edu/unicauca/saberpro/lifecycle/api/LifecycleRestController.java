package co.edu.unicauca.saberpro.lifecycle.api;

import co.edu.unicauca.saberpro.lifecycle.messaging.PreguntaEnviadaRevisionEvent;
import co.edu.unicauca.saberpro.lifecycle.messaging.QuestionReviewListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Permite ver desde Postman los eventos que ha consumido el lifecycle-service.
 */
@RestController
@RequestMapping("/api/ciclo-vida")
public class LifecycleRestController {

    private final QuestionReviewListener listener;

    public LifecycleRestController(QuestionReviewListener listener) {
        this.listener = listener;
    }

    @GetMapping("/revisiones")
    public List<PreguntaEnviadaRevisionEvent> revisiones() {
        return listener.getRecibidos();
    }
}
