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
import com.vision.vb.CbRefDetailsVb;
import com.vision.vb.CbRefHeaderVb;
import com.vision.vb.SmartSearchVb;

@Component
public class CbRefDetailsDao extends AbstractDao<CbRefDetailsVb> {

/*******Mapper Start**********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String Attribute01AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5001, "TAppr.REF_ATTRIBUTE_01", "REF_ATTRIBUTE_01_DESC");
	String Attribute01AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5001, "TPend.REF_ATTRIBUTE_01", "REF_ATTRIBUTE_01_DESC");

	String Attribute02AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5002, "TAppr.REF_ATTRIBUTE_02", "REF_ATTRIBUTE_02_DESC");
	String Attribute02AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5002, "TPend.REF_ATTRIBUTE_02", "REF_ATTRIBUTE_02_DESC");

	String Attribute03AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5003, "TAppr.REF_ATTRIBUTE_03", "REF_ATTRIBUTE_03_DESC");
	String Attribute03AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5003, "TPend.REF_ATTRIBUTE_03", "REF_ATTRIBUTE_03_DESC");

	String StatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.REF_STATUS", "REF_STATUS_DESC");
	String StatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.REF_STATUS", "REF_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CbRefDetailsVb vObject = new CbRefDetailsVb();
				if(rs.getString("COUNTRY")!= null){ 
					vObject.setCountry(rs.getString("COUNTRY"));
				}else{
					vObject.setCountry("");
				}
				if(rs.getString("LE_BOOK")!= null){ 
					vObject.setLeBook(rs.getString("LE_BOOK"));
				}else{
					vObject.setLeBook("");
				}
				if(rs.getString("REF_CODE")!= null){ 
					vObject.setRefCode(rs.getString("REF_CODE"));
				}else{
					vObject.setRefCode("");
				}
				if(rs.getString("REF_KEY")!= null){ 
					vObject.setRefKey(rs.getString("REF_KEY"));
				}else{
					vObject.setRefKey("");
				}
				if(rs.getString("REF_KEY_DESCRIPTION")!= null){ 
					vObject.setRefKeyDescription(rs.getString("REF_KEY_DESCRIPTION"));
				}else{
					vObject.setRefKeyDescription("");
				}
				if(rs.getString("REF_KEY_TYPE")!= null){ 
					vObject.setRefKeyType(rs.getString("REF_KEY_TYPE"));
				}else{
					vObject.setRefKeyType("");
				}
				if(rs.getString("REF_PARENT_KEY")!= null){ 
					vObject.setRefParentKey(rs.getString("REF_PARENT_KEY"));
				}else{
					vObject.setRefParentKey("");
				}
				if(rs.getString("REF_TRANSLATED_KEY")!= null){ 
					vObject.setRefTranslatedKey(rs.getString("REF_TRANSLATED_KEY"));
				}else{
					vObject.setRefTranslatedKey("");
				}
				vObject.setRefAttribute01At(rs.getInt("REF_ATTRIBUTE_01_AT"));
				if(rs.getString("REF_ATTRIBUTE_01")!= null){ 
					vObject.setRefAttribute01(rs.getString("REF_ATTRIBUTE_01"));
				}else{
					vObject.setRefAttribute01("");
				}
				vObject.setRefAttribute02At(rs.getInt("REF_ATTRIBUTE_02_AT"));
				if(rs.getString("REF_ATTRIBUTE_02")!= null){ 
					vObject.setRefAttribute02(rs.getString("REF_ATTRIBUTE_02"));
				}else{
					vObject.setRefAttribute02("");
				}
				vObject.setRefAttribute03At(rs.getInt("REF_ATTRIBUTE_03_AT"));
				if(rs.getString("REF_ATTRIBUTE_03")!= null){ 
					vObject.setRefAttribute03(rs.getString("REF_ATTRIBUTE_03"));
				}else{
					vObject.setRefAttribute03("");
				}
				vObject.setRefStatusNt(rs.getInt("REF_STATUS_NT"));
				vObject.setRefStatus(rs.getInt("REF_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if(rs.getString("DATE_LAST_MODIFIED")!= null){ 
					vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				}else{
					vObject.setDateLastModified("");
				}
				if(rs.getString("DATE_CREATION")!= null){ 
					vObject.setDateCreation(rs.getString("DATE_CREATION"));
				}else{
					vObject.setDateCreation("");
				}
				
				
				if(rs.getString("REF_ATTRIBUTE_01_DESC")!= null){ 
					vObject.setRefAttribute01Desc(rs.getString("REF_ATTRIBUTE_01_DESC"));
				}
				if(rs.getString("REF_ATTRIBUTE_02_DESC")!= null){ 
					vObject.setRefAttribute02Desc(rs.getString("REF_ATTRIBUTE_02_DESC"));
				}
				if(rs.getString("REF_ATTRIBUTE_03_DESC")!= null){ 
					vObject.setRefAttribute03Desc(rs.getString("REF_ATTRIBUTE_03_DESC"));
				}
				
				vObject.setStatusDesc(rs.getString("REF_STATUS_DESC"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				
				if(ValidationUtil.isValid(rs.getString("MAKER_NAME")))
					vObject.setMakerName(rs.getString("MAKER_NAME"));
				if(ValidationUtil.isValid(rs.getString("VERIFIER_NAME")))
					vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				
				return vObject;
			}
		};
		return mapper;
	}

/*******Mapper End**********/
	public List<CbRefDetailsVb> getQueryPopupResults(CbRefDetailsVb dObj){

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (Select TAppr.COUNTRY"
			+ ",TAppr.LE_BOOK"
			+ ",TAppr.REF_CODE"
			+ ",TAppr.REF_KEY"
			+ ",TAppr.REF_KEY_DESCRIPTION"
			+ ",TAppr.REF_KEY_TYPE"
			+ ",TAppr.REF_PARENT_KEY"
			+ ",TAppr.REF_TRANSLATED_KEY"
			+ ",TAppr.REF_ATTRIBUTE_01_AT"
			+ ",TAppr.REF_ATTRIBUTE_01, "+Attribute01AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_02_AT"
			+ ",TAppr.REF_ATTRIBUTE_02, "+Attribute02AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_03_AT"
			+ ",TAppr.REF_ATTRIBUTE_03, "+Attribute03AtApprDesc
			+ ",TAppr.REF_STATUS_NT"
			+ ",TAppr.REF_STATUS, "+StatusNtApprDesc
			+ ",TAppr.RECORD_INDICATOR_NT"
			+ ",TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ",TAppr.MAKER, "+makerApprDesc
			+ ",TAppr.VERIFIER, "+verifierApprDesc
			+ ",TAppr.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" from CB_REF_DETAILS TAppr  WHERE TAppr.COUNTRY = ?  AND TAppr.LE_BOOK = ? AND TAppr.REF_CODE = ? )TAppr  ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From CB_REF_DETAILS_PEND TPend WHERE TAppr.REF_CODE = TPend.REF_CODE AND TAppr.REF_KEY = TPend.REF_KEY AND TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK )");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (Select TPend.COUNTRY"
			+ ",TPend.LE_BOOK"
			+ ",TPend.REF_CODE"
			+ ",TPend.REF_KEY"
			+ ",TPend.REF_KEY_DESCRIPTION"
			+ ",TPend.REF_KEY_TYPE"
			+ ",TPend.REF_PARENT_KEY"
			+ ",TPend.REF_TRANSLATED_KEY"
			+ ",TPend.REF_ATTRIBUTE_01_AT"
			+ ",TPend.REF_ATTRIBUTE_01, "+Attribute01AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_02_AT"
			+ ",TPend.REF_ATTRIBUTE_02, "+Attribute02AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_03_AT"
			+ ",TPend.REF_ATTRIBUTE_03, "+Attribute03AtPendDesc
			+ ",TPend.REF_STATUS_NT"
			+ ",TPend.REF_STATUS, "+StatusNtPendDesc
			+ ",TPend.RECORD_INDICATOR_NT"
			+ ",TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ",TPend.MAKER, "+makerPendDesc
			+ ",TPend.VERIFIER, "+verifierPendDesc
			+ ",TPend.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" from CB_REF_DETAILS_PEND TPend WHERE TPend.COUNTRY = ?  AND TPend.LE_BOOK = ? AND TPend.REF_CODE = ?)TPend ");
		try
			{
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAppr.COUNTRY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) "+ val, strBufPending, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAppr.LE_BOOK) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) "+ val, strBufPending, data.getJoinType());
						break;


					case "refCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_CODE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_CODE) "+ val, strBufPending, data.getJoinType());
						break;

					case "refKey":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_KEY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_KEY) "+ val, strBufPending, data.getJoinType());
						break;

					case "refKeyDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_KEY_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_KEY_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;

					case "refKeyType":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_KEY_TYPE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_KEY_TYPE) "+ val, strBufPending, data.getJoinType());
						break;

					case "refParentKey":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_PARENT_KEY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_PARENT_KEY) "+ val, strBufPending, data.getJoinType());
						break;

					case "refTranslatedKey":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_TRANSLATED_KEY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_TRANSLATED_KEY) "+ val, strBufPending, data.getJoinType());
						break;

					case "refStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_STATUS) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_STATUS) "+ val, strBufPending, data.getJoinType());
						break;

					case "recordIndicator":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR) "+ val, strBufPending, data.getJoinType());
						break;

						default:
					}
					count++;
				}
			}
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			params.addElement(dObj.getCountry());
			params.addElement(CalLeBook);
			params.addElement(dObj.getRefCode());
			String orderBy=" Order By REF_CODE, REF_KEY, COUNTRY, LE_BOOK ";
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
	public List<CbRefDetailsVb> getQueryResultsAllByParent(CbRefHeaderVb dObj){
		setServiceDefaults();
		List<CbRefDetailsVb> collTemp = null;
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY"
				+ ",TAppr.LE_BOOK"
				+ ",TAppr.REF_CODE"
				+ ",TAppr.REF_KEY"
				+ ",TAppr.REF_KEY_DESCRIPTION"
				+ ",TAppr.REF_KEY_TYPE"
				+ ",TAppr.REF_PARENT_KEY"
				+ ",TAppr.REF_TRANSLATED_KEY"
				+ ",TAppr.REF_ATTRIBUTE_01_AT"
				+ ",TAppr.REF_ATTRIBUTE_01, "+Attribute01AtApprDesc
				+ ",TAppr.REF_ATTRIBUTE_02_AT"
				+ ",TAppr.REF_ATTRIBUTE_02, "+Attribute02AtApprDesc
				+ ",TAppr.REF_ATTRIBUTE_03_AT"
				+ ",TAppr.REF_ATTRIBUTE_03, "+Attribute03AtApprDesc
				+ ",TAppr.REF_STATUS_NT"
				+ ",TAppr.REF_STATUS, "+StatusNtApprDesc
				+ ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
				+ ",TAppr.MAKER, "+makerApprDesc
				+ ",TAppr.VERIFIER, "+verifierApprDesc
				+ ",TAppr.INTERNAL_STATUS"
				+ ", "+dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
				+ ", "+dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
				" from CB_REF_DETAILS TAppr WHERE TAppr.COUNTRY = ?  AND TAppr.LE_BOOK = ? AND TAppr.REF_CODE = ? ");
			String strWhereNotExists = new String( " Not Exists (Select 'X' From CB_REF_DETAILS_PEND TPend WHERE TAppr.REF_CODE = TPend.REF_CODE AND TAppr.REF_KEY = TPend.REF_KEY AND TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK )");
			StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY"
				+ ",TPend.LE_BOOK"
				+ ",TPend.REF_CODE"
				+ ",TPend.REF_KEY"
				+ ",TPend.REF_KEY_DESCRIPTION"
				+ ",TPend.REF_KEY_TYPE"
				+ ",TPend.REF_PARENT_KEY"
				+ ",TPend.REF_TRANSLATED_KEY"
				+ ",TPend.REF_ATTRIBUTE_01_AT"
				+ ",TPend.REF_ATTRIBUTE_01, "+Attribute01AtPendDesc
				+ ",TPend.REF_ATTRIBUTE_02_AT"
				+ ",TPend.REF_ATTRIBUTE_02, "+Attribute02AtPendDesc
				+ ",TPend.REF_ATTRIBUTE_03_AT"
				+ ",TPend.REF_ATTRIBUTE_03, "+Attribute03AtPendDesc
				+ ",TPend.REF_STATUS_NT"
				+ ",TPend.REF_STATUS, "+StatusNtPendDesc
				+ ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
				+ ",TPend.MAKER, "+makerPendDesc
				+ ",TPend.VERIFIER, "+verifierPendDesc
				+ ",TPend.INTERNAL_STATUS"
				+ ", "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
				+ ", "+dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
				" from CB_REF_DETAILS_PEND TPend  WHERE TPend.COUNTRY = ?  AND TPend.LE_BOOK = ? AND TPend.REF_CODE = ? ");
			String orderBy=" Order By COUNTRY, LE_BOOK ,REF_CODE, REF_KEY";
		Object objParams[]=null;
		try{
/*			if(intStatus == 0){
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}*/
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			Vector<Object> params = new Vector<Object>();
			params.addElement(dObj.getCountry());
			params.addElement(CalLeBook);
			params.addElement(dObj.getRefCode());
			int Ctr = 0;
			int Ctr2 = 0;
			String query = "";
			if(dObj.isVerificationRequired()){
				objParams = new Object[params.size()*2];
				for(Ctr=0; Ctr < params.size(); Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr);
				for(Ctr2=0 ; Ctr2 < params.size(); Ctr2++, Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr2);
				
				strBufApprove.append(" AND "+strWhereNotExists);
				strBufPending.append(orderBy);
				
				query = strBufApprove.toString() + " Union " + strBufPending.toString();
				
			}else{
				objParams = new Object[params.size()];
				for(Ctr=0; Ctr < params.size(); Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr);
				strBufApprove.append(orderBy);
				query = strBufApprove.toString();
			}
			collTemp = getJdbcTemplate().query(query,objParams,getMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (objParams != null)
			for(int i=0 ; i< objParams.length; i++)
				logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}


	public List<CbRefDetailsVb> getQueryResults(CbRefDetailsVb dObj, int intStatus){
		setServiceDefaults();

		List<CbRefDetailsVb> collTemp = null;

		final int intKeyFieldsCount = 4;
		String strQueryAppr = new String("Select TAppr.COUNTRY"
			+ ",TAppr.LE_BOOK"
			+ ",TAppr.REF_CODE"
			+ ",TAppr.REF_KEY"
			+ ",TAppr.REF_KEY_DESCRIPTION"
			+ ",TAppr.REF_KEY_TYPE"
			+ ",TAppr.REF_PARENT_KEY"
			+ ",TAppr.REF_TRANSLATED_KEY"
			+ ",TAppr.REF_ATTRIBUTE_01_AT"
			+ ",TAppr.REF_ATTRIBUTE_01, "+Attribute01AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_02_AT"
			+ ",TAppr.REF_ATTRIBUTE_02, "+Attribute02AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_03_AT"
			+ ",TAppr.REF_ATTRIBUTE_03, "+Attribute03AtApprDesc
			+ ",TAppr.REF_STATUS_NT"
			+ ",TAppr.REF_STATUS, "+StatusNtApprDesc
			+ ",TAppr.RECORD_INDICATOR_NT"
			+ ",TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ",TAppr.MAKER, "+makerApprDesc
			+ ",TAppr.VERIFIER, "+verifierApprDesc
			+ ",TAppr.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" From CB_REF_DETAILS TAppr WHERE  COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY"
			+ ",TPend.LE_BOOK"
			+ ",TPend.REF_CODE"
			+ ",TPend.REF_KEY"
			+ ",TPend.REF_KEY_DESCRIPTION"
			+ ",TPend.REF_KEY_TYPE"
			+ ",TPend.REF_PARENT_KEY"
			+ ",TPend.REF_TRANSLATED_KEY"
			+ ",TPend.REF_ATTRIBUTE_01_AT"
			+ ",TPend.REF_ATTRIBUTE_01, "+Attribute01AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_02_AT"
			+ ",TPend.REF_ATTRIBUTE_02, "+Attribute02AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_03_AT"
			+ ",TPend.REF_ATTRIBUTE_03, "+Attribute03AtPendDesc
			+ ",TPend.REF_STATUS_NT"
			+ ",TPend.REF_STATUS, "+StatusNtPendDesc
			+ ",TPend.RECORD_INDICATOR_NT"
			+ ",TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ",TPend.MAKER, "+makerPendDesc
			+ ",TPend.VERIFIER, "+verifierPendDesc
			+ ",TPend.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" From CB_REF_DETAILS_PEND TPend WHERE  COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ?  ");
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = CalLeBook;		
		objParams[2] = dObj.getRefCode();
		objParams[3] = dObj.getRefKey();


		try{
			if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
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
	
	public List<CbRefDetailsVb> getQueryResultsByParent(CbRefHeaderVb dObj, int intStatus){
		setServiceDefaults();

		List<CbRefDetailsVb> collTemp = null;

		String strQueryAppr = new String("Select TAppr.COUNTRY"
			+ ",TAppr.LE_BOOK"
			+ ",TAppr.REF_CODE"
			+ ",TAppr.REF_KEY"
			+ ",TAppr.REF_KEY_DESCRIPTION"
			+ ",TAppr.REF_KEY_TYPE"
			+ ",TAppr.REF_PARENT_KEY"
			+ ",TAppr.REF_TRANSLATED_KEY"
			+ ",TAppr.REF_ATTRIBUTE_01_AT"
			+ ",TAppr.REF_ATTRIBUTE_01, "+Attribute01AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_02_AT"
			+ ",TAppr.REF_ATTRIBUTE_02, "+Attribute02AtApprDesc
			+ ",TAppr.REF_ATTRIBUTE_03_AT"
			+ ",TAppr.REF_ATTRIBUTE_03, "+Attribute03AtApprDesc
			+ ",TAppr.REF_STATUS_NT"
			+ ",TAppr.REF_STATUS, "+StatusNtApprDesc
			+ ",TAppr.RECORD_INDICATOR_NT"
			+ ",TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
			+ ",TAppr.MAKER, "+makerApprDesc
			+ ",TAppr.VERIFIER, "+verifierApprDesc
			+ ",TAppr.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" From CB_REF_DETAILS TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ? ");
		String strQueryPend = new String("Select TPend.COUNTRY"
			+ ",TPend.LE_BOOK"
			+ ",TPend.REF_CODE"
			+ ",TPend.REF_KEY"
			+ ",TPend.REF_KEY_DESCRIPTION"
			+ ",TPend.REF_KEY_TYPE"
			+ ",TPend.REF_PARENT_KEY"
			+ ",TPend.REF_TRANSLATED_KEY"
			+ ",TPend.REF_ATTRIBUTE_01_AT"
			+ ",TPend.REF_ATTRIBUTE_01, "+Attribute01AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_02_AT"
			+ ",TPend.REF_ATTRIBUTE_02, "+Attribute02AtPendDesc
			+ ",TPend.REF_ATTRIBUTE_03_AT"
			+ ",TPend.REF_ATTRIBUTE_03, "+Attribute03AtPendDesc
			+ ",TPend.REF_STATUS_NT"
			+ ",TPend.REF_STATUS, "+StatusNtPendDesc
			+ ",TPend.RECORD_INDICATOR_NT"
			+ ",TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
			+ ",TPend.MAKER, "+makerPendDesc
			+ ",TPend.VERIFIER, "+verifierPendDesc
			+ ",TPend.INTERNAL_STATUS"
			+ ", "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED"
			+ ", "+dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION"+
			" From CB_REF_DETAILS_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ? ");
		final int intKeyFieldsCount = 3;
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = CalLeBook;
		objParams[2] = dObj.getRefCode();

		try{
			if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0){
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
	protected List<CbRefDetailsVb> selectApprovedRecord(CbRefDetailsVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}


	@Override
	protected List<CbRefDetailsVb> doSelectPendingRecord(CbRefDetailsVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}


	@Override
	protected int getStatus(CbRefDetailsVb records){return records.getRefStatus();}


	@Override
	protected void setStatus(CbRefDetailsVb vObject,int status){vObject.setRefStatus(status);}


	@Override
	protected int doInsertionAppr(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query =	"Insert Into CB_REF_DETAILS (COUNTRY, LE_BOOK, REF_CODE, REF_KEY, REF_KEY_DESCRIPTION, REF_KEY_TYPE, "
				+ "REF_PARENT_KEY, REF_TRANSLATED_KEY, REF_ATTRIBUTE_01_AT, REF_ATTRIBUTE_01, REF_ATTRIBUTE_02_AT, REF_ATTRIBUTE_02, "
				+ "REF_ATTRIBUTE_03_AT, REF_ATTRIBUTE_03, REF_STATUS_NT, REF_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, "
				+ "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+ 
		 "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+")"; 
		Object[] args = {vObject.getCountry(), CalLeBook,  vObject.getRefCode(), vObject.getRefKey(), 
				vObject.getRefKeyDescription(), vObject.getRefKeyType(), vObject.getRefParentKey(), vObject.getRefTranslatedKey(),
				vObject.getRefAttribute01At(), vObject.getRefAttribute01(), vObject.getRefAttribute02At(), vObject.getRefAttribute02(), 
				vObject.getRefAttribute03At(), vObject.getRefAttribute03(), vObject.getRefStatusNt(), vObject.getRefStatus(), 
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		 return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doInsertionPend(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query =	"Insert Into CB_REF_DETAILS_PEND (COUNTRY, LE_BOOK, REF_CODE, REF_KEY, REF_KEY_DESCRIPTION, REF_KEY_TYPE, "
				+ "REF_PARENT_KEY, REF_TRANSLATED_KEY, REF_ATTRIBUTE_01_AT, REF_ATTRIBUTE_01, REF_ATTRIBUTE_02_AT, REF_ATTRIBUTE_02, "
				+ "REF_ATTRIBUTE_03_AT, REF_ATTRIBUTE_03, REF_STATUS_NT, REF_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, "
				+ "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+ 
		 "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+")"; 
		Object[] args = {vObject.getCountry(), CalLeBook,  vObject.getRefCode(), vObject.getRefKey(), 
				vObject.getRefKeyDescription(), vObject.getRefKeyType(), vObject.getRefParentKey(), vObject.getRefTranslatedKey(),
				vObject.getRefAttribute01At(), vObject.getRefAttribute01(), vObject.getRefAttribute02At(), vObject.getRefAttribute02(), 
				vObject.getRefAttribute03At(), vObject.getRefAttribute03(), vObject.getRefStatusNt(), vObject.getRefStatus(), 
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		 return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doInsertionPendWithDc(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query =	"Insert Into CB_REF_DETAILS_PEND (COUNTRY, LE_BOOK, REF_CODE, REF_KEY, REF_KEY_DESCRIPTION, "
				+ "REF_KEY_TYPE, REF_PARENT_KEY, REF_TRANSLATED_KEY, REF_ATTRIBUTE_01_AT, REF_ATTRIBUTE_01, REF_ATTRIBUTE_02_AT, REF_ATTRIBUTE_02, "
				+ "REF_ATTRIBUTE_03_AT, REF_ATTRIBUTE_03, REF_STATUS_NT, REF_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+ 
		 "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+dateTimeConvert+")"; 
		Object[] args = {vObject.getCountry(), CalLeBook,  vObject.getRefCode(), vObject.getRefKey(), 
				vObject.getRefKeyDescription(), vObject.getRefKeyType(), vObject.getRefParentKey(), vObject.getRefTranslatedKey(), 
				vObject.getRefAttribute01At(), vObject.getRefAttribute01(), vObject.getRefAttribute02At(), vObject.getRefAttribute02(), 
				vObject.getRefAttribute03At(), vObject.getRefAttribute03(), vObject.getRefStatusNt(), vObject.getRefStatus(), vObject.getRecordIndicatorNt(), 
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation()};

		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doUpdateAppr(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
	String query = "Update CB_REF_DETAILS Set REF_KEY_DESCRIPTION = ?, REF_KEY_TYPE = ?, REF_PARENT_KEY = ?, REF_TRANSLATED_KEY = ?, "
			+ "REF_ATTRIBUTE_01_AT = ?, REF_ATTRIBUTE_01 = ?, REF_ATTRIBUTE_02_AT = ?, REF_ATTRIBUTE_02 = ?, REF_ATTRIBUTE_03_AT = ?, "
			+ "REF_ATTRIBUTE_03 = ?, REF_STATUS_NT = ?, REF_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, "
			+ "INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+" "
					+ "WHERE COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ? ";
		Object[] args = {vObject.getRefKeyDescription() , vObject.getRefKeyType() , vObject.getRefParentKey() 
				, vObject.getRefTranslatedKey() , vObject.getRefAttribute01At() , vObject.getRefAttribute01() , vObject.getRefAttribute02At() 
				, vObject.getRefAttribute02() , vObject.getRefAttribute03At() , vObject.getRefAttribute03() , vObject.getRefStatusNt() 
				, vObject.getRefStatus() , vObject.getRecordIndicatorNt() , vObject.getRecordIndicator() , vObject.getMaker() , vObject.getVerifier() 
				, vObject.getInternalStatus() ,vObject.getCountry() , CalLeBook ,  vObject.getRefCode() , vObject.getRefKey() };

		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int doUpdatePend(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update CB_REF_DETAILS_PEND Set REF_KEY_DESCRIPTION = ?, REF_KEY_TYPE = ?, REF_PARENT_KEY = ?, REF_TRANSLATED_KEY = ?, "
				+ "REF_ATTRIBUTE_01_AT = ?, REF_ATTRIBUTE_01 = ?, REF_ATTRIBUTE_02_AT = ?, REF_ATTRIBUTE_02 = ?, REF_ATTRIBUTE_03_AT = ?, "
				+ "REF_ATTRIBUTE_03 = ?, REF_STATUS_NT = ?, REF_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, "
				+ "INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+" "
						+ "WHERE COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ? ";
			Object[] args = {vObject.getRefKeyDescription() , vObject.getRefKeyType() , vObject.getRefParentKey() 
					, vObject.getRefTranslatedKey() , vObject.getRefAttribute01At() , vObject.getRefAttribute01() , vObject.getRefAttribute02At() 
					, vObject.getRefAttribute02() , vObject.getRefAttribute03At() , vObject.getRefAttribute03() , vObject.getRefStatusNt() 
					, vObject.getRefStatus() , vObject.getRecordIndicatorNt() , vObject.getRecordIndicator() , vObject.getMaker() , vObject.getVerifier() 
					, vObject.getInternalStatus() ,vObject.getCountry() , CalLeBook ,  vObject.getRefCode() , vObject.getRefKey() };

			return getJdbcTemplate().update(query,args);
		}


	@Override
	protected int doDeleteAppr(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_REF_DETAILS Where  COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ? ";
		Object[] args = {vObject.getCountry() ,CalLeBook ,  vObject.getRefCode() , vObject.getRefKey() };
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected int deletePendingRecord(CbRefDetailsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_REF_DETAILS_PEND Where  COUNTRY = ?  AND LE_BOOK = ? AND REF_CODE = ?  AND REF_KEY = ? ";
		Object[] args = {vObject.getCountry() , CalLeBook ,  vObject.getRefCode() , vObject.getRefKey() };
		return getJdbcTemplate().update(query,args);
	}


	@Override
	protected String getAuditString(CbRefDetailsVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);


			if(ValidationUtil.isValid(vObject.getRefCode()))
				strAudit.append("REF_CODE"+auditDelimiterColVal+vObject.getRefCode().trim());
			else
				strAudit.append("REF_CODE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefKey()))
				strAudit.append("REF_KEY"+auditDelimiterColVal+vObject.getRefKey().trim());
			else
				strAudit.append("REF_KEY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefKeyDescription()))
				strAudit.append("REF_KEY_DESCRIPTION"+auditDelimiterColVal+vObject.getRefKeyDescription().trim());
			else
				strAudit.append("REF_KEY_DESCRIPTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefKeyType()))
				strAudit.append("REF_KEY_TYPE"+auditDelimiterColVal+vObject.getRefKeyType().trim());
			else
				strAudit.append("REF_KEY_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefParentKey()))
				strAudit.append("REF_PARENT_KEY"+auditDelimiterColVal+vObject.getRefParentKey().trim());
			else
				strAudit.append("REF_PARENT_KEY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefTranslatedKey()))
				strAudit.append("REF_TRANSLATED_KEY"+auditDelimiterColVal+vObject.getRefTranslatedKey().trim());
			else
				strAudit.append("REF_TRANSLATED_KEY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

				strAudit.append("REF_ATTRIBUTE_01_AT"+auditDelimiterColVal+vObject.getRefAttribute01At());
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefAttribute01()))
				strAudit.append("REF_ATTRIBUTE_01"+auditDelimiterColVal+vObject.getRefAttribute01().trim());
			else
				strAudit.append("REF_ATTRIBUTE_01"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

				strAudit.append("REF_ATTRIBUTE_02_AT"+auditDelimiterColVal+vObject.getRefAttribute02At());
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefAttribute02()))
				strAudit.append("REF_ATTRIBUTE_02"+auditDelimiterColVal+vObject.getRefAttribute02().trim());
			else
				strAudit.append("REF_ATTRIBUTE_02"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

				strAudit.append("REF_ATTRIBUTE_03_AT"+auditDelimiterColVal+vObject.getRefAttribute03At());
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getRefAttribute03()))
				strAudit.append("REF_ATTRIBUTE_03"+auditDelimiterColVal+vObject.getRefAttribute03().trim());
			else
				strAudit.append("REF_ATTRIBUTE_03"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

				strAudit.append("REF_STATUS_NT"+auditDelimiterColVal+vObject.getRefStatusNt());
			strAudit.append(auditDelimiter);

				strAudit.append("REF_STATUS"+auditDelimiterColVal+vObject.getRefStatus());
			strAudit.append(auditDelimiter);

				strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);

				strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);

				strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);

				strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);

				strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getDateLastModified()))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getDateCreation()))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

		return strAudit.toString();
		}

	@Override
	protected void setServiceDefaults(){
		serviceName = "CbRefDetails";
		serviceDesc = "Cbrefdetails";
		tableName = "CB_REF_DETAILS";
		childTableName = "CB_REF_DETAILS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
		
	}


}
