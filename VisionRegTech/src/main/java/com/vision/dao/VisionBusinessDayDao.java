package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.VisionBusinessDayVb;

@Component
public class VisionBusinessDayDao extends AbstractDao<VisionBusinessDayVb>{

	@Autowired
	CommonDao commonDao;
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionBusinessDayVb visionBusinessDayVb = new VisionBusinessDayVb();
				visionBusinessDayVb.setCountry(rs.getString("COUNTRY"));
				visionBusinessDayVb.setLeBook(rs.getString("LE_BOOK"));
				if(rs.getString("BUSINESS_DATE") != null){
					visionBusinessDayVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					visionBusinessDayVb.setDbBusinessDate(rs.getString("BUSINESS_DATE"));
				}
				if(rs.getString("BUSINESS_YEAR_MONTH") != null){
					visionBusinessDayVb.setBusinessYearMonth(rs.getString("BUSINESS_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessYearMonth(rs.getString("DB_BUSINESS_YEAR_MONTH"));
				}
				if(rs.getString("BUSINESS_WEEK_DATE")!=null){
					visionBusinessDayVb.setBusinessWeeklyDate(rs.getString("BUSINESS_WEEK_DATE"));
					visionBusinessDayVb.setDbBusinessWeeklyDate(rs.getString("BUSINESS_WEEK_DATE"));
				}
				if(rs.getString("BUSINESS_QTR_YEAR_MONTH")!= null){
					visionBusinessDayVb.setBusinessQtrYearMonth(rs.getString("BUSINESS_QTR_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessQtrYearMonth(rs.getString("DB_BUSINESS_QTR_YEAR_MONTH"));
				}
				if(rs.getString("BUSINESS_HYR_YEAR_MONTH")!= null){
					visionBusinessDayVb.setBusinessHalfYearMonth(rs.getString("BUSINESS_HYR_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessHalfYearMonth(rs.getString("DB_BUSINESS_HYR_YEAR_MONTH"));
				}
				if(rs.getString("BUSINESS_YEAR")!= null){
					visionBusinessDayVb.setBusinessYear(rs.getString("BUSINESS_YEAR"));
				}
				visionBusinessDayVb.setVisionBusinessDayStatusNt(rs.getInt("VISION_BUSINESS_DAY_STATUS_NT"));
				visionBusinessDayVb.setVisionBusinessDayStatus(rs.getInt("VISION_BUSINESS_DAY_STATUS"));
				visionBusinessDayVb.setStatusDesc(rs.getString("VISION_BUSINESS_DAY_STATUS_DESC"));
				visionBusinessDayVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionBusinessDayVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionBusinessDayVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				visionBusinessDayVb.setMaker(rs.getLong("MAKER"));
				visionBusinessDayVb.setVerifier(rs.getLong("VERIFIER"));
				visionBusinessDayVb.setMakerName(rs.getString("MAKER_NAME"));
				visionBusinessDayVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				visionBusinessDayVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				visionBusinessDayVb.setDateCreation(rs.getString("DATE_CREATION"));
				visionBusinessDayVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				/*visionBusinessDayVb.setDlyFrequencyCount(rs.getInt("DLY_COUNT"));
				visionBusinessDayVb.setMonthlyFrequencyCount(rs.getInt("MTH_COUNT"));*/
				
				visionBusinessDayVb.setUpdAllowFlagDly(rs.getString("UPD_ALLOW_FLAG_DLY"));
				visionBusinessDayVb.setUpdAllowFlagMth(rs.getString("UPD_ALLOW_FLAG_MTH"));
				visionBusinessDayVb.setUpdAllowFlagWky(rs.getString("UPD_ALLOW_FLAG_WKY"));
				visionBusinessDayVb.setUpdAllowFlagQtr(rs.getString("UPD_ALLOW_FLAG_QTR"));
				visionBusinessDayVb.setUpdAllowFlagHyr(rs.getString("UPD_ALLOW_FLAG_HYR"));
				visionBusinessDayVb.setUpdAllowFlagAnn(rs.getString("UPD_ALLOW_FLAG_ANN"));
				
				return visionBusinessDayVb;
			}
		};
		return mapper;
	}
	protected RowMapper getMapperForReview(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionBusinessDayVb visionBusinessDayVb = new VisionBusinessDayVb();
				visionBusinessDayVb.setCountry(rs.getString("COUNTRY"));
				visionBusinessDayVb.setLeBook(rs.getString("LE_BOOK"));
				if(rs.getString("BUSINESS_DATE") != null){
					visionBusinessDayVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					visionBusinessDayVb.setDbBusinessDate(rs.getString("BUSINESS_DATE"));
				}
				if(rs.getString("BUSINESS_YEAR_MONTH") != null){
					visionBusinessDayVb.setBusinessYearMonth(rs.getString("BUSINESS_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessYearMonth(rs.getString("DB_BUSINESS_YEAR_MONTH"));
				}
				if(rs.getString("BUSINESS_WEEK_DATE")!=null){
					visionBusinessDayVb.setBusinessWeeklyDate(rs.getString("BUSINESS_WEEK_DATE"));
					visionBusinessDayVb.setDbBusinessWeeklyDate(rs.getString("BUSINESS_WEEK_DATE"));
				}
				if(rs.getString("BUSINESS_QTR_YEAR_MONTH")!= null){
					visionBusinessDayVb.setBusinessQtrYearMonth(rs.getString("BUSINESS_QTR_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessQtrYearMonth(rs.getString("DB_BUSINESS_QTR_YEAR_MONTH"));
				}
				if(rs.getString("BUSINESS_HYR_YEAR_MONTH")!= null){
					visionBusinessDayVb.setBusinessHalfYearMonth(rs.getString("BUSINESS_HYR_YEAR_MONTH"));
					visionBusinessDayVb.setDbBusinessHalfYearMonth(rs.getString("DB_BUSINESS_HYR_YEAR_MONTH"));

				}
				if(rs.getString("BUSINESS_YEAR")!= null){
					visionBusinessDayVb.setBusinessYear(rs.getString("BUSINESS_YEAR"));
				}
				visionBusinessDayVb.setVisionBusinessDayStatusNt(rs.getInt("VISION_BUSINESS_DAY_STATUS_NT"));
				visionBusinessDayVb.setVisionBusinessDayStatus(rs.getInt("VISION_BUSINESS_DAY_STATUS"));
				visionBusinessDayVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionBusinessDayVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionBusinessDayVb.setMaker(rs.getLong("MAKER"));
				visionBusinessDayVb.setVerifier(rs.getLong("VERIFIER"));
				visionBusinessDayVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				visionBusinessDayVb.setDateCreation(rs.getString("DATE_CREATION"));
				visionBusinessDayVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				/*visionBusinessDayVb.setDlyFrequencyCount(rs.getInt("DLY_COUNT"));
				visionBusinessDayVb.setMonthlyFrequencyCount(rs.getInt("MTH_COUNT"));*/
				
				/*visionBusinessDayVb.setUpdAllowFlagDly(rs.getString("UPD_ALLOW_FLAG_DLY"));
				visionBusinessDayVb.setUpdAllowFlagMth(rs.getString("UPD_ALLOW_FLAG_MTH"));
				visionBusinessDayVb.setUpdAllowFlagWky(rs.getString("UPD_ALLOW_FLAG_WKY"));
				visionBusinessDayVb.setUpdAllowFlagQtr(rs.getString("UPD_ALLOW_FLAG_QTR"));*/
				
				return visionBusinessDayVb;
			}
		};
		return mapper;
	}
	public List<VisionBusinessDayVb> getQueryResults(VisionBusinessDayVb dObj, int intStatus){
		Vector<Object> params = new Vector<Object>();
		String CalLeBook = removeDescLeBook(dObj.getLeBook()); 
		StringBuffer strBufApprove = new StringBuffer(" Select TAppr.COUNTRY, TAppr.LE_BOOK+ ' - ' +LEB.Leb_description AS LE_BOOK, FORMAT(TAppr.BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE, "+
				" TAppr.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TAppr.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TAppr.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TAppr.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.business_year_month,'01') as date)) as varchar) "+
				" AS BUSINESS_YEAR_MONTH, "+
				" 				  Format( TAppr.BUSINESS_WEEK_DATE,'dd-MMM-yyyy') BUSINESS_WEEK_DATE, "+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_QTR_YEAR_MONTH,"+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_HYR_YEAR_MONTH,"+
				" TAppr.BUSINESS_YEAR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TAppr.Country  "+
				" 				             And M2.Le_Book = TAppr.Le_Book And M2.Business_Date = TAppr.Business_Date And M2.Frequency_Process = 'DLY') =  "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = TAppr.Business_Date And M2.Frequency_Process = 'DLY' And M2.EOD_INITIATED_FLAG = 'Y'  "+
				" 				             And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = TAppr.Business_Date And M3.Frequency_Process = 'DLY' And M3.Template_Name != 'READINESS') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5 Where M4.Country = TAppr.Country  "+
				" 				             And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = TAppr.Business_Date And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'DLY' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Dly, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'MTH' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'MTH' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Mth, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = TAppr.Business_Week_Date And M2.Frequency_Process = 'WKY') = (Select Count(1) From ADF_PROCESS_CONTROL M2  "+
				" 				               Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book And M2.Business_Date = TAppr.Business_Week_Date "+
				" 				             And M2.Frequency_Process = 'WKY' And M2.EOD_INITIATED_FLAG = 'Y'  And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = TAppr.Business_Week_Date And M3.Frequency_Process = 'WKY' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = TAppr.Business_Week_Date "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'WKY' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Wky, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'QTR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'QTR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Qtr, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'HYR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'HYR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_HYR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M3.Frequency_Process = 'ANN' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'ANN' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_ANN, "+
				" 				 TAppr.VISION_BUSINESS_DAY_STATUS_NT, TAppr.VISION_BUSINESS_DAY_STATUS, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 1 AND NUM_SUB_TAB = TAppr.VISION_BUSINESS_DAY_STATUS ) VISION_BUSINESS_DAY_STATUS_DESC, "
				+ "TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR,"
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 7 AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR ) RECORD_INDICATOR_DESC, "+
				" 				 TAppr.MAKER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.MAKER,0) ) MAKER_NAME, "
				+ "TAppr.VERIFIER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.VERIFIER,0) ) VERIFIER_NAME, "
				+ "TAppr.INTERNAL_STATUS, Convert(varchar, GetDate(), 113) DATE_LAST_MODIFIED,"+
				"  Convert(varchar, GetDate(), 113) DATE_CREATION "+
				" 				 From Vision_Business_Day TAppr, Le_Book LEB Where TAppr.Country = LEB.Country And (TAppr.Le_Book = LEB.Le_Book) ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From VISION_BUSINESS_DAY_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY AND TPend.LE_BOOK = TAppr.LE_BOOK)");
		StringBuffer strBufPending = new StringBuffer(" Select TPend.COUNTRY, TPend.LE_BOOK+ ' - ' +LEB.Leb_description AS LE_BOOK, Format( TPend.BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE, "+
				" TPend.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TPend.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TPend.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TPend.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.business_year_month,'01') as date)) as varchar) "+
				" AS BUSINESS_YEAR_MONTH, "+
				" 				  Format( TPend.BUSINESS_WEEK_DATE,'dd-MMM-yyyy') BUSINESS_WEEK_DATE,"+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_QTR_YEAR_MONTH,"+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_HYR_YEAR_MONTH,"+				
				 " TPend.BUSINESS_YEAR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TPend.Country  "+
				" 				             And M2.Le_Book = TPend.Le_Book And M2.Business_Date = TPend.Business_Date And M2.Frequency_Process = 'DLY') =  "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = TPend.Business_Date And M2.Frequency_Process = 'DLY' And M2.EOD_INITIATED_FLAG = 'Y'  "+
				" 				             And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = TPend.Business_Date And M3.Frequency_Process = 'DLY' And M3.Template_Name != 'READINESS') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5 Where M4.Country = TPend.Country  "+
				" 				             And M4.Le_Book = TPend.Le_Book And M4.Business_Date = TPend.Business_Date And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'DLY' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Dly, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'MTH' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'MTH' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Mth, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = TPend.Business_Week_Date And M2.Frequency_Process = 'WKY') = (Select Count(1) From ADF_PROCESS_CONTROL M2  "+
				" 				               Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book And M2.Business_Date = TPend.Business_Week_Date "+
				" 				             And M2.Frequency_Process = 'WKY' And M2.EOD_INITIATED_FLAG = 'Y'  And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = TPend.Business_Week_Date And M3.Frequency_Process = 'WKY' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book And M4.Business_Date = TPend.Business_Week_Date "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'WKY' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Wky, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'QTR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'QTR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Qtr, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'HYR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'HYR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_HYR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M3.Frequency_Process = 'ANN' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'ANN' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_ANN, "+
				" 				 TPend.VISION_BUSINESS_DAY_STATUS_NT, TPend.VISION_BUSINESS_DAY_STATUS, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 1 AND NUM_SUB_TAB = TPend.VISION_BUSINESS_DAY_STATUS ) VISION_BUSINESS_DAY_STATUS_DESC, "
				+ "TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 7 AND NUM_SUB_TAB = TPend.RECORD_INDICATOR ) RECORD_INDICATOR_DESC, "+
				" 				 TPend.MAKER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.MAKER,0) ) MAKER_NAME, "
				+ "TPend.VERIFIER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.VERIFIER,0) ) VERIFIER_NAME, "
				+ "TPend.INTERNAL_STATUS, Convert(varchar, GetDate(), 113) DATE_LAST_MODIFIED,"+
				"  Convert(varchar, GetDate(), 113) DATE_CREATION "+
				" 				 From VISION_BUSINESS_DAY_PEND TPend, Le_Book LEB Where TPend.Country = LEB.Country And (TPend.Le_Book = LEB.Le_Book) ");

/*		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry().toUpperCase());//[COUNTRY]
		objParams[1] = new String(CalLeBook.toUpperCase());//[COUNTRY]
*/
		try
		{
			
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement("%"+dObj.getCountry().toUpperCase()+"%");
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.COUNTRY) like ?", strBufPending);
			}			
			
			if (ValidationUtil.isValid(CalLeBook))
			{
				params.addElement("%"+CalLeBook.toUpperCase()+"%");
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.LE_BOOK) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				CommonUtils.addToQuery("TAppr.COUNTRY+ ' - ' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY+ ' - ' +TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()))
			{
				params.addElement(dObj.getBusinessDate());
				CommonUtils.addToQuery("TAppr.BUSINESS_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_DATE = CONVERT(datetime, ?, 103) ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessYearMonth())){
				params.addElement("%" + dObj.getBusinessYearMonth() + "%");
				CommonUtils.addToQuery("(TAppr.BUSINESS_YEAR_MONTH) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("(TPend.BUSINESS_YEAR_MONTH) LIKE ?", strBufPending);
			}
			
			/*if (ValidationUtil.isValid(dObj.getBusinessQtrYearMonth())){
				params.addElement("%" + dObj.getBusinessQtrYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.BUSINESS_QTR_YEAR_MONTH LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_QTR_YEAR_MONTH LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessHalfYearMonth())){
				params.addElement("%" + dObj.getBusinessHalfYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.BUSINESS_HYR_YEAR_MONTH LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_HYR_YEAR_MONTH LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessYear())){
				params.addElement("%" + dObj.getBusinessQtrYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.Business_Year LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.Business_Year LIKE ?", strBufPending);
			}*/
			
			if (dObj.getVisionBusinessDayStatus() != -1){
				params.addElement(new Integer(dObj.getVisionBusinessDayStatus()));
				CommonUtils.addToQuery("TAppr.VISION_BUSINESS_DAY_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.VISION_BUSINESS_DAY_STATUS = ?", strBufPending);
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
			String orderBy = " Order By COUNTRY, LE_BOOK ";
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
	
	public List<VisionBusinessDayVb> getQueryPopupResults(VisionBusinessDayVb dObj){
		Vector<Object> params = new Vector<Object>();
		String CalLeBook = removeDescLeBook(dObj.getLeBook()); 
		StringBuffer strBufApprove = new StringBuffer(" Select TAppr.COUNTRY, TAppr.LE_BOOK+ ' - ' +LEB.Leb_description AS LE_BOOK, FORMAT(TAppr.BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE, "+
			  " TAppr.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TAppr.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TAppr.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TAppr.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.business_year_month,'01') as date)) as varchar) "+
				" AS BUSINESS_YEAR_MONTH, "+
				" 				Format( TAppr.BUSINESS_WEEK_DATE,'dd-MMM-yyyy') BUSINESS_WEEK_DATE,"+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_QTR_YEAR_MONTH,"+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_HYR_YEAR_MONTH,"+
				 " TAppr.BUSINESS_YEAR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TAppr.Country  "+
				" 				             And M2.Le_Book = TAppr.Le_Book And M2.Business_Date = TAppr.Business_Date And M2.Frequency_Process = 'DLY') =  "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = TAppr.Business_Date And M2.Frequency_Process = 'DLY' And M2.EOD_INITIATED_FLAG = 'Y'  "+
				" 				             And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = TAppr.Business_Date And M3.Frequency_Process = 'DLY' And M3.Template_Name != 'READINESS') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5 Where M4.Country = TAppr.Country  "+
				" 				             And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = TAppr.Business_Date And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'DLY' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Dly, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'MTH' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.Business_YEAR_MONTH)+'01')) "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'MTH' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Mth, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = TAppr.Business_Week_Date And M2.Frequency_Process = 'WKY') = (Select Count(1) From ADF_PROCESS_CONTROL M2  "+
				" 				               Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book And M2.Business_Date = TAppr.Business_Week_Date "+
				" 				             And M2.Frequency_Process = 'WKY' And M2.EOD_INITIATED_FLAG = 'Y'  And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = TAppr.Business_Week_Date And M3.Frequency_Process = 'WKY' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book And M4.Business_Date = TAppr.Business_Week_Date "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'WKY' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Wky, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'QTR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_QTR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'QTR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Qtr, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'HYR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TAppr.BUSINESS_HYR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'HYR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_HYR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TAppr.Country And M2.Le_Book = TAppr.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TAppr.Country And M3.Le_Book = TAppr.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M3.Frequency_Process = 'ANN' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TAppr.Country And M4.Le_Book = TAppr.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TAppr.BUSINESS_YEAR))) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'ANN' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_ANN, "+
				" 				 TAppr.VISION_BUSINESS_DAY_STATUS_NT, TAppr.VISION_BUSINESS_DAY_STATUS, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 1 AND NUM_SUB_TAB = TAppr.VISION_BUSINESS_DAY_STATUS ) VISION_BUSINESS_DAY_STATUS_DESC, "
				+ "TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR,"
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 7 AND NUM_SUB_TAB = TAppr.RECORD_INDICATOR ) RECORD_INDICATOR_DESC, "+
				" 				 TAppr.MAKER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.MAKER,0) ) MAKER_NAME, "
				+ "TAppr.VERIFIER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.VERIFIER,0) ) VERIFIER_NAME, "
				+ "TAppr.INTERNAL_STATUS, Convert(varchar, GetDate(), 113) DATE_LAST_MODIFIED,"+
				"  Convert(varchar, GetDate(), 113) DATE_CREATION "+
				" 				 From Vision_Business_Day TAppr, Le_Book LEB Where TAppr.Country = LEB.Country And (TAppr.Le_Book = LEB.Le_Book) ");
		String strWhereNotExists = new String( " Not Exists (Select 'X' From VISION_BUSINESS_DAY_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY AND TPend.LE_BOOK = TAppr.LE_BOOK)");
		StringBuffer strBufPending = new StringBuffer(" Select TPend.COUNTRY, TPend.LE_BOOK+ ' - ' +LEB.Leb_description AS LE_BOOK, Format( TPend.BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE, "+
				  " TPend.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TPend.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TPend.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TPend.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.business_year_month,'01') as date)) as varchar) "+
				" AS BUSINESS_YEAR_MONTH, "+
				" 				  Format( TPend.BUSINESS_WEEK_DATE,'dd-MMM-yyyy') BUSINESS_WEEK_DATE,"+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_QTR_YEAR_MONTH,"+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) "+
				" BUSINESS_HYR_YEAR_MONTH,"+	
				 " TPend.BUSINESS_YEAR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TPend.Country  "+
				" 				             And M2.Le_Book = TPend.Le_Book And M2.Business_Date = TPend.Business_Date And M2.Frequency_Process = 'DLY') =  "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2  Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = TPend.Business_Date And M2.Frequency_Process = 'DLY' And M2.EOD_INITIATED_FLAG = 'Y'  "+
				" 				             And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = TPend.Business_Date And M3.Frequency_Process = 'DLY' And M3.Template_Name != 'READINESS') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5 Where M4.Country = TPend.Country  "+
				" 				             And M4.Le_Book = TPend.Le_Book And M4.Business_Date = TPend.Business_Date And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'DLY' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Dly, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'MTH' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'MTH' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.Business_YEAR_MONTH)+'01')) "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'MTH' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Mth, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = TPend.Business_Week_Date And M2.Frequency_Process = 'WKY') = (Select Count(1) From ADF_PROCESS_CONTROL M2  "+
				" 				               Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book And M2.Business_Date = TPend.Business_Week_Date "+
				" 				             And M2.Frequency_Process = 'WKY' And M2.EOD_INITIATED_FLAG = 'Y'  And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') "+
				" 				             OR (Select Count(1) From ADF_PROCESS_CONTROL M3 Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = TPend.Business_Week_Date And M3.Frequency_Process = 'WKY' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book And M4.Business_Date = TPend.Business_Week_Date "+
				" 				             And M4.ADF_NUMBER = M5.ADF_NUMBER And M4.Frequency_Process = 'WKY' And M4.Template_Name != 'READINESS' "+
				" 				             And M4.Acquisition_Status in ('READYERR','DEPERR') And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Wky, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'QTR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'QTR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_QTR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'QTR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_Qtr, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M2.Frequency_Process = 'HYR' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M3.Frequency_Process = 'HYR' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, Convert(Varchar,TPend.BUSINESS_HYR_YEAR_MONTH)+'01')) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'HYR' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_HYR, "+
				" 				 Case When ( (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN') = "+
				" 				                  (Select Count(1) From ADF_PROCESS_CONTROL M2 Where M2.Country = TPend.Country And M2.Le_Book = TPend.Le_Book "+
				" 				             And M2.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M2.Frequency_Process = 'ANN' "+
				" 				             And M2.EOD_INITIATED_FLAG = 'Y' And M2.ADF_SUCCESSFUL_EMAIL_FLAG = 'YY') OR (Select Count(1) From ADF_PROCESS_CONTROL M3  "+
				" 				               Where M3.Country = TPend.Country And M3.Le_Book = TPend.Le_Book "+
				" 				             And M3.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M3.Frequency_Process = 'ANN' "+
				" 				             And M3.Template_Name != 'READINESS') = (Select Count(1) From ADF_PROCESS_CONTROL M4, ADF_SCHEDULES M5  "+
				" 				               Where M4.Country = TPend.Country And M4.Le_Book = TPend.Le_Book "+
				" 				             And M4.Business_Date = EOMONTH(Convert(Date, '01-Dec-'+Convert(Varchar,TPend.BUSINESS_YEAR))) And M4.ADF_NUMBER = M5.ADF_NUMBER "+
				" 				             And M4.Frequency_Process = 'ANN' And M4.Template_Name != 'READINESS' And M4.Acquisition_Status in ('READYERR','DEPERR') "+
				" 				             And M4.ADF_FAILED_EMAIL_FLAG = 'YY' And M5.ADF_Schedule_Status = 'E') "+
				" 				             ) Then 'Y' Else 'N' End Upd_Allow_Flag_ANN, "+
				" 				 TPend.VISION_BUSINESS_DAY_STATUS_NT, TPend.VISION_BUSINESS_DAY_STATUS, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 1 AND NUM_SUB_TAB = TPend.VISION_BUSINESS_DAY_STATUS ) VISION_BUSINESS_DAY_STATUS_DESC, "
				+ "TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, "
				+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB = 7 AND NUM_SUB_TAB = TPend.RECORD_INDICATOR ) RECORD_INDICATOR_DESC, "+
				" 				 TPend.MAKER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.MAKER,0) ) MAKER_NAME, "
				+ "TPend.VERIFIER, "+
				" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.VERIFIER,0) ) VERIFIER_NAME, "
				+ "TPend.INTERNAL_STATUS, Convert(varchar, GetDate(), 113) DATE_LAST_MODIFIED,"+
				"  Convert(varchar, GetDate(), 113) DATE_CREATION "+
				" 				 From VISION_BUSINESS_DAY_PEND TPend, Le_Book LEB Where TPend.Country = LEB.Country And (TPend.Le_Book = LEB.Le_Book) ");

/*		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry().toUpperCase());//[COUNTRY]
		objParams[1] = new String(CalLeBook.toUpperCase());//[COUNTRY]
*/
		try
		{
			
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement("%"+dObj.getCountry().toUpperCase()+"%");
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.COUNTRY) like ?", strBufPending);
			}			
			
			if (ValidationUtil.isValid(CalLeBook))
			{
				params.addElement("%"+CalLeBook.toUpperCase()+"%");
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.LE_BOOK) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				CommonUtils.addToQuery("TAppr.COUNTRY+ ' - ' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY+ ' - ' +TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()))
			{
				params.addElement(dObj.getBusinessDate());
				CommonUtils.addToQuery("TAppr.BUSINESS_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_DATE = CONVERT(datetime, ?, 103) ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessYearMonth())){
				params.addElement("%" + dObj.getBusinessYearMonth() + "%");
				CommonUtils.addToQuery("(TAppr.BUSINESS_YEAR_MONTH) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("(TPend.BUSINESS_YEAR_MONTH) LIKE ?", strBufPending);
			}
			
			/*if (ValidationUtil.isValid(dObj.getBusinessQtrYearMonth())){
				params.addElement("%" + dObj.getBusinessQtrYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.BUSINESS_QTR_YEAR_MONTH LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_QTR_YEAR_MONTH LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessHalfYearMonth())){
				params.addElement("%" + dObj.getBusinessHalfYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.BUSINESS_HYR_YEAR_MONTH LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BUSINESS_HYR_YEAR_MONTH LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBusinessYear())){
				params.addElement("%" + dObj.getBusinessQtrYearMonth() + "%");
				CommonUtils.addToQuery("TAppr.Business_Year LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.Business_Year LIKE ?", strBufPending);
			}*/
			
			if (dObj.getVisionBusinessDayStatus() != -1){
				params.addElement(new Integer(dObj.getVisionBusinessDayStatus()));
				CommonUtils.addToQuery("TAppr.VISION_BUSINESS_DAY_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.VISION_BUSINESS_DAY_STATUS = ?", strBufPending);
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
			String orderBy = " Order By COUNTRY, LE_BOOK ";
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
	public List<VisionBusinessDayVb> getQueryResultsForReview(VisionBusinessDayVb dObj, int intStatus){

		List<VisionBusinessDayVb> collTemp = null;
		final int intKeyFieldsCount = 2;
		String CalLeBook = removeDescLeBook(dObj.getLeBook());

		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY," +
				"TAppr.LE_BOOK, format(TAppr.BUSINESS_DATE, 'dd-MM-yyyy') BUSINESS_DATE, "+
				" TAppr.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TAppr.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TAppr.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TAppr.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.business_year_month,'01') as date)) as varchar) BUSINESS_YEAR_MONTH, "+ 
				" format(TAppr.BUSINESS_WEEK_DATE, 'dd-MM-yyyy') BUSINESS_WEEK_DATE,"+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) BUSINESS_QTR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TAppr.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) BUSINESS_HYR_YEAR_MONTH, "+	
				" TAppr.BUSINESS_YEAR, 0 DLY_COUNT, 0 MTH_COUNT," +
				"TAppr.VISION_BUSINESS_DAY_STATUS_NT, TAppr.VISION_BUSINESS_DAY_STATUS, TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, TAppr.MAKER, "+ 
				"TAppr.VERIFIER, TAppr.INTERNAL_STATUS, format(TAppr.DATE_LAST_MODIFIED,'dd-MM-yyyy HH24:MI:SS') DATE_LAST_MODIFIED,format(TAppr.DATE_CREATION,'dd-MM-yyyy HH24:MI:SS') DATE_CREATION "+
				" From VISION_BUSINESS_DAY TAppr WHERE COUNTRY = ? AND LE_BOOK = ?");

		StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY," +
				"TPend.LE_BOOK, format(TPend.BUSINESS_DATE, 'dd-MM-yyyy') BUSINESS_DATE, "+
				" TPend.BUSINESS_YEAR_MONTH as DB_BUSINESS_YEAR_MONTH,TPend.BUSINESS_QTR_YEAR_MONTH as DB_BUSINESS_QTR_YEAR_MONTH,TPend.BUSINESS_HYR_YEAR_MONTH as DB_BUSINESS_HYR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TPend.business_year_month,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.business_year_month,'01') as date)) as varchar) BUSINESS_YEAR_MONTH, "+ 
				" format(TPend.BUSINESS_WEEK_DATE, 'dd-MM-yyyy') BUSINESS_WEEK_DATE,"+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_QTR_YEAR_MONTH,'01') as date)) as varchar) BUSINESS_QTR_YEAR_MONTH, "+
				" LEFT(datename(m,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)),3)+'-'+cast(datepart(yyyy,cast(concat(TPend.BUSINESS_HYR_YEAR_MONTH,'01') as date)) as varchar) BUSINESS_HYR_YEAR_MONTH, "+	
				  "TPend.BUSINESS_YEAR, 0 DLY_COUNT, 0 MTH_COUNT, " +
				" TPend.VISION_BUSINESS_DAY_STATUS_NT, TPend.VISION_BUSINESS_DAY_STATUS, TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, TPend.MAKER, "+ 
                " TPend.VERIFIER, TPend.INTERNAL_STATUS, format(TPend.DATE_LAST_MODIFIED,'dd-MM-yyyy HH24:MI:SS') DATE_LAST_MODIFIED, format(TPend.DATE_CREATION,'dd-MM-yyyy HH24:MI:SS') DATE_CREATION "+ 
				" From VISION_BUSINESS_DAY_PEND TPend  WHERE COUNTRY = ? AND LE_BOOK = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = CalLeBook;

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strBufApprove.toString(),objParams,getMapperForReview());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strBufPending.toString(),objParams,getMapperForReview());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			if(intStatus == 0)
				logger.error(((strBufApprove == null) ? "strQueryAppr is Null" : strBufApprove.toString()));
			else
				logger.error(((strBufPending == null) ? "strQueryPend is Null" : strBufPending.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	@Override
	protected List<VisionBusinessDayVb> selectApprovedRecord(VisionBusinessDayVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<VisionBusinessDayVb> doSelectPendingRecord(VisionBusinessDayVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int doInsertionAppr(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into VISION_BUSINESS_DAY (COUNTRY, LE_BOOK, BUSINESS_DATE, BUSINESS_YEAR_MONTH,BUSINESS_WEEK_DATE,BUSINESS_QTR_YEAR_MONTH,VISION_BUSINESS_DAY_STATUS_NT,VISION_BUSINESS_DAY_STATUS, "+
				       " RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,BUSINESS_HYR_YEAR_MONTH,BUSINESS_YEAR) "+
			"Values (?, ?, CONVERT(datetime, ?, 103), ? , CONVERT(datetime, ?, 103), ?, ?, ? ,? ,? ,? , ?, ? , getDate(), getDate() , ? , ?)";
		Object[] args = {vObject.getCountry(),CalLeBook, vObject.getBusinessDate(), vObject.getBusinessYearMonth(),vObject.getBusinessWeeklyDate(),vObject.getBusinessQtrYearMonth(),
				vObject.getVisionBusinessDayStatusNt(),vObject.getVisionBusinessDayStatus(),
				        vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getBusinessHalfYearMonth(),vObject.getBusinessYear()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into VISION_BUSINESS_DAY_PEND (COUNTRY, LE_BOOK, BUSINESS_DATE, BUSINESS_YEAR_MONTH,BUSINESS_WEEK_DATE,BUSINESS_QTR_YEAR_MONTH,VISION_BUSINESS_DAY_STATUS_NT,VISION_BUSINESS_DAY_STATUS, "+
			       " RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, BUSINESS_HYR_YEAR_MONTH, BUSINESS_YEAR) "+
		"Values (?, ?, CONVERT(datetime, ?, 103), ? , CONVERT(datetime, ?, 103), ?, ?, ? ,?,? ,? , ?, ? , getDate(), getDate(), ?, ?)";
	Object[] args = {vObject.getCountry(),CalLeBook, vObject.getBusinessDate(), vObject.getBusinessYearMonth(),vObject.getBusinessWeeklyDate(),vObject.getBusinessQtrYearMonth(),vObject.getVisionBusinessDayStatusNt(),vObject.getVisionBusinessDayStatus(),
			        vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getBusinessHalfYearMonth(),vObject.getBusinessYear()};  
	return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPendWithDc(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into VISION_BUSINESS_DAY_PEND (COUNTRY, LE_BOOK, BUSINESS_DATE, BUSINESS_YEAR_MONTH,BUSINESS_WEEK_DATE,BUSINESS_QTR_YEAR_MONTH,VISION_BUSINESS_DAY_STATUS_NT,VISION_BUSINESS_DAY_STATUS, "+
			       " RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, BUSINESS_HYR_YEAR_MONTH, BUSINESS_YEAR) "+
		"Values (?, ?, CONVERT(datetime, ?, 103), ? , CONVERT(datetime, ?, 103), ?, ?, ? ,? ,? ,? , ?, ? , getDate(), getDate(), ?, ?)";
	Object[] args = {vObject.getCountry(),CalLeBook, vObject.getBusinessDate(), vObject.getBusinessYearMonth(),vObject.getBusinessWeeklyDate(),vObject.getBusinessQtrYearMonth(),vObject.getVisionBusinessDayStatusNt(),vObject.getVisionBusinessDayStatus(),
			        vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getBusinessHalfYearMonth(),vObject.getBusinessYear()};  
	return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update VISION_BUSINESS_DAY Set BUSINESS_DATE = CONVERT(datetime, ?, 103), BUSINESS_YEAR_MONTH = ?,BUSINESS_WEEK_DATE = CONVERT(datetime, ?, 103),BUSINESS_QTR_YEAR_MONTH = ?,BUSINESS_HYR_YEAR_MONTH = ?,BUSINESS_YEAR =?,VISION_BUSINESS_DAY_STATUS_NT =? , "+
		               "VISION_BUSINESS_DAY_STATUS =?, RECORD_INDICATOR_NT =?, RECORD_INDICATOR =?, MAKER =?, VERIFIER =?, INTERNAL_STATUS= ?, DATE_LAST_MODIFIED = getDate() "+
				       "Where COUNTRY = ? AND LE_BOOK = ? ";
		Object[] args = {vObject.getBusinessDate(), vObject.getBusinessYearMonth(),vObject.getBusinessWeeklyDate(),vObject.getBusinessQtrYearMonth(),vObject.getBusinessHalfYearMonth(),vObject.getBusinessYear(),vObject.getVisionBusinessDayStatusNt(),vObject.getVisionBusinessDayStatus(),
				         vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(), vObject.getCountry(), CalLeBook};
		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateAll(VisionBusinessDayVb vObject){
		if(ValidationUtil.isValid(vObject.getBusinessYearMonth()) && ValidationUtil.isValid(vObject.getBusinessDate())){
			String query = "Update VISION_BUSINESS_DAY Set BUSINESS_DATE = CONVERT(datetime, ?, 103), BUSINESS_YEAR_MONTH= ? ";
			Object[] args = {vObject.getBusinessDate(), vObject.getBusinessYearMonth()};
			return getJdbcTemplate().update(query,args);
		}else{
			String query = "Update VISION_BUSINESS_DAY Set BUSINESS_DATE = CONVERT(datetime, ?, 103) ";
			Object[] args = {vObject.getBusinessDate()};
			return getJdbcTemplate().update(query,args);
		}
	}
	@Override
	protected int doUpdatePend(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update VISION_BUSINESS_DAY_PEND Set BUSINESS_DATE = CONVERT(datetime, ?, 103), BUSINESS_YEAR_MONTH = ?,BUSINESS_WEEK_DATE = CONVERT(datetime, ?, 103),BUSINESS_QTR_YEAR_MONTH = ?,BUSINESS_HYR_YEAR_MONTH = ?,BUSINESS_YEAR = ?, VISION_BUSINESS_DAY_STATUS_NT =?, "+
		               "VISION_BUSINESS_DAY_STATUS =?, RECORD_INDICATOR_NT =?, RECORD_INDICATOR =?, MAKER =?, VERIFIER =?, INTERNAL_STATUS= ?, DATE_LAST_MODIFIED = getDate() "+
				       "Where COUNTRY = ? AND LE_BOOK = ? ";
		Object[] args = {vObject.getBusinessDate(), vObject.getBusinessYearMonth(),vObject.getBusinessWeeklyDate(),vObject.getBusinessQtrYearMonth(),vObject.getBusinessHalfYearMonth(),vObject.getBusinessYear(),vObject.getVisionBusinessDayStatusNt(),vObject.getVisionBusinessDayStatus(),
				         vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(), vObject.getCountry(), CalLeBook};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From VISION_BUSINESS_DAY Where COUNTRY = ? AND LE_BOOK = ?";
		Object[] args = {vObject.getCountry(), CalLeBook};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(VisionBusinessDayVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From VISION_BUSINESS_DAY_PEND Where COUNTRY = ? AND LE_BOOK = ?";
		Object[] args = {vObject.getCountry(), CalLeBook};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int getStatus(VisionBusinessDayVb records){return records.getVisionBusinessDayStatus();}
	@Override
	protected void setStatus(VisionBusinessDayVb vObject,int status){vObject.setVisionBusinessDayStatus(status);}
	@Override
	protected String frameErrorMessage(VisionBusinessDayVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			strErrMsg =  strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + "LE_BOOK:" + vObject.getLeBook();
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
	protected String getAuditString(VisionBusinessDayVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal(); 
		StringBuffer strAudit = new StringBuffer("");
		if(ValidationUtil.isValid(vObject.getCountry()))
			strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
		else
			strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);

		if(ValidationUtil.isValid(vObject.getLeBook()))
			strAudit.append("LE_BOOK"+auditDelimiterColVal+removeDescLeBook(vObject.getLeBook()).trim());
		else
			strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("BUSINESS_DATE"+auditDelimiterColVal+vObject.getBusinessDate());
		strAudit.append(auditDelimiter);

		strAudit.append("BUSINESS_YEAR_MONTH"+auditDelimiterColVal+vObject.getBusinessYearMonth());
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getBusinessWeeklyDate()))
			strAudit.append("BUSINESS_WEEK_DATE"+auditDelimiterColVal+vObject.getBusinessWeeklyDate().trim());
		else
			strAudit.append("BUSINESS_WEEK_DATE"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getBusinessQtrYearMonth()))
			strAudit.append("BUSINESS_QTR_YEAR_MONTH"+auditDelimiterColVal+vObject.getBusinessQtrYearMonth().trim());
		else
			strAudit.append("BUSINESS_QTR_YEAR_MONTH"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getBusinessHalfYearMonth()))
			strAudit.append("BUSINESS_HYR_YEAR_MONTH"+auditDelimiterColVal+vObject.getBusinessHalfYearMonth().trim());
		else
			strAudit.append("BUSINESS_HYR_YEAR_MONTH"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getBusinessYear()))
			strAudit.append("BUSINESS_YEAR"+auditDelimiterColVal+vObject.getBusinessYear().trim());
		else
			strAudit.append("BUSINESS_YEAR"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		
		strAudit.append("VISION_BUSINESS_DAY_STATUS_NT"+auditDelimiterColVal+vObject.getVisionBusinessDayStatusNt());
		strAudit.append(auditDelimiter);
		strAudit.append("VISION_BUSINESS_DAY_STATUS"+auditDelimiterColVal+vObject.getVisionBusinessDayStatus());
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
		serviceName = "VisionBusinessDay";
		serviceDesc = "VisionBusinessDay";//CommonUtils.getResourceManger().getString("visionBusinessDay");
		tableName = "VISION_BUSINESS_DAY";
		childTableName = "VISION_BUSINESS_DAY";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(VisionBusinessDayVb vObject) throws RuntimeCustomException {
		List<VisionBusinessDayVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			int intStaticDeletionFlag = getStatus(((ArrayList<VisionBusinessDayVb>)collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				logger.error("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			}else{
				logger.error("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		// Try inserting the record
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	@Override
	protected ExceptionCode doUpdateApprRecordForNonTrans(VisionBusinessDayVb vObject) throws RuntimeCustomException  {
		List<VisionBusinessDayVb> collTemp = null;
		VisionBusinessDayVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<VisionBusinessDayVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}	
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(VisionBusinessDayVb vObject) throws RuntimeCustomException {
		List<VisionBusinessDayVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		VisionBusinessDayVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))){
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0 ){
			int intStaticDeletionFlag = getStatus(((ArrayList<VisionBusinessDayVb>)collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<VisionBusinessDayVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if(vObject.isStaticDelete()){
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateAppr(vObject);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
		}else{
			//delete the record from the Approve Table
			retVal = doDeleteAppr(vObject);
			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);

		}
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if(vObject.isStaticDelete()){
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject,vObjectlocal);
		}else{
			exceptionCode = writeAuditLog(null,vObject);
			vObject.setRecordIndicator(-1);
		}
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}	
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doUpdateApprRecord(List<VisionBusinessDayVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		setServiceDefaults();
		try {
			if("ALL".equalsIgnoreCase(vObjects.get(0).getUpdateBusinessDate())){
				int dlyCount = getRunningDlyCountForBusinessDate(vObjects.get(0));
				int monthlyCount = 0;
				if(dlyCount != 0 ){
					exceptionCode = CommonUtils.getResultObject(serviceDesc, Constants.WE_HAVE_ERROR_DESCRIPTION, strApproveOperation, dlyCount+" Entities are in progress. We could not modify the Business Date/Year Month");
					return exceptionCode;
				}else if(dlyCount == 0 ){
					monthlyCount = getRunningMonthlyCountForBusinessDate(vObjects.get(0));
					if(monthlyCount != 0){
						exceptionCode = CommonUtils.getResultObject(serviceDesc, Constants.WE_HAVE_ERROR_DESCRIPTION, strApproveOperation, monthlyCount+" Entities are in progress. We could not modify the Business Date/Year Month");
						return exceptionCode;						
					}
				}
				doUpdateAll(vObjects.get(0));
			}else{
				for(VisionBusinessDayVb vObject : vObjects){
					if(vObject.isChecked()){
						if(vObject.isNewRecord()){
							strCurrentOperation = Constants.ADD;
							exceptionCode = doInsertApprRecordForNonTrans(vObject);
							if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
								throw buildRuntimeCustomException(exceptionCode);
							}
						}else{
							strCurrentOperation = Constants.MODIFY;
							exceptionCode = doUpdateApprRecordForNonTrans(vObject);
							if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
								throw buildRuntimeCustomException(exceptionCode);
							}
						}
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public int getRunningDlyCountForBusinessDate(VisionBusinessDayVb vObject) {
		final int intKeyFieldsCount = 1;
		int count = 0;
		String strQuery = new String("SELECT COUNT(DISTINCT T1.COUNTRY++T1.LE_BOOK) FROM ADF_PROCESS_CONTROL T1, VISION_BUSINESS_DAY T2 "
				+ "WHERE T1.EOD_INITIATED_FLAG = 'N' AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.BUSINESS_DATE = T2.BUSINESS_DATE "
				+ "AND T1.FREQUENCY_PROCESS = 'DLY' ");
				//+ "AND T1.BUSINESS_DATE = CONVERT(datetime, ?, 103)");
		Object objParams[] = new Object[intKeyFieldsCount];
//		objParams[0] = vObject.getBusinessDate();
		try
		{
//			count = getJdbcTemplate().queryForInt(strQuery.toString());
			count = getJdbcTemplate().queryForObject(strQuery, Integer.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	public int getRunningMonthlyCountForBusinessDate(VisionBusinessDayVb vObject) {
		final int intKeyFieldsCount = 1;
		int count = 0;
		String strQuery = new String("SELECT COUNT(DISTINCT T1.COUNTRY||T1.LE_BOOK) FROM ADF_PROCESS_CONTROL T1, VISION_BUSINESS_DAY T2 "
				+ "WHERE T1.EOD_INITIATED_FLAG = 'N' AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.BUSINESS_DATE = LAST_DAY(format"
				+ "(T2.BUSINESS_YEAR_MONTH,'RRRRMM')) "
				+ "AND T1.FREQUENCY_PROCESS = 'MTH' ");
//				+ "AND T1.BUSINESS_DATE = CONVERT(datetime, ?, 103)");
		Object objParams[] = new Object[intKeyFieldsCount];
//		objParams[0] = vObject.getBusinessDate();
		try
		{
//			count = getJdbcTemplate().queryForInt(strQuery.toString());
			count = getJdbcTemplate().queryForObject(strQuery, Integer.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}

}
