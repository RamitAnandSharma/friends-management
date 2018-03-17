package com.agrawal.rajeshwar.dao;

import org.springframework.data.repository.CrudRepository;

import com.agrawal.rajeshwar.dto.Friends;
import com.agrawal.rajeshwar.dto.User;

public interface FriendsRepository extends CrudRepository<Friends, User> {

}