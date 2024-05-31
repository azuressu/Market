package com.spartamarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpartamarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartamarketApplication.class, args);
	}

}
