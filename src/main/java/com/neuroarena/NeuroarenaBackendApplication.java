package com.neuroarena;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class NeuroarenaBackendApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("GROQ_API_KEY", dotenv.get("GROQ_API_KEY"));

		SpringApplication.run(NeuroarenaBackendApplication.class, args);
	}

}
