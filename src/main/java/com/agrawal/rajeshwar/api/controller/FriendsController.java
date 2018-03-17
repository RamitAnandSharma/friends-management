package com.agrawal.rajeshwar.api.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
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
public class FriendsController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.CONNECT_FRIENDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Connect 2 friends to make them friends")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = AddFriendsResponseEntity.class) })
    public AddFriendsResponseEntity makeFriends(@RequestBody(required = true) @NonNull FriendsEntity friendsEntity)
	    throws InvalidUserException {

	return this.friendsService.addFriends(friendsEntity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.FRIENDS_OF, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get friends of a user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = FriendsListResponseEntity.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = FriendsListResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = FriendsListResponseEntity.class) })
    public FriendsListResponseEntity getFriendsOf(@RequestBody(required = true) @NonNull String email)
	    throws InvalidUserException {

	return this.friendsService.getFriendsList(email);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.COMMON_FRIENDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Connect 2 friends to make them friends")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 422, message = "Invalid request parameters", response = AddFriendsResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = AddFriendsResponseEntity.class) })
    public FriendsListResponseEntity commonFriends(@RequestBody(required = true) @NonNull FriendsEntity friendsEntity)
	    throws InvalidUserException {

	return this.friendsService.getCommonFriends(friendsEntity);

    }
}