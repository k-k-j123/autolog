package com.Kaushik.Autolog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    String token;
    long userId;
}
