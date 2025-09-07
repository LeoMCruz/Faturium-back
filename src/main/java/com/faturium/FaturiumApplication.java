package com.faturium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaturiumApplication {

	public static void main(String[] args) {
		SpringApplication.run(FaturiumApplication.class, args);
	}

}
