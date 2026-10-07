package com.urlshortener.backend.service;
import com.urlshortener.backend.dto.AuthDto;
import com.urlshortener.backend.entity.Role;
import com.urlshortener.backend.entity.User;
import com.urlshortener.backend.exception.CustomException;
import com.urlshortener.backend.repository.UserRepository;
import com.urlshortener.backend.security.JwtService;
import com.urlshortener.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already exists", "EMAIL_EXISTS");
        }
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .role(Role.ROLE_USER)
            .build();
        userRepository.save(user);
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token = jwtService.generateToken(userDetails);
        
        AuthDto.UserDto userDto = new AuthDto.UserDto();
        userDto.setId(user.getId()); userDto.setName(user.getName()); userDto.setEmail(user.getEmail()); userDto.setRole(user.getRole().name());
        
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token); response.setUser(userDto);
        return response;
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token = jwtService.generateToken(userDetails);
        
        AuthDto.UserDto userDto = new AuthDto.UserDto();
        userDto.setId(user.getId()); userDto.setName(user.getName()); userDto.setEmail(user.getEmail()); userDto.setRole(user.getRole().name());
        
        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token); response.setUser(userDto);
        return response;
    }
}
