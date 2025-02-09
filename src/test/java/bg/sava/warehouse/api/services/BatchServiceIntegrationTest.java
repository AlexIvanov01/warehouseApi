package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchCreateDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchPageReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchUpdateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductCreateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.repository.BatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BatchServiceIntegrationTest extends BaseTest {

    @Autowired
    private BatchService batchService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private ModelMapper modelMapper;

    private ProductReadDto product;

    @BeforeEach
    void setUp() {
        ProductCreateDto productCreateDto = getEasyRandom().nextObject(ProductCreateDto.class);
        product = productService.createProduct(productCreateDto);
    }

    @Test
    void testGetBatches() {
        int pageNumber = 0;
        int pageSize = 5;

        List<BatchCreateDto> batches = getEasyRandom().objects(BatchCreateDto.class, 5).toList();
        batches.forEach(batch -> batchService.createBatch(product.getId(), batch));

        BatchPageReadDto batchPageReadDto = batchService.getBatches(product.getId(), pageNumber, pageSize);

        assertThat(batchPageReadDto).isNotNull();
        assertThat(batchPageReadDto.getBatches().size()).isEqualTo(5);
    }

    @Test
    void testGetBatchById() {
        BatchCreateDto batch = getEasyRandom().nextObject(BatchCreateDto.class);
        BatchReadDto createdBatch = batchService.createBatch(product.getId(), batch);

        BatchReadDto batchById = batchService.getBatchById(createdBatch.getId());

        assertThat(batchById).isNotNull();
        assertThat(batchById.getId()).isEqualTo(createdBatch.getId());
        assertThat(batchById.getLot()).isEqualTo(batch.getLot());
    }

    @Test
    void testCreateBatch() {
        BatchCreateDto batchCreateDto = getEasyRandom().nextObject(BatchCreateDto.class);

        BatchReadDto batchReadDto = batchService.createBatch(product.getId(), batchCreateDto);

        assertThat(batchReadDto).isNotNull();
        assertThat(batchReadDto.getId()).isNotNull();
        assertThat(batchReadDto.getLot()).isEqualTo(batchCreateDto.getLot());
    }

    @Test
    void testUpdateBatch() {
        BatchCreateDto batchCreateDto = getEasyRandom().nextObject(BatchCreateDto.class);

        BatchReadDto savedBatch = batchService.createBatch(product.getId(), batchCreateDto);

        BatchUpdateDto batchUpdateDto = modelMapper.map(savedBatch, BatchUpdateDto.class);
        batchUpdateDto.setLot("Updated lot");

        batchService.updateBatch(savedBatch.getId(), batchUpdateDto);

        BatchReadDto updatedBatch = batchService.getBatchById(savedBatch.getId());

        assertThat(updatedBatch).isNotNull();
        assertThat(updatedBatch.getId()).isEqualTo(savedBatch.getId());
        assertThat(updatedBatch.getLot()).isEqualTo(batchUpdateDto.getLot());
    }

    @Test
    void testDeleteBatch() {
        BatchCreateDto batchCreateDto = getEasyRandom().nextObject(BatchCreateDto.class);

        BatchReadDto savedBatch = batchService.createBatch(product.getId(), batchCreateDto);

        batchService.deleteBatch(savedBatch.getId());

        assertThat(batchRepository.findById(savedBatch.getId())).isEmpty();
    }

    @Test
    void testDeleteBatchWithInvalidId() {
        assertThatThrownBy(() -> batchService.deleteBatch(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Batch not found\"");
    }

    @Test
    void testGetBatchByIdWithInvalidId() {
        assertThatThrownBy(() -> batchService.getBatchById(UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Batch not found\"");
    }

    @Test
    void testUpdateBatchWithInvalidId() {
        assertThatThrownBy(() -> batchService.updateBatch(UUID.randomUUID(), getEasyRandom().nextObject(BatchUpdateDto.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Batch not found\"");
    }
}