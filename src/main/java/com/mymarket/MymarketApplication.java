package com.mymarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MymarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MymarketApplication.class, args);
	}

}
