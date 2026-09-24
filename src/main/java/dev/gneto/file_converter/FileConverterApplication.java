package dev.gneto.file_converter;

import dev.gneto.file_converter.services.ConversionJobService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FileConverterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileConverterApplication.class, args);
	}

	@Bean
    CommandLineRunner init(ConversionJobService conversionJobService) {
		return (args) -> {
			conversionJobService.initStorage();
		};
	}
}
