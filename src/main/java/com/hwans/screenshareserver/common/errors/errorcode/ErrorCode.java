package com.hwans.screenshareserver.common.errors.errorcode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getName();
    HttpStatus getStatus();
    String getDefaultMessage();
}
