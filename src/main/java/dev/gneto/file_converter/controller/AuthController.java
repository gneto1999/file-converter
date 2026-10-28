package dev.gneto.file_converter.controller;

import dev.gneto.file_converter.controller.docs.AuthControllerDocs;
import dev.gneto.file_converter.dto.request.AuthUserRequest;
import dev.gneto.file_converter.dto.request.UserRegisterRequest;
import dev.gneto.file_converter.dto.response.AuthUserResponse;
import dev.gneto.file_converter.dto.response.UserRegisteredResponse;
import dev.gneto.file_converter.services.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            value = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserRegisteredResponse> signup(@RequestBody @Valid UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<AuthUserResponse> login(@RequestBody @Valid AuthUserRequest request) {
        return ResponseEntity.ok().body(authService.login(request));
    }

    @PostMapping(
            value = "/refresh",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<AuthUserResponse> refreshToken(
            @RequestHeader("X-Refresh-Token") @NotEmpty String authorizationHeader) {
        return ResponseEntity.ok().body(authService.refreshToken(authorizationHeader));
    }

    @PostMapping(
            value = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Void> logout(@RequestHeader("X-Refresh-Token") @NotEmpty String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/logout-all")
    @Override
    public ResponseEntity<Void> logoutAll(Authentication authentication) {
        authService.logoutAll(authentication);
        return ResponseEntity.noContent().build();
    }
}
