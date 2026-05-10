package com.tas.condopilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CondopilotApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CondopilotApiApplication.class, args);
	}

}
