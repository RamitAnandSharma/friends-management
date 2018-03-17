package com.agrawal.rajeshwar.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListRequestEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FriendsService;
import com.agrawal.rajeshwar.service.UserService;
import com.agrawal.rajeshwar.utils.EmailUtils;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User saveIfNotExist(String email) throws InvalidUserException {

	EmailUtils.validateEmailOrThrowException(email);

	final String sanitizeEmail = EmailUtils.sanitizeEmail(email);
	User existingUser = this.userRepository.findFirstByEmail(sanitizeEmail);
	if (existingUser == null) {
	    return this.userRepository.save(User.builder().email(sanitizeEmail).build());
	} else {
	    return existingUser;
	}

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public GeneralResponseEntity addFriends(FriendsEntity friendsEntity) {

	if (friendsEntity == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("Invalid parameters");
	}

	if (CollectionUtils.isEmpty(friendsEntity.getFriends())) {
	    return GeneralResponseEntity.createErrorResponseEntity("Friend list cannot be empty");
	}
	if (friendsEntity.getFriends().size() != 2) {
	    return GeneralResponseEntity.createErrorResponseEntity("Please provide only 2 emails to make them friends");
	}

	String email1 = EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(0));
	String email2 = EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(1));

	if (email1.equals(email2)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot make friends, if users are same");
	}
	User user1Dto = null;
	User user2Dto = null;
	// create and save them if they dont exist
	try {
	    user1Dto = this.saveIfNotExist(email1);
	    user2Dto = this.saveIfNotExist(email2);
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return GeneralResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	// System.out.println("user 1 before \n" + user1Dto);
	// System.out.println("user 2 before \n" + user2Dto);

	// if they are already friends, dont make them friends again
	if (Optional.ofNullable(user1Dto.getAllFriends()).orElse(Sets.newHashSet()).contains(user2Dto)) {
	    return GeneralResponseEntity.createErrorResponseEntity(
		    "Cannot add them as friends as they are already friends");
	}

	// if they block each other, dont make them friends
	if (Optional.ofNullable(user1Dto.getHasBlockedUsers()).orElse(Sets.newHashSet()).contains(user2Dto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add them as friends as "
		    + user1Dto.getEmail() + " has blocked user " + user2Dto.getEmail());
	}

	if (Optional.ofNullable(user2Dto.getHasBlockedUsers()).orElse(Sets.newHashSet()).contains(user1Dto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add them as friends as "
		    + user2Dto.getEmail() + " has blocked user " + user1Dto.getEmail());
	}

	// make them mutual friends
	user1Dto.addFriend(user2Dto);
	this.userRepository.save(user1Dto);

	// user2Dto.addFriend(user1Dto);
	// this.userRepository.save(user2Dto);

	// System.out.println("user 1 after\n " + user1Dto);
	// System.out.println("user 2 after\n" + user2Dto);

	return GeneralResponseEntity.builder().success(true).build();

    }

    @Override
    public FriendsListResponseEntity getFriendsList(FriendsListRequestEntity entity) {

	if (entity == null) {
	    return FriendsListResponseEntity.createErrorResponseEntity("Invalid request");
	}

	User userDto;

	try {
	    userDto = this.userService.validateEmailAndReturnUser(entity.getEmail());
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return FriendsListResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	List<String> friendList = userDto.getAllFriends()
					 .stream()
					 .map(User::getEmail)
					 .filter(str -> StringUtils.isNotEmpty(str))
					 .collect(Collectors.toList());

	return FriendsListResponseEntity.builder()
					.success(true)
					.friends(friendList)
					.count(Long.valueOf(friendList.size()))
					.build();

    }

    @Override
    public FriendsListResponseEntity getCommonFriends(FriendsEntity friendsEntity) {

	if (friendsEntity == null) {
	    return FriendsListResponseEntity.createErrorResponseEntity("Invalid parameter");
	}

	if (CollectionUtils.isEmpty(friendsEntity.getFriends())) {
	    return FriendsListResponseEntity.createErrorResponseEntity("Friend list cannot be empty");
	}
	if (friendsEntity.getFriends().size() != 2) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "Please provide only 2 emails to get mutual friends");
	}

	User user1Dto, user2Dto = null;
	try {
	    user1Dto = this.userService.validateEmailAndReturnUser(friendsEntity.getFriends().get(0));
	    user2Dto = this.userService.validateEmailAndReturnUser(friendsEntity.getFriends().get(1));
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return FriendsListResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	if (user1Dto.equals(user2Dto)) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "Cannot find common friends if both users are same");
	}

	Set<User> friends = user1Dto.getAllFriends();

	friends.retainAll(Optional.ofNullable(user2Dto.getAllFriends()).orElse(Sets.newHashSet()));

	return FriendsListResponseEntity.builder()
					.success(true)
					.friends(friends.stream().map(User::getEmail).collect(Collectors.toList()))
					.count(Long.valueOf(friends.size()))
					.build();

    }

}
