package com.agrawal.rajeshwar.service;

import com.agrawal.rajeshwar.api.model.GeneralResponseEntity;
import com.agrawal.rajeshwar.api.model.ReceipentsResponseEntity;
import com.agrawal.rajeshwar.api.model.SubscriptionRequestEntity;
import com.agrawal.rajeshwar.api.model.UpdatesRequestEntity;

public interface SubscriptionService {
    GeneralResponseEntity addFollow(SubscriptionRequestEntity addFollowerEntity);

    GeneralResponseEntity blockUpdates(SubscriptionRequestEntity addFollowerEntity);

    ReceipentsResponseEntity getAllUpdateReceipents(UpdatesRequestEntity updatesRequestEntity);
}
