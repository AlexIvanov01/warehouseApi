package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Product;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductPageReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductUpdateDto;
import bg.sava.warehouse.api.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ModelMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductPageReadDto getProducts(int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);

        List<Product> productItems = productRepository.findAll(page).getContent();
        Type listType = new TypeToken<List<ProductReadDto>>() {}.getType();

        List<ProductReadDto> dtos = productMapper.map(productItems, listType);

        int pageCount = (int) Math.ceil(productRepository.count() / (float) pageSize);

        ProductPageReadDto productPageReadDto = new ProductPageReadDto();
        productPageReadDto.setProducts(dtos);
        productPageReadDto.setTotalPages(pageCount);

        return productPageReadDto;
    }

    public ProductReadDto getProductById(UUID id) {
        Optional<Product> productItem = productRepository.findById(id);
        if (productItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return productMapper.map(productItem.get(), ProductReadDto.class);
    }

    public ProductReadDto createProduct(ProductCreateDto productCreateDto) {
        Product product = productMapper.map(productCreateDto, Product.class);
        productRepository.save(product);
        return productMapper.map(product, ProductReadDto.class);
    }

    public void updateProduct(UUID id, ProductUpdateDto productUpdateDto) {
        Optional<Product> productItem = productRepository.findById(id);
        if (productItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        Product product = productMapper.map(productUpdateDto, Product.class);
        product.setId(id);
        product.setProductDateUpdated(Instant.now());
        productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id); // Delete by ID
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
