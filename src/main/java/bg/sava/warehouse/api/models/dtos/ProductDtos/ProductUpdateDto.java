package bg.sava.warehouse.api.models.dtos.ProductDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDto{
        @NotBlank
        private String name;
        @NotBlank
        private String description;
        @NotBlank
        private String brand;
        @NotBlank
        private String supplier;
        @NotBlank
        private String imageURL;
        @NotBlank
        private String status;
        @NotBlank
        private String barcode;
        @NotNull
        private int reorderLevel;
        @NotNull
        private Float weight;
}
