package bg.sava.warehouse.api.services;

import bg.sava.warehouse.api.models.User;
import bg.sava.warehouse.api.models.dtos.UserDto.AuthenticationResponse;
import bg.sava.warehouse.api.models.dtos.UserDto.UserAuthentication;
import bg.sava.warehouse.api.models.dtos.UserDto.UserCreateDto;
import bg.sava.warehouse.api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    public AuthenticationService(UserRepository userRepo, ModelMapper modelMapper, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(UserCreateDto user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(modelMapper.map(user, User.class));
        return new AuthenticationResponse(jwtService.generateToken(user.getUsername()));
    }

    public AuthenticationResponse verify(UserAuthentication user) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated())
            return new AuthenticationResponse(jwtService.generateToken(user.getUsername()));

        throw new BadCredentialsException(user.getUsername());
    }
}
