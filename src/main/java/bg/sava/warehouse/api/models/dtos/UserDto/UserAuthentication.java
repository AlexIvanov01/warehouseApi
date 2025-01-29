package bg.sava.warehouse.api.models.dtos.UserDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserAuthentication {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
