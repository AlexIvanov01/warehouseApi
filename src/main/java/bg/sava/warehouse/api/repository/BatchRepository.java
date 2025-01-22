package bg.sava.warehouse.api.repository;

import bg.sava.warehouse.api.models.Batch;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    Page<Batch> findByProductId(UUID productId, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Batch b SET b.quantity = :quantity WHERE b.id = :id")
    void updateQuantityById(@Param("id") UUID id, @Param("quantity") Integer quantity);

}
