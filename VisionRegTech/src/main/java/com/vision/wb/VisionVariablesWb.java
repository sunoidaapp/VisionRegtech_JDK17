/**
 * 
 */
package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.VisionVariablesDao;
import com.vision.util.CommonUtils;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionVariablesVb;

@Controller
public class VisionVariablesWb extends AbstractDynaWorkerBean<VisionVariablesVb>{

	@Autowired
	private VisionVariablesDao visionVariablesDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	
	public static Logger logger = LoggerFactory.getLogger(VisionVariablesWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("VISION_VARIABLES");
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<VisionVariablesVb> approvedCollection, List<VisionVariablesVb> pendingCollection) {
		ResourceBundle rsb=CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lVariable = new ReviewResultVb(rsb.getString("variable"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVariable(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVariable());
		lResult.add(lVariable);
		ReviewResultVb lValue = new ReviewResultVb(rsb.getString("variableValue"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getValue(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getValue());
		lResult.add(lValue);
		ReviewResultVb lVariableStatus = new ReviewResultVb(rsb.getString("variableStatus"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0), pendingCollection.get(0).getVariableStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVariableStatus() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0), approvedCollection.get(0).getVariableStatus()));
		lResult.add(lVariableStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getRecordIndicator() == -1?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1), approvedCollection.get(0).getRecordIndicator()));
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
	protected void setVerifReqDeleteType(VisionVariablesVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("VISION_VARIABLES");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
		if("Query".equalsIgnoreCase(vObject.getActionType())) {
			setFiterQueryValue(vObject);
		}
	}
	public void setFiterQueryValue(VisionVariablesVb dObj) {
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
//				String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
				String val = data.getValue();
				int intVal = 0;
				if("variableStatus".equalsIgnoreCase(data.getObject()) || "recordIndicator".equalsIgnoreCase(data.getObject())) {
					try {
						intVal = Integer.parseInt(val);
					}catch(Exception e){
						data.setObject("");
					}
				}
				switch (data.getObject()) {
				case "variable":
					dObj.setVariable(val);
					break;
				case "value":
					dObj.setValue(val);
					break;
				case "variableStatus":
					dObj.setVariableStatus(intVal);
					break;
				case "recordIndicator":
					dObj.setRecordIndicator(intVal);
					break;	
				default:
				}
				count++;
			}
		}
	}
	public VisionVariablesDao getVisionVariablesDao() {
		return visionVariablesDao;
	}
	public void setVisionVariablesDao(VisionVariablesDao visionVariablesDao) {
		this.visionVariablesDao = visionVariablesDao;
	}
	@Override
	protected void setAtNtValues(VisionVariablesVb vObject){
		vObject.setRecordIndicatorNt(7);
		vObject.setVariableStatusNt(1);
	}
	@Override
	protected AbstractDao<VisionVariablesVb> getScreenDao() {
		return visionVariablesDao;
	}
}