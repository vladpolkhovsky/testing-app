package by.vppolkhovsky.tests_app.controller;

import by.vppolkhovsky.tests_app.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class QuizRestController {

    @PostMapping("/create")
    public ResponseEntity<UserDto> register(@RequestBody UserDto user) {
        return ResponseEntity.ok(null);
    }
}
