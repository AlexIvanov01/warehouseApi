package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.repository.ProductRepository;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductPageReadDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
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
    void getProducts() {
        int pageNumber = 0;
        int pageSize = 5;

        List<ProductCreateDto> products = getEasyRandom().objects(ProductCreateDto.class, 5).collect(Collectors.toCollection(ArrayList::new));
        products.forEach(product -> productService.createProduct(product));

        ProductPageReadDto productPageReadDto = productService.getProducts(pageNumber, pageSize);

        assertThat(productPageReadDto).isNotNull();
        assertThat(productPageReadDto.getProducts().size()).isEqualTo(5);
    }

    @Test
    void getProductById() {
    }

    @Test
    void createProduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }
}