package com.tas.condopilot.residents.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResidentResponse {
	private Long id;
	private String name;
	private String email;
	private String phone;
	private Long unitId;
	private String unitNumber;
	private String block;
	private LocalDateTime createdAt;
}