package com.orbsec.organizationservice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class CustomError {
    private String errorMessage;
    private int statusCode;
    private Long timestamp;
}
