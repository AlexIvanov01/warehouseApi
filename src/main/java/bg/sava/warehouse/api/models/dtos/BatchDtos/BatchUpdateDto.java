package bg.sava.warehouse.api.models.dtos.BatchDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchUpdateDto {
    private String lot;
    private Integer quantity;
    private Double purchasePrice;
    private Double sellPrice;
    private LocalDate expirationDate;
}
