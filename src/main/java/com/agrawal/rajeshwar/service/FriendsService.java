package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;

public interface FriendsService {

    GeneralResponseEntity addFriends(FriendsEntity friendsEntity);

    FriendsListResponseEntity getFriendsList(String email);

    FriendsListResponseEntity getCommonFriends(FriendsEntity friendsEntity);
}
