package com.mrbread.rest;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.User;
import com.mrbread.dto.OrgUserDTO;
import com.mrbread.dto.UserDTO;
import com.mrbread.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createuser(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.salvarUser(userDTO));
    }

    @PostMapping(value = "/user/colaborador", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEmploye(@RequestBody UserDTO userDTO){
        if(!SecurityUtils.isAdmin()){
            throw new AppException("Operação não permitida",
                    "O usuário não possui permissão para criar novos usuarios na organização",
                    HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.salvarColaborador(userDTO));
    }

    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<OrgUserDTO> getAuthenticatedUser(){
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping(value = "/user/colaborador", produces = "application/json")
    public ResponseEntity<List<OrgUserDTO>> getAllOrgUsers(@PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable){
        if(!SecurityUtils.isAdmin() && SecurityUtils.isManager()){
            throw new AppException("Operação não permitida",
                    "O usuário não possui permissão para acessar os dados dos usuários da organização",
                    HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(userService.getAllOrganizationUsers());
    }
}

