package com.agrawal.rajeshwar.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.SubscriptionEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.SubscriptionService;
import com.agrawal.rajeshwar.utils.EmailUtils;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public GeneralResponseEntity addFollow(SubscriptionEntity addFollowerEntity) {
	GeneralResponseEntity validationResult = this.validateSubscriptionEntity(addFollowerEntity);
	if (validationResult != null) {
	    return validationResult;
	}

	String requestorMail = EmailUtils.sanitizeEmail(addFollowerEntity.getRequestor());
	String targetMail = EmailUtils.sanitizeEmail(addFollowerEntity.getTarget());

	if (requestorMail.equals(targetMail)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add follower for same user");
	}

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

    private GeneralResponseEntity validateSubscriptionEntity(SubscriptionEntity addFollowerEntity) {
	if (addFollowerEntity == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("Invalid parameters");
	}
	try {
	    EmailUtils.validateEmailOrThrowException(addFollowerEntity.getRequestor());
	} catch (InvalidUserException e) {
	    return GeneralResponseEntity.createErrorResponseEntity(
		    "Invalid email requestor user" + addFollowerEntity.getRequestor());
	}

	try {
	    EmailUtils.validateEmailOrThrowException(addFollowerEntity.getTarget());
	} catch (InvalidUserException e) {
	    return GeneralResponseEntity.createErrorResponseEntity(
		    "Invalid email of target user" + addFollowerEntity.getTarget());
	}
	return null;

    }

    @Override
    @Transactional
    public GeneralResponseEntity blockUpdates(SubscriptionEntity addFollowerEntity) {
	GeneralResponseEntity validationResult = this.validateSubscriptionEntity(addFollowerEntity);
	if (validationResult != null) {
	    return validationResult;
	}
	String requestorMail = EmailUtils.sanitizeEmail(addFollowerEntity.getRequestor());
	String targetMail = EmailUtils.sanitizeEmail(addFollowerEntity.getTarget());
	if (requestorMail.equals(targetMail)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot block the same user");
	}

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

	requestorDto.hasBlockedTheUser(targetDto);
	this.userRepository.save(targetDto);

	System.out.println("requestorDto after \n" + requestorDto);
	targetDto = this.userRepository.findFirstByEmail(targetMail);
	System.out.println("targetDto after \n" + targetDto);

	return GeneralResponseEntity.builder().success(true).build();

    }

}
