package bg.sava.warehouse.api.models.dtos.CustomerDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreateDto {
    @NotBlank
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
