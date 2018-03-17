package com.agrawal.rajeshwar.service.impl;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.dao.FriendsRepository;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.Friends;
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

	if (StringUtils.isEmpty(userEntity.getEmail())) {
	    throw new InvalidUserException("User Email cannot be null");
	}
	if (!EmailValidator.isValidMail(userEntity.getEmail())) {
	    throw new InvalidUserException("Invalid Email " + userEntity.getEmail());
	}
	User existingUser = this.userRepository.findFirstByEmail(userEntity.getEmail());
	if (existingUser == null) {
	    return this.userRepository.save(User.builder().email(userEntity.getEmail()).build());
	} else {
	    return existingUser;
	}

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public AddFriendsResponseEntity addFriends(FriendsEntity friendsEntity) {
	UserEntity user1 = UserEntity.builder().email(friendsEntity.getFriends().get(0)).build();
	UserEntity user2 = UserEntity.builder().email(friendsEntity.getFriends().get(1)).build();
	User user1Dto = null;
	User user2Dto = null;
	try {
	    user1Dto = this.saveIfNotExist(user1);
	    user2Dto = this.saveIfNotExist(user2);
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return AddFriendsResponseEntity.builder().success(false).errorMessage(e.getMessage()).build();
	}

	System.out.println(user1Dto);
	System.out.println(user2Dto);

	Friends user1Friends = Friends.builder().user(user1Dto).friendId(user2Dto.getId()).build();
	Set<Friends> friendsOfUser1 = Optional.ofNullable(user1Dto.getFriends()).orElse(Sets.newHashSet());
	if (friendsOfUser1.contains(user1Friends)) {
	    return AddFriendsResponseEntity.builder()
					   .success(false)
					   .errorMessage("Users " + user1Dto.getEmail() + " and " + user2Dto.getEmail()
						   + " are already friends.")
					   .build();
	}
	friendsOfUser1.add(user1Friends);
	user1Dto.setFriends(friendsOfUser1);
	System.out.println("start");
	this.userRepository.save(user1Dto);

	Friends user2Friends = Friends.builder().user(user2Dto).friendId(user1Dto.getId()).build();
	Set<Friends> friendsOfUser2 = Optional.ofNullable(user2Dto.getFriends()).orElse(Sets.newHashSet());
	friendsOfUser2.add(user2Friends);
	user2Dto.setFriends(friendsOfUser2);

	this.userRepository.save(user2Dto);
	System.out.println("end");

	System.out.println(user1Dto);
	System.out.println(user2Dto);

	return AddFriendsResponseEntity.builder().success(true).build();

    }

}
