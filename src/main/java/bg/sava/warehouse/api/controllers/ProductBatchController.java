package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.*;
import bg.sava.warehouse.api.services.ProductBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
public class ProductBatchController {
    private final ProductBatchService productBatchService;

    @Autowired
    public ProductBatchController(ProductBatchService productBatchService) {
        this.productBatchService = productBatchService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public BatchPageReadDto getProductBatches(
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam UUID productId) {
        return productBatchService.getBatches(productId, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BatchReadDto getProductBatchById(@PathVariable @Valid UUID id) {
        return productBatchService.getBatchById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BatchReadDto createProduct(@RequestBody @Valid BatchCreateDto batchCreateDto,
                                        @RequestParam UUID productId) {
        return productBatchService.createBatch(productId, batchCreateDto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBatch(@PathVariable UUID id, @RequestBody @Valid BatchUpdateDto batchUpdateDto) {
        productBatchService.updateBatch(id, batchUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBatch(@PathVariable @Valid UUID id) {
        productBatchService.deleteBatch(id);
    }

}
