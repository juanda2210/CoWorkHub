package com.campuslands.coworkhub.security;

import com.campuslands.coworkhub.business.service.UserAccountService;
import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserAccountDetailsService implements UserDetailsService {

    private final UserAccountService accounts;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountEntity account = accounts.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return AuthenticatedUser.from(account);
    }
}
