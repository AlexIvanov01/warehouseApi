package bg.sava.warehouse.api.models.dtos.ProductDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDto{
        private @NotBlank String name;
        private String description;
        private @NotBlank String brand;
        private @NotBlank String supplier;
        private String imageURL;
        private String status;
        private String barcode;
        private Integer reorderLevel;
        private Float weight;
}

