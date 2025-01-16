package bg.sava.warehouse.api.models.dtos;

import jakarta.annotation.sql.DataSourceDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReadDto {
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
}
