package com.njpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NgsJobPortalV2Application {

	public static void main(String[] args) {
		SpringApplication.run(NgsJobPortalV2Application.class, args);
	}

}
