package com.hwans.screenshareserver.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.List;

@Getter
@Accessors(chain = true)
@Builder
@AllArgsConstructor
public class ErrorResponseDto implements Serializable {
    private String name;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    public ErrorResponseDto(RestApiException restApiException) {
        this.name = restApiException.getErrorCode().getName();
        this.message = restApiException.getMessage();
        this.errors = null;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
