package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.Product;
import bg.sava.warehouse.api.models.Batch;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchCreateDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchPageReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchUpdateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.repository.BatchRepository;
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
public class BatchService {
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    @Autowired
    public BatchService(BatchRepository batchRepository, ProductRepository productRepository, ModelMapper mapper) {
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    public BatchPageReadDto getBatches(UUID product_id, int pageNumber, int pageSize) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<Batch> productBatches = batchRepository.findByProductId(product_id, page);
        if (productBatches.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        Type listType = new TypeToken<List<BatchReadDto>>() {}.getType();

        List<BatchReadDto> dtos = mapper.map(productBatches.getContent(), listType);

        int pageCount = (int) Math.ceil(batchRepository.count() / (float) pageSize);

        BatchPageReadDto batchPageReadDto = new BatchPageReadDto();
        batchPageReadDto.setBatches(dtos);
        batchPageReadDto.setProduct(mapper.map(productBatches.getContent().getFirst().getProduct(), ProductReadDto.class));
        batchPageReadDto.setTotalPages(pageCount);

        return batchPageReadDto;
    }

    public BatchReadDto getBatchById(UUID id) {
        Optional<Batch> productBatch = batchRepository.findById(id);
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
        Batch batch = mapper.map(batchCreateDto, Batch.class);
        batch.setProduct(product.get());
        batchRepository.save(batch);
        return mapper.map(batch, BatchReadDto.class);
    }

    public void updateBatch(UUID id, BatchUpdateDto batchUpdateDto) {
        Optional<Batch> productBatch = batchRepository.findById(id);
        if (productBatch.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found");
        }
        Batch batch = mapper.map(batchUpdateDto, Batch.class);
        batch.setId(id);
        batch.setProduct(productBatch.get().getProduct());
        batch.setBatchDateUpdated(Instant.now());
        batchRepository.save(batch);
    }

    public void deleteBatch(UUID id) {
        if (batchRepository.existsById(id)) {
            batchRepository.deleteById(id); // Delete by ID
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
