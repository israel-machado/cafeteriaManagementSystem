package com.project.cafeteriaManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.project.cafeteriaManagementSystem.repository")
public class CafeteriaManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafeteriaManagementSystemApplication.class, args);
	}

}
