package bg.sava.warehouse.api.models.dtos;

import bg.sava.warehouse.api.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchReadDto {
    private UUID id;
    private String lot;
    private int quantity;
    private double purchasePrice;
    private double sellPrice;
    private LocalDate expirationDate;
}
