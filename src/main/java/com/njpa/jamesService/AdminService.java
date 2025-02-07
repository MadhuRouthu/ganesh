package com.njpa.jamesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.njpa.jamesORMModel.Admin;
import com.njpa.jamesORMModel.TALogin;
import com.njpa.jamesORMModel.TAResumeUpload;
import com.njpa.jamesORMRepo.AdminRepo;
import com.njpa.jamesORMRepo.TALoginRepo;
import com.njpa.jamesORMRepo.TAResumeUploadRepo;

import jakarta.transaction.Transactional;

@Service
public class AdminService {
	@Autowired
	private AdminRepo adminRepo;
	@Autowired
	private TALoginRepo taloginrepo;
	@Autowired
	private TAResumeUploadRepo taResumeUploadRepo;

	public List<TALogin> verifyAdminUser(Admin admin) {
		// TODO Auto-generated method stub
		Optional<Admin> byEmail = adminRepo.findByEmail(admin.getEmail());
		Admin register = null;
		if (byEmail.isPresent()) {
			register = byEmail.get();
		}
		if (register.getPassword().equals(admin.getPassword())) {
			List<TALogin> all = taloginrepo.findAll();
			if (all.isEmpty()) {
				return new ArrayList<TALogin>();
			} else {
				return all;
			}
		} else {
			return null;
		}
	}

	public boolean AddTAUser(TALogin addTAUser) {
		// TODO Auto-generated method stub
		Optional<TALogin> byEmpId = taloginrepo.findByEmail(addTAUser.getEmail());
		boolean saved;
		if (byEmpId.isPresent()) {
			saved = false;
			return saved;
		} else {
			TALogin save = taloginrepo.save(addTAUser);
			if (save != null) {
				saved = true;
			} else {
				saved = false;
			}
			return saved;
		}
	}

	public boolean updateTAUser(TALogin addTAUser) {
		// TODO Auto-generated method stub
		Optional<TALogin> byEmpId = taloginrepo.findByEmail(addTAUser.getEmail());
		boolean saved = false;
		if (byEmpId.isPresent()) {
			TALogin addTAUser2 = byEmpId.get();
			addTAUser2.setPassword(addTAUser.getPassword());
			addTAUser2.setRole(addTAUser.getRole());
			TALogin save = taloginrepo.save(addTAUser2);
			if (save != null) {
				saved = true;
			} else {
				saved = false;
			}
		} else {
			saved = false;
		}
		return saved;
	}

	@Transactional
	public boolean deleteTAUser(String empId) {
		taloginrepo.deleteByEmail(empId);
		return true;
	}
	
	public boolean taSaveResume(MultipartFile file,String email, String name, String phoneNo) {
		try {
			TAResumeUpload taResumeUpload = new TAResumeUpload();
			taResumeUpload.setTaemail(email);
			taResumeUpload.setResumefileName(file.getOriginalFilename());
			taResumeUpload.setResume(file.getBytes());
			taResumeUpload.setResumecontentType(file.getContentType());
			taResumeUpload.setName(name);
			taResumeUpload.setPhoneNO(phoneNo);
			TAResumeUpload savedResume = taResumeUploadRepo.save(taResumeUpload);
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

	public List<TALogin> getAllTATeam() {
		List<TALogin> all = taloginrepo.findAll();
		if (all.isEmpty()) {
			return new ArrayList<TALogin>();
		} else {
			return all;
		}
	}

}
