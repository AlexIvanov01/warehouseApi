package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Batch {
    @Id
    @GeneratedValue
    private UUID id;
    private String lot;
    private Integer quantity;
    private Double purchasePrice;
    private Double sellPrice;
    private Instant batchDateAdded = Instant.now();
    private Instant batchDateUpdated = null;
    private LocalDate expirationDate;
    @ManyToOne( fetch = FetchType.EAGER )
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

}
