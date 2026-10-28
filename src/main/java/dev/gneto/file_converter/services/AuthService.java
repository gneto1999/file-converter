package dev.gneto.file_converter.services;

import dev.gneto.file_converter.dto.JwtToken;
import dev.gneto.file_converter.dto.request.AuthUserRequest;
import dev.gneto.file_converter.dto.request.UserRegisterRequest;
import dev.gneto.file_converter.dto.response.AuthUserResponse;
import dev.gneto.file_converter.dto.response.UserRegisteredResponse;
import dev.gneto.file_converter.enums.UserRole;
import dev.gneto.file_converter.exception.MissingJwtTokenException;
import dev.gneto.file_converter.exception.UserAlreadyRegisteredException;
import dev.gneto.file_converter.exception.UserNotFoundException;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.repository.UserRepository;
import dev.gneto.file_converter.security.jwt.JwtUtil;
import dev.gneto.file_converter.security.jwt.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil, RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public UserRegisteredResponse signup(UserRegisterRequest request) {
        if (userRepository.findUserByEmail(request.email()).isPresent()) {
            throw new UserAlreadyRegisteredException("User already exists with this email: " + request.email());
        }

        User newUser = new User(
                request.firstName(),
                request.firstName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );

        userRepository.save(newUser);

        String name = newUser.getFirstName() + " " + newUser.getLastName();

        return new UserRegisteredResponse(newUser.getPublicId(), name, newUser.getEmail(), newUser.getRole());
    }

    public AuthUserResponse login(AuthUserRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.email(), request.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        JwtToken accessToken = jwtUtil.generateAccessToken(user);
        JwtToken refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenService.registerRefreshToken(refreshToken, user);

        return new AuthUserResponse(
                accessToken.token(),
                refreshToken.token(),
                accessToken.subject(),
                accessToken.issueAt(),
                accessToken.expiresAt()
        );
    }

    @Transactional
    public AuthUserResponse refreshToken(String authorizationHeader) {
        String oldRefreshToken = parseToken(authorizationHeader);

        String username = jwtUtil.extractUsername(oldRefreshToken);

        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + username));

        JwtToken accessToken = jwtUtil.generateAccessToken(user);
        JwtToken refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenService.rotate(oldRefreshToken, refreshToken, user);

        return new AuthUserResponse(
                accessToken.token(),
                refreshToken.token(),
                accessToken.subject(),
                accessToken.issueAt(),
                accessToken.expiresAt()
        );
    }

    public void logout(String authorizationHeader) {
        String refreshToken = parseToken(authorizationHeader);

        refreshTokenService.revoke(refreshToken);
    }

    public void logoutAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        refreshTokenService.revokeAll(user);
    }

    private String parseToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new MissingJwtTokenException("Authorization header is missing or does not use the Bearer scheme");
        }

        return authorizationHeader.substring(7);
    }
}
