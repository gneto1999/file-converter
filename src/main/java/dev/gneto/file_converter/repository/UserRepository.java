package dev.gneto.file_converter.repository;


import dev.gneto.file_converter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findByPublicId(UUID publicId);
}
