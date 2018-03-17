package com.agrawal.rajeshwar.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = GeneralResponseEntity.GeneralResponseEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class GeneralResponseEntity implements ErrorResponseEntityInterface {

    private boolean success;

    private String errorMessage;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class GeneralResponseEntityBuilder {
    }

    @JsonIgnore
    public static GeneralResponseEntity createErrorResponseEntity(String message) {
	return GeneralResponseEntity.builder().success(false).errorMessage(message).build();
    }
}
