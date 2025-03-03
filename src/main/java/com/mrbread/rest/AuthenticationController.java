package com.mrbread.rest;

import com.mrbread.dto.AuthenticationRequest;
import com.mrbread.dto.AuthenticationResponse;
import com.mrbread.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    //faz login na aplicacao, retornando o token
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthenticationResponse> autenticar(@RequestBody AuthenticationRequest oauthRequest){
        return ResponseEntity.ok().body(authenticationService.generateToken(oauthRequest));
    }
}
