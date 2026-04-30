package com.umudugudu.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO: Inject UserRepository and load real User entity.
 * This stub compiles and lets Spring Boot start cleanly.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + phoneNumber)
                );

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                "", // no password for OTP login
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
