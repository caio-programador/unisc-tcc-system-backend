package br.com.unisc.unisctccsystembackend.controller;

import br.com.unisc.unisctccsystembackend.entities.DTO.LoginResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.RegisterDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.UserResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.security.TokenService;
import br.com.unisc.unisctccsystembackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "") String role, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size, @RequestParam(defaultValue = "") String name) {
        Page<UserResponseDTO> users = userService.getUsers(role, page, size, name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LoginResponseDTO> updateUser(@PathVariable Long id, @RequestBody RegisterDTO registerDTO, Authentication authentication) throws Exception {
        User currentUser = (User) authentication.getPrincipal();
        User user = userService.updateUserById(id, registerDTO, currentUser);
        var token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
