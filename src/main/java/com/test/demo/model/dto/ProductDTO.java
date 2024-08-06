package com.test.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ProductDTO(String id, @NotEmpty(message = "Name is mandatory") String name, Integer stock,
                         @Positive(message = "Price must be a positive value") Double price, LocalDateTime createdDate) { }