package com.aprianfirlanda.hrappserver.services.impl;

import com.aprianfirlanda.hrappserver.domain.dtos.SignInDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignUpDto;
import com.aprianfirlanda.hrappserver.domain.models.ERole;
import com.aprianfirlanda.hrappserver.domain.models.Role;
import com.aprianfirlanda.hrappserver.domain.models.User;
import com.aprianfirlanda.hrappserver.repositories.RoleRepository;
import com.aprianfirlanda.hrappserver.repositories.UserRepository;
import com.aprianfirlanda.hrappserver.security.services.UserDetailsImpl;
import com.aprianfirlanda.hrappserver.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public void userRegistration(SignUpDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                signUpDto.getUsername(),
                signUpDto.getEmail(),
                encoder.encode(signUpDto.getPassword())
        );

        Set<String> strRoles = signUpDto.getRoles();
        Set<Role> roles = new HashSet<>();

        final String ROLE_NOT_FOUND = "Error: Role is not found";
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public UserDetailsImpl userAuthentication(SignInDto signInDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDto.getUsername(),
                        signInDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
