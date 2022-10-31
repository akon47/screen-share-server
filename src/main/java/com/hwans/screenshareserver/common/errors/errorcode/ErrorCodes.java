package com.hwans.screenshareserver.common.errors.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class ErrorCodes {
    @RequiredArgsConstructor
    public enum BadRequest implements ErrorCode {
        BAD_REQUEST("Bad request."),
        INVALID_PARAMETER("Invalid parameters."),
        INVALID_CHANNEL_PASSWORD("Please check the channel password."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    @RequiredArgsConstructor
    public enum Unauthorized implements ErrorCode {
        UNAUTHORIZED("Valid credentials do not exist."),
        TOKEN_EXPIRED("Token expired."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.UNAUTHORIZED;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    @RequiredArgsConstructor
    public enum Forbidden implements ErrorCode {
        FORBIDDEN("You don't have access."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.FORBIDDEN;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    @RequiredArgsConstructor
    public enum NotFound implements ErrorCode {
        NOT_FOUND("does not exist."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.NOT_FOUND;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    @RequiredArgsConstructor
    public enum Conflict implements ErrorCode {
        CONFLICT("The request could not be completed due to conflicting requests."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.CONFLICT;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    @RequiredArgsConstructor
    public enum InternalServerError implements ErrorCode {
        INTERNAL_SERVER_ERROR("This is an internal server error."),
        ;

        private final String defaultMessage;

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }

        @Override
        public HttpStatus getStatus() {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        @Override
        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }
}
