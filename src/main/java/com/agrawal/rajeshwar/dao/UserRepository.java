package com.agrawal.rajeshwar.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.agrawal.rajeshwar.dto.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findFirstByEmail(String email);

    List<User> findAllByEmailIn(Set<String> emails);

}