package com.therapy.nest;

import org.springframework.boot.SpringApplication;

public class TestTherapyNestApplication {

	public static void main(String[] args) {
		SpringApplication.from(TestTherapyNestApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
