package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Orders {
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDate orderDate;
    private LocalDate shippedDate;
    private String shippingAddress;
    @ManyToOne
    private Customer customer;
    private String status;
    @ManyToMany
    @JoinTable(
            name = "order_batch",
            joinColumns = @JoinColumn(
                    name = "order_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "batch_id",
                    referencedColumnName = "id"
            )
    )
    private List<Batch> batches;
}