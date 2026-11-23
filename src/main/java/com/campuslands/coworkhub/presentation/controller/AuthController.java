package com.campuslands.coworkhub.presentation.controller;

import com.campuslands.coworkhub.business.service.UserAccountService;
import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import com.campuslands.coworkhub.presentation.dto.request.LoginRequest;
import com.campuslands.coworkhub.presentation.dto.response.LoginResponse;
import com.campuslands.coworkhub.presentation.dto.response.ProfileResponse;
import com.campuslands.coworkhub.security.AuthenticatedUser;
import com.campuslands.coworkhub.security.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserAccountService accounts;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UserAccountEntity account = accounts.authenticate(request.username(), request.password())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));
        String token = jwtService.generate(AuthenticatedUser.from(account));
        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    @GetMapping("/perfil")
    public ProfileResponse profile(@AuthenticationPrincipal AuthenticatedUser user) {
        return new ProfileResponse(user.getUsername(), user.getRole().name(), user.getMemberId());
    }
}
