package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonDeserialize(builder = FriendsListRequestEntity.FriendsListRequestEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
@ApiModel
public class FriendsListRequestEntity {

    @NonNull
    private String email;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class FriendsListRequestEntityBuilder {
    }
}
