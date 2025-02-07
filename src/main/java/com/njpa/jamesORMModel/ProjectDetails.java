package com.njpa.jamesORMModel;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor

public class ProjectDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long project_id;
	private String projectName;
	private String client;
	private String projectStatus;
	private String workedFromYear;
	private String workedFromMonth;
	private String projectDetails;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "registration_id")
	private Register register;

}