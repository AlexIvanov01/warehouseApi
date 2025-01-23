package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "BATCH")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String lot;
    private Integer quantity;
    private Double purchasePrice;
    private Double sellPrice;
    private Instant batchDateAdded = Instant.now();
    private Instant batchDateUpdated = null;
    private LocalDate expirationDate;
    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable=false)
    private Product product;
    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private List<OrderBatch> orders = new ArrayList<>();


    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", lot='" + lot + '\'' +
                ", quantity=" + quantity +
                ", purchasePrice=" + purchasePrice +
                ", sellPrice=" + sellPrice +
                ", expirationDate=" + expirationDate +
                ", product=" + (product != null ? product.getName() : "null") +
                '}';
    }
}
