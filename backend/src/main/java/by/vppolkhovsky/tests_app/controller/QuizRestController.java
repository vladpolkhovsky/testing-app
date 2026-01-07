package by.vppolkhovsky.tests_app.controller;

import by.vppolkhovsky.tests_app.controller.argresolver.JwtToken;
import by.vppolkhovsky.tests_app.dto.QuizContext;
import by.vppolkhovsky.tests_app.dto.QuizDto;
import by.vppolkhovsky.tests_app.dto.QuizRegistryDto;
import by.vppolkhovsky.tests_app.services.QuizContextHolder;
import by.vppolkhovsky.tests_app.services.QuizJpaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/editor")
public class QuizRestController {

    private final QuizJpaService quizJpaService;
    private final QuizContextHolder quizContextHolder;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public QuizRestController(QuizJpaService quizJpaService, QuizContextHolder quizContextHolder) {
        this.quizJpaService = quizJpaService;
        this.quizContextHolder = quizContextHolder;
    }

    @GetMapping("/create")
    public ResponseEntity<Void> create(@JwtToken(required = true) UUID userId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/editor/" + UUID.randomUUID()));

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .headers(httpHeaders)
            .build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizDto> get(@PathVariable UUID id, @JwtToken(required = true) UUID userId) {
        if (!quizJpaService.isUserHasAccessToQuiz(id, userId)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/"));

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .headers(httpHeaders)
                .build();
        }

        return ResponseEntity.of(quizJpaService.get(id));
    }

    @GetMapping(value = "/context/{contextId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizContext> get(@PathVariable String contextId) {
        QuizContext context = quizContextHolder.getContext(contextId);
        return ResponseEntity.ok(context);
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizDto> save(@PathVariable UUID id, @RequestBody QuizDto quiz, @JwtToken(required = true) UUID userId) {
        return ResponseEntity.ok(quizJpaService.save(id, quiz, userId));
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable UUID id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        quizJpaService.get(id).ifPresent((quiz) -> {
            String contextId = quizContextHolder.createContextId();
            httpHeaders.setLocation(URI.create("/g/" + contextId));
            quizContextHolder.createContext(contextId, quiz);
        });

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .headers(httpHeaders)
            .build();
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));

        quizJpaService.delete(id);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .headers(httpHeaders)
            .build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<QuizRegistryDto>> list(@JwtToken UUID userId) {
        if (userId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(quizJpaService.list(userId));
    }
}
