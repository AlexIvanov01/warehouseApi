package bg.sava.warehouse.api.repository;

import bg.sava.warehouse.api.models.OrderBatch;
import bg.sava.warehouse.api.models.OrderBatchKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBatchRepository extends JpaRepository<OrderBatch, OrderBatchKey> {
}
