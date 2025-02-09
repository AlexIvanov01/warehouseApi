package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductUpdateDto;
import bg.sava.warehouse.api.repository.ProductRepository;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductPageReadDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProductServiceIntegrationTest extends BaseTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    void testGetProducts() {
        int pageNumber = 0;
        int pageSize = 5;

        List<ProductCreateDto> products = getEasyRandom().objects(ProductCreateDto.class, 5).toList();
        products.forEach(product -> productService.createProduct(product));

        ProductPageReadDto productPageReadDto = productService.getProducts(pageNumber, pageSize);

        assertThat(productPageReadDto).isNotNull();
        assertThat(productPageReadDto.getProducts().size()).isEqualTo(5);
    }
    @Test
    void testGetProductById() {
        ProductCreateDto product = getEasyRandom().nextObject(ProductCreateDto.class);
        ProductReadDto createdProduct = productService.createProduct(product);

        ProductReadDto productById = productService.getProductById(createdProduct.getId());

        assertThat(productById).isNotNull();
        assertThat(productById.getId()).isEqualTo(createdProduct.getId());
        assertThat(productById.getName()).isEqualTo(product.getName());
    }

    @Test
    void testCreateProduct() {
        ProductCreateDto productCreateDto = getEasyRandom().nextObject(ProductCreateDto.class);

        ProductReadDto productReadDto = productService.createProduct(productCreateDto);

        assertThat(productReadDto).isNotNull();
        assertThat(productReadDto.getId()).isNotNull();
        assertThat(productReadDto.getName()).isEqualTo(productCreateDto.getName());
    }

    @Test
    void testUpdateProduct() {
        ProductCreateDto productCreateDto = getEasyRandom().nextObject(ProductCreateDto.class);

        ProductReadDto savedProduct = productService.createProduct(productCreateDto);

        ProductUpdateDto productUpdateDto = modelMapper.map(savedProduct, ProductUpdateDto.class);
        productUpdateDto.setName("Updated name");

        ProductReadDto updatedProduct = productService.updateProduct(savedProduct.getId(), productUpdateDto);

        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(updatedProduct.getName()).isEqualTo(productUpdateDto.getName());
    }

    @Test
    void testDeleteProduct() {
        ProductCreateDto productCreateDto = getEasyRandom().nextObject(ProductCreateDto.class);

        ProductReadDto savedProduct = productService.createProduct(productCreateDto);

        productService.deleteProduct(savedProduct.getId());

        assertThat(productRepository.findById(savedProduct.getId())).isEmpty();
    }

    @Test
    void testDeleteProductWithInvalidId() {
        assertThatThrownBy(() -> productService.deleteProduct(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Product not found\"");
    }

    @Test
    void testGetProductByIdWithInvalidId() {
        assertThatThrownBy(() -> productService.getProductById(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Product not found\"");
    }

    @Test
    void testUpdateProductWithInvalidId() {
        assertThatThrownBy(() -> productService.updateProduct(UUID.randomUUID(), getEasyRandom().nextObject(ProductUpdateDto.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Product not found\"");
    }
}