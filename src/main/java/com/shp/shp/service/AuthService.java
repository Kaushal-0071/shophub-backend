package com.shp.shp.service;


import com.shp.shp.dto.SigninRequest;
import com.shp.shp.entity.UserInfo;
import com.shp.shp.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String addUser(SigninRequest signinRequest) {
        UserInfo user = new UserInfo();
        user.setEmail(signinRequest.getEmail());
        user.setName(signinRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signinRequest.getPassword()));
        user.setRoles(signinRequest.getRoles());

        repository.save(user);
        return "user added to system ";
    }
}
