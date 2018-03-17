package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;

public interface FriendsService {

    AddFriendsResponseEntity addFriends(FriendsEntity friendsEntity);

}
