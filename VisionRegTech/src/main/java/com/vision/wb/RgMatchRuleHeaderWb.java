package com.vision.wb;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.RgBuildProcessDao;
import com.vision.dao.RgMatchRuleDetailsDao;
import com.vision.dao.RgMatchRuleHeaderDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.RgBuildsVb;
import com.vision.vb.RgMatchRuleDetailVb;
import com.vision.vb.RgMatchRuleHeaderVb;
import com.vision.vb.VisionUsersVb;

@Component
public class RgMatchRuleHeaderWb extends AbstractDynaWorkerBean<RgMatchRuleHeaderVb>{

	@Autowired 
	private RgMatchRuleHeaderDao rgMatchRuleHeaderDao;
	
	@Autowired 
	private RgMatchRuleDetailsDao rgMatchRuleDetailDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired 
	RgBuildProcessDao rgBuildProcessDao;
	
	public static Logger logger = LoggerFactory.getLogger(RgMatchRuleHeaderWb.class);
	
	public ArrayList getPageLoadValues(){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
//			ArrayList<AlphaSubTabVb> countryLst = (ArrayList<AlphaSubTabVb>)commonDao.getCountry();
//			ArrayList ctryLeBookLst = new ArrayList();
//			for(AlphaSubTabVb ctryLeBook : countryLst) {
//				ArrayList<AlphaSubTabVb> leBookLst = (ArrayList<AlphaSubTabVb>)commonDao.getLeBook(ctryLeBook.getAlphaSubTab());
//				ctryLeBook.setChildren(leBookLst);
//				ctryLeBookLst.add(ctryLeBook);
//			}
//			arrListLocal.add(ctryLeBookLst);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			ArrayList<String> forceMatchLst = new ArrayList<>();
			forceMatchLst.add("Y");
			forceMatchLst.add("N");
			arrListLocal.add(forceMatchLst);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(6017);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);//202
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			ArrayList<String> operatorLst = new ArrayList<>();
			operatorLst.add("AND");
			operatorLst.add("OR");
			arrListLocal.add(operatorLst);
			collTemp = rgMatchRuleHeaderDao.getTableNamesList();
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("RG_CUSTMATCH_RULE_HEADER");
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(7401);
			arrListLocal.add(collTemp);
			String autoRefreshTime = commonDao.findVisionVariableValue("RG_BUILD_AUTO_REFRESH");
			arrListLocal.add(autoRefreshTime);
			String buildStatus = rgBuildProcessDao.getRgBuildStatus();
			arrListLocal.add(buildStatus);
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
				country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
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

	public ExceptionCode reviewMatchRuleConfig(List<RgMatchRuleHeaderVb> approvedCollection,
			List<RgMatchRuleHeaderVb> pendingCollection) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		try {
			if (pendingCollection != null)
				getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
			if (approvedCollection != null)
				getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
			ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

//			ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),
//					(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getCountry(),
//					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//							: approvedCollection.get(0).getCountry());
//			lResult.add(lCountry);
//
//			ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),
//					(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getLeBook(),
//					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
//							: approvedCollection.get(0).getLeBook());
//			lResult.add(lLeBook);

			ReviewResultVb approachType = new ReviewResultVb(rsb.getString("approachType"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									pendingCollection.get(0).getApproachType()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
									approvedCollection.get(0).getApproachType()),
					(!pendingCollection.get(0).getApproachType().equals(approvedCollection.get(0).getApproachType())));
			lResult.add(approachType); 
			
			ReviewResultVb lRuleId = new ReviewResultVb(rsb.getString("rule")+" "+rsb.getString("id"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? "" : pendingCollection.get(0).getRuleId(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRuleId(),
					(!pendingCollection.get(0).getRuleId().equals(approvedCollection.get(0).getRuleId())));
			lResult.add(lRuleId);

			ReviewResultVb lRuleDescription = new ReviewResultVb(rsb.getString("rule")+" "+rsb.getString("description"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getRuleDescription(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRuleDescription(),
					(!pendingCollection.get(0).getRuleDescription()
							.equals(approvedCollection.get(0).getRuleDescription())));
			lResult.add(lRuleDescription);

			ReviewResultVb lRulePriority = new ReviewResultVb(rsb.getString("rule")+" "+rsb.getString("priority"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: Integer.toString(pendingCollection.get(0).getRulePriority()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: Integer.toString(approvedCollection.get(0).getRulePriority()),
					(pendingCollection.get(0).getRulePriority() != approvedCollection.get(0).getRulePriority()));
			lResult.add(lRulePriority);

			ReviewResultVb lForceMatch = new ReviewResultVb(rsb.getString("forceMatch"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getForceMatch(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getForceMatch(),
					(!pendingCollection.get(0).getForceMatch().equals(approvedCollection.get(0).getForceMatch())));
			lResult.add(lForceMatch);

			ReviewResultVb lThreshold = new ReviewResultVb(rsb.getString("threshold"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: Integer.toString(pendingCollection.get(0).getThreshold()),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: Integer.toString(approvedCollection.get(0).getThreshold()),
					(pendingCollection.get(0).getThreshold() != approvedCollection.get(0).getThreshold()));
			lResult.add(lThreshold);

			ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: pendingCollection.get(0).getRecordIndicatorDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRecordIndicatorDesc() == null ? ""
									: approvedCollection.get(0).getRecordIndicatorDesc(),
					(pendingCollection.get(0).getRecordIndicatorDesc() != approvedCollection.get(0)
							.getRecordIndicatorDesc()));
			lResult.add(lRecordIndicator);

			ReviewResultVb status = new ReviewResultVb(rsb.getString("status"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getRuleStatusDesc(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getRuleStatusDesc(),
					(pendingCollection.get(0).getRuleStatusDesc() != approvedCollection.get(0).getRuleStatusDesc()));
			lResult.add(status);

			ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
					(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
			lResult.add(lMaker);

			ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
					(pendingCollection == null || pendingCollection.isEmpty()) ? ""
							: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
					(approvedCollection == null || approvedCollection.isEmpty()) ? ""
							: approvedCollection.get(0).getVerifier() == 0 ? ""
									: approvedCollection.get(0).getVerifierName(),
					(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
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
			if (lResult != null && !lResult.isEmpty()) {
				exceptionCode.setResponse(lResult);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;

	}	

	public RgMatchRuleHeaderDao getRgCustmatchRuleHeaderDao() {
		return rgMatchRuleHeaderDao;
	}

	public void setRgCustmatchRuleHeaderDao(RgMatchRuleHeaderDao rgCustmatchRuleHeaderDao) {
		this.rgMatchRuleHeaderDao = rgCustmatchRuleHeaderDao;
	}

	@Override
	protected AbstractDao<RgMatchRuleHeaderVb> getScreenDao() {
		return rgMatchRuleHeaderDao;
	}

	@Override
	protected void setAtNtValues(RgMatchRuleHeaderVb vObject) {
		vObject.setApproachTypeAt(6017);
		vObject.setRuleStatusNt(1);
		vObject.setRecordIndicatorNt(7);
	}

	@Override
	protected void setVerifReqDeleteType(RgMatchRuleHeaderVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("RG_CUSTMATCH_RULE_HEADER");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}
	
	public ExceptionCode reviewRecordMatchRule(RgMatchRuleHeaderVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RgMatchRuleHeaderVb> approvedCollection = getScreenDao().getQueryResults(vObject, 0);
			List<RgMatchRuleHeaderVb> pendingCollection = getScreenDao().getQueryResults(vObject, 1);
			exceptionCode = reviewMatchRuleConfig(approvedCollection, pendingCollection);
		} catch (Exception ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getRgBuildDetails(RgBuildsVb rgBuildsVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RgBuildsVb> rgBuildlst = rgBuildProcessDao.getRgBuilds(rgBuildsVb);
			if (rgBuildlst == null || rgBuildlst.size() == 0) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Builds Configured");
			} else {
				List<RgBuildsVb> rgSummarylst = rgBuildProcessDao.getRgBuildsSummary(rgBuildsVb);
				exceptionCode.setOtherInfo(rgSummarylst);
				exceptionCode.setResponse(rgBuildlst);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			String buildStatus = rgBuildProcessDao.getRgBuildStatus();
			if (ValidationUtil.isValid(buildStatus))
				exceptionCode.setResponse1(buildStatus);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode doInsertBuildSchedule(RgBuildsVb rgBuildsVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			rgBuildsVb.setSequence(rgBuildProcessDao.getMaxRgBuildMaxSeq(rgBuildsVb));
			exceptionCode = rgBuildProcessDao.doInsertionRgBuildHeader(rgBuildsVb);
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				return exceptionCode;
			}
//			if (rgBuildsVb.getRgBuildlst() != null && rgBuildsVb.getRgBuildlst().size() > 0) {
//				for (RgBuildsVb dObj : rgBuildsVb.getRgBuildlst()) {
//					dObj.setSequence(rgBuildsVb.getSequence());
//					exceptionCode = rgBuildProcessDao.doInsertionRgBuildSchedule(dObj);
//				}
//			}
			exceptionCode.setOtherInfo(rgBuildsVb);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode getRgBuildHistory(RgBuildsVb rgBuildsVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RgBuildsVb> rgBuildlst = rgBuildProcessDao.getBuildHistory(rgBuildsVb);
			if(rgBuildlst == null || rgBuildlst.size() == 0) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No History Found");
			}else {
				exceptionCode.setOtherInfo(rgBuildsVb);
				exceptionCode.setResponse(rgBuildlst);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode listGroupedLogFiles() throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String folderPath = commonDao.findVisionVariableValue("RG_BUILDLOG_PATH");
			File directory = new File(folderPath);
			Map<String, List<File>> mapFiles = new TreeMap<>();

			if (directory.exists()) {
				File[] listFiles = directory.listFiles();
				Arrays.sort(listFiles, Comparator.comparingLong(File::lastModified).reversed());
				for (File listFile : listFiles) {
					List fileLst = new ArrayList<>();
					String fileName = listFile.getName();
					if (!fileName.contains(".log"))
						continue;
					fileName = fileName.substring(0, fileName.lastIndexOf(".log"));
					String date = "";
					if(fileName.length() > 10)
						date = fileName.substring(fileName.length() - 10);
					for (File File : listFiles) {
						if (File.getName().contains(date)) {
							fileLst.add(File.getName());

						}
					}
					mapFiles.put(date, fileLst);
				}
			}
			exceptionCode.setResponse(mapFiles);
		} catch (Exception e) {

		}
		return exceptionCode;
	}
	public ExceptionCode downloadFile(String logFileName) throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		try {
			String filePath = commonDao.findVisionVariableValue("RG_BUILDLOG_PATH");
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			tmpFilePath = tmpFilePath+File.separator;
			String[] logFile = logFileName.split(",");
			File lfile = new File(tmpFilePath +  "logs.zip");
			if (lfile.exists()) {
				lfile.delete();
			}
			if (logFile.length > 1) {
				FileOutputStream fos = new FileOutputStream(tmpFilePath +"logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (int i = 0; i < logFile.length; i++) {
					FileInputStream fis = new FileInputStream(filePath + logFile[i]);
					ZipEntry ze = new ZipEntry(logFile[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
				}
				zipOut.close();
				fos.close();
			} else {
				File my_file = new File(filePath +logFileName);
				out = new ByteArrayOutputStream();
				FileInputStream in = new FileInputStream(my_file);
				out = new ByteArrayOutputStream();
				lfile = new File(tmpFilePath +logFileName);
				if (lfile.exists()) {
					lfile.delete();
				}
				outputStream = new FileOutputStream(tmpFilePath+ logFileName);
				int length = logFileName.length();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				while ((length = in.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				out.writeTo(outputStream);
				outputStream.flush();
				outputStream.close();
				out.flush();
				out.close();
				in.close();

			}
			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Log File Downloaded");
			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Download Errror : " + ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
	}
	
	@Override
	public ExceptionCode getQueryResults(RgMatchRuleHeaderVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		setVerifReqDeleteType(vObject);
		Boolean pendFlag = true;
		List<RgMatchRuleDetailVb> matchRuleConfigDetaillst = new ArrayList<RgMatchRuleDetailVb>();
		List<RgMatchRuleHeaderVb> collTemp = rgMatchRuleHeaderDao.getQueryResults(vObject, intStatus);

		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = rgMatchRuleHeaderDao.getQueryResults(vObject, intStatus);
			pendFlag = false;
		}
		if (collTemp.size() == 0) {
			exceptionCode = CommonUtils.getResultObject(rgMatchRuleHeaderDao.getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No records found");
			return exceptionCode;
		} else {
			if (!pendFlag)
				matchRuleConfigDetaillst = rgMatchRuleDetailDao.getQueryResultsforMatchRuleDet(vObject,Constants.STATUS_ZERO);
			else 
				matchRuleConfigDetaillst = rgMatchRuleDetailDao.getQueryResultsforMatchRuleDet(vObject,Constants.STATUS_DELETE);
			if (matchRuleConfigDetaillst != null && !matchRuleConfigDetaillst.isEmpty()) {
				collTemp.get(0).setMatchRuleDetailLst(matchRuleConfigDetaillst);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Successful operation");
			return exceptionCode;
		}
	}
}