package com.njpa.dto;

import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.ProjectDetails;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class LoginResponse {
private CandidateDetails candidateDetails;
private ProjectDetails projectDetails;
private Experience experience;
}
