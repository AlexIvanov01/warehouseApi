package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderBatch {

    @EmbeddedId
    private OrderBatchKey id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("batchId")
    @JoinColumn(name = "batch_id")
    private Batch batch;

    private int count;

    public OrderBatch(Order order, Batch batch, int count) {
        this.order = order;
        this.batch = batch;
        this.id = new OrderBatchKey(order.getId(), batch.getId());
        this.count = count;
    }

    @Override
    public String toString() {
        return "OrderBatch{" +
                "orderId=" + (order != null ? order.getId() : "null") +
                ", batchId=" + (batch != null ? batch.getId() : "null") +
                ", count=" + count +
                '}';
    }

}
