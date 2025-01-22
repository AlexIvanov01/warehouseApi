package bg.sava.warehouse.api.models.dtos.ProductDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDto{
        private String name;
        private String description;
        private String brand;
        private String supplier;
        private String imageURL;
        private String status;
        private String barcode;
        private Integer reorderLevel;
        private Float weight;
}
