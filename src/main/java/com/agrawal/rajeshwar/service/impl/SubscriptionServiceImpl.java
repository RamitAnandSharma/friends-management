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
import com.agrawal.rajeshwar.service.UserService;
import com.agrawal.rajeshwar.utils.EmailUtils;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public GeneralResponseEntity addFollow(SubscriptionRequestEntity addFollowerEntity) {

	if (addFollowerEntity == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("Invalid parameters");
	}

	User requestorDto, targetDto = null;
	try {
	    requestorDto = this.userService.validateEmailAndReturnUser(addFollowerEntity.getRequestor());
	    targetDto = this.userService.validateEmailAndReturnUser(addFollowerEntity.getTarget());
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return GeneralResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	// System.out.println("requestorDto before \n" + requestorDto);
	// System.out.println("targetDto before \n" + targetDto);

	// validate same eamil
	if (requestorDto.equals(targetDto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot add follower for same user");
	}

	// System.out.println("requestorDto before \n" + requestorDto);
	// System.out.println("targetDto before \n" + targetDto);

	// check if already follower
	if (Optional.ofNullable(requestorDto.getIsFollowingUsers()).orElse(Sets.newHashSet()).contains(targetDto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("User " + requestorDto.getEmail()
		    + " is already subscribed to upates from user " + targetDto.getEmail());
	}

	// add the requester in the followed by list of target
	requestorDto.followAnotherUser(targetDto);
	this.userRepository.save(requestorDto);

	// System.out.println("requestorDto after \n" +
	// this.userRepository.findFirstByEmail(requestorMail));
	// System.out.println("targetDto after \n" +
	// this.userRepository.findFirstByEmail(targetMail));

	return GeneralResponseEntity.builder().success(true).build();

    }

    @Override
    public GeneralResponseEntity blockUpdates(SubscriptionRequestEntity addFollowerEntity) {
	if (addFollowerEntity == null) {
	    return GeneralResponseEntity.createErrorResponseEntity("Invalid parameters");
	}

	User requestorDto, targetDto = null;
	try {
	    requestorDto = this.userService.validateEmailAndReturnUser(addFollowerEntity.getRequestor());
	    targetDto = this.userService.validateEmailAndReturnUser(addFollowerEntity.getTarget());
	} catch (InvalidUserException e) {
	    log.error(e.getMessage(), e);
	    return GeneralResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	// System.out.println("requestorDto before \n" + requestorDto);
	// System.out.println("targetDto before \n" + targetDto);

	// validate same eamil
	if (requestorDto.equals(targetDto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("Cannot block updates for same user");
	}

	// check if user is already blocked
	if (Optional.ofNullable(requestorDto.getHasBlockedUsers()).orElse(Sets.newHashSet()).contains(targetDto)) {
	    return GeneralResponseEntity.createErrorResponseEntity("User " + requestorDto.getEmail()
		    + " has already blocked updates from user " + targetDto.getEmail());
	}

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

	final User senderDto;
	try {
	    senderDto = this.userService.validateEmailAndReturnUser(updatesRequestEntity.getSender());
	} catch (InvalidUserException e) {
	    return ReceipentsResponseEntity.createErrorResponseEntity(e.getMessage());
	}

	final Set<User> receipents = Sets.newHashSet();
	// all friends of sender
	receipents.addAll(Optional.ofNullable(senderDto.getAllFriends()).orElse(Sets.newHashSet()));

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

	// find all users in text
	// filter invalid mail, to avoid lots of db query for may invalid mails
	receipentsInText = receipentsInText.stream().filter(EmailUtils::isValidMail).collect(Collectors.toSet());
	List<User> usersInText = this.userRepository.findAllByEmailIn(receipentsInText);

	// add all in text
	receipents.addAll(usersInText);

	// remove all users who have blocked the sender
	receipents.removeAll(Optional.ofNullable(senderDto.getIsBlockedByUsers()).orElse(Sets.newHashSet()));

	// remove this userhimself, incase he was in the text
	receipents.remove(senderDto);

	return ReceipentsResponseEntity.builder()
				       .success(true)
				       .recipients(receipents.stream().map(User::getEmail).collect(Collectors.toList()))
				       .build();

    }

}
