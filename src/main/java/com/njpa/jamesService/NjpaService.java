package com.njpa.jamesService;

import java.util.List;

import org.json.JSONArray;
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

import jakarta.mail.MessagingException;

public interface NjpaService {

	public Register getIdbyEmail(String email);

	public boolean saveITSkills(Experience experience, String email);

	public String registerCandidateDetails(Register jsonData) throws Exception;

	public boolean saveEducationDetails(CandidateDetails jsonData, Register register);

	public boolean forgotPassword(String mail);

	public boolean forgotPasswordVerify(String mail, String token);

	public String login(LoginDto loginDto);

	public boolean resetPassword(String email, String password);

	public boolean saveProjectDetails(ProjectDetails details, String email);

	public boolean savePersonalDetails(CandidateDetails jsonData, Register register);

	public boolean saveProfilePhoto(Register idbyEmail, MultipartFile file);

	public boolean saveResume(MultipartFile file, Register idbyEmail);

	public Resume getResume(String email);

	public JSONArray getAllProfiles();

	public Experience getExperience(String email);

	public boolean loginTATeam(TALogin taLogin);

	public boolean verifyUser(String token, String email);

	public CandidateDetails getCandidateData(String email);

	public ProjectDetails getProjectDetails(String email);

	public boolean ResumeVerify(Register idbyEmail);

	public boolean taForgotPassword(String email);

	public boolean taForgotPasswordVerify(String email, String token);

	public boolean taResetPassword(String email, String password);

	public String sendOtp(String email) throws MessagingException;

	public List<Register> getAllRegisters();

	public CandidateData getCandidateDataByRegister(Register register);

	public boolean isCandidateDetailsComplete(CandidateDetails candidateDetails);

	public boolean isDataComplete(CandidateData candidateData);

	public String saveBasicData(BasicDataDto jsonData, Register idbyEmail);

	public BasicDataDto getBasicData(Register idbyEmail);

}
