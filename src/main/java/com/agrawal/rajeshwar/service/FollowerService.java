package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.AddFollowerEntity;
import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;

public interface FollowerService {
    GeneralResponseEntity addFollow(AddFollowerEntity addFollowerEntity);
}
