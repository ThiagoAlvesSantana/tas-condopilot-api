package com.tas.condopilot.auth.dto;

import com.tas.condopilot.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

	@Email(message = "E-mail inválido")
	@NotBlank(message = "E-mail é obrigatório")
	private String email;

	@NotBlank(message = "Senha é obrigatória")
	private String password;

	@NotNull(message = "Role é obrigatória")
	private Role role;
}