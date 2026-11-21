package com.campuslands.coworkhub.business.impl;

import com.campuslands.coworkhub.business.service.UserAccountService;
import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import com.campuslands.coworkhub.persistence.repository.UserAccountJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountJpaRepository accounts;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccountEntity> authenticate(String username, String password) {
        Optional<UserAccountEntity> account = accounts.findByUsername(username);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(password, account.get().getPassword())) {
            return Optional.empty();
        }
        return account;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccountEntity> findByUsername(String username) {
        return accounts.findByUsername(username);
    }
}
