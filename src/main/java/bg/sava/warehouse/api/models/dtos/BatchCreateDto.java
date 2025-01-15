package bg.sava.warehouse.api.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchCreateDto {
    @NotBlank
    private String lot;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double purchasePrice;
    @NotNull
    private Double sellPrice;
    @NotNull
    private LocalDate expirationDate;
}
