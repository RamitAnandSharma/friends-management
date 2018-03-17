package com.agrawal.rajeshwar.dao;

import org.springframework.data.repository.CrudRepository;

import com.agrawal.rajeshwar.dto.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findFirstByEmail(String email);

}