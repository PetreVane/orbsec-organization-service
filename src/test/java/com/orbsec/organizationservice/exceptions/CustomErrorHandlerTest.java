package com.orbsec.organizationservice.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CustomErrorHandlerTest {
    @Test
    void itShouldOrganizationErrorHandler() {
        // Given
        CustomErrorHandler customErrorHandler = new CustomErrorHandler();
        MissingOrganizationException exception = new MissingOrganizationException("An error occurred");

        // When
        ResponseEntity<CustomError> actualOrganizationErrorHandlerResult = customErrorHandler
                .organizationExceptionHandler(exception);

        // Then
        assertTrue(actualOrganizationErrorHandlerResult.getHeaders().isEmpty());
        assertTrue(actualOrganizationErrorHandlerResult.hasBody());
        assertEquals(HttpStatus.NOT_FOUND, actualOrganizationErrorHandlerResult.getStatusCode());
        CustomError body = actualOrganizationErrorHandlerResult.getBody();
        assertEquals("An error occurred", body.getErrorMessage());
        assertEquals(404, body.getStatusCode());
    }
}

