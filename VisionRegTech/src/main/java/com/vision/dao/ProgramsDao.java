package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.vb.ProgramsVb;

@Component
public class ProgramsDao extends com.vision.dao.AbstractDao<ProgramsVb> {
	@Override
	protected void setServiceDefaults(){
		serviceName = "Programs";
		serviceDesc = "Programs";
		tableName = "PROGRAMS";
		childTableName = "PROGRAMS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	protected RowMapper getQueryResultsMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProgramsVb programsVb = new ProgramsVb();
				programsVb.setProgram(rs.getString("PROGRAM"));
				programsVb.setProgramDescription(rs.getString("PROGRAM_DESCRIPTION"));
				programsVb.setProgramTypeAt(rs.getInt("PROGRAM_TYPE_AT"));
				programsVb.setProgramType(rs.getString("PROGRAM_TYPE"));
				programsVb.setSubmit(rs.getString("SUBMIT"));
				programsVb.setReconfirmation(rs.getString("RECONFIRMATION"));
				programsVb.setAccessCount(rs.getString("ACCESS_COUNT"));
				programsVb.setNotify(rs.getString("NOTIFY"));
				programsVb.setSupportContactMail(rs.getString("SUPPORT_CONTACT_MAIL"));
				programsVb.setSupportContactMobile(rs.getString("SUPPORT_CONTACT_MOBILE"));
				programsVb.setYearApplicabilityAt(rs.getInt("YEAR_APPLICABILITY_AT"));
				programsVb.setYearApplicability(rs.getString("YEAR_APPLICABILITY"));
				programsVb.setDataSourceApplicabilityAt(rs.getInt("DATA_SOURCE_APPLICABILITY_AT"));
				programsVb.setDataSourceApplicability(rs.getString("DATA_SOURCE_APPLICABILITY"));
				programsVb.setLastRunDate(rs.getString("LAST_RUN_DATE"));
				programsVb.setProgramAttribute1At(rs.getInt("PROGRAM_ATTRIBUTE_1_AT"));
				programsVb.setProgramAttribute1(rs.getString("PROGRAM_ATTRIBUTE_1"));
				programsVb.setProgramAttribute2At(rs.getInt("PROGRAM_ATTRIBUTE_2_AT"));
				programsVb.setProgramAttribute2(rs.getString("PROGRAM_ATTRIBUTE_2"));
				programsVb.setProgramStatusNt(rs.getInt("PROGRAM_STATUS_NT"));
				programsVb.setProgramStatus(rs.getInt("PROGRAM_STATUS"));
				programsVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				programsVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				programsVb.setMaker(rs.getLong("MAKER"));
				programsVb.setVerifier(rs.getLong("VERIFIER"));
				programsVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				programsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				programsVb.setDateCreation(rs.getString("DATE_CREATION"));
				return programsVb;
			}
		};
		return mapper;
	}
	public ProgramsVb getSupportContactDetails(String build){
		final int intKeyFieldsCount = 1;
		String query = new String("SELECT PROGRAM, PROGRAM_DESCRIPTION, PROGRAM_TYPE_AT, " +    
		   "PROGRAM_TYPE, SUBMIT, RECONFIRMATION, ACCESS_COUNT, NOTIFY, SUPPORT_CONTACT_MAIL, " +   
		   "SUPPORT_CONTACT_MOBILE, YEAR_APPLICABILITY_AT, YEAR_APPLICABILITY, " +   
		   "DATA_SOURCE_APPLICABILITY_AT, DATA_SOURCE_APPLICABILITY, LAST_RUN_DATE, " +  
		   "PROGRAM_ATTRIBUTE_1_AT, PROGRAM_ATTRIBUTE_1, PROGRAM_ATTRIBUTE_2_AT, " +  
		   "PROGRAM_ATTRIBUTE_2, PROGRAM_STATUS_NT, PROGRAM_STATUS, " +  
		   "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, " +  
		   "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION " +  
		   "FROM PROGRAMS WHERE PROGRAM = ?");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = build; //[BUILD]
		try
		{
			return (ProgramsVb) getJdbcTemplate().queryForObject(query, params, getQueryResultsMapper());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return null;
		}
	}
	public List<ProgramsVb> getMajorBuildList(){
		String query = new String("SELECT PROGRAM, PROGRAM_DESCRIPTION, PROGRAM_TYPE_AT, " +    
		   "PROGRAM_TYPE, SUBMIT, RECONFIRMATION, ACCESS_COUNT, NOTIFY, SUPPORT_CONTACT_MAIL, " +   
		   "SUPPORT_CONTACT_MOBILE, YEAR_APPLICABILITY_AT, YEAR_APPLICABILITY, DATA_SOURCE_APPLICABILITY_AT, " +
		   "DATA_SOURCE_APPLICABILITY, LAST_RUN_DATE, PROGRAM_ATTRIBUTE_1_AT, PROGRAM_ATTRIBUTE_1, PROGRAM_ATTRIBUTE_2_AT, " +  
		   "PROGRAM_ATTRIBUTE_2, PROGRAM_STATUS_NT, PROGRAM_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, " +  
		   "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION " +  
		   "FROM PROGRAMS WHERE PROGRAM_TYPE = 'MAJORBLD' AND PROGRAM_STATUS=0 ORDER BY SORT_FIELD ");
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
	
	public List<ProgramsVb> getMajorBuildListForFin(){
		String query = new String("SELECT PROGRAM, PROGRAM_DESCRIPTION, PROGRAM_TYPE_AT, " +    
		   "PROGRAM_TYPE, SUBMIT, RECONFIRMATION, ACCESS_COUNT, NOTIFY, SUPPORT_CONTACT_MAIL, " +   
		   "SUPPORT_CONTACT_MOBILE, YEAR_APPLICABILITY_AT, YEAR_APPLICABILITY, DATA_SOURCE_APPLICABILITY_AT, " +
		   "DATA_SOURCE_APPLICABILITY, LAST_RUN_DATE, PROGRAM_ATTRIBUTE_1_AT, PROGRAM_ATTRIBUTE_1, PROGRAM_ATTRIBUTE_2_AT, " +  
		   "PROGRAM_ATTRIBUTE_2, PROGRAM_STATUS_NT, PROGRAM_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, " +  
		   "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION " +  
		   "FROM PROGRAMS WHERE PROGRAM_TYPE = 'MAJORBLD' AND PROGRAM_STATUS=0 " +
		   "AND PROGRAM ='MGTBLD' ORDER BY SORT_FIELD ");
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
}