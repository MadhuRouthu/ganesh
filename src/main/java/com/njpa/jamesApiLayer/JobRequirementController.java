package com.njpa.jamesApiLayer;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.njpa.dto.JobRequirementDTO;
import com.njpa.dto.ResumeSummary;
import com.njpa.jamesORMModel.JobRequirement;
import com.njpa.jamesORMModel.JobsApplied;
import com.njpa.jamesORMModel.Resume;
import com.njpa.jamesService.JobRequirementService;
import com.njpa.jamesService.NjpaService;
import com.njpa.resume.parser.NgsResumeParser;

@RestController
@CrossOrigin
public class JobRequirementController {

	@Autowired
    private JobRequirementService service;
	
	@Autowired
	private NjpaService njpaService;

	// Create a new Job Requirement and return only the ID
    @PostMapping("/parse/resume")
    public ResponseEntity<ResumeSummary> parseResume(@RequestParam(name = "email") String email) {
        try {
        	Resume resume = njpaService.getResume(email);
            ResumeSummary summary = NgsResumeParser.parseResume(resume);
            System.out.println(summary);
            return ResponseEntity.ok(summary);
        } catch (IOException e) {
            // Handle the exception as needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Create a new Job Requirement and return only the ID
    @PostMapping("/post/job-requirements")
    public ResponseEntity<?> createJobRequirement(@RequestBody JobRequirement jobRequirement) {
        JobRequirementDTO createdJob = service.createJobRequirement(jobRequirement);
        Map<String, Long> response = new HashMap<>();
        response.put("id", createdJob.getId());
        return ResponseEntity.ok(response);
    }

    // Get all Job Requirements
    @GetMapping("/get/job-requirements")
    public ResponseEntity<?> getAllJobRequirements() {
        return ResponseEntity.ok(service.getAllJobRequirements());
    }

    // Update an existing Job Requirement by ID and return only the ID
    @PutMapping("/update/job-requirements/{id}")
    public ResponseEntity<?> updateJobRequirement(
            @PathVariable Long id, 
            @RequestBody JobRequirementDTO jobRequirementDTO) {
        JobRequirementDTO updatedJobRequirement = service.updateJobRequirement(id, jobRequirementDTO);
        return ResponseEntity.ok(updatedJobRequirement);
    }

    // Delete a Job Requirement by ID
    @DeleteMapping("/delete/job-requirements/{id}")
    public ResponseEntity<?> deleteJobRequirement(@PathVariable Long id) {
        service.deleteJobRequirement(id);
        return ResponseEntity.noContent().build();
    }
    
 // Create a new Job Requirement and return only the ID
    @PostMapping("/save/job-applied")
    public ResponseEntity<?> saveAppliedJob(@RequestBody JobsApplied jobRequirement) {
        JobsApplied createdJob = service.saveAppliedJob(jobRequirement);
        return ResponseEntity.ok(createdJob);
    }
    
    // Get all Job Applied
    @GetMapping("/get/all-jobApplied")
    public ResponseEntity<?> getAllJobApplied(@RequestParam String email) {
    	List<JobsApplied> appliedJob = service.getAppliedJob(email);
        return ResponseEntity.ok(appliedJob);
    }
}