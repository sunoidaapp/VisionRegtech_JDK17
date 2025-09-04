package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.ServiceLineConfigDao;
import com.vision.dao.TransLinesChannelDao;
import com.vision.dao.TransLinesGlDao;
import com.vision.dao.TransLinesSbuDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
@Component
public class ServiceLineConfigWb extends AbstractDynaWorkerBean<TransLineHeaderVb>{
	@Autowired
	private ServiceLineConfigDao serviceLineConfigDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private TransLinesSbuDao transLinesSbuDao;
	@Autowired
	private TransLinesChannelDao transLinesChannelDao;
	@Autowired
	private TransLinesGlDao transLinesGlDao;
	
	public static Logger logger = LoggerFactory.getLogger(ServiceLineConfigWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = numSubTabDao.findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected AbstractDao<TransLineHeaderVb> getScreenDao() {
		return serviceLineConfigDao;
	}
	@Override
	protected void setAtNtValues(TransLineHeaderVb vObject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setVerifReqDeleteType(TransLineHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList =(ArrayList<CommonVb>) commonDao.findVerificationRequiredAndStaticDelete("RA_MST_TRANS_LINE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	public ExceptionCode getQueryResults(TransLineHeaderVb vObject){
		int intStatus = 1;
		TransLineHeaderVb transLineHeaderVb = new TransLineHeaderVb();
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		List<TransLineGLVb> transGlList = new ArrayList<TransLineGLVb>();
		List<TransLineSbuVb> transSbuList = new ArrayList<TransLineSbuVb>();
		List<TransLineChannelVb> transChannelList = new ArrayList<TransLineChannelVb>();
		List<TransLineHeaderVb> collTemp = serviceLineConfigDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = serviceLineConfigDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(serviceLineConfigDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			if(!pendFlag) {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_ZERO);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_ZERO);
				transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_ZERO);
			}else {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_DELETE);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_DELETE);
				transChannelList = transLinesChannelDao.getTransChannelDetails(vObject, Constants.STATUS_DELETE);
			}
			if(transGlList != null && !transGlList.isEmpty()) {
				collTemp.get(0).setTransLineGllst(transGlList);
			}
			if(transSbuList != null && transSbuList.size() > 0) {
				StringJoiner sbuArrJoiner = new StringJoiner(",");
				transSbuList.forEach(transSbu -> {
					sbuArrJoiner.add(transSbu.getBusinessVertical());
				});
				collTemp.get(0).setBusinessVertical(sbuArrJoiner.toString());
			}
			if(transChannelList != null && transChannelList.size() > 0) {
				StringJoiner channelArrJoiner = new StringJoiner(",");
				transChannelList.forEach(transChannel -> {
					channelArrJoiner.add(transChannel.getChannelId());
				});
				collTemp.get(0).setChannelId(channelArrJoiner.toString());
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
	/*@Override
	protected List<TransLineHeaderVb> transformToReviewResults(List<TransLineHeaderVb> approvedCollection, List<TransLineHeaderVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null) 
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
        ArrayList<TransLineHeaderVb> lResult = new ArrayList<TransLineHeaderVb>();
        TransLineHeaderVb lCountry = new TransLineHeaderVb(rsb.getString("country"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
        lResult.add(lCountry);
        TransLineHeaderVb lLeBook = new TransLineHeaderVb(rsb.getString("leBook"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
        lResult.add(lLeBook);
        TransLineHeaderVb lAccountOfficer = new TransLineHeaderVb(rsb.getString("accountOfficer"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAccountOfficer(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAccountOfficer());
        lResult.add(lAccountOfficer);
        TransLineHeaderVb lAOName = new TransLineHeaderVb(rsb.getString("Aoname"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAoName(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAoName());
        lResult.add(lAOName);
        TransLineHeaderVb lAOBackUp = new TransLineHeaderVb(rsb.getString("backUpAccountOfficer"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getAoBackUp(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getAoBackUp());
        lResult.add(lAOBackUp);        
        TransLineHeaderVb lVisionOuc = new TransLineHeaderVb(rsb.getString("visionOUC"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVisionOUC(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVisionOUC());
        lResult.add(lVisionOuc);        
        TransLineHeaderVb lAoAttribute1 = new TransLineHeaderVb(rsb.getString("aoAttribute1"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),pendingCollection.get(0).getAoAttribute1()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(2),approvedCollection.get(0).getAoAttribute1()));
        lResult.add(lAoAttribute1);
        TransLineHeaderVb lAoAttribute2 = new TransLineHeaderVb(rsb.getString("aoAttribute2"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getAoAttribute2()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getAoAttribute2()));
        lResult.add(lAoAttribute2);
        
        TransLineHeaderVb lAoAttribute3 = new TransLineHeaderVb(rsb.getString("aoAttribute3"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getAoAttribute3()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getAoAttribute3()));
        lResult.add(lAoAttribute3);
        TransLineHeaderVb lAoAttribute4 = new TransLineHeaderVb(rsb.getString("aoAttribute4"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getAoAttribute4()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getAoAttribute4()));
        lResult.add(lAoAttribute4);
        TransLineHeaderVb lAoAttribute5 = new TransLineHeaderVb(rsb.getString("aoAttribute5"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),pendingCollection.get(0).getAoAttribute5()),
                (approvedCollection == null || approvedCollection.isEmpty())?"":getAtDescription((List<AlphaSubTabVb>)collTemp.get(3),approvedCollection.get(0).getAoAttribute5()));
        lResult.add(lAoAttribute5);
        TransLineHeaderVb lEffectiveDate = new TransLineHeaderVb(rsb.getString("effectiveDate"),
    			(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getEffectiveDate(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getEffectiveDate());
        lResult.add(lEffectiveDate); 
        
        
        TransLineHeaderVb lVariableStatus = new TransLineHeaderVb(rsb.getString("aoStatus"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),pendingCollection.get(0).getAoStatus()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(0),approvedCollection.get(0).getAoStatus()));
        lResult.add(lVariableStatus);
        TransLineHeaderVb lRecordIndicator = new TransLineHeaderVb(rsb.getString("recordIndicator"),(pendingCollection == null || pendingCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),pendingCollection.get(0).getRecordIndicator()),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":getNtDescription((List<NumSubTabVb>) collTemp.get(1),approvedCollection.get(0).getRecordIndicator()));
        lResult.add(lRecordIndicator);
        TransLineHeaderVb lMaker = new TransLineHeaderVb(rsb.getString("maker"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getMaker() == 0?"":pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getMaker() == 0?"":approvedCollection.get(0).getMakerName());
		lResult.add(lMaker);
		TransLineHeaderVb lVerifier = new TransLineHeaderVb(rsb.getString("verifier"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getVerifier() == 0?"":pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getVerifier() == 0?"":approvedCollection.get(0).getVerifierName());
		lResult.add(lVerifier);
        TransLineHeaderVb lDateLastModified = new TransLineHeaderVb(rsb.getString("dateLastModified"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateLastModified(),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateLastModified());
        lResult.add(lDateLastModified);
        TransLineHeaderVb lDateCreation = new TransLineHeaderVb(rsb.getString("dateCreation"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getDateCreation(),
        		(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getDateCreation());
        lResult.add(lDateCreation);
        TransLineHeaderVb lLocalCcy = new TransLineHeaderVb("Staff Id",(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getStaffId(),
				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getStaffId());
		lResult.add(lLocalCcy);
        return lResult;
	}
*/}
