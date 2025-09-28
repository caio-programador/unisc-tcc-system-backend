package br.com.unisc.unisctccsystembackend.repositories;

import br.com.unisc.unisctccsystembackend.entities.User;
import br.com.unisc.unisctccsystembackend.entities.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.userdetails.UserDetails;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
    Page<User> findAllByRoleAndNameContainingIgnoreCase(UserRole role, String name, Pageable pageable);
    Page<User> findAllByRole(UserRole role, Pageable pageable);
    Page<User> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM users u WHERE u.id = :id" )
    void deleteByIdDirectly(@Param("id") Long id);
}
