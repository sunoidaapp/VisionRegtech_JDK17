package com.vision.authentication;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import com.vision.VisionRegTech;
import com.vision.download.ExportXlsServlet;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import jcifs.Config;
import jcifs.util.Base64;
import jespa.security.Account;
import jespa.security.PasswordCredential;
import jespa.security.SecurityProvider;
import jespa.security.SecurityProviderException;
import jespa.util.CacheMap;
import jespa.util.Csv;
import jespa.util.LogStream;

/**
 * Servlet Filter implementation class SecurityFilter
 */
@Component
public class SecurityFilter implements Filter {

	@Autowired
	private Environment env;

	@Value("${twoFactorAuthMethod}")
	private String twoFactorAuthMethod;
	@Autowired
	//visionRegTech visionMdmApplication;
	VisionRegTech visionRegTech;
	
	@Autowired
	ExportXlsServlet exportXlsServlet;

	static final String DEFAULT_SECURITY_PROVIDER = "jespa.ntlm.NtlmSecurityProvider";
	static final String JESPA_FILTER_PROVIDER_STATE = "jespa.provider.state";
	static final String INDEX_REDIRECT_PAGE = "index.jsp";
	static final String INDEX_PAGE = "index.jsp";
	static final String USER_LOGIN_PAGE = "UserLogin.jsp";
	static LogStream log = null;
	jespa.security.Properties providerProperties;
	Constructor providerConstructor;
	String usernameParameter;
	String passwordParameter;
	String otpParameter;
	String logoutParameter;
	String anonymousParameter;
	String fallbackLocation;
	String appName;
	String[] excludes;
	String[] groupsDenied;
	String[] groupsAllowed;
	AuthContextPool<String, AuthContext> authContexts;
	// Contains user Name as Key with connection ID
	Map<String, String> sessionUsers;
	boolean capabilitiesAcceptNtlmssp;
	boolean capabilitiesAcceptSpnego;
	boolean disableWarning;
	String name;
	ServletContext servletContext;
	Map serviceProperties;
	String propertiesPath;
	long propertiesLastModified;
	long propertiesLastStat;
	FileOutputStream logStream;
	boolean skipAuth = false;
	boolean ldapAuth = false;
	String adServers = "";
	String activeDirectoryFlag = "Y";
	boolean twoFactorAuthFlag = false;
	/*
	 * To help protect the service.password in the config file this example filter
	 * optionally encrypts it. This is the secret key used to do that.
	 */
	static final String SECRET = "Spiral Architect";

	private static final String CONTEXT_FACTORY_CLASS = "com.sun.jndi.ldap.LdapCtxFactory";
	private String ldapServerUrls[];
	private int lastLdapUrlIndex;
	private String domainName;

	private List<String> excludedUrls;

	private static final String key = "aesEncryptionKey";
	private static final String initVector = "encryptionIntVec";
	
	/**
	 * Default constructor.
	 */
	public SecurityFilter() {
		// TODO Auto-generated constructor stub
	}

	class HttpStatusServletResponse extends HttpServletResponseWrapper {
		int sc = 200;

		HttpStatusServletResponse(HttpServletResponse rsp) {
			super(rsp);
		}

		public void setStatus(int sc) {
			super.setStatus(sc);
			this.sc = sc;
		}

//		public void setStatus(int sc, String sm) {
//			super.setStatus(sc, sm);
//			this.sc = sc;
//		}

		public int getStatus() {
			return this.sc;
		}
	}

	class AuthContextPool<K, V> extends CacheMap<K, V> {
		public AuthContextPool(long expiration, long period) {
			super("", expiration, period);
		}

		protected void dispose(V val) {
			if ((val instanceof SecurityFilter.AuthContext)) {
				SecurityFilter.AuthContext ctx = (SecurityFilter.AuthContext) val;
				if (ctx.provider != null)
					try {
						ctx.provider.dispose();
					} catch (SecurityProviderException spe) {
						System.err.print(spe.getMessage());
					}
			}
		}
	}

	class AuthContext {
		SecurityProvider provider;
		byte[] token;

		AuthContext(String name) throws SecurityProviderException {
			this.provider = SecurityFilter.this.newSecurityProvider();
			if (LogStream.level >= 4)
				SecurityFilter.log.println("HttpSecurityService: AuthContext: " + name);
		}
	}

	SecurityProvider newSecurityProvider() throws SecurityProviderException {
		try {
			return (SecurityProvider) this.providerConstructor.newInstance(new Object[] { this.providerProperties });
		} catch (Exception ex) {
			throw new SecurityProviderException(0, "Failed to instantiate SecurityProvider", ex);
		}
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		if (this.propertiesPath != null) {
			synchronized (this) {
				if (System.currentTimeMillis() > this.propertiesLastStat) {
					try {
						init(false);
					} catch (SecurityProviderException spe) {
						throw new ServletException("Failed to re-initialize HttpSecurityService", spe);
					} finally {
						this.propertiesLastStat = (System.currentTimeMillis() + 5000L);
					}
				}
			}
		}
		HttpServletRequest req = (HttpServletRequest) request;
		HttpStatusServletResponse rsp = new HttpStatusServletResponse((HttpServletResponse) response);
		// rsp.addHeader("Connection", "Keep-Alive");
		// rsp.addHeader("Keep-Alive", "timeout=60000");
		HttpSession ssn = req.getSession(true);
		ssn.setAttribute("visionUserToken", getTokenForLdap(req));		
		// For IE User Login Issue
		rsp.setHeader("Cache-Control", "no-cache");
		rsp.setHeader("Cache-Control", "no-store");
		rsp.setDateHeader("Expires", 0);
		rsp.setHeader("Pragma", "no-cache");
		// For IE User Login Issue

		boolean isProtected = isProtected(req);
		boolean isLogout = false;
		boolean isAnonymous = false;
		boolean isUnauthorized = true;
		boolean isUnauthorizedOpt = false;
		boolean isChallengeRequired = false;
		SecurityProvider provider = null;
		byte[] token = null;
		Object credential = null;
		String connectionId = "";
		String rpath = getRequestPath(req);
		AuthContext ctx = null;
		isLogout = isLogout(req);
		try {
//			connectionId = getConnectionId(req);
			
			if(twoFactorAuthFlag) {
				connectionId = getConnectionIdFromTemporaryToken(req);
			}else {
				connectionId = getConnectionId(req);				
			}
			ssn.setAttribute("skipAuthentication", skipAuth);
			if (skipAuth) {
				if (ssn.getAttribute("userDetails") != null) {
					SessionContextHolder.addOrUpdate(connectionId, ssn);
					ssn.setAttribute("sessionToken", SessionContextHolder.encodedSessionId(connectionId));
					SessionContextHolder.setContext(SessionContextHolder.getUserDetails(connectionId));
				} else {
					String user = System.getProperty("user.name");
//					String user = "GLADYS.RUGA";
					ssn.setAttribute("ntLoginDetails", user);
					login(req, rsp, user);
				}
				chain.doFilter(request, response);
				return;
			} else if (!isProtected && !isLogout) {
				// rsp.setDateHeader("Expires",
				// System.currentTimeMillis()+24*60*60*1000);
				chain.doFilter(request, response);
				return;
			}else if(rpath.contains("validategeneatedotp")){
				String strOtp = req.getParameter(this.otpParameter);
				if (ValidationUtil.isValid(strOtp)) {
					String ciphertextdec = strOtp;
					String sysGenOtp = (String) ssn.getAttribute("LoginOtp");
					AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
					VisionUsersVb userDetails =  SessionContextHolder.getUserDetails(connectionId);
					System.out.println(" Vision Id 1 "+userDetails.getVisionId());
					sysGenOtp = ""+authenticationBean.getVisionUsersDao().getOtpByVisionId(userDetails.getVisionId());
					
					if (ValidationUtil.isValid(sysGenOtp) && sysGenOtp.equals(ciphertextdec)) {
						System.out.println("two factor  Validated Successfully!! ");
						SessionContextHolder.addOrUpdate(connectionId, ssn);
						ssn.setAttribute("sessionToken", SessionContextHolder.encodedSessionId(connectionId));
						SessionContextHolder.setContext(SessionContextHolder.getUserDetails(connectionId));
						isUnauthorized = false;
						isChallengeRequired = false;
						req.setAttribute("OTPValidationStatus", "SUCCESS");
					} else {
						System.out.println("OTP not matched. Please enter valid OTP.");
						String twoFactorMethod = twoFactorAuthMethod.equalsIgnoreCase("C") ? "Captcha":"OTP";
						ssn.setAttribute("loginStatus", twoFactorMethod+" not matched. Please enter valid "+twoFactorMethod);
						req.setAttribute("OTPValidationStatus", "FAILED");
						isUnauthorized = false;
						isUnauthorizedOpt = true;
						isChallengeRequired = false;
					}
				} else {
					System.out.println("User OTP has expired.");
					ssn.setAttribute("loginStatus","User OTP has expired.");
					isUnauthorized = true;
				}
			} else if (rpath.contains("regeneatedotp")) {
				ExceptionCode exceptionCode1 = null;
				String getOtp = ValidationUtil.generateOTP(twoFactorAuthMethod);
				ssn.setAttribute("LoginOtp", getOtp);
				VisionUsersVb vObject =  SessionContextHolder.getUserDetails(connectionId);
				AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
//				ExceptionCode exceptionCode1 = authenticationBean.getVisionUsersDao().prepareAndSendMail(vObject, getOtp, "");
				
				if(!"C".equalsIgnoreCase(twoFactorAuthMethod)) {
					 exceptionCode1 = authenticationBean.getVisionUsersDao().prepareAndSendMail(vObject, getOtp, "");
					}else {
						exceptionCode1 =CommonUtils.generateCaptchaImageBase64(getOtp);
					}
				
				if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					System.out.println("Mail sent successful!!");
					ssn.setAttribute("loginStatus", exceptionCode1.getErrorMsg());
					authenticationBean.getVisionUsersDao().doDeleteOpt(vObject.getVisionId());
					req.setAttribute("OtpSentSuccess", "YES");
					req.setAttribute("OTPValidationStatus", "SUCCESS");
					authenticationBean.getVisionUsersDao().doInsertionOpt(vObject.getVisionId(), getOtp);
					req.setAttribute("img", exceptionCode1.getResponse());
				//	ssn.setAttribute("loginStatus","OTP Resend Successfully.");
				}else {
					req.setAttribute("OtpSentSuccess", "NO");
					req.setAttribute("OTPValidationStatus", "FAILED");
//					ssn.setAttribute("loginStatus","OTP Resend railed. Error: "+exceptionCode1.getErrorMsg());
					ssn.setAttribute("loginStatus", exceptionCode1.getErrorMsg());
					req.setAttribute("img", exceptionCode1.getResponse());
				}
				req.setAttribute("TwoFactorAuth", "TwoFactorAuth");
				isUnauthorized = false;
				isChallengeRequired = false;
				isUnauthorizedOpt = true;
			}else if (rpath.contains("ForgotPassword") || rpath.contains("GeneratePassword")) {
				String tempTokenForCid = req.getHeader("temporary-token");
				ConnectionHolder connectionHolder = SessionContextHolder.getconnectionClassFromTempToken(tempTokenForCid);
				if(!ValidationUtil.isValid(tempTokenForCid) || !ValidationUtil.isValid(connectionHolder)) {
					isUnauthorized = true;
					isChallengeRequired = true;
				}else {
					isUnauthorized = false;
					isChallengeRequired = false;
				}
			}else if(rpath.contains("changePasswordFirstTime")) {
				isUnauthorized = false;
				isChallengeRequired = false;
			}else if (rpath.contains("authenticate")) {
				try {
					credential = getRequestCredential(req);
					if (LogStream.level >= 4)
						log.println("HttpSecurityService: credential=" + credential);

					if (credential != null) {
						if (!ldapAuth) { // If LDAP Auth set to false in ntlm , then
											// it will check for Jespa.
							SecurityProvider tprovider = newSecurityProvider();
							try {
								tprovider.authenticate(credential);
								isAllowedAccess(tprovider, req, rsp);

								if (LogStream.level >= 2) {
									log.println("HttpSecurityService: " + tprovider.getIdentity()
											+ " successfully logged in");
								}
								storeProviderState(ssn, tprovider, connectionId);
								if (provider != null)
									provider.dispose();
								provider = tprovider;
								isUnauthorized = false;
								isChallengeRequired = false;
							} catch (SecurityProviderException spe) {
								spe.printStackTrace();
								ssn.setAttribute("loginStatusWithSPE", spe.getMessage());
								if (tprovider != null && tprovider.getAccount(null, null) != null) {
									System.out.println(tprovider.getAccount(null, null).getProperty("sAMAccountName"));
								}
								if (LogStream.level >= 2) {
									log.println("HttpSecurityService: " + connectionId + ": Login failed: "
											+ spe.getMessage());
									if (LogStream.level >= 4) {
										spe.printStackTrace(log);
									}
								}
								if (spe.getCode() == SecurityProviderException.STATUS_INVALID_CREDENTIALS
										&& credential != null) {
									String loginAttempts = (String) ssn.getAttribute("LoginAttempts");
									if (ValidationUtil.isValidId(loginAttempts)) {
										/*
										 * if(Integer.parseInt(loginAttempts) >=3){ ssn.setAttribute("loginStatus",
										 * "User has been locked. More than 3 Invalid attempts" ); }
										 */
										if (Integer.parseInt(loginAttempts) > 2) {
											token = null;
											removeAuthContext(connectionId);
											ssn.removeAttribute("LoginAttempts");
//											forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
											return;
										}
										ssn.setAttribute("LoginAttempts",
												String.valueOf(Integer.parseInt(loginAttempts) + 1));
									} else {
										ssn.setAttribute("LoginAttempts", "1");
									}
									if (((PasswordCredential) credential).getSecurityPrincipal() != null) {
										updateUnsuccessfulLoginAttempts(
												((PasswordCredential) credential).getSecurityPrincipal().getUsername());
									}
								}
								ssn.removeAttribute("jespa.provider.state");
								tprovider.dispose();
								isUnauthorized = true;
								isChallengeRequired = false;
							}
						} else { // If LDAP is true in ntlm then it will work on the
									// ldap authentication by using the domain name
							token = getTokenForLdap(req);
							if (token == null) {
								
								VisionUsersVb vObj = new VisionUsersVb();
								AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
								String loggedInUsername = request.getParameter("username");
								String strUserName = loggedInUsername.indexOf("\\") >= 0
										? loggedInUsername.substring(loggedInUsername.indexOf("\\"), loggedInUsername.length())
										: loggedInUsername;
								if (strUserName.indexOf("\\") >= 0) {
									strUserName = strUserName.replace("\\", "");
								}
								ExceptionCode exceptionCode = authenticationBean.findActiveDirectory(req, strUserName);
								if (exceptionCode != null) {
									if (exceptionCode.getRequest() != null) {
										vObj = (VisionUsersVb) exceptionCode.getRequest();
										activeDirectoryFlag = vObj.getActiveDirectoryFlag();
										log.println("User - ActiveDirectoryFlag:" + loggedInUsername);
										System.out.println("User - ActiveDirectoryFlag:" + loggedInUsername+ " activeDirectoryFlag : " + activeDirectoryFlag);
									} else {
										ssn.setAttribute("loginStatus", exceptionCode.getErrorMsg());
										vObj.setUserLoginId(loggedInUsername.toLowerCase());
										vObj.setActiveDirectoryFlag("");
										authenticationBean.getVisionUsersDao().doInsertUserLoginAudit(vObj, request.getRemoteAddr(), 1, "User does not exists or more than one user exists");
										//forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
										//return;
										isUnauthorized = true;
										isChallengeRequired = false;
									}
								}
								if ("Y".equalsIgnoreCase(activeDirectoryFlag)) {
									String decPwd = String.valueOf(((PasswordCredential) credential).getPassword());
									/* Not required with Angular
									 * 
									 * if (ssn.getAttribute("userDetails") != null) {
										SessionContextHolder.addOrUpdate(connectionId, ssn);
										ssn.setAttribute("sessionToken",
												SessionContextHolder.encodedSessionId(connectionId));
										SessionContextHolder.setContext(SessionContextHolder.getUserDetails(connectionId));
									}*/
									String userId = req.getParameter(this.usernameParameter);
									String providedDomainName = "";
									if (userId.contains("\\")) {
										providedDomainName = (userId.split("\\\\"))[0];
										userId = (userId.split("\\\\"))[1];
									}
									ssn.setAttribute("ntLoginDetails", userId);
									String adServerslst[] = null;
									boolean isAuthDomain = false;
									
									if(adServers.equalsIgnoreCase("skip")) { // Must remove this condition for production environment - DD
										isAuthDomain = true;
										login(req, rsp, userId);
										isUnauthorized = false;
										isChallengeRequired = false;
									} else if (ValidationUtil.isValid(adServers)) {
										adServerslst = adServers.split(",");
										boolean isValidDomainProvided = false;
										if (ValidationUtil.isValid(providedDomainName)) {
											for (String domainName : adServerslst) {
												if (providedDomainName.equalsIgnoreCase(domainName))
													isValidDomainProvided = true;
											}
										}
										if (!ValidationUtil.isValid(providedDomainName) || isValidDomainProvided) {
											if (isValidDomainProvided) {
												adServerslst = new String[1];
												adServerslst[0] = providedDomainName;
											}

											for (String adServerName : adServerslst) {
												if (isAuthDomain)
													continue;
												try {
													getActiveDirectoryAuthentication(adServerName.trim());
													if (ldapServerUrls != null && ldapServerUrls.length > 0) {
														boolean authResult = authenticate(userId, decPwd);
														if (authResult) {
															isAuthDomain = true;
															login(req, rsp, userId);
															isUnauthorized = false;
															isChallengeRequired = false;
														} else {
															if (credential != null) {
																isAuthDomain = true;
																String loginAttempts = (String) ssn
																		.getAttribute("LoginAttempts");
																if (ValidationUtil.isValidId(loginAttempts)) {
																	if (Integer.parseInt(loginAttempts) > 2) {
																		token = null;
																		removeAuthContext(connectionId);
																		ssn.removeAttribute("LoginAttempts");
//																		forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
																		return;
																	}
																	ssn.setAttribute("LoginAttempts", String
																			.valueOf(Integer.parseInt(loginAttempts) + 1));
																} else {
																	ssn.setAttribute("LoginAttempts", "1");
																}
																if (((PasswordCredential) credential)
																		.getSecurityPrincipal() != null) {
																	updateUnsuccessfulLoginAttempts(
																			((PasswordCredential) credential)
																					.getSecurityPrincipal().getUsername());
																}
															}
															ssn.removeAttribute("jespa.provider.state");
															isUnauthorized = true;
															isChallengeRequired = false;
														}
													} else {
														if (credential != null) {
															String loginAttempts = (String) ssn
																	.getAttribute("LoginAttempts");
															if (ValidationUtil.isValidId(loginAttempts)) {
																if (Integer.parseInt(loginAttempts) > 2) {
																	token = null;
																	removeAuthContext(connectionId);
																	ssn.removeAttribute("LoginAttempts");
//																	forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
																	return;
																}
																ssn.setAttribute("LoginAttempts", String
																		.valueOf(Integer.parseInt(loginAttempts) + 1));
															} else {
																ssn.setAttribute("LoginAttempts", "1");
															}
														}
														ssn.setAttribute("loginStatusWithSPE",
																"Invalid Domain Configuration. Please Contact System Admin");
														ssn.removeAttribute("jespa.provider.state");
														isUnauthorized = true;
														isChallengeRequired = false;
													}

												} catch (AccountException | FailedLoginException | NamingException spe) {
													spe.printStackTrace();
													ssn.setAttribute("loginStatusWithSPE", spe.getMessage());
													if (LogStream.level >= 2) {
														log.println("HttpSecurityService: " + connectionId
																+ ": Login failed: " + spe.getMessage());
														if (LogStream.level >= 4) {
															spe.printStackTrace(log);
														}
													}
													String loginAttempts = (String) ssn.getAttribute("LoginAttempts");
													if (ValidationUtil.isValidId(loginAttempts)) {
														if (Integer.parseInt(loginAttempts) > 2) {
															token = null;
															removeAuthContext(connectionId);
															ssn.removeAttribute("LoginAttempts");
//															forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
															return;
														}
														ssn.setAttribute("LoginAttempts",
																String.valueOf(Integer.parseInt(loginAttempts) + 1));
													} else {
														ssn.setAttribute("LoginAttempts", "1");
													}
													if (((PasswordCredential) credential).getSecurityPrincipal() != null) {
														updateUnsuccessfulLoginAttempts(((PasswordCredential) credential)
																.getSecurityPrincipal().getUsername());
													}
													ssn.removeAttribute("jespa.provider.state");
													isUnauthorized = true;
													isChallengeRequired = false;
												} catch (@SuppressWarnings("hiding") LoginException e) {
													e.printStackTrace();
													if (!isAuthDomain) {
														ssn.setAttribute("loginStatusWithSPE",
																"Invalid Domain Configuration. Please Contact System Admin");
													} else {
														ssn.setAttribute("loginStatusWithSPE", e.getMessage());
													}
													if (LogStream.level >= 2) {
														log.println("HttpSecurityService: " + connectionId
																+ ": Login failed: " + e.getMessage());
														if (LogStream.level >= 4) {
															e.printStackTrace(log);
														}
													}
													String loginAttempts = (String) ssn.getAttribute("LoginAttempts");
													if (ValidationUtil.isValidId(loginAttempts)) {
														if (Integer.parseInt(loginAttempts) > 2) {
															token = null;
															removeAuthContext(connectionId);
															ssn.removeAttribute("LoginAttempts");
//															forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
															return;
														}
														ssn.setAttribute("LoginAttempts",
																String.valueOf(Integer.parseInt(loginAttempts) + 1));
													} else {
														ssn.setAttribute("LoginAttempts", "1");
													}
													if (((PasswordCredential) credential).getSecurityPrincipal() != null) {
														updateUnsuccessfulLoginAttempts(((PasswordCredential) credential)
																.getSecurityPrincipal().getUsername());
													}
													ssn.removeAttribute("jespa.provider.state");
													isUnauthorized = true;
													isChallengeRequired = false;
												}
											}
										} else {
											ssn.setAttribute("loginStatusWithSPE","Provided domain is not authorised to access from Vision ["+ providedDomainName + "]");
											/* forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE); */
											ssn.removeAttribute("jespa.provider.state");
											isUnauthorized = true;
											isChallengeRequired = false;
										}
									}
									
								}else {
									System.out.println("Non Ad User : " + loggedInUsername);
									ssn.setAttribute("ntLoginDetails", loggedInUsername);
									String decPwd1 = String.valueOf(((PasswordCredential) credential).getPassword());
//									String decPwd = DigestUtils.md5Hex(decPwd1);
									req.setAttribute("Crctpwd", decPwd1);
									req.setAttribute("activeDirectoryFlag", activeDirectoryFlag);
									req.setAttribute("username", loggedInUsername);
									login(req, rsp, loggedInUsername);
									isUnauthorized = false;
									isChallengeRequired = false;
								}
								
								if(twoFactorAuthFlag && !isUnauthorized){
									ExceptionCode exceptionCode1 = null;
									String getOtp = ValidationUtil.generateOTP(twoFactorAuthMethod);
									ssn.setAttribute("LoginOtp", getOtp);
									VisionUsersVb vObject = (VisionUsersVb) ssn.getAttribute("userDetails");
									AuthenticationBean authenticationBean1 = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
									//AuthenticationBean authenticationBean = (AuthenticationBean)SpringBeanFinder.lookUp("authenticationBean");
									if(!"C".equalsIgnoreCase(twoFactorAuthMethod)) {
									 exceptionCode1 = authenticationBean1.getVisionUsersDao().prepareAndSendMail(vObject, getOtp, "");
									}else {
										exceptionCode1 =CommonUtils.generateCaptchaImageBase64(getOtp);
									}
									if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
										System.out.println("Mail sent SuccessFully!!");
										req.setAttribute("OtpSentSuccess", "YES");
										authenticationBean.getVisionUsersDao().doDeleteOpt(vObject.getVisionId());
										authenticationBean1.getVisionUsersDao().doInsertionOpt(vObject.getVisionId(), getOtp);
										req.setAttribute("img", exceptionCode1.getResponse());
									}else {
										req.setAttribute("OtpSentSuccess", "NO");
									}
									
//									req.setAttribute("tempTokenStorage", EncryptionServlet.genrateKey(req, rsp));
									req.setAttribute("TwoFactorAuth", "TwoFactorAuth");
								}
								
							}// Ldatp Ends Here
							
						}
					} else {
						if (ctx != null && ctx.provider != null) {
							System.out.println((String) ctx.provider.get("ntlmssp.account.name"));
						}

						isUnauthorized = true;
						isChallengeRequired = false;
						if (provider != null) {
							try {
								provider.dispose();
							} catch (SecurityProviderException spe2) {
							}
						}
					}

				} catch (SecurityProviderException spe1) {
					spe1.printStackTrace();
					if (ctx != null && ctx.provider != null) {
						System.out.println((String) ctx.provider.get("ntlmssp.account.name"));
					}
					if (LogStream.level >= 4)
						spe1.printStackTrace(log);

					isUnauthorized = true;
					isChallengeRequired = false;
					if (provider != null) {
						try {
							provider.dispose();
						} catch (SecurityProviderException spe2) {
						}
					}
				}
			}
		} catch (SecurityProviderException e) {
			e.printStackTrace();
		}
		boolean isForbidden = (!this.capabilitiesAcceptSpnego) && (!this.capabilitiesAcceptNtlmssp);
		if (LogStream.level >= 3) {
			log.println("HttpSecurityService: C: " + req.getMethod() + " " + getRequestPath(req));
		}
		if (LogStream.level >= 4) {
			log.println("HttpSecurityService: Request Headers: " + getRequestHeaders(req));
			if (LogStream.level >= 5)
				logSessionAttributes(req);
		}
		try {
			if (isLogout) {
				if (LogStream.level >= 4) {
					log.println("HttpSecurityService: isLogout=true, removing provider state");
				}
				ssn.removeAttribute("jespa.provider.state");
				connectionId = getConnectionId(req);
				SessionContextHolder.expireSession(connectionId);
				isChallengeRequired = isUnauthorized = isProtected;
				try {
					ctx = getAuthContext(connectionId);
					removeAuthContext(connectionId);
					ctx = null;
				} catch (Exception exception) {
				}
				ssn.invalidate();
				return;
			} else {
				if (LogStream.level >= 4) {
					log.println("HttpSecurityService: Loading session state from session " + ssn.getId());
				}
				if (!ldapAuth) {
					byte[] providerState = (byte[]) (byte[]) ssn.getAttribute("jespa.provider.state");
					if (providerState != null) {
						if (LogStream.level >= 4)
							log.println("HttpSecurityService: Importing provider state");
						try {
							provider = newSecurityProvider();
							provider.importState(providerState);
							isUnauthorized = false;
							isChallengeRequired = false;
						} catch (SecurityProviderException spe) {
							if (LogStream.level >= 1) {
								spe.printStackTrace(log);
							}
							provider = null;
							ssn.removeAttribute("jespa.provider.state");
						}
					} else {
						isChallengeRequired = isUnauthorized = isProtected;
						if (LogStream.level >= 4) {
							log.println("HttpSecurityService: No provider state: isProtected=" + isProtected);
						}
					}
				}
			}
			if ((provider == null) && ((isAnonymous = isAnonymous(req)))) {
				if (LogStream.level >= 4)
					log.println("HttpSecurityService: isAnonymous=true, installing anonymous provider state");
				try {
					provider = newSecurityProvider();
					provider.put("anonymous", "1");
					storeProviderState(ssn, provider, getConnectionId(req));
					isUnauthorized = false;
					isChallengeRequired = false;
				} catch (SecurityProviderException spe) {
					if (LogStream.level >= 1) {
						spe.printStackTrace(log);
					}
					if (provider != null) {
						provider.dispose();
						provider = null;
					}
				}
			}
			if ((this.capabilitiesAcceptSpnego) || (this.capabilitiesAcceptNtlmssp)) {
				token = getToken(req);
				if (!ldapAuth && token != null) {
					connectionId = getConnectionId(req);
					if (LogStream.level >= 4) {
						log.println("HttpSecurityService: " + connectionId + ": token.length=" + token.length);
					}
					ctx = getAuthContext(connectionId);
					try {
						token = ctx.provider.acceptSecContext(token, 0, token.length);
						isChallengeRequired = token != null;
						if (!ctx.provider.isComplete()) {
							if (LogStream.level >= 4) {
								log.println("HttpSecurityService: " + connectionId
										+ ": provider.isComplete=false, isUnauthorized=true");
							}
							isUnauthorized = true;
						} else {
							if (LogStream.level >= 2) {
								log.println("HttpSecurityService: " + connectionId + ": " + ctx.provider.getIdentity()
										+ " successfully authenticated");
							}
							removeAuthContext(connectionId);
							if (provider == null) {
								isAllowedAccess(ctx.provider, req, rsp);
								storeProviderState(ssn, ctx.provider, connectionId);
								provider = ctx.provider;
								isUnauthorized = false;
							} else {
								if (LogStream.level >= 4) {
									log.println("HttpSecurityService: " + connectionId
											+ ": Disposing auth context for existing provider: " + provider.getName());
								}
								ctx.provider.dispose();
							}
						}
					} catch (SecurityProviderException spe) {
						spe.printStackTrace();
						if (ctx != null && ctx.provider != null) {
							System.out.println((String) ctx.provider.get("ntlmssp.account.name"));
						}
						if (spe != null && "Not a Type 1 message.".equalsIgnoreCase(spe.getMessage())) {
							token = null;
							removeAuthContext(connectionId);
						} else {
							if (LogStream.level >= 2) {
								log.println("HttpSecurityService: " + connectionId + ": Authentication failed: "
										+ spe.getMessage());
								if (LogStream.level >= 4) {
									spe.printStackTrace(log);
								}
							}
							String loginAttempts = (String) ssn.getAttribute("LoginAttempts");
							if (ValidationUtil.isValidId(loginAttempts)) {
								/*
								 * if(Integer.parseInt(loginAttempts) >=3){ ssn.setAttribute("loginStatus",
								 * "User has been locked. More than 3 Invalid attempts" ); }
								 */
								if (Integer.parseInt(loginAttempts) > 2) {
									token = null;
									removeAuthContext(connectionId);
									ssn.removeAttribute("LoginAttempts");
//									forwardToJsp(req, (HttpServletResponse) response, USER_LOGIN_PAGE);
									return;
								}
								ssn.setAttribute("LoginAttempts", String.valueOf(Integer.parseInt(loginAttempts) + 1));
							} else {
								ssn.setAttribute("LoginAttempts", "1");
							}
							if (spe.getCode() == SecurityProviderException.STATUS_INVALID_CREDENTIALS) {
								updateUnsuccessfulLoginAttempts((String) ctx.provider.get("ntlmssp.account.name"));
							}
						}
					}
				} else if (ldapAuth) {
					token = getTokenForLdap(req);
					if (token != null) {
						/* Need to change for connection ID */
//						connectionId = getConnectionId(req);
						connectionId = SessionContextHolder.getConnectionIdFromToken(token);
						if(connectionId==null) {
							ssn.setAttribute("loginStatusWithSPE", "Session Expired");
							isUnauthorized = true;
						} else {
							SessionContextHolder.addOrUpdate(connectionId, ssn);
							VisionUsersVb userDetails =  SessionContextHolder.getUserDetails(connectionId);
							if(userDetails!=null) {
								SessionContextHolder.setContext(userDetails);
							} else {
								ssn.setAttribute("loginStatusWithSPE", "Session Expired");
								isUnauthorized = true;
							}
						}
						if (!rpath.contains("authenticate") && !SessionContextHolder.isSessionExpired(connectionId)) {
							if (SessionContextHolder.isTokenValidWthConnectionId(token, connectionId)) {
								isUnauthorized = false;
							} else {
								ssn.setAttribute("loginStatusWithSPE", "Session Expired");
								isUnauthorized = true;
							}
						} else {
							ssn.setAttribute("loginStatusWithSPE", "Session Expired");
							isUnauthorized = true;
						}
						if (rpath.contains("authenticate") && !isUnauthorized) {
							if (!SessionContextHolder.isSessionExpired(connectionId)) {
								if (!SessionContextHolder.isTokenValidWthConnectionId(token, connectionId)
										&& !isUnauthorized) {
								} else {
									ssn.setAttribute("loginStatusWithSPE", "Session Expired");
									isUnauthorized = true;
								}
							} else {
								ssn.setAttribute("loginStatusWithSPE", "Session Expired");
								isUnauthorized = true;
							}
						} else if(rpath.contains("authenticate")){
							isUnauthorized = true;
						}
					} else if ((rpath.contains("authenticate") || rpath.contains("ForgotPassword") || rpath.contains("GeneratePassword")
							|| rpath.contains("changePasswordFirstTime")
							|| rpath.contains("regeneatedotp")
							|| rpath.contains("validategeneatedotp")
							) && !isUnauthorized) {
						String tempTokenForCid = req.getHeader("temporary-token");
						if(ValidationUtil.isValid(tempTokenForCid)) {
							connectionId = SessionContextHolder.getConnectionIdFromTempToken(tempTokenForCid);
							if(ValidationUtil.isValid(connectionId) && !SessionContextHolder.isSessionExpired(connectionId)) {
								req.setAttribute("tempTokenStorage", EncryptionServlet.genrateKeyWithoutSessionStorage());
								SessionContextHolder.addOrUpdate(connectionId, ssn);
								int result = SessionContextHolder.addToken(String.valueOf(req.getAttribute("tempTokenStorage")), connectionId);
								if (result != Constants.SUCCESSFUL_OPERATION) {
									isUnauthorized = true;
									ssn.setAttribute("loginStatusWithSPE", "Problem in creating Token Holder");
								}
								if (!SessionContextHolder.isSessionExpired(connectionId)) {
									if (SessionContextHolder.isTokenValidWthConnectionId(
											String.valueOf(req.getAttribute("tempTokenStorage")), connectionId)
											&& !isUnauthorized) {
									} else {
										ssn.setAttribute("loginStatusWithSPE", "Session Expired");
										isUnauthorized = true;
									}
								} else {
									ssn.setAttribute("loginStatusWithSPE", "Session Expired");
									isUnauthorized = true;
								}
							} else {
								ssn.setAttribute("loginStatusWithSPE", "Session Expired");
								isUnauthorized = true;
							}
						} else {
							ssn.setAttribute("loginStatusWithSPE", "Invalid request");
							isUnauthorized = true;
						}
					} else {
						isUnauthorized = true;
					}
				}
			} else {
				if (LogStream.level >= 4) {
					log.println("HttpSecurityService: SecurityProvider does not support token authentication");
				}
				isChallengeRequired = false;
			}

		} catch (SecurityProviderException spe1) {
			spe1.printStackTrace();
			if (ctx != null && ctx.provider != null) {
				System.out.println((String) ctx.provider.get("ntlmssp.account.name"));
			}
			if (LogStream.level >= 4)
				spe1.printStackTrace(log);

			isUnauthorized = true;
			isChallengeRequired = false;
			if (provider != null) {
				try {
					provider.dispose();
				} catch (SecurityProviderException spe2) {
				}
			}
		}
		if ((token == null) && (!isForbidden)) {
			isForbidden = rsp.getStatus() == 403;
		}
		if (isForbidden) {
			isChallengeRequired = false;
		}
		try {
			connectionId = getConnectionId(req);
		} catch (SecurityProviderException spe) {
		}
		if (LogStream.level >= 4) {
			int size = 0;
			synchronized (this.authContexts) {
				size = this.authContexts.size();
			}
			connectionId = null;
			log.println("HttpSecurityService: isProtected=" + isProtected + ", token=" + (token != null)
					+ ", credential=" + credential + ", provider=" + provider + ", isLogout=" + isLogout
					+ ", isAnonymous=" + isAnonymous + ", isChallengeRequired=" + isChallengeRequired
					+ ", isUnauthorized=" + isUnauthorized + ", isForbidden=" + isForbidden + ", connectionId="
					+ connectionId + ", authContexts.size=" + size);
		}
		String rhdr = null;

		if (isChallengeRequired) {
			if (this.capabilitiesAcceptNtlmssp)
				rhdr = "NTLM";
			else if (this.capabilitiesAcceptSpnego)
				rhdr = "Negotiate";
			else {
				throw new ServletException(
						"Invalid state: isChallengeRequired = true but capabilitiesAcceptXxx = false");
			}
			if (token != null)
				rhdr = rhdr + " " + Base64.encode(token);
			rsp.setHeader("WWW-Authenticate", rhdr);
		}

		if (isUnauthorized) {
			String sm = isForbidden ? "403 Forbidden" : "401 Unauthorized";
			if (this.fallbackLocation != null && !"UserLogin.jsp".equalsIgnoreCase(fallbackLocation)) {
				rsp.setStatus(isForbidden ? 403 : 401);
			}
			if (this.fallbackLocation != null) {
				
				if(isUnauthorizedOpt) {
					throw new RuntimeCustomException(String.valueOf(ssn.getAttribute("loginStatus")));
				}
				
				
				/* Prompt error message - 12-24-2015 */
				String requestUserId = request.getParameter("username");
				System.out.println("rpath - "+rpath);
				if(!ValidationUtil.isValid(requestUserId)) {
					throw new RuntimeCustomException("Unsupported Request");
				}
				if (requestUserId.contains("\\")) {
					requestUserId = requestUserId.split("\\\\")[1];
				}
//				AuthenticationBean authenticationBean = (AuthenticationBean) SpringBeanFinder.lookUp("authenticationBean");
				AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
				String userloginAttempts = authenticationBean.getUnsuccessfulLoginAttempts(requestUserId);
				String MaxLoginAttempts = authenticationBean.getMaxLoginAttempts();
				String userStatus = authenticationBean.getUserStatus(requestUserId);
				System.out.println("User ID:" + requestUserId + "-----" + "User Status:" + userStatus + "-----"
						+ "MaxLoginAttempts:" + MaxLoginAttempts + "----" + "UserloginAttempts:" + userloginAttempts);
				if (!ValidationUtil.isValid(userloginAttempts)) {
					userloginAttempts = "0";
				}
				if (!ValidationUtil.isValid(MaxLoginAttempts)) {
					MaxLoginAttempts = "3";
				}
				String loginComment;
				if (ValidationUtil.isValid(req.getAttribute("UserExpiredStatus"))) {
					ssn.setAttribute("loginStatus", req.getAttribute("UserExpiredStatus"));
				}else if (ValidationUtil.isValid(req.getAttribute("firstTimeUserstatus"))) {
					ssn.setAttribute("loginStatus", req.getAttribute("firstTimeUserstatus"));
				}else if (ValidationUtil.isValid(ssn.getAttribute("loginStatusWithSPE")) && !ldapAuth) {
					if (String.valueOf(ssn.getAttribute("loginStatusWithSPE"))
							.contains("The supplied credentials are invalid")) {
						if (userStatus == null)
							ssn.setAttribute("loginStatus", "Invalid User Login ID. Contact System Admin");
						else if (ValidationUtil.isValid(userStatus) && !"0".equalsIgnoreCase(userStatus))
							ssn.setAttribute("loginStatus",
									"The User is InActive/Deleted. Please Contact system Admin");
						else if ("0".equalsIgnoreCase(userStatus))
							ssn.setAttribute("loginStatus",
									"Invalid password with same User Login ID [" + requestUserId + "]");
					} else {
						ssn.setAttribute("loginStatus", ssn.getAttribute("loginStatusWithSPE"));
					}
				} else if (ValidationUtil.isValid(ssn.getAttribute("loginStatusWithSPE")) && ldapAuth) {
					if (String.valueOf(ssn.getAttribute("loginStatusWithSPE")).contains("error code 49")) {
						if (userStatus == null){
							loginComment = "Invalid User Login ID. Contact System Admin";
							ssn.setAttribute("loginStatus", ""+loginComment);
							authenticationBean.writeUserLoginAudit(req,requestUserId,"FUSER",loginComment,0);
						}
						else if (ValidationUtil.isValid(userStatus) && !"0".equalsIgnoreCase(userStatus)){
							loginComment = "The User is InActive/Deleted. Please Contact system Admin";
							ssn.setAttribute("loginStatus",""+loginComment);
							authenticationBean.writeUserLoginAudit(req,requestUserId,"FADIN",loginComment,0);
						}
						else if ("0".equalsIgnoreCase(userStatus)){
							loginComment = "Invalid password";
							ssn.setAttribute("loginStatus","Invalid password with same User Login ID [" + requestUserId + "]");
							authenticationBean.writeUserLoginAudit(req, requestUserId,"FPASS",loginComment,0);
						}
					} else {
						ssn.setAttribute("loginStatus", ssn.getAttribute("loginStatusWithSPE"));
						authenticationBean.writeUserLoginAudit(req,requestUserId,"FOTHR","Others",0);
					}
				} else if (Integer.parseInt(userloginAttempts) >= Integer.parseInt(MaxLoginAttempts)) {
					ssn.setAttribute("loginStatus", "User account has been locked. Please contact system admin");
					loginComment = "User account has been locked. Please contact system admin";
					authenticationBean.writeUserLoginAudit(req, requestUserId,"FPASS",loginComment,0);
				} else if (ValidationUtil.isValid(userStatus) && !"0".equalsIgnoreCase(userStatus)) {
					ssn.setAttribute("loginStatus", "The User is InActive/Deleted. Please Contact system Admin");
					loginComment = "The User is InActive/Deleted. Please Contact system Admin";
					authenticationBean.writeUserLoginAudit(req, requestUserId,"FPASS",loginComment,0);
				} else if (ValidationUtil.isValid(requestUserId)) {
					ssn.setAttribute("loginStatus", "Invalid User Name or Password");
					loginComment = "Invalid User Name or Password";
					authenticationBean.writeUserLoginAudit(req, requestUserId,"FPASS",loginComment,0);
				} else if (!ValidationUtil.isValid(requestUserId)) {
					ssn.setAttribute("loginStatus", "");
				}
				if(ValidationUtil.isValid(req.getAttribute("loggedInUserID"))) {
					ExceptionCode exceptionCode = new ExceptionCode();
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg(String.valueOf(ssn.getAttribute("loginStatus")));
					exceptionCode.setResponse(req.getAttribute("loggedInUserID"));
					throw new RuntimeCustomException(exceptionCode);
				}else {
					throw new RuntimeCustomException(String.valueOf(ssn.getAttribute("loginStatus")));
				}
				/*
				 * rsp.setContentType("text/html"); ServletOutputStream os =
				 * rsp.getOutputStream(); os.
				 * println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
				 * os.println("<html><head>"); os.println("<script type='text/javascript'>");
				 * os.println("<!--"); os.println("window.location='" + this.fallbackLocation +
				 * "';"); os.println("//-->"); os.println("</script>"); os.println("</head><h3>"
				 * + sm + "</h3>"); os.println("<p/>Please visit this link: <a href='" +
				 * this.fallbackLocation + "'>" + this.fallbackLocation + "</a>");
				 * os.println("</html>"); os.close(); rsp.flushBuffer();
				 */
			}

			if (LogStream.level >= 4) {
				log.println("HttpSecurityService: S: " + sm);
				if (rhdr != null)
					log.println("HttpSecurityService:   WWW-Authenticate: " + rhdr);
			}
		} else {
			if (isChallengeRequired) {
				throw new ServletException("Invalid state: isUnauthorized = false but isChallengeRequired = true");
			}

			if (provider != null) {
				req = new HttpSecurityServletRequest(req, this, provider);
			}
			if (LogStream.level >= 4) {
				log.println("HttpSecurityService: calling chain.doFilter");
			}
			if (isProtected) {
				SessionContextHolder.addOrUpdate(connectionId, ssn);
				ssn.setAttribute("sessionToken", SessionContextHolder.encodedSessionId(connectionId));
				SessionContextHolder.setContext(SessionContextHolder.getUserDetails(connectionId));
				if (rpath != null && (rpath.contains("authenticate") 
						|| rpath.contains("ForgotPassword")) 
						|| rpath.contains("GeneratePassword")
						|| rpath.contains("regeneatedotp")
						|| rpath.contains("validategeneatedotp")) {
					chain.doFilter(req, rsp);
					return;
				}
			}
			chain.doFilter(req, rsp);
		}
	}

	private void init_log(Map props) throws SecurityProviderException {
		String logPath = getProperty(props, "jespa.log.path", null);
		if (logPath != null) {
			try {
				if (this.logStream != null) {
					try {
						this.logStream.close();
					} catch (IOException ioe) {
						this.logStream = null;
					}
				}
				this.logStream = new FileOutputStream(logPath, true);
				LogStream.setInstance(new TimestampedLogStream(this.logStream));
			} catch (FileNotFoundException fnfe) {
				//throw new SecurityProviderException(0, fnfe.getMessage(), fnfe);
			}
		}
		log = LogStream.getInstance();
		String logLevel = getProperty(props, "jespa.log.level", null);
		if (logLevel != null)
			LogStream.setLevel(Integer.parseInt(logLevel));
	}

	private void init(boolean isUpdate) throws SecurityProviderException {
		Map props = new HashMap();
		for (Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext();) {
			PropertySource propertySource = (PropertySource) it.next();
			if (propertySource instanceof MapPropertySource) {
				props.putAll(((MapPropertySource) propertySource).getSource());
			}
		}
		if (isUpdate) {
			init_log(props);
			this.providerProperties = new jespa.security.Properties();
			Iterator iter = props.keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = String.valueOf(props.get(name)).trim();
				if (name.startsWith("jcifs."))
					Config.setProperty(name, value);
				else if (name.startsWith("jespa.")) {
					if (name.endsWith(".service.password"))
						this.providerProperties.setEncryptedProperty(name.substring(6), value, null);
					else
						this.providerProperties.setProperty(name.substring(6), value);
				}
			}
			if (LogStream.level >= 4) {
				log.println("HttpSecurityService: " + this.providerProperties);
			}
			String classname = getProperty(props, "provider.classname", "jespa.ntlm.NtlmSecurityProvider");
			try {
				Class klass = Class.forName(classname);
				this.providerConstructor = klass.getConstructor(new Class[] { Map.class });
			} catch (Exception ex) {
				throw new SecurityProviderException(0,
						"Failed to acquire Constructor for provide.classname: " + classname, ex);
			}
			SecurityProvider tsp = newSecurityProvider();
			try {
				this.capabilitiesAcceptNtlmssp = tsp.getFlag("capabilities.accept.ntlmssp");
				this.capabilitiesAcceptSpnego = tsp.getFlag("capabilities.accept.spnego");
			} finally {
				tsp.dispose();
			}
			this.usernameParameter = getProperty(props, "http.parameter.username.name", null);
			this.passwordParameter = getProperty(props, "http.parameter.password.name", null);
			this.otpParameter = getProperty(props, "http.parameter.otpParameter.name", null);
			this.logoutParameter = getProperty(props, "http.parameter.logout.name", null);
			this.anonymousParameter = getProperty(props, "http.parameter.anonymous.name", null);
			this.fallbackLocation = getProperty(props, "fallback.location", null);
			String disable = getProperty(props, "http.parameter.password.disableWarning", null);
			this.disableWarning = ((disable != null) && (disable.equals("true")));
			this.skipAuth = Boolean.valueOf(getProperty(props, "skipAuth", "false"));
			this.appName = getProperty(props, "appName", "/Vision");
			this.ldapAuth = Boolean.valueOf(getProperty(props, "ldapAuth", "false"));
			this.adServers = getProperty(props, "adServers", "UnknownServer");
			this.twoFactorAuthFlag = Boolean.valueOf(getProperty(props, "twoFactorAuthFlag", "false"));
			try {
				this.excludes = getPropertyAsArray(props, "excludes");
				this.groupsDenied = getPropertyAsArray(props, "groups.denied");
				this.groupsAllowed = getPropertyAsArray(props, "groups.allowed");
			} catch (IOException ioe) {
				throw new SecurityProviderException(0, ioe.getMessage(), ioe);
			}
			if (LogStream.level >= 1)
				log.println("HttpSecurityService: " + toString());
		}
		sessionUsers = new HashMap<String, String>(0);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		String excludePattern = env.getProperty("exclude.url");
		excludedUrls = Arrays.asList(excludePattern.split(","));
		try {
			init(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}

	private String[] getPropertyAsArray(Map props, String name) throws IOException {
		String str = getProperty(props, name, null);
		if (str != null)
			return Csv.parseRow(str, ',', 5);
		return null;
	}

	private String getProperty(Map props, String name, String def) {
		if (props.containsKey(name)) {
			String ret = String.valueOf(props.get(name)).trim();
			if (ret.length() > 0)
				return ret;
		}
		return def;
	}

	private String arrayToString(String[] arr) {
		if ((arr != null) && (arr.length > 0)) {
			String ret = "";
			for (int ai = 0; ai < arr.length; ai++)
				ret = ret + ", " + arr[ai];
			return ret.substring(2);
		}
		return "";
	}

	/*protected String getConnectionId(HttpServletRequest req) throws SecurityProviderException {
		String cid = req.getHeader("Jespa-Connection-Id");
		if (cid != null) {
			return cid;
		}
		return req.getSession(true).getId();
	}*/

	protected String getConnectionId(HttpServletRequest req) throws SecurityProviderException {
		byte[] tokenForCid = getTokenForLdap(req);
		if (tokenForCid != null) {
			return SessionContextHolder.getConnectionIdFromToken(tokenForCid);
		} else {
			return req.getSession(true).getId();
		}
	}
	
	protected String getConnectionIdFromTemporaryToken(HttpServletRequest req) throws SecurityProviderException {
		String tempTokenForCid = req.getHeader("temporary-token");
		if (ValidationUtil.isValid(tempTokenForCid)) {
			return SessionContextHolder.getConnectionIdFromTempToken(tempTokenForCid);
		}
		return null;
	}
	
	String _getConnectionId(HttpServletRequest req) throws SecurityProviderException {
		return getConnectionId(req);
	}

	void storeProviderState(HttpSession ssn, SecurityProvider provider, String id) throws SecurityProviderException {
		if (LogStream.level >= 4) {
			log.println("HttpSecurityService: " + id + ": Installing SecurityProvider state");
		}
		int interval = ssn.getMaxInactiveInterval();
		if (interval <= 30000) {
			log.println("HttpSecurityService: MaxInactiveInterval is short (" + interval / 60
					+ " minutes), session-timeout = 600 is recommended");
		}
		byte[] providerState = (byte[]) (byte[]) provider.exportState();
		ssn.setAttribute("jespa.provider.state", providerState);
		if (LogStream.level >= 4)
			log.println("HttpSecurityService: " + id + ": SecurityProvider state installed");
	}

	protected String getRequestPath(HttpServletRequest request) throws ServletException {
		try {
			return new URI(request.getRequestURI()).normalize().getPath();
		} catch (URISyntaxException se) {
			if (LogStream.level >= 3)
				se.printStackTrace(log);
			throw new ServletException("Failed to compose request path", se);
		}
	}

	protected boolean isProtected(HttpServletRequest request) throws ServletException {
		String rpath = null;
		boolean result = true;
		System.out.println("request:"+request.getMethod());
		if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			return false;
		}
		if (excludedUrls.size() > 0) {
			if (request.getRequestURI() != null
					&& request.getRequestURI().contains(request.getContextPath() + "/files")) {
				result = false;
			} else {
				rpath = getRequestPath(request);
				String cpath = request.getContextPath();
				if (!rpath.startsWith(cpath)) {
					if (LogStream.level >= 3)
						log.println("HttpSecurityService: Request path does not start with context path: " + cpath);
				} else {
//					if ("/".equalsIgnoreCase(rpath) || "/generateKeypair".equalsIgnoreCase(rpath) || rpath.contains("/generateKeypair")) {
					if ("/".equalsIgnoreCase(rpath) || rpath.contains("/generateKeypair") 
							|| rpath.contains("swagger") || rpath.contains("api-docs")) {
						return false;
					}
					if (rpath.startsWith("/")) {
						rpath = rpath.substring(1, rpath.length());
					}
					if (excludedUrls.contains(rpath))
						return false;
				}
			}

		}
		log.println("rpath: " + rpath + " isProtected: " + result);
		return result;
	}

	private static boolean wildcmp(String exp, String str) {
		int slen = str.length();
		int elen = exp.length();
		int ep;
		int sp;
		int ei;
		int si = ei = sp = ep = 0;

		while ((si < slen) && ((ei == elen) || (exp.charAt(ei) != '*'))) {
			if ((ei == elen) || ((exp.charAt(ei) != str.charAt(si)) && (exp.charAt(ei) != '?')))
				return false;
			si++;
			ei++;
		}
		while (si < slen) {
			if ((ei < elen) && (exp.charAt(ei) == '*')) {
				ei++;
				if (ei == elen)
					return true;
				ep = ei;
				sp = si + 1;
				continue;
			}
			if ((ei < elen) && ((exp.charAt(ei) == str.charAt(si)) || (exp.charAt(ei) == '?'))) {
				ei++;
				si++;
				continue;
			}
			ei = ep;
			si = sp++;
		}
		while ((ei < elen) && (exp.charAt(ei) == '*')) {
			ei++;
		}
		return ei == elen;
	}

	/*
	 * protected void forwardToJsp(HttpServletRequest httpservletrequest,
	 * HttpServletResponse httpservletresponse, String s) { try { ServletContext sc
	 * = servletContext.getContext(appName); RequestDispatcher rqd =
	 * sc.getRequestDispatcher("/" + s); rqd.forward(httpservletrequest,
	 * httpservletresponse); } catch (Exception e) {
	 * log.println("Exception in forwardToJsp. Jsp Name: " + s);
	 * e.printStackTrace(log); } }
	 */

	protected boolean isLogout(HttpServletRequest request) throws ServletException {
		return (this.logoutParameter != null) && (request.getParameter(this.logoutParameter) != null);
	}

	private void login(HttpServletRequest request, HttpServletResponse response, String userId)
			throws SecurityProviderException {
//		AuthenticationBean authenticationBean = (AuthenticationBean) SpringBeanFinder.lookUp("authenticationBean");
		AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
		if (!authenticationBean.processLogin(request, response)) {
			if (LogStream.level >= 4) {
				log.println(("Authentication denied for user:" + userId));
			}
			throw new SecurityProviderException(SecurityProviderException.STATUS_ACCESS_DENIED,
					"Authentication denied for user:" + userId);
		}
	}

	private void updateUnsuccessfulLoginAttempts(String userId) {
//		AuthenticationBean authenticationBean = (AuthenticationBean) SpringBeanFinder.lookUp("authenticationBean");
		AuthenticationBean authenticationBean = getVisionRegTech().appContext.getBean(AuthenticationBean.class);
		//Commented the below line due to this application is not a parent application and using Vision user table - Deepak
	
		//authenticationBean.updateUnsuccessfulLoginAttempts(userId);
	}

	String getRequestHeaders(HttpServletRequest req) {
		String str = "";
		Enumeration e1 = req.getHeaderNames();
		while (e1.hasMoreElements()) {
			String name = (String) e1.nextElement();
			Enumeration e2 = req.getHeaders(name);
			while (e2.hasMoreElements()) {
				String value = (String) e2.nextElement();
				str = str + " | " + name + "=" + value;
			}
		}
		if (str.length() > 0) {
			str = str.substring(3);
		}
		return str;
	}

	void logSessionAttributes(HttpServletRequest req) {
		HttpSession ssn = req.getSession(true);
		String str = "";
		if (ssn != null) {
			Enumeration e = ssn.getAttributeNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				str = str + " | " + name + ": ";
				Object value = ssn.getAttribute(name);
				if ((value instanceof byte[]))
					str = str + "byte[" + ((byte[]) (byte[]) value).length + "]";
				else {
					str = str + "" + value;
				}
			}
		}
		if (str.length() > 0) {
			str = str.substring(2);
		}
		log.println("HttpSecurityService: Session Attributes:" + str);
	}

	AuthContext getAuthContext(String connectionId) throws SecurityProviderException {
		AuthContext ctx;
		synchronized (this.authContexts) {
			ctx = (AuthContext) this.authContexts.get(connectionId);
			if (ctx == null) {
				ctx = new AuthContext(connectionId);
				this.authContexts.put(connectionId, ctx);
			}
		}
		return ctx;
	}

	AuthContext removeAuthContext(String connectionId) throws SecurityProviderException {
		synchronized (this.authContexts) {
			AuthContext value = this.authContexts.get(connectionId);
			if (value != null) {
				this.authContexts.dispose(this.authContexts.remove(connectionId));
			}
			return value;
		}
	}

	protected boolean isAnonymous(HttpServletRequest request) throws ServletException {
		return (this.anonymousParameter != null) && (request.getParameter(this.anonymousParameter) != null);
	}

	byte[] getToken(HttpServletRequest req) {

		String hdr = req.getHeader("Authorization");
		if (hdr != null) {
			if (LogStream.level >= 4)
				log.println("HttpSecurityService:   Authorization: " + hdr);

			if (hdr.startsWith("NTLM ")) {
				if (this.capabilitiesAcceptNtlmssp)
					return Base64.decode(hdr.substring(5));
			} else if (hdr.startsWith("Negotiate ")) {
				if (this.capabilitiesAcceptSpnego)
					return Base64.decode(hdr.substring(10));
			} else {
				if (LogStream.level >= 4)
					log.println("HttpSecurityService: Authentication mechanism not supported by HttpSecurityService");

				return null;
			}
			if (LogStream.level >= 3)
				log.println(
						"HttpSecurityService: SecurityProvider does not accept token type supplied by client, ignoring token");
		}
		return null;
	}

	byte[] getTokenForLdap(HttpServletRequest req) {
		String hdr = req.getHeader("VisionAuthenticate");
		if (hdr != null) {
			if (LogStream.level >= 4)
				log.println("HttpSecurityService:   Authorization: " + hdr);
			Base64.encode((hdr.substring(6)).getBytes());
			if (hdr.startsWith("VISION")) {
				if (this.capabilitiesAcceptNtlmssp)
					return Base64.decode(hdr.substring(6));
					/*return Base64.decode(hdr.substring(6));*/
			} else {
				if (LogStream.level >= 4)
					log.println("HttpSecurityService: Authentication mechanism not supported by HttpSecurityService");
				return null;
			}
			if (LogStream.level >= 3)
				log.println(
						"HttpSecurityService: SecurityProvider does not accept token type supplied by client, ignoring token");
		}
		return null;
	}

	protected void isAllowedAccess(SecurityProvider provider, HttpServletRequest req, HttpServletResponse rsp)
			throws SecurityProviderException {
		HttpSession ssn = req.getSession(false);
		if (provider != null) {
			Account acct = provider.getAccount(null, null);
			if (acct == null) {
				if ((this.groupsAllowed != null) || (this.groupsDenied != null)) {
					if (LogStream.level >= 4) {
						log.println(
								"HttpSecurityService: SecurityProvider does not have a default Account - group based access control will be ignored");
					}
				}
				return;
			}
			if (this.groupsDenied != null) {
				for (int gi = 0; gi < this.groupsDenied.length; gi++) {
					if (acct.isMemberOf(this.groupsDenied[gi])) {
						throw new SecurityProviderException(7,
								provider.getIdentity() + " denied access by groups.denied: " + this.groupsDenied[gi]);
					}
				}
			}
			if (acct != null && this.groupsAllowed != null) {
				for (int gi = 0; gi < this.groupsAllowed.length; gi++) {
					if (acct.isMemberOf(this.groupsAllowed[gi])) {
						return;
					}
				}
			}
			String connectionId = getConnectionId(req);
			if (ssn.getAttribute("userDetails") == null || SessionContextHolder.isSessionExpired(connectionId)) {
				if (LogStream.level > 2) {
					log.println("HttpSecurityFilter: " + acct.getProperty("sAMAccountName")
							+ " successfully authenticated against " + acct.getProperty("sAMAccountName"));
				}
				ssn.setAttribute("ntLoginDetails", acct.getProperty("sAMAccountName"));
				if (!SessionContextHolder.isSessionExpired(connectionId)) {
					SessionContextHolder.expireSession(connectionId);
					ssn = req.getSession(true);
					ssn.setAttribute("ntLoginDetails", acct.getProperty("sAMAccountName"));
				}
				login(req, rsp, (String) acct.getProperty("sAMAccountName"));
			} else {
				SessionContextHolder.addOrUpdate(connectionId, ssn);
				ssn.setAttribute("sessionToken", SessionContextHolder.encodedSessionId(connectionId));
				SessionContextHolder.setContext(SessionContextHolder.getUserDetails(connectionId));
			}
		}
		return;
	}
	
	/**
	 * * Decrypt *
	 * 
	 * @param key
	 *            Decryption key *
	 * @param raw
	 *            Encrypted data *
	 * @Return decrypted plaintext *
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public static byte[] decrypt(PrivateKey pk, byte[] raw) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
			cipher.init(cipher.DECRYPT_MODE, pk);
			int blockSize = cipher.getBlockSize();
			ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
			int j = 0;
			while (raw.length - j * blockSize > 0) {
				bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
				j++;
			}
			return bout.toByteArray();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static String decryptStr(String paramStr, KeyPair keyPair) {
		try {
//			byte[] en_result = Base64.decode(paramStr);
			byte[] en_result = paramStr.getBytes();
			byte[] de_result = decrypt(keyPair.getPrivate(), en_result);
			StringBuffer sb = new StringBuffer();
			sb.append(new String(de_result));
			// Returns the decrypted string
			return sb.reverse().toString();			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String decrypt(String encrypted, KeyPair keys) {
		Cipher dec;
		try {
			dec = Cipher.getInstance("RSA/NONE/NoPadding","BC");
			dec.init(Cipher.DECRYPT_MODE, keys.getPrivate());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("RSA algorithm not supported", e);
		}
		String[] blocks = encrypted.split("\\s");
		StringBuffer result = new StringBuffer();
		try {
			for (int i = blocks.length - 1; i >= 0; i--) {
				byte[] data = hexStringToByteArray(blocks[i]);
				byte[] decryptedBlock = dec.doFinal(data);
				result.append(new String(decryptedBlock));
			}
		} catch (GeneralSecurityException e) {
			throw new RuntimeException("Decrypt error", e);
		}
		/**
		 * Some code is getting added in first 2 digits with Jcryption need to investigate
		 */
		return result.reverse().toString().substring(2);
	}
	public static byte[] hexStringToByteArray(String data) {
		int k = 0;
		byte[] results = new byte[data.length() / 2];
		for (int i = 0; i < data.length();) {
			results[k] = (byte) (Character.digit(data.charAt(i++), 16) << 4);
			results[k] += (byte) (Character.digit(data.charAt(i++), 16));
			k++;
		}
		return results;
	}
	protected Object getRequestCredential1Old(HttpServletRequest request) throws SecurityProviderException {
		if ((this.usernameParameter != null) && (this.passwordParameter != null)) {
			String acctname = request.getParameter(this.usernameParameter);
			String tempTokenForCid = request.getHeader("temporary-token");
			ConnectionHolder connectionHolder = SessionContextHolder.getconnectionClassFromTempToken(tempTokenForCid);
			KeyPair keyPair = null;
			if(ValidationUtil.isValid(tempTokenForCid)) {
				keyPair = SessionContextHolder.getKeyPairFromTempToken(tempTokenForCid);
			}
			if (acctname != null && keyPair != null) {
				acctname = acctname.trim();
				if (acctname.length() > 0) {
 					String password = request.getParameter(this.passwordParameter);
 					try {
 						String rsaPassword = password;
 	 					
 	 					byte[] encryptedData = rsaPassword.getBytes();
 						
 	 					decryptData(encryptedData, connectionHolder.getRsaPrivateKeySpec(),connectionHolder.getEncryptedData()); 
 					} catch(Exception e) {
 						e.printStackTrace();
 					}
 						
					/* Temporary Code */
					password = new String(Base64.decode(password));
					char[] passwordChars = null;
					passwordChars = password.toCharArray();
					
					return new PasswordCredential(acctname, passwordChars);
				}
			}
		}
		return null;
	}
	protected Object getRequestCredential(HttpServletRequest request) throws SecurityProviderException {
		if ((this.usernameParameter != null) && (this.passwordParameter != null)) {
			String acctname = request.getParameter(this.usernameParameter);
			String tempTokenForCid = request.getHeader("temporary-token");
			ConnectionHolder connectionHolder = SessionContextHolder.getconnectionClassFromTempToken(tempTokenForCid);
			KeyPair keyPair = null;
			if(ValidationUtil.isValid(tempTokenForCid)) {
				keyPair = SessionContextHolder.getKeyPairFromTempToken(tempTokenForCid);
			}
			if (acctname != null && keyPair != null) {
				acctname = acctname.trim();
				if (acctname.length() > 0) {
 					String password = request.getParameter(this.passwordParameter);
 					/* RSA Code - Start
 					 * 
 					 * try {
 						String rsaPassword = request.getParameter("rsaPassword");
 	 					
 	 					byte[] encryptedData = rsaPassword.getBytes();
 						
 	 					decryptData(encryptedData, connectionHolder.getRsaPrivateKeySpec(),connectionHolder.getEncryptedData()); 
 					} catch(Exception e) {
 						e.printStackTrace();
 					}
 					
 					* RSA Code - End
 					*/
 						
					/* Temporary Code - Start */
//					password = new String(Base64.decode(password));
					/* Temporary Code - End */
					
 					/* AES Code - Start */
 					try {
 						SecretKeySpec aesSecretKey;
 						aesSecretKey = new SecretKeySpec(tempTokenForCid.substring(0, 16).getBytes("UTF-8"),"AES" );
 						Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
 						cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
 						password = new String(cipher.doFinal(java.util.Base64.getDecoder().decode(password)));
 					} catch(Exception e) {
// 						e.printStackTrace();
 					}
 					/* AES Code - End */
					
					char[] passwordChars = null;
					passwordChars = password.toCharArray();
					
					return new PasswordCredential(acctname, passwordChars);
				}
			}
		}
		return null;
	}
	private void decryptData(byte[] dataFromBrowser, RSAPrivateKeySpec rsaPrivateKeySpec, byte[] encryptedData) {
		System.out.println("-------Decryption Started-------");
		byte[] decryptedData = null;
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] javaDecryptedData = cipher.doFinal(encryptedData);
			System.out.println("Java Decrypted Data : " + new String(javaDecryptedData));
			decryptedData = javaDecryptedData;
			System.out.println("Browser Decrypted Data : " + new String(decryptedData));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-------Decryption Completed-------");
	}
	
	public static String encrypt(String plainTxt) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(plainTxt.getBytes());
			return Base64.encode(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public static String decrypt(String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decode(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	/* AES Algorithm - START */
	
	
	public static String decryptText(String cipherText,String secret){
        String decryptedText=null;
        byte[] cipherData = java.util.Base64.getDecoder().decode(cipherText);
        byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            System.out.println("decryptedText success");
            return decryptedText;
        }
        catch (Exception ex){
        	System.out.println("error on decrypt: "+ex.getMessage());
            return decryptedText;
        }
    }
	
	public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {

        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;

        try {
            md.reset();

            // Repeat process until sufficient data has been generated
            while (generatedLength < keyLength + ivLength) {

                // Digest data (last digest if available, password data, salt if available)
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);

                // additional rounds
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }

                generatedLength += digestLength;
            }

            // Copy key and IV into separate byte arrays
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);

            return result;

        } catch (DigestException e) {

            throw new RuntimeException(e);

        } finally {
            // Clean out temporary data
            Arrays.fill(generatedData, (byte)0);
        }
    }
	
	
	/* AES Algorithm - END */	
	
	
	public void getActiveDirectoryAuthentication(String domainName) {
		this.domainName = domainName.toUpperCase();
		try {
			ldapServerUrls = nsLookup(domainName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Domain not Avaible.Trying to Connect to another domains..." + domainName);
		}
		lastLdapUrlIndex = 0;
	}

	private static String[] nsLookup(String argDomain) throws Exception {
		try {
			Hashtable<Object, Object> env = new Hashtable<Object, Object>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
			env.put("java.naming.provider.url", "dns:");
			DirContext ctx = new InitialDirContext(env);
			Attributes attributes = ctx.getAttributes(String.format("_ldap._tcp.%s", argDomain),
					new String[] { "srv" });
			// try thrice to get the KDC servers before throwing error
			for (int i = 0; i < 3; i++) {
				Attribute a = attributes.get("srv");
				if (a != null) {
					List<String> domainServers = new ArrayList<String>();
					NamingEnumeration<?> enumeration = a.getAll();
					while (enumeration.hasMoreElements()) {
						String srvAttr = (String) enumeration.next();
						// the value are in space separated 0) priority 1)
						// weight 2) port 3) server
						String values[] = srvAttr.toString().split(" ");
						domainServers.add(String.format("ldap://%s:%s", values[3], values[2]));
					}
					String domainServersArray[] = new String[domainServers.size()];
					domainServers.toArray(domainServersArray);
					return domainServersArray;
				}
			}
			throw new Exception("Unable to find srv attribute for the domain " + argDomain);
		} catch (NamingException exp) {
			throw new Exception("Error while performing nslookup. Root Cause: " + exp.getMessage(), exp);
		}
	}

	// LDAP Authentication
	public boolean authenticate(String username, String password)
			throws AccountException, FailedLoginException, NamingException {
		if (ldapServerUrls == null || ldapServerUrls.length == 0) {
			throw new AccountException("Unable to find ldap servers");
		}
		if (username == null || password == null || username.trim().length() == 0 || password.trim().length() == 0) {
			throw new FailedLoginException("Username or password is empty");
		}
		int retryCount = 0;
		int currentLdapUrlIndex = lastLdapUrlIndex;
		do {
			retryCount++;
			try {
				Hashtable<Object, Object> env = new Hashtable<Object, Object>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY_CLASS);
				env.put(Context.PROVIDER_URL, ldapServerUrls[currentLdapUrlIndex]);
				env.put(Context.SECURITY_PRINCIPAL, username + "@" + domainName);
				env.put(Context.SECURITY_CREDENTIALS, password);
				DirContext ctx = new InitialDirContext(env);
				ctx.close();
				lastLdapUrlIndex = currentLdapUrlIndex;
				return true;
			} catch (CommunicationException exp) {
				exp.printStackTrace();
				// if the exception of type communication we can assume the AD
				// is not reachable hence retry can be attempted with next
				// available AD
				if (retryCount < ldapServerUrls.length) {
					currentLdapUrlIndex++;
					if (currentLdapUrlIndex == ldapServerUrls.length) {
						currentLdapUrlIndex = 0;
					}
					continue;
				}
				return false;
			}
		} while (true);
	}

	public VisionRegTech getVisionRegTech() {
		return visionRegTech;
	}

	public void setVisionRegTech(VisionRegTech visionRegTech) {
		this.visionRegTech = visionRegTech;
	}
	

	
	
}