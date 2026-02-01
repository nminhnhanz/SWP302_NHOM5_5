package com.fpt.glassesshop.security;

import com.fpt.glassesshop.entity.UserAccount;
import com.fpt.glassesshop.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        boolean enabled = "ACTIVE".equalsIgnoreCase(userAccount.getAccountStatus());

        return new User(
                userAccount.getEmail(),
                userAccount.getPasswordHash(),
                enabled, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole())));
    }
}
