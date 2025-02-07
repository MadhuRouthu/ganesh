package com.njpa.jamesORMRepo;

import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Register;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateDetailsRepo extends JpaRepository<CandidateDetails, Long> {
Optional<CandidateDetails> findByRegister_Registrationid(Long registrationId);

CandidateDetails findByRegister(Register register);
}
