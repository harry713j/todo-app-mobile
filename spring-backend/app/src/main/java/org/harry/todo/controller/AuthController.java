package org.harry.todo.controller;

import lombok.AllArgsConstructor;
import org.harry.todo.dto.request.AuthRequestDTO;

import org.harry.todo.dto.request.RefreshTokenRequestDTO;
import org.harry.todo.dto.request.UserDTO;
import org.harry.todo.dto.response.JwtResponseDTO;
import org.harry.todo.entities.RefreshToken;
import org.harry.todo.service.JwtService;
import org.harry.todo.service.RefreshTokenService;
import org.harry.todo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO){
        try {
            Boolean isSignedUp = userDetailsService.signupUser(userDTO);

            if (Boolean.FALSE.equals(isSignedUp)) {
                return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDTO.getUsername());
            String jwtToken = jwtService.generateToken(userDTO.getUsername());

            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .build(), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>("Failed to sign up the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequestDTO){
        try{
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(),
                            authRequestDTO.getPassword()));

            if (authentication.isAuthenticated()){
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO
                        .getUsername());

                return new ResponseEntity<>(JwtResponseDTO.builder()
                        .accessToken(jwtService.generateToken(authRequestDTO.getUsername()))
                        .refreshToken(refreshToken.getToken())
                        .build(),
                        HttpStatus.OK
                );

            } else {
                return new ResponseEntity<>("Bad credentials", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to log in the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        try{
            refreshTokenService.deleteRefreshToken(refreshTokenRequestDTO.getRefreshToken());
            return new ResponseEntity<>("Log out successful", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to log out the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
