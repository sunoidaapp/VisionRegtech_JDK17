package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.util.ValidationUtil;
import com.vision.vb.EmailProcessControlVb;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailSchedulerDao extends AbstractDao<EmailProcessControlVb> {

	@Autowired
	CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(MailSchedulerDao.class);

	public void alertMail(EmailProcessControlVb mailScheduleVb, List<String> recipients) {
		try {
			// Fetch mail configuration from Vision variables
			final String hostName = commonDao.findVisionVariableValue("RG_MAIL_HOST");
			final String mailPort = commonDao.findVisionVariableValue("RG_MAIL_PORT");
			final String authFlag = commonDao.findVisionVariableValue("RG_MAIL_SMTP_AUTH"); // "true" or "false"
			final String username = commonDao.findVisionVariableValue("RG_MAIL_ID");
			final String password = commonDao.findVisionVariableValue("RG_MAIL_PWD");
			final String tlsEnable = commonDao.findVisionVariableValue("RG_TLS_ENABLE");

//			System.out.println("Host Name" + hostName);
//			System.out.println("Mail Port" + mailPort);
//			System.out.println("Auth Flag" + authFlag);
//			System.out.println("User Name" + username);
//			System.out.println("Password " + password);
//
//			logger.info("Host Name" + hostName);
//			logger.info("Mail Port" + mailPort);
//			logger.info("Auth Flag" + authFlag);
//			logger.info("User Name" + username);
//			logger.info("Password " + password);
//			logger.info("tlsEnable " + tlsEnable);

			boolean useAuth = Boolean.parseBoolean(authFlag);

			// Mail properties
			Properties props = new Properties();
			props.put("mail.smtp.host", hostName);
			props.put("mail.smtp.port", mailPort);
			props.put("mail.smtp.starttls.enable", tlsEnable);
			props.put("mail.smtp.auth", String.valueOf(useAuth));

			Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			if (recipients.isEmpty()) {
				System.out.println(" No valid recipients found. Skipping mail.");
				logger.info(" No valid recipients found. Skipping mail.");
				return;
			}

			// Build message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", recipients)));
			String emailSubject = mailScheduleVb.getEmailSubject().replaceAll("#TEMPLATE_NAME#",
					mailScheduleVb.getTemplateDesc());
			emailSubject = emailSubject.replaceAll("#REPORTING_DATE#", mailScheduleVb.getReportingDate());

			message.setSubject(emailSubject);
//			String emailBody = mailScheduleVb.getEmailBody();
			String emailBody = replaceEmailHashVar(mailScheduleVb.getEmailBody());
			emailBody = emailBody.replaceAll("#TEMPLATE_NAME#", mailScheduleVb.getTemplateDesc());
			emailBody = emailBody.replaceAll("#REPORTING_DATE#", mailScheduleVb.getReportingDate());

			StringBuilder content = new StringBuilder();
			String nl = System.lineSeparator();

			content.append(emailBody).append("<br><br>")
					.append("This is a system generated mail, Please do not reply.");
//			.append("</body></html>");
//			content.append("<html><body>").append("<p>Dear Team, Good day.</p>")
//					.append("<p>Please find the templates attached below.</p>").append("<br>").append(emailBody)
//					.append("<br>").append("<hr>").append("<p style='color:gray;font-size:12px;'>")
//					.append("This is a system generated mail, Please do not reply.").append("</p>")
//					.append("</body></html>");

			message.setContent(content.toString(), "text/html; charset=UTF-8");

			if (useAuth) {
				Transport transport = session.getTransport("smtp");
				transport.connect(hostName, username, password);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} else {
				Transport.send(message);
			}
			System.out.println("Mail sent successfully  ");
//			logger.info(" Mail sent successfully to: " + recipients);
		} catch (Exception e) {
			updateEmailProcessAudit(mailScheduleVb, "F");
			e.printStackTrace(); // replace with
			System.out.println("Mail sent Failed  ");
			logger.error("Mail sending failed", e);
		}
	}

	private String replaceEmailHashVar(String emailBody) {

		List<String> staticList = new ArrayList<>();
		List<String> dynamicList = new ArrayList<>();

		// Extract placeholders
		Pattern pattern = Pattern.compile("(STATIC|DYNAMIC):(#[A-Z_0-9]+#)");
		Matcher matcher = pattern.matcher(emailBody);

		while (matcher.find()) {
			String type = matcher.group(1);
			String value = matcher.group(2);

			if ("DYNAMIC".equals(type)) {
				dynamicList.add(value);
			} else {
				staticList.add(value);
			}
		}

		// Process dynamic placeholders
		for (String replaceKey : dynamicList) {

			// Fetch value using parameterized SQL
			String sql = "SELECT KEY_VALUE FROM RG_EMAIL_HASH_VAR WHERE REPLACE_KEY = ?";
			String keyValue = getJdbcTemplate().queryForObject(sql, String.class, replaceKey);

			if (ValidationUtil.isValid(keyValue)) {
				// Now fetch actual data (keyValue contains SQL query text)
				String value = getJdbcTemplate().queryForObject(keyValue, String.class);
				value = ValidationUtil.isValid(value) ? value : "";

				// Escape replaceKey for regex
				String safeKey = Pattern.quote("DYNAMIC:" + replaceKey);

				// Replace safely
				emailBody = emailBody.replaceAll(safeKey, Matcher.quoteReplacement(value));
			}
		}

		return emailBody;
	}

	// Helper to avoid null values showing as "null"
	private String nullSafe(Object value) {
		return value != null ? value.toString() : "";
	}

	public List<EmailProcessControlVb> getEmailScheduleDetails(String emailTriggerStatus) {
		List<EmailProcessControlVb> collTemp = null;
		try {
			String query = "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "SELECT  t1.COUNTRY,t1.LE_BOOK,t1.TEMPLATE_ID,t4.TEMPLATE_DESCRIPTION , "
						+ dbFunctionFormats("t1.REPORTING_DATE", "DATE_FORMAT", null)
						+ "AS REPORTING_DATE ,t1.CATEGORY_TYPE, " + " t1.EVENT_TYPE,t1.EMAIL_GROUP,"
						+ "   DBMS_LOB.SUBSTR( t3.EMAIL_SUBJECT, 4000) AS EMAIL_SUBJECT ,"
						+ "   DBMS_LOB.SUBSTR( t3.EMAIL_BODY, 4000) AS EMAIL_BODY ,"
						+ "	t1.TEMPLATE_STATUS,t1.EMAIL_TRIGGER_STATUS,t1.STATUS,"
						+ "	 t1.RECORD_INDICATOR,t1.MAKER,t1.VERIFIER,t1.INTERNAL_STATUS,"
						+ " LISTAGG (DISTINCT t2.USER_EMAIL_ID, ', ') "
						+ "             WITHIN GROUP (ORDER BY t2.USER_EMAIL_ID)  AS MAIL_GROUP_IDS "
						+ " FROM RG_EMAIL_PROCESS_CONTROLS t1 " + "" + " "
						+ " JOIN  VISION_USERS t2  ON t1.EMAIL_GROUP = t2.USER_GROUP " + getDbFunction("PIPELINE")
						+ " '-' " + getDbFunction("PIPELINE") + " t2.USER_PROFILE" + " "
						+ " AND  t1.EMAIL_TRIGGER_STATUS ='" + emailTriggerStatus + "' AND  "
						+ dbFunctionFormats(" t1.REPORTING_DATE", "DATE_FORMAT", null) + " = "
						+ dbFunctionFormats(getDbFunction("SYSDATE") + "-1", "DATE_FORMAT", null) + ""
						+ " JOIN RG_EMAIL_ALERT_SUBJECT_DETAILS t3 " + "   ON t3.EVENT_TYPE = t1.TEMPLATE_STATUS"
						+ " JOIN RG_TEMPLATE_CONFIG t4  ON t4.COUNTRY = t1.COUNTRY AND t4.LE_BOOK =t1.LE_BOOK AND T4.TEMPLATE_ID 						=t1.TEMPLATE_ID "
						+ " GROUP BY t1.COUNTRY, " + "         t1.LE_BOOK, " + "         t1.TEMPLATE_ID, "
						+ "         REPORTING_DATE, " + "         t1.CATEGORY_TYPE, " + "         t1.EVENT_TYPE, "
						+ "         t1.EMAIL_GROUP, " + "        DBMS_LOB.SUBSTR( t3.EMAIL_SUBJECT, 4000) , "
						+ "        DBMS_LOB.SUBSTR( t3.EMAIL_BODY, 4000) , " + "         t1.TEMPLATE_STATUS, "
						+ "         t1.EMAIL_TRIGGER_STATUS, " + "         t1.STATUS, "
						+ "         t1.RECORD_INDICATOR, " + "         t1.MAKER, " + "         t1.VERIFIER, "
						+ "         t1.INTERNAL_STATUS ,t4.TEMPLATE_DESCRIPTION";
			} else {
				query = "SELECT  " + "   t1.COUNTRY,  " + "   t1.LE_BOOK,  " + "   t1.TEMPLATE_ID,  "
						+ "   t4.TEMPLATE_DESCRIPTION,  "
						+ "   CONVERT(VARCHAR, t1.REPORTING_DATE, 105) AS REPORTING_DATE,  " + "   t1.CATEGORY_TYPE,  "
						+ "   t1.EVENT_TYPE,  " + "   t1.EMAIL_GROUP,  "
						+ "   SUBSTRING(t3.EMAIL_SUBJECT, 1, 4000) AS EMAIL_SUBJECT,  "
						+ "   SUBSTRING(t3.EMAIL_BODY, 1, 4000) AS EMAIL_BODY,  " + "   t1.TEMPLATE_STATUS,  "
						+ "   t1.EMAIL_TRIGGER_STATUS,  " + "   t1.STATUS,  " + "   t1.RECORD_INDICATOR,  "
						+ "   t1.MAKER,  " + "   t1.VERIFIER,  " + "   t1.INTERNAL_STATUS,  "
						+ "   STRING_AGG(t2.USER_EMAIL_ID, ',') AS MAIL_GROUP_IDS  "
						+ " FROM RG_EMAIL_PROCESS_CONTROLS t1  " + "   " + "JOIN  " + "(  " + "   SELECT DISTINCT  "
						+ "          USER_EMAIL_ID,  " + "          USER_GROUP,  " + "          USER_PROFILE  "
						+ "   FROM VISION_USERS  " + ") t2  "
						+ "   ON t1.EMAIL_GROUP = t2.USER_GROUP + '-' + t2.USER_PROFILE  "
						+ "  AND t1.EMAIL_TRIGGER_STATUS = 'YTP'  " + "   "
						+ "  AND CONVERT(VARCHAR, t1.REPORTING_DATE, 105) =  "
						+ "      CONVERT(VARCHAR, DATEADD(DAY, -1, GETDATE()), 105)  "
						+ " JOIN RG_EMAIL_ALERT_SUBJECT_DETAILS t3  " + "    ON t3.EVENT_TYPE = t1.TEMPLATE_STATUS  "
						+ " JOIN RG_TEMPLATE_CONFIG t4  " + "    ON t4.COUNTRY = t1.COUNTRY  "
						+ "   AND t4.LE_BOOK = t1.LE_BOOK  " + "   AND t4.TEMPLATE_ID = t1.TEMPLATE_ID  " + "GROUP BY  "
						+ "   t1.COUNTRY,  " + "   t1.LE_BOOK,  " + "   t1.TEMPLATE_ID,  "
						+ "   t4.TEMPLATE_DESCRIPTION,  " + "   CONVERT(VARCHAR, t1.REPORTING_DATE, 105),  "
						+ "   t1.CATEGORY_TYPE,  " + "   t1.EVENT_TYPE,  " + "   t1.EMAIL_GROUP,  "
						+ "   SUBSTRING(t3.EMAIL_SUBJECT, 1, 4000),  " + "   SUBSTRING(t3.EMAIL_BODY, 1, 4000),  "
						+ "   t1.TEMPLATE_STATUS,  " + "   t1.EMAIL_TRIGGER_STATUS,  " + "   t1.STATUS,  "
						+ "   t1.RECORD_INDICATOR,  " + "   t1.MAKER,  " + "   t1.VERIFIER,  "
						+ "   t1.INTERNAL_STATUS";
			}
			collTemp = getJdbcTemplate().query(query, getDetailsForEmailAlertMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}

	protected RowMapper getDetailsForEmailAlertMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EmailProcessControlVb vObject = new EmailProcessControlVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setTemplateDesc(rs.getString("TEMPLATE_DESCRIPTION"));
				vObject.setCategoryType(rs.getString("CATEGORY_TYPE"));
				vObject.setReportingDate(rs.getString("REPORTING_DATE"));
				vObject.setEventType(rs.getString("EVENT_TYPE"));
				vObject.setEmailGroup(rs.getString("EMAIL_GROUP"));
				vObject.setEmailSubject(rs.getString("EMAIL_SUBJECT"));
				vObject.setEmailBody(rs.getString("EMAIL_BODY"));
				vObject.setTemplateStatus(rs.getString("TEMPLATE_STATUS"));
				vObject.setEmailtriggerStatus(rs.getString("EMAIL_TRIGGER_STATUS"));
				vObject.setDbStatus(rs.getInt("STATUS"));
				vObject.setDbStatus(rs.getInt("STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setMailGroupIds(rs.getString("MAIL_GROUP_IDS"));
				return vObject;
			}
		};
		return mapper;
	}

	public int updateEmailProcessAudit(EmailProcessControlVb mailAlertVb, String status) {
		try {
			String query = "UPDATE RG_EMAIL_PROCESS_CONTROLS SET EMAIL_TRIGGER_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID =? AND "
					+ " REPORTING_DATE =" + dateConvert + "" + " AND EVENT_TYPE = ?";
			Object[] args = { status, mailAlertVb.getCountry(), mailAlertVb.getLeBook(), mailAlertVb.getTemplateId(),
					mailAlertVb.getReportingDate(), mailAlertVb.getEventType() };
			return getJdbcTemplate().update(query, args);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public List<EmailProcessControlVb> getEscalationDetails() {
		List<EmailProcessControlVb> collTemp = null;
		try {
			String query = "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = " SELECT t1.COUNTRY,t1.LE_BOOK,t1.TEMPLATE_ID,t2.TEMPLATE_NAME," + " t2.SUBMISSION_DATE," + " "
						+ dbFunctionFormats("t2.REPORTING_DATE", "DATE_FORMAT", null)
						+ " as  REPORTING_DATE	,t1.ESCLATION_TIME,t1.ALERT_SLOT,t1.ESCLATION_GROUP,t1.ESCLATION_GROUP As EMAIL_GROUP ,"
						+ "   DBMS_LOB.SUBSTR( t4.EMAIL_SUBJECT, 4000) AS EMAIL_SUBJECT ,"
						+ "   DBMS_LOB.SUBSTR( t4.EMAIL_BODY, 4000) AS EMAIL_BODY ,"
						+ " LISTAGG (DISTINCT t3.USER_EMAIL_ID, ', ')  "
						+ "	 WITHIN GROUP (ORDER BY t3.USER_EMAIL_ID)  AS  						MAIL_GROUP_IDS,t1.ESCLATION_COUNTER,t1.CATEGORY_TYPE,T1.EVENT_TYPE "
						+ " FROM RG_EMAIL_ALERT t1 "
						+ " JOIN  RG_TEMPLATES_HEADER  t2 ON t1.COUNTRY =t2.COUNTRY AND t1.LE_BOOK = t2.LE_BOOK "
						+ " AND t1.TEMPLATE_ID = t2.TEMPLATE_ID  AND T2.STATUS !='SS' "
						+ " AND  t1.ESCLATION_TIME < SYSDATE  " + ""
						+ " JOIN VISION_USERS t3 ON  t1.ESCLATION_GROUP = (t3.USER_GROUP" + getDbFunction("PIPELINE")
						+ " '-' " + getDbFunction("PIPELINE") + " t3.USER_PROFILE )" + ""
						+ " JOIN  RG_EMAIL_ALERT_SUBJECT_DETAILS t4  ON t4.EVENT_TYPE =t1.EVENT_TYPE "
						+ " GROUP BY t1.COUNTRY,t1.LE_BOOK,t1.TEMPLATE_ID,"
						+ " T2.SUBMISSION_DATE,REPORTING_DATE,t1.ESCLATION_TIME,"
						+ " t1.ALERT_SLOT,t1.ESCLATION_GROUP ,t2.TEMPLATE_NAME,t1.ESCLATION_COUNTER,t1.CATEGORY_TYPE,T1.EVENT_TYPE ,"
						+ " DBMS_LOB.SUBSTR (t4.EMAIL_SUBJECT, 4000), " + " DBMS_LOB.SUBSTR (t4.EMAIL_BODY, 4000)";
			} else {
				query = " SELECT   " + "   t1.COUNTRY, " + "   t1.LE_BOOK, " + "   t1.TEMPLATE_ID, "
						+ "   t2.TEMPLATE_NAME, " + "   t2.SUBMISSION_DATE, "
						+ "   CONVERT(VARCHAR, t2.REPORTING_DATE, 105) AS REPORTING_DATE, " + "   t1.ESCLATION_TIME, "
						+ "   t1.ALERT_SLOT, " + "   t1.ESCLATION_GROUP, " + "   t1.ESCLATION_GROUP AS EMAIL_GROUP, "
						+ "   SUBSTRING(t4.EMAIL_SUBJECT, 1, 4000) AS EMAIL_SUBJECT, "
						+ "   SUBSTRING(t4.EMAIL_BODY, 1, 4000)    AS EMAIL_BODY, "
						+ "   STRING_AGG(t3.USER_EMAIL_ID, ',')    AS MAIL_GROUP_IDS, " + "   t1.ESCLATION_COUNTER, "
						+ "   t1.CATEGORY_TYPE, " + "   t1.EVENT_TYPE " + "FROM RG_EMAIL_ALERT t1 "
						+ "JOIN RG_TEMPLATES_HEADER t2 " + "      ON t1.COUNTRY     = t2.COUNTRY "
						+ "     AND t1.LE_BOOK     = t2.LE_BOOK " + "     AND t1.TEMPLATE_ID = t2.TEMPLATE_ID "
						+ "     AND t2.STATUS     <> 'SS' " + "     AND t1.ESCLATION_TIME < GETDATE() " + "  "
						+ "JOIN ( " + "       SELECT DISTINCT " + "              USER_EMAIL_ID, "
						+ "              USER_GROUP, " + "              USER_PROFILE " + "       FROM VISION_USERS "
						+ "    ) t3 " + "    ON t1.ESCLATION_GROUP = t3.USER_GROUP + '-' + t3.USER_PROFILE " + "  "
						+ "JOIN RG_EMAIL_ALERT_SUBJECT_DETAILS t4 " + "    ON t4.EVENT_TYPE = t1.EVENT_TYPE " + "  "
						+ "GROUP BY " + "   t1.COUNTRY, " + "   t1.LE_BOOK, " + "   t1.TEMPLATE_ID, "
						+ "   t2.TEMPLATE_NAME, " + "   t2.SUBMISSION_DATE, "
						+ "   CONVERT(VARCHAR, t2.REPORTING_DATE, 105), " + "   t1.ESCLATION_TIME, "
						+ "   t1.ALERT_SLOT, " + "   t1.ESCLATION_GROUP, " + "   t1.ESCLATION_COUNTER, "
						+ "   t1.CATEGORY_TYPE, " + "   t1.EVENT_TYPE, " + "   SUBSTRING(t4.EMAIL_SUBJECT, 1, 4000), "
						+ "   SUBSTRING(t4.EMAIL_BODY, 1, 4000) ";
			}
			collTemp = getJdbcTemplate().query(query, getEscalationDetailMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}

	protected RowMapper getEscalationDetailMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EmailProcessControlVb vObject = new EmailProcessControlVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setTemplateDesc(rs.getString("TEMPLATE_NAME"));
				vObject.setReportingDate(rs.getString("REPORTING_DATE"));
				vObject.setEsclationTime(rs.getString("ESCLATION_TIME"));
				vObject.setEsclationAlertSlot(rs.getString("ALERT_SLOT"));
				vObject.setSubmissionDate(rs.getString("SUBMISSION_DATE"));
				vObject.setEsclationGroup(rs.getString("ESCLATION_GROUP"));
				vObject.setMailGroupIds(rs.getString("MAIL_GROUP_IDS"));
				vObject.setEsclationCount(rs.getString("ESCLATION_COUNTER"));
				vObject.setCategoryType(rs.getString("CATEGORY_TYPE"));
				vObject.setEventType(rs.getString("EVENT_TYPE"));
				vObject.setEmailSubject(rs.getString("EMAIL_SUBJECT"));
				vObject.setEmailBody(rs.getString("EMAIL_BODY"));
				vObject.setEmailGroup(rs.getString("EMAIL_GROUP"));
				return vObject;
			}
		};
		return mapper;
	}

	public EmailProcessControlVb getProcessControlRow(EmailProcessControlVb templateAlertdetail) {
		try {
			// Ensure the reporting date is trimmed
			String reportingDate = templateAlertdetail.getReportingDate().trim();

			String query = " SELECT NEXT_ESCLATION_TIME, ITERATION_COUNT" + " FROM RG_EMAIL_PROCESS_CONTROLS "
					+ "WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? " + " AND REPORTING_DATE = " + dateConvert
					+ " AND ITERATION_COUNT !=0  AND EVENT_TYPE =?  ";

			Object[] args = { templateAlertdetail.getCountry(), templateAlertdetail.getLeBook(),
					templateAlertdetail.getTemplateId(), reportingDate, templateAlertdetail.getEventType() };

			return getJdbcTemplate().queryForObject(query, args, (rs, rowNum) -> {
				EmailProcessControlVb v = new EmailProcessControlVb();
				v.setNextEsclationtime(rs.getString("NEXT_ESCLATION_TIME"));
				v.setIterationCount(rs.getInt("ITERATION_COUNT"));
				return v;
			});

		} catch (Exception e) {
			// Row not found is expected for first escalation
			System.out.println("No process control row found for template: " + templateAlertdetail.getTemplateId());
			return null;
		}
	}

	public String getNextEsclationTime(EmailProcessControlVb templateAlertdetail) {
		try {
			// Always ensure the format is correct
			String reportingDate = templateAlertdetail.getReportingDate().trim();

			String query = "SELECT Count (*) " + "FROM RG_EMAIL_PROCESS_CONTROLS " + "WHERE COUNTRY = ? "
					+ "  AND LE_BOOK = ? " + "  AND TEMPLATE_ID = ? "
					+ "  AND REPORTING_DATE = TO_DATE(?, 'DD-MM-YYYY')";

			Object[] args = { templateAlertdetail.getCountry(), templateAlertdetail.getLeBook(),
					templateAlertdetail.getTemplateId(), reportingDate };

			return getJdbcTemplate().queryForObject(query, args, String.class);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int UpdateEsclationCount(EmailProcessControlVb mailAlertVb, int cnt, LocalDateTime nextEsclation) {
		String query = "UPDATE RG_EMAIL_PROCESS_CONTROLS SET ITERATION_COUNT = ? , SEQUENCE_NO =?,NEXT_ESCLATION_TIME = ?, EMAIL_TRIGGER_STATUS ='S'  "
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID =? AND " + " "
				+ dbFunctionFormats("REPORTING_DATE", "DATE_FORMAT", null) + " =" + dateConvert + ""
				+ " AND TEMPLATE_STATUS = ?   ";
		Object[] args = { cnt, cnt, java.sql.Timestamp.valueOf(nextEsclation), mailAlertVb.getCountry(),
				mailAlertVb.getLeBook(), mailAlertVb.getTemplateId(), mailAlertVb.getReportingDate(),
				mailAlertVb.getEventType() };
		return getJdbcTemplate().update(query, args);
	}

	public int insertEmailProcessControl(EmailProcessControlVb esc, int iterationCnt, LocalDateTime nextTime) {
		try {
			String query = "INSERT INTO RG_EMAIL_PROCESS_CONTROLS ("
					+ "COUNTRY, LE_BOOK, TEMPLATE_ID, REPORTING_DATE, CATEGORY_TYPE_AT, CATEGORY_TYPE, "
					+ "EVENT_TYPE_AT, EVENT_TYPE, EMAIL_GROUP, EMAIL_SUBJECT, EMAIL_BODY, "
					+ "TEMPLATE_STATUS_AT, TEMPLATE_STATUS, EMAIL_TRIGGER_STATUS_AT, EMAIL_TRIGGER_STATUS, "
					+ "STATUS_NT, STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, "
					+ "INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, NEXT_ESCLATION_TIME, ITERATION_COUNT, SEQUENCE_NO"
					+ ") VALUES (" + "?, ?, ?, " + dateConvert
					+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getDbFunction("SYSDATE") + ", "
					+ getDbFunction("SYSDATE") + ", ?, ?, ?" + ")";

			Object[] args = { esc.getCountry(), esc.getLeBook(), esc.getTemplateId(), esc.getReportingDate(), // String
																												// in
																												// DD-MM-YYYY
					esc.getCategoryTypeAt(), // NUMBER
					esc.getCategoryType(), // VARCHAR
					esc.getEventTypeAt(), // NUMBER
					esc.getEventType(), // VARCHAR
					esc.getEmailGroup(), // VARCHAR
					esc.getEmailSubject(), // CLOB
					esc.getEmailBody(), // CLOB
					esc.getTemplateStatusAt(), // VARCHAR
					esc.getEventType(), // VARCHAR
					esc.getEmailtriggerStatusAt(), // VARCHAR
					"S", // VARCHAR
					esc.getStatusNt(), // NUMBER
					esc.getDbStatus(), // NUMBER
					esc.getRecordIndicatorNt(), // NUMBER
					esc.getRecordIndicator(), // NUMBER
					esc.getMaker(), // NUMBER
					esc.getVerifier(), // NUMBER
					esc.getInternalStatus(), // NUMBER
					java.sql.Timestamp.valueOf(nextTime), // NEXT_ESC_TIME
					iterationCnt, // ITERATION_COUNT = 0
					iterationCnt // SEQUENCE_NO
			};

			System.out.println("Inserting first escalation row for template: " + esc.getTemplateId());
			return getJdbcTemplate().update(query, args);

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
