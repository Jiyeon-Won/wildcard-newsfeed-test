package com.sparta.wildcard_newsfeed.security;

import com.sparta.wildcard_newsfeed.domain.user.entity.User;
import com.sparta.wildcard_newsfeed.domain.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthenticationUser implements UserDetails {

    /**
     * User Entity usercode
     */
    private final String usercode;
    /**
     * User Entity password
     */
    private final String password;
    /**
     * User Entity userRole
     */
    @Getter
    private UserRoleEnum userRoleEnum;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.usercode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRoleEnum == null) {
            throw new IllegalStateException("UserRoleEnum is null");
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRoleEnum.getRoleValue()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public static AuthenticationUser of(User user) {
        return AuthenticationUser.builder()
                .usercode(user.getUsercode())
                .password(user.getPassword())
                .userRoleEnum(user.getUserRoleEnum())
                .build();
    }
}