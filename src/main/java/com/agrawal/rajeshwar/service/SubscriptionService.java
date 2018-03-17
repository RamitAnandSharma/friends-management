package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.SubscriptionEntity;

public interface SubscriptionService {
    GeneralResponseEntity addFollow(SubscriptionEntity addFollowerEntity);

    GeneralResponseEntity blockUpdates(SubscriptionEntity addFollowerEntity);
}
