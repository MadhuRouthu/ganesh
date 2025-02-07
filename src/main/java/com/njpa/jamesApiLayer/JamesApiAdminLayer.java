package com.njpa.jamesApiLayer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.njpa.jamesORMModel.Admin;
import com.njpa.jamesORMModel.TALogin;
import com.njpa.jamesService.AdminService;
import com.njpa.jamesService.NjpaService;

@RestController
@CrossOrigin
public class JamesApiAdminLayer {

	@Autowired
	private AdminService adminService;

	@Autowired
	private NjpaService njpaService;

	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody Admin admin) {
		// Implement business logic here
		List<TALogin> verifyAdminUser = adminService.verifyAdminUser(admin);
		if (verifyAdminUser != null) {
			return ResponseEntity.ok(verifyAdminUser);
		} else {
			return ResponseEntity.badRequest().body("Login Failed");
		}
	}
	@GetMapping("/getAll/taTeam")
	public ResponseEntity<?> getAllTATeam() {
		// Implement business logic here
		List<TALogin> verifyAdminUser = adminService.getAllTATeam();
		if (verifyAdminUser != null) {
			return ResponseEntity.ok(verifyAdminUser);
		} else {
			return ResponseEntity.badRequest().body("Login Failed");
		}
	}

	@PostMapping("/admin/addTAUser")
	public ResponseEntity<?> addTAUser(@RequestBody TALogin addTAUser) {
		// Implement business logic here
		boolean verifyAdminUser = adminService.AddTAUser(addTAUser);
		if (verifyAdminUser) {
			return ResponseEntity.ok("User Added Successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to Add User");
		}
	}

	@PutMapping("/admin/updateTAUser")
	public ResponseEntity<?> updateTAUser(@RequestBody TALogin addTAUser) {
		// Implement business logic here
		boolean verifyAdminUser = adminService.updateTAUser(addTAUser);
		if (verifyAdminUser) {
			return ResponseEntity.ok("User Updated Successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to Update User");
		}
	}

	@DeleteMapping("/admin/deleteTAUser")
	public ResponseEntity<?> deleteTAUser(@RequestParam String empId) {
		// Implement business logic here
	   try {
	        boolean verifyAdminUser = adminService.deleteTAUser(empId);
	        if (verifyAdminUser) {
	            return ResponseEntity.ok("User Deleted Successfully");
	        } else {
	            return ResponseEntity.badRequest().body("Failed to Delete User");
	        }
	    } catch (Exception e) {
	        // Log the exception (optional)
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

	@PostMapping("/tateamforgot/password/v2")
	public ResponseEntity<String> taForgotPassword(@RequestParam String email) {
		try {

			boolean forgotPassword = njpaService.taForgotPassword(email);
			if (forgotPassword) {
				return ResponseEntity.ok("Email sent successfully. Please check your inbox for the verification link.");
			} else {
				return ResponseEntity.badRequest()
						.body("Email sent successfully. Please check your inbox for the verification link.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
		}

	}

	@GetMapping("/tateamreset-password/verify")
	public ResponseEntity<?> forgotPwdOtpVerify(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		// Implement business logic here
		boolean forgotPasswordVerify = njpaService.taForgotPasswordVerify(email, token);
		if (forgotPasswordVerify) {
			return ResponseEntity.ok("Email verified successfully");

		} else {
			return ResponseEntity.badRequest().body("Failed to verify the email");
		}
	}

	@PostMapping("/tateamforgot/password/update/v2")
	public ResponseEntity<?> forgotPwdUpdate(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		boolean resetPassword = njpaService.taResetPassword(email, password);
		if (resetPassword) {
			return ResponseEntity.ok("Password reset successfully");
		} else {
			return ResponseEntity.badRequest().body("Failed to reset password");
		}
	}
	
	@PostMapping("/tateam/upload/resume/v2")
	public ResponseEntity<?> taResumeUpload(@RequestBody MultipartFile file, @RequestParam(name = "email") String email,
			@RequestParam(name = "name") String name,@RequestParam(name = "phoneNo") String phoneNo) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
        }
        boolean saveResume = adminService.taSaveResume(file, email,name,phoneNo);
        if (saveResume) {
            return new ResponseEntity<>("Resume saved successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to save resume", HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

}
