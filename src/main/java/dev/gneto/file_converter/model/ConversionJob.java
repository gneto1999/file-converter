package dev.gneto.file_converter.model;

import dev.gneto.file_converter.enums.ConversionStatus;
import dev.gneto.file_converter.enums.FileFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversion_jobs")
public class ConversionJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "public_id", nullable = false, unique = true, updatable = false, insertable = false)
    private UUID publicId;

    @Column(name = "original_filename", nullable = false)
    private String originalFileName;

    @Column(name = "input_format", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileFormat inputFormat;

    @Column(name = "output_format", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileFormat outputFormat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConversionStatus status;

    @Column(name = "input_path", nullable = false)
    private String inputPath;

    @Column(name = "output_path")
    private String outputPath;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public ConversionJob() {
    }

    public ConversionJob(
            String originalFileName,
            FileFormat inputFormat,
            FileFormat outputFormat,
            String inputPath
    ) {
        this.originalFileName = originalFileName;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.status = ConversionStatus.PENDING;
        this.inputPath = inputPath;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public FileFormat getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(FileFormat inputFormat) {
        this.inputFormat = inputFormat;
    }

    public FileFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(FileFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
        this.status = status;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
