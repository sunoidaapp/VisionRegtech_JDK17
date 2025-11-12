/**
 * 
 */
package com.vision.authentication;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.vision.dao.CommonDao;
import com.vision.dao.VisionUsersDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.JCryptionUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.MenuVb;
import com.vision.vb.ProfileData;
import com.vision.vb.VisionUsersVb;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthenticationBean {
	@Value("${app.client}")
	private String client;
	@Value("${app.clientName}")
	private String clientName;
	@Value("${app.productName}")
	private String productName;

	public static Logger logger = LoggerFactory.getLogger(AuthenticationBean.class);
	@Autowired
	private VisionUsersDao visionUsersDao;
	@Autowired
	private CommonDao commonDao;
	private ServletContext servletContext;
	@Autowired
	private JavaMailSender mailSender;

	public boolean processLoginOld(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("***processLogin***");
		HttpSession httpses = request.getSession();
		if (httpses == null || httpses.getAttribute("ntLoginDetails") == null) {
			request.setAttribute("status", "LoginError");
			logger.error("Invalid user Login Id");
			return false;
		}
		String userId = (String) httpses.getAttribute("ntLoginDetails");
		VisionUsersVb lUser = new VisionUsersVb();
		try {
			if (ValidationUtil.isValid(userId)) {
				String strUserName = userId.indexOf("\\") >= 0
						? userId.substring(userId.indexOf("\\"), userId.length() - 1)
						: userId;
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setUserLoginId(strUserName.toUpperCase());
				visionUsersVb.setRecordIndicator(0);
				visionUsersVb.setUserStatus(0);
				List<VisionUsersVb> lUsers = new ArrayList<VisionUsersVb>();
				lUsers = visionUsersDao.getActiveUserByUserLoginId(visionUsersVb);
				if (lUsers == null || lUsers.isEmpty() || lUsers.size() > 1) {
					request.setAttribute("status", "LoginError");
					logger.error("User does not exists or more than one user exists with same login id[" + strUserName
							+ "]");
					return false;
				}
				lUser = ((ArrayList<VisionUsersVb>) lUsers).get(0);
				lUser.setUserGrpProfile(lUser.getUserGroup() + "-" + lUser.getUserProfile());

				// ---- App access check (Old flow) ----
//				if (!hasAppAccess(lUser)) {
//					String msg = "You are not authorized to access the application: " + productName;
//					httpses.setAttribute("loginStatus", msg);
//					request.setAttribute("status", "LoginError");
//					getVisionUsersDao().doInsertUserLoginAudit(
//							lUser, request.getRemoteAddr(), 1, "Unauthorized application access: " + productName);
//					return false;
//				}
				// ---- end app access check ----

				System.out.println("Getting default Country .. " + new Date());
				String countryLeBook = getVisionUsersDao().getDefaultCountryLeBook();
				String country = countryLeBook.split("-")[0];
				String leBook = countryLeBook.split("-")[1];
				leBook = "'" + leBook + "'";
				String legalVehicle = getVisionUsersDao().getDefaultLegalVehicle(countryLeBook);
				String countryDesc = getVisionUsersDao().getCountryDesc(country);
				String lebookDesc = getVisionUsersDao().getLebookDesc(countryLeBook);
				lUser.setDefaultCountry(country);
				lUser.setDefaultCountryDesc(countryDesc);
				lUser.setDefaultLeBook(leBook);
				lUser.setDefaultLebookDesc(lebookDesc);

				lUser.setClientName(clientName);
				lUser.setLastSuccessfulLoginDate(lUser.getLastActivityDate());
				// visionUsersDao.updateActivityDateByUserLoginId(lUser);

				System.out.println("Getting Menu Details .. " + new Date());
				ArrayList<Object> result = getMenuForUser(lUser);
				httpses.setAttribute("userDetails", lUser);
				httpses.setAttribute("menuDetails", result);
				lUser = findSystemInfo(request, lUser);
			} else {
				logger.error("Unusual case for login context");
				return false;
			}
		} catch (Exception e) {
			logger.error("Exception in AuthenticationBean : " + e.getMessage(), e);
			request.setAttribute("status", "LoginError");
			return false;
		}

		try {
			writeUserLoginAudit(request, userId, "SUCCF", "Login Success", lUser.getVisionId());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Returns the Profiles and Menu Items for the logged in user.
	 * 
	 * @return ArrayList<Object>. Index 0 contains list of profiles of the top level
	 *         menus and index 1 contains list of MenuVb.
	 */
	public ArrayList<Object> getMenuForUser(VisionUsersVb lCurrentUser) {
		if (lCurrentUser == null) {
			throw new RuntimeCustomException("Invalida session. Please reload the application.");
		}
		ArrayList<MenuVb> resultMenu = new ArrayList<MenuVb>();
		ArrayList<Object> result = new ArrayList<Object>();
		try {
			List<ProfileData> topLvlMenuList = commonDao.getTopLevelMenu(lCurrentUser);
			List<ProfileData> finalMenuGrouplst = new ArrayList<ProfileData>();
			if (topLvlMenuList != null && !topLvlMenuList.isEmpty()) {
				for (ProfileData profileData : topLvlMenuList) {
					ArrayList<MenuVb> resultChilds = new ArrayList<MenuVb>();
					MenuVb lMenuVb = new MenuVb();
					lMenuVb.setMenuName(profileData.getMenuItem());
					lMenuVb.setMenuGroup(profileData.getMenuGroup());
					lMenuVb.setMenuIcon(profileData.getMenuIcon());
					lMenuVb.setMenuProgram(profileData.getMenuProgram());
					lMenuVb.setProfileAdd(profileData.getProfileAdd());
					lMenuVb.setProfileModify(profileData.getProfileModify());
					lMenuVb.setProfileDelete(profileData.getProfileDelete());
					lMenuVb.setProfileView(profileData.getProfileView());
					lMenuVb.setProfileVerification(profileData.getProfileVerification());
					lMenuVb.setProfileUpload(profileData.getProfileUpload());
					lMenuVb.setProfileDownload(profileData.getProfileDownload());
					lMenuVb.setProfileSubmit(profileData.getProfileSubmit());
					lMenuVb.setMenuStatus(0);
					ArrayList<MenuVb> subMenuGroup = commonDao.getSubMenuItemsForMenuGroup(profileData.getMenuGroup(),
							lCurrentUser);
					if (subMenuGroup != null && !subMenuGroup.isEmpty()) {
						for (MenuVb menuVb : subMenuGroup) {
							ArrayList<MenuVb> subMenus = commonDao.getSubMenuItemsForSubMenuGroup(
									profileData.getMenuGroup(), menuVb.getParentSequence(), lCurrentUser);
							if (subMenus != null && subMenus.size() > 0) {
								menuVb.setChildren(subMenus);
							}
							menuVb.setProfileAdd(profileData.getProfileAdd());
							menuVb.setProfileModify(profileData.getProfileModify());
							menuVb.setProfileDelete(profileData.getProfileDelete());
							menuVb.setProfileView(profileData.getProfileView());
							menuVb.setProfileVerification(profileData.getProfileVerification());
							menuVb.setProfileUpload(profileData.getProfileUpload());
							menuVb.setProfileDownload(profileData.getProfileDownload());
							resultChilds.add(menuVb);
						}
					}

					lMenuVb.setChildren(resultChilds);
					finalMenuGrouplst.add(profileData);
					resultMenu.add(lMenuVb);
				}
			}
			result.add(finalMenuGrouplst);
			result.add(resultMenu);
		} catch (Exception e) {
			logger.error(
					"Exception in getting menu for the user[" + lCurrentUser.getVisionId() + "]. : " + e.getMessage(),
					e);
			throw new RuntimeCustomException("Failed to retrieve menu for your profile. Please contact System Admin.");
		}
		return result;
	}

	public boolean isFileExists(String currentScreenName) {
		try {
			String realPath = servletContext.getRealPath(currentScreenName + "Help.pdf");
			File lFile = new File(realPath);
			return lFile.exists();
		} catch (Exception exception) {
			return false;
		}
	}

	private ArrayList<MenuVb> getAllChilds(MenuVb tmpMenuVb, List<MenuVb> pChildMenu) {
		ArrayList<MenuVb> childMenus = new ArrayList<MenuVb>();
		while (pChildMenu.size() > 0) {
			MenuVb menuVb = pChildMenu.get(0);
			if (tmpMenuVb.getMenuSequence() == menuVb.getParentSequence()
					&& tmpMenuVb.getMenuSequence() != menuVb.getMenuSequence()) {
				childMenus.add(menuVb);
				pChildMenu.remove(menuVb);
				menuVb.setChildren(getAllChilds(menuVb, pChildMenu));
			} else {
				break;
			}
		}
		return childMenus;
	}

	public void updateUnsuccessfulLoginAttempts(String userId) {
		try {
			visionUsersDao.updateUnsuccessfulLoginAttempts(userId);
		} catch (Exception e) {
			logger.error("Exception in AuthenticationBean : " + e.getMessage(), e);
		}
	}

	public String getUnsuccessfulLoginAttempts(String userId) {
		try {
			if (!ValidationUtil.isValid(userId)) {
				return "0";
			}
			String legalVehicle = visionUsersDao.getUnsuccessfulLoginAttempts(userId);
			return legalVehicle;
		} catch (Exception e) {
			logger.error("Exception in getting the Login attempts : " + e.getMessage(), e);
			return null;
		}
	}

	public String getMaxLoginAttempts() {
		try {
			String MaxLogin = getCommonDao().findVisionVariableValue("MAX_LOGIN");
			if (!ValidationUtil.isValid(MaxLogin))
				MaxLogin = "3";
			return MaxLogin;
		} catch (Exception e) {
			logger.error("Exception in getting the Max Login attempts : " + e.getMessage(), e);
			return null;
		}
	}

	public String getUserStatus(String userId) {
		try {
			if (!ValidationUtil.isValid(userId)) {
				return "0";
			}
			String userStatus = visionUsersDao.getNonActiveUsers(userId);
			return userStatus;
		} catch (Exception e) {
			logger.error("Exception in getting the User Status : " + e.getMessage(), e);
			return null;
		}
	}

	public String findVisionVariableValuePasswordURL() {
		try {
			String PasswordResetURL = getCommonDao().findVisionVariableValue("NON_PASSWORD_RESETURL");
			return PasswordResetURL;
		} catch (Exception e) {
			logger.error("Exception in getting the Password Reset URL : " + e.getMessage(), e);
			return null;
		}
	}

	public String findVisionVariableValuePasswordResrtTime() {
		try {
			String PasswordResetTime = getCommonDao().findVisionVariableValue("Password_ResetTime");
			return PasswordResetTime;
		} catch (Exception e) {
			logger.error("Exception in getting the Password Reset Time : " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("null")
	public ExceptionCode callProcToPopulateForgotPasswordEmail(VisionUsersVb vObject, String resultForgotBy) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject = getVisionUsersDao().callProcToPopulateForgotPasswordEmail(vObject, resultForgotBy);
			exceptionCode.setErrorMsg(vObject.getErrorMessage());
			exceptionCode.setErrorCode(Integer.parseInt(vObject.getStatus()));
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
	}

	public ExceptionCode doSendEmail(VisionUsersVb vObject, String resultForgotBy) {
		ExceptionCode exceptionCode = null;
		try {
			MimeMessage msg = prepareEmail(vObject, resultForgotBy);
			getMailSender().send(msg);
			exceptionCode = CommonUtils.getResultObject("Your Email", Constants.SUCCESSFUL_OPERATION, "hasBeenSent",
					"");
		} catch (MailAuthenticationException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Your Email", Constants.ERRONEOUS_OPERATION, "hasNotBeenSent",
					"");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Your Email", Constants.ERRONEOUS_OPERATION, "hasNotBeenSent",
					"");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return exceptionCode;
	}

	private MimeMessage prepareEmail(final VisionUsersVb vObject, final String resultForgotBy)
			throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo("dakshina.deenadayalan@sunoida.com");
		helper.setText("Greetings :)");
		helper.setSubject("Mail From Spring Boot");
		return message;
	}

	public int doPasswordResetInsertion(VisionUsersVb vObj) {
		int intValue = getCommonDao().doPasswordResetInsertion(vObj);
		return intValue;
	}

	public VisionUsersVb getUserDetails() {
		return SessionContextHolder.getContext();
	}

	public VisionUsersDao getVisionUsersDao() {
		return visionUsersDao;
	}

	public void setVisionUsersDao(VisionUsersDao visionUsersDao) {
		this.visionUsersDao = visionUsersDao;
	}

	public CommonDao getCommonDao() {
		return commonDao;
	}

	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void writeUserLoginAudit(HttpServletRequest request, String userLoginId, String status, String comments,
			int visionId) throws UnknownHostException {
		try {
			VisionUsersVb vObject = new VisionUsersVb();
			vObject.setUserLoginId(userLoginId);
			vObject.setLoginStatus(status);
			vObject.setComments(comments);
			vObject.setVisionId(visionId);
			vObject = findSystemInfo(request, vObject);
			visionUsersDao.insertUserLoginAudit(vObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VisionUsersVb findSystemInfo(HttpServletRequest request, VisionUsersVb vObject) {
		try {
			String ipAddress = request.getRemoteAddr();
			if ("0:0:0:0:0:0:0:1".equalsIgnoreCase(ipAddress)) {
				ipAddress = InetAddress.getLocalHost().getHostAddress();
			}
			InetAddress inetAddress = InetAddress.getByName(ipAddress);
			vObject.setIpAddress(ipAddress);
			vObject.setRemoteHostName(inetAddress.getHostName());
			vObject.setMacAddress(getCommonDao().getMacAddress(ipAddress));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vObject;
	}

	public ExceptionCode findActiveDirectory(HttpServletRequest request, String username) {
		String userId = username;
		ExceptionCode exceptionCode = new ExceptionCode();
		String userIpAddress = request.getRemoteAddr();
		try {
			if (ValidationUtil.isValid(userId)) {
				String strUserName = userId;
				VisionUsersVb visionUsersVb = new VisionUsersVb();
				visionUsersVb.setUserLoginId(strUserName.toLowerCase());
				visionUsersVb.setRecordIndicator(0);
				visionUsersVb.setUserStatus(0);
				List<VisionUsersVb> lUsers = visionUsersDao.getActiveUserByUserLoginId(visionUsersVb);
				if (lUsers == null || lUsers.isEmpty() || lUsers.size() > 1) {
					exceptionCode.setErrorCode(0);
					exceptionCode.setErrorMsg("User does not exists or more than one user exists with same login id ["
							+ strUserName + "]");
					return exceptionCode;
				}
				if (lUsers.size() == 1) {
					/*
					 * if(lUsers.get(0).getLoggedinflag()=="Y" ||
					 * "Y".equalsIgnoreCase(lUsers.get(0).getLoggedinflag()) ){
					 * exceptionCode.setErrorCode(0);
					 * exceptionCode.setErrorMsg("User already logged in with same login id ["
					 * +strUserName+"]"); return exceptionCode; }
					 */
				}
				VisionUsersVb lUser = ((ArrayList<VisionUsersVb>) lUsers).get(0);
				lUser.setRemoteAddress(userIpAddress);
				lUser.getActiveDirectoryFlag();
				exceptionCode.setRequest(lUser);
				return exceptionCode;
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("LoginError");
			e.printStackTrace();
			logger.info("Error : findActiveDirectory : " + e.getMessage());
			return exceptionCode;
		}
		return exceptionCode;
	}

	public boolean processLogin(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("***processLogin***");
		HttpSession httpses = request.getSession();
		String activeDirectoryFlag = (String) request.getAttribute("activeDirectoryFlag");
		String userId = (String) httpses.getAttribute("ntLoginDetails");
		String userIpAddress = request.getRemoteAddr();
		VisionUsersVb lUser = null;
		List<VisionUsersVb> lUsers = null;

		if (!ValidationUtil.isValid(activeDirectoryFlag)) {
			VisionUsersVb vObj = new VisionUsersVb();
			ExceptionCode exceptCode = findActiveDirectory(request, userId);
			if (exceptCode != null) {
				if (exceptCode.getRequest() != null) {
					vObj = (VisionUsersVb) exceptCode.getRequest();
					activeDirectoryFlag = vObj.getActiveDirectoryFlag();
				} else {
					request.setAttribute("status", "LoginError");
					logger.error("Invalid user Login Id in Vision User Table ");
					return false;
				}
			}
		}
		if ("Y".equalsIgnoreCase(activeDirectoryFlag)
				&& (httpses == null || httpses.getAttribute("ntLoginDetails") == null)) {
			request.setAttribute("status", "LoginError");
			logger.error("Invalid user Login Id - ntLoginDetails or Active Directory flag ");
			return false;
		}
		try {
			String strUserName = userId.indexOf("\\") >= 0 ? userId.substring(userId.indexOf("\\"), userId.length() - 1)
					: userId;
			VisionUsersVb visionUsersVb = new VisionUsersVb();
			visionUsersVb.setUserLoginId(strUserName.toUpperCase());
			visionUsersVb.setRecordIndicator(0);
			visionUsersVb.setUserStatus(0);

			if ("Y".equalsIgnoreCase(activeDirectoryFlag)) {
				lUsers = visionUsersDao.getActiveUserByUserLoginId(visionUsersVb);
				if (lUsers == null || lUsers.isEmpty() || lUsers.size() > 1) {
					request.setAttribute("status", "LoginError");
					logger.error("User does not exists or more than one user exists with same login id[" + strUserName
							+ "]");
					return false;
				}

				lUser = ((ArrayList<VisionUsersVb>) lUsers).get(0);

				if (lUser.getUserStatus() != 0) {
					httpses.setAttribute("loginStatus",
							"The User Is Inactive/Deleted/Old for login id [" + strUserName + "]");
					getVisionUsersDao().doInsertUserLoginAudit(lUser, request.getRemoteAddr(), 1,
							"The User Is Inactive/Deleted/Old for login id");
					return false;
				}
				String maxLoginAttempts = getMaxLoginAttempts();
				if (ValidationUtil.isValid(lUser.getLastUnsuccessfulLoginAttempts())
						&& ValidationUtil.isValid(maxLoginAttempts)) {
					if (Integer.parseInt(lUser.getLastUnsuccessfulLoginAttempts()) >= Integer
							.parseInt(getMaxLoginAttempts())) {
						httpses.setAttribute("loginStatus",
								"User account has been locked. Please Contact System Admin");
						getVisionUsersDao().doInsertUserLoginAudit(lUser, request.getRemoteAddr(), 1,
								"User account has been locked in Vision level");
						return false;
					}
				}

				// ---- App access check (AD) ----
				if (!hasAppAccess(lUser)) {
					httpses.setAttribute("loginStatusWithSPE",
						    "You are not authorized to access the application: " + productName);
						request.setAttribute("status", "LoginError");

					request.setAttribute("status", "LoginError");
					getVisionUsersDao().doInsertUserLoginAudit(
							lUser, request.getRemoteAddr(), 1, "Unauthorized application access: " + productName);
					return false;
				}
				// ---- end app access check ----

				lUser.setUserGrpProfile(lUser.getUserGroup() + "-" + lUser.getUserProfile());
				if ("Y".equalsIgnoreCase(lUser.getUpdateRestriction())) {
					String listOfLeBook = getVisionUsersDao().getLeBookList(lUser);
					httpses.setAttribute("LEBOOK_LIST", listOfLeBook);
					String leBook = listOfLeBook;
					lUser.setUpdateRestrictionLeBook(listOfLeBook);
					lUser.setUpdateRestrictionLegalVehicle(lUser.getLegalVehicle());
					if (ValidationUtil.isValid(listOfLeBook)) {
						listOfLeBook = listOfLeBook.replaceAll("'", "");
						String[] listCountryLeBook = listOfLeBook.split(",");
						String country = listCountryLeBook[0].split("-")[0];
						String leBook1 = listCountryLeBook[0].split("-")[1];
						String countryDesc = getVisionUsersDao().getCountryDesc(country);
						String lebookDesc = getVisionUsersDao().getLebookDesc(listCountryLeBook[0]);
						lUser.setDefaultLebookDesc(lebookDesc);
						lUser.setDefaultCountryDesc(countryDesc);
						lUser.setDefaultLeBook(leBook1);
						lUser.setDefaultCountry(country);
					}
				} else {
					String countryLeBook = getVisionUsersDao().getDefaultCountryLeBook();
					String country = countryLeBook.split("-")[0];
					String leBook = countryLeBook.split("-")[1];
					leBook = "'" + leBook + "'";
					String legalVehicle = getVisionUsersDao().getDefaultLegalVehicle(countryLeBook);
					String countryDesc = getVisionUsersDao().getCountryDesc(country);
					String lebookDesc = getVisionUsersDao().getLebookDesc(countryLeBook);
					lUser.setDefaultCountry(country);
					lUser.setDefaultCountryDesc(countryDesc);
					lUser.setDefaultLeBook(leBook);
					lUser.setDefaultLebookDesc(lebookDesc);
				}

			} else {
				lUsers = visionUsersDao.getActiveUserByUserLoginId(visionUsersVb);
				if (lUsers == null || lUsers.isEmpty() || lUsers.size() > 1) {
					request.setAttribute("status", "LoginError");
					logger.error("User does not exists or more than one user exists with same login id[" + strUserName
							+ "]");
					return false;
				}

				lUser = ((ArrayList<VisionUsersVb>) lUsers).get(0);

				if (lUser.getUserStatus() != 0) {
					httpses.setAttribute("loginStatus",
							"The User Is Inactive/Deleted/Old for login id [" + strUserName + "]");
					getVisionUsersDao().doInsertUserLoginAudit(lUser, request.getRemoteAddr(), 1,
							"The User Is Inactive/Deleted/Old for login id");
					return false;
				}
				String maxLoginAttempts = getMaxLoginAttempts();
				if (ValidationUtil.isValid(lUser.getLastUnsuccessfulLoginAttempts())
						&& ValidationUtil.isValid(maxLoginAttempts)) {
					if (Integer.parseInt(lUser.getLastUnsuccessfulLoginAttempts()) >= Integer
							.parseInt(getMaxLoginAttempts())) {
						httpses.setAttribute("loginStatus",
								"User account has been locked. Please Contact System Admin");
						getVisionUsersDao().doInsertUserLoginAudit(lUser, request.getRemoteAddr(), 1,
								"User account has been locked in Vision level");
						return false;
					}
				}

				// ---- App access check (Non-AD) ----
				if (!hasAppAccess(lUser)) {
					String msg = "You are not authorized to access the application: " + productName;
					httpses.setAttribute("loginStatus", msg);
					request.setAttribute("status", "LoginError");
					getVisionUsersDao().doInsertUserLoginAudit(
							lUser, request.getRemoteAddr(), 1, "Unauthorized application access: " + productName);
					return false;
				}
				// ---- end app access check ----

				List<VisionUsersVb> lUsersPwd = visionUsersDao.getPasswordByVisionId(lUser);

				String Crctpwd = (String) request.getAttribute("Crctpwd");
				System.out.println("Crctpwd : " + Crctpwd);
				String digest = DigestUtils.md5Hex(Crctpwd);
				String dbEncriptPwd = lUsersPwd.get(0).getPassword1();

				if (lUsersPwd.size() >= 1 && Integer.parseInt(getPassHistoryCount(lUser.getVisionId())) == 0
						&& !dbEncriptPwd.equalsIgnoreCase(digest)) {
					updateUnsuccessfulLoginAttempts(strUserName.toLowerCase());
					request.setAttribute("status", "Invalid Password with same login id [" + strUserName + "]");
					getVisionUsersDao().doInsertUserLoginAudit(lUser, request.getRemoteAddr(), 1,
							"Invalid Password with same login id");
					return false;
				}
				if (Integer.parseInt(getPassHistoryCount(lUser.getVisionId())) == 0
						&& "N".equalsIgnoreCase(lUsersPwd.get(0).getPasswordResetFlag())) {
					request.setAttribute("loggedInUserID", lUser.getVisionId());
					request.setAttribute("firstTimeUserstatus",
							"You have logged in for the first time using a temporary password. Please proceed to set up a permanent password.|"
									+ lUser.getVisionId());
					httpses.setAttribute("loginStatus",
							"You have logged in for the first time using a temporary password. Please proceed to set up a permanent password.|"
									+ lUser.getVisionId());
					return false;
				}
				String existingPwd = "";
				System.out.println("Count : " + lUsersPwd.get(0).getChangeCount());
				if (lUsersPwd.get(0).getChangeCount() == 0 || lUsersPwd.get(0).getChangeCount() == 1) {
					existingPwd = lUsersPwd.get(0).getPassword1();
				} else if (lUsersPwd.get(0).getChangeCount() == 2) {
					existingPwd = lUsersPwd.get(0).getPassword2();
				} else {
					existingPwd = lUsersPwd.get(0).getPassword3();
				}
				String existingDecPwd = existingPwd;
				System.out.println("DB Pwd : " + existingDecPwd + " Login Pwd : " + digest);
				if (!digest.equalsIgnoreCase(existingDecPwd)) {
					updateUnsuccessfulLoginAttempts(strUserName.toLowerCase());
					request.setAttribute("status", "Invalid Password with same login id [" + strUserName + "]");
					getVisionUsersDao().doInsertUserLoginAudit(visionUsersVb, request.getRemoteAddr(), 1,
							"Invalid Password with same login id");
					return false;
				} else {
					setLoggedInFlag("Y", strUserName);
				}
				long diff = -1;
				String nextPasswordModifyDate = lUser.getNextPasswordModifyDate().substring(0, 10).trim();
				SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
				String curDate = sdformat.format(new Date());
				Date currentDate = sdformat.parse(curDate);
				Date dbDate = sdformat.parse(nextPasswordModifyDate);
				System.out
						.println("curDate : [" + curDate + "] NextPasswordModifyDate[" + nextPasswordModifyDate + "]");
				if (dbDate.compareTo(currentDate) <= 0) {
					request.setAttribute("UserExpiredStatus",
							"User password has expired. Please click Ok to reset the password.|" + lUser.getVisionId());
					httpses.setAttribute("loginStatus",
							"User password has expired. Please click Ok to reset the password.|" + lUser.getVisionId());
					request.setAttribute("loginUserId", lUsers.get(0).getVisionId());
					return false;
				}

				lUser.setUserGrpProfile(lUser.getUserGroup() + "-" + lUser.getUserProfile());
				System.out.println("Update : " + lUser.getUpdateRestriction());
				if ("Y".equalsIgnoreCase(lUser.getUpdateRestriction())) {
					String listOfLeBook = getVisionUsersDao().getLeBookList(lUser);
					httpses.setAttribute("LEBOOK_LIST", listOfLeBook);
					String leBook = listOfLeBook;
					lUser.setUpdateRestrictionLeBook(listOfLeBook);
					lUser.setUpdateRestrictionLegalVehicle(lUser.getLegalVehicle());
					if (ValidationUtil.isValid(listOfLeBook)) {
						listOfLeBook = listOfLeBook.replaceAll("'", "");
						System.out.println("listOfLeBook : " + listOfLeBook);
						String[] listCountryLeBook = listOfLeBook.split(",");
						String country = listCountryLeBook[0].split("-")[0];
						String leBook1 = listCountryLeBook[0].split("-")[1];
						String countryDesc = getVisionUsersDao().getCountryDesc(country);
						String lebookDesc = getVisionUsersDao().getLebookDesc(listCountryLeBook[0]);
						lUser.setDefaultLebookDesc(lebookDesc);
						lUser.setDefaultCountryDesc(countryDesc);
						lUser.setDefaultLeBook(leBook1);
						lUser.setDefaultCountry(country);
					}

				} else {

					String countryLeBook = getVisionUsersDao().getDefaultCountryLeBook();
					System.out.println("listOfLeBook 2 : " + countryLeBook);
					String country = countryLeBook.split("-")[0];
					String leBook = countryLeBook.split("-")[1];
					leBook = "'" + leBook + "'";
					String legalVehicle = getVisionUsersDao().getDefaultLegalVehicle(countryLeBook);
					String countryDesc = getVisionUsersDao().getCountryDesc(country);
					String lebookDesc = getVisionUsersDao().getLebookDesc(countryLeBook);
					lUser.setDefaultCountry(country);
					lUser.setDefaultCountryDesc(countryDesc);
					lUser.setDefaultLeBook(leBook);
					lUser.setDefaultLebookDesc(lebookDesc);
				}
			}

			String backupAo = "";
			if (ValidationUtil.isValid(lUser.getAccountOfficer())) {
				backupAo = visionUsersDao.getAoBackup(lUser.getAccountOfficer());
				if (ValidationUtil.isValid(backupAo) && backupAo.length() > 1) {
					lUser.setRestrictedAo(backupAo);
				}
			}
			lUser.setClientName(clientName);
			String homeDashboard = commonDao.getUserHomeDashboard(lUser);
			if (!ValidationUtil.isValid(homeDashboard))
				homeDashboard = "NA";
			lUser.setHomeDashboard(homeDashboard);

			lUser.setLastSuccessfulLoginDate(lUser.getLastActivityDate());
			ArrayList<Object> result = getMenuForUser(lUser);
			// Updation of Last Activity Date 
			visionUsersDao.updateActivityDateByUserLoginId(lUser);
			httpses.setAttribute("userDetails", lUser);
			httpses.setAttribute("menuDetails", result);

		} catch (Exception e) {
			logger.error("Exception in AuthenticationBean : " + e.getMessage(), e);
			request.setAttribute("status", "LoginError");
			return false;
		}
		try {
			writeUserLoginAudit(request, userId, "SUCCF", "Login Success", lUser.getVisionId());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return true;
	}

	public String getPassHistoryCount(int visionId) {
		try {
			String pwdHistoryCnt = getCommonDao().getPasswordHistoryCount(visionId);
			if (!ValidationUtil.isValid(pwdHistoryCnt))
				pwdHistoryCnt = "0";
			return pwdHistoryCnt;
		} catch (Exception e) {
			logger.error("Exception in getting the Password History Change Count : " + e.getMessage(), e);
			return null;
		}
	}

	public ExceptionCode setLoggedInFlag(String Flag, String username) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionUsersVb visionUsersVb = new VisionUsersVb();
			visionUsersVb.setLoggedinflag(Flag);
			visionUsersVb.setUserLoginId(username);
			int updateLoginFlag = commonDao.doUpdateLoginFlag(visionUsersVb);
			if (updateLoginFlag != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(0);
				exceptionCode.setErrorMsg("Logged in flag update failed for login id [" + username + "]");
				return exceptionCode;
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(0);
			exceptionCode.setErrorMsg("LoginError");
			return exceptionCode;
		}
		return exceptionCode;
	}

	@SuppressWarnings("null")
	public ExceptionCode callProcToResetPassword(VisionUsersVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject = getVisionUsersDao().callProcToResetPassword(vObject, "");
			exceptionCode.setErrorMsg(vObject.getErrorMessage());
			if (Integer.parseInt(vObject.getStatus()) == 0)
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			else
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
	}

	public String createVisionDynamicToken() {
		KeyPair keys = null;
		JCryptionUtil jCryptionUtil = new JCryptionUtil();
		keys = jCryptionUtil.generateKeypair(512);
		StringBuffer output = new StringBuffer();
		String n = JCryptionUtil.getPublicKeyModulus(keys);
		return n;
	}

	// ---------------------------
	// App access helper
	// ---------------------------
	private boolean hasAppAccess(VisionUsersVb user) {
		if (user == null) return false;
		String apps = user.getApplicationAccess();
		if (apps == null || apps.trim().isEmpty()) return false;

		String[] allowed = apps.split(Constants.COMMA);
		for (String a : allowed) {
			if (a != null && productName.equalsIgnoreCase(a.trim())) {
				return true;
			}
		}
		return false;
	}
}
