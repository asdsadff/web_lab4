package com.partsey.ssl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SslApplication {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.home"));
		SpringApplication.run(SslApplication.class, args);
	}

}
