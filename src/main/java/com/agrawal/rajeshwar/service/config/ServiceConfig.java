package com.agrawal.rajeshwar.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({ "com.worksap.company.hue.ec.webapi.order.controller",
	"com.worksap.company.hue.ec.webapi.order.exceptionhandler" })
@Configuration
public class ServiceConfig {

}