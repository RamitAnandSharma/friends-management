package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonDeserialize(builder = UpdatesRequestEntity.UpdatesRequestEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
@ApiModel(value = "Start Order Cancellation Response Entity", description = "Entity to display of check warning result.")
public class UpdatesRequestEntity {

    @ApiParam(value = "Email address of sender of updates")
    @NonNull
    private String sender;

    @ApiParam("Optional text update sent by the sender. It can contain email address of registered users who wish to receive updates")
    private String text;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class UpdatesRequestEntityBuilder {
    }
}
