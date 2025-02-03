package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductPageReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductUpdateDto;
import bg.sava.warehouse.api.services.JwtService;
import bg.sava.warehouse.api.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    ProductService productService;

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    ModelMapper modelMapper = new ModelMapper();

    List<ProductReadDto> products = new ArrayList<>();

    EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .objectPoolSize(100)
                .randomizationDepth(3)
                .stringLengthRange(5, 50)
                .dateRange(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 12, 31))
                .collectionSizeRange(1, 10);
        easyRandom = new EasyRandom(parameters);

        products = easyRandom.objects(ProductReadDto.class, 10).collect(Collectors.toList());
    }

    @Test
    void shouldFindAllProducts() throws Exception {
        ProductPageReadDto productPageReadDto = new ProductPageReadDto(products, 1);

        when(productService.getProducts(0, 10)).thenReturn(productPageReadDto);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(ow.writeValueAsString(productPageReadDto)));

        verify(productService, times(1)).getProducts(0, 10);
    }

    @Test
    void shouldFindProductById() throws Exception {
        ProductReadDto product = products.getFirst();

        when(productService.getProductById(product.getId())).thenReturn(product);

        mockMvc.perform(get("/api/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(ow.writeValueAsString(product)));

        verify(productService, times(1)).getProductById(product.getId());
    }

    @Test
    void shouldNotFindProductById() throws Exception {
        UUID id = UUID.randomUUID();

        when(productService.getProductById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(id);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductCreateDto productCreateDto = easyRandom.nextObject(ProductCreateDto.class);

        ProductReadDto productReadDto = modelMapper.map(productCreateDto, ProductReadDto.class);

        when(productService.createProduct(productCreateDto)).thenReturn(productReadDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(productCreateDto)))
                        .andExpect(status().isCreated())
                        .andExpect(content().json(ow.writeValueAsString(productReadDto)));

        ArgumentCaptor<ProductCreateDto> productCreateDtoArgumentCaptor = ArgumentCaptor.forClass(ProductCreateDto.class);
        verify(productService, times(1)).createProduct(productCreateDtoArgumentCaptor.capture());
        assertThat(productCreateDtoArgumentCaptor.getValue().getName()).isEqualTo(productCreateDto.getName());
    }

    @Test
    void shouldNotCreateProductWhenProductIsInvalid() throws Exception {
        ProductCreateDto productCreateDto = easyRandom.nextObject(ProductCreateDto.class);

        ProductReadDto productReadDto = modelMapper.map(productCreateDto, ProductReadDto.class);

        productCreateDto.setName(null);

        when(productService.createProduct(productCreateDto)).thenReturn(productReadDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(productCreateDto)))
                        .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(ProductCreateDto.class));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductReadDto updatedProduct = easyRandom.nextObject(ProductReadDto.class);
        updatedProduct.setId(products.getFirst().getId());

        ProductUpdateDto productUpdateDto = modelMapper.map(updatedProduct, ProductUpdateDto.class);

        when(productService.updateProduct(eq(updatedProduct.getId()), any(ProductUpdateDto.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/" + updatedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(productUpdateDto)))
                        .andExpect(status().isOk())
                        .andExpect(content().json(ow.writeValueAsString(updatedProduct)));

        verify(productService, times(1)).updateProduct(eq(updatedProduct.getId()), any(ProductUpdateDto.class));
    }

    @Test
    void shouldDeletePost() throws Exception {
        ProductReadDto product = products.getFirst();

        doNothing().when(productService).deleteProduct(product.getId());

        mockMvc.perform(delete("/api/products/" + product.getId()))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(product.getId());
    }
}