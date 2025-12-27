package by.vppolkhovsky.tests_app.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import by.vppolkhovsky.tests_app.controller.argresolver.JwtToken;
import by.vppolkhovsky.tests_app.dto.LoginRequest;
import by.vppolkhovsky.tests_app.dto.UserDto;
import by.vppolkhovsky.tests_app.jpa.entity.UserEntity;
import by.vppolkhovsky.tests_app.jpa.repository.UserRepository;
import by.vppolkhovsky.tests_app.mapper.UserMapper;
import by.vppolkhovsky.tests_app.services.DataSigner;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final DataSigner dataSigner;

    @Transactional(readOnly = true)
    @GetMapping(value = "/iam", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> iam(@JwtToken UUID userId) {
        return ResponseEntity.of(userRepository.findById(userId).map(userMapper::toDto));
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0) // 24 hours
            .build();

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.LOCATION, "/")
            .build();
    }

    @Transactional(readOnly = true)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest request) {
        Optional<UserDto> fetched = fetchAndCheck(request);

        if (fetched.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDto userDto = fetched.get();
        Map<String, String> jwt = Map.of("userid", userDto.getUserId(), "signature", dataSigner.createSignature(userDto.getUserId()));

        ResponseCookie cookie = ResponseCookie.from("JWT", Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(jwt)))
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(60 * 60 * 24) // 24 hours
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(userDto);
    }

    private @NonNull Optional<UserDto> fetchAndCheck(LoginRequest request) {
        return userRepository.findFirstByUsername(request.getUsername())
            .filter(user -> BCrypt.verifyer().verify(request.getPassword().getBytes(StandardCharsets.UTF_8), user.getPassword().getBytes(StandardCharsets.UTF_8)).verified)
            .map(userMapper::toDto);
    }

    @Transactional
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> register(@RequestBody LoginRequest request) {

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        byte[] hash = BCrypt.withDefaults().hash(12, request.getPassword().getBytes(StandardCharsets.UTF_8));
        String hashString = new String(hash, StandardCharsets.UTF_8);
        userEntity.setPassword(hashString);

        userRepository.save(userEntity);

        return login(request);
    }
}
