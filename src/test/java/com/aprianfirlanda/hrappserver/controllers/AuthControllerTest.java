package com.aprianfirlanda.hrappserver.controllers;

import com.aprianfirlanda.hrappserver.HrAppServerApplication;
import com.aprianfirlanda.hrappserver.domain.dtos.SignInDto;
import com.aprianfirlanda.hrappserver.domain.dtos.SignUpDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HrAppServerApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = "user-register-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void registerUser() throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername("test-user");
        signUpDto.setEmail("test.user@gmail.com");
        signUpDto.setPassword("password");
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_USER");
        signUpDto.setRoles(roleSet);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andDo(print());
    }

    @Test
    @Sql(scripts = "user-register-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "user-register-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void authenticateUser() throws Exception {
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("test-user");
        signInDto.setPassword("password");

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDto)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("hrapp"))
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.username").value(signInDto.getUsername()))
                .andExpect(jsonPath("$.email").value("test.user@email.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andDo(print());
    }

    @Test
    @Order(3)
    void logoutUser() throws Exception {
        Cookie cookie = new Cookie("customData", "customString");
        mockMvc.perform(post("/auth/sign-out")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().value("hrapp", ""))
                .andDo(print());
    }
}