package com.agrawal.rajeshwar.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListRequestEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.ReceipentsResponseEntity;
import com.agrawal.rajeshwar.api.model.SubscriptionRequestEntity;
import com.agrawal.rajeshwar.api.model.UpdatesRequestEntity;
import com.agrawal.rajeshwar.service.FriendsService;
import com.agrawal.rajeshwar.service.SubscriptionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;

@RestController
@Api(tags = { "Friends Management" })
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private SubscriptionService followerService;

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.CONNECT_FRIENDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Adds and create 2 users as friends")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = GeneralResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = GeneralResponseEntity.class) })
    public GeneralResponseEntity makeFriends(@RequestBody(required = true) @NonNull FriendsEntity friendsEntity) {

	return this.friendsService.addFriends(friendsEntity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.FRIENDS_OF, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get all friends of a user, included the ones who are blocked for updates")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = FriendsListResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = FriendsListResponseEntity.class) })
    public FriendsListResponseEntity getFriendsOf(
	    @RequestBody(required = true) @NonNull FriendsListRequestEntity entity) {

	return this.friendsService.getFriendsList(entity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.COMMON_FRIENDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Finds common friends between 2 friends")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = FriendsListResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = FriendsListResponseEntity.class) })
    public FriendsListResponseEntity commonFriends(@RequestBody(required = true) @NonNull FriendsEntity friendsEntity) {

	return this.friendsService.getCommonFriends(friendsEntity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.ADD_FOLLOW, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Subscribes the requestor user to receive updates from targe user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = GeneralResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = GeneralResponseEntity.class) })
    public GeneralResponseEntity addFollow(
	    @RequestBody(required = true) @NonNull SubscriptionRequestEntity addFollowerEntity) {

	return this.followerService.addFollow(addFollowerEntity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.BLOCK_UPDATES, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Blocks receiving updates for a requestor user from a target user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = GeneralResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = GeneralResponseEntity.class) })
    public GeneralResponseEntity blockUpdates(
	    @RequestBody(required = true) @NonNull SubscriptionRequestEntity addFollowerEntity) {

	return this.followerService.blockUpdates(addFollowerEntity);

    }

    @RequestMapping(method = RequestMethod.POST, value = ApiEndPoints.ALL_UPDATE_RECEIPENTS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get a list of receipents who would receive updates from a user")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = ReceipentsResponseEntity.class),
	    @ApiResponse(code = 500, message = "Internal Server Error", response = ReceipentsResponseEntity.class) })
    public ReceipentsResponseEntity getAllUpdateReceipents(
	    @RequestBody(required = true) @NonNull UpdatesRequestEntity updatesRequestEntity) {

	return this.followerService.getAllUpdateReceipents(updatesRequestEntity);

    }
}