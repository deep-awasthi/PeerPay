package com.peerpay.user.service;

import com.peerpay.common.dto.UserDTO;
import com.peerpay.common.exception.ResourceNotFoundException;
import com.peerpay.user.model.User;
import com.peerpay.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecretKey signingKey;
    private final long jwtExpirationMs = 86400000; // 24 hours

    public UserService(UserRepository userRepository,
                       @Value("${peerpay.jwt.secret:PeerPaySuperSecretKeyForJWTTokenSigningMin256Bits!!}") String secret) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Registers a new user.
     */
    @Transactional
    public UserDTO registerUser(UserDTO dto) {
        log.info("Registering new user with email: {}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered: " + dto.getPhone());
        }
        if (userRepository.findByUpiId(dto.getUpiId()).isPresent()) {
            throw new IllegalArgumentException("UPI ID already registered: " + dto.getUpiId());
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .upiId(dto.getUpiId())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("User registered successfully. ID: {}", user.getId());

        return mapToDTO(user);
    }

    /**
     * Authenticates a user and returns a signed JWT token.
     */
    public String login(String email, String password) {
        log.info("Attempting login for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("User authenticated. Generating JWT...");

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("upiId", user.getUpiId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    public UserDTO getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToDTO(user);
    }

    public UserDTO getUserByUpiId(String upiId) {
        User user = userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new ResourceNotFoundException("User", upiId));
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .upiId(user.getUpiId())
                .build();
    }
}
