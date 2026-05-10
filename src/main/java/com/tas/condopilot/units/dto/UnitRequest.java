package com.tas.condopilot.units.dto;

import com.tas.condopilot.common.enums.UnitStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnitRequest {

	@NotBlank(message = "Número é obrigatório")
	private String number;

	@NotBlank(message = "Bloco é obrigatório")
	private String block;

	@NotNull(message = "Status é obrigatório")
	private UnitStatus status;

	@NotNull(message = "Condomínio é obrigatório")
	private Long condoId;
}