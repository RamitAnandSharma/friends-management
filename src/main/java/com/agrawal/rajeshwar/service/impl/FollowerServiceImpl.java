package com.agrawal.rajeshwar.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.api.model.AddFollowerEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.FollowerService;
import com.agrawal.rajeshwar.utils.EmailValidator;

@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public GeneralResponseEntity addFollow(AddFollowerEntity addFollowerEntity) {
	if (addFollowerEntity == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("Invalid parameters");
	}
	try {
	    EmailValidator.validateEmailOrThrowException(addFollowerEntity.getRequestor());
	} catch (InvalidUserException e) {
	    return GeneralResponseEntity.createErrorResponseEntity(
		    "Invalid email requestor user" + addFollowerEntity.getRequestor());
	}

	try {
	    EmailValidator.validateEmailOrThrowException(addFollowerEntity.getTarget());
	} catch (InvalidUserException e) {
	    return GeneralResponseEntity.createErrorResponseEntity(
		    "Invalid email of target user" + addFollowerEntity.getTarget());
	}

	String requestorMail = EmailValidator.sanitizeEmail(addFollowerEntity.getRequestor());
	String targetMail = EmailValidator.sanitizeEmail(addFollowerEntity.getTarget());

	User requestorDto = this.userRepository.findFirstByEmail(requestorMail);
	User targetDto = this.userRepository.findFirstByEmail(targetMail);

	if (requestorDto == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("No user exists for email " + requestorMail);
	}

	if (targetDto == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("No user exists for email " + targetMail);
	}

	System.out.println("requestorDto before \n" + requestorDto);
	System.out.println("targetDto before \n" + targetDto);

	targetDto.addSubscriber(requestorDto);
	this.userRepository.save(targetDto);

	System.out.println("requestorDto after \n" + requestorDto);
	System.out.println("targetDto after \n" + targetDto);

	return GeneralResponseEntity.builder().success(true).build();

    }

}
