package com.sanawulai.recruitmentportal.services;

import com.sanawulai.recruitmentportal.dto.request.AuthRequest;
import com.sanawulai.recruitmentportal.dto.response.AuthResponse;
import com.sanawulai.recruitmentportal.dto.response.RegisterRequest;
import com.sanawulai.recruitmentportal.entity.User;
import com.sanawulai.recruitmentportal.enums.Role;
import com.sanawulai.recruitmentportal.repository.UserRepository;
import com.sanawulai.recruitmentportal.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    //in-memory token blacklist---- i shall use redis for this purpose later
    private Set<String> tokenBlacklist = new HashSet<>();

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token,user.getEmail(),user.getFirstName(),user.getLastName());
    }

    public AuthResponse authenticate(AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("User not found"));
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token,user.getEmail(),user.getFirstName(),user.getLastName());
    }
    public void logout(String token){
        if(token!=null &&token.startsWith("Bearer ")){
            String jwt = token.substring(7);
            tokenBlacklist.add(jwt);
        }
    }
    public boolean isTokenBlacklisted(String token){
        return tokenBlacklist.contains(token);
    }
}
