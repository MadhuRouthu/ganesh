package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.Register;

@Repository
public interface ExperienceRepo extends JpaRepository<Experience, Long> {
	Optional<Experience> findByRegister_Registrationid(Long registrationId);

	boolean existsByRegister(Register register);

	Experience findByRegister(Register register);
}
