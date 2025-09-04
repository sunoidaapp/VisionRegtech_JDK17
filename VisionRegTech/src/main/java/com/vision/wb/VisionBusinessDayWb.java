package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.VisionBusinessDayDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionBusinessDayVb;

@Controller
public class VisionBusinessDayWb extends AbstractDynaWorkerBean<VisionBusinessDayVb>{

	@Autowired
	private VisionBusinessDayDao visionBusinessDayDao; 
	
	@Override
	protected AbstractDao<VisionBusinessDayVb> getScreenDao() {
		return visionBusinessDayDao;
	}
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected void setAtNtValues(VisionBusinessDayVb vObject) {
		vObject.setVisionBusinessDayStatusNt(1);
		vObject.setRecordIndicatorNt(7);
	}

	@Override
	protected void setVerifReqDeleteType(VisionBusinessDayVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("VISION_BUSINESS_DAY");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());

	}

	public VisionBusinessDayDao getVisionBusinessDayDao() {
		return visionBusinessDayDao;
	}

	public void setVisionBusinessDayDao(VisionBusinessDayDao visionBusinessDayDao) {
		this.visionBusinessDayDao = visionBusinessDayDao;
	}
	@Override
	public ExceptionCode getQueryResults(VisionBusinessDayVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<VisionBusinessDayVb> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 41, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<VisionBusinessDayVb> approvedCollection, List<VisionBusinessDayVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
        ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
        ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
        lResult.add(lCountry);
        
        ReviewResultVb lStakeHolder = new ReviewResultVb(rsb.getString("stakeHolder"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
        lResult.add(lStakeHolder);
        
        ReviewResultVb lbusinessDate = new ReviewResultVb(rsb.getString("businessDate"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessDate(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessDate());
        lResult.add(lbusinessDate);
        
        ReviewResultVb lyearMonth = new ReviewResultVb(rsb.getString("yearMonth"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessYearMonth(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessYearMonth());
        lResult.add(lyearMonth);
        
        ReviewResultVb businessWeeklyDate = new ReviewResultVb(rsb.getString("businessWeeklyDate"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessWeeklyDate(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessWeeklyDate());
        lResult.add(businessWeeklyDate);
        
        ReviewResultVb businessQtrYearMonth = new ReviewResultVb(rsb.getString("businessQtrYearMonth"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessQtrYearMonth(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessQtrYearMonth());
        lResult.add(businessQtrYearMonth);        
        ReviewResultVb businessHyrYearMonth = new ReviewResultVb(rsb.getString("businessHyrYearMonth"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessHalfYearMonth(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessHalfYearMonth());
        lResult.add(businessHyrYearMonth);
        ReviewResultVb businessYear = new ReviewResultVb(rsb.getString("businessYear"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getBusinessYear(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getBusinessYear());
        lResult.add(businessYear);
        ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
                    (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()));
        lResult.add(lRecordIndicator);
        ReviewResultVb lVisionBusinessDayStatus = new ReviewResultVb(rsb.getString("status"),
        		((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVisionBusinessDayStatus()).toString(),
                ((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVisionBusinessDayStatus()).toString());
        lResult.add(lVisionBusinessDayStatus);
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

}
