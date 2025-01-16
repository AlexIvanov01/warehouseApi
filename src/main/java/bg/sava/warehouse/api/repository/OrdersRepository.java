package bg.sava.warehouse.api.repository;

import bg.sava.warehouse.api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<Customer, UUID> {
}
