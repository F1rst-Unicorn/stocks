package de.njsm.stocks.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ConfigurationPropertiesScan("de.njsm.stocks.server.util")
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
