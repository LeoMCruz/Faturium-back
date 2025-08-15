package com.mrbread.rest;

import com.mrbread.dto.AuthenticationRequest;
import com.mrbread.dto.AuthenticationResponse;
import com.mrbread.dto.GoogleAuthRequest;
import com.mrbread.dto.UserDTO;
import com.mrbread.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
