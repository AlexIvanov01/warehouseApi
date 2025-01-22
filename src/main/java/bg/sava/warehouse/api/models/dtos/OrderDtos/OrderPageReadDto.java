package bg.sava.warehouse.api.models.dtos.OrderDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPageReadDto {
    private List<OrderReadDto> orders;
    private int totalPages;
}
