package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;
import com.vision.vb.EmailScheduleVb;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailScheduleDao extends AbstractDao<EmailScheduleVb> {

	@Autowired
	CommonDao commonDao;

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EmailScheduleVb emailScheduleVb = new EmailScheduleVb();

				emailScheduleVb.setCountry(rs.getString("COUNTRY"));
				emailScheduleVb.setLeBook(rs.getString("LE_BOOK"));
				emailScheduleVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				emailScheduleVb.setSheduleDate(rs.getString("SCHEDULE_DATE"));
				emailScheduleVb.setMailTgDate(rs.getTimestamp("MAIL_TRIGGRED_DATE"));
				emailScheduleVb.setEsMailTgDate(rs.getTimestamp("ES_MAIL_TRIGGERED_DATE"));
				emailScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				emailScheduleVb.setEmailTo(rs.getString("EMAIL"));
				emailScheduleVb.setEmailCc(rs.getString("EMAIL_CC"));
				emailScheduleVb.setEmailBcc(rs.getString("EMAIL_BCC"));
				emailScheduleVb.setEmailSubject(rs.getString("EMAIL_SUBJECT"));
				emailScheduleVb.setEmailHeader(rs.getString("EMAIL_HEADER"));
				emailScheduleVb.setEmailFooter(rs.getString("EMAIL_FOOTER"));
				emailScheduleVb.setEmailBody(rs.getString("EMAIL_BODY"));
				emailScheduleVb.setMailTgCount(rs.getInt("MAIL_TRIGGRED_COUNT"));
				emailScheduleVb.setMailEsCount(rs.getInt("ES_MAIL_TRIGGER_COUNT"));
				emailScheduleVb.setEsEmail(rs.getString("ESCALTION_EMAIL"));
				emailScheduleVb.setTemplateDesc(rs.getString("TEMPLATE_DESCRIPTION"));
				emailScheduleVb.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				return emailScheduleVb;
			}
		};
		return mapper;
	}

	public List<EmailScheduleVb> getDetails() {
		List<EmailScheduleVb> collTemp = null;

		String query = "SELECT T1.COUNTRY, T1.LE_BOOK, T1.TEMPLATE_ID, T1.MAIL_TRIGGRED_DATE, T1.ES_MAIL_TRIGGERED_DATE, T3.TEMPLATE_DESCRIPTION, "
				+ ",T1.SCHEDULE_DATE  " + pipeLine + " '  ' " + pipeLine + "T3.RG_SUBMISSION_TIME AS SCHEDULE_DATE ,"
				+ dateFormat + "(T2.REPORTING_DATE," + formatdate + ") AS REPORTING_DATE, "
				+ "T1.EMAIL, T1.EMAIL_CC, T1.EMAIL_BCC, " + "T1.EMAIL_SUBJECT, " + clobFormat
				+ "(T1.EMAIL_HEADER) EMAIL_HEADER, " + clobFormat + "(T1.EMAIL_FOOTER) EMAIL_FOOTER, " + clobFormat
				+ "(T1.EMAIL_BODY) EMAIL_BODY, "
				+ "  T1.MAIL_TRIGGRED_COUNT,T1.ESCALTION_EMAIL,T1.ES_MAIL_TRIGGER_COUNT,"
				+ " (SELECT ALPHA_SUBTAB_DESCRIPTION     FROM ALPHA_SUB_TAB  WHERE ALPHA_TAB = T3.PROCESS_FREQUENCY_AT    AND ALPHA_SUB_TAB = T3.PROCESS_FREQUENCY) AS PROCESS_FREQUENCY "
				+ "FROM RG_EMAIL_CONFIG T1, RG_PROCESS_CONTROLS T2,RG_TEMPLATE_CONFIG T3 "
				+ "                WHERE T1.COUNTRY = T2.COUNTRY  " + " AND T1.LE_BOOK = T2.LE_BOOK  "
				+ "                AND T1.TEMPLATE_ID = T3.TEMPLATE_ID "
				+ "                AND T1.COUNTRY = T3.COUNTRY " + " AND T1.LE_BOOK = T3.LE_BOOK "
				+ "                AND T2.REPORTING_DATE = T1.SCHEDULE_DATE "
				+ "                AND T1.TEMPLATE_ID = T2.TEMPLATE_ID  "
//				+ "                AND T1.MAIL_TRIGGRED_DATE <= "+systemDate
				+ "                  AND T1.SCHEDULE_DATE <=  " + systemDate
				+ "                AND T2.RG_PROCESS_STATUS != 'SS' ";

		collTemp = getJdbcTemplate().query(query, getDetailMapper());
		return collTemp;
	}

	public void doPreparemail(EmailScheduleVb emailScheduleVb) {
		List<EmailScheduleVb> collTemp = getDetails();
		String tgCountStr = commonDao.findVisionVariableValue("RG_EMAIL_TRRIGER_COUNT");
		String esCountStr = commonDao.findVisionVariableValue("RG_ES_EMAIL_TRRIGER_COUNT");
		int tgCount = ValidationUtil.isValid(tgCountStr) ? Integer.parseInt(tgCountStr) : 3;
		int esTgCount = ValidationUtil.isValid(esCountStr) ? Integer.parseInt(esCountStr) : 3;
		if (collTemp != null && !collTemp.isEmpty()) {
			if (true)// need to add one more condition to combine mails
			{
				collTemp.stream().forEach(n -> {
					if (tgCount < n.getMailTgCount()) {
						n.setEmailTo(n.getEsEmail());
						n.setEmailBcc("");
						n.setEmailCc("");
					}
					if (esTgCount > n.getMailEsCount()) {
						return;
					}
					mail(n);
				});
			}
		}
	}

	public void mail(EmailScheduleVb scheduleObj) {
		final String fromEmail = "aravind.v0246@gmail.com";
		final String password = "ocxqeiqbojassbjw";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");

		Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(scheduleObj.getEmailTo()));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(scheduleObj.getEmailCc()));
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(scheduleObj.getEmailBcc()));
			message.setSubject(scheduleObj.getEmailSubject());
			String data = emailData(scheduleObj);
			String fullContent = "<html><body>" + scheduleObj.getEmailHeader() + "<br>" + scheduleObj.getEmailBody()
					+ "<br><br>" + data + "<br>" + scheduleObj.getEmailFooter() + "</body></html>";

			message.setContent(fullContent, "text/html");
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void mail() {
//		String[] toEmails, ccEmails, bccEmails;
//		String toEmailsStr = "", ccEmailsStr = "", bccEmailsStr = "";
//
//		EmailScheduleVb dObj = new EmailScheduleVb();
//		List<EmailScheduleVb> collTemp = getDetails(dObj);
//
//		if (collTemp == null || collTemp.isEmpty()) {
//			System.out.println("No email schedule details found.");
//			return;
//		}
//
//		EmailScheduleVb scheduleObj = collTemp.get(0);
//		int mailTgCount = scheduleObj.getMailTgCount();
//		int esmailTgCount = scheduleObj.getMailEsCount();
//		String scheduleDateStr = scheduleObj.getSheduleDate();
//		String tgCountStr = commonDao.findVisionVariableValue("RG_EMAIL_TRRIGER_COUNT");
//		int tgCount = Integer.parseInt(tgCountStr);
//		String esCountStr = ValidationUtil.isValid(commonDao.findVisionVariableValue("RG_ES_EMAIL_TRRIGER_COUNT"))
//				? commonDao.findVisionVariableValue("RG_ES_EMAIL_TRRIGER_COUNT")
//				: "3";
//
//		int esCount = Integer.parseInt(esCountStr);
//
//		System.out.println("tgCount --> " + tgCount);
//
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyHH:mm:ss", Locale.ENGLISH);
//		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyyHH:mm:ss", Locale.ENGLISH);
//
//		try {
//
//			Date scheduleDate = sdf.parse(scheduleDateStr);
//
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(scheduleDate);
//
//			cal.add(Calendar.HOUR_OF_DAY, -1);
//			Date oneHourBeforeSchedule = cal.getTime();
//
//			cal.add(Calendar.MINUTE, -3); // 3 minutes before one hour before schedule
//			Date windowStart = cal.getTime();
//
//			cal.add(Calendar.MINUTE, 6); // 3 minutes after one hour before schedule
//			Date windowEnd = cal.getTime();
//
//			String dbDateStr = commonDao.getSystemDate();
//			Date dbDate = sdf1.parse(dbDateStr);
//
//			if (dbDate.after(windowStart) && dbDate.before(windowEnd) && dbDate.after(scheduleObj.getEsMailTgDate()) ) {
//				if (esCount > esmailTgCount) {
//
//					toEmails = collTemp.stream().map(EmailScheduleVb::getEsEmail).filter(Objects::nonNull).distinct()
//							.toArray(String[]::new);
//
//					toEmailsStr = joinEmails(toEmails);
//
//					scheduleObj.setMailEsCount(scheduleObj.getMailEsCount() + 1);
//
//					int escalationUpdated = updateEsclationProcessControlForAll(collTemp);
//					System.out.println("Escalation schedule rows updated: " + escalationUpdated);
//				}
//			} else if (tgCount > mailTgCount) {
//				// Get distinct emails for To, Cc, and Bcc
//				toEmails = collTemp.stream().map(EmailScheduleVb::getEmailTo).filter(Objects::nonNull).distinct()
//						.toArray(String[]::new);
//
//				ccEmails = collTemp.stream().map(EmailScheduleVb::getEmailCc).filter(Objects::nonNull).distinct()
//						.toArray(String[]::new);
//
//				bccEmails = collTemp.stream().map(EmailScheduleVb::getEmailBcc).filter(Objects::nonNull).distinct()
//						.toArray(String[]::new);
//
//				toEmailsStr = joinEmails(toEmails);
//				ccEmailsStr = joinEmails(ccEmails);
//				bccEmailsStr = joinEmails(bccEmails);
//				int updatedCount = updateProcessControlForAll(collTemp);
//				System.out.println(updatedCount + " rows updated.");
//			} else {
//				return;
//			}
//
//		} catch (Exception e) {
//			System.err.println("Date parsing error: " + e.getMessage());
//			e.printStackTrace();
//			return;
//		}
//
//		// Prepare and Send Email
//		final String fromEmail = "aravind.v0246@gmail.com";
//		final String password = "ocxqeiqbojassbjw";
//
//		Properties props = new Properties();
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.host", "smtp.gmail.com");
//		props.put("mail.smtp.port", "587");
//		props.put("mail.smtp.connectiontimeout", "10000");
//		props.put("mail.smtp.timeout", "10000");
//
//		Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(fromEmail, password);
//			}
//		});
//
//		try {
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(fromEmail));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailsStr));
//			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmailsStr));
//			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccEmailsStr));
//
//			String reportingDate = scheduleObj.getReportingDate();
//			String subject;
//			if (tgCount <= mailTgCount) {
//				subject = String.format("Escalation Mail: Templates not submitted for [%s]", reportingDate);
//
//			} else {
//				subject = String.format("%s [%s]", scheduleObj.getEmailSubject(), reportingDate);
//
//			}
//
//			message.setSubject(subject);
//
//			String data = emailData(dObj);
//			String fullContent = "<html><body>" + scheduleObj.getEmailHeader() + "<br>" + scheduleObj.getEmailBody()
//					+ "<br><br>" + data + "<br>" + scheduleObj.getEmailFooter() + "</body></html>";
//
//			message.setContent(fullContent, "text/html");
//			Transport.send(message);
//
//			System.out.println("Email sent successfully.");
//			System.out.println("Data: " + data);
//			System.out.println("Updatequery -> " + scheduleObj.getMailTgDate());
//
//		} catch (Exception e) {
//			System.err.println("Date parsing error: " + e.getMessage());
//			e.printStackTrace();
//			return;
//		}
//	}

	private String joinEmails(String[] emails) {
		if (emails.length > 1) {
			return String.join(",", emails);
		} else if (emails.length == 1) {
			return emails[0];
		} else {
			return "";
		}
	}

//	public String emailData(EmailScheduleVb emailScheduleVb) {
//		StringBuilder htmlTable = new StringBuilder();
//
//		String query = "SELECT  DISTINCT " + " T1.TEMPLATE_ID, " + " T2.TEMPLATE_DESCRIPTION, "
//				+ " (SELECT ALPHA_SUBTAB_DESCRIPTION " + "    FROM ALPHA_SUB_TAB "
//				+ "   WHERE ALPHA_TAB = T2.PROCESS_FREQUENCY_AT "
//				+ "     AND ALPHA_SUB_TAB = T2.PROCESS_FREQUENCY) AS PROCESS_FREQUENCY, "
//				+ " TO_CHAR(T1.SCHEDULE_DATE, 'DD-Mon-RRRR')  ||  t2.RG_SUBMISSION_TIME AS SCHEDULE_DATE "
//				+ "FROM RG_EMAIL_CONFIG T1, RG_TEMPLATE_CONFIG T2, RG_PROCESS_CONTROLS T3 "
//				+ "WHERE T1.TEMPLATE_ID = T2.TEMPLATE_ID " + "  AND T1.TEMPLATE_ID = T3.TEMPLATE_ID "
//				+ "  AND T1.COUNTRY = T2.COUNTRY " + "  AND T1.LE_BOOK = T2.LE_BOOK "
//				+ "  AND T3.REPORTING_DATE = T1.SCHEDULE_DATE " + "  AND T1.SCHEDULE_DATE <= SYSDATE "
//				+ "  AND T1.MAIL_TRIGGRED_DATE <= SYSDATE " + "ORDER BY T1.TEMPLATE_ID";
//
//		// HTML table header with color style
//
//		htmlTable.append(
//				"<table width=\"80%\" cellspacing=0 cellpadding=0 align=center style=\"font:normal 12px Arial,Helvetica,sans-serif;border:solid 4px "
//						+ "#006599;border-top:none\">\r\n" + "  <tbody><tr>\r\n"
//						+ "    <td valign=top><table width=\"100%\" cellspacing=0 cellpadding=0 border=0 >\r\n"
//						+ "      <tbody><tr>\r\n"
//						+ "        <td height=41 style=\"font:normal 14px Arial,Helvetica,sans-serif;color:#FFF;padding:0px 4px;background-color:#006599;\" ><strong> GDI Submission Status Report </strong></td>\r\n"
//						+ "      </tr>\r\n" + "    </tbody></table>\r\n"
//						+ "<table width=\"100%\" cellspacing=0 cellpadding=0 border=0 style=\"font-size:12px;border: 1px solid #dfe8f6 !important;\">\r\n"
//						+ "  <tbody>");
//
//		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
//
//		htmlTable.append("<tr style=\"background-color: #1a1c1b;\">");
//		for (String columnName : rows.get(0).keySet()) {
//			String formattedCloumn = convertToReadableFormat(columnName);
//			htmlTable.append(
//					"<th style ='background-color: #1E8B75;border-right: 1px solid #FFF;color: white; padding: 5px;text-align: center;border-collapse: collapse;'>")
//					.append(formattedCloumn).append("</th>");
//		}
//		htmlTable.append("</tr>");
//
//		int rowCount = 0;
//		for (Map<String, Object> row : rows) {
//			htmlTable.append("<tr>");
//			rowCount++;
//			String rowColor = (rowCount % 2 == 0) ? "#DFE8F6" : "#FFF";
//			for (Object value : row.values()) {
//				htmlTable.append("<td style = \"background-color: " + rowColor
//						+ "; font-size:11px !important;padding: 5px; text-align: left;border-right: 1px solid #CCC;border-collapse: collapse;\" >")
//						.append(value != null ? value.toString() : "").append("</td>");
//			}
//			htmlTable.append("</tr>");
//		}
//		htmlTable.append("</tbody></table></body></html>");
//
//		return htmlTable.toString();
//	}
	public String emailData(EmailScheduleVb emailScheduleVb) {
	    StringBuilder htmlTable = new StringBuilder();

	    // HTML table header with color style
	    htmlTable.append(
	            "<table width=\"80%\" cellspacing=0 cellpadding=0 align=center style=\"font:normal 12px Arial,Helvetica,sans-serif;border:solid 4px "
	                    + "#006599;border-top:none\">\r\n" + "  <tbody><tr>\r\n"
	                    + "    <td valign=top><table width=\"100%\" cellspacing=0 cellpadding=0 border=0 >\r\n"
	                    + "      <tbody><tr>\r\n"
	                    + "        <td height=41 style=\"font:normal 14px Arial,Helvetica,sans-serif;color:#FFF;padding:0px 4px;background-color:#006599;\" ><strong> GDI Submission Status Report </strong></td>\r\n"
	                    + "      </tr>\r\n" + "    </tbody></table>\r\n"
	                    + "<table width=\"100%\" cellspacing=0 cellpadding=0 border=0 style=\"font-size:12px;border: 1px solid #dfe8f6 !important;\">\r\n"
	                    + "  <tbody>");

	    // HTML Table header
	    htmlTable.append("<tr style=\"background-color: #1a1c1b;\">");
	    htmlTable.append("<th style ='background-color: #1E8B75;border-right: 1px solid #FFF;color: white; padding: 5px;text-align: center;border-collapse: collapse;'>")
	            .append("TEMPLATE_ID").append("</th>");
	    htmlTable.append("<th style ='background-color: #1E8B75;border-right: 1px solid #FFF;color: white; padding: 5px;text-align: center;border-collapse: collapse;'>")
	            .append("TEMPLATE_DESCRIPTION").append("</th>");
	    htmlTable.append("<th style ='background-color: #1E8B75;border-right: 1px solid #FFF;color: white; padding: 5px;text-align: center;border-collapse: collapse;'>")
	            .append("PROCESS_FREQUENCY").append("</th>");
	    htmlTable.append("<th style ='background-color: #1E8B75;border-right: 1px solid #FFF;color: white; padding: 5px;text-align: center;border-collapse: collapse;'>")
	            .append("SCHEDULE_DATE").append("</th>");
	    htmlTable.append("</tr>");

	    // Extract values from the single EmailScheduleVb object
	    htmlTable.append("<tr>");
	    htmlTable.append("<td style = \"background-color: #FFF; font-size:11px !important;padding: 5px; text-align: left;border-right: 1px solid #CCC;border-collapse: collapse;\" >")
	            .append(emailScheduleVb.getTemplateId()).append("</td>");
	    htmlTable.append("<td style = \"background-color: #FFF; font-size:11px !important;padding: 5px; text-align: left;border-right: 1px solid #CCC;border-collapse: collapse;\" >")
	            .append(emailScheduleVb.getTemplateDesc()).append("</td>");
	    htmlTable.append("<td style = \"background-color: #FFF; font-size:11px !important;padding: 5px; text-align: left;border-right: 1px solid #CCC;border-collapse: collapse;\" >")
	            .append(emailScheduleVb.getProcessFrequency()).append("</td>");
	    htmlTable.append("<td style = \"background-color: #FFF; font-size:11px !important;padding: 5px; text-align: left;border-right: 1px solid #CCC;border-collapse: collapse;\" >")
	            .append(emailScheduleVb.getSheduleDate()).append("</td>");
	    htmlTable.append("</tr>");

	    htmlTable.append("</tbody></table></body></html>");

	    return htmlTable.toString();
	}

	private String convertToReadableFormat(String columnName) {
		StringBuilder readableName = new StringBuilder();
		boolean nextUpperCase = true;

		for (char c : columnName.toCharArray()) {
			if (c == '_') {
				readableName.append(' ');
				nextUpperCase = true;
			} else {
				if (nextUpperCase) {
					readableName.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					readableName.append(Character.toLowerCase(c));
				}
			}
		}

		return readableName.toString();
	}

	public int updateProcessControlForAll(List<EmailScheduleVb> collTemp) {
		int rowsUpdated = 0;

		for (EmailScheduleVb emailScheduleVb : collTemp) {

			emailScheduleVb.setMailTgCount(emailScheduleVb.getMailTgCount() + 1);

			Calendar cal = Calendar.getInstance();
			cal.setTime(emailScheduleVb.getMailTgDate());

			cal.add(Calendar.HOUR_OF_DAY, 1);

			Timestamp updatedMailTgTimestamp = new Timestamp(cal.getTimeInMillis());

			String query = "UPDATE RG_EMAIL_CONFIG " + "SET MAIL_TRIGGRED_COUNT = ?, " + "MAIL_TRIGGRED_DATE = ?, "
					+ "WHERE COUNTRY = ? AND LE_BOOK = ? " + "AND SCHEDULE_DATE =  " + dateConvert
					+ "AND TEMPLATE_ID = ?";

			Object[] args = { emailScheduleVb.getMailTgCount(), updatedMailTgTimestamp, emailScheduleVb.getCountry(),
					emailScheduleVb.getLeBook(), emailScheduleVb.getReportingDate(), emailScheduleVb.getTemplateId() // Pass
																														// TEMPLATE_ID
																														// from
																														// the
																														// object
			};

			rowsUpdated += getJdbcTemplate().update(query, args);
		}

		return rowsUpdated;
	}

	public int updateEsclationProcessControlForAll(List<EmailScheduleVb> collTemp) {
		int rowsUpdated = 0;

		for (EmailScheduleVb emailScheduleVb : collTemp) {
			emailScheduleVb.setMailEsCount(emailScheduleVb.getMailEsCount() + 1);
			// Add 1 hour to the escalation mail trigger date
			Calendar cal = Calendar.getInstance();
			cal.setTime(emailScheduleVb.getEsMailTgDate());

			cal.add(Calendar.HOUR_OF_DAY, 1); // Add 1 hour

			Timestamp updatedEsMailTgTimestamp = new Timestamp(cal.getTimeInMillis());

			String query = "UPDATE RG_EMAIL_CONFIG " + "SET ES_MAIL_TRIGGER_COUNT = ?, ES_MAIL_TRIGGERED_DATE = ? "
					+ "WHERE COUNTRY = ? AND LE_BOOK = ? " + "AND SCHEDULE_DATE =  " + dateConvert
					+ "AND TEMPLATE_ID = ?";

			Object[] args = { emailScheduleVb.getMailEsCount(), updatedEsMailTgTimestamp, emailScheduleVb.getCountry(),
					emailScheduleVb.getLeBook(), emailScheduleVb.getReportingDate(), emailScheduleVb.getTemplateId() };

			rowsUpdated += getJdbcTemplate().update(query, args);
		}

		return rowsUpdated;
	}

}
