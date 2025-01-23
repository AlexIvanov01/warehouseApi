package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "INVOICE")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen")
    @SequenceGenerator(name = "mySeqGen", sequenceName = "my_sequence_name", allocationSize = 1)
    private long id;
    private LocalDate invoiceDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String invoiceStatus;
    private Double totalAmount;


    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceDate=" + invoiceDate +
                ", order=" + (order != null ? order.getId() : "null") +
                ", invoiceStatus='" + invoiceStatus + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
