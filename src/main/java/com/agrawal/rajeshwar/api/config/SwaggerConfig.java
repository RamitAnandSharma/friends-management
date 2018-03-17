package com.agrawal.rajeshwar.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@SwaggerDefinition(info = @Info(description = "Friends Management System", version = "v1", title = "Friends Management System API", contact = @io.swagger.annotations.Contact(name = "Rajeshwar Agrawal", email = "rajeshwaragrawal101@gmail.com")))
public class SwaggerConfig {
    @Bean
    public Docket api() {

	List<ResponseMessage> globalResponses = Lists.newArrayList();
	globalResponses.add(new ResponseMessageBuilder().code(400)
							.message("Request parameter not valid")
							.responseModel(new ModelRef("BadRequestErrorInfo"))
							.build());

	return new Docket(DocumentationType.SWAGGER_2).select()
						      .apis(RequestHandlerSelectors.basePackage(
							      "com.agrawal.rajeshwar.api"))
						      .paths(PathSelectors.any())
						      .build()
						      .useDefaultResponseMessages(false)
						      .apiInfo(this.apiInfo())
						      .globalResponseMessage(RequestMethod.GET, globalResponses);
    }

    private ApiInfo apiInfo() {
	Contact contact = new Contact("Rajeshwar Agrawal", "", "rajeshwaragrawal101@gmail.com");

	return new ApiInfoBuilder().title("Friends Management API")
				   .contact(contact)
				   .description("Provides capabilities to manage a basic social network application")
				   .version("1.0")
				   .build();
    }
}