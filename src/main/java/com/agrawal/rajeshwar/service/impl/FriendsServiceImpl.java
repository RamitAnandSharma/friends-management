package com.agrawal.rajeshwar.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FriendsService;
import com.agrawal.rajeshwar.utils.EmailValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public int saveUser(UserEntity userEntity) throws InvalidUserException {
	if (userEntity == null || StringUtils.isEmpty(userEntity.getEmail())) {
	    throw new InvalidUserException();
	} else {
	    if (EmailValidator.validateMail(userEntity.getEmail())) {
		if (!this.isDuplicateMail(userEntity.getEmail())) {
		    return this.userRepository.save(User.builder().email(userEntity.getEmail()).build()).getId();
		} else {
		    log.error("duplicate - " + userEntity.getEmail());
		    throw new InvalidUserException();
		}
	    } else {
		throw new InvalidUserException();
	    }
	}
    }

    private boolean isDuplicateMail(String email) {
	if (CollectionUtils.isEmpty(this.userRepository.findByEmail(email))) {
	    return false;
	}
	return true;
    }

}
