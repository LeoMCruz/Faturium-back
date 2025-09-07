package com.faturium.rest;

import com.faturium.dto.AuthenticationRequest;
import com.faturium.dto.AuthenticationResponse;
import com.faturium.dto.GoogleAuthRequest;
import com.faturium.dto.UserDTO;
import com.faturium.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/login/google", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthenticationResponse> autenticarGoogle(@RequestBody GoogleAuthRequest googleAuthRequest){
        return ResponseEntity.ok().body(authenticationService.verifyGoogleAccount(googleAuthRequest));
    }

    @PostMapping(value = "/login/linkgoogle", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthenticationResponse> linkarContas(@RequestBody GoogleAuthRequest googleAuthRequest){
        return ResponseEntity.ok().body(authenticationService.linkGoogleId(googleAuthRequest));
    }

    @PostMapping(value = "/login/complete-profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> completeProfile(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.completeRegister(userDTO));
    }
}
