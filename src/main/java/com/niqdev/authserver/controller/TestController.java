package com.niqdev.authserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/some-secure-api")
public class TestController {

    @GetMapping
    public ResponseEntity<String> getSecureData() {
        return ResponseEntity.ok("Secure Data");
    }
}
