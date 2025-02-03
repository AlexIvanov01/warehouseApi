package bg.sava.warehouse.api.models.dtos.InvocieDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    private LocalDate invoiceDate;
    @NotBlank
    private String invoiceStatus;
    @NotNull
    private double totalAmount;
}
