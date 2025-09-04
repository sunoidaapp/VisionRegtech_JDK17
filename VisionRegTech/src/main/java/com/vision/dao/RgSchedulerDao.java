package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.vb.CommonVb;
import com.vision.vb.RgBuildsVb;

@Component
public class RgSchedulerDao extends AbstractDao<CommonVb>{
	@Autowired
	CommonApiDao commonApiDao;
	
	public List<RgBuildsVb> getRgBuildsSchedules(RgBuildsVb vObject) {
		List<RgBuildsVb> collTemp = null;
		try {
			String query = "  SELECT T1.Country,                           "+
						"          T1.LE_Book,                             "+
						"          T1.BUILD_ID,                             "+
						"          T1.Submitter_Id,                        "+
						"          T2.DEPENDENT_PROGRAM,                   "+
						"		   T1.SEQUENCE                             "+
						"     FROM Rg_Build_Schedules t1, Rg_Programs t2 "+
						"    WHERE     T1.Build_Schedule_Status = 'P'     "+
						"          AND T1.COUNTRY = T2.COUNTRY             "+
						"          AND T1.LE_BOOK = T2.LE_BOOK             "+
						"          AND T1.BUILD_ID = T2.PROGRAM             "+
						"		   AND T1.COUNTRY = ?                      "+
                        "          AND T1.LE_BOOK = ?					     "+	
                        "          AND T1.BUILD_ID = ?						   	 "+
                        "          AND T1.SEQUENCE = ? 						 "+
						" ORDER BY T1.Scheduled_Date,BUILD_NUMBER            ";
			
			Object args[] = {vObject.getCountry(),vObject.getLeBook(),vObject.getBuildId(),vObject.getSequence()};
			collTemp = getJdbcTemplate().query(query,args,getBuildScheduleMapper());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}
	protected RowMapper getBuildScheduleMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgBuildsVb vObject = new RgBuildsVb();
				vObject.setCountry(rs.getString("Country"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBuildId(rs.getString("BUILD_ID"));
				vObject.setDependentProgram(rs.getString("DEPENDENT_PROGRAM"));
				vObject.setSubmitterId(rs.getString("SUBMITTER_ID"));
				vObject.setSequence(rs.getInt("SEQUENCE"));
				return vObject;
			}
		};
		return mapper;
	}
	public int updateRgBuildSchedules(RgBuildsVb vObject,String status) {
		int retVal = 0;
		try {
			String updateCondition = "";
			if("I".equalsIgnoreCase(status)) 
				updateCondition = " START_TIME = SYSDATE,LOG_FILE = '"+vObject.getLogFile()+"' ";
			else if("C".equalsIgnoreCase(status) || "E".equalsIgnoreCase(status))
				updateCondition = " END_TIME = SYSDATE ";
			
			String query = "UPDATE RG_BUILD_SCHEDULES SET "+updateCondition+",BUILD_SCHEDULE_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND BUILD_ID = ? AND SEQUENCE = ? ";
			
			Object args[] = {status,vObject.getCountry(), vObject.getLeBook(), vObject.getBuildId(),vObject.getSequence()};
			
			retVal = getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	public int updateRgBuildSchedulesHeader(RgBuildsVb vObject,String status) {
		int retVal = 0;
		try {
			String query = "UPDATE RG_BUILD_SCHEDULES_HEADER SET SCHEDULE_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND  BUILD_ID = ? AND SEQUENCE = ? ";
			
			Object args[] = {status,vObject.getCountry(), vObject.getLeBook(), vObject.getBuildId(),vObject.getSequence()};
			
			retVal = getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	public int getDependencyStatus(RgBuildsVb vObject) {
		int retVal = 0;
		try {
			String query = " SELECT COUNT (1)                            "+
					"   FROM RG_PROGRAMS T1, RG_BUILD_SCHEDULES_HIS T2 "+
					"  WHERE     T1.PROGRAM = ?                          "+
					"        AND T1.COUNTRY = ?                          "+
					"        AND T1.LE_BOOK = ?                          "+
					"        AND T1.DEPENDENT_PROGRAM IS NOT NULL        "+
					"        AND T1.DEPENDENT_PROGRAM = T2.PROGRAM       "+
					"        AND T1.COUNTRY = T2.COUNTRY                 "+
					"        AND T1.LE_BOOK = T2.LE_BOOK                 "+
					"        AND T2.BUILD_SCHEDULE_STATUS = 'C'          ";
			
			Object args[] = {vObject.getProgram(),vObject.getCountry(), vObject.getLeBook()};
			retVal = getJdbcTemplate().queryForObject(query,args,Integer.class);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	public int updateLastRunStatus(RgBuildsVb vObject,String status) {
		int retVal = 0;
		try {
			String query = "UPDATE RG_PROGRAMS SET LAST_RUN_STATUS = ?,LAST_RUN_DATE = SYSDATE,LAST_EXECUTED_BY = ? "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND PROGRAM = ? ";
			
			Object args[] = {status,vObject.getSubmitterId(),vObject.getCountry(), vObject.getLeBook(),
					 vObject.getBuildId()};
			retVal = getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	public int moveScheduleToHistory(RgBuildsVb vObject) {
		int retVal = 0;
		try {
			String query = " INSERT INTO RG_BUILD_SCHEDULES_HIS "+                  
					" SELECT * FROM RG_BUILD_SCHEDULES "+ 
					" WHERE COUNTRY = ? AND LE_BOOK = ?  "+
					"  AND BUILD_ID  = ? AND SEQUENCE = ? ";
			
			Object args[] = {vObject.getCountry(), vObject.getLeBook(),vObject.getBuildId(),vObject.getSequence()};
			retVal = getJdbcTemplate().update(query,args);
			
			query = "DELETE FROM RG_BUILD_SCHEDULES "+ 
					" WHERE COUNTRY = ? AND LE_BOOK = ?  "+
					"  AND BUILD_ID  = ? AND SEQUENCE = ? ";
			retVal = getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
	public List<RgBuildsVb> getRgBuildsSchedulesHeader() {
		List<RgBuildsVb> collTemp = null;
		try {
			String query = " SELECT SEQUENCE,                       "+
					"        COUNTRY,                               "+
					"        LE_BOOK,                               "+
					"        BUILD_ID                            "+
					"   FROM RG_BUILD_SCHEDULES_HEADER             "+
					"  WHERE     SEQUENCE = (SELECT MIN (SEQUENCE)  "+
					"  FROM RG_BUILD_SCHEDULES_HEADER)             "+
					"        AND SCHEDULE_STATUS = 'P'              ";
			
			collTemp = getJdbcTemplate().query(query,getBuildScheduleHeaderMapper());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}
	protected RowMapper getBuildScheduleHeaderMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgBuildsVb vObject = new RgBuildsVb();
				vObject.setSequence(rs.getInt("SEQUENCE"));
				vObject.setCountry(rs.getString("Country"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBuildId(rs.getString("BUILD_ID"));
				return vObject;
			}
		};
		return mapper;
	}
	public int deleteScheduleHeader(RgBuildsVb vObject) {
		int retVal = 0;
		try {
			String query = "DELETE FROM RG_BUILD_SCHEDULES_HEADER "+ 
					" WHERE COUNTRY = ? AND LE_BOOK = ?  "+
					" AND BUILD_ID = ?  AND SEQUENCE = ? ";
			
			Object args[] = {vObject.getCountry(), vObject.getLeBook(), vObject.getBuildId(),vObject.getSequence()};

			retVal = getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return retVal;
	}
}
