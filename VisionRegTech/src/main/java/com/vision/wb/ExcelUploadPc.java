package com.vision.wb;

/*	Details :
 *	Excel Upload - excelUpload.pc Converted to Java
 * 	This Code will Upload any language file to the Table.
 * 	Written on 	: 20-December-2015
 * 	Written by	: S.Deepak
 * 	Tested by	: S.Deepak 
 *  */

/*import java.io.BufferedReader;*/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.vision.dao.TemplateScheduleDao;
import com.vision.util.ValidationUtil;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Component
public class ExcelUploadPc {

	public static Logger logger = LoggerFactory.getLogger(ExcelUploadPc.class);

	// public static String xluploadLogFilePath =
	// System.getenv("VISION_XLUPL_LOG_FILE_PATH");
	public static String xluploadLogFilePath = "";
	// findVisionVariableValue("VISION_XLUPL_LOG_FILE_PATH");
	// public static String xluploadDataFilePath =
	// System.getenv("VISION_XLUPL_DATA_FILE_PATH");
	public static String xluploadDataFilePath = "";
	// findVisionVariableValue("VISION_XLUPL_DATA_FILE_PATH");

	private String hostNameFtp;
	private String userNameFtp;
	private String passwordFtp;
	private Boolean multiServer = false;
	// private VisionUploadWb visionUploadWb;
	/*
	 * private String jdbcUrl = Constants.JDBC_URL_MAIN; private static String
	 * username = Constants.USER_NAME; private static String password =
	 * Constants.PASSWORD; private static String databaseType =
	 * Constants.DATABASE_TYPE;
	 */

	public static String username;

	public static String password;

//	public static String databaseType = System.getenv("RA_DATABASE_TYPE");

	private static String jdbcUrl;
	@Autowired
	TemplateScheduleDao templateScheduleDao;

	@Value("${spring.datasource.username}")
	public void setUsername(String username) {
		ExcelUploadPc.username = username;
	}

	@Value("${spring.datasource.url}")
	public void setJdbcUrl(String jdbcUrl) {
		ExcelUploadPc.jdbcUrl = jdbcUrl;
	}

	@Value("${spring.datasource.password}")
	public void setPassword(String password) {
		ExcelUploadPc.password = password;
	}
	/*
	 * public static String password ;
	 * 
	 * @Value("${spring.datasource.password}") public void setPassword(String
	 * password) { ExcelUploadPc.password = databaseType; }
	 */

	public static String databaseType;

	@Value("${app.databaseType}")
	public void setDatabaseType(String databaseType) {
		ExcelUploadPc.databaseType = databaseType;
	}

	public static String schedulerRunStatus;

	@Value("${upload.scheduler}")
	public void setSchedulerRunStatus(String schedulerRunStatus) {
		ExcelUploadPc.schedulerRunStatus = schedulerRunStatus;
	}

	String passwordEncryptFlag = "N";
//			isValid(System.getenv("VISION_PASSWORD_ENCRYPT_FLAG"))
//			? System.getenv("VISION_PASSWORD_ENCRYPT_FLAG")
//			: "N";

	// private CommonDao commonDao;
	String EXCEL_LOG_FILENAME = "VISION_UPLOAD.err";
	String EXCEL_FILE_NAME = "";
	Boolean firstTime = true;
	int MISSING_ENV_VARIABLE = 6;
	int INVALID_LOG_FILE = 7;
	int NO_OF_EXCEPTION_COLS = 6;
	int NO_OF_NUMERIC_TYPES = 7;
	int NO_OF_UPLOAD_COLS = 200;
	int NO_OF_GROUP_TABLES = 15;
	int NO_OF_MGT_HEADERS_COLS = 10;
	int NO_OF_MGT_BALANCES_COLS = 19;
	short NULL_VALUE = -1;
	int YES = 0;
	int NO = 1;
	int ERRONEOUS_EXIT = 1;
	int SUCCESSFUL_EXIT = 0;
	int ABORT_EXECUTION = 2;
	int NO_VALIDATION = 2;
	/*
	 * Macro Definitions for different record types that are inserted into the
	 * various target tables
	 */
	int FIN_ADJ_REMAP_REC_IND = -1;
	int REC_IND_APPROVED = 0;
	int REC_IND_MOD_PENDING = 1;
	int REC_IND_ADD_PENDING = 2;
	int INTERNAL_STATUS = 0;

	int SQL_NOT_FOUND = 1403;
	int SQL_NO_ERRORS = 0;
	String SQL_PK_CONS_VIOLATED = "ORA-02437";
	int SQL_TOO_MANY_RECORDS = -2112;

	int GENERIC_UPLOAD = 9999;
	int GENERIC_UPLOAD_RG = 9998;
	int MGT_UPLOAD = 50;
	int DC_UPLOAD = 60;
	int DC_UPLOAD_SHORT = 65;
	int FC_UPLOAD = 70;
	int DAILY_DC_UPLOAD = 90;
	int REPORTS_UPLOAD = 80;
	int STATUS_PENDING = 1;
	int STATUS_IN_PROGRESS = 2;
	int STATUS_FAILURE = 3;
	int STATUS_SUCCESS = 4;
	int STATUS_PARTIAL_SUCCESS = 5;
	int STATUS_SUCCESS_VALIDATION_FAILED = 6;

	int EOF_VALUE = -1;
	int TAB_POSITION_1 = 7;
	int TAB_POSITION_2 = 8;

	String INPUT_DATE_FORMAT;

	/* Macro definitions for MIS Uploads */
	int IDX_MISUPLOAD_YEAR = 0;
	int IDX_MISUPLOAD_MONTH = 1;
	int IDX_MISUPLOAD_MGT_REFERENCE = 2;
	int IDX_MISUPLOAD_MA_SEQUENCE = 3;
	int IDX_MISUPLOAD_VISION_OUC = 4;
	int IDX_MISUPLOAD_MRL_LINE = 5;
	int IDX_MISUPLOAD_PRODUCT = 6;
	int IDX_MISUPLOAD_ACCOUNT_OFFICER = 7;
	int IDX_MISUPLOAD_VISION_SBU = 8;
	int IDX_MISUPLOAD_MIS_CURRENCY = 9;
	int IDX_MISUPLOAD_COUNTRY = 10;
	int IDX_MISUPLOAD_LE_BOOK = 11;
	int IDX_MISUPLOAD_CONTRACT_ID = 12;
	int IDX_MISUPLOAD_CUSTOMER_ID = 13;
	int IDX_MISUPLOAD_EOP_AMOUNT = 14;
	int IDX_MISUPLOAD_AVG_AMOUNT = 15;
	int IDX_MISUPLOAD_PL_AMOUNT = 16;
	int IDX_MISUPLOAD_POOL_AMOUNT = 17;
	int IDX_MISUPLOAD_OFFSET_SEQUENCE = 18;

	/* Macro Definitions for DC Uploads */

	int DC_SOURCE_ID = 990;
	String DC_ADJ_SOURCE = "DC";
	String DC_REVERSAL_POSTING = "N";
	int DC_RECORD_TYPE = 7;

	int DLY_DC_SOURCE_ID = 990;
	String DLY_DC_ADJ_SOURCE = "DC";
	int DLY_DC_RECORD_TYPE = 7;

	int IDX_DCUPLOAD_YEAR_MONTH = 0;
	int IDX_DCUPLOAD_COUNTRY = 1;
	int IDX_DCUPLOAD_LE_BOOK = 2;
	int IDX_DCUPLOAD_ADJ_REFERENCE = 3;
	int IDX_DCUPLOAD_DATA_TYPE = 4;
	int IDX_DCUPLOAD_ORIGINATING_OUC = 5;
	int IDX_DCUPLOAD_FA_REASON_CODE = 6;
	int IDX_DCUPLOAD_SUPPLEMENT = 7;
	int IDX_DCUPLOAD_REVERSAL_FLAG = 8;
	int IDX_DCUPLOAD_FINANCIALS_DAILY = 9;
	int IDX_DCUPLOAD_FIN_DLY_END_DATE = 10;
	int IDX_DCUPLOAD_CONTRACT_ID = 11;
	int IDX_DCUPLOAD_CUSTOMER_ID = 12;
	int IDX_DCUPLOAD_CUSTOMER_ID_MGT = 13;
	int IDX_DCUPLOAD_VISION_OUC = 14;
	int IDX_DCUPLOAD_GL_ENRICH_ID = 15;
	int IDX_DCUPLOAD_OFFICE_ACCOUNT = 16;
	int IDX_DCUPLOAD_VISION_SBU = 17;
	int IDX_DCUPLOAD_ACCOUNT_OFFICER = 18;
	int IDX_DCUPLOAD_COST_CENTER = 19;
	int IDX_DCUPLOAD_BS_GL = 20;
	int IDX_DCUPLOAD_PL_GL = 21;
	int IDX_DCUPLOAD_CURRENCY = 22;
	int IDX_DCUPLOAD_MIS_CURRENCY = 23;
	int IDX_DCUPLOAD_PROFIT_LOSS_LCY = 24;
	int IDX_DCUPLOAD_EOP_BALANCE_LCY = 25;
	int IDX_DCUPLOAD_AVG_BALANCE_DR_LCY = 26;
	int IDX_DCUPLOAD_AVG_BALANCE_CR_LCY = 27;
	int IDX_DCUPLOAD_FLOATING_POOL_DR_LCY = 28;
	int IDX_DCUPLOAD_FLOATING_POOL_CR_LCY = 29;
	int IDX_DCUPLOAD_CORRES_POOL_DR_LCY = 30;
	int IDX_DCUPLOAD_CORRES_POOL_CR_LCY = 31;
	int IDX_DCUPLOAD_RESERVE_ASSET_COST_LCY = 32;
	int IDX_DCUPLOAD_SOURCE_ID = 33;
	int IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_1 = 34;
	int IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_2 = 35;
	int IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_3 = 36;
	int IDX_DCUPLOAD_ACCRUAL_STATUS = 37;
	int IDX_DCUPLOAD_VALUE_DATE = 38;
	int IDX_DCUPLOAD_POOL_CODE = 39;
	int IDX_DCUPLOAD_CALC_POOL = 40;
	int IDX_DCUPLOAD_RATE = 41;
	int IDX_DCUPLOAD_CREDIT_LINE = 42;

	/* DAILY DC */

	int IDX_DLY_DCUPLOAD_YEAR_MONTH = 0;
	int IDX_DLY_DCUPLOAD_COUNTRY = 1;
	int IDX_DLY_DCUPLOAD_LE_BOOK = 2;
	int IDX_DLY_DCUPLOAD_ADJ_REFERENCE = 3;
	int IDX_DLY_DCUPLOAD_DATA_TYPE = 4;
	int IDX_DLY_DCUPLOAD_ORIGINATING_OUC = 5;
	int IDX_DLY_DCUPLOAD_FA_REASON_CODE = 6;
	int IDX_DLY_DCUPLOAD_SUPPLEMENT = 7;
	int IDX_DLY_DCUPLOAD_CONTRACT_ID = 8;
	int IDX_DLY_DCUPLOAD_CUSTOMER_ID = 9;
	int IDX_DLY_DCUPLOAD_VISION_OUC = 10;
	int IDX_DLY_DCUPLOAD_GL_ENRICH_ID = 11;
	int IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT = 12;
	int IDX_DLY_DCUPLOAD_VISION_SBU = 13;
	int IDX_DLY_DCUPLOAD_ACCOUNT_OFFICER = 14;
	int IDX_DLY_DCUPLOAD_COST_CENTER = 15;
	int IDX_DLY_DCUPLOAD_BS_GL = 16;
	int IDX_DLY_DCUPLOAD_PL_GL = 17;
	int IDX_DLY_DCUPLOAD_CURRENCY = 18;
	int IDX_DLY_DCUPLOAD_MIS_CURRENCY = 19;
	int IDX_DLY_DCUPLOAD_SOURCE_ID = 20;
	int IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_1 = 21;
	int IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_2 = 22;
	int IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_3 = 23;
	int IDX_DLY_DCUPLOAD_ACCRUAL_STATUS = 24;
	int IDX_DLY_DCUPLOAD_VALUE_DATE = 25;
	int IDX_DLY_DCUPLOAD_TRANS_DATE = 26;
	int IDX_DLY_DCUPLOAD_TRANS_AMOUNT = 27;
	int IDX_DLY_DCUPLOAD_BAL_TYPE = 28;
	int IDX_DLY_DCUPLOAD_CREDIT_LINE = 29;
	int IDX_DLY_DCUPLOAD_ADJ_SOURCE = 30;

	/* Macro Definitions for FC Uploads */
	int IDX_FCUPLOAD_YEAR = 0;
	int IDX_FCUPLOAD_DATA_SOURCE = 1;
	int IDX_FCUPLOAD_COUNTRY = 2;
	int IDX_FCUPLOAD_LE_BOOK = 3;
	int IDX_FCUPLOAD_VISION_OUC = 4;
	int IDX_FCUPLOAD_VISION_SBU = 5;
	int IDX_FCUPLOAD_ACCOUNT_OFFICER = 6;
	int IDX_FCUPLOAD_CUSTOMER_ID = 7;
	int IDX_FCUPLOAD_CURRENCY = 8;
	int IDX_FCUPLOAD_MRL_LINE = 9;
	int IDX_FCUPLOAD_PRODUCT = 10;
	int IDX_FCUPLOAD_BAL_TYPE = 11;
	int IDX_FCUPLOAD_BALANCE_LCY_01 = 12;
	int IDX_FCUPLOAD_BALANCE_LCY_02 = 13;
	int IDX_FCUPLOAD_BALANCE_LCY_03 = 14;
	int IDX_FCUPLOAD_BALANCE_LCY_04 = 15;
	int IDX_FCUPLOAD_BALANCE_LCY_05 = 16;
	int IDX_FCUPLOAD_BALANCE_LCY_06 = 17;
	int IDX_FCUPLOAD_BALANCE_LCY_07 = 18;
	int IDX_FCUPLOAD_BALANCE_LCY_08 = 19;
	int IDX_FCUPLOAD_BALANCE_LCY_09 = 20;
	int IDX_FCUPLOAD_BALANCE_LCY_10 = 21;
	int IDX_FCUPLOAD_BALANCE_LCY_11 = 22;
	int IDX_FCUPLOAD_BALANCE_LCY_12 = 23;

	String REPORT_UPLOAD_COUNTRY = "";
	String REPORT_UPLOAD_LE_BOOK = "";
	String REPORT_UPLOAD_REPORT_ID = "";
	String REPORT_UPLOAD_PERIOD = "";

	String P_REPCOLLIST = "";
	String P_INSCOLCTR = "";
	String P_ALIASREPCOLLIST = "";
	String P_KEYCOLLIST = "";
	int P_STATUS = 0;
	String P_ERRORMSG = "";
	Boolean recordPresent = false;

	int FC_SOURCE_ID = 802;
	String FC_ADJ_SOURCE = "FC";
	int FC_RECORD_TYPE = 0;
	int GET_DATE = 1;
	int GET_TIME = 2;
	int GET_DATE_TIME = 3;
	int GET_DATE_SPECIAL = 4;
	int MAX_RECS_FETCH_COUNT = 5000;
	String logFileName = "";
	String uploadTableName = "";
	String tableName = "";
	String uploadFileName = "";
	String uploadFile = "";
	String sheetName = "";
	ResultSet rs;
	ArrayList columnNamelst = new ArrayList();
	ArrayList nullable = new ArrayList();
	ArrayList dataPrecision = new ArrayList();
	ArrayList<String> dataType = new ArrayList<String>();
	ArrayList dateTypelst = new ArrayList();
	ArrayList uploadAllowedlst = new ArrayList();
	ArrayList verificationRequiredlst = new ArrayList();
	ArrayList groupTablesTableNamelst = new ArrayList();
	ArrayList groupTablesVerificationReqdlst = new ArrayList();
	ArrayList groupTablesUploadSequencelst = new ArrayList();
	ArrayList groupTablesUploadFileNamelst = new ArrayList();
	ArrayList periodControlsArr = new ArrayList();
	ArrayList countrylst = new ArrayList();
	ArrayList leBooklst = new ArrayList();
	ArrayList yearMonthlst = new ArrayList();
	ArrayList adjSourcelst = new ArrayList();
	ArrayList adjReferencelst = new ArrayList();
	ArrayList adjIdlst = new ArrayList();
	ArrayList sequenceFalst = new ArrayList();
	ArrayList visionOuclst = new ArrayList();
	ArrayList mrlLinelst = new ArrayList();
	ArrayList customerIdlst = new ArrayList();
	ArrayList accountOfficerlst = new ArrayList();
	ArrayList visionSbulst = new ArrayList();
	ArrayList misCurrencylst = new ArrayList();
	ArrayList productlst = new ArrayList();
	ArrayList legalVehicleRatesLegalVehicleArr = new ArrayList();
	ArrayList legalVehicleRatesDataSource = new ArrayList();
	ArrayList legalVehicleRatesYearArr = new ArrayList();
	float[] legalVehicleRatesArrFxRate = null;
	ArrayList legalVehicleRatesArr = new ArrayList();
	ArrayList fxRateslst = new ArrayList();
	ArrayList legalVehicleRatesfxRateArr = new ArrayList();
	ArrayList fxRatelst = new ArrayList();
	ArrayList dataSourcelst = new ArrayList();
	ArrayList EmailArrayCountry = new ArrayList();
	ArrayList EmailArrayLeBook = new ArrayList();
	ArrayList EmailArrayYearMonth = new ArrayList();
	ArrayList EmailArrayAdjSource = new ArrayList();
	ArrayList EmailArrayAdjReference = new ArrayList();
	ArrayList EmailArrayMakerName = new ArrayList();
	ArrayList EmailArrayMessageText = new ArrayList();
	ArrayList EmailArrayEmailId = new ArrayList();
	String[] oucCodesArrOUC = null;
	String[] legalVehicleArr = null;
	String[] uploadData = null;
	String columnName = "";
	String indexName = "";
	long recsCount = 0;
	String uploadAllowed;
	String verificationRequired;
	String tableAttribute1;
	String submissionDate;
	String templateDate;

	int columnSize = 0;
	String sourceString = "";
	ArrayList columnSizelst = new ArrayList();
	int dataLength;
	ArrayList dataScalelst = new ArrayList();
	int uploadGroup;
	String insertStmt;
	String updateStmt;
	String sqlStatement;
	String countStmt = "";
	int uploadStatus = 0;
	int uploadSequence = 0;
	short indic1;
	short indic2;
	short indic3;
	int makerId = 0;
	int internalStatus = 0;
	String visionVariableValues = "";
	String visionVariable = "";
	String valueYes = "Y";
	String valueNo = "N";
	int year = 0;
	ArrayList yearlst = new ArrayList();
	int prevYear = 0;
	String country = "";
	String leBook = "";
	String mgtReference = "";
	ArrayList mgtReferencelst = new ArrayList();
	ArrayList maSequencelst = new ArrayList();
	ArrayList monthlst = new ArrayList();
	ArrayList noOfDayslst = new ArrayList();
	String prevMgtReference = "";
	int lineNumber = 0;
	ArrayList lineNumberlst = new ArrayList();
	String legalVehicle = "";
	ArrayList legalVehiclelst = new ArrayList();
	String actionId = "";
	String tableType = "";
	ArrayList tableTypelst = new ArrayList();
	String availableStatus = "";
	ArrayList availableStatuslst = new ArrayList();
	String visionOuc = "";
	String misCurrency = "";
	String currency = "";
	String customerId = "";
	String mrlLine = "";
	String product;
	String tempDataType = "";
	int count1 = 0;
	int count2 = 0;
	int maSequence;
	int newMaSequence;
	int batchSequence;
	int MaxMaSeq = 0;
	int month;
	String calcPool = "";
	String calcPool1 = "";
	String calcPoolRate;
	String calcPoolRate1;
	String transAmount;
	String transDate;
	String balType;
	String valueDate;

	int accrualStatus;
	String creditLine = "";
	String originatingOuc = "";

	String errorDescription = "";
	String errorType = "";
	String severity = "";

	ArrayList errorDescriptionlst = new ArrayList();
	ArrayList errorTypelst = new ArrayList();
	ArrayList severitylst = new ArrayList();
	ArrayList periodControlsYearArr = new ArrayList();

	short monthInd = 0;
	short maSequenceInd = 0;
	short ind1;
	short ind2;
	short ind3;
	short ind4;
	short ind5;
	short ind6;
	short ind7;
	short ind8;
	short ind9;
	short ind10;
	short ind11;
	short ind12;

	String compDataType = "";
	String compFaReasonCode = "";
	String compSupplement = "";
	String compOriginatingOuc = "";
	String compReversalFlag = "";
	String compFinancialsDaily = "";
	String compFinDlyEndDate = "";

	int yearMonth = 0;
	String actualyearMonth = "";
	String glEnrichId = "";
	String bsGl = "";
	String plGl = "";
	String contractId = "";
	String officeAccount = "";
	String costCenter = "";
	String visionSbu = "";
	String accountOfficer = "";
	int sourceId = 0;
	int recordType = 0;
	String originalOuc = "";
	String customerIdMgt = "";
	int finAdjDetailsStatus = 0;
	int finAdjHeadersStatus = 0;
	int adjId = 0;
	double newAdjId = 0;
	double sequenceFa = 0;
	String adjReference = "";
	String adjSource = "";
	double sequenceFm = 0;
	int offsetSequence = 0;
	int finMthSourceId = 0;
	int finMthRecordType = 0;
	int periodControlsIndex = 0;
	int currentYear = 0;
	int currentMonth = 0;
	String reportingDate = "";
	String firstDay = "";
	/* These variables are used for calling validational procedures */
	String status = "";
	String category = "";
	String errorMsg = "";
	int successCnt;
	int failCnt;
	int noOfDays;

	int maxSequenceFc;
	double fxRate = 0;
	String dataSource = "";

	String quotedType = "";
	ArrayList quotedTypelst = new ArrayList();
	String decimalsCheck = "";
	ArrayList decimalsChecklst = new ArrayList();
	String dateType = "";
	String abortFlag = "";
	String apprRecInsStmt;
	String paramUserId = "";
	String paramPassword = "";
	String connectString = "";
	int retVal;
	int valvalue;

	String locEMailId = "";
	String locEMailSubject = "";
	String locEMailBodyVar = "";
	String mailString = "";

	String prev_Country = "";
	String prev_LeBook = "";
	String prev_YearMonth = "";

	String mainLogFileName;
	char batchId;
	/*
	 * FILE *inputFilePtr; FILE *mainLogFilePtr; FILE *uploadLogFilePtr;
	 */
	int totalInputFileColumns = 0;
	int totalTableColumns = 0;
	int totalGroupUploadTables = 0;
	long filePosition = 0;
	int longestColumn = 0;
	int finalReturnValue = SUCCESSFUL_EXIT;
	int totalInputFileRecords = 0;
	int totalSuccessRecords = 0;
	int totalFailureRecords = 0;
	// char uploadData[NO_OF_UPLOAD_COLS];
	String originalTableName;
	char operationType;
	int recordIndicator;
	char tempUserName;
	char tempPassword;
	char uploadErrorsFirstTimeFlag = 'Y';
	String uploadLogFilePath;
	String uploadLogFileName;
	char uploadDataFilePath;
	String maInputType;
	char TAB_FLAG = 'N';
	int currentGroupCtr = 0;
	int DEBUG_MODE = YES;
	int totHeaderRecords = 0;
	int totDetailRecords = 0;
	int totBalanceRecords = 0;
	int totFailureDetailRecords = 0;
	int totFailureBalanceRecords = 0;
	int deletedRecsCount = 0;
	char lockFlag = 'N';
	int intMaxOucCodes = 0;
	int intMaxLVRates = 0;
	int intEmailCount = 0;
	int currRec = 0;
	int copyCnt = 0;
	int currFetchCnt = 0;
	int totalFetchCnt = 0;
	int intMaxPeriodControls = 0;
	String prevLegalVehicle = "";
	String prevDataSource = "";
	int noOfDaysSum = 0;
	String sqlErrorLog = "";
	String emailId = "";
	String subject = "";
	String localString = "";
	String colName = "";
	String overideFlag = "";

	ArrayList tableNamelst = new ArrayList();
	ArrayList verReqlst = new ArrayList();
	ArrayList uploadSeqlst = new ArrayList();
	ArrayList uploadFileNamelst = new ArrayList();
	ArrayList tableColumnList = new ArrayList();
	ArrayList fileColumnsNames = new ArrayList();
	ArrayList fileColumnsIndexColumn = new ArrayList();
	ArrayList fileColumnsNullable = new ArrayList();

	ArrayList fileColumnsColumnSize = new ArrayList();
	ArrayList fileColumnsQuotedType = new ArrayList();
	ArrayList fileColumnsDecimalsCheck = new ArrayList();
	ArrayList fileColumnsAfterDecimals = new ArrayList();
	ArrayList fileColumnsDataType = new ArrayList();
	/* BufferedReader bufferedReader1 = null; */
	InputStreamReader inputStreamReader = null;
	FileInputStream fileReader = null;

	CallableStatement callableStatement = null;
	String[] exceptionColList = { "MAKER", "VERIFIER", "DATE_CREATION", "DATE_LAST_MODIFIED", "INTERNAL_STATUS",
			"RECORD_INDICATOR" };

	String[] nonQuotedTypes = { "DECIMAL", "FLOAT", "NUMBER", "REAL", "INTEGER", "LONG", "SMALLINT" };

	String[] MgtHeadersColumnList = { "YEAR", "MGT_REFERENCE", "MA_DESCRIPTION", "MA_NOTES", "MA_SOURCE", "DATA_TYPE",
			"LEGAL_VEHICLE", "CURRENCY", "MA_REASON_CODE", "BATCH_SEQUENCE" };

	String[] MgtDetailsColumnList = { "YEAR", "MONTH", "MGT_REFERENCE", "MA_SEQUENCE", "VISION_OUC", "MRL_LINE",
			"PRODUCT", "ACCOUNT_OFFICER", "VISION_SBU", "MIS_CURRENCY", "COUNTRY", "LE_BOOK", "CONTRACT_ID",
			"CUSTOMER_ID", "EOP_AMOUNT", "AVG_AMOUNT", "PL_AMOUNT", "POOL_AMOUNT", "OFFSET_SEQUENCE" };
	Connection con = null;
	FileWriter logfile = null;
	FileWriter mainLogfile = null;
	BufferedWriter bufferedWriter = null;
	Statement stmt = null;
	boolean containsBusinessDate = false;
	Workbook workbook = new XSSFWorkbook();
	// String environmentNode =
	// ValidationUtil.isValid(System.getenv("VISION_NODE_NAME"))?System.getenv("VISION_NODE_NAME"):"N4";

	/*
	 * private void logWriter(String logString) { try { logfile = new
	 * FileWriter(uploadLogFileName, true); bufferedWriter = new
	 * BufferedWriter(logfile); bufferedWriter.newLine();
	 * bufferedWriter.write(getCurrentDate() + " : " + logString);
	 * bufferedWriter.close(); logfile.close(); } catch (Exception e) {
	 * System.out.println(e.getMessage()); } }
	 */

	/*
	 * public CommonDao getCommonDao() { return commonDao; }
	 * 
	 * public void setCommonDao(CommonDao commonDao) { this.commonDao = commonDao; }
	 */

	private void writeToMainLogFile(String logString) {
		try {
			mainLogfile = new FileWriter(mainLogFileName, true);
			bufferedWriter = new BufferedWriter(mainLogfile);
			bufferedWriter.newLine();
			bufferedWriter.write(getCurrentDate() + " : " + logString);
			bufferedWriter.close();
			mainLogfile.close();
		} catch (Exception e) {
//			System.out.println("writeToMainLogFile():" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void sqlLogWriter(String logString) {
		try {
			logfile = new FileWriter(uploadLogFileName, true);
			bufferedWriter = new BufferedWriter(logfile);
			bufferedWriter.newLine();
			bufferedWriter.write(getCurrentDate() + " : " + sqlErrorLog + "-" + logString);
			bufferedWriter.close();
			logfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("excelUpload.sqlLogWriter():" + e.getMessage());
		}
	}

	private void writeSQLErrToMainLogFile(String logString) {
		try {
			mainLogfile = new FileWriter(mainLogFileName, true);
			bufferedWriter = new BufferedWriter(mainLogfile);
			bufferedWriter.newLine();
			bufferedWriter.write(getCurrentDate() + " : " + sqlErrorLog + "-" + logString);
			bufferedWriter.close();
			mainLogfile.close();
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private int connnectToDatabase() throws ClassNotFoundException {
		try {

//			username = System.getenv("VISION_USER_NAME");
//
//			password = System.getenv("VISION_PASSWORD");
//
//			jdbcUrl = System.getenv("VISION_APP_CONNECT_STRING");

			/*
			 * String username ="Vision"; String password="Vision@123"; String jdbcUrlMain =
			 * "jdbc:sqlserver://10.16.1.38;instance=VISIONBISQL2019;port=52866;DatabaseName=VISION_RA";
			 * String dataBaseType = "MSSQL"; String tablePrefix = ""; String classPath =
			 * "D://RA//";
			 */

			String secretKey = "";
			if (isValid(passwordEncryptFlag) && passwordEncryptFlag.equals("Y")) {
				secretKey = System.getenv("VISION_DB_PASSWORD_ENCRYPT_KEY_N4");
				if (isValid(secretKey)) {
					writeSQLErrToMainLogFile("VISION_DB_PASSWORD_ENCRYPT_KEY_N4 : ************");
				} else {
					secretKey = System.getenv("VISION_DB_PASSWORD_ENCRYPT_KEY");
					writeSQLErrToMainLogFile("VISION_DB_PASSWORD_ENCRYPT_KEY : ************");
				}
				if (!isValid(secretKey)) {
					secretKey = "vision123";
					writeSQLErrToMainLogFile("Password decryption using default secret Key[*********]");
					writeSQLErrToMainLogFile(
							"Maintain Jasypt encryption secret Key in variable [VISION_DB_PASSWORD_ENCRYPT_KEY] ");
				}
				try {
					password = ExcelUploadPc.jaspytPasswordDecrypt(password, secretKey);
				} catch (Exception e) {
					writeSQLErrToMainLogFile(e.getMessage());
					writeSQLErrToMainLogFile("Error while decrypting Jasypt password.Exit further processing...!! ");
					return ERRONEOUS_EXIT;
				}

			}

			writeToMainLogFile("Connecting to the Database");
			// writeToMainLogFile("JDBC Url["+jdbcUrl+"]User Name["+username+"]
			// Password[********]");
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				Class.forName("oracle.jdbc.driver.OracleDriver");
//				System.out.println("password : "+password);
				con = DriverManager.getConnection(jdbcUrl, username, password);
				con.setAutoCommit(false);
				INPUT_DATE_FORMAT = "DD-MM-RRRR";
			} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
				/*
				 * String connectionUrl =
				 * "jdbc:sqlserver://yourserver.database.windows.net:1433;" +
				 * "database=AdventureWorks;" + "user=yourusername@yourserver;" +
				 * "password=yourpassword;" + "encrypt=true;" + "trustServerCertificate=false;"
				 * + "loginTimeout=30;";
				 */
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// String connectionUrl = jdbcUrl + "user=" + username + ";password=" +password;
				// //+ "trustServerCertificate=false;"
				String connectionUrl = jdbcUrl + ";user=" + username + ";password=" + password;
				con = DriverManager.getConnection(connectionUrl);
				con.setAutoCommit(false);
				INPUT_DATE_FORMAT = "103";
			} else {
				writeToMainLogFile(
						"Error connecting to the database !!!!!!- Invalid database type [" + databaseType + "]");
				return ERRONEOUS_EXIT;
			}

			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			e.printStackTrace();
			sqlLogWriter("Error connecting to the database :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int getCount() {
		try {
			int countResult = 0;
			rs = stmt.executeQuery(countStmt);
			while (rs.next())
				countResult = rs.getInt("count");
			return countResult;
		} catch (SQLException e) {
			sqlLogWriter("Error trying to count from table :" + e.getMessage());
			logWriter(countStmt, uploadLogFileName);
			return -1;
		}
	}

	public void doFinishingSteps() {
		/*
		 * if ("W".equalsIgnoreCase(getExcelUploadServerType())) {
		 * moveFileFromWindowToDBserver(); } else if (multiServer) moveFiletoDBserver();
		 */
		try {
			if (fileReader != null)
				fileReader.close();
			writeToMainLogFile(GetCurrentDateTime() + ": Connection Closed...!!");
			con.close();
		} catch (Exception e) {
			writeToMainLogFile("Error Closing Connection :" + e.getMessage());
		}
	}

	// Main program starts here.
//	@Scheduled(fixedRate = 30000)
	public int uploadData() {
		int uploadRetVal = 0;
		char tempStr;
		int reqsProcessed = 0;
		char abortFlag = 'N';
		int tempRetVal;
		finalReturnValue = 0;
		try {
//			System.out.println("xluploadLogFilePath -> "+xluploadLogFilePath);
//			System.out.println("xluploadDataFilePath -> "+xluploadDataFilePath);
			if (!ValidationUtil.isValid(xluploadLogFilePath) || !ValidationUtil.isValid(xluploadDataFilePath)) {
				writeToMainLogFile("Invalid Upload [" + xluploadDataFilePath + "] / Download [" + xluploadLogFilePath
						+ "]  Filepath");
				logger.error("Invalid Upload [" + xluploadDataFilePath + "] / Download [" + xluploadLogFilePath
						+ "]  Filepath");
				return ERRONEOUS_EXIT;
			}

			mainLogFileName = xluploadLogFilePath + EXCEL_LOG_FILENAME;
			retVal = connnectToDatabase();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("connectToDatabase() failed.  Aborting execution !!\n");
				return ERRONEOUS_EXIT;
			}
			writeToMainLogFile("Connected to Database \n");

			stmt = con.createStatement();
			String cronType = "";
			int maxThread = 0;
			int runThread = 0;
			String cronRunStatus = "";
			ResultSet rsNew = stmt.executeQuery(
					"SELECT CRON_TYPE,MAX_THREAD,RUN_THREAD,CRON_SCHEDULE_TIME,CRON_RUN_STATUS FROM PRD_CRON_CONTROL WHERE CRON_TYPE='VISION_XLUPLOAD' ");
			if (rsNew.next()) {
				cronType = rsNew.getString("CRON_TYPE");
				maxThread = rsNew.getInt("MAX_THREAD");
				runThread = rsNew.getInt("RUN_THREAD");
				cronRunStatus = rsNew.getString("CRON_RUN_STATUS");
			}
			rsNew.close();
			if ("Y".equalsIgnoreCase(cronRunStatus) && "Y".equalsIgnoreCase(schedulerRunStatus)
					&& runThread < maxThread) {

				mainLogfile = new FileWriter(mainLogFileName, true);// open the Error log file in append mode.
				bufferedWriter = new BufferedWriter(mainLogfile);
				mainLogfile.close();
				bufferedWriter.close();

				Boolean firstTimeFlag = true;
				for (;;) {
					uploadTableName = "";
					uploadFileName = "";
					makerId = 0;
					uploadSequence = 0;
					uploadGroup = 0;
					sheetName = "";
					try {
						if ("MSSQL".equalsIgnoreCase(databaseType)) {
							rs = stmt.executeQuery(
									"Select T1.SHEET_NAME,T1.TABLE_NAME,SUBSTRING(T1.FILE_NAME, 0, len(T1.FILE_NAME)-4) FILE_NAME,T1.MAKER, T1.UPLOAD_SEQUENCE, T2.UPLOAD_GROUP From VISION_UPLOAD T1, VISION_TABLES T2"
											+ " Where UPLOAD_SEQUENCE = (Select Min(UPLOAD_SEQUENCE) From VISION_UPLOAD Where "
											+ " UPLOAD_STATUS = 1 AND FILE_NAME LIKE ('%.XLSX'))"
											+ " And T1.TABLE_NAME = T2.TABLE_NAME ");
						} else {
							rs = stmt.executeQuery(
									"Select T1.SHEET_NAME,T1.TABLE_NAME,substr(T1.FILE_NAME, 0, length(T1.FILE_NAME)-5) FILE_NAME,T1.MAKER, T1.UPLOAD_SEQUENCE, T2.UPLOAD_GROUP From VISION_UPLOAD T1, VISION_TABLES T2"
											+ " Where UPLOAD_SEQUENCE = (Select Min(UPLOAD_SEQUENCE) From VISION_UPLOAD Where "
											+ " UPLOAD_STATUS = 1 AND FILE_NAME LIKE ('%.XLSX'))"
											+ " And T1.TABLE_NAME = T2.TABLE_NAME ");
						}
						if (rs.next()) {
							sheetName = rs.getString("SHEET_NAME");
							uploadTableName = rs.getString("TABLE_NAME");
							uploadFileName = rs.getString("FILE_NAME");
							makerId = rs.getInt("MAKER");
							uploadSequence = rs.getInt("UPLOAD_SEQUENCE");
							uploadGroup = rs.getInt("UPLOAD_GROUP");
							firstTimeFlag = false;
						} else {
							if (firstTimeFlag)
								writeToMainLogFile("Vision Upload - No Records to Process");
							else
								writeToMainLogFile("Vision Upload - Completed Successfully");
							doFinishingSteps();
							fileReader = null;
							return SUCCESSFUL_EXIT;
						}
					} catch (Exception e) {
						doFinishingSteps();
						writeToMainLogFile("" + e.getMessage());
						logWriter(e.getMessage(), uploadLogFileName);
						return ERRONEOUS_EXIT;
					}

					reqsProcessed++;

					totalInputFileColumns = 0;
					totalFailureRecords = 0;
					totalSuccessRecords = 0;

					uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + uploadSequence + "_" + makerId
							+ "_" + GetCurrentDateTime() + ".err";

					if (uploadGroup == MGT_UPLOAD) {
						retVal = mgtUpload();
						writeToMainLogFile(" ");
						/* Update the status of the run, now */
						if (retVal == STATUS_SUCCESS || retVal == SUCCESSFUL_EXIT) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							tempRetVal = updateGroupUploadStatus(STATUS_SUCCESS);
							con.commit();
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (MGT) Tables : SUCCESS");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.rollback();

							tempRetVal = updateGroupUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (MGT) Tables : FAILURE !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_PARTIAL_SUCCESS) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.commit();

							tempRetVal = updateGroupUploadStatus(STATUS_PARTIAL_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile("Final Upload Status for Group [" + uploadGroup
									+ "] (MGT) Tables : PARTIAL SUCCESS !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						}
						writeToMainLogFile("Return Status:" + retVal);
						/*
						 * Print the error report, if it anything other than STATUS_SUCCESS
						 */
						if (retVal == STATUS_PARTIAL_SUCCESS || retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT) {
							writeToMainLogFile("printMgtUploadErrorReport:");
							printMgtUploadErrorReport();
						}

						if (lockFlag == 'Y') {
							retVal = unlockMgtTablesAfterUpload();
							if (retVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
						}
						/*
						 * Clean up the entries in the Mgt_Validation_Errors tables, if any
						 */
						cleanupMGTUPLTableEntries();
						con.commit();
					} else if (uploadGroup == DC_UPLOAD || uploadGroup == DC_UPLOAD_SHORT) {
						/* Call the function to perform DC Uploads */
						retVal = dcUpload();
						writeToMainLogFile(" ");

						/* Update the status of the run, now */
						if (retVal == STATUS_SUCCESS) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.commit();

							tempRetVal = updateGroupUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (DC) Tables : SUCCESS");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (DC) Tables : FAILURE !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_PARTIAL_SUCCESS) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.commit();

							tempRetVal = updateGroupUploadStatus(STATUS_PARTIAL_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile("Final Upload Status for Group [" + uploadGroup
									+ "] (DC) Tables : PARTIAL SUCCESS !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						}

						/*
						 * Print the error report, if it anything other than STATUS_SUCCESS
						 */
						if (retVal == STATUS_PARTIAL_SUCCESS || retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT
								|| retVal == STATUS_SUCCESS_VALIDATION_FAILED) {
							printDcUploadErrorReport();
						}

						/*
						 * Clean up the entries in the Mgt_Validation_Errors tables, if any
						 */
						cleanupDcUPLTableEntries();

						if (lockFlag == 'Y') {
							retVal = unlockDCTablesAfterUpload();
							if (retVal == ERRONEOUS_EXIT) {
								doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						con.commit();
					} else if (uploadGroup == DAILY_DC_UPLOAD) {
						/* Call the function to perform Daily-DC Uploads */
						retVal = dailyDcUpload();
						writeToMainLogFile(" ");

						/* Update the status of the run, now */
						if (retVal == STATUS_SUCCESS) {
							/*
							 * Commit the whole work done, since everything has gone thru
							 */
							con.commit();

							tempRetVal = updateGroupUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (Daily DC) Tables : SUCCESS");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile("Final Upload Status for Group [" + uploadGroup
									+ "] (Daily DC) Tables : FAILURE !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else if (retVal == STATUS_PARTIAL_SUCCESS) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.commit();

							tempRetVal = updateGroupUploadStatus(STATUS_PARTIAL_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile("Final Upload Status for Group [" + uploadGroup
									+ "] (Daily DC) Tables : PARTIAL SUCCESS !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						}
						/** Print the error report, if it anything other than STATUS_SUCCESS */
						if (retVal == STATUS_PARTIAL_SUCCESS || retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT
								|| retVal == STATUS_SUCCESS_VALIDATION_FAILED) {
							printDailyDcUploadErrorReport();
						}
						/*
						 * Clean up the entries in the FIN_DLY_ADJ_Errors tables, if any
						 */
						cleanupDailyDcUPLTableEntries();

						if (lockFlag == 'Y') {
							retVal = unlockDailyDCTablesAfterUpload();
							if (retVal == ERRONEOUS_EXIT) {
								doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						con.commit();
					} else if (uploadGroup == FC_UPLOAD) {
						/* Call the function to perform DC Uploads */
						retVal = fcUpload();
						writeToMainLogFile(" ");

						/* Update the status of the run, now */
						if (retVal == STATUS_SUCCESS || retVal == SUCCESSFUL_EXIT) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							con.commit();

							tempRetVal = updateUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
								// doFinishingSteps();
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (FC) Tables : SUCCESS");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							// doFinishingSteps();
						} else if (retVal == STATUS_FAILURE || retVal == ERRONEOUS_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
								// doFinishingSteps();
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] (FC) Tables : FAILURE !!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						}

						/* Clean up the entries in the UPL tables, if any */
						cleanupFcUPLTableEntries();

						if (lockFlag == 'Y') {
							retVal = unlockFCTablesAfterUpload();
							if (retVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}

						}
						con.commit();
					} else if (uploadGroup == GENERIC_UPLOAD) {
						writeToMainLogFile("Generic Module Upload started!!!!!!!!!!!!!\n");
						if (DEBUG_MODE == YES)
							writeToMainLogFile("Invoking the Generic Upload module\n");

						retVal = genericUpload();
						if (retVal == SUCCESSFUL_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						} else {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						if (lockFlag == 'Y') {
							tempRetVal = unlockTableAfterUpload();
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						con.commit();

						// doFinishingSteps();
					} else if (uploadGroup == REPORTS_UPLOAD) {
						writeToMainLogFile("Reports Upload started!!!!!!!!!!!!!\n");
						if (DEBUG_MODE == YES)
							writeToMainLogFile("Invoking the Reports Upload module\n");

						retVal = reportsUpload();
						if (retVal == SUCCESSFUL_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						} else {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						if (lockFlag == 'Y') {
							tempRetVal = unlockTableAfterUpload();
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						con.commit();
						// doFinishingSteps();
					} else if (uploadGroup == GENERIC_UPLOAD_RG) {
						writeToMainLogFile("Generic RG Module Upload  started!!!!!!!!!!!!!\n");
						if (DEBUG_MODE == YES)
							writeToMainLogFile("Invoking the Generic Upload RG module\n");

						retVal = genericUploadRg();
						if (retVal == SUCCESSFUL_EXIT) {
							tempRetVal = updateUploadStatus(STATUS_SUCCESS);
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						} else {
							tempRetVal = updateUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						if (lockFlag == 'Y') {
							tempRetVal = unlockTableAfterUpload();
							if (tempRetVal == ERRONEOUS_EXIT) {
								// doFinishingSteps();
								retVal = ABORT_EXECUTION;
							}
						}
						con.commit();
						if (valvalue == SUCCESSFUL_EXIT && retVal == SUCCESSFUL_EXIT) {
							updateTemplateheadersStatus(tableAttribute1);
							updateProcessControlStatus(tableAttribute1);
						}
						// doFinishingSteps();
					}

					else {
						if (DEBUG_MODE == YES)
							writeToMainLogFile("Invoking the Special Upload module\n");

						retVal = specialUpload();
						if (retVal == SUCCESSFUL_EXIT) {
							/*
							 * Commit the whole work done, since, everything has gone thru
							 */
							tempRetVal = updateGroupUploadStatus(STATUS_SUCCESS);
							con.commit();
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
								// doFinishingSteps();
							}

							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile("Final Upload Status for Group [" + uploadGroup + "] Tables : SUCCESS");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						} else {
							/*
							 * Rollback the whole work done, since, the failure of one member of the Group
							 * results in the failure of the wholebatch.
							 */

							if (DEBUG_MODE == YES) {
								writeToMainLogFile("Inside updateGroupUploadStatus");
							}

							con.rollback();

							tempRetVal = updateGroupUploadStatus(STATUS_FAILURE);
							if (tempRetVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}

							if (currentGroupCtr < totalGroupUploadTables) {
								writeToMainLogFile("Further processing of subsequent tables has been aborted - FYI.\n");
							}
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							writeToMainLogFile(
									"Final Upload Status for Group [" + uploadGroup + "] Tables : FAILURE !!!!!!");
							writeToMainLogFile(
									"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						}

						if (DEBUG_MODE == YES) {
							writeToMainLogFile("After updateGroupUploadStatus");
						}

						retVal = truncateUPLTables();
						if (lockFlag == 'Y') {
							retVal = unlockTableGroupAfterUpload();
							if (retVal == ERRONEOUS_EXIT) {
								retVal = ABORT_EXECUTION;
							}
						}
					}
					if (retVal == ABORT_EXECUTION) {
						abortFlag = 'Y';
						break;
					}
					if (retVal == SUCCESSFUL_EXIT) {
						writeToMainLogFile("Upload to the table [" + uploadTableName + "] Succeeded.\n");
						uploadRetVal = SUCCESSFUL_EXIT;
					} else {
						writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
						uploadRetVal = ERRONEOUS_EXIT;
					}
					if (reqsProcessed == 0) {
						writeToMainLogFile("There were no pending requests found in the VISION_UPLOAD table - FYI\n");
						uploadRetVal = ERRONEOUS_EXIT;
					} else {
						writeToMainLogFile("Total requests processed for this run : [" + reqsProcessed + "]\n");
						uploadRetVal = SUCCESSFUL_EXIT;
					}
					con.commit();
				}
			} else {
				writeToMainLogFile("VISION_XLUPLOAD Cron is not Running");
			}

			doFinishingSteps();
		} catch (Exception e) {
			System.out.print(e.getMessage());
			sqlErrorLog = e.getMessage();
			writeSQLErrToMainLogFile(e.getMessage());
			doFinishingSteps();
		}
		return uploadRetVal;
	}

	private int genericUpload() {
		String tempStr = "";
		try {
			/* Check if upload is allowed for this table */

			retVal = checkIfUploadIsAllowed();
			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			/* Check if the table has been locked by some build process */
			retVal = checkForTableLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return ERRONEOUS_EXIT;
			}

			/* Lock the table to proceed with Upload */
			retVal = lockTableForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for XL Upload. Cannot proceed now with upload !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			/* Set the status to In-Progress mode for the upload request */
			logWriter("Set the status to In-Progress mode for the upload request:" + uploadTableName,
					uploadLogFileName);
//			System.out.println("Set the status to In-Progress mode  for the upload request:"+uploadTableName);

			retVal = updateUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT) {
				doFinishingSteps();
				return ABORT_EXECUTION;
			}
			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile("Uploading to the table [" + uploadTableName + "]");
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";

			logWriter("uploadLogFileName : " + uploadLogFileName, uploadLogFileName);
			try {
				logfile = new FileWriter(uploadLogFileName);// Open a Log file
															// in Write mode
				bufferedWriter = new BufferedWriter(logfile);
				logfile.close();
				bufferedWriter.close();

			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName + "] for logging process steps :\n "
						+ e.getMessage());
				writeToMainLogFile("Aborting further processing of this request - FYI:" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";

			try {
				/*
				 * bufferedReader1 = new BufferedReader(new InputStreamReader(new
				 * FileInputStream(uploadFile),"UTF8"));
				 */
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + tempStr + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + tempStr + "]\nSystem Msg [" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI:" + e.getMessage());
				logWriter("Aborting further processing of this request - FYI:" + e.getMessage(), uploadLogFileName);

				retVal = updateUploadStatus(STATUS_FAILURE);

				if (retVal == ERRONEOUS_EXIT)
					return ABORT_EXECUTION;

				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			logWriter(" Vision Upload Started \t  Upload File Name [" + uploadFileName + "]\t Upload Sequence: ["
					+ uploadSequence + "] \n", uploadLogFileName);
			logWriter("------------------------------------------------------------\n", uploadLogFileName);
			logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);
			originalTableName = uploadTableName;

			/*
			 * Decide the table name, depending on whether the specified table or the bulk
			 * table needs to be hit, with the upload
			 */
			if (valueYes.equalsIgnoreCase(verificationRequired)) {
				uploadTableName = uploadTableName + "_PEND";
			}
			logWriter("Table name obtained \t:[" + originalTableName + "]\n", uploadLogFileName);

			if (DEBUG_MODE == YES)
				logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);

			retVal = checkForTblExistence();

			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			retVal = loadTxtFileColumnNames(uploadFile);
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = validateTblCols();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = chkColCntForEachRow();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = fetchTableIndex();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);

			TAB_FLAG = 'Y';

			retVal = uploadFileData();
			TAB_FLAG = 'N';

			if (finalReturnValue == ERRONEOUS_EXIT) {
				con.rollback();
			} else {
				con.commit();
			}
			printSummaryReport(finalReturnValue);
			return (finalReturnValue);
		} catch (Exception e) {
//			System.out.println("genericUpload : " + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int checkForTblExistence() {
		try {
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Select Count(1)  From sys.tables Where name = '" + uploadTableName + "'";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "Select Count(1) From ALL_TABLES Where table_name = '" + uploadTableName
						+ "' AND UPPER(OWNER)=UPPER('" + username + "')";
			}
			rs = stmt.executeQuery(query);
			while (rs.next())
				recsCount = rs.getInt(1);
			if (recsCount == 0) {
				logWriter("The table [" + uploadTableName + "] could not be found for uploading !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			return SUCCESSFUL_EXIT;
		} catch (SQLException ex) {
			sqlErrorLog = ex.getMessage();
			sqlLogWriter("Error trying to validate presence of upload table :" + ex.getMessage());
			return ERRONEOUS_EXIT;
		} catch (Exception ex) {
			logWriter("checkForTblExistence :" + ex.getMessage(), uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
	}

	private int loadTxtFileColumnNames(String fileName) {
		try {
			String tempStr = "";
			char firstTimeFlag = 'Y';
			retVal = SUCCESSFUL_EXIT;
			tempStr = "";
			String headerRow = "";
			String[] columnNames = null;
			fileColumnsNames = new ArrayList();
			fileColumnsIndexColumn = new ArrayList();
			fileColumnsNullable = new ArrayList();
			// logWriter("Before Read !!"+(new Date()).toString());
			if (fileReader == null) {
				// bufferedReader1 = new BufferedReader(new
				// InputStreamReader(new FileInputStream(fileName),"UTF8"));
				fileReader = new FileInputStream(new File(uploadFile));
			}
			// logWriter("After Read !!"+(new Date()).toString());
			int i = 0;
			workbook = new XSSFWorkbook(fileReader);
			Sheet firstSheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = firstSheet.iterator();
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() != 0) {
					break;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (ValidationUtil.isValid(cell.getStringCellValue())) {
						headerRow = headerRow + cell.getStringCellValue() + ",";
					}
					i++;
				}
			}
			fileReader.close();

			/*
			 * bufferedReader1.mark(1); //If there is a Persian Words the First Char in the
			 * Data File is reading some Junk Chars- To avoid these lines are used.
			 * if(bufferedReader1.read()!=0xFEFF) bufferedReader1.reset(); headerRow =
			 * bufferedReader1.readLine();
			 */
			/* bufferedReader1.close(); */

			columnNames = headerRow.split(",");
			if (columnNames.length == 0) {
				logWriter("No data available in the text file, for upload !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			totalInputFileColumns = columnNames.length;

			for (int Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				tempStr = columnNames[Ctr1];
				tempStr = tempStr.toUpperCase();
				if (tempStr.length() > longestColumn)
					longestColumn = tempStr.length();

				if (tempStr.length() == 0) {
					logWriter("An extra tab or space(s) is found in the header lines !!", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				} else if (chkForAllSpaces(tempStr) == YES) {
					logWriter(tempStr + " - A column heading with just spaces is found in the header line !!",
							uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
				if (Arrays.asList(exceptionColList).contains(tempStr)) {
					if (firstTimeFlag == 'Y') {
						logWriter(tempStr + " - The following columns should not be given in the input file !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter(tempStr, uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
				fileColumnsNames.add(tempStr); // add [tempStr] to [fileColumns]
												// array list
				fileColumnsIndexColumn.add('N'); // fileColumns[Ctr2].indexColumn
													// = 'N';
				// fileColumnsNullable.add('N'); //fileColumns[Ctr2].nullable =
				// 'N';
				tempStr = "";
			}
			logWriter("Return Value[" + retVal + "]", uploadLogFileName);
			return (retVal);
		} catch (Exception e) {
			writeToMainLogFile("loadTxtFileColumnNames :" + e.getMessage());
			e.printStackTrace();
//			System.out.println("loadTxtFileColumnNames" + e);
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int chkForAllSpaces(String paramStrTxt) {
		char[] paramStr = paramStrTxt.toCharArray();
		int Ctr1 = 0;
		int returnValue = NO;
		for (Ctr1 = 0; Ctr1 < paramStr.length; Ctr1++) {
			if (paramStr[Ctr1] == ' ') {
				returnValue = YES;
				return returnValue;
			}
			if (!ValidationUtil.isValid(paramStr[Ctr1])) {
				returnValue = YES;
				return returnValue;
			}
		}
		return returnValue;
	}

	private int validateTblCols() {
		int Ctr1 = 0, Ctr2 = 0;
		char foundFlag = 'Y';
		char firstTimeFlag = 'Y';
		retVal = SUCCESSFUL_EXIT;
		sqlStatement = "";
		columnNamelst = new ArrayList();
		nullable = new ArrayList();
		columnSizelst = new ArrayList();
		quotedTypelst = new ArrayList();
		dataScalelst = new ArrayList();
		dateTypelst = new ArrayList();
		decimalsChecklst = new ArrayList();
		dataType = new ArrayList<String>();
		totalTableColumns = 0;
		try {
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Select Upper(LTrim(RTrim(T1.Name))) COLUMN_NAME, CASE WHEN (T1.Is_Nullable =0 ) THEN 'N' ELSE 'Y' END  NULLABLE, "
						+ " Case When T1.Precision = 0 Then T1.Max_Length Else T1.Precision End PRECISION, "
						+ "T2.Name  DATA_TYPE ,T1.Scale DATA_SCALE " + " From sys.columns T1 Inner Join sys.types T2 "
						+ "On T1.User_Type_id = T2.User_Type_id Inner Join Sys.all_objects T3 On T1.Object_id = T3.object_id "
						+ "Where T3.Name = '" + uploadTableName + "' order by COLUMN_ID";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "Select Upper(RTrim(column_name)) column_Name, nullable,Case When data_precision Is Null Then data_length Else data_precision End Precision, "
						+ " data_type, DATA_SCALE From all_tab_columns Where table_name = '" + uploadTableName
						+ "' AND UPPER(OWNER)=UPPER('" + username + "') order by COLUMN_ID";
			}
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				columnNamelst.add(rs.getString("column_Name"));
				nullable.add(rs.getString("nullable"));
				dataPrecision.add(rs.getInt("Precision"));
				dataType.add(rs.getString("data_type"));
				if (Arrays.asList(nonQuotedTypes).contains(rs.getString("data_type"))) {
					quotedType = valueNo;
				} else {
					quotedType = valueYes;
				}
				quotedTypelst.add(quotedType);
				if (rs.getString("data_type").equalsIgnoreCase("DATE")) {
					dateType = valueYes;
					columnSize = 10;
					columnSizelst.add(columnSize);
				} else {
					dateType = valueNo;
					columnSize = rs.getInt("Precision");
					columnSizelst.add(columnSize);
				}
				dateTypelst.add(dateType);
				dataScalelst.add(rs.getInt("DATA_SCALE"));
				if (rs.getInt("DATA_SCALE") != 0) {
					decimalsCheck = valueYes;
				} else {
					decimalsCheck = valueNo;
				}
				decimalsChecklst.add(decimalsCheck);

				totalTableColumns++;
			}
			tableColumnList.addAll(columnNamelst);
			tableColumnList.addAll(nullable);
			tableColumnList.addAll(columnSizelst);// data Precision
			tableColumnList.addAll(quotedTypelst);
			tableColumnList.addAll(dataScalelst);// after Decimals
			tableColumnList.addAll(dateTypelst);
			tableColumnList.addAll(decimalsChecklst);
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				foundFlag = 'N';
				for (Ctr2 = 0; Ctr2 < totalTableColumns; Ctr2++) {
					if (fileColumnsNames.get(Ctr1).equals(columnNamelst.get(Ctr2))) {
						fileColumnsColumnSize.add(Ctr1, columnSizelst.get(Ctr2));
						fileColumnsQuotedType.add(Ctr1, quotedTypelst.get(Ctr2));
						fileColumnsNullable.add(Ctr1, nullable.get(Ctr2));
						fileColumnsDecimalsCheck.add(Ctr1, decimalsChecklst.get(Ctr2));
						fileColumnsAfterDecimals.add(Ctr1, dataScalelst.get(Ctr2));
						fileColumnsDataType.add(Ctr1, dateTypelst.get(Ctr2));

						foundFlag = 'Y';
						break;
					}
				}
				if (foundFlag == 'N') {
					if (firstTimeFlag == 'Y') {
						logWriter("The following columns names are invalid for the selected table name !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("[" + fileColumnsNames.get(Ctr1) + "]", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
			}
			return retVal;
		} catch (Exception e) {
			writeToMainLogFile("validateTblCols() :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int chkColCntForEachRow() {
		int Ctr1 = 0, Ctr2 = 0;
		int lineCtr = 1;
		char ch;
		String tempFileData[];
		String localString = "";
		int tempFileDataCtr = 0;
		char firstTimeFlag = 'Y';
		int tempRetVal = 0;
		retVal = SUCCESSFUL_EXIT;
		int rowCount = 0;
		int validRowCount = 0;
		int invalidRowCount = 0;
		try {
			fileReader = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;
			// Workbook workbook = new XSSFWorkbook(fileReader);

			Sheet firstSheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String columnName = (String) fileColumnsNames.get(Ctr2);
						/*
						 * Changed by DD Cause[java.lang.Character cannot be cast to java.lang.String]
						 */
						// String columnSpecificDataType=(String)
						// dateTypelst.get(columnNamelst.indexOf(columnName));

						String columnSpecificDataType = String
								.valueOf(dateTypelst.get(columnNamelst.indexOf(columnName)));
						String specificData = "";
						// System.out.println("columnNamelst.indexOf(columnName)"+columnNamelst.indexOf(columnName));
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} 
//							else if (cellType == Cell.CELL_TYPE_STRING) {
								else if (cellType == CellType.STRING) {
								specificData = cell.getStringCellValue();
							} 
//						else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										} catch (Exception e) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										}
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									long part = 0;
									try {
										if (ValidationUtil.isValid(TempspecificData2)) {
											part = Long.parseLong(TempspecificData2);
											if (part <= 0)
												specificData = String.valueOf(
														(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
										}
									} catch (Exception e) {
										e.printStackTrace();
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}

								}
							}
						} else {
							specificData = " ";
						}
						if (valueYes.equalsIgnoreCase(fileColumnsDecimalsCheck.get(Ctr2).toString())) {
							String returnValue = decimalValueFormatter(specificData,
									Integer.parseInt(fileColumnsColumnSize.get(Ctr2).toString()),
									Integer.parseInt(fileColumnsAfterDecimals.get(Ctr2).toString()));
							specificData = returnValue;
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
						}

					}
					if (lineFetched == null) {
						break;
					} else {
						wordsArray = lineFetched.split("\n");
						lineFetched = "";
						for (String each : wordsArray) {
							lines.add(each);
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader.close();
			for (Ctr1 = 0; Ctr1 < lines.size(); Ctr1++) {
				tempFileData = lines.get(Ctr1).split("\t");
				/*
				 * if(Ctr1==0 || Ctr1 == 1){ logWriter("*********************************");
				 * logWriter( "New Each Line ["+lines.get(Ctr1)+"] ");
				 * logWriter("*********************************"); }
				 */
				/*
				 * if(tempFileData.length < fileColumnsNames.size()){ logWriter(
				 * "Invalid no. of Columns found on the Line["
				 * +appendZeroforCtr(Ctr1)+"] in ["+uploadFileName+"]"); return ERRONEOUS_EXIT;
				 * }else
				 */if (tempFileData.length > fileColumnsNames.size()) {
					logWriter("An Extra Column found on the Header Line [" + appendZeroforCtr(Ctr1) + "] in ["
							+ uploadFileName + "]", uploadLogFileName);
					return ERRONEOUS_EXIT;
				}
				for (Ctr2 = 0; Ctr2 < tempFileData.length; Ctr2++) {
					/*
					 * try{ System.out.println(tempFileData[Ctr2].matches("[ ]*" ));
					 * }catch(Exception e){ System.out.println(e); }
					 */
					if (tempFileData[Ctr2].matches("[ ]*")
							&& valueNo.equalsIgnoreCase("" + fileColumnsNullable.get(Ctr2).toString() + "")) {
						printUploadErrors("" + appendZeroforCtr(lineCtr + 1) + "\t\tData for the mandatory column ["
								+ fileColumnsNames.get(Ctr2) + "] is Null !!");
						return ERRONEOUS_EXIT;
					}
					if (tempFileData[Ctr2].equalsIgnoreCase(" ")) {
						if (valueNo.equalsIgnoreCase("" + fileColumnsNullable.get(Ctr2).toString() + "")) {
							printUploadErrors("" + appendZeroforCtr(lineCtr + 1) + "\t\tData for the mandatory column ["
									+ fileColumnsNames.get(Ctr2) + "] is Null !!");
							return ERRONEOUS_EXIT;
						}
					}
					if (valueNo.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr2).toString())) {
						tempRetVal = doNumericValidation(tempFileData[Ctr2],
								Integer.parseInt(fileColumnsAfterDecimals.get(Ctr2).toString()));
						if (tempRetVal == ERRONEOUS_EXIT) {
							printUploadErrors("" + appendZeroforCtr(lineCtr + 1)
									+ "\t\tInvalid numeric data for column [" + fileColumnsNames.get(Ctr2) + "] !!");
							retVal = ERRONEOUS_EXIT;
						}
					}
					if (valueYes.equalsIgnoreCase(fileColumnsDecimalsCheck.get(Ctr2).toString())) {
						tempRetVal = doDecimalsValidation(tempFileData[Ctr2],
								Integer.parseInt(fileColumnsColumnSize.get(Ctr2).toString()),
								Integer.parseInt(fileColumnsAfterDecimals.get(Ctr2).toString()));
						if (tempRetVal == ERRONEOUS_EXIT) {
							printUploadErrors("" + appendZeroforCtr(lineCtr + 1) + "\t\tData for column ["
									+ fileColumnsNames.get(Ctr2) + "] exceeds allowed precision/scale in table !!");
							retVal = ERRONEOUS_EXIT;
						}
					}
				}
				lineCtr++;
			}
			if (lines.size() < 1) {
				logWriter("No Columns found in the text File[" + uploadFileName + "]", uploadLogFileName);
				retVal = ERRONEOUS_EXIT;
			}

			totalInputFileRecords = lineCtr - 1;
			return retVal;
		} catch (Exception e) {
			writeToMainLogFile("Error While Checking data in each Cell : [" + e.getMessage() + "] on Row[" + (Ctr1 + 1)
					+ "] Col[" + Ctr2 + "] :" + e.getMessage());
			logWriter("Error While Checking data in each Cell on File[" + uploadFileName + "] Row[" + (Ctr1 + 1)
					+ "] Col[" + Ctr2 + "]:" + e.getMessage(), uploadLogFileName);
			logWriter("Note : No Sheet or Cell Reference allowed in the Uploaded sheet", uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
	}

	private int checkIfUploadIsAllowed() {
		try {
			rs = stmt.executeQuery("Select VERIFICATION_REQD From VISION_TABLES Where TABLE_NAME = '" + uploadTableName
					+ "' And UPLOAD_ALLOWED = 'Y'");
			while (rs.next())
				verificationRequired = rs.getString("VERIFICATION_REQD");
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeSQLErrToMainLogFile("Error trying to fetch upload status for this table :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int checkIfUploadIsRG() {
		try {
			rs = stmt.executeQuery("Select TABLE_ATTRIBUTE_1 From VISION_TABLES Where TABLE_NAME = '" + uploadTableName
					+ "' And UPLOAD_ALLOWED = 'Y'");
			while (rs.next())
				tableAttribute1 = rs.getString("TABLE_ATTRIBUTE_1");
			if (isValid(tableAttribute1))
				return SUCCESSFUL_EXIT;
			else
				return NO_VALIDATION;
		} catch (Exception e) {
			writeSQLErrToMainLogFile("Error trying to fetch upload status for this table :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int fetchTableIndex() {
		int Ctr1 = 0, Ctr2 = 0;
		char foundFlag = 'N';
		char firstTimeFlag = 'Y';
		ArrayList indexcolumnNamelst = new ArrayList();
		retVal = SUCCESSFUL_EXIT;
		try {
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Select LTrim(Rtrim(T1.Name)) INDEX_NAME  "
						+ " From sys.indexes T1 Inner Join sys.all_objects T2  " + "On T1.Object_id = T2.Object_id  "
						+ "Where T2.Name = '" + uploadTableName + "' And T1.Name like '%_PK%'";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "Select RTrim(INDEX_NAME) INDEX_NAME From USER_INDEXES Where TABLE_NAME = '" + uploadTableName
						+ "'  AND UPPER(TABLE_OWNER)=UPPER('" + username + "') And INDEX_NAME Like '%_PK'";
			}
			rs = stmt.executeQuery(query);
			while (rs.next())
				indexName = rs.getString("INDEX_NAME");
		} catch (Exception e) {
			sqlLogWriter("Error trying to fetch index name from catalog :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		try {
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Select LTrim(Rtrim(T2.Name))COLUMN_NAME "
						+ " From sys.index_columns T1 Inner Join sys.columns T2 "
						+ " On T1.column_id = T2.column_id And " + " T1.Object_id = T2.object_id "
						+ " Inner Join Sys.all_objects T3 " + "On T1.Object_id = T3.object_id "
						+ "Inner Join Sys.indexes T4 " + " On T1.object_id = T4.object_id And  "
						+ " T1.index_id = T4.index_id " + " Where T3.Name = '" + uploadTableName + "' "
						+ "And T4.Name = '" + indexName + "'";
			} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
				query = "Select COLUMN_NAME from USER_IND_COLUMNS Where TABLE_NAME = '" + uploadTableName
						+ "' And INDEX_NAME = '" + indexName + "'";
			}
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				columnName = rs.getString("COLUMN_NAME");
				for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
					if (fileColumnsNames.get(Ctr1).toString().equalsIgnoreCase(columnName)) {
						foundFlag = 'Y';
						fileColumnsIndexColumn.set(Ctr1, valueYes);
						fileColumnsNullable.set(Ctr1, valueNo);
					}
				}
				if (foundFlag == 'N') {
					if (firstTimeFlag == 'Y') {
						logWriter("The below mandatory index columns are not present in the input file !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter(columnName, uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
			}
			return (retVal);
		} catch (Exception e) {
			sqlLogWriter("Error selecting from all_ind_columns table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int uploadFileData() {
		try {

			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			String tempFileData[];
			char ch;
			int lineCtr = 1;
			String tempStr;
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			totalInputFileRecords = 0;
			retVal = SUCCESSFUL_EXIT;
			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;
			Workbook workbook = new XSSFWorkbook(fileReader1);

			Sheet firstSheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					lineFetched = "";
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String columnName = (String) fileColumnsNames.get(Ctr2);
						String columnSpecificDataType = dataType.get(columnNamelst.indexOf(columnName));
						String specificData = "";
						long specificDataMod = 0;
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							//int cellType = evaluator.evaluate(cell).getCellType();
							CellType cellType = evaluator.evaluate(cell).getCellType();
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} else if (cellType == CellType.STRING) {
								specificData = cell.getStringCellValue();
							} else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										} catch (Exception e) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										}
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}

								}
							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							if (ValidationUtil.isValid(specificDataMod) && specificDataMod != 0)
								lineFetched = lineFetched + specificDataMod;
							else
								lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							if (ValidationUtil.isValid(specificDataMod) && specificDataMod != 0)
								lineFetched = lineFetched + "	" + specificDataMod;
							else
								lineFetched = lineFetched + "	"
										+ specificData.replaceAll("'", "").replaceAll("\"", "");
						}
					}
					if (lineFetched == null) {
						break;
					} else {
						uploadData = new String[rowColumnCnt];
						String[] uploadDataTempArr = lineFetched.split("\t");
						for (int a = 0; a < uploadData.length; a++) {
							if (a < uploadDataTempArr.length)
								uploadData[a] = uploadDataTempArr[a];
							else
								uploadData[a] = "";
						}
						lineCtr++;
						if (valueYes.equals(verificationRequired)) {
							retVal = doVerifInsUpdOperations(lineCtr);
						} else {
							if (uploadTableName.contains("FEED_STG")) {
								retVal = doInsertUpdateOperationsForStagingTable(lineCtr);
							} else {
								retVal = doInsertUpdateOperations(lineCtr);
							}
						}
						if (retVal == ERRONEOUS_EXIT)
							finalReturnValue = ERRONEOUS_EXIT;

						totalInputFileRecords++;

						/*
						 * wordsArray = lineFetched.split("\n"); lineFetched=""; for(String each :
						 * wordsArray){ lines.add(each); }
						 */
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();

			if (finalReturnValue == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName)) {
				sqlStatement = "Update " + uploadTableName + " Set Fin_Adj_Remaps_Status = 10 Where Record_Indicator = "
						+ FIN_ADJ_REMAP_REC_IND;
				try {
					stmt.executeUpdate(sqlStatement);
				} catch (SQLException ex) {
					sqlLogWriter("Error updating " + uploadTableName + "for line Number " + lineNumber + ":"
							+ ex.getMessage());
					logWriter(sqlStatement, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
				/* Update as 1 - Active Pending */
				sqlStatement = "Update " + uploadTableName + " Set Record_Indicator = 1 Where Record_Indicator = "
						+ FIN_ADJ_REMAP_REC_IND;
				try {
					stmt.executeUpdate(sqlStatement);
				} catch (SQLException ex) {
					sqlLogWriter("Error updating " + uploadTableName + "for line Number " + lineNumber + ":"
							+ ex.getMessage());
					logWriter(sqlStatement, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
				con.commit();
			}
			if (valueYes.equalsIgnoreCase(verificationRequired) && retVal == SUCCESSFUL_EXIT) {
				sqlStatement = "Update " + uploadTableName + " Set RECORD_INDICATOR = 1 Where Exists (Select 1 From "
						+ originalTableName + " Where ";
				countStmt = "Select Count(1) as count From " + uploadTableName + ", " + originalTableName + " Where ";
				for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
					if (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString()))
						continue;
					if (theAndFlag == 'Y') {
						sqlStatement = sqlStatement + "And ";
						countStmt = countStmt + "And ";
					}
					tempStr = "RTrim(" + uploadTableName + "." + fileColumnsNames.get(Ctr1) + ") = RTrim("
							+ originalTableName + "." + fileColumnsNames.get(Ctr1) + ") ";
					sqlStatement = sqlStatement + tempStr;
					countStmt = countStmt + tempStr;
					theAndFlag = 'Y';
				}
				sqlStatement = sqlStatement + " And " + uploadTableName + ".RECORD_INDICATOR = 4) ";

				recsCount = getCount();
				if (recsCount < 0)
					return (ERRONEOUS_EXIT);
				if (recsCount > 0) {
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException ex) {
						sqlLogWriter("Error trying to update pending table (Update #1) for the uploaded data :"
								+ ex.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
						finalReturnValue = ERRONEOUS_EXIT;
					}
				}
				countStmt = "Select Count(1) as count  From " + uploadTableName + " Where RECORD_INDICATOR = 4 ";
				recsCount = getCount();

				if (recsCount < 0)
					return (ERRONEOUS_EXIT);

				if (recsCount > 0) {
					sqlStatement = " Update " + uploadTableName
							+ " Set RECORD_INDICATOR = 2 Where RECORD_INDICATOR = 4 ";
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException e) {
						sqlLogWriter("Error trying to update pending table (Update #2) for the uploaded data :"
								+ e.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
						finalReturnValue = ERRONEOUS_EXIT;
					}
				}
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("uploadFileData() :" + e.getMessage());
			doFinishingSteps();
			finalReturnValue = ERRONEOUS_EXIT;
			return ERRONEOUS_EXIT;
		}
	}

	private int doInsertUpdateOperationsForStagingTable(int paramLineCtr) {
		try {
			int Ctr1 = 0, Ctr2 = 0;
			String tempStr;
			char commaFlag = 'N';
			retVal = SUCCESSFUL_EXIT;
			insertStmt = "Insert Into " + uploadTableName + " (";
			int feedDateIndex = fileColumnsNames.indexOf("FEED_DATE");
			String feedDateSpecificValue = "";
			if (feedDateIndex != -1) {
				feedDateSpecificValue = uploadData[feedDateIndex];
			}

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";

				insertStmt = insertStmt + fileColumnsNames.get(Ctr1);
			}
			/*
			 * insertStmt = insertStmt + ", DATE_CREATION, DATE_LAST_MODIFIED) Values (";
			 */
			if (fileColumnsNames.contains("BUSINESS_DATE")) {
				containsBusinessDate = true;
			} else {
				containsBusinessDate = false;
			}
			if (containsBusinessDate == true)
				insertStmt = insertStmt + ", DATE_CREATION, DATE_LAST_MODIFIED) Values (";
			else
				insertStmt = insertStmt + ", DATE_CREATION, DATE_LAST_MODIFIED, BUSINESS_DATE) Values (";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";

				if (uploadData[Ctr1] == null || uploadData[Ctr1].replaceAll("'", "").trim().length() <= 0) {
					insertStmt = insertStmt + " Null ";
				} else if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
					if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
						if (uploadData[Ctr1].matches("[ ]*")) {
							insertStmt = insertStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
						} else {
							insertStmt = insertStmt
									+ dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1], INPUT_DATE_FORMAT);
						}
					} else {
						if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1)))
							insertStmt = insertStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						else {
							if (uploadData[Ctr1].matches("[ ]*")) {
								insertStmt = insertStmt + "''";
							} else {
								insertStmt = insertStmt + uploadData[Ctr1];
							}
						}
					}
				} else {
					if (uploadData[Ctr1].matches("[ ]*")) {
						insertStmt = insertStmt + "''";
					} else {
						insertStmt = insertStmt + "" + uploadData[Ctr1] + "";
					}
				}
			}
			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName)) {
				tempStr = "," + makerId + "," + makerId + "," + INTERNAL_STATUS + "," + FIN_ADJ_REMAP_REC_IND + ","
						+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ") ";
			} else {
				if (containsBusinessDate == true)
					tempStr = "," + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";
				else
					tempStr = "," + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ","
							+ dbFunctionFormats("DATE_FORMAT", feedDateSpecificValue, INPUT_DATE_FORMAT);
			}
			insertStmt = insertStmt + tempStr;
			updateStmt = "Update " + uploadTableName + " Set ";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					updateStmt = updateStmt + ",";

				updateStmt = updateStmt + "" + fileColumnsNames.get(Ctr1);
				updateStmt = updateStmt + " = ";

				if (uploadData[Ctr1].toString().length() == 0) {
					updateStmt = updateStmt + " Null ";
				} else {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1).toString())) {
						if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString())) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								tempStr = dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								tempStr = dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							tempStr = dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
						updateStmt = updateStmt + tempStr;
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
			}
			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName))
				tempStr = ",RECORD_INDICATOR = " + FIN_ADJ_REMAP_REC_IND + ", MAKER = " + makerId
						+ ", DATE_LAST_MODIFIED =" + getDbFunction("SYSDATE") + "";
			else
				tempStr = ", MAKER = " + makerId + ", DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE") + "";

			updateStmt = updateStmt + tempStr + " Where ";

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString()))
					continue;

				if (commaFlag == 'Y')
					updateStmt = updateStmt + " And ";

				updateStmt = updateStmt + fileColumnsNames.get(Ctr1);
				if (uploadData[Ctr1].length() == 0) {
					updateStmt = updateStmt + " Null ";
				} else {
					updateStmt = updateStmt + " = ";
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1).toString())) {
						if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString())) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							updateStmt = updateStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
				commaFlag = 'Y';
			}
			int recUpdated = 0;
			// try Updating the Records in the Table
			Boolean updateErrorFlag = false;
			/*
			 * try{ recUpdated = stmt.executeUpdate(updateStmt); if(recUpdated != 0){
			 * tempStr = appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded";
			 * logWriter(tempStr); totalSuccessRecords++; } }catch(SQLException e){
			 * updateErrorFlag =true; tempStr = appendZeroforCtr(paramLineCtr) +
			 * "\t\tUpdate Failed"; logWriter(tempStr+updateStmt);
			 * sqlLogWriter(e.getMessage()); finalReturnValue = ERRONEOUS_EXIT;
			 * totalFailureRecords++; } //If the None of the Records in Updated then try
			 * inserting the Record if(recUpdated == 0 && !updateErrorFlag){
			 */
			try {
				stmt.executeUpdate(insertStmt);
				tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded";
				logWriter(tempStr, uploadLogFileName);
				totalSuccessRecords++;
			} catch (SQLException e) {
				tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed";
				sqlLogWriter(tempStr + "|| :" + e.getMessage());
				logWriter(insertStmt, uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
			/* } */
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("insetUpd :" + e.getMessage());
			doFinishingSteps();
			return ERRONEOUS_EXIT;
		}
	}

	private int doInsertUpdateOperations(int paramLineCtr) {
		try {
			int Ctr1 = 0, Ctr2 = 0;
			String tempStr;
			char commaFlag = 'N';
			retVal = SUCCESSFUL_EXIT;
			insertStmt = "Insert Into " + uploadTableName + " (";
			int userLoginIdIndex = -1;
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";
				if (uploadTableName.toUpperCase().contains("VISION_USER")) {
					if (((String) fileColumnsNames.get(Ctr1)).toUpperCase().contains("USER_LOGIN_ID"))
						userLoginIdIndex = Ctr1;
				}

				insertStmt = insertStmt + fileColumnsNames.get(Ctr1);
			}
			insertStmt = insertStmt
					+ ", MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR, DATE_CREATION, DATE_LAST_MODIFIED) Values (";

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";

				if (uploadData[Ctr1] == null || uploadData[Ctr1].replaceAll("'", "").trim().length() <= 0) {
					insertStmt = insertStmt + " Null ";
				} else if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
					if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
						if (uploadData[Ctr1].matches("[ ]*")) {
							insertStmt = insertStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
						} else {
							insertStmt = insertStmt
									+ dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(), INPUT_DATE_FORMAT);
						}
					} else {
						if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
								&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
							insertStmt = insertStmt + dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");
						else if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1)))
							insertStmt = insertStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						else
							insertStmt = insertStmt + "'" + uploadData[Ctr1] + "'";
					}
				} else {
					if (uploadData[Ctr1].matches("[ ]*")) {
						insertStmt = insertStmt + "''";
					} else {
						insertStmt = insertStmt + uploadData[Ctr1];
					}
				}
			}
			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName)) {
				tempStr = "," + makerId + "," + makerId + "," + INTERNAL_STATUS + "," + FIN_ADJ_REMAP_REC_IND + ","
						+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ") ";
			} else {
				tempStr = "," + makerId + "," + makerId + "," + INTERNAL_STATUS + "," + REC_IND_APPROVED + ","
						+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ") ";
			}
			insertStmt = insertStmt + tempStr;
			updateStmt = "Update " + uploadTableName + " Set ";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					updateStmt = updateStmt + ",";

				updateStmt = updateStmt + "" + fileColumnsNames.get(Ctr1);
				updateStmt = updateStmt + " = ";

				if (uploadData[Ctr1].toString().length() == 0) {
					updateStmt = updateStmt + " Null ";
				} else {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1).toString())) {
						if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString())) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								tempStr = dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								tempStr = dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
									&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
								tempStr = dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");

							else
								tempStr = dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
						updateStmt = updateStmt + tempStr;
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
			}
			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName))
				tempStr = ",RECORD_INDICATOR = " + FIN_ADJ_REMAP_REC_IND + ", MAKER = " + makerId
						+ ", DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE");
			else
				tempStr = ", MAKER = " + makerId + ", DATE_LAST_MODIFIED = " + getDbFunction("SYSDATE");

			updateStmt = updateStmt + tempStr + " Where ";

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString()))
					continue;

				if (commaFlag == 'Y')
					updateStmt = updateStmt + " And ";

				updateStmt = updateStmt + fileColumnsNames.get(Ctr1);
				if (!ValidationUtil.isValid(uploadData[Ctr1])) {
					updateStmt = updateStmt + " Is Null ";
				} else {
					updateStmt = updateStmt + " = ";
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1).toString())) {
						if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString())) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
									&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
								updateStmt = updateStmt + dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");
							else
								updateStmt = updateStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
				commaFlag = 'Y';
			}
			int recUpdated = 0;
			// try Updating the Records in the Table
			Boolean updateErrorFlag = false;
			try {
				recUpdated = stmt.executeUpdate(updateStmt);
				if (recUpdated != 0) {
					tempStr = appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded  " + uploadTableName;
					logWriter(tempStr, uploadLogFileName);
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				updateErrorFlag = true;
				tempStr = appendZeroforCtr(paramLineCtr) + "\t\tUpdate Failed  :" + uploadTableName;
				logWriter(tempStr + "  " + updateStmt, uploadLogFileName);
				sqlLogWriter(":\n" + e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
			// If the None of the Records in Updated then try inserting the
			// Record
			if (recUpdated == 0 && !updateErrorFlag) {
				try {
					stmt.executeUpdate(insertStmt);
					tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded  " + uploadTableName;
					logWriter(tempStr, uploadLogFileName);
					totalSuccessRecords++;
				} catch (SQLException e) {
					tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed  " + uploadTableName;
					sqlLogWriter(tempStr + "|| :" + e.getMessage());
					logWriter(insertStmt, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
				}
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("insetUpd :" + e.getMessage());
			doFinishingSteps();
			return ERRONEOUS_EXIT;
		}
	}

	private int doVerifInsUpdOperations(int paramLineCtr) {
		try {
			int Ctr1 = 0, Ctr2 = 0;
			String tempStr;
			char commaFlag = 'N';
			char theAndFlag = 'N';
			retVal = SUCCESSFUL_EXIT;
			recordIndicator = 4;
			int userLoginIdIndex = -1;
			insertStmt = "Insert Into " + uploadTableName + " (";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";
				if (uploadTableName.toUpperCase().contains("VISION_USER")) {
					if (((String) fileColumnsNames.get(Ctr1)).toUpperCase().contains("USER_LOGIN_ID"))
						userLoginIdIndex = Ctr1;
				}
				insertStmt = insertStmt + fileColumnsNames.get(Ctr1);
			}
			insertStmt = insertStmt
					+ ", MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR, DATE_CREATION, DATE_LAST_MODIFIED) Values (";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";
				if (uploadData[Ctr1] == null || uploadData[Ctr1].matches("[ ]*")) {
					insertStmt = insertStmt + " Null ";
				} else if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
					if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
						if (uploadData[Ctr1].matches("[ ]*")) {
							insertStmt = insertStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
						} else {
							insertStmt = insertStmt
									+ dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(), INPUT_DATE_FORMAT);
						}
					} else {
						if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
								&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
							insertStmt = insertStmt + dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");
						else if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1)))
							insertStmt = insertStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						else
							insertStmt = insertStmt + "'" + uploadData[Ctr1] + "'";
					}
				} else {
					if (uploadData[Ctr1].matches("[ ]*")) {
						insertStmt = insertStmt + "''";
					} else {
						insertStmt = insertStmt + uploadData[Ctr1];
					}
				}
			}
			insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + recordIndicator + ","
					+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ") ";

			List<String> colLst = getCloumnNames(originalTableName, stmt);
			StringJoiner columns = new StringJoiner(",");
			for (String cols : colLst) {
				columns.add(cols);
			}
			apprRecInsStmt = "Insert Into " + uploadTableName + " Select " + columns + " From " + originalTableName
					+ " Where ";
			theAndFlag = 'N';
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (fileColumnsIndexColumn.get(Ctr1).toString().equalsIgnoreCase(valueNo))
					continue;

				if (theAndFlag == 'Y')
					apprRecInsStmt = apprRecInsStmt + " And ";

				apprRecInsStmt = apprRecInsStmt + fileColumnsNames.get(Ctr1);

				if (uploadData[Ctr1].length() == 0) {
					apprRecInsStmt = apprRecInsStmt + " Is Null ";
				} else {
					apprRecInsStmt = apprRecInsStmt + " = ";
					if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
						if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								apprRecInsStmt = apprRecInsStmt
										+ dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								apprRecInsStmt = apprRecInsStmt + dbFunctionFormats("DATE_FORMAT",
										uploadData[Ctr1].toString(), INPUT_DATE_FORMAT);
							}
						} else {
							if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
									&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
								apprRecInsStmt = apprRecInsStmt
										+ dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");

							else
								apprRecInsStmt = apprRecInsStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							apprRecInsStmt = apprRecInsStmt + "''";
						} else {
							apprRecInsStmt = apprRecInsStmt + uploadData[Ctr1];
						}
					}
				}

				theAndFlag = 'Y';
			}
			updateStmt = "Update " + uploadTableName + " Set ";
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					updateStmt = updateStmt + ", ";

				updateStmt = updateStmt + fileColumnsNames.get(Ctr1);
				updateStmt = updateStmt + " = ";

				if (!ValidationUtil.isValid(uploadData[Ctr1])) {
					updateStmt = updateStmt + " Null ";
				} else {
					if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
						if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								tempStr = dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								tempStr = dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
									&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
								tempStr = dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");
							else
								tempStr = dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
						updateStmt = updateStmt + tempStr;
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
			}
			updateStmt = updateStmt + ", RECORD_INDICATOR = 4, MAKER = " + makerId + ", DATE_LAST_MODIFIED=  "
					+ getDbFunction("SYSDATE") + " where ";
			theAndFlag = 'N';
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				// if (fileColumnsIndexColumn.get(Ctr1).equals(valueNo))
				if (fileColumnsIndexColumn.get(Ctr1).toString().equalsIgnoreCase(valueNo))
					continue;

				if (theAndFlag == 'Y')
					updateStmt = updateStmt + " And ";

				updateStmt = updateStmt + fileColumnsNames.get(Ctr1);

				if (!ValidationUtil.isValid(uploadData[Ctr1])) {
					updateStmt = updateStmt + " Is Null ";
				} else {
					updateStmt = updateStmt + " = ";
					if (valueYes.equals(fileColumnsQuotedType.get(Ctr1))) {
						if (valueYes.equals(fileColumnsDataType.get(Ctr1))) {
							if (uploadData[Ctr1].matches("[ ]*")) {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", "", INPUT_DATE_FORMAT);
							} else {
								updateStmt = updateStmt + dbFunctionFormats("DATE_FORMAT", uploadData[Ctr1].toString(),
										INPUT_DATE_FORMAT);
							}
						} else {
							if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))
									&& (userLoginIdIndex != -1 && Ctr1 == userLoginIdIndex))
								updateStmt = updateStmt + dbFunctionFormats("TRIM", uploadData[Ctr1].toLowerCase(), "");
							else
								updateStmt = updateStmt + dbFunctionFormats("TRIM", uploadData[Ctr1], "");
						}
					} else {
						if (uploadData[Ctr1].matches("[ ]*")) {
							updateStmt = updateStmt + "''";
						} else {
							updateStmt = updateStmt + uploadData[Ctr1];
						}
					}
				}
				theAndFlag = 'Y';
			}
			int noOfRowsAffected = 0;
			Boolean errorFlag = false;
			Boolean SuccessFlag = false;
			int errorCode = 0;
			recsCount = 0;
			Boolean update = false;
			try {
				noOfRowsAffected = stmt.executeUpdate(apprRecInsStmt);
				if (noOfRowsAffected != 0) {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded " + uploadTableName,
							uploadLogFileName);
					SuccessFlag = true;
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				errorFlag = true;
				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed " + uploadTableName + ":"
						+ e.getMessage(), uploadLogFileName);
				sqlLogWriter("\n" + appendZeroforCtr(paramLineCtr) + e.getMessage());
				logWriter(apprRecInsStmt, uploadLogFileName);
				totalFailureRecords++;
				finalReturnValue = ERRONEOUS_EXIT;
				errorCode = e.getErrorCode();
			}
			if (noOfRowsAffected == 0 && !errorFlag) {
				try {
					recsCount = stmt.executeUpdate(insertStmt);
					if (recsCount != 0) {
						totalSuccessRecords++;
						SuccessFlag = true;
						logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded  " + uploadTableName,
								uploadLogFileName);
					}
				} catch (SQLException e) {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed :" + e.getMessage(),
							uploadLogFileName);
					sqlLogWriter("\n" + appendZeroforCtr(paramLineCtr) + e.getMessage());
					totalFailureRecords++;
					SuccessFlag = true;
					recsCount = 0;
					update = true;
				}
			} else if (noOfRowsAffected != 0 && !errorFlag) {
				try {
					recsCount = stmt.executeUpdate(updateStmt);
					if (recsCount != 0) {
						totalSuccessRecords++;
						SuccessFlag = true;
						logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded  " + uploadTableName,
								uploadLogFileName);
					}
				} catch (SQLException e) {
					recsCount = 0;
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tUpdate Failed :" + e.getMessage(),
							uploadLogFileName);
					logWriter("updateStmt", uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
			} else if (noOfRowsAffected == 0 && errorFlag) {
				try {
					recsCount = stmt.executeUpdate(updateStmt);
					if (recsCount != 0) {
						totalSuccessRecords++;
						SuccessFlag = true;
						logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded  " + uploadTableName,
								uploadLogFileName);
					}
				} catch (SQLException e) {
					recsCount = 0;
					logWriter("" + appendZeroforCtr(paramLineCtr) + " Update Failed :" + e.getMessage(),
							uploadLogFileName);
					logWriter("updateStmt", uploadLogFileName);
					totalFailureRecords++;
					finalReturnValue = ERRONEOUS_EXIT;
				}
			}

			if (noOfRowsAffected == 0 && update) {
				try {
					logWriter("noOfRowsAffected != 0 && update and updateStmt:: " + updateStmt, uploadLogFileName);
					recsCount = stmt.executeUpdate(updateStmt);
					if (recsCount != 0) {
						totalSuccessRecords++;
						totalFailureRecords--;
						SuccessFlag = true;
						logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded  " + uploadTableName,
								uploadLogFileName);
					}
				} catch (SQLException e) {
					recsCount = 0;
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tUpdate Failed :" + e.getMessage(),
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
				if (recsCount == 0 && update)
					retVal = ERRONEOUS_EXIT;
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private void printSummaryReport(int paramStatus) {
		logWriter("", uploadLogFileName);
		if (paramStatus == SUCCESSFUL_EXIT) {
			logWriter("\tUpload Status         : Success ", uploadLogFileName);
		} else {
			logWriter("\tUpload Status         : Failed !!!!!!!", uploadLogFileName);
		}
		logWriter("------------------------------------------------------------", uploadLogFileName);
		logWriter("\tUpload File           : [" + uploadFileName + ".xlsx]", uploadLogFileName);
		logWriter("\tUpload Table Name     : [" + originalTableName + "]", uploadLogFileName);
		logWriter("\tTable uploaded        : [" + uploadTableName + "]", uploadLogFileName);
		logWriter("\tTotal Records present : [" + totalInputFileRecords + "]", uploadLogFileName);
		logWriter("\tRecords Succeeded     : [" + totalSuccessRecords + "]", uploadLogFileName);
		logWriter("\tRecords Failed        : [" + totalFailureRecords + "]", uploadLogFileName);
		logWriter("------------------------------------------------------------\n", uploadLogFileName);
		logWriter(" Vision Upload Ended \t Upload Sequence: [" + uploadSequence + "]  ", uploadLogFileName);
	}

	private void printUploadErrors(String paramMsgString) {
		if (uploadErrorsFirstTimeFlag == 'Y') {
			logWriter("\n", uploadLogFileName);
			logWriter("Line # \t\tError Description", uploadLogFileName);
			logWriter("*******\t\t**********************************************************************************",
					uploadLogFileName);
			uploadErrorsFirstTimeFlag = 'N';
		}
		logWriter(paramMsgString, uploadLogFileName);
	}

	private int doDecimalsValidation(String paramData, int paramSize, int paramDecimals) {
		// logWriter("paramData"+paramData+"] paramSize ["+paramSize+"]
		// paramDecimals ["+paramDecimals+"]");
		String beforeDecimals = "";
		String afterDecimals = "";
		if (paramData.contains(".") == true) {
			StringTokenizer z = new StringTokenizer(paramData, ".");
			beforeDecimals = z.nextToken();
			afterDecimals = z.nextToken();
		} else {
			beforeDecimals = paramData;
			afterDecimals = "";
		}
		if (beforeDecimals.length() > (paramSize - paramDecimals + 1))
			return ERRONEOUS_EXIT;

		if (afterDecimals.length() > paramDecimals) {
			BigDecimal bd = new BigDecimal(paramData);
			bd = bd.setScale(paramDecimals, RoundingMode.HALF_UP);
			paramData = bd.toString();
			return SUCCESSFUL_EXIT;
		}
		return SUCCESSFUL_EXIT;
	}

	private String decimalValueFormatter(String paramData, int paramSize, int paramDecimals) {
		String beforeDecimals = "";
		String afterDecimals = "";
		if (paramData.contains(".") == true) {
			StringTokenizer z = new StringTokenizer(paramData, ".");
			beforeDecimals = z.nextToken();
			afterDecimals = z.nextToken();
		} else {
			beforeDecimals = paramData;
			afterDecimals = "";
		}
		if (beforeDecimals.length() > (paramSize - paramDecimals + 1))
			return paramData;

		if (afterDecimals.length() > paramDecimals) {
			BigDecimal bd = new BigDecimal(paramData);
			bd = bd.setScale(paramDecimals, RoundingMode.HALF_UP);
			paramData = bd.toString();
			return paramData;
		}
		return paramData;
	}

	private int doNumericValidation(String paramData, int paramDecimals) {
		if (paramData != "" && paramData != null && ValidationUtil.isValid(paramData)) {
			int Ctr2 = 0;
			char firstTimeFlag = 'Y';
			char hyphenFirstTimeFlag = 'Y';
			// if the last character is a dot - means the number ends with a
			// dot. it is an error. return ERRONEOUS_EXIT
			if (paramData.charAt(paramData.length() - 1) == '.')
				return ERRONEOUS_EXIT;
			// if the paramDecimals paramter is 0 and if there is a decimal
			// point, then return ERRONEOUS_EXIT
			if (paramData.indexOf('.') != -1 && paramDecimals == 0) // IndexOf
																	// returns
																	// -1 if the
																	// Char is
																	// not found
				return ERRONEOUS_EXIT;

			int letterCount = 0;
			int index = -1;
			while ((index = paramData.indexOf('.', index + 1)) > 0)
				letterCount++;

			// More than one decimal
			if (letterCount > 1)
				return ERRONEOUS_EXIT;

			// hyphen is present it should be present at the very first
			// position.
			if (paramData.indexOf('-') != -1) {
				if (paramData.charAt(0) != '-')
					return ERRONEOUS_EXIT;
			}

			// Hypen should not Present more than once
			while ((index = paramData.indexOf('-', index + 1)) > 0)
				letterCount++;

			if (letterCount > 1)
				return ERRONEOUS_EXIT;

			// if any character at any place is not within '0' and '9' then
			// return ERRONEOUS_EXIT;
			char[] ch = paramData.toCharArray();
			for (int a = 0; a < ch.length; a++) {
				if (ch[a] == '-') {
					if (hyphenFirstTimeFlag == 'Y') {
						hyphenFirstTimeFlag = 'N';
						continue;
					}
					return (ERRONEOUS_EXIT);
				}
				if ((ch[a] < '0' || ch[a] > '9') && ch[a] != '.') {
					return ERRONEOUS_EXIT;
				}

			}
		}
		return SUCCESSFUL_EXIT;
	}

	private void initializeVariables() {
		retVal = 0;
		totalInputFileColumns = 0;
		totalTableColumns = 0;
		filePosition = 0;
		longestColumn = 0;
		totalInputFileRecords = 0;
		totalSuccessRecords = 0;
		totalFailureRecords = 0;
		operationType = ' ';
		recordIndicator = 4;
		uploadErrorsFirstTimeFlag = 'Y';
		verificationRequired = "";
		uploadTableName = "";
		uploadFileName = "";
	}

	private int updateUploadStatus(int paramStatus) {
		try {
			Statement stmt = con.createStatement();
			if (paramStatus == 2)
				sqlStatement = "Update VISION_UPLOAD Set Upload_Status = 2 Where Upload_Sequence = " + uploadSequence
						+ ""; /* Upload In Progress */
			else if (paramStatus == 3)
				sqlStatement = "Update VISION_UPLOAD Set Upload_Status = 3 Where Upload_Sequence = " + uploadSequence
						+ ""; /* Upload failed */
			else if (paramStatus == 4)
				sqlStatement = "Update VISION_UPLOAD Set Upload_Status = 4 , UPLOAD_DATE = " + getDbFunction("SYSDATE")
						+ " Where Upload_Sequence = " + uploadSequence + ""; /* Upload Successful */
			else if (paramStatus == 5)
				sqlStatement = "Update VISION_UPLOAD Set Upload_Status = 5 , UPLOAD_DATE = " + getDbFunction("SYSDATE")
						+ " Where Upload_Sequence = " + uploadSequence + ""; /* Upload Partial Successful */
			else if (paramStatus == 6)
				sqlStatement = "Update VISION_UPLOAD Set Upload_Status = 6 , UPLOAD_DATE = " + getDbFunction("SYSDATE")
						+ "Where Upload_Sequence = " + uploadSequence + ""; /* Upload Partial Successful */

			stmt.executeUpdate(sqlStatement);
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			writeSQLErrToMainLogFile("Error updating VISION_UPLOAD table for status of process (" + paramStatus
					+ "), for Upload Seq [" + uploadSequence + "] : \n" + " SQL Code [" + e.getErrorCode()
					+ "], Message [" + e.getMessage() + "]\n");
			return ERRONEOUS_EXIT;
		}
	}

	private int findIfUploadAllowedForGroup() {
		String tempStr = "";
		char abortFlag = 'N';
		int Ctr1 = 0;
		int tempSize = 0;
		totalGroupUploadTables = 0;
		tableNamelst = new ArrayList();
		uploadAllowedlst = new ArrayList();
		verificationRequiredlst = new ArrayList();
		uploadSeqlst = new ArrayList();
		uploadFileNamelst = new ArrayList();

		// Changed by DD - for removing the extension from the file name at time
		// of query
		/*
		 * sqlStatement = (
		 * " Select T1.TABLE_NAME, T1.UPLOAD_ALLOWED, T1.VERIFICATION_REQD, T2.UPLOAD_SEQUENCE, T2.FILE_NAME From "
		 * + " VISION_TABLES T1, VISION_UPLOAD T2 " +
		 * " Where T1.TABLE_NAME = T2.TABLE_NAME " + " And T1.UPLOAD_GROUP = " +
		 * uploadGroup + "" + " And T2.Upload_Status = 1 " +
		 * " Order By UPLOAD_SEQUENCE ");
		 */
		sqlStatement = (" Select T1.TABLE_NAME, T1.UPLOAD_ALLOWED, T1.VERIFICATION_REQD, T2.UPLOAD_SEQUENCE, substr(T2.FILE_NAME, 0, length(T2.FILE_NAME)-5) FILE_NAME From "
				+ " VISION_TABLES T1, VISION_UPLOAD T2 " + " Where T1.TABLE_NAME = T2.TABLE_NAME "
				+ " And T1.UPLOAD_GROUP = " + uploadGroup + "" + " And T2.Upload_Status = 1 "
				+ " Order By UPLOAD_SEQUENCE ");

		try {
			rs = stmt.executeQuery(sqlStatement);
			while (rs.next()) {
				tableNamelst.add(rs.getString("TABLE_NAME"));
				uploadAllowedlst.add(rs.getString("UPLOAD_ALLOWED"));
				verificationRequiredlst.add(rs.getString("VERIFICATION_REQD"));
				uploadSeqlst.add(rs.getString("UPLOAD_SEQUENCE"));
				uploadFileNamelst.add(rs.getString("FILE_NAME"));
				totalGroupUploadTables++;
			}
			tempSize = tableNamelst.size();
		} catch (SQLException e) {
			writeToMainLogFile(e.getMessage());
		}
		// Below Can be done by using the Contains with out using for loop.But
		// For loop used to get the Particular table name for which upload not
		// allowed.
		for (Ctr1 = 0; Ctr1 < tempSize; Ctr1++) {
			if (valueNo.equalsIgnoreCase(uploadAllowedlst.get(Ctr1).toString())) {
				writeToMainLogFile("Upload is not allowed for Table " + tableNamelst.get(Ctr1));
				abortFlag = 'Y';
			}
		}
		groupTablesTableNamelst = tableNamelst;
		groupTablesVerificationReqdlst = verificationRequiredlst;
		groupTablesUploadSequencelst = uploadSeqlst;
		groupTablesUploadFileNamelst = uploadFileNamelst;

		if (abortFlag == 'Y')
			return (ERRONEOUS_EXIT);

		if (DEBUG_MODE == YES) {
			writeToMainLogFile(
					"A total of [" + totalGroupUploadTables + "] tables were obtained for upload, in this group - FYI");
			writeToMainLogFile("The table names obtained for upload_group (" + uploadGroup + ") are......");
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				writeToMainLogFile("" + (Ctr1 + 1) + ". " + groupTablesTableNamelst.get(Ctr1));
			}
			writeToMainLogFile("\n");
		}
		return (SUCCESSFUL_EXIT);
	}

	private int updateGroupUploadStatus(int paramStatus) {
		String tempStr;
		int Ctr1 = 0;
		String query = "";
		try {
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				if (paramStatus == STATUS_SUCCESS)
					query = "Update VISION_UPLOAD Set Upload_Status =" + paramStatus + ",Upload_Date = "
							+ getDbFunction("SYSDATE") + " Where Upload_Sequence="
							+ groupTablesUploadSequencelst.get(Ctr1);
				else /*
						 * If it is STATUS_FAILURE && STATUS_PARTIAL_SUCCESS, then do this
						 */
					query = "Update VISION_UPLOAD Set Upload_Status =" + paramStatus + " Where Upload_Sequence="
							+ groupTablesUploadSequencelst.get(Ctr1);

				stmt.executeUpdate(query);
			}
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			writeSQLErrToMainLogFile("Error updating VISION_UPLOAD table for Sequence [" + uploadSequence
					+ "] with status (" + paramStatus + ") :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int specialUpload() {
		try {
			String tempStr = "";
			String firstTableName;
			int Ctr1 = 0, Ctr2 = 0;
			char abortFlag = 'N';
			finalReturnValue = SUCCESSFUL_EXIT;
			uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + makerId + "_" + GetCurrentDateTime()
					+ ".err";

			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";
			firstTableName = uploadTableName;

			if (DEBUG_MODE == YES)
				writeToMainLogFile("SpecialUpload Started............!! \n");

			retVal = findIfUploadAllowedForGroup();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/* Check for tables being uploaded being locked already by builds */
			try {
				rs = stmt.executeQuery(
						"Select Count(1) From Vision_Tables T1, Vision_Locking T3 Where T1.Upload_Group = "
								+ uploadGroup + " " + " And T1.Table_Name = T3.Table_Name And T3.Lock_Status = 'Y'");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				if (recsCount != 0) {
					writeToMainLogFile("Cannot proceed now with Upload Group [" + uploadGroup + "] Upload Table["
							+ uploadTableName + "] locked by build process \n");
					return (ERRONEOUS_EXIT);
				}
			} catch (SQLException e) {
				writeSQLErrToMainLogFile(
						"Error while trying to fetch the lock status from Vision_Locking table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			/* Now lock the tables being used for the upload */
			retVal = lockTableGroupForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for XL Upload. Cannot proceed now with upload !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}
			retVal = updateGroupUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT)
				return (ABORT_EXECUTION);

			con.commit();

			try {
				logfile = new FileWriter(uploadLogFileName); // file Opened in
																// Write mode
				logfile.close();
			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName
						+ "] for logging process steps !!\n  System Msg [" + e.getMessage() + "]\n");
				writeToMainLogFile("Aborting further processing of this request - FYI:" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			currentGroupCtr = 0;
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				initializeVariables();
				currentGroupCtr = Ctr1 + 1;
				uploadTableName = groupTablesTableNamelst.get(Ctr1).toString();
				uploadFileName = groupTablesUploadFileNamelst.get(Ctr1).toString();
				verificationRequired = valueYes;
				originalTableName = uploadTableName;
				uploadTableName = uploadTableName + "_UPL";

				logWriter(" ", uploadLogFileName);
				logWriter("Processing Group Table \t:[" + originalTableName + "] - " + (Ctr1 + 1) + " / "
						+ totalGroupUploadTables, uploadLogFileName);
				logWriter("----------------------------------------------------------", uploadLogFileName);

				if (DEBUG_MODE == YES)
					logWriter("Upload Table Name\t:[" + uploadTableName + "]", uploadLogFileName);

				logWriter("Upload Table Name\t:[" + uploadTableName + "]", uploadLogFileName);

				retVal = checkForTblExistence();

				if (retVal == ERRONEOUS_EXIT)
					return (ERRONEOUS_EXIT);
				uploadFile = xluploadDataFilePath + groupTablesUploadFileNamelst.get(Ctr1).toString() + "_" + makerId
						+ ".xlsx";
				try {
					/*
					 * bufferedReader1 = new BufferedReader(new InputStreamReader(new
					 * FileInputStream(uploadFile),"UTF8"));
					 */
					fileReader = new FileInputStream(new File(uploadFile));
				} catch (Exception e) {
					writeToMainLogFile("Error opening input file name [" + uploadFile + "]\nSystem Msg ["
							+ e.getMessage() + "]\n");
					logWriter(
							"Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n",
							uploadLogFileName);
					writeToMainLogFile("Aborting further processing of this request - FYI");
					logWriter("Aborting further processing of this request - FYI", uploadLogFileName);
					writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
					finalReturnValue = ERRONEOUS_EXIT;
					continue;
				}
				logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);
				retVal = loadTxtFileColumnNames(uploadFile);
				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nPreliminary validations Failed When Getting Column Names From File!!!!!!",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					continue;
				}
				retVal = validateTblCols();
				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nPreliminary validations Failed When Validating Table Columns!!!!!!",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					continue;
				}
				retVal = chkColCntForEachRow();
				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nPreliminary validations Failed For Column Count Mismatch!!!!!!", uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					continue;
				}
				retVal = fetchTableIndex();
				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nPreliminary validations Failed When Fetching Table Index!!!!!!", uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					continue;
				}
				logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);
				sqlStatement = "Truncate Table " + uploadTableName;
				try {
					stmt.executeUpdate(sqlStatement);
				} catch (SQLException e) {
					writeToMainLogFile("Error Truncating table [" + uploadTableName + "] !!!! \n system msg ["
							+ e.getMessage() + "]");
				}

				TAB_FLAG = 'Y';

				retVal = checkIfUploadIsAllowed();

				if (retVal == ERRONEOUS_EXIT) {
					writeToMainLogFile("Upload not allowed for the Table[" + uploadTableName + "]");
					return ERRONEOUS_EXIT;
				}

				retVal = uploadFileData();
				TAB_FLAG = 'N';

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("Upload to temporary table has failed !!", uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					con.rollback();
				} else {
					logWriter(" ", uploadLogFileName);
					logWriter("Upload to temporary table is complete - FYI\n", uploadLogFileName);
					if (DEBUG_MODE == YES)
						con.commit();
				}
				if (DEBUG_MODE == YES)
					printSummaryReport(retVal);

//				System.out.println("UPL table upload completed");
				fileReader.close();

			}
			if (finalReturnValue == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			if (DEBUG_MODE == YES) {
				logWriter(" ", uploadLogFileName);
				logWriter(" ", uploadLogFileName);
				logWriter("Upload to intermediate tables - succeeded - FYI.", uploadLogFileName);
				logWriter("Proceeding to populate actual target tables - FYI.", uploadLogFileName);
				logWriter(
						"*************************************************************************************************************************************************",
						uploadLogFileName);
			}
			currentGroupCtr = 0;
			/* This uploads the data into the */
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				initializeVariables();
				currentGroupCtr = Ctr1 + 1;
				uploadTableName = groupTablesTableNamelst.get(Ctr1).toString();
				uploadFileName = groupTablesUploadFileNamelst.get(Ctr1).toString();
				verificationRequired = (String) groupTablesVerificationReqdlst.get(Ctr1);
				originalTableName = uploadTableName;

				if (valueYes.equalsIgnoreCase(verificationRequired))
					uploadTableName = uploadTableName + "_PEND";

				logWriter("Table name obtained \t:[" + originalTableName + "]\n", uploadLogFileName);

				if (DEBUG_MODE == YES)
					logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);
				uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";
				try {
					fileReader = new FileInputStream(new File(uploadFile));
				} catch (Exception e) {
					writeToMainLogFile("Error opening input file name [" + uploadFile + "]\nSystem Msg ["
							+ e.getMessage() + "]\n");
					writeToMainLogFile("Aborting further processing of this request - FYI");
					writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
					return (ERRONEOUS_EXIT);
				}
				retVal = loadTxtFileColumnNames(uploadFile);
				if (retVal == ERRONEOUS_EXIT)
					return (ERRONEOUS_EXIT);

				retVal = validateTblCols();
				if (retVal == ERRONEOUS_EXIT)
					return (ERRONEOUS_EXIT);

				retVal = fetchTableIndex();
				if (retVal == ERRONEOUS_EXIT)
					return (ERRONEOUS_EXIT);

				TAB_FLAG = 'Y';

				retVal = uploadFileData();

				TAB_FLAG = 'N';

				if (retVal == ERRONEOUS_EXIT) {
					printSummaryReport(finalReturnValue);
					return (ERRONEOUS_EXIT);
				}

				if (finalReturnValue == ERRONEOUS_EXIT) {
					abortFlag = 'Y';
					break;
				}
				printSummaryReport(finalReturnValue);
				fileReader.close();
			}
			if (abortFlag == 'Y')
				return (ERRONEOUS_EXIT);

			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
//			System.out.println("Special Upload:" + e.getMessage());
			e.printStackTrace();
			return (ERRONEOUS_EXIT);
		}
	}

	private int truncateUPLTables() {
		String tempStr = "";
		try {
			int Ctr1 = 0;
			Statement stmt = con.createStatement();
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				/*
				 * String query = "Truncate Table " + tableNamelst.get(Ctr1) + "_UPL";
				 * stmt.executeUpdate(query);
				 */
				logWriter("Delete from " + tableNamelst.get(Ctr1) + "_UPL", uploadLogFileName);
				int recCnt = stmt.executeUpdate("Delete from " + tableNamelst.get(Ctr1) + "_UPL");
				logWriter("Upl Entries [" + recCnt + "] deleted from Table [" + tableNamelst.get(Ctr1) + "_UPL]",
						uploadLogFileName);
				writeSQLErrToMainLogFile("After truncating " + tableNamelst.get(Ctr1) + ", status is");
			}
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			return ERRONEOUS_EXIT;
		}
	}

	private int checkForTableLock() {
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"Select Count(1) From Vision_Locking T1, Vision_Tables T2 " + " Where T1.Table_Name = '"
							+ uploadTableName + "' And T1.Table_Name = T2.Table_Name And T1.Lock_Status = 'Y'");
			while (rs.next())
				recsCount = rs.getInt(1);

			if (recsCount != 0)
				return (ERRONEOUS_EXIT);

			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeSQLErrToMainLogFile("Error trying to fetch lock status from VISION_LOCKING table for ["
					+ uploadTableName + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int checkForMGTTableLock() {
		uploadTableName = "MGT_HEADERS";
		retVal = checkForTableLock();
		if (retVal == ERRONEOUS_EXIT) {
			writeToMainLogFile("The table [" + uploadTableName
					+ "] has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
			return (ERRONEOUS_EXIT);
		}
		return (SUCCESSFUL_EXIT);
	}

	private int insertIntoMgtHeadersUplTables(int paramLineCtr) {
		int Ctr1 = 0, Ctr2 = 0;

		insertStmt = "Insert Into " + uploadTableName + " (Action_Id, Line_Number, Upload_Allowed, "
				+ "Mgt_Headers_Status, Maker, Verifier, Internal_Status, Date_Creation, "
				+ "Date_Last_Modified, User_Responsibility";

		for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
			insertStmt = insertStmt + ", ";
			insertStmt = insertStmt + fileColumnsNames.get(Ctr1);
		}

		insertStmt = insertStmt + ") Values ('" + actionId + "', " + appendZeroforCtr(paramLineCtr) + ", 'Y', 10, "
				+ makerId + ", " + makerId + ", " + INTERNAL_STATUS + "" + getDbFunction("SYSDATE") + " "
				+ getDbFunction("SYSDATE") + " " + makerId;

		for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
			insertStmt = insertStmt + ", ";
			if (uploadData[Ctr1].length() == 0) {
				insertStmt = insertStmt + "Null";
			} else {
				if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1).toString())) {
					if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString())) {
						if (uploadData[Ctr1].matches("[ ]*")) {
							insertStmt = insertStmt + "To_Date('', '" + INPUT_DATE_FORMAT + "')";
						} else {
							insertStmt = insertStmt + "To_Date('" + uploadData[Ctr1] + "', '" + INPUT_DATE_FORMAT
									+ "')";
						}
					} else {
						if (valueYes.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString())) {
							insertStmt = insertStmt + "Trim(" + uploadData[Ctr1] + ")";
						} else {
							insertStmt = insertStmt + uploadData[Ctr1];
						}
					}
				} else {
					if (uploadData[Ctr1].matches("[ ]*")) {
						insertStmt = insertStmt + "''";
					} else {
						insertStmt = insertStmt + uploadData[Ctr1];
					}
				}
			}
		}
		insertStmt = insertStmt + ")";
		/* First try Update; should that fail, go for an insert */
		try {
			stmt.executeQuery(insertStmt);
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded", uploadLogFileName);
			totalSuccessRecords++;
			finalReturnValue = SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed :" + e.getMessage());
			logWriter("\n" + insertStmt, uploadLogFileName);
			finalReturnValue = ERRONEOUS_EXIT;
			totalFailureRecords++;
		}
		return finalReturnValue;
	}

	private int uploadMgtFileData(String paramTableName) {
		try {
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			char ch;
			int lineCtr = 1;
			String tempStr = "";
			String[] tempFileData = null;
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			totalInputFileRecords = 0;
			totalSuccessRecords = 0;
			totalFailureRecords = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			retVal = SUCCESSFUL_EXIT;

			TAB_FLAG = 'N';

			if ("MGT_HEADERS".equalsIgnoreCase(paramTableName))
				logWriter("Processing MGT_HEADERS records......\n", uploadLogFileName);
			else
				logWriter("Processing MGT_DETAILS & MGT_BALANCES records......\n", uploadLogFileName);

			TAB_FLAG = 'Y';

			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;
			workbook = new XSSFWorkbook(fileReader1);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			final int HeaderCount = nextRow.getLastCellNum();
			columnCount = nextRow.getLastCellNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String columnName = (String) fileColumnsNames.get(Ctr2);
						String columnSpecificDataType = String
								.valueOf(dateTypelst.get(columnNamelst.indexOf(columnName)));
						String specificData = "";
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} else if (cellType == CellType.STRING) { 
//								if (cellType == Cell.CELL_TYPE_STRING) {
								specificData = cell.getStringCellValue();
							}else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) { 
//							else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf((int) Math.round(cell.getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf((int) Math.round(cell.getNumericCellValue()));
									}
								}
							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase(""))
							lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						else
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
					}
					if (lineFetched == null) {
						break;
					} else {
						wordsArray = lineFetched.split("\n");
						lineFetched = "";
						for (String each : wordsArray) {
							lines.add(each);
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();
			for (Ctr1 = 0; Ctr1 < lines.size(); Ctr1++) {
				uploadData = lines.get(Ctr1).toString().split("\t");
				for (Ctr2 = 0; Ctr2 < uploadData.length; Ctr2++) {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr2).toString()))
						uploadData[Ctr2] = checkAndProcessQuotes(uploadData[Ctr2]);
				}
				if ("MGT_HEADERS".equalsIgnoreCase(paramTableName)) {
					retVal = insertIntoMgtHeadersUplTables(lineCtr);
				} else {
					retVal = insertIntoMgtBalancesTables(lineCtr);
				}
				if (retVal == ERRONEOUS_EXIT) {
					retVal = ERRONEOUS_EXIT;
					break;
				}
				totalInputFileRecords++;
				lineCtr++;
			}
			return (retVal);
		} catch (Exception e) {
//			System.out.println("uploadMgtFileData():" + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private String getActionId() {
		String tempActionId = "";

		// Get the java function to get the id of the current running process
		// and use it below.
		// sprintf(tempActionId, "XL%d", getpid());
		tempActionId = "XL" + makerId;
		String paramStr = tempActionId;
		return paramStr;
	}

	private String getMAInputType() {
		String tmpStr = appendZeroforCtr(makerId);
		tmpStr = "U" + tmpStr;
		return tmpStr;
	}

	private String formatFieldValue(String paramDataField, int paramFldIndex) {
		String localString = "";
		localString = localString + ", ";

		if (paramDataField.length() == 0) {
			localString = localString + "Null ";
		} else {
			if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(paramFldIndex).toString())) {
				if (valueYes.equalsIgnoreCase(fileColumnsIndexColumn.get(paramFldIndex).toString())) {
					localString = localString + "Trim( ";
					localString = localString + paramDataField;
					localString = localString + ")";
				} else {
					localString = localString + "";
					localString = localString + paramDataField;
					localString = localString + "";
				}
			} else {
				localString = localString + paramDataField;
			}
		}
		if (localString.trim().length() == 1) {
			localString = ", ''";
		}
		return localString;
	}

	private int mgtUpload() {
		try {

			char firstTimeFlag = 'Y';
			int maSequencesFilledInCount = 0;
			int totUploadableRecords = 0;
			int totDetailsRecords = 0;
			String tempStr = "";
			int Ctr1 = 0, tempParkingVar = 0;
			char abortFlag = 'N';

			finalReturnValue = STATUS_SUCCESS;
			try {
				rs = stmt.executeQuery("Select RTrim(Value) From Vision_Variables Where Variable = 'CURRENT_MONTH'");
				while (rs.next())

					tempStr = rs.getString(1);

			} catch (SQLException e) {
				writeSQLErrToMainLogFile("Error fetching Vision_Variables:CURRENT_MONTH :" + e.getMessage());
				finalReturnValue = STATUS_FAILURE;
				return (ERRONEOUS_EXIT);
			}

			currentMonth = Integer.parseInt(tempStr);
			writeToMainLogFile("Current_Month from Vision_Variables is : [" + currentMonth + "]\n");
			finalReturnValue = STATUS_SUCCESS;

			/* find if upload is permitted for this group or not */
			retVal = findIfUploadAllowedForGroup();

			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/*
			 * Check if the tables involved have been locked by build processes
			 */
			retVal = checkForMGTTableLock();

			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile(
						"One or more tables [MGT_HEADERS, MGT_DETAILS, MGT_BALANCES] have been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return (ERRONEOUS_EXIT);
			}
			retVal = lockMgtTablesForUpload();

			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for (MGT) XL Upload. Cannot proceed now with upload !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}
			retVal = updateGroupUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT) {
				return (ERRONEOUS_EXIT);
			}
			con.commit();
			uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + makerId + "_" + GetCurrentDateTime()
					+ ".err";
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";

			/*
			 * Open the main log file name and make it ready for writing log information
			 */
			try {
				logfile = new FileWriter(uploadLogFileName); // file Open in
																// Write mode
				logfile.close();
			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName
						+ "] for logging process steps !!\n  System Msg [" + e.getMessage() + "]\n");
				writeToMainLogFile("Aborting further processing of this request - FYI");
				return (ERRONEOUS_EXIT);
			}
			/* Frame the Action_Id For this upload */
			actionId = getActionId();
			/* Frame the MA_Input_Type value for this upload */
			maInputType = getMAInputType();
			currentGroupCtr = 0;
			// Iterate thru the arraylist for group upload tables
			for (Ctr1 = 0; Ctr1 < totalGroupUploadTables; Ctr1++) {
				initializeVariables();
				currentGroupCtr = Ctr1 + 1;
				uploadTableName = (String) groupTablesTableNamelst.get(Ctr1);
				uploadFileName = (String) groupTablesUploadFileNamelst.get(Ctr1);
				verificationRequired = valueNo;
				originalTableName = uploadTableName;
				uploadTableName = uploadTableName + "_UPL";
				logWriter(" ", uploadLogFileName);
				logWriter("Processing Group Table \t:[" + originalTableName + "] - " + (Ctr1 + 1) + " / "
						+ totalGroupUploadTables, uploadLogFileName);
				logWriter("----------------------------------------------------------", uploadLogFileName);
				if (DEBUG_MODE == YES) {
					logWriter("Upload Table Name\t:[" + uploadTableName + "]", uploadLogFileName);
				}
				uploadFile = xluploadDataFilePath + groupTablesUploadFileNamelst.get(Ctr1) + "_" + makerId + ".xlsx";
				try {
					/*
					 * bufferedReader1 = new BufferedReader(new InputStreamReader(new
					 * FileInputStream(uploadFile),"UTF8"));
					 */
					fileReader = new FileInputStream(new File(uploadFile));
				} catch (Exception e) {
					writeToMainLogFile("Error opening input file name [" + uploadFile + "]\nSystem Msg ["
							+ e.getMessage() + "]\n");
					writeToMainLogFile("Aborting further processing of this request - FYI");
					writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
					finalReturnValue = ERRONEOUS_EXIT;
					return (ERRONEOUS_EXIT);
				}
				logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);
				if ("MGT_HEADERS".equalsIgnoreCase(originalTableName)) {
					totHeaderRecords = 0;
					retVal = loadMGTHeaders(uploadFile);
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = ERRONEOUS_EXIT;
						break;
					}
				} else if (originalTableName.equals("MGT_BALANCES") == true) {
					totDetailRecords = 0;
					totBalanceRecords = 0;
					retVal = loadMGTBalances(uploadFile);
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = ERRONEOUS_EXIT;
						break;
					}
					uploadTableName = "MGT_DETAILS_UPL";
					originalTableName = "MGT_DETAILS";
					tempParkingVar = totalInputFileRecords;
					totalInputFileRecords = totDetailRecords;
					totalSuccessRecords = totDetailRecords;
					totalFailureRecords = totFailureDetailRecords;
					printSummaryReport(retVal);
					uploadTableName = "MGT_BALANCES_UPL";
					originalTableName = "MGT_BALANCES";
					totalInputFileRecords = tempParkingVar;
					totalSuccessRecords = totBalanceRecords;
					totalFailureRecords = totFailureBalanceRecords;
					printSummaryReport(retVal);
				}
				fileReader.close();
			}
			/*
			 * Commit the changes being done to the UPL tables, since we might want them to
			 * analyze production issues
			 */
			con.commit();
			/*
			 * If the finalReturnValue variable has been set to ERRONEOUS_EXIT, then there
			 * are errors while uploading the text file(s). Since we have reached that
			 * point, it's no use going further in this upload. We should abort, further
			 * processing, right now.
			 */
			if (finalReturnValue == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);
			logWriter(" ", uploadLogFileName);
			logWriter("Checking for initial abort conditions......\n", uploadLogFileName);
			sqlStatement = "Insert Into Mgt_Headers_Upl(Action_Id, Line_Number, Year, Mgt_Reference, Ma_Description, "
					+ "Ma_Notes, Ma_Attachments, Data_Type, Ma_Source, Currency, "
					+ "User_Responsibility, Legal_Vehicle, Ma_Reason_Code, Batch_Sequence, "
					+ "Upload_Allowed, Mgt_Headers_Status, Maker, Verifier, Internal_Status, "
					+ "Date_Last_Modified, Date_Creation) " + "Select '" + actionId
					+ "', -1, Year, Mgt_Reference, Ma_Description, "
					+ "Ma_Notes, Ma_Attachments, Data_Type, Ma_Source, Currency, "
					+ "User_Responsibility, Legal_Vehicle, Ma_Reason_Code, Batch_Sequence, "
					+ "Upload_Allowed, 10, Maker, Verifier, Internal_Status, "
					+ "Date_Last_Modified, Date_Creation From Mgt_Headers T1 "
					+ "Where Exists (Select 'X' From Mgt_Details_Upl TT Where " + "T1.Year = TT.Year "
					+ "And T1.Mgt_Reference = TT.Mgt_Reference " + "And TT.Action_Id = '" + actionId + "') "
					+ "And Not Exists (Select 'X' From Mgt_Headers_Upl T2 " + "Where T1.Year = T2.Year "
					+ "And T1.Mgt_Reference = T2.Mgt_Reference " + "And Action_Id = '" + actionId
					+ "'),uploadLogFileName";

			try {
				int mgtHeaderUplCnt = stmt.executeUpdate(sqlStatement);
				if (mgtHeaderUplCnt > 0) {
					logWriter("Records populated to Mgt_Headers_Upl Table : [" + mgtHeaderUplCnt + "] - FYI.\n",
							uploadLogFileName);
				} else {
					logWriter("No records were extracted from Mgt_Headers to the UPL table, for processing - FYI.\n",
							uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error populating Mgt_Headers_Upl table :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				finalReturnValue = STATUS_FAILURE;
				return (ERRONEOUS_EXIT);
			}
			try {
				stmt.executeUpdate("Update Mgt_Headers_Upl Set Upload_Allowed = 'N' Where Action_Id ='" + actionId
						+ "' And Data_Type = 'CA' And Line_Number != -1");
			} catch (SQLException e) {
				if (e.getErrorCode() != SQL_NO_ERRORS && e.getErrorCode() != SQL_NOT_FOUND) {
					logWriter("Error trying to update Mgt_Headers_Upl table for Data_Type = [CA] :" + e.getMessage(),
							uploadLogFileName);
					sqlLogWriter("for newly added records !!");
					return (ERRONEOUS_EXIT);
				}
			}
			con.commit();
			int count = 0;
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			try {
				rs = stmt.executeQuery("Select Distinct Year, Mgt_Reference From Mgt_Details_Upl T1 Where Action_Id = '"
						+ actionId + "' "
						+ " And Not Exists (Select 'X' From Mgt_Headers_Upl T2 Where T1.Year = T2.Year And T1.Mgt_Reference = T2.Mgt_Reference "
						+ " And T2.Action_Id = '" + actionId + "') ");
				if (rs.next()) {
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
					count++;
				}
				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter(
								"The following Year + Mgt_Reference combinations,are not present in the Mgt_Headers table",
								uploadLogFileName);
						logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
						logWriter("----\t-------------", uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
					retVal = deleteEntryFromUPLTables();
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}
					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);
				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
				retVal = chkIfRecsExistsInHdrsToProcess();

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
			} catch (SQLException e) {
				logWriter("Error preparing to select from MGT_HEADERS_UPL & Mgt_Details_Upl table :" + e.getMessage(),
						uploadLogFileName);
				sqlLogWriter("To find out non existent Year + Mgt_Headers combinations :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			count = 0;
			try {
				rs = stmt.executeQuery("Select Year, Mgt_Reference From Mgt_Headers_Upl T1 Where Action_Id = '"
						+ actionId + "' " + "And T1.Data_Type = 'CA' And Exists (Select 'X' From Mgt_Details_Upl T2 "
						+ "Where T1.Year = T2.Year And T1.Mgt_Reference = T2.Mgt_Reference " + "And T2.Action_Id = '"
						+ actionId + "')");
				if (rs.next()) {
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
				}
				for (Ctr1 = 0; Ctr1 < yearlst.size(); Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter(
								"Details & Balances are being uploaded for the following Year + Mgt_Reference combinations, "
										+ "for Header Records where Data_Type = 'CA'",
								uploadLogFileName);
						logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
						logWriter("----\t-------------", uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
					retVal = deleteEntryFromUPLTables();
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}
					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);

				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
			} catch (SQLException e) {
				logWriter("Error preparing to select from MGT_HEADERS_UPL & Mgt_Details_Upl table :" + e.getMessage(),
						uploadLogFileName);
				sqlLogWriter("To find whether only Header records are being uploaded for Cost Allocation Records !!"
						+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			count = 0;
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			try {
				rs = stmt.executeQuery("Select Year, Mgt_Reference From MGT_HEADERS T1 Where Upload_Allowed = 'N' "
						+ " And Data_Type != 'CA' And Exists (Select 'X' From (Select Year, Mgt_Reference From "
						+ " MGT_HEADERS_UPL Where Action_Id = '" + actionId + "' union "
						+ " Select Year, Mgt_Reference From MGT_DETAILS_UPL Where Action_Id = '" + actionId + "' "
						+ " union Select Year, Mgt_Reference From MGT_BALANCES_UPL Where Action_Id = '" + actionId
						+ "') T2 " + " Where T1.Year = T2.Year And T1.Mgt_Reference = T2.Mgt_Reference) "
						+ " Union Select Year, Mgt_Reference From Mgt_Headers_Upl Where " + " Action_Id = '" + actionId
						+ "' And Upload_Allowed = 'N' " + " And Data_Type != 'CA' ");
				while (rs.next()) {
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
					count++;
				}
				firstTimeFlag = 'Y';
				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter("Upload is not allowed for the following Year + Mgt_Reference combinations, "
								+ "present in the upload input files", uploadLogFileName);
						logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
						logWriter("----\t-------------", uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1), uploadLogFileName);
					retVal = deleteEntryFromUPLTables();
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}

					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);

				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
				/*
				 * Check if there are any more records to process in the UPL tables inspite of
				 * the deletion that has been done with the above loop
				 */
				retVal = chkIfRecsExistsInHdrsToProcess();
				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
			} catch (SQLException e) {
				logWriter(
						"Error preparing to select from MGT_HEADERS_UPL & MGT_HEADERS table to decide whether upload is allowed or not :"
								+ e.getMessage(),
						uploadLogFileName);
				sqlLogWriter("for the given " + yearlst.get(Ctr1) + mgtReferencelst.get(Ctr1) + "combinations :"
						+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			count = 0;
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			try {
				String query = "Select Distinct Year, Mgt_Reference From Mgt_Details_Upl Where Action_Id = '" + actionId
						+ "'";
				try {
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						yearlst.add(rs.getInt("Year"));
						mgtReferencelst.add(rs.getString("Mgt_Reference"));
						count++;
					}
				} catch (Exception e) {
					logWriter("Error while Executing the below Query1:" + e.getMessage(), uploadLogFileName);
					logWriter("" + query, uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					year = (Integer) yearlst.get(Ctr1);
					mgtReference = (String) mgtReferencelst.get(Ctr1);

					query = "Select Count(1) From Mgt_Details_Upl Where Action_Id = '" + actionId + "' "
							+ " And Year = " + year + " And Mgt_Reference = '" + mgtReference
							+ "' And Ma_Sequence Is Null";
					try {
						rs = stmt.executeQuery(query);
						while (rs.next())
							count1 = rs.getInt("Count(1)");
					} catch (Exception e) {
						logWriter("Error while Executing the below Query2:" + e.getMessage(), uploadLogFileName);
						logWriter("" + query, uploadLogFileName);
						finalReturnValue = STATUS_FAILURE;
						return (ERRONEOUS_EXIT);
					}
					try {
						rs = stmt.executeQuery("Select Count(1) From Mgt_Details_Upl Where Action_Id = '" + actionId
								+ "' " + " And Year = " + year + " And Mgt_Reference = '" + mgtReference
								+ "' And Ma_Sequence Is not null");
						while (rs.next())
							count2 = rs.getInt("Count(1)");
					} catch (Exception e) {
						logWriter("Error while Executing the below Query3:" + e.getMessage(), uploadLogFileName);
						logWriter("" + query, uploadLogFileName);
						finalReturnValue = STATUS_FAILURE;
						return (ERRONEOUS_EXIT);
					}
					if ((count1 > 0 && count2 != 0) || (count2 > 0 && count1 != 0)) {
						if (firstTimeFlag == 'Y') {
							logWriter("Value for Ma_Sequence is partially given for the following Entries..",
									uploadLogFileName);
							logWriter("Year\tMgt_Reference", uploadLogFileName);
							logWriter("----\t-------------", uploadLogFileName);
							firstTimeFlag = 'N';
						}
						logWriter("" + year + "\t" + mgtReference, uploadLogFileName);

						/*
						 * Delete this erroneous entry from the UPL Tables - viz. Headers, Details &
						 * Balances
						 */
						retVal = deleteEntryFromUPLTables();

						if (retVal == ERRONEOUS_EXIT) {
							finalReturnValue = STATUS_FAILURE;
							abortFlag = 'Y';
							break;
						}
						finalReturnValue = STATUS_PARTIAL_SUCCESS;
					}
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);
				/*
				 * Check if there are any more records to process in the UPL tables inspite of
				 * the deletion that has been done with the above loop
				 */
				retVal = chkIfRecsExistsInHdrsToProcess();

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error preparing to selecting from MGT_DETAILS_UPL Table for Ma_Sequence Validation :"
						+ e.getMessage());
				logWriter(
						"Select Distinct Year, Mgt_Reference From Mgt_Details_Upl Where Action_Id = '" + actionId + "'",
						uploadLogFileName);
				finalReturnValue = STATUS_FAILURE;
				return (ERRONEOUS_EXIT);
			}
			count = 0;
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			try {
				rs = stmt.executeQuery(
						"Select Line_Number, Year, Mgt_Reference From Mgt_Balances_Upl Where Ma_Sequence Is Null "
								+ " And Offset_Sequence Is Not Null And Action_Id = '" + actionId + "' ");

				while (rs.next()) {
					lineNumberlst.add(rs.getInt("Line_Number"));
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
					count++;
				}
				firstTimeFlag = 'Y';
				abortFlag = 'N';
				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter(
								"The following records contain Offset_Sequences given, without Ma_Sequence being specified ",
								uploadLogFileName);
						logWriter("" + lineNumberlst.get(Ctr1) + "#\t" + yearlst.get(Ctr1) + "\t"
								+ mgtReferencelst.get(Ctr1) + "", uploadLogFileName);
						logWriter("-----  \t----\t-------------", uploadLogFileName);
						firstTimeFlag = 'N';
						finalReturnValue = STATUS_FAILURE;
					}
					logWriter(
							"" + lineNumberlst.get(Ctr1) + "\t" + yearlst.get(Ctr1) + "\t" + mgtReferencelst.get(Ctr1),
							uploadLogFileName);
					/*
					 * Delete this erroneous entry from the UPL Tables - viz. Headers, Details &
					 * Balances
					 */
					retVal = deleteEntryFromUPLTables();
					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}
					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);
				/*
				 * Check if there are any more records to process in the UPL tables inspite of
				 * the deletion that has been done with the above loop
				 */
				retVal = chkIfRecsExistsInHdrsToProcess();

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error preparing to select for verification of Offset_Sequence & Ma_Sequences :"
						+ e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			count = 0;
			lineNumberlst = new ArrayList();
			legalVehiclelst = new ArrayList();
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			availableStatuslst = new ArrayList();
			try {
				rs = stmt.executeQuery("Select Line_Number, T1.Legal_Vehicle, 'Y' Avail_Status, "
						+ " T1.Year, T1.Mgt_Reference From MGT_HEADERS_UPL T1, LE_BOOK T2 " + " Where Action_Id = '"
						+ actionId + "' " + " And T1.Legal_Vehicle > '0' " + " And T1.Legal_Vehicle = T2.Legal_Vehicle "
						+ " And T1.Currency != T2.Reporting_Ccy " + " And Exists "
						+ " (Select 'X' From LEGAL_VEHICLE_RATES T3, MGT_BALANCES_UPL T4 "
						+ " Where T3.Month = T4.Month " + " And T3.Year = T4.Year "
						+ " And (Fx_Rate = 0 Or Fx_Rate Is Null) " + " And T1.Year = T4.Year "
						+ " And T1.Mgt_Reference = T4.Mgt_Reference " + " And T4.Action_Id = '" + actionId + "') "
						+ " Union " + " Select Line_Number, T1.Legal_Vehicle, 'N' Avail_Status, "
						+ " T1.Year, T1.Mgt_Reference From MGT_HEADERS_UPL T1 " + " Where Action_Id = '" + actionId
						+ "' " + " And Not Exists (Select 'X' From Le_Book T2 Where "
						+ " T1.Legal_Vehicle = T2.Legal_Vehicle) ");
				while (rs.next()) {
					lineNumberlst.add(rs.getInt("Line_Number"));
					legalVehiclelst.add(rs.getString("Legal_Vehicle"));
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
					availableStatuslst.add(rs.getString("Avail_Status"));
					count++;
				}

				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter("The following Legal_Vehicles in do not have Fx_Rate in Legal_Vehicle_Rates table : ",
								uploadLogFileName);
						logWriter("" + lineNumberlst.get(Ctr1) + "#\t" + legalVehiclelst.get(Ctr1) + "\t",
								uploadLogFileName);
						logWriter("-------\t-------------\t", uploadLogFileName);
						firstTimeFlag = 'N';
					}
					if (valueNo.equalsIgnoreCase(availableStatuslst.get(Ctr1).toString()))
						logWriter("" + lineNumberlst.get(Ctr1) + "\t" + legalVehiclelst.get(Ctr1)
								+ "\t(Invalid Legal Vehicle !!", uploadLogFileName);
					else
						logWriter("" + lineNumberlst.get(Ctr1) + "\t" + legalVehiclelst.get(Ctr1), uploadLogFileName);

					logWriter(tempStr, uploadLogFileName);

					/*
					 * Delete this erroneous entry from the UPL Tables - viz. Headers, Details &
					 * Balances
					 */
					retVal = deleteEntryFromUPLTables();

					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}

					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);

				/*
				 * Check if there are any more records to process in the UPL tables inspite of
				 * the deletion that has been done with the above loop
				 */
				retVal = chkIfRecsExistsInHdrsToProcess();

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				/*
				 * Populate the Ma_Sequences for those records where the Ma_Sequences are blank
				 */

				retVal = populateMaSequences();

				if (retVal == ERRONEOUS_EXIT) {
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
				/*
				 * Commit now, since we want the insertions made into the Mgt_Validation_Error
				 * tables, to be permanent
				 */
				con.commit();
			} catch (SQLException e) {
				sqlLogWriter("Error preparing to select from LEGAL_VEHICLES table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			count = 0;
			lineNumberlst = new ArrayList();
			yearlst = new ArrayList();
			mgtReferencelst = new ArrayList();
			try {
				rs = stmt.executeQuery(
						"Select Distinct Line_Number, T3.Year, T3.Mgt_Reference From Mgt_Details_Upl T3, Mgt_Details T6 "
								+ " Where T3.Action_Id = '" + actionId + "' " + " And T3.Year = T6.Year "
								+ " And T3.Mgt_Reference = T6.Mgt_Reference " + " And T3.Ma_Sequence = T6.Ma_Sequence "
								+ " And Not Exists (Select 'X' From MGT_DETAILS_UPL T1, MGT_DETAILS T2 "
								+ " Where T1.Action_Id = '" + actionId + "' " + " And T1.Year = T2.Year "
								+ " And T1.Mgt_Reference = T2.Mgt_Reference " + " And T1.Vision_Ouc = T2.Vision_Ouc "
								+ " And T1.Mrl_Line = T2.Mrl_Line " + " And T1.Country = T2.Country "
								+ " And T1.LE_Book = T2.LE_Book " + " And T1.Contract_Id = T2.Contract_Id "
								+ " And T1.Customer_Id = T2.Customer_Id " + " And T1.Vision_SBU = T2.Vision_SBU "
								+ " And T1.Account_Officer = T2.Account_Officer " + " And T1.Product = T2.Product "
								+ " And T1.Mis_Currency = T2.Mis_Currency " + " And T1.Rowid = T3.RowId) "
								+ " And Exists " + " (Select 'X' From MGT_BALANCES T5 " + " Where T3.Year = T5.Year "
								+ " And T3.Mgt_Reference = T5.Mgt_Reference " + " And T3.Ma_Sequence = T5.Ma_Sequence "
								+ " And T5.Month Not In (Select T4.Month "
								+ " From MGT_BALANCES_UPL T4 Where T4.Year = T3.Year "
								+ " And T4.Mgt_Reference = T3.Mgt_Reference " + " And T4.Ma_Sequence = T3.Ma_Sequence "
								+ " And T4.Action_Id = '" + actionId + "'))");
				while (rs.next()) {
					lineNumberlst.add(rs.getInt("Line_Number"));
					yearlst.add(rs.getInt("Year"));
					mgtReferencelst.add(rs.getString("Mgt_Reference"));
					count++;
				}

				firstTimeFlag = 'Y';

				for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
					if (firstTimeFlag == 'Y') {
						logWriter(
								"The MGT_DETAILS values for the following records are being changed, where approved Balances exists .....\n",
								uploadLogFileName);
						logWriter("" + lineNumberlst.get(Ctr1) + "#\t" + yearlst.get(Ctr1) + "\t"
								+ mgtReferencelst.get(Ctr1) + "", uploadLogFileName);
						logWriter("----\t\t----\t-------------", uploadLogFileName);
						firstTimeFlag = 'N';
						finalReturnValue = STATUS_FAILURE;
					}
					logWriter("" + lineNumber + "\t" + year + "\t" + mgtReference, uploadLogFileName);

					/*
					 * Delete this erroneous entry from the UPL Tables - viz. Headers, Details &
					 * Balances
					 */

					retVal = deleteEntryFromUPLTables();

					if (retVal == ERRONEOUS_EXIT) {
						finalReturnValue = STATUS_FAILURE;
						abortFlag = 'Y';
						break;
					}
					finalReturnValue = STATUS_PARTIAL_SUCCESS;
				}
				if (abortFlag == 'Y')
					return (ERRONEOUS_EXIT);

				if (firstTimeFlag == 'N') {
					con.commit();
					logWriter(" ", uploadLogFileName);
				}
				/*
				 * Check if there are any more records to process in the UPL tables inspite of
				 * the deletion that has been done with the above loop
				 */

				retVal = chkIfRecsExistsInHdrsToProcess();

				if (retVal == ERRONEOUS_EXIT) {
					logWriter("\nNo more records to process !!\n", uploadLogFileName);
					finalReturnValue = STATUS_FAILURE;
					return (ERRONEOUS_EXIT);
				}
			} catch (SQLException e) {
				sqlLogWriter(
						"Error Preparing to select from MGT_DETAILS tables, for values validation :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			status = "";
			category = "";
			errorMsg = "";
			successCnt = 0;
			failCnt = 0;

			try {
				callableStatement = con.prepareCall("{call PR_VALIDATE_MIS_ADJ (?,?,?,?,?,?)}");
				callableStatement.setString(1, actionId);
				callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);
				callableStatement.registerOutParameter(6, java.sql.Types.NUMERIC);

				callableStatement.executeQuery();

				status = callableStatement.getString(2);
				category = callableStatement.getString(3);
				errorMsg = callableStatement.getString(4);
				successCnt = callableStatement.getInt(5);
				failCnt = callableStatement.getInt(6);
			} catch (SQLException e) {
				sqlLogWriter("Error calling procedure PR_VALIDATE_MIS_ADJ to validate input data :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			con.commit();

			logWriter(" ", uploadLogFileName);

			if ("F".equalsIgnoreCase(status)) {
				logWriter("fatal error while trying to call validate procedure. Aborting further processing !!",
						uploadLogFileName);
				logWriter("Message got from the database ....", uploadLogFileName);
				logWriter(errorMsg + "\n", uploadLogFileName);
				return (STATUS_FAILURE);
			} else if ("E".equalsIgnoreCase(status)) {
				logWriter("-- All input records failed validation. Aborting further processing !!", uploadLogFileName);
				return (STATUS_FAILURE);
			} else if ("P".equalsIgnoreCase(status)) {
				logWriter("One or more of the input records have failed validation. " + errorMsg, uploadLogFileName);
				logWriter("Records that have failed will not be uploaded to the respective tables !!",
						uploadLogFileName);

				finalReturnValue = STATUS_PARTIAL_SUCCESS;
				count = 0;
				yearlst = new ArrayList();
				mgtReferencelst = new ArrayList();
				try {
					rs = stmt.executeQuery(
							"Select Distinct Year, Mgt_Reference From Mgt_Validation_Errors Where Action_Id = '"
									+ actionId + "' ");

					while (rs.next()) {
						yearlst.add(rs.getInt("Year"));
						mgtReferencelst.add(rs.getString("Mgt_Reference"));
					}
					for (Ctr1 = 0; Ctr1 < count; Ctr1++) {
						year = (Integer) yearlst.get(Ctr1);
						mgtReference = (String) mgtReferencelst.get(Ctr1);
						retVal = deleteEntryFromUPLTables();
						if (retVal == ERRONEOUS_EXIT) {
							finalReturnValue = STATUS_FAILURE;
							abortFlag = 'Y';
							break;
						}
					}
					if (abortFlag == 'Y')
						return (finalReturnValue);

					logWriter("Total Success records : [" + successCnt + "]", uploadLogFileName);
					logWriter("Total Failure records : [" + failCnt + "]\n", uploadLogFileName);
					logWriter("Processing records that have fully passed thru validations - FYI.\n", uploadLogFileName);
				} catch (SQLException e) {
					sqlLogWriter("Error preparing to select from Mgt_Validation_Errors table :" + e.getMessage());
					logWriter(sqlStatement, uploadLogFileName);
					return (ERRONEOUS_EXIT);
				}
			} else if ("C".equalsIgnoreCase(status)) {
				logWriter("Input records have passed thru Validations.", uploadLogFileName);
				logWriter("Proceeding to post the records to Pending table - FYI\n", uploadLogFileName);
			}
			/*
			 * The input file for balances might have duplicate records (Year +
			 * Mgt_Reference + Ma_Sequence + Month) for the combination. In such a case,
			 * their balances need to be cumulated and the duplicate entries be removed from
			 * the Balances_UPL table.
			 */
			recsCount = 0;

			try {
				rs = stmt.executeQuery(
						"Select Count(1) From (Select Year, Mgt_Reference, Ma_Sequence, Month, Count(1) From Mgt_Balances_Upl "
								+ " Where Action_Id = '" + actionId
								+ "' Group by Year, Mgt_Reference, Ma_Sequence, Month Having Count(1) > 1) T1");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count duplicate entries from Mgt_Balances_Upl :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			int recDeletedCnt = 0;

			if (recsCount > 0) {
				try {
					stmt.executeUpdate(
							" Update Mgt_Balances_Upl T1 Set (Eop_Amount, Avg_Amount, Pl_Amount, Pool_Amount) = "
									+ " (Select Sum(Eop_Amount), Sum(Avg_Amount), Sum(Pl_Amount), Sum(Pool_Amount)"
									+ " From Mgt_Balances_Upl T2 " + " Where T2.Action_Id     = '" + actionId + "'"
									+ " And   T1.Mgt_Reference = T2.Mgt_Reference"
									+ " And   T1.Ma_Sequence   = T2.Ma_Sequence" + " And   T1.Month         = T2.Month"
									+ " And   T1.Action_Id     = T2.Action_Id" + " And   T1.Year          = T2.Year) "
									+ " Where T1.Action_Id   = '" + actionId + "'");
				} catch (SQLException e) {
					sqlLogWriter("Error updating balances for redundant records in Mgt_Balances_Upl table :"
							+ e.getMessage());
					return (ERRONEOUS_EXIT);
				}
				try {
					recDeletedCnt = stmt.executeUpdate("Delete From Mgt_Balances_Upl T1 "
							+ " Where RowId > (Select Min(RowId) From Mgt_Balances_Upl T2" + " Where T2.Action_Id   = '"
							+ actionId + "'" + " And T1.Mgt_Reference = T2.Mgt_Reference"
							+ " And T1.Ma_Sequence   = T2.Ma_Sequence" + " And T1.Month         = T2.Month"
							+ " And T1.Action_Id     = T2.Action_Id" + " And T1.Year          = T2.Year)"
							+ " And T1.Action_Id     = '" + actionId + "'");
				} catch (SQLException e) {
					sqlLogWriter("Error Deleting redundant records in Mgt_Balances_Upl table :" + e.getMessage());
					return (ERRONEOUS_EXIT);
				}

				totBalanceRecords -= recDeletedCnt;

				if (DEBUG_MODE == YES) {
//					System.out.println("Redundant records deleted from Mgt_Balances_Upl : [" + recDeletedCnt + "] - FYI.\n");
					logWriter("Redundant records deleted from Mgt_Balances_Upl : [" + recDeletedCnt + "] - FYI.\n",
							uploadLogFileName);
				}
			}
			status = "";
			errorMsg = "";

			try {
				callableStatement = con.prepareCall("{call PR_INSERT_MIS_ADJ_TO_PEND_TBL (?,?,?)}");

				callableStatement.setString(1, actionId);
				callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR);

				callableStatement.executeUpdate();

				status = callableStatement.getString(2);
				errorMsg = callableStatement.getString(3);
			} catch (SQLException e) {
				sqlLogWriter("Error calling procedure PR_INSERT_MIS_ADJ_TO_PEND_TBL to validate input data :"
						+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			logWriter(" ", uploadLogFileName);

			if ("F".equalsIgnoreCase(status)) {
				logWriter("An error occured while trying to post records into the Pending tables !!",
						uploadLogFileName);
				logWriter(errorMsg, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			recsCount = 0;

			try {
				rs = stmt.executeQuery("Select Count(1) From Mgt_Details_Upl Where	Action_Id = '" + actionId + "'");

				while (rs.next())
					recsCount = rs.getInt(1);

			} catch (SQLException e) {
				sqlLogWriter("Error counting from Mgt_Details_Upl Table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}

			logWriter("Final Summary report for Mgt Group Upload : "
					+ (finalReturnValue == ERRONEOUS_EXIT ? "Failed !!!!!!" : "Success"), uploadLogFileName);
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter("Total Detail Records Obtained                 : [" + totDetailRecords + "]", uploadLogFileName);
			logWriter("Total Detail Records that passed Validations  : [" + recsCount + "]", uploadLogFileName);

			if (recsCount != totDetailRecords) {
				logWriter("Total Detail Records that failed Validations  : [" + (totDetailRecords - recsCount) + "]",
						uploadLogFileName);
			}

			logWriter(" ", uploadLogFileName);
			recsCount = 0;

			try {
				rs = stmt.executeQuery("Select Count(1) From Mgt_Balances_Upl Where	Action_Id = '" + actionId + "'");

				while (rs.next())
					recsCount = rs.getInt("Count(1)");

			} catch (SQLException e) {
				sqlLogWriter("Error counting from Mgt_Balances_Upl Table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}

			logWriter("Total Balance Records Obtained                : [" + totBalanceRecords + "]", uploadLogFileName);
			logWriter("Total Balance Records that passed Validations : [" + recsCount + "]", uploadLogFileName);

			if (recsCount != totBalanceRecords) {
				logWriter("Total Balance Records that failed Validations : [" + (totBalanceRecords - recsCount) + "]",
						uploadLogFileName);
			}

			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			return (finalReturnValue);
		} catch (Exception e) {
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int loadMGTHeaders(String uploadFileName) {
		retVal = checkForTblExistence();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		retVal = loadTxtFileColumnNames(uploadFileName);
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted on MGT loadTxtFileColumnNames !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		retVal = valiadteMgtTblColumns("MGT_HEADERS");
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted on valiadteMgtTblColumns !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		retVal = chkColCntForEachRow();
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted on chkColCntForEachRow!!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		TAB_FLAG = 'Y';
		retVal = uploadMgtFileData("MGT_HEADERS");
		TAB_FLAG = 'N';

		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted on uploadMgtFileData!!", uploadLogFileName);
			return (ABORT_EXECUTION);
		}

		printSummaryReport(retVal);

		return (SUCCESSFUL_EXIT);
	}

	private int insertIntoMgtBalancesTables(int paramLineCtr) {
		int Ctr1 = 0, Ctr2 = 0;
		String tempStr;
		char maSequenceFoundFlag = 'N';

		retVal = SUCCESSFUL_EXIT;

		/*
		 * Attempt to insert first into the Details table and then into the Balances
		 * table
		 */

		insertStmt = "Insert Into MGT_DETAILS_UPL (Action_Id, Line_Number, "
				+ " Maker, Internal_Status, Date_Creation, Date_Last_Modified, " + " Year, Mgt_Reference, "
				+ " Ma_Sequence, Vision_Ouc, " + " Mrl_Line, Product, Account_Officer, Vision_Sbu, Mis_Currency, "
				+ " Country, Le_Book, Contract_Id, Customer_Id, " + " Offset_Sequence, Ma_Input_Type) Values ('"
				+ actionId + "', " + appendZeroforCtr(paramLineCtr) + ", " + makerId + ", " + INTERNAL_STATUS
				+ ", SysDate, SysDate";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_YEAR], IDX_MISUPLOAD_YEAR);
		insertStmt = insertStmt + tempStr;

		year = Integer.parseInt(uploadData[IDX_MISUPLOAD_YEAR]);

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MGT_REFERENCE], IDX_MISUPLOAD_MGT_REFERENCE);
		insertStmt = insertStmt + tempStr;

		mgtReference = "";
		mgtReference = uploadData[IDX_MISUPLOAD_MGT_REFERENCE];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MA_SEQUENCE], IDX_MISUPLOAD_MA_SEQUENCE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_VISION_OUC], IDX_MISUPLOAD_VISION_OUC);
		insertStmt = insertStmt + tempStr;

		visionOuc = "";
		visionOuc = uploadData[IDX_MISUPLOAD_VISION_OUC];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MRL_LINE], IDX_MISUPLOAD_MRL_LINE);
		insertStmt = insertStmt + tempStr;

		mrlLine = "";
		mrlLine = uploadData[IDX_MISUPLOAD_MRL_LINE];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_PRODUCT], IDX_MISUPLOAD_PRODUCT);
		insertStmt = insertStmt + tempStr;

		product = "0";
		product = uploadData[IDX_MISUPLOAD_PRODUCT];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_ACCOUNT_OFFICER], IDX_MISUPLOAD_ACCOUNT_OFFICER);
		insertStmt = insertStmt + tempStr;

		accountOfficer = "";
		accountOfficer = uploadData[IDX_MISUPLOAD_ACCOUNT_OFFICER];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_VISION_SBU], IDX_MISUPLOAD_VISION_SBU);
		insertStmt = insertStmt + tempStr;

		visionSbu = "";
		visionSbu = uploadData[IDX_MISUPLOAD_VISION_SBU];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MIS_CURRENCY], IDX_MISUPLOAD_MIS_CURRENCY);
		insertStmt = insertStmt + tempStr;

		misCurrency = "";
		misCurrency = uploadData[IDX_MISUPLOAD_MIS_CURRENCY];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_COUNTRY], IDX_MISUPLOAD_COUNTRY);
		insertStmt = insertStmt + tempStr;

		country = "";
		country = uploadData[IDX_MISUPLOAD_COUNTRY];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_LE_BOOK], IDX_MISUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		leBook = "";
		leBook = uploadData[IDX_MISUPLOAD_LE_BOOK];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_CONTRACT_ID], IDX_MISUPLOAD_CONTRACT_ID);
		insertStmt = insertStmt + tempStr;

		contractId = "";
		contractId = uploadData[IDX_MISUPLOAD_CONTRACT_ID];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_CUSTOMER_ID], IDX_MISUPLOAD_CUSTOMER_ID);
		insertStmt = insertStmt + tempStr;

		customerId = "";
		customerId = uploadData[IDX_MISUPLOAD_CUSTOMER_ID];

		tempStr = "";
		// Changes need for this line
		/*
		 * tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE],
		 * IDX_MISUPLOAD_OFFSET_SEQUENCE); if(tempStr == " ")
		 */
		tempStr = "," + null;
		insertStmt = insertStmt + tempStr;

		/*
		 * if (ValidationUtil.isValid(uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE]) &&
		 * uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE].length() > 0) { offsetSequence =
		 * Integer.parseInt(uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE]); }
		 */

		maSequenceFoundFlag = 'N';

		if (uploadData[IDX_MISUPLOAD_MA_SEQUENCE].length() != 0) {
			maSequence = Integer.parseInt(uploadData[IDX_MISUPLOAD_MA_SEQUENCE]);
			maSequenceFoundFlag = 'Y';
		}

		insertStmt = insertStmt + ", '" + maInputType + "')";

		lineNumber = 0;
		try {
			if (maSequenceFoundFlag == 'Y') {
				rs = stmt.executeQuery("Select Line_Number From Mgt_Details_UPL " + " Where Year = " + year
						+ " And Mgt_Reference = " + mgtReference + "" + " And Ma_Sequence = " + maSequence
						+ " And Vision_Ouc = " + visionOuc + "" + " And Mrl_Line = " + mrlLine + " And Product = "
						+ product + "" + " And Account_Officer = " + accountOfficer + " And Vision_Sbu = " + visionSbu
						+ "" + " And Country = " + country + " And Le_Book = " + leBook + "" + " And Contract_Id = "
						+ contractId + " And Customer_Id = " + customerId + "" + " And Mis_Currency = " + misCurrency
						+ " And Action_Id = '" + actionId + "'");

				while (rs.next())
					lineNumber = rs.getInt("Line_Number");
			} else {
				rs = stmt.executeQuery(
						" Select Line_Number From Mgt_Details_UPL" + " Where Year = " + year + " And Mgt_Reference = "
								+ mgtReference + " And Vision_Ouc = " + visionOuc + " " + " And Mrl_Line = " + mrlLine
								+ " And Product = " + product + " And Account_Officer = " + accountOfficer + " "
								+ " And Vision_Sbu = " + visionSbu + " And Country = " + country + " And Le_Book = "
								+ leBook + "" + " And Contract_Id = " + contractId + " And Customer_Id = " + customerId
								+ " And Mis_Currency = " + misCurrency + "" + " And Action_Id = '" + actionId + "'");

				while (rs.next())
					lineNumber = rs.getInt("Line_Number");
			}
		} catch (SQLException e) {
			sqlLogWriter("Error trying to fetch line number from Mgt_Details_UPL table :\n" + e.getMessage());
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
		if (lineNumber == 0) {
			/* Attempt the insert operation now, for the MGT_DETAILS table */
			try {
				stmt.executeUpdate(insertStmt);

				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded (Mgt_Details)",
						uploadLogFileName);
				totDetailRecords++;
			} catch (SQLException e) {
				sqlLogWriter(
						"" + appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed (Mgt_Details) :" + e.getMessage());
				logWriter("\n" + insertStmt, uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totFailureDetailRecords++;
			}
			lineNumber = paramLineCtr;
		}

		/*
		 * Then, attempt to insert first into the Details table and then into the
		 * Balances table
		 */

		insertStmt = "Insert Into MGT_BALANCES_UPL (Action_Id, Line_Number, Internal_Line_Number, "
				+ " Mgt_Balances_Status, Offset_Sequence, "
				+ " Maker, Verifier, Internal_Status, Date_Creation, Date_Last_Modified, "
				+ " Year, Month, Mgt_Reference, Ma_Sequence, Eop_Amount, "
				+ " Avg_Amount, PL_Amount, Pool_Amount) Values ('" + actionId + "', " + paramLineCtr + ", " + lineNumber
				+ ", 10, null" +
				// " uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE].length() > 0 ?
				// uploadData[IDX_MISUPLOAD_OFFSET_SEQUENCE] : Null " +
				" , " + makerId + ", " + makerId + ", " + INTERNAL_STATUS + ", SysDate, SysDate";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_YEAR], IDX_MISUPLOAD_YEAR);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MONTH], IDX_MISUPLOAD_MONTH);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MGT_REFERENCE], IDX_MISUPLOAD_MGT_REFERENCE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_MA_SEQUENCE], IDX_MISUPLOAD_MA_SEQUENCE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_EOP_AMOUNT], IDX_MISUPLOAD_EOP_AMOUNT);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_AVG_AMOUNT], IDX_MISUPLOAD_AVG_AMOUNT);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_PL_AMOUNT], IDX_MISUPLOAD_PL_AMOUNT);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_MISUPLOAD_POOL_AMOUNT], IDX_MISUPLOAD_POOL_AMOUNT);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ")";

		try {
			int balrecCount = stmt.executeUpdate(insertStmt);
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded (Mgt_Balances)", uploadLogFileName);
			totBalanceRecords++;
		} catch (SQLException e) {
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed (Mgt_Balances) :" + e.getMessage());
			logWriter("\n" + insertStmt, uploadLogFileName);
			finalReturnValue = ERRONEOUS_EXIT;
			totFailureBalanceRecords++;

		}
		return (finalReturnValue);
	}

	private int loadMGTBalances(String uploadfileName) {
		retVal = checkForTblExistence();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		retVal = loadTxtFileColumnNames(uploadfileName);
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		retVal = valiadteMgtTblColumns("MGT_BALANCES");
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		retVal = chkColCntForEachRow();
		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		TAB_FLAG = 'Y';

		retVal = uploadMgtFileData("MGT_BALANCES");

		TAB_FLAG = 'N';

		if (retVal == ERRONEOUS_EXIT) {
			logWriter("\nExecution aborted !!", uploadLogFileName);
			return (ABORT_EXECUTION);
		}
		return (SUCCESSFUL_EXIT);
	}

	private int valiadteMgtTblColumns(String paramTableName) {
		int Ctr1 = 0, Ctr2 = 0;
		String lStr = "";
		char foundFlag = 'Y';
		char firstTimeFlag = 'Y';
		char continueFlag = 'N';
		tableColumnList = new ArrayList();
		columnNamelst = new ArrayList();
		columnSizelst = new ArrayList();
		quotedTypelst = new ArrayList();
		nullable = new ArrayList();
		decimalsChecklst = new ArrayList();
		dataScalelst = new ArrayList();
		dateTypelst = new ArrayList();

		retVal = SUCCESSFUL_EXIT;
		if ("MGT_HEADERS".equalsIgnoreCase(paramTableName)) {
			Ctr1 = 0;

			columnNamelst.add(Ctr1, "YEAR");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 4);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MGT_REFERENCE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 8);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MA_DESCRIPTION");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 60);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MA_NOTES");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 255);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MA_SOURCE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 30);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "DATA_TYPE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 10);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "LEGAL_VEHICLE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 5);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "CURRENCY");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 3);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MA_REASON_CODE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 4);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "BATCH_SEQUENCE");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 4);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');
		} else {
			Ctr1 = 0;
			columnNamelst.add(Ctr1, "YEAR");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 4);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MONTH");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 2);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MGT_REFERENCE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 8);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MA_SEQUENCE");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 4);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "VISION_OUC");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 10);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MRL_LINE");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 7);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "PRODUCT");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 8);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "ACCOUNT_OFFICER");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 15);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "VISION_SBU");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 10);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "MIS_CURRENCY");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 3);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "COUNTRY");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 2);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "LE_BOOK");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 2);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "CONTRACT_ID");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 20);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "CUSTOMER_ID");
			nullable.add(Ctr1, 'N');
			columnSizelst.add(Ctr1, 11);
			quotedTypelst.add(Ctr1, 'Y');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'N');

			columnNamelst.add(Ctr1, "EOP_AMOUNT");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 21);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 5);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'Y');

			columnNamelst.add(Ctr1, "AVG_AMOUNT");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 21);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 5);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'Y');

			columnNamelst.add(Ctr1, "PL_AMOUNT");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 21);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 5);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'Y');

			columnNamelst.add(Ctr1, "POOL_AMOUNT");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 21);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 5);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'Y');

			columnNamelst.add(Ctr1, "OFFSET_SEQUENCE");
			nullable.add(Ctr1, 'Y');
			columnSizelst.add(Ctr1, 8);
			quotedTypelst.add(Ctr1, 'N');
			dataScalelst.add(Ctr1, 0);
			dateTypelst.add(Ctr1, 'N');
			decimalsChecklst.add(Ctr1++, 'Y');
		}
		totalTableColumns = Ctr1;

		if (totalTableColumns == 0) {
			logWriter("The table does not have any columns to upload !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		foundFlag = 'Y';

		/* Check for difference between File Columns and Actual Columns */
		Set<String> diffBtwFileColumnsAndActualColumns = new HashSet<>(fileColumnsNames);
		diffBtwFileColumnsAndActualColumns.removeAll(columnNamelst);
		if (diffBtwFileColumnsAndActualColumns.size() > 0) {
			logWriter("The uploaded files have extra columns !!" + diffBtwFileColumnsAndActualColumns.toString(),
					uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		diffBtwFileColumnsAndActualColumns = new HashSet<>(columnNamelst);
		diffBtwFileColumnsAndActualColumns.removeAll(fileColumnsNames);
		if (diffBtwFileColumnsAndActualColumns.size() > 0) {
			logWriter("The uploaded files are missing some columns !!" + diffBtwFileColumnsAndActualColumns.toString(),
					uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}

		/* Check for Column ordering in the uploaded file */
		for (int indexForColumn = 0; indexForColumn < columnNamelst.size(); indexForColumn++) {
			if (!fileColumnsNames.get(indexForColumn).toString()
					.equalsIgnoreCase(columnNamelst.get(indexForColumn).toString())) {
				logWriter("The uploaded files must be ordered " + columnNamelst.toString(), uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
		}

		/* Validate the column names present in the flat file */
		for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
			if (fileColumnsNames.get(Ctr1).toString().equalsIgnoreCase(columnNamelst.get(Ctr1).toString())) {
				fileColumnsColumnSize.add(Ctr1, columnSizelst.get(Ctr1));
				fileColumnsQuotedType.add(Ctr1, quotedTypelst.get(Ctr1));
				fileColumnsNullable.add(Ctr1, nullable);
				fileColumnsDecimalsCheck.add(Ctr1, decimalsChecklst.get(Ctr1));
				fileColumnsAfterDecimals.add(Ctr1, dataScalelst.get(Ctr1));
				fileColumnsDataType.add(Ctr1, dateTypelst.get(Ctr1));
			} else {
				foundFlag = 'N';
			}
		}
		if (foundFlag == 'N') {
			retVal = ERRONEOUS_EXIT;

			if ("MGT_HEADERS".equalsIgnoreCase(paramTableName))
				logWriter("The columns in MGT_HEADERS file should ONLY be in the following order :", uploadLogFileName);
			else
				logWriter("The columns in MGT_BALANCES file should ONLY be in the following order & names :",
						uploadLogFileName);
			for (Ctr1 = 0; Ctr1 < totalTableColumns; Ctr1++) {
				logWriter("" + (Ctr1 + 1) + "%02d\t" + columnNamelst.get(Ctr1) + "", uploadLogFileName);
			}
		}
		if (retVal != SUCCESSFUL_EXIT)
			return (retVal);

		return (retVal);
	}

	private int printMgtUploadErrorReport() {
		String prevTableType = "";
		String tempStr;
		int localLineNumber = 0;
		char firstTimeFlag = 'Y';
		int recCounttmp = 0;
		int Ctr = 0;
		try {
			String query = " Select Table_Flag, Line_Number, Year, Mgt_Reference, Ma_Sequence, Month, Error_Desc, "
					+ " Error_Type, Severity From Mgt_Validation_Errors Where Action_Id = '" + actionId + "' "
					+ " Order By Table_Flag Desc, Line_Number, Error_Type";
			logWriter(query, uploadLogFileName);
			rs = stmt.executeQuery(query);

			// recCounttmp = rs.getRow();
			prevTableType = "X";
			while (rs.next()) {
				tableTypelst.add(rs.getString("Table_Flag"));
				lineNumberlst.add(rs.getInt("Line_Number"));
				yearlst.add(rs.getInt("Year"));
				mgtReferencelst.add(rs.getString("Mgt_Reference"));
				maSequencelst.add(rs.getInt("Ma_Sequence"));
				monthlst.add(rs.getInt("Month"));
				errorDescriptionlst.add(rs.getString("Error_Desc"));
				errorTypelst.add(rs.getString("Error_Type"));
				severitylst.add(rs.getString("Severity"));
				recCounttmp++;
			}
			logWriter("recCounttmp:" + recCounttmp, uploadLogFileName);
			for (Ctr = 0; Ctr < recCounttmp; Ctr++) {
				tableType = (String) tableTypelst.get(Ctr);
				lineNumber = (Integer) lineNumberlst.get(Ctr);
				year = (Integer) yearlst.get(Ctr);
				mgtReference = (String) mgtReferencelst.get(Ctr);
				maSequence = (Integer) maSequencelst.get(Ctr);
				month = (Integer) monthlst.get(Ctr);
				errorDescription = (String) errorDescriptionlst.get(Ctr);
				errorType = (String) errorTypelst.get(Ctr);
				severity = (String) severitylst.get(Ctr);
				if (!prevTableType.equalsIgnoreCase(tableType)) {
					logWriter(" ", uploadLogFileName);

					if (tableType.equalsIgnoreCase("H"))
						logWriter("MGT_HEADERS Errors", uploadLogFileName);
					else if (tableType.equalsIgnoreCase("D"))
						logWriter("MGT_DETAILS Errors", uploadLogFileName);
					else
						logWriter("MGT_BALANCES Errors", uploadLogFileName);

					logWriter(
							"----------------------------------------------------------------------------------------",
							uploadLogFileName);

					if (tableType.equalsIgnoreCase("B")) {
						logWriter("Line # \tYear Mgt Ref. Seq. Month Error Type  Description", uploadLogFileName);
						logWriter("-------\t---- -------- ---- ----- ----------  -----------", uploadLogFileName);
					} else if (tableType.equalsIgnoreCase("D")) {
						logWriter("Line # \tYear Mgt Ref. Seq. Error Type  Description", uploadLogFileName);
						logWriter("-------\t---- -------- ---- ----------  -----------", uploadLogFileName);
					} else {
						logWriter("Line # \tYear Mgt Ref. Error Type  Description", uploadLogFileName);
						logWriter("-------\t---- -------- ----------  -----------", uploadLogFileName);
					}

					firstTimeFlag = 'Y';

					prevTableType = tableType;
				}
				if (localLineNumber != lineNumber) {
					if (firstTimeFlag != 'Y')
						logWriter(" ", uploadLogFileName);

					firstTimeFlag = 'N';
					localLineNumber = lineNumber;

					if (tableType.equalsIgnoreCase("B")) {
						tempStr = "" + lineNumber + "\t" + year + " " + mgtReference + " " + maSequence + " " + month
								+ " " + errorType == "T" ? "Trigger" : "Validation" + " " + errorDescription;
					} else if (tableType.equalsIgnoreCase("D")) {
						tempStr = "" + lineNumber + "\t" + year + " " + mgtReference + " " + maSequence + " "
								+ errorType == "T" ? "Trigger" : "Validation" + " " + errorDescription;
					} else {
						tempStr = "" + lineNumber + "\t" + year + " " + mgtReference + " " + errorType == "T"
								? "Trigger"
								: "Validation" + " " + errorDescription;
					}
				} else {
					if (tableType.equalsIgnoreCase("B")) {
						tempStr = "\t" + year + " " + mgtReference + " " + maSequence + " " + month + " "
								+ errorType == "T" ? "Trigger" : "Validation" + " " + errorDescription;
					} else if (tableType.equalsIgnoreCase("D")) {
						tempStr = "\t" + year + " " + mgtReference + " " + maSequence + " " + errorType == "T"
								? "Trigger"
								: "Validation" + " " + errorDescription;
					} else {
						tempStr = "\t" + year + " " + mgtReference + " " + errorType == "T" ? "Trigger"
								: "Validation" + " " + errorDescription;
					}
				}
				logWriter(tempStr, uploadLogFileName);
			}
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to select for Errors table :" + e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
	}

	private int deleteEntryFromUPLTables() {
		try {
			deletedRecsCount = stmt.executeUpdate("delete From Mgt_Headers_Upl Where Year = " + year + " "
					+ " And Mgt_Reference = " + mgtReference + " And Action_Id = '" + actionId + "'");
		} catch (SQLException e) {
			sqlLogWriter("Error deleting from Mgt_Headers_Upl Table for Year [" + year + "], Mgt_Reference ["
					+ mgtReference + "], Action_Id [" + actionId + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		try {
			deletedRecsCount = stmt.executeUpdate("delete From Mgt_Details_Upl Where Year = " + year + " "
					+ " And Mgt_Reference = " + mgtReference + " And Action_Id = '" + actionId + "'");
		} catch (SQLException e) {
			sqlLogWriter("Error deleting from Mgt_Details_Upl Table for Year [" + year + "], Mgt_Reference ["
					+ mgtReference + "], Action_Id [" + actionId + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		try {
			deletedRecsCount = stmt.executeUpdate("delete From Mgt_Balances_Upl Where Year = " + year + " "
					+ " And Mgt_Reference = " + mgtReference + " And Action_Id = '" + actionId + "'");
		} catch (SQLException e) {
			sqlLogWriter("Error deleting from Mgt_Balances_Upl Table for Year [" + year + "], Mgt_Reference ["
					+ mgtReference + "], Action_Id [" + actionId + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		return (SUCCESSFUL_EXIT);
	}

	private int chkIfRecsExistsInHdrsToProcess() {
		try {
			rs = stmt.executeQuery("Select Count(1) From Mgt_Headers_Upl Where Action_Id = '" + actionId + "'");
			while (rs.next())
				recsCount = rs.getInt("Count(1)");
			if (recsCount == 0)
				return (ERRONEOUS_EXIT);

			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
//			System.out.println("chkIfRecsExistsInHdrsToProcess():" + e.getMessage());
			e.printStackTrace();
			return (ERRONEOUS_EXIT);
		}
	}

	private void cleanupMGTUPLTableEntries() {
		try {
			String tempStr = "";
			logWriter(" ", uploadLogFileName);
			logWriter("Performing Post upload cleanups......\n", uploadLogFileName);
			int rowsDeleted = 0;
			try {
				rowsDeleted = stmt
						.executeUpdate("Delete From Mgt_Validation_Errors Where Action_Id = '" + actionId + "'");

				if (rowsDeleted == 0)
					logWriter("No records were deleted from Mgt_Validation_Errors table - FYI.\n", uploadLogFileName);

				if (DEBUG_MODE == YES) {
					logWriter("Records deleted from the Mgt_Validation_Errors table : [" + rowsDeleted + "]\n",
							uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error trying to delete from Mgt_Validation_Errors for Action_Id = '" + actionId + "' :"
						+ e.getMessage());
				e.printStackTrace();
			}
			try {
				rowsDeleted = stmt.executeUpdate("Delete From Mgt_Headers_Upl Where Action_Id = '" + actionId + "'");

				if (rowsDeleted == 0)
					logWriter("No records were deleted from Mgt_Headers_Upl table - FYI.\n", uploadLogFileName);

				if (DEBUG_MODE == YES) {
					logWriter("Records deleted from the Mgt_Headers_Upl table : [" + rowsDeleted + "]\n",
							uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error trying to delete from Mgt_Headers_Upl for Action_Id = '" + actionId + "' :"
						+ e.getMessage());
			}
			try {
				rowsDeleted = stmt.executeUpdate("Delete From Mgt_Details_Upl Where Action_Id = '" + actionId + "'");

				if (rowsDeleted == 0)
					logWriter("No records were deleted from Mgt_Details_Upl table - FYI.\n", uploadLogFileName);

				if (DEBUG_MODE == YES) {
					logWriter("Records deleted from the Mgt_Details_Upl table : [" + rowsDeleted + "]\n",
							uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error trying to delete from Mgt_Details_Upl for Action_Id = '" + actionId + "' :"
						+ e.getMessage());
			}
			try {
				rowsDeleted = stmt.executeUpdate("Delete From Mgt_Balances_Upl Where Action_Id = '" + actionId + "'");

				if (rowsDeleted == 0)
					logWriter("No records were deleted from Mgt_Balances_Upl table - FYI.\n", uploadLogFileName);

				if (DEBUG_MODE == YES) {
					logWriter("Records deleted from the Mgt_Balances_Upl table : [" + rowsDeleted + "]\n",
							uploadLogFileName);
				}
			} catch (SQLException e) {
				sqlLogWriter("Error trying to delete from Mgt_Balances_Upl for Action_Id = '" + actionId + "':"
						+ e.getMessage());
			}
			con.commit();
		} catch (SQLException e) {
//			System.out.println("cleanupMGTUPLTableEntries :" + e.getMessage());
			e.printStackTrace();
			logWriter("cleanupMGTUPLTableEntries :" + e.getMessage(), uploadLogFileName);
		}
	}

	private String stripSigns(String paramString) {
		StringBuffer sr = new StringBuffer(paramString);
		for (int i = 0; i < sr.length(); i++) {
			if (sr.charAt(i) == '-' || sr.charAt(i) == '+')
				sr.deleteCharAt(i);
		}
		return sr.toString();
	}

	private int dcUpload() {
		try {
			char firstTimeFlag = 'Y';
			int totUploadableRecords = 0;
			int totDetailsRecords = 0;
			int Ctr1 = 0;
			char abortFlag = 'N';
			String tempStr = "";
			finalReturnValue = STATUS_SUCCESS;

			/* Frame the Action_Id For this upload */
			actionId = "";
			actionId = getActionId();
			/* find if upload is permitted for this group or not */
			retVal = findIfUploadAllowedForDCTbls();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/*
			 * Check for locks on these tables, if they have been locked by some build
			 * running at the time of this upload
			 */
			retVal = checkForDCTablesLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("One of the tables [FIN_ADJ_HEADERS, FIN_ADJ_DETAILS, FIN_ADJ_POSTINGS] "
						+ "has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return (ERRONEOUS_EXIT);
			}
			retVal = lockDCTablesForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for DC XL Upload. Cannot proceed now with upload !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}
			/* Set the status to In-Progress mode for the upload request */
			retVal = updateUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile("Uploading to the tables [Fin_Adj_Headers, Fin_Adj_Details & Fin_Adj_Postings]");
			uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + makerId + "_" + GetCurrentDateTime()
					+ ".err";
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";
			try {
				logfile = new FileWriter(uploadLogFileName); // file Open in
																// Write mode
				logfile.close();
			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName
						+ "] for logging process steps !!\n  System Msg [" + e.getMessage() + "]\n");
				writeToMainLogFile("Aborting further processing of this request - FYI");
				return (ERRONEOUS_EXIT);
			}
			logWriter("Action Id obtained for this run : [" + actionId + "] - FYI\n", uploadLogFileName);
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";
			uploadFileName = uploadFile;
			try {
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI");
				logWriter("Aborting further processing of this request - FYI", uploadLogFileName);

				retVal = updateUploadStatus(STATUS_FAILURE);

				if (retVal == ERRONEOUS_EXIT)
					return (ABORT_EXECUTION);

				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}

			logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);

			originalTableName = uploadTableName;

			logWriter("Tables Being uploaded\t:[Fin_Adj_Headers, Fin_Adj_Details & Fin_Adj_Postings]\n",
					uploadLogFileName);

			if (DEBUG_MODE == YES)
				logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);

			retVal = checkForExistenceOfDcTbls();

			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			retVal = loadTxtFileColumnNames(uploadFile);

			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			retVal = validateDCTblCols();

			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			retVal = chkColCntForEachRow();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);

			logWriter("Uploading to UPL Tables first.\n", uploadLogFileName);

			TAB_FLAG = 'Y';

			retVal = uploadDCFileData();

			TAB_FLAG = 'N';
			if (retVal == ERRONEOUS_EXIT) {
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			if (finalReturnValue == ERRONEOUS_EXIT) {
				con.rollback();
				return (ERRONEOUS_EXIT);
			} else {
				con.commit();
			}
			// con.commit();
			abortFlag = 'N';

			/* Starting abort conditions check */

			logWriter("\nChecking for abort conditions......", uploadLogFileName);

			/*
			 * Procure the Current Year & Month from the Vision_Variables table for usage
			 * later
			 */
			tempStr = "";
			try {
				rs = stmt.executeQuery("Select RTrim(Value) From Vision_Variables Where Variable = 'CURRENT_MONTH'");
				while (rs.next())
					tempStr = rs.getString(1);

				currentMonth = Integer.parseInt(tempStr);
			} catch (Exception e) {
				sqlLogWriter("Error fetching Vision_Variables:CURRENT_MONTH :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			try {
				rs = stmt.executeQuery("Select RTrim(Value) From Vision_Variables Where Variable = 'CURRENT_YEAR'");
				while (rs.next())
					tempStr = rs.getString(1);

				currentYear = Integer.parseInt(tempStr);
				yearMonth = currentYear * 100 + currentMonth;
				logWriter("\nCurrent Year_Month  from Vision_Variables is : [" + yearMonth + "]", uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error fetching Vision_Variables:CURRENT_MONTH :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			try {
				rs = stmt.executeQuery("Select Trim(To_Char(Reporting_Date))  From Period_Controls Where Month = "
						+ currentMonth + " And Year = " + currentYear + "");
				while (rs.next())
					firstDay = rs.getString(1);

				logWriter("\nCurrent First Day  from Period_Controls is : [" + firstDay + "]", uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error Reporting_Date from Period_Controls table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (currentMonth + 1 > 12) {
				currentYear++;
				currentMonth = 1;
			} else
				currentMonth++;

			try {
				rs = stmt.executeQuery("Select Trim(To_Char(Reporting_Date))  From Period_Controls Where Month = "
						+ currentMonth + " And Year = " + currentYear + "");
				while (rs.next())
					reportingDate = rs.getString(1);

				logWriter("\nCurrent Reporting Date  from Period_Controls is : [" + reportingDate + "]",
						uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error Reporting_Date from Period_Controls table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (currentMonth == 1) {
				currentMonth = 12;
				currentYear--;
			} else
				currentMonth--;

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 Where Action_Id = '" + actionId
						+ "' And T1.Year_Month < " + yearMonth + "");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Year_Month abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Trim(To_Char(T1.Year_Month)) From Fin_Adj_Headers_Upl T1 "
						+ "Where Action_Id = '" + actionId + "' " + "And T1.Year_Month < " + yearMonth + " ";

				errorMsg = "The following Year_Month values < Current Year & Month in Vision_Variables";
				logWriter(" " + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/*
			 * Check if the Country + Le_Book combination given in the upload table exists
			 * in the Le_Book table
			 */

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And Not Exists (Select 'X' From Le_Book T2  "
						+ " Where T1.Country = T2.Country And T1.Le_Book = T2.Le_Book " + " And T1.Action_Id = '"
						+ actionId + "')");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl 2 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Country + Le_Book existence in Le_Book table abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Country || ',' || Le_Book From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' And Not Exists (Select 'X' From Le_Book T2 "
						+ " Where T1.Country = T2.Country And T1.Le_Book = T2.Le_Book " + " And T1.Action_Id = '"
						+ actionId + "')";
				errorMsg = "The following Country + Le_Book combinations are invalid ";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/*
			 * Check if the Vision_Ouc given in the upload file has same Regulatory Vehicle
			 * as the Default_Ouc in the Le_Book table
			 */

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Postings_Upl T3, Ouc_Codes T4 "
						+ " Where Action_Id = '" + actionId + "' And T3.Vision_Ouc = T4.Vision_Ouc"
						+ " And T4.Regulatory_Vehicle != (Select Regulatory_Vehicle From Le_Book T1, Ouc_Codes T2"
						+ " Where T1.Default_Ouc = T2.Vision_Ouc And T1.Country = T3.Country"
						+ " And T1.Le_Book = T3.Le_Book)");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl 3 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Regulatory_Vehicles from the LE_Book table for abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), T3.Vision_Ouc  From Fin_Adj_Postings_Upl T3, Ouc_Codes T4 "
						+ " Where Action_Id = '" + actionId + "' " + " And T3.Vision_Ouc = T4.Vision_Ouc "
						+ " And T4.Regulatory_Vehicle != "
						+ " (Select Regulatory_Vehicle From Le_Book T1, Ouc_Codes T2 "
						+ " Where T1.Default_Ouc = T2.Vision_Ouc " + " And T1.Country = T3.Country "
						+ " And T1.Le_Book = T3.Le_Book) ";
				errorMsg = "The following Vision_Oucs don't have matching Regulatory_Vehicle for Country + Le_Book combination's Default_Oucs";
				logWriter("" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/*
			 * Check if Year_Month in the upload file > MV:Year_Month and Financials_Daily
			 * flag = 'Y'
			 */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And T1.Year_Month > " + yearMonth + "" + " And T1.Financials_Daily = 'Y'");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl 4 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Year_Month (for Fin Dly Flag = 'Y') abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Trim(To_Char(T1.Year_Month)) From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' " + " And T1.Year_Month > " + yearMonth + " "
						+ " And T1.Financials_Daily = 'Y'";
				errorMsg = "The following Year_Month is > Current Year/Month in Vision_Variables table, where Financials_Daily = 'Y'";
				logWriter("" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/* Data_Type check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And (Data_Type = 'MA'"
						+ " Or Data_Type Not In (Select Alpha_Sub_Tab From Alpha_Sub_Tab T2 Where "
						+ " T2.Alpha_Tab = 301))");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl 5 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Data_Type From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' " + " And (Trim(Data_Type) = 'MA' "
						+ " Or Data_Type Not In (Select Alpha_Sub_Tab From Alpha_Sub_Tab T2 Where "
						+ " T2.Alpha_Tab = 301))";
				errorMsg = "The following Data_Types are invalid";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/* Originating_Ouc check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1" + " Where Action_Id = '" + actionId
						+ "'" + " And Originating_Ouc In (Select Vision_Ouc From Ouc_Codes T2 Where"
						+ " Ouc_Status != 0)");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 6 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Originating_Ouc From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' "
						+ " And Originating_Ouc In (Select Vision_Ouc From Ouc_Codes T2 Where " + " Ouc_Status != 0)";
				errorMsg = "The following Originating_Oucs are invalid";
				logWriter("" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/* Reversal_Flag check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) Into :recsCount From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' And Reversal_Flag Not In ('Y', 'N')");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 7 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Reversal_Flag From Fin_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' " + " And Reversal_Flag Not In ('Y', 'N')";
				errorMsg = "The following Reversal_Flag values are invalid";
				logWriter("" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/* Maker check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery(" SELECT SUM (xx.Hdrs_Count)                                   "
						+ "   FROM (SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1                                            "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker NOT IN (SELECT Vu.Vision_Id                          "
						+ "                                       FROM Vision_Users VU)                      "
						+ "         UNION ALL                                                                "
						+ "         SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1, Vision_Users T2                           "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker = T2.Vision_Id                                       "
						+ "                AND (   (    T2.Update_restriction = 'Y'                          "
						+ "                         AND (   TRIM (T2.legal_vehicle) IS NOT NULL              "
						+ "                              OR TRIM (T2.country_leb) IS NOT NULL))              "
						+ "                     OR (    T2.Auto_Update_restriction = 'Y'                     "
						+ "                         AND (   TRIM (T2.Auto_legal_vehicle) IS NOT NULL         "
						+ "                              OR TRIM (T2.auto_country_leb) IS NOT NULL)))        "
						+ "                AND NOT EXISTS                                                    "
						+ "                           (SELECT 1                                              "
						+ "                              FROM vu_restrict_lv_cleb vu_restrict_lv_cleb        "
						+ "                             WHERE     vu_restrict_lv_cleb.vision_id =            "
						+ "                                          t1.maker                                "
						+ "                                   AND vu_restrict_lv_cleb.country =              "
						+ "                                          t1.country                              "
						+ "                                   AND vu_restrict_lv_cleb.le_book =              "
						+ "                                          t1.le_book)                             "
						+ "         UNION ALL                                                                "
						+ "         SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1, Vision_Users T2                           "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker = T2.Vision_Id                                       "
						+ "                AND (   (    T2.Update_restriction = 'Y'                          "
						+ "                         AND TRIM (T2.OUC_ATTRIBUTE) IS NOT NULL)                 "
						+ "                     OR (    T2.Auto_Update_restriction = 'Y'                     "
						+ "                         AND TRIM (T2.AUTO_OUC_ATTRIBUTE) IS NOT NULL))           "
						+ "                AND NOT EXISTS                                                    "
						+ "                           (SELECT 1                                              "
						+ "                              FROM vu_restrict_oucatt vu_restrict_oucatt          "
						+ "                             WHERE     vu_restrict_oucatt.vision_id = t1.maker    "
						+ "                                   AND vu_restrict_oucatt.OUC =                   "
						+ "                                          t1.originating_ouc)) xx                 ");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 8 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = " SELECT SUM (xx.Hdrs_Count)                                   "
						+ "   FROM (SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1                                            "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker NOT IN (SELECT Vu.Vision_Id                          "
						+ "                                       FROM Vision_Users VU)                      "
						+ "         UNION ALL                                                                "
						+ "         SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1, Vision_Users T2                           "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker = T2.Vision_Id                                       "
						+ "                AND (   (    T2.Update_restriction = 'Y'                          "
						+ "                         AND (   TRIM (T2.legal_vehicle) IS NOT NULL              "
						+ "                              OR TRIM (T2.country_leb) IS NOT NULL))              "
						+ "                     OR (    T2.Auto_Update_restriction = 'Y'                     "
						+ "                         AND (   TRIM (T2.Auto_legal_vehicle) IS NOT NULL         "
						+ "                              OR TRIM (T2.auto_country_leb) IS NOT NULL)))        "
						+ "                AND NOT EXISTS                                                    "
						+ "                           (SELECT 1                                              "
						+ "                              FROM vu_restrict_lv_cleb vu_restrict_lv_cleb        "
						+ "                             WHERE     vu_restrict_lv_cleb.vision_id =            "
						+ "                                          t1.maker                                "
						+ "                                   AND vu_restrict_lv_cleb.country =              "
						+ "                                          t1.country                              "
						+ "                                   AND vu_restrict_lv_cleb.le_book =              "
						+ "                                          t1.le_book)                             "
						+ "         UNION ALL                                                                "
						+ "         SELECT COUNT (1) Hdrs_Count                                              "
						+ "           FROM Fin_Adj_Headers_Upl T1, Vision_Users T2                           "
						+ "          WHERE     T1.Action_Id = '" + actionId + "'                                      "
						+ "                AND T1.Maker = T2.Vision_Id                                       "
						+ "                AND (   (    T2.Update_restriction = 'Y'                          "
						+ "                         AND TRIM (T2.OUC_ATTRIBUTE) IS NOT NULL)                 "
						+ "                     OR (    T2.Auto_Update_restriction = 'Y'                     "
						+ "                         AND TRIM (T2.AUTO_OUC_ATTRIBUTE) IS NOT NULL))           "
						+ "                AND NOT EXISTS                                                    "
						+ "                           (SELECT 1                                              "
						+ "                              FROM vu_restrict_oucatt vu_restrict_oucatt          "
						+ "                             WHERE     vu_restrict_oucatt.vision_id = t1.maker    "
						+ "                                   AND vu_restrict_oucatt.OUC =                   "
						+ "                                          t1.originating_ouc)) xx                 ";
				errorMsg = "The following Maker Ids are invalid";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/* Financials_Daily flag check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And Financials_Daily Not In ('Y', 'N')");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 9 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Financials_Daily From Fin_Adj_Headers_Upl T1, "
						+ " Where Action_Id = '" + actionId + "' " + " And Financials_Daily Not In ('Y', 'N')";
				errorMsg = "The following Financials_Daily flag values are invalid";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/* Financials_Daily flag check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And Financials_Daily Not In ('Y', 'N')");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 10 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Financials_Daily From Fin_Adj_Headers_Upl T1, "
						+ " Where Action_Id = '" + actionId + "' " + " And Financials_Daily Not In ('Y', 'N')";

				errorMsg = "The following Financials_Daily flag values are invalid";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/*
			 * Fin_Dly_End_Date check - should be populated only if Financials_Daily = 'Y'
			 */

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) from Fin_Adj_Headers_Upl T1" + " Where Action_Id = '" + actionId
						+ "' And " + " ((Fin_Dly_End_Date Is Null And Financials_Daily = 'Y')"
						+ " Or (Fin_Dly_End_Date Is Not Null And Financials_Daily = 'N')"
						+ " Or (Fin_Dly_End_Date Is Not Null And Fin_Dly_End_Date Not Between To_Date('" + firstDay
						+ "') And To_Date('" + reportingDate + "')))");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Adj_Headers_Upl 11 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Country + Le_Book existence in Le_Book table abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Count(1) from Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '" + actionId + "' "
						+ " ((Fin_Dly_End_Date Is Null And Financials_Daily = 'Y') "
						+ " Or (Fin_Dly_End_Date Is Not Null And Financials_Daily = 'N') "
						+ " Or (Fin_Dly_End_Date Is Not Null And Fin_Dly_End_Date Not Between To_Date('" + firstDay
						+ "') And To_Date('" + reportingDate + "'))) ";

				errorMsg = "The following Fin_Dly_End_Dates are invalid";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			if (abortFlag == 'Y') {
				logWriter("......Aborting upload......", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			/* End of checks for abort conditions */

			/*
			 * Now for implementing the validations & business rules, as we have
			 * successfully uploaded the data for the tables Fin_Adj_Headers_UPL,
			 * Fin_Adj_Details_UPL & Fin_Adj_Postings_UPL Functions. Call the procedure that
			 */

			/*
			 * TO DO - To call the procedure that does the validations for the records that
			 * were inserted into the UPL tables - While calling the validate function -
			 * just ensure that the Parameter for populating the Fin_Adj_Errors table is
			 * also being supplied.
			 */

			/*
			 * Intialize the variables that are required to call the procedure
			 */
			status = "";
			category = "";
			errorMsg = "";
			successCnt = 0;
			failCnt = 0;

			logWriter("\nValidating upload file data......\n", uploadLogFileName);
			con.commit();
			prepareMailString();
			try {
				callableStatement = con.prepareCall("{call PR_VALIDATE_FIN_ADJ (?,?,?,?,?,?,?,?,?,?)}");

				callableStatement.setString(1, actionId);
				callableStatement.setString(2, "N");
				callableStatement.setString(3, "N");
				callableStatement.setString(4, "N");
				callableStatement.setInt(5, makerId);
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(9, java.sql.Types.NUMERIC);
				callableStatement.registerOutParameter(10, java.sql.Types.NUMERIC);
				callableStatement.executeUpdate();
				status = callableStatement.getString(6);
				// status ="C";
				category = callableStatement.getString(7);
				errorMsg = callableStatement.getString(8);
				successCnt = callableStatement.getInt(9);
				failCnt = callableStatement.getInt(10);
			} catch (SQLException e) {
				sqlLogWriter("Error calling procedure PR_VALIDATE_FIN_ADJ to validate input data :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			/*
			 * Commit now, since we want the insertions made into the
			 * Fin_Adj_Validation_Errors tables, to be permanent
			 */
			con.commit();

			logWriter(" ", uploadLogFileName);

			if (status.equalsIgnoreCase("F")) {
				logWriter(
						"A fatal error occured while trying to validate the procedure. Aborting further processing !!",
						uploadLogFileName);
				logWriter("Message got from the database ....", uploadLogFileName);
				errorMsg = errorMsg + " !!!!!!\n\n";
				logWriter(errorMsg, uploadLogFileName);
				return (STATUS_FAILURE);
			} else if (status.equalsIgnoreCase("E")) {
				logWriter("All input records failed validation. Aborting further processing !!", uploadLogFileName);
				logWriter(" ", uploadLogFileName);
				logWriter("Final Summary report for DC Group Upload : Failed !!!!!!", uploadLogFileName);
				logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
						uploadLogFileName);
				logWriter("Total Success records : [" + successCnt + "]", uploadLogFileName);
				logWriter("Total Failure records : [" + failCnt + "]", uploadLogFileName);
				logWriter("ErrorMsg : [" + errorMsg + "]\n", uploadLogFileName);
				logWriter(" ", uploadLogFileName);
				/* printDcUploadErrorReport(); */

				// return(STATUS_FAILURE);
				retVal = updateUploadStatus(STATUS_SUCCESS_VALIDATION_FAILED);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}
				con.commit();
				logWriter("One or more of the input records have failed validation. ", uploadLogFileName);
				finalReturnValue = STATUS_SUCCESS_VALIDATION_FAILED;
			} else if (status.equalsIgnoreCase("P")) {
				retVal = updateUploadStatus(STATUS_PARTIAL_SUCCESS);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}
				con.commit();
				logWriter("One or more of the input records have failed validation. ", uploadLogFileName);
				finalReturnValue = STATUS_PARTIAL_SUCCESS;
			} else if (status.equalsIgnoreCase("C") || status
					.equalsIgnoreCase("W")) /*
											 * Perform the same operation for successful completion & Warning
											 */
			{
				retVal = updateUploadStatus(STATUS_SUCCESS);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}

				con.commit();
				logWriter("Input records have passed thru Validation.", uploadLogFileName);
				logWriter("Records Inserted/Updated in the Posting Tables - FYI\n", uploadLogFileName);

				String adjEmailAlertFlag = findVisionVariableValue("ADJUSTMENT_EMAIL");
				if (!ValidationUtil.isValid(adjEmailAlertFlag))
					adjEmailAlertFlag = "N";
				if ("Y".equalsIgnoreCase(adjEmailAlertFlag)) {
					retVal = sendValidaterEmailId();
					if (retVal == ERRONEOUS_EXIT) {
						return (ERRONEOUS_EXIT);
					}
				}
			}

			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter("Total Success records : [" + successCnt + "]", uploadLogFileName);
			logWriter("Total Failure records : [" + failCnt + "]\n", uploadLogFileName);
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter(" ", uploadLogFileName);

			/*
			 * TO DO Once the validations have succeeded, then Call the function/procedure
			 * to insert into the respective Fin_Adj_Headers, Fin_Adj_Details &
			 * Fin_Adj_Postings Tables
			 */
			return (finalReturnValue);
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("DCUpload():" + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int checkForDCTablesLock() {
		recsCount = 0;
		try {
			rs = stmt.executeQuery(" Select Count(1) From Vision_Locking "
					+ " Where Table_Name In ('FIN_ADJ_HEADERS', 'FIN_ADJ_DETAILS', 'FIN_ADJ_POSTINGS') "
					+ " And Lock_Status = 'Y'");
			while (rs.next())
				recsCount = rs.getInt("Count(1)");
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error trying to fetch lock status from VISION_LOCKING table for [Fin_Adj_Headers, Fin_Adj_Details & Fin_Adj_Postings]:"
							+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		/* If count is not zero, then the concerned table has been locked */
		if (recsCount != 0)
			return (ERRONEOUS_EXIT);

		return (SUCCESSFUL_EXIT);
	}

	private int lockDCTablesForUpload() {
		try {
			stmt.executeUpdate("Update Vision_Locking Set Lock_Status = 'Y' "
					+ " Where Table_Name In ('FIN_ADJ_HEADERS', 'FIN_ADJ_DETAILS', 'FIN_ADJ_POSTINGS')");

			con.commit();
			lockFlag = 'Y';
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error trying to Lock DC tables for XL Upload [" + uploadTableName + "]:" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int unlockDCTablesAfterUpload() {
		try {
			stmt.executeUpdate(
					"Update Vision_Locking Set Lock_Status = 'N' Where Table_Name In ('FIN_ADJ_HEADERS', 'FIN_ADJ_DETAILS', 'FIN_ADJ_POSTINGS')");
			lockFlag = 'N';
			con.commit();
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeSQLErrToMainLogFile(
					"Error trying to unlock DC tables for XL Upload [" + uploadTableName + "]:" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int findIfUploadAllowedForDCTbls() {
		char abortFlag = 'N';
		int Ctr1 = 0;
		totalGroupUploadTables = 0;
		int recCounttmp = 0;
		try {
			rs = stmt.executeQuery("Select Table_Name, Upload_Allowed From Vision_Tables "
					+ " Where Upper(Table_Name) In ('FIN_ADJ_HEADERS', 'FIN_ADJ_DETAILS', 'FIN_ADJ_POSTINGS') ");
			while (rs.next()) {
				tableNamelst.add(rs.getString("Table_Name"));
				uploadAllowedlst.add(rs.getString("Upload_Allowed"));
				recCounttmp++;
			}
			for (Ctr1 = 0; Ctr1 < recCounttmp; Ctr1++) {
				if (valueNo.equalsIgnoreCase(uploadAllowedlst.get(Ctr1).toString())) {
					writeToMainLogFile("Upload is not allowed for Table [" + tableNamelst.get(Ctr1).toString() + "].");
					abortFlag = 'Y';
				}
			}
			if (Ctr1 != 3) {
				logWriter("One or more of the tables for this upload are not present in Vision_Tables table !!",
						uploadLogFileName);
				logWriter("Aborting further upload \n", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			if (abortFlag == 'Y')
				return ERRONEOUS_EXIT;

			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error preparing to select from Vision_Tables for validating Upload_Allowed status for DC tables :\n"
							+ "SQL Code [" + e.getErrorCode() + "], Message [" + e.getMessage() + "]\n");
			writeToMainLogFile("Query is");
			writeToMainLogFile(sqlStatement);
			return ERRONEOUS_EXIT;

		}
	}

	private int checkForExistenceOfDcTbls() {
		try {
			rs = stmt.executeQuery(" Select Count(1) From ALL_TABLES "
					+ " Where Table_Name In ('FIN_ADJ_HEADERS', 'FIN_ADJ_DETAILS', 'FIN_ADJ_POSTINGS') AND UPPER(OWNER)=UPPER('"
					+ username + "')");

			while (rs.next())
				recsCount = rs.getInt("Count(1)");

			if (recsCount != 3) {
				logWriter(
						"One or more of the tables for DC Upload (Fin_Adj_Headers/Fin_Adj_Details/Fin_Adj_Postings) "
								+ " are not found in the system !!\nPlease create the tables and re-run this upload\n",
						uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			sqlLogWriter("Error trying to validate presence of upload table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int validateDCTblCols() {
		int Ctr1 = 0, Ctr2 = 0;
		char foundFlag = 'Y';
		char firstTimeFlag = 'Y';
		char continueFlag = 'N';
		int recCounttmp = 0;
		retVal = SUCCESSFUL_EXIT;
		tableColumnList = new ArrayList();
		columnNamelst = new ArrayList();
		columnSizelst = new ArrayList();
		quotedTypelst = new ArrayList();
		nullable = new ArrayList();
		decimalsChecklst = new ArrayList();
		dataScalelst = new ArrayList();
		dateTypelst = new ArrayList();
		fileColumnsColumnSize = new ArrayList();
		fileColumnsQuotedType = new ArrayList();
		fileColumnsNullable = new ArrayList();
		fileColumnsDecimalsCheck = new ArrayList();
		fileColumnsAfterDecimals = new ArrayList();
		fileColumnsDataType = new ArrayList();
		totalTableColumns = 0;
		String tableName = "";
		if (uploadGroup == DC_UPLOAD) {
			tableName = "DCUPLOAD";
		} else if (uploadGroup == DC_UPLOAD_SHORT) {
			tableName = "DCUPLOAD_SHORT";
		}

		try {
			rs = stmt.executeQuery("Select Column_Name, Col_Nullable, Col_Size, Quoted_Type, After_Decimals, "
					+ " Date_Type, Decimals_Check " + " From Xl_Upload_Cols_Repository Where Upload_Genre = '"
					+ tableName + "' " + " Order By Col_Sequence ");
			while (rs.next()) {
				columnNamelst.add(rs.getString("Column_Name"));
				nullable.add(rs.getString("Col_Nullable"));
				columnSizelst.add(rs.getString("Col_Size"));
				quotedTypelst.add(rs.getString("Quoted_Type"));
				dataScalelst.add(rs.getString("After_Decimals"));
				dateTypelst.add(rs.getString("Date_Type"));
				decimalsChecklst.add(rs.getString("Decimals_Check"));
				totalTableColumns++;
			}
			tableColumnList.addAll(columnNamelst);
			tableColumnList.addAll(nullable);
			tableColumnList.addAll(columnSizelst);
			tableColumnList.addAll(quotedTypelst);
			tableColumnList.addAll(dataScalelst);// after Decimals
			tableColumnList.addAll(dateTypelst);
			tableColumnList.addAll(decimalsChecklst);
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to select from Xl_Upload_Cols_Repository table :" + e.getMessage());
			logWriter("Query was ", uploadLogFileName);
			logWriter(sqlStatement, uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
		if (totalTableColumns == 0) {
			logWriter("The table does not have any columns to upload !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
		if (columnNamelst == null || columnNamelst.size() == 0) {
			logWriter("The Column List should be maintained in the table [Xl_Upload_Cols_Repository] !!",
					uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
		String missingColumns = "";
		for (int ct = 0; ct < columnNamelst.size(); ct++) {
			if (!fileColumnsNames.contains(columnNamelst.get(ct))) {
				if (ValidationUtil.isValid(missingColumns))
					missingColumns = missingColumns + "," + columnNamelst.get(ct);
				else
					missingColumns = missingColumns + columnNamelst.get(ct);
			}
		}
		if (ValidationUtil.isValid(missingColumns)) {
			logWriter("The Columns [" + missingColumns + "] are missing in the Excel File uploaded !!",
					uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
		for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
			foundFlag = 'N';
			for (Ctr2 = 0; Ctr2 < totalTableColumns; Ctr2++) {
				if (fileColumnsNames.get(Ctr1).equals(columnNamelst.get(Ctr2))) {
					fileColumnsColumnSize.add(Ctr1, columnSizelst.get(Ctr2));
					fileColumnsQuotedType.add(Ctr1, quotedTypelst.get(Ctr2));
					fileColumnsNullable.add(Ctr1, nullable.get(Ctr2));
					fileColumnsDecimalsCheck.add(Ctr1, decimalsChecklst.get(Ctr2));
					fileColumnsAfterDecimals.add(Ctr1, dataScalelst.get(Ctr2));
					fileColumnsDataType.add(Ctr1, dateTypelst.get(Ctr2));

					foundFlag = 'Y';
					break;
				}
			}
			if (foundFlag == 'N') {
				if (firstTimeFlag == 'Y') {
					logWriter("The following columns names are invalid for the selected table name !!",
							uploadLogFileName);
					firstTimeFlag = 'N';
				}
				logWriter("[" + fileColumnsNames.get(Ctr1) + "]", uploadLogFileName);
				retVal = ERRONEOUS_EXIT;
			}
		}
		if (retVal != SUCCESSFUL_EXIT)
			return (retVal);

		return (retVal);
	}

	private int uploadDCFileData() {
		retVal = SUCCESSFUL_EXIT;
		try {
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			char ch;
			int lineCtr = 1;
			String tempStr = "";
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			totalInputFileRecords = 0;
			adjSource = "";
			adjSource = "'" + DC_ADJ_SOURCE + "'";
			sourceId = DC_SOURCE_ID;
			recordType = DC_RECORD_TYPE;

			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = null;
			String[] wordsArray = null;
			workbook = new XSSFWorkbook(fileReader1);

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String specificData = "";
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							String columnSpecificDataType = (String) dateTypelst.get(Ctr2);
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} else if (cellType == CellType.STRING)  {
//								if (cellType == Cell.CELL_TYPE_STRING) {
								specificData = cell.getStringCellValue();
							}
							else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
//							else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										} catch (Exception e) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										}
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}

								}

							}
						} else {
							specificData = " ";
						}
						/*
						 * if(ValidationUtil.isValid(lineFetched)) lineFetched = (lineFetched +
						 * specificData).trim(); else lineFetched = specificData;
						 */
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = (lineFetched == null ? "" : lineFetched)
									+ specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
						}

					}

					if (lineFetched == null) {
						break;
					} else {
						wordsArray = lineFetched.split("\n");
						lineFetched = "";
						for (String each : wordsArray) {
							lines.add(each);
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();
			for (Ctr1 = 0; Ctr1 < lines.size(); Ctr1++) {

				uploadData = lines.get(Ctr1).toString().split("\t");
				for (Ctr2 = 0; Ctr2 < uploadData.length; Ctr2++) {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr2).toString()))
						uploadData[Ctr2] = checkAndProcessQuotes(uploadData[Ctr2]);
				}
				lineCtr++;

				retVal = doInsertUpdateOpsForDCUpload(lineCtr);

				if (retVal == ERRONEOUS_EXIT) {
					retVal = ERRONEOUS_EXIT;
					break;
				}
				totalInputFileRecords++;
			}
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("uploadDCFileData() :" + e.getMessage());
			e.printStackTrace();
			retVal = ERRONEOUS_EXIT;
		}
		return (retVal);
	}

	private int doInsertUpdateOpsForDCUpload(int paramLineCtr) {

		int Ctr1 = 0, Ctr2 = 0;
		String tempStr = "";
		int localRetVal = 0;
		char newHeadersFlag = 'N';
		char newDetailsFlag = 'N';
		char newPostingsFlag = 'N';
		char commaFlag = 'N';

		retVal = SUCCESSFUL_EXIT;

		/*
		 * Load the contents of the upload file into memory, those fields that are part
		 * of the virtual keys, for use later, in identifying existing records in
		 * Fin_Mth_Balances & Fin_Adj_Postings table
		 */
		retVal = loadVirtualKeyFields();
		if (retVal == ERRONEOUS_EXIT) {
			return (ERRONEOUS_EXIT);
		}
		if (uploadGroup == DC_UPLOAD_SHORT) {
			String officeAccountTmp = officeAccount.replaceAll("'", "");
			String contractIdTmp = contractId.replaceAll("'", "");
			if (!ValidationUtil.isValid(contractIdTmp) && !ValidationUtil.isValid(officeAccountTmp)
					|| ("0".equalsIgnoreCase(contractIdTmp) && "0".equalsIgnoreCase(officeAccountTmp))) {

				logWriter(paramLineCtr
						+ ": Contract Id and Office Accout both cannot be 0 in DC Short Upload!!!.Kindly Proceed through the Normal Upload ",
						uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
		}

		/*
		 * Frame an insert statement into the corresponding UPL tables of Headers,
		 * Details & Postings Tables and then commence the operations
		 */
		insertStmt = "Insert Into Fin_Adj_Headers_Upl (Country, Le_Book, Year_Month, "
				+ " Adj_Source, Adj_Reference, Data_Type, FA_Reason_Code, Supplement, "
				+ " Originating_Ouc, Reversal_Flag, Financials_Daily, Fin_Dly_End_Date, "
				+ " Fin_Adj_Headers_Status, Maker, Verifier, Internal_Status, "
				+ " Record_Indicator, Date_Creation, Date_Last_Modified, New_Record_Flag, "
				+ " Bulk_Update_Flag, Action_Id, Line_Number) Values (" + uploadData[IDX_DCUPLOAD_COUNTRY] + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_LE_BOOK], IDX_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_YEAR_MONTH], IDX_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr + ",";

		tempStr = "";
		tempStr = adjSource;
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ADJ_REFERENCE], IDX_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		adjReference = uploadData[IDX_DCUPLOAD_ADJ_REFERENCE];

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'LB'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_DATA_TYPE], IDX_DCUPLOAD_DATA_TYPE);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'7'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FA_REASON_CODE], IDX_DCUPLOAD_FA_REASON_CODE);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = formatFieldValue(uploadData[4], 4);
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_SUPPLEMENT], IDX_DCUPLOAD_SUPPLEMENT);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		String orginationOucDCShort = "";

		if (uploadGroup == DC_UPLOAD_SHORT) {
			String query = "";
			try {
				if (uploadGroup == DC_UPLOAD_SHORT) {
					String contractIdTmp = contractId.replaceAll("'", "");
					String eopBal = "0";
					if (ValidationUtil.isValid(uploadData[11])) {
						eopBal = uploadData[11];
					}
					if (contractIdTmp.equalsIgnoreCase("0")) {
						query = " SELECT Distinct Customer_ID,Customer_ID Customer_ID_Mgt,Currency,Currency MIS_Currency,"
								+ "  CASE WHEN  " + eopBal
								+ " != 0 AND t1.vision_gl BETWEEN T2.PL_GL_START AND T2.PL_GL_END THEN "
								+ "  T2.YTD_PL_GL "
								+ "  WHEN  t1.vision_gl BETWEEN T2.BS_GL_START AND T2.BS_GL_END OR t1.vision_gl BETWEEN T2.CONT_GL_START AND t2.CONT_GL_END "
								+ "  THEN T1.vision_gl ELSE '0' END BS_GL, "
								+ "  CASE  WHEN    ( t1.vision_gl BETWEEN T2.BS_GL_START AND T2.BS_GL_END "
								+ "  OR t1.vision_gl BETWEEN T2.CONT_GL_START AND t2.CONT_GL_END)  AND " + eopBal
								+ " != 0 THEN " + "  DECODE(ABS(" + eopBal + ")," + eopBal
								+ ",T2.GENERIC_LIABILITY_PL_GL,T2.GENERIC_ASSET_PL_GL) "
								+ "  WHEN t1.vision_gl BETWEEN T2.PL_GL_START AND T2.PL_GL_END   "
								+ "  THEN t1.vision_gl ELSE T2.GENERIC_ASSET_PL_GL END PL_GL,"
								+ "  Vision_ouc,Vision_ouc Original_ouc,"
								+ "  Vision_Sbu,t2.Default_OUC,t1.Account_Officer,'0' GL_Enrich_ID,'NA' Cost_Center,9999 Accrual_Status,'NA' Credit_Line "
								+ "  FROM Accounts t1, Le_Book T2 WHERE t1.country = t2.country "
								+ "  AND t1.le_book = t2.le_book AND t1.Country = " + uploadData[IDX_DCUPLOAD_COUNTRY]
								+ " AND t1.LE_book =  " + uploadData[IDX_DCUPLOAD_LE_BOOK] + " "
								+ "  and t1.Account_No = " + officeAccount + " ";
					} else {
						query = " SELECT Distinct Customer_ID,Customer_ID Customer_ID_Mgt,Currency,Currency MIS_Currency,"
								+ "  CASE WHEN  " + eopBal
								+ " != 0 AND t1.vision_gl BETWEEN T2.PL_GL_START AND T2.PL_GL_END THEN "
								+ "  T2.YTD_PL_GL "
								+ "  WHEN  t1.vision_gl BETWEEN T2.BS_GL_START AND T2.BS_GL_END OR t1.vision_gl BETWEEN T2.CONT_GL_START AND t2.CONT_GL_END "
								+ "  THEN T1.vision_gl ELSE '0' END BS_GL, "
								+ "  CASE  WHEN    ( t1.vision_gl BETWEEN T2.BS_GL_START AND T2.BS_GL_END "
								+ "	OR t1.vision_gl BETWEEN T2.CONT_GL_START AND t2.CONT_GL_END)  AND " + eopBal
								+ " != 0 THEN " + "  DECODE(ABS(" + eopBal + ")," + eopBal
								+ ",T2.GENERIC_LIABILITY_PL_GL,T2.GENERIC_ASSET_PL_GL) "
								+ "  WHEN t1.vision_gl BETWEEN T2.PL_GL_START AND T2.PL_GL_END   "
								+ "  THEN t1.vision_gl ELSE T2.GENERIC_ASSET_PL_GL END PL_GL,"
								+ "  Vision_ouc,Vision_ouc Original_ouc,"
								+ "  Vision_Sbu,t2.Default_OUC,t1.Account_Officer,'0' GL_Enrich_ID,'NA' Cost_Center,9999 Accrual_Status,'NA' Credit_Line "
								+ "  FROM Accounts t1, Le_Book T2 WHERE  t1.country = t2.country "
								+ "  AND t1.le_book = t2.le_book AND t1.Country = " + uploadData[IDX_DCUPLOAD_COUNTRY]
								+ " AND t1.LE_book =  " + uploadData[IDX_DCUPLOAD_LE_BOOK] + " "
								+ "  and t1.Account_No = " + contractId + " ";
					}
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						customerId = "'" + rs.getString("Customer_ID") + "'";
						customerIdMgt = "'" + rs.getString("Customer_ID_Mgt") + "'";
						// currency = "'"+rs.getString("Currency")+"'";
						// misCurrency = "'"+rs.getString("MIS_Currency")+"'";
						bsGl = "'" + rs.getString("BS_GL") + "'";
						plGl = "'" + rs.getString("PL_GL") + "'";
						visionOuc = "'" + rs.getString("Vision_ouc") + "'";
						originalOuc = "'" + rs.getString("Default_ouc") + "'";
						visionSbu = "'" + rs.getString("Vision_Sbu") + "'";
						originatingOuc = "'" + rs.getString("Default_ouc") + "'";
						accountOfficer = "'" + rs.getString("Account_Officer") + "'";
						glEnrichId = "'" + rs.getString("GL_Enrich_ID") + "'";
						costCenter = "'" + rs.getString("Cost_Center") + "'";
						accrualStatus = rs.getInt("Accrual_Status");
						creditLine = "'" + rs.getString("Credit_Line") + "'";
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			tempStr = "," + originatingOuc + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ORIGINATING_OUC], IDX_DCUPLOAD_ORIGINATING_OUC);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = formatFieldValue(uploadData[5], 5);
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_REVERSAL_FLAG], IDX_DCUPLOAD_REVERSAL_FLAG);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'N'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FINANCIALS_DAILY], IDX_DCUPLOAD_FINANCIALS_DAILY);
		}

		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FIN_DLY_END_DATE], IDX_DCUPLOAD_FIN_DLY_END_DATE);
		}

		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", 10";

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + REC_IND_APPROVED + ", "
				+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + "";

		/*
		 * Before inserting into the UPL table, check to see if the record already
		 * exists in the Fin_Adj_Headers table. If so, we must appropriately update
		 * New_Record_Flag in the UPL table.
		 */
		Boolean dataTypeFlag = true;
		try {
			rs = stmt.executeQuery(
					" Select Trim(Nvl(Data_Type, 'XX')) Data_Type " + " From Fin_Adj_Headers Where Country = " + country
							+ "" + " And Le_Book = " + leBook + " And Year_Month = " + yearMonth + ""
							+ " And Adj_Source = " + adjSource + " And Adj_Reference = " + adjReference + "");

			if (rs.next()) {
				tempDataType = rs.getString("Data_Type");
			} else {
				dataTypeFlag = false;
			}
		} catch (SQLException e) {
			logWriter("Error checking existing record from Fin_Adj_Headers table fsd :" + e.getMessage(),
					uploadLogFileName);
			sqlLogWriter(e.getMessage() + "!!Values used for the query were Country [" + country + "], Le_Book ["
					+ leBook + "], Year_Month [" + yearMonth + "], Adj_Source [" + adjSource + "], Adj_Reference ["
					+ adjReference + "]\n");
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
		if (uploadGroup == DC_UPLOAD) {
			if (!dataTypeFlag) {
				newHeadersFlag = 'Y';
				if (uploadData[IDX_DCUPLOAD_DATA_TYPE].equals("RE")) {
					logWriter(
							"" + appendZeroforCtr(paramLineCtr)
									+ "\t\tFin_Adj_Headers_Upl Insert Failed - Insert not allowed for Data_Type = RE",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
			} else {
				newHeadersFlag = 'N';
				if (tempDataType.equals("RE") || uploadData[IDX_DCUPLOAD_DATA_TYPE].equals("RE")) {
					logWriter(
							"" + appendZeroforCtr(paramLineCtr)
									+ "\t\tFin_Adj_Headers_Upl Insert Failed - Insert not allowed for Data_Type = RE",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
			}
		} else {
			if (!dataTypeFlag) {
				newHeadersFlag = 'Y';
			} else {
				newHeadersFlag = 'N';
			}
		}

		/*
		 * If mandatory fields are mis-match between one upload file line and the other
		 * it is an error
		 */
		Boolean sqlFound = true;
		try {
			rs = stmt.executeQuery("Select Data_Type, Fa_Reason_Code, Supplement, Originating_Ouc,"
					+ " Reversal_Flag, Financials_Daily, Trim(To_Char(Fin_Dly_End_Date, 'DD-MM-YYYY')) Fin_Dly_End_Date "
					+ " From Fin_Adj_Headers_Upl Where Action_Id = '" + actionId + "' " + " And Country = " + country
					+ " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth + " And Adj_Source = "
					+ adjSource + "" + " And Adj_Reference = " + adjReference + "");

			if (rs.next()) {
				compDataType = rs.getString("Data_Type");
				compFaReasonCode = rs.getString("Fa_Reason_Code");
				compSupplement = rs.getString("Supplement");
				compOriginatingOuc = rs.getString("Originating_Ouc");
				compReversalFlag = rs.getString("Reversal_Flag");
				compFinancialsDaily = rs.getString("Financials_Daily");
				compFinDlyEndDate = rs.getString("Fin_Dly_End_Date");
			} else {
				sqlFound = false;
			}
		} catch (SQLException e) {
			logWriter("Error checking existing record from Fin_Adj_Headers table :" + e.getMessage(),
					uploadLogFileName);
			sqlLogWriter(e.getMessage() + "!!Values used for the query were Country [" + country + "], Le_Book ["
					+ leBook + "], Year_Month [" + yearMonth + "], Adj_Source [" + adjSource + "], Adj_Reference ["
					+ adjReference + "]\n");
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
		if (sqlFound) {
			if (compDataType.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_DATA_TYPE])
					|| compSupplement.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_SUPPLEMENT])
					|| compOriginatingOuc.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_ORIGINATING_OUC])
					|| compReversalFlag.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_REVERSAL_FLAG])
					|| compFinancialsDaily.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_FINANCIALS_DAILY])
					|| compFaReasonCode.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_FA_REASON_CODE].toString())) {
				logWriter(
						"" + appendZeroforCtr(paramLineCtr)
								+ "\t\tFin_Adj_Headers_Upl Insert Failed - Inconsistent header details",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			} else {
				if (ValidationUtil.isValid(compFinDlyEndDate) && uploadData[IDX_DCUPLOAD_FIN_DLY_END_DATE].length() > 0
						|| !ValidationUtil.isValid(compFinDlyEndDate)
								&& uploadData[IDX_DCUPLOAD_FIN_DLY_END_DATE].length() == 0) {
					logWriter(
							"" + appendZeroforCtr(paramLineCtr)
									+ "\t\tFin_Adj_Headers_Upl Insert Failed - Inconsistent header details",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
				if (ValidationUtil.isValid(compFinDlyEndDate))
					indic1 = 0;
				else
					indic1 = -1;
				if (indic1 != -1 && uploadData[IDX_DCUPLOAD_FIN_DLY_END_DATE].length() > 0
						&& compFinDlyEndDate.equalsIgnoreCase(uploadData[IDX_DCUPLOAD_FIN_DLY_END_DATE])) {
					logWriter(
							"" + appendZeroforCtr(paramLineCtr)
									+ "\t\tFin_Adj_Headers_Upl Insert Failed - Inconsistent header details",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
			}
		}
		insertStmt = insertStmt + ", '" + newHeadersFlag + "', 'N', '" + actionId + "', " + paramLineCtr + ")";

		/* Attempt an insert operation to Fin_Adj_Headers_Upl Table now */
		if (!sqlFound) {
			try {
				stmt.executeUpdate(insertStmt);
				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Headers_Upl  Insert succeeded",
						uploadLogFileName);
				totalSuccessRecords++;
				con.commit();
			} catch (SQLException e) {
				if (!e.getMessage().contains("unique constraint")) {
					sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Headers_Upl Insert Failed :"
							+ e.getMessage());
					logWriter(insertStmt, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
				} else {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Headers_Upl  Insert Failed :"
							+ e.getMessage(), uploadLogFileName);
					totalFailureRecords++;
				}
			}
		}

		sequenceFm = 0;
		adjId = 0;
		sequenceFa = 0;

		if (uploadGroup == DC_UPLOAD) {
			customerId = uploadData[IDX_DCUPLOAD_CUSTOMER_ID];
			creditLine = uploadData[IDX_DCUPLOAD_CREDIT_LINE];
			originatingOuc = uploadData[IDX_DCUPLOAD_ORIGINATING_OUC];
			officeAccount = uploadData[IDX_DCUPLOAD_OFFICE_ACCOUNT];
		}

		Boolean dataFoundFlag = false;
		try {
			String query = "Select T1.Adj_Id, T1.Sequence_Fm, T1.Sequence_Fa "
					+ " From Fin_Adj_Postings T1, Fin_Adj_Details T2 " + " Where T1.Adj_Reference = " + adjReference
					+ " " + " And T1.Country       = " + country + "" + " And T1.Le_Book = " + leBook + ""
					+ " And T1.Year_Month      = " + yearMonth + "" + " And T1.Gl_Enrich_Id    = " + glEnrichId + ""
					+ " And T1.Bs_Gl           = " + bsGl + "" + " And T1.Pl_Gl           = " + plGl + ""
					+ " And T1.Currency        = " + currency + "" + " And T1.Mis_Currency    = " + misCurrency + ""
					+ " And T1.Contract_Id     = " + contractId + "" + " And T1.Office_Account  = " + officeAccount + ""
					+ " And T1.Customer_Id     = " + customerId + "" + " And T1.Vision_Ouc       = " + visionOuc + ""
					+ " And T1.Cost_Center     = " + costCenter + "" + " And T1.Vision_Sbu       = " + visionSbu + ""
					+ " And T1.Account_Officer = " + accountOfficer + "" + " And T1.Source_Id       = " + sourceId + ""
					+ " And T1.Original_Ouc    = " + originalOuc + "" + " And T1.Customer_Id_Mgt = " + customerIdMgt
					+ "" + " And T2.Fin_Adj_Details_Status != 40	" + " And t1.Country         = T2.Country"
					+ " And T1.Le_Book         = T2.Le_Book" + " And T1.Year_Month      = T2.Year_Month"
					+ " And T1.Adj_Source      = T2.Adj_Source" + " And T1.Adj_Reference   = T2.Adj_Reference"
					+ " And T1.Adj_Id          = T2.Adj_Id";

			rs = stmt.executeQuery(query);
			if (rs.next()) {
				adjId = rs.getInt("Adj_Id");
				sequenceFm = rs.getInt("Sequence_Fm");
				sequenceFa = rs.getInt("Sequence_Fa");
				dataFoundFlag = true;
			}
		} catch (SQLException e) {
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr)
					+ "\t\tError occured while trying to obtain Adj_Id From Postings for the given data :"
					+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		if (!dataFoundFlag) {
			sequenceFa = 0;
			adjId = 0;
			newDetailsFlag = 'Y';
			newPostingsFlag = 'Y';
		} else {
			newDetailsFlag = 'N';
			if (sequenceFa == 0) {
				newPostingsFlag = 'Y';
				sequenceFa = 1;
			} else
				newPostingsFlag = 'N';
		}
		try {
			insertStmt = "Insert Into Fin_Adj_Details_Upl(Country, Le_Book, "
					+ " Year_Month, Adj_Source, Adj_Reference, Adj_Id,  "
					+ " Override_Imbalance, Batch_Total_Amount, Postings_Count, "
					+ " Validation_Date, Posted_Date, Fin_Adj_Details_Status, " + " Maker, Verifier, Internal_Status, "
					+ " Record_Indicator, Date_Creation, Date_Last_Modified, "
					+ " New_Record_Flag, Bulk_Update_Flag, Action_Id, Line_Number) Values ("
					+ uploadData[IDX_DCUPLOAD_COUNTRY] + "";

			if (adjId == 0) {
				int tmpCount = 0;
				rs = stmt.executeQuery(" Select Adj_Id From Fin_Adj_Details " + " Where Country = " + country + ""
						+ " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth + ""
						+ " And Adj_Reference = " + adjReference + "" + " And Fin_Adj_Details_Status != 40");

				while (rs.next()) {
					adjId = rs.getInt("Adj_Id");
					tmpCount++;
				}

				if (tmpCount == 0) {
					adjId = 0;
				} else
					newDetailsFlag = 'N';
			}
		} catch (SQLException e) {
			if (e.getErrorCode() != SQL_NO_ERRORS && e.getErrorCode() != SQL_NOT_FOUND) {
				sqlLogWriter("Error trying to fetch unauth. Adj_Id from Fin_Adj_Details table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
		}
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_LE_BOOK], IDX_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_YEAR_MONTH], IDX_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr + ",";

		tempStr = "";
		tempStr = "'" + DC_ADJ_SOURCE + "'";
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ADJ_REFERENCE], IDX_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjId + ", 'N', 0, 0, NULL, NULL, 10";

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + REC_IND_APPROVED + ", "
				+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ", '" + newDetailsFlag + "', 'N', '"
				+ actionId + "', " + paramLineCtr + ")";

		int uplDetAvailCnt = 0;
		try {
			rs = stmt.executeQuery(" Select Adj_Id From Fin_Adj_Details_Upl " + " Where Country = " + country + ""
					+ " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth + "" + " And Adj_Reference = "
					+ adjReference + " and adj_source = " + adjSource + " and action_ID = '" + actionId + "' ");

			while (rs.next()) {
				uplDetAvailCnt++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (uplDetAvailCnt == 0) {
			try {
				stmt.executeUpdate(insertStmt);
				// logWriter(" \t\tFin_Adj_Details_Upl Insert succeeded");
				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Details_Upl  Insert succeeded",
						uploadLogFileName);
				con.commit();
				totalSuccessRecords++;
			} catch (SQLException e) {
				if (!e.getMessage().contains("unique constraint")) {
					sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + " \t\tFin_Adj_Details_Upl Insert Failed :"
							+ e.getMessage());
					finalReturnValue = ERRONEOUS_EXIT;
					retVal = ERRONEOUS_EXIT;
					totalFailureRecords++;
				} else {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Details_Upl  Insert Failed :"
							+ e.getMessage(), uploadLogFileName);
					totalFailureRecords++;
				}
			}
		}

		/*
		 * Before inserting into the Postings UPL table, see if there is a valid
		 * Sequence_Fm associated with the postings being inserted
		 */
		if (sequenceFm == 0) {
			sequenceFm = -1;
			try {
				rs = stmt.executeQuery("Select Nvl(Min(Sequence_Fm), -1) " + " From Fin_Mth_Balances T1 "
						+ " where T1.Country       = " + country + "" + " And T1.Le_Book = " + leBook + ""
						+ " And T1.Year_Month      = " + yearMonth + "" + " And T1.Gl_Enrich_Id    = " + glEnrichId + ""
						+ " And T1.Bs_Gl           = " + bsGl + "" + " And T1.Pl_Gl           = " + plGl + ""
						+ " And T1.Currency        = " + currency + "" + " And T1.Mis_Currency    = " + misCurrency + ""
						+ " And T1.Contract_Id     = " + contractId + "" + " And T1.Office_Account  = " + officeAccount
						+ "" + " And T1.Customer_Id     = " + customerId + "" + " And T1.Vision_Ouc       =" + visionOuc
						+ "" + " And T1.Cost_Center     = " + costCenter + "" + " And T1.Vision_Sbu       =" + visionSbu
						+ "" + " And T1.Account_Officer = " + accountOfficer + "" + " And T1.Source_Id       = "
						+ sourceId + "" + " And T1.Original_Ouc    = " + originalOuc + "" + " And T1.Customer_Id_Mgt = "
						+ customerIdMgt + "" + " And T1.Record_Type Not in (4,13) "
						+ " And T1.Record_Type Not In (Select Return_Value_Int From Vision_Variables_Narrow"
						+ " Where Variable = 'OFFSET_RECORD_TYPES')");
				while (rs.next())
					sequenceFm = rs.getInt(1);
			} catch (SQLException e) {
				sqlLogWriter(appendZeroforCtr(paramLineCtr)
						+ "\t\tError fetching the Sequence_Fm from Fin_Mth_Balances table :" + e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				return (ERRONEOUS_EXIT);
			}
		}
		if (sequenceFm == -1) {
			indic1 = NULL_VALUE;
			sequenceFm = 0;
			recordType = DC_RECORD_TYPE;
			sourceId = DC_SOURCE_ID;
		} else {
			try {
				rs = stmt.executeQuery("Select Record_Type, Source_Id " + " From Fin_Mth_Balances Where Country = "
						+ country + " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth
						+ " And Sequence_Fm = " + sequenceFm + "");

				while (rs.next()) {
					finMthRecordType = rs.getInt("Record_Type");
					finMthSourceId = rs.getInt("Source_Id");
				}
			} catch (SQLException e) {
				sqlLogWriter("Error fetching Record_Type & Source_ID from Fin_MTh_Balance table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			recordType = finMthRecordType;
			sourceId = finMthSourceId;
		}

		/*
		 * At this point, appropriately, the Fin_Adj_Postings_UPL record has to be
		 * inserted
		 */

		/*
		 * Prepare the Update statement also, as it would be useful later, if the same
		 * postings record were to be detected in the input file
		 */

		insertStmt = "Insert Into Fin_Adj_Postings_Upl(Country, Le_Book, Year_Month, Adj_Source, Sequence_FM, "
				+ " Adj_Reference, Adj_Id, Sequence_FA, Value_Date, " + " Reversal_Posting, Calc_Pool_Rate, Calc_Pool, "
				+ " Customer_ID, Customer_ID_Mgt, Gl_Enrich_Id, Contract_ID, Office_Account, "
				+ " BS_GL, PL_GL, Vision_OUC, Vision_SBU, Account_Officer, Cost_Center, "
				+ " Currency, Mis_Currency, Accrual_Status, Credit_Line, Pool_Code, "
				+ " Source_Id, Record_Type, Original_Ouc, Profit_Loss_Lcy, "
				+ " Eop_Balance_Lcy, Avg_Balance_Dr_Lcy, Avg_Balance_Cr_Lcy, Floating_Pool_Dr_Lcy, "
				+ " Floating_Pool_Cr_Lcy, Corres_Pool_Dr_Lcy, Corres_Pool_Cr_Lcy, "
				+ " Reserve_Asset_Cost_Lcy, Financial_Attribute_1, Financial_Attribute_2, "
				+ " Financial_Attribute_3, Source_Flag, Maker, "
				+ " Verifier, Internal_Status, Date_Last_Modified, Date_Creation, "
				+ " New_Record_Flag, Bulk_Update_Flag, Action_Id, Line_Number) Values ("
				+ uploadData[IDX_DCUPLOAD_COUNTRY] + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_LE_BOOK], IDX_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_YEAR_MONTH], IDX_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjSource + "";

		/*
		 * For Now, enter an Empty Sequence_Fm for this entry. We shall update at a
		 * later point
		 */
		insertStmt = insertStmt + ", " + sequenceFm + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ADJ_REFERENCE], IDX_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjId + ", " + sequenceFa + "";

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_VALUE_DATE], IDX_DCUPLOAD_VALUE_DATE);
		}
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", '" + DC_REVERSAL_POSTING + "'";

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_RATE], IDX_DCUPLOAD_RATE);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'N'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CALC_POOL], IDX_DCUPLOAD_CALC_POOL);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + customerId + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CUSTOMER_ID], IDX_DCUPLOAD_CUSTOMER_ID);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + customerIdMgt + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CUSTOMER_ID_MGT], IDX_DCUPLOAD_CUSTOMER_ID_MGT);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + glEnrichId + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_GL_ENRICH_ID], IDX_DCUPLOAD_GL_ENRICH_ID);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + contractId + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CONTRACT_ID], IDX_DCUPLOAD_CONTRACT_ID);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + officeAccount + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_OFFICE_ACCOUNT], IDX_DCUPLOAD_OFFICE_ACCOUNT);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + bsGl + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_BS_GL], IDX_DCUPLOAD_BS_GL);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + plGl + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_PL_GL], IDX_DCUPLOAD_PL_GL);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + visionOuc + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_VISION_OUC], IDX_DCUPLOAD_VISION_OUC);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + visionSbu + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_VISION_SBU], IDX_DCUPLOAD_VISION_SBU);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + accountOfficer + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ACCOUNT_OFFICER], IDX_DCUPLOAD_ACCOUNT_OFFICER);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + costCenter + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_COST_CENTER], IDX_DCUPLOAD_COST_CENTER);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + currency + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CURRENCY], IDX_DCUPLOAD_CURRENCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + misCurrency + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_MIS_CURRENCY], IDX_DCUPLOAD_MIS_CURRENCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + accrualStatus + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ACCRUAL_STATUS], IDX_DCUPLOAD_ACCRUAL_STATUS);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'NA'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CREDIT_LINE], IDX_DCUPLOAD_CREDIT_LINE);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'NOPOOL'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_POOL_CODE], IDX_DCUPLOAD_POOL_CODE);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",'990'";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_SOURCE_ID], IDX_DCUPLOAD_SOURCE_ID);
		}
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + recordType + "";

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = "," + originatingOuc + "";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_ORIGINATING_OUC], IDX_DCUPLOAD_ORIGINATING_OUC);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = formatFieldValue(uploadData[10], 10);
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_PROFIT_LOSS_LCY], IDX_DCUPLOAD_PROFIT_LOSS_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = formatFieldValue(uploadData[11], 11);
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_EOP_BALANCE_LCY], IDX_DCUPLOAD_EOP_BALANCE_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_AVG_BALANCE_DR_LCY], IDX_DCUPLOAD_AVG_BALANCE_DR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_AVG_BALANCE_CR_LCY], IDX_DCUPLOAD_AVG_BALANCE_CR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FLOATING_POOL_DR_LCY],
					IDX_DCUPLOAD_FLOATING_POOL_DR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FLOATING_POOL_CR_LCY],
					IDX_DCUPLOAD_FLOATING_POOL_CR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CORRES_POOL_DR_LCY], IDX_DCUPLOAD_CORRES_POOL_DR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_CORRES_POOL_CR_LCY], IDX_DCUPLOAD_CORRES_POOL_CR_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_RESERVE_ASSET_COST_LCY],
					IDX_DCUPLOAD_RESERVE_ASSET_COST_LCY);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_1],
					IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_1);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_2],
					IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_2);
		}
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		if (uploadGroup == DC_UPLOAD_SHORT) {
			tempStr = ",''";
		} else {
			tempStr = formatFieldValue(uploadData[IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_3],
					IDX_DCUPLOAD_FINANCIAL_ATTRIBUTE_3);
		}
		insertStmt = insertStmt + tempStr;

		/* This is the value of the field Source_Flag i.e. X for XL Upload */
		insertStmt = insertStmt + ", 'X'";

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + getDbFunction("SYSDATE") + ", "
				+ getDbFunction("SYSDATE") + ", '" + newPostingsFlag + "', 'N', '" + actionId + "', " + paramLineCtr
				+ ")";

		/*
		 * Find out whether the record already exists in the UPL table, in case if the
		 * same record has been given more than once in the same input file or not
		 */
		if (uploadGroup == DC_UPLOAD) {
			customerId = uploadData[IDX_DCUPLOAD_CUSTOMER_ID];
			officeAccount = uploadData[IDX_DCUPLOAD_OFFICE_ACCOUNT];
			creditLine = uploadData[IDX_DCUPLOAD_CREDIT_LINE];
			originatingOuc = uploadData[IDX_DCUPLOAD_ORIGINATING_OUC];
			if (ValidationUtil.isValid(uploadData[IDX_DCUPLOAD_ACCRUAL_STATUS])) {
				accrualStatus = Integer.parseInt(uploadData[IDX_DCUPLOAD_ACCRUAL_STATUS]);
			}
		}

		String errorMessage = "";
		int countPostings = 0;
		try {
			String query = "Select Calc_Pool, Calc_Pool_Rate " + " From Fin_Adj_Postings_Upl " + " Where Action_Id = '"
					+ actionId + "'" + " And Country = " + country + "" + " And Le_Book = " + leBook + ""
					+ " And Year_Month = " + yearMonth + "" + " And Adj_Source = " + adjSource + ""
					+ " And Sequence_Fm = " + sequenceFm + "" + " And Adj_Reference = " + adjReference + ""
					+ " And Customer_Id = " + customerId + "" + " And Customer_Id_Mgt = " + customerIdMgt + ""
					+ " And Gl_Enrich_Id = " + glEnrichId + "" + " And Contract_Id = " + contractId + ""
					+ " And Office_Account = " + officeAccount + "" + " And Bs_Gl = " + bsGl + "" + " And Pl_Gl = "
					+ plGl + "" + " And Vision_Ouc =" + visionOuc + "" + " And Vision_Sbu = " + visionSbu + ""
					+ " And Account_Officer = " + accountOfficer + "" + " And Cost_Center = " + costCenter + ""
					+ " And Currency  = " + currency + "" + " And Mis_Currency = " + misCurrency + ""
					+ " And Accrual_Status = " + accrualStatus + "" + " And Credit_Line = " + creditLine + ""
					+ " And Source_Id = " + sourceId + "" + " And Record_Type = " + recordType + ""
					+ " And Original_Ouc= " + originalOuc + "";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				calcPool1 = rs.getString("Calc_Pool");
				calcPoolRate1 = rs.getString("Calc_Pool_Rate");
				countPostings++;

			}
		} catch (SQLException e) {
			e.printStackTrace();
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr)
					+ "\t\tFin_Adj_Postings_Upl validation before insert/update failed :" + e.getMessage());
			finalReturnValue = ERRONEOUS_EXIT;
			totalFailureRecords++;
			return (SUCCESSFUL_EXIT);
		}
		if (countPostings != 0) {
			calcPoolRate = uploadData[IDX_DCUPLOAD_RATE];

			calcPool = "";
			calcPool = uploadData[IDX_DCUPLOAD_CALC_POOL];

			if (ValidationUtil.isValid(calcPool1)) {
				indic1 = 0;
			} else {
				indic1 = -1;
			}
			if (ValidationUtil.isValid(calcPoolRate1)) {
				indic2 = 0;
			} else {
				indic2 = -1;
			}
			if ((indic1 == -1 && "Y".equalsIgnoreCase(uploadData[IDX_DCUPLOAD_CALC_POOL])
					&& uploadData[IDX_DCUPLOAD_CALC_POOL].length() != 0)
					|| (indic1 == 0 && uploadData[IDX_DCUPLOAD_CALC_POOL].length() == 0
							&& "N".equalsIgnoreCase(uploadData[IDX_DCUPLOAD_CALC_POOL]))
					|| (indic2 == -1 && uploadData[IDX_DCUPLOAD_RATE].length() != 0
							&& "Y".equalsIgnoreCase(uploadData[IDX_DCUPLOAD_RATE]))
					|| (indic2 != -1 && uploadData[IDX_DCUPLOAD_RATE].length() == 0)
							&& "N".equalsIgnoreCase(uploadData[IDX_DCUPLOAD_RATE])) {
				logWriter("" + appendZeroforCtr(paramLineCtr)
						+ "       \t\tFin_Adj_Postings_Upl Insert Failed - Inconsistent Calc_Pool/Rate specified - I",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			}
			logWriter("" + "Indic1:" + indic1 + ":CalcPool" + uploadData[IDX_DCUPLOAD_CALC_POOL], uploadLogFileName);
			logWriter("" + "calcPool1:" + calcPool1 + ":calcPool" + calcPool.replaceAll("'", ""), uploadLogFileName);
			logWriter("" + "Indic2:" + indic2 + ":CalcPoolRate" + uploadData[IDX_DCUPLOAD_RATE], uploadLogFileName);
			if (indic1 != NULL_VALUE && uploadData[IDX_DCUPLOAD_CALC_POOL].length() != 0) {
				String calcPoolTemp = ValidationUtil.isValid(calcPool) ? calcPool.replaceAll("'", "") : "N";
				if (!calcPool1.equalsIgnoreCase(calcPoolTemp)) {
					logWriter("" + appendZeroforCtr(paramLineCtr)
							+ "       \t\tFin_Adj_Postings_Upl Insert Failed - Inconsistent Calc_Pool/Rate specified - II",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
			}

			if (indic2 != NULL_VALUE && uploadData[IDX_DCUPLOAD_RATE].length() != 0) {
				if (!calcPoolRate1.equalsIgnoreCase(calcPoolRate)) {
					logWriter("" + appendZeroforCtr(paramLineCtr)
							+ "       \t\tFin_Adj_Postings_Upl Insert Failed - Inconsistent Calc_Pool/Rate specified - III",
							uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
					return (SUCCESSFUL_EXIT);
				}
			}
			/*
			 * Update the existing record, augmenting the balances in the UPL table
			 */
			sqlStatement = "Update Fin_Adj_Postings_Upl Set ";
			commaFlag = 'N';

			if (uploadData[IDX_DCUPLOAD_PROFIT_LOSS_LCY].length() != 0) {
				sqlStatement = sqlStatement + " Profit_Loss_Lcy = Profit_Loss_Lcy + "
						+ uploadData[IDX_DCUPLOAD_PROFIT_LOSS_LCY] + "";
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_EOP_BALANCE_LCY].length() != 0) {
				tempStr = " Eop_Balance_Lcy = Eop_Balance_Lcy + " + uploadData[IDX_DCUPLOAD_EOP_BALANCE_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}
			if (uploadData[IDX_DCUPLOAD_AVG_BALANCE_DR_LCY].length() != 0) {
				tempStr = " Avg_Balance_Dr_Lcy = Avg_Balance_Dr_Lcy + " + uploadData[IDX_DCUPLOAD_AVG_BALANCE_DR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_AVG_BALANCE_CR_LCY].length() != 0) {
				tempStr = " Avg_Balance_Cr_Lcy = Avg_Balance_Cr_Lcy + " + uploadData[IDX_DCUPLOAD_AVG_BALANCE_CR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_FLOATING_POOL_DR_LCY].length() != 0) {
				tempStr = " Floating_Pool_Dr_Lcy = Floating_Pool_Dr_Lcy + "
						+ uploadData[IDX_DCUPLOAD_FLOATING_POOL_DR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_FLOATING_POOL_CR_LCY].length() != 0) {
				tempStr = " Floating_Pool_Cr_Lcy = Floating_Pool_Cr_Lcy + "
						+ uploadData[IDX_DCUPLOAD_FLOATING_POOL_CR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_CORRES_POOL_DR_LCY].length() != 0) {
				tempStr = " Corres_Pool_Dr_Lcy = Corres_Pool_Dr_Lcy + " + uploadData[IDX_DCUPLOAD_CORRES_POOL_DR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_CORRES_POOL_CR_LCY].length() != 0) {
				tempStr = " Corres_Pool_Cr_Lcy = Corres_Pool_Cr_Lcy + " + uploadData[IDX_DCUPLOAD_CORRES_POOL_CR_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}

			if (uploadData[IDX_DCUPLOAD_RESERVE_ASSET_COST_LCY].length() != 0) {
				tempStr = " Reserve_Asset_Cost_Lcy = Reserve_Asset_Cost_Lcy + "
						+ uploadData[IDX_DCUPLOAD_RESERVE_ASSET_COST_LCY];

				if (commaFlag == 'Y')
					sqlStatement = sqlStatement + ", ";

				sqlStatement = sqlStatement + tempStr;
				commaFlag = 'Y';
			}
			sqlStatement = sqlStatement + " Where Country      = " + country + "" + " And Le_Book           = " + leBook
					+ " " + " And Year_Month        =  " + yearMonth + " " + " And Adj_Source        = " + adjSource
					+ " " + " And Sequence_Fm       = " + sequenceFm + " " + " And Adj_Reference     = " + adjReference
					+ " " + " And Customer_Id       = " + customerId + " " + " And Customer_Id_Mgt   = " + customerIdMgt
					+ "" + " And Gl_Enrich_Id      = " + glEnrichId + "" + " And Contract_Id       = " + contractId + ""
					+ " And Office_Account    = " + officeAccount + "" + " And Bs_Gl             = " + bsGl + ""
					+ " And Pl_Gl             = " + plGl + "" + " And Vision_Ouc        = " + visionOuc + ""
					+ " And Vision_Sbu        = " + visionSbu + "" + " And Account_Officer   = " + accountOfficer + ""
					+ " And Cost_Center       = " + costCenter + "" + " And Currency          = " + currency + ""
					+ " And Mis_Currency      = " + misCurrency + "" + " And Accrual_Status    =  " + accrualStatus + ""
					+ " And Credit_Line       = " + creditLine + "" + " And Source_Id         =  " + sourceId + ""
					+ " And Record_Type       =  " + recordType + "" + " And Original_Ouc      = " + originatingOuc
					+ "";
			try {
				int cnt = stmt.executeUpdate(sqlStatement);
				if (cnt != 0) {
					logWriter("       \t\tFin_Adj_Postings_Upl Update succeeded", uploadLogFileName);
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				logWriter("[ " + sqlStatement + " ]", uploadLogFileName);
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "       \t\tFin_Adj_Postings_Upl Update Failed :"
						+ e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
		} else {
			/* Attempt to insert into the Fin_Adj_Postings_UPL Table, now */
			try {
				int cnt = stmt.executeUpdate(insertStmt);
				if (cnt > 0) {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Adj_Postings_Upl  Insert succeeded",
							uploadLogFileName);
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				logWriter("[ " + insertStmt + " ]", uploadLogFileName);
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "       \t\tFin_Adj_Postings_Upl Insert Failed :"
						+ e.getMessage());
				logWriter(insertStmt, uploadLogFileName);
//				System.out.println(insertStmt);
				// finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
		}
		return (SUCCESSFUL_EXIT);
	}

	private int loadVirtualKeyFields() {
		try {
			if (uploadGroup == DC_UPLOAD_SHORT) {

				yearMonth = Integer.parseInt(uploadData[IDX_DCUPLOAD_YEAR_MONTH]);

				country = "";
				country = uploadData[IDX_DCUPLOAD_COUNTRY];

				leBook = "";
				leBook = uploadData[IDX_DCUPLOAD_LE_BOOK];

				contractId = "";
				contractId = uploadData[6];

				officeAccount = "";
				officeAccount = uploadData[7];

				currency = "";
				currency = uploadData[8]; // Currency

				misCurrency = "";
				misCurrency = uploadData[9]; // MIS Currency
			} else {
				country = "";
				country = uploadData[IDX_DCUPLOAD_COUNTRY];

				leBook = "";
				leBook = uploadData[IDX_DCUPLOAD_LE_BOOK];

				yearMonth = Integer.parseInt(uploadData[IDX_DCUPLOAD_YEAR_MONTH]);

				glEnrichId = "";
				glEnrichId = uploadData[IDX_DCUPLOAD_GL_ENRICH_ID];

				bsGl = "";
				bsGl = uploadData[IDX_DCUPLOAD_BS_GL];

				plGl = "";
				plGl = uploadData[IDX_DCUPLOAD_PL_GL];

				currency = "";
				currency = uploadData[IDX_DCUPLOAD_CURRENCY];

				misCurrency = "";
				misCurrency = uploadData[IDX_DCUPLOAD_MIS_CURRENCY];

				contractId = "";
				contractId = uploadData[IDX_DCUPLOAD_CONTRACT_ID];

				officeAccount = "";
				officeAccount = uploadData[IDX_DCUPLOAD_OFFICE_ACCOUNT];

				officeAccount = "";
				officeAccount = uploadData[IDX_DCUPLOAD_CUSTOMER_ID];

				visionOuc = "";
				visionOuc = uploadData[IDX_DCUPLOAD_VISION_OUC];

				costCenter = "";
				costCenter = uploadData[IDX_DCUPLOAD_COST_CENTER];

				visionSbu = "";
				visionSbu = uploadData[IDX_DCUPLOAD_VISION_SBU];

				accountOfficer = "";
				accountOfficer = uploadData[IDX_DCUPLOAD_ACCOUNT_OFFICER];

				sourceId = Integer.parseInt(uploadData[IDX_DCUPLOAD_SOURCE_ID]);

				/* Vision_Ouc value will be set for Original_Ouc also */
				originalOuc = "";
				originalOuc = uploadData[IDX_DCUPLOAD_VISION_OUC];

				customerIdMgt = "";
				customerIdMgt = uploadData[IDX_DCUPLOAD_CUSTOMER_ID_MGT];
			}

			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("Error when loading keys [loadVirtualKeyFields()] :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private double getNewAdjId(int paramLineCtr) {
		newAdjId = 0;
		double paramAdjId = 0;
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("Select Last_Adj_Id From Fin_Adj_Stats " + " Where Country = '" + country + "'"
					+ " And Le_Book = '" + leBook + "'" + " And Year_Month = " + yearMonth + "");
			while (rs.next())
				newAdjId = rs.getInt("Last_Adj_Id");
		} catch (SQLException e) {
			if (e.getErrorCode() != SQL_NO_ERRORS && e.getErrorCode() != SQL_NOT_FOUND) {
				logWriter("Error trying to fetch Last_Adj_Id From Fin_Adj_Stats table :" + e.getMessage(),
						uploadLogFileName);
				sqlLogWriter("Values used to hit were Country [" + country + "], Le_Book [" + leBook
						+ "] & Year_Month [" + yearMonth + "]\n");
				return (ERRONEOUS_EXIT);
			} else if (e.getErrorCode() == SQL_NOT_FOUND) {
//				System.out.println("Tryin to insert a new value !!\n");
				/*
				 * Since the Combination for the Country + Le_Book + Year_Month are not found in
				 * the Fin_Adj_Stats table, now insert a new entry into it
				 */
				try {
					stmt.executeUpdate(
							"Insert Into Fin_Adj_Stats(Country, Le_Book, Year_Month, Last_Sequence_Fm, Last_Adj_Id) "
									+ " Values ('" + country + "'," + leBook + "," + yearMonth + ", 0, 1)");
				} catch (SQLException ex) {
					sqlLogWriter(
							"Error trying to Insert into Fin_Adj_Stats table, during the process of obtaining a new Adj_Id :"
									+ e.getMessage());
					logWriter("Values used for the insert were....", uploadLogFileName);
					sqlLogWriter("Country [" + country + "], Le_Book [" + leBook + "], Year_Month [" + yearMonth
							+ "], Last_Adj_Id [" + newAdjId + "]\n");
					return (ERRONEOUS_EXIT);
				}

				paramAdjId = 1;
				return paramAdjId;
			}
		}
		newAdjId++;
		try {
			stmt.executeUpdate(" Update Fin_Adj_Stats Set Last_Adj_Id = " + newAdjId + " " + " Where Country = '"
					+ country + "'" + " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth + "");
		} catch (SQLException e) {
			sqlLogWriter("Error trying to update Fin_Adj_Stats table, during the process of obtaining a new Adj_Id :"
					+ e.getMessage());
			logWriter("Values used for the Update were....", uploadLogFileName);
			sqlLogWriter("Country [" + country + "], Le_Book [" + leBook + "], Year_Month [" + yearMonth
					+ "], Last_Adj_Id [" + newAdjId + "]\n");
			return (ERRONEOUS_EXIT);
		}
		paramAdjId = newAdjId;
		return paramAdjId;
	}

	private void printDcUploadErrorReport() {
		String prevTableType = "";
		int localLineNumber = 0;
		char firstTimeFlag = 'Y';
		int recCount = 0;
		String tempStr = "";
		try {
			rs = stmt.executeQuery("Select Table_Flag, Line_Number, Country, Le_Book, "
					+ " Year_Month, Adj_Source, Adj_Reference, Adj_Id, Sequence_Fa, "
					+ " Error_Desc, Error_Type, Severity " + " From Fin_Adj_Validation_Errors " + " Where Action_Id = '"
					+ actionId + "' " + " Order By Table_Flag Desc, Line_Number, Error_Type ");
			recCount = rs.getRow();
			while (rs.next()) {
				tableTypelst.add(rs.getString("Table_Flag"));
				lineNumberlst.add(rs.getInt("Line_Number"));
				countrylst.add(rs.getString("Country"));
				leBooklst.add(rs.getInt("Le_Book"));
				yearMonthlst.add(rs.getInt("Year_Month"));
				adjSourcelst.add(rs.getString("Adj_Source"));
				adjReferencelst.add(rs.getString("Adj_Reference"));
				adjIdlst.add(rs.getInt("Adj_Id"));
				sequenceFalst.add(rs.getInt("Sequence_Fa"));
				errorDescriptionlst.add(rs.getString("Error_Desc"));
				errorTypelst.add(rs.getString("Error_Type"));
				severitylst.add(rs.getString("Severity"));

			}
		} catch (SQLException e) {
			sqlLogWriter(
					"Error preparing to Select from the Fin_Adj_Validation_Errors table for displaying a summary of errors encountered during the upload process :"
							+ e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return;
		}
		prevTableType = "X";

		for (int Ctr = 0; Ctr < recCount; Ctr++) {
			lineNumber = (Integer) lineNumberlst.get(Ctr);
			tableType = (String) tableTypelst.get(Ctr);
			errorDescription = (String) errorDescriptionlst.get(Ctr);
			errorType = (String) errorTypelst.get(Ctr);
			severity = (String) severitylst.get(Ctr);
			adjSource = (String) adjSourcelst.get(Ctr);
			adjReference = (String) adjReferencelst.get(Ctr);
			country = (String) countrylst.get(Ctr);
			leBook = (String) leBooklst.get(Ctr);
			yearMonth = (Integer) yearMonthlst.get(Ctr);
			adjId = (Integer) adjIdlst.get(Ctr);
			sequenceFa = (Integer) sequenceFalst.get(Ctr);

			/*
			 * printf("\n\n Point 1 tableType [%s]... ",tableType);
			 * fflush(stdout);fflush(stdin);getchar();
			 */

			if (prevTableType != tableType) {
				logWriter(" ", uploadLogFileName);

				if (tableType.equalsIgnoreCase("H"))
					logWriter("FIN_ADJ_HEADERS Errors", uploadLogFileName);
				else if (tableType.equalsIgnoreCase("D"))
					logWriter("FIN_ADJ_DETAILS Errors", uploadLogFileName);
				else
					logWriter("FIN_ADJ_POSTINGS Errors", uploadLogFileName);

				logWriter("----------------------------------------------------------------------------------------",
						uploadLogFileName);

				if (tableType.equalsIgnoreCase("P")) {
					logWriter(
							"Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Adj_Id Sequence_Fa Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ------ ----------- ----------   --------------------------",
							uploadLogFileName);

					// "" + lineNumber + "\t" + country + " " + leBook + " " +
					// yearMonth + " " + adjSource + " " + adjReference + " " +
					// adjId + " " + sequenceFa + " " + (errorType[0] == 'T' ?
					// "Trigger" : "Validation") + " " + errorDescription;

				} else if (tableType.equalsIgnoreCase("H")) {
					logWriter(
							"Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Adj_Id Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ------ ----------   --------------------------",
							uploadLogFileName);
				} else {
					logWriter("Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ----------   --------------------------",
							uploadLogFileName);
				}

				firstTimeFlag = 'Y';

				prevTableType = tableType;
			}

			if (localLineNumber != lineNumber) {
				if (firstTimeFlag != 'Y')
					logWriter(" ", uploadLogFileName);

				firstTimeFlag = 'N';
				localLineNumber = lineNumber;

				if (tableType.equalsIgnoreCase("P")) {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjId + " " + sequenceFa + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else if (tableType.equalsIgnoreCase("H")) {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				}
			} else {
				if (tableType.equalsIgnoreCase("P")) {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + sequenceFa + " "
							+ (errorType == "T" ? "Trigger" : "Validation") + " " + errorDescription;
				} else if (tableType.equalsIgnoreCase("D")) {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				}
			}

			logWriter(tempStr, uploadLogFileName);
		}
	}

	private int populateMaSequences() {
		char abortFlag = 'N';
		char batchSequenceFoundFlag;
		try {
			stmt.executeUpdate(" Update Mgt_Details_Upl T1 "
					+ " Set Ma_Sequence = (Select Ma_Sequence From Mgt_Details T2" + " Where T1.Year = T2.Year "
					+ " And T1.Mgt_Reference = T2.Mgt_Reference" + " And T1.Vision_Ouc = T2.Vision_Ouc"
					+ " And T1.Mrl_Line = T2.Mrl_Line" + " And T1.Country = T2.Country" + " And T1.Le_Book = T2.Le_Book"
					+ " And T1.Contract_id = T2.Contract_id" + " And T1.Customer_Id = T2.Customer_Id"
					+ " And T1.Vision_SBU = T2.Vision_SBU" + " And T1.Account_Officer = T2.Account_Officer"
					+ " And T1.Product = T2.Product" + " And T1.Mis_Currency = T2.Mis_Currency "
					+ " And T1.Action_Id = '" + actionId + "'" + " And T1.Ma_Sequence Is Null)"
					+ " Where T1.Action_Id = '" + actionId + "'" + " And T1.Ma_Sequence Is Null"
					+ " AND EXISTS (Select 'X' From Mgt_Details T2" + " Where T1.Year = T2.Year "
					+ " And T1.Mgt_Reference = T2.Mgt_Reference" + " And T1.Vision_Ouc = T2.Vision_Ouc"
					+ " And T1.Mrl_Line = T2.Mrl_Line" + " And T1.Country = T2.Country" + " And T1.Le_Book = T2.Le_Book"
					+ " And T1.Contract_id = T2.Contract_id" + " And T1.Customer_Id = T2.Customer_Id"
					+ " And T1.Vision_SBU = T2.Vision_SBU" + " And T1.Account_Officer = T2.Account_Officer"
					+ " And T1.Product = T2.Product" + " And T1.Mis_Currency = T2.Mis_Currency "
					+ " And T1.Action_Id = '" + actionId + "'" + " And T1.Ma_Sequence Is Null)");
		} catch (SQLException e) {
			sqlLogWriter("Error updating Mgt_Details_Upl table with matching sequences :" + e.getMessage());
			finalReturnValue = STATUS_FAILURE;
			return (ERRONEOUS_EXIT);
		}
		int countTmp = 0;
		yearlst = new ArrayList();
		mgtReferencelst = new ArrayList();
		visionOuclst = new ArrayList();
		countrylst = new ArrayList();
		leBooklst = new ArrayList();
		customerIdlst = new ArrayList();
		accountOfficerlst = new ArrayList();
		visionSbulst = new ArrayList();
		misCurrencylst = new ArrayList();
		try {
			rs = stmt.executeQuery("Select Year, Mgt_Reference, Vision_OUC, MRL_Line, Country, Le_Book, "
					+ " Customer_ID, Product, Account_Officer, Vision_Sbu, Mis_Currency "
					+ " From  MGT_DETAILS_UPL Where " + " Action_Id = '" + actionId + "' " + " And Ma_Sequence Is Null "
					+ " Order By Line_Number, Year, Mgt_Reference " + " For Update Of Ma_Sequence ");
			while (rs.next()) {
				yearlst.add(rs.getInt("Year"));
				mgtReferencelst.add(rs.getString("Mgt_Reference"));
				visionOuclst.add(rs.getString("Vision_OUC"));
				mrlLinelst.add(rs.getString("MRL_Line"));
				countrylst.add(rs.getString("Country"));
				leBooklst.add(rs.getInt("Le_Book"));
				customerIdlst.add(rs.getString("Customer_ID"));
				productlst.add(rs.getString("Product"));
				accountOfficerlst.add(rs.getString("Account_Officer"));
				visionSbulst.add(rs.getString("Vision_Sbu"));
				misCurrencylst.add(rs.getString("Mis_Currency"));
				countTmp++;
			}
		} catch (SQLException e) {
			sqlLogWriter(
					"Error preparing to select from MGT_Details_UPL for Updation of Ma_Sequences :" + e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
		for (int Ctr1 = 0; Ctr1 < countTmp; Ctr1++) {
			year = (Integer) yearlst.get(Ctr1);
			mgtReference = (String) mgtReferencelst.get(Ctr1);
			visionOuc = (String) visionOuclst.get(Ctr1);
			mrlLine = (String) mrlLinelst.get(Ctr1);
			country = (String) countrylst.get(Ctr1);
			leBook = (String) leBooklst.get(Ctr1);
			customerId = (String) customerIdlst.get(Ctr1);
			product = (String) productlst.get(Ctr1);
			accountOfficer = (String) accountOfficerlst.get(Ctr1);
			visionSbu = (String) visionSbulst.get(Ctr1);
			misCurrency = (String) misCurrencylst.get(Ctr1);

			if (year != prevYear || mgtReference.equals(prevMgtReference) != true) {
				prevMgtReference = "";
				prevMgtReference = mgtReference;
				prevYear = year;

				newMaSequence = 0;

				try {
					rs = stmt.executeQuery("Select Count(1), Nvl(Max(Nvl(Ma_Sequence, 0)), 0) From Mgt_Details "
							+ " Where Year = " + year + " And Mgt_Reference ='" + mgtReference + "'"
							+ " And Vision_OUC = '" + visionOuc + "' And Country = '" + country + "'"
							+ " And Le_Book = " + leBook + " And Customer_Id = '" + customerId + "'");
					while (rs.next()) {
						recsCount = rs.getInt(1);
						newMaSequence = rs.getInt(2);
					}
					newMaSequence++;
				} catch (SQLException e) {
					sqlLogWriter("Error trying to fetch Ma_Sequence from Mgt_Details Table :" + e.getMessage());
					abortFlag = 'Y';
					break;
				}
				if (recsCount == 0) {
					batchSequenceFoundFlag = 'N';
					try {
						rs = stmt.executeQuery("Select Batch_Sequence From MGT_HEADERS_UPL Where year = " + year
								+ " And Mgt_Reference = '" + mgtReference + "' And Action_Id = '" + actionId + "'");
						while (rs.next())
							batchSequence = rs.getInt(1);

						batchSequenceFoundFlag = 'Y';
					} catch (SQLException e) {
						sqlLogWriter(
								"Error trying to fetch Batch_Sequence from Mgt_Headers_UPL table for Ma_Sequence computation :"
										+ e.getMessage());
						abortFlag = 'Y';
						break;
					}
					if (batchSequenceFoundFlag == 'N')
						batchSequence = 1;

					try {
						rs = stmt.executeQuery(
								"Select NVL(MAX(NVL(Ma_Sequence,0)),0) " + " From MGT_DETAILS Where Year = " + year
										+ " And Mgt_Reference = '" + mgtReference + "'");
						while (rs.next())
							MaxMaSeq = rs.getInt(1);

						rs = stmt.executeQuery("Select (floor(" + MaxMaSeq + " / " + batchSequence + ")*"
								+ batchSequence + " + " + batchSequence + ") From Dual");

						while (rs.next())
							newMaSequence = rs.getInt(1);
					} catch (SQLException e) {
						sqlLogWriter(
								"Error trying to fetch Max Ma_Sequence from Mgt_Details table for Ma_Sequence computation :"
										+ e.getMessage());
						abortFlag = 'Y';
						break;
					}
					if (newMaSequence == 0) {
						logWriter("The Ma_Sequence could not be generated internally !!", uploadLogFileName);
						logWriter("A new Ma_Sequence could not be generated for Year [" + year + "], "
								+ "Mgt_Reference [" + mgtReference + "], Vision_Ouc [" + visionOuc + "], Mrl_Line ["
								+ mrlLine + "], Product [" + product + "], " + "Country [" + country + "], Le_Book ["
								+ leBook + "], Customer_Id [" + customerId + "] !!!!!!", uploadLogFileName);
						abortFlag = 'Y';
						break;
					}
				}
			}
			try {
				stmt.executeUpdate("Update Mgt_Details_Upl Set Ma_Sequence = " + newMaSequence + "");
			} catch (SQLException e) {
				sqlLogWriter("Error updating generated Ma_Sequence to Mgt_Details_UPL table :" + e.getMessage());
				abortFlag = 'Y';
				break;
			}
			newMaSequence++;
		}
		if (abortFlag == 'Y')
			return (ERRONEOUS_EXIT);

		try {
			stmt.executeUpdate("Update Mgt_Balances_Upl T1 Set Ma_Sequence = (Select Ma_Sequence "
					+ " From Mgt_Details_Upl T2 Where T1.Internal_Line_Number = T2.Line_Number"
					+ " And T1.Ma_Sequence Is Null And T2.Action_Id = '" + actionId + "'" + " And T1.Action_Id = '"
					+ actionId + "') Where T1.Action_Id = '" + actionId + "'" + " And T1.Ma_Sequence Is Null");
		} catch (SQLException e) {
			sqlLogWriter("Error assigning Ma_Sequences to Mgt_Balances_Upl :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		return (SUCCESSFUL_EXIT);
	}

	private void cleanupDcUPLTableEntries() {
		try {
			stmt.executeUpdate("Delete From Fin_Adj_Headers_UPL Where Action_Id = '" + actionId + "'");
			stmt.executeUpdate("Delete From Fin_Adj_Details_UPL Where Action_Id = '" + actionId + "'");
			stmt.executeUpdate("Delete From Fin_Adj_Postings_UPL Where Action_Id = '" + actionId + "'");
			con.commit();
		} catch (SQLException e) {
//			System.out.println("cleanupDcUPLTableEntries :" + e.getMessage());
			e.printStackTrace();
		}
	}

	private int lockTableForUpload() {
		try {
			stmt.executeUpdate(
					"Update Vision_Locking Set Lock_Status = 'Y' Where Table_Name = '" + uploadTableName + "'");
			con.commit();
			lockFlag = 'Y';
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeSQLErrToMainLogFile("Error trying to update Vision_Locking table for locking [" + uploadTableName
					+ "]:" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int unlockTableAfterUpload() {
		String tempStr = "";
		try {
			stmt.executeUpdate(
					"Update Vision_Locking Set Lock_Status = 'N' Where Table_Name = '" + uploadTableName + "'");
			con.commit();
			lockFlag = 'N';
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			tempStr = "Error trying to update Vision_Locking table for Unlocking [" + uploadTableName + "]!!";
			sqlLogWriter("Error trying to update Vision_Locking table for Unlocking [" + uploadTableName + "]:"
					+ e.getMessage());
			writeSQLErrToMainLogFile(tempStr);
			return (ERRONEOUS_EXIT);
		}
	}

	private int lockTableGroupForUpload() {
		try {
			stmt.executeQuery("Update Vision_Locking Set Lock_Status = 'Y' "
					+ " Where Table_Name In (Select T2.Table_Name From " + " Vision_Tables T1, Vision_Locking T2 "
					+ " Where T1.Upload_Group = '" + uploadGroup + "'" + " And T1.Table_Name = T2.Table_Name)");
			con.commit();
			lockFlag = 'Y';
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeSQLErrToMainLogFile(
					"Error trying to lock tables for the upload group [" + uploadGroup + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int unlockTableGroupAfterUpload() {
		try {
			String query = "Update Vision_Locking Set Lock_Status = 'N' Where Table_Name In (Select T2.Table_Name From Vision_Tables T1, Vision_Locking T2 "
					+ " Where T1.Upload_Group = '" + uploadGroup + "' And T1.Table_Name = T2.Table_Name)";
			stmt.executeUpdate(query);
			lockFlag = 'N';
			con.commit();
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeSQLErrToMainLogFile(
					"Error trying to lock tables for the upload group [" + uploadGroup + "] :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int lockMgtTablesForUpload() {
		try {
			uploadTableName = "MGT_HEADERS";

			retVal = lockTableForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for MGT XL Upload. Cannot proceed now with upload \n");
				return (ERRONEOUS_EXIT);
			}

			lockFlag = 'Y';

			con.commit();

			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
//			System.out.println("lockMgtTablesForUpload():" + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}

	}

	private int unlockMgtTablesAfterUpload() {
		try {
			String tempStr;
			uploadTableName = "MGT_HEADERS";
			retVal = unlockTableAfterUpload();
			if (retVal == ERRONEOUS_EXIT) {
				tempStr = "The table [" + uploadTableName
						+ "] could not be unlocked for MGT XL Upload. Cannot proceed now with upload !!!!!!";
				writeToMainLogFile(tempStr);
				return ERRONEOUS_EXIT;
			}
			lockFlag = 'N';
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeToMainLogFile("unlockMgtTablesAfterUpload :" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private void displayAbortErrorsForDC(String sqlStatement, String errorMsg) {
		char firstTimeFlag = 'Y';
		try {
			rs = stmt.executeQuery(sqlStatement);
			while (rs.next())
				lineNumberlst.add(rs.getInt(1));
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to select from tables, for abort conditions validation :" + e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return;
		}
		firstTimeFlag = 'Y';
		uploadErrorsFirstTimeFlag = 'Y';
		for (int Ctr = 0; Ctr < lineNumberlst.size(); Ctr++) {
			lineNumber = (Integer) lineNumberlst.get(Ctr);
			printUploadErrors("" + lineNumber + "\t\t" + errorMsg);
		}
	}

	private int fcUpload() {
		try {
			originalTableName = "Forecast_Headers & Forecast_Balances";

			/* Check if upload is allowed for the tables involved */
			retVal = checkIfFCUploadIsAllowed();

			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/* Check if the table has been locked by some build process */
			retVal = checkForFCTableLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return (ERRONEOUS_EXIT);
			}

			/* Lock the table to proceed with Upload */
			retVal = lockFCTablesForUpload();

			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile(
						"The tables FORECAST_HEADERS/FORECAST_BALANCES could not be locked for XL Upload. Cannot proceed now with upload !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}

			/* Set the status to In-Progress mode for the upload request */
			retVal = updateUploadStatus(STATUS_IN_PROGRESS);

			if (retVal == ERRONEOUS_EXIT)
				return (ABORT_EXECUTION);

			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile("Uploading to the table [" + uploadTableName + "]");

			uploadTableName = "";
			uploadTableName = "FORECAST_HEADERS";
			uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + makerId + "_" + GetCurrentDateTime()
					+ ".err";
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";
			writeToMainLogFile("The log file name used is " + uploadLogFileName);
//			System.out.println("uploadLogFileName:" + uploadLogFileName);
			/*
			 * Open the main log file name and make it ready for writing log information
			 */
			try {
				logfile = new FileWriter(uploadLogFileName); // file Open in
																// Write mode
				logfile.close();
			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName
						+ "] for logging process steps !!\n  System Msg [" + e.getMessage() + "]\n");
				writeToMainLogFile("Aborting further processing of this request - FYI");
				return (ERRONEOUS_EXIT);
			}
			String tempStr = "";
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";
			uploadFileName = uploadFile;
			try {
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI");
				logWriter("Aborting further processing of this request - FYI", uploadLogFileName);
				retVal = updateUploadStatus(STATUS_FAILURE);
				if (retVal == ERRONEOUS_EXIT) {
					return (ABORT_EXECUTION);
				}
				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed :\n" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);

			logWriter("Tables being uploaded : FORECAST_HEADERS & FORECAST_BALANCES \n", uploadLogFileName);

			retVal = checkForExistenceOfFcTbls();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			logWriter("FC Table Existence Check Completed ", uploadLogFileName);

			retVal = loadTxtFileColumnNames(uploadFile);
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			logWriter("File Column Names are loaded. ", uploadLogFileName);

			retVal = validateFCTblCols();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			logWriter("FC Table Column Validation Completed ", uploadLogFileName);

			retVal = chkColCntForEachRow();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			logWriter("Column Count Check on Each Row Pass thru", uploadLogFileName);

			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);

			TAB_FLAG = 'Y';

			retVal = uploadFCFileData();

			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			TAB_FLAG = 'N';

			if (finalReturnValue == ERRONEOUS_EXIT) {
				retVal = updateUploadStatus(STATUS_FAILURE);

				con.rollback();
			} else {
				retVal = updateUploadStatus(STATUS_SUCCESS);

				con.commit();
			}
			printSummaryReport(finalReturnValue);
			return (finalReturnValue);
		} catch (Exception e) {
//			System.out.println("fcUpload :" + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int unlockFCTablesAfterUpload() {
		String tempStr;
		try {
			stmt.executeUpdate(
					"Update Vision_Locking Set Lock_Status = 'N' Where Table_Name In ('FORECAST_HEADERS', 'FORECAST_BALANCES')");
			lockFlag = 'N';
			con.commit();
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeSQLErrToMainLogFile(
					"Error trying to unlock FC tables (Forecast_Headers & Forecast_Balances for XL Upload [%s]:"
							+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private void cleanupFcUPLTableEntries() {
		String tempStr;
		try {
			stmt.executeUpdate("Delete From Forecast_Headers_Upl Where Action_Id = '" + actionId + "'");
			con.commit();
		} catch (Exception e) {
			sqlLogWriter("Error deleting temporary records from Forecast_Headers_Upl table :" + e.getMessage());
		}
		try {
			stmt.executeUpdate("Delete From Forecast_Balances_Upl Where Action_Id = '" + actionId + "'");
		} catch (Exception e) {
			sqlLogWriter("Error deleting temporary records from Forecast_Balances_Upl table :" + e.getMessage());
		}
	}

	private int checkIfFCUploadIsAllowed() {
		/* Check if upload is allowed for FORECAST_HEADERS table */
		uploadTableName = "";
		uploadTableName = "FORECAST_HEADERS";

		retVal = checkIfUploadIsAllowed();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		/* Check if upload is allowed for FORECAST_BALANCES table */
		uploadTableName = "";
		uploadTableName = "FORECAST_BALANCES";

		retVal = checkIfUploadIsAllowed();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		return (SUCCESSFUL_EXIT);
	}

	private int checkForFCTableLock() {
		/* Check if the table FORECAST_HEADERS has been locked already */
		uploadTableName = "";
		uploadTableName = "FORECAST_HEADERS";

		retVal = checkForTableLock();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		/* Check if the table FORECAST_BALANCES has been locked already */
		uploadTableName = "";
		uploadTableName = "FORECAST_BALANCES";

		retVal = checkForTableLock();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		return (SUCCESSFUL_EXIT);
	}

	private int lockFCTablesForUpload() {
		/* Now, lock the table FORECAST_HEADERS before processing starts */
		uploadTableName = "FORECAST_HEADERS";

		retVal = lockTableForUpload();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		/* Now, lock the table FORECAST_BALANCES before processing starts */
		uploadTableName = "FORECAST_BALANCES";

		retVal = lockTableForUpload();
		if (retVal == ERRONEOUS_EXIT)
			return (ERRONEOUS_EXIT);

		return (SUCCESSFUL_EXIT);
	}

	private int checkForExistenceOfFcTbls() {
		try {
			rs = stmt.executeQuery(
					"Select Count(*) From ALL_TABLES Where Table_Name In ('FORECAST_HEADERS', 'FORECAST_BALANCES') AND UPPER(OWNER)=UPPER('"
							+ username + "')");
			while (rs.next())
				recsCount = rs.getInt(1);
			if (recsCount != 2) {
				logWriter(
						"One or more of the tables for Forecast Upload (Forecast_Headers/Forecast_Balances) "
								+ "are not found in the system !!\nPlease create the tables and re-run this upload\n",
						uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
			sqlLogWriter("Error trying to validate presence of upload table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int validateFCTblCols() {
		try {
			int Ctr1 = 0, Ctr2 = 0;
			char foundFlag = 'Y';
			char firstTimeFlag = 'Y';
			char continueFlag = 'N';
			int cntTmp = 0;
			retVal = SUCCESSFUL_EXIT;
			cntTmp = 0;
			columnNamelst = new ArrayList();
			columnSizelst = new ArrayList();
			quotedTypelst = new ArrayList();
			nullable = new ArrayList();
			decimalsChecklst = new ArrayList();
			dataScalelst = new ArrayList();
			dateTypelst = new ArrayList();
			fileColumnsColumnSize = new ArrayList();
			fileColumnsQuotedType = new ArrayList();
			fileColumnsNullable = new ArrayList();
			fileColumnsDecimalsCheck = new ArrayList();
			fileColumnsAfterDecimals = new ArrayList();
			fileColumnsDataType = new ArrayList();
			try {
				sqlStatement = " Select Column_Name, Col_Nullable, Col_Size, Quoted_Type, After_Decimals, "
						+ " Date_Type, Decimals_Check From Xl_Upload_Cols_Repository Where Upload_Genre = 'FCUPLOAD' "
						+ " Order By Col_Sequence";
				rs = stmt.executeQuery(sqlStatement);
				while (rs.next()) {
					columnNamelst.add(rs.getString("Column_Name"));
					nullable.add(rs.getString("Col_Nullable"));
					columnSizelst.add(rs.getString("Col_Size"));
					quotedTypelst.add(rs.getString("Quoted_Type"));
					dataScalelst.add(rs.getString("After_Decimals"));
					dateTypelst.add(rs.getString("Date_Type"));
					decimalsChecklst.add(rs.getString("Decimals_Check"));
					cntTmp++;
				}
				tableColumnList.addAll(columnNamelst);
				tableColumnList.addAll(nullable);
				tableColumnList.addAll(dataPrecision);
				tableColumnList.addAll(quotedTypelst);
				tableColumnList.addAll(dataScalelst);// after Decimals
				tableColumnList.addAll(dateTypelst);
				tableColumnList.addAll(decimalsChecklst);
				totalTableColumns = cntTmp;
			} catch (SQLException e) {
				sqlLogWriter("Error preparing to select from Xl_Upload_Cols_Repository table :" + e.getMessage());
				logWriter("Query was ", uploadLogFileName);
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			if (totalTableColumns == 0) {
				logWriter("The table does not have any columns to upload !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			if (columnNamelst == null || columnNamelst.size() == 0) {
				logWriter("The Column List should be maintained in the table [Xl_Upload_Cols_Repository] !!",
						uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			String missingColumns = "";
			StringJoiner fileInvalidCols = new StringJoiner(",");
			Boolean invalidfileCol = false;
			for (int z = 0; z < fileColumnsNames.size(); z++) {
				if (!columnNamelst.contains(fileColumnsNames.get(z))) {
					fileInvalidCols.add((CharSequence) fileColumnsNames.get(z));
					invalidfileCol = true;
				}
			}
			if (invalidfileCol) {
				logWriter("Invalid Columns present in the uploaded Excel[" + fileInvalidCols.toString() + "]",
						uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			for (int ct = 0; ct < columnNamelst.size(); ct++) {
				if (!fileColumnsNames.contains(columnNamelst.get(ct))) {
					if (ValidationUtil.isValid(missingColumns))
						missingColumns = missingColumns + "," + columnNamelst.get(ct);
					else
						missingColumns = missingColumns + columnNamelst.get(ct);
				}
			}
			if (ValidationUtil.isValid(missingColumns)) {
				logWriter("The Columns [" + missingColumns + "] are missing in the Excel File uploaded !!",
						uploadLogFileName);
				return ERRONEOUS_EXIT;
			}

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				/*
				 * TO DO remove after verification - for debugging purposes only
				 * printf("input file column name is [%s]\n", fileColumns[Ctr1].columnName);
				 */
				if (fileColumnsNames.get(Ctr1).toString().equalsIgnoreCase(columnNamelst.get(Ctr1).toString())) {
					fileColumnsColumnSize.add(Ctr1, columnSizelst.get(Ctr1));
					fileColumnsQuotedType.add(Ctr1, quotedTypelst.get(Ctr1));
					fileColumnsNullable.add(Ctr1, nullable);
					fileColumnsDecimalsCheck.add(Ctr1, decimalsChecklst.get(Ctr1));
					fileColumnsAfterDecimals.add(Ctr1, dataScalelst.get(Ctr1));
					fileColumnsDataType.add(Ctr1, dateTypelst.get(Ctr1));

					foundFlag = 'Y';
				}
				if (foundFlag == 'N') {
					if (firstTimeFlag == 'Y') {
						logWriter(
								"The following columns are either not present in the right order or not in the table !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("[" + fileColumnsNames.get(Ctr1) + "]", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
			}
			if (retVal != SUCCESSFUL_EXIT)
				return (retVal);

			return (retVal);
		} catch (Exception e) {
			logWriter("Failed on Column Validation due to invalid column[" + e.getMessage() + "] ", uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
	}

	private int uploadFCFileData() {
		try {
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			char ch;
			int lineCtr = 1;
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			totalInputFileRecords = 0;

			TAB_FLAG = 'N';

			/* Load the Ouc_Codes table into memory for uploading data */
			retVal = loadOucCodesIntoMemory();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/* Load the Legal_Vehicles table into memory for uploading data */
			retVal = loadLVRatesIntoMemory();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/* Load the Legal_Vehicles table into memory for uploading data */
			retVal = loadPeriodCtrlsIntoMemory();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			logWriter(" ", uploadLogFileName);

			TAB_FLAG = 'Y';

			/* Frame the Action_Id For this upload */
			actionId = "";
			actionId = getActionId();

			retVal = SUCCESSFUL_EXIT;

			adjSource = "";
			adjSource = "'" + FC_ADJ_SOURCE + "'";

			sourceId = FC_SOURCE_ID;
			recordType = FC_RECORD_TYPE;

			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;
			/* Workbook workbook = new XSSFWorkbook(fileReader1); */

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			logWriter("Total Lines on File to Process[" + rowCount + "]", uploadLogFileName);
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String columnName = (String) fileColumnsNames.get(Ctr2);
						String columnSpecificDataType = String
								.valueOf(dateTypelst.get(columnNamelst.indexOf(columnName)));
						String specificData = "";
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} else if (cellType == CellType.STRING) { 
//								if (cellType == Cell.CELL_TYPE_STRING) {
								specificData = cell.getStringCellValue();
							}else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) { 
//							else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(dataType.get(Ctr2))) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf((int) Math.round(cell.getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf((int) Math.round(cell.getNumericCellValue()));
									}
								}
							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase(""))
							lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						else
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
					}
					if (lineFetched == null) {
						break;
					} else {
						wordsArray = lineFetched.split("\n");
						lineFetched = "";
						for (String each : wordsArray) {
							lines.add(each);
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();
			for (Ctr1 = 0; Ctr1 < lines.size(); Ctr1++) {
				uploadData = lines.get(Ctr1).toString().split("\t");
				for (Ctr2 = 0; Ctr2 < uploadData.length; Ctr2++) {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr2).toString()))
						uploadData[Ctr2] = checkAndProcessQuotes(uploadData[Ctr2]);
				}
				lineCtr++;
				retVal = doInsertUpdateOpsForFCUpload(lineCtr);

				if (retVal == ERRONEOUS_EXIT) {
					retVal = ERRONEOUS_EXIT;
					break;
				}
				totalInputFileRecords++;
			}
			/*
			 * If there has been an error during this upload operation, abort any further
			 * operations, now
			 */
			if (finalReturnValue == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/* Commit the changes to the UPL tables */
			con.commit();
			try {
				sqlStatement = "Update Forecast_Balances_Upl T1 "
						+ " Set Balance_Ytd=(NVL(Balance_01,0) + NVL(Balance_02,0) + NVL(Balance_03,0) + NVL(Balance_04,0) + NVL(Balance_05,0) + NVL(Balance_06,0) + NVL(Balance_07,0)+ "
						+ "  NVL(Balance_08,0) + NVL(Balance_09,0) + NVL(Balance_10,0) + NVL(Balance_11,0) + NVL(Balance_12,0)),  "
						+ "  Balance_Full_Year=(NVL(Balance_01,0) + NVL(Balance_02,0) + NVL(Balance_03,0) + NVL(Balance_04,0) + NVL(Balance_05,0) + NVL(Balance_06,0) + NVL(Balance_07,0)+ "
						+ "  NVL(Balance_08,0) + NVL(Balance_09,0) + NVL(Balance_10,0) + NVL(Balance_11,0) + NVL(Balance_12,0)),  "
						+ "  Balance_Lcy_Ytd=(NVL(Balance_Lcy_01,0) + NVL(Balance_Lcy_02,0) + NVL(Balance_Lcy_03,0) + NVL(Balance_Lcy_04,0) + NVL(Balance_Lcy_05,0) + NVL(Balance_Lcy_06,0) + NVL(Balance_Lcy_07,0)+ "
						+ "  NVL(Balance_Lcy_08,0) + NVL(Balance_Lcy_09,0) + NVL(Balance_Lcy_10,0) + NVL(Balance_Lcy_11,0) + NVL(Balance_Lcy_12,0)),  "
						+ "  Balance_Lcy_Full_Year=(NVL(Balance_Lcy_01,0) + NVL(Balance_Lcy_02,0) + NVL(Balance_Lcy_03,0) + NVL(Balance_Lcy_04,0) + NVL(Balance_Lcy_05,0) + NVL(Balance_Lcy_06,0) + NVL(Balance_Lcy_07,0)+ "
						+ "  NVL(Balance_Lcy_08,0) + NVL(Balance_Lcy_09,0) + NVL(Balance_Lcy_10,0) + NVL(Balance_Lcy_11,0) + NVL(Balance_Lcy_12,0)) "
						+ "  Where " + "     T1.Action_Id = '" + actionId + "' " + "  And T1.Bal_Type Not in(1,2) ";
				logWriter("Updating Balance YTD and Full_Year for Bal Type not in (1,2) ", uploadLogFileName);
				stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
				finalReturnValue = ERRONEOUS_EXIT;
				sqlLogWriter("Error trying to update Forecast_Balances_Upl table for Bal_Type not in(1,2) :"
						+ e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			try {
				sqlStatement = " Update Forecast_Balances_Upl  "
						+ " Set Balance_Ytd = Balance_12, Balance_Full_Year = Balance_12,"
						+ " Balance_Lcy_Ytd = Balance_Lcy_12, Balance_Lcy_Full_Year = Balance_Lcy_12  "
						+ "  Where Action_Id =  '" + actionId + "' And Bal_Type =1  ";

				logWriter("Updating Balance YTD and Full_Year for Bal Type in (1) ", uploadLogFileName);

				stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
				finalReturnValue = ERRONEOUS_EXIT;
				sqlLogWriter("Error trying to update Forecast_Balances_Upl  table for Bal_Type = 3 :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			try {
				ArrayList periodConArrayList = (ArrayList) periodControlsArr.get(periodControlsIndex);

				logWriter("noOfDaysSum[" + noOfDaysSum + "]", uploadLogFileName);

				if (noOfDaysSum == 0) {
					noOfDaysSum = (int) periodConArrayList.get(0) + (int) periodConArrayList.get(1)
							+ (int) periodConArrayList.get(2) + (int) periodConArrayList.get(3)
							+ (int) periodConArrayList.get(4) + (int) periodConArrayList.get(5)
							+ (int) periodConArrayList.get(6) + (int) periodConArrayList.get(7)
							+ (int) periodConArrayList.get(8) + (int) periodConArrayList.get(9)
							+ (int) periodConArrayList.get(10) + (int) periodConArrayList.get(11);
				}

				logWriter("noOfDaysSum from Period Controls[" + noOfDaysSum + "]", uploadLogFileName);

				sqlStatement = "Update Forecast_Balances_Upl T1 " + " Set " + " Balance_Ytd = ((Balance_01 * "
						+ periodConArrayList.get(0) + ") " + " + (Balance_02 * " + periodConArrayList.get(1) + ") "
						+ " + (Balance_03 * " + periodConArrayList.get(2) + ") " + " + (Balance_04 * "
						+ periodConArrayList.get(3) + ") " + " + (Balance_05 * " + periodConArrayList.get(4) + ") "
						+ " + (Balance_06 * " + periodConArrayList.get(5) + ") " + " + (Balance_07 * "
						+ periodConArrayList.get(6) + ") " + " + (Balance_08 * " + periodConArrayList.get(7) + ") "
						+ " + (Balance_09 * " + periodConArrayList.get(8) + ") " + " + (Balance_10 * "
						+ periodConArrayList.get(9) + ") " + " + (Balance_11 * " + periodConArrayList.get(10) + ")"
						+ " + (Balance_12 * " + periodConArrayList.get(11) + "))/" + noOfDaysSum + ", " + " "
						+ " Balance_Full_Year =	((Balance_01 *" + periodConArrayList.get(0) + ") " + " + (Balance_02 * "
						+ periodConArrayList.get(1) + ") " + " + (Balance_03 * " + periodConArrayList.get(2) + ") "
						+ " + (Balance_04 * " + periodConArrayList.get(3) + ") " + " + (Balance_05 * "
						+ periodConArrayList.get(4) + ") " + " + (Balance_06 * " + periodConArrayList.get(5) + ")"
						+ " + (Balance_07 * " + periodConArrayList.get(6) + ") " + " + (Balance_08 * "
						+ periodConArrayList.get(7) + ") " + " + (Balance_09 * " + periodConArrayList.get(8) + ") "
						+ " + (Balance_10 * " + periodConArrayList.get(9) + ") " + " + (Balance_11 * "
						+ periodConArrayList.get(10) + ")" + " + (Balance_12 * " + periodConArrayList.get(11) + "))/"
						+ noOfDaysSum + ", " + " Balance_Lcy_Ytd = ((Balance_Lcy_01 * " + periodConArrayList.get(0)
						+ ")" + " + (Balance_Lcy_02 * " + periodConArrayList.get(1) + ")" + " + (Balance_Lcy_03 * "
						+ periodConArrayList.get(2) + ")" + " + (Balance_Lcy_04 * " + periodConArrayList.get(3) + ") "
						+ " + (Balance_Lcy_05 * " + periodConArrayList.get(4) + ") " + " + (Balance_Lcy_06 * "
						+ periodConArrayList.get(5) + ")" + " + (Balance_Lcy_07 * " + periodConArrayList.get(6) + ") "
						+ " + (Balance_Lcy_08 * " + periodConArrayList.get(7) + ") " + " + (Balance_Lcy_09 * "
						+ periodConArrayList.get(8) + ") " + " + (Balance_Lcy_10 * " + periodConArrayList.get(9) + ") "
						+ " + (Balance_Lcy_11 * " + periodConArrayList.get(10) + ") " + " + (Balance_Lcy_12 * "
						+ periodConArrayList.get(11) + "))/" + noOfDaysSum + ", "
						+ " Balance_Lcy_Full_Year = ((Balance_Lcy_01 * " + periodConArrayList.get(0) + ") "
						+ " + (Balance_Lcy_02 * " + periodConArrayList.get(1) + ") " + " + (Balance_Lcy_03 * "
						+ periodConArrayList.get(2) + ") " + " + (Balance_Lcy_04 * " + periodConArrayList.get(3) + ") "
						+ " + (Balance_Lcy_05 * " + periodConArrayList.get(4) + ") " + " + (Balance_Lcy_06 * "
						+ periodConArrayList.get(5) + ")" + " + (Balance_Lcy_07 * " + periodConArrayList.get(6) + ") "
						+ " + (Balance_Lcy_08 * " + periodConArrayList.get(7) + ") " + " + (Balance_Lcy_09 * "
						+ periodConArrayList.get(8) + ")" + " + (Balance_Lcy_10 * " + periodConArrayList.get(9) + ")"
						+ " + (Balance_Lcy_11 * " + periodConArrayList.get(10) + ")" + " + (Balance_Lcy_12 * "
						+ periodConArrayList.get(11) + "))/" + noOfDaysSum + " Where " + " T1.Action_Id =  '" + actionId
						+ "' " + " And T1.Bal_Type =2 ";

				logWriter("Updating Balance YTD and Full_Year for Bal Type in (2) ", uploadLogFileName);
				stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
				finalReturnValue = ERRONEOUS_EXIT;
				sqlLogWriter("Error trying to update Forecast_Balances_Upl table for  Bal_Type = 2:" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			try {
				String query = "Delete From Forecast_Balances T3 Where Exists (Select 'X' "
						+ " From Forecast_Headers_Upl T1, Forecast_Headers T2 Where " + " T1.Action_Id = '" + actionId
						+ "'" + " And T1.Year = T2.Year " + " And T1.Data_Source = T2.Data_Source "
						+ " And T1.Country = T2.Country " + " And T1.Le_Book = T2.Le_Book "
						+ " And T1.Vision_Ouc = T2.Vision_Ouc " + " And T1.Vision_SBU = T2.Vision_SBU "
						+ " And T1.Account_Officer = T2.Account_Officer " + " And T1.Customer_ID = T2.Customer_ID "
						+ " And T1.MRL_Line = T2.MRL_Line " + " And T3.Year = T2.Year "
						+ " And T3.Sequence_Fc = T2.Sequence_Fc " + " And T3.Data_Source = T2.Data_Source" + ")";

				deletedRecsCount = stmt.executeUpdate(query);

				TAB_FLAG = 'N';
				logWriter("\nRecords deleted from Forecast_Balances : [" + deletedRecsCount + "]\n", uploadLogFileName);
			} catch (SQLException e) {
				finalReturnValue = ERRONEOUS_EXIT;
				sqlLogWriter("Error deleting existing records from the Forecast_Balances table :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			try {
				rs = stmt.executeQuery(" Select Nvl(Max(Sequence_Fc+1), 1) From Forecast_Headers ");
				while (rs.next())
					maxSequenceFc = rs.getInt(1);
			} catch (SQLException e) {
				this.logWriter("Error generating Max Sequence" + e.getMessage(), uploadLogFileName);
			}

			try {
				deletedRecsCount = stmt.executeUpdate("Delete From Forecast_Headers T1 Where Exists (Select 'X' "
						+ " From Forecast_Headers_Upl T2 Where " + " T2.Action_Id = '" + actionId + "' "
						+ " And T1.Year = T2.Year " + " And T1.Data_Source = T2.Data_Source "
						+ " And T1.Country = T2.Country " + " And T1.Le_Book = T2.Le_Book "
						+ " And T1.Vision_Ouc = T2.Vision_Ouc" + " And T1.Vision_SBU = T2.Vision_SBU "
						+ " And T1.Account_Officer = T2.Account_Officer " + " And T1.Customer_ID = T2.Customer_ID "
						+ " And T1.MRL_Line = T2.MRL_Line " + ")");

				TAB_FLAG = 'N';
				logWriter("\nRecords deleted from Forecast_Headers : [" + deletedRecsCount + "]\n", uploadLogFileName);
			} catch (SQLException e) {
				finalReturnValue = ERRONEOUS_EXIT;
				sqlLogWriter("Error deleting existing records from the Forecast_Headers table :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			/* Generate new Sequence_Fc for the Forecast_Headers_Upl table */
			try {
				stmt.executeUpdate("Update Forecast_Headers_Upl Set Sequence_Fc = " + maxSequenceFc
						+ " + Line_Number-2 " + " Where Action_Id = '" + actionId + "'");
			} catch (SQLException e) {
				sqlLogWriter("Error updating new Sequence_Fc to Forecast_Headers_Upl table :" + e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			con.commit();
			/* Generate new Sequence_Fc for the Forecast_Balances_Upl table */
			try {
				stmt.executeUpdate("Update Forecast_Balances_Upl Set Sequence_Fc = " + maxSequenceFc
						+ " + Line_Number-2 " + " Where Action_Id = '" + actionId + "'");
			} catch (SQLException e) {
				sqlLogWriter("Error updating new Sequence_Fc to Forecast_Balances_Upl table :" + e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			con.commit();
			try {
				Statement stmt = con.createStatement();
				recsCount = stmt.executeUpdate("Insert Into Forecast_Headers(Year, Sequence_Fc, Data_Source, "
						+ "Country, Le_Book, Customer_Id, Customer_Id_Mgt, Contract_Id, Office_Account, "
						+ "Gl_Enrich_Id, Vision_Ouc, Customer_Ouc, Vision_Sbu_At, Vision_Sbu, Account_Officer, "
						+ "Cost_Center, Currency, Mis_Currency, "
						+ "Mrl_Line, Cognos_Mrl_Line, Product, Result_Source_At, Result_Source, "
						+ "Record_Type, Source_Id, Internal_Status, Date_Last_Modified, Date_Creation,Maker,Verifier,RECORD_INDICATOR,Planning_Status) "
						+ "Select Year, Sequence_Fc, Data_Source, "
						+ "Country, Le_Book, Customer_Id, Customer_Id_Mgt, Contract_Id, Office_Account, "
						+ "Gl_Enrich_Id, Vision_Ouc, Customer_Ouc, Vision_Sbu_At, Vision_Sbu, Account_Officer, "
						+ "Cost_Center, Currency, Mis_Currency, "
						+ "Mrl_Line, Cognos_Mrl_Line, Product, Result_Source_At, Result_Source, "
						+ "Record_Type, Source_Id, Internal_Status, Date_Last_Modified, Date_Creation,9999,9999,0,40 "
						+ "From Forecast_Headers_Upl " + "Where Action_Id = '" + actionId + "' ");
			} catch (SQLException e) {
				sqlLogWriter("Error insert records into Forecast_Headers table :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			logWriter("\nRecords inserted into Forecast_Headers : [" + recsCount + "]", uploadLogFileName);

			try {
				Statement stmt = con.createStatement();
				recsCount = stmt
						.executeUpdate("Insert Into Forecast_Balances(Year, Sequence_Fc, Country,LE_Book,Data_Source, "
								+ " Bal_Type, Balance_01, Balance_02, Balance_03, Balance_04, Balance_05, Balance_06, "
								+ " Balance_07, Balance_08, Balance_09, Balance_10, Balance_11, Balance_12, Balance_Ytd, "
								+ " Balance_Full_Year, Balance_Lcy_01, Balance_Lcy_02, Balance_Lcy_03, Balance_Lcy_04, "
								+ " Balance_Lcy_05, Balance_Lcy_06, Balance_Lcy_07, Balance_Lcy_08, Balance_Lcy_09, "
								+ " Balance_Lcy_10, Balance_Lcy_11, Balance_Lcy_12, Balance_Lcy_Ytd, Balance_Lcy_Full_Year, "
								+ " Date_Last_Modified, Date_Creation) "
								+ " Select Year, Sequence_Fc,Country,LE_Book, Data_Source, "
								+ " Bal_Type, Balance_01, Balance_02, Balance_03, Balance_04, Balance_05, Balance_06, "
								+ " Balance_07, Balance_08, Balance_09, Balance_10, Balance_11, Balance_12, Balance_Ytd, "
								+ " Balance_Full_Year, Balance_Lcy_01, Balance_Lcy_02, Balance_Lcy_03, Balance_Lcy_04, "
								+ " Balance_Lcy_05, Balance_Lcy_06, Balance_Lcy_07, Balance_Lcy_08, Balance_Lcy_09, "
								+ " Balance_Lcy_10, Balance_Lcy_11, Balance_Lcy_12, Balance_Lcy_Ytd, Balance_Lcy_Full_Year, "
								+ " Date_Last_Modified, Date_Creation " + " From Forecast_Balances_Upl "
								+ " Where Action_Id = '" + actionId + "' ");
			} catch (SQLException e) {
				sqlLogWriter("Error insert records into Forecast_Balances table :" + e.getMessage());
				logWriter(sqlStatement, uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			logWriter("\nRecords inserted into Forecast_Balances : [" + recsCount + "]", uploadLogFileName);
			con.commit();
			TAB_FLAG = 'Y';
			return (retVal);
		} catch (Exception e) {
			System.out.print("FCUploadError :" + e.getMessage());
			finalReturnValue = ERRONEOUS_EXIT;
			return ERRONEOUS_EXIT;
		}
	}

	private int doInsertUpdateOpsForFCUpload(int paramLineCtr) {
		int Ctr1 = 0, Ctr2 = 0;
		int localRetVal = 0;
		char newHeadersFlag = 'N';
		char newDetailsFlag = 'N';
		char newPostingsFlag = 'N';
		char commaFlag = 'N';
		String tempStr = "";
		retVal = SUCCESSFUL_EXIT;

		/*
		 * Frame an insert statement into the corresponding UPL tables of Headers,
		 * Details & Postings Tables and then commence the operations
		 */

		insertStmt = "Insert Into Forecast_Headers_Upl(Action_Id, Line_Number, Year, Sequence_Fc, "
				+ "Data_Source, Forecast_Reference, Fc_Input_Type, Country, "
				+ "Le_Book, Customer_Id, Customer_Id_Mgt, Contract_Id, Office_Account, "
				+ "Gl_Enrich_Id, Vision_Ouc, Customer_Ouc, Vision_Sbu, Account_Officer, Cost_Center, "
				+ "Currency, Mis_Currency, Mrl_Line, Cognos_Mrl_Line, Product, Ouc_Product, "
				+ "Result_Source, Record_Type, Source_Id, Internal_Status, "
				+ "Date_Last_Modified, Date_Creation) Values ('" + actionId + "', " + paramLineCtr + ", "
				+ uploadData[IDX_FCUPLOAD_YEAR].trim();

		/* Sequence_Fc */
		// insertStmt = insertStmt + ", 0";
		insertStmt = insertStmt + ", " + maxSequenceFc;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_DATA_SOURCE], IDX_FCUPLOAD_DATA_SOURCE);
		insertStmt = insertStmt + tempStr;

		/* Forecast_Reference */
		insertStmt = insertStmt + ", Null";

		/* Fc_Input_Type */
		insertStmt = insertStmt + ", Null";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_COUNTRY], IDX_FCUPLOAD_COUNTRY);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_LE_BOOK], IDX_FCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		/* Customer_Id */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_CUSTOMER_ID], IDX_FCUPLOAD_CUSTOMER_ID);
		insertStmt = insertStmt + tempStr;

		/* Customer_Id_Mgt */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_CUSTOMER_ID], IDX_FCUPLOAD_CUSTOMER_ID);
		insertStmt = insertStmt + tempStr;
		/* Contract_Id */
		insertStmt = insertStmt + "," + "'0'";

		/* Office_Account */
		insertStmt = insertStmt + "," + "'0'";

		/* Gl_Enrich_Id */
		insertStmt = insertStmt + "," + "'0'";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_VISION_OUC], IDX_FCUPLOAD_VISION_OUC);
		insertStmt = insertStmt + tempStr;

		/* Customer_Ouc */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_VISION_OUC], IDX_FCUPLOAD_VISION_OUC);
		insertStmt = insertStmt + tempStr;

		/* Vision_Sbu */
		/* strcat(insertStmt, ", 'NA'"); Changed Based on SD Request */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_VISION_SBU], IDX_FCUPLOAD_VISION_SBU);
		insertStmt = insertStmt + tempStr;

		/* Account_Officer */
		/*
		 * insertStmt = insertStmt + ", 'NA'"; Deepak changed as per SD on 14 Aug 2018
		 */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_ACCOUNT_OFFICER], IDX_FCUPLOAD_ACCOUNT_OFFICER);
		insertStmt = insertStmt + tempStr;

		/* Cost_Center */
		insertStmt = insertStmt + ", 'NA'";

		/* Currency */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_CURRENCY], IDX_FCUPLOAD_CURRENCY);
		insertStmt = insertStmt + tempStr;

		/* Mis_Currency */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_CURRENCY], IDX_FCUPLOAD_CURRENCY);
		insertStmt = insertStmt + tempStr;

		/* Mrl_Line */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_MRL_LINE], IDX_FCUPLOAD_MRL_LINE);
		insertStmt = insertStmt + tempStr;

		/* Cognos_Mrl_Line */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_MRL_LINE], IDX_FCUPLOAD_MRL_LINE);
		insertStmt = insertStmt + tempStr;

		/* Product */
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_PRODUCT], IDX_FCUPLOAD_PRODUCT);
		insertStmt = insertStmt + tempStr;

		/*
		 * Ouc_Product - For now, insert a Null value. Later update the temp table to a
		 * proper Ouc_Product from Ouc_Codes table
		 */
		insertStmt = insertStmt + ", Null";

		/*
		 * Result_Source, Record_Type, Source_Id, Internal_Status, Date_Last_Modified &
		 * Date_Creation
		 */
		tempStr = "";
		tempStr = adjSource + "," + recordType + "," + sourceId + "," + INTERNAL_STATUS + "," + getDbFunction("SYSDATE")
				+ ", " + getDbFunction("SYSDATE") + ")";
		insertStmt = insertStmt + "," + tempStr;
		try {
			stmt.executeUpdate(insertStmt);
			retVal = SUCCESSFUL_EXIT;
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tForecast_Headers_Upl   Insert succeeded",
					uploadLogFileName);
		} catch (SQLException e) {
			sqlLogWriter(
					"" + appendZeroforCtr(paramLineCtr) + "\t\tForecast_Headers_Upl  Insert Failed :" + e.getMessage());
			logWriter(insertStmt, uploadLogFileName);
			finalReturnValue = ERRONEOUS_EXIT;
		}
		/* Now attempt to insert into the Forecast_Balances_Upl table */
		insertStmt = "Insert Into Forecast_Balances_Upl(Action_Id, Line_Number, Year, "
				+ "Sequence_Fc,Country,Le_Book, Data_Source, Bal_Type, Balance_01, Balance_02, "
				+ "Balance_03, Balance_04, Balance_05, Balance_06, Balance_07, Balance_08, "
				+ "Balance_09, Balance_10, Balance_11, Balance_12, Balance_Ytd, Balance_Full_Year, "
				+ "Balance_Lcy_01, Balance_Lcy_02, Balance_Lcy_03, Balance_Lcy_04, Balance_Lcy_05, Balance_Lcy_06, "
				+ "Balance_Lcy_07, Balance_Lcy_08, Balance_Lcy_09, Balance_Lcy_10, Balance_Lcy_11, "
				+ "Balance_Lcy_12, Balance_Lcy_Ytd, Balance_Lcy_Full_Year, Internal_Status, "
				+ "Date_Last_Modified, Date_Creation) " + "Values ('" + actionId + "', " + paramLineCtr + ", "
				+ uploadData[IDX_FCUPLOAD_YEAR];

		/* Sequence_Fc */
		// insertStmt = insertStmt + ", 0";
		insertStmt = insertStmt + ", " + maxSequenceFc;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_COUNTRY], IDX_FCUPLOAD_COUNTRY);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_LE_BOOK], IDX_FCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_DATA_SOURCE], IDX_FCUPLOAD_DATA_SOURCE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BAL_TYPE], IDX_FCUPLOAD_BAL_TYPE);
		insertStmt = insertStmt + tempStr;
		// retVal = findLegalVehicleRates();
		// retVal = 1;
		logWriter("retVal : " + retVal, uploadLogFileName);
		retVal = SUCCESSFUL_EXIT;
		if (retVal == NULL_VALUE) {
			finalReturnValue = ERRONEOUS_EXIT;
			totalFailureRecords++;
			logWriter("Total Failure record count increased due to Legal Vehicle returns NULL value",
					uploadLogFileName);
			return (SUCCESSFUL_EXIT);
		}
		double fxRateTmp = 0.0;
		ArrayList fxRateTmpList = (ArrayList) legalVehicleRatesArr.get(retVal);

		/* Budget data will be uploaded in Booking currency in entity level - Kanak */

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_01];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_02];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_03];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_04];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_05];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_06];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_07];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_08];
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_09];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_10];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_11];
		insertStmt = insertStmt + tempStr;

		tempStr = ", " + uploadData[IDX_FCUPLOAD_BALANCE_LCY_12];
		insertStmt = insertStmt + tempStr;

		/* Balance_Ytd, Balance_Full_Year */
		insertStmt = insertStmt + ", 0, 0";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_01], IDX_FCUPLOAD_BALANCE_LCY_01);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_02], IDX_FCUPLOAD_BALANCE_LCY_02);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_03], IDX_FCUPLOAD_BALANCE_LCY_03);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_04], IDX_FCUPLOAD_BALANCE_LCY_04);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_05], IDX_FCUPLOAD_BALANCE_LCY_05);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_06], IDX_FCUPLOAD_BALANCE_LCY_06);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_07], IDX_FCUPLOAD_BALANCE_LCY_07);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_08], IDX_FCUPLOAD_BALANCE_LCY_08);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_09], IDX_FCUPLOAD_BALANCE_LCY_09);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_10], IDX_FCUPLOAD_BALANCE_LCY_10);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_11], IDX_FCUPLOAD_BALANCE_LCY_11);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_FCUPLOAD_BALANCE_LCY_12], IDX_FCUPLOAD_BALANCE_LCY_12);
		insertStmt = insertStmt + tempStr;

		/*
		 * Balance_Lcy_Ytd, Balance_Lcy_Full_Year, Internal_Status, Date_Last_Modified,
		 * Date_Creation
		 */
		insertStmt = insertStmt + ", 0, 0, 0, SysDate, SysDate)";
		try {
			stmt.executeUpdate(insertStmt);
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tForecast_Balances_Upl  Insert succeeded",
					uploadLogFileName);
			totalSuccessRecords++;
			retVal = SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			sqlLogWriter(
					"" + appendZeroforCtr(paramLineCtr) + "\t\tForecast_Balances_Upl Insert Failed:" + e.getMessage());
			logWriter(insertStmt, uploadLogFileName);
			retVal = ERRONEOUS_EXIT;
			totalFailureRecords++;
		}
		return (retVal);
	}

	private int loadOucCodesIntoMemory() {
		int Ctr = 0;
		recsCount = 0;
		String tempStr = "";
		visionOuclst = new ArrayList();
		legalVehiclelst = new ArrayList();

		try {
			rs = stmt.executeQuery("Select Count(1) From Ouc_Codes");
			while (rs.next())
				recsCount = rs.getInt(1);
		} catch (SQLException e) {
			sqlLogWriter("Error trying to count from Ouc_Codes table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		intMaxOucCodes = (int) recsCount;

		try {
			rs = stmt.executeQuery("Select Vision_Ouc, Legal_Vehicle From Ouc_Codes");
			while (rs.next()) {
				visionOuclst.add(rs.getString("Vision_Ouc"));
				legalVehiclelst.add(rs.getString("Legal_Vehicle"));
			}
		} catch (SQLException e) {
			logWriter("Error preparing to select Vision_Ouc & Legal_Vehicles from Ouc_Codes Table :" + e.getMessage(),
					uploadLogFileName);
			sqlLogWriter(sqlStatement);
			return (ERRONEOUS_EXIT);
		}
		/*
		 * for(Ctr = 0;Ctr < intMaxOucCodes;Ctr++) { visionOuc =
		 * (String)visionOuclst.get(Ctr); legalVehicle =
		 * (String)legalVehiclelst.get(Ctr);
		 * 
		 * oucCodesArrOUC[Ctr] = visionOuc; legalVehicleArr[Ctr] = legalVehicle; }
		 */
		if (DEBUG_MODE == YES)
			logWriter("Records loaded from Ouc_Codes table[" + recsCount + "]- FYI.", uploadLogFileName);

		return (SUCCESSFUL_EXIT);
	}

	private int loadLVRatesIntoMemory() {
		try {
			legalVehicleRatesArr = new ArrayList();
			legalVehicleRatesLegalVehicleArr = new ArrayList();
			legalVehicleRatesDataSource = new ArrayList();
			legalVehicleRatesYearArr = new ArrayList();
			legalVehicleRatesfxRateArr = new ArrayList();
			legalVehiclelst = new ArrayList();
			yearlst = new ArrayList();
			dataSourcelst = new ArrayList();
			monthlst = new ArrayList();
			fxRatelst = new ArrayList();

			int yearCounter = 0;
			int Ctr = 0;
			int Ctr2 = 0;
			String tempStr = "";
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(*) From Legal_Vehicle_Rates");
				while (rs.next())
					recsCount = rs.getInt(1);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count from Legal_Vehicle_Rates table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			intMaxLVRates = (int) recsCount;
			try {
				rs = stmt.executeQuery(
						"Select Legal_Vehicle, Year, Data_Source, month, Fx_Rate From Legal_Vehicle_Rates "
								+ "Order by Legal_Vehicle, Year, Data_Source, month ");
				while (rs.next()) {
					legalVehiclelst.add(rs.getString("Legal_Vehicle"));
					yearlst.add(rs.getInt("Year"));
					dataSourcelst.add(rs.getString("Data_Source"));
					monthlst.add(rs.getInt("month"));
					fxRatelst.add(rs.getFloat("Fx_Rate"));
				}
			} catch (SQLException e) {
				logWriter("Error preparing to select Vision_Ouc & Legal_Vehicles from Legal_Vehicle_Rates Table :"
						+ e.getMessage(), uploadLogFileName);
				sqlLogWriter(sqlStatement);
				return (ERRONEOUS_EXIT);
			}
			prevLegalVehicle = "XXXX";
			int Ctr3 = 0;
			ArrayList fxRateTmpList = new ArrayList();
			for (Ctr = 0; Ctr < legalVehiclelst.size(); Ctr++) {
				legalVehicle = (String) legalVehiclelst.get(Ctr);
				year = (Integer) yearlst.get(Ctr);
				dataSource = (String) dataSourcelst.get(Ctr);
				month = (Integer) monthlst.get(Ctr);
				fxRate = (Float) fxRatelst.get(Ctr);

				if (!prevLegalVehicle.equalsIgnoreCase(legalVehicle) || prevYear != year
						|| !prevDataSource.equalsIgnoreCase(dataSource)) {
					Ctr3++;
					for (Ctr2 = 0; Ctr2 < 12; Ctr2++) {
						fxRateTmpList.add(-1);
					}
					legalVehicleRatesArr.add(fxRateTmpList);
					prevLegalVehicle = legalVehicle;
					prevYear = year;
					prevDataSource = dataSource;
					yearCounter = 0;
					legalVehicleRatesLegalVehicleArr.add(legalVehicle);
					legalVehicleRatesDataSource.add(dataSource);
					legalVehicleRatesYearArr.add(year);
				}

				fxRateTmpList.set(month - 1, fxRate);
				int fxRateTmpListSize = legalVehicleRatesArr.size() - 1;
				legalVehicleRatesArr.set(fxRateTmpListSize, fxRateTmpList);
				/*
				 * legalVehicleRatesfxRateArr.add(fxRate);
				 */
				yearCounter++;
			}
			intMaxLVRates = Ctr3;

			if (DEBUG_MODE == YES)
				logWriter("Records loaded from Legal_Vehicle_Rates table [" + Ctr + "] - FYI.", uploadLogFileName);

			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
			logWriter("Error Found on loadLVRatesIntoMemory function :" + e.getMessage(), uploadLogFileName);
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
	}

	private int loadPeriodCtrlsIntoMemory() {
		try {
			int Ctr = 0;
			int Ctr2 = 0;
			String tempStr = "";
			yearlst = new ArrayList();
			monthlst = new ArrayList();
			noOfDayslst = new ArrayList();
			periodControlsArr = new ArrayList();
			periodControlsYearArr = new ArrayList();
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(*) From Period_Controls");
				while (rs.next())
					recsCount = rs.getInt(1);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count from Period_Controls table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			intMaxPeriodControls = (int) recsCount;
			try {
				rs = stmt.executeQuery("Select Year, Month, No_Of_Days From Period_Controls Order by Year, Month");
				while (rs.next()) {
					yearlst.add(rs.getInt("Year"));
					monthlst.add(rs.getInt("Month"));
					noOfDayslst.add(rs.getInt("No_Of_Days"));
				}
			} catch (SQLException e) {
				logWriter("Error preparing to select No_Of_Days from Period_Controls Table :" + e.getMessage(),
						uploadLogFileName);
				sqlLogWriter(sqlStatement);
				return (ERRONEOUS_EXIT);
			}

			prevYear = 0;
			ArrayList tmpYearList = new ArrayList();
			int ctr3 = 0;
			for (Ctr = 0; Ctr < intMaxPeriodControls; Ctr++) {
				year = (Integer) yearlst.get(Ctr);
				month = (Integer) monthlst.get(Ctr);
				noOfDays = (Integer) noOfDayslst.get(Ctr);
				if (prevYear != year) {
					tmpYearList = new ArrayList();
					for (Ctr2 = 0; Ctr2 < 12; Ctr2++) {
						tmpYearList.add(Ctr2, -1);
					}
					periodControlsArr.add(tmpYearList);
					prevYear = year;
					periodControlsYearArr.add(year);
					ctr3++;
				}
				tmpYearList.set(month - 1, noOfDays);
				int periodControlsSize = periodControlsArr.size() - 1;
				periodControlsArr.set(periodControlsSize, tmpYearList);
			}
			intMaxPeriodControls = ctr3 == 0 ? 1 : ctr3;

			if (DEBUG_MODE == YES)
				logWriter("Records loaded from Period_Controls table [" + intMaxPeriodControls + "] - FYI.",
						uploadLogFileName);

			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
			e.printStackTrace();
			finalReturnValue = ERRONEOUS_EXIT;
			return ERRONEOUS_EXIT;
		}
	}

	private int findLegalVehicleRates() {
		try {
			char foundFlag = 'N';
			int actualIndex = 0;
			String tempStr = "";
			String paramLegalVehicle = "";
			int paramYear;
			String paramDataSource = "";
			String paramVisionOuc = "";
			int Ctr = 0;
			int Ctr2 = 0;
			int yearsCount = 0;
			ArrayList tmpListPeriodControlsArr = new ArrayList();

			/*
			 * First identify whether the year being uploaded is present in the
			 * Period_Controls table or not
			 */
			paramYear = Integer.parseInt(uploadData[IDX_FCUPLOAD_YEAR]);
			foundFlag = 'N';
			noOfDaysSum = 0;
			for (Ctr = 0; Ctr < intMaxPeriodControls; Ctr++) {
				if (paramYear == (Integer) periodControlsYearArr.get(Ctr)) {
					foundFlag = 'Y';
					periodControlsIndex = Ctr;
					tmpListPeriodControlsArr.add(periodControlsArr.get(Ctr));
					ArrayList daysPerYearlst = (ArrayList) tmpListPeriodControlsArr.get(0);
					for (Ctr2 = 0; Ctr2 < 12; Ctr2++)
						if ((Integer) daysPerYearlst.get(Ctr2) != -1) // PeriodControlsArr
																		// contains
																		// noOfDays.
						{
							noOfDaysSum += (Integer) daysPerYearlst.get(Ctr2);
							yearsCount++;
						}
				}
			}
			if (foundFlag == 'N') {
				logWriter("No entries found for Year [" + paramYear + "], in Period_Controls table !!",
						uploadLogFileName);
				return (NULL_VALUE);
			}
			if (yearsCount != 12) {
				logWriter("Only " + Ctr2 + " months have been maintained for the Year [" + paramYear + "] ",
						uploadLogFileName);
				return (NULL_VALUE);
			}
			paramVisionOuc = uploadData[IDX_FCUPLOAD_VISION_OUC];
			paramVisionOuc = paramVisionOuc.replaceAll("'", "");
			foundFlag = 'N';
			for (Ctr = 0; Ctr < intMaxOucCodes; Ctr++) {
				if (paramVisionOuc.equalsIgnoreCase(visionOuclst.get(Ctr).toString())) {
					foundFlag = 'Y';
					break;
				}
			}
			if (foundFlag == 'N') {
				logWriter("Vision_Ouc [" + paramVisionOuc + "] is not maintained in the Ouc_Codes table !!",
						uploadLogFileName);
				return (NULL_VALUE);
			}
			paramLegalVehicle = (String) legalVehiclelst.get(retVal);
			logWriter("paramLegalVehicle : " + paramLegalVehicle, uploadLogFileName);
			paramYear = Integer.parseInt(uploadData[IDX_FCUPLOAD_YEAR]);
			logWriter("paramYear : " + paramYear, uploadLogFileName);
			paramDataSource = uploadData[IDX_FCUPLOAD_DATA_SOURCE].replaceAll("'", "");
			logWriter("paramDataSource : " + paramDataSource, uploadLogFileName);
			logWriter("intMaxLVRates : " + intMaxLVRates, uploadLogFileName);

			foundFlag = 'N';
			for (Ctr = 0; Ctr < intMaxLVRates; Ctr++) {
				if (paramLegalVehicle.equalsIgnoreCase(legalVehicleRatesLegalVehicleArr.get(Ctr).toString())
						&& paramYear == (Integer) legalVehicleRatesYearArr.get(Ctr)
						&& paramDataSource.equalsIgnoreCase(legalVehicleRatesDataSource.get(Ctr).toString())) {
					foundFlag = 'Y';
					actualIndex = Ctr;
					Ctr2 = 0;
					ArrayList tmpListLvRates = new ArrayList();
					tmpListLvRates = (ArrayList) legalVehicleRatesArr.get(actualIndex);
					logWriter("tmpListLvRates.size() : " + tmpListLvRates.size(), uploadLogFileName);
					for (Ctr = 0; Ctr < 12; Ctr++) {
						logWriter("Ctr : " + Ctr, uploadLogFileName);
						logWriter("tmpListLvRates.get(Ctr) : " + tmpListLvRates.get(Ctr), uploadLogFileName);
						if ((Double) tmpListLvRates.get(Ctr) != -1)
							Ctr2++;
					}
					break;
				}
			}
			if (foundFlag == 'N') {
				logWriter("Rate for combination Legal_Vehicle [" + paramLegalVehicle + "], Year [" + paramYear
						+ "], Data_Source [" + paramDataSource + "] is not Maintained !!", uploadLogFileName);
				return (NULL_VALUE);
			}
			if (Ctr2 == 12)
				return (actualIndex);

			logWriter("Only " + Ctr2 + " rates have been maintained for Legal_Vehicle [" + paramLegalVehicle
					+ "], Year [" + paramYear + "] & Data_Source [" + paramDataSource + "] "
					+ "in the Legal_Vehicle_Rates table !!", uploadLogFileName);
			return (NULL_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (NULL_VALUE);
	}

	private int prepareMailString() {
		String tempStr = "";
		recsCount = 0;
		try {
			rs = stmt.executeQuery("SELECT COUNT(1) FROM FIN_ADJ_HEADERS_UPL T1,LE_BOOK T2 "
					+ " WHERE T1.COUNTRY = T2.COUNTRY AND  T1.LE_BOOK = T2.LE_BOOK " + " AND  T1.ACTION_ID = '"
					+ actionId + "' AND  (TRIM(T2.MAKER_EMAIL) IS NOT NULL OR  TRIM(T2.VERIFIER_EMAIL) IS NOT NULL)");
			while (rs.next())
				recsCount = rs.getInt("COUNT(1)");
		} catch (SQLException e) {
			sqlLogWriter("Error trying to count from FIN_ADJ_HEADERS_UPL table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		intEmailCount = (int) recsCount;
		if (intEmailCount == 0)
			intEmailCount = 1;
		totalFetchCnt = 0;
		int Ctr = 1;
		try {
			sqlStatement = "SELECT DISTINCT T1.COUNTRY , T1.LE_BOOK , T1.YEAR_MONTH, "
					+ " T1.ADJ_SOURCE, T1.ADJ_REFERENCE, T3.USER_NAME , "
					+ " CASE WHEN TRIM(T2.MAKER_EMAIL   ) IS NOT NULL THEN 'Validate' "
					+ " WHEN TRIM(T2.VERIFIER_EMAIL) IS NOT NULL THEN 'Authorize' "
					+ " ELSE 'Authorize' END MESSAGE, NVL(TRIM(T2.MAKER_EMAIL),TRIM(T2.VERIFIER_EMAIL)) MAIL_ID "
					+ " FROM FIN_ADJ_HEADERS_UPL T1, LE_BOOK T2, VISION_USERS T3 "
					+ " WHERE  T1.COUNTRY = T2.COUNTRY AND  T1.LE_BOOK = T2.LE_BOOK " + " AND  T1.ACTION_ID = '"
					+ actionId + "' AND  (TRIM(T2.MAKER_EMAIL) IS NOT NULL "
					+ " OR TRIM(T2.VERIFIER_EMAIL) IS NOT NULL)  AND  T1.MAKER = T3.VISION_ID "
					+ " ORDER BY T1.COUNTRY , T1.LE_BOOK , T1.YEAR_MONTH, " + " T1.ADJ_SOURCE, T1.ADJ_REFERENCE ";
			rs = stmt.executeQuery(sqlStatement);
			while (rs.next()) {
				EmailArrayCountry.add(rs.getString("COUNTRY"));
				EmailArrayLeBook.add(rs.getString("LE_BOOK"));
				EmailArrayYearMonth.add(rs.getInt("YEAR_MONTH"));
				EmailArrayAdjSource.add(rs.getString("ADJ_SOURCE"));
				EmailArrayAdjReference.add(rs.getString("ADJ_REFERENCE"));
				EmailArrayMakerName.add(rs.getString("USER_NAME"));
				EmailArrayMessageText.add(rs.getString("MESSAGE"));
				EmailArrayEmailId.add(rs.getString("MAIL_ID"));
				totalFetchCnt++;
				currFetchCnt = Ctr;
				Ctr++;
				copyCnt++;
				retVal = doProcess();
				if (retVal == ERRONEOUS_EXIT) {
					con.rollback();
					logWriter("Rolling back the current transactions batch ... ", uploadLogFileName);
					return (ERRONEOUS_EXIT);
				}
				if (currFetchCnt < MAX_RECS_FETCH_COUNT) {
					break;
				}
			}
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to FIN_ADJ_HEADERS_UPL Table :" + e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
	}

	private int doProcess() {
		// Add the values directly to the List of Email Array so we no need to
		// Use this function
		return (SUCCESSFUL_EXIT);
	}

	private int sendValidaterEmailId() {
		String appLink = findVisionVariableValue("VISION_APP_LINK");
		for (currRec = 0; currRec < copyCnt; currRec++) {
			if (currRec == 0) {
				locEMailId = "" + EmailArrayEmailId.get(currRec);
				locEMailSubject = "Vision BI: Financial Adjustment For Country" + " <" + EmailArrayCountry.get(currRec)
						+ ">  LeBook " + " <" + EmailArrayLeBook.get(currRec) + "> YearMonth" + " <"
						+ EmailArrayYearMonth.get(currRec) + "> " + " Uploaded By " + EmailArrayMakerName.get(currRec);
				locEMailBodyVar = locEMailBodyVar
						+ "<br><tr><td> This is to inform you that Financial Adjustments have been uploaded by  "
						+ EmailArrayMakerName.get(currRec) + "  for <b>Country</b> <  " + EmailArrayCountry.get(currRec)
						+ " >  <b>LeBook</b>" + " <" + EmailArrayLeBook.get(currRec) + ">  <b>YearMonth</b> <"
						+ EmailArrayYearMonth.get(currRec) + "> </td></tr>";
				locEMailBodyVar = locEMailBodyVar + "<br><tr><td> Click on this link to log into VisionBI and "
						+ EmailArrayMessageText.get(currRec) + "  the adjustments  " + appLink + "  </td></tr>";

				locEMailBodyVar = locEMailBodyVar
						+ "<br><table width='30%' cellspacing=0 cellpadding=0 border=1><tbody><tr><th  style ='padding:3px;font-size:10.0pt;font-family:Tahoma,sans-serif;text-align:center;font-weight:bold;border-right:1px solid #000;color:maroon;' nowrap><b>Adj Source</b></th>"
						+ "<th style ='padding:3px;font-size:10.0pt;font-family:Tahoma,sans-serif;text-align:center;font-weight:bold;border-right:1px solid #000;color:maroon;' nowrap><b>Adj Reference</b></th></tr>";
				locEMailBodyVar = locEMailBodyVar
						+ "<tr><td style ='padding:3px;font-size:12px;text-align:center;color:#000;border-top:1px solid #000;'>"
						+ EmailArrayAdjSource.get(currRec)
						+ "</td> <td style ='padding:3px;font-family: Roboto,RobotoDraft,Helvetica,Arial,sans-serif;font-size:12px;text-align:center;color:#000;border-top:1px solid #000;'>"
						+ EmailArrayAdjReference.get(currRec) + "</td></tr></tbody></table>";

				prev_Country = (String) EmailArrayCountry.get(currRec);
				prev_LeBook = String.valueOf(EmailArrayLeBook.get(currRec));
				prev_YearMonth = String.valueOf(EmailArrayYearMonth.get(currRec));
				continue;
			}
			if (!(prev_Country.equalsIgnoreCase((String) EmailArrayCountry.get(currRec)))
					|| !(prev_LeBook.equalsIgnoreCase((String) EmailArrayLeBook.get(currRec)))
					|| !(prev_LeBook.equalsIgnoreCase((String) EmailArrayLeBook.get(currRec)))) {

				locEMailBodyVar = locEMailBodyVar + EmailArrayAdjSource.get(currRec) + " "
						+ EmailArrayAdjReference.get(currRec);
				mailString = locEMailBodyVar + "" + locEMailSubject + "" + locEMailSubject;
				retVal = sendAsMail(locEMailId, locEMailSubject, locEMailBodyVar);
				if (retVal == -1) {
					localString = "<tr><td>The Mail not Send to the Email Id :" + locEMailId + "</td></tr>";
					return (SUCCESSFUL_EXIT);
				}
				locEMailId = "" + EmailArrayEmailId.get(currRec);
				locEMailSubject = "Vision BI: Financial Adjustment For Country" + " <" + EmailArrayCountry.get(currRec)
						+ ">  LeBook " + " <" + EmailArrayLeBook.get(currRec) + "> YearMonth" + " <"
						+ EmailArrayYearMonth.get(currRec) + "> " + " Uploaded By Maker  <"
						+ EmailArrayMakerName.get(currRec) + ">";
				locEMailBodyVar = locEMailBodyVar
						+ "<br><tr><td> This is to inform you that Financial Adjustments have been uploaded by  <b>Maker</b> < "
						+ EmailArrayMakerName.get(currRec) + " >  for <b>Country</b> <  "
						+ EmailArrayCountry.get(currRec) + " >  <b>LeBook</b>" + " <" + EmailArrayLeBook.get(currRec)
						+ ">  <b>YearMonth</b> <" + EmailArrayYearMonth.get(currRec) + "> </td></tr>";
				locEMailBodyVar = locEMailBodyVar + "<br><tr><td> Click on this link to log into VisionBI and "
						+ EmailArrayMessageText.get(currRec) + "  the adjustments  " + appLink + "  </td></tr>";

				locEMailBodyVar = locEMailBodyVar
						+ "<br><table width='30%' cellspacing=0 cellpadding=0 border=1><tbody><tr><th  style ='padding:3px;font-size:10.0pt;font-family:Tahoma,sans-serif;text-align:center;font-weight:bold;border-right:1px solid #000;color:maroon;' nowrap><b>Adj Source</b></th>"
						+ "<th style ='padding:3px;font-size:10.0pt;font-family:Tahoma,sans-serif;text-align:center;font-weight:bold;border-right:1px solid #000;color:maroon;' nowrap><b>Adj Reference</b></th></tr>";
				locEMailBodyVar = locEMailBodyVar
						+ "<tr><td style ='padding:3px;font-size:12px;text-align:center;color:#000;border-top:1px solid #000;'>"
						+ EmailArrayAdjSource.get(currRec)
						+ "</td> <td style ='padding:3px;font-family: Roboto,RobotoDraft,Helvetica,Arial,sans-serif;font-size:12px;text-align:center;color:#000;border-top:1px solid #000;'>"
						+ EmailArrayAdjReference.get(currRec) + "</td></tr></tbody></table>";
				prev_Country = (String) EmailArrayCountry.get(currRec);
				prev_LeBook = String.valueOf(EmailArrayLeBook.get(currRec));
				prev_YearMonth = String.valueOf(EmailArrayYearMonth.get(currRec));
				continue;
			}
		}
		if (copyCnt > 0) {
			locEMailBodyVar = locEMailBodyVar
					+ "<br><tr><td>Note: Please do not reply to this e-mail, it is an automated message.</td></tr>";
			mailString = locEMailBodyVar + "" + locEMailSubject + "" + locEMailId;
			retVal = sendAsMail(locEMailId, locEMailSubject, locEMailBodyVar);
			if (retVal == -1) {
				localString = "\nThe Mail not Send to the Email Id :" + locEMailId;
				return (SUCCESSFUL_EXIT);
			}
		}
		return SUCCESSFUL_EXIT;
	}

	public int sendAsMail(String receiver, String subjects, String body) {
		logWriter("Getting the values from vision variables..", uploadLogFileName);
		String emailId = findVisionVariableValue("VISION_APP_SCHEDULER_SUPPORT_EMAIL");
		// String emailId =
		// getCommonDao().findVisionVariableValue("VISION_APP_SCHEDULER_FROM_EMAIL");
		// String password = getCommonDao().findVisionVariableValue("EMAIL_PASSWORD");
		String mailhost = findVisionVariableValue("EMAIL_HOST");
		String mailPort = findVisionVariableValue("EMAIL_PORT");
		logWriter("Sending Email from " + emailId + " to " + receiver, uploadLogFileName);
		if (!ValidationUtil.isValid(mailhost)) {
			mailhost = "172.18.1.55";
		}
		if ("-1" == mailPort) {
			mailPort = "25";
		}
		Properties pr = new Properties();
		// pr.put("mail.smtp.auth", "true"); // for username and password only For Gmail
		// not for Banks
		// pr.put("mail.smtp.starttls.enable", "true");
		// pr.put("mail.smtp.user", emailId);// username
		// pr.put("mail.smtp.host", mailhost); // here host is gmail.com// for banks is
		// can IP address for the host server.
		// pr.put("mail.smtp.port", mailPort); // email port
		///// Below are the configurations for the Bank Email not for Gmail.
		pr.put("mail.smtp.host", mailhost);
		pr.put("mail.smtp.user", emailId);
		pr.put("mail.smtp.port", mailPort);
		pr.put("mail.smtp.starttls.enable", "false");
		/*
		 * jakarta.mail.Session session = jakarta.mail.Session.getDefaultInstance(pr, new
		 * Authenticator() {
		 * 
		 * @Override protected PasswordAuthentication getPasswordAuthentication() {
		 * return new PasswordAuthentication(emailId, password); } });
		 */
		jakarta.mail.Session session = jakarta.mail.Session.getDefaultInstance(pr); // Use in case of deploying to the
																				// Production server
		MimeMessage message = new MimeMessage(session);
		MimeBodyPart messagePart = new MimeBodyPart();
		if (ValidationUtil.isValid(receiver)) {
			String[] toIds = receiver.split(";");
			try {
				InternetAddress[] toAddress = new InternetAddress[toIds.length];
				int counter = 0;
				for (String recipients : toIds) {
					toAddress[counter] = new InternetAddress(recipients.trim());
					counter++;
				}
				message.setRecipients(Message.RecipientType.TO, toAddress);
			} catch (AddressException e) {
				e.printStackTrace();
				logWriter("AddressException...", uploadLogFileName);
			} catch (MessagingException e) {
				e.printStackTrace();
				logWriter("MessagingException...", uploadLogFileName);
			}
		}
		try {
			message.setFrom(new InternetAddress(emailId));
			// message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver));
			message.setSubject(subjects);
			// message.setText(body);
			logWriter("body:" + body, uploadLogFileName);
			message.setText(body);
			// messagePart.setText(body);
			logWriter("body:" + body, uploadLogFileName);
			messagePart.setContent(body, "text/html");
			Multipart hotmailMP = new MimeMultipart();
			hotmailMP.addBodyPart(messagePart);
			// message.setContent(hotmailMP);
			message.setContent(hotmailMP, "text/html");
			Transport transport = session.getTransport("smtp");
			transport.send(message);

//	   System.out.println("Sent message successfully....");
//			System.out.println("Mail Sent Succesfully");
			logWriter("Mail Sent Succesfully", uploadLogFileName);
		} catch (Exception e) {
//			System.out.println("Mail Sent Failed...");
			logWriter("Mail Sent Failed...", uploadLogFileName);
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	private String checkAndProcessQuotes(String sourceString) {
		try {
			sourceString = sourceString.trim();
			if (sourceString.contains("'")) {
				sourceString = sourceString.replaceAll("'", "");
			}
			sourceString = "'" + sourceString + "'";
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("Error on Excel Upload Line [checkAndProcessQuotes]");
		}
		return sourceString;
	}

	private String GetCurrentDateTime() {
		String currentDate = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		return currentDate;
	}

	private String appendZeroforCtr(int paramLineCtr) {
		int ParamLength = 0;
		String paramLineCtr1 = "";
		if (String.valueOf(paramLineCtr).length() < 6) {
			ParamLength = String.valueOf(paramLineCtr).length();
		}
		paramLineCtr1 = "" + paramLineCtr;
		for (int Ctr = ParamLength; Ctr < 6; Ctr++) {
			paramLineCtr1 = "0" + paramLineCtr1 + "";
		}
		return paramLineCtr1;
	}

	/*
	 * public VisionUploadWb getVisionUploadWb() { return visionUploadWb; }
	 * 
	 * public void setVisionUploadWb(VisionUploadWb visionUploadWb) {
	 * this.visionUploadWb = visionUploadWb; }
	 */

	public String getPassword() {
		return password;
	}

	/*
	 * public void setPassword(String password) { this.password =
	 * ValidationUtil.passwordDecrypt(password); }
	 */

	public String getHostNameFtp() {
		return hostNameFtp;
	}

	public void setHostNameFtp(String hostNameFtp) {
		this.hostNameFtp = hostNameFtp;
	}

	public String getUserNameFtp() {
		return userNameFtp;
	}

	public void setUserNameFtp(String userNameFtp) {
		this.userNameFtp = userNameFtp;
	}

	public String getPasswordFtp() {
		return passwordFtp;
	}

	public void setPasswordFtp(String passwordFtp) {
		this.passwordFtp = ValidationUtil.passwordDecrypt(passwordFtp);
	}

	public String getUploadDir() {
		try {
			String uploadDir = "";
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE = 'VISION_XLUPL_LOG_FILE_PATH'");
			while (rs.next())
				uploadDir = rs.getString("VALUE");

			return uploadDir;
		} catch (SQLException e) {
//			System.out.println("Error reading values for parameter [XLUPL_LOG_FILE_PATH] :\n" + e.getMessage());
//			System.out.println(e.getMessage());
			e.printStackTrace();
			return "null";
		}
	}

	public void moveFiletoDBserver() {
		try {
//			System.out.println("FTP Start on ExcelUpload..!");
			FileInputStream inMainLog = null;
			FileInputStream inUplLog = null;
			inMainLog = new FileInputStream(mainLogFileName);
			inUplLog = new FileInputStream(uploadLogFileName);
			JSch jsch = new JSch();
			jsch.setKnownHosts("c\\:known_hosts");
			Session session = jsch.getSession(getUserNameFtp(), getHostNameFtp());
			{
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies
											// in host key being in known-hosts
											// file
				session.setPassword(getPasswordFtp());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(getUploadDir());
			sftpChannel.put(inMainLog, EXCEL_LOG_FILENAME);
			sftpChannel.put(inUplLog, EXCEL_FILE_NAME);
			sftpChannel.exit();
			channel = session.openChannel("shell");
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			commander.println("exit");
			commander.close();
			do {
				Thread.sleep(1000);
			} while (!channel.isEOF());
			session.disconnect();
			File file = new File(EXCEL_FILE_NAME);
			file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			System.out.println("File Not found :" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("IOExpception :" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("Exception :" + e.getMessage());
		}
	}

	public void moveFileFromWindowToDBserver() {
		try {
//			System.out.println("Windows to DB Server : FTP Start on ExcelUpload..!");
			FileInputStream inMainLog = null;
			FileInputStream inUplLog = null;
			inMainLog = new FileInputStream(mainLogFileName);
			inUplLog = new FileInputStream(uploadLogFileName);
			JSch jsch = new JSch();
			jsch.setKnownHosts("c\\:known_hosts");
//			System.out.println("getHostNameFtp:" + getUserNameFtp());
//			System.out.println("getHostNameFtp:" + getHostNameFtp());
			Session session = jsch.getSession(getUserNameFtp(), getHostNameFtp());
			{
				UserInfo ui = new MyUserInfo();
				session.setUserInfo(ui); // OR non-interactive version. Relies
											// in host key being in known-hosts
											// file
				session.setPassword(getPasswordFtp());
			}
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			logWriter("Trying to connect to the session", uploadLogFileName);
//			System.out.println("Session is established");
//			System.out.println("Trying to connect to the session");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			logWriter("Trying to connect to the Channel", uploadLogFileName);
//			System.out.println("Trying to connect to the Channel");
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(getUploadDir());
			sftpChannel.put(inMainLog, EXCEL_LOG_FILENAME);
			sftpChannel.put(inUplLog, EXCEL_FILE_NAME);
			sftpChannel.exit();
			channel = session.openChannel("shell");
			OutputStream inputstream_for_the_channel = channel.getOutputStream();
			PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
			channel.connect();
			commander.println("exit");
			commander.close();
			do {
				Thread.sleep(1000);
			} while (!channel.isEOF());
			session.disconnect();
			logWriter("Trying to disconnect to the session", uploadLogFileName);
//			System.out.println("Trying to disconnect to the session");
			File file = new File(EXCEL_FILE_NAME);
			file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			System.out.println("File Not found :" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("IOExpception :" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("Exception :" + e.getMessage());
		}
	}

	public class MyUserInfo implements UserInfo {
		public String getPassword() {
			return password;
		}

		public boolean promptYesNo(String str) {
			return false;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
			return;
		}
	}

	public Boolean getMultiServer() {
		return multiServer;
	}

	public void setMultiServer(Boolean multiServer) {
		this.multiServer = multiServer;
	}

	private int dailyDcUpload() {
		try {
			char firstTimeFlag = 'Y';
			int totUploadableRecords = 0;
			int totDetailsRecords = 0;
			int Ctr1 = 0;
			char abortFlag = 'N';
			String tempStr = "";
			finalReturnValue = STATUS_SUCCESS;

			/* Frame the Action_Id For this upload */
			actionId = "";
			actionId = getActionId();
//			System.out.println("action id:" + actionId);
			/* find if upload is permitted for this group or not */
			retVal = findIfUploadAllowedForDailyDCTbls();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			/*
			 * Check for locks on these tables, if they have been locked by some build
			 * running at the time of this upload
			 */
			retVal = checkForDailyDCTablesLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("One of the tables [FIN_DLY_ADJ_HEADERS, FIN_DLY_ADJ_DETAILS, FIN_DLY_ADJ_POSTINGS] "
						+ "has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return (ERRONEOUS_EXIT);
			}
			retVal = lockDailyDCTablesForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for Daily DC XL Upload. Cannot proceed now with upload !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}
			/* Set the status to In-Progress mode for the upload request */
			retVal = updateUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile(
					"Uploading to the tables [Fin_Dly_Adj_Headers, Fin_Dly_Adj_Details & Fin_Dly_Adj_Postings]");
			uploadLogFileName = xluploadLogFilePath + uploadTableName + "_" + makerId + "_" + GetCurrentDateTime()
					+ ".err";
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";
			try {
				logfile = new FileWriter(uploadLogFileName); // file Open in
																// Write mode
				logfile.close();
			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName
						+ "] for logging process steps :\n  System Msg [" + e.getMessage() + "]\n");
				writeToMainLogFile("Aborting further processing of this request - FYI");
				return (ERRONEOUS_EXIT);
			}
			logWriter("Action Id obtained for this run : [" + actionId + "] - FYI\n", uploadLogFileName);
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";
			uploadFileName = uploadFile;
			try {
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + uploadFile + "]\nSystem Msg [" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI");
				logWriter("Aborting further processing of this request - FYI", uploadLogFileName);

				retVal = updateUploadStatus(STATUS_FAILURE);

				if (retVal == ERRONEOUS_EXIT)
					return (ABORT_EXECUTION);

				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
				return (ERRONEOUS_EXIT);
			}

			logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);

			originalTableName = uploadTableName;

			logWriter("Tables Being uploaded\t:[Fin_Dly_Adj_Headers, Fin_Dly_Adj_Details & Fin_Dly_Adj_Postings]\n",
					uploadLogFileName);

			if (DEBUG_MODE == YES)
				logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);

			retVal = checkForExistenceOfDailyDcTbls();
			if (retVal == ERRONEOUS_EXIT)
				return (ERRONEOUS_EXIT);

			retVal = loadTxtFileColumnNames(uploadFile);
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			retVal = validateDailyDCTblCols();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			retVal = chkColCntForEachRow();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}

			logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);
			logWriter("Uploading to UPL Tables first.\n", uploadLogFileName);

			TAB_FLAG = 'Y';
			retVal = uploadDailyDCFileData();
			TAB_FLAG = 'N';
			if (retVal == ERRONEOUS_EXIT) {
				con.rollback();
				return (ERRONEOUS_EXIT);
			}
			if (finalReturnValue == ERRONEOUS_EXIT) {
				con.rollback();
				return (ERRONEOUS_EXIT);
			} else {
				con.commit();
			}
			// con.commit();
			abortFlag = 'N';

			/* Starting abort conditions check */

			logWriter("Checking for abort conditions......", uploadLogFileName);

			/*
			 * Procure the Current Year & Month from the Vision_Variables table for usage
			 * later
			 */
			tempStr = "";
			try {
				rs = stmt.executeQuery("Select RTrim(Value) From Vision_Variables Where Variable = 'CURRENT_MONTH'");
				while (rs.next())
					tempStr = rs.getString(1);

				currentMonth = Integer.parseInt(tempStr);
			} catch (Exception e) {
				sqlLogWriter("Error fetching Vision_Variables:CURRENT_MONTH :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			try {
				rs = stmt.executeQuery("Select RTrim(Value) From Vision_Variables Where Variable = 'CURRENT_YEAR'");
				while (rs.next())
					tempStr = rs.getString(1);

				currentYear = Integer.parseInt(tempStr);
				// yearMonth = currentYear * 100 + currentMonth;
				logWriter("Current Year_Month  from Vision_Variables is : [" + yearMonth + "]", uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error fetching Vision_Variables:CURRENT_MONTH :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}

			try {
				rs = stmt.executeQuery(
						"Select To_Char(Business_date,'RRRRMM') CURRENT_YEAR_MONTH from Vision_Business_day where Country = "
								+ country + " and LE_Book = " + leBook + " ");
				while (rs.next())
					tempStr = rs.getString(1);

				yearMonth = Integer.parseInt(tempStr);
				actualyearMonth = tempStr;
				currentYear = Integer.parseInt(actualyearMonth.substring(0, 4));
				currentMonth = Integer.parseInt(actualyearMonth.substring(4, 6));
				logWriter("Current Year_Month  from V_Curr_Year_Month is : [" + yearMonth + "]", uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error fetching data from V_Curr_Year_Month :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			try {
				rs = stmt.executeQuery("Select Trim(To_Char(Reporting_Date))  From Period_Controls Where Month = "
						+ currentMonth + " And Year = " + currentYear + "");
				while (rs.next())
					firstDay = rs.getString(1);

				logWriter("Current First Day  from Period_Controls is : [" + firstDay + "]", uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error Reporting_Date from Period_Controls table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (currentMonth + 1 > 12) {
				currentYear++;
				currentMonth = 1;
			} else
				currentMonth++;

			try {
				rs = stmt.executeQuery("Select Trim(To_Char(Reporting_Date))  From Period_Controls Where Month = "
						+ currentMonth + " And Year = " + currentYear + "");
				while (rs.next())
					reportingDate = rs.getString(1);

				logWriter("Current Reporting Date  from Period_Controls is : [" + reportingDate + "]",
						uploadLogFileName);
			} catch (Exception e) {
				sqlLogWriter("Error Reporting_Date from Period_Controls table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (currentMonth == 1) {
				currentMonth = 12;
				currentYear--;
			} else
				currentMonth--;

			recsCount = 0;
			int tmpPrvMonth = currentMonth - 1;
			String prevMonth = "";
			if (tmpPrvMonth < 10) {
				prevMonth = "0" + tmpPrvMonth;
			}
			String tmpPrevYearMonth = currentYear + prevMonth;
			int prevYearMonth = Integer.parseInt(tmpPrevYearMonth);
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Headers_Upl T1 Where Action_Id = '" + actionId
						+ "' And T1.Year_Month < " + prevYearMonth + "");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter("\n Fin_Dly_Adj_Headers_Upl CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Year_Month abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Trim(To_Char(T1.Year_Month)) From Fin_Dly_Adj_Headers_Upl T1 "
						+ "Where Action_Id = '" + actionId + "' " + "And T1.Year_Month < " + yearMonth + " ";

				errorMsg = "The following Year_Month values < Current Year & Month in Vision_Variables";
				logWriter("" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/*
			 * Check if the Country + Le_Book combination given in the upload table exists
			 * in the Le_Book table
			 */

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And Not Exists (Select 'X' From Le_Book T2  "
						+ " Where T1.Country = T2.Country And T1.Le_Book = T2.Le_Book " + " And T1.Action_Id = '"
						+ actionId + "')");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Dly_Adj_Headers_Upl 2 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Country + Le_Book existence in Le_Book table abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Country || ',' || Le_Book From Fin_Dly_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' And Not Exists (Select 'X' From Le_Book T2 "
						+ " Where T1.Country = T2.Country And T1.Le_Book = T2.Le_Book " + " And T1.Action_Id = '"
						+ actionId + "')";
				errorMsg = "The following Country + Le_Book combinations are invalid ";
				logWriter("\n" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/*
			 * Check if the Vision_Ouc given in the upload file has same Regulatory Vehicle
			 * as the Default_Ouc in the Le_Book table
			 */

			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Postings_Upl T3, Ouc_Codes T4 "
						+ " Where Action_Id = '" + actionId + "' And T3.Vision_Ouc = T4.Vision_Ouc"
						+ " And T4.Regulatory_Vehicle != (Select Regulatory_Vehicle From Le_Book T1, Ouc_Codes T2"
						+ " Where T1.Default_Ouc = T2.Vision_Ouc And T1.Country = T3.Country"
						+ " And T1.Le_Book = T3.Le_Book)");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter("\n Fin_Adj_Headers_Upl 3 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Regulatory_Vehicles from the LE_Book table for abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), T3.Vision_Ouc  From Fin_Dly_Adj_Postings_Upl T3, Ouc_Codes T4 "
						+ " Where Action_Id = '" + actionId + "' " + " And T3.Vision_Ouc = T4.Vision_Ouc "
						+ " And T4.Regulatory_Vehicle != "
						+ " (Select Regulatory_Vehicle From Le_Book T1, Ouc_Codes T2 "
						+ " Where T1.Default_Ouc = T2.Vision_Ouc " + " And T1.Country = T3.Country "
						+ " And T1.Le_Book = T3.Le_Book) ";
				errorMsg = "The following Vision_Oucs don't have matching Regulatory_Vehicle for Country + Le_Book combination's Default_Oucs";
				logWriter("" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/*
			 * Check if Year_Month in the upload file > MV:Year_Month and Financials_Daily
			 * flag = 'Y'
			 */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And T1.Year_Month > " + yearMonth + "");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter(" Fin_Dly_Adj_Headers_Upl 4 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error trying to count for Year_Month (for Fin Dly Flag = 'Y') abort condition validation :"
								+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Trim(To_Char(T1.Year_Month)) From Fin_Dly_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' " + " And T1.Year_Month > " + yearMonth + " ";
				errorMsg = "The following Year_Month is > Current Year/Month in Vision_Variables table";
				logWriter("\n" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/* Data_Type check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' And (Data_Type = 'MA'"
						+ " Or Data_Type Not In (Select Alpha_Sub_Tab From Alpha_Sub_Tab T2 Where "
						+ " T2.Alpha_Tab = 301))");
				while (rs.next())
					recsCount = rs.getInt("Count(1)");

				logWriter("\n Fin_Dly_Adj_Headers_Upl 5 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Data_Type From Fin_Dly_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' " + " And (Trim(Data_Type) = 'MA' "
						+ " Or Data_Type Not In (Select Alpha_Sub_Tab From Alpha_Sub_Tab T2 Where "
						+ " T2.Alpha_Tab = 301))";
				errorMsg = "The following Data_Types are invalid";
				logWriter("\n" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/* Originating_Ouc check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Count(1) From Fin_Dly_Adj_Headers_Upl T1" + " Where Action_Id = '"
						+ actionId + "'" + " And Originating_Ouc In (Select Vision_Ouc From Ouc_Codes T2 Where"
						+ " Ouc_Status != 0)");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter(" Fin_Adj_Headers_Upl 6 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = "Select Trim(To_Char(Line_Number)), Originating_Ouc From Fin_Dly_Adj_Headers_Upl T1 "
						+ " Where Action_Id = '" + actionId + "' "
						+ " And Originating_Ouc In (Select Vision_Ouc From Ouc_Codes T2 Where " + " Ouc_Status != 0)";
				errorMsg = "The following Originating_Oucs are invalid";
				logWriter("\n" + errorMsg, uploadLogFileName);

				displayAbortErrorsForDC(sqlStatement, errorMsg);

				abortFlag = 'Y';
			}
			/* Reversal_Flag check */
			/*
			 * recsCount = 0; try{ rs = stmt.executeQuery(
			 * "Select Count(1) Into :recsCount From Fin_Dly_Adj_Headers_Upl T1 " +
			 * " Where Action_Id = '" + actionId + "' And Reversal_Flag Not In ('Y', 'N')");
			 * while(rs.next()) recsCount = rs.getInt(1);
			 * 
			 * logWriter("\n Fin_Adj_Headers_Upl 7 CNT is : [" + recsCount + "]"); }
			 * catch(SQLException e){ sqlLogWriter(
			 * "Error trying to count for Data_Type abort condition validation !!"
			 * +e.getMessage()); return(ERRONEOUS_EXIT); } if (recsCount != 0) {
			 * sqlStatement=
			 * "Select Trim(To_Char(Line_Number)), Reversal_Flag From Fin_Adj_Headers_Upl T1 "
			 * + " Where Action_Id = '" + actionId + "' " +
			 * " And Reversal_Flag Not In ('Y', 'N')"; errorMsg =
			 * "The following Reversal_Flag values are invalid"; logWriter("\n"+errorMsg);
			 * 
			 * displayAbortErrorsForDC(sqlStatement,errorMsg);
			 * 
			 * abortFlag = 'Y'; }
			 */
			/* Maker check */
			recsCount = 0;
			try {
				rs = stmt.executeQuery("Select Sum(Hdrs_Count) From "
						+ " (Select Count(1) Hdrs_Count From Fin_Dly_Adj_Headers_Upl T1 " + " Where Action_Id = '"
						+ actionId + "' " + " And Maker Not In (Select Vision_Id From Vision_Users) " + " Union "
						+ " Select Count(1) Hdrs_Count From Fin_Dly_Adj_Headers_Upl T1, Vision_Users T2"
						+ " Where Action_Id = '" + actionId + "' " + " And T1.Maker = T2.Vision_Id"
						+ " And T2.Update_Restriction = 'Y'"
						+ " And (T1.Country != Nvl(T2.Country, 'XX') Or T1.Le_Book != Nvl(T2.Le_Book, 'XX')))");
				while (rs.next())
					recsCount = rs.getInt(1);

				logWriter("\n Fin_Dly_Adj_Headers_Upl 8 CNT is : [" + recsCount + "]", uploadLogFileName);
			} catch (SQLException e) {
				sqlLogWriter("Error trying to count for Data_Type abort condition validation :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (recsCount != 0) {
				sqlStatement = " Select Trim(To_Char(Line_Number)), Trim(To_Char(Maker)) From Fin_Dly_Adj_Headers_Upl "
						+ " Where Action_Id = '" + actionId + "' "
						+ " And Maker Not In (Select Vision_Id From Vision_Users) " + " Union "
						+ " Select Trim(To_Char(Line_Number)), Trim(To_Char(T1.Maker)) From Fin_Adj_Headers_Upl T1, Vision_Users T2 "
						+ " Where Action_Id = '" + actionId + "' " + " And T1.Maker = T2.Vision_Id "
						+ " And T2.Update_Restriction = 'Y' "
						+ " And (T1.Country != Nvl(T2.Country, 'XX') Or T1.Le_Book != Nvl(T2.Le_Book, 'XX'))";
				errorMsg = "The following Maker Ids are invalid";
				logWriter("\n" + errorMsg, uploadLogFileName);
				displayAbortErrorsForDC(sqlStatement, errorMsg);
				abortFlag = 'Y';
			}
			/* Financials_Daily flag check */
			/*
			 * recsCount = 0; try{ rs = stmt.executeQuery(
			 * "Select Count(1) From Fin_Adj_Headers_Upl T1 " +
			 * " Where Action_Id = '"+actionId+ "' And Financials_Daily Not In ('Y', 'N')");
			 * while(rs.next()) recsCount = rs.getInt(1);
			 * 
			 * logWriter("\n Fin_Adj_Headers_Upl 9 CNT is : [" + recsCount + "]"); }
			 * catch(SQLException e){ sqlLogWriter(
			 * "Error trying to count for Data_Type abort condition validation !!"
			 * +e.getMessage()); return(ERRONEOUS_EXIT); } if (recsCount != 0) {
			 * sqlStatement =
			 * "Select Trim(To_Char(Line_Number)), Financials_Daily From Fin_Adj_Headers_Upl T1, "
			 * + " Where Action_Id = '" + actionId + "' " +
			 * " And Financials_Daily Not In ('Y', 'N')"; errorMsg =
			 * "The following Financials_Daily flag values are invalid";
			 * logWriter("\n"+errorMsg); displayAbortErrorsForDC(sqlStatement,errorMsg);
			 * abortFlag = 'Y'; }
			 */
			/* Financials_Daily flag check */
			/*
			 * recsCount = 0; try{ rs = stmt.executeQuery(
			 * "Select Count(1) From Fin_Adj_Headers_Upl T1 " + " Where Action_Id = '" +
			 * actionId + "' And Financials_Daily Not In ('Y', 'N')"); while(rs.next())
			 * recsCount = rs.getInt(1);
			 * 
			 * logWriter("\n Fin_Adj_Headers_Upl 10 CNT is : [" + recsCount + "]"); }
			 * catch(SQLException e){ sqlLogWriter(
			 * "Error trying to count for Data_Type abort condition validation !!"
			 * +e.getMessage()); return(ERRONEOUS_EXIT); } if (recsCount != 0) {
			 * sqlStatement =
			 * "Select Trim(To_Char(Line_Number)), Financials_Daily From Fin_Adj_Headers_Upl T1, "
			 * + " Where Action_Id = '" + actionId + "' " +
			 * " And Financials_Daily Not In ('Y', 'N')";
			 * 
			 * errorMsg = "The following Financials_Daily flag values are invalid";
			 * logWriter("\n"+errorMsg); displayAbortErrorsForDC(sqlStatement,errorMsg);
			 * abortFlag = 'Y'; }
			 */
			/*
			 * Fin_Dly_End_Date check - should be populated only if Financials_Daily = 'Y'
			 */

			/*
			 * recsCount = 0; try{ rs = stmt.executeQuery(
			 * "Select Count(1) from Fin_Adj_Headers_Upl T1" + " Where Action_Id = '" +
			 * actionId + "' And " +
			 * " ((Fin_Dly_End_Date Is Null And Financials_Daily = 'Y')" +
			 * " Or (Fin_Dly_End_Date Is Not Null And Financials_Daily = 'N')" +
			 * " Or (Fin_Dly_End_Date Is Not Null And Fin_Dly_End_Date Not Between To_Date('"
			 * +firstDay+ "') And To_Date('" +reportingDate+ "')))"); while(rs.next())
			 * recsCount = rs.getInt("Count(1)");
			 * 
			 * logWriter("\n Fin_Adj_Headers_Upl 11 CNT is : [" + recsCount + "]"); }
			 * catch(SQLException e){ sqlLogWriter(
			 * "Error trying to count for Country + Le_Book existence in Le_Book table abort condition validation !!"
			 * +e.getMessage()); return(ERRONEOUS_EXIT); } if (recsCount != 0) {
			 * sqlStatement = "Select Count(1) from Fin_Adj_Headers_Upl T1 " +
			 * " Where Action_Id = '" + actionId + "' " +
			 * " ((Fin_Dly_End_Date Is Null And Financials_Daily = 'Y') " +
			 * " Or (Fin_Dly_End_Date Is Not Null And Financials_Daily = 'N') " +
			 * " Or (Fin_Dly_End_Date Is Not Null And Fin_Dly_End_Date Not Between To_Date('"
			 * + firstDay + "') And To_Date('" + reportingDate + "'))) ";
			 * 
			 * errorMsg = "The following Fin_Dly_End_Dates are invalid";
			 * logWriter("\n"+errorMsg); displayAbortErrorsForDC(sqlStatement,errorMsg);
			 * abortFlag = 'Y'; } if (abortFlag == 'Y') {
			 * logWriter("\nAborting upload......"); return(ERRONEOUS_EXIT); }
			 */

			/* End of checks for abort conditions */

			/*
			 * Now for implementing the validations & business rules, as we have
			 * successfully uploaded the data for the tables Fin_Adj_Headers_UPL,
			 * Fin_Adj_Details_UPL & Fin_Adj_Postings_UPL Functions. Call the procedure that
			 */

			/*
			 * TO DO - To call the procedure that does the validations for the records that
			 * were inserted into the UPL tables - While calling the validate function -
			 * just ensure that the Parameter for populating the Fin_Adj_Errors table is
			 * also being supplied.
			 */

			/*
			 * Intialize the variables that are required to call the procedure
			 */
			status = "";
			category = "";
			errorMsg = "";
			successCnt = 0;
			failCnt = 0;

			logWriter("Validating upload file data......\n", uploadLogFileName);
			con.commit();
			// prepareMailString();
			try {
				callableStatement = con.prepareCall("{call PR_VALIDATE_FIN_DLY_ADJ (?,?,?,?,?,?,?,?)}");

				callableStatement.setString(1, actionId);
				callableStatement.setString(2, "N");
				callableStatement.setString(3, "N");
				// callableStatement.setString(4,"N");
				// callableStatement.setInt(5,makerId);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR); // Status
																					// (C-Completed,E-Error,P-PartialCompleted(warning),F-Fatal(if
																					// table
																					// not
																					// available))
				callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR); // Category
																					// (T-Trigger
																					// error,V-Validation
																					// Error)
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR); // Error
																					// Message
				callableStatement.registerOutParameter(7, java.sql.Types.INTEGER); // Success
																					// Count
				callableStatement.registerOutParameter(8, java.sql.Types.INTEGER); // Failure
																					// Count
				callableStatement.executeUpdate();
				status = callableStatement.getString(4);

				category = callableStatement.getString(5);
				errorMsg = callableStatement.getString(6);
				successCnt = callableStatement.getInt(7);
				failCnt = callableStatement.getInt(8);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error calling procedure PR_VALIDATE_FIN_DLY_ADJ to validate input data :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			/*
			 * Commit now, since we want the insertions made into the
			 * Fin_Dly_Adj_Validation_Errors tables, to be permanent
			 */
			con.commit();

			logWriter(" ", uploadLogFileName);

			if (status.equalsIgnoreCase("F")) {
				logWriter(
						"A fatal error occured while trying to validate the procedure. Aborting further processing !!",
						uploadLogFileName);
				logWriter("Message got from the database ....", uploadLogFileName);
				errorMsg = errorMsg + " !!!!!!\n\n";
				logWriter(errorMsg, uploadLogFileName);
				return (STATUS_FAILURE);
			} else if (status.equalsIgnoreCase("E")) {
				logWriter("All input records failed validation. Aborting further processing !!", uploadLogFileName);
				logWriter(" ", uploadLogFileName);
				logWriter("Final Summary report for DC Group Upload : Failed !!!!!!", uploadLogFileName);
				logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
						uploadLogFileName);
				logWriter("Total Success records : [" + successCnt + "]", uploadLogFileName);
				logWriter("Total Failure records : [" + failCnt + "]", uploadLogFileName);
				logWriter("ErrorMsg : [" + errorMsg + "]\n", uploadLogFileName);
				logWriter(" ", uploadLogFileName);
				/* printDcUploadErrorReport(); */

				// return(STATUS_FAILURE);
				retVal = updateUploadStatus(STATUS_SUCCESS_VALIDATION_FAILED);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}
				con.commit();
				logWriter("One or more of the input records have failed validation. ", uploadLogFileName);
				finalReturnValue = STATUS_SUCCESS_VALIDATION_FAILED;
			} else if (status.equalsIgnoreCase("P")) {
				retVal = updateUploadStatus(STATUS_PARTIAL_SUCCESS);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}
				con.commit();
				logWriter("One or more of the input records have failed validation. ", uploadLogFileName);
				finalReturnValue = STATUS_PARTIAL_SUCCESS;
			} else if (status.equalsIgnoreCase("C") || status
					.equalsIgnoreCase("W")) /*
											 * Perform the same operation for successful completion & Warning
											 */
			{
				retVal = updateUploadStatus(STATUS_SUCCESS);
				if (retVal == ERRONEOUS_EXIT) {
					return (ERRONEOUS_EXIT);
				}

				con.commit();
				logWriter("Input records have passed thru Validation.", uploadLogFileName);
				logWriter("Records Inserted/Updated in the Posting Tables - FYI\n", uploadLogFileName);

				/*
				 * retVal = sendValidaterEmailId(); if (retVal == ERRONEOUS_EXIT) { return
				 * (ERRONEOUS_EXIT); }
				 */
			}

			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter("Total Success records : [" + successCnt + "]", uploadLogFileName);
			logWriter("Total Failure records : [" + failCnt + "]\n", uploadLogFileName);
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter(" ", uploadLogFileName);

			/*
			 * TO DO Once the validations have succeeded, then Call the function/procedure
			 * to insert into the respective Fin_Adj_Headers, Fin_Adj_Details &
			 * Fin_Adj_Postings Tables
			 */
			return (finalReturnValue);
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("DCUpload():" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private void printDailyDcUploadErrorReport() {
		String prevTableType = "";
		int localLineNumber = 0;
		char firstTimeFlag = 'Y';
		int recCount = 0;
		String tempStr = "";
		try {
			rs = stmt.executeQuery(
					"Select Table_Flag, Line_Number, Country, Le_Book, Year_Month, Adj_Source, Adj_Reference, Adj_Id, Sequence_Fa, "
							+ " Error_Desc, Error_Type, Severity From FIN_DLY_ADJ_VALIDATION_ERRORS Where Action_Id = '"
							+ actionId + "' " + " Order By Table_Flag Desc, Line_Number, Error_Type ");
			recCount = rs.getRow();
			while (rs.next()) {
				tableTypelst.add(rs.getString("Table_Flag"));
				lineNumberlst.add(rs.getInt("Line_Number"));
				countrylst.add(rs.getString("Country"));
				leBooklst.add(rs.getInt("Le_Book"));
				yearMonthlst.add(rs.getInt("Year_Month"));
				adjSourcelst.add(rs.getString("Adj_Source"));
				adjReferencelst.add(rs.getString("Adj_Reference"));
				adjIdlst.add(rs.getInt("Adj_Id"));
				sequenceFalst.add(rs.getInt("Sequence_Fa"));
				errorDescriptionlst.add(rs.getString("Error_Desc"));
				errorTypelst.add(rs.getString("Error_Type"));
				severitylst.add(rs.getString("Severity"));

			}
		} catch (SQLException e) {
			sqlLogWriter(
					"Error preparing to Select from the FIN_DLY_ADJ_VALIDATION_ERRORS table for displaying a summary of errors encountered during the upload process :"
							+ e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return;
		}
		prevTableType = "X";

		for (int Ctr = 0; Ctr < recCount; Ctr++) {
			lineNumber = (Integer) lineNumberlst.get(Ctr);
			tableType = (String) tableTypelst.get(Ctr);
			errorDescription = (String) errorDescriptionlst.get(Ctr);
			errorType = (String) errorTypelst.get(Ctr);
			severity = (String) severitylst.get(Ctr);
			adjSource = (String) adjSourcelst.get(Ctr);
			adjReference = (String) adjReferencelst.get(Ctr);
			country = (String) countrylst.get(Ctr);
			leBook = (String) leBooklst.get(Ctr);
			yearMonth = (Integer) yearMonthlst.get(Ctr);
			adjId = (Integer) adjIdlst.get(Ctr);
			sequenceFa = (Integer) sequenceFalst.get(Ctr);

			/*
			 * printf("\n\n Point 1 tableType [%s]... ",tableType);
			 * fflush(stdout);fflush(stdin);getchar();
			 */

			if (prevTableType != tableType) {
				logWriter(" ", uploadLogFileName);

				if (tableType.equalsIgnoreCase("H"))
					logWriter("FIN_DLY_ADJ_HEADERS Errors", uploadLogFileName);
				else if (tableType.equalsIgnoreCase("D"))
					logWriter("FIN_DLY_ADJ_DETAILS Errors", uploadLogFileName);
				else
					logWriter("FIN_DLY_ADJ_POSTINGS Errors", uploadLogFileName);

				logWriter("----------------------------------------------------------------------------------------",
						uploadLogFileName);

				if (tableType.equalsIgnoreCase("P")) {
					logWriter(
							"Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Adj_Id Sequence_Fa Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ------ ----------- ----------   --------------------------",
							uploadLogFileName);

				} else if (tableType.equalsIgnoreCase("H")) {
					logWriter(
							"Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Adj_Id Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ------ ----------   --------------------------",
							uploadLogFileName);
				} else {
					logWriter("Line # \tCountry Le_Book Year_Month Adj_Source Adj_Reference Error Type   Description",
							uploadLogFileName);
					logWriter(
							"------ \t------- ------- ---------- ---------- ------------- ----------   --------------------------",
							uploadLogFileName);
				}

				firstTimeFlag = 'Y';
				prevTableType = tableType;
			}
			if (localLineNumber != lineNumber) {
				if (firstTimeFlag != 'Y')
					logWriter(" ", uploadLogFileName);

				firstTimeFlag = 'N';
				localLineNumber = lineNumber;

				if (tableType.equalsIgnoreCase("P")) {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjId + " " + sequenceFa + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else if (tableType.equalsIgnoreCase("H")) {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else {
					tempStr = "" + lineNumber + "\t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				}
			} else {
				if (tableType.equalsIgnoreCase("P")) {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + sequenceFa + " "
							+ (errorType == "T" ? "Trigger" : "Validation") + " " + errorDescription;
				} else if (tableType.equalsIgnoreCase("D")) {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + adjId + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				} else {
					tempStr = "       \t" + country + " " + leBook + " " + yearMonth + " " + adjSource + " "
							+ adjReference + " " + (errorType == "T" ? "Trigger" : "Validation") + " "
							+ errorDescription;
				}
			}

			logWriter(tempStr, uploadLogFileName);
		}
	}

	private void cleanupDailyDcUPLTableEntries() {
		try {
			stmt.executeUpdate("Delete From Fin_Dly_Adj_Headers_UPL Where Action_Id = '" + actionId + "'");
			stmt.executeUpdate("Delete From Fin_Dly_Adj_Details_UPL Where Action_Id = '" + actionId + "'");
			stmt.executeUpdate("Delete From Fin_Dly_Adj_Postings_UPL Where Action_Id = '" + actionId + "'");
			con.commit();
		} catch (SQLException e) {
//			System.out.println("cleanupDailyDcUPLTableEntries :" + e.getMessage());
			e.printStackTrace();
		}
	}

	private int unlockDailyDCTablesAfterUpload() {
		try {
			stmt.executeUpdate(
					"Update Vision_Locking Set Lock_Status = 'N' Where Table_Name In ('FIN_DLY_ADJ_HEADERS', 'FIN_DLY_ADJ_DETAILS', 'FIN_DLY_ADJ_POSTINGS')");
			lockFlag = 'N';
			con.commit();
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			writeSQLErrToMainLogFile(
					"Error trying to unlock Daily DC tables for XL Upload [" + uploadTableName + "]:" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int findIfUploadAllowedForDailyDCTbls() {
		char abortFlag = 'N';
		int Ctr1 = 0;
		totalGroupUploadTables = 0;
		int recCounttmp = 0;
		try {
			rs = stmt.executeQuery("Select Table_Name, Upload_Allowed From Vision_Tables "
					+ " Where Upper(Table_Name) In ('FIN_DLY_ADJ_HEADERS', 'FIN_DLY_ADJ_DETAILS', 'FIN_DLY_ADJ_POSTINGS') ");
			while (rs.next()) {
				tableNamelst.add(rs.getString("Table_Name"));
				uploadAllowedlst.add(rs.getString("Upload_Allowed"));
				recCounttmp++;
			}
			for (Ctr1 = 0; Ctr1 < recCounttmp; Ctr1++) {
				if (valueNo.equalsIgnoreCase(uploadAllowedlst.get(Ctr1).toString())) {
					writeToMainLogFile("Upload is not allowed for Table [" + tableNamelst.get(Ctr1).toString() + "].");
					abortFlag = 'Y';
				}
			}
			if (Ctr1 != 3) {
				logWriter("One or more of the tables for this upload are not present in Vision_Tables table !!",
						uploadLogFileName);
				logWriter("Aborting further upload \n", uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			if (abortFlag == 'Y')
				return (ERRONEOUS_EXIT);

			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error preparing to select from Vision_Tables for validating Upload_Allowed status for Daily DC tables :\n"
							+ "SQL Code [" + e.getErrorCode() + "], Message [" + e.getMessage() + "]\n");
			writeToMainLogFile("Query is");
			writeToMainLogFile(sqlStatement);
			return (ERRONEOUS_EXIT);

		}
	}

	private int checkForDailyDCTablesLock() {
		recsCount = 0;
		try {
			rs = stmt.executeQuery(" Select Count(1) From Vision_Locking "
					+ " Where Table_Name In ('FIN_DLY_ADJ_HEADERS', 'FIN_DLY_ADJ_DETAILS', 'FIN_DLY_ADJ_POSTINGS') "
					+ " And Lock_Status = 'Y'");
			while (rs.next())
				recsCount = rs.getInt("Count(1)");
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error trying to fetch lock status from VISION_LOCKING table for [Fin_Dly_Adj_Headers, Fin_Dly_Adj_Details & Fin_Dly_Adj_Postings]:"
							+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		/* If count is not zero, then the concerned table has been locked */
		if (recsCount != 0)
			return (ERRONEOUS_EXIT);

		return (SUCCESSFUL_EXIT);
	}

	private int lockDailyDCTablesForUpload() {
		try {
			stmt.executeUpdate("Update Vision_Locking Set Lock_Status = 'Y' "
					+ " Where Table_Name In ('FIN_DLY_ADJ_HEADERS', 'FIN_DLY_ADJ_DETAILS', 'FIN_DLY_ADJ_POSTINGS')");

			con.commit();
			lockFlag = 'Y';
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			writeToMainLogFile(
					"Error trying to Lock Daily DC tables for XL Upload [" + uploadTableName + "]:" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int checkForExistenceOfDailyDcTbls() {
		try {
			rs = stmt.executeQuery(" Select Count(1) From ALL_TABLES "
					+ " Where Table_Name In ('FIN_DLY_ADJ_HEADERS', 'FIN_DLY_ADJ_DETAILS', 'FIN_DLY_ADJ_POSTINGS') AND UPPER(OWNER)=UPPER('"
					+ username + "')");

			while (rs.next())
				recsCount = rs.getInt("Count(1)");

			if (recsCount != 3) {
				logWriter(
						"One or more of the tables for Daily DC Upload (Fin_Dly_Adj_Headers/Fin_Dly_Adj_Details/Fin_Dly_Adj_Postings) "
								+ " are not found in the system !!\nPlease create the tables and re-run this upload\n",
						uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			return (SUCCESSFUL_EXIT);
		} catch (SQLException e) {
			sqlLogWriter("Error trying to validate presence of upload table :" + e.getMessage());
			return (ERRONEOUS_EXIT);
		}
	}

	private int uploadDailyDCFileData() {
		try {
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			char ch;
			int lineCtr = 1;
			String tempStr = "";
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			totalInputFileRecords = 0;
			retVal = SUCCESSFUL_EXIT;
			adjSource = "";
			adjSource = "'" + DLY_DC_ADJ_SOURCE + "'";
			sourceId = DLY_DC_SOURCE_ID;
			recordType = DLY_DC_RECORD_TYPE;

			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = null;
			String[] wordsArray = null;
			/* Workbook workbook = new XSSFWorkbook(fileReader1); */

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String specificData = "";
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							String columnSpecificDataType = (String) dateTypelst.get(Ctr2);
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							} else if (cellType == CellType.STRING) {
//								if (cellType == Cell.CELL_TYPE_STRING) {
								specificData = cell.getStringCellValue();
							}else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) { 
//							else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										} catch (Exception e) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										}
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}

								}

							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = (lineFetched == null ? "" : lineFetched)
									+ specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							specificData = specificData.replaceAll("\r", " ");
							specificData = specificData.replaceAll("\n", " ");
							specificData = specificData.replaceAll("\t", " ");
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
						}
					}
					if (lineFetched == null) {
						break;
					} else {
						wordsArray = lineFetched.split("\n");
						lineFetched = "";
						for (String each : wordsArray) {
							lines.add(each);
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();
			for (Ctr1 = 0; Ctr1 < lines.size(); Ctr1++) {

				uploadData = lines.get(Ctr1).toString().split("\t");
				for (Ctr2 = 0; Ctr2 < uploadData.length; Ctr2++) {
					if (valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr2).toString()))
						uploadData[Ctr2] = checkAndProcessQuotes(uploadData[Ctr2]);
				}
				lineCtr++;

				retVal = doInsertUpdateOpsForDailyDCUpload(lineCtr);

				if (retVal == ERRONEOUS_EXIT) {
					retVal = ERRONEOUS_EXIT;
					break;
				}
				totalInputFileRecords++;
			}
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("uploadDCFileData():" + e.getMessage());
			retVal = ERRONEOUS_EXIT;
		}
		return (SUCCESSFUL_EXIT);
	}

	private int doInsertUpdateOpsForDailyDCUpload(int paramLineCtr) {
		int Ctr1 = 0, Ctr2 = 0;
		String tempStr = "";
		int localRetVal = 0;
		char newHeadersFlag = 'N';
		char newDetailsFlag = 'N';
		char newPostingsFlag = 'N';
		char commaFlag = 'N';

		retVal = SUCCESSFUL_EXIT;

		/*
		 * Load the contents of the upload file into memory, those fields that are part
		 * of the virtual keys, for use later, in identifying existing records in
		 * Fin_Dly_Balances & Fin_Dly_Adj_Postings table
		 */
		retVal = loadVirtualKeyFieldsForDailyDC();
		if (retVal == ERRONEOUS_EXIT) {
			return (ERRONEOUS_EXIT);
		}
		/*
		 * Frame an insert statement into the corresponding UPL tables of Headers,
		 * Details & Postings Tables and then commence the operations
		 */
		insertStmt = "Insert Into Fin_Dly_Adj_Headers_Upl (Country, Le_Book, Year_Month, "
				+ " Adj_Source, Adj_Reference, Data_Type, FA_Reason_Code, Supplement, "
				+ " Originating_Ouc, Fin_Dly_Adj_Headers_Status, Maker, Verifier, Internal_Status, "
				+ " Record_Indicator, Date_Creation, Date_Last_Modified, New_Record_Flag, "
				+ " Bulk_Update_Flag, Action_Id, Line_Number) Values (" + uploadData[IDX_DLY_DCUPLOAD_COUNTRY] + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_LE_BOOK], IDX_DLY_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_YEAR_MONTH], IDX_DLY_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr + ",";

		tempStr = "";
		tempStr = adjSource;
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ADJ_REFERENCE], IDX_DLY_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		adjReference = uploadData[IDX_DLY_DCUPLOAD_ADJ_REFERENCE];

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_DATA_TYPE], IDX_DLY_DCUPLOAD_DATA_TYPE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_FA_REASON_CODE], IDX_DLY_DCUPLOAD_FA_REASON_CODE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_SUPPLEMENT], IDX_DLY_DCUPLOAD_SUPPLEMENT);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ORIGINATING_OUC], IDX_DLY_DCUPLOAD_ORIGINATING_OUC);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", 10";

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + REC_IND_APPROVED + ", "
				+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + "";

		/*
		 * Before inserting into the UPL table, check to see if the record already
		 * exists in the Fin_Dly_Adj_Headers table. If so, we must appropriately update
		 * New_Record_Flag in the UPL table.
		 */
		Boolean dataTypeFlag = true;
		try {
			rs = stmt.executeQuery(
					" Select Trim(Nvl(Data_Type, 'XX')) Data_Type " + " From Fin_Dly_Adj_Headers Where Country = "
							+ country + "" + " And Le_Book = " + leBook + " And Year_Month = " + yearMonth + ""
							+ " And Adj_Source = " + adjSource + " And Adj_Reference = " + adjReference + "");

			if (rs.next()) {
				tempDataType = rs.getString("Data_Type");
			} else {
				dataTypeFlag = false;
			}
		} catch (SQLException e) {
			logWriter("Error checking existing record from Fin_Dly_Adj_Headers table fsd :" + e.getMessage(),
					uploadLogFileName);
			sqlLogWriter(e.getMessage() + "!!Values used for the query were Country [" + country + "], Le_Book ["
					+ leBook + "], Year_Month [" + yearMonth + "], Adj_Source [" + adjSource + "], Adj_Reference ["
					+ adjReference + "]\n");
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
		if (!dataTypeFlag) {
			newHeadersFlag = 'Y';
			if (uploadData[IDX_DLY_DCUPLOAD_DATA_TYPE].equals("RE")) {
				logWriter(
						"" + appendZeroforCtr(paramLineCtr)
								+ "\t\tFin_Adj_Headers_Upl Insert Failed - Insert not allowed for Data_Type = RE",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			}
		} else {
			newHeadersFlag = 'N';
			if (tempDataType.equals("RE") || uploadData[IDX_DLY_DCUPLOAD_DATA_TYPE].equals("RE")) {
				logWriter(
						"" + appendZeroforCtr(paramLineCtr)
								+ "\t\tFin_Dly_Adj_Headers_Upl Insert Failed - Insert not allowed for Data_Type = RE",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			}
		}
		/*
		 * If mandatory fields are mis-match between one upload file line and the other
		 * it is an error
		 */
		Boolean sqlFound = true;
		try {
			rs = stmt.executeQuery("Select Data_Type, Fa_Reason_Code, Supplement, Originating_Ouc "
					+ " From Fin_DLY_Adj_Headers_Upl Where Action_Id = '" + actionId + "' " + " And Country = "
					+ country + " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth
					+ " And Adj_Source = " + adjSource + "" + " And Adj_Reference = " + adjReference + "");

			if (rs.next()) {
				compDataType = rs.getString("Data_Type");
				compFaReasonCode = rs.getString("Fa_Reason_Code");
				compSupplement = rs.getString("Supplement");
				compOriginatingOuc = rs.getString("Originating_Ouc");
			} else {
				sqlFound = false;
			}
		} catch (SQLException e) {
			logWriter("Error checking existing record from Fin_Dly_Adj_Headers table :" + e.getMessage(),
					uploadLogFileName);
			sqlLogWriter(e.getMessage() + "!!Values used for the query were Country [" + country + "], Le_Book ["
					+ leBook + "], Year_Month [" + yearMonth + "], Adj_Source [" + adjSource + "], Adj_Reference ["
					+ adjReference + "]\n");
			finalReturnValue = ERRONEOUS_EXIT;
			return (ERRONEOUS_EXIT);
		}
		if (sqlFound) {
			if (compDataType.equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_DATA_TYPE])
					|| compSupplement.equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_SUPPLEMENT])
					|| compOriginatingOuc.equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_ORIGINATING_OUC])
					|| compFaReasonCode.equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_FA_REASON_CODE].toString())) {
				logWriter(
						"" + appendZeroforCtr(paramLineCtr)
								+ "\t\tFin_Dly_Adj_Headers_Upl Insert Failed - Inconsistent header details",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			} else {
			}
		}
		insertStmt = insertStmt + ", '" + newHeadersFlag + "', 'N', '" + actionId + "', " + paramLineCtr + ")";

		/* Attempt an insert operation to Fin_Adj_Headers_Upl Table now */
		try {
			stmt.executeUpdate(insertStmt);
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Headers_Upl  Insert succeeded",
					uploadLogFileName);
			totalSuccessRecords++;
		} catch (SQLException e) {
			if (!e.getMessage().contains("unique constraint")) {
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Headers_Upl Insert Failed :"
						+ e.getMessage());
				logWriter(insertStmt, uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
			} else {
				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Headers_Upl  Insert succeeded :"
						+ e.getMessage(), uploadLogFileName);
				totalFailureRecords++;
			}
		}

		sequenceFm = 0;
		adjId = 0;
		sequenceFa = 0;

		customerId = uploadData[IDX_DLY_DCUPLOAD_CUSTOMER_ID];
		creditLine = uploadData[IDX_DLY_DCUPLOAD_CREDIT_LINE];
		originatingOuc = uploadData[IDX_DLY_DCUPLOAD_ORIGINATING_OUC];
		officeAccount = uploadData[IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT];
		Boolean dataFoundFlag = false;
		try {
			rs = stmt.executeQuery("Select T1.Adj_Id, T1.Sequence_FD, T1.Sequence_Fa "
					+ " From Fin_Dly_Adj_Postings T1, Fin_Dly_Adj_Details T2 " + " Where T1.Adj_Reference = "
					+ adjReference + " " + " And T1.Country       = " + country + "" + " And T1.Le_Book = " + leBook
					+ "" + " And T1.Year_Month      = " + yearMonth + "" + " And T1.Gl_Enrich_Id    = " + glEnrichId
					+ "" + " And T1.Bs_Gl           = " + bsGl + "" + " And T1.Pl_Gl           = " + plGl + ""
					+ " And T1.Currency        = " + currency + "" + " And T1.Mis_Currency    = " + misCurrency + ""
					+ " And T1.Contract_Id     = " + contractId + "" + " And T1.Office_Account  = " + officeAccount + ""
					+ " And T1.Customer_Id     = " + customerId + "" + " And T1.Vision_Ouc       = " + visionOuc + ""
					+ " And T1.Cost_Center     = " + costCenter + "" + " And T1.Vision_Sbu       = " + visionSbu + ""
					+ " And T1.Account_Officer = " + accountOfficer + "" + " And T1.Source_Id       = " + sourceId + ""
					+ " And T1.Original_Ouc    = " + originalOuc + "" + " And T2.Fin_DLY_Adj_Details_Status != 40	"
					+ " And t1.Country         = T2.Country" + " And T1.Le_Book         = T2.Le_Book"
					+ " And T1.Year_Month      = T2.Year_Month" + " And T1.Adj_Source      = T2.Adj_Source"
					+ " And T1.Adj_Reference   = T2.Adj_Reference" + " And T1.Adj_Id          = T2.Adj_Id");

			if (rs.next()) {
				adjId = rs.getInt("Adj_Id");
				sequenceFm = rs.getInt("Sequence_FD");
				sequenceFa = rs.getInt("Sequence_Fa");
			}
		} catch (SQLException e) {
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr)
					+ "\t\tError occured while trying to obtain Adj_Id From Postings for the given data :"
					+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		if (!dataFoundFlag) {
			sequenceFa = 0;
			adjId = 0;
			newDetailsFlag = 'Y';
			newPostingsFlag = 'Y';
		} else {
			newDetailsFlag = 'N';
			if (sequenceFa == 0) {
				newPostingsFlag = 'Y';
				sequenceFa = 1;
			} else
				newPostingsFlag = 'N';
		}
		try {
			insertStmt = "Insert Into Fin_Dly_Adj_Details_Upl(Country, Le_Book, "
					+ " Year_Month, Adj_Source, Adj_Reference, Adj_Id,  "
					+ " Override_Imbalance, Batch_Total_Amount, Postings_Count, "
					+ " Validation_Date, Posted_Date, Fin_Dly_Adj_Details_Status, "
					+ " Maker, Verifier, Internal_Status, " + " Record_Indicator, Date_Creation, Date_Last_Modified, "
					+ " New_Record_Flag, Bulk_Update_Flag, Action_Id, Line_Number) Values ("
					+ uploadData[IDX_DLY_DCUPLOAD_COUNTRY] + "";

			if (adjId == 0) {
				int tmpCount = 0;
				rs = stmt.executeQuery(" Select Adj_Id From Fin_Dly_Adj_Details " + " Where Country = " + country + ""
						+ " And Le_Book = " + leBook + "" + " And Year_Month = " + yearMonth + ""
						+ " And Adj_Reference = " + adjReference + "" + " And Fin_Dly_Adj_Details_Status != 40");

				while (rs.next()) {
					adjId = rs.getInt("Adj_Id");
					tmpCount++;
				}

				if (tmpCount == 0) {
					adjId = 0;
				} else
					newDetailsFlag = 'N';
			}
		} catch (SQLException e) {
			if (e.getErrorCode() != SQL_NO_ERRORS && e.getErrorCode() != SQL_NOT_FOUND) {
				sqlLogWriter("Error trying to fetch unauth. Adj_Id from Fin_Dly_Adj_Details table :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
		}
		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_LE_BOOK], IDX_DLY_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_YEAR_MONTH], IDX_DLY_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr + ",";

		tempStr = "";
		tempStr = "'" + DLY_DC_ADJ_SOURCE + "'";
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ADJ_REFERENCE], IDX_DLY_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjId + ", 'N', 0, 0, NULL, NULL, 10";

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + REC_IND_APPROVED + ", "
				+ getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ", '" + newDetailsFlag + "', 'N', '"
				+ actionId + "', " + paramLineCtr + ")";

		try {
			stmt.executeUpdate(insertStmt);
			// logWriter(" \t\tFin_Adj_Details_Upl Insert succeeded");
			logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Details_Upl  Insert succeeded",
					uploadLogFileName);
			totalSuccessRecords++;
		} catch (SQLException e) {
			if (!e.getMessage().contains("unique constraint")) {
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + " \t\tFin_Dly_Adj_Details_Upl Insert Failed :"
						+ e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				totalFailureRecords++;
			} else {
				logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Details_Upl  Insert succeeded :"
						+ e.getMessage(), uploadLogFileName);
				totalFailureRecords++;
			}
		}
		/*
		 * Before inserting into the Postings UPL table, see if there is a valid
		 * Sequence_Fm associated with the postings being inserted
		 */
		if (sequenceFm == 0) {
			sequenceFm = -1;
			try {
				rs = stmt.executeQuery("Select Nvl(Min(Sequence_Fd), -1) " + " From Fin_Dly_Balances T1 "
						+ " where T1.Country       = " + country + "" + " And T1.Le_Book = " + leBook + ""
						+ " And T1.Year_Month      = " + yearMonth + " ");
				while (rs.next())
					sequenceFm = rs.getInt(1);
			} catch (SQLException e) {
				sqlLogWriter(appendZeroforCtr(paramLineCtr)
						+ "\t\tError fetching the Sequence_Fd from Fin_Dly_Balances table :" + e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				return (ERRONEOUS_EXIT);
			}
		}
		if (sequenceFm == -1) {
			indic1 = NULL_VALUE;
			sequenceFm = 0;
		} else {
			/*
			 * try{ rs = stmt.executeQuery("Select Record_Type, Source_Id " +
			 * " From Fin_Mth_Balances Where Country = " +country+ " And Le_Book = "
			 * +leBook+ "'" + " And Year_Month = " +yearMonth+ " And Sequence_Fm = "
			 * +sequenceFm+ "");
			 * 
			 * while(rs.next()){ finMthRecordType = rs.getInt("Record_Type"); finMthSourceId
			 * = rs.getInt("Source_Id"); } }catch(SQLException e){ sqlLogWriter(
			 * "Error fetching Record_Type & Source_ID from Fin_MTh_Balance table !!" );
			 * return(ERRONEOUS_EXIT); } recordType = finMthRecordType; sourceId =
			 * finMthSourceId;
			 */
		}

		/*
		 * At this point, appropriately, the Fin_Dly_Adj_Postings_UPL record has to be
		 * inserted
		 */

		/*
		 * Prepare the Update statement also, as it would be useful later, if the same
		 * postings record were to be detected in the input file
		 */

		insertStmt = "Insert Into Fin_Dly_Adj_Postings_Upl(Country, Le_Book, Year_Month, Adj_Source, Sequence_FD, "
				+ " Adj_Reference, Adj_Id, Sequence_FA, Value_Date, "
				+ " Customer_ID, Gl_Enrich_Id, Contract_ID, Office_Account, "
				+ " BS_GL, PL_GL, Vision_OUC, Vision_SBU, Account_Officer, Cost_Center, "
				+ " Currency, Mis_Currency, Accrual_Status, Credit_Line, "
				+ " Source_Id, Record_Type, Original_Ouc, Financial_Attribute_1, Financial_Attribute_2, "
				+ " Financial_Attribute_3, Source_Flag, BAL_TYPE, TRANS_DATE, TRANS_AMOUNT, Maker, "
				+ " Verifier, Internal_Status, Date_Last_Modified, Date_Creation, "
				+ " New_Record_Flag, Bulk_Update_Flag, Action_Id, Line_Number) Values ("
				+ uploadData[IDX_DLY_DCUPLOAD_COUNTRY] + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_LE_BOOK], IDX_DLY_DCUPLOAD_LE_BOOK);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_YEAR_MONTH], IDX_DLY_DCUPLOAD_YEAR_MONTH);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjSource + "";

		/*
		 * For Now, enter an Empty Sequence_Fm for this entry. We shall update at a
		 * later point
		 */
		insertStmt = insertStmt + ", " + sequenceFm + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ADJ_REFERENCE], IDX_DLY_DCUPLOAD_ADJ_REFERENCE);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + adjId + ", " + sequenceFa + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_VALUE_DATE], IDX_DLY_DCUPLOAD_VALUE_DATE);
		tempStr = ", " + "To_Date(" + tempStr.replace(", ", "") + ", 'DD/MM/YYYY')";
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_CUSTOMER_ID], IDX_DLY_DCUPLOAD_CUSTOMER_ID);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_GL_ENRICH_ID], IDX_DLY_DCUPLOAD_GL_ENRICH_ID);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_CONTRACT_ID], IDX_DLY_DCUPLOAD_CONTRACT_ID);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT], IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_BS_GL], IDX_DLY_DCUPLOAD_BS_GL);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_PL_GL], IDX_DLY_DCUPLOAD_PL_GL);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_VISION_OUC], IDX_DLY_DCUPLOAD_VISION_OUC);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_VISION_SBU], IDX_DLY_DCUPLOAD_VISION_SBU);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ACCOUNT_OFFICER], IDX_DLY_DCUPLOAD_ACCOUNT_OFFICER);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_COST_CENTER], IDX_DLY_DCUPLOAD_COST_CENTER);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_CURRENCY], IDX_DLY_DCUPLOAD_CURRENCY);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_MIS_CURRENCY], IDX_DLY_DCUPLOAD_MIS_CURRENCY);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ACCRUAL_STATUS], IDX_DLY_DCUPLOAD_ACCRUAL_STATUS);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_CREDIT_LINE], IDX_DLY_DCUPLOAD_CREDIT_LINE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_SOURCE_ID], IDX_DLY_DCUPLOAD_SOURCE_ID);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + recordType + "";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_ORIGINATING_OUC], IDX_DLY_DCUPLOAD_ORIGINATING_OUC);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_1],
				IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_1);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_2],
				IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_2);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_3],
				IDX_DLY_DCUPLOAD_FINANCIAL_ATTRIBUTE_3);
		insertStmt = insertStmt + tempStr;

		/* This is the value of the field Source_Flag i.e. X for XL Upload */
		insertStmt = insertStmt + ", 'X'";

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_BAL_TYPE], IDX_DLY_DCUPLOAD_BAL_TYPE);
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_TRANS_DATE], IDX_DLY_DCUPLOAD_TRANS_DATE);
		tempStr = ", " + "To_Date(" + tempStr.replace(", ", "") + ", 'DD/MM/YYYY')";
		insertStmt = insertStmt + tempStr;

		tempStr = "";
		tempStr = formatFieldValue(uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT], IDX_DLY_DCUPLOAD_TRANS_AMOUNT);
		insertStmt = insertStmt + tempStr;

		insertStmt = insertStmt + ", " + makerId + ", 0, " + INTERNAL_STATUS + ", " + getDbFunction("SYSDATE") + ", "
				+ getDbFunction("SYSDATE") + ", '" + newPostingsFlag + "', 'N', '" + actionId + "', " + paramLineCtr
				+ ")";

		/*
		 * Find out whether the record already exists in the UPL table, in case if the
		 * same record has been given more than once in the same input file or not
		 */

		customerId = uploadData[IDX_DLY_DCUPLOAD_CUSTOMER_ID];
		officeAccount = uploadData[IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT];
		creditLine = uploadData[IDX_DLY_DCUPLOAD_CREDIT_LINE];
		originatingOuc = uploadData[IDX_DLY_DCUPLOAD_ORIGINATING_OUC];
		if (ValidationUtil.isValid(uploadData[IDX_DLY_DCUPLOAD_ACCRUAL_STATUS])) {
			accrualStatus = Integer.parseInt(uploadData[IDX_DLY_DCUPLOAD_ACCRUAL_STATUS]);
		}

		String errorMessage = "";
		int countPostings = 0;
		try {
			rs = stmt.executeQuery("Select TRANS_AMOUNT " + " From Fin_Dly_Adj_Postings_Upl " + " Where Action_Id = '"
					+ actionId + "'" + " And Country = " + country + "" + " And Le_Book = " + leBook + ""
					+ " And Year_Month = " + yearMonth + "" + " And Adj_Source = " + adjSource + ""
					+ " And Sequence_Fd = " + sequenceFm + "" + " And Adj_Reference = " + adjReference + ""
					+ " And Customer_Id = " + customerId + "" + " And Gl_Enrich_Id = " + glEnrichId + ""
					+ " And Contract_Id = " + contractId + "" + " And Office_Account = " + officeAccount + ""
					+ " And Bs_Gl = " + bsGl + "" + " And Pl_Gl = " + plGl + "" + " And Vision_Ouc =" + visionOuc + ""
					+ " And Vision_Sbu = " + visionSbu + "" + " And Account_Officer = " + accountOfficer + ""
					+ " And Cost_Center = " + costCenter + "" + " And Currency  = " + currency + ""
					+ " And Mis_Currency = " + misCurrency + "" + " And Accrual_Status = " + accrualStatus + ""
					+ " And Credit_Line = " + creditLine + "" + " And Source_Id = " + sourceId + ""
					+ " And Record_Type = " + recordType + "" + " And Original_Ouc= " + originalOuc + "");
			while (rs.next()) {
				transAmount = rs.getString("TRANS_AMOUNT");
				countPostings++;

			}
		} catch (SQLException e) {
			e.printStackTrace();
			sqlLogWriter("" + appendZeroforCtr(paramLineCtr)
					+ "\t\tFin_Dly_Adj_Postings_Upl validation before insert/update failed :" + e.getMessage());
			finalReturnValue = ERRONEOUS_EXIT;
			totalFailureRecords++;
			return (SUCCESSFUL_EXIT);
		}
		if (countPostings != 0) {

			transAmount = "";
			transAmount = uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT];

			if (ValidationUtil.isValid(transAmount)) {
				indic1 = 0;
			} else {
				indic1 = -1;
			}
			if ((indic1 == -1 && "Y".equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT])
					&& uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT].length() != 0)
					|| (indic1 == 0 && uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT].length() == 0
							&& "N".equalsIgnoreCase(uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT]))) {
				logWriter("" + appendZeroforCtr(paramLineCtr)
						+ "       \t\tFin_Dly_Adj_Postings_Upl Insert Failed - Inconsistent Calc_Pool/Rate specified - I",
						uploadLogFileName);
				finalReturnValue = ERRONEOUS_EXIT;
				totalFailureRecords++;
				return (SUCCESSFUL_EXIT);
			}

			/*
			 * Update the existing record, augmenting the balances in the UPL table
			 */
			sqlStatement = "Update Fin_Dly_Adj_Postings_Upl Set ";
			commaFlag = 'N';

			if (uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT].length() != 0) {
				sqlStatement = sqlStatement + " TRANS_AMOUNT = TRANS_AMOUNT + "
						+ uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT] + "";
				commaFlag = 'Y';
			}

			sqlStatement = sqlStatement + " Where Country      = " + country + "" + " And Le_Book           = " + leBook
					+ " " + " And Year_Month        =  " + yearMonth + " " + " And Adj_Source        = " + adjSource
					+ " " + " And Sequence_Fd       = " + sequenceFm + " " + " And Adj_Reference     = " + adjReference
					+ " " + " And Customer_Id       = " + customerId + " " + " And Gl_Enrich_Id      = " + glEnrichId
					+ "" + " And Contract_Id       = " + contractId + "" + " And Office_Account    = " + officeAccount
					+ "" + " And Bs_Gl             = " + bsGl + "" + " And Pl_Gl             = " + plGl + ""
					+ " And Vision_Ouc        = " + visionOuc + "" + " And Vision_Sbu        = " + visionSbu + ""
					+ " And Account_Officer   = " + accountOfficer + "" + " And Cost_Center       = " + costCenter + ""
					+ " And Currency          = " + currency + "" + " And Mis_Currency      = " + misCurrency + ""
					+ " And Accrual_Status    =  " + accrualStatus + "" + " And Credit_Line       = " + creditLine + ""
					+ " And Source_Id         =  " + sourceId + "" + " And Record_Type       =  " + recordType + ""
					+ " And Original_Ouc      = " + originatingOuc + "";
			try {
				int cnt = stmt.executeUpdate(sqlStatement);
				if (cnt != 0) {
					logWriter("       \t\tFin_Dly_Adj_Postings_Upl Update succeeded", uploadLogFileName);
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "       \t\tFin_Dly_Adj_Postings_Upl Update Failed :"
						+ e.getMessage());
				finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
		} else {
			/* Attempt to insert into the Fin_Adj_Postings_UPL Table, now */
			try {
				int cnt = stmt.executeUpdate(insertStmt);
				if (cnt > 0) {
					logWriter("" + appendZeroforCtr(paramLineCtr) + "\t\tFin_Dly_Adj_Postings_Upl  Insert succeeded",
							uploadLogFileName);
					totalSuccessRecords++;
				}
			} catch (SQLException e) {
				sqlLogWriter("" + appendZeroforCtr(paramLineCtr) + "       \t\tFin_Dly_Adj_Postings_Upl Insert Failed :"
						+ e.getMessage());
				logWriter(insertStmt, uploadLogFileName);
//				System.out.println(insertStmt);
				// finalReturnValue = ERRONEOUS_EXIT;
				retVal = ERRONEOUS_EXIT;
				totalFailureRecords++;
			}
		}
		return (SUCCESSFUL_EXIT);
	}

	private int loadVirtualKeyFieldsForDailyDC() {
		try {
			yearMonth = Integer.parseInt(uploadData[IDX_DLY_DCUPLOAD_YEAR_MONTH]);

			country = "";
			country = uploadData[IDX_DLY_DCUPLOAD_COUNTRY];

			leBook = "";
			leBook = uploadData[IDX_DLY_DCUPLOAD_LE_BOOK];

			adjReference = "";
			adjReference = uploadData[IDX_DLY_DCUPLOAD_ADJ_REFERENCE];

			compDataType = "";
			compDataType = uploadData[IDX_DLY_DCUPLOAD_DATA_TYPE];

			/* Vision_Ouc value will be set for Original_Ouc also */
			originalOuc = "";
			originalOuc = uploadData[IDX_DLY_DCUPLOAD_VISION_OUC];

			compFaReasonCode = "";
			compFaReasonCode = uploadData[IDX_DLY_DCUPLOAD_FA_REASON_CODE];

			compSupplement = "";
			compSupplement = uploadData[IDX_DLY_DCUPLOAD_SUPPLEMENT];

			contractId = "";
			contractId = uploadData[IDX_DLY_DCUPLOAD_CONTRACT_ID];

			customerId = "";
			customerId = uploadData[IDX_DLY_DCUPLOAD_CUSTOMER_ID];

			visionOuc = "";
			visionOuc = uploadData[IDX_DLY_DCUPLOAD_VISION_OUC];

			glEnrichId = "";
			glEnrichId = uploadData[IDX_DLY_DCUPLOAD_GL_ENRICH_ID];

			officeAccount = "";
			officeAccount = uploadData[IDX_DLY_DCUPLOAD_OFFICE_ACCOUNT];

			visionSbu = "";
			visionSbu = uploadData[IDX_DLY_DCUPLOAD_VISION_SBU];

			accountOfficer = "";
			accountOfficer = uploadData[IDX_DLY_DCUPLOAD_ACCOUNT_OFFICER];

			costCenter = "";
			costCenter = uploadData[IDX_DLY_DCUPLOAD_COST_CENTER];

			bsGl = "";
			bsGl = uploadData[IDX_DLY_DCUPLOAD_BS_GL];

			plGl = "";
			plGl = uploadData[IDX_DLY_DCUPLOAD_PL_GL];

			currency = "";
			currency = uploadData[IDX_DLY_DCUPLOAD_CURRENCY];

			misCurrency = "";
			misCurrency = uploadData[IDX_DLY_DCUPLOAD_MIS_CURRENCY];

			sourceId = Integer.parseInt(uploadData[IDX_DLY_DCUPLOAD_SOURCE_ID]);

			transDate = "";
			transDate = uploadData[IDX_DLY_DCUPLOAD_TRANS_DATE];
			// transDate = "To_Date("+transDate+", 'DD/MM/YYYY')";

			valueDate = "";
			valueDate = uploadData[IDX_DLY_DCUPLOAD_VALUE_DATE];
			// valueDate = "To_Date("+valueDate+", 'DD/MM/YYYY')";

			creditLine = "";
			creditLine = uploadData[IDX_DLY_DCUPLOAD_CREDIT_LINE];

			transAmount = "";
			transAmount = uploadData[IDX_DLY_DCUPLOAD_TRANS_AMOUNT];

			balType = "";
			balType = uploadData[IDX_DLY_DCUPLOAD_BAL_TYPE];
			return SUCCESSFUL_EXIT;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("Error when loading keys [loadVirtualKeyFieldsForDailyDC()]:" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	private int validateDailyDCTblCols() {
		int Ctr1 = 0, Ctr2 = 0;
		char foundFlag = 'Y';
		char firstTimeFlag = 'Y';
		char continueFlag = 'N';
		int recCounttmp = 0;
		retVal = SUCCESSFUL_EXIT;
		tableColumnList = new ArrayList();
		columnNamelst = new ArrayList();
		columnSizelst = new ArrayList();
		quotedTypelst = new ArrayList();
		nullable = new ArrayList();
		decimalsChecklst = new ArrayList();
		dataScalelst = new ArrayList();
		dateTypelst = new ArrayList();
		fileColumnsColumnSize = new ArrayList();
		fileColumnsQuotedType = new ArrayList();
		fileColumnsNullable = new ArrayList();
		fileColumnsDecimalsCheck = new ArrayList();
		fileColumnsAfterDecimals = new ArrayList();
		fileColumnsDataType = new ArrayList();
		totalTableColumns = 0;
		try {
			rs = stmt.executeQuery("Select Column_Name, Col_Nullable, Col_Size, Quoted_Type, After_Decimals, "
					+ " Date_Type, Decimals_Check "
					+ " From Xl_Upload_Cols_Repository Where Upload_Genre = 'DLYDCUPLOAD' "
					+ " Order By Col_Sequence ");
			while (rs.next()) {
				columnNamelst.add(rs.getString("Column_Name"));
				nullable.add(rs.getString("Col_Nullable"));
				columnSizelst.add(rs.getString("Col_Size"));
				quotedTypelst.add(rs.getString("Quoted_Type"));
				dataScalelst.add(rs.getString("After_Decimals"));
				dateTypelst.add(rs.getString("Date_Type"));
				decimalsChecklst.add(rs.getString("Decimals_Check"));
				totalTableColumns++;
			}
			tableColumnList.addAll(columnNamelst);
			tableColumnList.addAll(nullable);
			tableColumnList.addAll(columnSizelst);
			tableColumnList.addAll(quotedTypelst);
			tableColumnList.addAll(dataScalelst);// after Decimals
			tableColumnList.addAll(dateTypelst);
			tableColumnList.addAll(decimalsChecklst);
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to select from Xl_Upload_Cols_Repository table :" + e.getMessage());
			logWriter("Query was ", uploadLogFileName);
			logWriter(sqlStatement, uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
		if (totalTableColumns == 0) {
			logWriter("The table does not have any columns to upload !!", uploadLogFileName);
			return (ERRONEOUS_EXIT);
		}
		for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
			/*
			 * TO DO remove after verification - for debugging purposes only
			 * printf("input file column name is [%s]\n", fileColumns[Ctr1].columnName);
			 */
			if (fileColumnsNames.get(Ctr1).toString().equalsIgnoreCase(columnNamelst.get(Ctr1).toString())) {
				// System.out.println(""+fileColumnsNames.get(Ctr1).toString());
				fileColumnsColumnSize.add(Ctr1, columnSizelst.get(Ctr1));
				fileColumnsQuotedType.add(Ctr1, quotedTypelst.get(Ctr1));
				fileColumnsNullable.add(Ctr1, nullable);
				fileColumnsDecimalsCheck.add(Ctr1, decimalsChecklst.get(Ctr1));
				fileColumnsAfterDecimals.add(Ctr1, dataScalelst.get(Ctr1));
				fileColumnsDataType.add(Ctr1, dateTypelst.get(Ctr1));
			} else {
				foundFlag = 'N';
			}
		}

		if (retVal != SUCCESSFUL_EXIT)
			return (retVal);

		return (retVal);
	}

	public int reportsUpload() {
		String tempStr = "";
		try {
			/* Check if upload is allowed for this table */
			retVal = checkIfUploadIsAllowed();
			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			/* Check if the table has been locked by some build process */
			retVal = checkForTableLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return ERRONEOUS_EXIT;
			}

			/* Lock the table to proceed with Upload */
			retVal = lockTableForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for XL Upload. Cannot proceed now with upload !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			/* Set the status to In-Progress mode for the upload request */
			retVal = updateUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT) {
				doFinishingSteps();
				return ABORT_EXECUTION;
			}
			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile("Uploading to the table [" + uploadTableName + "]");
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";
			uploadLogFileName = xluploadLogFilePath + EXCEL_FILE_NAME;
			try {
				logfile = new FileWriter(uploadLogFileName);// Open a Log file
															// in Write mode
				bufferedWriter = new BufferedWriter(logfile);
				logfile.close();
				bufferedWriter.close();

			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName + "] for logging process steps :\n "
						+ e.getMessage());
				writeToMainLogFile("Aborting further processing of this request - FYI");
				return (ERRONEOUS_EXIT);
			}
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";
			try {
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + tempStr + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + tempStr + "]\nSystem Msg [:" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI");
				logWriter("Aborting further processing of this request - FYI", uploadLogFileName);

				retVal = updateUploadStatus(STATUS_FAILURE);
				if (retVal == ERRONEOUS_EXIT)
					return ABORT_EXECUTION;

				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			logWriter("Upload File Name\t:[" + uploadFileName + ".xlsx;]\n", uploadLogFileName);
			originalTableName = uploadTableName;

			/*
			 * Decide the table name, depending on whether the specified table or the bulk
			 * table needs to be hit, with the upload
			 */
			if (valueYes.equalsIgnoreCase(verificationRequired)) {
				uploadTableName = uploadTableName;
			}
			logWriter("Table name obtained \t:[" + originalTableName + "]\n", uploadLogFileName);

			if (DEBUG_MODE == YES)
				logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);

			retVal = checkForTblExistence();
			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			loadReportFileData(uploadFile);
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\n Reorts Data Not found !!", uploadLogFileName);
				logWriter("\n Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = validateTblColsForReportUpload();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			try {
				callableStatement = con.prepareCall("{call PR_REPORT_COL_MAP (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
				callableStatement.setString(1, REPORT_UPLOAD_COUNTRY);
				callableStatement.setString(2, REPORT_UPLOAD_LE_BOOK);
				callableStatement.setString(3, REPORT_UPLOAD_REPORT_ID);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
				callableStatement.setString(8, "N");
				callableStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(10, java.sql.Types.VARCHAR);

				callableStatement.executeQuery();

				P_REPCOLLIST = callableStatement.getString(4);
				P_INSCOLCTR = callableStatement.getString(5);
				P_ALIASREPCOLLIST = callableStatement.getString(6);
				P_KEYCOLLIST = callableStatement.getString(7);

				status = callableStatement.getString(9);
				errorMsg = callableStatement.getString(10);
			} catch (SQLException e) {
				sqlLogWriter("Error calling procedure PR_REPORT_COL_MAP to validate input data :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			if (!"0".equalsIgnoreCase(status)) {
				logWriter("Error occured after execute the procedure. Aborting further processing !!",
						uploadLogFileName);
				logWriter("Message got from the database ....", uploadLogFileName);
				errorMsg = errorMsg + " !!!!!!\n\n";
				logWriter(errorMsg, uploadLogFileName);
				return (ERRONEOUS_EXIT);
			}
			/*
			 * retVal = validateTblColsFromMappingsTable(); if (retVal == ERRONEOUS_EXIT) {
			 * logWriter("\nExecution aborted !!"); return ERRONEOUS_EXIT; }
			 * 
			 * 
			 */ retVal = deleteEntryFromUPLTablesForReports("Y");
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			con.commit();

			TAB_FLAG = 'Y';
			String mainUploadTableName = uploadTableName;
			uploadTableName = uploadTableName + "_UPL ";
			retVal = uploadReportsFileData();
			uploadTableName = mainUploadTableName;
			TAB_FLAG = 'N';

			if (finalReturnValue == ERRONEOUS_EXIT) {
				con.rollback();
			} else {
				con.commit();
			}
			try {
				callableStatement = con.prepareCall("{call PR_REPORT_DATA_VALIDATION (?, ?, ?, ?, ?, ?)}");

				callableStatement.setString(1, REPORT_UPLOAD_COUNTRY);
				callableStatement.setString(2, REPORT_UPLOAD_LE_BOOK);
				callableStatement.setString(3, REPORT_UPLOAD_REPORT_ID);
				callableStatement.setString(4, "N");
				callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC);
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
				callableStatement.executeUpdate();
				// P_STATUS = callableStatement.getInt(5);
				P_STATUS = 0;
				errorMsg = callableStatement.getString(6);
			} catch (SQLException e) {
				sqlLogWriter(
						"Error calling procedure PR_REPORT_DATA_VALIDATION to validate input data :" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			/*
			 * Commit now, since we want the insertions made into the
			 * Fin_Adj_Validation_Errors tables, to be permanent
			 */
			con.commit();

			logWriter(" ", uploadLogFileName);

			if (P_STATUS != 0) {
				if ("Unsuccessful".equalsIgnoreCase(errorMsg)) {
					printReortUploadErrorReport();
					return (ERRONEOUS_EXIT);
				} else {
					logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
							uploadLogFileName);
					logWriter("ErrorMsg : [" + errorMsg + "]\n", uploadLogFileName);
					logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
							uploadLogFileName);
				}
				return (ERRONEOUS_EXIT);
			}
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter("Records Upload Successfully ", uploadLogFileName);
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			logWriter(" ", uploadLogFileName);
			retVal = deleteEntryFromUPLTablesForReports("N");
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\nExecution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			con.commit();
			// printSummaryReport(finalReturnValue);
			// return(finalReturnValue);
			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
			logWriter("genericUpload : " + e.getMessage(), uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
	}

	public int uploadReportsFileData() {
		try {
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			String tempFileData[];
			char ch;
			int lineCtr = 1;
			String tempStr;
			char theAndFlag = 'N';
			int columnCount = 0;
			int rowCount = 0;
			int validRowCount = 0;
			int invalidRowCount = 0;
			totalInputFileRecords = 0;
			retVal = SUCCESSFUL_EXIT;
			FileInputStream fileReader1 = null;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;
			workbook = new XSSFWorkbook(fileReader1);

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					lineFetched = "";
					for (Ctr2 = 0; Ctr2 < totalInputFileColumns; Ctr2++) {
						String specificData = "";
						if (ValidationUtil.isValid(rowData.getCell(Ctr2))) {
							if ("BLOB".equalsIgnoreCase(dataType.get(Ctr2))
									|| "CLOB".equalsIgnoreCase(dataType.get(Ctr2))) {
								specificData = rowData.getCell(Ctr2).getRichStringCellValue().toString();
							}else if (rowData.getCell(Ctr2).getCellType() == CellType.STRING) { 
//							else if (rowData.getCell(Ctr2).getCellType() == Cell.CELL_TYPE_STRING) {
								specificData = rowData.getCell(Ctr2).getStringCellValue();
							} else if (rowData.getCell(Ctr2).getCellType() == CellType.NUMERIC
								      || rowData.getCell(Ctr2).getCellType() == CellType.FORMULA) {
//							else if (rowData.getCell(Ctr2).getCellType() == Cell.CELL_TYPE_NUMERIC
//									|| rowData.getCell(Ctr2).getCellType() == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(rowData.getCell(Ctr2))) {
									if ("DATE".equalsIgnoreCase(dataType.get(Ctr2))) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String
												.valueOf(sdf.format(rowData.getCell(Ctr2).getDateCellValue()));
									} else {
										logWriter("--------" + columnNamelst.get(Ctr2) + "----" + dataType.get(Ctr2),
												uploadLogFileName);
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
										specificData = String
												.valueOf(sdf.format(rowData.getCell(Ctr2).getDateCellValue()));
									}
								} else {
									specificData = String.valueOf(rowData.getCell(Ctr2).getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}
								}
							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							lineFetched = lineFetched + "	" + specificData.replaceAll("'", "").replaceAll("\"", "");
						}
					}
					if (lineFetched == null) {
						break;
					} else {
						uploadData = lineFetched.split("\t");
						recordPresent = false;
						for (int i = 0; i < uploadData.length; i++) {
							if (ValidationUtil.isValid(uploadData[i]))
								recordPresent = true;
						}
						String[] uploadDataTmp = null;
						if (recordPresent) {
							for (Ctr2 = 0; Ctr2 < uploadData.length; Ctr2++) {
								uploadData[Ctr2] = checkAndProcessQuotes(uploadData[Ctr2]);
							}
							lineCtr++;
							if (valueYes.equals(verificationRequired)) {
								// retVal = doVerifInsUpdOperations(lineCtr);
								retVal = doInsertUpdateOperationsForReports(lineCtr);
							} else {
								retVal = doInsertUpdateOperationsForReports(lineCtr);
							}
							if (retVal == ERRONEOUS_EXIT)
								finalReturnValue = ERRONEOUS_EXIT;

							totalInputFileRecords++;
						}
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();

			if (finalReturnValue == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			if (valueYes.equalsIgnoreCase(verificationRequired) && retVal == SUCCESSFUL_EXIT) {
				sqlStatement = "Update " + uploadTableName + " Set RECORD_INDICATOR = 1 Where Exists (Select 1 From "
						+ originalTableName + " Where ";
				countStmt = "Select Count(1) as count From " + uploadTableName + ", " + originalTableName + " Where ";
				for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
					if (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString()))
						continue;

					if (theAndFlag == 'Y') {
						sqlStatement = sqlStatement + "And ";
						countStmt = countStmt + "And ";
					}
					tempStr = "RTrim(" + uploadTableName + "." + fileColumnsNames.get(Ctr1) + ") = RTrim("
							+ originalTableName + "." + fileColumnsNames.get(Ctr1) + ") ";
					sqlStatement = sqlStatement + tempStr;
					countStmt = countStmt + tempStr;
					theAndFlag = 'Y';
				}
				sqlStatement = sqlStatement + " And " + uploadTableName + ".RECORD_INDICATOR = 4) ";

				recsCount = getCount();
				if (recsCount < 0)
					return (ERRONEOUS_EXIT);
				if (recsCount > 0) {
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException ex) {
						sqlLogWriter("Error trying to update pending table (Update #1) for the uploaded data :"
								+ ex.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
					}
				}
				countStmt = "Select Count(1) as count From " + uploadTableName + " Where RECORD_INDICATOR = 4 ";
				recsCount = getCount();

				if (recsCount < 0)
					return (ERRONEOUS_EXIT);

				if (recsCount > 0) {
					sqlStatement = " Update " + uploadTableName
							+ " Set RECORD_INDICATOR = 1 Where RECORD_INDICATOR = 4 ";
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException e) {
						sqlLogWriter("Error trying to update pending table (Update #2) for the uploaded data :"
								+ e.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
					}
				}
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("uploadFileData():" + e.getMessage());
			doFinishingSteps();
			return ERRONEOUS_EXIT;
		}
	}

	private int deleteEntryFromUPLTablesForReports(String finalDelete) {
		if ("Y".equalsIgnoreCase(finalDelete)) {
			try {
				deletedRecsCount = stmt
						.executeUpdate("delete From VISION_REPORT_DATA " + " Where COUNTRY = '" + REPORT_UPLOAD_COUNTRY
								+ "' " + " and LE_BOOK = '" + REPORT_UPLOAD_LE_BOOK + "' " + " and PERIOD = '"
								+ REPORT_UPLOAD_PERIOD + "' " + " and REPORT_ID = '" + REPORT_UPLOAD_REPORT_ID + "' ");
			} catch (SQLException e) {
				sqlLogWriter("Error deleting from VISION_REPORT_DATA_UPL Table for COUNTRY [" + REPORT_UPLOAD_COUNTRY
						+ "], LE_BOOK [" + REPORT_UPLOAD_LE_BOOK + "], REPORT_ID [" + REPORT_UPLOAD_REPORT_ID + "] :"
						+ e.getMessage());
				return (ERRONEOUS_EXIT);
			}
		}
		try {
			deletedRecsCount = stmt.executeUpdate("delete From VISION_REPORT_DATA_UPL " + " Where COUNTRY = '"
					+ REPORT_UPLOAD_COUNTRY + "' " + " and LE_BOOK = '" + REPORT_UPLOAD_LE_BOOK + "' "
					+ " and REPORT_ID = '" + REPORT_UPLOAD_REPORT_ID + "' ");
		} catch (SQLException e) {
			sqlLogWriter("Error deleting from VISION_REPORT_DATA_UPL Table for COUNTRY [" + REPORT_UPLOAD_COUNTRY
					+ "], LE_BOOK [" + REPORT_UPLOAD_LE_BOOK + "], REPORT_ID [" + REPORT_UPLOAD_REPORT_ID + "] :"
					+ e.getMessage());
			return (ERRONEOUS_EXIT);
		}
		return (SUCCESSFUL_EXIT);
	}

	private int validateTblColsForReportUpload() {
		int Ctr1 = 0, Ctr2 = 0;
		char foundFlag = 'Y';
		char firstTimeFlag = 'Y';
		retVal = SUCCESSFUL_EXIT;
		sqlStatement = "";
		columnNamelst = new ArrayList();
		dataType = new ArrayList();
		totalTableColumns = 0;
		columnNamelst.add("COUNTRY");
		columnNamelst.add("LE_BOOK");
		columnNamelst.add("REPORT_ID");
		columnNamelst.add("PERIOD");
		dataType.add("CHAR");
		dataType.add("CHAR");
		dataType.add("CHAR");
		dataType.add("CHAR");
		try {
			rs = stmt.executeQuery(
					"SELECT UPPER(TRIM(REP_COLUMN_NAME)) COLUMN_NAME,REP_COLUMN_TYPE FROM REPORT_COL_MAPPING "
							+ " Where COUNTRY = '" + REPORT_UPLOAD_COUNTRY + "' " + " and LE_BOOK = '"
							+ REPORT_UPLOAD_LE_BOOK + "' " + " and REPORT_ID = '" + REPORT_UPLOAD_REPORT_ID + "' ");
			while (rs.next()) {
				columnNamelst.add(rs.getString("COLUMN_NAME"));
				dataType.add(rs.getString("REP_COLUMN_TYPE"));
				totalTableColumns++;
			}
			if (columnNamelst.size() < 5) {
				logWriter("Report column Mapping is not found for the Report Id : " + REPORT_UPLOAD_REPORT_ID + "",
						uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				foundFlag = 'N';
				for (Ctr2 = 0; Ctr2 < columnNamelst.size(); Ctr2++) {
					if (fileColumnsNames.get(Ctr1).equals(columnNamelst.get(Ctr2))) {
						foundFlag = 'Y';
						break;
					}
				}
				if (foundFlag == 'N') {
					if (firstTimeFlag == 'Y') {
						logWriter("The following columns names are invalid for the selected Report !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("[" + fileColumnsNames.get(Ctr1) + "]", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
			}
			for (Ctr1 = 0; Ctr1 < columnNamelst.size(); Ctr1++) {
				foundFlag = 'N';
				for (Ctr2 = 0; Ctr2 < totalInputFileColumns; Ctr2++) {
					if (columnNamelst.get(Ctr1).equals(fileColumnsNames.get(Ctr2))) {
						foundFlag = 'Y';
						break;
					}
				}
				if (foundFlag == 'N') {
					if (firstTimeFlag == 'Y') {
						logWriter("The following columns names are not found on the given Uploaded file !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter("[" + columnNamelst.get(Ctr1) + "]", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
			}
			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (fileColumnsNames.get(Ctr1).toString().equalsIgnoreCase(columnNamelst.get(Ctr1).toString())) {
					foundFlag = 'Y';
				} else {
					foundFlag = 'N';
					logWriter("The following columns names are not given in the correct order !!", uploadLogFileName);
					logWriter("[" + fileColumnsNames.get(Ctr1) + "]", uploadLogFileName);
					return ERRONEOUS_EXIT;
				}
			}
			return retVal;
		} catch (Exception e) {
			writeToMainLogFile("validateTblCols():" + e.getMessage());
			return ERRONEOUS_EXIT;
		}
	}

	public int loadReportFileData(String fileName) {
		try {
			String tempStr = "";
			char firstTimeFlag = 'Y';
			retVal = SUCCESSFUL_EXIT;
			tempStr = "";
			String headerRow = "";
			String[] columnNames = null;
			fileColumnsNames = new ArrayList();
			fileColumnsIndexColumn = new ArrayList();
			fileColumnsNullable = new ArrayList();
			if (fileReader == null) {
				/*
				 * bufferedReader1 = new BufferedReader(new InputStreamReader(new
				 * FileInputStream(fileName),"UTF8"));
				 */
				fileReader = new FileInputStream(new File(uploadFile));
			}
			int i = 0;
			Workbook workbook = new XSSFWorkbook(fileReader);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = firstSheet.getRow(1);
			for (int loopCnt = 0; loopCnt < 4; loopCnt++) {
				Cell cell = nextRow.getCell(loopCnt);
				if (loopCnt == 0) {
					REPORT_UPLOAD_COUNTRY = cell.getStringCellValue();
				} else if (loopCnt == 1) {
					REPORT_UPLOAD_LE_BOOK = cell.getStringCellValue();
				} else if (loopCnt == 2) {
					REPORT_UPLOAD_REPORT_ID = cell.getStringCellValue();
				} else if (loopCnt == 3) {
					// REPORT_UPLOAD_PERIOD = cell.getStringCellValue();
//					if (cell.getCellType() == 0) {
					if (cell.getCellType() == CellType.NUMERIC) {
						REPORT_UPLOAD_PERIOD = String.valueOf(cell.getNumericCellValue());
						if (REPORT_UPLOAD_PERIOD.contains("E")) {
							double num = cell.getNumericCellValue();
							DecimalFormat pattern = new DecimalFormat("##########");
							NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
							REPORT_UPLOAD_PERIOD = testNumberFormat.format(num).replaceAll(",", "");
						}
					} else
						REPORT_UPLOAD_PERIOD = cell.getStringCellValue();
				}
			}
			/* while (iterator.hasNext()) { */
			nextRow = firstSheet.getRow(0);
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (ValidationUtil.isValid(cell.getStringCellValue())) {
					headerRow = headerRow + cell.getStringCellValue() + ",";
				}
				i++;
			}
			/* } */
			fileReader.close();
			columnNames = headerRow.split(",");
			if (columnNames.length == 0) {
				logWriter("No data available in the text file, for upload !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			totalInputFileColumns = columnNames.length;
			for (int Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				tempStr = columnNames[Ctr1];
				tempStr = tempStr.toUpperCase();
				if (tempStr.length() > longestColumn)
					longestColumn = tempStr.length();

				if (tempStr.length() == 0) {
					logWriter("An extra tab or space(s) is found in the header lines !!", uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				} else if (chkForAllSpaces(tempStr) == YES) {
					logWriter(tempStr + " - A column heading with just spaces is found in the header line !!",
							uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
				if (Arrays.asList(exceptionColList).contains(tempStr)) {
					if (firstTimeFlag == 'Y') {
						logWriter(tempStr + " - The following columns should not be given in the input file !!",
								uploadLogFileName);
						firstTimeFlag = 'N';
					}
					logWriter(tempStr, uploadLogFileName);
					retVal = ERRONEOUS_EXIT;
				}
				fileColumnsNames.add(tempStr); // add [tempStr] to [fileColumns]
												// array list
				fileColumnsIndexColumn.add('N'); // fileColumns[Ctr2].indexColumn
													// = 'N';
				tempStr = "";
			}
			return (SUCCESSFUL_EXIT);
		} catch (Exception e) {
			writeToMainLogFile("loadReportFileData :" + e.getMessage());
			e.printStackTrace();
			logWriter("loadTxtFileColumnNames" + e, uploadLogFileName);
			return ERRONEOUS_EXIT;
		}
	}

	private void printReortUploadErrorReport() {
		int recCount = 0;
		try {
			rs = stmt.executeQuery("Select COUNTRY, LE_BOOK,  REPORT_ID, RECORD_NUM, RECORD_ERROR_MSG " + " From "
					+ uploadTableName + "_UPL " + " Where COUNTRY = '" + REPORT_UPLOAD_COUNTRY + "' "
					+ " and LE_BOOK = '" + REPORT_UPLOAD_LE_BOOK + "' " + " and REPORT_ID = '" + REPORT_UPLOAD_REPORT_ID
					+ "' " + " and RECORD_ERROR_MSG !='Successful' "
					+ " Order By COUNTRY, LE_BOOK,  REPORT_ID, RECORD_NUM  ");
			recCount = rs.getRow();
			logWriter("All input records failed validation. Aborting further processing !!", uploadLogFileName);
			logWriter(" ", uploadLogFileName);
			logWriter("Final Summary report for Report Upload : Failed !!!!!!", uploadLogFileName);
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
			while (rs.next()) {
				// String tempStr =
				// appendZeroforCtr(rs.getString("RECORD_NUM"))+ "\t\tInsert
				// succeeded";
				String tempStr = " Upolad Failed in Row number ["
						+ appendZeroforCtr(Integer.parseInt(rs.getString("RECORD_NUM"))) + "] \t\tError Message["
						+ rs.getString("RECORD_ERROR_MSG") + "]";
				logWriter(tempStr, uploadLogFileName);
			}
			logWriter("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", uploadLogFileName);
		} catch (SQLException e) {
			sqlLogWriter("Error preparing to Select from the " + uploadTableName
					+ "_UPL table for displaying a summary of errors encountered during the upload process :"
					+ e.getMessage());
			logWriter(sqlStatement, uploadLogFileName);
			return;
		}
	}

	public int doInsertUpdateOperationsForReports(int paramLineCtr) {
		try {
			int Ctr1 = 0, Ctr2 = 0;
			String tempStr;
			char commaFlag = 'N';
			retVal = SUCCESSFUL_EXIT;
			insertStmt = "Insert Into " + uploadTableName + " ( COUNTRY, LE_BOOK, REPORT_ID, PERIOD, ";
			/*
			 * for(Ctr1=0; Ctr1 < totalInputFileColumns; Ctr1++) { if (Ctr1 != 0) insertStmt
			 * = insertStmt + ", ";
			 * 
			 * insertStmt = insertStmt + fileColumnsNames.get(Ctr1); }
			 */
			String[] columNamesArray = P_INSCOLCTR.split(",");
			/* String [] indexColumNamesArray = P_KEYCOLLIST.split(","); */
			insertStmt = insertStmt + P_INSCOLCTR;
			insertStmt = insertStmt + ", RECORD_NUM ";
			insertStmt = insertStmt
					+ ", MAKER, VERIFIER, INTERNAL_STATUS, RECORD_INDICATOR, DATE_CREATION, DATE_LAST_MODIFIED) Values (";
			totalInputFileColumns = columNamesArray.length + 4;

			for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
				if (Ctr1 != 0)
					insertStmt = insertStmt + ", ";

				if (uploadData[Ctr1] == null || uploadData[Ctr1].trim().length() == 0) {
					insertStmt = insertStmt + " Null ";
				}
				/*
				 * else if(valueYes.equals(fileColumnsQuotedType.get(Ctr1))) { if
				 * (valueYes.equals(fileColumnsDataType.get(Ctr1))) { insertStmt = insertStmt +
				 * " To_Date(" + uploadData[Ctr1] + ", '" + INPUT_DATE_FORMAT + "') "; } else {
				 * if (valueYes.equals(fileColumnsIndexColumn.get(Ctr1))) insertStmt =
				 * insertStmt + "Trim(" +uploadData[Ctr1]+ ") "; else insertStmt = insertStmt +
				 * "" +uploadData[Ctr1]+ ""; } }
				 */else {
					insertStmt = insertStmt + uploadData[Ctr1];
				}
			}
			tempStr = "," + paramLineCtr + "," + makerId + "," + makerId + "," + INTERNAL_STATUS + ","
					+ REC_IND_APPROVED + "," + getDbFunction("SYSDATE") + ", " + getDbFunction("SYSDATE") + ")";
			insertStmt = insertStmt + tempStr;
			/*
			 * updateStmt = "Update " +uploadTableName+ " Set "; for(Ctr1=0; Ctr1 <
			 * totalInputFileColumns; Ctr1++) { if (Ctr1 != 0) updateStmt = updateStmt+ ",";
			 * 
			 * updateStmt = updateStmt+ "" +columNamesArray[Ctr1]; updateStmt = updateStmt+
			 * " = ";
			 * 
			 * if (uploadData[Ctr1] == null || uploadData[Ctr1].trim().length() == 0) {
			 * updateStmt = updateStmt+ " Null "; } else {
			 * if(valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1). toString())) {
			 * if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString ())) {
			 * tempStr = " To_Date(" +uploadData[Ctr1].toString()+ ",'" +INPUT_DATE_FORMAT+
			 * "') "; } else{ tempStr = " Trim(" +uploadData[Ctr1]+ ") "; } updateStmt =
			 * updateStmt+tempStr; } else { updateStmt = updateStmt+uploadData[Ctr1]; }
			 * updateStmt = updateStmt+uploadData[Ctr1]; } } tempStr = ", MAKER = "
			 * +makerId+ ", DATE_LAST_MODIFIED = sysdate";
			 * 
			 * updateStmt = updateStmt+tempStr+" Where ";
			 */

			/*
			 * for(Ctr1=0; Ctr1 < indexColumNamesArray.length; Ctr1++) { if
			 * (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1). toString()))
			 * continue;
			 * 
			 * if (commaFlag == 'Y') updateStmt = updateStmt+ " And ";
			 * 
			 * updateStmt = updateStmt+columNamesArray[Ctr1]; if (uploadData[Ctr1] == null
			 * || uploadData[Ctr1].trim().length() == 0) { updateStmt = updateStmt +
			 * " Is Null "; } else { updateStmt = updateStmt+ " = ";
			 * if(valueYes.equalsIgnoreCase(fileColumnsQuotedType.get(Ctr1). toString())) {
			 * if (valueYes.equalsIgnoreCase(fileColumnsDataType.get(Ctr1).toString ())) {
			 * updateStmt = updateStmt + " To_Date(" +uploadData[Ctr1].toString()+",'"
			 * +INPUT_DATE_FORMAT+ "') "; } else{ updateStmt = updateStmt +
			 * "Trim("+uploadData[Ctr1]+")"; } } else { updateStmt =
			 * updateStmt+uploadData[Ctr1]; } updateStmt = updateStmt+uploadData[Ctr1]; }
			 * commaFlag = 'Y'; }
			 */
			int recUpdated = 0;
			// try Updating the Records in the Table
			Boolean updateErrorFlag = false;
			/*
			 * try{ recUpdated = stmt.executeUpdate(updateStmt); if(recUpdated != 0){
			 * tempStr = appendZeroforCtr(paramLineCtr) + "\t\tUpdate succeeded";
			 * logWriter(tempStr); totalSuccessRecords++; } }catch(SQLException e){
			 * updateErrorFlag =true; tempStr = appendZeroforCtr(paramLineCtr) +
			 * "\t\tUpdate Failed"; logWriter(tempStr+updateStmt);
			 * sqlLogWriter(e.getMessage()); finalReturnValue = ERRONEOUS_EXIT;
			 * totalFailureRecords++; }
			 */
			// If the None of the Records in Updated then try inserting the
			// Record
			if (recUpdated == 0 && !updateErrorFlag) {
				try {
					stmt.executeUpdate(insertStmt);
					tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert succeeded in UPL table[" + uploadTableName
							+ "]";
					logWriter(tempStr, uploadLogFileName);
					totalSuccessRecords++;
				} catch (SQLException e) {
					tempStr = appendZeroforCtr(paramLineCtr) + "\t\tInsert Failed in UPL table[" + uploadTableName
							+ "]";
					sqlLogWriter(tempStr + "||:" + e.getMessage());
					logWriter(insertStmt, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
					totalFailureRecords++;
				}
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("insetUpd:" + e.getMessage());
			doFinishingSteps();
			return ERRONEOUS_EXIT;
		}
	}

	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public String findVisionVariableValue(String pVariableName) throws DataAccessException {
		String value = "";
		if (!ValidationUtil.isValid(pVariableName)) {
			return null;
		}
		try {
			String sql = "select VALUE FROM VISION_VARIABLES where UPPER(VARIABLE) = UPPER('" + pVariableName + "')";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				value = rs.getString("VALUE");
			}
		} catch (SQLException e) {
			sqlLogWriter("Error While Fetching the Vision Variable " + pVariableName + " " + e.getMessage());
			e.printStackTrace();
		}
		return value;
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
				functionName = "'HH:MM:SS'";
				break;
			}
		}
		return functionName;
	}

	public String dbFunctionFormats(String formatReq, String columnName, String dateFormat) {
		String returnStr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			switch (formatReq) {
			case "DATE_FORMAT":
				returnStr = "CONVERT(DATETIME,'" + columnName + "'," + dateFormat + ")";
				break;
			case "TRIM":
				returnStr = "LTrim(Rtrim('" + columnName + "')) ";
				break;
			case "RG_DATE_FORMAT":
				returnStr = "CONVERT(DATE," + columnName + "," + dateFormat + ")";
				break;
			}
		} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
			switch (formatReq) {
			case "DATE_FORMAT":
				returnStr = "To_Date('" + columnName + "','" + dateFormat + "')";
				break;
			case "TRIM":
				returnStr = "Trim('" + columnName + "') ";
				break;
			case "RG_DATE_FORMAT":
				returnStr = "To_Date(" + columnName + ",'" + dateFormat + "')";
				break;
			}
		}
		return returnStr;
	}

	public static String jaspytPasswordDecrypt(String encryptedPwd, String secretKey) {
		String decryptedPwd = "";
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(secretKey);
			encryptor.setAlgorithm("PBEWithSHA1AndDESede");
			encryptedPwd = encryptedPwd.substring(4, encryptedPwd.length() - 1);
			decryptedPwd = encryptor.decrypt(encryptedPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedPwd;
	}

	private boolean isValid(String pInput) {
		return !((pInput == null) || (pInput.trim().length() == 0) || ("".equals(pInput)));
	}

	public void logWriter(String logString, String logFileName) {
		try {
			logfile = new FileWriter(logFileName, true);
			bufferedWriter = new BufferedWriter(logfile);
			bufferedWriter.newLine();
			bufferedWriter.write(new Date() + " : " + logString);
			bufferedWriter.close();
			logfile.close();
		} catch (Exception e) {
//			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<String> getCloumnNames(String TableName, Statement stmt) throws SQLException {
		String columns = "";
		String query = "";
		List<String> collTemp = new ArrayList<>();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Select Upper(LTrim(RTrim(T1.Name))) COLUMN_NAME " + " From sys.columns T1 Inner Join sys.types T2 "
					+ "On T1.User_Type_id = T2.User_Type_id Inner Join Sys.all_objects T3 On T1.Object_id = T3.object_id "
					+ "Where T3.Name = '" + TableName
					+ "' AND T1.Name NOT IN ( SELECT  NAME  FROM SYS.IDENTITY_COLUMNS "
					+ " WHERE OBJECT_NAME(OBJECT_ID) = '" + TableName + "' )" + " order by COLUMN_ID";

		} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query = " SELECT COLUMN_NAME  FROM all_tab_columns " + " WHERE UPPER(TABLE_NAME) = upper('" + TableName
					+ "' ) AND UPPER(OWNER)=UPPER('" + username + "') order by COLUMN_ID";
		}
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			columns = rs.getString("COLUMN_NAME");
			collTemp.add(columns);
		}
		return collTemp;
	}

	public String getXluploadLogFilePath() {
		return xluploadLogFilePath;
	}

	@Value("${upload.xluploadLogFilePath}")
	public void setXluploadLogFilePath(String xluploadLogFilePath) {
		ExcelUploadPc.xluploadLogFilePath = xluploadLogFilePath;
	}

	public String getXluploadDataFilePath() {
		return xluploadDataFilePath;
	}

	@Value("${upload.xluploadDataFilePath}")
	public void setXluploadDataFilePath(String xluploadDataFilePath) {
		ExcelUploadPc.xluploadDataFilePath = xluploadDataFilePath;
	}

	private int updateTemplateheadersStatus(String templateId) {
		try {
			Statement stmt = con.createStatement();
			sqlStatement = "UPDATE RG_TEMPLATES_HEADER SET STATUS = 'VP', SUBMISSION_DATE= "
					+ dbFunctionFormats("RG_DATE_FORMAT", getDbFunction("SYSDATE"), INPUT_DATE_FORMAT)
					+ ", MAKER=  "+makerId+",VERIFIER= 0,VERSION_NO = 0,SUBMITTER = 0,REPORTING_DATE = "
					+ dbFunctionFormats("DATE_FORMAT", templateDate, INPUT_DATE_FORMAT)
					+ " WHERE TEMPLATE_ID = '" + templateId + "' ";
			stmt.executeUpdate(sqlStatement);
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {

			return ERRONEOUS_EXIT;
		}
	}

	private int updateProcessControlStatus(String templateId) {
		try {
			Statement stmt = con.createStatement();
			sqlStatement = "UPDATE RG_PROCESS_CONTROLS SET RG_PROCESS_STATUS = 'VP',SUBMISSION_DATE="
					+ dbFunctionFormats("RG_DATE_FORMAT", getDbFunction("SYSDATE"), INPUT_DATE_FORMAT)
					+ " ,PROCESS_START_TIME =null,PROCESS_END_TIME=null,MAKER=  "+makerId+",VERIFIER= 0,SUBMITTER = 0 ,VERSION_NO =0,REPORTING_DATE= "
					+ dbFunctionFormats("DATE_FORMAT", templateDate, INPUT_DATE_FORMAT)
					+ " WHERE TEMPLATE_ID = '"
					+ templateId + "'";
			stmt.executeUpdate(sqlStatement);
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {

			return ERRONEOUS_EXIT;
		}
	}

	private int truncateSourceTables(String tableName) {
		try {
			Statement stmt = con.createStatement();
			sqlStatement = "TRUNCATE TABLE " + tableName;
			stmt.executeUpdate(sqlStatement);
			return SUCCESSFUL_EXIT;
		} catch (SQLException e) {

			return ERRONEOUS_EXIT;
		}
	}

	public String getSourceTableName(String country, String leBook, String templateId) throws DataAccessException {
		String value = "";
		if (!ValidationUtil.isValid(country)) {
			return null;
		}
		try {
			String sql = "SELECT SOURCE_TABLE FROM RG_TEMPLATE_CONFIG WHERE COUNTRY = '" + country + "' AND LE_BOOK = '"
					+ leBook + "'AND TEMPLATE_ID = '" + templateId + "'";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				value = rs.getString("SOURCE_TABLE");
			}
		} catch (SQLException e) {
			sqlLogWriter("Error While Fetching the SOURCE_TABLE " + country + " " + leBook + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	private int uploadFileDataRG() {
		try {
			int rowCount = 0, validRowCount = 0, invalidRowCount = 0, totalInputFileRecords = 0;
			int retVal = SUCCESSFUL_EXIT;

			FileInputStream fileReader1 = new FileInputStream(new File(uploadFile));
			Workbook workbook = new XSSFWorkbook(fileReader1);
			Sheet firstSheet = workbook.getSheet(sheetName);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			rowCount = firstSheet.getLastRowNum();

			// <<< Check only first valid row for SS submission >>>
			for (int Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					for (int Ctr2 = 0; Ctr2 < totalInputFileColumns; Ctr2++) {
						String columnName = (String) fileColumnsNames.get(Ctr2);
						if (columnName.equalsIgnoreCase(colName)) {
							Cell cell = rowData.getCell(Ctr2);
							String specificData = "";

							if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
								CellType cellType = evaluator.evaluate(cell).getCellType();
								if ("BLOB".equalsIgnoreCase(dataType.get(columnNamelst.indexOf(columnName)))
										|| "CLOB".equalsIgnoreCase(dataType.get(columnNamelst.indexOf(columnName)))) {
									specificData = cell.getRichStringCellValue().toString();
								}else if (cellType == CellType.STRING) { 
//								else if (cellType == Cell.CELL_TYPE_STRING) {
									specificData = cell.getStringCellValue();
								}else if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
//								else if (cellType == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
									specificData = new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
								}
							}
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							sdf.setLenient(false); // Strict parsing

							try {
							    Date specificDate = sdf.parse(specificData);
							    Date today = new Date();

							    if (specificDate.after(today)) {
							        logWriter("Invalid Date: [" + specificData + "] is in the future.", uploadLogFileName);
							        fileReader1.close();
							        finalReturnValue = ERRONEOUS_EXIT;
							        return ERRONEOUS_EXIT;
							    }
							} catch (Exception e) {
							    logWriter("Invalid Date Format !!.", uploadLogFileName);
							    fileReader1.close();
							    finalReturnValue = ERRONEOUS_EXIT;
							    return ERRONEOUS_EXIT;
							}
							String templateStatus = getSourcesubmissionCheck(country, leBook, tableAttribute1,
									specificData);
							if ("SS".equalsIgnoreCase(templateStatus)) {
								logWriter("Data Present[ " + "Y" + " ]", uploadLogFileName);
								logWriter("Already Submitted[ " + "Y" + " ] For Reporting Date["+templateDate+"] on Submission Date[" + submissionDate + "]",
										uploadLogFileName);
								fileReader1.close();
								finalReturnValue = ERRONEOUS_EXIT;
								return ERRONEOUS_EXIT;
							} else {
								templateDate = specificData;
								logWriter("Already Submitted[ " + "N" + " ]", uploadLogFileName);
							}
							break; // Only check for the first matching column
						}
					}
					break; // Only check the first valid data row
				}
			}
			// >>> Check for existing data and handle override
			String tableName = getSourceTableName(country, leBook, tableAttribute1);
			if (!isValid(tableName)) {
				logWriter("unable to Find the Source Table for the template Id " + tableAttribute1, uploadLogFileName);
			}

			int checkResult = checkAndHandleExistingRecords(tableName);
			if (checkResult == ERRONEOUS_EXIT) {
				finalReturnValue = ERRONEOUS_EXIT;
				return ERRONEOUS_EXIT; // Stop insertion
			}
			int Ctr1 = 0, Ctr2 = 0, charPositionCtr = 0;
			String tempFileData[];
			char ch;
			int lineCtr = 1;
			String tempStr;
			char theAndFlag = 'N';
			int columnCount = 0;
			totalInputFileRecords = 0;
			retVal = SUCCESSFUL_EXIT;
			fileReader1 = new FileInputStream(new File(uploadFile));
			ArrayList<String> lines = new ArrayList<String>();
			String lineFetched = "";
			String[] wordsArray = null;

			Iterator<Row> iterator = firstSheet.iterator();
			Row nextRow = iterator.next();
			rowCount = firstSheet.getLastRowNum();
			columnCount = nextRow.getLastCellNum();
			for (Ctr1 = 1; Ctr1 <= rowCount; Ctr1++) {
				Row rowData = firstSheet.getRow(Ctr1);
				if (ValidationUtil.isValid(rowData)) {
					validRowCount = Ctr1;
					int rowColumnCnt = firstSheet.getRow(Ctr1).getLastCellNum();
					if (rowColumnCnt < totalInputFileColumns) {
						rowColumnCnt = totalInputFileColumns;
					}
					lineFetched = "";
					for (Ctr2 = 0; Ctr2 < rowColumnCnt; Ctr2++) {
						Cell cell = rowData.getCell(Ctr2);
						String columnName = (String) fileColumnsNames.get(Ctr2);
						String columnSpecificDataType = dataType.get(columnNamelst.indexOf(columnName));
						String specificData = "";
						long specificDataMod = 0;
						if (ValidationUtil.isValid(cell) && ValidationUtil.isValid(evaluator.evaluate(cell))) {
							CellType cellType = evaluator.evaluate(cell).getCellType();
							if ("BLOB".equalsIgnoreCase(columnSpecificDataType)
									|| "CLOB".equalsIgnoreCase(columnSpecificDataType)) {
								specificData = cell.getRichStringCellValue().toString();
							}else if (cellType == CellType.STRING) { 
//							else if (cellType == Cell.CELL_TYPE_STRING) {
								specificData = cell.getStringCellValue();
							} else if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) { 
//							else if (cellType == Cell.CELL_TYPE_NUMERIC || cellType == Cell.CELL_TYPE_FORMULA) {
								if (DateUtil.isCellDateFormatted(cell)) {
									if ("DATE".equalsIgnoreCase(columnSpecificDataType)) {
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
									} else {
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										} catch (Exception e) {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
											specificData = String.valueOf(sdf.format(cell.getDateCellValue()));
										}
									}
								} else {
									specificData = String.valueOf(cell.getNumericCellValue());
									if (specificData.contains("E")) {
										double num = rowData.getCell(Ctr2).getNumericCellValue();
										DecimalFormat pattern = new DecimalFormat("##########");
										NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
										specificData = testNumberFormat.format(num).replaceAll(",", "");
									}
									String[] TempspecificData = specificData.split(Pattern.quote("."));
									String TempspecificData1 = TempspecificData[0];
									String TempspecificData2 = "";
									if (TempspecificData.length > 1)
										TempspecificData2 = TempspecificData[1];
									int part = 0;
									try {
										part = Integer.valueOf(TempspecificData2);
										if (part <= 0)
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									} catch (Exception e) {
										if (TempspecificData2.equalsIgnoreCase("0"))
											specificData = String.valueOf(
													(int) Math.round(rowData.getCell(Ctr2).getNumericCellValue()));
									}

								}
							}
						} else {
							specificData = " ";
						}
						if (lineFetched == "" || lineFetched == null || lineFetched.equalsIgnoreCase("")) {
							if (ValidationUtil.isValid(specificDataMod) && specificDataMod != 0)
								lineFetched = lineFetched + specificDataMod;
							else
								lineFetched = lineFetched + specificData.replaceAll("'", "").replaceAll("\"", "");
						} else {
							if (ValidationUtil.isValid(specificDataMod) && specificDataMod != 0)
								lineFetched = lineFetched + "	" + specificDataMod;
							else
								lineFetched = lineFetched + "	"
										+ specificData.replaceAll("'", "").replaceAll("\"", "");
						}
					}
					if (lineFetched == null) {
						break;
					} else {
						uploadData = new String[rowColumnCnt];
						String[] uploadDataTempArr = lineFetched.split("\t");
						for (int a = 0; a < uploadData.length; a++) {
							if (a < uploadDataTempArr.length)
								uploadData[a] = uploadDataTempArr[a];
							else
								uploadData[a] = "";
						}
						lineCtr++;
						if (valueYes.equals(verificationRequired)) {
							retVal = doVerifInsUpdOperations(lineCtr);
						} else {
							if (uploadTableName.contains("FEED_STG")) {
								retVal = doInsertUpdateOperationsForStagingTable(lineCtr);
							} else {
								retVal = doInsertUpdateOperations(lineCtr);
							}
						}
						if (retVal == ERRONEOUS_EXIT)
							finalReturnValue = ERRONEOUS_EXIT;

						totalInputFileRecords++;

						/*
						 * wordsArray = lineFetched.split("\n"); lineFetched=""; for(String each :
						 * wordsArray){ lines.add(each); }
						 */
					}
				} else {
					invalidRowCount++;
					if (invalidRowCount == 2)
						break;
				}
			}
			fileReader1.close();

			if (finalReturnValue == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			if ("FIN_ADJ_REMAPS".equalsIgnoreCase(uploadTableName)
					|| "FIN_ADJ_REMAPS_PEND".equalsIgnoreCase(uploadTableName)) {
				sqlStatement = "Update " + uploadTableName + " Set Fin_Adj_Remaps_Status = 10 Where Record_Indicator = "
						+ FIN_ADJ_REMAP_REC_IND;
				try {
					stmt.executeUpdate(sqlStatement);
				} catch (SQLException ex) {
					sqlLogWriter("Error updating " + uploadTableName + "for line Number " + lineNumber + ":"
							+ ex.getMessage());
					logWriter(sqlStatement, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
				/* Update as 1 - Active Pending */
				sqlStatement = "Update " + uploadTableName + " Set Record_Indicator = 1 Where Record_Indicator = "
						+ FIN_ADJ_REMAP_REC_IND;
				try {
					stmt.executeUpdate(sqlStatement);
				} catch (SQLException ex) {
					sqlLogWriter("Error updating " + uploadTableName + "for line Number " + lineNumber + ":"
							+ ex.getMessage());
					logWriter(sqlStatement, uploadLogFileName);
					finalReturnValue = ERRONEOUS_EXIT;
				}
				con.commit();
			}
			if (valueYes.equalsIgnoreCase(verificationRequired) && retVal == SUCCESSFUL_EXIT) {
				sqlStatement = "Update " + uploadTableName + " Set RECORD_INDICATOR = 1 Where Exists (Select 1 From "
						+ originalTableName + " Where ";
				countStmt = "Select Count(1) as count From " + uploadTableName + ", " + originalTableName + " Where ";
				for (Ctr1 = 0; Ctr1 < totalInputFileColumns; Ctr1++) {
					if (valueNo.equalsIgnoreCase(fileColumnsIndexColumn.get(Ctr1).toString()))
						continue;
					if (theAndFlag == 'Y') {
						sqlStatement = sqlStatement + "And ";
						countStmt = countStmt + "And ";
					}
					tempStr = "RTrim(" + uploadTableName + "." + fileColumnsNames.get(Ctr1) + ") = RTrim("
							+ originalTableName + "." + fileColumnsNames.get(Ctr1) + ") ";
					sqlStatement = sqlStatement + tempStr;
					countStmt = countStmt + tempStr;
					theAndFlag = 'Y';
				}
				sqlStatement = sqlStatement + " And " + uploadTableName + ".RECORD_INDICATOR = 4) ";

				recsCount = getCount();
				if (recsCount < 0)
					return (ERRONEOUS_EXIT);
				if (recsCount > 0) {
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException ex) {
						sqlLogWriter("Error trying to update pending table (Update #1) for the uploaded data :"
								+ ex.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
						finalReturnValue = ERRONEOUS_EXIT;
					}
				}
				countStmt = "Select Count(1) as count  From " + uploadTableName + " Where RECORD_INDICATOR = 4 ";
				recsCount = getCount();

				if (recsCount < 0)
					return (ERRONEOUS_EXIT);

				if (recsCount > 0) {
					sqlStatement = " Update " + uploadTableName
							+ " Set RECORD_INDICATOR = 2 Where RECORD_INDICATOR = 4 ";
					try {
						stmt.executeUpdate(sqlStatement);
					} catch (SQLException e) {
						sqlLogWriter("Error trying to update pending table (Update #2) for the uploaded data :"
								+ e.getMessage());
						logWriter(sqlStatement, uploadLogFileName);
						retVal = ERRONEOUS_EXIT;
						finalReturnValue = ERRONEOUS_EXIT;
					}
				}
			}
			if (retVal == ERRONEOUS_EXIT)
				finalReturnValue = ERRONEOUS_EXIT;
			return retVal;

		} catch (Exception e) {
			e.printStackTrace();
			writeToMainLogFile("uploadFileData() :" + e.getMessage());
			doFinishingSteps();
			return ERRONEOUS_EXIT;
		}
	}

	public String getSourcesubmissionCheck(String country, String leBook, String templateId, String reportingDate)
			throws DataAccessException {
		String value = "";
		if (!ValidationUtil.isValid(country)) {
			return null;
		}
		try {
//			 sql = "SELECT STATUS FROM RG_TEMPLATES_HEADER  WHERE COUNTRY = '" + country + "' AND LE_BOOK = '"
//					+ leBook + "'AND TEMPLATE_ID = '" + templateId + "' and SUBMISSION_DATE ="+ dbFunctionFormats("DATE_FORMAT", reportingDate, INPUT_DATE_FORMAT);
////							+ " to_date('" + reportingDate
////					+ "','DD-MM-RRRR')"
//					;

			String sql = " SELECT STATUS, " + "REPORTING_DATE , " + " SUBMISSION_DATE FROM (" + " SELECT STATUS,"
					+ dbFunctionFormats("RG_DATE_FORMAT", "REPORTING_DATE", INPUT_DATE_FORMAT) + "REPORTING_DATE , "
					+ dbFunctionFormats("RG_DATE_FORMAT", "SUBMISSION_DATE", INPUT_DATE_FORMAT)
					+ " SUBMISSION_DATE  FROM RG_TEMPLATES_HEADER  WHERE COUNTRY = '" + country + "'AND LE_BOOK = '"
					+ leBook + "' AND TEMPLATE_ID ='" + templateId + "' " + " UNION " + " SELECT STATUS,"
					+ dbFunctionFormats("RG_DATE_FORMAT", "REPORTING_DATE", INPUT_DATE_FORMAT) + "REPORTING_DATE , "
					+ dbFunctionFormats("RG_DATE_FORMAT", "SUBMISSION_DATE", INPUT_DATE_FORMAT)
					+ " SUBMISSION_DATE  FROM RG_TEMPLATES_HEADER_HIS  WHERE COUNTRY = '" + country + "'AND LE_BOOK = '"
					+ leBook + "' AND TEMPLATE_ID ='" + templateId + "')T1 WHERE REPORTING_DATE = "
					+ dbFunctionFormats("DATE_FORMAT", reportingDate, INPUT_DATE_FORMAT) + " AND STATUS ='SS'";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				value = rs.getString("STATUS");
				submissionDate = rs.getString("SUBMISSION_DATE");
				templateDate = rs.getString("REPORTING_DATE");
			}
		} catch (SQLException e) {
			sqlLogWriter("Error While STATUS from  RG_TEMPLATES_HEADER for " + country + " " + leBook + e.getMessage());
			e.printStackTrace();
		}
		return value;
	}

	private int genericUploadRg() {
		String tempStr = "";
		try {
			colName = findVisionVariableValue("UPLOAD_COLUMN_NAME");
			overideFlag = findVisionVariableValue("UPLOAD_OVERRIDE");
			country = findVisionVariableValue("DEFAULT_COUNTRY");
			leBook = findVisionVariableValue("DEFAULT_LE_BOOK");
			/* Check if upload is allowed for this table */
			valvalue = checkIfUploadIsRG();
			retVal = checkIfUploadIsAllowed();
			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			/* Check if the table has been locked by some build process */
			retVal = checkForTableLock();
			if (retVal == ERRONEOUS_EXIT) {
				writeToMainLogFile("The table [" + uploadTableName
						+ "] has been locked by build process.  Cannot proceed now with Upload - FYI.\n");
				return ERRONEOUS_EXIT;
			}

			/* Lock the table to proceed with Upload */
			retVal = lockTableForUpload();
			if (retVal == ERRONEOUS_EXIT) {
				writeSQLErrToMainLogFile("The table [" + uploadTableName
						+ "] could not be locked for XL Upload. Cannot proceed now with upload !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			/* Set the status to In-Progress mode for the upload request */
			logWriter("Set the status to In-Progress mode for the upload request:" + uploadTableName,
					uploadLogFileName);
//			System.out.println("Set the status to In-Progress mode  for the upload request:"+uploadTableName);

			retVal = updateUploadStatus(STATUS_IN_PROGRESS);
			if (retVal == ERRONEOUS_EXIT) {
				doFinishingSteps();
				return ABORT_EXECUTION;
			}
			con.commit();

			finalReturnValue = SUCCESSFUL_EXIT;

			writeToMainLogFile("Uploading to the table [" + uploadTableName + "]");
			EXCEL_FILE_NAME = uploadTableName + "_" + makerId + "_" + GetCurrentDateTime() + ".err";

			logWriter("uploadLogFileName : " + uploadLogFileName, uploadLogFileName);
			try {
				logfile = new FileWriter(uploadLogFileName);// Open a Log file
															// in Write mode
				bufferedWriter = new BufferedWriter(logfile);
				logfile.close();
				bufferedWriter.close();

			} catch (Exception e) {
				writeToMainLogFile("Error opening log file [" + uploadLogFileName + "] for logging process steps :\n "
						+ e.getMessage());
				writeToMainLogFile("Aborting further processing of this request - FYI:" + e.getMessage());
				return (ERRONEOUS_EXIT);
			}
			uploadFile = xluploadDataFilePath + uploadFileName + "_" + makerId + ".xlsx";

			try {
				/*
				 * bufferedReader1 = new BufferedReader(new InputStreamReader(new
				 * FileInputStream(uploadFile),"UTF8"));
				 */
				fileReader = new FileInputStream(new File(uploadFile));
			} catch (Exception e) {
				writeToMainLogFile(
						"Error opening input file name [" + tempStr + "]\nSystem Msg [" + e.getMessage() + "]\n");
				logWriter("Error opening input file name [" + tempStr + "]\nSystem Msg [" + e.getMessage() + "]\n",
						uploadLogFileName);
				writeToMainLogFile("Aborting further processing of this request - FYI:" + e.getMessage());
				logWriter("Aborting further processing of this request - FYI:" + e.getMessage(), uploadLogFileName);

				retVal = updateUploadStatus(STATUS_FAILURE);

				if (retVal == ERRONEOUS_EXIT)
					return ABORT_EXECUTION;

				writeToMainLogFile("Upload to the table [" + uploadTableName + "] Failed !!!!!!\n");
				return ERRONEOUS_EXIT;
			}
			logWriter(" Vision Upload Started \t  Upload File Name [" + uploadFileName + "]\t Upload Sequence: ["
					+ uploadSequence + "] Resubmission : [" + overideFlag + "]\n", uploadLogFileName);
			logWriter("------------------------------------------------------------\n", uploadLogFileName);
			logWriter("Upload File Name\t:[" + uploadFileName + "]\n", uploadLogFileName);
			originalTableName = uploadTableName;

			/*
			 * Decide the table name, depending on whether the specified table or the bulk
			 * table needs to be hit, with the upload
			 */
			if (valueYes.equalsIgnoreCase(verificationRequired)) {
				uploadTableName = uploadTableName + "_PEND";
			}
			logWriter("Table name obtained \t:[" + originalTableName + "]\n", uploadLogFileName);

			if (DEBUG_MODE == YES)
				logWriter("Upload Table Name\t:[" + uploadTableName + "]\n", uploadLogFileName);

			retVal = checkForTblExistence();

			if (retVal == ERRONEOUS_EXIT)
				return ERRONEOUS_EXIT;

			retVal = loadTxtFileColumnNames(uploadFile);
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = validateTblCols();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = chkColCntForEachRow();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			retVal = fetchTableIndex();
			if (retVal == ERRONEOUS_EXIT) {
				logWriter("\t Execution aborted !!", uploadLogFileName);
				return ERRONEOUS_EXIT;
			}
			logWriter("Preliminary validations are thru - FYI\n", uploadLogFileName);

			TAB_FLAG = 'Y';

			retVal = uploadFileDataRG();
			TAB_FLAG = 'N';

			if (finalReturnValue == ERRONEOUS_EXIT) {
				con.rollback();
			} else {
				con.commit();
			}
			printSummaryReport(finalReturnValue);
			return (finalReturnValue);
		} catch (Exception e) {
//			System.out.println("genericUpload : " + e.getMessage());
			e.printStackTrace();
			return ERRONEOUS_EXIT;
		}
	}

	private int checkAndHandleExistingRecords(String tableName) {
		int combinedCount = 0;
		retVal = SUCCESSFUL_EXIT;
		String table1 = tableName;
		String table2 = tableName + "_PEND";
		String countQuery = "";

		try {
			countQuery = "SELECT COUNT(*) AS CNT FROM ( " + "SELECT * FROM " + table1 +
//	                " WHERE COUNTRY = '" + country + "' AND LE_BOOK = '" + leBook + "' AND TABLE_ATTRIBUTE1 = '" + tableAttribute1 + "' " +
					" UNION ALL " + "SELECT * FROM " + table2 +
//	                " WHERE COUNTRY = '" + country + "' AND LE_BOOK = '" + leBook + "' AND TABLE_ATTRIBUTE1 = '" + tableAttribute1 + "' " +
					") t1";

			rs = stmt.executeQuery(countQuery);
			if (rs.next()) {
				combinedCount = rs.getInt("CNT");
			}

			if (combinedCount > 0) {
				logWriter("Found " + combinedCount + " existing records. ", uploadLogFileName);

				if ("Y".equalsIgnoreCase(overideFlag)) {
					// Truncate matching records in both tables
					String delQuery1 = "DELETE FROM " + table1;
					String delQuery2 = "DELETE FROM " + table2;
//					String delAudQuery2 = "DELETE FROM " + table1+"_AUDIT";
					try {
						int deleted1 = stmt.executeUpdate(delQuery1);
						int deleted2 = stmt.executeUpdate(delQuery2);
//						int delAud = stmt.executeUpdate(delAudQuery2);
						logWriter("Deleted " + deleted1 + " rows from " + table1 + " and " + deleted2 + " rows from "
								+ table2 + ".", uploadLogFileName);
					} catch (SQLException ex) {
						sqlLogWriter("Error deleting records: " + ex.getMessage());
						return ERRONEOUS_EXIT;
					}
				} else {
					logWriter("Resubmission  :[" + overideFlag + "]", uploadLogFileName);
					return ERRONEOUS_EXIT;
				}
			}
		} catch (Exception e) {
			sqlLogWriter("Error checking existing records: " + e.getMessage());
			return ERRONEOUS_EXIT;
		}

		return retVal;
	}

}
