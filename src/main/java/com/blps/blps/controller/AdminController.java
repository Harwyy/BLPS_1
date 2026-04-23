package com.blps.blps.controller;

import com.blps.blps.dto.UserDto;
import com.blps.blps.dto.request.CreateUserRequest;
import com.blps.blps.security.model.XmlUser;
import com.blps.blps.security.service.XmlUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final XmlUserDetailsService userDetailsService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(mapToUserDtoList(userDetailsService.getAllUsers()));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            XmlUser created = userDetailsService.createUser(request);
            return ResponseEntity.ok(mapToUserDto(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "User creation failed: " + e.getMessage()));
        }
    }

    private List<UserDto> mapToUserDtoList(List<XmlUser> users) {
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(XmlUser user) {
        return new UserDto(user.getUsername(), user.getRole(), user.getReferenceId());
    }
}