package com.tas.condopilot.condos.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CondoResponse {

	private Long id;
	private String name;
	private LocalDateTime createdAt;
}
