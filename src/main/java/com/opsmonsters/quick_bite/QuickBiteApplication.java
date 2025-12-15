package com.opsmonsters.quick_bite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.opsmonsters.quick_bite")
public class QuickBiteApplication {
	public static void main(String[] args) {
		SpringApplication.run(QuickBiteApplication.class, args);
	}
}
