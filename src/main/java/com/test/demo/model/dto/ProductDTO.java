package com.test.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;
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

  @NotEmpty(message = "Price is mandatory")
  private Double price;
}
