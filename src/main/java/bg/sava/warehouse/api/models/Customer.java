package bg.sava.warehouse.api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CUSTOMER")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String companyName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;
    private String bankName;
    private String iban;
    private String bic;
    private String vatNumber;
    private String uic;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.DETACH)
    private List<Order> orders;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", orders=" + (orders != null ? orders.size() : 0) +
                '}';
    }


}

