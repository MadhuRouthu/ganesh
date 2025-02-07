package com.njpa.jamesService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.njpa.customexceptions.ResourceNotFoundException;
import com.njpa.dto.JobRequirementDTO;
import com.njpa.jamesORMModel.JobRequirement;
import com.njpa.jamesORMModel.JobsApplied;
import com.njpa.jamesORMRepo.JobRequirementRepository;
import com.njpa.jamesORMRepo.JobsAppliedRepo;

@Service
public class JobRequirementService {

	@Autowired
	private JobRequirementRepository repository;

	@Autowired
	private JobRequirementRepository jobRequirementRepository;
	
	@Autowired
	private JobsAppliedRepo jobsAppliedRepo;

	public JobRequirementDTO createJobRequirement(JobRequirement jobRequirement) {
//		String id = generateRandomId();
//		jobRequirement.setId(id);
		// Insert current date using java.sql.Date
        Date currentDate = Date.valueOf(LocalDate.now());
		jobRequirement.setDateSincePosted(currentDate);
		JobRequirement savedJobRequirement = jobRequirementRepository.save(jobRequirement);

		return mapToDTO(savedJobRequirement);
	}

	private JobRequirementDTO mapToDTO(JobRequirement jobRequirement) {
		JobRequirementDTO dto = new JobRequirementDTO();
		dto.setId(jobRequirement.getId());
		dto.setDescription(jobRequirement.getDescription());
		dto.setJd(jobRequirement.getJd());
		return dto;
	}

	private String generateRandomId() {
		Random random = new Random();
		int number = random.nextInt(100000); // Generate a number between 0 and 99999
		return String.format("%05d", number); // Format to five digits with leading zeros
	}

	public List<JobRequirement> getAllJobRequirements() {
		return repository.findAll();
	}

	public JobRequirementDTO updateJobRequirement(Long id, JobRequirementDTO jobRequirementDTO) {
		// Find the existing job requirement by ID
		JobRequirement existingJobRequirement = jobRequirementRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("JobRequirement not found"));

		// Update the fields
		
		existingJobRequirement.setDescription(jobRequirementDTO.getDescription());
		existingJobRequirement.setJd(jobRequirementDTO.getJd());

		// Save the updated job requirement
		JobRequirement updatedJobRequirement = jobRequirementRepository.save(existingJobRequirement);

		// Return the updated DTO
		return mapToDTO(updatedJobRequirement);
	}

	public void deleteJobRequirement(Long id) {
		JobRequirement jobRequirement = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Job Requirement not found"));
		repository.delete(jobRequirement);
	}

	public JobsApplied saveAppliedJob(JobsApplied jobRequirement) {
		// TODO Auto-generated method stub
		JobsApplied save = jobsAppliedRepo.save(jobRequirement);
		if(save!=null) {
			return save;
		}else {
		return null;
		}
	}

	public List<JobsApplied> getAppliedJob(String email) {
		// TODO Auto-generated method stub
		List<JobsApplied> byEmail = jobsAppliedRepo.findByEmail(email);
		if(byEmail!=null) {
			return byEmail;
		}else {
			return null;
		}
	}
}