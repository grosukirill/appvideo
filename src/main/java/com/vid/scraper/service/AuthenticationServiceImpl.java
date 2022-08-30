package com.vid.scraper.service;

import com.vid.scraper.exception.AuthenticationException;
import com.vid.scraper.exception.EmailTakenException;
import com.vid.scraper.exception.MatchingPasswordException;
import com.vid.scraper.exception.UserNotFoundException;
import com.vid.scraper.model.*;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailTakenException(String.format("Email [%s] is already taken!", request.getEmail()));
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .build());
        String token = tokenService.generate(user);
        return new LoginResponse(true, LoginDto.from(user, token));
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            return new LoginResponse(true, LoginDto.from(user, tokenService.generate(user)));
        } else {
            throw new AuthenticationException("Could not authenticate this user!");
        }
    }

    @Override
    public void changePassword(String rawToken, ChangePasswordRequest request) {
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new MatchingPasswordException("Passwords cannot be the same!");
        }
        User user = getUserFromToken(rawToken);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong old password");
        }
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    private User getUserFromToken(String rawToken) {
        Map<String, String> userData = tokenService.getUserDataFromToken(rawToken);
        Optional<User> user = userRepository.findByEmail(userData.get("email"));
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User with token [%s] not found", rawToken));
        }
        return user.get();
    }
}
