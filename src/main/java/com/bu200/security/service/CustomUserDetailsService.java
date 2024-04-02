package com.bu200.security.service;

import com.bu200.security.dto.CustomUserDetails;
import com.bu200.security.entity.User;
import com.bu200.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //매니저가 UserDetailsService 를 구현한 클래스를 찾아 이 메서드를 실행시킨다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findByAccountId(username);   //유저의 정보를 DB 에서 조회해오고,
        if(userData != null){
            return new CustomUserDetails(userData); //유저가 있다면 내가 만든 CustomUserDetails 의 형태로 return 한다. CustomUserDetails 보러가셈
        }

        return null;
    }
}
