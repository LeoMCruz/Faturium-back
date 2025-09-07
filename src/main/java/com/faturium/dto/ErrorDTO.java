package com.faturium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorDTO {
    private String title;
    private String description;
}
