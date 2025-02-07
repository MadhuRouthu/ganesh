package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.JobRequirement;

@Repository
public interface JobRequirementRepository extends JpaRepository<JobRequirement, Long> {

	Optional<JobRequirement> findById(Long id);
}
