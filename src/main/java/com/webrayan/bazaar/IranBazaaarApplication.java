package com.webrayan.bazaar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IranBazaaarApplication {

	public static void main(String[] args) {
//		String userHome = System.getProperty("user.home");
//		System.setProperty("spring.config.location", "file:" + userHome + "/.bazaar/application.properties");
		SpringApplication.run(IranBazaaarApplication.class, args);

	}

}
