package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.ProjectDetails;
import com.njpa.jamesORMModel.Register;

@Repository
public interface ProjectDetailsRepo extends JpaRepository<ProjectDetails, Long> {
	Optional<ProjectDetails> findByRegister_Registrationid(Long registrationId);

	boolean existsByRegister(Register register);

	ProjectDetails findByRegister(Register register);
}
