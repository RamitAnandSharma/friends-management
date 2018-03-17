package com.agrawal.rajeshwar.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.ReceipentsResponseEntity;
import com.agrawal.rajeshwar.api.model.SubscriptionRequestEntity;
import com.agrawal.rajeshwar.api.model.UpdatesRequestEntity;
import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;
import com.agrawal.rajeshwar.service.SubscriptionService;
import com.agrawal.rajeshwar.utils.EmailUtils;
import com.google.common.collect.Sets;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public GeneralResponseEntity addFollow(SubscriptionRequestEntity addFollowerEntity) {
	GeneralResponseEntity validationResult = this.validateSubscriptionEntity(addFollowerEntity);
	if (validationResult != null) {
	    return validationResult;
	}

	String requestorMail = EmailUtils.sanitizeEmail(addFollowerEntity.getRequestor());
	String targetMail = EmailUtils.sanitizeEmail(addFollowerEntity.getTarget());

	// validate same eamil
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

	// System.out.println("requestorDto before \n" + requestorDto);
	// System.out.println("targetDto before \n" + targetDto);

	// add the requester in the followed by list of target
	requestorDto.followAnotherUser(targetDto);
	this.userRepository.save(requestorDto);

	// System.out.println("requestorDto after \n" +
	// this.userRepository.findFirstByEmail(requestorMail));
	// System.out.println("targetDto after \n" +
	// this.userRepository.findFirstByEmail(targetMail));

	return GeneralResponseEntity.builder().success(true).build();

    }

    private GeneralResponseEntity validateSubscriptionEntity(SubscriptionRequestEntity addFollowerEntity) {
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
    public GeneralResponseEntity blockUpdates(SubscriptionRequestEntity addFollowerEntity) {
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

	// System.out.println("requestorDto before \n" + requestorDto);
	// System.out.println("targetDto before \n" + targetDto);

	requestorDto.hasBlockedTheUser(targetDto);
	this.userRepository.save(requestorDto);

	// System.out.println("requestorDto after \n" +
	// this.userRepository.findFirstByEmail(requestorMail));
	// System.out.println("targetDto after \n" +
	// this.userRepository.findFirstByEmail(targetMail));

	return GeneralResponseEntity.builder().success(true).build();

    }

    @Override
    public ReceipentsResponseEntity getAllUpdateReceipents(UpdatesRequestEntity updatesRequestEntity) {
	if (updatesRequestEntity == null) {
	    return ReceipentsResponseEntity.createErrorResponseEntity("Invalid parameters");
	}
	try {
	    EmailUtils.validateEmailOrThrowException(updatesRequestEntity.getSender());
	} catch (InvalidUserException e) {
	    return ReceipentsResponseEntity.createErrorResponseEntity(
		    "Invalid email sender user" + updatesRequestEntity.getSender());
	}

	String senderMail = EmailUtils.sanitizeEmail(updatesRequestEntity.getSender());
	User senderDto = this.userRepository.findFirstByEmail(senderMail);
	if (senderDto == null) {
	    return ReceipentsResponseEntity.createErrorResponseEntity("No user exists for email " + senderMail);
	}

	Set<User> receipents = Sets.newHashSet();
	// all friends of sender
	receipents.addAll(Optional.ofNullable(senderDto.getFriends()).orElse(Sets.newHashSet()));

	// all those who follow the sender
	receipents.addAll(Optional.ofNullable(senderDto.getIsFollowedByUsers()).orElse(Sets.newHashSet()));
	Set<String> receipentsInText = Sets.newHashSet();
	if (StringUtils.isNotEmpty(updatesRequestEntity.getText())) {
	    Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
			       .matcher(updatesRequestEntity.getText());
	    while (m.find()) {
		receipentsInText.add(m.group());
	    }
	}
	// System.out.println(receipentsInText);
	// find all users in text
	List<User> usersInText = this.userRepository.findAllByEmailIn(receipentsInText);
	// System.out.println("users in text are " + usersInText);

	// add all in text
	receipents.addAll(usersInText);

	// remove all users who have blocked the sender
	receipents.removeAll(Optional.ofNullable(senderDto.getIsBlockedByUsers()).orElse(Sets.newHashSet()));

	return ReceipentsResponseEntity.builder()
				       .success(true)
				       .recipients(receipents.stream().map(User::getEmail).collect(Collectors.toList()))
				       .build();

    }

}
