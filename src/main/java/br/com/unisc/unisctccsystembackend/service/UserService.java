package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.DTO.UserResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import br.com.unisc.unisctccsystembackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDTO> getUsers(String role) {
        List<User> users;

        if(role != null && !role.isEmpty()){
            users = userRepository.findAllByRole(UserRole.valueOf(role));
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(this::toDTO).toList();
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return toDTO(user);
    }

    private UserResponseDTO toDTO(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }


}
