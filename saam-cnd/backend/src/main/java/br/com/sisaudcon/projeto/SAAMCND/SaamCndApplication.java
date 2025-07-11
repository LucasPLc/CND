package br.com.sisaudcon.projeto.SAAMCND;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "SAAM-CND API", version = "1.0", description = "API para Gerenciamento de Certidões Negativas de Débitos"))
public class SaamCndApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaamCndApplication.class, args);
	}

}
