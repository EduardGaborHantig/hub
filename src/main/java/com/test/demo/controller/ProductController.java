package com.test.demo.controller;

import com.test.demo.model.dto.ProductDTO;
import com.test.demo.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

  private final ProductService productService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) {
    return productService.createProduct(productDTO);
  }

  @PutMapping("/{id}")
  public ProductDTO updateProduct(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
    return productService.updateProduct(id, productDTO);
  }

  @PatchMapping("/{id}/price")
  public ProductDTO updateProductPrice(@PathVariable String id, @RequestParam @Positive(message = "Price must be a positive value") Double price) {
    return productService.updateProductPrice(id, price);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable String id) {
    productService.deleteProduct(id);
  }

  @GetMapping("/{id}")
  public ProductDTO getProductById(@PathVariable String id) {
    return productService.getProductById(id);
  }

  @GetMapping
  public Page<ProductDTO> getAllProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return productService.getAllProducts(page, size);
  }
}
