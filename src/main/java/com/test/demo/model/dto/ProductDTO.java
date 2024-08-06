package com.test.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

  private String id;

  @NotEmpty(message = "Name is mandatory")
  private String name;

  private Integer stock;

  @Positive(message = "Price must be a positive value")
  private Double price;

  private LocalDateTime createdDate;
}
