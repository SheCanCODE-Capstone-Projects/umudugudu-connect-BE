package com.umudugudu.security;

import com.umudugudu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.umudugudu.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhoneNumber(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );
        if (user.getRole() == null) {
            throw new RuntimeException("User role is null for user: " + user.getPhoneNumber());
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                user.getPassword() != null ? user.getPassword() : "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
