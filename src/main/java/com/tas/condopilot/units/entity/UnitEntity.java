package com.tas.condopilot.units.entity;

import com.tas.condopilot.common.entity.BaseEntity;
import com.tas.condopilot.common.enums.UnitStatus;
import com.tas.condopilot.condos.entity.CondoEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20)
	private String number;

	@Column(nullable = false, length = 20)
	private String block;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UnitStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "condo_id", nullable = false)
	private CondoEntity condo;
}