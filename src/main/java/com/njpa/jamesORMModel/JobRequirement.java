package com.njpa.jamesORMModel;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequirement {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_requirement_seq_generator")
    @SequenceGenerator(name = "job_requirement_seq_generator", sequenceName = "risk_assessment_seq", initialValue = 10000, allocationSize = 1)
	private Long id;
    
	private String description;
	@Column(length = 100000)
	private String jd;
	
	private String client;
	
	private Date dateSincePosted;
}
