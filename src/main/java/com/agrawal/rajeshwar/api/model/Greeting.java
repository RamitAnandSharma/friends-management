package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonDeserialize(builder = Greeting.GreetingBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class Greeting {

    @NonNull
    @ApiModelProperty(value = "id", required = true)
    private final Long id;

    @NonNull
    @ApiModelProperty(value = "contet", required = true)
    private final String content;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class LatestOrderDetailResponseEntityBuilder {
    }
}