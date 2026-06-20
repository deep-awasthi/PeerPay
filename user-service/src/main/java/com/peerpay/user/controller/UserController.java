package com.peerpay.user.controller;

import com.peerpay.common.dto.ApiResponse;
import com.peerpay.common.dto.UserDTO;
import com.peerpay.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody UserDTO dto) {
        log.info("Received request to register user with email: {}", dto.getEmail());
        UserDTO registeredUser = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(registeredUser, "User registered successfully"));
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email and password are required"));
        }

        String token = userService.login(email, password);
        return ResponseEntity.ok(ApiResponse.success(Map.of("token", token), "Login successful"));
    }

    /**
     * Fetches details of a user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String contextUserId) {
        
        log.info("Fetching profile for user ID: {} | Context User ID: {}", id, contextUserId);
        
        // Context user ID is injected by API gateway from JWT
        if (contextUserId != null && !contextUserId.equals(id)) {
            log.warn("Access denied for context user {} requesting user profile {}", contextUserId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied. You cannot view other profiles."));
        }

        UserDTO user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User details fetched"));
    }

    /**
     * Fetches user details by UPI ID (needed by Payment service to check payee existence).
     */
    @GetMapping("/upi/{upiId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUpiId(@PathVariable String upiId) {
        log.info("Fetching user for UPI ID: {}", upiId);
        UserDTO user = userService.getUserByUpiId(upiId);
        return ResponseEntity.ok(ApiResponse.success(user, "User details fetched by UPI ID"));
    }
}
