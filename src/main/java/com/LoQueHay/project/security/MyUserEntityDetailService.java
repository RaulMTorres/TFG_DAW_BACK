package com.LoQueHay.project.security;

import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.repository.MyUserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserEntityDetailService implements UserDetailsService {

    private final MyUserEntityRepository myUserEntityRepository;

    public MyUserEntityDetailService(MyUserEntityRepository myUserEntityRepository) {
        this.myUserEntityRepository = myUserEntityRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUserEntity user = myUserEntityRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return new MyUserEntityDetails(user);
    }
}
