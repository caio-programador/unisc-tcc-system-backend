package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
    Page<User> findAllByRoleAndNameContainingIgnoreCase(UserRole role, String name, Pageable pageable);
    Page<User> findAllByRole(UserRole role, Pageable pageable);
    Page<User> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
