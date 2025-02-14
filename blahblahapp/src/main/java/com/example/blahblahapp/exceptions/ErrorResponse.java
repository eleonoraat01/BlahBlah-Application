package com.example.blahblahapp.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private HttpStatus statusCode;

    public ErrorResponse(String message) {
        this.message = message;
        this.statusCode = HttpStatus.BAD_REQUEST;
    }
}
