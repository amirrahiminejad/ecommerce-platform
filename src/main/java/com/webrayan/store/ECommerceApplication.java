package com.webrayan.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommerceApplication {

	public static void main(String[] args) {
//		String userHome = System.getProperty("user.home");
//		System.setProperty("spring.config.location", "file:" + userHome + "/.store/application.properties");
		SpringApplication.run(ECommerceApplication.class, args);

	}

}
