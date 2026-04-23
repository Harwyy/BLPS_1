package com.blps.blps.controller;

import com.blps.blps.dto.request.LoginRequest;
import com.blps.blps.dto.request.RegisterRequest;
import com.blps.blps.security.jwt.JwtTokenProvider;
import com.blps.blps.security.service.XmlUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final XmlUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(buildTokenResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            userDetailsService.registerUser(registerRequest);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }

    private Authentication authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private Map<String, String> buildTokenResponse(String jwt) {
        return Map.of(
                "accessToken", jwt,
                "tokenType", "Bearer"
        );
    }

}