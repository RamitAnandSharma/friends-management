package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.FriendsEntity;
import com.agrawal.rajeshwar.api.model.FriendsListRequestEntity;
import com.agrawal.rajeshwar.api.model.FriendsListResponseEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;

public interface FriendsService {

    GeneralResponseEntity addFriends(FriendsEntity friendsEntity);

    FriendsListResponseEntity getCommonFriends(FriendsEntity friendsEntity);

    FriendsListResponseEntity getFriendsList(FriendsListRequestEntity entity);
}
