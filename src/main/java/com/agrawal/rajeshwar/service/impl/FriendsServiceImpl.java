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
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FriendsService;
import com.agrawal.rajeshwar.utils.EmailUtils;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private UserRepository userRepository;

    private User saveIfNotExist(UserEntity userEntity) throws InvalidUserException {

	if (userEntity == null) {
	    throw new InvalidUserException("User cannot be null");
	}
	String sanitizeEmail = EmailUtils.sanitizeEmail(userEntity.getEmail());
	EmailUtils.validateEmailOrThrowException(sanitizeEmail);
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

	if (CollectionUtils.isEmpty(friendsEntity.getFriends())) {
	    return GeneralResponseEntity.createErrorResponseEntity("Friend list cannot be empty");
	}
	if (friendsEntity.getFriends().size() != 2) {
	    return GeneralResponseEntity.createErrorResponseEntity("Please provide only 2 emails to make them friends");
	}

	UserEntity user1 = UserEntity.builder()
				     .email(EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(0)))
				     .build();
	UserEntity user2 = UserEntity.builder()
				     .email(EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(1)))
				     .build();
	if (user1.getEmail().equals(user2.getEmail())) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot make friends, if users are same");
	}
	User user1Dto = null;
	User user2Dto = null;
	try {
	    user1Dto = this.saveIfNotExist(user1);
	    user2Dto = this.saveIfNotExist(user2);
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return GeneralResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	System.out.println("user 1 before \n" + user1Dto);
	System.out.println("user 2 before \n" + user2Dto);

	if (Optional.ofNullable(user1Dto.getHasBlockedUsers()).orElse(Sets.newHashSet()).contains(user2Dto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add them as friends as "
		    + user1Dto.getEmail() + " has blocked user " + user2Dto.getEmail());
	}

	if (Optional.ofNullable(user2Dto.getHasBlockedUsers()).orElse(Sets.newHashSet()).contains(user1Dto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add them as friends as "
		    + user2Dto.getEmail() + " has blocked user " + user1Dto.getEmail());
	}

	user1Dto.addFriend(user2Dto);
	this.userRepository.save(user1Dto);

	user2Dto.addFriend(user1Dto);
	this.userRepository.save(user2Dto);

	System.out.println("user 1 after\n " + user1Dto);
	System.out.println("user 2 after\n" + user2Dto);

	return GeneralResponseEntity.builder().success(true).build();

    }

    @Override
    public FriendsListResponseEntity getFriendsList(String email) {
	try {
	    EmailUtils.validateEmailOrThrowException(email);
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return FriendsListResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	User userDto = this.userRepository.findFirstByEmail(email.trim());
	if (userDto == null) {
	    return FriendsListResponseEntity.createErrorResponseEntity("User is not a registered member");
	}

	List<String> friendList = Optional.ofNullable(userDto.getFriends())
					  .orElse(Sets.newHashSet())
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

	if (CollectionUtils.isEmpty(friendsEntity.getFriends())) {
	    return FriendsListResponseEntity.createErrorResponseEntity("Friend list cannot be empty");
	}
	if (friendsEntity.getFriends().size() != 2) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "Please provide only 2 emails to get mutual friends");
	}

	String email1 = EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(0));
	String email2 = EmailUtils.sanitizeEmail(friendsEntity.getFriends().get(1));
	if (email1.equals(email2)) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "Cannot find mutual friends if both emails are same");
	}

	User user1Dto = this.userRepository.findFirstByEmail(email1);
	if (user1Dto == null) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "User with email " + email1 + " is not a registered member");
	}

	User user2Dto = this.userRepository.findFirstByEmail(email2);
	if (user2Dto == null) {
	    return FriendsListResponseEntity.createErrorResponseEntity(
		    "User with email " + email2 + " is not a registered member");
	}

	// find intersection of 2 friends list, this is wrong way, but due to time
	// constraint, I am doing this method. Ideally, I should write a SQL query that
	// would get this list

	Set<User> friendsUser1 = Optional.ofNullable(user1Dto.getFriends()).orElse(Sets.newHashSet());

	friendsUser1.retainAll(Optional.ofNullable(user2Dto.getFriends()).orElse(Sets.newHashSet()));

	return FriendsListResponseEntity.builder()
					.success(true)
					.friends(friendsUser1.stream().map(User::getEmail).collect(Collectors.toList()))
					.count(Long.valueOf(user1Dto.getFriends().size()))
					.build();

    }

}
