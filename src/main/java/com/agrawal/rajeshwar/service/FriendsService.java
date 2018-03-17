package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.UserEntity;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;

public interface FriendsService {
    int saveUser(UserEntity user) throws InvalidUserException;
}
