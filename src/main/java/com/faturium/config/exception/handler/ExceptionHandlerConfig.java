package com.faturium.config.exception.handler;

import com.faturium.config.exception.AppException;
import com.faturium.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class ExceptionHandlerConfig {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDTO> appException(AppException appException){
        var errorDto = new ErrorDTO(appException.getTitle(),
                appException.getDescription());
        return ResponseEntity.status(appException.getStatusCode())
                .body(errorDto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex) {
        AppException appException = new AppException(
                "Operação não permitida",
                "O usuário não possui permissão para acessar este recurso",
                HttpStatus.FORBIDDEN
        );

        return appException(appException);
    }

}
