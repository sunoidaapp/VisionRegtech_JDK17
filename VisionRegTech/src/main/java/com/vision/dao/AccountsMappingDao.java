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
import com.vision.vb.AccountsMappingVb;
@Component
public class AccountsMappingDao extends AbstractDao<AccountsMappingVb>{
	

	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountsMappingVb accountsMappingVb = new AccountsMappingVb();
				accountsMappingVb.setCountry(rs.getString("COUNTRY"));
				accountsMappingVb.setLeBook(rs.getString("LE_BOOK"));
				accountsMappingVb.setAccountNo(rs.getString("ACCOUNT_NO"));
				accountsMappingVb.setProductDr(rs.getString("PRODUCT_DR"));
				accountsMappingVb.setProductDrDesc(rs.getString("PRODUCT_DR_DESC"));
				accountsMappingVb.setProductCr(rs.getString("PRODUCT_CR"));
				accountsMappingVb.setProductCrDesc(rs.getString("PRODUCT_CR_DESC"));
				accountsMappingVb.setFrlLineBsDr(rs.getString("FRL_LINE_BS_DR"));
				accountsMappingVb.setFrlLineBsDrDesc(rs.getString("FRL_LINE_BS_DR_DESC"));
				accountsMappingVb.setFrlLineBsCr(rs.getString("FRL_LINE_BS_CR"));
				accountsMappingVb.setFrlLineBsCrDesc(rs.getString("FRL_LINE_BS_CR_DESC"));
				accountsMappingVb.setFrlLinePlDr(rs.getString("FRL_LINE_PL_DR"));
				accountsMappingVb.setFrlLinePlDrDesc(rs.getString("FRL_LINE_PL_DR_DESC"));
				accountsMappingVb.setFrlLinePlCr(rs.getString("FRL_LINE_PL_CR"));
				accountsMappingVb.setFrlLinePlCrDesc(rs.getString("FRL_LINE_PL_CR_DESC"));
				accountsMappingVb.setMrlLineDr(rs.getString("MRL_LINE_DR"));
				accountsMappingVb.setMrlLineDrDesc(rs.getString("MRL_LINE_DR_DESC"));
				accountsMappingVb.setMrlLineCr(rs.getString("MRL_LINE_CR"));
				accountsMappingVb.setMrlLineCrDesc(rs.getString("MRL_LINE_CR_DESC"));
				accountsMappingVb.setAccountsMappingStatusNt(rs.getInt("ACCOUNTS_MAPPING_STATUS_NT"));
				accountsMappingVb.setAccountsMappingStatus(rs.getInt("ACCOUNTS_MAPPING_STATUS"));
				accountsMappingVb.setDbStatus(rs.getInt("ACCOUNTS_MAPPING_STATUS"));
				accountsMappingVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				accountsMappingVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				accountsMappingVb.setMaker(rs.getLong("MAKER"));
				accountsMappingVb.setVerifier(rs.getLong("VERIFIER"));
				accountsMappingVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				accountsMappingVb.setDateCreation(rs.getString("DATE_CREATION"));
				accountsMappingVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return accountsMappingVb;
			}
		};
		return mapper;
	}
	/**
	 * Based on SD request on 20-Nov-2012  Account_Name will taken from ACCOUNTS_MAPPING_EXPANDED.
	 */
	public List<AccountsMappingVb> getQueryPopupResults(AccountsMappingVb dObj){
		
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY," +
			"TAppr.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK, TAppr.ACCOUNT_NO, " +
			"TAppr.PRODUCT_DR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TAppr.PRODUCT_DR) AS PRODUCT_DR_DESC ," +
			"TAppr.PRODUCT_CR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TAppr.PRODUCT_CR) AS PRODUCT_CR_DESC, " +
			"TAppr.FRL_LINE_BS_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_BS_DR) as FRL_LINE_BS_DR_DESC," +
			"TAppr.FRL_LINE_BS_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_BS_CR) as FRL_LINE_BS_CR_DESC," +
			"TAppr.FRL_LINE_PL_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_PL_DR) as FRL_LINE_PL_DR_DESC," +
			"TAppr.FRL_LINE_PL_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_PL_CR) as FRL_LINE_PL_CR_DESC," +
			"TAppr.MRL_LINE_DR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TAppr.MRL_LINE_DR) as MRL_LINE_DR_DESC, " +
			"TAppr.MRL_LINE_CR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TAppr.MRL_LINE_CR) as MRL_LINE_CR_DESC, " +
			"TAppr.ACCOUNTS_MAPPING_STATUS_NT," +
			"TAppr.ACCOUNTS_MAPPING_STATUS, TAppr.RECORD_INDICATOR_NT," +
			"TAppr.RECORD_INDICATOR, TAppr.MAKER, TAppr.VERIFIER," +
			"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
			" From ACCOUNTS_MAPPING TAppr ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From ACCOUNTS_MAPPING_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY And TPend.LE_BOOK = TAppr.LE_BOOK And TPend.ACCOUNT_NO = TAppr.ACCOUNT_NO)");
		StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY," +
			"TPend.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK , TPend.ACCOUNT_NO, " +
			"TPend.PRODUCT_DR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TPend.PRODUCT_DR) AS PRODUCT_DR_DESC ," +
			"TPend.PRODUCT_CR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TPend.PRODUCT_CR) AS PRODUCT_CR_DESC, " +
			"TPend.FRL_LINE_BS_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_BS_DR) as FRL_LINE_BS_DR_DESC," +
			"TPend.FRL_LINE_BS_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_BS_CR) as FRL_LINE_BS_CR_DESC," +
			"TPend.FRL_LINE_PL_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_PL_DR) as FRL_LINE_PL_DR_DESC," +
			"TPend.FRL_LINE_PL_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_PL_CR) as FRL_LINE_PL_CR_DESC," +
			"TPend.MRL_LINE_DR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TPend.MRL_LINE_DR) as MRL_LINE_DR_DESC, " +
			"TPend.MRL_LINE_CR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TPend.MRL_LINE_CR) as MRL_LINE_CR_DESC, " +
			"TPend.ACCOUNTS_MAPPING_STATUS_NT," +
			"TPend.ACCOUNTS_MAPPING_STATUS, TPend.RECORD_INDICATOR_NT," +
			"TPend.RECORD_INDICATOR, TPend.MAKER, TPend.VERIFIER," +
			"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
			" From ACCOUNTS_MAPPING_PEND TPend   ");
		try
		{
			//check if the column [ACCOUNTS_MAPPING_STATUS] should be included in the query
			if (dObj.getAccountsMappingStatus() != -1)
			{
				params.addElement(new Integer(dObj.getAccountsMappingStatus()));
				CommonUtils.addToQuery("TAppr.ACCOUNTS_MAPPING_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.ACCOUNTS_MAPPING_STATUS = ?", strBufPending);
			} 
			//check if the column [COUNTRY] should be included in the query
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement("%" + dObj.getCountry() + "%");
				CommonUtils.addToQuery("TAppr.COUNTRY LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				CommonUtils.addToQuery("TAppr.COUNTRY+ '-' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY+ '-' +TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
			}
			//check if the column [LE_BOOK] should be included in the query
			if (ValidationUtil.isValid(dObj.getLeBook()))
			{
				params.addElement("%" + CalLeBook + "%");
				CommonUtils.addToQuery("TAppr.LE_BOOK LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.LE_BOOK LIKE ?", strBufPending);
			}

			//check if the column [ACCOUNT_NO] should be included in the query
			if (ValidationUtil.isValid(dObj.getAccountNo()))
			{
				params.addElement("%" + dObj.getAccountNo() + "%");
				CommonUtils.addToQuery("TAppr.ACCOUNT_NO LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.ACCOUNT_NO LIKE ?", strBufPending);
			}

			//check if the column [PRODUCT_DR] should be included in the query
			if (ValidationUtil.isValid(dObj.getProductDr()))
			{
				params.addElement("%" + dObj.getProductDr() + "%");
				CommonUtils.addToQuery("TAppr.PRODUCT_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PRODUCT_DR LIKE ?", strBufPending);
			}

			//check if the column [PRODUCT_CR] should be included in the query
			if (ValidationUtil.isValid(dObj.getProductCr()))
			{
				params.addElement("%" + dObj.getProductCr() + "%");
				CommonUtils.addToQuery("TAppr.PRODUCT_CR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PRODUCT_CR LIKE ?", strBufPending);
			}

			//check if the column [FRL_LINE_BS_DR] should be included in the query
			if (ValidationUtil.isValid(dObj.getFrlLineBsDr()))
			{
				params.addElement("%" + dObj.getFrlLineBsDr() + "%");
				CommonUtils.addToQuery("TAppr.FRL_LINE_BS_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FRL_LINE_BS_DR LIKE ?", strBufPending);
			}

			//check if the column [FRL_LINE_BS_CR] should be included in the query
			if (ValidationUtil.isValid(dObj.getFrlLineBsCr()))
			{
				params.addElement("%" + dObj.getFrlLineBsCr() + "%");
				CommonUtils.addToQuery("TAppr.FRL_LINE_BS_CR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FRL_LINE_BS_CR LIKE ?", strBufPending);
			}

			//check if the column [FRL_LINE_PL_DR] should be included in the query
			if (ValidationUtil.isValid(dObj.getFrlLinePlDr()))
			{
				params.addElement("%" + dObj.getFrlLinePlDr() + "%");
				CommonUtils.addToQuery("TAppr.FRL_LINE_PL_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FRL_LINE_PL_DR LIKE ?", strBufPending);
			}

			//check if the column [FRL_LINE_PL_CR] should be included in the query
			if (ValidationUtil.isValid(dObj.getFrlLinePlCr()))
			{
				params.addElement("%" + dObj.getFrlLinePlCr() + "%");
				CommonUtils.addToQuery("TAppr.FRL_LINE_PL_CR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FRL_LINE_PL_CR LIKE ?", strBufPending);
			}

			//check if the column [MRL_LINE_DR] should be included in the query
			if (ValidationUtil.isValid(dObj.getMrlLineDr()))
			{
				params.addElement("%" + dObj.getMrlLineDr() + "%");
				CommonUtils.addToQuery("TAppr.MRL_LINE_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MRL_LINE_DR LIKE ?", strBufPending);
			}

			//check if the column [MRL_LINE_CR] should be included in the query
			if (ValidationUtil.isValid(dObj.getMrlLineCr()))
			{
				params.addElement("%" + dObj.getMrlLineCr() + "%");
				CommonUtils.addToQuery("TAppr.MRL_LINE_CR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MRL_LINE_CR LIKE ?", strBufPending);
			}

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
			String orderBy=" Order By COUNTRY, LE_BOOK, ACCOUNT_NO";
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
	/**
	 * Based on SD request on 20-Nov-2012  Account_Name will taken from ACCOUNTS_MAPPING_EXPANDED.
	 * Changed PRODUCT_DESCRIPTION Short to Lon on the same above Date.
	 */
	public List<AccountsMappingVb> getQueryResults(AccountsMappingVb dObj, int intStatus){
		
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		
		setServiceDefaults();
		List<AccountsMappingVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryAppr = new String("Select TAppr.COUNTRY," +
			"TAppr.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK, TAppr.ACCOUNT_NO,  " +
			"TAppr.PRODUCT_DR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TAppr.PRODUCT_DR) AS PRODUCT_DR_DESC ," +
			"TAppr.PRODUCT_CR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TAppr.PRODUCT_CR) AS PRODUCT_CR_DESC , " +
			"TAppr.FRL_LINE_BS_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_BS_DR) as FRL_LINE_BS_DR_DESC," +
			"TAppr.FRL_LINE_BS_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_BS_CR) as FRL_LINE_BS_CR_DESC," +
			"TAppr.FRL_LINE_PL_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_PL_DR) as FRL_LINE_PL_DR_DESC," +
			"TAppr.FRL_LINE_PL_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TAppr.FRL_LINE_PL_CR) as FRL_LINE_PL_CR_DESC," +
			"TAppr.MRL_LINE_DR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TAppr.MRL_LINE_DR) as MRL_LINE_DR_DESC, " +
			"TAppr.MRL_LINE_CR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TAppr.MRL_LINE_CR) as MRL_LINE_CR_DESC, " +
			"TAppr.ACCOUNTS_MAPPING_STATUS_NT," +
			"TAppr.ACCOUNTS_MAPPING_STATUS, TAppr.RECORD_INDICATOR_NT," +
			"TAppr.RECORD_INDICATOR, TAppr.MAKER, TAppr.VERIFIER," +
			"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
			" From ACCOUNTS_MAPPING TAppr  " + 
			"Where TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.ACCOUNT_NO = ?");

		String strQueryPend = new String("Select TPend.COUNTRY," +
			"TPend.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK , TPend.ACCOUNT_NO,  " +
			"TPend.PRODUCT_DR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TPend.PRODUCT_DR) AS PRODUCT_DR_DESC ," +
			"TPend.PRODUCT_CR, (Select PRODUCT_DESCRIPTION From Product_Codes Where PRODUCT = TPend.PRODUCT_CR) AS PRODUCT_CR_DESC , " +
			"TPend.FRL_LINE_BS_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_BS_DR) as FRL_LINE_BS_DR_DESC," +
			"TPend.FRL_LINE_BS_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_BS_CR) as FRL_LINE_BS_CR_DESC," +
			"TPend.FRL_LINE_PL_DR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_PL_DR) as FRL_LINE_PL_DR_DESC," +
			"TPend.FRL_LINE_PL_CR, (Select FRL_DESCRIPTION  From FRL_Lines Where FRL_LINE = TPend.FRL_LINE_PL_CR) as FRL_LINE_PL_CR_DESC," +
			"TPend.MRL_LINE_DR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TPend.MRL_LINE_DR) as MRL_LINE_DR_DESC, " +
			"TPend.MRL_LINE_CR, (Select MRL_DESCRIPTION From MRL_Lines Where MRL_LINE = TPend.MRL_LINE_CR) as MRL_LINE_CR_DESC, " +
			"TPend.ACCOUNTS_MAPPING_STATUS_NT," +
			"TPend.ACCOUNTS_MAPPING_STATUS, TPend.RECORD_INDICATOR_NT," +
			"TPend.RECORD_INDICATOR, TPend.MAKER, TPend.VERIFIER," +
			"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
			" From ACCOUNTS_MAPPING_PEND TPend " +
			"Where TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.ACCOUNT_NO = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());	//[COUNTRY]
		objParams[1] = new String(CalLeBook);	//[LE_BOOK]
		objParams[2] = new String(dObj.getAccountNo());	//[ACCOUNT_NO]


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
	protected List<AccountsMappingVb> selectApprovedRecord(AccountsMappingVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<AccountsMappingVb> doSelectPendingRecord(AccountsMappingVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(AccountsMappingVb records){return records.getAccountsMappingStatus();}
	@Override
	protected void setStatus(AccountsMappingVb vObject,int status){vObject.setAccountsMappingStatus(status);}	
	@Override
	protected int doInsertionAppr(AccountsMappingVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into ACCOUNTS_MAPPING ( COUNTRY, LE_BOOK, ACCOUNT_NO, PRODUCT_DR, PRODUCT_CR, " +
		"FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, MRL_LINE_CR, " +
		"ACCOUNTS_MAPPING_STATUS_NT, ACCOUNTS_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, " +
		"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)" +
		"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(), GetDate())";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getAccountNo(), vObject.getProductDr(),
				vObject.getProductCr(),vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(),vObject.getFrlLinePlDr(),
				vObject.getFrlLinePlCr(),vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getAccountsMappingStatusNt(),vObject.getAccountsMappingStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(AccountsMappingVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into ACCOUNTS_MAPPING_PEND ( COUNTRY, LE_BOOK, ACCOUNT_NO, PRODUCT_DR, PRODUCT_CR, " +
		"FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, MRL_LINE_CR, " +
		"ACCOUNTS_MAPPING_STATUS_NT, ACCOUNTS_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, " +
		"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)" +
		"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(), GetDate())";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getAccountNo(), vObject.getProductDr(),
				vObject.getProductCr(),vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(),vObject.getFrlLinePlDr(),
				vObject.getFrlLinePlCr(),vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getAccountsMappingStatusNt(),vObject.getAccountsMappingStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPendWithDc(AccountsMappingVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into ACCOUNTS_MAPPING_PEND ( COUNTRY, LE_BOOK, ACCOUNT_NO, PRODUCT_DR, PRODUCT_CR, " +
		"FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, MRL_LINE_CR, " +
		"ACCOUNTS_MAPPING_STATUS_NT, ACCOUNTS_MAPPING_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, " +
		"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)" +
		"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(), CONVERT(datetime, ?, 103))";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getAccountNo(), vObject.getProductDr(),
				vObject.getProductCr(),vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(),vObject.getFrlLinePlDr(),
				vObject.getFrlLinePlCr(),vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getAccountsMappingStatusNt(),vObject.getAccountsMappingStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(AccountsMappingVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Update ACCOUNTS_MAPPING Set PRODUCT_DR = ?, PRODUCT_CR = ?, FRL_LINE_BS_DR = ?, " +
		"FRL_LINE_BS_CR = ?, FRL_LINE_PL_DR = ?, FRL_LINE_PL_CR = ?, MRL_LINE_DR = ?, MRL_LINE_CR = ?, ACCOUNTS_MAPPING_STATUS_NT = ?, ACCOUNTS_MAPPING_STATUS = ?, RECORD_INDICATOR_NT = ?, " +
		"RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() " +
		"Where COUNTRY = ? AND LE_BOOK = ? AND ACCOUNT_NO = ?";
		Object[] args = {vObject.getProductDr(),vObject.getProductCr(),vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(),
			vObject.getFrlLinePlDr(),
			vObject.getFrlLinePlCr(),vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getAccountsMappingStatusNt(),vObject.getAccountsMappingStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),CalLeBook,vObject.getAccountNo()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(AccountsMappingVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Update ACCOUNTS_MAPPING_PEND Set PRODUCT_DR = ?, PRODUCT_CR = ?, FRL_LINE_BS_DR = ?, " +
		"FRL_LINE_BS_CR = ?, FRL_LINE_PL_DR = ?, FRL_LINE_PL_CR = ?, MRL_LINE_DR = ?, MRL_LINE_CR = ?, ACCOUNTS_MAPPING_STATUS_NT = ?, ACCOUNTS_MAPPING_STATUS = ?, RECORD_INDICATOR_NT = ?, " +
		"RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() " +
		"Where COUNTRY = ? AND LE_BOOK = ? AND ACCOUNT_NO = ?";
		Object[] args = {vObject.getProductDr(),vObject.getProductCr(),vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(),
			vObject.getFrlLinePlDr(),
			vObject.getFrlLinePlCr(),vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getAccountsMappingStatusNt(),vObject.getAccountsMappingStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),
			vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),CalLeBook,vObject.getAccountNo()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(AccountsMappingVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From ACCOUNTS_MAPPING Where COUNTRY = ? AND LE_BOOK = ? AND ACCOUNT_NO = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getAccountNo()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(AccountsMappingVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From ACCOUNTS_MAPPING_PEND Where COUNTRY = ? AND LE_BOOK = ? AND ACCOUNT_NO = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getAccountNo()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(AccountsMappingVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		try{
			strErrMsg =  strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + ",LE_BOOK:" + CalLeBook;
			strErrMsg =  strErrMsg + ",ACCOUNT_NO:" + vObject.getAccountNo();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation.  Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation.  Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}
	@Override
	protected String getAuditString(AccountsMappingVb vObject){
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

			if(vObject.getAccountNo() != null && !vObject.getAccountNo().equalsIgnoreCase(""))
				strAudit.append("ACCOUNT_NO"+auditDelimiterColVal+vObject.getAccountNo().trim());
			else
				strAudit.append("ACCOUNT_NO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getProductDr() != null && !vObject.getProductDr().equalsIgnoreCase(""))
				strAudit.append("PRODUCT_DR"+auditDelimiterColVal+vObject.getProductDr().trim());
			else
				strAudit.append("PRODUCT_DR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getProductCr() != null && !vObject.getProductCr().equalsIgnoreCase(""))
				strAudit.append("PRODUCT_CR"+auditDelimiterColVal+vObject.getProductCr().trim());
			else
				strAudit.append("PRODUCT_CR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getFrlLineBsDr() != null && !vObject.getFrlLineBsDr().equalsIgnoreCase(""))
				strAudit.append("FRL_LINE_BS_DR"+auditDelimiterColVal+vObject.getFrlLineBsDr().trim());
			else
				strAudit.append("FRL_LINE_BS_DR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(vObject.getFrlLineBsCr() != null && !vObject.getFrlLineBsCr().equalsIgnoreCase(""))
				strAudit.append("FRL_LINE_PL_DR"+auditDelimiterColVal+vObject.getFrlLineBsCr().trim());
			else
				strAudit.append("FRL_LINE_PL_DR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getFrlLinePlDr() != null && !vObject.getFrlLinePlDr().equalsIgnoreCase(""))
				strAudit.append("FRL_LINE_PL_CR"+auditDelimiterColVal+vObject.getFrlLinePlDr().trim());
			else
				strAudit.append("FRL_LINE_PL_CR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getFrlLinePlCr() != null && !vObject.getFrlLinePlCr().equalsIgnoreCase(""))
				strAudit.append("FRL_LINE_PL_CR"+auditDelimiterColVal+vObject.getFrlLinePlCr().trim());
			else
				strAudit.append("FRL_LINE_PL_CR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getMrlLineDr() != null && !vObject.getMrlLineDr().equalsIgnoreCase(""))
				strAudit.append("MRL_LINE_DR"+auditDelimiterColVal+vObject.getMrlLineDr().trim());
			else
				strAudit.append("MRL_LINE_DR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getMrlLineCr() != null && !vObject.getMrlLineCr().equalsIgnoreCase(""))
				strAudit.append("MRL_LINE_CR"+auditDelimiterColVal+vObject.getMrlLineCr().trim());
			else
				strAudit.append("MRL_LINE_CR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("ACCOUNTS_MAPPING_STATUS_NT"+auditDelimiterColVal+vObject.getAccountsMappingStatusNt());
			strAudit.append(auditDelimiter);
			strAudit.append("ACCOUNTS_MAPPING_STATUS"+auditDelimiterColVal+vObject.getAccountsMappingStatus());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			
			if(vObject.getRecordIndicator() == -1)
			{vObject.setRecordIndicator(0);}
			
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if(vObject.getDateLastModified() != null)
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getDateCreation() != null)
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
		serviceName = "AccountsMapping";
		serviceDesc = CommonUtils.getResourceManger().getString("accountsMapping");
		tableName = "ACCOUNTS_MAPPING";
		childTableName = "ACCOUNTS_MAPPING";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getBuildStatus(AccountsMappingVb accountsMappingVb){
		return getBuildModuleStatus(accountsMappingVb.getCountry(), accountsMappingVb.getLeBook());
	}
	public List<AccountsMappingVb> getAccountName(AccountsMappingVb object) {
		List<AccountsMappingVb> collTemp = null;
		String query = "select ACCOUNT_NAME from ACCOUNTS_MAPPING_EXPANDED where COUNTRY = '"+object.getCountry()+"' " +
				"And LE_BOOK = '"+object.getLeBook()+"' And ACCOUNT_NO_MAP = '"+object.getAccountNo()+"' ORDER BY COUNTRY, LE_BOOK, ACCOUNT_NO  ";
		try{
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AccountsMappingVb contractsVb = new AccountsMappingVb();
					contractsVb.setAccountNoDesc(rs.getString("ACCOUNT_NAME"));
					return contractsVb;
				}
			};
			collTemp = getJdbcTemplate().query(query, mapper);
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}
