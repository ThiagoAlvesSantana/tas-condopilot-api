package com.tas.condopilot.residents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResidentRequest {

	@NotBlank(message = "Nome é obrigatório")
	private String name;

	@Email(message = "E-mail inválido")
	@NotBlank(message = "E-mail é obrigatório")
	private String email;

	private String phone;

	@NotNull(message = "unitId é obrigatório")
	private Long unitId;
}