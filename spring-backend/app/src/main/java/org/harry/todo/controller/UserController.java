package org.harry.todo.controller;


import lombok.AllArgsConstructor;
import org.harry.todo.dto.request.UserDTO;
import org.harry.todo.dto.response.UserResponseDTO;
import org.harry.todo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username){
        try {
            UserResponseDTO userResponseDTO = userDetailsService.getUser(username);
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDTO userDTO){
        try {
            UserResponseDTO userResponseDTO = userDetailsService.updateUser(username, userDTO);
            return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update the User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username){
        try{
            userDetailsService.deleteUser(username);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("Failed to delete the User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
