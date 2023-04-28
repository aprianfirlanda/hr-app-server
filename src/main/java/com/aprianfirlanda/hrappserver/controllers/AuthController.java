package com.aprianfirlanda.hrappserver.controllers;

import com.aprianfirlanda.hrappserver.domain.dtos.ResponseDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignInDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignUpDto;
import com.aprianfirlanda.hrappserver.domain.dtos.UserInfoDto;
import com.aprianfirlanda.hrappserver.security.jwt.JwtUtils;
import com.aprianfirlanda.hrappserver.security.services.UserDetailsImpl;
import com.aprianfirlanda.hrappserver.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInDto signInDto) {
        UserDetailsImpl userDetails = authService.userAuthentication(signInDto);

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoDto(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));
    }

    @PostMapping("/sign-up")
    public ResponseDto registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        authService.userRegistration(signUpDto);

        return new ResponseDto("User registered successfully!");
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ResponseDto("You've been signed out!"));
    }

    @GetMapping("/verify-register")
    public ResponseDto verifyUserRegistration(
            @RequestParam String token
    ) {
        authService.verifyUserRegistration(UUID.fromString(token));

        return new ResponseDto("User registered successfully!");
    }
}
