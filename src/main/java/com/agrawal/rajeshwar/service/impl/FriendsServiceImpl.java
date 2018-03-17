package com.agrawal.rajeshwar.service.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.dao.FriendsRepository;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.Friends;
import com.agrawal.rajeshwar.dto.User;
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

    @Override
    public int saveUser(UserEntity userEntity) {
	if (userEntity != null && StringUtils.isNotEmpty(userEntity.getEmail())
		&& EmailValidator.isValidMail(userEntity.getEmail()) && !this.isDuplicateMail(userEntity.getEmail())) {
	    return this.userRepository.save(User.builder().email(userEntity.getEmail()).build()).getId();
	}
	return 0;
    }

    @Override
    public boolean addFriends(FriendsEntity friendsEntity) {
	UserEntity user1 = UserEntity.builder().email(friendsEntity.getFriends().get(0)).build();
	UserEntity user2 = UserEntity.builder().email(friendsEntity.getFriends().get(1)).build();
	this.saveUser(user1);
	this.saveUser(user2);
	User user1Dto = this.userRepository.findFirstByEmail(user1.getEmail());
	User user2Dto = this.userRepository.findFirstByEmail(user2.getEmail());

	Friends user1Friends = Friends.builder().user(user1Dto).friendId(user2Dto.getId()).build();
	Optional.ofNullable(user1Dto.getFriends()).orElse(Sets.newHashSet()).add(user1Friends);

	this.saveUser(user1);
	this.friendsRepository.save(user1Friends);

	Friends user2Friends = Friends.builder().user(user2Dto).friendId(user1Dto.getId()).build();
	Optional.ofNullable(user2Dto.getFriends()).orElse(Sets.newHashSet()).add(user2Friends);

	this.saveUser(user2);
	this.friendsRepository.save(user2Friends);

	return true;

    }

    private boolean isDuplicateMail(String email) {
	if (this.userRepository.findFirstByEmail(email) == null) {
	    return false;
	}
	return true;
    }

}
