package com.njpa.jamesORMRepo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.njpa.jamesORMModel.Register;

@Repository
public interface RegisterRepo extends JpaRepository<Register, Long> {
	Optional<Register> findByEmailID(String emailID);
	Optional<Register> findByOtp(String otp);

}
