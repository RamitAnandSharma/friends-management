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
@JsonDeserialize(builder = ReceipentsResponseEntity.ReceipentsResponseEntityBuilder.class)
@Value
@Builder
@AllArgsConstructor
public class ReceipentsResponseEntity implements ErrorResponseEntityInterface {

	private List<String> recipients;

	private boolean success;

	private String errorMessage;

	@JsonPOJOBuilder(withPrefix = "")
	public static final class ReceipentsResponseEntityBuilder {
	}

	@JsonIgnore
	public static ReceipentsResponseEntity createErrorResponseEntity(String message) {
		return ReceipentsResponseEntity.builder().success(false).errorMessage(message).build();
	}
}
