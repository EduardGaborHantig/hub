package com.test.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.demo.model.Product;
import com.test.demo.model.dto.ProductDTO;
import com.test.demo.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
  private final ProductRepository productRepository;

  private final ObjectMapper objectMapper;

  public ProductService(ProductRepository productRepository, ObjectMapper objectMapper) {
    this.productRepository = productRepository;
    this.objectMapper = objectMapper;
  }

  public ProductDTO createProduct(ProductDTO productDTO) {
    Product product = objectMapper.convertValue(productDTO, Product.class);
    product.setCreatedDate(LocalDateTime.now());
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  @Transactional
  public ProductDTO updateProductPrice(String id, Double price) throws Exception {
    Product product = productRepository.findById(id).orElseThrow(Exception::new);
    product.setPrice(price);
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  @Transactional
  public ProductDTO updateProduct(String id, ProductDTO productDTO) throws Exception {
    Product product = productRepository.findById(id).orElseThrow(Exception::new);
    product.setName(productDTO.getName());
    product.setStock(productDTO.getStock());
    product.setPrice(productDTO.getPrice());
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  public void deleteProduct(String id) {
    productRepository.deleteById(id);
  }

  public ProductDTO getProductById(String id) throws Exception {
    Product product = productRepository.findById(id).orElseThrow(Exception::new);
    return objectMapper.convertValue(product, ProductDTO.class);
  }

  public Page<ProductDTO> getAllProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> productPage = productRepository.findAll(pageable);

    List<ProductDTO> productDTOS = productPage.stream()
        .map(product -> objectMapper.convertValue(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new PageImpl<>(productDTOS, pageable, productPage.getTotalElements());
  }
}
