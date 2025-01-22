package bg.sava.warehouse.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class OrderBatchKey implements Serializable {

    @Column(name = "order_id")
    private UUID orderId;
    @Column(name = "batch_id")
    private UUID batchId;

    @Override
    public int hashCode() {
        return Objects.hash(orderId, batchId);
    }

    @Override
    public boolean equals (Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBatchKey that = (OrderBatchKey) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(batchId, that.batchId);
    }
}

