package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.TALogin;

@Repository
public interface TALoginRepo extends JpaRepository<TALogin, Long> {
	Optional<TALogin> findByEmail(String email);

	void deleteByEmail(String empId);
}
