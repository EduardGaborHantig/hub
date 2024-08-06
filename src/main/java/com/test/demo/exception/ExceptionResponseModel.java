package com.test.demo.exception;

import org.springframework.http.HttpStatus;

public record ExceptionResponseModel(Object errors, HttpStatus code) { }
