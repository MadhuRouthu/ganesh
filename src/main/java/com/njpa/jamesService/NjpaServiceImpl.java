package com.njpa.jamesService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.njpa.customexceptions.UserAlreadyRegisteredException;
import com.njpa.dto.BasicDataDto;
import com.njpa.dto.CandidateData;
import com.njpa.dto.LoginDto;
import com.njpa.jamesORMModel.CandidateDetails;
import com.njpa.jamesORMModel.Experience;
import com.njpa.jamesORMModel.ProjectDetails;
import com.njpa.jamesORMModel.Register;
import com.njpa.jamesORMModel.Resume;
import com.njpa.jamesORMModel.TALogin;
import com.njpa.jamesORMRepo.CandidateDetailsRepo;
import com.njpa.jamesORMRepo.ExperienceRepo;
import com.njpa.jamesORMRepo.ProjectDetailsRepo;
import com.njpa.jamesORMRepo.RegisterRepo;
import com.njpa.jamesORMRepo.ResumeRepo;
import com.njpa.jamesORMRepo.TALoginRepo;
import com.njpa.util.EmailUtil;
import com.njpa.util.OtpUtil;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class NjpaServiceImpl implements NjpaService {

	@Autowired
	private EmailUtil emailUtil;

	@Autowired
	private OtpUtil otpUtil;

	@Autowired
	private RegisterRepo registerRepo;

	@Autowired
	private CandidateDetailsRepo candidateDetailsRepo;

	@Autowired
	private ProjectDetailsRepo projectDetailsRepo;

	@Autowired
	private ExperienceRepo itSkillsRepo;

	@Autowired
	private ResumeRepo resumeRepository;

	@Autowired
	private TALoginRepo taloginrepo;

	@Override
	public Register getIdbyEmail(String email) {
		Optional<Register> byEmailID = registerRepo.findByEmailID(email);
		Register register = null;
		if (byEmailID.isPresent()) {
			register = byEmailID.get();
		}
		return register;
	}

	@Override
	public boolean saveITSkills(Experience itSkills, String email) {
		boolean saved = false;

		// Find the Register entity by email
		Optional<Register> byEmailID = registerRepo.findByEmailID(email);
		if (byEmailID.isPresent()) {
			Register register = byEmailID.get();

			Optional<Experience> existingExperience = itSkillsRepo
					.findByRegister_Registrationid(register.getRegistrationid());

			Experience experience;
			if (existingExperience.isPresent()) {
				// If candidateId is present, update the existing Experience object
				experience = existingExperience.get();
			} else {
				// If candidateId is not present, create a new Experience object
				experience = new Experience();
			}

			// Set the properties of the Experience object
			experience.setSkillName(itSkills.getSkillName());
			experience.setExperienceYears(itSkills.getExperienceYears());
			experience.setWorkingStatus(itSkills.getWorkingStatus());
			experience.setRole(itSkills.getRole());
			experience.setRegister(register);

			// Save the Experience object
			Experience savedExperience = itSkillsRepo.save(experience);
			if (savedExperience != null) {
				saved = true;
			}
		}

		return saved;
	}

	@Override
	public String registerCandidateDetails(Register jsonData) throws Exception, UserAlreadyRegisteredException {
		Optional<Register> existingUser = registerRepo.findByEmailID(jsonData.getEmailID());
		if (existingUser.isPresent()) {
			if (!existingUser.get().isActive()) {
				throw new UserAlreadyRegisteredException("User already registered but not verified");
			} else {
				throw new UserAlreadyRegisteredException("User already registered and verified");
			}
		}

		String otp = otpUtil.generateOtp();
		emailUtil.sendOtpEmail(jsonData.getEmailID(), otp);

		Register register = new Register();
		register.setFirstName(jsonData.getFirstName());
		register.setLastName(jsonData.getLastName());
		register.setEmailID(jsonData.getEmailID());
		register.setActive(false);
		register.setPassword(jsonData.getPassword());
		register.setConfirmPassword(jsonData.getConfirmPassword());
		register.setMobileNumber(jsonData.getMobileNumber());
		register.setOtp(otp);
		register.setTechnologyApplied(jsonData.getTechnologyApplied());

		Register savedUser = registerRepo.save(register);
		if (savedUser != null) {
			return "Data Saved. Verification email sent.";
		} else {
			throw new Exception("Data Not Saved");
		}
	}

	@Override
	public boolean forgotPassword(String email) {
		// Implement business logic here
		String otp = otpUtil.generateOtp();
		Optional<Register> register = registerRepo.findByEmailID(email);
		if (register.isPresent()) {
			try {
				Register register2 = register.get();
				emailUtil.forgotPasswordEmailSending(email, otp);
				register2.setOtp(otp);
				registerRepo.save(register2);

			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		} 

	}

	@Override
	public String login(LoginDto loginDto) {
		// Implement business logic here
		Optional<Register> user = registerRepo.findByEmailID(loginDto.getEmail());
		String fullName = null;
		if (user.isPresent()) {
			Register register = user.get();
			fullName = register.getFirstName();
			fullName = fullName + "," + register.getTechnologyApplied();
			if (!register.isActive()) {
				return "Candidate Registered but not Verified";
			} else if (!loginDto.getPassword().equals(register.getPassword())) {
				return "You have Entered Incorrect Password";
			}
		} else {
			return "Candidate not found";
		}

		return fullName;
	}

	@Override
	public boolean resetPassword(String email, String password) {
		// Implement business logic here
		Optional<Register> byEmailID = registerRepo.findByEmailID(email);
		if (byEmailID.isPresent()) {
			Register register = byEmailID.get();
			register.setPassword(password);
			Register save = registerRepo.save(register);
			if (save != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean savePersonalDetails(CandidateDetails jsonData, Register register) {

		CandidateDetails candidateDetails = candidateDetailsRepo
				.findByRegister_Registrationid(register.getRegistrationid())
				.orElseThrow(() -> new RuntimeException("Data not Saved"));
		boolean saved = false;
		boolean dataComplete = false;
		try {
			CandidateData candidateDataByRegister = getCandidateDataByRegister(register);
			if (candidateDataByRegister != null) {
				dataComplete = isDataComplete(candidateDataByRegister);
			}
			if (dataComplete) {
				emailUtil.sendConfirmationEmail(register.getEmailID(), register.getFirstName());
			}
			if (candidateDetails != null) {
				candidateDetails.setCandidateName(jsonData.getCandidateName());
				candidateDetails.setAge(jsonData.getAge());
				candidateDetails.setGender(jsonData.getGender());
				candidateDetails.setMarital_Status(jsonData.getMarital_Status());
				candidateDetails.setLanguages_Known(jsonData.getLanguages_Known());
				candidateDetails.setD_No(jsonData.getD_No());
				candidateDetails.setStreet(jsonData.getStreet());
				candidateDetails.setLandMark(jsonData.getLandMark());
				candidateDetails.setMandal(jsonData.getMandal());
				candidateDetails.setDistrict(jsonData.getDistrict());
				candidateDetails.setState(jsonData.getState());
				candidateDetails.setPincode(jsonData.getPincode());
				candidateDetails.setCurrentCity(jsonData.getCurrentCity());

				CandidateDetails save = candidateDetailsRepo.save(candidateDetails);

				if (save != null) {
					saved = true;
				} else {
					saved = false;
				}
			} else {
				saved = false;
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return saved;
	}

	@Override
	public boolean saveProfilePhoto(Register idbyEmail, MultipartFile file) {
		// Implement business logic here
		boolean saved = false;
		try {
			// Generate a unique filename
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			// Save the file to the database
			Long registrationid = idbyEmail.getRegistrationid();
			Optional<CandidateDetails> byRegister_Registrationid = candidateDetailsRepo
					.findByRegister_Registrationid(registrationid);
			CandidateDetails candidateDetails;
			if (byRegister_Registrationid.isPresent()) {
				candidateDetails = byRegister_Registrationid.get();

			} else {
				candidateDetails = new CandidateDetails();
			}
			candidateDetails.setFileName(fileName);
			candidateDetails.setContentType(file.getContentType());
			candidateDetails.setData(file.getBytes());
			candidateDetails.setRegister(idbyEmail);
			CandidateDetails save = candidateDetailsRepo.save(candidateDetails);
			if (save != null) {
				saved = true;
			} else {
				saved = false;
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return saved;
	}

	@Override
	public boolean saveResume(MultipartFile file, Register idbyEmail) {
		try {
			Long registrationid = idbyEmail.getRegistrationid();
			Optional<Resume> byCandidateDetailsId = resumeRepository.findByRegister_Registrationid(registrationid);
			Resume resume;

			if (byCandidateDetailsId.isPresent()) {
				resume = byCandidateDetailsId.get();
			} else {
				resume = new Resume();
			}

			resume.setResumefileName(file.getOriginalFilename());
			resume.setResume(file.getBytes());
			resume.setResumecontentType(file.getContentType());
			resume.setRegister(idbyEmail);
			Resume savedResume = resumeRepository.save(resume);
			if (savedResume != null) {
				return true;
			} else {
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace(); // Log the exception for debugging
			return false;
		}
	}

	@Override
	public Resume getResume(String email) {
		// Implement business logic here
		Register byEmailID = getIdbyEmail(email);
		Optional<Resume> byCandidateDetails_CandidateId = resumeRepository
				.findByRegister_Registrationid(byEmailID.getRegistrationid());
		if (byCandidateDetails_CandidateId.isPresent()) {
			return byCandidateDetails_CandidateId.get();
		} else {
			return null;
		}
	}

	@Override
	public JSONArray getAllProfiles() {
		// Retrieve all data
		List<Register> all = registerRepo.findAll();
		List<CandidateDetails> all2 = candidateDetailsRepo.findAll();
		List<Experience> all4 = itSkillsRepo.findAll();

		// Create maps for quicker lookups based on registrationId
		Map<Long, CandidateDetails> candidateDetailsMap = all2.stream()
				.collect(Collectors.toMap(cd -> cd.getRegister().getRegistrationid(), Function.identity()));
		Map<Long, Experience> experienceMap = all4.stream()
				.collect(Collectors.toMap(ex -> ex.getRegister().getRegistrationid(), Function.identity()));

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		for (int i = 0; i < all.size(); i++) {
			jsonObject = new JSONObject();
			Long registrationId = all.get(i).getRegistrationid();
			String emailID = all.get(i).getEmailID();
			String fullName = all.get(i).getFirstName();
			long mobileNumber = all.get(i).getMobileNumber();

			jsonObject.put("email", emailID);
			jsonObject.put("fullName", fullName);
			jsonObject.put("mobileNumber", mobileNumber);

			// Add candidate details if present
			CandidateDetails candidateDetails = candidateDetailsMap.get(registrationId);
			if (candidateDetails != null) {
				jsonObject.put("id",candidateDetails.getCandidateId());
				jsonObject.put("location", candidateDetails.getCurrentCity());
				jsonObject.put("qualification", candidateDetails.getGraduation_Qualification());
				jsonObject.put("specialization", candidateDetails.getGraduation_Specialization());
			}

			// Add experience details if present
			Experience experience = experienceMap.get(registrationId);
			if (experience != null) {
				jsonObject.put("skills", experience.getSkillName());
				jsonObject.put("experience", experience.getExperienceYears());
				jsonObject.put("role", experience.getRole());
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	@Override
	public Experience getExperience(String email) {
		// Implement business logic here
		Register idbyEmail = getIdbyEmail(email);
		Optional<Experience> byRegister_Registrationid = itSkillsRepo
				.findByRegister_Registrationid(idbyEmail.getRegistrationid());
		Experience experience = null;
		if (byRegister_Registrationid.isPresent()) {
			experience = byRegister_Registrationid.get();
		}
		if (experience != null) {
			return experience;
		} else {
			return null;
		}
	}

	@Override
	public boolean loginTATeam(TALogin taLogin) {
		// Implement business logic here
		Optional<TALogin> byId = taloginrepo.findByEmail(taLogin.getEmail());
		boolean present = false;
		if (byId.isPresent()) {
			TALogin login = byId.get();
			if (login.getPassword().equals(taLogin.getPassword())) {
				present = true;
			} else {
				present = false;
			}
		}
		return present;
	}

	@Override
	public boolean saveEducationDetails(CandidateDetails JsonData, Register register) {

		boolean saved = false;

		// Check if CandidateDetails already exists for the given register
		Optional<CandidateDetails> existingCandidateDetails = candidateDetailsRepo
				.findByRegister_Registrationid(register.getRegistrationid());
		CandidateDetails candidateDetails;
		if (existingCandidateDetails.isPresent()) {
			// Update existing CandidateDetails
			candidateDetails = existingCandidateDetails.get();
		} else {
			candidateDetails = new CandidateDetails();
		}
		candidateDetails.setSSC_School_Name(JsonData.getSSC_School_Name());
		candidateDetails.setSSC_Marks(JsonData.getSSC_Marks());
		candidateDetails.setSSC_YOP(JsonData.getSSC_YOP());
		candidateDetails.setSSC_Board(JsonData.getSSC_Board());
		candidateDetails.setSSC_CourseStream(JsonData.getSSC_CourseStream());
		candidateDetails.setInterBoard(JsonData.getInterBoard());
		candidateDetails.setInter_College(JsonData.getInter_College());
		candidateDetails.setInter_Marks(JsonData.getInter_Marks());
		candidateDetails.setInter_Group(JsonData.getInter_Group());
		candidateDetails.setInter_YOP(JsonData.getInter_YOP());
		candidateDetails.setGraduation_College_Name(JsonData.getGraduation_College_Name());
		candidateDetails.setGraduation_GPA(JsonData.getGraduation_GPA());
		candidateDetails.setGraduation_Qualification(JsonData.getGraduation_Qualification());
		candidateDetails.setGraduation_Specialization(JsonData.getGraduation_Specialization());
		candidateDetails.setGraduation_YOP(JsonData.getGraduation_YOP());
		candidateDetails.setGraduation_University(JsonData.getGraduation_University());
		candidateDetails.setGraduation_Project(JsonData.getGraduation_Project());
		candidateDetails.setPost_Graduation_University(JsonData.getPost_Graduation_University());
		candidateDetails.setPost_Graduation_College_Name(JsonData.getPost_Graduation_College_Name());
		candidateDetails.setPost_Graduation_YOP(JsonData.getPost_Graduation_YOP());
		candidateDetails.setPost_Graduation_Qualification(JsonData.getPost_Graduation_Qualification());
		candidateDetails.setPost_Graduation_Specialization(JsonData.getPost_Graduation_Specialization());
		candidateDetails.setPost_Graduation_GPA(JsonData.getPost_Graduation_GPA());
		candidateDetails.setPost_Graduation_Project(JsonData.getPost_Graduation_Project());
		candidateDetails.setRegister(register);
		// Save the new CandidateDetails
		CandidateDetails savedCandidateDetails = candidateDetailsRepo.save(candidateDetails);
		if (savedCandidateDetails != null) {
			saved = true;
		} else {
			saved = false;
		}
		return saved;
	}

	@Override
	public boolean saveProjectDetails(ProjectDetails details, String email) {

		boolean saved = false;
		// Find the Register entity by email
		Optional<Register> byEmailID = registerRepo.findByEmailID(email);

		if (byEmailID.isPresent()) {
			Register register = byEmailID.get();
			// Check if a ProjectDetails entry already exists for the CandidateDetails
			Optional<ProjectDetails> existingProjectDetails = projectDetailsRepo
					.findByRegister_Registrationid(register.getRegistrationid());
			ProjectDetails existingDetails = null;
			if (existingProjectDetails.isPresent()) {
				// Update the existing ProjectDetails entry
				existingDetails = existingProjectDetails.get();
			} else {
				// Save the new ProjectDetails entry
				existingDetails = new ProjectDetails();
			}
			existingDetails.setProjectName(details.getProjectName());
			existingDetails.setClient(details.getClient());
			existingDetails.setProjectStatus(details.getProjectStatus());
			existingDetails.setWorkedFromYear(details.getWorkedFromYear());
			existingDetails.setWorkedFromMonth(details.getWorkedFromMonth());
			existingDetails.setProjectDetails(details.getProjectDetails());
			existingDetails.setRegister(register);
			// Save the updated ProjectDetails
			ProjectDetails savedProjectDetails = projectDetailsRepo.save(existingDetails);
			if (savedProjectDetails != null) {
				saved = true;
			} else {
				saved = false;
			}
		}
		return saved;
	}

	@Override
	public boolean verifyUser(String email, String token) {
		Optional<Register> user = registerRepo.findByEmailID(email);
		try {
			if (user.isPresent() && user.get().getOtp().equals(token)) {
				Register register = user.get();
				register.setActive(true);
				registerRepo.save(register);
				String technologyApplied = user.get().getTechnologyApplied();

				emailUtil.sendApplyForAnyPosition(email, user.get().getFirstName(), technologyApplied);

				return true;
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean forgotPasswordVerify(String mail, String token) {
		// TODO Auto-generated method stub
		boolean saved = false;
		Optional<Register> byVerificationToken = registerRepo.findByEmailID(mail);
		if (byVerificationToken.isPresent()) {
			Register register = byVerificationToken.get();
			if (register.getOtp().equals(token)) {
				register.setActive(true);
				Register save = registerRepo.save(register);
				if (save != null) {
					saved = true;
				} else {
					saved = false;
				}
			}
		}
		return saved;
	}

	@Override
	public CandidateDetails getCandidateData(String email) {
		// TODO Auto-generated method stub
		Register idbyEmail = getIdbyEmail(email);
		CandidateDetails candidateDetails = null;
		Optional<CandidateDetails> byRegister_Registrationid = candidateDetailsRepo
				.findByRegister_Registrationid(idbyEmail.getRegistrationid());
		if (byRegister_Registrationid.isPresent()) {
			candidateDetails = byRegister_Registrationid.get();
		}
		if (candidateDetails != null) {
			return candidateDetails;
		} else {
			return null;
		}
	}

	@Override
	public ProjectDetails getProjectDetails(String email) {
		// TODO Auto-generated method stub
		Register idbyEmail = getIdbyEmail(email);
		Optional<ProjectDetails> byRegister_Registrationid = projectDetailsRepo
				.findByRegister_Registrationid(idbyEmail.getRegistrationid());
		ProjectDetails projectDetails = null;
		if (byRegister_Registrationid.isPresent()) {
			projectDetails = byRegister_Registrationid.get();
		}
		if (projectDetails != null) {
			return projectDetails;
		} else {
			return null;
		}
	}

	@Override
	public boolean ResumeVerify(Register email) {
		// TODO Auto-generated method stub
		Optional<Resume> byRegister_Registrationid = resumeRepository
				.findByRegister_Registrationid(email.getRegistrationid());
		if (byRegister_Registrationid.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean taForgotPassword(String email) {
		// TODO Auto-generated method stub
		String otp = otpUtil.generateOtp();
		Optional<TALogin> register = taloginrepo.findByEmail(email);
		if (register.isPresent()) {
			try {
				TALogin register2 = register.get();
				emailUtil.tateamforgotPasswordEmailSending(email, otp);
				register2.setOtp(otp);
				taloginrepo.save(register2);

			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean taForgotPasswordVerify(String email, String token) {
		// TODO Auto-generated method stub
		boolean saved = false;
		Optional<TALogin> byVerificationToken = taloginrepo.findByEmail(email);
		if (byVerificationToken.isPresent()) {
			TALogin register = byVerificationToken.get();
			if (register.getOtp().equals(token)) {
				saved = true;
			} else {
				saved = false;
			}
		}
		return saved;
	}

	@Override
	public boolean taResetPassword(String email, String password) {
		// TODO Auto-generated method stub
		Optional<TALogin> byEmailID = taloginrepo.findByEmail(email);
		if (byEmailID.isPresent()) {
			TALogin register = byEmailID.get();
			register.setPassword(password);
			TALogin save = taloginrepo.save(register);
			if (save != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String sendOtp(String email) throws MessagingException {
		// TODO Auto-generated method stub
		Optional<Register> existingUser = registerRepo.findByEmailID(email);
		if (existingUser.isPresent() && !existingUser.get().isActive()) {
			String otp = otpUtil.generateOtp();
			emailUtil.sendOtpEmail(email, otp);
			return "Verification email sent.";
		} else {
			throw new UserAlreadyRegisteredException("Failed to send the Verification email");
		}
	}

	@Override
	public boolean isDataComplete(CandidateData candidateData) {
		Register register = candidateData.getRegister();

		if (register == null || register.getFirstName() == null || register.getFirstName().isEmpty()
				|| register.getEmailID() == null || register.getEmailID().isEmpty()) {
			return false;
		}

		CandidateDetails candidateDetails = candidateData.getCandidateDetails();
		if (candidateDetails == null) {
			return false;
		}
		boolean existsByExperience = itSkillsRepo.existsByRegister(register);
		boolean existsByproject = projectDetailsRepo.existsByRegister(register);
		boolean existsByResume = resumeRepository.existsByRegister(register);
		boolean candidateDetailsComplete = isCandidateDetailsComplete(candidateDetails);
		if (existsByExperience && existsByproject && existsByResume && candidateDetailsComplete) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCandidateDetailsComplete(CandidateDetails candidateDetails) {
		return candidateDetails.getCandidateName() != null && !candidateDetails.getCandidateName().isEmpty()
				&& candidateDetails.getAge() != null && !candidateDetails.getAge().isEmpty()
				&& candidateDetails.getGender() != null && !candidateDetails.getGender().isEmpty()
				&& candidateDetails.getLanguages_Known() != null && !candidateDetails.getLanguages_Known().isEmpty()
				&& candidateDetails.getD_No() != null && !candidateDetails.getD_No().isEmpty()
				&& candidateDetails.getStreet() != null && !candidateDetails.getStreet().isEmpty()
				&& candidateDetails.getLandMark() != null && !candidateDetails.getLandMark().isEmpty()
				&& candidateDetails.getMandal() != null && !candidateDetails.getMandal().isEmpty()
				&& candidateDetails.getDistrict() != null && !candidateDetails.getDistrict().isEmpty()
				&& candidateDetails.getState() != null && !candidateDetails.getState().isEmpty()
				&& candidateDetails.getPincode() != null && !candidateDetails.getPincode().isEmpty()
				&& candidateDetails.getCurrentCity() != null && !candidateDetails.getCurrentCity().isEmpty()
				&& candidateDetails.getGraduation_College_Name() != null
				&& !candidateDetails.getGraduation_College_Name().isEmpty()
				&& candidateDetails.getGraduation_GPA() != null && !candidateDetails.getGraduation_GPA().isEmpty()
				&& candidateDetails.getGraduation_Qualification() != null
				&& !candidateDetails.getGraduation_Qualification().isEmpty()
				&& candidateDetails.getGraduation_Specialization() != null
				&& !candidateDetails.getGraduation_Specialization().isEmpty()
				&& candidateDetails.getGraduation_YOP() != null && !candidateDetails.getGraduation_YOP().isEmpty()
				&& candidateDetails.getGraduation_University() != null
				&& !candidateDetails.getGraduation_University().isEmpty()
				&& candidateDetails.getGraduation_Project() != null
				&& !candidateDetails.getGraduation_Project().isEmpty() && candidateDetails.getMarital_Status() != null
				&& !candidateDetails.getMarital_Status().isEmpty() && candidateDetails.getFileName() != null
				&& !candidateDetails.getFileName().isEmpty() && candidateDetails.getContentType() != null
				&& !candidateDetails.getContentType().isEmpty() && candidateDetails.getData() != null
				&& candidateDetails.getData().length > 0;
	}

	@Override
	public CandidateData getCandidateDataByRegister(Register register) {
		CandidateDetails candidateDetails = candidateDetailsRepo.findByRegister(register);
		Experience experience = itSkillsRepo.findByRegister(register);
		ProjectDetails projectDetails = projectDetailsRepo.findByRegister(register);
		Resume resume = resumeRepository.findByRegister(register);
		return new CandidateData(candidateDetails, experience, resume, projectDetails, register);
	}

	@Override
	public List<Register> getAllRegisters() {
		return registerRepo.findAll();
	}

	@Override
	public String saveBasicData(BasicDataDto jsonData, Register idbyEmail) {
		// TODO Auto-generated method stub
		Optional<CandidateDetails> byRegister_Registrationid = candidateDetailsRepo
				.findByRegister_Registrationid(idbyEmail.getRegistrationid());
		CandidateDetails candidateDetails = null;
		if (byRegister_Registrationid.isPresent()) {
			candidateDetails = byRegister_Registrationid.get();
		} else {
			candidateDetails = new CandidateDetails();
		}
		candidateDetails.setFirstName(jsonData.getFirstName());
		candidateDetails.setLastName(jsonData.getLastName());
		candidateDetails.setAddress(jsonData.getAddress());
		candidateDetails.setRegister(idbyEmail);
		CandidateDetails save = candidateDetailsRepo.save(candidateDetails);
		if (save != null) {
			return "Success";
		} else {
			return "Failed to Save";
		}
	}

	@Override
	public BasicDataDto getBasicData(Register idbyEmail) {
		// TODO Auto-generated method stub
		Optional<CandidateDetails> byRegister_Registrationid = candidateDetailsRepo
				.findByRegister_Registrationid(idbyEmail.getRegistrationid());
		CandidateDetails candidateDetails = null;
		BasicDataDto basicDataDto = new BasicDataDto();
		if (byRegister_Registrationid.isPresent()) {
			candidateDetails = byRegister_Registrationid.get();
			basicDataDto.setAddress(candidateDetails.getAddress());
			basicDataDto.setFirstName(candidateDetails.getFirstName());
			basicDataDto.setLastName(candidateDetails.getLastName());
			return basicDataDto;
		} else {
			return null;
		}
	}
}
