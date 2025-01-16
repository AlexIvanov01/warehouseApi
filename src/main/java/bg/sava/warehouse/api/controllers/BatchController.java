package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.*;
import bg.sava.warehouse.api.services.BatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
public class BatchController {
    private final BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public BatchPageReadDto getProductBatches(
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam UUID productId) {
        return batchService.getBatches(productId, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BatchReadDto getProductBatchById(@PathVariable @Valid UUID id) {
        return batchService.getBatchById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BatchReadDto createProduct(@RequestBody @Valid BatchCreateDto batchCreateDto,
                                        @RequestParam UUID productId) {
        return batchService.createBatch(productId, batchCreateDto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateBatch(@PathVariable UUID id, @RequestBody @Valid BatchUpdateDto batchUpdateDto) {
        batchService.updateBatch(id, batchUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBatch(@PathVariable @Valid UUID id) {
        batchService.deleteBatch(id);
    }

}
