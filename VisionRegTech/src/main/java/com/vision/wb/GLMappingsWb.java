package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.GLMappingsDao;
import com.vision.util.CommonUtils;
import com.vision.vb.CommonVb;
import com.vision.vb.GLMappingsVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;

@Controller
public class GLMappingsWb extends AbstractDynaWorkerBean<GLMappingsVb>{
	
	@Autowired
	private GLMappingsDao glMappingsDao;
	public GLMappingsDao getGlMappingsDao() {
		return glMappingsDao;
	}
	public void setGlMappingsDao(GLMappingsDao glMappingsDao) {
		this.glMappingsDao = glMappingsDao;
	}
	public static Logger logger = LoggerFactory.getLogger(GLMappingsWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("GL_MAPPINGS");
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	/*@Override
	protected ExceptionCode doValidate(GLMappingsVb vObject){
		ExceptionCode exceptionCode = null;
		String updateRestriction = SessionContextHolder.getContext().getUpdateRestrictionLeBook();
		updateRestriction = updateRestriction.replace("'", "");
		if(!updateRestriction.contains(vObject.getLeBook())){
			exceptionCode = CommonUtils.getResultObject(getGlMappingsDao().getServiceDesc(), Constants.WE_HAVE_ERROR_DESCRIPTION, "Add", "This Vision Account is restricted to below set off LE Books "+updateRestriction);
			exceptionCode.setOtherInfo(vObject);
		}
		return exceptionCode;
	}*/
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<GLMappingsVb> approvedCollection, List<GLMappingsVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		/*ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
		lResult.add(lCountry);*/
		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
		lResult.add(lLeBook);
		ReviewResultVb lBsGl = new ReviewResultVb(rsb.getString("bsGl"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBsGl(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBsGl());
		lResult.add(lBsGl);
		ReviewResultVb lPlGl = new ReviewResultVb(rsb.getString("plGL"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getPlGl(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getPlGl());
		lResult.add(lPlGl);
		ReviewResultVb lGlEnrichId = new ReviewResultVb(rsb.getString("glEnrichId"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getGlEnrichId(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getGlEnrichId());
		lResult.add(lGlEnrichId);   
		ReviewResultVb lFrlLineBsDr = new ReviewResultVb(rsb.getString("frlLineBSDr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLineBsDr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLineBsDr());
		lResult.add(lFrlLineBsDr); 
		ReviewResultVb lFrlLineBsCr = new ReviewResultVb(rsb.getString("frlLineBSCr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLineBsCr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLineBsCr());
		lResult.add(lFrlLineBsCr);
		ReviewResultVb lFrlLinePlDr = new ReviewResultVb(rsb.getString("frlLinePLDr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLinePlDr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLinePlDr());
		lResult.add(lFrlLinePlDr);
		ReviewResultVb lFrlLinePlCr = new ReviewResultVb(rsb.getString("frlLinePLCr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFrlLinePlCr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFrlLinePlCr());
		lResult.add(lFrlLinePlCr);
		ReviewResultVb lMrlLineDr = new ReviewResultVb(rsb.getString("MrlLineDr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMrlLineDr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMrlLineDr());
		lResult.add(lMrlLineDr);
		ReviewResultVb lMrlLineCr = new ReviewResultVb(rsb.getString("MrlLineCr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMrlLineCr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMrlLineCr());
		lResult.add(lMrlLineCr);
		ReviewResultVb lProductDr = new ReviewResultVb(rsb.getString("productDr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductDr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductDr());
		lResult.add(lProductDr);   
		ReviewResultVb lProductCr = new ReviewResultVb(rsb.getString("productCr"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProductCr(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProductCr());
		lResult.add(lProductCr); 
		/*ReviewResultVb lOucOverride = new ReviewResultVb(rsb.getString("OucOverride"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getOucOverride(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getOucOverride());
		lResult.add(lOucOverride);
		ReviewResultVb lTaxAllowable = new ReviewResultVb(rsb.getString("taxAllowable"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTaxAllowable(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTaxAllowable());
		lResult.add(lTaxAllowable);*/
		ReviewResultVb lGlMapStatus = new ReviewResultVb(rsb.getString("status"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getGlMapStatus()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getGlMapStatus()));
		lResult.add(lGlMapStatus);   
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName());
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName());
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified());
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation());
		lResult.add(lDateCreation);
		return lResult;
	}
	@Override
	protected void setVerifReqDeleteType(GLMappingsVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("GL_MAPPINGS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected void setAtNtValues(GLMappingsVb vObject){
		vObject.setRecordIndicatorNt(7);
		vObject.setGlMapStatusNt(1);
	}
	@Override
	protected AbstractDao<GLMappingsVb> getScreenDao() {
		return glMappingsDao;
	}
	/*@Override
	protected void doFormateData(GLMappingsVb vObject){
		vObject.setTaxAllowable(vObject.getTaxAllowable().replaceAll(",", ""));
	}*/
}