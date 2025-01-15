package bg.sava.warehouse.api.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReadDto {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private String supplier;
    private String imageURL;
    private String status;
    private String barcode;
    private int reorderLevel;
    private float weight;
}
