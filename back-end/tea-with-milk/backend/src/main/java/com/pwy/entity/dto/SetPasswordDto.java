package com.pwy.entity.dto;

import lombok.Data;

@Data
public class SetPasswordDto {
    private String originalPassword;

    private String newPassword;
}
