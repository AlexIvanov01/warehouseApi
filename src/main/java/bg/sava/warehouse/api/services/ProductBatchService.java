package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Product;
import bg.sava.warehouse.api.models.ProductBatch;
import bg.sava.warehouse.api.models.dtos.*;
import bg.sava.warehouse.api.repository.ProductBatchRepository;
import bg.sava.warehouse.api.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class ProductBatchService {
    private final ProductBatchRepository productBatchRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Autowired
    public ProductBatchService(ProductBatchRepository productBatchRepository, ProductRepository productRepository, ModelMapper mapper) {
        this.productBatchRepository = productBatchRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public BatchPageReadDto getBatches(UUID product_id, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<ProductBatch> productBatches = productBatchRepository.findByProductId(product_id, page);
        if (productBatches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        Type listType = new TypeToken<List<BatchReadDto>>() {}.getType();

        List<BatchReadDto> dtos = mapper.map(productBatches.getContent(), listType);

        int pageCount = (int) Math.ceil(productBatchRepository.count() / (float) pageSize);

        BatchPageReadDto batchPageReadDto = new BatchPageReadDto();
        batchPageReadDto.setBatches(dtos);
        batchPageReadDto.setProduct(mapper.map(productBatches.getContent().getFirst().getProduct(), ProductReadDto.class));
        batchPageReadDto.setTotalPages(pageCount);

        return batchPageReadDto;
    }

    public BatchReadDto getBatchById(UUID id) {
        Optional<ProductBatch> productBatch = productBatchRepository.findById(id);
        if (productBatch.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found");
        }
        return mapper.map(productBatch.get(), BatchReadDto.class);
    }

    public BatchReadDto createBatch(UUID productID, BatchCreateDto batchCreateDto) {
        Optional<Product> product = productRepository.findById(productID);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        ProductBatch productBatch = mapper.map(batchCreateDto, ProductBatch.class);
        productBatch.setProduct(product.get());
        productBatchRepository.save(productBatch);
        return mapper.map(productBatch, BatchReadDto.class);
    }

    public void updateBatch(UUID id, BatchUpdateDto batchUpdateDto) {
        Optional<ProductBatch> productBatch = productBatchRepository.findById(id);
        if (productBatch.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found");
        }
        ProductBatch batch = mapper.map(batchUpdateDto, ProductBatch.class);
        batch.setId(id);
        batch.setProduct(productBatch.get().getProduct());
        batch.setBatchDateUpdated(Instant.now());
        productBatchRepository.save(batch);
    }

    public void deleteBatch(UUID id) {
        if (productBatchRepository.existsById(id)) {
            productBatchRepository.deleteById(id); // Delete by ID
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
