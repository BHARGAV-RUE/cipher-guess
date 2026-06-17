package com.bhargav.crack_the_number;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CrackTheNumberApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrackTheNumberApplication.class, args);
	}
	@Bean
	CommandLineRunner test(Environment env) {
		return args -> {
			System.out.println("DB_URL: " + env.getProperty("DB_URL"));
			System.out.println("Datasource URL: " +
					env.getProperty("spring.datasource.url"));
		};
	}
}
