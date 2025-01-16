package bg.sava.warehouse.api.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceUpdateDto {
    private LocalDate invoiceDate;
    private String invoiceStatus;
    private double totalAmount;
}
