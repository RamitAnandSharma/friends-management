package com.agrawal.rajeshwar.webapi.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrawal.rajeshwar.webapi.entity.Greeting;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = { "greetings" })
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(method = RequestMethod.GET, value = "/greeting", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Give greetings")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Greeting.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = Greeting.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = Greeting.class) })
    public Greeting greeting(
	    @RequestParam(value = "name", defaultValue = "World", required = true) @NonNull String name) {
	System.out.println("hi");
	GreetingController.log.debug("asdf");
	return new Greeting(this.counter.incrementAndGet(), String.format(GreetingController.template, name));
    }
}