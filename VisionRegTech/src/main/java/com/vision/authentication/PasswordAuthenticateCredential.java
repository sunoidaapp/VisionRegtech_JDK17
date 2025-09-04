package com.vision.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vision.VisionRegTech;
import com.vision.authentication.service.AuthenticationService;
import com.vision.dao.CommonDao;
import com.vision.dao.VisionUsersDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class PasswordAuthenticateCredential {

	@Autowired
	VisionUsersDao visionUsersDao;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	CommonDao commonDao;
	@Autowired
	VisionRegTech visionRegTech;
	
	@PostMapping(value = "PasswordReset")
	public ResponseEntity<JSONExceptionCode> PasswordReset(HttpServletRequest request,
			HttpServletResponse response, @RequestBody VisionUsersVb vObj) {
		HttpStatus status = HttpStatus.OK;
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		try {
			AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
			
			ExceptionCode exceptionCode1 = null;
			/*String reqVisionId = request.getParameter("reqVisionId");
			String password = request.getParameter("password");
			
			System.out.println(reqVisionId);
			System.out.println(password);
			vObj.setVisionId(Integer.parseInt(reqVisionId));
			vObj.setPassword1(password);*/
			
			String visionToken = request.getHeader("VisionAuthenticate");
			vObj.setVisionToken(visionToken);
			exceptionCode1 = authenticationBean.callProcToResetPassword(vObj);
			exceptionCode.setOtherInfo(exceptionCode1.getErrorCode());
			exceptionCode.setMessage(exceptionCode1.getErrorMsg());
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	@PostMapping(value = "GeneratePassword")
	public ResponseEntity<JSONExceptionCode> GeneratePassword(HttpServletRequest request,
			HttpServletResponse response, @RequestBody VisionUsersVb vObj) {
		HttpStatus status = HttpStatus.OK;
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		try {
			AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
//			VisionUsersVb vObj = new VisionUsersVb();
			ExceptionCode exceptionCode1 = null;
/*			String reqVisionId = request.getParameter("reqVisionId");
			String password = request.getParameter("password");
			System.out.println(reqVisionId);
			System.out.println(password);
			vObj.setVisionId(Integer.parseInt(reqVisionId));
			vObj.setPassword1(password);*/
			
			exceptionCode1 = authenticationBean.callProcToResetPassword(vObj);
			exceptionCode.setOtherInfo(exceptionCode1.getErrorCode());
			exceptionCode.setMessage(exceptionCode1.getErrorMsg());
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	@PostMapping(value = "changePasswordFirstTime")
	public ResponseEntity<JSONExceptionCode> PasswordResetFirstTime(HttpServletRequest request,
			HttpServletResponse response, @RequestBody VisionUsersVb vObj) {
		HttpStatus status = HttpStatus.OK;
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		try {
			AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
			
			ExceptionCode exceptionCode1 = null;
			exceptionCode1 = authenticationBean.callProcToResetPassword(vObj);
			exceptionCode.setOtherInfo(exceptionCode1.getErrorCode());
			exceptionCode.setStatus(exceptionCode1.getErrorCode());
			exceptionCode.setMessage(exceptionCode1.getErrorMsg());
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	@PostMapping(value = "ForgotPassword")
	public ResponseEntity<JSONExceptionCode> ForgotPassword(HttpServletRequest request,
			HttpServletResponse response, @RequestBody VisionUsersVb vObject) {
		HttpStatus status = HttpStatus.OK;
		JSONExceptionCode exceptionCode = new JSONExceptionCode();
		try {
			

			AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
			
			String pwdResetURL = authenticationBean.findVisionVariableValuePasswordURL();
			String pwdResetTime = authenticationBean.findVisionVariableValuePasswordResrtTime();
			ExceptionCode exceptionCode1 = null;

//			VisionUsersVb vObj = new VisionUsersVb();
//			String requestmailId = request.getParameter("emailID");
//			String resultForgotBy = request.getParameter("txtResultForgotBy");

			if (ValidationUtil.isValid(vObject.getTroubleShootFor())) {
				vObject.setPwdResetTime(pwdResetTime);
				vObject.setPasswordResetURL(pwdResetURL);
				exceptionCode1 = authenticationBean.callProcToPopulateForgotPasswordEmail(vObject, vObject.getTroubleShootFor());
				if (exceptionCode1.getErrorCode() == 1) {
				} else if (exceptionCode1.getErrorCode() == 0) {
					
					vObject.setUserName(exceptionCode1.getErrorMsg().split("@")[0]);
					vObject.setUserLoginId(exceptionCode1.getErrorMsg().split("@")[1]);
					vObject.setVisionId(Integer.parseInt(exceptionCode1.getErrorMsg().split("@")[2]));
//					String ciphertext = ValidationUtil.passwordEncryptWithUrlEncode(Integer.toString(vObject.getVisionId()));
					String ciphertext = Integer.toString(vObject.getVisionId());
					vObject.setScreenName(ciphertext);
					exceptionCode1 = authenticationBean.getVisionUsersDao().prepareAndSendMail(vObject, "",vObject.getTroubleShootFor());
					if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						vObject.setEmailStatus("S");
					} else {
						vObject.setEmailStatus("E");
//						ssn.setAttribute("forgotPasswordErrorStatus", exceptionCode.getErrorMsg());
					}
					if ("S".equalsIgnoreCase(vObject.getEmailStatus())) {
						int retVal = authenticationBean.doPasswordResetInsertion(vObject);
						if (retVal != Constants.SUCCESSFUL_OPERATION) {
						}
//						ssn.setAttribute("forgotPasswordStatus", exceptionCode.getErrorMsg());
					}
				}
			}
			exceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(), exceptionCode1.getErrorMsg(), exceptionCode1.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(exceptionCode, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	public VisionRegTech getVisionRegTech() {
		return visionRegTech;
	}
	public void setVisionRegTech(VisionRegTech visionRegTech) {
		this.visionRegTech = visionRegTech;
	}
	
	
	
	
}