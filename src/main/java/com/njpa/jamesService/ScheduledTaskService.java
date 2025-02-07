package com.njpa.jamesService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.njpa.dto.CandidateData;
import com.njpa.jamesORMModel.Register;
import com.njpa.util.EmailUtil;

import jakarta.mail.MessagingException;

@Service
public class ScheduledTaskService {

	@Autowired
	private NjpaService njpaService;

	@Autowired
	private EmailUtil emailService;

	@Scheduled(cron = "0 0 17 ? * FRI")
	public void checkCandidateDataAndSendEmails() {
		List<Register> allRegisters = njpaService.getAllRegisters();

		for (Register register : allRegisters) {
			CandidateData candidateData = njpaService.getCandidateDataByRegister(register);
			boolean isComplete = njpaService.isDataComplete(candidateData);

			try {
			  if (!isComplete) {
					emailService.sendReminderEmail(register.getEmailID(), register.getFirstName());
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}
