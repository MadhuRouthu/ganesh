package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.njpa.jamesORMModel.Admin;

public interface AdminRepo extends JpaRepository<Admin, Long> {
	Optional<Admin> findByEmail(String emailID);
	long count();
}
