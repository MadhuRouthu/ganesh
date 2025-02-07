package com.njpa.jamesORMRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.njpa.jamesORMModel.JobsApplied;

public interface JobsAppliedRepo extends JpaRepository<JobsApplied,Long>{

	List<JobsApplied> findByEmail(String email);

}
