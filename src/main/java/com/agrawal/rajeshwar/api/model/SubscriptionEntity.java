package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonDeserialize(builder = SubscriptionEntity.SubscriptionEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class SubscriptionEntity {

    @NonNull
    private String requestor;

    @NonNull
    private String target;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class SubscriptionEntityBuilder {
    }
}
