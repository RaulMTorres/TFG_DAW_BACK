package com.LoQueHay.project.security;

import com.LoQueHay.project.model.MyUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MyUserEntityDetails implements UserDetails {

    private final MyUserEntity user;

    public MyUserEntityDetails(MyUserEntity user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


    //Spring Security espera True si la cuenta no esta bloqueada
    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    //De momento las credenciales nunca expiran
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //De momento las cuentas nunca expiran
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
