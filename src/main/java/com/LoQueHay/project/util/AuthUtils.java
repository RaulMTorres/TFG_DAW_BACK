// AuthUtils.java
package com.LoQueHay.project.util;

import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.repository.MyUserEntityRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private final MyUserEntityRepository userRepository;

    public AuthUtils(MyUserEntityRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MyUserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // el username viene del token
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
