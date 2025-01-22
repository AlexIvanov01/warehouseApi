package bg.sava.warehouse.api.models.dtos.OrderDtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDto {
    private LocalDate orderDate;
    private LocalDate shippedDate;
    private String shippingAddress;
    private String orderStatus;
    @NotNull
    private UUID customerId;
    @NotNull
    private Map<UUID, Integer> batchesMap;
}
