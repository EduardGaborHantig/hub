package com.test.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.demo.exception.ProductNotFoundException;
import com.test.demo.model.Product;
import com.test.demo.model.dto.ProductDTO;
import com.test.demo.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

  @MockBean
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Captor
  private ArgumentCaptor<Product> productCaptor;

  @Test
  void createProduct_whenValidInput_thenReturnsCreatedProduct() {
    var currentTime = LocalDateTime.now();

    ProductDTO productDTO = new ProductDTO(null, "Test Product", 1, 15.5, null);
    Product product = new Product();
    product.setName(productDTO.name());
    product.setStock(productDTO.stock());
    product.setPrice(productDTO.price());
    product.setCreatedDate(currentTime);

    when(productRepository.save(any(Product.class))).thenReturn(product);

    ProductDTO createdProduct = productService.createProduct(productDTO);
    assertEquals(productDTO.name(), createdProduct.name());
    assertEquals(productDTO.stock(), createdProduct.stock());
    assertEquals(productDTO.price(), createdProduct.price());
    assertEquals(product.getCreatedDate(), createdProduct.createdDate());

    verify(productRepository).save(productCaptor.capture());
    assertNotNull(productCaptor.getValue().getCreatedDate());
  }

  @Test
  void updateProduct_whenValidIdAndInput_thenReturnsUpdatedProduct() {
    String productId = "123";
    var currentTime = LocalDateTime.now();

    ProductDTO productDTO = new ProductDTO(null, "Test Product", 1, 15.5, null);
    Product product = new Product();
    product.setName(productDTO.name());
    product.setStock(productDTO.stock());
    product.setPrice(productDTO.price());
    product.setCreatedDate(currentTime);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    ProductDTO updatedProduct = productService.updateProduct(productId, productDTO);
    assertEquals(productDTO.name(), updatedProduct.name());
    assertEquals(productDTO.stock(), updatedProduct.stock());
    assertEquals(productDTO.price(), updatedProduct.price());
    assertEquals(product.getCreatedDate(), updatedProduct.createdDate());
  }

  @Test
  void updateProduct_whenProductNotFound_thenThrowsException() {
    String productId = "123";
    ProductDTO productDTO = new ProductDTO(null, "Test Product", 1, 15.5, null);

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, productDTO));
  }


  @Test
  void updateProductPrice_whenValidIdAndPrice_thenReturnsUpdatedProduct() {
    String productId = "123";
    var currentTime = LocalDateTime.now();

    ProductDTO productDTO = new ProductDTO(null, "Test Product", 1, 50.0, null);
    Product product = new Product();
    product.setName(productDTO.name());
    product.setStock(productDTO.stock());
    product.setPrice(productDTO.price());
    product.setCreatedDate(currentTime);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    ProductDTO updatedProduct = productService.updateProductPrice(productId, 50.0);
    assertEquals(productDTO.name(), updatedProduct.name());
    assertEquals(productDTO.stock(), updatedProduct.stock());
    assertEquals(productDTO.price(), updatedProduct.price());
    assertEquals(product.getCreatedDate(), updatedProduct.createdDate());
  }

  @Test
  void updateProductPrice_whenProductNotFound_thenThrowsException() {
    String productId = "123";
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> productService.updateProductPrice(productId, 50.0));
  }

  @Test
  void deleteProduct_whenValidId_thenProductIsDeleted() {
    String productId = "123";

    doNothing().when(productRepository).deleteById(productId);

    productService.deleteProduct(productId);

    verify(productRepository, times(1)).deleteById(productId);
  }


  @Test
  void getProductById_whenValidId_thenReturnsProduct() {
    String productId = "12345";
    LocalDateTime now = LocalDateTime.now();
    Product product = new Product();
    product.setId(productId);
    product.setName("Test Name");
    product.setCreatedDate(now);
    product.setStock(1);
    product.setPrice(25.5);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    ProductDTO foundProduct = productService.getProductById(productId);
    assertEquals("Test Name", foundProduct.name());
    assertEquals(now, foundProduct.createdDate());
    assertEquals(1, foundProduct.stock());
    assertEquals(25.5, foundProduct.price());
  }

  @Test
  void getProductById_whenProductNotFound_thenThrowsException() {
    String productId = "1234";

    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
  }

  @Test
  void getAllProducts_whenCalled_thenReturnsPaginatedProducts() {
    int page = 0;
    int size = 10;

    Pageable pageable = PageRequest.of(page, size);
    Product product1 = new Product();
    product1.setId("1");
    product1.setName("Product1");
    product1.setStock(10);
    product1.setPrice(100.0);
    product1.setCreatedDate(LocalDateTime.now());

    Product product2 = new Product();
    product2.setId("2");
    product2.setName("Product2");
    product2.setStock(20);
    product2.setPrice(200.0);
    product2.setCreatedDate(LocalDateTime.now());

    List<Product> products = List.of(product1, product2);

    Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

    when(productRepository.findAll(pageable)).thenReturn(productPage);

    Page<ProductDTO> result = productService.getAllProducts(page, size);

    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals(2, result.getContent().size());
    assertEquals("Product1", result.getContent().get(0).name());
    assertEquals("Product2", result.getContent().get(1).name());

    verify(productRepository, times(1)).findAll(pageable);
  }
}
