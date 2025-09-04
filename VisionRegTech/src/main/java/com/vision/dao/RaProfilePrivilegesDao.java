package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.RaProfileVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class RaProfilePrivilegesDao extends AbstractDao<RaProfileVb> {
	@Value("${app.productName}")
	private String productName;
	@Value("${app.databaseType}")
	private String databaseType;

	String ProfileStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "T1.PROFILE_STATUS",
			"PROFILE_STATUS_DESC");
	String ProfileStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "T1.PROFILE_STATUS",
			"PROFILE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "T1.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "T1.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RaProfileVb profilePrivilegesVb = new RaProfileVb();
				profilePrivilegesVb.setUserGroup(rs.getString("USER_GROUP"));
				profilePrivilegesVb.setUserGroupDesc(rs.getString("USER_GROUP_Desc"));
				profilePrivilegesVb.setUserProfile(rs.getString("USER_PROFILE"));
				profilePrivilegesVb.setUserProfileDesc(rs.getString("USER_PROFILE_Desc"));
				profilePrivilegesVb.setMenuGroup(rs.getString("MENU_GROUP"));
				profilePrivilegesVb.setMenuGroupDesc(rs.getString("MENU_GROUP_NAME"));
				profilePrivilegesVb.setProfileAdd(rs.getString("P_ADD"));
				profilePrivilegesVb.setProfileModify(rs.getString("P_MODIFY"));
				profilePrivilegesVb.setProfileDelete(rs.getString("P_DELETE"));
				profilePrivilegesVb.setProfileInquiry(rs.getString("P_INQUIRY"));
				profilePrivilegesVb.setProfileVerification(rs.getString("P_VERIFICATION"));
				profilePrivilegesVb.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				profilePrivilegesVb.setProfileDownload(rs.getString("P_DOWNLOAD"));
				profilePrivilegesVb.setProfileSubmit(rs.getString("P_SUBMIT"));
				profilePrivilegesVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				profilePrivilegesVb.setProfileStatus(rs.getInt("PROFILE_STATUS"));
				profilePrivilegesVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				profilePrivilegesVb.setProfileStatusDesc(rs.getString("PROFILE_STATUS_DESC"));
				profilePrivilegesVb.setMakerName(rs.getString("MAKER_NAME"));
				profilePrivilegesVb.setMaker(rs.getInt("MAKER"));
				profilePrivilegesVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				profilePrivilegesVb.setDateCreation(rs.getString("DATE_CREATION"));
				profilePrivilegesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				profilePrivilegesVb.setExcludeMenu(rs.getString("EXCLUDE_MENU_PROGRAM_LIST"));
				return profilePrivilegesVb;
			}
		};
		return mapper;
	}

	protected RowMapper getQueryResultsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RaProfileVb profilePrivilegesVb = new RaProfileVb();
				profilePrivilegesVb.setUserGroup(rs.getString("USER_GROUP"));
				profilePrivilegesVb.setUserGroupDesc(rs.getString("USER_GROUP_Desc"));
				profilePrivilegesVb.setUserProfile(rs.getString("USER_PROFILE"));
				profilePrivilegesVb.setUserProfileDesc(rs.getString("USER_PROFILE_Desc"));
				profilePrivilegesVb.setMenuGroup(rs.getString("MENU_GROUP"));
				profilePrivilegesVb.setMenuGroupDesc(rs.getString("MENU_GROUP_NAME"));
				profilePrivilegesVb.setProfileAdd(rs.getString("P_ADD"));
				profilePrivilegesVb.setProfileModify(rs.getString("P_MODIFY"));
				profilePrivilegesVb.setProfileDelete(rs.getString("P_DELETE"));
				profilePrivilegesVb.setProfileInquiry(rs.getString("P_INQUIRY"));
				profilePrivilegesVb.setProfileVerification(rs.getString("P_VERIFICATION"));
				profilePrivilegesVb.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				profilePrivilegesVb.setProfileDownload(rs.getString("P_DOWNLOAD"));
				profilePrivilegesVb.setProfileSubmit(rs.getString("P_SUBMIT"));
				profilePrivilegesVb.setMaker(rs.getInt("MAKER"));
				profilePrivilegesVb.setMakerName(rs.getString("MAKER_NAME"));
				profilePrivilegesVb.setVerifier(rs.getInt("VERIFIER"));
				profilePrivilegesVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				profilePrivilegesVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				profilePrivilegesVb.setProfileStatus(rs.getInt("PROFILE_STATUS"));
				profilePrivilegesVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				profilePrivilegesVb.setProfileStatusDesc(rs.getString("PROFILE_STATUS_DESC"));
				profilePrivilegesVb.setDateCreation(rs.getString("DATE_CREATION"));
				profilePrivilegesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				profilePrivilegesVb.setExcludeMenu(rs.getString("EXCLUDE_MENU_PROGRAM_LIST"));
				return profilePrivilegesVb;
			}
		};
		return mapper;
	}

	public List<RaProfileVb> getQueryPopupResults(RaProfileVb dObj) {
		StringBuffer strBufApprove = null;
		StringBuffer strBufPend = null;
		Vector<Object> params = new Vector<Object>();
		String userGroup = "";
		String userProfile = "";
		if (ValidationUtil.isValid(dObj.getUserGroup()) && !"ALL".equalsIgnoreCase(dObj.getUserGroup()))
			userGroup = " AND USER_GROUP = ? ";
		if (ValidationUtil.isValid(dObj.getUserProfile()) && !"ALL".equalsIgnoreCase(dObj.getUserProfile()))
			userProfile = " AND USER_PROFILE = ? ";
		strBufApprove = new StringBuffer(" SELECT *			  		    "
				+ "   FROM (SELECT User_Group,                                            "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 1 AND Alpha_sub_tab = T1.User_Group)  "
				+ "                   User_Group_desc,                                    "
				+ "                User_Profile,                                          "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 2 AND Alpha_sub_tab = T1.User_Profile)  "
				+ "                   User_Profile_desc,                                  " +
				// " t2.MENU_GROUP_SEQ Menu_group, "+
				" 				 T1.MENU_GROUP AS Menu_group,							"
				+ "                T2.MENU_GROUP_NAME, "
				+ "                P_Add,                                                 "
				+ "                P_Modify,                                              "
				+ "                P_Delete,                                              "
				+ "                P_Inquiry,                                             "
				+ "                P_Verification,                                        "
				+ "                P_Excel_Upload,P_DOWNLOAD,P_SUBMIT, 							"
				+ "				t1.MAKER,					"
				+ "				 t1.PROFILE_STATUS,										" + "				 "
				+ ProfileStatusNtApprDesc + ",							"
				+ "				 t1.RECORD_INDICATOR,										" + "				 "
				+ RecordIndicatorNtApprDesc + ",							"
				+ " 	(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(t1.MAKER,0) ) MAKER_NAME," + "  (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(t1.VERIFIER,0) ) VERIFIER_NAME, " + "   " + getDbFunction("DATEFUNC")
				+ "(t1.DATE_CREATION, '" + getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME")
				+ "') DATE_CREATION, " + "   " + getDbFunction("DATEFUNC") + "(t1.DATE_LAST_MODIFIED, '"
				+ getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME")
				+ "') DATE_LAST_MODIFIED,EXCLUDE_MENU_PROGRAM_LIST,  "
				+ "  	t1.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1 "
				+ "           FROM PRD_PROFILE_PRIVILEGES_NEW t1, PRD_MENU_GROUP t2          "
				+ "          WHERE UPPER(MENU_GROUP) = UPPER(T2.MENU_GROUP_SEQ) and T1.Application_Access = ?  "
				+ " 	and T1.Application_Access = T2.Application_Access " + " 	" + userGroup + " " + userProfile
				+ " " + "    and t2.MENU_GROUP_STATUS = 0 ) tappr ");

		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From PRD_PROFILE_PRIV_NEW_PEND TPend Where TPend.Application_Access = Application_Access "
						+ " and TPend.User_Group = tappr.User_Group and TPend.User_profile = tappr.User_profile "
						+ "  AND TPend.MENU_GROUP = tappr.Menu_group) ");

		strBufPend = new StringBuffer(" SELECT *			  		    "
				+ "   FROM (SELECT User_Group,                                            "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 1 AND Alpha_sub_tab = T1.User_Group)  "
				+ "                   User_Group_desc,                                    "
				+ "                User_Profile,                                          "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 2 AND Alpha_sub_tab = T1.User_Profile)  "
				+ "                   User_Profile_desc,                                  " +
				// " t2.MENU_GROUP_SEQ Menu_group, "+
				" 				 T1.MENU_GROUP AS Menu_group,							"
				+ "                T2.MENU_GROUP_NAME, "
				+ "                P_Add,                                                 "
				+ "                P_Modify,                                              "
				+ "                P_Delete,                                              "
				+ "                P_Inquiry,                                             "
				+ "                P_Verification,                                        "
				+ "                P_Excel_Upload,P_DOWNLOAD,P_SUBMIT, " + "				t1.MAKER,					"
				+ "				 t1.PROFILE_STATUS,										" + "				 "
				+ ProfileStatusNtPendDesc + ",							"
				+ "				 t1.RECORD_INDICATOR,										" + "				 "
				+ RecordIndicatorNtPendDesc + ",							"
				+ " 	(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(t1.MAKER,0) ) MAKER_NAME," + "  (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(t1.VERIFIER,0) ) VERIFIER_NAME, " + "   " + getDbFunction("DATEFUNC")
				+ "(t1.DATE_CREATION, '" + getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME")
				+ "') DATE_CREATION, " + "   " + getDbFunction("DATEFUNC") + "(t1.DATE_LAST_MODIFIED, '"
				+ getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME")
				+ "') DATE_LAST_MODIFIED,EXCLUDE_MENU_PROGRAM_LIST," + "  	t1.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1 "
				+ "           FROM PRD_PROFILE_PRIV_NEW_PEND t1, PRD_MENU_GROUP t2          "
				+ "          WHERE UPPER(MENU_GROUP) = UPPER(T2.MENU_GROUP_SEQ) and T1.Application_Access = ?  "
				+ " 	and T1.Application_Access = T2.Application_Access " + " 	" + userGroup + " " + userProfile
				+ " " + " and t2.MENU_GROUP_STATUS = 0 ) tpend ");

		params.addElement(productName);
		if (ValidationUtil.isValid(dObj.getUserGroup()) && !"ALL".equalsIgnoreCase(dObj.getUserGroup()))
			params.addElement(dObj.getUserGroup());
		if (ValidationUtil.isValid(dObj.getUserProfile()) && !"ALL".equalsIgnoreCase(dObj.getUserProfile()))
			params.addElement(dObj.getUserProfile());

		try {
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "userGroupDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.User_Group_desc) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.User_Group_desc) " + val, strBufPend,
								data.getJoinType());
						break;

					case "userProfileDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.User_Profile_desc) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.User_Profile_desc) " + val, strBufPend,
								data.getJoinType());
						break;

					case "profileStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.PROFILE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.PROFILE_STATUS_DESC) " + val, strBufPend,
								data.getJoinType());
						break;

					case "screenNameDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.MENU_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.MENU_NAME) " + val, strBufPend, data.getJoinType());
						break;

					case "profileAdd":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Add) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Add) " + val, strBufPend, data.getJoinType());
						break;

					case "profileModify":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Modify) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Modify) " + val, strBufPend, data.getJoinType());
						break;

					case "profileDelete":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Delete) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Delete) " + val, strBufPend, data.getJoinType());
						break;

					case "profileInquiry":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Inquiry) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Inquiry) " + val, strBufPend, data.getJoinType());
						break;

					case "profileVerification":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Verification) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Verification) " + val, strBufPend,
								data.getJoinType());
						break;

					case "profileUpload":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_Excel_Upload) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_Excel_Upload) " + val, strBufPend,
								data.getJoinType());
						break;

					case "profileDownload":
						CommonUtils.addToQuerySearch(" upper(TAPPR.P_DOWNLOAD) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.P_DOWNLOAD) " + val, strBufPend, data.getJoinType());
						break;

					case "menuGroupDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.MENU_GROUP_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.MENU_GROUP_NAME) " + val, strBufPend,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) " + val, strBufPend,
								data.getJoinType());
						break;

					case "dateCreation":
						CommonUtils.addToQuerySearch(" upper(TAPPR.DATE_CREATION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.DATE_CREATION) " + val, strBufPend,
								data.getJoinType());
						break;
					default:
					}
					count++;
				}
			}
			String orderBy = " Order by DATE_LAST_MODIFIED_1 desc,User_Group,User_Profile,MENU_GROUP_NAME ";
			return getQueryPopupResults(dObj, strBufPend, strBufApprove, strWhereNotExists, orderBy, params);

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List<RaProfileVb> getQueryResults(RaProfileVb dObj, int intStatus) {
		setServiceDefaults();
		List<RaProfileVb> collTemp = null;
		final int intKeyFieldsCount = 2;
		String userProfile = dObj.getUserGroup() + "-" + dObj.getUserProfile();
		StringBuffer strQueryAppr = new StringBuffer(" SELECT *			  		    "
				+ "   FROM (SELECT User_Group,                                            "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 1 AND Alpha_sub_tab = User_Group)  "
				+ "                   User_Group_desc,                                    "
				+ "                User_Profile,                                          "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 2 AND Alpha_sub_tab = User_Profile)  "
				+ "                   User_Profile_desc,                                  "
				+ "                t2.MENU_GROUP_SEQ Menu_group,                                         "
				+ "                T2.MENU_GROUP_NAME, "
				+ "                P_Add,                                                 "
				+ "                P_Modify,                                              "
				+ "                P_Delete,                                              "
				+ "                P_Inquiry,                                             "
				+ "                P_Verification,                                        "
				+ "                P_Excel_Upload,P_DOWNLOAD,P_SUBMIT, "
				+ "				t1.MAKER,t1.VERIFIER,					" + "				t1.PROFILE_STATUS,        "
				+ "				 t1.RECORD_INDICATOR,			" + "				 " + ProfileStatusNtApprDesc
				+ ",							" + "				 " + RecordIndicatorNtApprDesc
				+ ",							" + " 	(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(t1.MAKER,0) ) MAKER_NAME,"
				+ "  (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(t1.VERIFIER,0) ) VERIFIER_NAME, " + "   " + getDbFunction("DATEFUNC") + "(t1.DATE_CREATION, '"
				+ getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME") + "') DATE_CREATION, " + "   "
				+ getDbFunction("DATEFUNC") + "(t1.DATE_LAST_MODIFIED, '" + getDbFunction("DATEFORMAT") + " "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED,EXCLUDE_MENU_PROGRAM_LIST  "
				+ "           FROM PRD_PROFILE_PRIVILEGES_NEW t1, PRD_MENU_GROUP t2          "
				+ "          WHERE UPPER(MENU_GROUP) = UPPER(T2.MENU_GROUP_SEQ) and T1.Application_Access = '"
				+ productName + "' " + " 	and T1.Application_Access = T2.Application_Access  " + "	AND USER_GROUP"
				+ getDbFunction("PIPELINE") + "'-'" + getDbFunction("PIPELINE") + "USER_PROFILE IN (?)  "
				+ "   AND t1.MENU_GROUP = ? ) tappr ");

		StringBuffer strQueryPend = new StringBuffer(" SELECT *			  		    "
				+ "   FROM (SELECT User_Group,                                            "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 1 AND Alpha_sub_tab = User_Group)  "
				+ "                   User_Group_desc,                                    "
				+ "                User_Profile,                                          "
				+ "                (SELECT Alpha_subtab_description                       "
				+ "                   FROM alpha_Sub_tab                                  "
				+ "                  WHERE alpha_tab = 2 AND Alpha_sub_tab = User_Profile)  "
				+ "                   User_Profile_desc,                                  "
				+ "                t2.MENU_GROUP_SEQ Menu_group,                                         "
				+ "                T2.MENU_GROUP_NAME, "
				+ "                P_Add,                                                 "
				+ "                P_Modify,                                              "
				+ "                P_Delete,                                              "
				+ "                P_Inquiry,                                             "
				+ "                P_Verification,                                        "
				+ "                P_Excel_Upload,P_DOWNLOAD,P_SUBMIT, "
				+ "				t1.MAKER,t1.VERIFIER,					" + "				t1.PROFILE_STATUS,        "
				+ "				 t1.RECORD_INDICATOR,			" + "				 " + ProfileStatusNtPendDesc
				+ ",							" + "				 " + RecordIndicatorNtPendDesc
				+ ",							" + " 	(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
				+ getDbFunction("NVL") + "(t1.MAKER,0) ) MAKER_NAME,"
				+ "  (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(t1.VERIFIER,0) ) VERIFIER_NAME, " + "   " + getDbFunction("DATEFUNC") + "(t1.DATE_CREATION, '"
				+ getDbFunction("DATEFORMAT") + " " + getDbFunction("TIME") + "') DATE_CREATION, " + "   "
				+ getDbFunction("DATEFUNC") + "(t1.DATE_LAST_MODIFIED, '" + getDbFunction("DATEFORMAT") + " "
				+ getDbFunction("TIME") + "') DATE_LAST_MODIFIED,EXCLUDE_MENU_PROGRAM_LIST  "
				+ "           FROM PRD_PROFILE_PRIV_NEW_PEND t1, PRD_MENU_GROUP t2          "
				+ "          WHERE UPPER(MENU_GROUP) = UPPER(T2.MENU_GROUP_SEQ) and T1.Application_Access = '"
				+ productName + "' " + " 	and T1.Application_Access = T2.Application_Access  " + "	AND USER_GROUP"
				+ getDbFunction("PIPELINE") + "'-'" + getDbFunction("PIPELINE") + "USER_PROFILE IN (?)  "
				+ "   AND t1.MENU_GROUP = ? ) Pend ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getUserGroup() + "-" + dObj.getUserProfile());
		objParams[1] = new String(dObj.getMenuGroup());
		/*
		 * objParams[0] = new String(dObj.getUserGroup()); // [USER_GROUP] objParams[1]
		 * = new String(dObj.getUserProfile()); // [USER_PROFILE] objParams[2] = new
		 * String(dObj.getMenuGroup()); // [SCREEN NAME]
		 */ try {
			// logger.info("Executing approved query");
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getQueryResultsMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getQueryResultsMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			// logger.error(((strQueryAppr == null) ? "strQueryAppr is null" :
			// strQueryAppr.toString()));

			return null;
		}
	}

	@Override
	protected List<RaProfileVb> selectApprovedRecord(RaProfileVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RaProfileVb> doSelectPendingRecord(RaProfileVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "Profile Privileges";
		serviceDesc = "Profile Privileges";
		tableName = "PRD_PROFILE_PRIVILEGES_NEW";
		childTableName = "PRD_PROFILE_PRIV_NEW_PEND";
		// intCurrentUserId = CustomContextHolder.getContext().getVisionId();
		VisionUsersVb vObj = SessionContextHolder.getContext();
		intCurrentUserId = vObj.getVisionId();
		userGrpProfile = vObj.getUserGrpProfile();
	}

	@Override
	protected int getStatus(RaProfileVb records) {
		return records.getProfileStatus();
	}

	@Override
	protected void setStatus(RaProfileVb vObject, int status) {
		vObject.setProfileStatus(status);
	}

	@Override
	protected int doInsertionAppr(RaProfileVb vObject) {
		vObject.setProfileInquiry("Y");
		String query = "";
		query = "Insert Into PRD_Profile_privileges_New(USER_GROUP, USER_PROFILE, MENU_GROUP, "
				+ " P_ADD, P_MODIFY, P_DELETE, P_INQUIRY, P_VERIFICATION, P_EXCEL_UPLOAD,P_DOWNLOAD,P_SUBMIT,"
				+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
				+ " DATE_LAST_MODIFIED, DATE_CREATION,Application_Access,EXCLUDE_MENU_PROGRAM_LIST) "
				+ " Values (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, ?, ?, 0, " + getDbFunction("SYSDATE") + ", "
				+ getDbFunction("SYSDATE") + ",'" + productName + "',?)";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup(),
				vObject.getProfileAdd(), vObject.getProfileModify(), vObject.getProfileDelete(),
				vObject.getProfileInquiry(), vObject.getProfileVerification(), vObject.getProfileUpload(),
				vObject.getProfileDownload(),vObject.getProfileSubmit(), vObject.getMaker(), vObject.getVerifier(), vObject.getExcludeMenu() };
		return getJdbcTemplate().update(query, args);
	}

	private int doInsertTemplateAccess(RaProfileVb vObject, String dashboardId, int status) {
		String query = "";
		String tableName = "";
		if (status == Constants.STATUS_ZERO)
			tableName = "PRD_TEMPLATE_ACCESS";
		else
			tableName = "PRD_TEMPLATE_ACCESS_PEND";

		query = "Insert Into " + tableName + "(USER_GROUP,USER_PROFILE, TEMPLATE_ID, "
				+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
				+ " DATE_LAST_MODIFIED, DATE_CREATION,PRODUCT_NAME) " + " Values (?, ?, ?, 0, 0, ?, ?, 0, "
				+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ",?)";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), dashboardId, vObject.getMaker(),
				vObject.getVerifier(), productName };
		return getJdbcTemplate().update(query, args);
	}

	private int doDeleteTemplateAccess(RaProfileVb vObject, int status) {
		String query = "";
		String tableName = "";
		if (status == Constants.STATUS_ZERO)
			tableName = "PRD_TEMPLATE_ACCESS";
		else
			tableName = "PRD_TEMPLATE_ACCESS_PEND";

		query = "Delete From " + tableName + " Where USER_GROUP = ? And USER_PROFILE = ?  ";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(RaProfileVb vObject) {
		vObject.setProfileInquiry("Y");
		String query = "";
		query = "Insert Into PRD_PROFILE_PRIV_NEW_PEND(USER_GROUP, USER_PROFILE, MENU_GROUP, "
				+ " P_ADD, P_MODIFY, P_DELETE, P_INQUIRY, P_VERIFICATION, P_EXCEL_UPLOAD,P_DOWNLOAD,P_SUBMIT,"
				+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
				+ " DATE_LAST_MODIFIED, DATE_CREATION,Application_Access,EXCLUDE_MENU_PROGRAM_LIST) "
				+ " Values (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, " + getDbFunction("SYSDATE") + ", "
				+ getDbFunction("SYSDATE") + ",'" + productName + "',?)";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup(),
				vObject.getProfileAdd(), vObject.getProfileModify(), vObject.getProfileDelete(),
				vObject.getProfileInquiry(), vObject.getProfileVerification(), vObject.getProfileUpload(),
				vObject.getProfileDownload(),vObject.getProfileSubmit(), vObject.getProfileStatus(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getExcludeMenu() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(RaProfileVb vObject) {
		vObject.setProfileInquiry("Y");
		String dateCreation = "";
		if ("ORACLE".equalsIgnoreCase(databaseType))
			dateCreation = "To_Date(?, 'DD-MM-YYYY HH24:MI:SS')";
		else if ("MSSQL".equalsIgnoreCase(databaseType))
			dateCreation = "CONVERT(datetime, ?, 103)";

		String query = "";
		query = "Insert Into PRD_PROFILE_PRIV_NEW_PEND(USER_GROUP, USER_PROFILE, MENU_GROUP, "
				+ " P_ADD, P_MODIFY, P_DELETE, P_INQUIRY, P_VERIFICATION, P_EXCEL_UPLOAD,P_DOWNLOAD,P_SUBMIT,"
				+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
				+ " DATE_LAST_MODIFIED, DATE_CREATION,Application_Access,EXCLUDE_MENU_PROGRAM_LIST) "
				+ " Values (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, 0, " + getDbFunction("SYSDATE") + ", "
				+ dateCreation + ",'" + productName + "',?)";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup(),
				vObject.getProfileAdd(), vObject.getProfileModify(), vObject.getProfileDelete(),
				vObject.getProfileInquiry(), vObject.getProfileVerification(), vObject.getProfileUpload(),
				vObject.getProfileDownload(),vObject.getProfileSubmit(), vObject.getProfileStatus(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getDateCreation(), vObject.getExcludeMenu() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(RaProfileVb vObject) {
		vObject.setProfileInquiry("Y");
		String query = "";
		query = "Update PRD_PROFILE_PRIVILEGES_NEW Set "
				+ " P_ADD = ?, P_MODIFY = ?,P_DELETE = ?, P_INQUIRY = ?, P_VERIFICATION = ?, P_EXCEL_UPLOAD = ?, "
				+ " P_DOWNLOAD = ?,P_SUBMIT = ? ,MAKER = ?, VERIFIER = ?, " + " DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE")
				+ " ,EXCLUDE_MENU_PROGRAM_LIST= ? ,PROFILE_STATUS = ? "
				+ " Where USER_GROUP = ?  And USER_PROFILE = ?  " + " And MENU_GROUP = ? and Application_Access = '"
				+ productName + "' ";
		Object args[] = { vObject.getProfileAdd(), vObject.getProfileModify(), vObject.getProfileDelete(),
				vObject.getProfileInquiry(), vObject.getProfileVerification(), vObject.getProfileUpload(),
				vObject.getProfileDownload(),vObject.getProfileSubmit(), vObject.getMaker(), vObject.getVerifier(), vObject.getExcludeMenu(),
				vObject.getProfileStatus(), vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(RaProfileVb vObject) {
		vObject.setProfileInquiry("Y");
		String query = "";
		query = "Update PRD_PROFILE_PRIV_NEW_PEND Set "
				+ " P_ADD = ?, P_MODIFY = ?,P_DELETE = ?, P_INQUIRY = ?, P_VERIFICATION = ?, P_EXCEL_UPLOAD = ?, "
				+ " P_DOWNLOAD = ?,P_SUBMIT = ? ,MAKER = ?, VERIFIER = ?, " + " DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE")
				+ " ,EXCLUDE_MENU_PROGRAM_LIST= ?, PROFILE_STATUS = ?" + " Where USER_GROUP = ?  And USER_PROFILE = ?  "
				+ " And MENU_GROUP = ? and Application_Access = '" + productName + "' ";
		Object args[] = { vObject.getProfileAdd(), vObject.getProfileModify(), vObject.getProfileDelete(),
				vObject.getProfileInquiry(), vObject.getProfileVerification(), vObject.getProfileUpload(),
				vObject.getProfileDownload(),vObject.getProfileSubmit(), vObject.getMaker(), vObject.getVerifier(), vObject.getExcludeMenu(),
				vObject.getProfileStatus(), vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(RaProfileVb vObject) {
		String query = "Delete From PRD_Profile_privileges_NEW "
				+ " Where USER_GROUP = ? And USER_PROFILE = ? And MENU_GROUP = ? and Application_Access = ? ";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup(), productName };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(RaProfileVb vObject) {
		String query = "Delete From PRD_PROFILE_PRIV_NEW_PEND "
				+ " Where USER_GROUP = ? And USER_PROFILE = ? And MENU_GROUP = ? and Application_Access = ? ";
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getMenuGroup(), productName };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String frameErrorMessage(RaProfileVb vObject, String strOperation) {
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg = strErrMsg + " USER_GROUP:" + vObject.getUserGroup();
			strErrMsg = strErrMsg + " USER_PROFILE:" + vObject.getUserProfile();
			strErrMsg = strErrMsg + " SCREEN_NAME:" + vObject.getScreenName();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}

	@Override
	protected String getAuditString(RaProfileVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			strAudit.append("USER_GROUP_AT" + auditDelimiterColVal + vObject.getUserGroupAt());
			strAudit.append(auditDelimiter);
			if (ValidationUtil.isValid(vObject.getUserGroup()))
				strAudit.append("USER_GROUP" + auditDelimiterColVal + vObject.getUserGroup().trim());
			else
				strAudit.append("USER_GROUP" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("USER_PROFILE_AT" + auditDelimiterColVal + vObject.getUserProfileAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getUserProfile()))
				strAudit.append("USER_PROFILE" + auditDelimiterColVal + vObject.getUserProfile().trim());
			else
				strAudit.append("USER_PROFILE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("MENU_GROUP" + auditDelimiterColVal + vObject.getMenuGroup());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileAdd()))
				strAudit.append("P_ADD" + auditDelimiterColVal + vObject.getProfileAdd().trim());
			else
				strAudit.append("P_ADD" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileModify()))
				strAudit.append("P_MODIFY" + auditDelimiterColVal + vObject.getProfileModify().trim());
			else
				strAudit.append("P_MODIFY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileDelete()))
				strAudit.append("P_DELETE" + auditDelimiterColVal + vObject.getProfileDelete().trim());
			else
				strAudit.append("P_DELETE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileInquiry()))
				strAudit.append("P_INQUIRY" + auditDelimiterColVal + vObject.getProfileInquiry().trim());
			else
				strAudit.append("P_INQUIRY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileVerification()))
				strAudit.append("P_VERIFICATION" + auditDelimiterColVal + vObject.getProfileVerification().trim());
			else
				strAudit.append("P_VERIFICATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileUpload()))
				strAudit.append("P_EXCEL_UPLOAD" + auditDelimiterColVal + vObject.getProfileUpload().trim());
			else
				strAudit.append("P_EXCEL_UPLOAD" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProfileDownload()))
				strAudit.append("P_DOWNLOAD" + auditDelimiterColVal + vObject.getProfileDownload().trim());
			else
				strAudit.append("P_DOWNLOAD" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
			
			if (ValidationUtil.isValid(vObject.getProfileSubmit()))
				strAudit.append("P_SUBMIT" + auditDelimiterColVal + vObject.getProfileSubmit().trim());
			else
				strAudit.append("P_SUBMIT" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("PROFILE_STATUS_NT" + auditDelimiterColVal + vObject.getProfileStatusNt());
			strAudit.append(auditDelimiter);
			strAudit.append("PROFILE_STATUS" + auditDelimiterColVal + vObject.getProfileStatus());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if (ValidationUtil.isValid(vObject.getDateLastModified()))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDateCreation()))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	public List<AlphaSubTabVb> findScreenName(int menuGroup) {
		String sql = " Select MENU_PROGRAM ALPHA_SUB_TAB,Menu_Name ALPHA_SUBTAB_DESCRIPTION from PRD_VISION_MENU where  "
				+ "  Menu_Group = ? and Application_Access = '" + productName + "' order by menu_Sequence ";
		Object[] lParams = new Object[1];
		lParams[0] = menuGroup;
		return getJdbcTemplate().query(sql, lParams, getAlpSubTabMapper());
	}

	public List<NumSubTabVb> getTopLevelMenu() throws DataAccessException {
		String sql = " Select MENU_GROUP_SEQ,MENU_GROUP_NAME from PRD_MENU_GROUP where MENU_GROUP_Status = 0 and APPLICATION_ACCESS='"
				+ productName + "' ";
		Object[] lParams = new Object[1];
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				NumSubTabVb vObject = new NumSubTabVb();
				vObject.setDescription(rs.getString("MENU_GROUP_NAME"));
				vObject.setNumSubTab(rs.getInt("MENU_GROUP_SEQ"));
				return vObject;
			}
		};
		List<NumSubTabVb> profileData = getJdbcTemplate().query(sql, mapper);
		return profileData;
	}

	protected RowMapper getAlpSubTabMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	protected RowMapper getSavedReportsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				alphaSubTabVb.setApplicationId(rs.getString("APPLICATION_ID"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	protected RowMapper getReportLstMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				alphaSubTabVb.setApplicationId(rs.getString("APPLICATION_ID"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

	public List<AlphaSubTabVb> findUserGroupPP() {
		String sql = " Select distinct User_Group ALPHA_SUB_TAB,(Select alpha_subtab_description from Alpha_sub_tab "
				+ " where Alpha_tab = 1 and alpha_sub_tab=User_Group) ALPHA_SUBTAB_DESCRIPTION from PRD_PROFILE_PRIVILEGES_NEW  "
				+ " WHERE APPLICATION_ACCESS='" + productName + "' union  "
				+ " Select distinct User_Group ALPHA_SUB_TAB,(Select alpha_subtab_description from Alpha_sub_tab "
				+ " where Alpha_tab = 1 and alpha_sub_tab=User_Group) ALPHA_SUBTAB_DESCRIPTION from PRD_PROFILE_PRIV_NEW_PEND  "
				+ " WHERE APPLICATION_ACCESS='" + productName + "' ";
		Object[] lParams = new Object[0];
		return getJdbcTemplate().query(sql, lParams, getAlpSubTabMapper());
	}

	public List<AlphaSubTabVb> findUserProfilePP(String userGroup) {
		String sql = " Select distinct User_Profile ALPHA_SUB_TAB,(Select alpha_subtab_description from Alpha_sub_tab "
				+ " where Alpha_tab = 2 and alpha_sub_tab=User_Profile) ALPHA_SUBTAB_DESCRIPTION from PRD_PROFILE_PRIVILEGES_NEW "
				+ " where User_Group = ? " + " AND APPLICATION_ACCESS='" + productName + "' " + " UNION "
				+ " Select distinct User_Profile ALPHA_SUB_TAB,(Select alpha_subtab_description from Alpha_sub_tab "
				+ " where Alpha_tab = 2 and alpha_sub_tab=User_Profile) ALPHA_SUBTAB_DESCRIPTION from PRD_PROFILE_PRIV_NEW_PEND "
				+ " where User_Group = ? " + " AND APPLICATION_ACCESS='" + productName + "' ";
		Object[] lParams = new Object[2];
		lParams[0] = userGroup;
		lParams[1] = userGroup;
		return getJdbcTemplate().query(sql, lParams, getAlpSubTabMapper());
	}

	public int doDeleteProfileDashboard(RaProfileVb vObject) {
		try {
			String query = "Delete From PRD_PROFILE_DASHBOARDS Where " + " USER_GROUP" + getDbFunction("PIPELINE")
					+ "'-'" + getDbFunction("PIPELINE") + "USER_PROFILE IN (" + userGrpProfile + ") "
					+ " AND APPLICATION_ACCESS = ? ";
			Object args[] = { productName };
			return getJdbcTemplate().update(query, args);
		} catch (Exception e) {
			return 0;
		}
	}

	public int doInsertionProfileDashboard(RaProfileVb vObject) {
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = "Insert Into PRD_PROFILE_DASHBOARDS(USER_GROUP, USER_PROFILE, HOME_DASHBOARD,"
					+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
					+ " DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, 0, 0, ?, ?, 0, Sysdate, Sysdate,'" + productName + "')";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into PRD_PROFILE_DASHBOARDS(USER_GROUP, USER_PROFILE, HOME_DASHBOARD,"
					+ " PROFILE_STATUS, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS,"
					+ " DATE_LAST_MODIFIED, DATE_CREATION,APPLICATION_ACCESS) "
					+ " Values (?, ?, ?, 0, 0, ?, ?, 0, GETDATE(), GETDATE(),'" + productName + "')";
		}
		Object args[] = { vObject.getUserGroup(), vObject.getUserProfile(), vObject.getHomeDashboard(),
				vObject.getMaker(), vObject.getVerifier() };

		return getJdbcTemplate().update(query, args);
	}

//	// Get TEMPLATE List
	public List<AlphaSubTabVb> getAllTemplateList() {
		String sql = "";
		try {
				sql = " SELECT TEMPLATE_ID ALPHA_SUB_TAB,TEMPLATE_DESCRIPTION ALPHA_SUBTAB_DESCRIPTION, '"+productName+"'APPLICATION_ID FROM RG_TEMPLATE_CONFIG WHERE TEMPLATE_STATUS = 0  ORDER BY TEMPLATE_ID ";
				return getJdbcTemplate().query(sql, getReportLstMapper());
		} catch (Exception e) {
			return null;
		}
	}
//
	public List<AlphaSubTabVb> getSelectedTemplateList(RaProfileVb vObject, int status) {
		String sql = "";
		String tableName = "";
		if (status == Constants.STATUS_ZERO)
			tableName = "PRD_TEMPLATE_ACCESS";
		else
			tableName = "PRD_TEMPLATE_ACCESS_PEND";
		try {
			sql = "  SELECT TEMPLATE_ID ALPHA_SUB_TAB,'' ALPHA_SUBTAB_DESCRIPTION,'"+productName+"' APPLICATION_ID FROM "
					+ tableName + " WHERE USER_GROUP=?" + " AND USER_PROFILE = ? ORDER BY TEMPLATE_ID ";

			Object args[] = { vObject.getUserGroup(), vObject.getUserProfile() };
			return getJdbcTemplate().query(sql, args, getSavedReportsMapper());
		} catch (Exception e) {
			return null;
		}
	}
//	public List<AlphaSubTabVb> getSelectedReportsList(RaProfileVb vObject, int status) {
//		String sql = "";
//		String tableName = "";
//		if(status == Constants.STATUS_ZERO)
//			tableName = "PRD_REPORT_ACCESS";
//		else 	
//			tableName = "PRD_REPORT_ACCESS_PEND";
//		try {
//			sql = " SELECT REPORT_ID ALPHA_SUB_TAB,'' ALPHA_SUBTAB_DESCRIPTION,PRODUCT_NAME APPLICATION_ID FROM "+tableName+" WHERE USER_GROUP=?"
//					+ " AND USER_PROFILE = ? ORDER BY REPORT_ID ";
//
//			Object args[] = {vObject.getUserGroup(),vObject.getUserProfile()};
//			return getJdbcTemplate().query(sql,args,getSavedReportsMapper());
//		} catch (Exception e) {
//			return null;
//		}
//	}

	public ExceptionCode doInsertApprRecordForNonTrans(RaProfileVb vObject) throws RuntimeCustomException {
		List<RaProfileVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<RaProfileVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				logger.error("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.error("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		// Try inserting the record
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			if ("30".equalsIgnoreCase(vObject.getMenuGroup()) )
				insertTemplateAccess(vObject, Constants.STATUS_ZERO);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		/*
		 * exceptionCode = writeAuditLog(vObject, null); if(exceptionCode.getErrorCode()
		 * != Constants.SUCCESSFUL_OPERATION){ exceptionCode =
		 * getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

	public ExceptionCode doUpdateApprRecordForNonTrans(RaProfileVb vObject) throws RuntimeCustomException {
		List<RaProfileVb> collTemp = null;
		RaProfileVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			if ("30".equalsIgnoreCase(vObject.getMenuGroup()) )
				insertTemplateAccess(vObject, Constants.STATUS_ZERO);
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
//		String systemDate = 
		vObject.setDateLastModified(getSystemDate());
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		/*
		 * exceptionCode = writeAuditLog(vObject, vObjectlocal);
		 * if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
		 * exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

	protected ExceptionCode doDeleteApprRecordForNonTrans(RaProfileVb vObject) throws RuntimeCustomException {
		List<RaProfileVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		RaProfileVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<RaProfileVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());

		// delete the record from the Approve Table
		retVal = doDeleteAppr(vObject);
		if ("30".equalsIgnoreCase(vObject.getMenuGroup())) {
			doDeleteTemplateAccess(vObject,Constants.SUCCESSFUL_OPERATION);
		}
		/*
		 * if ("REPORTS".equalsIgnoreCase(vObject.getScreenName()) ||
		 * "reports".equalsIgnoreCase(vObject.getScreenName())) {
		 * doDeleteReportAccess(vObject); } else if
		 * ("DASHBOARDS".equalsIgnoreCase(vObject.getScreenName()) ||
		 * "dashboards".equalsIgnoreCase(vObject.getScreenName())) {
		 * doDeleteDashboardAccess(vObject); }
		 */
//			vObject.setRecordIndicator(-1);
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);

		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		/*
		 * if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
		 * exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

//
	private void insertTemplateAccess(RaProfileVb vObject, int status) {
		try {
			if ("30".equalsIgnoreCase(vObject.getMenuGroup())) {
				doDeleteTemplateAccess(vObject, status);
				if (vObject.getTemplatelst() != null && vObject.getTemplatelst().size() > 0) {
					vObject.getTemplatelst().forEach(dashboardVb -> {
						doInsertTemplateAccess(vObject, dashboardVb.getAlphaSubTab(), status);
					});
				}
			}
		} catch (Exception e) {
			logger.error("Error while inserting Dashboard Access:" + e.getMessage());

		}
	}

	protected ExceptionCode doInsertRecordForNonTrans(RaProfileVb vObject) throws RuntimeCustomException {
		List<RaProfileVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.ADD;
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int staticDeletionFlag = getStatus(((ArrayList<RaProfileVb>) collTemp).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE) {
				// logger.info("Collection size is greater than zero - Duplicate record found,
				// but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				// logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		// Try to see if the record already exists in the pending table, but not in
		// approved table
		collTemp = null;
		collTemp = doSelectPendingRecord(vObject);

		// The collTemp variable could not be null. If so, there is no problem fetching
		// data
		// return back error code to calling routine
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// if record already exists in pending table, modify the record
		if (collTemp.size() > 0) {
			RaProfileVb vObjectLocal = ((ArrayList<RaProfileVb>) collTemp).get(0);
			if (vObjectLocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				exceptionCode = getResultObject(Constants.PENDING_FOR_ADD_ALREADY);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			// Try inserting the record
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			vObject.setDateCreation(systemDate);
			retVal = doInsertionPend(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				if ("30".equalsIgnoreCase(vObject.getMenuGroup()) )
					insertTemplateAccess(vObject, Constants.STATUS_PENDING);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}
	}

	protected ExceptionCode doUpdateRecordForNonTrans(RaProfileVb vObject) throws RuntimeCustomException {
		List<RaProfileVb> collTemp = null;
		RaProfileVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		// Search if record already exists in pending. If it already exists, check for
		// status
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);

			// Check if the record is pending for deletion. If so return the error
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) {
				exceptionCode = getResultObject(Constants.RECORD_PENDING_FOR_DELETION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePend(vObject);
			} else {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePend(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			else {
				if ("30".equalsIgnoreCase(vObject.getMenuGroup()) )
					insertTemplateAccess(vObject,Constants.STATUS_PENDING);
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);

		} else {
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);

			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Even if record is not there in Appr. table reject the record
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// This is required for Audit Trail.
			if (collTemp.size() > 0) {
				vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendWithDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			else {
				if ("30".equalsIgnoreCase(vObject.getMenuGroup()) )
					insertTemplateAccess(vObject,Constants.STATUS_PENDING);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode doRejectRecord(RaProfileVb vObject) throws RuntimeCustomException {
		RaProfileVb vObjectlocal = null;
		List<RaProfileVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			if (vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3)
				vObject.setRecordIndicator(0);
			else
				vObject.setRecordIndicator(-1);
			// See if such a pending request exists in the pending table
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);
			// Delete the record from the Pending table
			if (deletePendingRecord(vObjectlocal) == 0) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				if ("30".equalsIgnoreCase(vObject.getMenuGroup()))
					doDeleteTemplateAccess(vObjectlocal, Constants.STATUS_PENDING);

			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Reject.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public ExceptionCode doApproveRecord(RaProfileVb vObject, boolean staticDelete) throws RuntimeCustomException {
		RaProfileVb oldContents = null;
		RaProfileVb vObjectlocal = null;
		List<RaProfileVb> collTemp = null;
		List<AlphaSubTabVb> templateLstPend = null;
		AlphaSubTabVb vObjectReportsPend = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
				exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// See if such a pending request exists in the pending table
			vObject.setVerifier(intCurrentUserId);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<RaProfileVb>) collTemp).get(0);

			if ("30".equalsIgnoreCase(vObject.getMenuGroup())) {
				templateLstPend = getSelectedTemplateList(vObject, Constants.STATUS_PENDING);
			}
			if (templateLstPend != null && !templateLstPend.isEmpty())
				vObjectlocal.setTemplatelst(templateLstPend);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<RaProfileVb>) collTemp).get(0);
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionAppr(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				else{
					if("30".equalsIgnoreCase(vObject.getMenuGroup())) 
						insertTemplateAccess(vObjectlocal,Constants.STATUS_ZERO);
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
				strApproveOperation = Constants.ADD;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization

				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// If record already exists in the approved table, reject the addition
				if (collTemp.size() > 0) {
					// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateAppr(vObjectlocal);
				}
				// Modify the existing contents of the record in Approved table
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				else{
					if("30".equalsIgnoreCase(vObject.getMenuGroup()) )
						insertTemplateAccess(vObjectlocal,Constants.STATUS_ZERO);
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) { // Delete authorization
				if (staticDelete) {
					// Update the existing record status in the Approved table to delete
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doUpdateAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);

				} else {
					// Delete the existing record from the Approved table
					retVal = doDeleteAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					} else {
						if ("30".equalsIgnoreCase(vObject.getMenuGroup()))
							doDeleteTemplateAccess(vObjectlocal, Constants.STATUS_ZERO);
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
				// Set the current operation to write to audit log
				strApproveOperation = Constants.DELETE;
			} else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Delete the record from the Pending table
			retVal = deletePendingRecord(vObjectlocal);

			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				if ("30".equalsIgnoreCase(vObject.getMenuGroup()))
					doDeleteTemplateAccess(vObjectlocal, Constants.STATUS_PENDING);
			}
			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				exceptionCode = writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				exceptionCode = writeAuditLog(vObjectlocal, oldContents);

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Approve.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
}
