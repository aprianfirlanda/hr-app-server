package com.aprianfirlanda.hrappserver.controllers;

import com.aprianfirlanda.hrappserver.domain.dtos.LoginDto;
import com.aprianfirlanda.hrappserver.domain.dtos.ResponseDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignUpDto;
import com.aprianfirlanda.hrappserver.domain.dtos.UserInfoDto;
import com.aprianfirlanda.hrappserver.domain.models.ERole;
import com.aprianfirlanda.hrappserver.domain.models.Role;
import com.aprianfirlanda.hrappserver.domain.models.User;
import com.aprianfirlanda.hrappserver.repositories.RoleRepository;
import com.aprianfirlanda.hrappserver.repositories.UserRepository;
import com.aprianfirlanda.hrappserver.security.jwt.JwtUtils;
import com.aprianfirlanda.hrappserver.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return ResponseEntity.badRequest().body(new ResponseDto("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return ResponseEntity.badRequest().body(new ResponseDto("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpDto.getUsername(),
                signUpDto.getEmail(),
                encoder.encode(signUpDto.getPassword()));

        Set<String> strRoles = signUpDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new ResponseDto("User registered successfully!"));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ResponseDto("You've been signed out!"));
    }
}
