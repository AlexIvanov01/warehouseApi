package bg.sava.warehouse.api.models.dtos.BatchDtos;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String lot;
    @NotBlank
    private Integer quantity;
    @NotBlank
    private Double purchasePrice;
    @NotBlank
    private Double sellPrice;
    @NotBlank
    private LocalDate expirationDate;
}
