package com.agrawal.rajeshwar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.agrawal.rajeshwar.webapi.config.WebApiConfig;

@SpringBootApplication
@Import(value = { WebApiConfig.class })
public class Application {

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }
}
