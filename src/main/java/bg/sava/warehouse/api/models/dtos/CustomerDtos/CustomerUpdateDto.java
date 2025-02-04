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
public class CustomerUpdateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String companyName;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    @NotBlank
    private String bankName;
    @NotBlank
    private String iban;
    @NotBlank
    private String bic;
    @NotBlank
    private String vatNumber;
    @NotBlank
    private String uic;
}
