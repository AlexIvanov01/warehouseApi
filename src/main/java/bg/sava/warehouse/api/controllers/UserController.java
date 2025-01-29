package bg.sava.warehouse.api.controllers;

import bg.sava.warehouse.api.models.dtos.UserDto.AuthenticationResponse;
import bg.sava.warehouse.api.models.dtos.UserDto.UserAuthentication;
import bg.sava.warehouse.api.models.dtos.UserDto.UserCreateDto;
import bg.sava.warehouse.api.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final AuthenticationService userService;

    @Autowired
    public UserController(AuthenticationService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody UserCreateDto user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody UserAuthentication user) {
        return userService.verify(user);
    }
}
