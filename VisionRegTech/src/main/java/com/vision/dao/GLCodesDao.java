package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.GLCodesVb;

@Component
public class GLCodesDao extends AbstractDao<GLCodesVb> {
	
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				GLCodesVb glCodesVb = new GLCodesVb();
				glCodesVb.setCountry(rs.getString("COUNTRY"));
				glCodesVb.setLeBook(rs.getString("LE_BOOK"));
				glCodesVb.setVisionGl(rs.getString("VISION_GL"));
				glCodesVb.setGlDescription(rs.getString("GL_DESCRIPTION"));
				glCodesVb.setGlTypeNt(rs.getInt("GL_TYPE_NT"));
				glCodesVb.setGlType(rs.getInt("GL_TYPE"));
				glCodesVb.setGlSignReversal(rs.getInt("GL_SIGN_REVERSAL"));
				glCodesVb.setGlStatusNt(rs.getInt("GL_STATUS_NT"));
				glCodesVb.setGlStatus(rs.getInt("GL_STATUS"));
				glCodesVb.setDbStatus(rs.getInt("GL_STATUS"));
				glCodesVb.setStatusDesc(rs.getString("GL_STATUS_DESC"));
				glCodesVb.setGlStatusDate(rs.getString("GL_STATUS_DATE"));
				glCodesVb.setGlAttribute1At(rs.getInt("GL_ATTRIBUTE_1_AT"));
				glCodesVb.setGlAttribute1(rs.getString("GL_ATTRIBUTE_1"));
				glCodesVb.setGlAttribute2At(rs.getInt("GL_ATTRIBUTE_2_AT"));
				glCodesVb.setGlAttribute2(rs.getString("GL_ATTRIBUTE_2"));
				glCodesVb.setGlAttribute3At(rs.getInt("GL_ATTRIBUTE_3_AT"));
				glCodesVb.setGlAttribute3(rs.getString("GL_ATTRIBUTE_3"));
				glCodesVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				glCodesVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				glCodesVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				glCodesVb.setMaker(rs.getLong("MAKER"));
				glCodesVb.setVerifier(rs.getLong("VERIFIER"));
				glCodesVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				glCodesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				glCodesVb.setDateCreation(rs.getString("DATE_CREATION"));
				return glCodesVb;
			}
		};
		return mapper;
	}
	
	public List<GLCodesVb> getQueryPopupResults(GLCodesVb dObj){
		Vector<Object> params = new Vector<Object>();
		
		String statusApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_STATUS", "GL_STATUS_DESC");
		String statusPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_STATUS", "GL_STATUS_DESC");

		String recIndApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		String recIndPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY," +
				"(SELECT LE_BOOK+ '-' +LEB_DESCRIPTIon LE_BOOK FROM LE_BOOK WHERE COUNTRY = TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) AS LE_BOOK, TAppr.VISION_GL, TAppr.GL_DESCRIPTION," +
				"TAppr.GL_TYPE_NT, TAppr.GL_TYPE," +
				"TAppr.GL_SIGN_REVERSAL,TAppr.GL_STATUS_NT," +
				"TAppr.GL_STATUS, "+statusApprDesc+", Format(TAppr.GL_STATUS_DATE, 'dd-MM-yyyy') GL_STATUS_DATE," +
				"TAppr.GL_ATTRIBUTE_1_AT, TAppr.GL_ATTRIBUTE_1, TAppr.GL_ATTRIBUTE_2_AT, TAppr.GL_ATTRIBUTE_2," +
				"TAppr.GL_ATTRIBUTE_3_AT, TAppr.GL_ATTRIBUTE_3," +
				"TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+recIndApprDesc+", TAppr.MAKER, TAppr.VERIFIER," +
				"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_CODES TAppr  ");
			
			String strWhereNotExists = new String( "  Not Exists (Select 'X' From GL_CODES_PEND TPend " +
				 " Where TPend.COUNTRY = TAppr.COUNTRY And TPend.LE_BOOK = TAppr.LE_BOOK And TPend.VISION_GL = TAppr.VISION_GL) ");
			
			StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY," +
				"(SELECT LE_BOOK+ '-' +LEB_DESCRIPTIon LE_BOOK FROM LE_BOOK WHERE COUNTRY = Tpend.COUNTRY and LE_BOOK=TPend.LE_BOOK) AS LE_BOOK, TPend.VISION_GL, TPend.GL_DESCRIPTION," +
				"TPend.GL_TYPE_NT, TPend.GL_TYPE," +
				"TPend.GL_SIGN_REVERSAL, TPend.GL_STATUS_NT," +
				"TPend.GL_STATUS, "+statusPendDesc+", Format(TPend.GL_STATUS_DATE, 'dd/MM/yyyy') GL_STATUS_DATE," +
				"TPend.GL_ATTRIBUTE_1_AT, TPend.GL_ATTRIBUTE_1, TPend.GL_ATTRIBUTE_2_AT, TPend.GL_ATTRIBUTE_2," +
				"TPend.GL_ATTRIBUTE_3_AT, TPend.GL_ATTRIBUTE_3," +
				"TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+recIndPendDesc+", TPend.MAKER, TPend.VERIFIER," +
				"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_CODES_PEND TPend ");
		try
		{
			//check if the column [GL_STATUS] should be included in the query
			if (dObj.getGlStatus() != -1)
			{
				params.addElement(new Integer(dObj.getGlStatus()));
				CommonUtils.addToQuery("TAppr.GL_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_STATUS = ?", strBufPending);
			}
			//check if the column [COUNTRY] should be included in the query
			/*if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement(dObj.getCountry().toUpperCase());
				CommonUtils.addToQuery("TAppr.COUNTRY = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY = ?", strBufPending);
			}*/

			//check if the column [LE_BOOK] should be included in the query
			if (ValidationUtil.isValid(dObj.getLeBook()))
			{
				String CalLeBook = removeDescLeBook(dObj.getLeBook());
				/*String[] TempLeBook = null;
				if(dObj.getLeBook().length()>3){
					TempLeBook=dObj.getLeBook().split("-");
				}*/
				params.addElement("%"+CalLeBook+ "%");
				CommonUtils.addToQuery("TAppr.LE_BOOK LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.LE_BOOK LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				CommonUtils.addToQuery("TAppr.COUNTRY+ '-' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY+ '-' +TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
			}
			//check if the column [VISION_GL] should be included in the query
			if (ValidationUtil.isValid(dObj.getVisionGl()))
			{
				params.addElement("%" + dObj.getVisionGl().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.VISION_GL) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.VISION_GL) LIKE ?", strBufPending);
			}
			//check if the column [GL_DESCRIPTION] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlDescription()))
			{
				params.addElement("%" + dObj.getGlDescription().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.GL_DESCRIPTION) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.GL_DESCRIPTION) LIKE ?", strBufPending);
			}

			//check if the column [GL_TYPE] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlType()) && -1 !=dObj.getGlType())
			{
				params.addElement(new Integer(dObj.getGlType()));
				CommonUtils.addToQuery("TAppr.GL_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_TYPE = ?", strBufPending);
			}
			//check if the column [getGlStatusDate] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlStatusDate()))
			{
				String endDate=dObj.getGlStatusDate();
				if(endDate.trim().indexOf(" ")>-1){
					params.addElement(dObj.getGlStatusDate());
					CommonUtils.addToQuery("TAppr.GL_STATUS_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
					CommonUtils.addToQuery("TPend.GL_STATUS_DATE = CONVERT(datetime, ?, 103) ", strBufPending);
				}else{
					params.addElement(endDate.trim());
					params.addElement(endDate.trim());
					CommonUtils.addToQuery("TAppr.GL_STATUS_DATE BETWEEN CONVERT(datetime, ?, 103) AND CONVERT(datetime, ?, 103) ", strBufApprove);
					CommonUtils.addToQuery("TPend.GL_STATUS_DATE BETWEEN CONVERT(datetime, ?, 103) AND CONVERT(datetime, ?, 103) ", strBufPending);
				}
			}
			//check if the column [GL_ATTRIBUTE_1] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlAttribute1()) && !"-1".equalsIgnoreCase(dObj.getGlAttribute1()))
			{
				params.addElement(dObj.getGlAttribute1());
				CommonUtils.addToQuery("TAppr.GL_ATTRIBUTE_1 = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_ATTRIBUTE_1 = ?", strBufPending);
			}
			//check if the column [GL_ATTRIBUTE_2] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlAttribute2()) &&  !"-1".equalsIgnoreCase(dObj.getGlAttribute2()))
			{
				params.addElement(dObj.getGlAttribute2());
				CommonUtils.addToQuery("TAppr.GL_ATTRIBUTE_2 = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_ATTRIBUTE_2 = ?", strBufPending);
			}
			//check if the column [GL_ATTRIBUTE_3] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlAttribute3()) && !"-1".equalsIgnoreCase(dObj.getGlAttribute3()))
			{
				params.addElement(dObj.getGlAttribute3());
				CommonUtils.addToQuery("TAppr.GL_ATTRIBUTE_3 = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_ATTRIBUTE_3 = ?", strBufPending);
			}
			//check if the column [RECORD_INDICATOR] should be included in the query
			if (dObj.getRecordIndicator() != -1){
				if (dObj.getRecordIndicator() > 3){
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				}else{
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}
			String orderBy=" Order By COUNTRY, LE_BOOK, VISION_GL";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;

		}
	}
	public List<GLCodesVb> getQueryResults(GLCodesVb dObj, int intStatus){

		String CalLeBook = removeDescLeBook(dObj.getLeBook());

		String statusApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_STATUS", "GL_STATUS_DESC");
		String statusPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_STATUS", "GL_STATUS_DESC");

		String recIndApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		String recIndPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		
		List<GLCodesVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String[] TempLeBook = null;
		/*if(dObj.getLeBook().length()>3){
			TempLeBook=dObj.getLeBook().split("-");
		}*/
		String strQueryAppr = new String("Select TAppr.COUNTRY," +
				"(SELECT LE_BOOK+ '-' +LEB_DESCRIPTIon LE_BOOK FROM LE_BOOK WHERE COUNTRY = TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) AS LE_BOOK, TAppr.VISION_GL, TAppr.GL_DESCRIPTION," +
				"TAppr.GL_TYPE_NT, TAppr.GL_TYPE," +
				"TAppr.GL_SIGN_REVERSAL, TAppr.GL_STATUS_NT," +
				"TAppr.GL_STATUS, "+statusApprDesc+", Format(TAppr.GL_STATUS_DATE, 'dd-MM-yyyy') GL_STATUS_DATE," +
				"TAppr.GL_ATTRIBUTE_1_AT, TAppr.GL_ATTRIBUTE_1, TAppr.GL_ATTRIBUTE_2_AT, TAppr.GL_ATTRIBUTE_2," +
				"TAppr.GL_ATTRIBUTE_3_AT, TAppr.GL_ATTRIBUTE_3," +
				"TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, "+recIndApprDesc+", TAppr.MAKER, TAppr.VERIFIER," +
				"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_CODES TAppr " + 
				"Where TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.VISION_GL = ?");

			String strQueryPend = new String("Select TPend.COUNTRY," +
				"(SELECT LE_BOOK+ '-' +LEB_DESCRIPTIon LE_BOOK FROM LE_BOOK WHERE COUNTRY = Tpend.COUNTRY and LE_BOOK=TPend.LE_BOOK) AS LE_BOOK, TPend.VISION_GL, TPend.GL_DESCRIPTION," +
				"TPend.GL_TYPE_NT, TPend.GL_TYPE," +
				"TPend.GL_SIGN_REVERSAL, TPend.GL_STATUS_NT," +
				"TPend.GL_STATUS, "+statusPendDesc+", Format(TPend.GL_STATUS_DATE, 'dd-MM-yyyy') GL_STATUS_DATE," +
				"TPend.GL_ATTRIBUTE_1_AT, TPend.GL_ATTRIBUTE_1, TPend.GL_ATTRIBUTE_2_AT, TPend.GL_ATTRIBUTE_2," +
				"TPend.GL_ATTRIBUTE_3_AT, TPend.GL_ATTRIBUTE_3, " +
				"TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "+recIndPendDesc+", TPend.MAKER, TPend.VERIFIER," +
				"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_CODES_PEND TPend " + 
				"Where TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.VISION_GL = ?");

			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());	//[COUNTRY]
			objParams[1] = new String(CalLeBook);	//[LE_BOOK]
			objParams[2] = new String(dObj.getVisionGl());	//[VISION_GL]

		try{
		  if(!dObj.isVerificationRequired()){intStatus =0;}
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
	protected List<GLCodesVb> selectApprovedRecord(GLCodesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<GLCodesVb> doSelectPendingRecord(GLCodesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(GLCodesVb records){return records.getGlStatus();}
	@Override
	protected void setStatus(GLCodesVb vObject,int status){vObject.setGlStatus(status);}
	
	@Override
	protected int doInsertionAppr(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into GL_CODES ( COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT," +
			"GL_TYPE, GL_SIGN_REVERSAL, GL_STATUS_NT, GL_STATUS," +
			"GL_STATUS_DATE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2," +
			"GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, RECORD_INDICATOR_NT, RECORD_INDICATOR," +
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) " +
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,GetDate(), GetDate()) ";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getVisionGl(), vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
			vObject.getGlSignReversal(),
			vObject.getGlStatusNt(),vObject.getGlStatus(), vObject.getGlStatusDate(), vObject.getGlAttribute1At(),vObject.getGlAttribute1(),vObject.getGlAttribute2At(),vObject.getGlAttribute2(),
			vObject.getGlAttribute3At(),vObject.getGlAttribute3(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into GL_CODES_PEND ( COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT," +
			"GL_TYPE, GL_SIGN_REVERSAL, GL_STATUS_NT, GL_STATUS," +
			"GL_STATUS_DATE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2," +
			"GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, RECORD_INDICATOR_NT, RECORD_INDICATOR," +
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) " +
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(), GetDate()) ";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getVisionGl(), vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
			vObject.getGlSignReversal(),
			vObject.getGlStatusNt(),vObject.getGlStatus(), vObject.getGlStatusDate(), vObject.getGlAttribute1At(),vObject.getGlAttribute1(),vObject.getGlAttribute2At(),vObject.getGlAttribute2(),
			vObject.getGlAttribute3At(),vObject.getGlAttribute3(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPendWithDc(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into GL_CODES_PEND ( COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT," +
			"GL_TYPE, GL_SIGN_REVERSAL, GL_STATUS_NT, GL_STATUS," +
			"GL_STATUS_DATE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2," +
			"GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, RECORD_INDICATOR_NT, RECORD_INDICATOR," +
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) " +
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?,GetDate(), CONVERT(datetime, ?, 103)) ";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getVisionGl(), vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
			vObject.getGlSignReversal(),
			vObject.getGlStatusNt(),vObject.getGlStatus(), vObject.getGlStatusDate(), vObject.getGlAttribute1At(),vObject.getGlAttribute1(),vObject.getGlAttribute2At(),vObject.getGlAttribute2(),
			vObject.getGlAttribute3At(),vObject.getGlAttribute3(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update GL_CODES Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?,"+
			"GL_SIGN_REVERSAL = ?, GL_STATUS_NT = ?, "+
			"GL_STATUS = ?, GL_STATUS_DATE = CONVERT(datetime, ?, 103), GL_ATTRIBUTE_1_AT = ?, "+
			"GL_ATTRIBUTE_1 = ?, GL_ATTRIBUTE_2_AT = ?, GL_ATTRIBUTE_2 = ?, GL_ATTRIBUTE_3_AT = ?, "+
			"GL_ATTRIBUTE_3 = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,"+
			"MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() Where COUNTRY = ? AND LE_BOOK = ? AND VISION_GL = ?";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
			vObject.getGlSignReversal(),
			vObject.getGlStatusNt(),vObject.getGlStatus(), vObject.getGlStatusDate(), vObject.getGlAttribute1At(),vObject.getGlAttribute1(),vObject.getGlAttribute2At(),vObject.getGlAttribute2(),
			vObject.getGlAttribute3At(),vObject.getGlAttribute3(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getCountry(),CalLeBook,vObject.getVisionGl()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update GL_CODES_PEND Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?,"+
			"GL_SIGN_REVERSAL = ?, GL_STATUS_NT = ?, "+
			"GL_STATUS = ?, GL_STATUS_DATE = CONVERT(datetime, ?, 103), GL_ATTRIBUTE_1_AT = ?, "+
			"GL_ATTRIBUTE_1 = ?, GL_ATTRIBUTE_2_AT = ?, GL_ATTRIBUTE_2 = ?, GL_ATTRIBUTE_3_AT = ?, "+
			"GL_ATTRIBUTE_3 = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?,"+
			"MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() Where COUNTRY = ? AND LE_BOOK = ? AND VISION_GL = ?";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
			vObject.getGlSignReversal(),
			vObject.getGlStatusNt(),vObject.getGlStatus(), vObject.getGlStatusDate(), vObject.getGlAttribute1At(),vObject.getGlAttribute1(),vObject.getGlAttribute2At(),vObject.getGlAttribute2(),
			vObject.getGlAttribute3At(),vObject.getGlAttribute3(), vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
			vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getCountry(),CalLeBook,vObject.getVisionGl()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
/*
		String[] TempLeBook = null;
		if(vObject.getLeBook().length()>3){
			TempLeBook=vObject.getLeBook().split("-");
		}*/
		String query = "Delete From GL_CODES Where COUNTRY = ? AND LE_BOOK = ? AND VISION_GL = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getVisionGl()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(GLCodesVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());

		/*String[] TempLeBook = null;
		if(vObject.getLeBook().length()>3){
			TempLeBook=vObject.getLeBook().split("-");
		}*/
		String query = "Delete From GL_CODES_PEND Where COUNTRY = ? AND LE_BOOK = ? AND VISION_GL = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getVisionGl()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(GLCodesVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		String CalLeBook = removeDescLeBook(vObject.getLeBook());

		/*String[] TempLeBook = null;
		if(vObject.getLeBook().length()>3){
			TempLeBook=vObject.getLeBook().split("-");
		}*/
		try{
			strErrMsg =  strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + ",LE_BOOK:" + CalLeBook;
			strErrMsg =  strErrMsg + ",VISION_GL:" + vObject.getVisionGl();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}
	@Override
	protected String getAuditString(GLCodesVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			if(vObject.getCountry() != null && !vObject.getCountry().equalsIgnoreCase(""))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			String CalLeBook = removeDescLeBook(vObject.getLeBook());
			if(CalLeBook != null && !CalLeBook.equalsIgnoreCase(""))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+CalLeBook.trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getVisionGl() != null && !vObject.getVisionGl().equalsIgnoreCase(""))
				strAudit.append("VISION_GL"+auditDelimiterColVal+vObject.getVisionGl().trim());
			else
				strAudit.append("VISION_GL"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getGlDescription() != null && !vObject.getGlDescription().equalsIgnoreCase(""))
				strAudit.append("GL_DESCRIPTION"+auditDelimiterColVal+vObject.getGlDescription().trim());
			else
				strAudit.append("GL_DESCRIPTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("GL_TYPE_NT"+auditDelimiterColVal+vObject.getGlTypeNt());
			strAudit.append(auditDelimiter);
			strAudit.append("GL_TYPE"+auditDelimiterColVal+vObject.getGlType());
			strAudit.append(auditDelimiter);
			strAudit.append("GL_SIGN_REVERSAL"+auditDelimiterColVal+vObject.getGlSignReversal());
			strAudit.append(auditDelimiter);
			
			strAudit.append("GL_STATUS_NT"+auditDelimiterColVal+vObject.getGlStatusNt());
			strAudit.append(auditDelimiter);
			strAudit.append("GL_STATUS"+auditDelimiterColVal+vObject.getGlStatus());
			strAudit.append(auditDelimiter);
			if(vObject.getGlStatusDate() != null && !vObject.getGlStatusDate().equalsIgnoreCase(""))
				strAudit.append("GL_STATUS_DATE"+auditDelimiterColVal+vObject.getGlStatusDate().trim());
			else
				strAudit.append("GL_STATUS_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("GL_ATTRIBUTE_1_AT"+auditDelimiterColVal+vObject.getGlAttribute1At());
			strAudit.append(auditDelimiter);
			if(vObject.getGlAttribute1() != null && !vObject.getGlAttribute1().equalsIgnoreCase(""))
				strAudit.append("GL_ATTRIBUTE_1"+auditDelimiterColVal+vObject.getGlAttribute1().trim());
			else
				strAudit.append("GL_ATTRIBUTE_1"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("GL_ATTRIBUTE_2_AT"+auditDelimiterColVal+vObject.getGlAttribute2At());
			strAudit.append(auditDelimiter);
			if(vObject.getGlAttribute2() != null && !vObject.getGlAttribute2().equalsIgnoreCase(""))
				strAudit.append("GL_ATTRIBUTE_2"+auditDelimiterColVal+vObject.getGlAttribute2().trim());
			else
				strAudit.append("GL_ATTRIBUTE_2"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("GL_ATTRIBUTE_3_AT"+auditDelimiterColVal+vObject.getGlAttribute3At());
			strAudit.append(auditDelimiter);
			if(vObject.getGlAttribute3() != null && !vObject.getGlAttribute3().equalsIgnoreCase(""))
				strAudit.append("GL_ATTRIBUTE_3"+auditDelimiterColVal+vObject.getGlAttribute3().trim());
			else
				strAudit.append("GL_ATTRIBUTE_3"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			if(vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if(vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "GLCodes";
		serviceDesc = CommonUtils.getResourceManger().getString("glCodes");//"GL Codes";
		tableName = "GL_CODES";
		childTableName = "GL_CODES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getBuildStatus(GLCodesVb glCodesVb){
		return getBuildModuleStatus(glCodesVb.getCountry(), glCodesVb.getLeBook());
	}
}