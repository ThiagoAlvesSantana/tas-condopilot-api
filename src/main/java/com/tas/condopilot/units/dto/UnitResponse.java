package com.tas.condopilot.units.dto;

import com.tas.condopilot.common.enums.UnitStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UnitResponse {
	private Long id;
	private String number;
	private String block;
	private UnitStatus status;
	private Long condoId;
	private String condoName;
	private LocalDateTime createdAt;
}