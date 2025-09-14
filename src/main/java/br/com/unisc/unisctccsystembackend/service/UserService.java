package br.com.unisc.unisctccsystembackend.service;

import br.com.unisc.unisctccsystembackend.entities.DTO.RegisterDTO;
import br.com.unisc.unisctccsystembackend.entities.DTO.UserResponseDTO;
import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import br.com.unisc.unisctccsystembackend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Page<UserResponseDTO> getUsers(String role, int page, int size, String name) {
        Page<User> users;
        Pageable pageable = PageRequest.of(page, size);

        if(name != null && !name.isEmpty() && (role != null && !role.isEmpty())){
            users = userRepository.findAllByRoleAndNameContainingIgnoreCase(UserRole.valueOf(role), name, pageable);
        }else if (name != null && !name.isEmpty()) {
            users = userRepository.findAllByNameContainingIgnoreCase(name, pageable);
        }else if (role != null && !role.isEmpty()) {
            users = userRepository.findAllByRole(UserRole.valueOf(role), pageable);
        }else {
            users = userRepository.findAll(pageable);
        }
        List<UserResponseDTO> formattedUsersList = users.stream().map(this::toDTO).toList();
        return new PageImpl<UserResponseDTO>(formattedUsersList, pageable, users.getTotalElements());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return toDTO(user);
    }

    public User updateUserById(Long id, RegisterDTO registerDTO, User currentUser) throws Exception {
        if (!currentUser.getRole().equals(UserRole.COORDENADOR) && !currentUser.getId().equals(id)) {
            throw new BadRequestException("You do not have permission to update this user");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(registerDTO.name() != null && !registerDTO.name().isEmpty()){
            user.setName(registerDTO.name());
        }
        if(registerDTO.email() != null && !registerDTO.email().isEmpty()){
            user.setEmail(registerDTO.email());
        }
        if(registerDTO.password() != null && !registerDTO.password().isEmpty()){
            String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
            user.setPassword(encryptedPassword);
        }
        if(registerDTO.role() != null){
            user.setRole(registerDTO.role());
        }

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);
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
