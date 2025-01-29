package bg.sava.warehouse.api.models.dtos.UserDto;

import bg.sava.warehouse.api.models.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private Role role;
}

