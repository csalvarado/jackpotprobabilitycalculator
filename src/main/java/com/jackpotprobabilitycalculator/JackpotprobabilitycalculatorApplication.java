package com.jackpotprobabilitycalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class JackpotprobabilitycalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(JackpotprobabilitycalculatorApplication.class, args);
	}

}
