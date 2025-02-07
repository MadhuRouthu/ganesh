package com.njpa.dto;

import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.ProjectDetails;
import com.njpa.jamesORMModel.Register;
import com.njpa.jamesORMModel.Resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateData {
private CandidateDetails candidateDetails;
private Experience experience;
private Resume resume;
private ProjectDetails projectDetails;
private Register register;
}
