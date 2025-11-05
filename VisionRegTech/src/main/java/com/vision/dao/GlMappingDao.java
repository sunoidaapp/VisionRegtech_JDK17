package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.GLMappingVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionDynamicHashVariablesVb;
import com.vision.vb.GLMappingVb;
import com.vision.vb.VisionUsersVb;

@Component
public class GlMappingDao extends AbstractDao<GLMappingVb> {

	@Autowired
	CommonDao commonDao;
	@Override
	protected void setServiceDefaults() {
		serviceName = "GlMapping";
		serviceDesc = "GL Mapping";
		tableName = "GL_MAPPINGS";
		childTableName = "GL_MAPPINGS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	String mappingStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_MAPPING_STATUS", "MAPPING_STATUS_DESC");
	String mappingStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_MAPPING_STATUS", "MAPPING_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	
	String glAttributeTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1004, "TAppr.GL_MAPPING_ATTRIBUTE_TYPE", "GL_MAPPING_ATTRIBUTE_TYPE_DESC");
	String glAttributeTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1004, "TPend.GL_MAPPING_ATTRIBUTE_TYPE", "GL_MAPPING_ATTRIBUTE_TYPE_DESC");
	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				GLMappingVb vObject = new GLMappingVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setGlMappingStatus(rs.getInt("GL_MAPPING_STATUS"));
				vObject.setStatusDesc(rs.getString("MAPPING_STATUS_DESC"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setGlMappingGroupId(rs.getString("GL_MAPPING_GROUP_ID"));
				vObject.setGlCode(rs.getString("GL_CODE"));
				vObject.setGlCodeDesc(rs.getString("GL_CODE_DESC"));
				vObject.setCbGlCode(rs.getString("CB_GL_CODE"));
				vObject.setCbGlCodeDesc(rs.getString("CB_GL_CODE_DESC"));
				
				vObject.setGlMappingSeq(rs.getString("GL_MAPPING_SEQUENCE"));
				vObject.setGlMappingAttrType(rs.getString("GL_MAPPING_ATTRIBUTE_TYPE"));
				vObject.setGlMappingAttr(rs.getString("GL_MAPPING_ATTRIBUTE"));
				return vObject;
			}
		};
		return mapper;
	}
	
	
	public List<GLMappingVb> getQueryPopupResults(GLMappingVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer(" SELECT * FROM ( SELECT TAPPR.COUNTRY, TAPPR.LE_BOOK, TAPPR.TEMPLATE_ID, TAPPR.GL_CODE,"
				+ "(SELECT GL_DESCRIPTION   FROM GL_CODES  WHERE GL_STATUS = 0 AND VISION_GL =TAPPR.GL_CODE ) GL_CODE_DESC,"
				+ " TAPPR.GL_MAPPING_GROUP_ID, TAPPR.GL_MAPPING_SEQUENCE,  "
			    +"  TAPPR.GL_MAPPING_ATTRIBUTE_TYPE_AT, TAPPR.GL_MAPPING_ATTRIBUTE_TYPE, TAPPR.GL_MAPPING_ATTRIBUTE,  "
			    +"  TAPPR.CB_GL_CODE,"+glAttributeTypeNtApprDesc
			    + ",(SELECT GL_DESCRIPTION   FROM CB_GL_CODES  WHERE GL_STATUS = 0 AND GL_CODE =TAPPR.CB_GL_CODE ) CB_GL_CODE_DESC,"
			    + " TAPPR.GL_MAPPING_STATUS_NT, TAPPR.GL_MAPPING_STATUS, TAPPR.RECORD_INDICATOR_NT, TAPPR.RECORD_INDICATOR, TAPPR.MAKER,  "
			    +"  TAPPR.VERIFIER, TAPPR.INTERNAL_STATUS,"
			    + dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TAPPR.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1 ," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
			    +makerApprDesc+" , "+verifierApprDesc+" , "
			    + RecordIndicatorNtApprDesc+" , "+mappingStatusNtApprDesc 
			    + " FROM GL_MAPPINGS TAPPR "
			   +")TAppr ");

		strWhereNotExists = new String(
				" NOT EXISTS (SELECT 'X' FROM GL_MAPPINGS_PEND TPEND WHERE TAPPR.COUNTRY = TPEND.COUNTRY "
						+ " AND TAPPR.LE_BOOK = TPEND.LE_BOOK" + " AND TAPPR.TEMPLATE_ID =TPEND.TEMPLATE_ID "
						 +"   AND   TAPPR.GL_CODE= TPEND.GL_CODE                      "
						    +"   AND   TAPPR.GL_MAPPING_GROUP_ID=  TPEND.GL_MAPPING_GROUP_ID          "
						    +"   AND   TAPPR.GL_MAPPING_SEQUENCE=  TPEND.GL_MAPPING_SEQUENCE          "
						    +"   AND    TAPPR.GL_MAPPING_ATTRIBUTE_TYPE= TPEND.GL_MAPPING_ATTRIBUTE_TYPE    "
						    +"   AND     TAPPR.GL_MAPPING_ATTRIBUTE=TPEND.GL_MAPPING_ATTRIBUTE         "		
						+ ")");

		strBufPending = new StringBuffer(" SELECT * FROM (SELECT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.TEMPLATE_ID, TPEND.GL_CODE, "
				+ "(SELECT GL_DESCRIPTION   FROM GL_CODES  WHERE GL_STATUS = 0 AND VISION_GL =TPEND.GL_CODE ) GL_CODE_DESC,"
				+ "TPEND.GL_MAPPING_GROUP_ID, TPEND.GL_MAPPING_SEQUENCE,  "
			    +"  TPEND.GL_MAPPING_ATTRIBUTE_TYPE_AT, TPEND.GL_MAPPING_ATTRIBUTE_TYPE, TPEND.GL_MAPPING_ATTRIBUTE,  "
			    +"  TPEND.CB_GL_CODE,"+glAttributeTypeNtPendDesc
			    + ",(SELECT GL_DESCRIPTION   FROM CB_GL_CODES  WHERE GL_STATUS = 0 AND GL_CODE =TPEND.CB_GL_CODE ) CB_GL_CODE_DESC,"
			    + " TPEND.GL_MAPPING_STATUS_NT, TPEND.GL_MAPPING_STATUS, TPEND.RECORD_INDICATOR_NT, TPEND.RECORD_INDICATOR, TPEND.MAKER,  "
			    +"  TPEND.VERIFIER, TPEND.INTERNAL_STATUS,"
			    + dbFunctionFormats("TPEND.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TPEND.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1 ," + " "
				+ dbFunctionFormats("TPEND.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
			    +makerPendDesc+" , "+verifierPendDesc+" , "
			    + RecordIndicatorNtPendDesc+" , "+mappingStatusNtPendDesc 
			    + " FROM GL_MAPPINGS_PEND TPEND"
			    +" )TPend");

		try {

			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.COUNTRY) " + val, strBufPending, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.LE_BOOK) " + val, strBufPending, data.getJoinType());
						break;

					case "glCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.GL_CODE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.GL_CODE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;


					case "glMappingGroupId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.GL_MAPPING_GROUP_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.GL_MAPPING_GROUP_ID ) " + val, strBufPending,
								data.getJoinType());
						break;
					case "glMappingSeq":
						CommonUtils.addToQuerySearch(" upper(TAPPR.GL_MAPPING_SEQUENCE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.GL_MAPPING_SEQUENCE ) " + val, strBufPending,
								data.getJoinType());
						break;
					case "glMappingAttr":
						CommonUtils.addToQuerySearch(" upper(TAPPR.GL_MAPPING_ATTRIBUTE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.GL_MAPPING_ATTRIBUTE ) " + val, strBufPending,
								data.getJoinType());
						break;
					case "cbGlCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CB_GL_CODE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.CB_GL_CODE_DESC ) " + val, strBufPending,
								data.getJoinType());
						break;
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glMappingStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.MAPPING_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.MAPPING_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;	
					case "glMappingAttrType":
						CommonUtils.addToQuerySearch(" upper(TAPPR.GL_MAPPING_ATTRIBUTE_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.GL_MAPPING_ATTRIBUTE_TYPE_DESC ) " + val, strBufPending,
								data.getJoinType());
						break;
						
					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
					CommonUtils.addToQuery(" COUNTRY IN ('" + visionUsersVb.getCountry() + "') ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN ('" + visionUsersVb.getCountry() + "') ", strBufPending);
				}
				if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
					CommonUtils.addToQuery(" LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ", strBufPending);
				}
			}
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				String calLeBook = removeDescLeBook(dObj.getLeBook());
				CommonUtils.addToQuery(" LE_BOOK IN ('" + calLeBook + "') ", strBufApprove);
				CommonUtils.addToQuery(" LE_BOOK IN ('" + calLeBook + "') ", strBufPending);
			}
//			params.addElement(dObj.getCountry());
//			params.addElement(dObj.getLeBook());
//			params.addElement(dObj.getTemplateId());
			
//			 orderBy = " Order By "+dbFunctionFormats("DATE_LAST_MODIFIED","TO_DATETIME_FORMAT",null)+" DESC";
			orderBy = " Order by DATE_LAST_MODIFIED DESC";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			if (params != null)
				for (int i = 0; i < params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	@Override
	protected List<GLMappingVb> selectApprovedRecord(GLMappingVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<GLMappingVb> doSelectPendingRecord(GLMappingVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	
	public List<GLMappingVb> getQueryResults(GLMappingVb dObj, int intStatus){

		List<GLMappingVb> collTemp = null;
		final int intKeyFieldsCount = 8;
		setServiceDefaults();
		String strQueryAppr = new String(" SELECT TAPPR.COUNTRY, TAPPR.LE_BOOK, TAPPR.TEMPLATE_ID, TAPPR.GL_CODE,"
				+ "(SELECT GL_DESCRIPTION   FROM GL_CODES  WHERE GL_STATUS = 0 AND VISION_GL =TAPPR.GL_CODE ) GL_CODE_DESC,"
				+ " TAPPR.GL_MAPPING_GROUP_ID, TAPPR.GL_MAPPING_SEQUENCE,  "
			    +"  TAPPR.GL_MAPPING_ATTRIBUTE_TYPE_AT, TAPPR.GL_MAPPING_ATTRIBUTE_TYPE, TAPPR.GL_MAPPING_ATTRIBUTE,  "
			    +"  TAPPR.CB_GL_CODE, "
			    + "(SELECT GL_DESCRIPTION   FROM CB_GL_CODES  WHERE GL_STATUS = 0 AND GL_CODE =TAPPR.CB_GL_CODE ) CB_GL_CODE_DESC,"
			    + "TAPPR.GL_MAPPING_STATUS_NT, TAPPR.GL_MAPPING_STATUS, TAPPR.RECORD_INDICATOR_NT, TAPPR.RECORD_INDICATOR, TAPPR.MAKER,  "
			    +"  TAPPR.VERIFIER, TAPPR.INTERNAL_STATUS,"
			    + dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
			    +makerApprDesc+" , "+verifierApprDesc+" , "
			    + RecordIndicatorNtApprDesc+" , "+mappingStatusNtApprDesc 
			    + " FROM GL_MAPPINGS TAPPR "
			    +" WHERE TAPPR.COUNTRY =? "
			    +" AND TAPPR.LE_BOOK = ? "
			    +" AND TAPPR.TEMPLATE_ID =? "
			    +  "   AND    TAPPR.GL_CODE                      = ? "
			    +"   AND    TAPPR.GL_MAPPING_GROUP_ID          = ? "
			    +"   AND    TAPPR.GL_MAPPING_SEQUENCE          = ? "
			    +"   AND    TAPPR.GL_MAPPING_ATTRIBUTE_TYPE    = ? "
			    +"   AND    TAPPR.GL_MAPPING_ATTRIBUTE         = ? "
//			    +"   AND    TAPPR.CB_GL_CODE                   = ? "
			    );
			String strQueryPend = new String(" SELECT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.TEMPLATE_ID, TPEND.GL_CODE, TPEND.GL_MAPPING_GROUP_ID, TPEND.GL_MAPPING_SEQUENCE,  "
				    +"  TPEND.GL_MAPPING_ATTRIBUTE_TYPE_AT, TPEND.GL_MAPPING_ATTRIBUTE_TYPE, TPEND.GL_MAPPING_ATTRIBUTE,  "
				    +"  TPEND.CB_GL_CODE,"
				    + "(SELECT GL_DESCRIPTION   FROM GL_CODES  WHERE GL_STATUS = 0 AND VISION_GL =TPEND.GL_CODE ) GL_CODE_DESC,"
				    + "(SELECT GL_DESCRIPTION   FROM CB_GL_CODES  WHERE GL_STATUS = 0 AND GL_CODE =TPEND.CB_GL_CODE ) CB_GL_CODE_DESC,"
				    + " TPEND.GL_MAPPING_STATUS_NT, TPEND.GL_MAPPING_STATUS, TPEND.RECORD_INDICATOR_NT, TPEND.RECORD_INDICATOR, TPEND.MAKER,  "
				    +"  TPEND.VERIFIER, TPEND.INTERNAL_STATUS,"
				    + dbFunctionFormats("TPEND.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
					+ " DATE_LAST_MODIFIED," + " "
					+ dbFunctionFormats("TPEND.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
				    +makerPendDesc+" , "+verifierPendDesc+" , "
				    + RecordIndicatorNtPendDesc+" , "+mappingStatusNtPendDesc 
				    + " FROM GL_MAPPINGS_PEND TPEND"
				    +" WHERE TPEND.COUNTRY =? "
				    +" AND TPEND.LE_BOOK = ? "
				    +" AND TPEND.TEMPLATE_ID =? "
				    +"   AND    TPEND.GL_CODE                      = ? "
				    +"   AND    TPEND.GL_MAPPING_GROUP_ID          = ? "
				    +"   AND    TPEND.GL_MAPPING_SEQUENCE          = ? "
				    +"   AND    TPEND.GL_MAPPING_ATTRIBUTE_TYPE    = ? "
				    +"   AND    TPEND.GL_MAPPING_ATTRIBUTE         = ? "
				    //+"   AND    TPEND.CB_GL_CODE                   = ? "
				    );

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTemplateId());
		objParams[3] = new String(dObj.getGlCode());
		objParams[4] = new String(dObj.getGlMappingGroupId());
		objParams[5] = new String(dObj.getGlMappingSeq());
		objParams[6] = new String(dObj.getGlMappingAttrType());
		objParams[7] = new String(dObj.getGlMappingAttr());
		
		
		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	
	@Override
	protected int doInsertionAppr(GLMappingVb vObject){
		String query = " INSERT INTO GL_MAPPINGS (COUNTRY, LE_BOOK, TEMPLATE_ID, GL_CODE, GL_MAPPING_GROUP_ID, GL_MAPPING_SEQUENCE,  "
				 +"   GL_MAPPING_ATTRIBUTE_TYPE_AT, GL_MAPPING_ATTRIBUTE_TYPE, GL_MAPPING_ATTRIBUTE, CB_GL_CODE, GL_MAPPING_STATUS_NT, "
				 +"   GL_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,  "
				 +"   VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)  "
				 +" VALUES (?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?, "+systemDate+","+systemDate+")";
		Object[] args = {vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getGlCode(),
				vObject.getGlMappingGroupId(), vObject.getGlMappingSeq(),vObject.getGlMappingAttrTypeAt(), vObject.getGlMappingAttrType(), vObject.getGlMappingAttr(),
				vObject.getCbGlCode(),vObject.getGlMappingStatusNt(),vObject.getGlMappingStatus(),vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(GLMappingVb vObject){
		String query = " INSERT INTO GL_MAPPINGS_PEND (COUNTRY, LE_BOOK, TEMPLATE_ID, GL_CODE, GL_MAPPING_GROUP_ID, GL_MAPPING_SEQUENCE,  "
				 +"   GL_MAPPING_ATTRIBUTE_TYPE_AT, GL_MAPPING_ATTRIBUTE_TYPE, GL_MAPPING_ATTRIBUTE, CB_GL_CODE, GL_MAPPING_STATUS_NT, "
				 +"   GL_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,  "
				 +"   VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)  "
				 +" VALUES (?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?, "+systemDate+","+systemDate+")";
					Object[] args = {vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getGlCode(),
							vObject.getGlMappingGroupId(), vObject.getGlMappingSeq(),vObject.getGlMappingAttrTypeAt(), vObject.getGlMappingAttrType(), vObject.getGlMappingAttr(),
							vObject.getCbGlCode(),vObject.getGlMappingStatusNt(),vObject.getGlMappingStatus(),vObject.getRecordIndicatorNt(),
							vObject.getRecordIndicator(), vObject.getMaker(),
							vObject.getVerifier(), vObject.getInternalStatus()};  
		return getJdbcTemplate().update(query,args);	
	}
	@Override
	protected int doInsertionPendWithDc(GLMappingVb vObject){

		String query = " INSERT INTO GL_MAPPINGS_PEND (COUNTRY, LE_BOOK, TEMPLATE_ID, GL_CODE, GL_MAPPING_GROUP_ID, GL_MAPPING_SEQUENCE,  "
				 +"   GL_MAPPING_ATTRIBUTE_TYPE_AT, GL_MAPPING_ATTRIBUTE_TYPE, GL_MAPPING_ATTRIBUTE, CB_GL_CODE, GL_MAPPING_STATUS_NT, "
				 +"   GL_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,  "
				 +"   VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)  "
				 +" VALUES (?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?, "+systemDate+","+dateTimeConvert+")";
					Object[] args = {vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getGlCode(),
							vObject.getGlMappingGroupId(), vObject.getGlMappingSeq(),vObject.getGlMappingAttrTypeAt(), vObject.getGlMappingAttrType(), vObject.getGlMappingAttr(),
							vObject.getCbGlCode(),vObject.getGlMappingStatusNt(),vObject.getGlMappingStatus(),vObject.getRecordIndicatorNt(),
							vObject.getRecordIndicator(), vObject.getMaker(),
							vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation()};  
		return getJdbcTemplate().update(query,args);	
	
	}
	
	
	@Override
	protected int doUpdateAppr(GLMappingVb vObject){
		String query = " UPDATE GL_MAPPINGS "
				 +" SET  CB_GL_CODE                   = ? ,"
				 +"       GL_MAPPING_STATUS            = ? ,"
				 +"       RECORD_INDICATOR             = ? ,"
				 +"       MAKER                        = ? ,"
				 +"       VERIFIER                     = ? ,"
				 +"       DATE_LAST_MODIFIED           = "+systemDate+","
				 +"       DATE_CREATION                = "+dateTimeConvert
				 +"   WHERE  COUNTRY                      = ? "
				 +"   AND    LE_BOOK                      = ? "
				 +"   AND    TEMPLATE_ID                  = ? "
				 +"   AND    GL_CODE                      = ? "
				 +"   AND    GL_MAPPING_GROUP_ID          = ? "
				 +"   AND    GL_MAPPING_SEQUENCE          = ? "
				 +"   AND    GL_MAPPING_ATTRIBUTE_TYPE    = ? "
				 +"   AND    GL_MAPPING_ATTRIBUTE         = ? ";
		Object[] args = {
				vObject.getCbGlCode(),vObject.getGlMappingStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(),vObject.getDateCreation(),vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getGlCode(),
				vObject.getGlMappingGroupId(), vObject.getGlMappingSeq(), vObject.getGlMappingAttrType(), vObject.getGlMappingAttr()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(GLMappingVb vObject){
		String query = " UPDATE GL_MAPPINGS_PEND "
				 +" SET  CB_GL_CODE                   = ? ,"
				 +"       GL_MAPPING_STATUS            = ? ,"
				 +"       RECORD_INDICATOR             = ? ,"
				 +"       MAKER                        = ? ,"
				 +"       VERIFIER                     = ? ,"
				 +"       DATE_LAST_MODIFIED           = "+systemDate+","
				 +"       DATE_CREATION                = "+dateTimeConvert
				 +"   WHERE  COUNTRY                      = ? "
				 +"   AND    LE_BOOK                      = ? "
				 +"   AND    TEMPLATE_ID                  = ? "
				 +"   AND    GL_CODE                      = ? "
				 +"   AND    GL_MAPPING_GROUP_ID          = ? "
				 +"   AND    GL_MAPPING_SEQUENCE          = ? "
				 +"   AND    GL_MAPPING_ATTRIBUTE_TYPE    = ? "
				 +"   AND    GL_MAPPING_ATTRIBUTE         = ? ";
		Object[] args = {
				vObject.getCbGlCode(),vObject.getGlMappingStatus(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(),vObject.getDateCreation(),vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getGlCode(),
				vObject.getGlMappingGroupId(), vObject.getGlMappingSeq(), vObject.getGlMappingAttrType(), vObject.getGlMappingAttr()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(GLMappingVb dObj){
		String query = "DELETE FROM GL_MAPPINGS "
			    +" WHERE COUNTRY =? "
			    +" AND LE_BOOK = ? "
			    +" AND TEMPLATE_ID =? "
			    +"   AND    GL_CODE                      = ? "
			    +"   AND    GL_MAPPING_GROUP_ID          = ? "
			    +"   AND    GL_MAPPING_SEQUENCE          = ? "
			    +"   AND    GL_MAPPING_ATTRIBUTE_TYPE    = ? "
			    +"   AND   GL_MAPPING_ATTRIBUTE         = ? ";
		Object[] args = {dObj.getCountry() ,
				dObj.getLeBook() ,
				dObj.getTemplateId() ,
				dObj.getGlCode() ,
				dObj.getGlMappingGroupId() ,
				dObj.getGlMappingSeq() ,
				dObj.getGlMappingAttrType() ,
				dObj.getGlMappingAttr() ,};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(GLMappingVb dObj){
		String query = "DELETE FROM GL_MAPPINGS_PEND TPEND"
			    +" WHERE TPEND.COUNTRY =? "
			    +" AND TPEND.LE_BOOK = ? "
			    +" AND TPEND.TEMPLATE_ID =? "
			    +"   AND    TPEND.GL_CODE                      = ? "
			    +"   AND    TPEND.GL_MAPPING_GROUP_ID          = ?"
			    +"   AND    TPEND.GL_MAPPING_SEQUENCE          = ? "
			    +"   AND    TPEND.GL_MAPPING_ATTRIBUTE_TYPE    = ? "
			    +"   AND    TPEND.GL_MAPPING_ATTRIBUTE         = ? ";
		Object[] args = {dObj.getCountry() ,
				dObj.getLeBook() ,
				dObj.getTemplateId() ,
				dObj.getGlCode() ,
				dObj.getGlMappingGroupId() ,
				dObj.getGlMappingSeq() ,
				dObj.getGlMappingAttrType() ,
				dObj.getGlMappingAttr() ,};
		return getJdbcTemplate().update(query,args);
	}
	public List<AlphaSubTabVb> getTemplatName() throws DataAccessException {
		try {
			String sql = " SELECT TEMPLATE_ID ID,TEMPLATE_DESCRIPTION DESCRIPTION  FROM RG_TEMPLATE_CONFIG WHERE TYPE_OF_SUBMISSION != 'XML' AND TEMPLATE_STATUS = 0 ORDER BY ID";
			List<AlphaSubTabVb> templatNameLst = getJdbcTemplate().query(sql, getPageLoadValuesMapper());
			return templatNameLst;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AlphaSubTabVb>();
		}
	}

	public RowMapper getPageLoadValuesMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				vObject.setAlphaSubTab(rs.getString("ID"));
				vObject.setDescription(rs.getString("DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	
}
