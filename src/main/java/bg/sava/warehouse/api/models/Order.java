package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDate orderDate;
    private LocalDate shippedDate;
    private String shippingAddress;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable=false)
    private Customer customer;
    @OneToOne(mappedBy = "order")
    private Invoice invoice;
    private String status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderBatch> batches = new ArrayList<>();

    public void addBatch(Batch batch, int count) {
        OrderBatch orderBatch = new OrderBatch(this, batch, count);
        batches.add(orderBatch);
        batch.getOrders().add(orderBatch);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", shippedDate=" + shippedDate +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", customer=" + (customer != null ? customer.getName() : "null") +
                ", status='" + status + '\'' +
                ", batches=" + (batches != null ? batches.toString() : 0) +
                '}';
    }
}