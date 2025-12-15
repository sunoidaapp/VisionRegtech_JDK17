package com.vision.authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vision.authentication.service.AuthenticationService;
import com.vision.dao.CommonDao;
import com.vision.dao.VisionUsersDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.NotificationsVb;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jcifs.util.Base64;
import jespa.security.SecurityProviderException;

@RestController
public class AuthenticateCredential {

	@Autowired
	VisionUsersDao visionUsersDao;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	CommonDao commonDao;
	
	
	@Autowired
	AuthenticationBean authenticationBean;

	private static String twoFactorAuthFlag;
	
	@Value("${twoFactorAuthFlag}")
	public void setTwoFactorAuthFlag(String twoFactorAuthFlag) {
		AuthenticateCredential.twoFactorAuthFlag = twoFactorAuthFlag;
	}
	
	@PostMapping(value = "authenticate")
	public ResponseEntity<JSONExceptionCode> getUserAndMenuDetailsForResponseWthToken(HttpServletRequest request,
			HttpServletResponse response) {
		
		String optSend = String.valueOf(request.getAttribute("OtpSentSuccess"));
		String img = String.valueOf(request.getAttribute("img"));
		if(!ValidationUtil.isValid(optSend))
			optSend = "NO";
		System.out.println("OTP Mail Sent : "+optSend);
		if ("FALSE".equalsIgnoreCase(twoFactorAuthFlag.toUpperCase()) || "NO".equalsIgnoreCase(optSend)) {
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<String, Object>();
			HttpStatus status = HttpStatus.OK;
			JSONExceptionCode exceptionCode = new JSONExceptionCode();
			HttpSession session = request.getSession();
			try {
				responseMap.put("token",
						"VISION" + Base64.encode((request.getAttribute("tempTokenStorage") + "").getBytes()));
				Date expirationDate = SessionContextHolder.getTokenProps(request.getAttribute("tempTokenStorage") + "")
						.getNxtExpireDate();

				List<Object> menuSessionList = new ArrayList<Object>();
				if (session.getAttribute("menuDetails") != null) {
					menuSessionList = (List<Object>) session.getAttribute("menuDetails");
				} else {
					menuSessionList = null;
				}

				responseMap.put("expired_on", expirationDate.getTime());
				responseMap.put("user_details", session.getAttribute("userDetails"));
				if (menuSessionList != null && menuSessionList.size() > 1) {
					responseMap.put("menu_details", menuSessionList.get(0));
					responseMap.put("menu_hierarchy", menuSessionList.get(1));
					String sessionTimeOut = commonDao.findVisionVariableValue("SESSION_TIMEOUT");
					if(!ValidationUtil.isValid(sessionTimeOut)) {
						sessionTimeOut = "60";
					}
					responseMap.put("SessionTimeOut", sessionTimeOut);
				}
				responseMap.put("twoFactorAuthFlag", twoFactorAuthFlag);
				responseMap.put("OtpSendFlag", optSend);
				responseMap.put("businessDay", session.getAttribute("businessDay"));
				exceptionCode.setResponse(responseMap);

				SessionContextHolder.removeTempTokenForConnectionId(request.getHeader("temporary-token"));

				/*
				 * Set the values to the header of the response
				 * 
				 * HttpHeaders headers = new HttpHeaders(); headers.add("VisionAuthenticate",
				 * "VISION"+Base64.encode((request.getAttribute("tempTokenStorage")+"").getBytes
				 * ())); return new ResponseEntity<JSONExceptionCode>(exceptionCode, headers,
				 * status);
				 */
				return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeCustomException(e.getMessage());
			}
		
		} else {
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<String, Object>();
			JSONExceptionCode exceptionCode = new JSONExceptionCode();
			responseMap.put("temporary-token", request.getHeader("temporary-token"));
			responseMap.put("twoFactorAuthFlag", twoFactorAuthFlag);
			responseMap.put("OtpSendFlag", optSend);
			String otpTimeOut = commonDao.findVisionVariableValue("OTP_TIMEOUT");
			if(!ValidationUtil.isValid(otpTimeOut)) {
				otpTimeOut = "60";
			}
			responseMap.put("img", img);
			responseMap.put("OtpTimeOut", otpTimeOut);
			exceptionCode.setResponse(responseMap);
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, HttpStatus.OK);
			//return new ResponseEntity<JSONExceptionCode>(HttpStatus.OK);
		}
	}

	@PostMapping(value = "forgotUsername")
	public ResponseEntity<JSONExceptionCode> responseForForgotPasswordEmail(HttpServletRequest request,
			HttpServletResponse response) {
		JSONExceptionCode responseCode = null;
		HttpSession ssn = request.getSession(true);

		String pwdResetURL = authenticationService.findVisionVariableValuePasswordURL();
		String pwdResetTime = authenticationService.findVisionVariableValuePasswordResrtTime();
		ExceptionCode exceptionCode = null;

		VisionUsersVb vObj = new VisionUsersVb();
		String requestmailId = request.getParameter("emailID");
		String resultForgotBy = ValidationUtil.isValid(request.getParameter("txtResultForgotBy"))
				? request.getParameter("txtResultForgotBy")
				: "Username";

		if (ValidationUtil.isValid(resultForgotBy)) {
			vObj.setUserEmailId(requestmailId);
			vObj.setPwdResetTime(pwdResetTime);
			vObj.setPasswordResetURL(pwdResetURL);
			exceptionCode = authenticationService.callProcToPopulateForgotPasswordEmail(vObj, resultForgotBy);
			if (exceptionCode.getErrorCode() == 1) {
//				ssn.setAttribute("forgotPasswordErrorStatus", exceptionCode.getErrorMsg());
				responseCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(), null);
			} else if (exceptionCode.getErrorCode() == 0) {
				vObj.setUserName(exceptionCode.getErrorMsg().split("@")[0]);
				vObj.setUserLoginId(exceptionCode.getErrorMsg().split("@")[1]);
				vObj.setVisionId(Integer.parseInt(exceptionCode.getErrorMsg().split("@")[2]));
				ssn.setAttribute("reqVisionID", vObj.getVisionId());
				String ciphertext = ValidationUtil.passwordEncryptWithUrlEncode(Integer.toString(vObj.getVisionId()));
				vObj.setScreenName(ciphertext);
				exceptionCode = authenticationService.doSendEmail(vObj, resultForgotBy);
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					vObj.setEmailStatus("S");
					responseCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
							null);
				} else {
					vObj.setEmailStatus("E");
//					ssn.setAttribute("forgotPasswordErrorStatus", exceptionCode.getErrorMsg());
					responseCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
							null);
				}
			}
		} else {
			responseCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Configuration Issue", null);
		}

		return new ResponseEntity<JSONExceptionCode>(responseCode, HttpStatus.OK);
	}

	@PostMapping(value = "refreshToken")
	public ResponseEntity<JSONExceptionCode> updateToken(HttpServletRequest request, HttpServletResponse response) {
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<String, Object>();
		try {
			byte[] oldToken = authenticationService.getTokenForLdap(request);
			String connectionId = authenticationService.getConnectionId(request);
			if (SessionContextHolder.isTokenAvailable(new String(oldToken))) {
				String newToken = EncryptionServlet.genrateKeyWithoutSessionStorage();
				SessionContextHolder.updateToken(new String(oldToken), connectionId, newToken);
				if (SessionContextHolder.isTokenValidWthConnectionId(newToken, connectionId)) {
					responseMap.put("token", "VISION" + Base64.encode(newToken.getBytes()));
					responseMap.put("expired_on", SessionContextHolder.getTokenProps(newToken).getNxtExpireDate());
				} else {
					throw new RuntimeCustomException("Problem in refreshing token");
				}
			} else {
				throw new RuntimeCustomException("Token Expire");
			}
			exceptionCode.setResponse(responseMap);
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, HttpStatus.OK);
		} catch (Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	@GetMapping(value = "notification")
	public ResponseEntity<JSONExceptionCode> getNotification() {
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		HashMap<String,Object> notificationMap = new HashMap<String,Object>();
		try {
			ArrayList<NotificationsVb> bdayNotifyList = (ArrayList<NotificationsVb>)commonDao.getMdmNotification("B");
			ArrayList<NotificationsVb> dealNotifyList = (ArrayList<NotificationsVb>)commonDao.getMdmNotification("D");
			notificationMap.put("BDAYNotify", bdayNotifyList);
			notificationMap.put("DEALNotify", dealNotifyList);
			exceptionCode.setResponse(notificationMap);
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, HttpStatus.OK);	
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, HttpStatus.OK);
		}
	}
	@PostMapping(value = "updateNotification")
	//@ApiOperation(value = "Update Notification",notes = "Notification Update",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> updateNotification(@RequestBody NotificationsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		JSONExceptionCode jsonExceptionCode  = null;
		int retVal = Constants.SUCCESSFUL_OPERATION;
		try {
			//Notification Status update
			retVal = commonDao.updateNotification(vObject);
			if(retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("Notifacation Updated");	
			}

			//Deal - Next Action Date updation for Reminder
			if("D".equalsIgnoreCase(vObject.getNotificationType()) && vObject.getDealReminder()) {
				retVal = commonDao.updateDealActionDate(vObject);
				if(retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				}else {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Notifacation Updated");	
				}
			}
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Notification Updated", exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch (Exception e) {
			e.printStackTrace();
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Updation Error!!", exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "validategeneatedotp")
	public ResponseEntity<JSONExceptionCode> doValidateGeneatedOTP(HttpServletRequest request,
	        HttpServletResponse response) {
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    HttpStatus status = HttpStatus.OK;
	    JSONExceptionCode exceptionCode = new JSONExceptionCode();
	    HttpSession session = request.getSession(false); // don't create new session if none
	 
	    try {
	        String validationStatus = String.valueOf(request.getAttribute("OTPValidationStatus"));
	        if (!"SUCCESS".equalsIgnoreCase(validationStatus)) {
	            exceptionCode.setMessage(String.valueOf(session != null ? session.getAttribute("loginStatus") : "OTP validation failed"));
	            exceptionCode.setStatus(Constants.ERRONEOUS_OPERATION);
	            return new ResponseEntity<>(exceptionCode, status);
	        }
	 
	        // token and expiry
	        Object tempTokenObj = request.getAttribute("tempTokenStorage");
	        String tempToken = tempTokenObj != null ? tempTokenObj.toString() : "";
	        responseMap.put("token", "VISION" + Base64.encode((tempToken).getBytes()));
	        Date expirationDate = SessionContextHolder.getTokenProps(tempToken).getNxtExpireDate();
	 
	        String connectionId = getConnectionIdFromTemporaryToken(request);
	        VisionUsersVb userDetails = null;
	        List<Object> menuSessionList = new ArrayList<>();
	 
	        // try session-stored menu first
	        if (session != null && session.getAttribute("menuDetails") != null) {
	            menuSessionList = (List<Object>) session.getAttribute("menuDetails");
	            // if menu was in session, try to get userDetails from SessionContextHolder context
	            userDetails = SessionContextHolder.getContext();
	        }
	 
	        // fallback: try to get userDetails using connectionId (temp token)
	        if (userDetails == null) {
	            userDetails = SessionContextHolder.getUserDetails(connectionId);
	        }
	 
	        // If still null, we cannot proceed safely — return error
	        if (userDetails == null) {
	            // log helpful info for debugging
	        	System.out.println("validateGeneratedOTP: userDetails is null. connectionId={"+connectionId+"}, tempTokenPresent={"+!tempToken.isEmpty()+"}, sessionMenuPresent={"+(session != null && session.getAttribute("menuDetails") != null)+"}");
	            exceptionCode.setMessage("User details not found after OTP validation.");
	            exceptionCode.setStatus(Constants.ERRONEOUS_OPERATION);
	            return new ResponseEntity<>(exceptionCode, status);
	        }
	 
	        // If menuSessionList empty, obtain it from authenticationBean using resolved userDetails
	        if (menuSessionList == null || menuSessionList.isEmpty()) {
	            menuSessionList = authenticationBean.getMenuForUser(userDetails);
	        }
	 
	        // Build response
	        responseMap.put("expired_on", expirationDate.getTime());
	        responseMap.put("user_details", userDetails);
	        if (menuSessionList != null && menuSessionList.size() > 1) {
	            responseMap.put("menu_details", menuSessionList.get(0));
	            responseMap.put("menu_hierarchy", menuSessionList.get(1));
	        }
	 
	        // safe call now that userDetails is guaranteed non-null
	        try {
	            visionUsersDao.doDeleteOpt(userDetails.getVisionId());
	            System.out.println("Sucess to delete OTP for userId {"+userDetails.getVisionId()+"}");
	        } catch (Exception daoEx) {
	            // Log and continue — deletion failure shouldn't break the whole flow
	        	System.out.println("Failed to delete OTP for userId {"+userDetails.getVisionId()+"}: {"+daoEx.getMessage()+"}");
	        }
	 
	        responseMap.put("businessDay", session != null ? session.getAttribute("businessDay") : null);
	        exceptionCode.setResponse(responseMap);
	        SessionContextHolder.removeTempTokenForConnectionId(request.getHeader("temporary-token"));
	        exceptionCode.setStatus(Constants.SUCCESSFUL_OPERATION);
	        return new ResponseEntity<>(exceptionCode, status);
	 
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	        // Return a controlled error rather than rethrowing raw runtime exception
	        exceptionCode.setMessage("Internal server error while validating OTP.");
	        exceptionCode.setStatus(Constants.ERRONEOUS_OPERATION);
	        return new ResponseEntity<>(exceptionCode, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	 
	 

	
	@PostMapping(value = "regeneatedotp")
	public ResponseEntity<JSONExceptionCode> doReGeneratedOTP(HttpServletRequest request,
			HttpServletResponse response) {
			LinkedHashMap<String, Object> responseMap = new LinkedHashMap<String, Object>();
			HttpStatus status = HttpStatus.OK;
			JSONExceptionCode exceptionCode = new JSONExceptionCode();
			HttpSession session = request.getSession();
			try {
				String validationStatus = String.valueOf(request.getAttribute("OTPValidationStatus"));
				String img = String.valueOf(request.getAttribute("img"));
				if("SUCCESS".equalsIgnoreCase(validationStatus)) {
					responseMap.put("temporary-token", request.getHeader("temporary-token"));
					responseMap.put("twoFactorAuthFlag", twoFactorAuthFlag);
					responseMap.put("img", img);
					exceptionCode.setMessage(String.valueOf(session.getAttribute("loginStatus")));
					exceptionCode.setStatus(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setResponse(responseMap);
				}else {
					responseMap.put("temporary-token", request.getHeader("temporary-token"));
					responseMap.put("twoFactorAuthFlag", twoFactorAuthFlag);
					exceptionCode.setMessage(String.valueOf(session.getAttribute("loginStatus")));
					exceptionCode.setStatus(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setResponse(responseMap);
				}
				return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeCustomException(e.getMessage());
			}
	}
	protected String getConnectionIdFromTemporaryToken(HttpServletRequest req) throws SecurityProviderException {
		String tempTokenForCid = req.getHeader("temporary-token");
		if (ValidationUtil.isValid(tempTokenForCid)) {
			return SessionContextHolder.getConnectionIdFromTempToken(tempTokenForCid);
		}
		return null;
	}
}