package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.Register;
import com.njpa.jamesORMModel.Resume;

@Repository
public interface ResumeRepo extends JpaRepository<Resume, Long> {
	Optional<Resume> findByRegister_Registrationid(Long registrationId);

	boolean existsByRegister(Register register);

	Resume findByRegister(Register register);
}
