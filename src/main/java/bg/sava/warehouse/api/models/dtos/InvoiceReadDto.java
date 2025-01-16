package bg.sava.warehouse.api.models.dtos;

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
public class InvoiceReadDto {
    private int id;
    private LocalDate invoiceDate;
    private UUID orderId;
    private String invoiceStatus;
    private double totalAmount;
}
