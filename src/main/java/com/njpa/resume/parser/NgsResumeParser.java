package com.njpa.resume.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.njpa.dto.ResumeSummary;
import com.njpa.jamesORMModel.Resume;
import com.njpa.jamesService.NjpaServiceImpl;
import com.textkernel.tx.DataCenter;
import com.textkernel.tx.TxClient;
import com.textkernel.tx.exceptions.TxException;
import com.textkernel.tx.models.Document;
import com.textkernel.tx.models.api.parsing.ParseOptions;
import com.textkernel.tx.models.api.parsing.ParseRequest;
import com.textkernel.tx.models.api.parsing.ParseResumeResponse;
import com.textkernel.tx.models.resume.employment.ExperienceSummary;

@Service
public class NgsResumeParser {

	public static ResumeSummary parseResume(Resume resume) throws IOException {
		ResumeSummary resumeSummary = new ResumeSummary();
		TxClient client = new TxClient("26990900", "sXs9tx8C8tkqReR3YSz6qgR8JzOXE26ldQqB2VdR", DataCenter.US);
		if (resume.getResume()!= null) {
			// Create a temporary file with the appropriate extension
			File tempFile = File.createTempFile("tempResume", getFileExtension(resume.getResumefileName()));
			byte[] resumeBytes = resume.getResume();

			// Write the byte[] to the temporary file
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				fos.write(resumeBytes);
			}

			// Create a Document using the temporary file's absolute path
			Document doc = new Document(tempFile.getAbsolutePath());

			// Create ParseRequest with the Document and ParseOptions
			ParseRequest request = new ParseRequest(doc, new ParseOptions());
			ParseResumeResponse response = null;

			try {
				response = client.parseResume(request);
				// If we get here, the parsing was successful
				System.out.println(response.toString());

				// Extract the required data from the response
				extractResumeData(response, resumeSummary);
			} catch (TxException e) {
				// Handle exceptions from the TxClient
				System.out.println(
						"Code: " + e.HttpStatusCode + ", Error: " + e.TxErrorCode + ", Message: " + e.getMessage());
			} finally {
				// Delete the temporary file
				if (tempFile.exists()) {
					tempFile.delete();
				}
			}
		}
		return resumeSummary;
	}

	static void extractResumeData(ParseResumeResponse response, ResumeSummary resumeSummary) {
		if (response != null && response.Value != null && response.Value.ResumeData != null) {
			// Extract ProfessionalSummary
			String professionalSummary = response.Value.ResumeData.ProfessionalSummary;
			setProfessionalSummary(professionalSummary, resumeSummary);

			// Extract ExperienceSummary
			if (response.Value.ResumeData.EmploymentHistory != null) {
				ExperienceSummary experienceSummary = response.Value.ResumeData.EmploymentHistory.ExperienceSummary;
				setExperienceSummary(experienceSummary, resumeSummary);
			} else {
				System.out.println("Experience Summary: Not Available");
			}

			// Extract QualificationsSummary
			String qualificationsSummary = response.Value.ResumeData.QualificationsSummary;
			setQualificationsSummary(qualificationsSummary, resumeSummary);
		}
	}

	static void setProfessionalSummary(String professionalSummary, ResumeSummary resumeSummary) {
		if (professionalSummary != null && !professionalSummary.isEmpty()) {
			resumeSummary.setProfessionalSummary(professionalSummary);
			System.out.println("Professional Summary: " + professionalSummary);
		} else {
			System.out.println("Professional Summary: Not Available");
		}
	}

	static void setExperienceSummary(ExperienceSummary experienceSummary, ResumeSummary resumeSummary) {
		if (experienceSummary != null && experienceSummary.Description != null
				&& !experienceSummary.Description.isEmpty()) {
			resumeSummary.setExperienceSummary(experienceSummary.Description);
			System.out.println("Experience Summary: " + experienceSummary.Description);
		} else {
			System.out.println("Experience Summary: Not Available");
		}
	}

	static void setQualificationsSummary(String qualificationsSummary, ResumeSummary resumeSummary) {
		if (qualificationsSummary != null && !qualificationsSummary.isEmpty()) {
			resumeSummary.setQualificationsSummary(qualificationsSummary);
			System.out.println("Qualifications Summary: " + qualificationsSummary);
		} else {
			System.out.println("Qualifications Summary: Not Available");
		}
	}

	// Helper method to get the file extension from the original filename
	private static String getFileExtension(String originalFilename) {
		if (originalFilename == null) {
			return ".tmp";
		}
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex == -1) {
			return ".tmp";
		}
		return originalFilename.substring(dotIndex);
	}
}

/*
 * package com.njpa.resume.parser;
 * 
 * import java.io.IOException; import java.time.format.DateTimeFormatter;
 * 
 * import org.json.JSONArray; import org.json.JSONObject; import
 * org.springframework.stereotype.Service;
 * 
 * import com.textkernel.tx.DataCenter; import com.textkernel.tx.TxClient;
 * import com.textkernel.tx.exceptions.TxException; import
 * com.textkernel.tx.models.Document; import com.textkernel.tx.models.TxDate;
 * import com.textkernel.tx.models.api.parsing.ParseOptions; import
 * com.textkernel.tx.models.api.parsing.ParseRequest; import
 * com.textkernel.tx.models.api.parsing.ParseResumeResponse; import
 * com.textkernel.tx.models.resume.PersonalAttributes; import
 * com.textkernel.tx.models.resume.contactinfo.ContactInformation; import
 * com.textkernel.tx.models.resume.contactinfo.WebAddressType; import
 * com.textkernel.tx.models.resume.education.EducationDetails; import
 * com.textkernel.tx.models.resume.education.EducationHistory; import
 * com.textkernel.tx.models.resume.employment.EmploymentHistory; import
 * com.textkernel.tx.models.resume.employment.Position;
 * 
 * @Service public class NgsResumeParser { static JSONArray jsonArray = null;
 * static JSONObject jsonObject = null;
 * 
 * // public static void main(String[] args) throws IOException { public static
 * JSONArray name() throws IOException { TxClient client = new
 * TxClient("26990900", "sXs9tx8C8tkqReR3YSz6qgR8JzOXE26ldQqB2VdR",
 * DataCenter.US); jsonArray = new JSONArray(); // A Document is an unparsed
 * File (PDF, Word Doc, etc) Document doc = new
 * Document("C:\\Users\\NGS105\\Downloads\\Uday Kiran T Java&Spring boot Resume.docx"
 * );
 * 
 * // when you create a ParseRequest, you can specify many configuration
 * settings // in the ParseOptions. See //
 * https://developer.textkernel.com/tx-platform/v10/resume-parser/api/
 * ParseRequest request = new ParseRequest(doc, new ParseOptions());
 * ParseResumeResponse response = null; try { response =
 * client.parseResume(request); // if we get here, it was 200-OK and all
 * operations succeeded System.out.println(response.toString());
 * 
 * // now we can use the response to output some of the data from the resume
 * printBasicResumeInfo(response); } catch (TxException e) { // the document
 * could not be parsed, always try/catch for TxExceptions when // using TxClient
 * System.out.println( "Code: " + e.HttpStatusCode + ", Error: " + e.TxErrorCode
 * + ", Message: " + e.getMessage()); } return jsonArray; }
 * 
 * static void printBasicResumeInfo(ParseResumeResponse response) { if (response
 * != null && response.Value != null && response.Value.ResumeData != null) {
 * printContactInfo(response.Value.ResumeData.ContactInformation);
 * printPersonalInfo(response.Value.ResumeData.PersonalAttributes);
 * printWorkHistory(response.Value.ResumeData.EmploymentHistory);
 * printEducation(response.Value.ResumeData.Education); } }
 * 
 * static void printHeader(String headerName) { jsonObject = new JSONObject();
 * jsonObject.put("headerName", headerName); jsonArray.put(jsonObject);
 * System.out.println( System.lineSeparator() + System.lineSeparator() +
 * "--------------- " + headerName + " ---------------"); }
 * 
 * static void printContactInfo(ContactInformation contactInfo) { jsonObject =
 * new JSONObject();
 * 
 * if (contactInfo != null) { printHeader("CONTACT INFORMATION");
 * 
 * // Candidate Name if (contactInfo.CandidateName != null &&
 * contactInfo.CandidateName.FormattedName != null) {
 * jsonObject.put("FormattedName", contactInfo.CandidateName.FormattedName);
 * System.out.println("Name: " + contactInfo.CandidateName.FormattedName); }
 * 
 * // Email Addresses if (contactInfo.EmailAddresses != null &&
 * !contactInfo.EmailAddresses.isEmpty()) { jsonObject.put("EmailAddresses",
 * contactInfo.EmailAddresses.get(0)); System.out.println("Email: " +
 * contactInfo.EmailAddresses.get(0)); } else {
 * System.out.println("Email: Not Available"); }
 * 
 * // Telephone Numbers if (contactInfo.Telephones != null &&
 * !contactInfo.Telephones.isEmpty()) { jsonObject.put("Telephones",
 * contactInfo.Telephones.get(0)); System.out.println("Phone: " +
 * contactInfo.Telephones.get(0)); } else {
 * System.out.println("Phone: Not Available"); }
 * 
 * // Location if (contactInfo.Location != null) { if
 * (contactInfo.Location.Municipality != null) { jsonObject.put("Municipality",
 * contactInfo.Location.Municipality); System.out.println("City: " +
 * contactInfo.Location.Municipality); } if (contactInfo.Location.Regions !=
 * null && !contactInfo.Location.Regions.isEmpty()) { jsonObject.put("Region",
 * contactInfo.Location.Regions.get(0)); System.out.println("Region: " +
 * contactInfo.Location.Regions.get(0)); } if (contactInfo.Location.CountryCode
 * != null) { jsonObject.put("Country", contactInfo.Location.CountryCode);
 * System.out.println("Country: " + contactInfo.Location.CountryCode); } }
 * 
 * // LinkedIn Address if (contactInfo.WebAddresses != null) {
 * contactInfo.WebAddresses.stream() .filter(w ->
 * w.Type.equals(WebAddressType.LinkedIn.Value)) .findFirst() .ifPresent(w -> {
 * jsonObject.put("LinkedIn", w.Address); System.out.println("LinkedIn: " +
 * w.Address); }); } } else {
 * System.out.println("Contact Information: Not Available"); }
 * 
 * jsonArray.put(jsonObject); }
 * 
 * 
 * static void printPersonalInfo(PersonalAttributes personalInfo) { // personal
 * information (only some examples listed here, there are many others)
 * jsonObject = new JSONObject(); if (personalInfo != null) {
 * printHeader("PERSONAL INFORMATION");
 * 
 * if (personalInfo.DateOfBirth != null)
 * jsonObject.put("DateOfBirth",getTxDateAsString(personalInfo.DateOfBirth));
 * System.out.println("Date of Birth: " +
 * getTxDateAsString(personalInfo.DateOfBirth)); if (personalInfo.DrivingLicense
 * != null) jsonObject.put("DrivingLicense",personalInfo.DrivingLicense);
 * System.out.println("Driving License: " + personalInfo.DrivingLicense); if
 * (personalInfo.Nationality != null)
 * jsonObject.put("Nationality",personalInfo.Nationality);
 * System.out.println("Nationality: " + personalInfo.Nationality); if
 * (personalInfo.VisaStatus != null)
 * jsonObject.put("VisaStatus",personalInfo.VisaStatus);
 * System.out.println("Visa Status: " + personalInfo.VisaStatus); }
 * jsonArray.put(jsonObject); }
 * 
 * @SuppressWarnings("deprecation") static void
 * printWorkHistory(EmploymentHistory employment) { jsonObject = new
 * JSONObject();
 * 
 * if (employment != null) { printHeader("EXPERIENCE SUMMARY");
 * 
 * // Experience Summary if (employment.ExperienceSummary != null) {
 * jsonObject.put("Years Experience",
 * Math.round(employment.ExperienceSummary.MonthsOfWorkExperience / 12.0));
 * jsonObject.put("Avg Years Per Employer",
 * Math.round(employment.ExperienceSummary.AverageMonthsPerEmployer / 12.0));
 * jsonObject.put("Years Management Experience",
 * Math.round(employment.ExperienceSummary.MonthsOfManagementExperience /
 * 12.0));
 * 
 * System.out.println("Years Experience: " +
 * Math.round(employment.ExperienceSummary.MonthsOfWorkExperience / 12.0));
 * System.out.println("Avg Years Per Employer: " +
 * Math.round(employment.ExperienceSummary.AverageMonthsPerEmployer / 12.0));
 * System.out.println("Years Management Experience: " +
 * Math.round(employment.ExperienceSummary.MonthsOfManagementExperience /
 * 12.0)); }
 * 
 * // Loop through each position for (Position position : employment.Positions)
 * { jsonObject = new JSONObject(); // New JSON object for each position
 * 
 * System.out.println(System.lineSeparator() + "POSITION '" + position.Id +
 * "'"); jsonObject.put("Position ID", position.Id);
 * 
 * // Employer Check if (position.Employer != null && position.Employer.Name !=
 * null) { jsonObject.put("Employer", position.Employer.Name.Normalized);
 * System.out.println("Employer: " + position.Employer.Name.Normalized); } else
 * { jsonObject.put("Employer", "Not Available");
 * System.out.println("Employer: Not Available"); }
 * 
 * // Job Title if (position.JobTitle != null) { jsonObject.put("Title",
 * position.JobTitle.Normalized); System.out.println("Title: " +
 * position.JobTitle.Normalized); } else { jsonObject.put("Title",
 * "Not Available"); System.out.println("Title: Not Available"); }
 * 
 * // Date Range String startDate = getTxDateAsString(position.StartDate);
 * String endDate = getTxDateAsString(position.EndDate);
 * jsonObject.put("Date Range", startDate + " - " + endDate);
 * System.out.println("Date Range: " + startDate + " - " + endDate);
 * 
 * // Add position details to the JSON array jsonArray.put(jsonObject); } } }
 * 
 * 
 * static void printEducation(EducationHistory education) { jsonObject = new
 * JSONObject(); // New JSON object for education data
 * 
 * // Basic education display if (education != null) {
 * printHeader("EDUCATION SUMMARY");
 * 
 * // Check and print the highest degree if (education.HighestDegree != null &&
 * education.HighestDegree.Name != null) { jsonObject.put("Highest Degree",
 * education.HighestDegree.Name.Normalized);
 * System.out.println("Highest Degree: " +
 * education.HighestDegree.Name.Normalized); }
 * 
 * // Iterate through education details for (EducationDetails edu :
 * education.EducationDetails) { jsonObject = new JSONObject(); // New object
 * for each education entry System.out.println(System.lineSeparator() +
 * "EDUCATION '" + edu.Id + "'"); jsonObject.put("Education ID", edu.Id);
 * 
 * // Check and print school name if (edu.SchoolName != null) {
 * jsonObject.put("School", edu.SchoolName.Normalized);
 * System.out.println("School: " + edu.SchoolName.Normalized); }
 * 
 * // Check and print degree if (edu.Degree != null && edu.Degree.Name != null)
 * { jsonObject.put("Degree", edu.Degree.Name.Normalized);
 * System.out.println("Degree: " + edu.Degree.Name.Normalized); }
 * 
 * // Check and print majors (focus areas) if (edu.Majors != null) {
 * jsonObject.put("Focus", String.join(", ", edu.Majors));
 * System.out.println("Focus: " + String.join(", ", edu.Majors)); }
 * 
 * // Check and print GPA if available if (edu.GPA != null) {
 * jsonObject.put("GPA", edu.GPA.NormalizedScore + "/1.0 (" + edu.GPA.Score +
 * "/" + edu.GPA.MaxScore + ")"); System.out.println("GPA: " +
 * edu.GPA.NormalizedScore + "/1.0 (" + edu.GPA.Score + "/" + edu.GPA.MaxScore +
 * ")"); } else { jsonObject.put("GPA", "Not Available");
 * System.out.println("GPA: Not Available"); }
 * 
 * // Check graduation status and print end date String endDateRepresents =
 * (edu.Graduated != null && edu.Graduated.Value) ? "Graduated" :
 * "Last Attended"; jsonObject.put(endDateRepresents,
 * getTxDateAsString(edu.EndDate)); System.out.println(endDateRepresents + ": "
 * + getTxDateAsString(edu.EndDate));
 * 
 * // Add each education detail to the JSON array jsonArray.put(jsonObject); } }
 * }
 * 
 * 
 * static String getTxDateAsString(TxDate date) { // a TxDate represents a date
 * found on a resume, so it can either be // 'current', as in
 * "July 2018 - current" // a year, as in "2018 - 2020" // a year and month, as
 * in "2018/06 - 2020/07" // a year/month/day, as in "5/4/2018 - 7/2/2020" if
 * (date == null) return ""; if (date.IsCurrentDate) return "current";
 * 
 * String format = "yyyy";
 * 
 * if (date.FoundMonth) { format += "-MM";// only print the month if it was
 * actually found on the resume/job
 * 
 * if (date.FoundDay) format += "-dd";// only print the day if it was actually
 * found }
 * 
 * return date.Date.format(DateTimeFormatter.ofPattern(format)); } }
 */