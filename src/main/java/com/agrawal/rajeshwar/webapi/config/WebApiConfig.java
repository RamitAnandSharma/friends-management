package com.agrawal.rajeshwar.webapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.agrawal.rajeshwar.service.config.ServiceConfig;

@ComponentScan({ "com.rajeshwar.agrawal.webapi.controller" })
@Import(value = { SwaggerConfig.class, ServiceConfig.class })
@Configuration
public class WebApiConfig {

}