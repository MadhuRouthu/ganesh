package com.njpa.util;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtil {

	@Autowired
	private JavaMailSender javaMailSender;
	@Value("${cors.allowedOrigins}")
	private String domain;

	public void sendOtpEmail(String email, String otp) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Verify OTP");

		// Format the link with email and otp
		String link = domain+"/register-verify?email=" + email + "&token=" + otp;

		String emailContent = "<html><body>";
		emailContent += "<h2>Verification code</h2>";
		emailContent += "<p>Please use the verification code below to sign in.</p>";
		emailContent += "<div><a href=\"" + link + "\" target=\"_blank\">Click here to verify</a></div>";
		;
		emailContent += "<p>If you didn’t request this, you can ignore this email.</p>";
		emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
		emailContent += "<br>";
		emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
		emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
		emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
		emailContent += "</body></html>";

		mimeMessage.setContent(emailContent, "text/html; charset=utf-8");

		// Send the email
		javaMailSender.send(mimeMessage);

	}

	public void forgotPasswordEmailSending(String email, String otp) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Verify OTP");

		// Format the link with email and otp
		String link = domain+"/reset-password?email=" + email + "&token=" + otp;

		String emailContent = "<html><body>";
		emailContent += "<h2>Verification code</h2>";
		emailContent += "<p>Please use the verification link to Reset Your Password.</p>";
		emailContent += "<div><a href=\"" + link + "\" target=\"_blank\">Click here to verify</a></div>";
		;
		emailContent += "<p>If you didn’t request this, you can ignore this email.</p>";
		emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
		emailContent += "<br>";
		emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
		emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
		emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
		emailContent += "</body></html>";

		mimeMessage.setContent(emailContent, "text/html; charset=utf-8");

		// Send the email
		javaMailSender.send(mimeMessage);

	}

	public void tateamforgotPasswordEmailSending(String email, String otp) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Verify OTP");

		// Format the link with email and otp
		String link = domain+"/tateamreset-password?email=" + email + "&token=" + otp;

		String emailContent = "<html><body>";
		emailContent += "<h2>Verification code</h2>";
		emailContent += "<p>Please use the verification link to Reset Your Password.</p>";
		emailContent += "<div><a href=\"" + link + "\" target=\"_blank\">Click here to verify</a></div>";
		;
		emailContent += "<p>If you didn’t request this, you can ignore this email.</p>";
		emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
		emailContent += "<br>";
		emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
		emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
		emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
		emailContent += "</body></html>";

		mimeMessage.setContent(emailContent, "text/html; charset=utf-8");

		// Send the email
		javaMailSender.send(mimeMessage);

	}

	public void sendConfirmationEmail(String email, String fullName) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Profile Update Confirmation");

		String emailContent = "<html><body>";
		emailContent += "<h2>Profile Update Confirmation</h2>";
		emailContent += "<p>Dear " + fullName + ",</p>";
		emailContent += "<p>Your profile has been successfully updated.</p>";
		emailContent += "<p>If you have any questions, please contact our support team.</p>";
		emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
		emailContent += "<br>";
		emailContent += "<p><a href=\""+domain+"/loginform\">Click here to login and view/update your profile</a></p>";
		emailContent += "<br><br>";
		emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
		emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
		emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
		emailContent += "</body></html>";

		mimeMessage.setContent(emailContent, "text/html; charset=utf-8");
		javaMailSender.send(mimeMessage);
	}

	public void sendReminderEmail(String email, String fullName) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Profile Update Reminder");

		String emailContent = "<html><body>";
		emailContent += "<h2>Profile Update Reminder</h2>";
		emailContent += "<p>Dear " + fullName + ",</p>";
		emailContent += "<p>Please complete your profile update to ensure all your details are accurate and up to date.</p>";
		emailContent += "<p>If you have any questions, please contact our support team.</p>";
		emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
		emailContent += "<br>";
		emailContent += "<p><a href=\""+domain+"/loginform\">Click here to login and update your profile</a></p>";
		emailContent += "<br><br>";
		emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
		emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
		emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
		emailContent += "</body></html>";

		mimeMessage.setContent(emailContent, "text/html; charset=utf-8");
		javaMailSender.send(mimeMessage);
	}

	public void sendApplyForAnyPosition(String email, String fullName, String technologyApplied) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Application Confirmation");

        String emailContent = "<html><body>";
        emailContent += "<h2>Profile Update Reminder</h2>";
        emailContent += "<p>Dear " + fullName + ",</p>";
        emailContent += "<p>Thank you for registering with us and verifying your email address.</p>";
        emailContent += "<p>You have successfully registered for a position in " + technologyApplied + " with our company.</p>";
        emailContent += "<p>Please do update your profile. If you have any questions, please contact our support team.</p>";
        emailContent += "<p>Thanks,<br>The NGS Job Portal Team</p>";
        emailContent += "<br>";
        emailContent += "<p><a href=\"" + domain + "/loginform\">Click here to login and check the status of your application</a></p>";
        emailContent += "<br><br>";
        emailContent += "<p>NeuroGaint Systems Pvt Ltd (NGS),</p>";
        emailContent += "<p>3rd floor, Soma enterprises, Road No. 10, opp city central mall, Banjara Hills, Hyderabad, Telangana - 500034</p>";
        emailContent += "<p>Office: +91-9849267118 | info@neurogaint.com | <a href=\"http://www.neurogaint.com\">www.neurogaint.com</a></p>";
        emailContent += "</body></html>";

        mimeMessage.setContent(emailContent, "text/html; charset=utf-8");
        javaMailSender.send(mimeMessage);
	}
}
