package com.njpa.dto;

import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.Register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateExperienceDTO {
	private CandidateDetails candidateDetails;
    private Experience experience;
    private Register register;
}
