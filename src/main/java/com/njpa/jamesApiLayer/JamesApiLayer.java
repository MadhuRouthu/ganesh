package com.njpa.jamesApiLayer;

import java.io.IOException;
import java.util.Base64;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.njpa.dto.BasicDataDto;
import com.njpa.dto.CandidateData;
import com.njpa.dto.LoginDto;
import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.ProjectDetails;
import com.njpa.jamesORMModel.Register;
import com.njpa.jamesORMModel.Resume;
import com.njpa.jamesORMModel.TALogin;
import com.njpa.jamesService.NjpaService;

import jakarta.mail.MessagingException;

@RestController
@CrossOrigin
public class JamesApiLayer {

	@Autowired
	private final NjpaService njpaService;

	public JamesApiLayer(NjpaService njpaService) {
		this.njpaService = njpaService;
	}

	@PostMapping("/register/v2")
	public ResponseEntity<?> userRegister(@RequestBody Register jsonData) throws Exception {
		String response = njpaService.registerCandidateDetails(jsonData);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/register/verify")
	public ResponseEntity<?> registerTokenVerify(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		boolean registrationSuccessful = njpaService.verifyUser(email, token);
		if (registrationSuccessful) {
			return ResponseEntity.ok("Registration successful");
		} else {
			return ResponseEntity.badRequest().body("Invalid or expired token");
		}
	}

	@PostMapping("/register/sendOtp/v2")
	public ResponseEntity<?> sendOtp(@RequestParam String email) throws MessagingException {
		String response = njpaService.sendOtp(email);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login/v2")
	public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto) {
		String login = njpaService.login(loginDto);

		if (login.equals("Candidate not found")) {
			return ResponseEntity.badRequest().body("Candidate not found");
		} else if (login.equals("Candidate Registered but not Verified")) {
			return ResponseEntity.badRequest().body("Candidate Registered but not Verified");
		} else if (login.equals("You have Entered Incorrect Password")) {
			return ResponseEntity.badRequest().body("You have Entered Incorrect Password");
		} else {
			return ResponseEntity.ok(login);
		}
	}

	@PostMapping("/forgot/password/v2")
	public ResponseEntity<String> forgotPassword(@RequestParam String email) {
		boolean forgotPassword = njpaService.forgotPassword(email);
		if (forgotPassword) {
			return ResponseEntity.ok("Email sent successfully. Please check your inbox for the verification link.");
		} else {
			return ResponseEntity.badRequest()
					.body("Email sent successfully. Please check your inbox for the verification link.");
		}
	}

	@GetMapping("/reset-password/verify")
	public ResponseEntity<?> forgotPwdOtpVerify(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		boolean forgotPasswordVerify = njpaService.forgotPasswordVerify(email, token);
		if (forgotPasswordVerify) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to verify the email");
		}
	}

	@PostMapping("/forgot/password/update/v2")
	public ResponseEntity<?> forgotPwdUpdate(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		boolean resetPassword = njpaService.resetPassword(email, password);
		if (resetPassword) {
			return ResponseEntity.ok("Password reset successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to reset password");
		}
	}

	@PostMapping("/saveEducation/v2")
	public ResponseEntity<?> saveEducation(@RequestBody CandidateDetails jsonData,
			@RequestParam("userEmail") String email) {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		boolean saveEducationDetails = njpaService.saveEducationDetails(jsonData, idbyEmail);
		if (saveEducationDetails) {
			return ResponseEntity.ok("Education details saved successfully!");
		} else {
			return ResponseEntity.badRequest().body("Education details Not saved successfully!");
		}
	}

	@PostMapping("/saveExperience/v2")
	public ResponseEntity<?> saveExperience(@RequestBody Experience experience,
			@RequestParam("userEmail") String email) {
		boolean saveITSkills = njpaService.saveITSkills(experience, email);
		if (saveITSkills) {
			return ResponseEntity.ok("Experience details saved successfully!");
		}
		return ResponseEntity.badRequest().body("Experience details not saved successfully!");
	}

	@PostMapping("/savePDF0/v2")
	public ResponseEntity<?> savePI(@RequestBody CandidateDetails jsonData, @RequestParam("userEmail") String email) {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		boolean savePersonalDetails = njpaService.savePersonalDetails(jsonData, idbyEmail);
		if (savePersonalDetails) {
			return new ResponseEntity<>("Personal Information saved successfully!", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Personal Information Not saved successfully!",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/savePD/v2")
	public ResponseEntity<?> savePD(@RequestBody ProjectDetails details, @RequestParam("userEmail") String email) {
		boolean projectDetails = njpaService.saveProjectDetails(details, email);
		if (projectDetails) {
			return new ResponseEntity<>("Project details saved Succesfully", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Error Saving project details", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/talogin/v2")
	public ResponseEntity<?> talogin(@RequestBody TALogin taLogin) {
		boolean loginTATeam = njpaService.loginTATeam(taLogin);
		if (loginTATeam) {
			JSONArray allProfiles = njpaService.getAllProfiles();
			if (allProfiles != null) {
				return ResponseEntity.ok(allProfiles.toString());
			} else {
				return ResponseEntity.noContent().build();
			}
		}
		return ResponseEntity.badRequest().body("User is not present");
	}

	@GetMapping("/resume/download/v2")
	public ResponseEntity<byte[]> resumeDownload(@RequestParam(name = "email") String email) {
		Resume optionalResume = njpaService.getResume(email);

		if (optionalResume == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(optionalResume.getResumecontentType()));
		headers.setContentDispositionFormData("attachment", optionalResume.getResumefileName());
		headers.set("filename", optionalResume.getResumefileName());
		return new ResponseEntity<>(optionalResume.getResume(), headers, HttpStatus.OK);
	}

	@PostMapping("/image/upload/v2")
	public ResponseEntity<?> imageUpload(@RequestParam("profileImage") MultipartFile file,
			@RequestParam(name = "email") String email) {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		boolean savePersonalDetails = njpaService.saveProfilePhoto(idbyEmail, file);
		if (savePersonalDetails) {
			try {
				// Convert file to base64 string
				byte[] fileBytes = file.getBytes();
				String base64Image = Base64.getEncoder().encodeToString(fileBytes);
				return ResponseEntity.ok(base64Image);
			} catch (IOException e) {
				return ResponseEntity.status(500).body("Error processing image file");
			}
		} else {
			return ResponseEntity.badRequest().body("Failed to save profile image!");
		}
	}

	@PostMapping("/resume/upload/v2")
	public ResponseEntity<?> resumeUpload(@RequestBody MultipartFile file, @RequestParam(name = "email") String email) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
		}

		Register idbyEmail = njpaService.getIdbyEmail(email);
		boolean saveResume = njpaService.saveResume(file, idbyEmail);
		if (saveResume) {
			return new ResponseEntity<>("Resume saved successfully", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Failed to save resume", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/resume/check/v2")
	public ResponseEntity<?> resumeVerify(@RequestParam(name = "email") String email) {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		boolean verifyResume = njpaService.ResumeVerify(idbyEmail);
		if (verifyResume) {
			return new ResponseEntity<>("Resume is present", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Resume is not present", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unused")
	@GetMapping("/getCD/v2")
	public ResponseEntity<?> getCandidateData(@RequestParam(name = "userEmail") String email) {
		CandidateDetails candidateDetails = njpaService.getCandidateData(email);
		ProjectDetails projectDetails = njpaService.getProjectDetails(email);
		Experience experience = njpaService.getExperience(email);
		Resume resume = njpaService.getResume(email);
		CandidateData candidateData = new CandidateData(candidateDetails, experience, resume, projectDetails,
				njpaService.getIdbyEmail(email));
		if (candidateData != null) {
			return ResponseEntity.ok(candidateData);
		} else {
			return ResponseEntity.badRequest().body("Failed retrive data");
		}
	}

	@PostMapping("save/basicData/v2")
	public ResponseEntity<?> saveBasicData(@RequestBody BasicDataDto jsonData, @RequestParam("email") String email)
			throws Exception {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		String response = njpaService.saveBasicData(jsonData,idbyEmail);
		return ResponseEntity.ok(response);
	}
	@PostMapping("get/basicData/v2")
	public ResponseEntity<?> getBasicData(@RequestParam("email") String email)
			throws Exception {
		Register idbyEmail = njpaService.getIdbyEmail(email);
		BasicDataDto response = njpaService.getBasicData(idbyEmail);
		return ResponseEntity.ok(response);
	}

}
