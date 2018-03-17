package com.agrawal.rajeshwar.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.dao.FriendsRepository;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FriendsService;
import com.agrawal.rajeshwar.utils.EmailValidator;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    private User saveIfNotExist(UserEntity userEntity) throws InvalidUserException {

	if (userEntity == null) {
	    throw new InvalidUserException("User cannot be null");
	}
	String sanitizeEmail = FriendsServiceImpl.sanitizeEmail(userEntity.getEmail());
	this.validateEmailOrThrowException(sanitizeEmail);
	User existingUser = this.userRepository.findFirstByEmail(sanitizeEmail);
	if (existingUser == null) {
	    return this.userRepository.save(User.builder().email(sanitizeEmail).build());
	} else {
	    return existingUser;
	}

    }

    private void validateEmailOrThrowException(String email) throws InvalidUserException {
	if (StringUtils.isEmpty(email)) {
	    throw new InvalidUserException("User Email cannot be null");
	}
	if (!EmailValidator.isValidMail(email)) {
	    throw new InvalidUserException("Invalid Email " + email);
	}
    }

    private static String sanitizeEmail(String email) {
	return Optional.ofNullable(email).orElse("").trim().toLowerCase();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public AddFriendsResponseEntity addFriends(FriendsEntity friendsEntity) {

	if (CollectionUtils.isEmpty(friendsEntity.getFriends())) {
	    return AddFriendsResponseEntity.createErrorResponseEntity("Friend list cannot be empty");
	}
	if (friendsEntity.getFriends().size() != 2) {
	    return AddFriendsResponseEntity.createErrorResponseEntity(
		    "Please provide only 2 emails to make them friends");
	}

	UserEntity user1 = UserEntity.builder()
				     .email(FriendsServiceImpl.sanitizeEmail(friendsEntity.getFriends().get(0)))
				     .build();
	UserEntity user2 = UserEntity.builder()
				     .email(FriendsServiceImpl.sanitizeEmail(friendsEntity.getFriends().get(1)))
				     .build();
	if (user1.getEmail().equals(user2.getEmail())) {
	    return AddFriendsResponseEntity.createErrorResponseEntity("Cannot make friends, if users are same");
	}
	User user1Dto = null;
	User user2Dto = null;
	try {
	    user1Dto = this.saveIfNotExist(user1);
	    user2Dto = this.saveIfNotExist(user2);
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return AddFriendsResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	System.out.println("user 1 before \n" + user1Dto);
	System.out.println("user 2 before \n" + user2Dto);

	user1Dto.addFriend(user2Dto);
	this.userRepository.save(user1Dto);

	user2Dto.addFriend(user1Dto);
	this.userRepository.save(user2Dto);

	System.out.println("user 1 after\n " + user1Dto);
	System.out.println("user 2 after\n" + user2Dto);

	return AddFriendsResponseEntity.builder().success(true).build();

    }

    @Override
    public FriendsListResponseEntity getFriendsList(String email) {
	try {
	    this.validateEmailOrThrowException(email);
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

}
