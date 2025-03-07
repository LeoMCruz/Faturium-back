package com.mrbread.domain.model;

import java.util.UUID;

public interface PertenceOrganizacao {
    UUID getId();
    Organizacao getOrganizacao();
    Status getStatus();
}
