package org.harry.todo.service;

import lombok.Data;
import org.harry.todo.dto.request.UserDTO;
import org.harry.todo.dto.response.UserResponseDTO;
import org.harry.todo.entities.CustomUserDetails;
import org.harry.todo.entities.RefreshToken;
import org.harry.todo.entities.User;
import org.harry.todo.repository.UserRepository;
import org.harry.todo.util.ValidationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;



@Data
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("Could not found the user with username: " + username);
        }

        return new CustomUserDetails(user);
    }

    public User checkIfUserAlreadyExists(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        return optionalUser.orElse(null);
    }

    public Boolean signupUser(UserDTO userDTO) {
        // validate the username, password & email
        if (!ValidationUtil.validateUsername(userDTO.getUsername()) ||
                !ValidationUtil.validatePassword(userDTO.getPassword()) ||
                !ValidationUtil.validateEmail(userDTO.getEmail())) {

            if (!ValidationUtil.validateUsername(userDTO.getUsername())) {
                System.err.println("Username must be at least 4 characters long and contain " +
                        "alphabets & numbers");
            }

            if (!ValidationUtil.validatePassword(userDTO.getPassword())) {
                System.err.println("Password must be 8 characters long and contain alphabets, number & symbols");
            }

            if (!ValidationUtil.validateEmail(userDTO.getEmail())) {
                System.err.println("Please provide a valid email");
            }

            return false;
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .dateOfBirth(userDTO.getDateOfBirth())
                .build();

        if (Objects.nonNull(checkIfUserAlreadyExists(user))) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return true;
    }

    public UserResponseDTO getUser(String username) {
        Optional<User> existedUserOpt = userRepository.findByUsername(username);

        if (existedUserOpt.isPresent()) {
            User user = existedUserOpt.get();
            return UserResponseDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .name(user.getName())
                    .userId(user.getUserId())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
        } else {
            throw new RuntimeException("User not found with the username: " + username);
        }
    }

    public UserResponseDTO updateUser(String username, UserDTO userDTO) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            throw new RuntimeException("User with username " + username + " doesn't exists");
        }

        if (userDTO.getEmail() != null && ValidationUtil.validateEmail(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }

        if (userDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userDTO.getDateOfBirth());
        }

        User updatedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .userId(updatedUser.getUserId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .build();
    }

    public void deleteUser(String username) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(
                        user -> {

                            refreshTokenService.deleteRefreshTokenByUser(user);
                            userRepository.delete(user);

                        },
                        () -> {
                            throw new RuntimeException("User with username " + username + " doesn't exists");
                        }
                );
    }
}
