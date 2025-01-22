package bg.sava.warehouse.api.models.dtos.CustomerDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPageReadDto {
    private List<CustomerReadDto> customers;
    private int totalPages;
}

