package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.dto.User;
import com.agrawal.rajeshwar.exceptions.InvalidUserException;

import lombok.NonNull;

public interface UserService {
    @NonNull
    User validateEmailAndReturnUser(String email) throws InvalidUserException;
}
