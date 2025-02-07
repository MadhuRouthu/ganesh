package com.njpa.jamesORMModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Register {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "registration_id")
	private Long registrationid;
	private boolean active;
	private String firstName;
	private String lastName;
	private String emailID;
	private String password;
	private String confirmPassword;
	private String otp;
	private long mobileNumber;
	private String technologyApplied;

}