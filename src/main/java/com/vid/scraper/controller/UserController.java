package com.vid.scraper.controller;

import com.vid.scraper.model.EditUserRequest;
import com.vid.scraper.model.LoginDto;
import com.vid.scraper.model.Response;
import com.vid.scraper.model.UserResponse;
import com.vid.scraper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> findUserById(@RequestHeader("Authorization") String rawToken) {
        UserResponse response = userService.findUserByToken(rawToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<?> editUser(@RequestHeader("Authorization") String rawToken, @RequestBody EditUserRequest request) {
        LoginDto loginDto = userService.editUser(rawToken, request);
        return ResponseEntity.status(HttpStatus.OK).body(loginDto);
    }
}
