package org.harry.todo.service;

import org.harry.todo.entities.RefreshToken;
import org.harry.todo.entities.User;
import org.harry.todo.repository.RefreshTokenRepository;
import org.harry.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(String username) {
        User userExtracted = userRepository.findByUsername(username)
                .orElse(null);


        if (userExtracted == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUser(userExtracted);

        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();

            if (refreshToken.getExpiryDate().isAfter(Instant.now())) {
                return refreshToken;
            } else {
                refreshTokenRepository.delete(refreshToken);
            }
        }


        RefreshToken refreshToken = RefreshToken.builder()
                .user(userExtracted)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1500000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token is expired, Please login again!");
        }

        return token;
    }

    public void deleteRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = getRefreshToken(token);

        refreshTokenOpt.ifPresentOrElse(
                refreshToken -> refreshTokenRepository.delete(refreshToken),
                () -> {
                    throw new RuntimeException("Invalid or Expired refresh token");
                }
        );

    }

    public void deleteRefreshTokenByUser(User user){
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user);

        if (refreshTokenOptional.isEmpty()){
            throw new RuntimeException("Failed to delete refresh token associated with user");
        }

        refreshTokenRepository.delete(refreshTokenOptional.get());
    }
}
