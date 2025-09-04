package com.vision.wb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.TemplateScheduleCronDao;
import com.vision.dao.TemplateScheduleDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateScheduleVb;

@Component
@EnableAsync
public class TemplateScheduleCron {

    private static final Logger logger = LoggerFactory.getLogger(TemplateScheduleCron.class);

    @Value("${schedule.rgBuild}")
    private String rgBuildFlag;

    @Autowired
    TemplateScheduleCronDao templateScheduleCronDao;

    @Autowired
    TemplateScheduleDao templateScheduleDao;

    // ✅ Lazy injection to avoid circular dependency
    @Autowired
    @Lazy
    private TemplateScheduleWb templateScheduleWb;

    @Autowired
    CommonDao commonDao;

    /**
     * ✅ 1️⃣ BATCH SUBMIT CRON
     * Runs every 5 minutes.
     * Does batch submission if RG_AUTO_SUBMIT_FLAG = Y.
     */
    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void tempSchCron() {
        String autoSubmit = commonDao.findVisionVariableValue("RG_AUTO_SUBMIT_FLAG");

        if (ValidationUtil.isValid(rgBuildFlag) && "Y".equalsIgnoreCase(rgBuildFlag)) {
            if (ValidationUtil.isValid(autoSubmit) && "Y".equalsIgnoreCase(autoSubmit)) {
                List<TemplateScheduleVb> collTemp = templateScheduleCronDao.getTemplateConfigDetail();

                if (collTemp != null && !collTemp.isEmpty()) {
                    templateScheduleWb.Submit(collTemp); // batch submit logic
                    collTemp.forEach(n -> logger.info("Batch Submitted Template ID: {}", n.getTemplateId()));
                } else {
                    logger.info("Auto Submit enabled but no records found. Flag: {}", autoSubmit);
                }
            } else {
                logger.info("Auto Submit flag OFF or invalid: {}", autoSubmit);
            }
        } else {
            logger.info("RG Build Flag OFF or invalid: {}", rgBuildFlag);
        }
    }

    /**
     * ✅ 2️⃣ SINGLE SUBMIT CRON
     * Runs every 30 seconds.
     * Picks one eligible record and processes it individually.
     */
    @Scheduled(fixedRate = 30000) // every 30 seconds
    @Async
    public void dosubmit() {
        ExceptionCode exceptionCode = new ExceptionCode();

        TemplateScheduleVb templateScheduleVb = templateScheduleDao.fetchTemplateSchedule();
        if (templateScheduleVb != null && ValidationUtil.isValid(rgBuildFlag) && "Y".equalsIgnoreCase(rgBuildFlag)) {
            TemplateConfigVb templateConfigVb = new TemplateConfigVb();
            templateConfigVb.setCountry(templateScheduleVb.getCountry());
            templateConfigVb.setLeBook(templateScheduleVb.getLeBook());
            templateConfigVb.setTemplateId(templateScheduleVb.getTemplateId());
            templateConfigVb.setReportingDate(templateScheduleVb.getReportingDate());
            templateConfigVb.setSubmissionDate(templateScheduleVb.getSubmissionDate());

            templateScheduleCronDao.updateProcessControlInternalStatus(0, templateScheduleVb);
            templateScheduleCronDao.updateTemplatesHeaderInternalStatus(0, templateScheduleVb);

            exceptionCode = templateScheduleWb.submission(templateConfigVb);

            String processStatus;
            if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
                processStatus = "SS";
                exceptionCode.setErrorMsg("Submission Success");
                logger.info("Single Submit Success for Template ID: {}", templateScheduleVb.getTemplateId());
            } else {
                processStatus = "SR";
                exceptionCode.setErrorMsg("Submission Failed");
                logger.warn("Single Submit Failed for Template ID: {}", templateScheduleVb.getTemplateId());
            }

            templateScheduleVb.setRecordIndicator(Constants.STATUS_ZERO);
            templateScheduleCronDao.updateProcessControl1(processStatus, templateScheduleVb, true);
            templateScheduleCronDao.updateProcessControlheader1(
                processStatus,
                (String) exceptionCode.getResponse1(),
                (String) exceptionCode.getResponse2(),
                templateScheduleVb
            );
        } else {
            logger.info("No eligible TemplateSchedule found or RG Build Flag OFF: {}", rgBuildFlag);
        }
    }

}
