package com.webrayan.commerce.core.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Base controller class providing common functionality for all controllers
 */
@RequestMapping("/api")
public abstract class BaseController {

    /**
     * Create a success response with data
     */
    protected ResponseEntity<Map<String, Object>> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a success response with message
     */
    protected ResponseEntity<Map<String, Object>> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a success response with data and message
     */
    protected ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Create an error response with message
     */
    protected ResponseEntity<Map<String, Object>> createErrorResponse(String message, int statusCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(statusCode).body(response);
    }

    /**
     * Create a bad request error response
     */
    protected ResponseEntity<Map<String, Object>> createBadRequestResponse(String message) {
        return createErrorResponse(message, 400);
    }

    /**
     * Create an unauthorized error response
     */
    protected ResponseEntity<Map<String, Object>> createUnauthorizedResponse(String message) {
        return createErrorResponse(message, 401);
    }

    /**
     * Create a not found error response
     */
    protected ResponseEntity<Map<String, Object>> createNotFoundResponse(String message) {
        return createErrorResponse(message, 404);
    }

    /**
     * Create an internal server error response
     */
    protected ResponseEntity<Map<String, Object>> createInternalErrorResponse(String message) {
        return createErrorResponse(message, 500);
    }
}
