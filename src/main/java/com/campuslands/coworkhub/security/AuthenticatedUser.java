package com.campuslands.coworkhub.security;

import com.campuslands.coworkhub.persistence.entity.UserAccountEntity;
import com.campuslands.coworkhub.persistence.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final String username;
    private final String password;
    private final Role role;
    private final Long memberId;

    public AuthenticatedUser(String username, String password, Role role, Long memberId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.memberId = memberId;
    }

    public static AuthenticatedUser from(UserAccountEntity account) {
        Long memberId = account.getMember() == null ? null : account.getMember().getId();
        return new AuthenticatedUser(
                account.getUsername(), account.getPassword(), account.getRole(), memberId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
