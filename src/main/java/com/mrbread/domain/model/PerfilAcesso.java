package com.mrbread.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PerfilAcesso {
    ADMIN("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_DEFAULT"),
    MANAGER("ROLE_MANAGER", "ROLE_DEFAULT"),
    DEFAULT("ROLE_DEFAULT");

    PerfilAcesso(String ... roleName){
        this.roles = Arrays.asList(roleName);
    }
    private final List<String> roles;
}
