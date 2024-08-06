package com.test.demo.model;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {

  @Id
  private String id;
  private String name;
  private Integer stock;
  private Double price;
  private LocalDateTime createdDate;
}
