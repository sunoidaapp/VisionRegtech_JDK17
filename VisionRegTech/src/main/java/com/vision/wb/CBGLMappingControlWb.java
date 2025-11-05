package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CBGLMappingControlDao;
import com.vision.dao.CommonDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CBGLMappingControlVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionUsersVb;

@Component
public class CBGLMappingControlWb extends AbstractDynaWorkerBean<CBGLMappingControlVb> {

	@Autowired
	CBGLMappingControlDao cbglMappingDao;
	
	@Autowired
	private CommonDao commonDao;
	
	@Override
	protected AbstractDao<CBGLMappingControlVb> getScreenDao() {
		return cbglMappingDao;
	}

	@Override
	protected void setAtNtValues(CBGLMappingControlVb vObject) {
		    vObject.setCbGlCtrlStatusNt(1);
			vObject.setRecordIndicatorNt(7); 
			vObject.setRuleValue01AT(5012); 
			vObject.setRuleValue02AT(5013); 
			vObject.setRuleValue03AT(5014); 
			vObject.setRuleValue04AT(5015); 
			vObject.setRuleValue05AT(5016); 
			vObject.setRuleValue06AT(5017); 
			vObject.setRuleValue07AT(5018); 	
			vObject.setRuleValue08AT(5019); 
			vObject.setRuleValue09AT(5020); 
			vObject.setRuleValue10AT(5021); 
	}


	@Override
	protected void setVerifReqDeleteType(CBGLMappingControlVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("CB_GL_MAPPING_CONTROL");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	
	
	public ArrayList<Object> getPageLoadValues() {
		List<?> collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<>();
		try {	
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1); 
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7); 
			arrListLocal.add(collTemp);
			AlphaSubTabVb alphaTab = getAlphaSubTabDao().findAlphaTab(5012);
			arrListLocal.add(alphaTab);
			AlphaSubTabVb alphaTab1 = getAlphaSubTabDao().findAlphaTab(5013);
			arrListLocal.add(alphaTab1);
			AlphaSubTabVb alphaTab2 = getAlphaSubTabDao().findAlphaTab(5014);
			arrListLocal.add(alphaTab2);
			AlphaSubTabVb alphaTab3 = getAlphaSubTabDao().findAlphaTab(5015);
			arrListLocal.add(alphaTab3);
			AlphaSubTabVb alphaTab4 = getAlphaSubTabDao().findAlphaTab(5016);
			arrListLocal.add(alphaTab4);
			AlphaSubTabVb alphaTab5 = getAlphaSubTabDao().findAlphaTab(5017);
			arrListLocal.add(alphaTab5);
			AlphaSubTabVb alphaTab6 = getAlphaSubTabDao().findAlphaTab(5018);
			arrListLocal.add(alphaTab6);
			AlphaSubTabVb alphaTab7 = getAlphaSubTabDao().findAlphaTab(5019);
			arrListLocal.add(alphaTab7);
			AlphaSubTabVb alphaTab8 = getAlphaSubTabDao().findAlphaTab(5020);
			arrListLocal.add(alphaTab8);
			AlphaSubTabVb alphaTab9 = getAlphaSubTabDao().findAlphaTab(5021);
			arrListLocal.add(alphaTab9);		
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("CB_GL_MAPPING_CONTROL");
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
			CBGLMappingControlVb vObject = new CBGLMappingControlVb();
			setAtNtValues(vObject);
			arrListLocal.add(vObject);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public ExceptionCode generateRuleId() {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String ruleId = cbglMappingDao.getRuleId();
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(ruleId);
			exceptionCode.setErrorMsg("sucess");
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return exceptionCode;
	}
	
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<CBGLMappingControlVb> approvedCollection,List<CBGLMappingControlVb> pendingCollection) {
		ArrayList<Object> collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<>();
		
		ReviewResultVb lCbGlCodeDesc= new ReviewResultVb(rsb.getString("cbGlCodeDesc"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCbGlCodeDesc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbGlCodeDesc(),
				(!pendingCollection.get(0).getCbGlCodeDesc()
						.equals(approvedCollection.get(0).getCbGlCodeDesc())));
		lResult.add(lCbGlCodeDesc);
		
		ReviewResultVb lruleId= new ReviewResultVb(rsb.getString("ruleId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getRuleId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getRuleId(),
				(!pendingCollection.get(0).getRuleId()
						.equals(approvedCollection.get(0).getRuleId())));
		lResult.add(lruleId);
		
		ReviewResultVb lnre = new ReviewResultVb(rsb.getString("nre"),
			    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5012, pendingCollection.get(0).getRuleValue01()),
			    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5012, approvedCollection.get(0).getRuleValue01()),
			    (!pendingCollection.get(0).getRuleValue01().equalsIgnoreCase(approvedCollection.get(0).getRuleValue01())));
		lResult.add(lnre);
		
		ReviewResultVb lnreresidency = new ReviewResultVb(rsb.getString("residency"),
			    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5013, pendingCollection.get(0).getRuleValue02()),
			    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5013, approvedCollection.get(0).getRuleValue02()),
			    (!pendingCollection.get(0).getRuleValue02().equalsIgnoreCase(approvedCollection.get(0).getRuleValue02())));
		lResult.add(lnreresidency);
		
		ReviewResultVb lsna = new ReviewResultVb(rsb.getString("sna"),
			    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5014, pendingCollection.get(0).getRuleValue03()),
			    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
			        : cbglMappingDao.getAlphaSubTabDescription(5014, approvedCollection.get(0).getRuleValue03()),
			    (!pendingCollection.get(0).getRuleValue03().equalsIgnoreCase(approvedCollection.get(0).getRuleValue03())));
		lResult.add(lsna);
		
		ReviewResultVb ldrBalInd = new ReviewResultVb(rsb.getString("drBalInd"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5015, pendingCollection.get(0).getRuleValue04()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5015, approvedCollection.get(0).getRuleValue04()),
		    (!pendingCollection.get(0).getRuleValue04().equalsIgnoreCase(approvedCollection.get(0).getRuleValue04())));
		lResult.add(ldrBalInd);

		ReviewResultVb lnotDefined = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5016, pendingCollection.get(0).getRuleValue05()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5016, approvedCollection.get(0).getRuleValue05()),
		    (!pendingCollection.get(0).getRuleValue05().equalsIgnoreCase(approvedCollection.get(0).getRuleValue05())));
		lResult.add(lnotDefined);

		ReviewResultVb lnotDefined2 = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5017, pendingCollection.get(0).getRuleValue06()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5017, approvedCollection.get(0).getRuleValue06()),
		    (!pendingCollection.get(0).getRuleValue06().equalsIgnoreCase(approvedCollection.get(0).getRuleValue06())));
		lResult.add(lnotDefined2);

		ReviewResultVb lnotDefined3 = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5018, pendingCollection.get(0).getRuleValue07()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5018, approvedCollection.get(0).getRuleValue07()),
		    (!pendingCollection.get(0).getRuleValue07().equalsIgnoreCase(approvedCollection.get(0).getRuleValue07())));
		lResult.add(lnotDefined3);

		ReviewResultVb lnotDefined4 = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5019, pendingCollection.get(0).getRuleValue08()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5019, approvedCollection.get(0).getRuleValue08()),
		    (!pendingCollection.get(0).getRuleValue08().equalsIgnoreCase(approvedCollection.get(0).getRuleValue08())));
		lResult.add(lnotDefined4);

		ReviewResultVb lnotDefined5 = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5020, pendingCollection.get(0).getRuleValue09()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5020, approvedCollection.get(0).getRuleValue09()),
		    (!pendingCollection.get(0).getRuleValue09().equalsIgnoreCase(approvedCollection.get(0).getRuleValue09())));
		lResult.add(lnotDefined5);

		ReviewResultVb lnotDefined6 = new ReviewResultVb(rsb.getString("notDefined"),
		    (pendingCollection == null || pendingCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5021, pendingCollection.get(0).getRuleValue10()),
		    (approvedCollection == null || approvedCollection.isEmpty()) ? ""
		        : cbglMappingDao.getAlphaSubTabDescription(5021, approvedCollection.get(0).getRuleValue10()),
		    (!pendingCollection.get(0).getRuleValue10().equalsIgnoreCase(approvedCollection.get(0).getRuleValue10())));
		lResult.add(lnotDefined6);
			
		ReviewResultVb lCbGlCode= new ReviewResultVb(rsb.getString("cbGlCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCbGlCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbGlCode(),
				(!pendingCollection.get(0).getCbGlCode()
						.equals(approvedCollection.get(0).getCbGlCode())));
		lResult.add(lCbGlCode);
		
		ReviewResultVb lcbglControl = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getCbGlCtrlStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getCbGlCtrlStatus()),
				(pendingCollection.get(0).getCbGlCtrlStatus() != approvedCollection.get(0).getCbGlCtrlStatus()));
		lResult.add(lcbglControl);


    	ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								approvedCollection.get(0).getRecordIndicator()),
				(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);

		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
				(!pendingCollection.get(0).getMakerName().equals(approvedCollection.get(0).getMakerName())));
		lResult.add(lMaker);
		
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(!pendingCollection.get(0).getVerifierName().equals(approvedCollection.get(0).getVerifierName())));
		lResult.add(lVerifier);

		ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateLastModified(),
				(!pendingCollection.get(0).getDateLastModified()
						.equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);

		ReviewResultVb lDateCreation = new ReviewResultVb(rsb.getString("dateCreation"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateCreation(),
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		
		return lResult;
	}
	
	
	public ExceptionCode getQueryResults(CBGLMappingControlVb vObject) {
			List<CBGLMappingControlVb> collTemp = cbglMappingDao.getQueryResults(vObject, Constants.SUCCESSFUL_OPERATION);
		if (collTemp.size() == 0) {
			collTemp = cbglMappingDao.getQueryResults(vObject, Constants.STATUS_ZERO);
		}
		if (collTemp.size() == 0) {
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			doSetDesctiptionsAfterQuery((CBGLMappingControlVb) ((ArrayList<CBGLMappingControlVb>) collTemp).get(0));
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames((CBGLMappingControlVb) ((ArrayList<CBGLMappingControlVb>) collTemp).get(0));
			exceptionCode.setOtherInfo(((ArrayList<CBGLMappingControlVb>) collTemp).get(0));
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}

}
