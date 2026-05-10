package com.tas.condopilot.residents.entity;

import com.tas.condopilot.common.entity.BaseEntity;
import com.tas.condopilot.units.entity.UnitEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "residents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(length = 30)
	private String phone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id", nullable = false)
	private UnitEntity unit;
}