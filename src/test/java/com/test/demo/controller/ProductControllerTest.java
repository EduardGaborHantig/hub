package com.test.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.demo.model.dto.ProductDTO;
import com.test.demo.service.ProductService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

  @MockBean
  private ProductService productService;

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void createProduct_whenValidInput_thenReturnsCreatedProduct() throws Exception {
    var currentTime = LocalDateTime.now();

    ProductDTO productDTO = new ProductDTO(null, "Test Product", 1, 15.5, currentTime);
    when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

    mockMvc.perform(post("/api/v1/products")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(productDTO.name()))
        .andExpect(jsonPath("$.stock").value(productDTO.stock()))
        .andExpect(jsonPath("$.price").value(productDTO.price()))
        .andExpect(jsonPath("$.createdDate").value(productDTO.createdDate().toString()));

    verify(productService).createProduct(any(ProductDTO.class));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void updateProduct_whenValidInput_thenReturnsUpdatedProduct() throws Exception {
    var currentTime = LocalDateTime.now();
    String productId = "123";
    ProductDTO productDTO = new ProductDTO(productId, "Updated Product", 1, 20.0, currentTime);

    when(productService.updateProduct(eq(productId), any(ProductDTO.class))).thenReturn(productDTO);

    mockMvc.perform(put("/api/v1/products/{id}", productId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(productDTO.name()))
        .andExpect(jsonPath("$.stock").value(productDTO.stock()))
        .andExpect(jsonPath("$.price").value(productDTO.price()))
        .andExpect(jsonPath("$.createdDate").value(productDTO.createdDate().toString()));

    verify(productService).updateProduct(eq(productId), any(ProductDTO.class));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void updateProductPrice_whenValidInput_thenReturnsUpdatedProduct() throws Exception {
    var currentTime = LocalDateTime.now();
    String productId = "123";
    ProductDTO productDTO = new ProductDTO(productId, "Test Product", 1, 50.0, currentTime);

    when(productService.updateProductPrice(eq(productId), any(Double.class))).thenReturn(productDTO);

    mockMvc.perform(patch("/api/v1/products/{id}/price", productId)
            .with(csrf())
            .param("price", "50.0")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(productDTO.name()))
        .andExpect(jsonPath("$.stock").value(productDTO.stock()))
        .andExpect(jsonPath("$.price").value(50.0))
        .andExpect(jsonPath("$.createdDate").value(productDTO.createdDate().toString()));

    verify(productService).updateProductPrice(eq(productId), any(Double.class));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void deleteProduct_whenValidId_thenReturnsNoContent() throws Exception {
    String productId = "123";
    doNothing().when(productService).deleteProduct(productId);

    mockMvc.perform(delete("/api/v1/products/{id}", productId)
            .with(csrf()))
        .andExpect(status().isNoContent());

    verify(productService).deleteProduct(productId);
  }

  @Test
  @WithMockUser(username = "user")
  void getProductById_whenValidId_thenReturnsProduct() throws Exception {
    String productId = "12345";
    var currentTime = LocalDateTime.now();
    ProductDTO productDTO = new ProductDTO(productId, "Test Product", 1, 25.5, currentTime);

    when(productService.getProductById(productId)).thenReturn(productDTO);

    mockMvc.perform(get("/api/v1/products/{id}", productId)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId))
        .andExpect(jsonPath("$.name").value(productDTO.name()))
        .andExpect(jsonPath("$.stock").value(productDTO.stock()))
        .andExpect(jsonPath("$.price").value(productDTO.price()))
        .andExpect(jsonPath("$.createdDate").value(currentTime.toString()));

    verify(productService).getProductById(productId);
  }

  @Test
  @WithMockUser(username = "user")
  void getAllProducts_whenCalled_thenReturnsPaginatedProducts() throws Exception {
    int page = 0;
    int size = 10;
    var currentTime = LocalDateTime.now();

    ProductDTO productDTO1 = new ProductDTO("1", "Product1", 10, 100.0, currentTime);
    ProductDTO productDTO2 = new ProductDTO("2", "Product2", 20, 200.0, currentTime);
    List<ProductDTO> products = List.of(productDTO1, productDTO2);

    when(productService.getAllProducts(page, size)).thenReturn(new PageImpl<>(products));

    mockMvc.perform(get("/api/v1/products")
            .param("page", String.valueOf(page))
            .param("size", String.valueOf(size))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value(productDTO1.name()))
        .andExpect(jsonPath("$.content[1].name").value(productDTO2.name()));

    verify(productService).getAllProducts(page, size);
  }
}
