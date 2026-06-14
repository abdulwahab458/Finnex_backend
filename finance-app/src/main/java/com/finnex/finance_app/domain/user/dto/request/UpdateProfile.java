package com.finnex.finance_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfile {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
