package com.vision.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.MailSchedulerDao;
import com.vision.dao.TemplateScheduleDao;
import com.vision.vb.EmailProcessControlVb;

@Component
public class EscalationScheduler {

	@Autowired
	private MailSchedulerDao mailSchedulerDao;

	@Autowired
	private TemplateScheduleDao templateScheduleDao;

	@Value("${esclation.alerts}")
	private String esclationAlertFlag;

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	public static Logger logger = LoggerFactory.getLogger(EscalationScheduler.class);

	// Runs every 1 minute
	@Scheduled(cron = "0 */1 * * * *")
	public void runEscalationScheduler() {
//		System.out.println("\n\n================ ESCALATION SCHEDULER STARTED ================");
//		System.out.println("System Time: " + LocalDateTime.now().format(dtf));
//		logger.info("System Time: " + LocalDateTime.now().format(dtf));
		try {
			// Get all escalation templates
			if (esclationAlertFlag.equalsIgnoreCase("Y")) {
				List<EmailProcessControlVb> escList = mailSchedulerDao.getEscalationDetails();
//			System.out.println("Total escalation records found: " + escList.size());

				for (EmailProcessControlVb esc : escList) {

//				System.out.println("\n--------------------------------------------------");
//				System.out.println(
//						"Processing: " + esc.getCountry() + " | " + esc.getLeBook() + " | " + esc.getTemplateId());
//				System.out.println("Escalation Start Time: " + esc.getEsclationTime());
//				System.out.println("Alert Slot (minutes): " + esc.getEsclationAlertSlot());
//				System.out.println("Max Allowed (Counter): " + esc.getEsclationCount());
//
//				logger.info("Processing: " + esc.getCountry() + " | " + esc.getLeBook() + " | " + esc.getTemplateId());
//				logger.info("Escalation Start Time: " + esc.getEsclationTime());
//				logger.info("Alert Slot (minutes): " + esc.getEsclationAlertSlot());
//				logger.info("Max Allowed (Counter): " + esc.getEsclationCount());

					// Check if already exists in process control table
					EmailProcessControlVb dbRow = mailSchedulerDao.getProcessControlRow(esc);

					LocalDateTime nextEscTime;
					int iterationCount;

					if (dbRow == null) {
						// First-time escalation → insert row
//						System.out.println("First-time escalation → inserting into RG_EMAIL_PROCESS_CONTROLS");

						nextEscTime = toLocalDate(esc.getEsclationTime())
								.plusMinutes(getInt(esc.getEsclationAlertSlot()));
						iterationCount = 1;

						mailSchedulerDao.insertEmailProcessControl(esc, iterationCount, nextEscTime);
						esc.setIterationCount(iterationCount);
						esc.setNextEsclationtime(nextEscTime.format(dtf));

//						System.out.println(
//								"Inserted row with ITERATION_COUNT=1, NEXT_ESC_TIME=" + nextEscTime.format(dtf));

					} else {
						// Already exists → use DB values
						iterationCount = dbRow.getIterationCount();
						nextEscTime = toLocalDate(dbRow.getNextEsclationtime());
						esc.setIterationCount(iterationCount);
						esc.setNextEsclationtime(dbRow.getNextEsclationtime());

//						System.out.println("Existing row found → ITERATION_COUNT=" + iterationCount + ", NEXT_ESC_TIME="
//								+ nextEscTime.format(dtf));
					}

					int totalAllowed = getInt(esc.getEsclationCount());

					if (iterationCount > totalAllowed) {
//						System.out.println("Max escalation reached — skipping.");
						continue;
					}

					LocalDateTime sysTime = LocalDateTime.now();
//					System.out.println("System Time: " + sysTime.format(dtf));

					// Determine if it is time to send mail
					LocalDateTime sendTime = (iterationCount == 1) ? toLocalDate(esc.getEsclationTime()) : nextEscTime;

					if (sendTime.isAfter(sysTime)) {
//						System.out
//								.println("Not time yet for escalation (Next Send Time: " + sendTime.format(dtf) + ")");
						continue;
					}

					// ---------------------------
					// SEND ESCALATION EMAIL
					// ---------------------------
//					System.out.println("Sending escalation email to: " + esc.getMailGroupIds());
					List<String> recipients = List.of(esc.getMailGroupIds().split(","));
					mailSchedulerDao.alertMail(esc, recipients);

					// ---------------------------
					// UPDATE ITERATION COUNT + NEXT_ESC_TIME
					// ---------------------------
					int newCount = iterationCount + 1;
					LocalDateTime newNextTime = sendTime.plusMinutes(getInt(esc.getEsclationAlertSlot()));

//					System.out.println("Updating ITERATION_COUNT to: " + newCount);
//					System.out.println("New NEXT_ESC_TIME: " + newNextTime.format(dtf));

					mailSchedulerDao.UpdateEsclationCount(esc, newCount, newNextTime);

					// Insert audit only on first escalation
					if (newCount == 0 || newCount == 1) {
						templateScheduleDao.insertEscalationEmailAudit(esc, "E", 1);
//						System.out.println("Escalation audit inserted for first mail.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println("================ ESCALATION SCHEDULER ENDED ==================\n\n");
	}

	// Helper to parse int safely
	private int getInt(String v) {
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return 0;
		}
	}

	// Convert date string from DB to LocalDateTime
	private LocalDateTime toLocalDate(String v) {
		try {
			return LocalDateTime.parse(v.replace(" ", "T"));
		} catch (Exception e) {
			return null;
		}
	}
}
