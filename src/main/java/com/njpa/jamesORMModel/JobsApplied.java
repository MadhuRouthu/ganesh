package com.njpa.jamesORMModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsApplied {
	@Id
	@Column(updatable = false, nullable = false)
	private Long id;

	private String jobApplied;
	private String email;
}
