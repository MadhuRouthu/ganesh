package com.njpa.dto;

import java.util.List;

import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.Register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllCandidatesData {
	private List<Register> all;
	private List<CandidateDetails> all2;
	private List<Experience> all4;
}
