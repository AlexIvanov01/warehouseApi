package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "PRODUCT")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private String supplier;
    private String imageURL;
    private String status;
    @Column(length = 20)
    private String barcode;
    private Integer reorderLevel;
    private Float weight;
    private Instant productDateAdded = Instant.now();
    private Instant productDateUpdated = null;
    @OneToMany(mappedBy = "product")
    private List<Batch> batches;
}
