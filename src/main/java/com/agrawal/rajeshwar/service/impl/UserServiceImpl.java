package com.agrawal.rajeshwar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.UserService;
import com.agrawal.rajeshwar.utils.EmailUtils;

import lombok.NonNull;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @NonNull
    public User validateEmailAndReturnUser(String email) throws InvalidUserException {
	EmailUtils.validateEmailOrThrowException(email);

	final String sanitizedMail = EmailUtils.sanitizeEmail(email);
	User userDto = this.userRepository.findFirstByEmail(sanitizedMail);
	if (userDto == null) {
	    throw new InvalidUserException("No user is registered for email " + email);
	}
	return userDto;
    }

}
