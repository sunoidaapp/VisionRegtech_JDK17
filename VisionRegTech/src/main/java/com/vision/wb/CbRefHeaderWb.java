package com.vision.wb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CbRefDetailsDao;
import com.vision.dao.CbRefHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.CbRefDetailsVb;
import com.vision.vb.CbRefHeaderVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionUsersVb;
@Controller
public class CbRefHeaderWb extends AbstractDynaWorkerBean<CbRefHeaderVb>{

	@Autowired 
	private CbRefHeaderDao cbrefheaderDao;
	
	@Autowired
	private CbRefDetailsDao cbRefDetailsDao;
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5001);// Attribute 1
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5002);// Attribute 2
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(5003);// Attribute 3
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("CB_REF_HEADER");
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().getLegalEntity();
			arrListLocal.add(collTemp);
			String country = "";
			String leBook = "";
			VisionUsersVb visionUsers = SessionContextHolder.getContext();
			if("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
				if(ValidationUtil.isValid(visionUsers.getCountry())) {
					country = visionUsers.getCountry();
				}
				if(ValidationUtil.isValid(visionUsers.getLeBook())) {
					leBook = visionUsers.getLeBook();
				}
			}else {
				country = getCommonDao().findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = getCommonDao().findVisionVariableValue("DEFAULT_LE_BOOK");
			}
			String countryLeBook = country + "-" + leBook;
			arrListLocal.add(countryLeBook);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	
	public ExceptionCode getQueryResults(CbRefHeaderVb vObject){
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<CbRefHeaderVb> collTemp = getScreenDao().getQueryResults(vObject,intStatus);
		if (collTemp != null && collTemp.isEmpty()) {
			intStatus = 0;
			collTemp = getScreenDao().getQueryResults(vObject, intStatus);
		}
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{

			CbRefHeaderVb cbRefHeaderVb = collTemp.get(0);
			cbRefHeaderVb.setVerificationRequired(vObject.isVerificationRequired());
			
			CbRefDetailsVb cbRefDetailsVb = new CbRefDetailsVb();
			
			cbRefDetailsVb.setCountry(vObject.getCountry());
			cbRefDetailsVb.setLeBook(vObject.getLeBook());
			cbRefDetailsVb.setRefCode(vObject.getRefCode());
			cbRefDetailsVb.setCurrentPage(vObject.getCurrentPage());
			
			List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryResultsAllByParent(vObject);
			cbRefHeaderVb.setCbRefDetailsList(cbRefDetailsList);
			
//			List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryPopupResults(cbRefDetailsVb);
//			cbRefHeaderVb.setCbRefDetailsList(cbRefDetailsList);
			doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<CbRefHeaderVb> approvedCollection, List<CbRefHeaderVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

//		ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
//			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//		lResult.add(lCountry);
//
//		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
//			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
//			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//		lResult.add(lLeBook);

		ReviewResultVb lRefCode = new ReviewResultVb(rsb.getString("refCode"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefCode(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefCode(),(!pendingCollection.get(0).getRefCode().equals(approvedCollection.get(0).getRefCode())));
		lResult.add(lRefCode);

		ReviewResultVb lRefDescription = new ReviewResultVb(rsb.getString("description"),
			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefDescription(),
			(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefDescription(),(!pendingCollection.get(0).getRefDescription().equals(approvedCollection.get(0).getRefDescription())));
		lResult.add(lRefDescription);

		ReviewResultVb lFtpGrpStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStatusDesc(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStatusDesc(),(!pendingCollection.get(0).getStatusDesc().equals(approvedCollection.get(0).getStatusDesc())));
			lResult.add(lFtpGrpStatus);

			ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRecordIndicatorDesc(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicatorDesc(),(!pendingCollection.get(0).getRecordIndicatorDesc().equals(approvedCollection.get(0).getRecordIndicatorDesc())));
				lResult.add(lRecordIndicator);
				
			ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMakerName(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMakerName(),(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
			lResult.add(lMaker);
			
			ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifierName(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifierName(),(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
			lResult.add(lVerifier);				

			ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
			lResult.add(lDateLastModified);
			ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
				lResult.add(lDateCreation);
			return lResult; 
			

	}			
	public CbRefHeaderDao getCbrefheaderDao() {
			return cbrefheaderDao;
		}
		
	public void setCbrefheaderDao(CbRefHeaderDao cbrefheaderDao) {
		this.cbrefheaderDao = cbrefheaderDao;
	}

	@Override
	protected AbstractDao<CbRefHeaderVb> getScreenDao() {
		return cbrefheaderDao;
	}

	@Override
	protected void setAtNtValues(CbRefHeaderVb vObject) {
	}

	@Override
	protected void setVerifReqDeleteType(CbRefHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("CB_REF_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	public CbRefDetailsDao getCbRefDetailsDao() {
		return cbRefDetailsDao;
	}

	public void setCbRefDetailsDao(CbRefDetailsDao cbRefDetailsDao) {
		this.cbRefDetailsDao = cbRefDetailsDao;
	}
	
	public ExceptionCode modifyRecord(CbRefHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<CbRefHeaderVb> deepCopy = new DeepCopy<CbRefHeaderVb>();
		CbRefHeaderVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doUpdateApprRecord(vObject);
			}else{
				exceptionCode = getScreenDao().doUpdateRecord(vObject);
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Modify Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	public ExceptionCode deleteRecord(CbRefHeaderVb vObject){
		ExceptionCode exceptionCode  = null;
		DeepCopy<CbRefHeaderVb> deepCopy = new DeepCopy<CbRefHeaderVb>();
		CbRefHeaderVb clonedObject = null;
		try{
			setAtNtValues(vObject);
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copy(vObject);
			doFormateData(vObject);
			exceptionCode = doValidate(vObject);
			if(exceptionCode!=null && exceptionCode.getErrorMsg()!=""){
				return exceptionCode;
			}
			if(!vObject.isVerificationRequired()){
				exceptionCode = getScreenDao().doDeleteApprRecord(vObject);
			}else{
				exceptionCode = getScreenDao().doDeleteRecord(vObject);
			}
			getScreenDao().fetchMakerVerifierNames(vObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Delete Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	
	public ExceptionCode deleteDetails(List<CbRefDetailsVb> vObjects){
		ExceptionCode exceptionCode  = null;
		DeepCopy<List<CbRefDetailsVb>> deepCopy = new DeepCopy<List<CbRefDetailsVb>>();
		List<CbRefDetailsVb> clonedObject = null;
		CbRefHeaderVb cbRefHeaderVb = new CbRefHeaderVb();
		CbRefDetailsVb vObject = new CbRefDetailsVb();
		try{
			setVerifReqDeleteType(cbRefHeaderVb);
			clonedObject = deepCopy.copy(vObjects);
//			doFormateData(vObject);
//			exceptionCode = doValidate(vObject);
			
			vObject.setVerificationRequired(cbRefHeaderVb.isVerificationRequired());
			vObject.setStaticDelete(cbRefHeaderVb.isStaticDelete());
			
			if(!vObject.isVerificationRequired()){
				exceptionCode = cbRefDetailsDao.doDeleteApprRecord(vObjects, vObject);
			}else{
				exceptionCode = cbRefDetailsDao.doDeleteRecord(vObjects, vObject);
			}
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Delete Exception " + rex.getCode().getErrorMsg());
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(clonedObject);
			return exceptionCode;
		}
	}
	
	public ArrayList reviewRecord(CbRefHeaderVb vObject){
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			List<CbRefHeaderVb> approvedGroupCollection = cbrefheaderDao.getQueryResults(vObject,0);
			List<CbRefHeaderVb> pendingGroupCollection = cbrefheaderDao.getQueryResults(vObject,1);
			List<ReviewResultVb> groupReviewList =  transformToReviewResults(approvedGroupCollection,pendingGroupCollection);
			arrListLocal.add(groupReviewList);
			
			List<CbRefDetailsVb> detailsPendingList = cbRefDetailsDao.getQueryResultsByParent(vObject, 1);
			Map<String, ArrayList<Object>> childList = new HashMap<String, ArrayList<Object>>();
			ArrayList<Object> childCurvesListAll = new ArrayList<Object>();
			if(ValidationUtil.isValid(detailsPendingList)) {
				for(CbRefDetailsVb detailsVb : detailsPendingList) {
					if(detailsVb.getRecordIndicator() == 1) {
						List<CbRefDetailsVb> approvedMethodCollection = cbRefDetailsDao.getQueryResults(detailsVb,0);
						List<CbRefDetailsVb> pendingMethodCollection = cbRefDetailsDao.getQueryResults(detailsVb,1);
						List<ReviewResultVb> detailsConfigResult =  transformToReviewResultsDetails(approvedMethodCollection,pendingMethodCollection);
						childCurvesListAll.add(detailsConfigResult);
					}
				}
//				childList.put("DETAILS", childCurvesListAll);
			}
			arrListLocal.add(childCurvesListAll);
			
			
			return arrListLocal;
		}catch(Exception ex){
			return null;
		}
	}
	
	protected List<ReviewResultVb> transformToReviewResultsDetails(List<CbRefDetailsVb> approvedCollection, List<CbRefDetailsVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

//				ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry(),(!pendingCollection.get(0).getCountry().equals(approvedCollection.get(0).getCountry())));
//				lResult.add(lCountry);
//
//				ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
//					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
//					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),(!pendingCollection.get(0).getLeBook().equals(approvedCollection.get(0).getLeBook())));
//				lResult.add(lLeBook);

				ReviewResultVb lRefCode = new ReviewResultVb(rsb.getString("refCode"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefCode(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefCode(),(!pendingCollection.get(0).getRefCode().equals(approvedCollection.get(0).getRefCode())));
				lResult.add(lRefCode);

				ReviewResultVb lRefKey = new ReviewResultVb(rsb.getString("refKey"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefKey(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefKey(),(!pendingCollection.get(0).getRefKey().equals(approvedCollection.get(0).getRefKey())));
				lResult.add(lRefKey);

				ReviewResultVb lRefKeyDescription = new ReviewResultVb(rsb.getString("description"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefKeyDescription(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefKeyDescription(),(!pendingCollection.get(0).getRefKeyDescription().equals(approvedCollection.get(0).getRefKeyDescription())));
				lResult.add(lRefKeyDescription);

				ReviewResultVb lRefKeyType = new ReviewResultVb(rsb.getString("refKeyType"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefKeyType(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefKeyType(),(!pendingCollection.get(0).getRefKeyType().equals(approvedCollection.get(0).getRefKeyType())));
				lResult.add(lRefKeyType);

				ReviewResultVb lRefParentKey = new ReviewResultVb(rsb.getString("refParentKey"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefParentKey(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefParentKey(),(!pendingCollection.get(0).getRefParentKey().equals(approvedCollection.get(0).getRefParentKey())));
				lResult.add(lRefParentKey);

				ReviewResultVb lRefTranslatedKey = new ReviewResultVb(rsb.getString("refTranslatedKey"),
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefTranslatedKey(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefTranslatedKey(),(!pendingCollection.get(0).getRefTranslatedKey().equals(approvedCollection.get(0).getRefTranslatedKey())));
				lResult.add(lRefTranslatedKey);


				ReviewResultVb lRefAttribute01 = new ReviewResultVb(rsb.getString("attribute")+" 01",
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefAttribute01Desc(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefAttribute01Desc(),(!pendingCollection.get(0).getRefAttribute01Desc().equals(approvedCollection.get(0).getRefAttribute01Desc())));
				lResult.add(lRefAttribute01);


				ReviewResultVb lRefAttribute02 = new ReviewResultVb(rsb.getString("attribute")+" 02",
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefAttribute02Desc(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefAttribute02Desc(),(!pendingCollection.get(0).getRefAttribute02Desc().equals(approvedCollection.get(0).getRefAttribute02Desc())));
				lResult.add(lRefAttribute02);


				ReviewResultVb lRefAttribute03 = new ReviewResultVb(rsb.getString("attribute")+" 03",
					(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRefAttribute03Desc(),
					(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRefAttribute03Desc(),(!pendingCollection.get(0).getRefAttribute03Desc().equals(approvedCollection.get(0).getRefAttribute03Desc())));
				lResult.add(lRefAttribute03);

				ReviewResultVb lFtpGrpStatus = new ReviewResultVb(rsb.getString("status"),
						(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStatusDesc(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStatusDesc(),(!pendingCollection.get(0).getStatusDesc().equals(approvedCollection.get(0).getStatusDesc())));
					lResult.add(lFtpGrpStatus);

					ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
							(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getRecordIndicatorDesc(),
							(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicatorDesc(),(!pendingCollection.get(0).getRecordIndicatorDesc().equals(approvedCollection.get(0).getRecordIndicatorDesc())));
						lResult.add(lRecordIndicator);
						
					ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
							(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMakerName(),
							(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMakerName(),(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
					lResult.add(lMaker);
					
					ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
							(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifierName(),
							(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifierName(),(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
					lResult.add(lVerifier);				

					ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),(!pendingCollection.get(0).getDateLastModified().equals(approvedCollection.get(0).getDateLastModified())));
					lResult.add(lDateLastModified);
					ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
						(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation(),(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
						lResult.add(lDateCreation);

				return lResult; 
				

	}
	public ExceptionCode rejectDetails(List<CbRefDetailsVb> vObjects,CbRefDetailsVb queryPopObj){
		ExceptionCode exceptionCode  = null;
		DeepCopy<CbRefDetailsVb> deepCopy = new DeepCopy<CbRefDetailsVb>();
		List<CbRefDetailsVb> clonedObjects = null;
		CbRefHeaderVb cbRefHeaderVb = new CbRefHeaderVb();
		try{
			setVerifReqDeleteType(cbRefHeaderVb);
			clonedObjects = deepCopy.copyCollection(vObjects);
			
			queryPopObj.setVerificationRequired(cbRefHeaderVb.isVerificationRequired());
			queryPopObj.setStaticDelete(cbRefHeaderVb.isStaticDelete());
			
			exceptionCode =  cbRefDetailsDao.doBulkReject(vObjects);
			exceptionCode.setOtherInfo(queryPopObj);
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Bulk Reject Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(queryPopObj);
			exceptionCode.setResponse(clonedObjects);
			return exceptionCode;
		}
	}	
	public List<ReviewResultVb> reviewRecordDetails(CbRefDetailsVb vObject){
		try{
			List<CbRefDetailsVb> approvedCollection = cbRefDetailsDao.getQueryResults(vObject,0);
			List<CbRefDetailsVb> pendingCollection = cbRefDetailsDao.getQueryResults(vObject,1);
			return transformToReviewResultsDetails(approvedCollection,pendingCollection);
		}catch(Exception ex){
			return null;
		}
	}	

}