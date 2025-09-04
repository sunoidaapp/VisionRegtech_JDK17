package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.GLCodesDao;
import com.vision.util.CommonUtils;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.GLCodesVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;

@Controller
public class GLCodesWb extends AbstractDynaWorkerBean<GLCodesVb>{
	
	@Autowired
	private GLCodesDao glCodesDao;
	
	public GLCodesDao getGlCodesDao() {
		return glCodesDao;
	}
	public void setGlCodesDao(GLCodesDao glCodesDao) {
		this.glCodesDao = glCodesDao;
	}
	public static Logger logger = LoggerFactory.getLogger(GLCodesWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(51);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(71);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(72);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(73);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("GL_CODES");
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<GLCodesVb> approvedCollection, List<GLCodesVb> pendingCollection) {
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
		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("stakeHolder"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
		lResult.add(lLeBook);
		
		ReviewResultVb lVisionGl = new ReviewResultVb(rsb.getString("visionGL"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVisionGl(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVisionGl());
		lResult.add(lVisionGl);
		ReviewResultVb lGlDescription = new ReviewResultVb(rsb.getString("description"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getGlDescription(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getGlDescription());
		lResult.add(lGlDescription);
		ReviewResultVb lGlType = new ReviewResultVb(rsb.getString("glType"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>)collTemp.get(2),pendingCollection.get(0).getGlType()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>)collTemp.get(2),approvedCollection.get(0).getGlType()));
		lResult.add(lGlType); 
		ReviewResultVb lGlSignReversal = new ReviewResultVb(rsb.getString("gLSignReversal"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getGlSignReversal() == -1?"No":"Yes",
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getGlSignReversal() == -1?"No":"Yes");
		lResult.add(lGlSignReversal);
		ReviewResultVb lGlStatus = new ReviewResultVb(rsb.getString("glStatus"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getGlStatus()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getGlStatus()));
		lResult.add(lGlStatus);
		ReviewResultVb lGlStatusDate = new ReviewResultVb(rsb.getString("glStatusDate"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getGlStatusDate(),
		(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getGlStatusDate());
		lResult.add(lGlStatusDate); 
		ReviewResultVb lGlAttribute1 = new ReviewResultVb(rsb.getString("gLAttribute1"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getGlAttribute1()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getGlAttribute1()));
		lResult.add(lGlAttribute1);
		ReviewResultVb lGlAttribute2 = new ReviewResultVb(rsb.getString("gLAttribute2"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(4),pendingCollection.get(0).getGlAttribute2()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(4),approvedCollection.get(0).getGlAttribute2()));
		lResult.add(lGlAttribute2); 
		ReviewResultVb lGlAttribute3 = new ReviewResultVb(rsb.getString("gLAttribute3"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getGlAttribute3()),
			(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getGlAttribute3()));
		lResult.add(lGlAttribute3);
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
	protected void setVerifReqDeleteType(GLCodesVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("GL_CODES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected void setAtNtValues(GLCodesVb vObject){
		vObject.setRecordIndicatorNt(7);
		vObject.setGlStatusNt(1);
		vObject.setGlTypeNt(51);
		vObject.setGlAttribute1At(71);
		vObject.setGlAttribute2At(72);
		vObject.setGlAttribute3At(73);
	}
	@Override
	protected AbstractDao<GLCodesVb> getScreenDao() {
		return glCodesDao;
	}
}