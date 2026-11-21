package com.campuslands.coworkhub.business.service;

import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;

import java.util.Optional;

public interface UserAccountService {

    Optional<UserAccountEntity> authenticate(String username, String password);

    Optional<UserAccountEntity> findByUsername(String username);
}