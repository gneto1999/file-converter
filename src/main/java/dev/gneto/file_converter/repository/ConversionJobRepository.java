package dev.gneto.file_converter.repository;

import dev.gneto.file_converter.model.ConversionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversionJobRepository extends JpaRepository<ConversionJob, Long> {
    Optional<ConversionJob> findByPublicId(UUID publicId);
}
