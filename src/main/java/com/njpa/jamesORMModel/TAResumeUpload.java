package com.njpa.jamesORMModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor 

public class TAResumeUpload {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resume_id")
	private Long resumeId;
	@Lob
	@Column(length = 16777215)
	private byte[] Resume;
	private String resumefileName;
	private String resumecontentType;
	private String taemail;
	private String name;
	private String phoneNO;

}