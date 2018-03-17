package com.agrawal.rajeshwar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agrawal.rajeshwar.dao.UserRepository;
import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.service.FriendsService;

@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
	return this.userRepository.save(user);
    }

}
