package com.aprianfirlanda.hrappserver.controllers;

import com.aprianfirlanda.hrappserver.domain.dtos.ResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

public class TestController {
    @GetMapping("/all")
    public ResponseDto allAccess() {
        return new ResponseDto("Public Content.");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseDto userAccess() {
        return new ResponseDto("User Content.");
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto adminAccess() {
        return new ResponseDto("Admin Board.");
    }
}
