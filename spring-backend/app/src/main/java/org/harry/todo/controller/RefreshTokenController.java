package org.harry.todo.controller;

import lombok.AllArgsConstructor;
import org.harry.todo.dto.request.RefreshTokenRequestDTO;
import org.harry.todo.dto.response.JwtResponseDTO;
import org.harry.todo.entities.RefreshToken;
import org.harry.todo.service.JwtService;
import org.harry.todo.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class RefreshTokenController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    // when the access token will expire this endpoint will be called
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.getRefreshToken(refreshTokenRequestDTO.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getUsername());
                    return ResponseEntity.ok(JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDTO.getRefreshToken())
                            .build()
                    );
                }).orElseThrow(
                        () -> new RuntimeException("Invalid or Expired Refresh Token")
                );
    }
}
