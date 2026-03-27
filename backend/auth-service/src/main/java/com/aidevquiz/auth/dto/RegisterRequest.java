package com.aidevquiz.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@JsonAlias("username") @NotBlank String name, @Email String email, @Size(min = 8) String password) {
}
