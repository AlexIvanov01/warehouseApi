package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductPageReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductUpdateDto;
import bg.sava.warehouse.api.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductPageReadDto getProducts(
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return productService.getProducts(pageNumber - 1, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductReadDto getProductById(@PathVariable @Valid UUID id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductReadDto createProduct(@RequestBody @Valid ProductCreateDto productCreateDto) {
        return productService.createProduct(productCreateDto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDto productUpdateDto) {
        productService.updateProduct(id, productUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

}
