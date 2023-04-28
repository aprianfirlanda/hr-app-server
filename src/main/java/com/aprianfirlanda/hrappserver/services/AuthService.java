package com.aprianfirlanda.hrappserver.services;

import com.aprianfirlanda.hrappserver.domain.dtos.SignInDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignUpDto;
import com.aprianfirlanda.hrappserver.security.services.UserDetailsImpl;

import java.util.UUID;

public interface AuthService {
    void userRegistration(SignUpDto signUpDto);
    UserDetailsImpl userAuthentication(SignInDto signInDto);
    void verifyUserRegistration(UUID token);
}
