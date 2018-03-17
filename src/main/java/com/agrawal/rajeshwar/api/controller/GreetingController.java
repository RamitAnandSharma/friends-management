package com.agrawal.rajeshwar.api.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.Greeting;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FriendsService;

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

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(method = RequestMethod.GET, value = "/greeting", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Give greetings")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Greeting.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = Greeting.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = Greeting.class) })
    public Greeting greeting(
	    @RequestParam(value = "name", defaultValue = "World", required = true) @NonNull String name)
	    throws InvalidUserException {

	return new Greeting(this.counter.incrementAndGet(), String.format(GreetingController.template, name));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/friends", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Give greetings")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = AddFriendsResponseEntity.class) })
    public AddFriendsResponseEntity greeting(@RequestBody(required = true) @NonNull FriendsEntity friendsEntity)
	    throws InvalidUserException {

	return this.friendsService.addFriends(friendsEntity);

    }
}