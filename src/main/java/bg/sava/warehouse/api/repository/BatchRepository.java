package bg.sava.warehouse.api.repository;

import bg.sava.warehouse.api.models.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    Page<Batch> findByProductId(UUID productId, Pageable pageable);
}
