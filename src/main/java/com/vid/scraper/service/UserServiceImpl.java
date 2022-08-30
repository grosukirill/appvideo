package com.vid.scraper.service;

import com.vid.scraper.exception.UserNotFoundException;
import com.vid.scraper.model.EditUserRequest;
import com.vid.scraper.model.LoginDto;
import com.vid.scraper.model.entity.User;
import com.vid.scraper.model.UserDto;
import com.vid.scraper.model.UserResponse;
import com.vid.scraper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
    public UserResponse findUserByToken(String rawToken) {
        User user =  getUserFromToken(rawToken);
        UserDto userDto = UserDto.from(user);
        return new UserResponse(true, userDto);
    }

    @Override
    public LoginDto editUser(String rawToken, EditUserRequest request) {
        User user = getUserFromToken(rawToken);
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        User updateUser = userRepository.save(user);
        String newToken = tokenService.generate(updateUser);
        return LoginDto.from(updateUser, newToken);
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
