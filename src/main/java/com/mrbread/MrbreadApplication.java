package com.mrbread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MrbreadApplication {

	public static void main(String[] args) {
		SpringApplication.run(MrbreadApplication.class, args);
	}

}
