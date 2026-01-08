package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AdfControlsVb;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CronStatusVb;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.ReportsWriterVb;
import com.vision.vb.VisionUsersVb;

@Component
public class AdfSchedulesDao extends AbstractDao<AdfSchedulesVb> {

	@Autowired
	private AdfControlsDao adfControlsDao;
	
	@Autowired
	private AdfSchedulesDetailsDao adfSchedulesDetailsDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	@Override
	protected void setServiceDefaults(){
		serviceName = "AdfSchedules";
		serviceDesc = "ADF Schedules";
		tableName = "ADF_SCHEDULES";
		childTableName = "ADF_SCHEDULES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	
	public long getLastFetchIntervel(){
		String query = "SELECT round((sysdate - LAST_FETCH_TIME) * (24 * 60))  Fetch_Intvl " +
				" FROM BUILD_CRON_FETCH_DET ";
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			query = "SELECT DATEDIFF(MINUTE, LAST_FETCH_TIME,"+systemDate+")  Fetch_Intvl  FROM BUILD_CRON_FETCH_DET ";
		}
		try	{
			long count = getJdbcTemplate().queryForObject(query, long.class);
			return count;
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "strBufApprove is Null":query.toString()));
			return 3;
		}
	}
	protected RowMapper getQueryforAdfScheduleResultsMapper(String cmp){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
				adfSchedulesVb.setStakeHolder(rs.getString("STAKE_HOLDER"));
				adfSchedulesVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				if(cmp.equalsIgnoreCase("EXISTS")) {
					adfSchedulesVb.setSeriousErrorCnt(rs.getString("SERIOUS_ERROR_COUNT"));
					adfSchedulesVb.setWarningErrorCnt(rs.getString("WARNING_COUNT"));
				}
				adfSchedulesVb.setScheduledDate(rs.getString("SCHEDULED_DATE"));
				adfSchedulesVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				adfSchedulesVb.setSubmitterId(rs.getInt("SUBMITTER_ID"));
				adfSchedulesVb.setSubmitterName(rs.getInt("SUBMITTER_ID")+" - "+rs.getString("SUBMITTER_NAME"));
				adfSchedulesVb.setSupportContact(rs.getString("SUPPORT_CONTACT"));
				adfSchedulesVb.setNotify(rs.getString("NOTIFY"));
				adfSchedulesVb.setAdfScheduleStatusAt(rs.getInt("ADF_SCHEDULE_STATUS_AT"));
				adfSchedulesVb.setAdfScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
				adfSchedulesVb.setStatusDesc(rs.getString("ADF_SCHEDULE_STATUS_DESC"));
				adfSchedulesVb.setParallelProcsCount(rs.getInt("PARALLEL_PROCS_COUNT"));
				adfSchedulesVb.setRecurringFrequencyAt(rs.getInt("RECURRING_FREQUENCY_AT"));
				adfSchedulesVb.setRecurringFrequency(rs.getString("RECURRING_FREQUENCY"));
				adfSchedulesVb.setStartTime(ValidationUtil.isValid(rs.getString("START_TIME"))?rs.getString("START_TIME"):"");
				adfSchedulesVb.setEndTime(ValidationUtil.isValid(rs.getString("END_TIME"))?rs.getString("END_TIME"):"");
//				adfSchedulesVb.setEndTime(rs.getString("END_TIME"));
				adfSchedulesVb.setDuration(rs.getString("Duration"));
				adfSchedulesVb.setCountry(rs.getString("COUNTRY"));
				adfSchedulesVb.setLeBook(rs.getString("LE_BOOK"));
				adfSchedulesVb.setDateCreation(rs.getString("DATE_CREATION"));
				adfSchedulesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				adfSchedulesVb.setNode(rs.getString("NODE"));
				adfSchedulesVb.setNodeRequestTime(rs.getString("NODE_REQUEST_TIME"));
				adfSchedulesVb.setNodeOverrideTime(rs.getString("NODE_OVERRIDE_TIME"));
				adfSchedulesVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfSchedulesVb.setFeedDate(rs.getString("FEED_DATE"));
				adfSchedulesVb.setEodModifyFlag(rs.getString("EOD_INITIATED_FLAG"));
				adfSchedulesVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				adfSchedulesVb.setAdfType(rs.getString("ADF_TYPE"));
//				adfSchedulesVb.setAdfCustomStatus(rs.getString("STATUS"));
				adfSchedulesVb.setAdfCustomStatus(ValidationUtil.isValid(rs.getString("STATUS"))?rs.getString("STATUS"):"");
				adfSchedulesVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				if(ValidationUtil.isValid(rs.getString("FOR_LOG_START_TIME")))
					adfSchedulesVb.setLogStartTime(rs.getString("FOR_LOG_START_TIME"));
				if(ValidationUtil.isValid(rs.getString("MAKER")))
					adfSchedulesVb.setMakerId(rs.getInt("MAKER"));
				
				return adfSchedulesVb;
			}
		};
		return mapper;
	}
	protected RowMapper getQueryResultsMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
				adfSchedulesVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				adfSchedulesVb.setScheduledDate(rs.getString("SCHEDULED_DATE"));
				adfSchedulesVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				adfSchedulesVb.setSubmitterId(rs.getInt("SUBMITTER_ID"));
				adfSchedulesVb.setSupportContact(rs.getString("SUPPORT_CONTACT"));
				adfSchedulesVb.setNotify(rs.getString("NOTIFY"));
				adfSchedulesVb.setAdfScheduleStatusAt(rs.getInt("ADF_SCHEDULE_STATUS_AT"));
				adfSchedulesVb.setAdfScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
				adfSchedulesVb.setParallelProcsCount(rs.getInt("PARALLEL_PROCS_COUNT"));
				adfSchedulesVb.setRecurringFrequencyAt(rs.getInt("RECURRING_FREQUENCY_AT"));
				adfSchedulesVb.setRecurringFrequency(rs.getString("RECURRING_FREQUENCY"));
				adfSchedulesVb.setStartTime(rs.getString("START_TIME"));
				adfSchedulesVb.setEndTime(rs.getString("END_TIME"));
				adfSchedulesVb.setDuration(rs.getString("Duration"));
				adfSchedulesVb.setCountry(rs.getString("COUNTRY"));
				adfSchedulesVb.setLeBook(rs.getString("LE_BOOK"));
				adfSchedulesVb.setDateCreation(rs.getString("DATE_CREATION"));
				adfSchedulesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				adfSchedulesVb.setNode(rs.getString("NODE"));
				adfSchedulesVb.setNodeRequestTime(rs.getString("NODE_REQUEST_TIME"));
				adfSchedulesVb.setNodeOverrideTime(rs.getString("NODE_OVERRIDE_TIME"));
				adfSchedulesVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfSchedulesVb.setFeedDate(rs.getString("FEED_DATE"));
				adfSchedulesVb.setEodModifyFlag(rs.getString("EOD_INITIATED_FLAG"));
				adfSchedulesVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				adfSchedulesVb.setAdfType(rs.getString("ADF_TYPE"));
				adfSchedulesVb.setAdfCustomStatus(rs.getString("STATUS"));
				adfSchedulesVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				if(ValidationUtil.isValid(rs.getString("MAKER")))
					adfSchedulesVb.setMakerId(rs.getInt("MAKER"));
				
				return adfSchedulesVb;
			}
		};
		return mapper;
	}
	public List<AdfSchedulesVb> getQueryResults(AdfSchedulesVb dObj, int status){
		setServiceDefaults();
		Vector<Object> params = new Vector<Object>();
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		dObj.setMaxRecords(10000);
		StringBuffer strBufApprove = new StringBuffer();
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
		 strBufApprove = new StringBuffer( " SELECT  "
				 + "     TAppr.major_build,  "
				 + "     TAppr.maker,  "
				 + "     FORMAT(TAppr.scheduled_date, 'dd-MMM-yyyy HH:mm:ss') AS SCHEDULED_DATE,  "
				 + "     TAppr.adf_number,  "
				 + "     TAppr.parallel_procs_count,  "
				 + "     TAppr.notify,  "
				 + "     TAppr.support_contact,  "
				 + "     TAppr.submitter_id,  "
				 + "     v1.USER_NAME AS submitter_name,  "
				 + "     FORMAT((SELECT MIN(APC.start_time)  "
				 + "             FROM adf_process_control APC  "
				 + "             WHERE APC.adf_number = TAppr.adf_number), 'dd-MMM-yyyy HH:mm:ss') AS Start_Time,  "
				 + "     (SELECT MAX(APC.end_time)  "
				 + "      FROM adf_process_control APC  "
				 + "      WHERE APC.adf_number = TAppr.adf_number) AS End_Time,  "
				 + "     TAppr.adf_schedule_status_at,  "
				 + "     TAppr.adf_schedule_status,  "
				 + "     (SELECT alpha_subtab_description  "
				 + "      FROM alpha_sub_tab  "
				 + "      WHERE alpha_tab = 1204  "
				 + "        AND TAppr.adf_schedule_status = alpha_sub_tab) AS ADF_SCHEDULE_STATUS_DESC,  "
				 + "     FORMAT(TAppr.date_last_modified, 'dd-MMM-yyyy HH:mm:ss') AS DATE_LAST_MODIFIED,  "
				 + "     FORMAT(TAppr.date_creation, 'dd-MMM-yyyy HH:mm:ss') AS DATE_CREATION,  "
				 + "     TAppr.country AS COUNTRY,  "
				 + "     TAppr.country + ' - ' + LEB.leb_description AS STAKE_HOLDER,  "
				 + "     TAppr.le_book AS LE_BOOK,  "
				 + "     TAppr.recurring_frequency_at,  "
				 + "     TAppr.recurring_frequency,  "
				 + "     ISNULL(TAppr.node_override, TAppr.node_request) AS NODE,  "
				 + "     DATEDIFF(MINUTE, (SELECT MIN(APC.start_time)  "
				 + "                       FROM adf_process_control APC  "
				 + "                       WHERE APC.adf_number = TAppr.adf_number), GETDATE()) AS Duration,  "
				 + "     FORMAT(TAppr.node_request_time, 'dd-MMM-yyyy HH:mm:ss') AS NODE_REQUEST_TIME,  "
				 + "     FORMAT(TAppr.node_override_time, 'dd-MMM-yyyy HH:mm:ss') AS NODE_OVERRIDE_TIME,  "
				 + "     FORMAT(TAppr.business_date, 'dd-MMM-yyyy') AS BUSINESS_DATE,  "
				 + "     FORMAT(TAppr.feed_date, 'dd-MMM-yyyy') AS FEED_DATE,  "
				 + "     TAppr.frequency_process_at,  "
				 + "     TAppr.frequency_process,  "
				 + "     TAppr.acquisition_process_type_at,  "
				 + "     TAppr.acquisition_process_type,  "
				 + "     CASE  "
				 + "         WHEN (SELECT COUNT(1)  "
				 + "               FROM adf_process_control APC  "
				 + "               WHERE APC.adf_number = TAppr.adf_number  "
				 + "               AND (APC.eod_initiated_flag = 'Y'  "
				 + "               OR APC.auth_status IN (0, 4))) > 0  "
				 + "         THEN 'Y'  "
				 + "         ELSE 'N'  "
				 + "     END AS EOD_INITIATED_FLAG,  "
				 + "     CASE  "
				 + "         WHEN TAppr.adf_schedule_status = 'C'  "
				 + "              AND (CASE  "
				 + "                   WHEN (SELECT COUNT(1)  "
				 + "                         FROM adf_process_control APC  "
				 + "                         WHERE APC.adf_number = TAppr.adf_number  "
				 + "                           AND (APC.eod_initiated_flag = 'Y'  "
				 + "                                OR APC.auth_status IN (0, 4))) > 0  "
				 + "                   THEN 'Y'  "
				 + "                   ELSE 'N' END) = 'Y'  "
				 + "              AND (SELECT COUNT(1)  "
				 + "                   FROM adf_process_control APC  "
				 + "                   WHERE APC.adf_number = TAppr.adf_number  "
				 + "                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0  "
				 + "         THEN 'Completed (P) with Errors'  "
				 + "  "
				 + "         WHEN TAppr.adf_schedule_status = 'C'  "
				 + "              AND (CASE  "
				 + "                   WHEN (SELECT COUNT(1)  "
				 + "                         FROM adf_process_control APC  "
				 + "                         WHERE APC.adf_number = TAppr.adf_number  "
				 + "                           AND (APC.eod_initiated_flag = 'Y'  "
				 + "                                OR APC.auth_status IN (0, 4))) > 0  "
				 + "                   THEN 'Y'  "
				 + "                   ELSE 'N' END) = 'N'  "
				 + "              AND (SELECT COUNT(1)  "
				 + "                   FROM adf_process_control APC  "
				 + "                   WHERE APC.adf_number = TAppr.adf_number  "
				 + "                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0  "
				 + "         THEN 'Completed with Errors'  "
				 + "  "
				 + "         WHEN TAppr.adf_schedule_status = 'C'  "
				 + "              AND (CASE  "
				 + "                   WHEN (SELECT COUNT(1)  "
				 + "                         FROM adf_process_control APC  "
				 + "                         WHERE APC.adf_number = TAppr.adf_number  "
				 + "                           AND (APC.eod_initiated_flag = 'Y'  "
				 + "                                OR APC.auth_status IN (0, 4))) > 0  "
				 + "                   THEN 'Y'  "
				 + "                   ELSE 'N' END) = 'Y'  "
				 + "              AND (SELECT COUNT(1)  "
				 + "                   FROM adf_process_control APC  "
				 + "                   WHERE APC.adf_number = TAppr.adf_number  "
				 + "                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) = 0  "
				 + "         THEN 'Completed (P)'  "
				 + "  "
				 + "         ELSE (SELECT AA.alpha_subtab_description  "
				 + "               FROM alpha_sub_tab AA  "
				 + "               WHERE AA.alpha_tab = TAppr.adf_schedule_status_at  "
				 + "               AND AA.alpha_sub_tab = TAppr.adf_schedule_status)  "
				 + "     END AS STATUS,  "
				 + "     CASE  "
				 + "         WHEN TAppr.adf_schedule_status = 'C'  "
				 + "              AND (SELECT COUNT(1)  "
				 + "                   FROM adf_process_control APC  "
				 + "                   WHERE APC.adf_number = TAppr.adf_number  "
				 + "                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0  "
				 + "         THEN (SELECT COUNT(1)  "
				 + "               FROM adf_process_control APC  "
				 + "               WHERE APC.adf_number = TAppr.adf_number  "
				 + "                 AND APC.acquisition_status = 'COMPSERR')  "
				 + "         ELSE NULL  "
				 + "     END AS SERIOUS_ERROR_COUNT,  "
				 + "     CASE  "
				 + "         WHEN TAppr.adf_schedule_status = 'C'  "
				 + "              AND (SELECT COUNT(1)  "
				 + "                   FROM adf_process_control APC  "
				 + "                   WHERE APC.adf_number = TAppr.adf_number  "
				 + "                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0  "
				 + "         THEN (SELECT COUNT(1)  "
				 + "               FROM adf_process_control APC  "
				 + "               WHERE APC.adf_number = TAppr.adf_number  "
				 + "                 AND APC.acquisition_status = 'COMPNSERR')  "
				 + "         ELSE NULL  "
				 + "     END AS WARNING_COUNT,  "
				 + "     NULLIF((SELECT AA.alpha_subtab_description  "
				 + "             FROM alpha_sub_tab AA  "
				 + "             WHERE AA.alpha_tab = TAppr.acquisition_process_type_at  "
				 + "               AND AA.alpha_sub_tab = TAppr.acquisition_process_type)  "
				 + "             + ': ' + (SELECT AA.alpha_subtab_description  "
				 + "                       FROM alpha_sub_tab AA  "
				 + "                       WHERE AA.alpha_tab = TAppr.frequency_process_at  "
				 + "                       AND AA.alpha_sub_tab = TAppr.frequency_process), ': ') AS ADF_TYPE,  "
				 + "  "
				 + "     FORMAT(TAppr.start_time, 'dd-MMM-yyyy HH:mm:ss') AS FOR_LOG_START_TIME  "
				 + " FROM adf_schedules TAppr,  "
				 + "      le_book LEB,  "
				 + "      VISION_USERS v1  "
				 + " WHERE TAppr.country = LEB.country  "
				 + " AND TAppr.le_book = LEB.le_book  "
				 + " AND TAppr.SUBMITTER_ID = v1.VISION_ID"
				 + "  ");
		}else if ("ORACLE".equalsIgnoreCase(databaseType) ) {
		 strBufApprove = new StringBuffer("  SELECT TAppr.major_build, "
			     +"  TAppr.maker, "
			     +"  To_Char(TAppr.scheduled_date, 'dd-Mon-yyyy HH24:mm:ss') SCHEDULED_DATE, "
			     +"  TAppr.adf_number, "
			     +"  TAppr.parallel_procs_count, "
			     +"  TAppr.notify, "
			     +"  TAppr.support_contact, "
			     +"  TAppr.submitter_id, "
			     +"  v1.USER_NAME submitter_name, "
			     +"  To_Char((SELECT Min(APC.start_time) "
			     +"           FROM   adf_process_control APC "
			     +"           WHERE  APC.adf_number = TAppr.adf_number), "
			     +"          'dd-Mon-yyyy HH24:mm:ss') Start_Time, "
			     +"  (SELECT Max(APC.end_time) "
			     +"   FROM   adf_process_control APC "
			     +"   WHERE  APC.adf_number = TAppr.adf_number) End_Time, "
			     +"  TAppr.adf_schedule_status_at, "
			     +"  TAppr.adf_schedule_status, "
			     +"  (SELECT alpha_subtab_description "
			     +"   FROM   alpha_sub_tab "
			     +"   WHERE  alpha_tab = 1204 "
			     +"          AND TAppr.adf_schedule_status = alpha_sub_tab) ADF_SCHEDULE_STATUS_DESC, "
			     +"  To_Char(TAppr.date_last_modified, 'dd-Mon-yyyy HH24:mm:ss') DATE_LAST_MODIFIED, "
			     +"  To_Char(TAppr.date_creation, 'dd-Mon-yyyy HH24:mm:ss') DATE_CREATION, "
			     +"  TAppr.country COUNTRY, "
			     +"  TAppr.country || ' - ' || LEB.leb_description STAKE_HOLDER, "
			     +"  TAppr.le_book LE_BOOK, "
			     +"  TAppr.recurring_frequency_at, "
			     +"  TAppr.recurring_frequency, "
			     +"  NVL(TAppr.node_override, TAppr.node_request) NODE, "
			     +"  ROUND ((SYSDATE - start_time) * (24 * 60)) Duration, "
			     +"  To_Char(TAppr.node_request_time, 'dd-Mon-yyyy HH24:mm:ss') NODE_REQUEST_TIME, "
			     +"  To_Char(TAppr.node_override_time, 'dd-Mon-yyyy HH24:mm:ss') NODE_OVERRIDE_TIME, "
			     +"  To_Char(TAppr.business_date, 'dd-Mon-yyyy') BUSINESS_DATE, "
			     +"  To_Char(TAppr.feed_date, 'dd-Mon-yyyy') FEED_DATE, "
			     +"  TAppr.frequency_process_at, "
			     +"  TAppr.frequency_process, "
			     +"  TAppr.acquisition_process_type_at, "
			     +"  TAppr.acquisition_process_type, "
			     +"  CASE "
			     +"    WHEN (SELECT Count(1) "
			     +"          FROM   adf_process_control APC "
			     +"          WHERE  APC.adf_number = TAppr.adf_number "
			     +"                 AND (APC.eod_initiated_flag = 'Y' "
			     +"                       OR APC.auth_status IN (0, 4))) > 0 THEN 'Y' "
			     +"    ELSE 'N' "
			     +"  END AS EOD_INITIATED_FLAG, "
			     +"  CASE "
			     +"    WHEN TAppr.adf_schedule_status = 'C' AND (CASE "
			     +"      WHEN (SELECT Count(1) "
			     +"            FROM   adf_process_control APC "
			     +"            WHERE  APC.adf_number = TAppr.adf_number " 
			     +"                   AND (APC.eod_initiated_flag = 'Y' "
			     +"                        OR APC.auth_status IN (0, 4))) > 0 THEN 'Y' "
			     +"      ELSE 'N' END) = 'Y' "
			     +"         AND (SELECT Count(1) "
			     +"              FROM   adf_process_control APC "
			     +"              WHERE  APC.adf_number = TAppr.adf_number "
			     +"                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0 THEN 'Completed (P) with Errors' "
			     +"    WHEN TAppr.adf_schedule_status = 'C' AND (CASE "
				+"	 	WHEN (SELECT Count(1) "
				+"	 			FROM   adf_process_control APC "
				+"	 			WHERE  APC.adf_number = TAppr.adf_number "
				+"	 				AND (APC.eod_initiated_flag = 'Y' "
				+"	 						OR APC.auth_status IN (0, 4))) > 0 THEN 'Y' "
				+"	 	ELSE 'N' "
				+"	 	END) = 'N' "
			     +"         AND (SELECT Count(1) "
			     +"              FROM   adf_process_control APC "
			     +"              WHERE  APC.adf_number = TAppr.adf_number "
			     +"                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0 THEN 'Completed with Errors' "
			     +"    WHEN TAppr.adf_schedule_status = 'C' AND (CASE "
				+"	 	WHEN (SELECT Count(1) "
				+"	 		FROM   adf_process_control APC "
				+"	 		WHERE  APC.adf_number = TAppr.adf_number "
				+"	 				AND (APC.eod_initiated_flag = 'Y' "
				+"	 					OR APC.auth_status IN (0, 4))) > 0 THEN 'Y' "
				+"	 	ELSE 'N' "
				+"	 END) = 'Y' "
			     +"         AND (SELECT Count(1) "
			     +"              FROM   adf_process_control APC "
			     +"              WHERE  APC.adf_number = TAppr.adf_number "
			     +"                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) = 0 THEN 'Completed (P)' "
			     +"    ELSE (SELECT AA.alpha_subtab_description "
			     +"          FROM   alpha_sub_tab AA "
			     +"          WHERE  AA.alpha_tab = TAppr.adf_schedule_status_at "
			     +"                 AND AA.alpha_sub_tab = TAppr.adf_schedule_status) "
			     +"  END STATUS, "
			     +"  CASE "
			     +"    WHEN TAppr.adf_schedule_status = 'C' "
			     +"         AND (SELECT Count(1) "
			     +"              FROM   adf_process_control APC "
			     +"              WHERE  APC.adf_number = TAppr.adf_number "
			     +"                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0 THEN (SELECT Count(1) "
				+"	 FROM   adf_process_control APC "
				+"	 WHERE  APC.adf_number = TAppr.adf_number "
				+"	 		AND APC.acquisition_status = 'COMPSERR') "
			     +"    ELSE NULL "
			     +"  END SERIOUS_ERROR_COUNT, "
			     +"  CASE "
			     +"    WHEN TAppr.adf_schedule_status = 'C' "
			     +"         AND (SELECT Count(1) "
			     +"              FROM   adf_process_control APC "
			     +"              WHERE  APC.adf_number = TAppr.adf_number "
			     +"                     AND APC.acquisition_status IN ('COMPSERR', 'COMPNSERR')) != 0 THEN (SELECT Count(1) "
				+"	 FROM   adf_process_control APC "
				+"	 WHERE  APC.adf_number = TAppr.adf_number "
				+"	 		AND APC.acquisition_status = 'COMPNSERR') "
			     +"    ELSE NULL "
			     +"  END WARNING_COUNT, "
			     +"  NULLIF((SELECT AA.alpha_subtab_description "
			     +"          FROM   alpha_sub_tab AA "
			     +"          WHERE  AA.alpha_tab = TAppr.acquisition_process_type_at "
			     +"                 AND AA.alpha_sub_tab = TAppr.acquisition_process_type) || ': ' || (SELECT AA.alpha_subtab_description "
				+"	 FROM   alpha_sub_tab AA "
				+"	 WHERE  AA.alpha_tab = TAppr.frequency_process_at "
				+"	 		AND AA.alpha_sub_tab = TAppr.frequency_process), ': ') ADF_TYPE, "
			     +"  To_Char(TAppr.start_time, 'dd-Mon-yyyy HH24:mm:ss') FOR_LOG_START_TIME "
			     +"  FROM   adf_schedules TAppr, "
			     +"  le_book LEB, "
			     +"  VISION_USERS v1 "
			     +"  WHERE  TAppr.country = LEB.country "
			     +"  AND TAppr.le_book = LEB.le_book "
			     +"  AND TAppr.SUBMITTER_ID = v1.VISION_ID ");
			}
		try{
			if (ValidationUtil.isValid(dObj.getAdfNumber())){
				params.addElement(dObj.getAdfNumber());
				strBufApprove.append(" AND TAppr.ADF_NUMBER = ? ");
			}
			if (ValidationUtil.isValid(dObj.getCountry())){
				params.addElement(dObj.getCountry().toUpperCase());
				strBufApprove.append(" AND TAppr.COUNTRY = ? ");
			}
			if (ValidationUtil.isValid(CalLeBook)){
				params.addElement(CalLeBook);
				strBufApprove.append(" AND TAppr.LE_BOOK = ? ");
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
				strBufApprove.append(" AND TAppr.COUNTRY IN("+SessionContextHolder.getContext().getCountry().toUpperCase()+") ");
				strBufApprove.append(" AND TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getLeBook().toUpperCase()+") ");
			}	
			if(ValidationUtil.isValid(dObj.getMajorBuild()) && !"-1".equalsIgnoreCase(dObj.getMajorBuild())){
				params.addElement(dObj.getMajorBuild().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.MAJOR_BUILD) = UPPER(?) ");
			}else if(ValidationUtil.isValid(dObj.getAdfName()) && !"-1".equalsIgnoreCase(dObj.getAdfName())){
				params.addElement(dObj.getAdfName().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.MAJOR_BUILD) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getAcquisitionProcessType()) && !"-1".equalsIgnoreCase(dObj.getAcquisitionProcessType())){
				params.addElement(dObj.getAcquisitionProcessType().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.ACQUISITION_PROCESS_TYPE) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getFrequencyProcess()) && !"-1".equalsIgnoreCase(dObj.getFrequencyProcess())){
				params.addElement(dObj.getFrequencyProcess().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.FREQUENCY_PROCESS) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getAdfScheduleStatus()) && !"-1".equalsIgnoreCase(dObj.getAdfScheduleStatus())){
				params.addElement(dObj.getAdfScheduleStatus());
				strBufApprove.append(" AND TAppr.ADF_SCHEDULE_STATUS = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getStatus()) && !"-1".equalsIgnoreCase(dObj.getStatus())){
				params.addElement(dObj.getStatus());
				strBufApprove.append(" AND TAppr.ADF_SCHEDULE_STATUS = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()))	{
		    	params.addElement(dObj.getBusinessDate());
		    	strBufApprove.append(" AND TAppr.BUSINESS_DATE = "+dateTimeConvert+" ");
			}
			if(ValidationUtil.isValid(dObj.getCategoryType()) && !"-1".equalsIgnoreCase(dObj.getCategoryType())){
				params.addElement(dObj.getCategoryType().toUpperCase());
				strBufApprove.append(" AND UPPER(LEB.CATEGORY_TYPE) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getSubmitterId())  && dObj.getSubmitterId()!=0){
				params.addElement(dObj.getSubmitterId());
				strBufApprove.append(" AND TAppr.SUBMITTER_ID = ? ");
			}
			if(ValidationUtil.isValid(dObj.getNode()) && !"-1".equalsIgnoreCase(dObj.getNode())){
				params.addElement(dObj.getNode());
				strBufApprove.append(" AND nvl(node_override, node_request) = ? ");
			}
			//Update Restriction - UBA
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction())){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					strBufApprove.append(" AND TAppr.COUNTRY IN("+toSqlInList(SessionContextHolder.getContext().getCountry().toUpperCase())+") ");
				}
				if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
					strBufApprove.append(" AND TAppr.LE_BOOK IN("+toSqlInList(SessionContextHolder.getContext().getLeBook().toUpperCase())+") ");
				}
			}
			String userGroupProie = visionUsersVb.getUserGroup()+"-"+visionUsersVb.getUserProfile();
			strBufApprove.append("AND (Exists (select 1 from build_access blda "+
                    " where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "+ 
                    " and blda.major_build = 'ALL' and blda.ba_status = 0) OR Exists (select 1  "+
                    " from build_access blda where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"'  "+
                    " and blda.major_build = TAppr.Major_Build and blda.ba_status = 0))");
			
			//String orderBy = " ORDER BY START_TIME, COUNTRY, LE_BOOK ";
			String orderBy = " ORDER BY  adf_number ";
//			System.out.println(strBufApprove);
			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, "", orderBy, params, getQueryforAdfScheduleResultsMapper("EXISTS"));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	private String toSqlInList(String CcountryLeBook) {
	    return Arrays.stream(CcountryLeBook.split(","))
	            .map(String::trim)
	            .map(val -> "'" + val + "'")
	            .collect(Collectors.joining(","));
	}
	public AdfSchedulesVb getQueryResultsForDetails(String adfNumber) {
		final int intKeyFieldsCount = 1;
		String strBufApprove = new String();
		if("MSSQL".equalsIgnoreCase(databaseType)|| "SQLSERVER".equalsIgnoreCase(databaseType)) {
		 strBufApprove = new String("Select TAppr.MAJOR_BUILD, TAppr.MAKER, T1.TEMPLATE_NAME, Format(TAppr.SCHEDULED_DATE, 'dd-MM-yyyy HH:mm:ss') SCHEDULED_DATE, TAppr.ADF_NUMBER, TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT, "+
				 "				 TAppr.SUBMITTER_ID, "
				 +"    v6.USER_NAME AS SUBMITTER_NAME, " 
				 + "Format(T1.START_TIME, 'dd-MM-yyyy HH:mm:ss') START_TIME, Format(T1.END_TIME, 'dd-MM-yyyy HH:mm:ss') END_TIME, "+
				 "				 TAppr.ADF_SCHEDULE_STATUS_AT, TAppr.ADF_SCHEDULE_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED, "+
				 "				 Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION,"
				    +"    (SELECT ALPHA_SUBTAB_DESCRIPTION "
				    +"     FROM   ALPHA_SUB_TAB "
				    +"     WHERE  ALPHA_TAB = 1204 "
				    +"            AND ALPHA_SUB_TAB =TAppr.ADF_SCHEDULE_STATUS ) AS ADF_SCHEDULE_STATUS_DESC, "+
				 "				 TAppr.COUNTRY,"+
				 "    TAppr.COUNTRY + ' - ' + LEB.LEB_DESCRIPTION AS STAKE_HOLDER, "+
				 "    TAppr.LE_BOOK, "+
				 "				 TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, "+
				 "				 isNull(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, "+
				 "				 ROUND((Select isNull(DATEDIFF(MINUTE,isNull(V1.PROCESS_END_TIME,isNull(V1.INT_END_TIME,isNull(V1.UPL_END_TIME,isNull(V1.EXT_END_TIME,isNull(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME)"+
				 "				 )))), V1.PROCESS_START_TIME),0)  "+
				 "				 From ACQ_VERSION_NO V1  "+
				 "				 Where V1.Country = TAppr.Country "+
				 "				 And V1.LE_Book = TAppr.Le_Book "+
				 "				 And V1.Business_Date = TAppr.Business_Date  "+
				 "				 And V1.Feed_Date = TAppr.FEed_Date  "+
				 "				 And V1.TEmplate_name = T1.Template_Name  "+
				 "				 And V1.Version_No =  "+
				 "				 (Select max(V2.Version_No)  "+
				 "				 from acq_Version_No V2  "+
				 "				 Where V1.Country = V2.Country  "+ 
				 "				 and v1.Le_Book = V2.Le_Book  "+
				 "				 and V1.Business_Date = V2.Business_Date  "+
				 "				 And V1.FEed_Date = V2.Feed_Date  "+
				 "				 and V1.Template_Name = V2.Template_Name)) * 24 * 60,0) Duration, "+
				 "				 Format(TAppr.NODE_REQUEST_TIME, 'dd-MM-yyyy HH:mm:ss') NODE_REQUEST_TIME, "+
				 "				 Format(TAppr.NODE_OVERRIDE_TIME, 'dd-MM-yyyy HH:mm:ss') NODE_OVERRIDE_TIME, Format(TAppr.BUSINESS_DATE, 'dd-MM-yyyy') BUSINESS_DATE, "+
				 "				 Format(TAppr.FEED_DATE, 'dd-MM-yyyy') FEED_DATE, TAppr.FREQUENCY_PROCESS_AT, TAppr.FREQUENCY_PROCESS, TAppr.ACQUISITION_PROCESS_TYPE_AT, TAppr.ACQUISITION_PROCESS_TYPE, "+
				 "				 CASE "+
				 "				 WHEN (SELECT COUNT (1) ff"+
				 "				         FROM ADF_PROCESS_CONTROL APC "+
				 "				        WHERE     APC.ADF_NUMBER = TAppr.ADF_NUMBER "+
				 "				              AND (APC.EOD_INITIATED_FLAG = 'Y' "+
				 "				                   OR APC.AUTH_STATUS IN (0, 4))) > 0 "+
				 "				 THEN 'Y' ELSE 'N' END AS EOD_INITIATED_FLAG, "+
				 "				 (Select AA.Alpha_SubTab_Description  "+
				 "				 From Alpha_Sub_Tab AA  "+
				 "				 Where AA.Alpha_Tab = TAppr.Acquisition_Process_Type_AT "+
				 "				 And AA.Alpha_Sub_Tab = TAppr.Acquisition_Process_Type)+'-'+ "+
				 "				 (Select AA.Alpha_SubTab_Description  "+
				 "				 From Alpha_Sub_Tab AA  "+
				 "				 Where AA.Alpha_Tab = TAppr.Frequency_Process_AT "+
				 "				 And AA.Alpha_Sub_Tab = TAppr.FREQUENCY_PROCESS) ADF_TYPE, "+
				 "				 Case "+
				 "				 When TAppr.ADF_SCHEDULE_STATUS = 'C' "+
				 "				 And (SELECT COUNT (1) "+
				 "				      FROM ADF_PROCESS_CONTROL APC "+
				 "				      WHERE APC.ADF_NUMBER = TAppr.ADF_NUMBER "+
				 "				      And APC.ACquisition_Status in ('COMPSERR','COMPNSERR')) != 0 "+
				 "				    Then CONCAT('Completed with Errors. Serious:( ',  (SELECT COUNT (1)FROM ADF_PROCESS_CONTROL APC  WHERE APC.ADF_NUMBER = TAppr.ADF_NUMBER And APC.ACquisition_Status = 'COMPSERR') , ')', ' ,Warning:(', (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL APC WHERE APC.ADF_NUMBER = TAppr.ADF_NUMBER And APC.ACquisition_Status = 'COMPNSERR'), ').')     				"+
				 "				 Else (Select AA.Alpha_SubTab_Description "+
				 "				       From Alpha_Sub_Tab AA "+
				 "				       Where AA.Alpha_Tab = TAppr.ADF_SCHEDULE_STATUS_AT "+
				 "				       And AA.Alpha_Sub_Tab = TAppr.ADF_SCHEDULE_STATUS)  End STATUS,Format (TAppr.START_TIME, 'dd-MM-yyyy HH:mm:ss') FOR_LOG_START_TIME "+
				 "				 FROM ADF_SCHEDULES TAppr,ADF_PROCESS_CONTROL T1, LE_BOOK LEB,VISION_USERS v6 where T1.ADF_NUMBER = TAppr.ADF_NUMBER "+
				 "				 AND T1.COUNTRY = LEB.COUNTRY AND T1.LE_BOOK = LEB.LE_BOOK "+
				 "				 AND TAPPR.COUNTRY = LEB.COUNTRY AND TAPPR.LE_BOOK = LEB.LE_BOOK 	AND TAppr.SUBMITTER_ID = v6.VISION_ID  AND T1.ADF_NUMBER = ?  ");
		}else {
		 strBufApprove = new String("  SELECT   TAppr.MAJOR_BUILD, "
			    +"    TAppr.MAKER, "
			    +"    T1.TEMPLATE_NAME, "
			    + "T1.TEMPLATE_NAME ||' - '||GENERAL_DESCRIPTION TEMPLATE_DESC,"
			    +"    To_Char(TAppr.SCHEDULED_DATE, 'dd-Mon-yyyy HH24:mm:ss') SCHEDULED_DATE, "
			    +"    TAppr.ADF_NUMBER, "
			    +"    TAppr.PARALLEL_PROCS_COUNT, "
			    +"    TAppr.NOTIFY, "
			    +"    TAppr.SUPPORT_CONTACT, "
			    +"    TAppr.SUBMITTER_ID, "
			    +"    v6.USER_NAME AS SUBMITTER_NAME, "
			    +"    To_Char(T1.START_TIME, 'dd-Mon-yyyy HH24:mm:ss') AS START_TIME, "
			    +"    To_Char(T1.END_TIME, 'dd-Mon-yyyy HH24:mm:ss') AS END_TIME, "
			    +"    TAppr.ADF_SCHEDULE_STATUS_AT, "
			    +"    TAppr.ADF_SCHEDULE_STATUS, "
			    +"    To_Char(TAppr.DATE_LAST_MODIFIED, 'dd-Mon-yyyy HH24:mm:ss') AS DATE_LAST_MODIFIED, "
			    +"    To_Char(TAppr.DATE_CREATION, 'dd-Mon-yyyy HH24:mm:ss') AS DATE_CREATION, "
			    +"    (SELECT ALPHA_SUBTAB_DESCRIPTION "
			    +"     FROM   ALPHA_SUB_TAB "
			    +"     WHERE  ALPHA_TAB = 1204 "
			    +"            AND ALPHA_SUB_TAB =TAppr.ADF_SCHEDULE_STATUS ) AS ADF_SCHEDULE_STATUS_DESC, "
			    +"    TAppr.COUNTRY, "
			    +"    TAppr.COUNTRY || ' - ' || LEB.LEB_DESCRIPTION AS STAKE_HOLDER, "
			    +"    TAppr.LE_BOOK, "
			    +"    TAppr.RECURRING_FREQUENCY_AT, "
			    +"    TAppr.RECURRING_FREQUENCY, "
			    +"    NVL(TAppr.NODE_OVERRIDE, TAppr.NODE_REQUEST) AS NODE, "
			    +"   ROUND((Select NVL((NVL(V1.PROCESS_END_TIME,NVL(V1.INT_END_TIME,NVL(V1.UPL_END_TIME,NVL(V1.EXT_END_TIME,NVL(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME))))) - V1.PROCESS_START_TIME),0)  "
			    +"     FROM   ACQ_VERSION_NO V1 "
			    +"     WHERE  V1.COUNTRY = TAppr.COUNTRY "
			    +"            AND V1.LE_BOOK = TAppr.LE_BOOK "
			    +"            AND V1.BUSINESS_DATE = TAppr.BUSINESS_DATE "
			    +"            AND V1.FEED_DATE = TAppr.FEED_DATE "
			    +"            AND V1.TEMPLATE_NAME = T1.TEMPLATE_NAME "
			    +"            AND V1.VERSION_NO = (SELECT MAX(V2.VERSION_NO) "
				+" 			FROM   ACQ_VERSION_NO V2 "
				+" 			WHERE  V1.COUNTRY = V2.COUNTRY "
				+" 				AND V1.LE_BOOK = V2.LE_BOOK "
				+" 				AND V1.BUSINESS_DATE = V2.BUSINESS_DATE "
				+" 				AND V1.FEED_DATE = V2.FEED_DATE "
				+" 				AND V1.TEMPLATE_NAME = V2.TEMPLATE_NAME))) AS DURATION, "
			    +"    To_Char(TAppr.NODE_REQUEST_TIME, 'dd-Mon-yyyy HH24:mm:ss') AS NODE_REQUEST_TIME, "
			    +"    To_Char(TAppr.NODE_OVERRIDE_TIME, 'dd-Mon-yyyy HH24:mm:ss') AS NODE_OVERRIDE_TIME, "
			    +"    To_Char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') AS BUSINESS_DATE, "
			    +"    To_Char(TAppr.FEED_DATE, 'dd-Mon-yyyy') AS FEED_DATE, "
			    +"    TAppr.FREQUENCY_PROCESS_AT, "
			    +"    TAppr.FREQUENCY_PROCESS, "
			    +"    TAppr.ACQUISITION_PROCESS_TYPE_AT, "
			    +"    TAppr.ACQUISITION_PROCESS_TYPE, "
			    +"    CASE "
			    +"      WHEN (SELECT COUNT(1) "
			    +"            FROM   ADF_PROCESS_CONTROL APC "
			    +"            WHERE  APC.ADF_NUMBER = TAppr.ADF_NUMBER "
			    +"                   AND (APC.EOD_INITIATED_FLAG = 'Y' "
			    +"                        OR APC.AUTH_STATUS IN (0, 4))) > 0 THEN 'Y' "
			    +"      ELSE 'N' "
			    +"    END AS EOD_INITIATED_FLAG, "
			    +"    (SELECT AA.ALPHA_SUBTAB_DESCRIPTION "
			    +"     FROM   ALPHA_SUB_TAB AA "
			    +"     WHERE  AA.ALPHA_TAB = TAppr.ACQUISITION_PROCESS_TYPE_AT "
			    +"            AND AA.ALPHA_SUB_TAB = TAppr.ACQUISITION_PROCESS_TYPE) || '-' || "
			    +"    (SELECT AA.ALPHA_SUBTAB_DESCRIPTION "
			    +"     FROM   ALPHA_SUB_TAB AA "
			    +"     WHERE  AA.ALPHA_TAB = TAppr.FREQUENCY_PROCESS_AT "
			    +"            AND AA.ALPHA_SUB_TAB = TAppr.FREQUENCY_PROCESS) AS ADF_TYPE, "
			    +"    CASE "
			    +"      WHEN TAppr.ADF_SCHEDULE_STATUS = 'C' "
			    +"           AND (SELECT COUNT(1) "
			    +"                FROM   ADF_PROCESS_CONTROL APC "
			    +"                WHERE  APC.ADF_NUMBER = TAppr.ADF_NUMBER "
			    +"                       AND APC.ACQUISITION_STATUS IN ('COMPSERR', 'COMPNSERR')) != 0 THEN "
			    +"        'Completed with Errors. Serious:(' || (SELECT COUNT(1) "
				+" 			FROM   ADF_PROCESS_CONTROL APC "
				+" 			WHERE  APC.ADF_NUMBER = TAppr.ADF_NUMBER "
				+" 					AND APC.ACQUISITION_STATUS = 'COMPSERR') || '), Warning:(' || "
			    +"        (SELECT COUNT(1) "
			    +"         FROM   ADF_PROCESS_CONTROL APC "
			    +"         WHERE  APC.ADF_NUMBER = TAppr.ADF_NUMBER "
			    +"                AND APC.ACQUISITION_STATUS = 'COMPNSERR') || ').' "
			    +"      ELSE (SELECT AA.ALPHA_SUBTAB_DESCRIPTION "
			    +"            FROM   ALPHA_SUB_TAB AA "
			    +"            WHERE  AA.ALPHA_TAB = TAppr.ADF_SCHEDULE_STATUS_AT "
			    +"                   AND AA.ALPHA_SUB_TAB = TAppr.ADF_SCHEDULE_STATUS) "
			    +"    END AS STATUS, "
			    +"    To_Char(TAppr.START_TIME, 'dd-Mon-yyyy HH24:mm:ss') AS FOR_LOG_START_TIME "
				+" 	FROM    ADF_SCHEDULES TAppr,ADF_PROCESS_CONTROL T1, LE_BOOK LEB,VISION_USERS v6,	TEMPLATE_NAMES tnames "
				+" 	WHERE  "
				+" 	T1.ADF_NUMBER = TAppr.ADF_NUMBER    "
				+" 	AND T1.COUNTRY = LEB.COUNTRY "
				+" 	AND T1.LE_BOOK = LEB.LE_BOOK "
				+" 	AND TAppr.SUBMITTER_ID = v6.VISION_ID "
				+" 	AND TAPPR.COUNTRY = LEB.COUNTRY  "
				+" 	AND TAPPR.LE_BOOK = LEB.LE_BOOK  "
				+ "AND T1.TEMPLATE_NAME = TNAMES.TEMPLATE_NAME"
				+"	AND T1.ADF_NUMBER = ? ");
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = adfNumber;
		try {
			List<AdfSchedulesVb> result = getJdbcTemplate().query(strBufApprove, params,
					getQueryforAdfScheduleResultsMapper(""));
			if (result == null || result.isEmpty()) {
				return null;
			} else {
				return result.get(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove));
			if (params != null)
				for (int i = 0; i < params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return null;
		}
	}
	@Override
	protected ExceptionCode doUpdateApprRecordForNonTrans(AdfSchedulesVb adfSchedulesVb) throws RuntimeCustomException{
		ExceptionCode exceptionCode = null;
		adfSchedulesVb.setMaker(intCurrentUserId);
		int runningCount = getCountOfRunningTemplates(adfSchedulesVb.getAdfNumber());
		if(runningCount>0){
			exceptionCode = CommonUtils.getResultObject("AdfSchedules", Constants.WE_HAVE_ERROR_DESCRIPTION, "Modify", "EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["+adfSchedulesVb.getAdfNumber()+"]. Please refresh & check.");
			throw new RuntimeCustomException(exceptionCode);
		}
		adfSchedulesVb.setAdfScheduleStatus("P");		
		retVal = doUpdateApprStatus(adfSchedulesVb);
	    if(retVal != Constants.SUCCESSFUL_OPERATION){
	    	exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
	    }
	    for (AdfControlsVb buildControlsVb: adfSchedulesVb.getAdfControlsList()) {
			if("Modify".equalsIgnoreCase(adfSchedulesVb.getOperationFroPopUp())){
				if("Y".equalsIgnoreCase(buildControlsVb.getRunIt())){
					AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(adfSchedulesVb.getAdfNumber());
					lAdfSchedulesDetailsVb.setTemplateName(buildControlsVb.getExcelTemplateId());
					buildControlsVb.setBusinessDate(adfSchedulesVb.getBusinessDate());
					exceptionCode = getAdfControlsDao().updateModifyBuildControls(lAdfSchedulesDetailsVb);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					buildControlsVb.setDateLastExtraction(buildControlsVb.getDateLastExtraction());
					long returnCount=getAdfControlsDao().getRecordCount(buildControlsVb);
					buildControlsVb.setNode(adfSchedulesVb.getNode());
					if(returnCount > 0){
						getAdfControlsDao().doUpdateApprForAdd(buildControlsVb);
					}else if(returnCount==0){
						exceptionCode = CommonUtils.getResultObject("Modify", Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD, "Modify", ""); 
						throw new RuntimeCustomException(exceptionCode);
					}
				}	
			}else{
				if("Y".equalsIgnoreCase(buildControlsVb.getRunIt())){
					buildControlsVb.setDateLastExtraction(buildControlsVb.getDateLastExtraction());
					long returnCount=getAdfControlsDao().getRecordCount(buildControlsVb);
					buildControlsVb.setNode(adfSchedulesVb.getNode());
					if(returnCount > 0){
						logger.error("Collection size is greater than zero - Duplicate record found");
						exceptionCode = CommonUtils.getResultObject("Add", Constants.DUPLICATE_KEY_INSERTION, "Add", "");
						throw new RuntimeCustomException(exceptionCode);
					}else if(returnCount==0){
						buildControlsVb.setAdfNumber(Long.parseLong(adfSchedulesVb.getAdfNumber()));
						buildControlsVb.setAcquisitionStatus("YTP");
						buildControlsVb.setFeedDate(adfSchedulesVb.getFeedDate());
						getAdfControlsDao().doInsertionAppr(buildControlsVb);
					}
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	protected int doUpdateApprStatus(AdfSchedulesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String frequencyProcessModified=vObject.getFrequencyProcess();
		/*frequencyProcessModified=frequencyProcessModified.replaceFirst("ACQ", "");
		frequencyProcessModified=frequencyProcessModified.substring(0,(frequencyProcessModified.length()-4));*/
		
		String query = "UPDATE ADF_SCHEDULES SET ADF_SCHEDULE_STATUS = ?, SUBMITTER_ID = ?, DATE_LAST_MODIFIED = "+systemDate+" WHERE COUNTRY=? AND LE_BOOK=? "
				+ "AND BUSINESS_DATE="+dateTimeConvert+" AND FREQUENCY_PROCESS=? AND MAJOR_BUILD = ? AND ADF_NUMBER = ? ";
		
		if(ValidationUtil.isValid(vObject.getAdfName())) {
			vObject.setMajorBuild(vObject.getAdfName().toUpperCase());
		}
		Object[] args = {vObject.getAdfScheduleStatus(), vObject.getMaker(), vObject.getCountry(), CalLeBook, vObject.getBusinessDate(), frequencyProcessModified, vObject.getMajorBuild(), vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(AdfSchedulesVb adfSchedulesVb) throws RuntimeCustomException{
		ExceptionCode exceptionCode = null;
		adfSchedulesVb.setMaker(intCurrentUserId);
		retVal = doInsertionAppr(adfSchedulesVb);
		
		if(retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		for (AdfSchedulesDetailsVb vBCObject: adfSchedulesVb.getAdfSchedulesDetailsList()) {
			vBCObject.setBuildNumber(adfSchedulesVb.getAdfNumber());
			exceptionCode = getAdfSchedulesDetailsDao().doInsertApprRecordForNonTrans(vBCObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				throw buildRuntimeCustomException(exceptionCode); 
			}
		}
		for (AdfControlsVb buildControlsVb: adfSchedulesVb.getAdfControlsList()) {
			if("Modify".equalsIgnoreCase(adfSchedulesVb.getOperationFroPopUp())){
				int runningCount = getCountOfRunningTemplates(adfSchedulesVb.getAdfNumber());
				if(runningCount>0){
					exceptionCode = CommonUtils.getResultObject("AdfSchedules", Constants.WE_HAVE_ERROR_DESCRIPTION, "Modify", "EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["+adfSchedulesVb.getAdfNumber()+"]. Please refresh & check.");
					throw new RuntimeCustomException(exceptionCode);
				}
				if("Y".equalsIgnoreCase(buildControlsVb.getRunIt())){
					/*if("Y".equalsIgnoreCase(buildControlsVb.getFullBuild())){*/
						buildControlsVb.setLastBuildDate(buildControlsVb.getDateLastExtraction());
						long returnCount=getAdfControlsDao().getRecordCount(buildControlsVb);
						buildControlsVb.setNode(adfSchedulesVb.getNode());
						
						AdfSchedulesDetailsVb adfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
						adfSchedulesDetailsVb.setAdfNumber(adfSchedulesVb.getAdfNumber());
						adfSchedulesDetailsVb.setTemplateName(buildControlsVb.getExcelTemplateId());
						exceptionCode = getAdfControlsDao().updateAddBuildControls(adfSchedulesDetailsVb);
						if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							throw new RuntimeCustomException(exceptionCode); 
						}
						if(returnCount > 0){
							getAdfControlsDao().doUpdateApprForAdd(buildControlsVb);
						}else if(returnCount==0){
							exceptionCode = CommonUtils.getResultObject("AdfSchedules", Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD, "Modify", ""); 
							throw new RuntimeCustomException(exceptionCode);
						}
				}
			}else{
				if("Y".equalsIgnoreCase(buildControlsVb.getRunIt())){
					buildControlsVb.setDateLastExtraction(buildControlsVb.getDateLastExtraction());
					long returnCount=getAdfControlsDao().getRecordCount(buildControlsVb);
					
					buildControlsVb.setNode(adfSchedulesVb.getNode());
					AdfSchedulesDetailsVb adfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					adfSchedulesDetailsVb.setAdfNumber(adfSchedulesVb.getAdfNumber());
					adfSchedulesDetailsVb.setTemplateName(buildControlsVb.getExcelTemplateId());
					
					if(returnCount > 0){
						logger.error("Collection size is greater than zero - Duplicate record found");
						exceptionCode = CommonUtils.getResultObject("AdfSchedules", Constants.DUPLICATE_KEY_INSERTION, "Add", "");
						exceptionCode.setResponse(adfSchedulesVb.getAdfSchedulesDetailsList());
						throw new RuntimeCustomException(exceptionCode);
					}else if(returnCount==0){
						buildControlsVb.setAdfNumber(Long.parseLong(adfSchedulesVb.getAdfNumber()));
						buildControlsVb.setAcquisitionStatus("YTP");
						buildControlsVb.setFeedDate(adfSchedulesVb.getFeedDate());
						getAdfControlsDao().doInsertionAppr(buildControlsVb);
						
						exceptionCode = getAdfControlsDao().updateAddBuildControls(adfSchedulesDetailsVb);
						if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							throw new RuntimeCustomException(exceptionCode); 
						}
					}
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(AdfSchedulesVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		AdfSchedulesVb vObjectlocal = null;
		vObjectlocal = getQueryResultsForDetails(vObject.getAdfNumber());
		if (vObjectlocal == null){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if("I".equalsIgnoreCase(vObjectlocal.getAdfScheduleStatus())){
			strErrorDesc = "Cannot delete templates, which are in progress.";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode); 
		}
		// Moving the ADF_PROCESS_CONTROL data to History table
		retVal = doInsertionProcessControlHistory(vObject);
		
		AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
		lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
		
		exceptionCode = getAdfControlsDao().updateBuildControls(lAdfSchedulesDetailsVb, "");
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			throw buildRuntimeCustomException(exceptionCode); 
		}
		try{
			getAdfControlsDao().doDeleteUploadFiling(lAdfSchedulesDetailsVb);
			getAdfControlsDao().doDeleteAdfAlertEmail(lAdfSchedulesDetailsVb);
			getAdfControlsDao().doDeleteAdfAlertSms(lAdfSchedulesDetailsVb);
		}catch (SQLException e){
			e.printStackTrace();
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
		ExceptionCode lResultObject = getAdfSchedulesDetailsDao().doDeleteApprRecordForNonTrans(lAdfSchedulesDetailsVb);
		if(lResultObject.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			strErrorDesc = lResultObject.getErrorMsg();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		retVal = doDeleteAppr(vObjectlocal);
		if(retVal != Constants.SUCCESSFUL_OPERATION){
			strErrorDesc = "Unable to delete the record.";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	public ExceptionCode reInitiateForParticularStatus(List<AdfSchedulesVb> vObjects, String reInitiateStatus){
		AdfSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Re-Initiate";
		try
		{
			for(AdfSchedulesVb vObject : vObjects){
				if(vObject.isChecked()){
					// check to see if the record already exists in the approved table
					vObjectlocal = getQueryResultsForDetails(vObject.getAdfNumber());

					// If records are there, check for the status and decide what error to return back
					if (vObjectlocal == null){
						exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
						throw buildRuntimeCustomException(exceptionCode);
					}	
					//First run the build Control Query
					AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					exceptionCode = getAdfControlsDao().updateBuildControls(lAdfSchedulesDetailsVb, vObject.getUpdateAllAvailableTemplateStatus());
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					exceptionCode = getAdfSchedulesDetailsDao().updateStatusOfNonCompletedBuilds(lAdfSchedulesDetailsVb,"Reinitiate",reInitiateStatus);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					vObjectlocal.setSubmitterId((int)intCurrentUserId);
					vObjectlocal.setAdfScheduleStatus("R");
					retVal = updateBuildSchedulesStatus(vObjectlocal);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						strErrorDesc = "Unable to reset the record status to 'R'.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode reInitiate(List<AdfSchedulesVb> vObjects){
		AdfSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Re-Initiate";
		try
		{
			for(AdfSchedulesVb vObject : vObjects){
				if(vObject.isChecked()){
					// check to see if the record already exists in the approved table
					vObjectlocal = getQueryResultsForDetails(vObject.getAdfNumber());

					// If records are there, check for the status and decide what error to return back
					if (vObjectlocal == null){
						exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
						throw buildRuntimeCustomException(exceptionCode);
					}	
					//First run the build Control Query
					AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					exceptionCode = getAdfControlsDao().updateBuildControls(lAdfSchedulesDetailsVb, vObject.getUpdateAllAvailableTemplateStatus());
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					exceptionCode = getAdfSchedulesDetailsDao().updateStatusOfNonCompletedBuildsForImidiateResubmit(lAdfSchedulesDetailsVb,"Reinitiate");
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					vObjectlocal.setSubmitterId((int)intCurrentUserId);
					//vObjectlocal.setAdfScheduleStatus("R");
					retVal = updateBuildSchedulesStatus(vObjectlocal);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						strErrorDesc = "Unable to reset the record status to 'R'.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public int checkExpandFlagFor(AdfSchedulesVb vObject){
		final int intKeyFieldsCount = 1;
		String query = new String("select count(1) from BUILD_SCHEDULES_DETAILS BSD JOIN BUILD_CONTROLS  BC ON ASSOCIATED_BUILD = BUILD AND " +
				"BC.BUILD_MODULE=BSD.BUILD_MODULE WHERE BSD.BUILD_NUMBER=? AND EXPAND_FLAG = 'Y'"); 
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = vObject.getAdfNumber(); 
		try
		{
//			return getJdbcTemplate().queryForInt(query,params);
			int count = getJdbcTemplate().queryForObject(query, params, int.class);
			return count;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return 0;
		}
	}
	@Override
	protected int doInsertionAppr(AdfSchedulesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String frequencyProcessModified=vObject.getFrequencyProcess();
		/*frequencyProcessModified=frequencyProcessModified.replaceFirst("ACQ", "");
		frequencyProcessModified=frequencyProcessModified.substring(0,(frequencyProcessModified.length()-4));*/
		String modifiedNode=vObject.getNode();
		if(vObject.getNode().length()>2){
			modifiedNode = vObject.getNode().substring(vObject.getNode().length()-2, vObject.getNode().length());
		}
		String query = "INSERT INTO ADF_SCHEDULES (MAJOR_BUILD, SCHEDULED_DATE, ADF_NUMBER, PARALLEL_PROCS_COUNT, NOTIFY, SUPPORT_CONTACT, SUBMITTER_ID, START_TIME, END_TIME,  "+
				"ADF_SCHEDULE_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, COUNTRY, LE_BOOK, RECURRING_FREQUENCY_AT, RECURRING_FREQUENCY, NODE_REQUEST,  "+
				"NODE_REQUEST_TIME, BUSINESS_DATE, FEED_DATE, FREQUENCY_PROCESS,  "+
				"ACQUISITION_PROCESS_TYPE, MAKER ) VALUES (? ,"+dateTimeConvert+" , ?, ?, ?, ?, ?, "+systemDate+","+systemDate+", ?, "+
				systemDate+","+systemDate+", ?, ?, ?, ?, ?, "+
				systemDate+", "+dateTimeConvert+", "+dateTimeConvert+", ?, ?, ?) ";
		if(ValidationUtil.isValid(vObject.getAdfName())) {
			vObject.setMajorBuild(vObject.getAdfName().toUpperCase());
		}
		Object[] args = {vObject.getMajorBuild(), vObject.getScheduledDate(), vObject.getAdfNumber(),
			vObject.getParallelProcsCount(), vObject.getNotify(), vObject.getSupportContact(),
			vObject.getSubmitterId(), vObject.getAdfScheduleStatus(),vObject.getCountry(),CalLeBook,vObject.getRecurringFrequencyAt(),
			vObject.getRecurringFrequency(),modifiedNode,vObject.getBusinessDate(),vObject.getFeedDate(),frequencyProcessModified,vObject.getAcquisitionProcessType(),vObject.getSubmitterId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(AdfSchedulesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String frequencyProcessModified=vObject.getFrequencyProcess();
		/*frequencyProcessModified=frequencyProcessModified.replaceFirst("ACQ", "");
		frequencyProcessModified=frequencyProcessModified.substring(0,(frequencyProcessModified.length()-4));*/
		String checkQuery = "Select NODE_OVERRIDE from ADF_SCHEDULES where COUNTRY = ? AND LE_BOOK= ? AND BUSINESS_DATE= "+dateTimeConvert+" AND FREQUENCY_PROCESS=? ";
		
		Object[] params= {vObject.getCountry(),CalLeBook,vObject.getBusinessDate(),frequencyProcessModified};
		
		String overRideNodeCheck = getJdbcTemplate().queryForObject(checkQuery,params, String.class);
		
		String query = "UPDATE ADF_SCHEDULES SET MAJOR_BUILD = ?, SCHEDULED_DATE = "+dateTimeConvert+", ADF_NUMBER = ?, PARALLEL_PROCS_COUNT = ?, NOTIFY = ?, SUPPORT_CONTACT = ?, SUBMITTER_ID = ?, START_TIME = "+dateTimeConvert+", END_TIME = "+dateTimeConvert+", "+
				"ADF_SCHEDULE_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+", DATE_CREATION ="+dateTimeConvert+", COUNTRY = ?, LE_BOOK = ?, RECURRING_FREQUENCY_AT = ?, RECURRING_FREQUENCY = ?, NODE_REQUEST = ?,  "+
				" BUSINESS_DATE = "+dateTimeConvert+", FEED_DATE = "+dateTimeConvert+", FREQUENCY_PROCESS_AT = ?, FREQUENCY_PROCESS = ?, ACQUISITION_PROCESS_TYPE_AT = ?,  "+
				"ACQUISITION_PROCESS_TYPE = ? WHERE COUNTRY=? AND LE_BOOK=? AND BUSINESS_DATE="+dateTimeConvert+" AND FREQUENCY_PROCESS=?";
		if(ValidationUtil.isValid(vObject.getAdfName())) {
			vObject.setMajorBuild(vObject.getAdfName().toUpperCase());
		}
		Object[] args = {vObject.getMajorBuild(), vObject.getScheduledDate(), vObject.getAdfNumber(),
				vObject.getParallelProcsCount(), vObject.getNotify(), vObject.getSupportContact(),
				vObject.getSubmitterId(),vObject.getStartTime(),vObject.getEndTime(),
				vObject.getAdfScheduleStatus(),vObject.getDateCreation(),vObject.getCountry(),CalLeBook,vObject.getRecurringFrequencyAt(),
				vObject.getRecurringFrequency(),vObject.getNode(),vObject.getBusinessDate(),vObject.getFeedDate(),frequencyProcessModified};
		if(ValidationUtil.isValid(overRideNodeCheck)){
			query = "UPDATE ADF_SCHEDULES SET MAJOR_BUILD = ?, SCHEDULED_DATE = "+dateTimeConvert+", ADF_NUMBER = ?, PARALLEL_PROCS_COUNT = ?, NOTIFY = ?, SUPPORT_CONTACT = ?, SUBMITTER_ID = ?, START_TIME = "+dateTimeConvert+", END_TIME = "+dateTimeConvert+", "+
					"ADF_SCHEDULE_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+", DATE_CREATION = "+dateTimeConvert+", COUNTRY = ?, LE_BOOK = ?, RECURRING_FREQUENCY_AT = ?, RECURRING_FREQUENCY = ?, NODE_OVERRIDE = ?,  "+
					"BUSINESS_DATE = "+dateTimeConvert+", FEED_DATE = "+dateTimeConvert+", FREQUENCY_PROCESS_AT = ?, FREQUENCY_PROCESS = ?, ACQUISITION_PROCESS_TYPE_AT = ?,  "+
					"ACQUISITION_PROCESS_TYPE = ? WHERE COUNTRY=? AND LE_BOOK=? AND BUSINESS_DATE="+dateTimeConvert+" AND FREQUENCY_PROCESS=?";
		}
		return getJdbcTemplate().update(query,args);
	}
	private int updateBuildSchedulesStatus(AdfSchedulesVb adfSchedulesDetailsVb){
		final int intKeyFieldsCount = 3;
		String query = new String("Update ADF_SCHEDULES Set SUBMITTER_ID = ?, ADF_SCHEDULE_STATUS = Case when ACQUISITION_PROCESS_TYPE = 'XLS' Then 'P' Else 'R' End"
				+ ", DATE_LAST_MODIFIED = "+systemDate+" "+
				"Where MAJOR_BUILD = ? AND ADF_NUMBER = ?");
		if(ValidationUtil.isValid(adfSchedulesDetailsVb.getAdfName())) {
			adfSchedulesDetailsVb.setMajorBuild(adfSchedulesDetailsVb.getAdfName().toUpperCase());
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = adfSchedulesDetailsVb.getSubmitterId();
		//params[1] = adfSchedulesDetailsVb.getAdfScheduleStatus(); // Commenting for case on status - Faraz & Deepak R    
		params[1] = adfSchedulesDetailsVb.getMajorBuild();
		params[2] = adfSchedulesDetailsVb.getAdfNumber();
		try{
			return getJdbcTemplate().update(query,params);
		}catch(Exception ex){
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			logger.error(ex.getMessage(), ex);
			return 0;
		}
	}
	private int updateBuildSchedulesNode(AdfSchedulesVb adfSchedulesDetailsVb){
		final int intKeyFieldsCount = 4;
		String query = new String("Update ADF_SCHEDULES Set SUBMITTER_ID = ?,NODE_OVERRIDE = ?,NODE_OVERRIDE_TIME = "+systemDate+", DATE_LAST_MODIFIED = "+systemDate+" "+
				"Where MAJOR_BUILD = ? AND ADF_NUMBER = ? AND ADF_SCHEDULE_STATUS IN ('K','E','P')");
		if(ValidationUtil.isValid(adfSchedulesDetailsVb.getAdfName())) {
			adfSchedulesDetailsVb.setMajorBuild(adfSchedulesDetailsVb.getAdfName().toUpperCase());
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = adfSchedulesDetailsVb.getSubmitterId();
		params[1] = adfSchedulesDetailsVb.getNode();
//		params[2] = adfSchedulesDetailsVb.getAdfScheduleStatus();
		params[2] = adfSchedulesDetailsVb.getMajorBuild();
		params[3] = adfSchedulesDetailsVb.getAdfNumber();
		try
		{
			return getJdbcTemplate().update(query,params);
		}catch(Exception ex){
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			logger.error(ex.getMessage(), ex);
			return 0;
		}
		
	}
	@Override
	public int doDeleteAppr(AdfSchedulesVb vObject){
		String query = "Delete From ADF_SCHEDULES Where ADF_NUMBER = ?";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	public AdfControlsDao getAdfControlsDao() {
		return adfControlsDao;
	}

	public void setAdfControlsDao(AdfControlsDao adfControlsDao) {
		this.adfControlsDao = adfControlsDao;
	}

	public AdfSchedulesDetailsDao getAdfSchedulesDetailsDao() {
		return adfSchedulesDetailsDao;
	}

	public void setAdfSchedulesDetailsDao(
			AdfSchedulesDetailsDao adfSchedulesDetailsDao) {
		this.adfSchedulesDetailsDao = adfSchedulesDetailsDao;
	}
	public long findCronCount(String environmentVariable){
		String sql="SELECT COUNT(1) FROM VISION_VARIABLES WHERE VARIABLE IN ( "
				+ "	SELECT 'ADF_CRON_'||SERVER_NAME||'_'||NODE_NAME FROM VISION_NODE_CREDENTIALS "
				+ "	WHERE SERVER_ENVIRONMENT='"+environmentVariable+"' AND "
				+ "	NODE_STATUS=0 "
				+ "	)";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
		sql="SELECT COUNT(1) FROM VISION_VARIABLES WHERE VARIABLE IN ( " +
				" SELECT concat('ADF_CRON_',SERVER_NAME,NODE_NAME) FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT='"+environmentVariable+"' AND NODE_STATUS=0)";
		}
//		return getJdbcTemplate().queryForLong(sql);
		long count = 0;
		try {		
			count = getJdbcTemplate().queryForObject(sql, long.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public List<AdfSchedulesVb> findCronName(AdfSchedulesVb dObj,String enironmentVariable){
		List<AdfSchedulesVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = new String("SELECT  (substr(variable,10)) AS NAME FROM VISION_VARIABLES WHERE VARIABLE IN (  " +
				" SELECT 'ADF_CRON_'||SERVER_NAME||'_'||NODE_NAME FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");

		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("SELECT  (SUBSTRING(VARIABLE, 10, LEN(variable))) AS NAME FROM VISION_VARIABLES WHERE VARIABLE IN (  " +
					" SELECT concat('ADF_CRON_',SERVER_NAME,'_',NODE_NAME) FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = enironmentVariable;
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
					adfSchedulesVb.setCronName(rs.getString("NAME"));
					return adfSchedulesVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams, mapper);
			
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public List<AdfSchedulesVb> findCronStatus(AdfSchedulesVb dObj,String enironmentVariable){
		List<AdfSchedulesVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		String strQueryAppr = new String("SELECT VALUE AS STATUS FROM VISION_VARIABLES WHERE VARIABLE IN ( " +
				" SELECT 'ADF_CRON_'||SERVER_NAME||'_'||NODE_NAME FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");
			if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
				strQueryAppr = new String("SELECT VALUE AS STATUS FROM VISION_VARIABLES WHERE VARIABLE IN ( " +
						" SELECT concat('ADF_CRON_',SERVER_NAME,'_',NODE_NAME) FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");
			}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = enironmentVariable;
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
					adfSchedulesVb.setCronStatus(rs.getString("STATUS"));
					return adfSchedulesVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams, mapper);
			
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public ExceptionCode transfer(List<AdfSchedulesVb> vObjects, String updateNode){
		AdfSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Transfer";
		try
		{
			for(AdfSchedulesVb vObject : vObjects){
				if(vObject.isChecked()){
					// check to see if the record already exists in the approved table
					vObjectlocal = getQueryResultsForDetails(vObject.getAdfNumber());

					// If records are there, check for the status and decide what error to return back
					if (vObjectlocal == null){
						exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
						throw buildRuntimeCustomException(exceptionCode);
					}
					if(!("E".equalsIgnoreCase(vObjectlocal.getAdfScheduleStatus()) || "K".equalsIgnoreCase(vObjectlocal.getAdfScheduleStatus()))){
						strErrorDesc = "You can only transfer scheduled templates with status Errored or Terminated.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					//First run the build Control Query
					AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
					lAdfSchedulesDetailsVb.setAdfNumber(vObject.getAdfNumber());
					lAdfSchedulesDetailsVb.setNode(updateNode);
					exceptionCode = getAdfSchedulesDetailsDao().updateStatusOfNonCompletedBuilds(lAdfSchedulesDetailsVb, "Transfer", "YTP");
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					vObjectlocal.setSubmitterId((int)intCurrentUserId);
					vObjectlocal.setAdfScheduleStatus("R");
					vObjectlocal.setNode(updateNode);
					
					retVal = updateBuildSchedulesNode(vObjectlocal);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						strErrorDesc = "Unable to reset the record status to 'P'.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public List<AdfSchedulesVb> getMajorBuildList(){
		String query = new String("Select TAppr.MAJOR_BUILD, TAppr.MAKER, To_Char(TAppr.SCHEDULED_DATE, 'dd-Mon-yyyy HH:mm:ss') SCHEDULED_DATE, TAppr.ADF_NUMBER, TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT, "+
				"TAppr.SUBMITTER_ID, To_Char(TAppr.START_TIME, 'dd-Mon-yyyy HH24:mm:ss') START_TIME, To_Char(TAppr.END_TIME, 'dd-Mon-yyyy HH24:mm:ss') END_TIME, "+
				"TAppr.ADF_SCHEDULE_STATUS_AT, TAppr.ADF_SCHEDULE_STATUS, To_Char(TAppr.DATE_LAST_MODIFIED, 'dd-Mon-yyyy HH24:mm:ss') DATE_LAST_MODIFIED, "+
				"To_Char(TAppr.DATE_CREATION, 'dd-Mon-yyyy HH24:mm:ss') DATE_CREATION, TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, "+
				"nvl(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, round(("+systemDate+" - start_time) * (24 * 60)) Duration, To_Char(TAppr.NODE_REQUEST_TIME, 'dd-Mon-yyyy HH24:mm:ss') NODE_REQUEST_TIME, "+
				"To_Char(TAppr.NODE_OVERRIDE_TIME, 'dd-Mon-yyyy HH24:mm:ss') NODE_OVERRIDE_TIME, To_Char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
				"To_Char(TAppr.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE, TAppr.FREQUENCY_PROCESS_AT, TAppr.FREQUENCY_PROCESS, TAppr.ACQUISITION_PROCESS_TYPE_AT, TAppr.ACQUISITION_PROCESS_TYPE "+
				"Case When (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL WHERE ADF_NUMBER = TAppr.ADF_NUMBER AND EOD_INITIATED_FLAG = 'Y') > 0 Then 'Y' Else 'N' End AS EOD_INITIATED_FLAG "+
				"FROM ADF_SCHEDULES TAppr");
		if(databaseType.equalsIgnoreCase("MSSQL")|| "SQLSERVER".equalsIgnoreCase(databaseType)) {
		query = new String("Select TAppr.MAJOR_BUILD, TAppr.MAKER, Format(TAppr.SCHEDULED_DATE, 'dd-MM-yyyy HH:mm:ss') SCHEDULED_DATE, TAppr.ADF_NUMBER, TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT, "+
				"TAppr.SUBMITTER_ID, Format(TAppr.START_TIME, 'dd-MM-yyyy HH:mm:ss') START_TIME, Format(TAppr.END_TIME, 'dd-MM-yyyy HH:mm:ss') END_TIME, "+
				"TAppr.ADF_SCHEDULE_STATUS_AT, TAppr.ADF_SCHEDULE_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED, "+
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION, TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, "+
				"isNull(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, round((GetDate() - start_time) * (24 * 60)) Duration, Format(TAppr.NODE_REQUEST_TIME, 'dd-MM-yyyy HH:mm:ss') NODE_REQUEST_TIME, "+
				"Format(TAppr.NODE_OVERRIDE_TIME, 'dd-MM-yyyy HH:mm:ss') NODE_OVERRIDE_TIME, Format(TAppr.BUSINESS_DATE, 'dd-MM-yyyy') BUSINESS_DATE, "+
				"Format(TAppr.FEED_DATE, 'dd-MM-yyyy') FEED_DATE, TAppr.FREQUENCY_PROCESS_AT, TAppr.FREQUENCY_PROCESS, TAppr.ACQUISITION_PROCESS_TYPE_AT, TAppr.ACQUISITION_PROCESS_TYPE "+
				"Case When (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL WHERE ADF_NUMBER = TAppr.ADF_NUMBER AND EOD_INITIATED_FLAG = 'Y') > 0 Then 'Y' Else 'N' End AS EOD_INITIATED_FLAG "+
				"FROM ADF_SCHEDULES TAppr");
		}
		Object params[] = null;
		try
		{
			return  getJdbcTemplate().query(query, params, getQueryResultsMapper());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			return null;
		}
	}
	public  long  getMaxAdfNumber(){
		StringBuffer strBufApprove = new StringBuffer("Select "+getDbFunction("NVL")+"(MAX(TAppr.ADF_NUMBER),0) From ADF_SCHEDULES TAppr ");
		try{
//			return getJdbcTemplate().queryForLong(strBufApprove.toString());
			long count = getJdbcTemplate().queryForObject(strBufApprove.toString(), long.class);
			return count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 1000;
		}
	}
	public ExceptionCode checkModifyAllowed(List<AdfControlsVb> pBuildControls){
		AdfSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Re-Initiate";
		try
		{
			
			if(!("E".equalsIgnoreCase(vObjectlocal.getAdfScheduleStatus()) || "K".equalsIgnoreCase(vObjectlocal.getAdfScheduleStatus()))){
				strErrorDesc = "You can only Re-Initiate ADF's with status Errored or Terminated.";
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	private int checkBuildForModify(String adfNumber) // if EOD = 'Y' or AUTH ID is not null then cannot modify/reinitiate can only delete
	{
		final int intKeyFieldsCount = 1;
		String query = new String("select count(1) from ADF_PROCESS_CONTROL where ADF_NUMBER = ? and (EOD_INITIATED_FLAG = 'Y' OR AUTH_ID is not null)"); 
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = adfNumber; 
		try
		{
			int count = getJdbcTemplate().queryForObject(query, int.class);
			return count;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return 0;
		}
	}
	
	public  String  getDateForBusinessFeedDate(AdfSchedulesVb adfSchedulesVb){
		String trimmedLeBook = removeDescLeBook(adfSchedulesVb.getLeBook());
		if(ValidationUtil.isValid(adfSchedulesVb.getAdfName())) {
			adfSchedulesVb.setMajorBuild(adfSchedulesVb.getAdfName().toUpperCase());
		}
		String query=" Select "+dateFormat+"(Feed_Date, "+formatdate+") Feed_Date from Adf_feed_Controls where Country = '"+adfSchedulesVb.getCountry()+"' and LE_Book = '"+trimmedLeBook+"' "+ 
				   " and Acquisition_Process_type = '"+adfSchedulesVb.getAcquisitionProcessType()+"' and FREQUENCY_PROCESS='"+adfSchedulesVb.getFrequencyProcess()+"' "+
				   " and Major_build='"+adfSchedulesVb.getMajorBuild()+"' ";
		try{
			return getJdbcTemplate().queryForObject(query, String.class);
		}catch(Exception ex){
			logger.error("!!!Businees Feed Date not maintained in the Adf Feed Controls Table for the build:"+adfSchedulesVb.getMajorBuild()+"!!!!");
			return "";
		}
	}
	public List<AdfSchedulesVb> getAcquisitionProcessTypeAndMajorBild(AdfSchedulesVb adfSchedulesVb, String requestType){
		List<AdfSchedulesVb> collTemp = null;
		String trimmedLeBook = removeDescLeBook(adfSchedulesVb.getLeBook());
		StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT ADF.ACQUISITION_PROCESS_TYPE ALPHA_SUB_TAB , AT.ALPHA_SUBTAB_DESCRIPTION ALPHA_SUB_TAB_DESC FROM ADF_DATA_ACQUISITION ADF, ALPHA_SUB_TAB AT "+  
			" WHERE ADF.COUNTRY = '"+adfSchedulesVb.getCountry()+"' AND ADF.LE_BOOK = '"+trimmedLeBook+"' AND ADF.ACQUISITION_PROCESS_TYPE != 'RTT' AND ADF.DATA_ACQU_STATUS = 0 "+
			" AND AT.ALPHA_TAB = ADF.ACQUISITION_PROCESS_TYPE_AT "+
			" AND AT.ALPHA_SUB_TAB = ADF.ACQUISITION_PROCESS_TYPE");
		if("FREQUENCYPROCESS".equalsIgnoreCase(requestType)){
			strQueryAppr = new StringBuffer(" SELECT DISTINCT FREQUENCY_PROCESS ALPHA_SUB_TAB, AT.ALPHA_SUBTAB_DESCRIPTION ALPHA_SUB_TAB_DESC "+
					" FROM ADF_DATA_ACQUISITION ADF, ALPHA_SUB_TAB AT "+
				    " WHERE ADF.FREQUENCY_PROCESS=AT.ALPHA_SUB_TAB AND ADF.FREQUENCY_PROCESS_AT=AT.ALPHA_TAB "+
				    " AND ADF.COUNTRY = '"+adfSchedulesVb.getCountry()+"' AND ADF.LE_BOOK = '"+trimmedLeBook+"' "+
				    " AND ADF.ACQUISITION_PROCESS_TYPE = '"+adfSchedulesVb.getAcquisitionProcessType()+"' AND ADF.DATA_ACQU_STATUS = 0 ");
		}else if("MAJOR".equalsIgnoreCase(requestType)){
			if(databaseType.equalsIgnoreCase("MSSQL")) {
			strQueryAppr = new StringBuffer("SELECT DISTINCT isNull(MAJOR_BUILD,CONCAT('ACQ',ADF.FREQUENCY_PROCESS,'FEED')) ALPHA_SUB_TAB, "+
					" isNull(MAJOR_BUILD,CONCAT('ACQ',ADF.FREQUENCY_PROCESS,'FEED')) ALPHA_SUB_TAB_DESC "+
					" FROM ADF_DATA_ACQUISITION ADF WHERE ADF.COUNTRY = '"+adfSchedulesVb.getCountry()+"' AND ADF.LE_BOOK = '"+trimmedLeBook+"' "+
					" AND ADF.ACQUISITION_PROCESS_TYPE = '"+adfSchedulesVb.getAcquisitionProcessType()+"'"+
					" AND ADF.FREQUENCY_PROCESS = '"+adfSchedulesVb.getFrequencyProcess()+"' AND ADF.DATA_ACQU_STATUS = 0");
			}else if(databaseType.equalsIgnoreCase("ORACLE"))
			{
				strQueryAppr = new StringBuffer("SELECT DISTINCT NVL(MAJOR_BUILD,'ACQ'||ADF.FREQUENCY_PROCESS||'FEED') ALPHA_SUB_TAB, "+
						" NVL(MAJOR_BUILD,'ACQ'||ADF.FREQUENCY_PROCESS||'FEED') ALPHA_SUB_TAB_DESC "+
						" FROM ADF_DATA_ACQUISITION ADF WHERE ADF.COUNTRY = '"+adfSchedulesVb.getCountry()+"' AND ADF.LE_BOOK = '"+trimmedLeBook+"' "+
						" AND ADF.ACQUISITION_PROCESS_TYPE = '"+adfSchedulesVb.getAcquisitionProcessType()+"'"+
						" AND ADF.FREQUENCY_PROCESS = '"+adfSchedulesVb.getFrequencyProcess()+"' AND ADF.DATA_ACQU_STATUS = 0");
			}
			
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			String userGroupProie = visionUsersVb.getUserGroup()+"-"+visionUsersVb.getUserProfile();
			strQueryAppr.append(" AND (Exists (select 1 from build_access blda "+  
					" where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "+ 
                    " and blda.major_build = 'ALL' and blda.ba_status = 0)  OR Exists (select 1  "+
                    " from build_access blda where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"'  "+
                    " and blda.major_build = ADF.Major_Build and blda.ba_status = 0)) ");
			
		}
		try{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getFrequencyProcessAndMajorBildMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	public RowMapper getFrequencyProcessAndMajorBildMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				vObject.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
			 	vObject.setDescription(rs.getString("ALPHA_SUB_TAB_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	
	public int getCountOfRunningTemplates(String adfNumber){
//		return 0;
		int count = 0;
		final int intKeyFieldsCount = 1;
		if(!ValidationUtil.isValid(adfNumber)){
			adfNumber = "0";
		}
		
		
		String strQuery = new String (" SELECT COUNT (1) FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "+
				 " WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
			       " AND T1.ADF_NUMBER = ? "+
			       " AND (   T1.EOD_INITIATED_FLAG = 'Y' "+
			            " OR T1.AUTH_STATUS IN (0, 4) "+
			            " OR T2.ADF_SCHEDULE_STATUS IN ('I', 'M') "+
			            " OR (    T2.ADF_SCHEDULE_STATUS IN ('P', 'R') "+
			                " AND (  SYSDATE "+
			                     " + (  NVL ( "+
			                             " (SELECT TO_NUMBER (VV.VALUE) "+
			                                " FROM Vision_Variables VV "+
			                               " WHERE VV.Variable = "+
			                                        " 'ADF_SCHEDULE_MODIFY_CUTOFF_TIME'), "+
			                             " 10) "+
			                        " / 1440)) > T1.Next_Process_Time))");
		
		if(databaseType.equalsIgnoreCase("MSSQL")) {
			 strQuery = new String(" SELECT COUNT (1) FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "+
					 " WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
				       " AND T1.ADF_NUMBER = ? "+
				       " AND (   T1.EOD_INITIATED_FLAG = 'Y' "+
				            " OR T1.AUTH_STATUS IN (0, 4) "+
				            " OR T2.ADF_SCHEDULE_STATUS IN ('I', 'M') "+
				            " OR (    T2.ADF_SCHEDULE_STATUS IN ('P', 'R') "+
				                " AND (  "+systemDate+" "+
				                     " + (  ISNULL ( "+
				                             " (SELECT CONVERT (INT, VV.VALUE) "+
				                                " FROM Vision_Variables VV "+
				                               " WHERE VV.Variable = "+
				                                        " 'ADF_SCHEDULE_MODIFY_CUTOFF_TIME'), "+
				                             " 10) "+
				                        " / 1440)) > T1.Next_Process_Time))");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =adfNumber;
		try
		{
			count = getJdbcTemplate().queryForObject(strQuery, objParams, int.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	public int doInsertionProcessControlHistory(AdfSchedulesVb vObject){
		String sydDate = getSystemDate();
		String query = "INSERT INTO ADF_PROCESS_CONTROL_HISTORY (COUNTRY ,LE_BOOK ,BUSINESS_DATE ,TEMPLATE_NAME ,FILE_PATTERN ,EXCEL_FILE_PATTERN ,EXCEL_TEMPLATE_ID ,NEXT_PROCESS_TIME "
				+ ",ACQUISITION_PROCESS_TYPE_AT ,ACQUISITION_PROCESS_TYPE ,CONNECTIVITY_TYPE_AT ,CONNECTIVITY_TYPE ,CONNECTIVITY_DETAILS ,DATABASE_TYPE_AT ,DATABASE_TYPE ,"
				+ "DATABASE_CONNECTIVITY_DETAILS ,SERVER_FOLDER_DETAILS ,SOURCE_SCRIPT_TYPE_AT ,SOURCE_SCRIPT_TYPE ,SOURCE_SERVER_SCRIPTS ,TARGET_SCRIPT_TYPE_AT ,TARGET_SCRIPT_TYPE "
				+ ",TARGET_SERVER_SCRIPTS ,READINESS_SCRIPTS_TYPE_AT ,READINESS_SCRIPTS_TYPE ,ACQUISITION_READINESS_SCRIPTS ,PREACTIVITY_SCRIPT_TYPE_AT ,PREACTIVITY_SCRIPT_TYPE "
				+ ",PREACTIVITY_SCRIPTS ,FREQUENCY_PROCESS_AT ,FREQUENCY_PROCESS ,START_TIME ,END_TIME ,PROCESS_SEQUENCE ,ACCESS_PERMISSION ,ACQUISITION_READINESS_FLAG , "
				+ "RECORDS_FETCHED_COUNT ,DATE_LAST_EXTRACTION ,DEBUG_MODE ,ACQUISITION_STATUS_AT ,ACQUISITION_STATUS ,INTEGRITY_SCRIPT_NAME ,ALERT1_TIMESLOT ,ALERT1_EMAIL_IDS "
				+ ",ALERT1_MOBILE_NO ,ALERT1_SEND_FLAG ,ALERT2_TIMESLOT ,ALERT2_EMAIL_IDS ,ALERT2_MOBILE_NO ,ALERT2_SEND_FLAG ,ALERT3_TIMESLOT ,ALERT3_EMAIL_IDS ,ALERT3_MOBILE_NO "
				+ ",ALERT3_SEND_FLAG ,ADF_SUCCESSFUL_EMAIL_IDS ,ADF_SUCCESSFUL_EMAIL_FLAG ,ADF_FAILED_EMAIL_IDS ,ADF_FAILED_EMAIL_FLAG ,EOD_INITIATED_FLAG ,AUTOAUTH_TIME "
				+ ",ACQU_PROCESSCONTROL_STATUS_NT ,ACQU_PROCESSCONTROL_STATUS ,RECORD_INDICATOR_NT ,RECORD_INDICATOR ,MAKER ,VERIFIER ,INTERNAL_STATUS ,DATE_LAST_MODIFIED ,DATE_CREATION "
				+ ",MANDATORY_FLAG ,MANDATORY_FLAG_AT ,AUTH_ID ,AUTH_TIME ,NODE_REQUEST ,NODE_OVERRIDE ,NODE_REQUEST_TIME ,NODE_OVERRIDE_TIME ,EXT_ITERATION_COUNT ,INTEGRITY_SCRIPT_TYPE_AT ,INTEGRITY_SCRIPT_TYPE "
				+ ",ADF_NUMBER ,SUB_ADF_NUMBER ,AUTH_STATUS_NT ,AUTH_STATUS ,FEED_DATE ,CB_EMAIL_IDS ,MAJOR_BUILD ,PURGE_FLAG) "
				+ "SELECT T1.COUNTRY ,T1.LE_BOOK ,T1.BUSINESS_DATE ,T1.TEMPLATE_NAME,T1.FILE_PATTERN ,T1.EXCEL_FILE_PATTERN ,T1.EXCEL_TEMPLATE_ID ,T1.NEXT_PROCESS_TIME "
				+ ",T1.ACQUISITION_PROCESS_TYPE_AT ,T1.ACQUISITION_PROCESS_TYPE ,T1.CONNECTIVITY_TYPE_AT ,T1.CONNECTIVITY_TYPE ,T1.CONNECTIVITY_DETAILS ,T1.DATABASE_TYPE_AT "
				+ ",T1.DATABASE_TYPE ,T1.DATABASE_CONNECTIVITY_DETAILS ,T1.SERVER_FOLDER_DETAILS ,T1.SOURCE_SCRIPT_TYPE_AT ,T1.SOURCE_SCRIPT_TYPE ,T1.SOURCE_SERVER_SCRIPTS "
				+ ",T1.TARGET_SCRIPT_TYPE_AT ,T1.TARGET_SCRIPT_TYPE ,T1.TARGET_SERVER_SCRIPTS ,T1.READINESS_SCRIPTS_TYPE_AT ,T1.READINESS_SCRIPTS_TYPE ,T1.ACQUISITION_READINESS_SCRIPTS "
				+ ",T1.PREACTIVITY_SCRIPT_TYPE_AT ,T1.PREACTIVITY_SCRIPT_TYPE ,T1.PREACTIVITY_SCRIPTS ,T1.FREQUENCY_PROCESS_AT ,T1.FREQUENCY_PROCESS ,T1.START_TIME ,T1.END_TIME "
				+ ",T1.PROCESS_SEQUENCE ,T1.ACCESS_PERMISSION ,T1.ACQUISITION_READINESS_FLAG ,T1.RECORDS_FETCHED_COUNT ,T1.DATE_LAST_EXTRACTION ,T1.DEBUG_MODE ,T1.ACQUISITION_STATUS_AT "
				+ ",T1.ACQUISITION_STATUS ,T1.INTEGRITY_SCRIPT_NAME ,T1.ALERT1_TIMESLOT ,T1.ALERT1_EMAIL_IDS ,T1.ALERT1_MOBILE_NO ,T1.ALERT1_SEND_FLAG ,T1.ALERT2_TIMESLOT "
				+ ",T1.ALERT2_EMAIL_IDS ,T1.ALERT2_MOBILE_NO ,T1.ALERT2_SEND_FLAG ,T1.ALERT3_TIMESLOT ,T1.ALERT3_EMAIL_IDS ,T1.ALERT3_MOBILE_NO ,T1.ALERT3_SEND_FLAG "
				+ ",T1.ADF_SUCCESSFUL_EMAIL_IDS ,T1.ADF_SUCCESSFUL_EMAIL_FLAG ,T1.ADF_FAILED_EMAIL_IDS ,T1.ADF_FAILED_EMAIL_FLAG ,T1.EOD_INITIATED_FLAG ,T1.AUTOAUTH_TIME "
				+ ",T1.ACQU_PROCESSCONTROL_STATUS_NT ,T1.ACQU_PROCESSCONTROL_STATUS ,T1.RECORD_INDICATOR_NT ,T1.RECORD_INDICATOR ,'"+intCurrentUserId+"' MAKER , '"+intCurrentUserId+"' VERIFIER ,T1.INTERNAL_STATUS "
				+ ", "+systemDate+" DATE_LAST_MODIFIED ,T1.DATE_CREATION ,T1.MANDATORY_FLAG ,T1.MANDATORY_FLAG_AT ,T1.AUTH_ID ,T1.AUTH_TIME ,T1.NODE_REQUEST ,T1.NODE_OVERRIDE ,T1.NODE_REQUEST_TIME ,T1.NODE_OVERRIDE_TIME "
				+ ",T1.EXT_ITERATION_COUNT ,T1.INTEGRITY_SCRIPT_TYPE_AT ,T1.INTEGRITY_SCRIPT_TYPE ,T1.ADF_NUMBER ,T1.SUB_ADF_NUMBER ,T1.AUTH_STATUS_NT ,T1.AUTH_STATUS "
				+ ",T2.FEED_DATE ,T1.CB_EMAIL_IDS ,T2.MAJOR_BUILD ,'MANUAL' PURGE_FLAG FROM ADF_PROCESS_CONTROL T1,ADF_SCHEDULES T2 WHERE T1.ADF_NUMBER = T2.ADF_NUMBER AND T1.ADF_NUMBER = ? ";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	public PromptTreeVb callProcToPopulateAdfReportData(ReportsWriterVb reportsWriterVb) {
		setServiceDefaults();
		strCurrentOperation = "Query";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs =  null;
		PromptTreeVb promptTreeVb = new PromptTreeVb();
		try{
			if(!ValidationUtil.isValid(reportsWriterVb.getProcedure1())){
				strErrorDesc = "Invalid Procedure in Report Writer table for report Id["+reportsWriterVb.getReportId()+"].";
				throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
			}
			String scalingFactor = "1";
			if(ValidationUtil.isValid(reportsWriterVb.getScalingFactor()))
				scalingFactor = reportsWriterVb.getScalingFactor().replaceAll(",", "");
			
			
			String sessionId = "";
			//For the dash boards the session id will be set by the caller.
			if(ValidationUtil.isValid(reportsWriterVb.getSessionId())){
				sessionId = reportsWriterVb.getSessionId();
			}else{
				sessionId = String.valueOf(System.currentTimeMillis());
			}
			String drillIdKey ="";
			if(ValidationUtil.isValid(reportsWriterVb.getParameter4()))
				drillIdKey =reportsWriterVb.getParameter4();
			else
				drillIdKey ="";
			logger.info("Parameter 1 : "+reportsWriterVb.getParameter1()+" Parameter 2 : "+reportsWriterVb.getParameter1()+" Parameter 3 : "+reportsWriterVb.getParameter3());
			con = getConnection();
			cs = con.prepareCall("{call "+reportsWriterVb.getProcedure1()+"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cs.setString(1, String.valueOf(intCurrentUserId));
	        cs.setString(2, sessionId);
	        cs.setString(3, reportsWriterVb.getReportId());
        	cs.setString(4, scalingFactor);//P_Scaling_Factor
        	cs.setString(5, reportsWriterVb.getParameter1());//P_Prompt_Value_1
        	cs.setString(6, reportsWriterVb.getParameter2());//P_Prompt_Value_2
        	cs.setString(7, reportsWriterVb.getParameter3());//P_Prompt_Value_3
        	cs.setString(8, drillIdKey);//P_Prompt_Value_4
        	cs.setString(9, "");//P_Prompt_Value_5
        	cs.setString(10, "");//P_Prompt_Value_6
        	cs.setString(11, "");//P_Prompt_Value_7
        	cs.setString(12, "");//P_Prompt_Value_8
        	cs.registerOutParameter(13, java.sql.Types.VARCHAR); //Table Name
	        cs.registerOutParameter(14, java.sql.Types.VARCHAR); //Status 
	        /*P_Status = -1, if there is an error; P_ErrorMsg will contain the error string
				p_Status =  0, if procedure executes successfully. P_ErrorMsg will contain nothing in this case
				p_Status =  1, Procedure has fetched NO records for the given query criteria. P_ErrorMsg will contain nothing.*/
	        cs.registerOutParameter(15, java.sql.Types.VARCHAR); //Error Message
//			ResultSet rs = cs.executeQuery();
	        cs.execute();
			promptTreeVb.setSessionId(sessionId);
			promptTreeVb.setReportId(reportsWriterVb.getReportId());
			promptTreeVb.setTableName(cs.getString(13));
            promptTreeVb.setStatus(cs.getString(14));
            promptTreeVb.setErrorMessage(cs.getString(15));
		    cs.close();
		    if("-1".equalsIgnoreCase(promptTreeVb.getStatus())){
		    	strErrorDesc = promptTreeVb.getErrorMessage();
				throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		    }
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return promptTreeVb;
	}
	public String getAdfLegalvehicle(ReportsWriterVb reportsWriterVb){
		String countryLebook [] = reportsWriterVb.getLegalVehiclesFlag().split("-");
		String query = "select LEGAL_VEHICLE||'-'||country||'-'||le_book from le_book  where "+ 
				"country = '"+countryLebook[0].trim()+"' "+ 
				"and le_book = '"+countryLebook[1].trim()+"'  ";
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType))
		query = "select concat(LEGAL_VEHICLE,'-',country,'-',le_book) from le_book  where "+ 
				"country = '"+countryLebook[0].trim()+"' "+ 
				"and le_book = '"+countryLebook[1].trim()+"'  ";
		return getJdbcTemplate().queryForObject(query, String.class);
	}	
	public String getAdfBusinessFeedDate(String Date){		
		
		
		String Queyr=	"SELECT TO_CHAR(TO_DATE('"+Date+"','DD-Mon-RRRR'),'DD-MON-RRRR') FROM VISION_BUSINESS_DAY WHERE COUNTRY='ZZ' AND LE_BOOK='99'";
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType))
		Queyr = "SELECT format(convert(date,'"+Date+"',103),'dd-Mon-yyyy')  FROM VISION_BUSINESS_DAY WHERE COUNTRY='ZZ' AND LE_BOOK='999'";
		return getJdbcTemplate().queryForObject(Queyr, String.class);
	}	
	public int doInsertionAudit(CronStatusVb vObject, String status){
		String query = "";
			query = "Insert Into VISION_NODE_CREDENTIALS_AUDIT ( SERVER_ENVIRONMENT , SERVER_NAME , NODE_NAME , NODE_IP , "
					+ "NODE_USER , NODE_PWD , MAKER , VERIFIER, DATE_LAST_MODIFIED, DATE_CREATION,NODE_STATUS ,CRON_NAME )"+
					"Values (?, ?, ?, ?, ?,?,?,?, "+systemDate+", "+systemDate+", ?, ?)";
		Object[] args = {vObject.getServerEnvironment(), vObject.getServerName(), vObject.getNodeName(), vObject.getNodeIp(),
				vObject.getNodeUser(), vObject.getNodePwd(),vObject.getMaker(),
			vObject.getVerifier(), status, vObject.getCronName()};  
		return getJdbcTemplate().update(query,args);
	}
	public List<AdfSchedulesVb> getQueryResultsForStatusBars(AdfSchedulesVb dObj, int status){
		setServiceDefaults();
		Vector<Object> params = new Vector<Object>();
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		dObj.setMaxRecords(10000);
		StringBuffer strBufApprove = new StringBuffer(" SELECT ADF_SCHEDULE_STATUS, "
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =1204 AND ADF_SCHEDULE_STATUS = ALPHA_SUB_TAB) ADF_SCHEDULE_STATUS_DESC, "
				+ " COUNT(ADF_NUMBER) STATUS_COUNT "+
			 " FROM ADF_SCHEDULES TAppr, LE_BOOK LEB  "+
			 " WHERE TAppr.Country = LEB.Country And TAppr.Le_Book = LEB.Le_Book ");

		try{
			
			if (ValidationUtil.isValid(dObj.getAdfNumber())){
				params.addElement(dObj.getAdfNumber());
				strBufApprove.append(" AND TAppr.ADF_NUMBER = ? ");
			}
			if (ValidationUtil.isValid(dObj.getCountry())){
				params.addElement(dObj.getCountry().toUpperCase());
				strBufApprove.append(" AND TAppr.COUNTRY = ? ");
			}
			if (ValidationUtil.isValid(CalLeBook)){
				params.addElement(CalLeBook);
				strBufApprove.append(" AND TAppr.LE_BOOK = ? ");
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
				strBufApprove.append(" AND TAppr.COUNTRY IN("+SessionContextHolder.getContext().getCountry().toUpperCase()+") ");
				strBufApprove.append(" AND TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getLeBook().toUpperCase()+") ");
			}	
			if(ValidationUtil.isValid(dObj.getMajorBuild()) && !"-1".equalsIgnoreCase(dObj.getMajorBuild())){
				params.addElement(dObj.getMajorBuild().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.MAJOR_BUILD) = UPPER(?) ");
			}else if(ValidationUtil.isValid(dObj.getAdfName()) && !"-1".equalsIgnoreCase(dObj.getAdfName())){
				params.addElement(dObj.getAdfName().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.MAJOR_BUILD) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getAcquisitionProcessType()) && !"-1".equalsIgnoreCase(dObj.getAcquisitionProcessType())){
				params.addElement(dObj.getAcquisitionProcessType().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.ACQUISITION_PROCESS_TYPE) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getFrequencyProcess()) && !"-1".equalsIgnoreCase(dObj.getFrequencyProcess())){
				params.addElement(dObj.getFrequencyProcess().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.FREQUENCY_PROCESS) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getAdfScheduleStatus()) && !"-1".equalsIgnoreCase(dObj.getAdfScheduleStatus())){
				params.addElement(dObj.getAdfScheduleStatus());
				strBufApprove.append(" AND TAppr.ADF_SCHEDULE_STATUS = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getStatus()) && !"-1".equalsIgnoreCase(dObj.getStatus())){
					params.addElement(dObj.getStatus());
					strBufApprove.append(" AND TAppr.ADF_SCHEDULE_STATUS = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate())){
		    	params.addElement(dObj.getBusinessDate());
		    	strBufApprove.append(" AND TAppr.BUSINESS_DATE = "+dateTimeConvert+" ");
			}
			if(ValidationUtil.isValid(dObj.getCategoryType()) && !"-1".equalsIgnoreCase(dObj.getCategoryType())){
				params.addElement(dObj.getCategoryType().toUpperCase());
				strBufApprove.append(" AND UPPER(LEB.CATEGORY_TYPE) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getSubmitterId())  && dObj.getSubmitterId()!=0){
				params.addElement(dObj.getSubmitterId());
				strBufApprove.append(" AND TAppr.SUBMITTER_ID = ? ");
			}
			if(ValidationUtil.isValid(dObj.getNode()) && !"-1".equalsIgnoreCase(dObj.getNode())){
				params.addElement(dObj.getNode());
				strBufApprove.append(" AND nvl(node_override, node_request) = ? ");
			}
			
			//Update Restriction - UBA
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction())){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					strBufApprove.append(" AND TAppr.COUNTRY IN("+toSqlInList(SessionContextHolder.getContext().getCountry().toUpperCase())+") ");
				}
				if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
					strBufApprove.append(" AND TAppr.LE_BOOK IN("+toSqlInList(SessionContextHolder.getContext().getLeBook().toUpperCase())+") ");
				}
			}
			String userGroupProie = visionUsersVb.getUserGroup()+"-"+visionUsersVb.getUserProfile();
			strBufApprove.append("AND (Exists (select 1 from build_access blda "+
                    " where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "+ 
                    " and blda.major_build = 'ALL' and blda.ba_status = 0) OR Exists (select 1  "+
                    " from build_access blda where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"'  "+
                    " and blda.major_build = TAppr.Major_Build and blda.ba_status = 0))");
			
			String orderBy = " ORDER BY START_TIME, COUNTRY, LE_BOOK ";
			strBufApprove.append(" GROUP BY ADF_SCHEDULE_STATUS ");
//			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, "", orderBy, params, getQueryforAdfScheduleResultsMapper());
			Object objParams[] = new Object[params.size()];
			for(int Ctr=0; Ctr < params.size(); Ctr++)
				objParams[Ctr] = (Object) params.elementAt(Ctr);
			
			return getJdbcTemplate().query(strBufApprove.toString(),objParams,getQueryforAdfScheduleStatusMapper());

		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	

	protected RowMapper getQueryforAdfScheduleStatusMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
				adfSchedulesVb.setAdfScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
				adfSchedulesVb.setStatusDesc(rs.getString("ADF_SCHEDULE_STATUS_DESC"));
				adfSchedulesVb.setParallelProcsCount(rs.getInt("STATUS_COUNT"));
				return adfSchedulesVb;
			}
		};
		return mapper;
	}
	public int doUpdateScheduleStatus(AdfSchedulesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String frequencyProcessModified=vObject.getFrequencyProcess();
//		vObject.setAdfScheduleStatus("K");
		vObject.setSubmitterId((int)intCurrentUserId);
		/*frequencyProcessModified=frequencyProcessModified.replaceFirst("ACQ", "");
		frequencyProcessModified=frequencyProcessModified.substring(0,(frequencyProcessModified.length()-4));*/
		
		String query = "UPDATE ADF_SCHEDULES SET  SUBMITTER_ID = ?, DATE_LAST_MODIFIED = "+systemDate+" ,"
				+ " TERMINATE_FLAG = 'Y' WHERE COUNTRY=? AND LE_BOOK=? "
				+ "AND BUSINESS_DATE="+dateTimeConvert+" AND FREQUENCY_PROCESS=? AND MAJOR_BUILD = ? AND ADF_NUMBER = ? ";
		
		if(ValidationUtil.isValid(vObject.getAdfName())) {
			vObject.setMajorBuild(vObject.getAdfName().toUpperCase());
		}
		Object[] args = { vObject.getSubmitterId(), vObject.getCountry(), CalLeBook, vObject.getBusinessDate(), frequencyProcessModified, vObject.getMajorBuild(), vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
}