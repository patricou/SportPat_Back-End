package com.pat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages={"com.pat"})
public class SportpatApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SportpatApplication.class, args);
	}

    public void run(String... arg0) throws Exception{

	}
}
