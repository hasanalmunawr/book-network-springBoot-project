package com.hasanalmunawr.booknetwork.authentication;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Tag(name = "Authentication")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register",
    consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterRequest request
    ) throws MessagingException {
        authService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthRequest request
    ) {
        if (request == null) {
            log.info("INI REQUEST NYA NULLLLLLLLLLLLLLLLLL");
        }
        return ResponseEntity.ok(authService.login(request));

    }

    @GetMapping(path = "/activate-account")
    public void confirm(
            @RequestParam String tokenCode
    ) throws MessagingException {
        authService.activateAccount(tokenCode);
    }
}
