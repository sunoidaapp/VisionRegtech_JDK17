package com.vision.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.MailSchedulerDao;
import com.vision.dao.RgSchedulerDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.ValidationUtil;
import com.vision.vb.EmailProcessControlVb;

@Component
public class MailAlertScheduler {
//
	@Value("${email.alerts}")
	private String mailAlertFlag;

	@Autowired
	RgSchedulerDao rgSchedulerDao;
	@Autowired
	MailSchedulerDao mailSchedulerDao;
	@Autowired
	CommonDao commonDao;

	public static Logger logger = LoggerFactory.getLogger(MailAlertScheduler.class);

	@Scheduled(fixedRate = 30000) // every 30 seconds
	@Async
	public void dosubmit() {
		ExceptionCode exceptionCode = new ExceptionCode();

		if (ValidationUtil.isValid(mailAlertFlag) && "Y".equalsIgnoreCase(mailAlertFlag)) {
			List<EmailProcessControlVb> templateSchedulelst = mailSchedulerDao.getEmailScheduleDetails("YTP");
			if (templateSchedulelst != null && !templateSchedulelst.isEmpty()) {
				for (EmailProcessControlVb mailAlertVb : templateSchedulelst) {

					String mailAlert = commonDao.findVisionVariableValue("RG_MAIL_ALERT_FLAG");
					mailAlert = ValidationUtil.isValid(mailAlert) ? mailAlert : "N";
					if (mailAlert.equalsIgnoreCase("Y")) {
						List<String> recipients = new ArrayList<>();
						if (ValidationUtil.isValid(mailAlertVb.getMailGroupIds())) {
							String[] mailidlst = mailAlertVb.getMailGroupIds().split(",");
							for (String email : mailidlst) {
								recipients.add(email.trim().toLowerCase());
							}

						}
//						System.out.println("Sending Alert email to: " + mailAlertVb.getMailGroupIds());
//						logger.info("Sending Alert email to: " + mailAlertVb.getMailGroupIds());
						mailSchedulerDao.alertMail(mailAlertVb, recipients);
						mailSchedulerDao.updateEmailProcessAudit(mailAlertVb, "S");
					}
				}
			}
		} else {
			logger.info("No eligible TemplateSchedule found or Mail Alert Flag OFF: {}", mailAlertFlag);
		}
	}

}
