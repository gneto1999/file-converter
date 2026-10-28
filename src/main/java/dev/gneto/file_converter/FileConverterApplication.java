package dev.gneto.file_converter;

import dev.gneto.file_converter.services.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileConverterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileConverterApplication.class, args);
	}

	@Bean
    CommandLineRunner init(FileStorageService fileStorageService) {
		return (args) -> {
			fileStorageService.initStorage();
		};
	}
}
