package com.aprianfirlanda.hrappserver.controllers;

import com.aprianfirlanda.hrappserver.HrAppServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HrAppServerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_USER"})
    void allContentWithRoleUser() throws Exception {
        mockMvc.perform(get("/api/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Public Content."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_ADMIN"})
    void allContentWithRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Public Content."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_USER"})
    void userContentWithRoleUser() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User Content."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_ADMIN"})
    void userContentWithRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User Content."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_ADMIN"})
    void adminContentWithRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Admin Board."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test-user", authorities = {"ROLE_USER"})
    void adminContentWithRoleUser() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}