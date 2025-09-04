package com.vision.wb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.dao.AbstractDao;
import com.vision.dao.DataAcquisitionDynamicDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.DataAcquisitionDynamicVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;

@Component
public class DataAcquisitionDynamicWb extends AbstractDynaWorkerBean<DataAcquisitionDynamicVb>{
	private final String CBD="CBD";	
	@Autowired
	private DataAcquisitionDynamicDao dataAcquisitionDynamicDao;
	
	
	public DataAcquisitionDynamicDao getDataAcquisitionDynamicDao() {
		return dataAcquisitionDynamicDao;
	}
	public void setDataAcquisitionDynamicDao(
			DataAcquisitionDynamicDao dataAcquisitionDynamicDao) {
		this.dataAcquisitionDynamicDao = dataAcquisitionDynamicDao;
	}
	public static Logger logger = LoggerFactory.getLogger(DataAcquisitionDynamicWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);//Status
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);//Record Indicator
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1079);//Acquisition_Process_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1081);//Connectivity_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1082);//DataBase_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1083);//Source_Script_Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1077);//Frequency_Process
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("ADF_DATA_ACQUISITION");
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(206);//Build_Schedule_Status
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1201);//Mandatory Flag [ADF]
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(2032);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<DataAcquisitionDynamicVb> approvedCollection, List<DataAcquisitionDynamicVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if(pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
        ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
        ReviewResultVb lStakeHolder = new ReviewResultVb(rsb.getString("stakeHolder"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook(),
                		(!pendingCollection.get(0).getLeBook()
								.equals(approvedCollection.get(0).getLeBook())));
        lResult.add(lStakeHolder);
        ReviewResultVb lTemplateName = new ReviewResultVb(rsb.getString("templateName"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTemplateName(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTemplateName(),
                		(!pendingCollection.get(0).getTemplateName()
								.equals(approvedCollection.get(0).getTemplateName())));
        lResult.add(lTemplateName);
        ReviewResultVb ltemplateDesc = new ReviewResultVb(rsb.getString("description"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTemplateDesc(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTemplateDesc(),
                		(!pendingCollection.get(0).getTemplateDesc()
								.equals(approvedCollection.get(0).getTemplateDesc())));
        lResult.add(ltemplateDesc);
        ReviewResultVb lfeedStgName = new ReviewResultVb(rsb.getString("stagingName"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFeedStgName(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFeedStgName(),
                		(!pendingCollection.get(0).getFeedStgName()
								.equals(approvedCollection.get(0).getFeedStgName())));
        lResult.add(lfeedStgName);
        ReviewResultVb lfilePattern = new ReviewResultVb(rsb.getString("filePattern"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getFilePattern(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getFilePattern(),
                		(!pendingCollection.get(0).getFilePattern()
								.equals(approvedCollection.get(0).getFilePattern())));
        lResult.add(lfilePattern);
        ReviewResultVb lexcelFilePattern = new ReviewResultVb(rsb.getString("excelFilePattern"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExcelFilePattern(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getExcelFilePattern(),
                		(!pendingCollection.get(0).getExcelFilePattern()
								.equals(approvedCollection.get(0).getExcelFilePattern())));
        lResult.add(lexcelFilePattern);
        ReviewResultVb lexcelTemplateId = new ReviewResultVb(rsb.getString("excelTemplateId"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getExcelTemplateId(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getExcelTemplateId(),
                		(!pendingCollection.get(0).getExcelTemplateId()
								.equals(approvedCollection.get(0).getExcelTemplateId())));
        lResult.add(lexcelTemplateId);
        ReviewResultVb lacquisitionProcessType = new ReviewResultVb(rsb.getString("acquisitionProcessType"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),pendingCollection.get(0).getAcquisitionProcessType()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),approvedCollection.get(0).getAcquisitionProcessType()),
						(!pendingCollection.get(0).getAcquisitionProcessType()
								.equals(approvedCollection.get(0).getAcquisitionProcessType())));
		lResult.add(lacquisitionProcessType);
        ReviewResultVb lconnectivityType = new ReviewResultVb(rsb.getString("connectivitytype"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getConnectivityType()),    
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getConnectivityType()),
        				(!pendingCollection.get(0).getConnectivityType()
								.equals(approvedCollection.get(0).getConnectivityType())));
        lResult.add(lconnectivityType);
        ReviewResultVb lconnectivityDetails = new ReviewResultVb(rsb.getString("connectivityDetails"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getConnectivityDetails(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getConnectivityDetails(),
                		(!pendingCollection.get(0).getConnectivityDetails()
								.equals(approvedCollection.get(0).getConnectivityDetails())));
        lResult.add(lconnectivityDetails);
        ReviewResultVb ldatabaseType = new ReviewResultVb(rsb.getString("databaseType"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(4),pendingCollection.get(0).getDatabaseType()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(4),approvedCollection.get(0).getDatabaseType()),
        				(!pendingCollection.get(0).getDatabaseType()
								.equals(approvedCollection.get(0).getDatabaseType())));
        lResult.add(ldatabaseType);
        ReviewResultVb ldatabaseConnectivityDetails = new ReviewResultVb(rsb.getString("databaseConnectivityDetails"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDatabaseConnectivityDetails(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDatabaseConnectivityDetails(),
                		(!pendingCollection.get(0).getDatabaseConnectivityDetails()
								.equals(approvedCollection.get(0).getDatabaseConnectivityDetails())));
        lResult.add(ldatabaseConnectivityDetails);
        ReviewResultVb lserverFolderDetails = new ReviewResultVb(rsb.getString("serverFolderDetails"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getServerFolderDetails(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getServerFolderDetails(),
                		(!pendingCollection.get(0).getServerFolderDetails()
								.equals(approvedCollection.get(0).getServerFolderDetails())));
        lResult.add(lserverFolderDetails);
        ReviewResultVb lsourceScriptType = new ReviewResultVb(rsb.getString("sourceScriptType"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getSourceScriptType()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getSourceScriptType()),
        				(!pendingCollection.get(0).getSourceScriptType()
								.equals(approvedCollection.get(0).getSourceScriptType())));
        lResult.add(lsourceScriptType);
        ReviewResultVb lsourceServerScript = new ReviewResultVb(rsb.getString("sourceServerScript"),
        		(String)((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getSourceServerScript()),
                (String)((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getSourceServerScript()),
                (!pendingCollection.get(0).getSourceServerScript()
						.equals(approvedCollection.get(0).getSourceServerScript())));
        lResult.add(lsourceServerScript);
        ReviewResultVb ltargetScriptType = new ReviewResultVb(rsb.getString("targetScriptType"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getTargetScriptType()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getTargetScriptType()),
        				(!pendingCollection.get(0).getTargetScriptType()
								.equals(approvedCollection.get(0).getTargetScriptType())));
        lResult.add(ltargetScriptType);
        ReviewResultVb ltargetServerScript = new ReviewResultVb(rsb.getString("targetServerScript"),
        		(String)((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getTargetServerScript()),
        		(String)((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getTargetServerScript()),
        		(!pendingCollection.get(0).getTargetServerScript()
						.equals(approvedCollection.get(0).getTargetServerScript())));
        lResult.add(ltargetServerScript);
        ReviewResultVb lreadinessScriptType = new ReviewResultVb(rsb.getString("readinessScriptsType"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getReadinessScriptType()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getReadinessScriptType()),
        				(!pendingCollection.get(0).getReadinessScriptType()
								.equals(approvedCollection.get(0).getReadinessScriptType())));
        lResult.add(lreadinessScriptType);
        ReviewResultVb lacquisitionReadinessScripts = new ReviewResultVb(rsb.getString("acquisitionReadinessScripts"),
        		(String)((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAcquisitionReadinessScripts()),
                (String)((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAcquisitionReadinessScripts()),
                (!pendingCollection.get(0).getAcquisitionReadinessScripts()
						.equals(approvedCollection.get(0).getAcquisitionReadinessScripts())));
        lResult.add(lacquisitionReadinessScripts);
        ReviewResultVb lpreactivityScriptType = new ReviewResultVb(rsb.getString("preactivityScriptType"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getPreactivityScriptType()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getPreactivityScriptType()),
                		(!pendingCollection.get(0).getPreactivityScriptType()
								.equals(approvedCollection.get(0).getPreactivityScriptType())));
        lResult.add(lpreactivityScriptType);
        ReviewResultVb lpreactivityScripts = new ReviewResultVb(rsb.getString("preactivityScripts"),
        		(String)((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getPreactivityScripts()),
                (String)((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getPreactivityScripts()),
                (!pendingCollection.get(0).getPreactivityScripts()
						.equals(approvedCollection.get(0).getPreactivityScripts())));
        lResult.add(lpreactivityScripts);
        ReviewResultVb lintegrityScriptName = new ReviewResultVb(rsb.getString("integrityScriptName"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getIntegrityScriptName(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getIntegrityScriptName(),
                		(!pendingCollection.get(0).getIntegrityScriptName()
								.equals(approvedCollection.get(0).getIntegrityScriptName())));
        lResult.add(lintegrityScriptName);
        ReviewResultVb lfrequencyProcess = new ReviewResultVb(rsb.getString("processFrequency"),
		(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(6),pendingCollection.get(0).getFrequencyProcess()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(6),approvedCollection.get(0).getFrequencyProcess()),
                		(!pendingCollection.get(0).getFrequencyProcess()
								.equals(approvedCollection.get(0).getFrequencyProcess())));
        lResult.add(lfrequencyProcess);
        ReviewResultVb lprocessSequence = new ReviewResultVb(rsb.getString("processSequence"),
        		((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getProcessSequence()).toString(),
                ((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getProcessSequence()).toString(),
                (!pendingCollection.get(0).getProcessSequence()
						.equals(approvedCollection.get(0).getProcessSequence())));
        lResult.add(lprocessSequence);
        
        String startTimeValuePend="";//StartTimePending
		String startTimeValue="";//StartTimeApproved
		if(ValidationUtil.isValid(pendingCollection.get(0).getAdfStartDay())){
			int startDay=Integer.parseInt(pendingCollection.get(0).getAdfStartDay());
			startTimeValuePend=String.valueOf(startDay-1);
			if(startTimeValuePend.equalsIgnoreCase("0")){//For Avoiding "CBD + 0" in display
				startTimeValuePend=CBD;
			}else{
				startTimeValuePend=CBD+" + "+startTimeValuePend;
			}
		}
		if(ValidationUtil.isValid(approvedCollection.get(0).getAdfStartDay())){
			int startDay=Integer.parseInt(approvedCollection.get(0).getAdfStartDay());
			startTimeValue=String.valueOf(startDay-1);
			if(startTimeValue.equalsIgnoreCase("0")){//For Avoiding "CBD + 0" in display
				startTimeValue=CBD;
			}else{
				startTimeValue=CBD+" + "+startTimeValue;
			}
		}
		ReviewResultVb ladfStartTime = new ReviewResultVb(rsb.getString("starttime"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":startTimeValuePend,
				(approvedCollection == null || approvedCollection.isEmpty())?"":startTimeValue,
						(!startTimeValuePend.equals(startTimeValue)));
		lResult.add(ladfStartTime);
        
		
		String endTimeValuePend="";//EndTimePending
		String endTimeValue="";//EndTimeApproved
		if(ValidationUtil.isValid(pendingCollection.get(0).getAdfEndDay())){
			int endDay=Integer.parseInt(pendingCollection.get(0).getAdfEndDay());
			endTimeValuePend=String.valueOf(endDay-1);
			if(endTimeValuePend.equalsIgnoreCase("0")){//For Avoiding "CBD + 0" in display
				endTimeValuePend=CBD;
			}else{
				endTimeValuePend=CBD+" + "+endTimeValuePend;
			}
		}
		if(ValidationUtil.isValid(approvedCollection.get(0).getAdfEndDay())){
			int endDay=Integer.parseInt(approvedCollection.get(0).getAdfEndDay());
			endTimeValue=String.valueOf(endDay-1);
			if(endTimeValue.equalsIgnoreCase("0")){//For Avoiding "CBD + 0" in display
				endTimeValue=CBD;
			}else{
				endTimeValue=CBD+" + "+endTimeValue;
			}
		}
		/*ReviewResultVb ladfEndTime = new ReviewResultVb(rsb.getString("endtime"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":endTimeValuePend+" ("+pendingCollection.get(0).getAdfEndTime()+")",
				(approvedCollection == null || approvedCollection.isEmpty())?"":endTimeValue+" ("+approvedCollection.get(0).getAdfEndTime()+")");
		lResult.add(ladfEndTime);*/
		ReviewResultVb ladfEndTime = new ReviewResultVb(rsb.getString("endtime"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":endTimeValuePend,
				(approvedCollection == null || approvedCollection.isEmpty())?"":endTimeValue,
						(!endTimeValuePend.equals(endTimeValue)));
		lResult.add(ladfEndTime);
		
        ReviewResultVb laccessPermission = new ReviewResultVb(rsb.getString("accessPermission"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAccessPermission(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAccessPermission(),
                		(!pendingCollection.get(0).getAccessPermission()
								.equals(approvedCollection.get(0).getAccessPermission())));
        lResult.add(laccessPermission);
        ReviewResultVb ldateLastExtraction = new ReviewResultVb(rsb.getString("dateLastExtraction"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastExtraction(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastExtraction(),
                		(!pendingCollection.get(0).getDateLastExtraction()
								.equals(approvedCollection.get(0).getDateLastExtraction())));
        lResult.add(ldateLastExtraction);
        ReviewResultVb ldebugMode = new ReviewResultVb(rsb.getString("debugMode"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDebugMode(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDebugMode(),
                		(!pendingCollection.get(0).getDebugMode()
								.equals(approvedCollection.get(0).getDebugMode())));
        lResult.add(ldebugMode);
        ReviewResultVb lmandatory = new ReviewResultVb(rsb.getString("mandatory"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(9),pendingCollection.get(0).getMandatoryFlag()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(9),approvedCollection.get(0).getMandatoryFlag()),
						(!pendingCollection.get(0).getMandatoryFlag()
								.equals(approvedCollection.get(0).getMandatoryFlag())));
		lResult.add(lmandatory);        
        ReviewResultVb lIntergrityScriptsType = new ReviewResultVb(rsb.getString("integrityscripttype"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),pendingCollection.get(0).getIntegrityScriptType()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(5),approvedCollection.get(0).getIntegrityScriptType()),
						(!pendingCollection.get(0).getIntegrityScriptType()
								.equals(approvedCollection.get(0).getIntegrityScriptType())));
		lResult.add(lIntergrityScriptsType);
		ReviewResultVb lTemplateControlStatus = new ReviewResultVb(rsb.getString("templateControlStatus"),
				(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(8),pendingCollection.get(0).getTemplateControlStatus()),
				(approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(8),approvedCollection.get(0).getTemplateControlStatus()),
						(!pendingCollection.get(0).getTemplateControlStatus()
								.equals(approvedCollection.get(0).getTemplateControlStatus())));
		lResult.add(lTemplateControlStatus);
        ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
                    (approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()),
                    		(pendingCollection.get(0).getRecordIndicator()
    								!= approvedCollection.get(0).getRecordIndicator()));
        lResult.add(lRecordIndicator);
        ReviewResultVb ldataAcqStatus = new ReviewResultVb(rsb.getString("status"),
        		((pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDataAcqStatus()).toString(),
                ((approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDataAcqStatus()).toString(),
                (pendingCollection.get(0).getDataAcqStatus()
						!= approvedCollection.get(0).getDataAcqStatus()));
        lResult.add(ldataAcqStatus);
        ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName(),
						(pendingCollection.get(0).getMaker()
								!= approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName(),
						(pendingCollection.get(0).getVerifier()
								!= approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
        ReviewResultVb lDateLastModified = new ReviewResultVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
                    (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified(),
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
	@Override
	public List<ReviewResultVb> reviewRecord(DataAcquisitionDynamicVb vObject){
		try{
			List<DataAcquisitionDynamicVb> approvedCollection = getScreenDao().getQueryResultsForReview(vObject,1);
			List<DataAcquisitionDynamicVb> pendingCollection = getScreenDao().getQueryResultsForReview(vObject,0);
			return transformToReviewResults(approvedCollection,pendingCollection);
		}catch(Exception ex){
			return null;
		}
	}
	@Override
	protected void setVerifReqDeleteType(DataAcquisitionDynamicVb vObject){
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) getCommonDao().findVerificationRequiredAndStaticDelete("ADF_DATA_ACQUISITION");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	@Override
	protected void setAtNtValues(DataAcquisitionDynamicVb vObject){
		vObject.setDataAcqStatusNt(1);
		vObject.setRecordIndicatorNt(7);
		vObject.setAcquisitionProcessTypeAt(1079);
		vObject.setConnectivityTypeAt(1081);
		vObject.setDatabaseTypeAt(1082);
		vObject.setSourceScriptTypeAt(1083);
		vObject.setTargetScriptTypeAt(1083);
		vObject.setReadinessScriptTypeAt(1083);
		vObject.setPreactivityScriptTypeAt(1083);
		vObject.setFrequencyProcessAt(1077);
		vObject.setIntegrityScriptTypeAt(1083);
		vObject.setTemplateControlStatusAt(206);
		vObject.setMandatoryFlagAt(1201);
	}
	@Override
	protected AbstractDao<DataAcquisitionDynamicVb> getScreenDao() {
		return dataAcquisitionDynamicDao;
	}
	public String dbConTest(DataAcquisitionDynamicVb vObject) {
		String dbURL=vObject.getPopDbLink();
		
		String retVal="0";
		
		if("ORACLE".equalsIgnoreCase(vObject.getPopDbConnectivityType())){
			System.out.println("-------- Oracle JDBC Connection Testing ------");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			retVal="0:-:"+e.getMessage();
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return retVal;
		}
		System.out.println("Oracle JDBC Driver Registered!");
		Connection connection = null;
		try {
			if(ValidationUtil.isValid(vObject.getPopDbLink())){
				String user=vObject.getUser();
				String pass=vObject.getPwd();
				String SERVER_ADDRESS = vObject.getDbip().trim();
				int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
				Socket s;
				try{  
					s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
					if (s.isConnected())
						s.close();    
					} 
				catch (UnknownHostException e){ // unknown host 
					retVal="0:-:"+e.getMessage();
					System.out.println("Oracle Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				catch (IOException e) { // io exception, service probably not running 
					retVal="0:-:"+e.getMessage();
					System.out.println("Oracle Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				} 
				catch (NullPointerException e) {
					retVal="0:-:"+e.getMessage();
					System.out.println("Oracle Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				connection = DriverManager.getConnection(dbURL, user, pass);
			}/*else if(ValidationUtil.isValid(vObject.getPopDbLink())){
				connection = DriverManager.getConnection(vObject.getPopDbLink());
			}*/
			
		} catch (SQLException e) {
			retVal="0:-:"+e.getMessage();
			System.out.println("Oracle Connection Failed! Check output console");
			e.printStackTrace();
			return retVal;
		}
		if (connection != null) {
			retVal="1:-:SUCCESS";
			try {
				connection.close();
				System.out.println("Oracle connection closed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Oracle connection established successfully!");
		} else {
			retVal="0-Failed to make oracle connection!";
			System.out.println("Failed to make oracle connection!");
			return retVal;
		}
		}else if("MSSQL".equalsIgnoreCase(vObject.getPopDbConnectivityType())){
			System.out.println("-------- MsSQL JDBC Connection Testing ------");
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("Where is your MsSQL JDBC Driver?");
				e.printStackTrace();
				return retVal;
			}
			System.out.println("MsSQL JDBC Driver Registered!");
			Connection connection = null;
			try {
				if(!ValidationUtil.isValid(vObject.getPopDbLink())){					
				String user=vObject.getUser();
				String pass=vObject.getPwd();
				System.out.println(dbURL);
				String SERVER_ADDRESS = vObject.getDbip().trim();
				int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
				Socket s;
				try{  
					s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
					if (s.isConnected())
						s.close();    
					} 
				catch (UnknownHostException e){ // unknown host 
					retVal="0:-:"+e.getMessage();
					System.out.println("MsSQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				catch (IOException e) { // io exception, service probably not running 
					retVal="0:-:"+e;
					System.out.println("MsSQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				} 
				catch (NullPointerException e) {
					retVal="0:-:"+e.getMessage();
					System.out.println("MsSQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				connection = DriverManager.getConnection(dbURL, user, pass);
				}
				else if(ValidationUtil.isValid(vObject.getPopDbLink())){
					connection = DriverManager.getConnection(vObject.getPopDbLink());
				}	
			}catch(IllegalArgumentException e){
				retVal="0:-:"+e.getMessage();
				System.out.println("MsSQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}catch (SQLException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("MsSQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			if (connection != null) {
				retVal="1:-:SUCCESS";
				try {
					connection.close();
					System.out.println("MsSQL connection closed!");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("MsSQL connection established successfully!");
			} else {
				retVal="0-Failed to make MsSQL connection!";
				System.out.println("Failed to make MsSQL connection!");
				return retVal;
			}
		}else if("MYSQL".equalsIgnoreCase(vObject.getPopDbConnectivityType())){
			System.out.println("-------- MySQL JDBC Connection Testing ------");
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("Where is your MySQL JDBC Driver?");
				e.printStackTrace();
				return retVal;
			}
			System.out.println("MySQL JDBC Driver Registered!");
			Connection connection = null;
			try {
				if(!ValidationUtil.isValid(vObject.getPopDbLink())){					
				String user=vObject.getDbuser();
				String pass=vObject.getDbpwd();
				String SERVER_ADDRESS = vObject.getDbip().trim();
				int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
				Socket s;
				try{  
					s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
					if (s.isConnected())
						s.close();    
					} 
				catch (UnknownHostException e){ // unknown host 
					retVal="0:-:"+e.getMessage();
					System.out.println("MySQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				catch (IOException e) { // io exception, service probably not running 
					retVal="0:-:"+e.getMessage();
					System.out.println("MySQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				} 
				catch (NullPointerException e) {
					retVal="0:-:"+e.getMessage();
					System.out.println("MySQL Connection Failed! Check output console");
					e.printStackTrace();
					return retVal;
				}
				connection = DriverManager.getConnection(dbURL, user, pass);
				}else if(ValidationUtil.isValid(vObject.getPopDbLink())){
					connection = DriverManager.getConnection(vObject.getPopDbLink());
				}	
			} catch (SQLException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("MySQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			if (connection != null) {
				retVal="1:-:SUCCESS";
				try {
					connection.close();
					System.out.println("MySQL connection closed!");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("MySQL connection established successfully!");
			} else {
				retVal="0-Failed to make MySQL connection!";
				System.out.println("Failed to make MySQL connection!");
				return retVal;
			}
	  }
	else if("POSTGRESQL".equalsIgnoreCase(vObject.getPopDbConnectivityType())){
		System.out.println("-------- POSTGRESQL JDBC Connection Testing ------");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			retVal="0:-:"+e.getMessage();
			System.out.println("Where is your POSTGRESQL JDBC Driver?");
			e.printStackTrace();
			return retVal;
		}
		System.out.println("POSTGRESQL JDBC Driver Registered!");
		Connection connection = null;
		try {
			if(!ValidationUtil.isValid(vObject.getPopDbLink())){
			String user=vObject.getUser();
			String pass=vObject.getPwd();
			String SERVER_ADDRESS = vObject.getDbip().trim();
			int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
			Socket s;
			try{  
				s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
				if (s.isConnected())
					s.close();    
				} 
			catch (UnknownHostException e){ // unknown host 
				retVal="0:-:"+e.getMessage();
				System.out.println("POSTGRESQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			catch (IOException e) { // io exception, service probably not running 
				retVal="0:-:"+e.getMessage();
				System.out.println("POSTGRESQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			} 
			catch (NullPointerException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("POSTGRESQL Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			connection = DriverManager.getConnection(dbURL, user, pass);
			}else if(ValidationUtil.isValid(vObject.getPopDbLink())){
				connection = DriverManager.getConnection(vObject.getPopDbLink());
			}
		} catch (SQLException e) {
			retVal="0:-:"+e.getMessage();
			System.out.println("POSTGRESQL Connection Failed! Check output console");
			e.printStackTrace();
			return retVal;
		}
		if (connection != null) {
			retVal="1:-:SUCCESS";
			try {
				connection.close();
				System.out.println("POSTGRESQL connection closed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("POSTGRESQL connection established successfully!");
		} else {
			retVal="0-Failed to make POSTGRESQL connection!";
			System.out.println("Failed to make POSTGRESQL connection!");
			return retVal;
		}
	} else if("SYBASE".equalsIgnoreCase(vObject.getPopDbConnectivityType())) {
		System.out.println("-------- SYBASE JDBC Connection Testing ------");
		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDataSource");
		} catch (ClassNotFoundException e) {
			retVal="0:-:"+e.getMessage();
			System.out.println("Where is your SYBASE JDBC Driver?");
			e.printStackTrace();
			return retVal;
		}
		System.out.println("SYBASE JDBC Driver Registered!");
		Connection connection = null;
		try {
			if(ValidationUtil.isValid(vObject.getPopDbLink())){
			String user=vObject.getUser();
			String pass=vObject.getPwd();
			String SERVER_ADDRESS = vObject.getDbip().trim();
			int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
			Socket s;
			try{  
				s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
				if (s.isConnected())
					s.close();    
				} 
			catch (UnknownHostException e){ // unknown host 
				retVal="0:-:"+e.getMessage();
				System.out.println("SYBASE Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			catch (IOException e) { // io exception, service probably not running 
				retVal="0:-:"+e.getMessage();
				System.out.println("SYBASE Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			} 
			catch (NullPointerException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("SYBASE Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			connection = DriverManager.getConnection(dbURL, user, pass);
			}/*else if(ValidationUtil.isValid(vObject.getPopDbLink())){
				connection = DriverManager.getConnection(vObject.getPopDbLink());
			}*/
		} catch (SQLException e) {
			retVal="0:-:"+e;
			System.out.println("SYBASE Connection Failed! Check output console");
			e.printStackTrace();
			return retVal;
		}
		if (connection != null) {
			retVal="1:-:SUCCESS";
			try {
				connection.close();
				System.out.println("SYBASE connection closed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("SYBASE connection established successfully!");
		} else {
			retVal="0-Failed to make SYBASE connection!";
			System.out.println("Failed to make SYBASE connection!");
			return retVal;
		}
	}
	return retVal;
 }
	public String dbConReTest(DataAcquisitionDynamicVb vObject) {
		String retVal="0";
		
		String dbURL=vObject.getPopDbLink();
		
		if("ORACLE".equalsIgnoreCase(vObject.getPopDbConnectivityType())){
			String command = "java -jar /home/vision/execs/ConnectionTest.jar ";
			String user=vObject.getUser();
			String pass=vObject.getPwd();
			command = command + dbURL + " " + user + " " + pass;
			String SERVER_ADDRESS = vObject.getDbip().trim();
			int TCP_SERVER_PORT = Integer.parseInt(vObject.getDbport().trim());
			Socket s;
			try{  
				s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
				if (s.isConnected())
					s.close();    
			}catch (UnknownHostException e){ // unknown host 
				retVal="0:-:"+e.getMessage();
				System.out.println("Oracle Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}catch (IOException e) { // io exception, service probably not running 
				retVal="0:-:"+e.getMessage();
				System.out.println("Oracle Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}catch (NullPointerException e) {
				retVal="0:-:"+e.getMessage();
				System.out.println("Oracle Connection Failed! Check output console");
				e.printStackTrace();
				return retVal;
			}
			try {
				Process pro = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
				retVal = in.readLine();
				System.out.println("sssssssss"+retVal);
				logger.error("sssssssss"+retVal);
			} catch (Exception e) {
				retVal="0:-:"+e.getMessage();
				e.printStackTrace();
				return retVal;
		    }
		}
		return retVal;
	}
@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})	
public ExceptionCode doDbInsertUpdateToDynVarTable(DataAcquisitionDynamicVb vObject){
			ExceptionCode exceptionCode  = null;
			DeepCopy<DataAcquisitionDynamicVb> deepCopy = new DeepCopy<DataAcquisitionDynamicVb>();
			DataAcquisitionDynamicVb clonedObject = null;
			try
			{
				clonedObject = deepCopy.copy(vObject);
				exceptionCode = getDataAcquisitionDynamicDao().doInsertUpdateRecordForDbpopup(vObject);
				getScreenDao().fetchMakerVerifierNames(vObject);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}catch(RuntimeCustomException rex){
				logger.error("Insert Exception " + rex.getCode().getErrorMsg());
				logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
				exceptionCode = rex.getCode();
				exceptionCode.setOtherInfo(clonedObject);
				return exceptionCode;
			}
		}
public ExceptionCode reviewRecord1(DataAcquisitionDynamicVb vObject) {
	ExceptionCode exceptionCode = new ExceptionCode();
	List<ReviewResultVb> list = null;
	try {
		List<DataAcquisitionDynamicVb> approvedCollection = dataAcquisitionDynamicDao.getQueryResultsForReview(vObject, 0);
		List<DataAcquisitionDynamicVb> pendingCollection = dataAcquisitionDynamicDao.getQueryResultsForReview(vObject, 1);
		list = transformToReviewResults(approvedCollection, pendingCollection);
		exceptionCode.setResponse(list);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	} catch (Exception ex) {
		exceptionCode.setErrorMsg(ex.getMessage());
	}
	return exceptionCode;
}	
}
