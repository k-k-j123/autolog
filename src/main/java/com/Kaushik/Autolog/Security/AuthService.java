package com.Kaushik.Autolog.Security;

import com.Kaushik.Autolog.DTO.LoginRequestDto;
import com.Kaushik.Autolog.DTO.LoginResponseDto;
import com.Kaushik.Autolog.DTO.SignupResponseDto;
import com.Kaushik.Autolog.Entity.Users;
import com.Kaushik.Autolog.Repository.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtils authUtils;
    private final UsersRepo userRepository;

    /*
    here we call the authenticate manager to authenticate the user and pass it
    username password authentication token with the email and password from the
    login request dto. On successful auth we get the email from the authentication object and use it to fetch the user
    from the database and then generate a jwt token for the user using the auth utils and return it in the login response
    dto along with the user id
     */
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        //the core method that does the authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );


        String email = authentication.getName();
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + email));

        //create a jwt token for the user using auth utils
        String token = authUtils.generateAccessToken(user);
        return new LoginResponseDto(token, user.getId());

    }

    public SignupResponseDto signup(LoginRequestDto signupRequestDto) {
        Optional<Users> user = userRepository.findByEmail(signupRequestDto.getEmail());
        if (user.isPresent()) {
            throw new RuntimeException("User already exists with email: " + signupRequestDto.getEmail());
        }

        Users newUser = new Users();
        newUser.setEmail(signupRequestDto.getEmail());
        newUser.setPassword(new BCryptPasswordEncoder().encode(signupRequestDto.getPassword()));
        Users savedUser = userRepository.save(newUser);
        return new SignupResponseDto(savedUser.getId(), savedUser.getEmail());
    }
}
