package com.agrawal.rajeshwar.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonDeserialize(builder = FriendsEntity.FriendsEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class FriendsEntity {

    @NonNull
    private List<String> friends;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class FriendsEntityBuilder {
    }
}
