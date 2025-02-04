package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchCreateDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchPageReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchReadDto;
import bg.sava.warehouse.api.models.dtos.BatchDtos.BatchUpdateDto;
import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import bg.sava.warehouse.api.services.BatchService;
import bg.sava.warehouse.api.services.JwtService;
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
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BatchesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BatchesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    BatchService batchService;

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    ModelMapper modelMapper = new ModelMapper();

    ProductReadDto product;
    List<BatchReadDto> batches = new ArrayList<>();

    EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .objectPoolSize(100)
                .randomizationDepth(3)
                .stringLengthRange(5, 50)
                .dateRange(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
                .collectionSizeRange(1, 10);
        easyRandom = new EasyRandom(parameters);

        batches = easyRandom.objects(BatchReadDto.class, 10).collect(Collectors.toList());
        product = easyRandom.nextObject(ProductReadDto.class);
    }

    @Test
    void shouldFindAllBatches() throws Exception {
        BatchPageReadDto batchPageReadDto = new BatchPageReadDto(product, batches, 1);

        when(batchService.getBatches(product.getId(), 1, 10))
                .thenReturn(batchPageReadDto);

        mockMvc.perform(get("/api/batches" )
                .param("productId", product.getId().toString()))
                .andExpect(status().isOk());

        verify(batchService, times(1)).getBatches(product.getId(), 0, 10);
    }

    @Test
    void shouldFindBatchById() throws Exception {
        BatchReadDto batch = batches.getFirst();

        when(batchService.getBatchById(batch.getId())).thenReturn(batch);

        mockMvc.perform(get("/api/batches/" + batch.getId()))
                .andExpect(status().isOk());

        verify(batchService, times(1)).getBatchById(batch.getId());
    }

    @Test
    void shouldNotFindBatchById() throws Exception {
        UUID id = UUID.randomUUID();

        when(batchService.getBatchById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));

        mockMvc.perform(get("/api/batches/" + id))
                .andExpect(status().isNotFound());

        verify(batchService, times(1)).getBatchById(id);
    }

    @Test
    void shouldCreateBatch() throws Exception {
        BatchCreateDto batchCreateDto = easyRandom.nextObject(BatchCreateDto.class);

        BatchReadDto batchReadDto = modelMapper.map(batchCreateDto, BatchReadDto.class);

        when(batchService.createBatch(any(UUID.class), eq(batchCreateDto))).thenReturn(batchReadDto);

        Locale.setDefault(Locale.US);
        String json = """
        {
            "lot": "%s",
            "quantity": %d,
            "purchasePrice": %.10f,
            "sellPrice": %.10f,
            "expirationDate": "%s"
        }
        """.formatted(
            batchCreateDto.getLot(),
            batchCreateDto.getQuantity(),
            batchCreateDto.getPurchasePrice(),
            batchCreateDto.getSellPrice(),
            batchCreateDto.getExpirationDate().toString()
        );

        mockMvc.perform(post("/api/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .param("productId", UUID.randomUUID().toString()))
                .andExpect(status().isCreated());

        ArgumentCaptor<BatchCreateDto> batchCreateDtoArgumentCaptor = ArgumentCaptor.forClass(BatchCreateDto.class);
        verify(batchService, times(1)).createBatch(any(UUID.class), batchCreateDtoArgumentCaptor.capture());
        assertThat(batchCreateDtoArgumentCaptor.getValue().getLot()).isEqualTo(batchCreateDto.getLot());
    }

    @Test
    void shouldNotCreateBatchWhenBatchIsInvalid() throws Exception {
        BatchCreateDto batchCreateDto = easyRandom.nextObject(BatchCreateDto.class);

        batchCreateDto.setLot("");

        Locale.setDefault(Locale.US);
        String json = """
        {
            "lot": "%s",
            "quantity": %d,
            "purchasePrice": %.10f,
            "sellPrice": %.10f,
            "expirationDate": "%s"
        }
        """.formatted(
                batchCreateDto.getLot(),
                batchCreateDto.getQuantity(),
                batchCreateDto.getPurchasePrice(),
                batchCreateDto.getSellPrice(),
                batchCreateDto.getExpirationDate().toString()
        );


        mockMvc.perform(post("/api/batches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .param("productId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());

        verify(batchService, never()).createBatch(any(UUID.class), any(BatchCreateDto.class));
    }

    @Test
    void shouldUpdateBatch() throws Exception {
        BatchReadDto updatedBatch = easyRandom.nextObject(BatchReadDto.class);
        updatedBatch.setId(batches.getFirst().getId());

        BatchUpdateDto batchUpdateDto = modelMapper.map(updatedBatch, BatchUpdateDto.class);

        Locale.setDefault(Locale.US);
        String json = """
        {
            "lot": "%s",
            "quantity": %d,
            "purchasePrice": %.10f,
            "sellPrice": %.10f,
            "expirationDate": "%s"
        }
        """.formatted(
                batchUpdateDto.getLot(),
                batchUpdateDto.getQuantity(),
                batchUpdateDto.getPurchasePrice(),
                batchUpdateDto.getSellPrice(),
                batchUpdateDto.getExpirationDate().toString()
        );


        doNothing().when(batchService).updateBatch(eq(updatedBatch.getId()), any(BatchUpdateDto.class));

        mockMvc.perform(put("/api/batches/" + updatedBatch.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(batchService, times(1)).updateBatch(eq(updatedBatch.getId()), any(BatchUpdateDto.class));
    }

    @Test
    void shouldDeleteBatch() throws Exception {
        BatchReadDto batch = batches.getFirst();

        doNothing().when(batchService).deleteBatch(batch.getId());

        mockMvc.perform(delete("/api/batches/" + batch.getId()))
                .andExpect(status().isNoContent());

        verify(batchService, times(1)).deleteBatch(batch.getId());
    }
}