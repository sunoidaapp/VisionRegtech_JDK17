
package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.VisionDynamicHashVariablesDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionDynamicHashVariablesVb;
@Component
public class VisionDynamicHashVariablesWb extends AbstractDynaWorkerBean<VisionDynamicHashVariablesVb> {

	@Autowired
	private VisionDynamicHashVariablesDao visionDynamicHashVariablesDao;
	@Autowired
	private CommonDao commonDao;
	public static Logger logger = LoggerFactory.getLogger(VisionDynamicHashVariablesWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7);
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1056);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1083);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(7605);// dbConnectivityType
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<VisionDynamicHashVariablesVb> approvedCollection,
			List<VisionDynamicHashVariablesVb> pendingCollection) {
		ArrayList collTemp = getPageLoadValues();
		ResourceBundle rsb = CommonUtils.getResourceManger();
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		ReviewResultVb lGlEnrichId = new ReviewResultVb(rsb.getString("variableName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVariableName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVariableName(),
						(!pendingCollection.get(0).getVariableName()
								.equals(approvedCollection.get(0).getVariableName())));
		lResult.add(lGlEnrichId);

		ReviewResultVb lGlEnrichType = new ReviewResultVb(rsb.getString("variableType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(2),
								pendingCollection.get(0).getVariableType()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(2),
								approvedCollection.get(0).getVariableType()),
						(pendingCollection.get(0).getVariableType()
								!=(approvedCollection.get(0).getVariableType())));
		lResult.add(lGlEnrichType);

		ReviewResultVb lUserProfile = new ReviewResultVb(rsb.getString("scriptType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								pendingCollection.get(0).getScriptType()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								approvedCollection.get(0).getScriptType()),
						(!pendingCollection.get(0).getScriptType()
								.equals(approvedCollection.get(0).getScriptType())));
		lResult.add(lUserProfile);
		ReviewResultVb lVariableScript = new ReviewResultVb(rsb.getString("variableScript"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVariableScript(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVariableScript(),
						(!pendingCollection.get(0).getVariableScript()
								.equals(approvedCollection.get(0).getVariableScript())));
		lResult.add(lVariableScript);
		ReviewResultVb lGLEnrichIdsStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getVariableStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getVariableStatus()),
						(!String.valueOf(pendingCollection.get(0).getVariableStatus())
								.equals(String.valueOf(approvedCollection.get(0).getVariableStatus()))));
		lResult.add(lGLEnrichIdsStatus);
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
								(pendingCollection.get(0).getMaker()!=(approvedCollection.get(0).getMaker())));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
								(pendingCollection.get(0).getMaker()!=(approvedCollection.get(0).getMaker())));
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
						(!pendingCollection.get(0).getDateCreation()
								.equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
	}

	@Override
	protected void setVerifReqDeleteType(VisionDynamicHashVariablesVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("VISION_DYNAMIC_HASH_VAR");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
	}

	@Override
	protected void setAtNtValues(VisionDynamicHashVariablesVb vObject) {
		vObject.setRecordIndicatorNt(7);
		vObject.setVariableStatusNt(1);
		vObject.setVariableTypeNt(1056);
		vObject.setScriptTypeAt(1083);

	}

	public VisionDynamicHashVariablesDao getVisionDynamicHashVariablesDao() {
		return visionDynamicHashVariablesDao;
	}

	public void setVisionDynamicHashVariablesDao(VisionDynamicHashVariablesDao visionDynamicHashVariablesDao) {
		this.visionDynamicHashVariablesDao = visionDynamicHashVariablesDao;
	}

	@Override
	protected AbstractDao<VisionDynamicHashVariablesVb> getScreenDao() {
		return visionDynamicHashVariablesDao;
	}

	public String createDbUrl(VisionDynamicHashVariablesVb visionDynamicHashVariablesVb) {
		String jdbcUrl = "";
		String dbIP = visionDynamicHashVariablesVb.getDatabaseIp();
		String dbPortNumber = visionDynamicHashVariablesVb.getDbPort();
		String dataBaseName = visionDynamicHashVariablesVb.getDbName();
		String dbInstance = visionDynamicHashVariablesVb.getDbInstance();
		String dbServiceName = visionDynamicHashVariablesVb.getVariableName();
		String hostName = dbServiceName;
		String serverName = visionDynamicHashVariablesVb.getServerHostName();
		String Sid = visionDynamicHashVariablesVb.getSid(); 

		if ("ORACLE".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {
			if (ValidationUtil.isValid(Sid)) {
				jdbcUrl = "jdbc:oracle:thin:@" + dbIP + ":" + dbPortNumber + ":" + Sid;
			} else {
				jdbcUrl = "jdbc:oracle:thin:@" + dbIP + ":" + dbPortNumber + ":" + hostName;
			}
		} else if ("MSSQL".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {
			if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance
						+ ";databaseName=" + hostName;
			} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(hostName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance;
			} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
			} else {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
			}
		} else if ("MYSQL".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {

			if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIP + ":" + dbPortNumber + "//instanceName=" + dbInstance
						+ "//databaseName=" + dataBaseName;
			} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIP + ":" + dbPortNumber + "//instanceName=" + dbInstance;
			} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
			} else {
				jdbcUrl = "jdbc:mysql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
			}
		} else if ("POSTGRESQL".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {
			jdbcUrl = "jdbc:postgresql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
		} else if ("SYBASE".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {
			jdbcUrl = "jdbc:sybase:Tds:" + dbIP + ":" + dbPortNumber + "?ServiceName=" + hostName;
		} else if ("INFORMIX".equalsIgnoreCase(visionDynamicHashVariablesVb.getDatabaseType())) {
			jdbcUrl = "jdbc:informix-sqli://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName + ":informixserver="
					+ serverName;
		}
		return jdbcUrl;
	}
	
	public ExceptionCode reviewRecord1(VisionDynamicHashVariablesVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReviewResultVb> list = null;
		try {
			List<VisionDynamicHashVariablesVb> approvedCollection = getScreenDao().getQueryResults(vObject, 0);
			List<VisionDynamicHashVariablesVb> pendingCollection = getScreenDao().getQueryResults(vObject, 1);
			list = transformToReviewResults(approvedCollection, pendingCollection);
			exceptionCode.setResponse(list);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}
	public List<VisionDynamicHashVariablesVb> getDisplayTagList(VisionDynamicHashVariablesVb vObject) {
		String variableName = vObject.getVariableName();
		List<VisionDynamicHashVariablesVb> collTemp = new ArrayList<>();
		List<VisionDynamicHashVariablesVb> finalList = new ArrayList<>();
		boolean newRec = true;
		try {
			String ConnectorScript = vObject.getConnectorScripts();
			if(ValidationUtil.isValid(variableName)) {
				ConnectorScript = visionDynamicHashVariablesDao.getScriptValue(variableName,true);
			}
			if (!ValidationUtil.isValid(ConnectorScript)) {
				ConnectorScript = visionDynamicHashVariablesDao.getScriptValue(variableName,false);
			}
			if(!ValidationUtil.isValid(vObject.getConnectorType())) {
				String dataBase=visionDynamicHashVariablesDao.getValue(ConnectorScript, "DATABASE_TYPE");
				vObject.setConnectorType(ValidationUtil.isValid(dataBase)?dataBase:"");
			}
			
			collTemp = visionDynamicHashVariablesDao.getDisplayTagList(vObject.getConnectorType(), ConnectorScript);
			if (!collTemp.isEmpty() && collTemp.size() > 0) {
				for (VisionDynamicHashVariablesVb visionDynamicHashVariablesVb : collTemp) {
					if (ValidationUtil.isValid(visionDynamicHashVariablesVb.getTagValue()) && ValidationUtil.isValid(variableName)) {
						if (visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("SERVICE_NAME") || visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("SID")
								|| visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("DB_IP") || visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("USER")
								|| visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("DB IP") || visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("DB NAME")
								|| visionDynamicHashVariablesVb.getTagName().equalsIgnoreCase("SERVICE NAME")) {
							visionDynamicHashVariablesVb.setFieldDisable(true);
						}
						if ("DB_PWD".equalsIgnoreCase(visionDynamicHashVariablesVb.getTagName()) || "PWD".equalsIgnoreCase(visionDynamicHashVariablesVb.getTagName())) {
							if ("Yes".equalsIgnoreCase(visionDynamicHashVariablesVb.getEncryption()) || "Y".equalsIgnoreCase(visionDynamicHashVariablesVb.getEncryption())) {
								String encryptedPassword = visionDynamicHashVariablesVb.getTagValue();
								String password ="";
								 if (CommonUtils.isBase64(encryptedPassword)) {
								 password = new String(jespa.util.Base64.decode(encryptedPassword));
								 }else {
									 password= encryptedPassword;
								 }
								visionDynamicHashVariablesVb.setTagValue(password);
							}
						}
					}
					finalList.add(visionDynamicHashVariablesVb);
				}
			}
			return finalList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	

	public String dynamicScriptCreation(VisionDynamicHashVariablesVb vObject) {
		JSONObject extractVbData = new JSONObject(vObject);
		StringBuilder scriptFormation = new StringBuilder();
		if (ValidationUtil.isValid(vObject.getTagList())) {
			JSONArray tagListArr = (JSONArray) extractVbData.getJSONArray("tagList");
			if (tagListArr.length() != 0) {
				if ("DB".equalsIgnoreCase(vObject.getConnectionType())) {
					scriptFormation.append("{DATABASE_TYPE:#CONSTANT$@!" + vObject.getConnectorType() + "#}");
					// scriptFormation.append("{DB_URL:#CONSTANT$@!" + vObject.getDbUrl() + "#}");
				}

				for (int i = 0; i < tagListArr.length(); i++) {
					JSONObject jsonObj = tagListArr.getJSONObject(i);
					String ch = fixJSONObject(jsonObj);
					JSONObject jsonData = new JSONObject(ch);
					String tagName = jsonData.getString("TAG");
					String encryption = jsonData.getString("ENCRYPTION");
					String value = jsonData.getString("VALUE");
					if ("yes".equalsIgnoreCase(encryption)) {
						scriptFormation.append("{" + tagName + ":#ENCRYPT$@!" + value + "#}");
					} else {
						scriptFormation.append("{" + tagName + ":#CONSTANT$@!" + value + "#}");
					}
				}
			}

			return String.valueOf(scriptFormation);
		} else {
			return commonDao.getScript(vObject.getVariableName());
		}
	}
	public String fixJSONObject(JSONObject obj) {
		String jsonString = obj.toString();
		for (int i = 0; i < obj.names().length(); i++) {
			try {
				jsonString = jsonString.replace(obj.names().getString(i), obj.names().getString(i).toUpperCase());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonString;
	}
}
