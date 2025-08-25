package com.therapy.nest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TherapyNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TherapyNestApplication.class, args);
	}

}
