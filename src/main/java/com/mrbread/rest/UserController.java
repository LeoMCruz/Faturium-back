package com.mrbread.rest;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.dto.OrgUserDTO;
import com.mrbread.dto.UserDTO;
import com.mrbread.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createuser(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.salvarUser(userDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/user/colaborador", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createEmploye(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.salvarColaborador(userDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/user", produces = "application/json")
    public ResponseEntity<OrgUserDTO> getAuthenticatedUser(){
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping(value = "/user/colaborador", produces = "application/json")
    public ResponseEntity<List<OrgUserDTO>> getAllOrgUsers(@PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(userService.getAllOrganizationUsers(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @PutMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDTO));
    }
}

