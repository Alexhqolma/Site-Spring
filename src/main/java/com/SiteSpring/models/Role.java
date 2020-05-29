package com.SiteSpring.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    OWNER,
    VIP;

    @Override
    public String getAuthority() {
        return name();
    }
}
