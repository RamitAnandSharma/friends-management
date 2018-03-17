package com.agrawal.rajeshwar.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = FriendsListResponseEntity.AddFriendsResponseEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class FriendsListResponseEntity implements ErrorResponseEntityInterface {

    private List<String> friends;

    private Long count;

    private boolean success;

    private String errorMessage;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class AddFriendsResponseEntityBuilder {
    }

    @JsonIgnore
    public static FriendsListResponseEntity createErrorResponseEntity(String message) {
	return FriendsListResponseEntity.builder().success(false).errorMessage(message).build();
    }
}
