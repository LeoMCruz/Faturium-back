package com.mrbread.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthenticationResponse {
    private String accessToken;
    private String errorMessage;
    private Boolean profileComplete;
}
