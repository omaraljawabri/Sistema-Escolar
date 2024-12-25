package com.sistema_escolar.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PublishProvaRequestDTO {
    public Integer expirationHours;
    public Integer expirationMinutes;
}
