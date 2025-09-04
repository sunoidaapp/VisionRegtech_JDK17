package com.vision.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.LevelOfDisplayVb;
import com.vision.vb.MenuVb;
import com.vision.vb.NotificationsVb;
import com.vision.vb.ProfileData;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.ServletContext;

@Component
public class CommonDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommonApiDao commonApiDao;

	@Value("${app.databaseType}")
	private String databaseType;

	@Value("${app.productName}")
	private String productName;

	@Value("${app.client}")
	private String clientType;

	@Value("${app.client}")
	private String client;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<CommonVb> findVerificationRequiredAndStaticDelete(String pTableName) throws DataAccessException {

		String sql = "select DELETE_TYPE,VERIFICATION_REQD FROM VISION_TABLES where UPPER(TABLE_NAME) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pTableName;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setStaticDelete(
						rs.getString("DELETE_TYPE") == null || rs.getString("DELETE_TYPE").equalsIgnoreCase("S") ? true
								: false);
				commonVb.setVerificationRequired(rs.getString("VERIFICATION_REQD") == null
						|| rs.getString("VERIFICATION_REQD").equalsIgnoreCase("Y") ? true : false);
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs == null || commonVbs.isEmpty()) {
			commonVbs = new ArrayList<CommonVb>();
			CommonVb commonVb = new CommonVb();
			commonVb.setStaticDelete(true);
			commonVb.setVerificationRequired(true);
			commonVbs.add(commonVb);
		}
		return commonVbs;
	}

	public List<ProfileData> getTopLevelMenu(int visionId) throws DataAccessException {
		String sql = "SELECT distinct NST.NUM_SUBTAB_DESCRIPTION, PP.MENU_GROUP,PP.MENU_ICON ,"
				+ "PP.P_ADD, PP.P_MODIFY, PP.P_DELETE, PP.P_INQUIRY, PP.P_VERIFICATION , PP.P_EXCEL_UPLOAD "
				+ "FROM PROFILE_PRIVILEGES PP, VISION_USERS MU, NUM_SUB_TAB NST "
				+ "where PP.USER_GROUP = MU.USER_GROUP and PP.USER_PROFILE = MU.USER_PROFILE "
				+ "and  NST.NUM_SUB_TAB = PP.MENU_GROUP and MU.VISION_ID = ? AND PP.PROFILE_STATUS = 0 AND NST.NUM_SUBTAB_STATUS=0 "
				+ "and num_tab = 176 "
				// + "and pp.Application_Access Like '%"+productName+"%' "
				+ "order by PP.MENU_GROUP";
//		System.out.println(sql);
		Object[] lParams = new Object[1];
		lParams[0] = visionId;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setMenuItem(rs.getString("NUM_SUBTAB_DESCRIPTION"));
				profileData.setMenuGroup(rs.getInt("MENU_GROUP"));
				profileData.setProfileAdd(rs.getString("P_ADD"));
				profileData.setProfileModify(rs.getString("P_MODIFY"));
				profileData.setProfileDelete(rs.getString("P_DELETE"));
				profileData.setProfileInquiry(rs.getString("P_INQUIRY"));
				profileData.setProfileVerification(rs.getString("P_VERIFICATION"));
				profileData.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				profileData.setMenuIcon(rs.getString("MENU_ICON"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql, lParams, mapper);
		return profileData;
	}

	public List<ProfileData> getTopLevelMenu() throws DataAccessException {
		String sql = " Select MENU_GROUP_SEQ,MENU_GROUP_NAME,MENU_GROUP_ICON from PRD_MENU_GROUP where MENU_GROUP_Status = 0 "
				+ "and Application_Access = ? ORDER BY MENU_GROUP_SEQ ";
		Object[] lParams = new Object[1];
		lParams[0] = productName;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setMenuItem(rs.getString("MENU_GROUP_NAME"));
				profileData.setMenuGroup(rs.getInt("MENU_GROUP_SEQ"));
				profileData.setMenuIcon(rs.getString("MENU_GROUP_ICON"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql, lParams, mapper);
		return profileData;
	}

	public ArrayList<MenuVb> getSubMenuItemsForMenuGroup(int menuGroup, VisionUsersVb visionUsersVb)
			throws DataAccessException {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = " SELECT * " + " FROM PRD_VISION_MENU T1 " + " JOIN PRD_PROFILE_PRIVILEGES_NEW T2  "
					+ " ON T1.MENU_GROUP = T2.MENU_GROUP " + " WHERE T1.MENU_GROUP = ? " + " AND T1.MENU_STATUS = 0 "
					+ " AND UPPER(T1.MENU_NAME) != 'SEPERATOR' " + " AND T1.APPLICATION_ACCESS = ? "
					+ " AND T1.MENU_SEQUENCE = T1.PARENT_SEQUENCE "
					+ " AND (T2.USER_GROUP || '-' || T2.USER_PROFILE) = ? "
					+ " AND NOT EXISTS (SELECT 1 FROM PRD_PROFILE_PRIVILEGES_NEW T3 "
					+ " WHERE T3.MENU_GROUP = T1.MENU_GROUP " + " AND (T3.USER_GROUP || '-' || T3.USER_PROFILE) = ? "
					+ " AND ',' || TRIM(T3.EXCLUDE_MENU_PROGRAM_LIST) || ',' LIKE '%,' || TRIM(T1.MENU_PROGRAM) || ',%') "
					+ " ORDER BY T1.PARENT_SEQUENCE, T1.MENU_SEQUENCE ";
		} else {
			sql = " SELECT * " + " FROM PRD_VISION_MENU T1 " + " JOIN PRD_PROFILE_PRIVILEGES_NEW T2  "
					+ " ON T1.MENU_GROUP = T2.MENU_GROUP " + " WHERE T1.MENU_GROUP = ? " + " AND T1.MENU_STATUS = 0 "
					+ " AND UPPER(T1.MENU_NAME) != 'SEPERATOR' " + " AND T1.APPLICATION_ACCESS = ? "
					+ " AND T1.MENU_SEQUENCE = T1.PARENT_SEQUENCE "
					+ " AND (T2.USER_GROUP + '-' + T2.USER_PROFILE) = ? "
					+ " AND NOT EXISTS (SELECT 1 FROM PRD_PROFILE_PRIVILEGES_NEW T3 "
					+ " WHERE T3.MENU_GROUP = T1.MENU_GROUP " + " AND (T3.USER_GROUP + '-' + T3.USER_PROFILE) = ? "
					+ " AND ',' + TRIM(T3.EXCLUDE_MENU_PROGRAM_LIST) + ',' LIKE '%,' + TRIM(T1.MENU_PROGRAM) + ',%') "
					+ " ORDER BY T1.PARENT_SEQUENCE, T1.MENU_SEQUENCE ";
		}
		String grpProfile = visionUsersVb.getUserGroup() + "-" + visionUsersVb.getUserProfile();
		Object[] lParams = new Object[4];
		lParams[0] = menuGroup;
		lParams[1] = productName;
		lParams[2] = grpProfile;
		lParams[3] = grpProfile;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				menuVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				return menuVb;
			}
		};
		ArrayList<MenuVb> menuList = (ArrayList<MenuVb>) getJdbcTemplate().query(sql, lParams, mapper);
		return menuList;
	}

	public ArrayList<MenuVb> getSubMenuItemsForSubMenuGroup(int menuGroup, int parentSequence, int visionId)
			throws DataAccessException {
		String sql = "";
		if ("OLD".equalsIgnoreCase(clientType)) {
			sql = " SELECT *                                                          "
					+ "     FROM PRD_VISION_MENU t1, PRD_PROFILE_PRIVILEGES t2, VISION_USERS t3   "
					+ "    WHERE     MENU_GROUP = ?                                               "
					+ "          AND MENU_STATUS = 0                                              "
					+ "          AND UPPER (MENU_NAME) != 'SEPERATOR'                             "
					+ "          AND t1.Application_Access = '" + productName + "'                "
					+ " 		 AND t1.Application_Access = t2.Application_Access "
					+ "          AND Menu_Sequence <> Parent_Sequence                             "
					+ "          AND Parent_Sequence = " + parentSequence + "                         "
					+ "          AND T1.MENU_PROGRAM = t2.screen_Name                                "
					+ "          AND t3.vision_ID = '" + visionId + "'                                "
					+ "          AND t2.User_Group = t3.user_Group                                "
					+ "          AND T2.USER_PROFILE = t3.User_Profile                            "
					+ " ORDER BY PARENT_SEQUENCE, MENU_SEQUENCE                                   ";
		} else {
			sql = " SELECT *                                                          "
					+ "     FROM PRD_VISION_MENU t1, PRD_PROFILE_PRIVILEGES t2, VISION_USERS t3   "
					+ "    WHERE     MENU_GROUP = ?                                               "
					+ "          AND MENU_STATUS = 0                                              "
					+ "          AND UPPER (MENU_NAME) != 'SEPERATOR'                             "
					+ "          AND t1.Application_Access = '" + productName + "'                  "
					+ " 		 AND t1.Application_Access = t2.Application_Access "
					+ "          AND Menu_Sequence <> Parent_Sequence                             "
					+ "          AND Parent_Sequence = " + parentSequence + "                         "
					+ "          AND T1.MENU_PROGRAM = t2.screen_Name                                "
					+ "          AND t3.vision_ID = '" + visionId + "'                                "
					+ "          AND t2.User_Group||'-'||T2.USER_PROFILE = t3.USER_GRP_PROFILE    "
					+ " ORDER BY PARENT_SEQUENCE, MENU_SEQUENCE                                   ";
		}

		Object[] lParams = new Object[1];
		lParams[0] = menuGroup;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				menuVb.setProfileAdd(rs.getString("P_ADD"));
				menuVb.setProfileModify(rs.getString("P_MODIFY"));
				menuVb.setProfileDelete(rs.getString("P_DELETE"));
				menuVb.setProfileView(rs.getString("P_INQUIRY"));
				menuVb.setProfileVerification(rs.getString("P_VERIFICATION"));
				menuVb.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				menuVb.setProfileDownload(rs.getString("P_DOWNLOAD"));
				return menuVb;
			}
		};
		ArrayList<MenuVb> menuList = (ArrayList<MenuVb>) getJdbcTemplate().query(sql, lParams, mapper);
		return menuList;
	}

	public String findVisionVariableValue(String pVariableName) throws DataAccessException {
		if (!ValidationUtil.isValid(pVariableName)) {
			return null;
		}
		String sql = "select VALUE FROM VISION_VARIABLES where UPPER(VARIABLE) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pVariableName;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("VALUE"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs != null && !commonVbs.isEmpty()) {
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}

	public void findDefaultHomeScreen(VisionUsersVb vObject) throws DataAccessException {
		int count = 0;
		String sql = "SELECT COUNT(1) FROM PWT_REPORT_SUITE WHERE VISION_ID = " + vObject.getVisionId();
		count = getJdbcTemplate().queryForObject(sql, Integer.class);
		/*
		 * if(count>0){ vObject.setDefaultHomeScreen(true); }
		 */
	}

	public int getMaxOfId() {
		String sql = "select max(vision_id) from (Select max(vision_id) vision_id from vision_users UNION ALL select Max(vision_id) from vision_users_pend)";
		int i = getJdbcTemplate().queryForObject(sql, Integer.class);
		return i;
	}

	public String getVisionBusinessDayForExpAnalysis(String countryLeBook) {
		Object args[] = { countryLeBook };
		return getJdbcTemplate().queryForObject(
				"select TO_CHAR(BUSINESS_DATE,'Mon-RRRR') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY ||'-'|| LE_BOOK=?",
				args, String.class);
	}

	public String getyearMonthForTop10Deals(String countryLeBook) {
		Object args[] = { countryLeBook };
		return getJdbcTemplate().queryForObject(
				"select TO_CHAR(BUSINESS_DATE,'RRRRMM') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY ||'-'|| LE_BOOK=?",
				args, String.class);
	}

	public String getVisionBusinessDate(String countryLeBook) {
		Object args[] = { countryLeBook };
		return getJdbcTemplate().queryForObject(
				"select TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE  from Vision_Business_Day  WHERE COUNTRY ||'-'|| LE_BOOK=?",
				args, String.class);
	}

	public String getVisionCurrentYearMonth() {
		return getJdbcTemplate().queryForObject(
				"select to_char(to_date(CURRENT_YEAR_MONTH,'RRRRMM'),'Mon-RRRR') CURRENT_YEAR_MONTH  from V_Curr_Year_Month",
				String.class);
	}

	public int getUploadCount() {
		String sql = "Select count(1) from Vision_Upload where Upload_Status = 1 AND FILE_NAME LIKE '%XLSX'";
		int i = getJdbcTemplate().queryForObject(sql, Integer.class);
		return i;
	}

	public int doPasswordResetInsertion(VisionUsersVb vObject) {
		Date oldDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		String resetValidity = df.format(oldDate);
		if (ValidationUtil.isValid(vObject.getPwdResetTime())) {
			Date newDate = DateUtils.addHours(oldDate, Integer.parseInt(vObject.getPwdResetTime()));
			resetValidity = df.format(newDate);
		}
		String query = "Insert Into FORGOT_PASSWORD ( VISION_ID, RESET_DATE, RESET_VALIDITY, RS_STATUS_NT, RS_STATUS)"
				+ "Values (?, GetDate(), CONVERT(datetime, ?, 103), ?, ?)";
		Object[] args = { vObject.getVisionId(), resetValidity, vObject.getUserStatusNt(), vObject.getUserStatus() };
		return getJdbcTemplate().update(query, args);
	}

	public List<LevelOfDisplayVb> getQueryUserGroupProfile() throws DataAccessException {
		String sql = "SELECT USER_GROUP, USER_PROFILE FROM PROFILE_PRIVILEGES " + " GROUP BY USER_GROUP, USER_PROFILE";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				LevelOfDisplayVb lodVb = new LevelOfDisplayVb();
				lodVb.setUserGroup(rs.getString("USER_GROUP"));
				lodVb.setUserProfile(rs.getString("USER_PROFILE"));
				return lodVb;
			}
		};
		List<LevelOfDisplayVb> lodVbList = getJdbcTemplate().query(sql, mapper);
		return lodVbList;
	}

	public List<ProfileData> getQueryUserGroup() throws DataAccessException {
		String sql = "SELECT DISTINCT USER_GROUP FROM PROFILE_PRIVILEGES ORDER BY USER_GROUP";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setUserGroup(rs.getString("USER_GROUP"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql, mapper);
		return profileData;
	}

	public List<ProfileData> getQueryUserGroupBasedProfile(String userGroup) throws DataAccessException {
		String sql = "SELECT DISTINCT USER_PROFILE,USER_GROUP FROM PROFILE_PRIVILEGES where USER_GROUP ='" + userGroup
				+ "' ORDER BY USER_GROUP";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setUserGroup(rs.getString("USER_GROUP"));
				profileData.setUserProfile(rs.getString("USER_PROFILE"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql, mapper);
		return profileData;
	}

	public String getSystemDate() {
		String sql = "SELECT To_Char(SysDate, 'DD-MM-YYYY HH24:MI:SS') AS SYSDATE1 FROM DUAL";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return (rs.getString("SYSDATE1"));
			}
		};
		return (String) getJdbcTemplate().queryForObject(sql, null, mapper);
	}

	public String getSystemDate12Hr() {
		String sql = "SELECT To_Char(SysDate, 'DD-MM-YYYY HH:MI:SS') AS SYSDATE1 FROM DUAL";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return (rs.getString("SYSDATE1"));
			}
		};
		return (String) getJdbcTemplate().queryForObject(sql, null, mapper);
	}

	public String getScriptValue(String pVariableName) throws DataAccessException, Exception {
		Object params[] = { pVariableName };
		String sql = new String(
				"select VARIABLE_SCRIPT from VISION_DYNAMIC_HASH_VAR WHERE VARIABLE_TYPE = 2  AND UPPER(VARIABLE_NAME)=UPPER(?)");
		return getJdbcTemplate().queryForObject(sql, params, String.class);
	}

	public List<AlphaSubTabVb> getAvailableNodesLst() throws DataAccessException {
		String sql = "SELECT NODE_NAME,NODE_DESCRIPTION FROM VISION_NODE_CREDENTIALS WHERE NODE_STATUS = 0 ORDER BY NODE_DESCRIPTION";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObj = new AlphaSubTabVb();
				vObj.setAlphaSubTab(rs.getString("NODE_NAME"));
				vObj.setDescription(rs.getString("NODE_DESCRIPTION"));
				return vObj;
			}
		};
		List<AlphaSubTabVb> nodeAvailablelst = getJdbcTemplate().query(sql, mapper);
		return nodeAvailablelst;
	}

	// Deepak maintained seperately.Already avialble on AbstractCommonDao
	public String parseErrorMsgCommon(UncategorizedSQLException ecxception) {
		String strErrorDesc = ecxception.getSQLException() != null ? ecxception.getSQLException().getMessage()
				: ecxception.getMessage();
		String sqlErrorCodes[] = { "ORA-00928:", "ORA-00942:", "ORA-00998:", "ORA-01400:", "ORA-01722:", "ORA-04098:",
				"ORA-01810:", "ORA-01840:", "ORA-01843:", "ORA-20001:", "ORA-20002:", "ORA-20003:", "ORA-20004:",
				"ORA-20005:", "ORA-20006:", "ORA-20007:", "ORA-20008:", "ORA-20009:", "ORA-200010:", "ORA-20011:",
				"ORA-20012:", "ORA-20013:", "ORA-20014:", "ORA-20015:", "ORA-20016:", "ORA-20017:", "ORA-20018:",
				"ORA-20019:", "ORA-20020:", "ORA-20021:", "ORA-20022:", "ORA-20023:", "ORA-20024:", "ORA-20025:",
				"ORA-20102:", "ORA-20105:", "ORA-01422:", "ORA-06502:", "ORA-20082:", "ORA-20030:", "ORA-20010:",
				"ORA-20034:", "ORA-20043:", "ORA-20111:", "ORA-06512:", "ORA-04088:", "ORA-06552:", "ORA-00001:" };
		for (String sqlErrorCode : sqlErrorCodes) {
			if (ValidationUtil.isValid(strErrorDesc) && strErrorDesc.lastIndexOf(sqlErrorCode) >= 0) {
				strErrorDesc = strErrorDesc.substring(
						strErrorDesc.lastIndexOf(sqlErrorCode) + sqlErrorCode.length() + 1, strErrorDesc.length());
				if (strErrorDesc.indexOf("ORA-06512:") >= 0) {
					strErrorDesc = strErrorDesc.substring(0, strErrorDesc.indexOf("ORA-06512:"));
				}
			}
		}
		return strErrorDesc;
	}

	public List<NotificationsVb> getMdmNotification(String notifyType) throws DataAccessException {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		String sql = " Select Sequence,Notification_Type,to_Char(Notification_date,'DD-MM-RRRR') Notify_date,CIF_ID,CIF_Name,Vision_Sbu,Vision_Ouc,Deal_No,"
				+ " (select Alpha_subtab_description from alpha_sub_tab where Alpha_sub_tab= Deal_stage and Alpha_tab =6007) Deal_stage_desc,"
				+ " to_Char(action_date,'DD-MM-RRRR') action_date,Case when notification_status = 0 then 'Y' "
				+ " else 'N' End Read_Flag " + " from mdm_notification where Action_Date <= sysdate "
				+ " and Notification_Type = '" + notifyType + "' ";

		if ("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction())) {
			if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
				sql = sql + "and Country in ('" + visionUsersVb.getCountry() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
				sql = sql + "and LE_Book in ('" + visionUsersVb.getLeBook() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getAccountOfficer())) {
				sql = sql + "and Account_officer in ('" + visionUsersVb.getAccountOfficer() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getOucAttribute())) {
				sql = sql + "and Vision_OUC in ('" + visionUsersVb.getOucAttribute() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getSbuCode())) {
				sql = sql + "and Vision_sbu in ('" + visionUsersVb.getSbuCode() + "')";
			}
		}
		sql = sql + " Order by Sequence ";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				NotificationsVb vObj = new NotificationsVb();
				vObj.setSequence(rs.getString("Sequence"));
				vObj.setNotificationType(rs.getString("Notification_Type"));
				vObj.setNotificationDate(rs.getString("Notify_date"));
				vObj.setCifID(rs.getString("CIF_ID"));
				vObj.setCifName(rs.getString("CIF_Name"));
				vObj.setVisionSbu(rs.getString("Vision_Sbu"));
				vObj.setVisionOuc(rs.getString("Vision_Ouc"));
				vObj.setDealNo(rs.getString("Deal_No"));
				vObj.setDealStage(rs.getString("Deal_stage_desc"));
				vObj.setActionDate(rs.getString("action_date"));
				vObj.setReadFlag(rs.getString("Read_Flag"));
				return vObj;
			}
		};
		List<NotificationsVb> nodeAvailablelst = getJdbcTemplate().query(sql, mapper);
		return nodeAvailablelst;
	}

	public int updateNotification(NotificationsVb vObject) {
		String query = " Update MDM_Notification set NOTIFICATION_STATUS = 0,NOTIFICATION_READ_DATE= sysdate where Sequence = '"
				+ vObject.getSequence() + "' ";
		Object[] args = {};
		return getJdbcTemplate().update(query, args);
	}

	public int updateDealActionDate(NotificationsVb vObject) {
		String query = " Update Deal_entry set NEXT_ACTION_DATE = To_Date('" + vObject.getActionDate()
				+ "','DD-MM-RRRR') where Deal_ID = '" + vObject.getDealNo() + "' ";
		Object[] args = {};
		return getJdbcTemplate().update(query, args);
	}

	public List<AlphaSubTabVb> findAccountOfficer(String module) {
		String sql = " Select distinct account_Officer ALPHA_SUB_TAB,account_Officer||' - '||AO_Name Alpha_subtab_description from Account_Officers Alpha_subtab_description where Ao_Status = 0 ";
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
			if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
				sql = sql + " and Country IN ('" + visionUsersVb.getCountry() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
				sql = sql + " and COUNTRY+'-'+LE_BOOK IN ('" + visionUsersVb.getLeBook() + "') ";
			}
			if (ValidationUtil.isValid(visionUsersVb.getAccountOfficer())) {
				sql = sql + " and account_Officer IN ('" + visionUsersVb.getAccountOfficer() + "'"
						+ restrictedAoPermission(module) + ") ";
			}
		}
		sql = sql + " Order by account_Officer ";
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObj = new AlphaSubTabVb();
				vObj.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				vObj.setDescription(rs.getString("Alpha_subtab_description"));
				return vObj;
			}
		};
		return getJdbcTemplate().query(sql, mapper);
	}

	public String restrictedAoPermission(String module) {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		String allowedAoAccess = "";
		if (ValidationUtil.isValid(visionUsersVb.getRestrictedAo())) {
			String aoBackuplst[] = visionUsersVb.getRestrictedAo().split(",");
			for (int ctr = 0; ctr < aoBackuplst.length; ctr++) {
				String accessXml = getAoAccessRights(visionUsersVb.getAccountOfficer(), aoBackuplst[ctr]);
				String permission = CommonUtils.getValueForXmlTag(accessXml, module);
				if (ValidationUtil.isValid(permission) && "Y".equalsIgnoreCase(permission)) {
					if (ValidationUtil.isValid(allowedAoAccess)) {
						allowedAoAccess = allowedAoAccess + "','" + aoBackuplst[ctr];
					} else {
						allowedAoAccess = aoBackuplst[ctr];
					}
				}
			}
			allowedAoAccess = ",'" + allowedAoAccess + "'";
		}
		return allowedAoAccess;
	}

	public String getAoAccessRights(String accountOfficer, String backupAo) {
		try {
			String query = " SELECT " + "  Access_Rights_Xml " + "  FROM MDM_AO_LEAVE_MGMT Where account_Officer = '"
					+ backupAo + "' " + "  and AO_back_up = '" + accountOfficer
					+ "' and To_date(sysdate,'DD-MM-RRRR') >= To_date(leave_from,'DD-MM-RRRR') "
					+ "  and To_date(sysdate,'DD-MM-RRRR') <= To_date(leave_to,'DD-MM-RRRR') ";
			return getJdbcTemplate().queryForObject(query, null, String.class);
		} catch (Exception e) {
			return null;
		}
	}

	public String getUserHomeDashboard(String userGroupProfile) {
		String homeDashboard = "NA";
		try {
			String query = " Select NVL(Home_dashboard,'NA') from MDM_PROFILE_DASHBOARDS where User_group||'-'||User_Profile in ('"
					+ userGroupProfile + "')";
			return getJdbcTemplate().queryForObject(query, null, String.class);
		} catch (Exception e) {
			return homeDashboard;
		}
	}

	/* Update to SQL Query */
	public String getUserHomeDashboard(VisionUsersVb visionUsersVb) {
		String homeDashboard = "NA";
		String query = "";
		try {
			if ("NEW".equalsIgnoreCase(client)) {
				String[] userGrpPfl = visionUsersVb.getUserGrpProfile().split("-");
				query = " Select NVL(Home_dashboard,'NA') from MDM_PROFILE_DASHBOARDS where " + "User_group = '"
						+ userGrpPfl[0] + "' AND User_Profile = '" + userGrpPfl[1] + "' ";
				homeDashboard = getJdbcTemplate().queryForObject(query, String.class);
			} else if ("OLD".equalsIgnoreCase(client)) {
				query = " Select NVL(Home_dashboard,'NA') from MDM_PROFILE_DASHBOARDS where "
						+ "User_group =? AND User_Profile = ? ";
				Object[] lParams = new Object[2];
				lParams[0] = visionUsersVb.getUserGroup();
				lParams[1] = visionUsersVb.getUserProfile();
				// lParams[2] = productName;
				homeDashboard = getJdbcTemplate().queryForObject(query, lParams, String.class);
			}
		} catch (Exception e) {
		}
		return homeDashboard;
	}

	// This Function is Hard Coded for SBU Logic for Fidelity
	public String getVisionSbu(String parentVal) {
		String sbu = "";
		try {
			sbu = getJdbcTemplate().queryForObject(" SELECT DISTINCT "
					+ " ''''|| LISTAGG (VISION_SBU, ''',''') WITHIN GROUP (ORDER BY VISION_SBU)|| '''' "
					+ " Sbu FROM (SELECT DISTINCT VISION_SBU FROM VISION_SBU_MDM " + " WHERE    VISION_SBU IN ("
					+ parentVal + ") OR PARENT_SBU IN (" + parentVal + ") OR DIVISON IN (" + parentVal + ") "
					+ " OR BANK_GROUP IN (" + parentVal + ") ) ", null, String.class);

//			System.out.println("Sbu Query:" + sbu);
		} catch (Exception e) {
//			System.out.println("Error while getting SBU:" + e.getMessage());
			e.printStackTrace();
			sbu = "";
		}
		return sbu;
	}

//	public static String getMacAddress(String ip) throws IOException {
//		String address = null;
//		String str = "";
//		String macAddress = "";
//		try {
//
//			String cmd = "arp -a " + ip;
//			Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream());
//			Pattern pattern = Pattern
//					.compile("(([0-9A-Fa-f]{2}[-:]){5}[0-9A-Fa-f]{2})|(([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4})");
//			try {
//				while (s.hasNext()) {
//					str = s.next();
//					Matcher matcher = pattern.matcher(str);
//					if (matcher.matches()) {
//						break;
//					} else {
//						str = null;
//					}
//				}
//			} finally {
//				s.close();
//			}
//			if (!ValidationUtil.isValid(str)) {
//				return ip;
//			}
//			return (str != null) ? str.toUpperCase() : null;
//		} catch (SocketException ex) {
//			ex.printStackTrace();
//			return ip;
//		}
//	}

	// Method to get MAC Address for local or remote IP
	public static String getMacAddress(String ip) {
		String macAddress = "";

		try {
			// Step 1: Check if IP is local (on the same machine)
			InetAddress inetAddress = InetAddress.getByName(ip);
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

			if (networkInterface != null) {
				byte[] macBytes = networkInterface.getHardwareAddress();
				if (macBytes != null) {
					// Convert bytes to MAC address format
					StringBuilder macBuilder = new StringBuilder();
					for (byte b : macBytes) {
						macBuilder.append(String.format("%02X:", b));
					}
					macAddress = macBuilder.substring(0, macBuilder.length() - 1); // Remove the trailing colon
					return macAddress;
				}
			}

			// Step 2: If no local MAC address found, try fetching from ARP cache for remote
			// IPs
			macAddress = getMacAddressFromARP(ip);

			if (macAddress.isEmpty()) {
				macAddress = ip; // Return IP if MAC address not found
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return macAddress.toUpperCase();
	}

	// Method to fetch MAC address using ARP or IP neighbor command (Linux/Windows)
	private static String getMacAddressFromARP(String ip) {
		String macAddress = "";

		try {
			// For Windows: arp -a <IP>
			// For Linux: ip neighbour show <IP> or arp -n <IP>
			String cmd = System.getProperty("os.name").toLowerCase().contains("win") ? "arp -a " + ip
					: "ip neighbour show " + ip;

			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			// Regex pattern to match MAC address (both standard and dot notation)
			Pattern pattern = Pattern
					.compile("(([0-9A-Fa-f]{2}[-:]){5}[0-9A-Fa-f]{2})|(([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4})");

			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					macAddress = matcher.group();
					break;
				}
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return macAddress;
	}

	public HashMap<String, String> getAllBusinessDate() {
		try {
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "SELECT COUNTRY+'-'+LE_BOOK COUNTRY,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM Vision_Business_Day";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "SELECT COUNTRY||'-'||LE_BOOK COUNTRY,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE FROM Vision_Business_Day";
			}
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					HashMap<String, String> businessDateMap = new HashMap<String, String>();
					while (rs.next()) {
						for (int cn = 1; cn <= colCount; cn++) {
							businessDateMap.put(rs.getString("COUNTRY"), rs.getString("BUSINESS_DATE"));
						}
					}
					return businessDateMap;
				}
			};
			return (HashMap<String, String>) getJdbcTemplate().query(query, mapper);
		} catch (Exception e) {
			return null;
		}
	}

	public String getDbFunction(String reqFunction) {
		String functionName = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			switch (reqFunction) {
			case "DATEFUNC":
				functionName = "FORMAT";
				break;
			case "SYSDATE":
				functionName = "GetDate()";
				break;
			case "NVL":
				functionName = "ISNULL";
				break;
			case "TIME":
				functionName = "HH:mm:ss";
				break;
			case "DATEFORMAT":
				functionName = "dd-MMM-yyyy";
				break;
			case "CONVERT":
				functionName = "CONVERT";
				break;
			case "TYPE":
				functionName = "varchar,";
				break;
			case "TIMEFORMAT":
				functionName = "108";
				break;
			case "PIPELINE":
				functionName = "+";
				break;
			case "YEAR_FORMAT":
				functionName = "yyyy";
				break;
			case "MONTHYEAR_FORMAT":
				functionName = "MMM-yyyy";
				break;
			case "DATETIME_FORMAT":
				functionName = "dd-MMM-yyyy HH:mm:ss";
				break;
			}
		} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
			switch (reqFunction) {
			case "DATEFUNC":
				functionName = "TO_CHAR";
				break;
			case "SYSDATE":
				functionName = "SYSDATE";
				break;
			case "NVL":
				functionName = "NVL";
				break;
			case "TIME":
				functionName = "HH24:MI:SS";
				break;
			case "DATEFORMAT":
				functionName = "DD-Mon-RRRR";
				break;
			case "CONVERT":
				functionName = "TO_CHAR";
				break;
			case "TYPE":
				functionName = "";
				break;
			case "TIMEFORMAT":
				functionName = "HH:MM:SS";
				break;
			case "PIPELINE":
				functionName = "||";
				break;
			case "YEAR_FORMAT":
				functionName = "RRRR";
				break;
			case "MONTHYEAR_FORMAT":
				functionName = "Mon-YYYY";
				break;
			case "DATETIME_FORMAT":
				functionName = "DD-Mon-RRRR HH24:MI:SS";
				break;
			}
		}

		return functionName;
	}

	public List<AlphaSubTabVb> getVisionBusinessDate() throws DataAccessException {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "select COUNTRY||'-'||LE_BOOK ALPHA_SUB_TAB,TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') ALPHA_SUBTAB_DESCRIPTION  from Vision_Business_Day ";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "select COUNTRY+'-'+LE_BOOK ALPHA_SUB_TAB,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') ALPHA_SUBTAB_DESCRIPTION  from Vision_Business_Day  ";
		}
		return getJdbcTemplate().query(sql, getGenMapper());
	}

	protected RowMapper getGenMapper() {
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

	public List<ProfileData> getTopLevelMenu(VisionUsersVb visionUsersVb) throws DataAccessException {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "Select MENU_GROUP_SEQ,MENU_GROUP_NAME,MENU_GROUP_ICON,MENU_PROGRAM, "
					+ " T2.P_ADD,T2.P_MODIFY,P_DELETE,P_INQUIRY,P_VERIFICATION,P_EXCEL_UPLOAD,P_DOWNLOAD,P_SUBMIT  from PRD_MENU_GROUP T1,PRD_PROFILE_PRIVILEGES_NEW T2 "
					+ " where T1.MENU_GROUP_Status = 0  and T1.Application_Access = ?  AND T2.USER_GROUP||'-'||T2.USER_PROFILE = ? "
					+ " AND T1.Application_Access = T2.Application_Access  AND T1.MENU_GROUP_SEQ = T2.MENU_GROUP  ORDER BY Menu_Display_Order ";
		} else {
			sql = "Select MENU_GROUP_SEQ,MENU_GROUP_NAME,MENU_GROUP_ICON,MENU_PROGRAM, "
					+ " T2.P_ADD,T2.P_MODIFY,P_DELETE,P_INQUIRY,P_VERIFICATION,P_EXCEL_UPLOAD,P_DOWNLOAD,P_SUBMIT  from PRD_MENU_GROUP T1,PRD_PROFILE_PRIVILEGES_NEW T2 "
					+ " where T1.MENU_GROUP_Status = 0  and T1.Application_Access = ?  AND T2.USER_GROUP+'-'+T2.USER_PROFILE = ? "
					+ " AND T1.Application_Access = T2.Application_Access  AND T1.MENU_GROUP_SEQ = T2.MENU_GROUP  ORDER BY Menu_Display_Order ";
		}
		String grpProfile = "";
		if (ValidationUtil.isValid(visionUsersVb.getUserGroup())
				&& ValidationUtil.isValid(visionUsersVb.getUserProfile()))
			grpProfile = visionUsersVb.getUserGroup() + "-" + visionUsersVb.getUserProfile();
		else
			grpProfile = visionUsersVb.getUserGrpProfile();
		Object[] lParams = new Object[2];
		lParams[0] = productName;
		lParams[1] = grpProfile;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProfileData profileData = new ProfileData();
				profileData.setMenuItem(rs.getString("MENU_GROUP_NAME"));
				profileData.setMenuGroup(rs.getInt("MENU_GROUP_SEQ"));
				profileData.setMenuIcon(rs.getString("MENU_GROUP_ICON"));
				profileData.setMenuProgram(rs.getString("MENU_PROGRAM"));
				profileData.setProfileAdd(rs.getString("P_ADD"));
				profileData.setProfileModify(rs.getString("P_MODIFY"));
				profileData.setProfileDelete(rs.getString("P_DELETE"));
				profileData.setProfileView(rs.getString("P_INQUIRY"));
				profileData.setProfileVerification(rs.getString("P_VERIFICATION"));
				profileData.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				profileData.setProfileDownload(rs.getString("P_DOWNLOAD"));
				profileData.setProfileSubmit(rs.getString("P_SUBMIT"));
				return profileData;
			}
		};
		List<ProfileData> profileData = getJdbcTemplate().query(sql, lParams, mapper);
		return profileData;
	}

	public ArrayList<MenuVb> getSubMenuItemsForSubMenuGroup(int menuGroup, int parentSequence,
			VisionUsersVb visionUsersVb) throws DataAccessException {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = " SELECT * FROM PRD_VISION_MENU t1,PRD_PROFILE_PRIVILEGES_NEW T2  WHERE  T1.MENU_GROUP = ?  AND PARENT_SEQUENCE = ? "
					+ " AND MENU_STATUS = 0   AND SEPARATOR = 'N'  AND T1.APPLICATION_ACCESS = ?  AND T1.MENU_GROUP = T2.MENU_GROUP "
					+ " AND T1.APPLICATION_ACCESS = T2.APPLICATION_ACCESS  AND T2.USER_GROUP||'-'||T2.USER_PROFILE = ?  ORDER BY MENU_SEQUENCE ";
		} else {
			sql = " SELECT * FROM PRD_VISION_MENU t1,PRD_PROFILE_PRIVILEGES_NEW T2  WHERE  T1.MENU_GROUP = ?  AND PARENT_SEQUENCE = ? "
					+ " AND MENU_STATUS = 0   AND SEPARATOR = 'N'  AND T1.APPLICATION_ACCESS = ?  AND T1.MENU_GROUP = T2.MENU_GROUP "
					+ " AND T1.APPLICATION_ACCESS = T2.APPLICATION_ACCESS  AND T2.USER_GROUP+'-'+T2.USER_PROFILE = ?  ORDER BY MENU_SEQUENCE ";
		}

		String grpProfile = visionUsersVb.getUserGroup() + "-" + visionUsersVb.getUserProfile();
		Object[] lParams = new Object[4];
		lParams[0] = menuGroup;
		lParams[1] = parentSequence;
		lParams[2] = productName;
		lParams[3] = grpProfile;

		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MenuVb menuVb = new MenuVb();
				menuVb.setMenuProgram(rs.getString("MENU_PROGRAM"));
				menuVb.setMenuName(rs.getString("MENU_NAME"));
				menuVb.setMenuSequence(rs.getInt("MENU_SEQUENCE"));
				menuVb.setParentSequence(rs.getInt("PARENT_SEQUENCE"));
				menuVb.setSeparator(rs.getString("SEPARATOR"));
				menuVb.setMenuGroup(rs.getInt("MENU_GROUP"));
				menuVb.setMenuStatus(rs.getInt("MENU_STATUS"));
				menuVb.setProfileAdd(rs.getString("P_ADD"));
				menuVb.setProfileModify(rs.getString("P_MODIFY"));
				menuVb.setProfileDelete(rs.getString("P_DELETE"));
				menuVb.setProfileView(rs.getString("P_INQUIRY"));
				menuVb.setProfileVerification(rs.getString("P_VERIFICATION"));
				menuVb.setProfileUpload(rs.getString("P_EXCEL_UPLOAD"));
				menuVb.setProfileDownload(rs.getString("P_DOWNLOAD"));
				return menuVb;
			}
		};
		ArrayList<MenuVb> menuList = (ArrayList<MenuVb>) getJdbcTemplate().query(sql, lParams, mapper);
		return menuList;
	}

	public String getPasswordHistoryCount(int visionId) throws DataAccessException {
		String sql = "select Change_Count from Vision_Pwd_History where VISION_ID=?";
		Object[] lParams = new Object[1];
		lParams[0] = visionId;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("Change_Count"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs != null && !commonVbs.isEmpty()) {
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}

	public int doUpdateLoginFlag(VisionUsersVb vObject) {
		String query = "Update VISION_USERS Set LOGGED_IN_FLAG = ?  where UPPER(USER_LOGIN_ID)=UPPER('"
				+ vObject.getUserLoginId() + "')";
		Object[] args = { vObject.getLoggedinflag() };
		return getJdbcTemplate().update(query, args);
	}

	public List<AlphaSubTabVb> getCountryLeBook() throws DataAccessException {
		String sql = "  SELECT DISTINCT Country ALPHA_SUB_TAB,LE_BOOK ALPHA_SUBTAB_DESCRIPTION FROM LE_book WHERE LEB_Status = 0 AND COUNTRY != 'ZZ'";
		return getJdbcTemplate().query(sql, getGenMapper());
	}

	public List<AlphaSubTabVb> getCountryList() throws DataAccessException {
		String sql = "  SELECT DISTINCT COUNTRY ALPHA_SUB_TAB,COUNTRY_DESCRIPTION ALPHA_SUBTAB_DESCRIPTION FROM COUNTRIES WHERE COUNTRY_STATUS = 0 AND COUNTRY != 'ZZ' ORDER BY ALPHA_SUB_TAB";
		return getJdbcTemplate().query(sql, getGenMapper());
	}

	public List<AlphaSubTabVb> getCountry() throws DataAccessException {
		try {
			String sql = "SELECT distinct COUNTRY ID,COUNTRY " + getDbFunction("PIPELINE") + "' - '"
					+ getDbFunction("PIPELINE")
					+ " (select Country_description from countries where country = T1.Country) DESCRIPTION FROM LE_BOOK T1 WHERE LEB_STATUS = 0 ORDER BY COUNTRY";
			List<AlphaSubTabVb> countryLst = getJdbcTemplate().query(sql, getPageLoadValuesMapper());
			return countryLst;
		} catch (Exception e) {
			return new ArrayList<AlphaSubTabVb>();
		}
	}

	public List<AlphaSubTabVb> getLeBook(String country) throws DataAccessException {
		try {
			String sql = "SELECT distinct LE_BOOK ID,LE_BOOK||' - '|| LEB_DESCRIPTION AS DESCRIPTION FROM LE_BOOK WHERE LEB_STATUS = 0 AND COUNTRY = '"
					+ country + "' ORDER BY LE_BOOK";
			List<AlphaSubTabVb> leBookLst = getJdbcTemplate().query(sql, getPageLoadValuesMapper());
			return leBookLst;
		} catch (Exception e) {
			return new ArrayList<AlphaSubTabVb>();
		}
	}

	public RowMapper getPageLoadValuesMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				vObject.setAlphaSubTab(rs.getString("ID"));
				vObject.setDescription(rs.getString("DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	public ExceptionCode getReqdConnection(Connection conExt, String connectionName) {
		ExceptionCode exceptionCodeCon = new ExceptionCode();
		try {
			if (!ValidationUtil.isValid(connectionName) || "DEFAULT".equalsIgnoreCase(connectionName)) {
				conExt = commonApiDao.getConnection();
				exceptionCodeCon.setResponse(conExt);
			} else {
				String dbScript = getScriptValue(connectionName);
				if (!ValidationUtil.isValid(dbScript)) {
					exceptionCodeCon.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCodeCon.setErrorMsg("DB Connection Name is Invalid");
					return exceptionCodeCon;
				}
				exceptionCodeCon = CommonUtils.getConnection(dbScript);
			}
			exceptionCodeCon.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCodeCon.setErrorMsg(e.getMessage());
			exceptionCodeCon.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCodeCon;
	}

//	public String calcDuration(String durationSec) {
//		int seconds = Integer.parseInt(durationSec) % 60;
//		int minutes = Integer.parseInt(durationSec) / 60;
//		if (minutes > 1 || minutes == 1)
//			durationSec = minutes + " Min(s) " + seconds + " Sec(s) ";
//		else
//			durationSec = seconds + " Sec(s) ";
//		return durationSec;
//	}
	public String calcDuration(String durationSec) {
	    // Convert the string to a double first
	    double duration = Double.parseDouble(durationSec);

	    // Convert to total seconds as an integer
	    int totalSeconds = (int) duration;

	    // Break down into minutes and seconds
	    int minutes = totalSeconds / 60;
	    int seconds = totalSeconds % 60;

	    // Build the output string
	    if (minutes > 0)
	        return minutes + " Min " + seconds + " Sec";
	    else
	        return seconds + " Sec";
	}

	public String getRestrictionsByUsers(String screenName, String operation) throws DataAccessException {
		if (!ValidationUtil.isValid(screenName)) {
			return null;
		}
		VisionUsersVb usersVb = SessionContextHolder.getContext();
		String column = "P_ADD";
		if ("MODIFY".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_MODIFY";
		if ("DELETE".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_DELETE";
		if ("APPROVE".equalsIgnoreCase(operation.toUpperCase()) || "REJECT".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_VERIFICATION";
		if ("DOWNLOAD".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_DOWNLOAD";
		if ("UPLOAD".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_EXCEL_UPLOAD";
		if ("QUERY".equalsIgnoreCase(operation.toUpperCase()))
			column = "P_INQUIRY";
		String sql = "SELECT " + column + " USER_RESTRINCTION FROM PRD_PROFILE_PRIVILEGES_NEW "
				+ "WHERE APPLICATION_ACCESS = ? " + " AND MENU_GROUP = ? " + " AND USER_GROUP = ? "
				+ " AND USER_PROFILE = ? ";
		Object[] lParams = new Object[4];
		lParams[0] = productName;
		lParams[1] = screenName;
		lParams[2] = usersVb.getUserGroup();
		lParams[3] = usersVb.getUserProfile();
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("USER_RESTRINCTION"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if (commonVbs != null && !commonVbs.isEmpty()) {
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}

	public String getScript(String VaraibalName) {
		try {
			if (ValidationUtil.isValid(VaraibalName)) {
				String query = "SELECT VARIABLE_SCRIPT FROM VISION_DYNAMIC_HASH_VAR WHERE VARIABLE_NAME = ?";
				Object objParams[] = { VaraibalName };
				return getJdbcTemplate().queryForObject(query.toString(), objParams, String.class);
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public List<CommonVb> getPrimaryKey(String tableName) {
		List<CommonVb> columName = null;
		try {
			String indexQuery = " Select DISTINCT T1.COLUMN_NAME,DATA_TYPE   from USER_IND_COLUMNS T1,USER_TAB_COLUMNS T2 Where "
					+ " T1.TABLE_NAME = T2.TABLE_NAME AND T1.COLUMN_NAME= T2.COLUMN_NAME "
					+ " AND	UPPER(T1.TABLE_NAME) =UPPER(?) "
					+ " And INDEX_NAME IN (Select RTrim(INDEX_NAME) INDEX_NAME From USER_INDEXES Where UPPER(TABLE_NAME) =UPPER(?)  "
					+ " And (INDEX_NAME Like '%_PK' OR INDEX_NAME Like '%_IDX')) ";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				indexQuery = " SELECT  DISTINCT I1.COLUMN_NAME,I2.DATA_TYPE "
						+ "  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE I1, INFORMATION_SCHEMA.COLUMNS I2 " + " WHERE  "
						+ " I1.TABLE_NAME = I2.TABLE_NAME AND" + " I1.COLUMN_NAME = I2.COLUMN_NAME AND "
						+ " I1.TABLE_NAME = UPPER(?) AND CONSTRAINT_NAME in ( " + " SELECT   CONSTRAINT_NAME FROM  "
						+ " INFORMATION_SCHEMA.TABLE_CONSTRAINTS  "
						+ " WHERE TABLE_NAME = UPPER(?)AND (CONSTRAINT_NAME LIKE '%_PK' OR CONSTRAINT_NAME LIKE '%_IDX'))";
			}
			Object[] lParams = new Object[2];
			lParams[0] = tableName;
			lParams[1] = tableName;

			return getJdbcTemplate().query(indexQuery, lParams, new RowMapper<CommonVb>() {
				@Override
				public CommonVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					CommonVb commonVb = new CommonVb();
					commonVb.setScreenName(rs.getString("COLUMN_NAME")); // Accessing the alias name
					commonVb.setActionType(rs.getString("DATA_TYPE"));
					return commonVb;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columName;
	}

	public List<CommonVb> getColumns(String tableName) {
		ArrayList<CommonVb> columName = null;
		String query = "Select COLUMN_NAME   from USER_TAB_COLUMNS Where UPPER(TABLE_NAME) =UPPER(?)";
		try {
			if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
				query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.columns Where UPPER(TABLE_NAME) =UPPER(?)";
			}
			Object[] lParams = new Object[1];
			lParams[0] = tableName;

			return getJdbcTemplate().query(query, lParams, new RowMapper<CommonVb>() {
				@Override
				public CommonVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					CommonVb commonVb = new CommonVb();
					commonVb.setScreenName(rs.getString("COLUMN_NAME")); // Accessing the alias name
					return commonVb;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return columName;
	}

	public String getAssetFolderUrl(ServletContext servletContext) {
		String baseDir = servletContext.getRealPath("/WEB-INF/classes/images");

		// Validate and normalize the path
		Path assetFolderPath = Paths.get(baseDir).normalize();

		// Ensure the constructed path is within the intended directory
		Path webInfPath = Paths.get(servletContext.getRealPath("/WEB-INF/classes")).normalize();
		if (!assetFolderPath.startsWith(webInfPath)) {
			throw new SecurityException("Attempt to access unauthorized path");
		}

		return assetFolderPath.toString();
	}

//		public List<AlphaSubTabVb> getLegalEntity() {
//			String sql = "Select Country" + getDbFunction("PIPELINE") + "'-'" + getDbFunction("PIPELINE")
//					+ "LE_Book ID,LEB_DESCRIPTION  DESCRIPTION "
//					+ "from LE_Book where LEB_Status = 0 and Country != 'ZZ' Order by Country,LE_Book ";
//			return getJdbcTemplate().query(sql, getPageLoadValuesMapper());
//		}

	public List<AlphaSubTabVb> getLegalEntity() {
		String sql = "Select Country" + getDbFunction("PIPELINE") + "'-'" + getDbFunction("PIPELINE")
				+ "LE_Book ALPHA_SUB_TAB,LEB_DESCRIPTION ALPHA_SUBTAB_DESCRIPTION "
				+ "from LE_Book where LEB_Status = 0 and Country != 'ZZ' Order by Country,LE_Book ";
//			if(clientName.equals("CLOUD")) {
//				String defaultLvQuery = commonApiDao.getPrdQueryConfig("RA_DEFAULT_LV");
//				sql = StringUtils.isNotEmpty(defaultLvQuery)?defaultLvQuery:sql;
//			}
		return getJdbcTemplate().query(sql, getAlphaSubTabMapper());
	}

	protected RowMapper getAlphaSubTabMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}

}