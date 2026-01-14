package com.healthcare.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IaServiceApplication.class, args);
	}

}
