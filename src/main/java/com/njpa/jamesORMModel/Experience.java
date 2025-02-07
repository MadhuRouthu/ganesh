package com.njpa.jamesORMModel;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Experience {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long experienceId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "registration_id")
	private Register register;
	private List<String> skillName;
	private List<String> experienceYears;
	private String workingStatus;
	private String role;

}