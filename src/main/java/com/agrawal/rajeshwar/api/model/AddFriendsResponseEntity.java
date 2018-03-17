package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AddFriendsResponseEntity.AddFriendsResponseEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class AddFriendsResponseEntity implements ErrorResponseEntityInterface {

    private boolean success;

    private String errorMessage;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class AddFriendsResponseEntityBuilder {
    }

    @JsonIgnore
    public static AddFriendsResponseEntity createErrorResponseEntity(String message) {
	return AddFriendsResponseEntity.builder().success(false).errorMessage(message).build();
    }
}
