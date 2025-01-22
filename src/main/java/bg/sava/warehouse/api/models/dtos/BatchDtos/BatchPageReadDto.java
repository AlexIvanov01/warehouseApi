package bg.sava.warehouse.api.models.dtos.BatchDtos;

import bg.sava.warehouse.api.models.dtos.ProductDtos.ProductReadDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchPageReadDto {
    private ProductReadDto product;
    private List<BatchReadDto> batches;
    private int totalPages;
}
