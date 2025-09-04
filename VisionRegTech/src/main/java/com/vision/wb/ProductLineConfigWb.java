package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.AlphaSubTabDao;
import com.vision.dao.CommonDao;
import com.vision.dao.NumSubTabDao;
import com.vision.dao.ProductLineConfigDao;
import com.vision.dao.TransLinesGlDao;
import com.vision.dao.TransLinesSbuDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
@Component
public class ProductLineConfigWb extends AbstractDynaWorkerBean<TransLineHeaderVb>{
	@Autowired
	private ProductLineConfigDao productLineConfigDao;
	@Autowired
	private NumSubTabDao numSubTabDao;
	@Autowired
	private AlphaSubTabDao alphaSubTabDao;
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	TransLinesSbuDao transLinesSbuDao;
	
	@Autowired
	TransLinesGlDao transLinesGlDao;
	
	public static Logger logger = LoggerFactory.getLogger(ProductLineConfigWb.class);
	
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
		return productLineConfigDao;
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
		List<TransLineHeaderVb> collTemp = productLineConfigDao.getQueryResults(vObject,intStatus);
		if (collTemp.size() == 0){
			intStatus = 0;
			collTemp = productLineConfigDao.getQueryResults(vObject,intStatus);
			pendFlag = false;
		}
		if(collTemp.size() == 0){
			exceptionCode = CommonUtils.getResultObject(productLineConfigDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		}else{
			if(!pendFlag) {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_ZERO);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_ZERO);
			}else {
				transGlList = transLinesGlDao.getTransGLDetails(vObject,Constants.STATUS_DELETE);
				transSbuList = transLinesSbuDao.getTransSbuDetails(vObject,Constants.STATUS_DELETE);
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
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<TransLineHeaderVb> approvedCollection, List<TransLineHeaderVb> pendingCollection) {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		ArrayList collTemp = getPageLoadValues();
		if(pendingCollection != null) 
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if(approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
        ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
        ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
        		(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
                (approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
        lResult.add(lCountry);
        
        return lResult;
	}
}
