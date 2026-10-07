package com.urlshortener.backend.repository;
import com.urlshortener.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<String> findEmailById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
