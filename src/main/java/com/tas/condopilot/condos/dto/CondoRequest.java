package com.tas.condopilot.condos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CondoRequest {

	@NotBlank(message = "O nome é obrigatório")
	private String name;
}
