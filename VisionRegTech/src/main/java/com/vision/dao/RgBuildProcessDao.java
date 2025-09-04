package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.vb.RgBuildsVb;
import com.vision.vb.RgMatchRuleHeaderVb;

@Component
public class RgBuildProcessDao extends AbstractDao<RgMatchRuleHeaderVb> {

	public List<RgBuildsVb> getRgBuilds(RgBuildsVb rgBuildsVb) {
		List<RgBuildsVb> collTemp = null;
		try {
			String query = " SELECT S1.*,                                                                                    "
					+ "        (SELECT USER_NAME                                                                             "
					+ "           FROM VISION_USERS                                                                          "
					+ "          WHERE VISION_ID = SUBMITTER_ID)                                                             "
					+ "           USER_NAME,                                                                                 "
					+ "        (SELECT ALPHA_SUBTAB_DESCRIPTION                                                              "
					+ "           FROM ALPHA_SUB_TAB                                                                         "
					+ "          WHERE ALPHA_TAB = 6520 AND ALPHA_SUB_TAB = S1.STATUS)                                       "
					+ "           STATUS_DESC                                                                                "
					+ "   FROM (  SELECT T1.COUNTRY,                                                                         "
					+ "                  T1.LE_BOOK,                                                                         "
					+ "                  T1.PROGRAM_SEQ,                                                                     "
					+ "                  T1.PROGRAM,                                                                         "
					+ "                  T1.PROGRAM_DESCRIPTION,                                                             "
					+ "                  NVL (T2.SUBMITTER_ID, T1.LAST_EXECUTED_BY) SUBMITTER_ID,                            "
					+ "                  TO_CHAR (NVL(T2.START_TIME,T3.START_TIME), 'DD-Mon-RRRR HH24:MI:SS') START_TIME,    "
					+ "                  TO_CHAR (NVL(T2.END_TIME,T3.END_TIME), 'DD-Mon-RRRR HH24:MI:SS') END_TIME,          "
					+ "                  TO_CHAR (T1.LAST_RUN_DATE, 'DD-Mon-RRRR HH24:MI:SS')                                "
					+ "                     LAST_RUN_DATE,                                                                   "
					+ "                  NVL (T2.BUILD_SCHEDULE_STATUS, T1.LAST_RUN_STATUS) STATUS,                          "
					+ "                  CASE when T2.BUILD_SCHEDULE_STATUS = 'I' then t2.log_file                           "
					+ "					else   T3.LOG_FILE end LOG_FILE                                                      "
					+ "             FROM RG_PROGRAMS T1, RG_BUILD_SCHEDULES T2,                                              "
					+ "                 (SELECT COUNTRY, LE_BOOK, BUILD_ID, START_TIME, END_TIME, LOG_FILE                   "
					+ "              FROM RG_BUILD_SCHEDULES_HIS WHERE COUNTRY = ? AND LE_BOOK = ?                           "
					+ "            AND SEQUENCE = ( SELECT MAX(SEQUENCE) FROM RG_BUILD_SCHEDULES_HIS                         "
					+ "              WHERE COUNTRY = ? AND LE_BOOK = ?   ) ) T3                                              "
					+ "            WHERE  T1.PROGRAM_STATUS=0                                                                "
					+ "                  AND T1.PROGRAM = T2.BUILD_ID(+)                                                     "
					+ "                  AND T1.COUNTRY = T2.COUNTRY(+)                                                      "
					+ "                  AND T1.LE_BOOK = T2.LE_BOOK(+)                                                      "
					+ "                  AND T1.COUNTRY = ?                                                                  "
					+ "                  AND T1.LE_BOOK = ?                                                                  "
					+ "                  AND T1.PROGRAM = T3.BUILD_ID(+)                                                     "
					+ "                  AND T1.COUNTRY = T3.COUNTRY(+)                                                      "
					+ "                  AND T1.LE_BOOK = T3.LE_BOOK(+)                                                      "
					+ "         ORDER BY T1.PROGRAM_SEQ) S1                                                                  ";

			Object args[] = { rgBuildsVb.getCountry(), rgBuildsVb.getLeBook(), rgBuildsVb.getCountry(),
					rgBuildsVb.getLeBook(), rgBuildsVb.getCountry(), rgBuildsVb.getLeBook() };

			collTemp = getJdbcTemplate().query(query, getBuildMapper(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}

	protected RowMapper getBuildMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgBuildsVb vObject = new RgBuildsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setProgramSeq(rs.getInt("PROGRAM_SEQ"));
				vObject.setProgram(rs.getString("PROGRAM"));
				vObject.setProgramDesc(rs.getString("PROGRAM_DESCRIPTION"));
				vObject.setSubmitterId(rs.getString("USER_NAME"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setLastRunDate(rs.getString("LAST_RUN_DATE"));
				vObject.setRunStatus(rs.getString("STATUS"));
				vObject.setRunStatusDesc(rs.getString("STATUS_DESC"));
				vObject.setLogFile(rs.getString("LOG_FILE"));
				// vObject.setPlanningStatus(rs.getString("RULE_STATUS"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<RgBuildsVb> getRgBuildsSummary(RgBuildsVb rgBuildsVb) {
		List<RgBuildsVb> collTemp = null;
		try {
			String query = " SELECT Build_Schedule_Status, SUM (cnt) Status_Cnt                    "
					+ "     FROM (  SELECT Build_Schedule_Status, COUNT (Build_Schedule_Status) cnt  "
					+ "               FROM Rg_BUILD_SCHEDULES                                       "
					+ "              WHERE     Country = ?                                           "
					+ "                    AND LE_Book = ?                                           "
					+ "           GROUP BY Build_Schedule_Status                                     "
					+ "           UNION                                                              "
					+ "           SELECT ALpha_sub_tab, 0                                            "
					+ "             FROM Alpha_sub_tab                                               "
					+ "            WHERE Alpha_tab = 6520)                                           "
					+ " GROUP BY Build_Schedule_Status                                               ";

			Object args[] = { rgBuildsVb.getCountry(), rgBuildsVb.getLeBook() };
			collTemp = getJdbcTemplate().query(query, getBuildSummaryMapper(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}

	protected RowMapper getBuildSummaryMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgBuildsVb vObject = new RgBuildsVb();
				vObject.setRunStatus(rs.getString("Build_Schedule_Status"));
				vObject.setStatusCnt(rs.getInt("Status_Cnt"));
				return vObject;
			}
		};
		return mapper;
	}

	public String getRgBuildStatus() {
		try {
			String sql = "SELECT CRON_RUN_STATUS FROM PRD_CRON_CONTROL WHERE CRON_TYPE = 'RG_BUILD_CRON' ";
			return getJdbcTemplate().queryForObject(sql, String.class);
		} catch (Exception e) {
			return "";
		}
	}

	public int getMaxRgBuildMaxSeq(RgBuildsVb rgBuildsVb) {
		int retVal = 0;
		try {
			String query = " SELECT NVL (MAX (SEQUENCE), 0) + 1                            "
					+ "   FROM Rg_BUILD_SCHEDULES_HIS                                       "
					+ "  WHERE COUNTRY = ? AND LE_BOOK = ?   ";

			Object args[] = { rgBuildsVb.getCountry(), rgBuildsVb.getLeBook() };
			retVal = getJdbcTemplate().queryForObject(query, Integer.class, args);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertionRgBuildHeader(RgBuildsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			setServiceDefaults();
			String query = "Insert Into RG_BUILD_SCHEDULES_HEADER (SEQUENCE,COUNTRY, LE_BOOK, BUILD_ID,  "
					+ "SUBMITTER_ID,SCHEDULE_STATUS,SCHEDULED_DATE,DATE_LAST_MODIFIED, DATE_CREATION)"
					+ "Values (?, ?, ?, ?,?, ?,sysDate,SysDate, SysDate)";
			Object[] args = { vObject.getSequence(), vObject.getCountry(), vObject.getLeBook(), vObject.getBuildId(),
					intCurrentUserId, "P" };

			int retVal = getJdbcTemplate().update(query, args);
			if (retVal == 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Rg Builds Header - Record not inserted..");
				
				return exceptionCode;
			} else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Rg Builds Header - Added Successfully");
			}
			if (vObject.getRgBuildlst() != null && vObject.getRgBuildlst().size() > 0) {
				for (RgBuildsVb dObj : vObject.getRgBuildlst()) {
					dObj.setSequence(vObject.getSequence());
					exceptionCode = doInsertionRgBuildSchedule(dObj);
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		
		return exceptionCode;
	}

	@Override
	protected void setServiceDefaults() {
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	public ExceptionCode doInsertionRgBuildSchedule(RgBuildsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			setServiceDefaults();
			String query = "Insert Into RG_BUILD_SCHEDULES (SEQUENCE,COUNTRY, LE_BOOK,BUILD_ID, SCHEDULED_DATE, "
					+ "BUILD_NUMBER, SUBMITTER_ID, BUILD_SCHEDULE_STATUS,DATE_LAST_MODIFIED, DATE_CREATION)"
					+ "Values (?, ?,?, ?, sysDate, ?, ?, ?,SysDate, SysDate)";
			Object[] args = { vObject.getSequence(), vObject.getCountry(), vObject.getLeBook(), vObject.getBuildId(),
					vObject.getProgramSeq(), intCurrentUserId, "P" };

			int retVal = getJdbcTemplate().update(query, args);
			if (retVal == 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Rg Builds - Record not inserted..");
			} else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Rg Builds - Added Successfully");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public List<RgBuildsVb> getBuildHistory(RgBuildsVb rgBuildsVb) {
		List<RgBuildsVb> collTemp = null;
		try {
			String query = " SELECT COUNTRY,                                                     "
					+ "        LE_BOOK,                                                            "
					+ "        PROGRAM,                                                            "
					+ "        (SELECT PROGRAM_DESCRIPTION                                         "
					+ "           FROM RG_PROGRAMS                                                "
					+ "          WHERE     COUNTRY = T1.COUNTRY                                    "
					+ "                AND LE_BOOK = T1.LE_BOOK                                    "
					+ "                AND PROGRAM = T1.BUILD_ID)                                   "
					+ "           PROGRAM_DESC,                                                    "
					+ "        TO_CHAR (SCHEDULED_DATE, 'DD-Mon-RRRR HH24:MI:SS') SCHEDULED_DATE,  "
					+ "        TO_CHAR (START_TIME, 'DD-Mon-RRRR HH24:MI:SS') START_TIME,          "
					+ "        TO_CHAR (END_TIME, 'DD-Mon-RRRR HH24:MI:SS') END_TIME,              "
					+ "        BUILD_SCHEDULE_STATUS,                                              "
					+ "        (SELECT ALPHA_SUBTAB_DESCRIPTION                                    "
					+ "           FROM ALPHA_SUB_TAB                                               "
					+ "          WHERE ALPHA_TAB = 6520 AND ALPHA_SUB_TAB = BUILD_SCHEDULE_STATUS) "
					+ "           BUILD_SCHEDULE_STATUS_DESC,                                      "
					+ "           SUBMITTER_ID||' - '||(SELECT USER_NAME                           "
					+ "                    FROM VISION_USERS                                       "
					+ "                    WHERE VISION_ID = SUBMITTER_ID)                         "
					+ "                    USER_NAME,LOG_FILE                                      "
					+ "   FROM RG_BUILD_SCHEDULES_HIS T1                                          "
					+ "  WHERE     COUNTRY = ?                                                     "
					+ "        AND LE_BOOK = ?                                                     "
					+ "        AND BUILD_ID = ?  ORDER BY SCHEDULED_DATE DESC                        ";

			Object args[] = { rgBuildsVb.getCountry(), rgBuildsVb.getLeBook(), rgBuildsVb.getBuildId() };
			collTemp = getJdbcTemplate().query(query, args, getBuildHistoryMapper());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return collTemp;
	}

	protected RowMapper getBuildHistoryMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgBuildsVb vObject = new RgBuildsVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setProgram(rs.getString("PROGRAM"));
				vObject.setProgramDesc(rs.getString("PROGRAM_DESC"));
				vObject.setSubmitterId(rs.getString("USER_NAME"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setRunStatus(rs.getString("BUILD_SCHEDULE_STATUS"));
				vObject.setRunStatusDesc(rs.getString("BUILD_SCHEDULE_STATUS_DESC"));
				vObject.setLogFile(rs.getString("LOG_FILE"));
				return vObject;
			}
		};
		return mapper;
	}
}
