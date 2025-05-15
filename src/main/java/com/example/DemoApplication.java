package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// Cargar variables del archivo .env
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> {
			// Solo establece la variable si no existe ya en el entorno
			if (System.getenv(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});

		SpringApplication.run(DemoApplication.class, args);
	}

}
