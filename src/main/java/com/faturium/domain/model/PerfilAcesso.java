package com.faturium.domain.model;

import lombok.Getter;

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
