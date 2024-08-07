package com.test.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.exception.ProductNotFoundException;
import com.test.demo.model.Product;
import com.test.demo.model.dto.ProductDTO;
import com.test.demo.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Caching(evict = {
      @CacheEvict(value = "products", allEntries = true),
      @CacheEvict(value = "paginatedProducts", allEntries = true)
  })
  public ProductDTO createProduct(ProductDTO productDTO) {
    Product product = objectMapper.convertValue(productDTO, Product.class);
    product.setCreatedDate(LocalDateTime.now());
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  @CachePut(value = "products", key = "#id")
  @CacheEvict(value = "paginatedProducts", allEntries = true)
  @Transactional
  public ProductDTO updateProductPrice(String id, Double price) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    product.setPrice(price);
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  @CachePut(value = "products", key = "#id")
  @CacheEvict(value = "paginatedProducts", allEntries = true)
  @Transactional
  public ProductDTO updateProduct(String id, ProductDTO productDTO) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    product.setName(productDTO.name());
    product.setStock(productDTO.stock());
    product.setPrice(productDTO.price());
    return objectMapper.convertValue(productRepository.save(product), ProductDTO.class);
  }

  @Caching(evict = {
    @CacheEvict(value = "products", key = "#id"),
    @CacheEvict(value = "paginatedProducts", allEntries = true)
  })
  public void deleteProduct(String id) {
    productRepository.deleteById(id);
  }

  @Cacheable(value = "products", key = "#id")
  public ProductDTO getProductById(String id) {
    Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    return objectMapper.convertValue(product, ProductDTO.class);
  }

  @Cacheable(value = "paginatedProducts")
  public Page<ProductDTO> getAllProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> productPage = productRepository.findAll(pageable);

    List<ProductDTO> productDTOS = productPage.stream()
        .map(product -> objectMapper.convertValue(product, ProductDTO.class))
        .collect(Collectors.toList());

    return new PageImpl<>(productDTOS, pageable, productPage.getTotalElements());
  }
}
