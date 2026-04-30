//package com.umudugudu.security;
//
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///**
// * TODO: Inject UserRepository and load real User entity.
// * This stub compiles and lets Spring Boot start cleanly.
// */
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Override
//    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
//        // TODO: replace with:
//        //   User user = userRepository.findByPhoneNumber(phoneNumber)
//        //       .orElseThrow(() -> new UsernameNotFoundException("User not found: " + phoneNumber));
//        //   return new org.springframework.security.core.userdetails.User(
//        //       user.getPhoneNumber(), "", List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
//        throw new UsernameNotFoundException("UserRepository not yet wired — implement this service.");
//    }
//}
package com.umudugudu.security;

import com.umudugudu.entity.User;
import com.umudugudu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseGet(() ->
                        userRepository.findByPhoneNumber(username)
                                .orElseThrow(() ->
                                        new UsernameNotFoundException("User not found: " + username)
                                )
                );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                user.getPassword() != null ? user.getPassword() : "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}

