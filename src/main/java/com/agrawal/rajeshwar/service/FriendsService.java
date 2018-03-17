package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.AddFriendsResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;

public interface FriendsService {

    AddFriendsResponseEntity addFriends(FriendsEntity friendsEntity);

    FriendsListResponseEntity getFriendsList(String email);

}
