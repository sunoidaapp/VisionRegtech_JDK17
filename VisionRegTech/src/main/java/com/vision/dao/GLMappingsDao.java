package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.GLMappingsVb;
import com.vision.vb.VisionUsersVb;
@Component
public class GLMappingsDao extends AbstractDao<GLMappingsVb> {
	 	
	
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				GLMappingsVb glMappingsVb = new GLMappingsVb();
				glMappingsVb.setCountry(rs.getString("COUNTRY"));
				glMappingsVb.setLeBook(rs.getString("LE_BOOK"));
				glMappingsVb.setBsGl(rs.getString("BS_GL"));
				glMappingsVb.setPlGl(rs.getString("PL_GL"));
				glMappingsVb.setGlEnrichId(rs.getString("GL_ENRICH_ID"));
				glMappingsVb.setProductDr(rs.getString("PRODUCT_DR"));
				glMappingsVb.setProductCr(rs.getString("PRODUCT_CR"));
				glMappingsVb.setFrlLineBsDr(rs.getString("FRL_LINE_BS_DR"));
				glMappingsVb.setFrlLineBsCr(rs.getString("FRL_LINE_BS_CR"));
				glMappingsVb.setFrlLinePlDr(rs.getString("FRL_LINE_PL_DR"));
				glMappingsVb.setFrlLinePlCr(rs.getString("FRL_LINE_PL_CR"));
				glMappingsVb.setMrlLineDr(rs.getString("MRL_LINE_DR"));
				glMappingsVb.setMrlLineCr(rs.getString("MRL_LINE_CR"));
				/*if(rs.getString("OUC_OVERRIDE")!=null){
					glMappingsVb.setOucOverride(rs.getString("OUC_OVERRIDE"));
				}
				else{
					glMappingsVb.setOucOverride("");
				}
				glMappingsVb.setTaxAllowable(rs.getString("TAX_ALLOWABLE"));*/
				glMappingsVb.setGlMapStatusNt(rs.getInt("GL_MAP_STATUS_NT"));
				glMappingsVb.setGlMapStatus(rs.getInt("GL_MAP_STATUS"));
				glMappingsVb.setStatusDesc(rs.getString("GL_MAP_STATUS_DESC"));
				glMappingsVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				glMappingsVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				glMappingsVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				glMappingsVb.setMaker(rs.getLong("MAKER"));
				glMappingsVb.setVerifier(rs.getLong("VERIFIER"));
				glMappingsVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				glMappingsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				glMappingsVb.setDateCreation(rs.getString("DATE_CREATION"));
				return glMappingsVb;
			}
		};
		return mapper;
	}
	
	private RowMapper getQueryMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				GLMappingsVb glMappingsVb = new GLMappingsVb();
				glMappingsVb.setCountry(rs.getString("COUNTRY"));
				glMappingsVb.setLeBook(rs.getString("LE_BOOK"));
				glMappingsVb.setBsGl(rs.getString("BS_GL"));
				glMappingsVb.setPlGl(rs.getString("PL_GL"));
				glMappingsVb.setGlEnrichId(rs.getString("GL_ENRICH_ID"));
				glMappingsVb.setProductDr(rs.getString("PRODUCT_DR"));
				glMappingsVb.setProductCr(rs.getString("PRODUCT_CR"));
				glMappingsVb.setFrlLineBsDr(rs.getString("FRL_LINE_BS_DR"));
				glMappingsVb.setFrlLineBsCr(rs.getString("FRL_LINE_BS_CR"));
				glMappingsVb.setFrlLinePlDr(rs.getString("FRL_LINE_PL_DR"));
				glMappingsVb.setFrlLinePlCr(rs.getString("FRL_LINE_PL_CR"));
				glMappingsVb.setMrlLineDr(rs.getString("MRL_LINE_DR"));
				glMappingsVb.setMrlLineCr(rs.getString("MRL_LINE_CR"));
				/*if(rs.getString("OUC_OVERRIDE")!=null){
					glMappingsVb.setOucOverride(rs.getString("OUC_OVERRIDE"));
				}
				else{
					glMappingsVb.setOucOverride("");
				}
				glMappingsVb.setTaxAllowable(rs.getString("TAX_ALLOWABLE"));*/
				glMappingsVb.setGlMapStatusNt(rs.getInt("GL_MAP_STATUS_NT"));
				glMappingsVb.setGlMapStatus(rs.getInt("GL_MAP_STATUS"));
				glMappingsVb.setDbStatus(rs.getInt("GL_MAP_STATUS"));
				glMappingsVb.setStatusDesc(rs.getString("GL_MAP_STATUS_DESC"));
				glMappingsVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				glMappingsVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				glMappingsVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				glMappingsVb.setMaker(rs.getLong("MAKER"));
				glMappingsVb.setVerifier(rs.getLong("VERIFIER"));
				glMappingsVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				glMappingsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				glMappingsVb.setDateCreation(rs.getString("DATE_CREATION"));
				glMappingsVb.setBsGlDesc(rs.getString("BS_GL_DESC"));
				glMappingsVb.setPlGlDesc(rs.getString("PL_GL_DESC"));
				glMappingsVb.setGlEnrichIdDesc(rs.getString("GL_ENRICH_DESC"));
				glMappingsVb.setProductDrDesc(rs.getString("PRODUCT_DR_DESC"));
				glMappingsVb.setProductCrDesc(rs.getString("PRODUCT_CR_DESC"));
				glMappingsVb.setFrlLineBsDrDes(rs.getString("FRL_LINE_BS_DR_DESC"));
				glMappingsVb.setFrlLineBsCrDesc(rs.getString("FRL_LINE_BS_CR_DESC"));
				glMappingsVb.setFrlLinePlDrDesc(rs.getString("FRL_LINE_PL_DR_DESC"));
				glMappingsVb.setFrlLinePlCrDesc(rs.getString("FRL_LINE_PL_CR_DESC"));
				glMappingsVb.setMrlLineDrDesc(rs.getString("MRL_LINE_DR_DESC"));
				glMappingsVb.setMrlLineCrDesc(rs.getString("MRL_LINE_CR_DESC"));
				/*if(rs.getString("OUC_DESC")!=null){
					glMappingsVb.setOucOverrideDesc(rs.getString("OUC_DESC"));	
				}else{
					glMappingsVb.setOucOverrideDesc("");
				}*/
				return glMappingsVb;
			}
		};
		return mapper;
	}
	public List<GLMappingsVb> getQueryPopupResults(GLMappingsVb dObj){
		
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		
		String statusApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_MAP_STATUS", "GL_MAP_STATUS_DESC");
		String statusPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_MAP_STATUS", "GL_MAP_STATUS_DESC");

		String recIndApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		String recIndPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY," +
				"TAppr.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK, TAppr.BS_GL, TAppr.PL_GL," +
				"TAppr.GL_ENRICH_ID, TAppr.PRODUCT_DR, TAppr.PRODUCT_CR," +
				"TAppr.FRL_LINE_BS_DR, TAppr.FRL_LINE_BS_CR, TAppr.FRL_LINE_PL_DR," +
				"TAppr.FRL_LINE_PL_CR, TAppr.MRL_LINE_DR, TAppr.MRL_LINE_CR," +
				"TAppr.GL_MAP_STATUS_NT," +
				"TAppr.GL_MAP_STATUS, "+statusApprDesc+", TAppr.RECORD_INDICATOR_NT," +
				"TAppr.RECORD_INDICATOR , "+recIndApprDesc+", TAppr.MAKER, TAppr.VERIFIER," +
				"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_MAPPINGS TAppr ");
			String strWhereNotExists = new String( "  Not Exists (Select 'X' From GL_MAPPINGS_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY And TPend.LE_BOOK = TAppr.LE_BOOK And TPend.BS_GL = TAppr.BS_GL And TPend.PL_GL = TAppr.PL_GL And TPend.GL_ENRICH_ID = TAppr.GL_ENRICH_ID)");
			StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY," +
				"TPend.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK , TPend.BS_GL, TPend.PL_GL," +
				"TPend.GL_ENRICH_ID, TPend.PRODUCT_DR, TPend.PRODUCT_CR," +
				"TPend.FRL_LINE_BS_DR, TPend.FRL_LINE_BS_CR, TPend.FRL_LINE_PL_DR," +
				"TPend.FRL_LINE_PL_CR, TPend.MRL_LINE_DR, TPend.MRL_LINE_CR," +
				"TPend.GL_MAP_STATUS_NT," +
				"TPend.GL_MAP_STATUS, "+statusPendDesc+", TPend.RECORD_INDICATOR_NT," +
				"TPend.RECORD_INDICATOR, "+recIndPendDesc+", TPend.MAKER, TPend.VERIFIER," +
				"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_MAPPINGS_PEND TPend ");
		try
		{
			
			//check if the column [GL_MAP_STATUS] should be included in the query
			if (dObj.getGlMapStatus() != -1)
			{
				params.addElement(new Integer(dObj.getGlMapStatus()));
				CommonUtils.addToQuery("TAppr.GL_MAP_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_MAP_STATUS = ?", strBufPending);
			}

			//check if the column [COUNTRY] should be included in the query
//			if (ValidationUtil.isValid(dObj.getCountry()))
//			{
//				params.addElement(dObj.getCountry().toUpperCase());
//				CommonUtils.addToQuery("TAppr.COUNTRY = ?", strBufApprove);
//				CommonUtils.addToQuery("TPend.COUNTRY = ?", strBufPending);
//			}
//
//			//check if the column [LE_BOOK] should be included in the query
//			if (ValidationUtil.isValid(dObj.getLeBook()))
//			{
//				params.addElement("%"+CalLeBook+ "%");
//				CommonUtils.addToQuery("TAppr.LE_BOOK LIKE ?", strBufApprove);
//				CommonUtils.addToQuery("TPend.LE_BOOK LIKE ?", strBufPending);
//			}
//			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
//			{
//				CommonUtils.addToQuery("TAppr.COUNTRY+ '-' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
//				CommonUtils.addToQuery("TPend.COUNTRY+ '-' +TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
//			}
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(visionUsersVb.getCountry()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(visionUsersVb.getCountry()) + ") ", strBufPending);
				}
				if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
					CommonUtils.addToQuery(" LE_BOOK IN (" + toSqlInList(visionUsersVb.getLeBook()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN (" + toSqlInList(visionUsersVb.getLeBook()) + ") ", strBufPending);
				}
			} else {
				if (ValidationUtil.isValid(dObj.getCountry())) {
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(dObj.getCountry()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(dObj.getCountry()) + ") ", strBufPending);
				}
				if (ValidationUtil.isValid(dObj.getLeBook())) {
					String calLeBook = removeDescLeBook(dObj.getLeBook());
					CommonUtils.addToQuery(" LE_BOOK IN (" + toSqlInList(calLeBook )+ ") ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN (" + toSqlInList(calLeBook) + ") ", strBufPending);
				}
			}
			//check if the column [BS_GL] should be included in the query
			if (ValidationUtil.isValid(dObj.getBsGl()))
			{
				params.addElement("%" + dObj.getBsGl()+ "%");
				CommonUtils.addToQuery("TAppr.BS_GL LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BS_GL LIKE ?", strBufPending);
			
				/*params.addElement(dObj.getBsGl());
				CommonUtils.addToQuery("TAppr.BS_GL = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.BS_GL = ?", strBufPending);*/	
				
			}

			//check if the column [PL_GL] should be included in the query
			if (ValidationUtil.isValid(dObj.getPlGl()))
			{
				params.addElement("%" + dObj.getPlGl()+ "%");
				CommonUtils.addToQuery("TAppr.PL_GL LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PL_GL LIKE ?", strBufPending);
				/*
				params.addElement(dObj.getPlGl());
				CommonUtils.addToQuery("TAppr.PL_GL = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PL_GL = ?", strBufPending);*/	
				
			}

			//check if the column [GL_ENRICH_ID] should be included in the query
			if (ValidationUtil.isValid(dObj.getGlEnrichId()))
			{
				params.addElement(dObj.getGlEnrichId());
				CommonUtils.addToQuery("TAppr.GL_ENRICH_ID = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.GL_ENRICH_ID = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getProductDr()))
			{
				params.addElement("%" + dObj.getProductDr()+ "%");
				CommonUtils.addToQuery("TAppr.PRODUCT_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PRODUCT_DR LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getProductCr()))
			{
				params.addElement("%" + dObj.getProductCr()+ "%");
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
			if (ValidationUtil.isValid(dObj.getMrlLineDr()) && !"M000000".equalsIgnoreCase(dObj.getMrlLineDr()))
			{
				params.addElement("%" + dObj.getMrlLineDr() + "%");
				CommonUtils.addToQuery("TAppr.MRL_LINE_DR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MRL_LINE_DR LIKE ?", strBufPending);
			}

			//check if the column [MRL_LINE_CR] should be included in the query
			if (ValidationUtil.isValid(dObj.getMrlLineCr()) && !"M000000".equalsIgnoreCase(dObj.getMrlLineCr()))
			{
				params.addElement("%" + dObj.getMrlLineCr() + "%");
				CommonUtils.addToQuery("TAppr.MRL_LINE_CR LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MRL_LINE_CR LIKE ?", strBufPending);
			}

			//check if the column [OUC_OVERRIDE] should be included in the query
			/*if (ValidationUtil.isValid(dObj.getOucOverride()))
			{
				params.addElement("%" + dObj.getOucOverride() + "%");
				CommonUtils.addToQuery("TAppr.OUC_OVERRIDE LIKE ?", strBufApprove);
				CommonUtils.addToQuery("TPend.OUC_OVERRIDE LIKE ?", strBufPending);
			}*/

			//check if the column [TAX_ALLOWABLE] should be included in the query
			/*if (ValidationUtil.isValid(dObj.getTaxAllowable()))
			{
				params.addElement(dObj.getTaxAllowable());
				CommonUtils.addToQuery("TAppr.TAX_ALLOWABLE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TAX_ALLOWABLE = ?", strBufPending);
			}*/

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
			String orderBy=" Order By COUNTRY, LE_BOOK, BS_GL, PL_GL, GL_ENRICH_ID";
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
	private String toSqlInList(String CcountryLeBook) {
	    return Arrays.stream(CcountryLeBook.split(","))
	            .map(String::trim)
	            .map(val -> "'" + val + "'")
	            .collect(Collectors.joining(","));
	}

	public List<GLMappingsVb> getQueryResults(GLMappingsVb dObj, int intStatus){
		
		String CalLeBook = removeDescLeBook(dObj.getLeBook());

		List<GLMappingsVb> collTemp = null;
		final int intKeyFieldsCount = 5;
		setServiceDefaults();
		String statusApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_MAP_STATUS", "GL_MAP_STATUS_DESC");
		String statusPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_MAP_STATUS", "GL_MAP_STATUS_DESC");

		String recIndApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		String recIndPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
		
		String strQueryAppr = new String("Select TAppr.COUNTRY," +
				"TAppr.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK, TAppr.BS_GL,(SELECT Gl_Description FROM GL_CODES WHERE VISION_GL=TAppr.BS_GL  AND COUNTRY=TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) AS BS_GL_DESC, " +
				"TAppr.PL_GL, (SELECT Gl_Description FROM GL_CODES WHERE VISION_GL=TAppr.PL_GL  AND COUNTRY=TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) AS PL_GL_DESC, " +
				"TAppr.GL_ENRICH_ID,(SELECT Gl_Enrich_Description FROM GL_ENRICH_IDS WHERE GL_ENRICH_ID=TAppr.GL_ENRICH_ID) AS GL_ENRICH_DESC," +
				" TAppr.PRODUCT_DR, (SELECT Product_Description FROM PRODUCT_CODES WHERE PRODUCT=TAppr.PRODUCT_DR) AS PRODUCT_DR_DESC," +
				" TAppr.PRODUCT_CR,(SELECT Product_Description FROM PRODUCT_CODES WHERE PRODUCT=TAppr.PRODUCT_CR) AS PRODUCT_CR_DESC," +
				"TAppr.FRL_LINE_BS_DR, (SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TAppr.FRL_LINE_BS_DR) AS FRL_LINE_BS_DR_DESC," +
				"TAppr.FRL_LINE_BS_CR, (SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TAppr.FRL_LINE_BS_CR) AS FRL_LINE_BS_CR_DESC," +
				"TAppr.FRL_LINE_PL_DR,(SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TAppr.FRL_LINE_PL_DR) AS FRL_LINE_PL_DR_DESC," +
				"TAppr.FRL_LINE_PL_CR,(SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TAppr.FRL_LINE_PL_CR) AS FRL_LINE_PL_CR_DESC," +
				"TAppr.MRL_LINE_DR,(SELECT Mrl_Description FROM MRL_LINES WHERE MRL_LINE=TAppr.MRL_LINE_DR) AS MRL_LINE_DR_DESC," +
				"TAppr.MRL_LINE_CR,(SELECT Mrl_Description FROM MRL_LINES WHERE MRL_LINE=TAppr.MRL_LINE_CR) AS MRL_LINE_CR_DESC," +
				"TAppr.GL_MAP_STATUS_NT," +
				"TAppr.GL_MAP_STATUS , "+statusApprDesc+", TAppr.RECORD_INDICATOR_NT," +
				"TAppr.RECORD_INDICATOR, "+recIndApprDesc+", TAppr.MAKER, TAppr.VERIFIER," +
				"TAppr.INTERNAL_STATUS, Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
				" From GL_MAPPINGS TAppr " + 
				"Where TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.BS_GL = ? AND TAppr.PL_GL = ? AND TAppr.GL_ENRICH_ID = ?");


			String strQueryPend = new String("Select TPend.COUNTRY," +
					"TPend.LE_BOOK+ '-' +(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK , TPend.BS_GL,(SELECT Gl_Description FROM GL_CODES WHERE VISION_GL=TPend.BS_GL  AND COUNTRY=TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK) AS BS_GL_DESC, " +
					"TPend.PL_GL, (SELECT Gl_Description FROM GL_CODES WHERE VISION_GL=TPend.PL_GL AND COUNTRY=TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK) AS PL_GL_DESC, " +
					"TPend.GL_ENRICH_ID,(SELECT Gl_Enrich_Description FROM GL_ENRICH_IDS WHERE GL_ENRICH_ID=TPend.GL_ENRICH_ID) AS GL_ENRICH_DESC," +
					" TPend.PRODUCT_DR, (SELECT Product_Description FROM PRODUCT_CODES WHERE PRODUCT=TPend.PRODUCT_DR) AS PRODUCT_DR_DESC," +
					" TPend.PRODUCT_CR,(SELECT Product_Description FROM PRODUCT_CODES WHERE PRODUCT=TPend.PRODUCT_CR) AS PRODUCT_CR_DESC," +
					"TPend.FRL_LINE_BS_DR, (SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TPend.FRL_LINE_BS_DR) AS FRL_LINE_BS_DR_DESC," +
					"TPend.FRL_LINE_BS_CR, (SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TPend.FRL_LINE_BS_CR) AS FRL_LINE_BS_CR_DESC," +
					"TPend.FRL_LINE_PL_DR,(SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TPend.FRL_LINE_PL_DR) AS FRL_LINE_PL_DR_DESC," +
					"TPend.FRL_LINE_PL_CR,(SELECT Frl_Description FROM FRL_LINES WHERE FRL_LINE=TPend.FRL_LINE_PL_CR) AS FRL_LINE_PL_CR_DESC," +
					"TPend.MRL_LINE_DR,(SELECT Mrl_Description FROM MRL_LINES WHERE MRL_LINE=TPend.MRL_LINE_DR) AS MRL_LINE_DR_DESC," +
					"TPend.MRL_LINE_CR,(SELECT Mrl_Description FROM MRL_LINES WHERE MRL_LINE=TPend.MRL_LINE_CR) AS MRL_LINE_CR_DESC," +
					"TPend.GL_MAP_STATUS_NT," +
					"TPend.GL_MAP_STATUS, "+statusPendDesc+", TPend.RECORD_INDICATOR_NT," +
					"TPend.RECORD_INDICATOR, "+recIndPendDesc+", TPend.MAKER, TPend.VERIFIER," +
					"TPend.INTERNAL_STATUS, Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
					"Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION" +
					" From GL_MAPPINGS_PEND TPend " + 
					"Where TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.BS_GL = ? AND TPend.PL_GL = ? AND TPend.GL_ENRICH_ID = ?");

			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getCountry());	//[COUNTRY]
			objParams[1] = new String(CalLeBook);	//[LE_BOOK]
			objParams[2] = new String(dObj.getBsGl());	//[BS_GL]
			objParams[3] = new String(dObj.getPlGl());	//[PL_GL]
			objParams[4] = new String(dObj.getGlEnrichId());	//[GL_ENRICH_ID]

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getQueryMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getQueryMapper());
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
	protected List<GLMappingsVb> selectApprovedRecord(GLMappingsVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<GLMappingsVb> doSelectPendingRecord(GLMappingsVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(GLMappingsVb records){return records.getGlMapStatus();}
	@Override
	protected void setStatus(GLMappingsVb vObject,int status){vObject.setGlMapStatus(status);}
	@Override
	protected int doInsertionAppr(GLMappingsVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		 if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into GL_MAPPINGS(COUNTRY, LE_BOOK, BS_GL, PL_GL, GL_ENRICH_ID, PRODUCT_DR, "+
			"PRODUCT_CR, FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, "+
			"MRL_LINE_CR, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, "+
			"RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(),GetDate())";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getBsGl(), vObject.getPlGl(), vObject.getGlEnrichId(), vObject.getProductDr(),
			vObject.getProductCr(), vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(), vObject.getFrlLinePlDr(), vObject.getFrlLinePlCr(),
			vObject.getMrlLineDr(),vObject.getMrlLineCr(), vObject.getGlMapStatusNt(),vObject.getGlMapStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
			return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(GLMappingsVb vObject){
		
		 String CalLeBook = removeDescLeBook(vObject.getLeBook());
		 if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into GL_MAPPINGS_PEND ( COUNTRY, LE_BOOK, BS_GL, PL_GL, GL_ENRICH_ID, PRODUCT_DR, "+
			"PRODUCT_CR, FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, "+
			"MRL_LINE_CR, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, "+
			"RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GetDate(),GetDate())";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getBsGl(), vObject.getPlGl(), vObject.getGlEnrichId(), vObject.getProductDr(),
			vObject.getProductCr(), vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(), vObject.getFrlLinePlDr(), vObject.getFrlLinePlCr(),
			vObject.getMrlLineDr(),vObject.getMrlLineCr(), vObject.getGlMapStatusNt(),vObject.getGlMapStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPendWithDc(GLMappingsVb vObject){
		
		 String CalLeBook = removeDescLeBook(vObject.getLeBook());
		 if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Insert Into GL_MAPPINGS_PEND ( COUNTRY, LE_BOOK, BS_GL, PL_GL, GL_ENRICH_ID, PRODUCT_DR, "+
			"PRODUCT_CR, FRL_LINE_BS_DR, FRL_LINE_BS_CR, FRL_LINE_PL_DR, FRL_LINE_PL_CR, MRL_LINE_DR, "+
			"MRL_LINE_CR, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, "+
			"RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "+
			"Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,GetDate(),CONVERT(datetime, ?, 103))";
		Object[] args = { vObject.getCountry(),CalLeBook,vObject.getBsGl(), vObject.getPlGl(), vObject.getGlEnrichId(), vObject.getProductDr(),
			vObject.getProductCr(), vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(), vObject.getFrlLinePlDr(), vObject.getFrlLinePlCr(),
			vObject.getMrlLineDr(),vObject.getMrlLineCr(), vObject.getGlMapStatusNt(),vObject.getGlMapStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(GLMappingsVb vObject){
		
		 String CalLeBook = removeDescLeBook(vObject.getLeBook());
		 if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		 }
		 if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		 } 
		String query = "Update GL_MAPPINGS Set PRODUCT_DR = ?, PRODUCT_CR = ?, FRL_LINE_BS_DR = ?, "+
			"FRL_LINE_BS_CR = ?, FRL_LINE_PL_DR = ?, FRL_LINE_PL_CR = ?, MRL_LINE_DR = ?, "+
			"MRL_LINE_CR = ?, GL_MAP_STATUS_NT = ?, GL_MAP_STATUS = ?, "+
			"RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, "+
			"DATE_LAST_MODIFIED = GetDate() Where COUNTRY = ? AND LE_BOOK = ? AND BS_GL = ? AND PL_GL = ? AND GL_ENRICH_ID = ?";
		Object[] args = { vObject.getProductDr(), vObject.getProductCr(), vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(), vObject.getFrlLinePlDr(), vObject.getFrlLinePlCr(),
			vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getGlMapStatusNt(),vObject.getGlMapStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
			vObject.getCountry(),CalLeBook,vObject.getBsGl(), vObject.getPlGl(), vObject.getGlEnrichId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(GLMappingsVb vObject){
		
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		if("".equalsIgnoreCase(vObject.getMrlLineCr())){
			 vObject.setMrlLineCr("M000000");
		}
		if("".equalsIgnoreCase(vObject.getMrlLineDr())){
			 vObject.setMrlLineDr("M000000");
		} 
		String query = "Update GL_MAPPINGS_PEND Set PRODUCT_DR = ?, PRODUCT_CR = ?, FRL_LINE_BS_DR = ?, "+
			"FRL_LINE_BS_CR = ?, FRL_LINE_PL_DR = ?, FRL_LINE_PL_CR = ?, MRL_LINE_DR = ?, "+
			"MRL_LINE_CR = ?, GL_MAP_STATUS_NT = ?, GL_MAP_STATUS = ?, "+
			"RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, "+
			"DATE_LAST_MODIFIED = GetDate() Where COUNTRY = ? AND LE_BOOK = ? AND BS_GL = ? AND PL_GL = ? AND GL_ENRICH_ID = ?";
		Object[] args = { vObject.getProductDr(), vObject.getProductCr(), vObject.getFrlLineBsDr(), vObject.getFrlLineBsCr(), vObject.getFrlLinePlDr(), vObject.getFrlLinePlCr(),
			vObject.getMrlLineDr(),vObject.getMrlLineCr(),vObject.getGlMapStatusNt(),vObject.getGlMapStatus(),
			vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
			vObject.getCountry(),CalLeBook,vObject.getBsGl(), vObject.getPlGl(), vObject.getGlEnrichId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(GLMappingsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From GL_MAPPINGS Where COUNTRY = ? AND LE_BOOK = ? AND BS_GL = ? AND PL_GL = ? AND GL_ENRICH_ID = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getBsGl(),vObject.getPlGl(),vObject.getGlEnrichId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(GLMappingsVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From GL_MAPPINGS_PEND Where COUNTRY = ? AND LE_BOOK = ? AND BS_GL = ? AND PL_GL = ? AND GL_ENRICH_ID = ?";
		Object[] args = {vObject.getCountry(),CalLeBook,vObject.getBsGl(),vObject.getPlGl(),vObject.getGlEnrichId()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(GLMappingsVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		try{
			strErrMsg =  strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + ",LE_BOOK:" + CalLeBook;
			strErrMsg =  strErrMsg + ",BS_GL:" + vObject.getBsGl();
			strErrMsg =  strErrMsg + ",PL_GL:" + vObject.getPlGl();
			strErrMsg =  strErrMsg + ",GL_ENRICH_ID:" + vObject.getGlEnrichId();
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
	protected String getAuditString(GLMappingsVb vObject){
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

			if(vObject.getBsGl() != null && !vObject.getBsGl().equalsIgnoreCase(""))
				strAudit.append("BS_GL"+auditDelimiterColVal+vObject.getBsGl().trim());
			else
				strAudit.append("BS_GL"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getPlGl() != null && !vObject.getPlGl().equalsIgnoreCase(""))
				strAudit.append("PL_GL"+auditDelimiterColVal+vObject.getPlGl().trim());
			else
				strAudit.append("PL_GL"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getGlEnrichId() != null && !vObject.getGlEnrichId().equalsIgnoreCase(""))
				strAudit.append("GL_ENRICH_ID"+auditDelimiterColVal+vObject.getGlEnrichId().trim());
			else
				strAudit.append("GL_ENRICH_ID"+auditDelimiterColVal+"NULL");
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
				strAudit.append("FRL_LINE_BS_CR"+auditDelimiterColVal+vObject.getFrlLineBsCr().trim());
			else
				strAudit.append("FRL_LINE_BS_CR"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(vObject.getFrlLinePlDr() != null && !vObject.getFrlLinePlDr().equalsIgnoreCase(""))
				strAudit.append("FRL_LINE_PL_DR"+auditDelimiterColVal+vObject.getFrlLinePlDr().trim());
			else
				strAudit.append("FRL_LINE_PL_DR"+auditDelimiterColVal+"NULL");
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
			strAudit.append("GL_MAP_STATUS_NT"+auditDelimiterColVal+vObject.getGlMapStatusNt());
			strAudit.append(auditDelimiter);
			strAudit.append("GL_MAP_STATUS"+auditDelimiterColVal+vObject.getGlMapStatus());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicator());
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
		serviceName = "GLMappings";
		serviceDesc = CommonUtils.getResourceManger().getString("glMappings");
		tableName = "GL_MAPPINGS";
		childTableName = "GL_MAPPINGS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getBuildStatus(GLMappingsVb glMappingsVb){
		return getBuildModuleStatus(glMappingsVb.getCountry(), glMappingsVb.getLeBook());
	}
}