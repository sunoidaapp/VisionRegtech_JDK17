package com.vision.wb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.BuildSchedulesDao;
import com.vision.dao.UploadFilingDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.CreateCsv;
import com.vision.util.DeepCopy;
import com.vision.util.ExcelExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ADFAcqVersionsVb;
import com.vision.vb.BuildSchedulesVb;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.FileInfoVb;
import com.vision.vb.PromptIdsVb;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.TemplateStgVb;
import com.vision.vb.UploadFilingVb;
import com.vision.vb.XbrlVb;

import jakarta.servlet.ServletContext;

@Controller
public class UploadFilingWb extends AbstractDynaWorkerBean<UploadFilingVb> implements ApplicationContextAware, ServletContextAware{
	
	@Autowired
    private UploadFilingDao uploadFilingDao;
    public ApplicationContext applicationContext;
	private int blockSize;
	

	private ServletContext servletContext;
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}
	
	@Value("${ftp.serverType}")
	private String serverType;

	@Value("${ftp.hostName}")
	private String hostName;
	
	@Value("${ftp.prompt}")
	private char prompt;
	
	@Value("${ftp.userName}")
    private String userName;
	
	@Value("${ftp.password}")
    private String password;
	
	@Value("${ftp.securedFtp}")
    private boolean securedFtp;
	
	@Value("${ftp.knownHostsFileName}")
    private String knownHostsFileName;
	
	@Value("${nonAdFtp.serverType}")
    private String nonAdFtp = "WINDOWS";
	
	@Value("${ftp.processDir}")
    private String processDir;
	
	@Value("${ftp.scriptDir}")
    private String scriptsProcessDir = "/home/vision/scripts";
	
    private String uploadDir;
    private String adUploadDir;
    private String downloadDir;
    private String buildLogsDir;
    private String timezoneId;
    private String dateFormate;
    private String scriptDir;
	private int fileType;
    private String foderDrive;
    private int uploadFileChkIntervel = 30;
    private static final String SERVICE_NAME = "Upload Filling";
    private static final int DIR_TYPE_DOWNLOAD = 1;
    private WebApplicationContext webApplicationContext = null;
    
    @Autowired
    private BuildSchedulesDao buildSchedulesDao;
    
//    private VelocityEngine velocityEngine;
    private String appFtpUserName = "";
    private String  appFtpPassword = "";
    private String  appFtpHostName = "";
    private String ACQUISITION_STATUS = "ACQUISITION_STATUS";
    private String ACQUISITION_READINESS_FLAG = "ACQUISITION_READINESS_FLAG";
    private String FILING_STATUS = "FILING_STATUS";
    private String PROCESS_END_TIME = "PROCESS_END_TIME";
    private String UPLOAD_NODE = "N1";
    
    public ExceptionCode doUpload(String fileName, byte[] data, long version, String node) {
    	ExceptionCode exceptionCode  = null;
        try{
        	UPLOAD_NODE = node;
        	setUploadDownloadDirFromDB();
        	/*if("WINDOWS".equalsIgnoreCase(serverType)){
        		return uploadFilesToWindows(fileName,data);
        	}*/
        	if(securedFtp){
        		fileName = fileName.toUpperCase();
        		String fileExtension = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
        		if("XLSX".equalsIgnoreCase(fileExtension))
        			fileName = fileName.substring(0, fileName.lastIndexOf('.'))+".XLSX";
        		else
        			fileName = fileName.substring(0, fileName.lastIndexOf('.'))+".XLS";
        		
        		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
    			if(!ValidationUtil.isValid(environmentParam))
    				environmentParam="UAT";
    			String accHostName =getUploadFilingDao().getServerCredentials( environmentParam, UPLOAD_NODE, "NODE_IP");
    			String userName =getUploadFilingDao().getServerCredentials( environmentParam, UPLOAD_NODE, "NODE_USER");
    			String password =getUploadFilingDao().getServerCredentials( environmentParam, UPLOAD_NODE, "NODE_PWD");
    			if(!ValidationUtil.isValid(accHostName)){
    				accHostName = getHostName();
    			}
    			if(!ValidationUtil.isValid(userName)){
    				userName = getUserName();
    			}
    			if(!ValidationUtil.isValid(password)){
    				password = getPassword();
    			}
    			JSch jsch = new JSch();
    			
//    			Session session = jsch.getSession( getUserName(), getHostName() );
    			Session session = jsch.getSession( userName, accHostName, 22 );
    			{   // "interactive" version   // can selectively update specified known_hosts file    
    				// need to implement UserInfo interface  
    				// MyUserInfo is a swing implementation provided in    
    				//  examples/Sftp.java in the JSch dist   
    				UserInfo ui = new MyUserInfo();   
    				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
    				session.setPassword( password);
    			}
    			System.out.println(userName+" host : "+ accHostName);
    			java.util.Properties config = new java.util.Properties(); 
    			config.put("StrictHostKeyChecking", "no");
    			session.setConfig(config);
//    			session.setConfig("StrictHostKeyChecking", "no");
    			session.connect();
    			Channel channel = session.openChannel( "sftp" ); 
    			channel.connect();
    			channel = session.openChannel("shell");
    			OutputStream inputstream_for_the_channel = channel.getOutputStream();
    			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
    			channel.connect();
    			commander.println("cd "+uploadDir);
    			System.out.println("Upload Dir : "+uploadDir);
    			StringBuilder cmd = new StringBuilder();
    			if(!ValidationUtil.isValid(nonAdFtp))
    				nonAdFtp = "WINDOWS";
    			if("WINDOWS".equalsIgnoreCase(nonAdFtp)) {
    				String uploadDirNew = "";
    				if(uploadDir.indexOf("/") == 0) {
	    				uploadDirNew = uploadDir.replaceAll("//", "\\");
	    				uploadDirNew = uploadDirNew.substring(1, uploadDirNew.length());
    				}else {
    					uploadDirNew = uploadDir;
    				}
    				String uploadAdDirNew = "";
    				if(adUploadDir.indexOf("/") == 0) {
    					uploadAdDirNew = adUploadDir.replaceAll("//", "\\");
    					uploadAdDirNew = uploadAdDirNew.substring(1, uploadAdDirNew.length());
    				}else {
    					uploadAdDirNew = adUploadDir;
    				}
    				if(version == 0)
    					version  = 1;
    				String adFile = fileName.substring(0, fileName.lastIndexOf('.'))+"_"+version+"."+fileExtension;
    				System.out.println("######################################");
    				System.out.println("Upload Dir : "+uploadDirNew+" File : "+fileName);
    				System.out.println("Upload AD Dir : "+uploadAdDirNew+" AD File : "+adFile);
					File srcFile = new File(uploadDirNew+""+fileName);
					File dstDir = new File(uploadAdDirNew+""+adFile);
					if(dstDir.exists()) {
						dstDir.delete();
						System.out.println("File Exist and deleted Successfully!! ");	
					}
//    				FileUtils.copyFileToDirectory(srcFile, dstDir);
					if(srcFile.exists()) {
						FileSystemUtils.copyRecursively(srcFile, dstDir);
						System.out.println("File Moved to AD Dir Successfully!! ");
					}
    				System.out.println("######################################");
    			}else {
	    			if('/' != uploadDir.charAt(uploadDir.length()-1)){
	    				//cmd.append(" mv "+fileName+" "+adUploadDir+"/"+fileName.substring(0, fileName.lastIndexOf('.'))+"_"+version+".XLSX");
	    				cmd.append(" mv "+fileName+" "+adUploadDir+"/"+fileName.substring(0, fileName.lastIndexOf('.'))+"_"+version+"."+fileExtension);
	    			}else{
	    				//cmd.append(" mv "+fileName+" "+adUploadDir+"/"+fileName.substring(0, fileName.lastIndexOf('.'))+"_"+version+".XLSX");
	    				cmd.append(" mv "+fileName+" "+adUploadDir+"/"+fileName.substring(0, fileName.lastIndexOf('.'))+"_"+version+"."+fileExtension);
	    			}
    			}
    			commander.println(cmd);
    			commander.println("exit");
    			commander.close();
    			
    			do {Thread.sleep(500); } while(!channel.isEOF()); 
    			
    			channel = session.openChannel("sftp"); 
    			channel.connect();
    			session.setTimeout(30000);
    			ChannelSftp sftpChannel = (ChannelSftp) channel;
    	        sftpChannel.cd(uploadDir);
    	        InputStream in = new ByteArrayInputStream(data);
    			sftpChannel.put(in,fileName);
    			sftpChannel.exit();
    			
    			channel = session.openChannel("shell");
    			inputstream_for_the_channel = channel.getOutputStream();
    			commander = new PrintStream(inputstream_for_the_channel, true);
    			channel.connect();
    			commander.println("cd "+scriptDir);
    			cmd = new StringBuilder();
    			if('/' != uploadDir.charAt(uploadDir.length()-1)){
    				cmd.append("./remSpecialChar.sh "+uploadDir+"/"+fileName);
    			}else{
    				cmd.append("./remSpecialChar.sh "+uploadDir+fileName);
    			}
    			commander.println(cmd);
    			commander.println("exit");
    			commander.close();
    			do {Thread.sleep(1000); } while(!channel.isEOF()); 
    			session.disconnect();
        	}else{
        		FTPClient ftpClient = getConnection();
            	ftpClient.connect(getHostName());
            	boolean response = ftpClient.login(getUserName(), getPassword());
            	if(!response){
            		ftpClient.disconnect();
                	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
        			exceptionCode.setResponse(fileName);
        			throw new RuntimeCustomException(exceptionCode);
            	}
        		ftpClient.setFileType(fileType);
            	response = ftpClient.changeWorkingDirectory(uploadDir);
            	if(!response){
            		ftpClient.disconnect();
                	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
        			exceptionCode.setResponse(fileName);
        			throw new RuntimeCustomException(exceptionCode);
            	}
    	        fileName = fileName.toUpperCase();
    	        fileName = fileName.substring(0, fileName.lastIndexOf('.'))+"_"+SessionContextHolder.getContext().getVisionId()+".XLSX";
    	        InputStream in = new ByteArrayInputStream(data);
    	        ftpClient.storeFile(fileName, in);
    	        in.close(); //close the io streams
    	        ftpClient.disconnect();
        	}
        }catch(FileNotFoundException e){
        	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }catch(IOException e){
        	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }catch(Exception e){
        	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }
        exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
		exceptionCode.setOtherInfo(fileName);
        return exceptionCode;
    }
    public ExceptionCode uploadFilesToWindows(String fileName, byte[] data) {
    	ExceptionCode exceptionCode  = null;
    	String fileExtension = "";
        try{
			if(fileName.contains(".XLSX") || fileName.contains(".XLS"))
				fileExtension = "XLSX";
    		FTPClient ftpClient = getConnection();
			
			ftpClient.connect(getHostName()); boolean response =
			ftpClient.login(getUserName(), getPassword());
        	if(!response){
        		ftpClient.disconnect();
            	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
    			exceptionCode.setResponse(fileName);
    			throw new RuntimeCustomException(exceptionCode);
        	}
    		ftpClient.setFileType(fileType);
        	response = ftpClient.changeWorkingDirectory("ADF_Upload_Dir");
        	if(!response){
        		ftpClient.disconnect();
            	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
    			exceptionCode.setResponse(fileName);
    			throw new RuntimeCustomException(exceptionCode);
        	}
	        fileName = fileName.toUpperCase();
	        fileName = fileName.substring(0, fileName.lastIndexOf('.'))+"_"+SessionContextHolder.getContext().getVisionId()+fileExtension;
	        InputStream in = new ByteArrayInputStream(data);
	        ftpClient.storeFile(fileName, in);
	        in.close(); //close the io streams
	        ftpClient.disconnect();
        }
        catch(FileNotFoundException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }catch(IOException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }catch(Exception e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
        }
        exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
		exceptionCode.setOtherInfo(fileName);
        return exceptionCode;
    }    
/*	public ExceptionCode getQueryResults(UploadFilingVb vObject){
		setVerifReqDeleteType(vObject);
		vObject.setBusinessDate(getUploadFilingDao().getBusinessDate(vObject.getCountry(), vObject.getLeBook()));
		List<UploadFilingVb> collTemp = getScreenDao().getQueryResults(vObject, 1);
		doSetDesctiptionsAfterQuery(collTemp);
		if(collTemp.size() == 0){
			collTemp = getUploadFilingDao().getUploadFillingQueryResults(vObject);
			if(collTemp.size() == 0){
				ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 41, "Query", "");
				vObject.setBusinessDate("");
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}else{
				doSetDesctiptionsAfterQuery(collTemp);
				vObject.setShareHolder(collTemp.get(0).getShareHolder());
				doSetDesctiptionsAfterQuery(vObject);
				ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(collTemp);
				return exceptionCode;
			}
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			vObject.setShareHolder(collTemp.get(0).getShareHolder());
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}*/
	public ExceptionCode getQueryResults(UploadFilingVb vObject){
		setVerifReqDeleteType(vObject);
		if(!ValidationUtil.isValid(vObject.getBusinessDate())){
			vObject.setBusinessDate(getUploadFilingDao().getBusinessDate(vObject.getCountry(), vObject.getLeBook()));	
		}
		List<UploadFilingVb> collTemp = getScreenDao().getQueryResults(vObject, 1);
		doSetDesctiptionsAfterQuery(collTemp);
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 41, "Query", "");
			//vObject.setBusinessDate("");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			vObject.setShareHolder(collTemp.get(0).getShareHolder());
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}	
/*	public ArrayList getPageLoadValues(){
		List collTemp = null;
		String defaultCCountryLeBook="";
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1011);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			
			String country = SessionContextHolder.getContext().getCountry();
			String leBook = SessionContextHolder.getContext().getLeBook();
			defaultCCountryLeBook =country+"-"+leBook;
			if(!ValidationUtil.isValid(country) && !ValidationUtil.isValid(leBook)){
				country = getCommonDao().findVisionVariableValue("DEFAULT_COUNTRY");
				leBook =	getCommonDao().findVisionVariableValue("DEFAULT_LE_BOOK");
				defaultCCountryLeBook =country+"-"+leBook;
			}
			String businessDate = getCommonDao().getVisionBusinessDate(defaultCCountryLeBook);
			UploadFilingVb dObject = new UploadFilingVb();
			dObject.setCountry(country);
			dObject.setLeBook(leBook);
			dObject.setBusinessDate(businessDate);
			
			List<UploadFilingVb> uploadFilingList = getUploadFilingDao().getDefaultTemplates(dObject);
			arrListLocal.add(uploadFilingList);
			dObject.setBusinessDate("");
			arrListLocal.add(dObject);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}*/
	
	public ArrayList getPageLoadValues(UploadFilingVb nonAdfSchedulesVb){
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try{
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1084);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(1077);
			arrListLocal.add(collTemp);
			List<UploadFilingVb> list = getQueryPopupResults(nonAdfSchedulesVb);
			arrListLocal.add(list);
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}	

	public ExceptionCode listFilesFromFtpServer(int dirType,String orderBy){
		ExceptionCode exceptionCode  = null;
		TelnetConnection telnetConnection = null;
        try{ 
        	setUploadDownloadDirFromDB();
        	if("WINDOWS".equalsIgnoreCase(serverType)){
        		return listFilesFromFtpWindowServer(dirType, orderBy);
        	}
        	if(isSecuredFtp()){
        		return listFilesFromSFtpServer(dirType,orderBy);
        	}
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
			//telnetConnection.connect(getServerType());
			telnetConnection.connect();
			if(dirType == DIR_TYPE_DOWNLOAD)
				telnetConnection.sendCommand("cd "+downloadDir);
			else
				telnetConnection.sendCommand("cd "+buildLogsDir);
			String responseStr = telnetConnection.sendCommand("ls -ltc ");
			String[] fileEntryArray  = responseStr.split("\r\n");
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<String> fileEntryList = unixFtpEntryParser.preParse(new LinkedList<String>(Arrays.asList(fileEntryArray)));
			List<FTPFile> lfiles = new ArrayList<FTPFile>(fileEntryList.size());
			for(String fileEntry:fileEntryList){
				FTPFile ftpFile = unixFtpEntryParser.parseFTPEntry(fileEntry);
				if(ftpFile != null)
					lfiles.add(ftpFile);
			}
			for(FTPFile fileName:lfiles){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
/*				if(dirType == 1){
					fileInfoVb.setDate(formatDate(fileName));
					lFileList.add(fileInfoVb);
				}*/
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
	    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
//	    	exceptionCode.setResponse(createParentChildRelations(createData(),orderBy,dirType));
	    	exceptionCode.setResponse(createParentChildRelations(lFileList,orderBy,dirType));
        }
       catch(FileNotFoundException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch(IOException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch (Exception e) {
			e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
		}finally{
        	if(telnetConnection != null && telnetConnection.isConnected()){
        		telnetConnection.disconnect();
        		telnetConnection = null;
        	}
        }
        return exceptionCode;
	}
	public ExceptionCode listFilesFromFtpWindowServer(int dirType,String orderBy){
		ExceptionCode exceptionCode  = null;
		TelnetConnection telnetConnection = null;
        try{ 
        	
    		FTPClient ftpClient = getConnection();
        	ftpClient.connect(getHostName());
        	boolean response = ftpClient.login(getUserName(), getPassword());
        	if(!response){
        		ftpClient.disconnect();
            	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");;
    			throw new RuntimeCustomException(exceptionCode);
        	}
			if(dirType == DIR_TYPE_DOWNLOAD)
				//ftpClient.sendCommand("cd "+downloadDir);
				response = ftpClient.changeWorkingDirectory(downloadDir);
			else
				//ftpClient.sendCommand("cd "+buildLogsDir);
				response = ftpClient.changeWorkingDirectory(buildLogsDir);
			
			FTPFile[] files  = ftpClient.listFiles();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			for(FTPFile fileName:files){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
/*				if(dirType == 1){
					//fileInfoVb.setDate(formatDate(fileName));
					lFileList.add(fileInfoVb);
				}*/
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
			/*List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
			//telnetConnection.connect(getServerType());
			telnetConnection.connect();
			if(dirType == DIR_TYPE_DOWNLOAD)
				telnetConnection.sendCommand("cd "+downloadDir);
			else
				telnetConnection.sendCommand("cd "+buildLogsDir);
			String responseStr = telnetConnection.sendCommand("dir ");
			String[] fileEntryArray  = responseStr.split("\r\n");
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<String> fileEntryList = unixFtpEntryParser.preParse(new LinkedList<String>(Arrays.asList(fileEntryArray)));
			List<FTPFile> lfiles = new ArrayList<FTPFile>(fileEntryList.size());
			for(String fileEntry:fileEntryList){
				FTPFile ftpFile = unixFtpEntryParser.parseFTPEntry(fileEntry);
				if(ftpFile != null)
					lfiles.add(ftpFile);
			}
			for(FTPFile fileName:lfiles){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
				if(dirType == 1){
					fileInfoVb.setDate(formatDate(fileName));
					lFileList.add(fileInfoVb);
				}
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}*/
	    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
//	    	exceptionCode.setResponse(createParentChildRelations(createData(),orderBy,dirType));
	    	exceptionCode.setResponse(createParentChildRelations(lFileList,orderBy,dirType));
        }
       catch(FileNotFoundException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch(IOException e){
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch (Exception e) {
			e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
		}finally{
        	if(telnetConnection != null && telnetConnection.isConnected()){
        		telnetConnection.disconnect();
        		telnetConnection = null;
        	}
        }
        return exceptionCode;
	}
	public ExceptionCode listFilesFromSFtpServer(int dirType,String orderBy){
		ExceptionCode exceptionCode  = null;
        try{
        	setUploadDownloadDirFromDB();
        	JSch jsch = new JSch();  
			jsch.setKnownHosts( getKnownHostsFileName() );
			Session session = jsch.getSession( getUserName(), getHostName() ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword( getPassword());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel( "sftp" ); 
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if(dirType == DIR_TYPE_DOWNLOAD){
				sftpChannel.cd(downloadDir);
			}else{
				sftpChannel.cd(buildLogsDir);
			}
			Vector<ChannelSftp.LsEntry> vtc =  sftpChannel.ls("*NONADF*.*");
			sftpChannel.disconnect();
			session.disconnect();
			List<FileInfoVb> lFileList = new ArrayList<FileInfoVb>();
			FTPClientConfig conf = new FTPClientConfig(getServerType());
			conf.setServerTimeZoneId(getTimezoneId());
			UnixFTPEntryParser unixFtpEntryParser = new UnixFTPEntryParser(conf);
			List<FTPFile> lfiles = new ArrayList<FTPFile>(vtc.size());
			for(ChannelSftp.LsEntry lsEntry :  vtc) {
				FTPFile file= unixFtpEntryParser.parseFTPEntry(lsEntry.getLongname());
				if(file != null)
					lfiles.add(file);
			}
			for(FTPFile fileName:lfiles){
				if(fileName.getName().endsWith(".zip") || fileName.getName().endsWith(".tar")) continue;
				FileInfoVb fileInfoVb = new FileInfoVb();
				fileInfoVb.setName(fileName.getName());
				fileInfoVb.setSize(formatFileSize(fileName.getSize()));
/*				if(dirType == 1){
					fileInfoVb.setDate(formatDate(fileName));
					lFileList.add(fileInfoVb);
				}*/
				if(dirType == 1 && (fileInfoVb.getName().startsWith("VISION_UPLOAD") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"_")>0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if(dirType == 2 && (fileInfoVb.getName().startsWith("BUILDCRON") && StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")<=0)){
					Calendar cal = new GregorianCalendar();
					int month = cal.get(Calendar.MONTH)+1;
					int year = cal.get(Calendar.YEAR);
					int day = cal.get(Calendar.DAY_OF_MONTH);
					fileInfoVb.setDate(CommonUtils.getFixedLength(String.valueOf(day), "0", 2)+"-"+CommonUtils.getFixedLength(String.valueOf(month), "0", 2)+"-"+year);
					lFileList.add(fileInfoVb);
				}else if((dirType == 2 || dirType == 1)&& (StringUtils.countOccurrencesOf(fileInfoVb.getName(),"-")>0)){
					fileInfoVb.setDate(formatDate1(fileName));
					lFileList.add(fileInfoVb);
				}
			}
	    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
	    	exceptionCode.setResponse(createParentChildRelations(lFileList,orderBy,dirType));
        }
       catch(FileNotFoundException e){
    	   	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch(IOException e){
        	e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
        }
        catch (Exception e) {
			e.printStackTrace();
        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
		}
        return exceptionCode;
	}
	private String formatFileSize(long numSize) {
        String strReturn;
        BigDecimal lSize = BigDecimal.valueOf((numSize)).divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
        if(lSize.floatValue() <=0){
        	lSize = BigDecimal.valueOf(1);
        }
        strReturn = lSize + " KB";
        if (lSize.floatValue() > 1024) {
        	lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
            strReturn = lSize + " MB";
            if (lSize.floatValue() > 1024) {
            	lSize = lSize.divide(BigDecimal.valueOf(1024), 1, BigDecimal.ROUND_HALF_UP);
                strReturn = lSize + " GB";
            }
        }                
        return strReturn;
    }
	private String formatDate1(FTPFile fileName) {
		String fileName1  = fileName.getName();
		String year = fileName1.substring(fileName1.length()-14,fileName1.length()-10);
		String month  = fileName1.substring(fileName1.length()-9,fileName1.length()-7);
		String day = fileName1.substring(fileName1.length()-6,fileName1.length()-4); 
        return CommonUtils.getFixedLength(day, "0", 2)+"-"+CommonUtils.getFixedLength(month, "0", 2)+"-"+year;
    }	
	private FTPClient getConnection() throws IOException {
		FTPClient ftpClient= new FTPClient();
		FTPClientConfig conf = new FTPClientConfig(getServerType());
		conf.setServerTimeZoneId(getTimezoneId());
		ftpClient.configure(conf);
        return ftpClient;
    }

    private void setUploadDownloadDirFromDB(){
    	String uploadLogFilePathFromDB = getVisionVariableValue("VISION_XLUPL_LOG_FILE_PATH");
    	if(uploadLogFilePathFromDB != null && !uploadLogFilePathFromDB.isEmpty()){
    		String[] downloadPath = uploadLogFilePathFromDB.split("\\\\");
    		int length = downloadPath.length;
    		downloadDir = downloadPath[length-1];
    	}
    	String uploadDataFilePathFromDB = getVisionVariableValue("NON_ADF_XL_UPLOAD_PATH");
    	System.out.println("uploadDataFilePathFromDB : "+uploadDataFilePathFromDB);
    	logger.info("uploadDataFilePathFromDB : "+uploadDataFilePathFromDB);
    	if(uploadDataFilePathFromDB != null && !uploadDataFilePathFromDB.isEmpty()){
    		String[] uploadPath = uploadDataFilePathFromDB.split("\\\\");
    		int length = uploadPath.length;
    		uploadDir = uploadPath[length-1];
    	}
    	String adUploadDataFilePathFromDB = getVisionVariableValue("NON_ADF_AD_XL_PATH");
    	if(adUploadDataFilePathFromDB != null && !adUploadDataFilePathFromDB.isEmpty()){
    		String[] adUploadPath = adUploadDataFilePathFromDB.split("\\\\");
    		int length = adUploadPath.length;
    		adUploadDir = adUploadPath[length-1];
    	}
    	String buildLogsFilePathFromDB = getVisionVariableValue("BUILDCRON_LOG_FILE_PATH");
    	if(buildLogsFilePathFromDB != null && !buildLogsFilePathFromDB.isEmpty()){
    		String[] buildLogPath = buildLogsFilePathFromDB.split("\\\\");
    		int length = buildLogPath.length;
    		buildLogsDir = buildLogPath[length-1];
    	}
    	String uploadFileChkIntervelFromDB = getVisionVariableValue("VISION_XLUPL_FILE_CHK_INTVL");
    	if(uploadFileChkIntervelFromDB != null && !uploadFileChkIntervelFromDB.isEmpty()){
    		if(ValidationUtil.isValidId(uploadFileChkIntervelFromDB))
    			uploadFileChkIntervel = Integer.valueOf(uploadFileChkIntervelFromDB);
    	}
    	String scriptPathFromDB = getVisionVariableValue("BUILDCRON_SCRIPTS_PATH");
    	if(scriptPathFromDB != null && !scriptPathFromDB.isEmpty()){
    		String[] scriptPath = scriptPathFromDB.split("\\\\");
    		int length = scriptPath.length;
    		scriptDir = scriptPath[length-1];
    	}
		String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
		if(!ValidationUtil.isValid(environmentParam))
			environmentParam="UAT";
		String environmentNode = UPLOAD_NODE;
		if(!ValidationUtil.isValid(environmentNode))
			environmentNode="N1";
		
    	String serverHost = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_IP");
    	if(ValidationUtil.isValid(serverHost)){
    		hostName = serverHost;
    	}
    	String serverUserName = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_USER");
    	if(ValidationUtil.isValid(serverUserName)){
    		userName = serverUserName;
    	}
    	String serverPassword = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_PWD");
    	if(ValidationUtil.isValid(serverPassword)){
    		password = serverPassword;
    	}
    }
    public ExceptionCode insertRecord(ExceptionCode pRequestCode, List<UploadFilingVb> vObjects, String tarNode){
		ExceptionCode exceptionCode  = null;
		DeepCopy<UploadFilingVb> deepCopy = new DeepCopy<UploadFilingVb>();
		List<UploadFilingVb> clonedObject = null;
		UploadFilingVb vObject = null;
		Set<String> checkedFileNames = new TreeSet<String>();
		try
		{
			setAtNtValues(vObjects);
			vObject = (UploadFilingVb) pRequestCode.getOtherInfo();
			setVerifReqDeleteType(vObject);
			clonedObject = deepCopy.copyCollection(vObjects);
			doFormateData(vObjects);
			
			String currentNode = System.getenv("VISION_NODE_NAME");
			if(!ValidationUtil.isValid(currentNode)) {
				currentNode = "UAT";
			}
//			currentNode = "UAT";
			String xclUploadPath= getCommonDao().findVisionVariableValue("NON_ADF_XL_UPLOAD_PATH");
			for(UploadFilingVb vObj:vObjects){
				if(vObj.isChecked()){
					checkedFileNames.add(vObj.getFileName().toUpperCase());
				}
			}
			
			int runningCount = getUploadFilingDao().getCountOfRunningTemplates(vObject.getAdfNumber(), 1);
			if(runningCount>0){
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "You cannot submit at this moment because templates have been processed.");
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
					exceptionCode.setResponse(clonedObject);
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}
			}
			runningCount = 0;
			runningCount = getUploadFilingDao().getCountOfRunningTemplates(vObject.getAdfNumber(), 2);
			if(runningCount>0){
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "You cannot submit at this moment because templates are in progress.");
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
					exceptionCode.setResponse(clonedObject);
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}
			}
			runningCount = 0;
			runningCount = getUploadFilingDao().getCountOfRunningTemplates(vObject.getAdfNumber(), 3);
			if(runningCount>0){
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "You cannot submit at this moment because templates are in queue to be processed.");
				if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
					exceptionCode.setResponse(clonedObject);
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}
			}
			
			System.out.println("checkedFileNames - different:"+checkedFileNames);
			
			
			if(checkedFileNames.size()>0){
				for(String excelFileName : checkedFileNames){
					if(ValidationUtil.isValid(excelFileName)){
						exceptionCode = checkeExcelFile(excelFileName, tarNode, xclUploadPath);
						if(exceptionCode!=null && exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Please upload the file '"+excelFileName+"'!");
							exceptionCode.setResponse(clonedObject);
							exceptionCode.setOtherInfo(vObject);
							return exceptionCode;
						}
					}						
			    }
			}
			
			/*Start Commented by Prakash on 10-Jan-2019*/
			/*System.out.println("checkedFileNames - same:"+checkedFileNames.size());
			if(checkedFileNames.size()>0){
				for(String excelFileName : checkedFileNames){
					if(ValidationUtil.isValid(excelFileName)){
						UploadFilingVb vObj= new UploadFilingVb();
						vObj.setFileName(excelFileName);
						Set<String> checkedExcelTemplateIds = getExcelTemplateIds(vObjects, excelFileName);
						exceptionCode = fileCheckInLocal(excelFileName, xclUploadPath, checkedExcelTemplateIds);
						if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
							exceptionCode.setResponse(clonedObject);
							exceptionCode.setOtherInfo(vObject);
							return exceptionCode;
						}	
					}						
			    }
			}*/
			/*End Commented by Prakash on 10-Jan-2019*/
			
			
			exceptionCode = getUploadFilingDao().doInsertRecord(vObjects);
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(vObjects);
			// Deleting Excel Files from Local
			/*if(!isLocal)
				deleteLocalExcelFiles(checkedFileNames, xclUploadPath);*/
			
			return exceptionCode;
		}catch(RuntimeCustomException rex){
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setResponse(clonedObject);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
	}
    public Set<String> getExcelTemplateIds(List<UploadFilingVb> vObjects, String filrName){
  	  Set<String> excelTemplateIds = new TreeSet<>();
  		 for(UploadFilingVb vObject : vObjects){
  			 if(vObject.isChecked()){
  				 if(filrName.toUpperCase().equalsIgnoreCase(vObject.getFileName().toUpperCase())){
  					 excelTemplateIds.add(vObject.getExcelTemplateId().toUpperCase());
  				 }
  			 }
  		 }
  	  return excelTemplateIds;
    }
    public void deleteLocalExcelFiles(Set<String> checkedFileNames, String xclUploadPath){
		if(checkedFileNames.size()>0){
			for(String excelFileName : checkedFileNames){
				if(ValidationUtil.isValid(excelFileName)){
					File file = new File(xclUploadPath, excelFileName+".XLS");
					if(file.exists()){
						file.delete();
					}
					File file1 = new File(xclUploadPath, excelFileName+".XLSX");
					if(file1.exists()){
						file1.delete();
					}
				}						
		    }
		}
    }
	private BufferedInputStream downloadFilesFromFTP(String pFileNames, int dirType){
		BufferedInputStream bufferedInputStream = null;
		TelnetConnection telnetConnection = null;
		String[] fileNames = pFileNames.split(" @- ");
		setUploadDownloadDirFromDB();
		FTPClient ftpClient = null;
		try {
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
	    	boolean response = ftpClient.login(getUserName(), getPassword());
	    	if(!response){
	    		ftpClient.disconnect();
	    		throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
	    	}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(dirType == DIR_TYPE_DOWNLOAD)
				response = ftpClient.changeWorkingDirectory(downloadDir);
			else
				response = ftpClient.changeWorkingDirectory(buildLogsDir);
	    	if(!response){
	    		ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
	    	}
	    	if(fileNames.length== 1){
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream(fileNames[0]));
			}else if(fileNames.length > 1){
	    		telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
				telnetConnection.connect();
				if(dirType == DIR_TYPE_DOWNLOAD)
					telnetConnection.sendCommand("cd "+downloadDir);
				else
					telnetConnection.sendCommand("cd "+buildLogsDir);
				//for(int i=0;i<fileNames.length;i++){
				for(int i=0;i<fileNames.length;i++){
					telnetConnection.sendCommand("echo "+fileNames[i]+" >> example.txt");
				}
				telnetConnection.sendCommand("tar cvf logs.tar `cat example.txt`");
				telnetConnection.sendCommand("rm example.txt");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				if(dirType == DIR_TYPE_DOWNLOAD)
					response = ftpClient.changeWorkingDirectory(downloadDir);
				else
					response = ftpClient.changeWorkingDirectory(buildLogsDir);
		    	if(!response){
		    		ftpClient.disconnect();
					throw new FTPConnectionClosedException();
		    	}
		    	bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream("logs.tar"));
			}else{
				throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ftpClient != null && ftpClient.isConnected()){
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
			}
			if(telnetConnection != null && telnetConnection.isConnected()){
				telnetConnection.disconnect();
			}
		}
		return bufferedInputStream;
	}
	private BufferedInputStream downloadFilesFromFTPWindows(String pFileNames, int dirType){
		BufferedInputStream bufferedInputStream = null;
		TelnetConnection telnetConnection = null;
		String[] fileNames = pFileNames.split(" @- ");
		pFileNames = pFileNames.replaceAll(" @- ", " ");
		setUploadDownloadDirFromDB();
		FTPClient ftpClient = null;
		boolean response;
		try {
    		ftpClient = getConnection();
			ftpClient.connect(getHostName());
	    	response = ftpClient.login(getUserName(), getPassword());
	    	if(!response){
	    		ftpClient.disconnect();
	    		throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
	    	}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(dirType == DIR_TYPE_DOWNLOAD)
				response = ftpClient.changeWorkingDirectory(downloadDir);
			else
				response = ftpClient.changeWorkingDirectory(buildLogsDir);
	    	if(!response){
	    		ftpClient.disconnect();
				throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
	    	}			
	    	if(fileNames.length== 1){
				bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream(fileNames[0]));
			}else if(fileNames.length > 1){
				
/*				String lFileList ="";
    			for(String lFileInfoVb : fileNames){
            		//lFileList += "\\"+lFileInfoVb +" ";
            		lFileList += "\"E:\\Vision_FTP\\XLUPLOADLOGS\\"+lFileInfoVb +"\" ";
            	}
    			ftpClient.sendCommand("del logs.zip\"");
    			ftpClient.sendCommand("zip logs.zip\" "+lFileList);
*/				
	    		telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
	    		telnetConnection.connect();
	    		//telnetConnection.sendCommand("e:");
				if(dirType == DIR_TYPE_DOWNLOAD)
					telnetConnection.sendCommand("cd "+processDir+downloadDir);
				else
					telnetConnection.sendCommand("cd "+processDir+buildLogsDir);
				telnetConnection.sendCommand(foderDrive);
				telnetConnection.sendCommand("del logs.tar ");
				telnetConnection.sendCommand("zip logs.tar "+pFileNames);
				ftpClient = getConnection();
				ftpClient.connect(getHostName());
		    	response = ftpClient.login(getUserName(), getPassword());
		    	if(!response){
		    		ftpClient.disconnect();
		    		throw new FTPConnectionClosedException("Unable to connect to Remote Computer");
		    	}				
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				if(dirType == DIR_TYPE_DOWNLOAD)
					response = ftpClient.changeWorkingDirectory(downloadDir);
				else
					response = ftpClient.changeWorkingDirectory(buildLogsDir);
		    	if(!response){
		    		ftpClient.disconnect();
					throw new FTPConnectionClosedException();
		    	}
		    	bufferedInputStream = new BufferedInputStream(ftpClient.retrieveFileStream("logs.tar"));
			}else{
				throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ftpClient != null && ftpClient.isConnected()){
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
			}
			if(telnetConnection != null && telnetConnection.isConnected()){
				telnetConnection.disconnect();
			}
		}
		return bufferedInputStream;
	}	
	public BufferedInputStream downloadFilesFromSFTP(String pFileNames, int dirType){
		BufferedInputStream bufferedInputStream = null;
		String[] fileNames = pFileNames.split(" @- ");
		setUploadDownloadDirFromDB();
		Session session = null;
		try {
			JSch jsch = new JSch();  
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession( getUserName(), getHostName() ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword(getPassword()); 
			}  
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp"); 
			channel.connect();  
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			if(dirType == DIR_TYPE_DOWNLOAD)
				sftpChannel.cd(downloadDir);
			else
				sftpChannel.cd(buildLogsDir);
	    	if(fileNames.length== 1){
	    		try{
					InputStream ins = sftpChannel.get(fileNames[0]);
		    		bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
	    		}catch(SftpException e){}
			}else if(fileNames.length > 1){
				channel = session.openChannel("shell");  
				OutputStream inputstream_for_the_channel = channel.getOutputStream();
				PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
				channel.connect();
				if(dirType == DIR_TYPE_DOWNLOAD)
					commander.println("cd "+downloadDir+"\n");
				else
					commander.println("cd "+buildLogsDir+"\n");
				//for(int i=0;i<fileNames.length;i++){
				for(int i=0;i<fileNames.length;i++){
					commander.println("echo "+fileNames[i]+" >> example.txt"+"\n");
				}
				commander.println("tar cvf logs.tar `cat example.txt`"+"\n");
				commander.println("rm example.txt");
				commander.println("exit");
				commander.close();
				do {Thread.sleep(1000); } while(!channel.isEOF()); 
				try{
		    		InputStream ins = sftpChannel.get("logs.tar");
		    		bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
		    	}catch(SftpException ex){}
			}else{
				throw new FileNotFoundException("File not found on the Server.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(session != null && session.isConnected()){
				session.disconnect();
			}
		}
		return bufferedInputStream;
	}
/*	@Deprecated
	public ExceptionCode downloadFiles(List<FileInfoVb> pFileList, int dirType){
    	String lFileList ="";
//    	FtpConnection ftpConnection = null;
    	ExceptionCode exceptionCode  = null;
    	TelnetConnection telnetConnection = null;
    	FileInfoVb lFileVb = new FileInfoVb();
    	if(pFileList == null || pFileList.size() <0){
    		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
			throw new RuntimeCustomException(exceptionCode);
    	}
    	if(pFileList.size() > 1){
        	try{
        		setUploadDownloadDirFromDB();
        		telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
        		if(FTPClientConfig.SYST_NT.equalsIgnoreCase(getServerType())){
        			telnetConnection.connect();
        			for(FileInfoVb lFileInfoVb : pFileList){
                		lFileList += "\"D:\\Vision Documents\\FtpDrops\\xlupload_logs\\"+lFileInfoVb.getName() +"\" ";
                	}
        			telnetConnection.sendCommand("del \"D:\\Vision Documents\\FtpDrops\\xlupload_logs\\logs.zip\"");
        			telnetConnection.sendCommand("zip \"D:\\Vision Documents\\FtpDrops\\xlupload_logs\\logs.zip\" "+lFileList);
        			ftpConnection = new FtpConnection(getHostName());
                	int response = ftpConnection.login(getUserName(), getPassword());
                	if(response != FtpConstants.LOGIN_OK){
                		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
                	}
                	boolean cdResponse;
                	if(dirType == DIR_TYPE_DOWNLOAD)
                		cdResponse = ftpConnection.chdir(downloadDir);
        			else
        				cdResponse = ftpConnection.chdir(buildLogsDir);
                	if(!cdResponse){
                		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
                	}
                	ftpConnection.binary();
                	InputStream input = ftpConnection.getDownloadInputStream("logs.zip");
        			if(input != null){
        				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        				int nRead;
        				byte[] data = new byte[16384];
        				while ((nRead = input.read(data, 0, data.length)) != -1) {
        				  buffer.write(data, 0, nRead);
        				}
        				buffer.flush();
        				byte[] totalData = buffer.toByteArray(); 
        				buffer.close();
            			lFileVb.setData(totalData);
            			lFileVb.setSize(totalData.length+"");
            			lFileVb.setName("logs.zip");
            			input.close();
        			}else{
        				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
        			}
        		}else{
        			for(FileInfoVb lFileInfoVb : pFileList){
                		lFileList += lFileInfoVb.getName() +" ";
                	}
        			telnetConnection.connect();
        			if(dirType == DIR_TYPE_DOWNLOAD)
        				telnetConnection.sendCommand("cd "+downloadDir);
        			else
        				telnetConnection.sendCommand("cd "+buildLogsDir);
        			
        			StringBuffer tmp = new StringBuffer();
   					for(FileInfoVb tepFileVb:pFileList){
   						if(tmp.length() > 255){
   							telnetConnection.sendCommand("echo "+tmp.toString().trim()+" >> example.txt");
   							tmp = new StringBuffer();
   						}
   						tmp.append(tepFileVb.getName() + " ");
   					}
   					telnetConnection.sendCommand("tar cvf logs.tar `cat example.txt | tr \" \" \"\n\"`");
       					
       				
        			ftpConnection = new FtpConnection(getHostName());
                	int response = ftpConnection.login(getUserName(), getPassword());
                	if(response != FtpConstants.LOGIN_OK){
                		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
                	}
                	boolean cdResponse;
                	if(dirType == DIR_TYPE_DOWNLOAD)
                		cdResponse = ftpConnection.chdir(downloadDir);
        			else
        				cdResponse = ftpConnection.chdir(buildLogsDir);
                	if(!cdResponse){
                		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
                	}
                	ftpConnection.binary();
                	InputStream input = ftpConnection.getDownloadInputStream("logs.tar");
        			if(input != null){
        				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        				int nRead;
        				byte[] data = new byte[16384];
        				while ((nRead = input.read(data, 0, data.length)) != -1) {
        				  buffer.write(data, 0, nRead);
        				}
        				buffer.flush();
        				byte[] totalData = buffer.toByteArray(); 
        				buffer.close();
            			lFileVb.setData(totalData);
            			lFileVb.setSize(totalData.length+"");
            			lFileVb.setName("logs.tar");
            			input.close();
        			}else{
        				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
        				throw new RuntimeCustomException(exceptionCode);
        			}
        		}
        	}catch (Exception e) {
        		e.printStackTrace();
        		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
    			throw new RuntimeCustomException(exceptionCode);
    		}finally{
    			if(telnetConnection != null && telnetConnection.isConnected()){
    				telnetConnection.disconnect();
    				telnetConnection = null;
    			}
        		if(ftpConnection != null)
        			ftpConnection.disconnect();
    		}
    	}else{
    		try{
    			setUploadDownloadDirFromDB();
    			ftpConnection = new FtpConnection(getHostName());
            	int response = ftpConnection.login(getUserName(), getPassword());
            	if(response != FtpConstants.LOGIN_OK){
            		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
    				throw new RuntimeCustomException(exceptionCode);
            	}
            	boolean cdResponse;
            	if(dirType == DIR_TYPE_DOWNLOAD)
            		cdResponse = ftpConnection.chdir(downloadDir);
    			else
    				cdResponse = ftpConnection.chdir(buildLogsDir);
            	if(!cdResponse){
            		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
    				throw new RuntimeCustomException(exceptionCode);
            	}
            	ftpConnection.binary();
            	InputStream input = ftpConnection.getDownloadInputStream(pFileList.get(0).getName());
    			if(input != null){
    				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    				int nRead;
    				byte[] data = new byte[16384];
    				while ((nRead = input.read(data, 0, data.length)) != -1) {
    				  buffer.write(data, 0, nRead);
    				}
    				buffer.flush();
    				byte[] totalData = buffer.toByteArray(); 
    				buffer.close();
        			lFileVb.setData(totalData);
        			lFileVb.setSize(totalData.length+"");
        			lFileVb.setName(pFileList.get(0).getName());
        			input.close();
    			}else{
    				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
    				throw new RuntimeCustomException(exceptionCode);
    			}
        	}catch (Exception e) {
        		e.printStackTrace();
        		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Download", "");;
    			throw new RuntimeCustomException(exceptionCode);
    		}finally{
        		if(ftpConnection != null)
        			ftpConnection.disconnect();
				
    		}
    	}
    	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Download", "");
    	exceptionCode.setResponse(lFileVb);
    	return exceptionCode;
	}*/
	protected ExceptionCode doValidate(UploadFilingVb pObject, List<UploadFilingVb> vObjects){
		ExceptionCode exceptionCode = null;
		long currentUser = SessionContextHolder.getContext().getVisionId();
		FTPClient ftpClient;
		try {
			if(isSecuredFtp()){
				return doValidateSFtp(pObject, vObjects);
			}
			ftpClient = getConnection();
			ftpClient.connect(getHostName());
	    	boolean response = ftpClient.login(getUserName(), getPassword());
	    	if(!response){
	    		ftpClient.disconnect();
	        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");;
				throw new RuntimeCustomException(exceptionCode);
	    	}
			ftpClient.setFileType(fileType);
	    	response = ftpClient.changeWorkingDirectory(uploadDir);
	    	if(!response){
	    		ftpClient.disconnect();
	        	exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");;
				throw new RuntimeCustomException(exceptionCode);
	    	}
	    	long maxIntervel = 0;
			for(UploadFilingVb lVUploadVb : vObjects){
				if(lVUploadVb.isChecked()){
					setVerifReqDeleteType(lVUploadVb);
					int countOfUplTables = getUploadFilingDao().getCountOfUploadTables(lVUploadVb);
					lVUploadVb.setMaker(currentUser);
					if(countOfUplTables <= 0){
						String strErrorDesc = "No sufficient privileges to upload for the Table["+lVUploadVb.getTemplateName()+"]";
		            	 exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert", strErrorDesc);
		            	 throw new RuntimeCustomException(exceptionCode);
					 }
					 FTPFile file = isFileExists(lVUploadVb.getFileName().toUpperCase()+"_"+currentUser+".txt", ftpClient);
		             if(file == null){
		            	 String strErrorDesc = lVUploadVb.getFileName() + ".txt does not exists. Please upload the file first.";
		            	 exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert", strErrorDesc);
		            	 throw new RuntimeCustomException(exceptionCode);
		             }
		             maxIntervel = Math.max((((System.currentTimeMillis()/1000) - file.getTimestamp().getTimeInMillis())/60) , maxIntervel);
				}
			}
			if(maxIntervel >= uploadFileChkIntervel && pObject.getCheckUploadInterval() == false){
				String strErrorDesc = "Upload file(s) is more than "+uploadFileChkIntervel+" mins old. Do you want to continue with upload?";
				exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert", strErrorDesc);
				throw new RuntimeCustomException(exceptionCode);
            }
			ftpClient.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");;
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	protected ExceptionCode doValidateSFtp(UploadFilingVb pObject, List<UploadFilingVb> vObjects){
		ExceptionCode exceptionCode = null;
		long currentUser = SessionContextHolder.getContext().getVisionId();
		try {
			JSch jsch = new JSch();  
			jsch.setKnownHosts(getKnownHostsFileName());
			Session session = jsch.getSession( getUserName(), getHostName() ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword(getPassword()); 
			}  
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp"); 
			channel.connect();  
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(uploadDir);
			long maxIntervel = 0;
			for(UploadFilingVb lVUploadVb : vObjects){
				if(lVUploadVb.isChecked()){
					setVerifReqDeleteType(lVUploadVb);
					 int countOfUplTables = getUploadFilingDao().getCountOfUploadTables(lVUploadVb);
					 lVUploadVb.setMaker(currentUser);
					 if(countOfUplTables <= 0){
		            	 String strErrorDesc = "No sufficient privileges to upload for the Table["+lVUploadVb.getTemplateName()+"]";
		            	 exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert", strErrorDesc);
		            	 throw new RuntimeCustomException(exceptionCode);
					 }
					 ChannelSftp.LsEntry entry = isFileExists(lVUploadVb.getFileName().toUpperCase()+"_"+currentUser+".txt", sftpChannel);
		             if( entry == null){
		            	 String strErrorDesc = lVUploadVb.getFileName() + ".txt does not exists. Please upload the file first.";
		            	 exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.WE_HAVE_ERROR_DESCRIPTION, "Insert", strErrorDesc);
		            	 throw new RuntimeCustomException(exceptionCode);
		             }
		             maxIntervel = Math.max((((System.currentTimeMillis()/1000) - entry.getAttrs().getMTime())/60) , maxIntervel);
				}
			}
			if(maxIntervel >= uploadFileChkIntervel && pObject.getCheckUploadInterval() == false){
				String strErrorDesc = "Upload file(s) is more than "+uploadFileChkIntervel+" mins old. Do you want to continue with upload?";
				exceptionCode = new ExceptionCode();
				exceptionCode.setErrorSevr("W");
				exceptionCode.setErrorCode(Constants.WE_HAVE_ERROR_DESCRIPTION);
				exceptionCode.setErrorMsg(strErrorDesc);
				throw new RuntimeCustomException(exceptionCode);
            }
			sftpChannel.disconnect();
			session.disconnect();
		} catch (SftpException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");;
			throw new RuntimeCustomException(exceptionCode);
		} catch (JSchException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Add", "");;
			throw new RuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
    private List<FileInfoVb> createParentChildRelations(List<FileInfoVb> legalTreeList,String orderBy,int dirType) throws IOException {
        List<FileInfoVb> lResult = new ArrayList<FileInfoVb>(0);
        Set set =new HashSet<FileInfoVb>();
        //Top Roots are added. 
        for(FileInfoVb fileVb:legalTreeList){
        	if("Date".equalsIgnoreCase(orderBy)){
	        	//String date = fileVb.getName().substring(fileVb.getName().length()-14, fileVb.getName().length()-4);
	        	if(fileVb.getDate()!=null && fileVb.getDate()!=""){
	        		set.add(fileVb.getDate());
	             }
        	}else{
        		String fileNeme = "";
        		if(dirType==2){
        			int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
	        		if(cout == 0){
	        			int cout1 = StringUtils.countOccurrencesOf(fileVb.getName(), "-");
	        			if(cout1 == 0)
	        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
	        			else
	        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 15);
	        		}else if(cout>1)
	        			fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 21);
        		}else{
        			int cout = StringUtils.countOccurrencesOf(fileVb.getName(), "_");
        			if(cout==1){
        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 4);
        			}else if(cout>1){
        				fileNeme = fileVb.getName().substring(0, fileVb.getName().length() - 20);
        			}
        		}
	        	if(fileNeme!=null && fileNeme.length()>0){
	        		set.add(fileNeme);
	             }
        	}
        }
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String date = (String) iterator.next();
			FileInfoVb fileVb = new FileInfoVb();
			fileVb.setDescription(date);
			lResult.add(fileVb);
		}
        //For each top node add all child's and to that child's add sub child's recursively.
        for(FileInfoVb legalVb:lResult){
              addChilds(legalVb,legalTreeList,orderBy,dirType);
        }
       if("Date".equalsIgnoreCase(orderBy)){
           final SimpleDateFormat dtF= new SimpleDateFormat("dd-MM-RRRR");
	        //set the empty lists to null. this is required for UI to display the leaf nodes properly.
	        Collections.sort(lResult, new Comparator<FileInfoVb>(){
	            public int compare (FileInfoVb m1, FileInfoVb m2){
	            	try {
						return dtF.parse(m1.getDescription()).compareTo(dtF.parse(m2.getDescription()));
					} catch (ParseException e) {
						return 0;
					}
	            }
	        });
	        Collections.reverse(lResult);
       }else{
	        Collections.sort(lResult, new Comparator<FileInfoVb>(){
	            public int compare (FileInfoVb m1, FileInfoVb m2){
	            	return m1.getDescription().compareTo(m2.getDescription());
	            }
	        });
       }
        return lResult;
    }
    public void addChilds(FileInfoVb vObject, List<FileInfoVb> legalTreeListCopy,String orderBy,int dirType) {
        for(FileInfoVb fileTreeVb:legalTreeListCopy){
        	if("Date".equalsIgnoreCase(orderBy)){
	        	//String date = fileTreeVb.getName().substring(fileTreeVb.getName().length()-14, fileTreeVb.getName().length()-4);
		        if(vObject.getDescription().equalsIgnoreCase(fileTreeVb.getDate())){
		        	if(vObject.getChildren() == null){
		        		vObject.setChildren(new ArrayList<FileInfoVb>(0));
		             }
		            fileTreeVb.setDescription(fileTreeVb.getName());
		            vObject.getChildren().add(fileTreeVb);
		         }
        	}else{
        		String fileNeme = "";
        		if(dirType==2){
        			int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
	        		if(cout == 0){
	        			int cout1 = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "-");
	        			if(cout1 == 0)
	        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
	        			else
	        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 15);
	        		}else if(cout>1)
	        			fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 21);
        		}else{
        			int cout = StringUtils.countOccurrencesOf(fileTreeVb.getName(), "_");
        			if(cout==1){
        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 4);
        			}else if(cout>1){
        				fileNeme = fileTreeVb.getName().substring(0, fileTreeVb.getName().length() - 20);
        			}        			
        		}
		        if(vObject.getDescription().equalsIgnoreCase(fileNeme)){
		        	if(vObject.getChildren() == null){
		        		vObject.setChildren(new ArrayList<FileInfoVb>(0));
		             }
		            fileTreeVb.setDescription(fileTreeVb.getName());
		            vObject.getChildren().add(fileTreeVb);
		         }
        	}
        }
    }
    public ExceptionCode fileDownload(int dirType, String fileNames, String strBuildNumber, String strBuild){
    	ExceptionCode exceptionCode=null;
    	try{
    		String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + "\\";
			}
			System.out.print("File Path:"+ filePath);
    		if(dirType == 3){
    			BufferedInputStream in= null;
    			File file = null;
    			FileOutputStream fos = null;
    			String extension = "";
    			FileInfoVb fileInfoVb = new FileInfoVb();
    			BuildSchedulesVb buildSchedulesVb = getBuildSchedulesDao().getQueryResultsForDetails(strBuildNumber, strBuild);
    			String startTime = buildSchedulesVb.getStartTime();
    			String status = buildSchedulesVb.getBuildScheduleStatus();
    			if(startTime != null && !startTime.isEmpty() && ("E".equalsIgnoreCase(status) ||"I".equalsIgnoreCase(status))){
    				if(startTime.indexOf(" ") >0){
    					startTime = startTime.substring(0,startTime.indexOf(" "));
    				}
    				startTime = startTime.substring(6)+ "-" + startTime.substring(3, 5) + "-" +  startTime.substring(0 , 2);
    				if("ZZ".equalsIgnoreCase(buildSchedulesVb.getCountry()) && buildSchedulesDao.checkExpandFlagFor(buildSchedulesVb) >0){
    					StringBuffer fileName = new StringBuffer();
    					fileName.append(buildSchedulesVb.getBuild()).append("_")
    					.append(buildSchedulesVb.getCountry()).append("_")
    					.append(buildSchedulesVb.getLeBook()).append("_")
    					.append(startTime).append(".zip");
    					StringBuffer command = new StringBuffer();
    					command.append(buildSchedulesVb.getBuild()).append("_")
    					.append("*").append("_")
    					.append(startTime).append(".log");
    					fileInfoVb= new FileInfoVb();
    					fileInfoVb.setName(fileName.toString());
    					in = getLogFiles(fileInfoVb, command.toString());
    					file = new File(filePath + fileInfoVb.getName());
        				fos = new FileOutputStream(file);
    				}else{
    					StringBuffer fileName = new StringBuffer();
    					fileName.append(buildSchedulesVb.getBuild()).append("_")
    					.append(buildSchedulesVb.getCountry()).append("_")
    					.append(buildSchedulesVb.getLeBook()).append("_")
    					.append(startTime).append(".log");
    					extension = ".txt";
    					fileInfoVb.setName(fileName.toString());
    					in = getLogFile(fileInfoVb);
    					file = new File(filePath+fileInfoVb.getName()+extension);
        				fos = new FileOutputStream(file);
    				}
        			int bit = 4096;
        			while((bit) >= 0){
        				bit = in.read();
        				fos.write(bit);
        			}
        			fos.close();
        			exceptionCode = CommonUtils.getResultObject("", 1, "", "");
        			exceptionCode.setResponse(1);
        			exceptionCode.setRequest(fileInfoVb.getName()+extension);
        			in.close();        			
    			}else{
        			exceptionCode = CommonUtils.getResultObject("", Constants.ERRONEOUS_OPERATION, "", "");
        			exceptionCode.setResponse(1);
    			}
    		}else{
    			BufferedInputStream in= null;
    			File file = null;
    			FileOutputStream fos = null;
    			String fName = "";
    			int number = 0;
    			//fileNames = fileNames.substring(0,fileNames.length()-4);
    			String[] arrFileNames =  fileNames.split(" @- ");
    			if(arrFileNames.length == 1){
    				fName = arrFileNames[0]+".txt";
    				file = new File(filePath+fName);
    				fos = new FileOutputStream(file);
    				if("WINDOWS".equalsIgnoreCase(getServerType())){
    					in = downloadFilesFromFTPWindows(fileNames, dirType);
    				}else if(isSecuredFtp())
    					in = downloadFilesFromSFTP(fileNames, dirType);
    				else
    					in = downloadFilesFromFTP(fileNames, dirType);
    				number = 1;
    			}else if(arrFileNames.length != 1){
    				if("WINDOWS".equalsIgnoreCase(getServerType())){
    					in = downloadFilesFromFTPWindows(fileNames, dirType);
    				}else if(isSecuredFtp())
    					in = downloadFilesFromSFTP(fileNames, dirType);
    				else
    					in = downloadFilesFromFTP(fileNames, dirType);
    				file = new File(filePath+"logs.tar");
    				fos = new FileOutputStream(file);
    				fName = "logs.tar";
    				number = 2;
    			}else{
    				throw new FileNotFoundException("File not found on the Server.");
    			}
    			int bit = 4096;
    			while((bit) >= 0){
    				bit = in.read();
    				fos.write(bit);
    			}
    			in.close();
    			fos.close();
    			exceptionCode = CommonUtils.getResultObject("", 1, "", "");
    			exceptionCode.setRequest(fName);
    			exceptionCode.setResponse(number);
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
		return exceptionCode;
    }
	public BufferedInputStream getLogFile(FileInfoVb fileInfoVb){
		FTPClient ftpClient = null;
		BufferedInputStream input = null;
		try{
			setUploadDownloadDirFromDB();
			if(isSecuredFtp()){
				return getLogFileFromSFTP(fileInfoVb);
			}
    		ftpClient = getConnection();
			ftpClient.connect(getHostName());
	    	boolean response = ftpClient.login(getUserName(), getPassword());
	    	if(!response){
	    		logger.error("Unable to login to the FTP Server.");
	        	return null;
	    	}
	    	response = ftpClient.changeWorkingDirectory(buildLogsDir);
	    	if(!response){
	    		logger.error("Unable to login to the FTP Server.");
	        	return null;
	    	}
	    	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	    	input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if(input == null){
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		logger.error("Unable to login to the FTP Server.");    		
        	return null;
		}finally{
    		if(ftpClient != null)
    		try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return input;
	}
	public BufferedInputStream getLogFileFromSFTP(FileInfoVb fileInfoVb){
		BufferedInputStream input = null;
		Session session = null;
		try{
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();  
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession( getUserName(), getHostName() ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword(getPassword()); 
			}  
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp"); 
			channel.connect();  
			ChannelSftp sftpChannel = (ChannelSftp) channel;
	    	sftpChannel.cd(buildLogsDir);
	    	try{
	    		InputStream ins = sftpChannel.get(fileInfoVb.getName());
	    		input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
	    	}catch(SftpException ex){
	    		try{
	    			if(input == null){
	    				InputStream ins = sftpChannel.get("BUILDCRON.log");
	    	    		input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
	    				fileInfoVb.setName("BUILDCRON.log");
	    			}
	    		}catch(SftpException ex1){
	    			
	    		}
	    	}
    	}catch (Exception e) {
    		e.printStackTrace();
    		logger.error("Unable to login to the FTP Server.");    		
        	return null;
		}finally{
    		if(session != null)
    			session.disconnect();
		}
		return input;
	}
	public BufferedInputStream getLogFilesFromSFTP(FileInfoVb fileInfoVb, String command){
		BufferedInputStream input = null;
		Session session = null;
		Channel shellChannel = null;
		try{
			setUploadDownloadDirFromDB();
			JSch jsch = new JSch();  
			jsch.setKnownHosts(getKnownHostsFileName());
			session = jsch.getSession( getUserName(), getHostName() ); 
			{   // "interactive" version   // can selectively update specified known_hosts file    
				// need to implement UserInfo interface  
				// MyUserInfo is a swing implementation provided in    
				//  examples/Sftp.java in the JSch dist   
				UserInfo ui = new MyUserInfo();   
				session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
				session.setPassword(getPassword()); 
			}  
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			
			shellChannel = session.openChannel("shell");  
			OutputStream inputstream_for_the_channel = shellChannel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			shellChannel.connect();
			commander.println("cd "+buildLogsDir);
			commander.println("tar -cvf "+fileInfoVb.getName()+" "+command);
			commander.println("exit");
			commander.close();
			do {Thread.sleep(1000); } while(!shellChannel.isEOF()); 
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(buildLogsDir);
			try{
				InputStream ins = sftpChannel.get(fileInfoVb.getName());
	    		input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
			}catch(SftpException exp){
				try{
					InputStream ins = sftpChannel.get("BUILDCRON.log");
		    		input = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ins)));
					fileInfoVb.setName("BUILDCRON.log");
				}catch(SftpException ex){
					
				}
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		logger.error("Unable to login to the FTP Server.");    		
        	return null;
		}finally{
    		if(session != null)
    			session.disconnect();
		}
		return input;
	}
	public BufferedInputStream getLogFiles(FileInfoVb fileInfoVb, String command){
		FTPClient ftpClient = null;
		TelnetConnection telnetConnection = null;
		BufferedInputStream input = null;
		try{
			setUploadDownloadDirFromDB();
			if(isSecuredFtp()){
				return getLogFilesFromSFTP(fileInfoVb,command);
			}
			telnetConnection = new TelnetConnection(hostName,userName,password, prompt);
			telnetConnection.connect();
			telnetConnection.sendCommand("cd "+buildLogsDir);
			telnetConnection.sendCommand("tar -cvf "+fileInfoVb.getName()+" "+command);
    		ftpClient = getConnection();
			ftpClient.connect(getHostName());
	    	boolean response = ftpClient.login(getUserName(), getPassword());
	    	if(!response){
	    		logger.error("Unable to login to the FTP Server.");
	        	return null;
	    	}
	    	response = ftpClient.changeWorkingDirectory(buildLogsDir);
	    	if(!response){
	    		logger.error("Unable to login to the FTP Server.");
	        	return null;
	    	}
	    	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	    	input = new BufferedInputStream(ftpClient.retrieveFileStream(fileInfoVb.getName()));
			if(input == null){
				input = new BufferedInputStream(ftpClient.retrieveFileStream("BUILDCRON.log"));
				fileInfoVb.setName("BUILDCRON.log");
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		logger.error("Unable to login to the FTP Server.");    		
        	return null;
		}finally{
    		if(ftpClient != null)
    		try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(telnetConnection != null && telnetConnection.isConnected()){
				telnetConnection.disconnect();
			}
		}
		return input;
	}
	private FTPFile isFileExists(String strFileName, FTPClient ftpClient) throws IOException{
		FTPFile[] tmp = ftpClient.listFiles(strFileName);
		if(tmp != null && tmp.length >0)
   			return tmp[0];
    	return null;
    }
	private ChannelSftp.LsEntry isFileExists(String strFileName, ChannelSftp ftpClient) throws SftpException{
		try{
			Vector<ChannelSftp.LsEntry> lvec = ftpClient.ls(strFileName);
			return(lvec != null && lvec.size() >0) ? lvec.get(0): null;
		}catch(SftpException e){
		}
		return null;
    }
	@Override
	protected AbstractDao<UploadFilingVb> getScreenDao() {
		return uploadFilingDao;
	}

	@Override
	protected void setAtNtValues(UploadFilingVb object) {
		object.setFilingStatusNt(1011);
	}

	@Override
	protected void setVerifReqDeleteType(UploadFilingVb object) {
		object.setVerificationRequired(false);
		object.setStaticDelete(false);
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUploadDir() {
		return uploadDir;
	}
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
	public String getDownloadDir() {
		return downloadDir;
	}
	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public String getTimezoneId() {
		return timezoneId;
	}
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}
	public String getDateFormate() {
		return dateFormate;
	}
	public void setDateFormate(String dateFormate) {
		this.dateFormate = dateFormate;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getBuildLogsDir() {
		return buildLogsDir;
	}
	public void setBuildLogsDir(String buildLogsDir) {
		this.buildLogsDir = buildLogsDir;
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public BuildSchedulesDao getBuildSchedulesDao() {
		return buildSchedulesDao;
	}
	public void setBuildSchedulesDao(BuildSchedulesDao buildSchedulesDao) {
		this.buildSchedulesDao = buildSchedulesDao;
	}
	public boolean isSecuredFtp() {
		return securedFtp;
	}
	public void setSecuredFtp(boolean securedFtp) {
		this.securedFtp = securedFtp;
	}
	public WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}
	public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}
	public String getKnownHostsFileName() {
		return knownHostsFileName;
	}
	public void setKnownHostsFileName(String knownHostsFileName) {
		this.knownHostsFileName = knownHostsFileName;
	}
	/*public class MyUserInfo implements UserInfo{
	    public String getPassword(){ return password; }
	    public boolean promptYesNo(String str){
	      return false;
	    }
	    public String getPassphrase(){ return null; }
	    public boolean promptPassphrase(String message){ return true; }
	    public boolean promptPassword(String message){ return false; }
	    
	    public void showMessage(String message){
	      return;
	    }
	}*/
	public char getPrompt() {
		return prompt;
	}
	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}
	public String getProcessDir() {
		return processDir;
	}

	public void setProcessDir(String processDir) {
		this.processDir = processDir;
	}
	public String getFoderDrive() {
		return foderDrive;
	}
	public void setFoderDrive(String foderDrive) {
		this.foderDrive = foderDrive;
	}
	public String getScriptDir() {
		return scriptDir;
	}
	public void setScriptDir(String scriptDir) {
		this.scriptDir = scriptDir;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public UploadFilingDao getUploadFilingDao() {
		return uploadFilingDao;
	}
	public void setUploadFilingDao(UploadFilingDao uploadFilingDao) {
		this.uploadFilingDao = uploadFilingDao;
	}
	
	/*
	 * private String genrateReport(UploadFilingVb uploadFilingVb,
	 * List<TemplateStgVb> reportsStg,String reportId){ Map<String , Object> map =
	 * prepareData(uploadFilingVb, reportsStg); String result =
	 * VelocityEngineUtils.mergeTemplateIntoString(getVelocityEngine(),
	 * "com/vision/vm/BLS0500.vm", map); return result; }
	 */
	//private Map<String , Object> prepareData(UploadFilingVb uploadFilingVb, List<PromptIdsVb> prompts, List<TemplateStgVb> reportsStg){
	private Map<String , Object> prepareData(UploadFilingVb uploadFilingVb, List<TemplateStgVb> reportsStg){
		Map<String , Object> map = new HashMap<String, Object>();
		ArrayList<XbrlVb> xbrlMappinglst = new ArrayList<XbrlVb>();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM-RRRR");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("RRRR-MM-dd");
		SimpleDateFormat formatter3 = new SimpleDateFormat("dd-MMM-RRRR");
		//ExceptionCode exceptionCode1= null;
		//exceptionCode1=createXBLRReport(uploadFilingVb, prompts);

		Date date = new Date();
		
		Date endDate = null;
		Date startDate = null;
		Date prevDate = null;
		Date halfStartDate = null;
		Date quatStartDate = null;
		Date PrevQuatEndDate= null;
		Date PrevHalfEndDate =null;
		Date fortNigtDate =null;
		Calendar cal = Calendar.getInstance();
		Boolean dailyRep = false;
		
		
		startDate = date;
		endDate = date;
		prevDate = date;
		quatStartDate = date;
		PrevQuatEndDate = date;
		PrevHalfEndDate  = date;
		fortNigtDate = date;
		
		//xbrlMappinglst = getReportWriterDao(). getXbrlContext(uploadFilingVb);
		
//	    map.put("bankName", getReportWriterDao().findVisionVariableValueLoc("BANK_NAME"));
//		map.put("bankTitle", getReportWriterDao().findVisionVariableValueLoc("BANK_TITLE"));
//		map.put("version", getReportWriterDao().findVisionVariableValueLoc("VISION_VERSION"));
		
		map.put("startDate", formatter1.format(startDate));
		map.put("startDateORCLFrmt", formatter2.format(startDate));
		map.put("endDate", formatter1.format(endDate));
		map.put("endDateORCLFrmt", formatter2.format(endDate));
		map.put("prevDate", formatter1.format(prevDate));
		map.put("prevDateORCLFrmt", formatter2.format(prevDate));
		
/*		map.put("quatStartDate", formatter1.format(quatStartDate));
		map.put("quatStartDateORCLFrmt", formatter2.format(quatStartDate));
		map.put("quatEndDate", formatter1.format(endDate));
		map.put("quatEndDateORCLFrmt", formatter2.format(endDate));
		map.put("PrevQuatEndDate", formatter1.format(PrevQuatEndDate));
		map.put("PrevQuatEndDateORCLFrmt", formatter2.format(PrevQuatEndDate));*/
		
/*		map.put("halfStartDate", formatter1.format(halfStartDate));
		map.put("halfStartDateORCLFrmt", formatter2.format(halfStartDate));
		map.put("halfEndDate", formatter1.format(endDate));
		map.put("halfEndDateORCLFrmt", formatter2.format(endDate));
		map.put("PrevHalfEndDate", formatter1.format(PrevHalfEndDate));
		map.put("PrevHalfEndDateORCLFrmt", formatter2.format(PrevHalfEndDate));
		map.put("fortNigtDate",formatter1.format(fortNigtDate));
		map.put("fortNigtDateORCLFrmt",formatter2.format(fortNigtDate));*/
		
		map.put("uploadFilingVb", uploadFilingVb);
		map.put("reportsStgList", reportsStg);
		map.put("xbrlMappinglst", xbrlMappinglst);
		Boolean endDateFlag = false;
		int recCounterTemp1 = 0;
		int recCounterTemp2 = 0;
		int recCounterTemp3 = 0;
/*		if(reportsStg != null && reportsStg.size() > 0){
			for(int  ctr = 0;ctr < reportsStg.size();ctr++){
				TemplateStgVb tempVb = reportsStg.get(ctr);
				if( "7".equalsIgnoreCase(tempVb.getRowId())){
//					xbrlEndDate =  tempVb.getCellData();
					endDateFlag = true;
				}
				if("0".equalsIgnoreCase(tempVb.getColId()) && "14".equalsIgnoreCase(tempVb.getTemplateName())){
					recCounterTemp1++;
				}
				if("0".equalsIgnoreCase(tempVb.getColId()) && "15".equalsIgnoreCase(tempVb.getTemplateName())){
					recCounterTemp2++;
				}
				if("0".equalsIgnoreCase(tempVb.getColId()) && "16".equalsIgnoreCase(tempVb.getTemplateName())){
					recCounterTemp3++;
				}
			}
		}
		map.put("recCounterTemp1", recCounterTemp1);
		map.put("recCounterTemp2", recCounterTemp2);
		map.put("recCounterTemp3", recCounterTemp3);*/
			
		Date myDate = new Date();
		/*if(dailyRep)
			xbrlEndDate = new SimpleDateFormat("RRRR-MM-dd").format(startDate);
		else
			xbrlEndDate = new SimpleDateFormat("RRRR-MM-dd").format(endDate);*/
		return map;
	}
	
	public ExceptionCode createXBLRReport(UploadFilingVb uploadFilingVb){
		ExceptionCode exceptionCode = null;
		FileOutputStream fileOS = null;
		try{
			
			String filePattern = getUploadFilingDao().getXBRLFileName(uploadFilingVb);
			
			logger.info("Started the extraction for XBRL");
			
			uploadFilingVb.setSessionId("SC_XBRL_UPLOAD");
			logger.info("End extraction for XBLR");
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			String xbrlFilePath = getVisionVariableValue("NON_ADF_FILE_UPLOAD_PATH");
	    	if(xbrlFilePath != null && !xbrlFilePath.isEmpty()){
	    		String[] buildLogPath = xbrlFilePath.split("\\\\");
	    		int length = buildLogPath.length;
	    		xbrlFilePath = buildLogPath[length-1];
	    	}else{
	    		xbrlFilePath = "/home/vision/NON_ADF_XL_UPLOAD_PATH/";	    		
	    	}
//		    File xbrlFile = new File(xbrlFilePath+"RW402_GLMAPS_DLY_01-NOV-2016"+".xml");
			File xbrlFile = new File(xbrlFilePath+filePattern+".xml");
//			File lFileZip = new File(filePath+ValidationUtil.encode(uploadFilingVb.getTemplateName())+"_"+SessionContextHolder.getContext().getVisionId()+".zip");
			
		    File lFile = new File(filePath+ValidationUtil.encode(uploadFilingVb.getTemplateName())+".xml");
//			File lFileZip = new File(filePath+ValidationUtil.encode(uploadFilingVb.getTemplateName())+"_"+SessionContextHolder.getContext().getVisionId()+".zip");
			
			if(lFile.exists()){
				//lFile.delete();
			}
			
			fileOS = new FileOutputStream(lFile);
//			fileOS.write(data.getBytes());
//			FileOutputStream fos = new FileOutputStream(lFileZip);
//			ZipOutputStream zos = new ZipOutputStream(fos);
//			ZipEntry ze = new ZipEntry(lFile.getName());
//			zos.putNextEntry(ze);
			
			FileInputStream fis = new FileInputStream(xbrlFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
//                zos.write(buffer, 0, len);
                fileOS.write(buffer, 0, len);
            }
//			zos.closeEntry();
//			zos.close();
//			fos.close();
			fis.close();
		}catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeCustomException(e.getMessage());
		}finally{
			try{
				//getReportWriterDao().callProcToCleanUpTables(uploadFilingVb);
				if(fileOS != null){
					fileOS.flush();
				    fileOS.close();
				    fileOS = null;
				}
			}catch (Exception ex){}													
		}
		exceptionCode =  CommonUtils.getResultObject("", 1, "", "");
		exceptionCode.setRequest(uploadFilingVb.getTemplateName());
		//exceptionCode.setRequest(xbrlEndDate);
		return exceptionCode;
	}
	/*public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}
	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}*/
	public ExceptionCode getUploadFillingQueryResults(UploadFilingVb vObject){
		setVerifReqDeleteType(vObject);
		/*vObject.setBusinessDate(getUploadFilingDao().getBusinessDate(vObject.getCountry(), vObject.getLeBook())); */
		List<UploadFilingVb> collTemp = getUploadFilingDao().getUploadFillingQueryResults(vObject);
/*		doSetDesctiptionsAfterQuery(collTemp);
		ExceptionCode exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Query", "");
		exceptionCode.setOtherInfo(vObject);
		exceptionCode.setResponse(collTemp);*/
		
		if(collTemp.size() == 0){
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 41, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}else{
			doSetDesctiptionsAfterQuery(collTemp);
			doSetDesctiptionsAfterQuery(vObject);
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(collTemp);
			return exceptionCode;
		}
	}
	
	
	class NonADFProcesser implements Runnable{
			private UploadFilingVb vObject = null;
			String adfAdFilePath = null;
			String adfLogPath = null;
			String adfFileUploadPath =null;
			String unixScriptDir = null;
			NonADFProcesser(UploadFilingVb process, String pAdfAdFilePath, String pAdfLogPath, String pAdfFileUploadPath, String pUnixScriptDir){
				super();
				vObject = process;
				adfAdFilePath =pAdfAdFilePath;
				adfLogPath = pAdfLogPath;
				adfFileUploadPath = pAdfFileUploadPath;
				unixScriptDir =pUnixScriptDir;
			}
			private void logMessage(String message){
				logger.info(message+"for Country["+vObject.getCountry()+"], LEBook["+vObject.getLeBook()+"], File Name["+vObject.getFileName()+"], "
						+ "Template Name["+vObject.getTemplateName()+"],Business Date["+vObject.getBusinessDate()+"] at "+System.currentTimeMillis());
			}
			public void run(){
				
				logMessage("Non ADFProcess Started");
				List<UploadFilingVb> result = getUploadFilingDao().getDataFromProcessControl(vObject);
				String strDBDate = null;
				long maker = SessionContextHolder.getContext().getVisionId();
				try{
					
				
							vObject = result.get(0);
							
						    strDBDate = getUploadFilingDao().getSystemDate();
							ADFAcqVersionsVb aDFAcqVersionsVb = new ADFAcqVersionsVb();
							aDFAcqVersionsVb.setCountry(vObject.getCountry());
							aDFAcqVersionsVb.setLeBook(vObject.getLeBook());
							aDFAcqVersionsVb.setAcquisitionProcessType(vObject.getAcquisitionProcessType());
							aDFAcqVersionsVb.setBusinessDate(vObject.getBusinessDate());
							aDFAcqVersionsVb.setFeedDate(vObject.getBusinessDate()); 
							aDFAcqVersionsVb.setTemplateName(vObject.getTemplateName());
							aDFAcqVersionsVb.setUplStartTime(strDBDate);
							aDFAcqVersionsVb.setProcessStartTime(strDBDate);
							aDFAcqVersionsVb.setVerificationRequired(false);
							aDFAcqVersionsVb.setBusinessDate(vObject.getBusinessDate());
							aDFAcqVersionsVb.setMaker(maker);
							aDFAcqVersionsVb.setVerifier(maker);
							aDFAcqVersionsVb.setRecordIndicator(0);
							aDFAcqVersionsVb.setPreActivityScriptType(vObject.getPreActivityScriptType());
							aDFAcqVersionsVb.setPreActivityScript(vObject.getPreActivityScript());
							aDFAcqVersionsVb.setIntegrityScriptType(vObject.getIntegrityScriptType());
							aDFAcqVersionsVb.setIntegrityScriptName(vObject.getIntegrityScriptName());
							aDFAcqVersionsVb.setFrequencyProcess(vObject.getFrequencyProcess());
							
							
							
							int retVal = getUploadFilingDao().insertUpdateAdfAcqVersion(aDFAcqVersionsVb);
							vObject.setVersionNo(aDFAcqVersionsVb.getVersionNo());
							if(retVal != Constants.SUCCESSFUL_OPERATION){
								
								//TODO: Update the Upload Filling Table with the status failure
								getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "12");
								getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTERR");
								return;
							}
				}catch(Exception e){
					getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "12");
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTERR");
					e.printStackTrace();
					return;
				}
				
				logMessage("Insert/Update to Version Number completed");
				
				logMessage("Starting the preactivity Process");
				try{
				
						int retVal = doPreActivityProcess(vObject, vObject.getVersionNo(), adfAdFilePath, adfLogPath, adfFileUploadPath, unixScriptDir);
						if(retVal != Constants.SUCCESSFUL_OPERATION){
							//TODO: Update the Status in Filling Table for the Pre Activity failed
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "12");
							getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTERR");
							logMessage("Preactivity Process failed.");
							return;
						}else{
							//TODO: Update the Status in Filling Table for the Upload Process Start
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "13");
							getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTCOMP");
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "7");
							getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "UPLIP");
						}
				}catch(Exception e){
					getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "12");
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTERR");
					e.printStackTrace();
					return;
				}
				long intCurrentUserId = SessionContextHolder.getContext().getVisionId();
				/*long maxUploadSequence = getUploadFilingDao().getMaxSequence();*/
				try{
						//Try inserting the record in Vision upload
						vObject.setUploadStatus(1);
					    vObject.setMaker(intCurrentUserId);
					    vObject.setDateCreation(strDBDate);
					    vObject.setDateLastModified(strDBDate);
					    /*vObject.setUploadSequence(++maxUploadSequence+"");*/
					    vObject.setVisionTableUploadDate(strDBDate);
					    vObject.setMaker(maker);
					    vObject.setVerifier(maker);
					    int retVal = getUploadFilingDao().doInsertionApprUpload(vObject); 
						if (retVal != Constants.SUCCESSFUL_OPERATION)
						{
							//TODO: Update the Upload Filling Table with the status failure and the version table
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "8");
							getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "UPLERR");
							String sysDate=getUploadFilingDao().getSysteDateForAdfProcess();
							getUploadFilingDao().updateStausOfVersionTable(vObject,PROCESS_END_TIME, sysDate);
							
							return;
						}else{
							//TODO: Update the Upload Filling to InProgress	
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "7");
							getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "UPLIP");
						}
				}catch(Exception e){
					getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "8");
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "UPLERR");
					String sysDate=getUploadFilingDao().getSysteDateForAdfProcess();
					getUploadFilingDao().updateStausOfVersionTable(vObject,PROCESS_END_TIME, sysDate);
					e.printStackTrace();
					return;
				}
				boolean uploadCompleted = false;
				long uploadStatus = 0;
				while(!uploadCompleted){
					uploadStatus = getUploadFilingDao().getUploadStatus(vObject);
					if(uploadStatus != 1 && uploadStatus != 2){
						uploadCompleted = true;
					}
				}
				if(uploadStatus != 4){
					//TODO: Update the Filling Table and Version table with the status upload  error
					getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "8");
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "UPLERR");
					logMessage("File Upload Failed. Please check the log for details");
					return;
				}else{
					//TODO: Update the Filling Table and Version table with the status Pre Activity Start
					getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "14");
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTIP");
				}
				
			
				logMessage("Integrity Process Started.");
				try{
					int retVal = doIntegrityProcess(vObject, vObject.getVersionNo(), adfAdFilePath, adfLogPath, adfFileUploadPath, unixScriptDir);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						//TODO: Update the Status in Filling Table for the Integrity Process  failed
						/*getUploadFilingDao().updateStausOfEachProcess(vObject, ACQUISITION_STATUS, "COMP");*/
						String processEndTime =  getUploadFilingDao().getSysteDateForAdfProcess();
						getUploadFilingDao().updateStausOfVersionTable(vObject, "PROCESS_END_TIME", processEndTime);
						logMessage("Integrity Process failed.");
						return;
					}else{
						//TODO: Update the Status in Filling Table for the Integrity Process Success
						/*getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "16");
						getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMP");*/
						String processEndTime =  getUploadFilingDao().getSysteDateForAdfProcess();
						getUploadFilingDao().updateStausOfVersionTable(vObject, "PROCESS_END_TIME", processEndTime);
						logMessage("Integrity Process Completed.");
					}
				}catch(Exception e){
					getUploadFilingDao().updateStausOfEachProcess(vObject, ACQUISITION_STATUS, "COMP");
					String processEndTime =  getUploadFilingDao().getSysteDateForAdfProcess();
					getUploadFilingDao().updateStausOfVersionTable(vObject, "PROCESS_END_TIME", processEndTime);
					logMessage("Integrity Process failed.");
					e.printStackTrace();
					return;
				}
				
			}
			private int doPreActivityProcess(UploadFilingVb vObject,long versionNo,String adfAdFilePath,String adfLogPath,String adfFileUploadPath,String unixScriptDir){
				
				String preActivityScripts = vObject.getPreActivityScript();
				String scriptType = "UNIXSCRIPT";
				if("MACROVAR".equalsIgnoreCase(vObject.getPreActivityScriptType())){
					List<String> hashVariableList = getUploadFilingDao().getDynamicHashVariable(preActivityScripts);
					if(hashVariableList != null && !hashVariableList.isEmpty()){
						preActivityScripts = hashVariableList.get(0);
						scriptType = hashVariableList.get(1);
					}
				}
				if(ValidationUtil.isValid(preActivityScripts)){
					if("UNIXSCRIPT".equalsIgnoreCase(scriptType)){
						preActivityScripts = replaceMacroValuesInFilePattern(vObject,adfAdFilePath,adfLogPath,adfFileUploadPath,preActivityScripts);
						if("Y".equalsIgnoreCase(vObject.getDebugMode()))
							getUploadFilingDao().insetAuditTrialData(vObject,"Executing Pre Activity Script", "Executing Pre Activity Script["+preActivityScripts+"]");
						logger.info("Executing Pre Activity Script["+preActivityScripts+"] for LE Book["+vObject.getLeBook()+"]");
						System.out.println("Executing Pre Activity Script["+preActivityScripts+"] for LE Book["+vObject.getLeBook()+"]");
						
						System.out.println("appFtpUserName["+appFtpUserName+"], appFtpPassword["+appFtpPassword+"], appFtpHostName["+appFtpHostName+"] "
								+ "preActivityScripts["+preActivityScripts+"],unixScriptDir["+unixScriptDir+"]");
						int retVal = execUnxComAndGetResult(appFtpUserName, appFtpPassword, appFtpHostName, preActivityScripts, unixScriptDir);
						System.out.println("retVal of PreExtraction Process:"+retVal+":");
						if(retVal != 0){
							// Failed Executing Script
							retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "PREEXTERR");
							if("Y".equalsIgnoreCase(vObject.getDebugMode())){
								retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Pre Activity Script Execute Failed", "Pre Activity Script Execute Failed ["+preActivityScripts+"]");
								return Constants.ERRONEOUS_OPERATION;
							}else{
								return Constants.ERRONEOUS_OPERATION;
							}
						}
					}
				}
					return Constants.SUCCESSFUL_OPERATION;
			}
			private String changeDateFormat(String oldDateString){
				String OLD_FORMAT = "dd-MM-yyyy";
				String NEW_FORMAT = "dd-MMM-yyyy";
				try {
					System.out.println("String OLD_FORMAT "+oldDateString);
				SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
				Date d = sdf.parse(oldDateString);
				sdf.applyPattern(NEW_FORMAT);
				return sdf.format(d).toUpperCase();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return oldDateString;
			}
				
				private String replaceMacroValuesInFilePattern(UploadFilingVb vObject,String adfAdFilePath,String adfLogPath, String adfFileUploadPath, String source){
					String procedureValue = source;
					
				if(vObject.getFilePattern().indexOf("#LE_BOOK#") > 0){
					procedureValue = procedureValue.replaceAll("#LE_BOOK#",vObject.getLeBook());
				}
				if(source.indexOf("#VERSION_NO#") > 0){
					long version = ((vObject.getVersionNo()- 1) == -1 ? 0 : vObject.getVersionNo()- 1);
					procedureValue = procedureValue.replaceAll("#VERSION_NO#",""+version);
				}
				if(source.indexOf("#AD_FILE_PATH#") > 0){
					procedureValue = procedureValue.replaceAll("#AD_FILE_PATH#", adfAdFilePath);
				}		
				if(source.indexOf("#BUSINESS_DATE#") > 0){
					procedureValue = procedureValue.replaceAll("#BUSINESS_DATE#",changeDateFormat(vObject.getBusinessDate()));
				}
				if(source.indexOf("#PROCESS_FREQ#") > 0){
					procedureValue = procedureValue.replaceAll("#PROCESS_FREQ#",vObject.getFrequencyProcess());
				}
				if(source.indexOf("#TEMPLATE_NAME#") > 0){
					procedureValue = procedureValue.replaceAll("#TEMPLATE_NAME#",vObject.getTemplateName());	
				}
				if(source.indexOf("#DEBUG_MODE#") > 0){
					procedureValue = procedureValue.replaceAll("#DEBUG_MODE#",vObject.getDebugMode());	
				}
				if(source.indexOf("#LOG_PATH#") > 0){
					procedureValue = procedureValue.replaceAll("#LOG_PATH#",adfLogPath);	
				}
				if(source.indexOf("#COUNTRY#") > 0){
					procedureValue = procedureValue.replaceAll("#COUNTRY#",vObject.getCountry());	
				}
			
				if(source.indexOf("#FEED_DATE#") > 0){
					procedureValue = procedureValue.replaceAll("#FEED_DATE#",changeDateFormat(vObject.getBusinessDate()));	
				}
				
				if(source.indexOf("#TABLE_NAME#") > 0){
						procedureValue = procedureValue.replaceAll("#TABLE_NAME#",vObject.getFeedStgName());	
				}
				if(source.indexOf("#FILE_PATH#") > 0){
					procedureValue = procedureValue.replaceAll("#FILE_PATH#",adfFileUploadPath);
				}
				if(source.indexOf("#FILE_NAME#") > 0){
					procedureValue = procedureValue.replaceAll("#FILE_NAME#",vObject.getFileName());
				}
				if(source.indexOf("#FEED_CATEGORY#") > 0 ){
					procedureValue = procedureValue.replaceAll("#FEED_CATEGORY#",vObject.getFeedCategory());
				}
				if(source.indexOf("#PROGRAM#") > 0 ){
					procedureValue = procedureValue.replaceAll("#PROGRAM#",vObject.getProgram());
				}
				if(source.indexOf("#DEFAULT_ACQ_PROCESS_TYPE#") > 0 ){
					procedureValue = procedureValue.replaceAll("#DEFAULT_ACQ_PROCESS_TYPE#",vObject.getDefaultAcqProcessType());
				}		
				return procedureValue;
			}
			private int doIntegrityProcess(UploadFilingVb vObject,long versionNo,String adfAdFilePath,String adfLogPath,String adfFileUploadPath,String unixScriptDir){
				String integrityScriptName1 = vObject.getIntegrityScriptName();
				int retVal;
				if(ValidationUtil.isValid(integrityScriptName1)){
			//			String integrityStartTime = getSysteDateForAdfProcess();
			//			retVal = updateAcqVesionStatus("INT_START_TIME", integrityStartTime);
					String scriptType = vObject.getIntegrityScriptType();
					if("MACROVAR".equalsIgnoreCase(scriptType)){
						List<String> hashVariableList = getUploadFilingDao().getDynamicHashVariable(integrityScriptName1);
						if(hashVariableList != null && !hashVariableList.isEmpty()){
							integrityScriptName1 = hashVariableList.get(0);
							scriptType = hashVariableList.get(1);
						}
					}
					String integrityScriptName = replaceMacroValuesInFilePattern(vObject,adfAdFilePath,adfLogPath,adfFileUploadPath,integrityScriptName1);
					vObject.setIntegrityScriptName(integrityScriptName);
					// Run the Unix script for DBP
					if("UNIXSCRIPT".equalsIgnoreCase(scriptType)){
						getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "14");
						retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTIP");
						if("Y".equalsIgnoreCase(vObject.getDebugMode()))
							retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Executing Integrity Script", "Executing Integrity Script["+integrityScriptName+"]");
						System.out.println("Executing integrity Script["+integrityScriptName+"] for LE Book["+vObject.getLeBook()+"]");
						
						System.out.println("appFtpUserName["+appFtpUserName+"], appFtpPassword["+appFtpPassword+"], appFtpHostName["+appFtpHostName+"] "
								+ "integrityScripts["+integrityScriptName+"],unixScriptDir["+unixScriptDir+"]");

						int  integrityReturnValue = execUnxComAndGetResult(appFtpUserName, appFtpPassword, appFtpHostName, integrityScriptName, unixScriptDir);
						System.out.println("retVal of PreExtraction Process:"+integrityReturnValue+":");
						if(integrityReturnValue == 0){
							// Fetch data from VISION_DBP_ERRORS table and insert into Audit Trail Table
							// Update ACQUISITION_STATUS to Completed with Errors.
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "16");
							retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTCOMP");
							if("Y".equalsIgnoreCase(vObject.getDebugMode()))
								retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Integrity Script Execute Successfully", "Integrity Script Execute Successfully");
							
							List<String> dbpErrorsList = getUploadFilingDao().getCountOfErrorsInDBP(vObject);
							if(dbpErrorsList!=null && !dbpErrorsList.isEmpty()){
								String errorCount = dbpErrorsList.get(0);
								String errorType = dbpErrorsList.get(1);
								if(Integer.parseInt(errorCount) != 0){
									if(ValidationUtil.isValid(errorType) && "W".equalsIgnoreCase(errorType)){
										getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "17");
										retVal =getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMPNSERR");
										if("Y".equalsIgnoreCase(vObject.getDebugMode()))
											retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Data Validated with Warings", "Data Validated with Warings :  Count ["+errorCount+"] ");
			//								String integrityEndTime = getSysteDateForAdfProcess();
			//								logger.info("Warings : integrityEndTime : "+integrityEndTime);
			//								retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
										return Constants.ERRONEOUS_OPERATION;
									}else if(ValidationUtil.isValid(errorType) && "S".equalsIgnoreCase(errorType)){
										getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "18");
										retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMPSERR");
										if("Y".equalsIgnoreCase(vObject.getDebugMode()))
											retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Data Validated with Errors", "Data Validated with Errors : Count ["+errorCount+"] for more details see Details in Error Screen");
			//								String integrityEndTime = getSysteDateForAdfProcess();
			//								logger.info("Series Warings : integrityEndTime : "+integrityEndTime);
			//								retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
										return Constants.ERRONEOUS_OPERATION;
									}
								}else{
									getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "19");
									getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMP");
								}
							}
						}else{
							// Failed Executing Script
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "15");
							retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTERR");
							if("Y".equalsIgnoreCase(vObject.getDebugMode()))
								retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Integrity Script Execute Failed", "Integrity Script Execute Failed ["+integrityScriptName+"] : Retrun Value["+integrityReturnValue+"]");
			//					String integrityEndTime = getSysteDateForAdfProcess();
			//					logger.info("integrityEndTime : "+integrityEndTime);
			//					retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
							return Constants.ERRONEOUS_OPERATION;
						}
					}else if("PLSQL".equalsIgnoreCase(scriptType)){
						getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "14");
						retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTIP");
						String executeIntegrityScriptName = getUploadFilingDao().replaceMacroValues(vObject,adfAdFilePath,adfLogPath,adfFileUploadPath,vObject.getIntegrityScriptName());
						String integrityStatus = callReadinessProc(vObject,executeIntegrityScriptName,adfAdFilePath,adfLogPath,adfFileUploadPath);
			//				////System.out.println("integrityStatus : "+integrityStatus);
						String[] array = integrityStatus.split("@");
						String status = array[0];
						String ErrorMessage = array[1];
						////System.out.println("status : "+status);
						////System.out.println("ErrorMessage : "+ErrorMessage);
						if("-1".equalsIgnoreCase(status)){
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "15");
							retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTERR");
							if("Y".equalsIgnoreCase(vObject.getDebugMode()))
								retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Integrity Script Execute Failed", "Integrity Script Execute Failed ["+executeIntegrityScriptName+"] Return Value["+status+"]");
			//					String integrityEndTime = getSysteDateForAdfProcess();
			//					logger.info("PLSQL integrityEndTime : "+integrityEndTime);
			//					retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
							return Constants.ERRONEOUS_OPERATION;
						}else{
							getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "16");
							retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "INTCOMP");
							if("Y".equalsIgnoreCase(vObject.getDebugMode()))
								retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Integrity Script Execute Successfully", "Integrity Script Execute Successfully");
							List<String> dbpErrorsList = getUploadFilingDao().getCountOfErrorsInDBP(vObject);
							if(dbpErrorsList!=null && !dbpErrorsList.isEmpty()){
								String errorCount = dbpErrorsList.get(0);
								String errorType = dbpErrorsList.get(1);
								if(Integer.parseInt(errorCount) != 0){
									if(ValidationUtil.isValid(errorType) && "W".equalsIgnoreCase(errorType)){
										getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "17");
										retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMPNSERR");
										if("Y".equalsIgnoreCase(vObject.getDebugMode()))
											retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Data Validated with Warings", "Data Validated with Warings :  Count ["+errorCount+"] ");
			//								String integrityEndTime = getSysteDateForAdfProcess();
			//								retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
										return Constants.ERRONEOUS_OPERATION;
									}else if(ValidationUtil.isValid(errorType) && "S".equalsIgnoreCase(errorType)){
										getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "18");
										retVal = getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMPSERR");
										if("Y".equalsIgnoreCase(vObject.getDebugMode()))
											retVal = getUploadFilingDao().insetAuditTrialData(vObject,"Data Validated with Errors", "Data Validated with Errors : Count ["+errorCount+"] for more details see Details in Error Screen");
			//								String integrityEndTime = getSysteDateForAdfProcess();
			//								retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
										return Constants.ERRONEOUS_OPERATION;
									}
								}else{
									getUploadFilingDao().updateStausOfUploadFiling(vObject,FILING_STATUS, "19");
									getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "COMP");
								}
							}
						}
					}
				}
			//		String integrityEndTime = getSysteDateForAdfProcess();
			//		retVal = updateAcqVesionStatus("INT_END_TIME", integrityEndTime);
				return Constants.SUCCESSFUL_OPERATION;
			}	
			public String callReadinessProc( UploadFilingVb vObject,String acquisitionReadinessScripts,String adfAdFilePath,String adfLogPath,String adfFileUploadPath){
				CallableStatement cs =  null;
				String procedure = "";
				String status = "0";
				String errorMsg = "";
				try{
					
					
					procedure =  getUploadFilingDao().replaceMacroValues(vObject,adfAdFilePath,adfLogPath,adfFileUploadPath,acquisitionReadinessScripts);
			//			int outPrams = StringUtils.countMatches(procedure, "OUTPUT_");
					int outPrams  = 2;
					procedure = procedure.replaceAll("#OUTPUT_STATUS#", "?");
					procedure = procedure.replaceAll("#OUTPUT_ERRORMSG#", "?");
					if("Y".equalsIgnoreCase(vObject.getDebugMode()))
						getUploadFilingDao().insetAuditTrialData(vObject,"Readiness Process : Execute "+vObject.getSourceScriptType()+" is done ", "Readiness Procedure : ["+procedure+"]  ");
					
					List<String> result=getUploadFilingDao().callReadinessProc(outPrams,procedure);
					if(result!=null && result.size()==2){
						status=result.get(0);
						errorMsg=result.get(1);
					}
				    if("-1".equalsIgnoreCase(status)){
			//		    	strErrorDesc = adfProcessProCVb.getErrorMessage();
				    	getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "EXTERR");
						if("Y".equalsIgnoreCase(vObject.getDebugMode()))
							getUploadFilingDao().insetAuditTrialData(vObject,"Readiness Failed", "Readiness Process : Execute Execute "+vObject.getSourceScriptType()+" Failed : ["+procedure+"]  Error : "+errorMsg.trim());
			//		    	adfProcessProCVb.setStatus("-1");
				    }else{
				    	getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_READINESS_FLAG, "Y");
						if("Y".equalsIgnoreCase(vObject.getDebugMode()))
							getUploadFilingDao().insetAuditTrialData(vObject,"Readiness Process : Execute   "+vObject.getSourceScriptType()+" Successfully ", "Execute "+vObject.getSourceScriptType()+" Successfully : ["+procedure+"]");
				    }
				}catch(Exception ex){
					ex.printStackTrace();
			//			strErrorDesc = ex.getMessage().trim();
					if("Y".equalsIgnoreCase(vObject.getDebugMode()))
						getUploadFilingDao().insetAuditTrialData(vObject,"Execute Procedure Failed ", ex.getMessage().trim());
			//			InsetAuditTrialData("Readiness Process : Execute  "+SourceScriptType()+" Failed ", "Readiness Process : Execute Execute "+SourceScriptType()+" Failed : ["+procedure+"]  Error : "+ex.getMessage().trim());
					getUploadFilingDao().updateStausOfEachProcess(vObject,ACQUISITION_STATUS, "EXTERR");
					getUploadFilingDao().updateExtractionErrorCount(vObject);
					return "-1"+"@"+ex.getMessage();
				}finally{
			//			JdbcUtils.closeStatement(cs);
			//			DataSourceUtils.releaseConnection(connection, getDataSource());
					
				}
				return status+"@"+errorMsg;
			}
			private int execUnxComAndGetResult(String user, String password, String host,
			        String command, String scriptDir) {
				int returnVal = -1;
			    int port = 22;
			
			    try {
			        JSch jsch = new JSch();
			//	        logger.info("host : "+host+" user : "+user+" port : "+port+" scriptDir : "+scriptDir +" Excuting Script : "+command);
			        Session session = jsch.getSession(user, host, port);
			        session.setPassword(password);
			        session.setConfig("StrictHostKeyChecking", "no");
			//	        logger.info("Establishing Connection...");
			        session.connect();
			//	        logger.info("Connection established.");
			        Channel channel = session.openChannel("shell");
					OutputStream inputstream_for_the_channel = channel.getOutputStream();
					PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
					channel.connect();
					 InputStream in = channel.getInputStream();
					commander.println("cd "+scriptDir);
					StringBuilder cmd = new StringBuilder();
					cmd.append(command);
			//			logger.info("cmd : "+cmd);
					commander.println(cmd);
					commander.println("exit");
					commander.close();	        
			        byte[] tmp = new byte[1024];
			        while (true) {
			            while (in.available() > 0) {
			                int i = in.read(tmp, 0, 1024);
			                if (i < 0)
			                    break;
			            }
			            if (channel.isClosed()) {
			            	returnVal = channel.getExitStatus();
			                break;
			            }
			            try {
			                Thread.sleep(1000);
			            } catch (Exception e) {
			            	return returnVal;
			            }
			        }
			        channel.disconnect();
			        session.disconnect();
			    }
			
			    catch (Exception e) {
			    	e.printStackTrace();
			    	return returnVal;
			    }
			    return returnVal;
			}
	}
	public String getAppFtpHostName() {
		return appFtpHostName;
	}
	public void setAppFtpHostName(String appFtpHostName) {
		this.appFtpHostName = appFtpHostName;
	}
	public String getAppFtpUserName() {
		return appFtpUserName;
	}
	public void setAppFtpUserName(String appFtpUserName) {
		this.appFtpUserName = appFtpUserName;
	}
	public String getAppFtpPassword() {
		return appFtpPassword;
	}
	public void setAppFtpPassword(String appFtpPassword) {
		this.appFtpPassword = appFtpPassword;
	}
/*	public ExceptionCode exportListDataToCSV(UploadFilingVb uploadFilingVb, int currentUserId){
		ExceptionCode exceptionCode = null;
		long min = 0 ;
		long max = 5000;
		PrintWriter out = null;
		FileWriter fw = null;
		CreateCsv csv = new CreateCsv();
		String csvSeparator = ",";
		String lineSeparator = "\n";
		ArrayList<UploadFilingVb> reportsStg = null;
		try{
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			String trimmedLeBook=getUploadFilingDao().removeDescLeBook(uploadFilingVb.getLeBook());
			fw = new FileWriter(filePath+ValidationUtil.encode(uploadFilingVb.getCountry()+trimmedLeBook+uploadFilingVb.getBusinessDate()+uploadFilingVb.getTemplateName())+"_"+currentUserId+".csv");
//			fw = new FileWriter(filePath+ValidationUtil.encode(uploadFilingVb.getTemplateName())+".csv");
			out = new PrintWriter(fw);

			List<ColumnHeadersVb> columnHeaders = getUploadFilingDao().getColumnHeaders(uploadFilingVb);
			List<String> colTyps = new ArrayList<String>(columnHeaders.size());
			for(ColumnHeadersVb columnHeadersVb:columnHeaders){
					out.print(columnHeadersVb.getCaption().replaceAll("_", " "));
					out.print(csvSeparator);
					colTyps.add(columnHeadersVb.getColType());
			}
			out.print(lineSeparator);
			out.print("S.No");
			out.print(csvSeparator);
			out.print("Error Description");
			out.print(lineSeparator);
			if("UPLRECERR".equalsIgnoreCase(uploadFilingVb.getAcquisitionStatus())){
				reportsStg = (ArrayList<UploadFilingVb>) getUploadFilingDao().getUploadErrors(uploadFilingVb);
			}else{
				reportsStg = (ArrayList<UploadFilingVb>) getUploadFilingDao().getIntegrityErrors(uploadFilingVb);
			}
			if(reportsStg!=null && !reportsStg.isEmpty()){
				for(UploadFilingVb uploVb : reportsStg){
					for(int headerCount = 0; headerCount<2; headerCount++){
						if(headerCount==0)
							out.print(uploVb.getUploadSequence());
						else
							out.print(uploVb.getMakerName());
						out.print(csvSeparator);
					}
					out.print(lineSeparator);
				}
			}
			
			out.flush();
			out.close();
			fw.close();
			exceptionCode = CommonUtils.getResultObject(getUploadFilingDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		}catch(RuntimeCustomException rex){
			logger.error("Export Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(uploadFilingVb);
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return CommonUtils.getResultObject("", Constants.ERRONEOUS_OPERATION, "Query", e.getMessage());
		}
		return exceptionCode;
	}	*/
	
	public ExceptionCode exportListDataToCSV(UploadFilingVb uploadFilingVb, int currentUserId){
		ExceptionCode exceptionCode = null;
		long min = 0 ;
		long max = 5000;
		PrintWriter out = null;
		FileWriter fw = null;
		CreateCsv csv = new CreateCsv();
		String csvSeparator = ",";
		String lineSeparator = "\n";
		try{
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			String trimmedLeBook=getUploadFilingDao().removeDescLeBook(uploadFilingVb.getLeBook());
			String legalVehcile = getUploadFilingDao().getLegalVehicle(uploadFilingVb);
			fw = new FileWriter(filePath+ValidationUtil.encode(uploadFilingVb.getCountry()+trimmedLeBook+uploadFilingVb.getBusinessDate()+uploadFilingVb.getTemplateName())+"_"+currentUserId+".csv");			
			out = new PrintWriter(fw);
			PromptTreeVb promptTree = getUploadFilingDao().callProcToPopulateListReportData(uploadFilingVb, legalVehcile, currentUserId);
			if(promptTree == null || "1".equalsIgnoreCase(promptTree.getStatus())){
				exceptionCode = CommonUtils.getResultObject(getUploadFilingDao().getServiceName(), Constants.NO_RECORDS_FOUND, "Query", "");
				exceptionCode.setOtherInfo(uploadFilingVb);
				return exceptionCode;
			}
			List<ColumnHeadersVb> columnHeadersTemp = getUploadFilingDao().getColumnHeaders(uploadFilingVb.getExportReportId(), "SC"+currentUserId);
			List<ColumnHeadersVb> columnHeaders = new ArrayList<ColumnHeadersVb>(); 
			for(ColumnHeadersVb columnHeadersVb:columnHeadersTemp){
				if(!"DD_KEY_ID".equalsIgnoreCase(columnHeadersVb.getCaption()) 
						&& !"DD_KEY_VALUE".equalsIgnoreCase(columnHeadersVb.getCaption()) 
						&& !"SORT_FIELD".equalsIgnoreCase(columnHeadersVb.getCaption()))
							columnHeaders.add(columnHeadersVb);
			}
			List<String> colTyps = new ArrayList<String>(columnHeaders.size());
			for(ColumnHeadersVb columnHeadersVb:columnHeaders){
					out.print(columnHeadersVb.getCaption().replaceAll("_", " "));
					out.print(csvSeparator);
					colTyps.add(columnHeadersVb.getColType());
			}
			out.print(lineSeparator);
			String reportsStg = getUploadFilingDao().getListReportDataForExportAsXMLString(promptTree, columnHeaders, min, max);
			do{
				csv.writeDataToCSV(reportsStg, out, csvSeparator, lineSeparator, colTyps );
				min = max;
				max += 5000;
				reportsStg = getUploadFilingDao().getListReportDataForExportAsXMLString(promptTree, columnHeaders, min, max);
			}while(!"<tableData></tableData>".equalsIgnoreCase(reportsStg));
			out.flush();
			out.close();
			fw.close();
			getUploadFilingDao().callProcToCleanUpTables(uploadFilingVb);
			exceptionCode = CommonUtils.getResultObject(getUploadFilingDao().getServiceName(), Constants.SUCCESSFUL_OPERATION, "Query", "");
		}catch(RuntimeCustomException rex){
			logger.error("Export Exception " + rex.getCode().getErrorMsg());
			logger.info("Export Exception " + rex.getCode().getErrorMsg());
			getUploadFilingDao().callProcToCleanUpTables(uploadFilingVb);
			exceptionCode = rex.getCode();
			exceptionCode.setOtherInfo(uploadFilingVb);
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return CommonUtils.getResultObject("", Constants.ERRONEOUS_OPERATION, "Query", e.getMessage());
		}
		return exceptionCode;
	}
	
	public List<PromptIdsVb> creatPromptsId(UploadFilingVb uploadFilingVb,String legalVehcileDes,String frequencyProcessDesc, String majorBuild){
		List<PromptIdsVb> list = new ArrayList<PromptIdsVb>();
		PromptIdsVb promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Legal Vehicle");
		promptIdsVb.setPromptDesc(legalVehcileDes);
		list.add(promptIdsVb);
		
		promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Business Date");
		promptIdsVb.setPromptDesc(uploadFilingVb.getBusinessDate());
		list.add(promptIdsVb);
		
		promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Feed Date");
		promptIdsVb.setPromptDesc(uploadFilingVb.getFeedDate());
		list.add(promptIdsVb);
		
		promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Process Frequencey");
		promptIdsVb.setPromptDesc(frequencyProcessDesc);
		list.add(promptIdsVb);
		
		promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Major Build");
		promptIdsVb.setPromptDesc(majorBuild);
		list.add(promptIdsVb);		
		
		promptIdsVb = new PromptIdsVb();
		promptIdsVb.setPromptType("COMBO");
		promptIdsVb.setPromptString("Template Name");
		if(uploadFilingVb.getTemplateName().indexOf(",")>0)
			promptIdsVb.setPromptDesc("All");
		else {
			String desc = getUploadFilingDao().getTemplateNameDesc(uploadFilingVb.getTemplateName());
			if(ValidationUtil.isValid(desc))
				promptIdsVb.setPromptDesc(desc);
			else
				promptIdsVb.setPromptDesc(uploadFilingVb.getTemplateName());
		}
		list.add(promptIdsVb);
		
		return list;
	}
	public ExceptionCode listExportToXls(UploadFilingVb uploadFilingVb, int currentUserId){
		FileOutputStream fileOS = null;
		String filePath = "";
		String fileName ="";
		try{
			String trimmedLeBook=getUploadFilingDao().removeDescLeBook(uploadFilingVb.getLeBook()).toUpperCase();
			String legalVehcile = getUploadFilingDao().getLegalVehicle(uploadFilingVb);
			String legalVehcileDes = getUploadFilingDao().getLegalVehicleDesc(legalVehcile);
			String frequencyProcessDesc = getUploadFilingDao().getAlphaSubTabDescription(1077, uploadFilingVb.getFrequencyProcess());
			
			String majorBuild = getUploadFilingDao().getMajorBuildDesc(uploadFilingVb.getMajorBuild());
			System.out.println("majorBuild Desc : "+majorBuild);
			
			PromptTreeVb promptTree = getUploadFilingDao().callProcToPopulateListReportData(uploadFilingVb, legalVehcile, currentUserId);			
			List<ColumnHeadersVb> columnHeadersTemp = getUploadFilingDao().getColumnHeaders(uploadFilingVb.getExportReportId(), "SC"+currentUserId);
			List<ColumnHeadersVb> columnHeaders = new ArrayList<ColumnHeadersVb>();
			for(ColumnHeadersVb columnHeadersVb:columnHeadersTemp){
				if(!"DD_KEY_ID".equalsIgnoreCase(columnHeadersVb.getCaption()) && 
					!"DD_KEY_VALUE".equalsIgnoreCase(columnHeadersVb.getCaption()) && 
						!"SORT_FIELD".equalsIgnoreCase(columnHeadersVb.getCaption()))
							columnHeaders.add(columnHeadersVb);
			}
			columnHeadersTemp = null;
			Map<Integer,Integer> columnWidths = new HashMap<Integer,Integer>(columnHeaders.size());
			List<String> colTyps = new ArrayList<String>(columnHeaders.size());
			for(int loopCnt= 0; loopCnt < columnHeaders.size(); loopCnt++){
				ColumnHeadersVb columnHeadersVb = columnHeaders.get(loopCnt); 
				colTyps.add(columnHeadersVb.getColType());
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
			}
			filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			File lFile = null;
			String templateName  = "";
			if(ValidationUtil.isValid(uploadFilingVb.getTemplateName())) {
				if(uploadFilingVb.getTemplateName().indexOf(",")>0) {
					templateName  = "ALL";
				}else {
					templateName  = uploadFilingVb.getTemplateName();
				}
				fileName = uploadFilingVb.getCountry()+trimmedLeBook+uploadFilingVb.getBusinessDate()+templateName;
				lFile = new File(filePath+ValidationUtil.encode(fileName)+"_"+currentUserId+".xlsx");
			}else {
				fileName = uploadFilingVb.getCountry()+trimmedLeBook+uploadFilingVb.getBusinessDate();
				lFile = new File(filePath+ValidationUtil.encode(fileName)+"_"+currentUserId+".xlsx");
			}
			ExcelExportUtil.createTemplateFile(lFile);
			long min = 0;
			long max = ExcelExportUtil.MAX_FETCH_RECORDS;
			int rowNum = 8;
			boolean createHeadersAndFooters = true;
			OPCPackage pkg = OPCPackage.open(new FileInputStream(lFile.getAbsolutePath()));
			XSSFWorkbook workbook = new XSSFWorkbook(pkg);
			SXSSFWorkbook workBook = new SXSSFWorkbook(workbook, 500);
			Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workbook);
			SXSSFSheet sheet = (SXSSFSheet)workBook.getSheetAt(workBook.getActiveSheetIndex());
			int headerCnt  = colTyps.size();
			columnWidths.put(0, 4800);
			columnWidths.put(6, 5000);
//			String assetFolderUrl = ((WebApplicationContext)applicationContext).getServletContext().getRealPath("images");
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			uploadFilingVb.setMaker(SessionContextHolder.getContext().getVisionId());
			getScreenDao().fetchMakerVerifierNames(uploadFilingVb);
			List<PromptIdsVb> prompts = creatPromptsId(uploadFilingVb, legalVehcileDes, frequencyProcessDesc, majorBuild);
			System.out.println("Create Prompts Starts!");
			ExcelExportUtil.createPromptsUploadExlce(uploadFilingVb, prompts, sheet, workBook, assetFolderUrl, styls, headerCnt);
			System.out.println("Create Prompts End!");
			String xmlData = getUploadFilingDao().getListReportDataForExportAsXMLString(promptTree, columnHeaders, min, max);
			/*Checks the format type is FHT*/
			String xmlDataHeader = getUploadFilingDao().getListReportDataForExportAsXMLStringHeader(promptTree, columnHeaders, min, max);
			int hearRow = 0;
			do{
				if((max % SpreadsheetVersion.EXCEL2007.getMaxRows())==0){
					rowNum = 6;
					sheet = (SXSSFSheet)workBook.createSheet();
					createHeadersAndFooters = true;
				}
				//Add headers
				if(createHeadersAndFooters && !"FHT".equalsIgnoreCase(xmlDataHeader)){
					ExcelExportUtil.writeHeadersForListData(sheet,columnHeaders, styls, rowNum, columnWidths);
					++rowNum;
					hearRow = rowNum;
				}
				rowNum = ExcelExportUtil.writeListDataModify(sheet, colTyps, styls, xmlData, rowNum, columnWidths,columnHeaders);				
				//rowNum = ExcelExportUtil.writeListData(sheet, colTyps, styls, xmlData, rowNum, columnWidths);				
				/*if(createHeadersAndFooters){
					String assetFolderUrl = ((WebApplicationContext)applicationContext).getServletContext().getRealPath("assets");
					reportWriterVb.setMaker(SessionContextHolder.getContext().getVisionId());
					getScreenDao().fetchMakerVerifierNames(reportWriterVb);
					ExcelExportUtil.createPrompts(reportWriterVb, prompts, sheet, workBook, assetFolderUrl, styls, headerCnt);
				}*/
				createHeadersAndFooters = false;
				min = max;
				max += ExcelExportUtil.MAX_FETCH_RECORDS;
				xmlData = getUploadFilingDao().getListReportDataForExportAsXMLString(promptTree, columnHeaders, min, max);
			}while(!"<tableData></tableData>".equalsIgnoreCase(xmlData));
			
			sheet.createFreezePane(1, hearRow);
			
			for(int loopCount=0; loopCount<headerCnt;loopCount++){
				sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
			}
			for(int loopCount=0; loopCount<headerCnt;loopCount++){
				sheet.autoSizeColumn(loopCount, true);
			}
			fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
		}catch (Exception e) {
			if(fileOS != null){
				try{fileOS.close();}catch(Exception ex){}
			}
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}finally{
			try{getUploadFilingDao().callProcToCleanUpTables(uploadFilingVb);}catch (Exception ex){}
		}
		ExceptionCode exceptionCode = CommonUtils.getResultObject("", 1, "", "");
		exceptionCode.setRequest(fileName);
		exceptionCode.setResponse(filePath);
		return exceptionCode;
	}
	public String getNodeRequestByAdfNumber(String adfNumber){
		String nodeName = getUploadFilingDao().getNodeRequestByAdfNumber(adfNumber);
		return nodeName;
	}
	
	public ExceptionCode fileCheckInLocal(String exelFileName, String xclUploadPath, Set<String> templateNames){
		ExceptionCode exceptionCode  = null;
		boolean checkXlsx = new File(xclUploadPath, exelFileName+".XLSX").exists();
		logger.info("checkXlsx : 1  : "+checkXlsx);
		if(!checkXlsx){
			boolean checkXls = new File(xclUploadPath, exelFileName+".XLS").exists();
			logger.info("checkXls : 1  : "+checkXls);
			if(!checkXls){
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Please upload the file '"+exelFileName+"'!");
				return exceptionCode;								
			}else if(checkXls){
				logger.info("checkXls : 2  : "+checkXls+" File Name : "+exelFileName);
				try {
					Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(xclUploadPath, exelFileName+".XLS")));
					for(String templateName : templateNames){
						System.out.println("checkedFileNames - templateName:"+templateName);
						int indexWorkbook = workbook.getSheetIndex(templateName);
						System.out.println("checkedFileNames - indexWorkbook:"+indexWorkbook);
						if(indexWorkbook == -1){
							exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Sheet Name ["+templateName+"] does not exist in uploaded file '"+exelFileName+"'!");
							return exceptionCode;
						}
					}
				}catch ( IOException | IllegalStateException e) {
					logger.info("Exception while Reding the XLS File");
					e.printStackTrace();
					exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Invalid XLS file!"+e.getMessage());
					return exceptionCode;
				}catch (Exception e){
					logger.info("Exception while Reding the XLS File");
					e.printStackTrace();
					exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Invalid XLS file!"+e.getMessage());
					return exceptionCode;
				}
			}
		}else{
			logger.info("checkXlsx : 2  : "+checkXlsx);
			try {
//				OPCPackage opcPackage = OPCPackage.open(new File(xclUploadPath, exelFileName+".XLSX"));
				String fileAndPath = xclUploadPath+exelFileName+".XLSX";
				OPCPackage opcPackage = OPCPackage.open(fileAndPath);
		        XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
		        for(String templateName : templateNames){
		        	System.out.println("checkedFileNames - templateName:"+templateName);
					int indexWorkbook = workbook.getSheetIndex(templateName);
					System.out.println("checkedFileNames - indexWorkbook:"+indexWorkbook);
					if(indexWorkbook == -1){
						exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Sheet Name ["+templateName+"] does not exist in uploaded file '"+exelFileName+"'!");
						return exceptionCode;								
					}
		        }
			}catch (InvalidFormatException | IOException | IllegalStateException e) {
				logger.info("Exception wihle Reding the XLSX File");
				e.printStackTrace();
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Invalid XLSX file!"+e.getMessage());
				return exceptionCode;
			}catch (Exception e){
				logger.info("Exception wihle Reding the XLSX File");
				e.printStackTrace();
				exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Invalid XLSX file!"+e.getMessage());
				return exceptionCode;
			}
		}
		return CommonUtils.getResultObject("Non ADF Process", Constants.SUCCESSFUL_OPERATION, "Submit", "");
	}
	
	public ExceptionCode moveFilestoLocal(String FileName,String tarNode, String xclUploadPath){
		ExceptionCode exceptionCode  = new ExceptionCode();

		boolean moveFilestoLocal=false;
		try{
			System.out.println("FTP Start on ExcelUpload..!");
			
			String OriginalFileName  = FileName+".XLSX";
			
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if(!ValidationUtil.isValid(environmentParam))
				environmentParam="UAT";
			
			String environmentNode = tarNode;
			if(!ValidationUtil.isValid(environmentNode))
				environmentNode="N1";
			
	    	String serverHost = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_IP");
	    	if(ValidationUtil.isValid(serverHost)){
	    		hostName = serverHost;
	    	}
	    	
	    	String serverUserName = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_USER");
	    	if(ValidationUtil.isValid(serverUserName)){
	    		userName = serverUserName;
	    	}
	    	
	    	String serverPassword = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_PWD");
	    	if(ValidationUtil.isValid(serverPassword)){
	    		password = serverPassword;
	    	}
	    	System.out.println("File Download Started : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
	        try{
	        	JSch jsch = new JSch();
				jsch.setKnownHosts(getKnownHostsFileName());
				Session session = jsch.getSession(userName, hostName);
				{      
					UserInfo ui = new MyUserInfo();   
					session.setUserInfo(ui);       
					session.setPassword(password); 
				} 
	            java.util.Properties config = new java.util.Properties();
	            config.put("StrictHostKeyChecking", "no");
	            session.setConfig(config);
	            session.connect();
	            System.out.println("Host connected.");
	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            System.out.println("sftp channel opened and connected.");
	            ChannelSftp channelSftp = (ChannelSftp) channel;
	            channelSftp.cd(xclUploadPath);
	
	        	/*Download Files to Local*/
	            try{
	            	System.out.println("XLSX File Download started ");
		        	byte[] buffer = new byte[1024];
		            BufferedInputStream bis = new BufferedInputStream(channelSftp.get(OriginalFileName));
		            File newFile = new File(xclUploadPath+OriginalFileName);
		            OutputStream os = new FileOutputStream(newFile);
		            BufferedOutputStream bos = new BufferedOutputStream(os);
		            int readCount;
		            while ((readCount = bis.read(buffer)) > 0) {
		                bos.write(buffer, 0, readCount);
		            }
		            bis.close();
		            bos.close();
	                System.out.println("XLSX File Downloaded successfully ");
		        }catch(SftpException e){
		        	System.out.println("File Download Failed1 : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
		        	try{
			        	System.out.println("XLS File Download started ");
		  	            OriginalFileName  = FileName+".XLS";
		       	        byte[] buffer = new byte[1024];
			            BufferedInputStream bis = new BufferedInputStream(channelSftp.get(OriginalFileName));
			            File newFile = new File(xclUploadPath+OriginalFileName);
			            OutputStream os = new FileOutputStream(newFile);
			            BufferedOutputStream bos = new BufferedOutputStream(os);
			            int readCount;
			            while ((readCount = bis.read(buffer)) > 0) {
			                bos.write(buffer, 0, readCount);
			            }
			            bis.close();
			            bos.close();
		                System.out.println("XLS File Downloaded successfully ");
		                session.disconnect();
		    	        channelSftp.exit();
		              	return CommonUtils.getResultObject("Non ADF Process", Constants.SUCCESSFUL_OPERATION, "Submit", "");
		        	}catch(SftpException sftpExp){
		        		System.out.println("File Download Failed2 : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
		        		sftpExp.printStackTrace();
		        		exceptionCode.setErrorMsg(sftpExp.getMessage());
		        		exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		        		moveFilestoLocal = false;
		   	        	return exceptionCode;
		        	}
		        }
                /*Upload Files to remote server
			       File f = new File("C:\\xlupload\\FIM0300_DATA_1005_2017-03-03.err");
		           channelSftp.put(new FileInputStream(f), f.getName());
		           System.out.println("File transfered successfully to host.");*/
		             
                /*File file = new File(OriginalFileName);
    			file.delete();*/
    	        session.disconnect();
    	        channelSftp.exit();
    	        moveFilestoLocal = true;
    		}catch(Exception e){
    		    e.printStackTrace();
    		    System.out.println("File Download Failed3 : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
            	exceptionCode = CommonUtils.getResultObject("Non ADF Process",20, "Submit",  "Unable to connect to Remote Computer");
   	        	moveFilestoLocal = false;
   	        	return exceptionCode;
    		}
        }catch(Exception e){
        	e.printStackTrace();
        	System.out.println("File Download Failed4 : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
        	exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", e.getMessage());
        	moveFilestoLocal = false;
        	return exceptionCode;
        }
		System.out.println("File Download Success : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
		return CommonUtils.getResultObject("Non ADF Process", Constants.SUCCESSFUL_OPERATION, "Submit", "");
	}
	public class MyUserInfo implements UserInfo{
	    public String getPassword(){ return password; }
	    public boolean promptYesNo(String str){
	      return false;
	    }
	    public String getPassphrase(){ return null; }
	    public boolean promptPassphrase(String message){ return true; }
	    public boolean promptPassword(String message){ return false; }
	    
	    public void showMessage(String message){
	      return;
	    }
	}
	
	public List<UploadFilingVb> getDrillDownCommentsPopup(UploadFilingVb uploadFilingVb){
		List<UploadFilingVb> arrListLocal = new ArrayList<UploadFilingVb>();
		try{
			String legalVehcile = getUploadFilingDao().getLegalVehicle(uploadFilingVb);
			PromptTreeVb promptTree = getUploadFilingDao().callProcToPopulateListReportData(uploadFilingVb, legalVehcile, SessionContextHolder.getContext().getVisionId());			
			if(promptTree == null || "1".equalsIgnoreCase(promptTree.getStatus())){
			}
			List<UploadFilingVb> uplodFilling = getUploadFilingDao().getCommentDrilldownResults(uploadFilingVb, promptTree.getTableName());
			arrListLocal.addAll(uplodFilling);
		}catch(RuntimeCustomException rex){
			logger.error("Comments Drill Down Exception " + rex.getCode().getErrorMsg());
			return arrListLocal;
		}finally{
			try{getUploadFilingDao().callProcToCleanUpTables(uploadFilingVb);}catch (Exception ex){}
		}
		return arrListLocal;
	}
	
	public ExceptionCode  checkeExcelFile(String FileName,String tarNode, String xclUploadPath){
		ExceptionCode exceptionCode  = new ExceptionCode();
		try{
			System.out.println("FTP Start on ExcelUpload..!");
			String OriginalFileName  = FileName+".XLSX";
			String environmentParam = System.getenv("VISION_SERVER_ENVIRONMENT");
			if(!ValidationUtil.isValid(environmentParam))
				environmentParam="UAT";
			
			String environmentNode = tarNode;
			if(!ValidationUtil.isValid(environmentNode))
				environmentNode="N1";
			
	    	String serverHost = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_IP");
	    	if(ValidationUtil.isValid(serverHost)){
	    		hostName = serverHost;
	    	}
	    	String serverUserName = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_USER");
	    	if(ValidationUtil.isValid(serverUserName)){
	    		userName = serverUserName;
	    	}
	    	String serverPassword = getUploadFilingDao().getServerCredentials(environmentParam, environmentNode, "NODE_PWD");
	    	if(ValidationUtil.isValid(serverPassword)){
	    		password = serverPassword;
	    	}
	    	System.out.println("File Download Started : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
	    	ChannelSftp channelSftp = null;
	    	Session session = null;
	    	JSch jsch = new JSch();
	        try{
	        	/*JSch jsch = new JSch();
				jsch.setKnownHosts(getKnownHostsFileName());
				session = jsch.getSession(userName, hostName);
				{      
					UserInfo ui = new MyUserInfo();   
					session.setUserInfo(ui);       
					session.setPassword(password); 
				}*/
	        	
	        	session = jsch.getSession( userName, hostName, 22 );
				{   // "interactive" version   // can selectively update specified known_hosts file    
					// need to implement UserInfo interface  
					// MyUserInfo is a swing implementation provided in    
					//  examples/Sftp.java in the JSch dist   
					UserInfo ui = new MyUserInfo();   
					session.setUserInfo(ui);    // OR non-interactive version. Relies in host key being in known-hosts file   
					session.setPassword( password);
				}
				System.out.println(userName+" host : "+ hostName);
				java.util.Properties config = new java.util.Properties(); 
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
//				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
	            System.out.println("Host connected.");
	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            System.out.println("sftp channel opened and connected.");
	            channelSftp = (ChannelSftp) channel;
	            channelSftp.cd(xclUploadPath);
	            channelSftp.lstat(OriginalFileName);
    		}catch (SftpException e) {
                e.printStackTrace();
                OriginalFileName  = FileName+".XLS";
                try{
                	channelSftp.lstat(OriginalFileName);
                }catch (SftpException ex) {
                	System.out.println("Excel File :"+FileName+" not exist in Node:"+tarNode+" UploadPath:"+xclUploadPath);
                    exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", "Please upload the file '"+FileName+"'!");
                    return exceptionCode;                	
                }
            }finally{
            	session.disconnect();
    	        channelSftp.exit();            	
            }
        }catch(Exception e){
        	e.printStackTrace();
        	System.out.println("Excel File :"+FileName+" not exist in Node:"+tarNode+" UploadPath:"+xclUploadPath);
        	exceptionCode =  CommonUtils.getResultObject("Non ADF Process", 20, "Submit", e.getMessage());
        	return exceptionCode;
        }
		System.out.println("Excel File Chheck : FileName"+FileName+":tarNode:"+tarNode+":xclUploadPath:"+xclUploadPath);
		return CommonUtils.getResultObject("Non ADF Process", Constants.SUCCESSFUL_OPERATION, "Submit", "");
	}
	public List<UploadFilingVb> getQueryPopupResults(UploadFilingVb queryPopupObj){
		List<UploadFilingVb> arrListLocal = new ArrayList<UploadFilingVb>();
		try{
			setVerifReqDeleteType(queryPopupObj);
			doFormateDataForQuery(queryPopupObj);
			List<UploadFilingVb> arrListResult = getScreenDao().getQueryPopupResults(queryPopupObj);
			if(arrListResult == null){
				arrListLocal= new ArrayList<UploadFilingVb>();
			}else{
				arrListLocal.addAll(arrListResult);
			}
			return arrListLocal;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the QueryPopup results.", ex);
			return null;
		}
	}
	
	public ExceptionCode getAcqUploadFilingPanel1(UploadFilingVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = getUploadFilingDao().getPanelInfo1(queryPopupObj);
			if(exceptionCode.getResponse()!=null) {
				exceptionCode.setOtherInfo(queryPopupObj);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			String autoRefreshVal =  getCommonDao().findVisionVariableValue("EUPLOAD_AUTO_REFRESH_INTERVAL");
			if(!ValidationUtil.isValid(autoRefreshVal))
				autoRefreshVal = "10";
			exceptionCode.setResponse1(autoRefreshVal);
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Acq - E Upload Filing Panel1 results.", ex);
		}
		return exceptionCode;
	}

	public ExceptionCode getAcqUploadFilingPanel2(UploadFilingVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = getUploadFilingDao().getPanelInfo2(queryPopupObj);
			if(exceptionCode.getResponse()!=null) {
				exceptionCode.setOtherInfo(queryPopupObj);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Acq - E Upload Filing Panel2 results.", ex);
		}
		return exceptionCode;
	}
	public boolean checkCondtions(UploadFilingVb uploadFilingVb) {
		boolean isCount = false;
		String operation = "Add";
		ExceptionCode exceptionCode = new ExceptionCode();
		uploadFilingVb.setActionType(operation);
		int runningCount = getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 1);
		if(runningCount>0){
			exceptionCode.setErrorCode(Constants.STATUS_ZERO);
			uploadFilingVb.setMakerName("You cannot submit at this moment because templates have been processed.");
			isCount = true;
		}
		runningCount = 0;
		runningCount = getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 2);
		if(runningCount>0){
			exceptionCode.setErrorCode(Constants.STATUS_ZERO);
			uploadFilingVb.setMakerName("You cannot submit at this moment because templates are in progress.");
			isCount = true;
		}
		runningCount = 0;
		runningCount = getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 3);
		if(runningCount>0){
			exceptionCode.setErrorCode(Constants.STATUS_ZERO);
			uploadFilingVb.setMakerName("You cannot submit at this moment because templates are in queue to be processed.");
			isCount = true;
		}
		int errorCode = Constants.SUCCESSFUL_OPERATION;
		String errorMessage = "Query Results";
		if(isCount) {
			errorCode = exceptionCode.getErrorCode();
			errorMessage = uploadFilingVb.getMakerName();
		}
		return isCount;
	}
	public ExceptionCode getAcqUploadFilingPanel3(UploadFilingVb queryPopupObj){
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = getUploadFilingDao().getPanelInfo3(queryPopupObj);
			if(exceptionCode.getResponse()!=null) {
				List<UploadFilingVb> list = (ArrayList<UploadFilingVb>)exceptionCode.getResponse();
				boolean isUplad = checkCondtions(list.get(0));
				queryPopupObj.setNewRecord(isUplad);
				exceptionCode.setOtherInfo(queryPopupObj);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception in getting the Acq - E Upload Filing Panel3 results.", ex);
		}
		return exceptionCode;
	}
}