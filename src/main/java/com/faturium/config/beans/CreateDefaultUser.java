//package com.faturium.config.beans;
//
//import com.faturium.config.exception.AppException;
//import com.faturium.domain.repository.UserRepository;
//import com.faturium.dto.UserDTO;
//import com.faturium.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//
//import java.util.Collections;
//
//@Slf4j
//@Configuration
//public class CreateDefaultUser {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Bean
//    @Order(2)
//    CommandLineRunner createAdminUser() {
//        return args -> {
//            if (!userRepository.existsByLogin("admin")) {
//                UserDTO adminUser = UserDTO.builder()
//                        .username("admin")
//                        .password("password")
//                        .regras(Collections.singleton("ADMIN"))
//                        .build();
//                try {
//                    userService.salvarUser(adminUser);
//                    log.info("Usuário admin criado com sucesso!");
//                } catch (Exception e) {
//                    throw new AppException("Erro ao criar usuário admin", e.getMessage(), HttpStatus.CONFLICT);
//                }
//            }
//        };
//    }
//
//    @Bean
//    @Order(3)
//    CommandLineRunner createBasicUser() {
//        return args -> {
//            if (!userRepository.existsByLogin("user")) {
//                UserDTO adminUser = UserDTO.builder()
//                        .username("user")
//                        .password("password")
//                        .regras(Collections.singleton("USER"))
//                        .build();
//                try {
//                    userService.salvarUser(adminUser);
//                    log.info("Usuário user criado com sucesso!");
//                } catch (Exception e) {
//                    throw new AppException("Erro ao criar usuário user", e.getMessage(), HttpStatus.CONFLICT);
//                }
//            }
//        };
//    }
//}
