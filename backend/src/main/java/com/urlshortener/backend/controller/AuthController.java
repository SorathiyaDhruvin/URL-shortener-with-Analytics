package com.urlshortener.backend.controller;
import com.urlshortener.backend.dto.ApiResponse;
import com.urlshortener.backend.dto.AuthDto;
import com.urlshortener.backend.security.UserDetailsImpl;
import com.urlshortener.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/register")
    public ApiResponse<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "Registration successful");
    }
    
    @PostMapping("/login")
    public ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ApiResponse.success(authService.login(request), "Login successful");
    }
    
    @GetMapping("/me")
    public ApiResponse<AuthDto.UserDto> me(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        AuthDto.UserDto user = new AuthDto.UserDto();
        user.setId(userDetails.getId());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getAuthorities().iterator().next().getAuthority());
        return ApiResponse.success(user, "User profile fetched");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null, "Logout successful");
    }
}
