package com.lock_practice_code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class LockPracticeCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LockPracticeCodeApplication.class, args);
	}

}
