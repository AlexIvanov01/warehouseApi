package bg.sava.warehouse.api.models.dtos.OrderDtos;

import bg.sava.warehouse.api.models.dtos.CustomerDtos.CustomerReadDto;
import bg.sava.warehouse.api.models.dtos.InvocieDtos.InvoiceReadDto;
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
public class OrderReadDto {
    private UUID id;
    private LocalDate orderDate;
    private LocalDate shippedDate;
    private String shippingAddress;
    private String orderStatus;
    private CustomerReadDto customer;
    private InvoiceReadDto invoice;
    private Map<UUID, Integer> batches;
}
