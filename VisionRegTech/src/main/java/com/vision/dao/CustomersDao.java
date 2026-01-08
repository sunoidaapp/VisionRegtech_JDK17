package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CustomersVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class CustomersDao extends AbstractDao<CustomersVb> {

	String customerStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.CUSTOMER_STATUS",
			"CUSTOMER_STATUS_DESC");
	String customerStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.CUSTOMER_STATUS",
			"CUSTOMER_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	// Use SpEL to initialize customersTable directly
	@Value("#{ '${app.cloud}' == 'Y' ? 'CRS_CUSTOMERS' : 'CUSTOMERS' }")
	private String Customers;
	@Value("#{ '${app.cloud}' == 'Y' ? 'CRS_CUSTOMER_EXTRAS' : 'CUSTOMER_EXTRAS' }")
	private String CustomersExtras;

	@Autowired
	CustomerManualDao customerManualDao;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomersVb customersVb = new CustomersVb();
				customersVb.setCountry(rs.getString("COUNTRY"));
				customersVb.setLeBook(rs.getString("LE_BOOK"));
				customersVb.setCustomerId(rs.getString("CUSTOMER_ID"));
				if (rs.getString("CUSTOMER_ACRONYM") != null) {
					customersVb.setCustomerAcronym(rs.getString("CUSTOMER_ACRONYM"));
				} else {
					customersVb.setCustomerAcronym("");
				}
				customersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				customersVb.setMaker(rs.getLong("MAKER"));
				customersVb.setVerifier(rs.getLong("VERIFIER"));
				customersVb.setCustomerOpenDate(rs.getString("CUSTOMER_OPEN_DATE"));
				customersVb.setCustomerName(rs.getString("CUSTOMER_NAME"));
				customersVb.setVisionOuc(rs.getString("VISION_OUC"));
				customersVb.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
				customersVb.setVisionSbu(rs.getString("VISION_SBU"));
				if (rs.getString("GLOBAL_CUSTOMER_ID") != null) {
					customersVb.setGlobalCustomerId(rs.getString("GLOBAL_CUSTOMER_ID"));
				} else {
					customersVb.setGlobalCustomerId("");
				}
				customersVb.setNaicsCode(rs.getString("NAICS_CODE"));
				customersVb.setCbOrgCodeAt(rs.getInt("CB_ORG_CODE_AT"));
				if (rs.getString("CB_ORG_CODE") != null) {
					customersVb.setCbOrgCode(rs.getString("CB_ORG_CODE"));
				} else {
					customersVb.setCbOrgCode("");
				}
				customersVb.setCbEconomicActCodeAt(rs.getInt("CB_ECONOMIC_ACT_CODE_AT"));
				if(rs.getString("CB_ECONOMIC_ACT_CODE")!=null) {
				customersVb.setCbEconomicActCode(rs.getString("CB_ECONOMIC_ACT_CODE"));
				}else {
					customersVb.setCbEconomicActCode("");
				}
				if (rs.getString("CB_DOMICILE") != null) {
					customersVb.setCbDomicile(rs.getString("CB_DOMICILE"));
				} else {
					customersVb.setCbDomicile("");
				}
				customersVb.setCbNationality(rs.getString("CB_NATIONALITY"));
				customersVb.setCbResidence(rs.getString("CB_RESIDENCE"));
				customersVb.setCbMajorPartyIndicator(rs.getString("CB_MAJOR_PARTY_INDICATOR"));
				customersVb.setCustomerIdTypeAt(rs.getInt("CUSTOMER_ID_TYPE_AT"));
				if (rs.getString("CUSTOMER_ID_TYPE") != null) {
					customersVb.setCustomerIdType(rs.getString("CUSTOMER_ID_TYPE"));
				} else {
					customersVb.setCustomerIdType("");
				}
				if (rs.getString("ID_DETAILS") != null) {
					customersVb.setIdDetails(rs.getString("ID_DETAILS"));
				} else {
					customersVb.setIdDetails("");
				}
				customersVb.setPrimaryCid(rs.getString("PRIMARY_CID"));
				customersVb.setParentCid(rs.getString("PARENT_CID"));
				customersVb.setUltimateParent(rs.getString("ULTIMATE_PARENT"));
				if (rs.getString("CUSTOMER_HIERARCHY_1") != null) {
					customersVb.setCustomerHierarchy1(rs.getString("CUSTOMER_HIERARCHY_1"));
				} else {
					customersVb.setCustomerHierarchy1("");
				}
				if (rs.getString("CUSTOMER_HIERARCHY_2") != null) {
					customersVb.setCustomerHierarchy2(rs.getString("CUSTOMER_HIERARCHY_2"));
				} else {
					customersVb.setCustomerHierarchy2("");
				}
				if (rs.getString("CUSTOMER_HIERARCHY_3") != null) {
					customersVb.setCustomerHierarchy3(rs.getString("CUSTOMER_HIERARCHY_3"));
				} else {
					customersVb.setCustomerHierarchy3("");
				}
				customersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER"));
				customersVb.setCreditClassificationAt(rs.getInt("CREDIT_CLASSIFICATION_AT"));
				customersVb.setCreditClassification(rs.getString("CREDIT_CLASSIFICATION"));
				customersVb.setExternalRiskRatingAt(rs.getInt("EXTERNAL_RISK_RATING_AT"));
				customersVb.setExternalRiskRating(rs.getString("EXTERNAL_RISK_RATING"));
				customersVb.setObligorRiskRatingAt(rs.getInt("OBLIGOR_RISK_RATING_AT"));
				customersVb.setObligorRiskRating(rs.getString("OBLIGOR_RISK_RATING"));
				customersVb.setRelatedPartyAt(rs.getInt("RELATED_PARTY_AT"));
				customersVb.setRelatedParty(rs.getString("RELATED_PARTY"));
				customersVb.setCustomerTieringAt(rs.getInt("CUSTOMER_TIERING_AT"));
				customersVb.setCustomerTiering(rs.getString("CUSTOMER_TIERING"));
				customersVb.setSalesAcquisitionChannelAt(rs.getInt("SALES_ACQUISITION_CHANNEL_AT"));
				customersVb.setSalesAcquisitionChannel(rs.getString("SALES_ACQUISITION_CHANNEL"));
				if (rs.getString("MARKETING_CAMPAIGN") != null) {
					customersVb.setMarketingCampaign(rs.getString("MARKETING_CAMPAIGN"));
				} else {
					customersVb.setMarketingCampaign("");
				}
				if (rs.getString("CROSSSALE_REFER_ID") != null) {
					customersVb.setCrosssaleReferId(rs.getString("CROSSSALE_REFER_ID"));
				} else {
					customersVb.setCrosssaleReferId("");
				}
				customersVb.setCustomerIndicator(rs.getString("CUSTOMER_INDICATOR"));
				customersVb.setCustomerIndicatorAt(rs.getInt("CUSTOMER_INDICATOR_AT"));
				customersVb.setNumOfAccounts(rs.getString("NUM_OF_ACCOUNTS"));
				if (rs.getString("CUSTOMER_ATTRIBUTE_1") != null) {
					customersVb.setCustomerAttribute1(rs.getString("CUSTOMER_ATTRIBUTE_1"));
				} else {
					customersVb.setCustomerAttribute1("");
				}
				if (rs.getString("CUSTOMER_ATTRIBUTE_2") != null) {
					customersVb.setCustomerAttribute2(rs.getString("CUSTOMER_ATTRIBUTE_2"));
				} else {
					customersVb.setCustomerAttribute2("");
				}
				if (rs.getString("CUSTOMER_ATTRIBUTE_3") != null) {
					customersVb.setCustomerAttribute3(rs.getString("CUSTOMER_ATTRIBUTE_3"));
				} else {
					customersVb.setCustomerAttribute3("");
				}
				customersVb.setCustomerSexAT(rs.getInt("CUSTOMER_SEX_AT"));
				customersVb.setCustomerSex(rs.getString("CUSTOMER_SEX"));
				if (rs.getString("COMM_ADDRESS_1") != null) {
					customersVb.setCommAddress1(rs.getString("COMM_ADDRESS_1"));
				} else {
					customersVb.setCommAddress1("");
				}
				if (rs.getString("COMM_ADDRESS_2") != null) {
					customersVb.setCommAddress2(rs.getString("COMM_ADDRESS_2"));
				} else {
					customersVb.setCommAddress2("");
				}
				customersVb.setCommCity(rs.getString("COMM_CITY"));
				customersVb.setCommState(rs.getString("COMM_STATE"));
				if (rs.getString("COMM_PIN_CODE") != null) {
					customersVb.setCommPinCode(rs.getString("COMM_PIN_CODE"));
				} else {
					customersVb.setCommPinCode("");
				}
				if (rs.getString("PHONE_NUMBER") != null) {
					customersVb.setPhoneNumber(rs.getString("PHONE_NUMBER"));
				} else {
					customersVb.setPhoneNumber("");
				}
				customersVb.setCommStateAt(rs.getString("COMM_STATE_AT"));
				customersVb.setCommCityAt(rs.getString("COMM_CITY_AT"));
				customersVb.setCustomerStatusNt(rs.getInt("CUSTOMER_STATUS_NT"));
				customersVb.setDbStatus(rs.getInt("CUSTOMER_STATUS"));
				customersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				customersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				customersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				customersVb.setDateCreation(rs.getString("DATE_CREATION"));
				customersVb.setCrsFlag(rs.getString("CRS_FLAG"));
				customersVb.setFatcaFlag(rs.getString("FATCA_FLAG"));
				customersVb.setCustomerStatus(rs.getInt("CUSTOMER_STATUS"));
				customersVb.setStatusDesc(rs.getString("CUSTOMER_STATUS_DESC"));
				customersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				customersVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				customersVb.setMakerName(rs.getString("MAKER_NAME"));
				customersVb.setVerifierName(rs.getString("VERIFIER_NAME"));

				customersVb.setCbDomicileDesc(rs.getString("CB_DOMICILE_DESC"));
				customersVb.setCbNationalityDesc(rs.getString("CB_NATIONALITY_DESC"));
				customersVb.setCbResidenceDesc(rs.getString("CB_RESIDENCE_DESC"));
				customersVb.setNaicsCodeDesc(rs.getString("NAICS_CODE_DESC"));
				customersVb.setPrimaryCidDesc(rs.getString("PRIMARY_CID_DESC"));
				customersVb.setParentCidDesc(rs.getString("PARENT_CID_DESC"));
				customersVb.setUltimateParentDesc(rs.getString("ULTIMATE_PARENT_DESC"));
				customersVb.setAccountOfficerDesc(rs.getString("ACCOUNT_OFFICER_DESC"));
				customersVb.setCustomerAttribute1Desc(rs.getString("CUSTOMER_ATTRIBUTE_1_DESC"));
				customersVb.setCustomerAttribute2Desc(rs.getString("CUSTOMER_ATTRIBUTE_2_DESC"));
				customersVb.setCustomerAttribute3Desc(rs.getString("CUSTOMER_ATTRIBUTE_3_DESC"));
				customersVb.setCustomerHierarchy1Desc(rs.getString("CUSTOMER_HIERARCHY_1_DESC"));
				customersVb.setCustomerHierarchy2Desc(rs.getString("CUSTOMER_HIERARCHY_2_DESC"));
				customersVb.setCustomerHierarchy3Desc(rs.getString("CUSTOMER_HIERARCHY_3_DESC"));
				customersVb.setVisionOucDesc(rs.getString("VISION_OUC_DESC"));
				customersVb.setVisionSbuDesc(rs.getString("VISION_SBU_DESC"));
				customersVb.setCrsOverRide(rs.getString("CRS_OVERRIDE"));
				customersVb.setFatcaOverRide(rs.getString("FATCA_OVERRIDE"));
				if (customersVb.getCrsOverRide().equalsIgnoreCase("I")) {
					customersVb.setCrsOverRideDesc("Included");
				} else if (customersVb.getCrsOverRide().equalsIgnoreCase("E")) {
					customersVb.setCrsOverRideDesc("Excluded");
				} else {
					customersVb.setCrsOverRideDesc("Not Applicable");
				}
				if (customersVb.getFatcaOverRide().equalsIgnoreCase("I")) {
					customersVb.setFatcaOverRideDesc("Included");
				} else if (customersVb.getFatcaOverRide().equalsIgnoreCase("E")) {
					customersVb.setFatcaOverRideDesc("Excluded");
				} else {
					customersVb.setFatcaOverRideDesc("Not Applicable");
				}
				
				customersVb.setDualNationality1(rs.getString("DUAL_NATIONALITY_1"));
				if (rs.getString("DUAL_NATIONALITY_2") != null) {
					customersVb.setDualNationality2(rs.getString("DUAL_NATIONALITY_2"));
				} else {
					customersVb.setDualNationality2("");
				}
				if (rs.getString("DUAL_NATIONALITY_3") != null) {
					customersVb.setDualNationality3(rs.getString("DUAL_NATIONALITY_3"));
				} else {
					customersVb.setDualNationality3("");
				}
				customersVb.setSsn(rs.getString("SSN"));
				customersVb.setCustomerTin(rs.getString("CUSTOMER_TIN"));
				customersVb.setSubSegment(rs.getString("SUB_SEGMENT"));
				customersVb.setComplianceStatus(rs.getString("COMPLIANCE_STATUS"));
				customersVb.setJointAccount(rs.getString("JOINT_ACCOUNT"));
				if (rs.getString("PHONE_NUMBER_02") != null) {
					customersVb.setPhoneNumber2(rs.getString("PHONE_NUMBER_02"));
				} else {
					customersVb.setPhoneNumber2("");
				}
				if (rs.getString("PHONE_NUMBER_03") != null) {
					customersVb.setPhoneNumber3(rs.getString("PHONE_NUMBER_03"));
				} else {
					customersVb.setPhoneNumber3("");
				}
				if (rs.getString("PHONE_NUMBER_04") != null) {
					customersVb.setPhoneNumber4(rs.getString("PHONE_NUMBER_04"));
				} else {
					customersVb.setPhoneNumber4("");
				}
				if (rs.getString("PHONE_NUMBER_05") != null) {
					customersVb.setPhoneNumber5(rs.getString("PHONE_NUMBER_05"));
				} else {
					customersVb.setPhoneNumber5("");
				}
				if (rs.getString("PHONE_NUMBER_06") != null) {
					customersVb.setPhoneNumber6(rs.getString("PHONE_NUMBER_06"));
				} else {
					customersVb.setPhoneNumber6("");
				}
				if (rs.getString("PHONE_NUMBER_07") != null) {
					customersVb.setPhoneNumber7(rs.getString("PHONE_NUMBER_07"));
				} else {
					customersVb.setPhoneNumber7("");
				}
				if (rs.getString("COMM_ADDRESS_3") != null) {
					customersVb.setCommAddress3(rs.getString("COMM_ADDRESS_3"));
				} else {
					customersVb.setCommAddress3("");
				}
				return customersVb;
			}
		};
		return mapper;
	}

	protected RowMapper getQueryResultMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomersVb customersVb = new CustomersVb();
				customersVb.setCountry(rs.getString("COUNTRY"));
				customersVb.setLeBook(rs.getString("LE_BOOK"));
				customersVb.setCustomerId(rs.getString("CUSTOMER_ID"));
				customersVb.setCustomerAcronym(rs.getString("CUSTOMER_ACRONYM"));
				customersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				customersVb.setMaker(rs.getLong("MAKER"));
				customersVb.setVerifier(rs.getLong("VERIFIER"));
				customersVb.setCustomerOpenDate(rs.getString("CUSTOMER_OPEN_DATE"));
				customersVb.setCustomerName(rs.getString("CUSTOMER_NAME"));
				customersVb.setVisionOuc(rs.getString("VISION_OUC"));
				customersVb.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
				customersVb.setVisionSbu(rs.getString("VISION_SBU"));
				if (rs.getString("GLOBAL_CUSTOMER_ID") != null) {
					customersVb.setGlobalCustomerId(rs.getString("GLOBAL_CUSTOMER_ID"));
				} else {
					customersVb.setGlobalCustomerId("");
				}
				customersVb.setNaicsCode(rs.getString("NAICS_CODE"));
				customersVb.setCbOrgCodeAt(rs.getInt("CB_ORG_CODE_AT"));
				customersVb.setCbOrgCode(rs.getString("CB_ORG_CODE"));
				customersVb.setCbEconomicActCodeAt(rs.getInt("CB_ECONOMIC_ACT_CODE_AT"));
				if (rs.getString("CB_ECONOMIC_ACT_CODE") != null) {
					customersVb.setCbEconomicActCode(rs.getString("CB_ECONOMIC_ACT_CODE"));
				} else {
					customersVb.setCbEconomicActCode("");
				}
				if(rs.getString("CB_DOMICILE")!=null) {
				customersVb.setCbDomicile(rs.getString("CB_DOMICILE"));
				}else {
					customersVb.setCbDomicile("");
				}
				customersVb.setCbNationality(rs.getString("CB_NATIONALITY"));
				customersVb.setCbResidence(rs.getString("CB_RESIDENCE"));
				customersVb.setCbMajorPartyIndicator(rs.getString("CB_MAJOR_PARTY_INDICATOR"));
				customersVb.setCustomerIdTypeAt(rs.getInt("CUSTOMER_ID_TYPE_AT"));
				if (rs.getString("CUSTOMER_ID_TYPE") != null) {
					customersVb.setCustomerIdType(rs.getString("CUSTOMER_ID_TYPE"));
				} else {
					customersVb.setCustomerIdType("");
				}
				if (rs.getString("ID_DETAILS") != null) {
					customersVb.setIdDetails(rs.getString("ID_DETAILS"));
				} else {
					customersVb.setIdDetails("");
				}
				customersVb.setPrimaryCid(rs.getString("PRIMARY_CID"));
				customersVb.setParentCid(rs.getString("PARENT_CID"));
				customersVb.setUltimateParent(rs.getString("ULTIMATE_PARENT"));
				if (rs.getString("CUSTOMER_HIERARCHY_1") != null) {
					customersVb.setCustomerHierarchy1(rs.getString("CUSTOMER_HIERARCHY_1"));
				} else {
					customersVb.setCustomerHierarchy1("");
				}
				if (rs.getString("CUSTOMER_HIERARCHY_2") != null) {
					customersVb.setCustomerHierarchy2(rs.getString("CUSTOMER_HIERARCHY_2"));
				} else {
					customersVb.setCustomerHierarchy2("");
				}
				if (rs.getString("CUSTOMER_HIERARCHY_3") != null) {
					customersVb.setCustomerHierarchy3(rs.getString("CUSTOMER_HIERARCHY_3"));
				} else {
					customersVb.setCustomerHierarchy3("");
				}
				customersVb.setAccountOfficer(rs.getString("ACCOUNT_OFFICER"));
				customersVb.setCreditClassificationAt(rs.getInt("CREDIT_CLASSIFICATION_AT"));
				customersVb.setCreditClassification(rs.getString("CREDIT_CLASSIFICATION"));
				customersVb.setExternalRiskRatingAt(rs.getInt("EXTERNAL_RISK_RATING_AT"));
				customersVb.setExternalRiskRating(rs.getString("EXTERNAL_RISK_RATING"));
				customersVb.setObligorRiskRatingAt(rs.getInt("OBLIGOR_RISK_RATING_AT"));
				customersVb.setObligorRiskRating(rs.getString("OBLIGOR_RISK_RATING"));
				customersVb.setRelatedPartyAt(rs.getInt("RELATED_PARTY_AT"));
				customersVb.setRelatedParty(rs.getString("RELATED_PARTY"));
				customersVb.setCustomerTieringAt(rs.getInt("CUSTOMER_TIERING_AT"));
				customersVb.setCustomerTiering(rs.getString("CUSTOMER_TIERING"));
				customersVb.setSalesAcquisitionChannelAt(rs.getInt("SALES_ACQUISITION_CHANNEL_AT"));
				customersVb.setSalesAcquisitionChannel(rs.getString("SALES_ACQUISITION_CHANNEL"));
				if (rs.getString("MARKETING_CAMPAIGN") != null) {
					customersVb.setMarketingCampaign(rs.getString("MARKETING_CAMPAIGN"));
				} else {
					customersVb.setMarketingCampaign("");
				}
				if (rs.getString("CROSSSALE_REFER_ID") != null) {
					customersVb.setCrosssaleReferId(rs.getString("CROSSSALE_REFER_ID"));
				} else {
					customersVb.setCrosssaleReferId("");
				}
				customersVb.setCustomerIndicator(rs.getString("CUSTOMER_INDICATOR"));
				customersVb.setCustomerIndicatorAt(rs.getInt("CUSTOMER_INDICATOR_AT"));
				customersVb.setNumOfAccounts(rs.getString("NUM_OF_ACCOUNTS"));
				if (rs.getString("CUSTOMER_ATTRIBUTE_1") != null) {
					customersVb.setCustomerAttribute1(rs.getString("CUSTOMER_ATTRIBUTE_1"));
				} else {
					customersVb.setCustomerAttribute1("");
				}
				if (rs.getString("CUSTOMER_ATTRIBUTE_2") != null) {
					customersVb.setCustomerAttribute2(rs.getString("CUSTOMER_ATTRIBUTE_2"));
				} else {
					customersVb.setCustomerAttribute2("");
				}
				if (rs.getString("CUSTOMER_ATTRIBUTE_3") != null) {
					customersVb.setCustomerAttribute3(rs.getString("CUSTOMER_ATTRIBUTE_3"));
				} else {
					customersVb.setCustomerAttribute3("");
				}
				customersVb.setCustomerSexAT(rs.getInt("CUSTOMER_SEX_AT"));
				customersVb.setCustomerSex(rs.getString("CUSTOMER_SEX"));
				if (rs.getString("COMM_ADDRESS_1") != null) {
					customersVb.setCommAddress1(rs.getString("COMM_ADDRESS_1"));
				} else {
					customersVb.setCommAddress1("");
				}
				if (rs.getString("COMM_ADDRESS_2") != null) {
					customersVb.setCommAddress2(rs.getString("COMM_ADDRESS_2"));
				} else {
					customersVb.setCommAddress2("");
				}
				customersVb.setCommCity(rs.getString("COMM_CITY"));
				customersVb.setCommState(rs.getString("COMM_STATE"));
				if (rs.getString("COMM_PIN_CODE") != null) {
					customersVb.setCommPinCode(rs.getString("COMM_PIN_CODE"));
				} else {
					customersVb.setCommPinCode("");
				}
				if (rs.getString("PHONE_NUMBER") != null) {
					customersVb.setPhoneNumber(rs.getString("PHONE_NUMBER"));
				} else {
					customersVb.setPhoneNumber("");
				}
				customersVb.setCommStateAt(rs.getString("COMM_STATE_AT"));
				customersVb.setCommCityAt(rs.getString("COMM_CITY_AT"));
				customersVb.setCustomerStatusNt(rs.getInt("CUSTOMER_STATUS_NT"));
				customersVb.setDbStatus(rs.getInt("CUSTOMER_STATUS"));
				customersVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				customersVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				customersVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				customersVb.setDateCreation(rs.getString("DATE_CREATION"));
				customersVb.setCrsFlag(rs.getString("CRS_FLAG"));
				customersVb.setFatcaFlag(rs.getString("FATCA_FLAG"));
				customersVb.setCustomerStatus(rs.getInt("CUSTOMER_STATUS"));
				customersVb.setStatusDesc(rs.getString("CUSTOMER_STATUS_DESC"));
				customersVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				customersVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				customersVb.setMakerName(rs.getString("MAKER_NAME"));
				customersVb.setVerifierName(rs.getString("VERIFIER_NAME"));

//				customersVb.setCbDomicileDesc(rs.getString("CB_DOMICILE_DESC"));
				customersVb.setCbNationalityDesc(rs.getString("CB_NATIONALITY_DESC"));
				customersVb.setCbResidenceDesc(rs.getString("CB_RESIDENCE_DESC"));
//				customersVb.setNaicsCodeDesc(rs.getString("NAICS_CODE_DESC"));
//				customersVb.setPrimaryCidDesc(rs.getString("PRIMARY_CID_DESC"));
//				customersVb.setParentCidDesc(rs.getString("PARENT_CID_DESC"));
//				customersVb.setUltimateParentDesc(rs.getString("ULTIMATE_PARENT_DESC"));
				customersVb.setAccountOfficerDesc(rs.getString("ACCOUNT_OFFICER_DESC"));
//				customersVb.setCustomerAttribute1Desc(rs.getString("CUSTOMER_ATTRIBUTE_1_DESC"));
//				customersVb.setCustomerAttribute2Desc(rs.getString("CUSTOMER_ATTRIBUTE_2_DESC"));
//				customersVb.setCustomerAttribute3Desc(rs.getString("CUSTOMER_ATTRIBUTE_3_DESC"));
//				customersVb.setCustomerHierarchy1Desc(rs.getString("CUSTOMER_HIERARCHY_1_DESC"));
//				customersVb.setCustomerHierarchy2Desc(rs.getString("CUSTOMER_HIERARCHY_2_DESC"));
//				customersVb.setCustomerHierarchy3Desc(rs.getString("CUSTOMER_HIERARCHY_3_DESC"));
				customersVb.setVisionOucDesc(rs.getString("VISION_OUC_DESC"));
				customersVb.setVisionSbuDesc(rs.getString("VISION_SBU_DESC"));
				customersVb.setCrsOverRide(rs.getString("CRS_OVERRIDE"));
				customersVb.setFatcaOverRide(rs.getString("FATCA_OVERRIDE"));
				if (customersVb.getCrsOverRide().equalsIgnoreCase("I")) {
					customersVb.setCrsOverRideDesc("Included");
				} else if (customersVb.getCrsOverRide().equalsIgnoreCase("E")) {
					customersVb.setCrsOverRideDesc("Excluded");
				} else {
					customersVb.setCrsOverRideDesc("Not Applicable");
				}
				if (customersVb.getFatcaOverRide().equalsIgnoreCase("I")) {
					customersVb.setFatcaOverRideDesc("Included");
				} else if (customersVb.getFatcaOverRide().equalsIgnoreCase("E")) {
					customersVb.setFatcaOverRideDesc("Excluded");
				} else {
					customersVb.setFatcaOverRideDesc("Not Applicable");
				}
				customersVb.setDualNationality1(rs.getString("DUAL_NATIONALITY_1"));
				customersVb.setDualNationality2(rs.getString("DUAL_NATIONALITY_2"));
				customersVb.setDualNationality3(rs.getString("DUAL_NATIONALITY_3"));

//				customersVb.setDualNationality1Desc(rs.getString("DUAL_NATIONALITY_1_DESC"));
//				customersVb.setDualNationality2Desc(rs.getString("DUAL_NATIONALITY_2_DESC"));
//				customersVb.setDualNationality3Desc(rs.getString("DUAL_NATIONALITY_3_DESC"));
				customersVb.setSsn(rs.getString("SSN"));
				customersVb.setCustomerTin(rs.getString("CUSTOMER_TIN"));
				customersVb.setSubSegment(rs.getString("SUB_SEGMENT"));
				customersVb.setComplianceStatus(rs.getString("COMPLIANCE_STATUS"));
				customersVb.setJointAccount(rs.getString("JOINT_ACCOUNT"));
				if (rs.getString("PHONE_NUMBER_02") != null) {
					customersVb.setPhoneNumber2(rs.getString("PHONE_NUMBER_02"));
				} else {
					customersVb.setPhoneNumber2("");
				}
				if (rs.getString("PHONE_NUMBER_03") != null) {
					customersVb.setPhoneNumber3(rs.getString("PHONE_NUMBER_03"));
				} else {
					customersVb.setPhoneNumber3("");
				}
				if (rs.getString("PHONE_NUMBER_04") != null) {
					customersVb.setPhoneNumber4(rs.getString("PHONE_NUMBER_04"));
				} else {
					customersVb.setPhoneNumber4("");
				}
				if (rs.getString("PHONE_NUMBER_05") != null) {
					customersVb.setPhoneNumber5(rs.getString("PHONE_NUMBER_05"));
				} else {
					customersVb.setPhoneNumber5("");
				}
				if (rs.getString("PHONE_NUMBER_06") != null) {
					customersVb.setPhoneNumber6(rs.getString("PHONE_NUMBER_06"));
				} else {
					customersVb.setPhoneNumber6("");
				}
				if (rs.getString("PHONE_NUMBER_07") != null) {
					customersVb.setPhoneNumber7(rs.getString("PHONE_NUMBER_07"));
				} else {
					customersVb.setPhoneNumber7("");
				}
				if (rs.getString("COMM_ADDRESS_3") != null) {
					customersVb.setCommAddress3(rs.getString("COMM_ADDRESS_3"));
				} else {
					customersVb.setCommAddress3("");
				}
				List<CustomerManualColVb> manualList = new ArrayList<CustomerManualColVb>();
				CustomerManualColVb cmvb = new CustomerManualColVb();
				cmvb.setEntity(customersVb.getEntity());
				cmvb.setCustomerId(customersVb.getCustomerId());
				manualList = customerManualDao.getQueryPopupResults(cmvb);
				customersVb.setManualList(manualList);
				return customersVb;
			}
		};
		return mapper;
	}

	public List<CustomersVb> getQueryPopupResults(CustomersVb dObj) {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (Select TAppr.COUNTRY,"
				+ "TAppr.LE_BOOK, TAppr.CUSTOMER_ID, TAppr.CUSTOMER_NAME," + "TAppr.CUSTOMER_ACRONYM, "
//				+ "To_Char(TAppr.CUSTOMER_OPEN_DATE, 'DD-MM-RRRR') CUSTOMER_OPEN_DATE, "
				+ dbFunctionFormats("TAPPR.CUSTOMER_OPEN_DATE", "DATE_FORMAT", null) + " CUSTOMER_OPEN_DATE," + " "
				+ "TAppr.VISION_OUC, TAppr.VISION_SBU_AT,"
				+ "TAPPR.VISION_SBU, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.VISION_SBU AND ALPHA_TAB = 3)   VISION_SBU_DESC"
				+ ", TAppr.GLOBAL_CUSTOMER_ID, TAppr.NAICS_CODE,"
				+ "TAppr.CB_ORG_CODE_AT, TAppr.CB_ORG_CODE, TAppr.CB_ECONOMIC_ACT_CODE_AT,"
				+ "TAppr.CB_ECONOMIC_ACT_CODE, TAppr.CB_DOMICILE,"
				+ "TAppr.CB_NATIONALITY, TAppr.CB_RESIDENCE, TAppr.CB_MAJOR_PARTY_INDICATOR,"
				+ "TAppr.CUSTOMER_ID_TYPE_AT, TAppr.CUSTOMER_ID_TYPE,"
				+ "TAppr.ID_DETAILS, TAppr.PRIMARY_CID, TAppr.PARENT_CID,"
				+ "TAppr.ULTIMATE_PARENT, TAppr.CUSTOMER_HIERARCHY_1,"
				+ "TAppr.CUSTOMER_HIERARCHY_2, TAppr.CUSTOMER_HIERARCHY_3,TAppr.ACCOUNT_OFFICER, "
				+ "(SELECT  AO_NAME  AS DESCRIPTION FROM ACCOUNT_OFFICERS WHERE COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK AND ACCOUNT_OFFICER = TAPPR.ACCOUNT_OFFICER ) ACCOUNT_OFFICER_DESC ,"
				+ "TAppr.CREDIT_CLASSIFICATION_AT,TAppr.CREDIT_CLASSIFICATION, TAppr.EXTERNAL_RISK_RATING_AT,"
				+ "TAppr.EXTERNAL_RISK_RATING, TAppr.OBLIGOR_RISK_RATING_AT,"
				+ "TAppr.OBLIGOR_RISK_RATING, TAppr.RELATED_PARTY_AT,"
				+ "TAppr.RELATED_PARTY, TAppr.CUSTOMER_TIERING_AT,"
				+ "TAppr.CUSTOMER_TIERING, TAppr.SALES_ACQUISITION_CHANNEL_AT,"
				+ "TAppr.SALES_ACQUISITION_CHANNEL, TAppr.MARKETING_CAMPAIGN,"
				+ "TAppr.CROSSSALE_REFER_ID, TAppr.CUSTOMER_INDICATOR, TAppr.CUSTOMER_INDICATOR_AT,"
				+ "TAppr.NUM_OF_ACCOUNTS, TAppr.CUSTOMER_ATTRIBUTE_1,"
				+ "TAppr.CUSTOMER_ATTRIBUTE_2, TAppr.CUSTOMER_ATTRIBUTE_3, TAppr.CUSTOMER_SEX_AT, TAppr.CUSTOMER_SEX,TAppr.COMM_ADDRESS_1,TAppr.COMM_ADDRESS_2 ,TAppr.COMM_ADDRESS_3, TAppr.COMM_CITY, "
				+ "TAppr.COMM_STATE, TAppr.COMM_PIN_CODE, TAppr.PHONE_NUMBER,TAppr.PHONE_NUMBER_02,TAppr.PHONE_NUMBER_03,TAppr.PHONE_NUMBER_04,TAppr.PHONE_NUMBER_05,TAppr.PHONE_NUMBER_06,TAppr.PHONE_NUMBER_07, TAppr.COMM_STATE_AT, TAppr.COMM_CITY_AT,"
				+ "TAppr.CUSTOMER_STATUS_NT, TAppr.CUSTOMER_STATUS," + customerStatusNtApprDesc
				+ " ,TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR," + RecordIndicatorNtApprDesc
				+ " ,TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS," + makerApprDesc + " , " + verifierApprDesc
				+ " , " + dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED," + " " + dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null)
				+ " DATE_CREATION ," + "TAppr.CRS_FLAG,TAppr.FATCA_FLAG,"
				+ "(SELECT NAICS_DESCRIPTION FROM NAICS_CODES WHERE NAICS_CODE = TAppr.NAICS_CODE) NAICS_CODE_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.CB_DOMICILE) CB_DOMICILE_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.CB_NATIONALITY)CB_NATIONALITY_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY =TAppr.CB_RESIDENCE)CB_RESIDENCE_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK  AND T1.CUSTOMER_ID = TAppr.PRIMARY_CID)PRIMARY_CID_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK AND T1.CUSTOMER_ID = TAppr.PARENT_CID) PARENT_CID_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME  FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK AND T1.CUSTOMER_ID = TAppr.ULTIMATE_PARENT)\r\n"
				+ " ULTIMATE_PARENT_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_HIERARCHY_1) CUSTOMER_HIERARCHY_1_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_HIERARCHY_2) CUSTOMER_HIERARCHY_2_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_HIERARCHY_3) CUSTOMER_HIERARCHY_3_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_ATTRIBUTE_1) CUSTOMER_ATTRIBUTE_1_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_ATTRIBUTE_2) CUSTOMER_ATTRIBUTE_2_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TAppr.CUSTOMER_ATTRIBUTE_3) CUSTOMER_ATTRIBUTE_3_DESC"
				+ ",(SELECT OUC_DESCRIPTION AS DESCRIPTION FROM   OUC_CODES t1  WHERE T1.COUNTRY = TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK AND t1.VISION_OUC = TAPPR.VISION_OUC AND OUC_STATUS = 0) VISION_OUC_DESC "
				+ ",TAPPR.FATCA_OVERRIDE,TAPPR.CRS_OVERRIDE, T1.DUAL_NATIONALITY_1, T1.DUAL_NATIONALITY_2, T1.DUAL_NATIONALITY_3,TAppr.SSN,TAPPR.CUSTOMER_TIN,TAPPR.SUB_SEGMENT,TAppr.COMPLIANCE_STATUS ,TAppr.JOINT_ACCOUNT "
				+ " FROM " + Customers + " TAPPR INNER JOIN " + CustomersExtras
				+ " T1 ON ( TAPPR.COUNTRY= T1.COUNTRY AND TAPPR.LE_BOOK= T1.LE_BOOK AND TAPPR.CUSTOMER_ID = T1.CUSTOMER_ID) "
				+ ") TAPPR ");

		String strWhereNotExists = new String(" Not Exists (Select TPend.COUNTRY,"
				+ "TPend.LE_BOOK, TPend.CUSTOMER_ID, TPend.CUSTOMER_ACRONYM, TPend.RECORD_INDICATOR, TPend.MAKER, TPend.VERIFIER"
				+ "  From CUSTOMERS_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY And TPend.LE_BOOK = TAppr.LE_BOOK And TPend.CUSTOMER_ID = TAppr.CUSTOMER_ID)");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (Select TPend.COUNTRY,"
				+ "TPend.LE_BOOK, TPend.CUSTOMER_ID, TPend.CUSTOMER_NAME," + "TPend.CUSTOMER_ACRONYM,"
//				+ " To_Char(TPend.CUSTOMER_OPEN_DATE, 'DD-MM-RRRR') CUSTOMER_OPEN_DATE, "
				+ dbFunctionFormats("TPend.CUSTOMER_OPEN_DATE", "DATE_FORMAT", null) + " CUSTOMER_OPEN_DATE," + " "
				+ "TPend.VISION_OUC, TPend.VISION_SBU_AT,"
				+ "TPend.VISION_SBU,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TPend.VISION_SBU AND ALPHA_TAB = 3)   VISION_SBU_DESC"
				+ ", TPend.GLOBAL_CUSTOMER_ID, TPend.NAICS_CODE,"
				+ "TPend.CB_ORG_CODE_AT, TPend.CB_ORG_CODE, TPend.CB_ECONOMIC_ACT_CODE_AT,"
				+ "TPend.CB_ECONOMIC_ACT_CODE, TPend.CB_DOMICILE,"
				+ "TPend.CB_NATIONALITY, TPend.CB_RESIDENCE, TPend.CB_MAJOR_PARTY_INDICATOR,"
				+ "TPend.CUSTOMER_ID_TYPE_AT, TPend.CUSTOMER_ID_TYPE,"
				+ "TPend.ID_DETAILS, TPend.PRIMARY_CID, TPend.PARENT_CID,"
				+ "TPend.ULTIMATE_PARENT, TPend.CUSTOMER_HIERARCHY_1,"
				+ "TPend.CUSTOMER_HIERARCHY_2, TPend.CUSTOMER_HIERARCHY_3,TPend.ACCOUNT_OFFICER, "
				+ "(SELECT  AO_NAME  AS DESCRIPTION FROM ACCOUNT_OFFICERS WHERE COUNTRY = TPend.COUNTRY AND LE_BOOK = TPend.LE_BOOK AND ACCOUNT_OFFICER = TPend.ACCOUNT_OFFICER ) ACCOUNT_OFFICER_DESC ,"
				+ "TPend.CREDIT_CLASSIFICATION_AT,TPend.CREDIT_CLASSIFICATION, TPend.EXTERNAL_RISK_RATING_AT,"
				+ "TPend.EXTERNAL_RISK_RATING, TPend.OBLIGOR_RISK_RATING_AT,"
				+ "TPend.OBLIGOR_RISK_RATING, TPend.RELATED_PARTY_AT,"
				+ "TPend.RELATED_PARTY, TPend.CUSTOMER_TIERING_AT,"
				+ "TPend.CUSTOMER_TIERING, TPend.SALES_ACQUISITION_CHANNEL_AT,"
				+ "TPend.SALES_ACQUISITION_CHANNEL, TPend.MARKETING_CAMPAIGN,"
				+ "TPend.CROSSSALE_REFER_ID, TPend.CUSTOMER_INDICATOR, TPend.CUSTOMER_INDICATOR_AT,"
				+ "TPend.NUM_OF_ACCOUNTS, TPend.CUSTOMER_ATTRIBUTE_1,"
				+ "TPend.CUSTOMER_ATTRIBUTE_2, TPend.CUSTOMER_ATTRIBUTE_3, TPend.CUSTOMER_SEX_AT, TPend.CUSTOMER_SEX,TPend.COMM_ADDRESS_1, TPend.COMM_ADDRESS_2,TPend.COMM_ADDRESS_3, TPend.COMM_CITY, "
				+ "TPend.COMM_STATE, TPend.COMM_PIN_CODE, TPend.PHONE_NUMBER,TPend.PHONE_NUMBER_02,TPend.PHONE_NUMBER_03,TPend.PHONE_NUMBER_04,TPend.PHONE_NUMBER_05,TPend.PHONE_NUMBER_06,TPend.PHONE_NUMBER_07, TPend.COMM_STATE_AT, TPend.COMM_CITY_AT,"
				+ "TPend.CUSTOMER_STATUS_NT, TPend.CUSTOMER_STATUS," + customerStatusNtPendDesc
				+ " ,TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR," + RecordIndicatorNtPendDesc
				+ " ,TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS," + makerPendDesc + " , " + verifierPendDesc
				+ " , " + dbFunctionFormats("TPend.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED," + " " + dbFunctionFormats("TPend.DATE_CREATION", "DATETIME_FORMAT", null)
				+ " DATE_CREATION ," + "TPend.CRS_FLAG,TPend.FATCA_FLAG,"
				+ "(SELECT NAICS_DESCRIPTION FROM NAICS_CODES WHERE NAICS_CODE = TPend.NAICS_CODE) NAICS_CODE_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.CB_DOMICILE) CB_DOMICILE_DESC "
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.CB_NATIONALITY)CB_NATIONALITY_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY =TPend.CB_RESIDENCE)CB_RESIDENCE_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK  AND T1.CUSTOMER_ID = TPend.PRIMARY_CID)PRIMARY_CID_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK AND T1.CUSTOMER_ID = TPend.PARENT_CID) PARENT_CID_DESC\r\n"
				+ ",(SELECT CUSTOMER_NAME  FROM CUSTOMERS T1 WHERE T1.CUSTOMER_STATUS = 0 AND T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK AND T1.CUSTOMER_ID = TPend.ULTIMATE_PARENT)\r\n"
				+ " ULTIMATE_PARENT_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_HIERARCHY_1) CUSTOMER_HIERARCHY_1_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_HIERARCHY_2) CUSTOMER_HIERARCHY_2_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_HIERARCHY_3) CUSTOMER_HIERARCHY_3_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_ATTRIBUTE_1) CUSTOMER_ATTRIBUTE_1_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_ATTRIBUTE_2) CUSTOMER_ATTRIBUTE_2_DESC\r\n"
				+ ",(SELECT CUSTOMER_ATTRIBUTE_NAME  FROM    CUSTOMER_ATTRIBUTES WHERE CUSTOMER_ATT_STATUS = 0 AND CUSTOMER_ATTRIBUTE = TPend.CUSTOMER_ATTRIBUTE_3) CUSTOMER_ATTRIBUTE_3_DESC"
				+ ",(SELECT OUC_DESCRIPTION AS DESCRIPTION FROM OUC_CODES t1  WHERE T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK  AND  t1.VISION_OUC = TPend.VISION_OUC AND OUC_STATUS = 0) VISION_OUC_DESC "
				+ ",TPend.FATCA_OVERRIDE, TPend.CRS_OVERRIDE, T2.DUAL_NATIONALITY_1, T2.DUAL_NATIONALITY_2, T2.DUAL_NATIONALITY_3,TPEND.SSN,TPEND.CUSTOMER_TIN,TPEND.SUB_SEGMENT,TPEND.COMPLIANCE_STATUS ,TPEND.JOINT_ACCOUNT "
				+ " FROM " + Customers + "_PEND TPEND INNER JOIN " + CustomersExtras
				+ "_PEND T2 ON ( TPEND.COUNTRY= T2.COUNTRY AND TPEND.LE_BOOK= T2.LE_BOOK AND TPEND.CUSTOMER_ID= T2.CUSTOMER_ID)"
				+ " ) TPend ");
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
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAppr.COUNTRY) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) " + val, strBufPending, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAppr.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) " + val, strBufPending, data.getJoinType());
						break;

					case "customerId":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerName":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_NAME) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerAcronym":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_ACRONYM) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_ACRONYM) " + val, strBufPending,
								data.getJoinType());
						break;

					case "visionOuc":
						CommonUtils.addToQuerySearch(" upper(TAppr.VISION_OUC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VISION_OUC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "visionSbuDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.VISION_SBU_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VISION_SBU_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "globalCustomerId":
						CommonUtils.addToQuerySearch(" upper(TAppr.GLOBAL_CUSTOMER_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GLOBAL_CUSTOMER_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "naicsCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.NAICS_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.NAICS_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbOrgCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_ORG_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_ORG_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbEconomicActCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_ECONOMIC_ACT_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_ECONOMIC_ACT_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbDomicile":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_DOMICILE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_DOMICILE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbNationalityDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_NATIONALITY_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_NATIONALITY_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbResidenceDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_RESIDENCE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_RESIDENCE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbMajorPartyIndicator":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_MAJOR_PARTY_INDICATOR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_MAJOR_PARTY_INDICATOR) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerIdType":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_ID_TYPE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_ID_TYPE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "idDetails":
						CommonUtils.addToQuerySearch(" upper(TAppr.ID_DETAILS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ID_DETAILS) " + val, strBufPending,
								data.getJoinType());
						break;

					case "primaryCid":
						CommonUtils.addToQuerySearch(" upper(TAppr.PRIMARY_CID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PRIMARY_CID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "parentCid":
						CommonUtils.addToQuerySearch(" upper(TAppr.PARENT_CID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PARENT_CID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ultimateParent":
						CommonUtils.addToQuerySearch(" upper(TAppr.ULTIMATE_PARENT) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ULTIMATE_PARENT) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerHierarchy1":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_HIERARCHY_1) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_HIERARCHY_1) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerHierarchy2":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_HIERARCHY_2) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_HIERARCHY_2) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerHierarchy3":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_HIERARCHY_3) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_HIERARCHY_3) " + val, strBufPending,
								data.getJoinType());
						break;

					case "accountOfficerDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.ACCOUNT_OFFICER_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ACCOUNT_OFFICER_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerSex":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_SEX) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_SEX) " + val, strBufPending,
								data.getJoinType());
						break;

					case "creditClassification":
						CommonUtils.addToQuerySearch(" upper(TAppr.CREDIT_CLASSIFICATION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CREDIT_CLASSIFICATION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "externalRiskRating":
						CommonUtils.addToQuerySearch(" upper(TAppr.EXTERNAL_RISK_RATING) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.EXTERNAL_RISK_RATING) " + val, strBufPending,
								data.getJoinType());
						break;

					case "obligorRiskRating":
						CommonUtils.addToQuerySearch(" upper(TAppr.OBLIGOR_RISK_RATING) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.OBLIGOR_RISK_RATING) " + val, strBufPending,
								data.getJoinType());
						break;

					case "relatedParty":
						CommonUtils.addToQuerySearch(" upper(TAppr.RELATED_PARTY) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RELATED_PARTY) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerTiering":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_TIERING) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_TIERING) " + val, strBufPending,
								data.getJoinType());
						break;

					case "salesAcquisitionChannel":
						CommonUtils.addToQuerySearch(" upper(TAppr.SALES_ACQUISITION_CHANNEL) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.SALES_ACQUISITION_CHANNEL) " + val, strBufPending,
								data.getJoinType());
						break;

					case "marketingCampaign":
						CommonUtils.addToQuerySearch(" upper(TAppr.MARKETING_CAMPAIGN) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.MARKETING_CAMPAIGN) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crosssaleReferId":
						CommonUtils.addToQuerySearch(" upper(TAppr.CROSSSALE_REFER_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CROSSSALE_REFER_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerIndicator":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_INDICATOR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_INDICATOR) " + val, strBufPending,
								data.getJoinType());
						break;

					case "numOfAccounts":
						CommonUtils.addToQuerySearch(" upper(TAppr.NUM_OF_ACCOUNTS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.NUM_OF_ACCOUNTS) " + val, strBufPending,
								data.getJoinType());
						break;

					case "statusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerOpenDate":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_OPEN_DATE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_OPEN_DATE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "commAddress1":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_ADDRESS_1) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_ADDRESS_1) " + val, strBufPending,
								data.getJoinType());
						break;

					case "commAddress2":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_ADDRESS_2) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_ADDRESS_2) " + val, strBufPending,
								data.getJoinType());
						break;

					case "commCity":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_CITY) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_CITY) " + val, strBufPending,
								data.getJoinType());
						break;

					case "commState":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_STATE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_STATE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "commPinCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_PIN_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_PIN_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "phoneNumber":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerTin":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_TIN) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_TIN) " + val, strBufPending,
								data.getJoinType());
						break;

					case "customerTypeCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.CUSTOMER_TYPE_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CUSTOMER_TYPE_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "profitCenter":
						CommonUtils.addToQuerySearch(" upper(TAppr.PROFIT_CENTER) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PROFIT_CENTER) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "fatcaFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;
					case "phoneNumber2":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_02) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_02) " + val, strBufPending,
								data.getJoinType());
					case "phoneNumber3":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_03) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_03) " + val, strBufPending,
								data.getJoinType());
					case "phoneNumber4":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_04) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_04) " + val, strBufPending,
								data.getJoinType());
					case "phoneNumber5":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_05) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_05) " + val, strBufPending,
								data.getJoinType());
					case "phoneNumber6":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_06) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_06) " + val, strBufPending,
								data.getJoinType());
					case "phoneNumber7":
						CommonUtils.addToQuerySearch(" upper(TAppr.PHONE_NUMBER_07) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PHONE_NUMBER_07) " + val, strBufPending,
								data.getJoinType());
					case "commAddress3":
						CommonUtils.addToQuerySearch(" upper(TAppr.COMM_ADDRESS_3) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COMM_ADDRESS_3) " + val, strBufPending,
								data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" TAppr.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				CommonUtils.addToQuery(" TAppr.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufPending);
			}
//			String orderBy = " Order By DATE_LAST_MODIFIED, COUNTRY, LE_BOOK, CUSTOMER_ID  DESC";
			String orderBy = " Order By DATE_LAST_MODIFIED  DESC";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params,
					getMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			if (params != null)
				for (int i = 0; i < params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;

		}
	}

	public List<CustomersVb> getQueryResults(CustomersVb dObj, int intStatus) {
		List<CustomersVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.COUNTRY,"
				+ "TAppr.LE_BOOK, TAppr.CUSTOMER_ID, TAppr.CUSTOMER_NAME," + "TAppr.CUSTOMER_ACRONYM, "
//				+ " To_Char(TAppr.CUSTOMER_OPEN_DATE, 'DD-MM-RRRR') CUSTOMER_OPEN_DATE,"
				+ dbFunctionFormats("TAppr.CUSTOMER_OPEN_DATE", "DATE_FORMAT", null) + " CUSTOMER_OPEN_DATE," + " "
				+ " TAppr.VISION_OUC, TAppr.VISION_SBU_AT," + " TAppr.GLOBAL_CUSTOMER_ID, TAppr.NAICS_CODE,"
				+ "TAPPR.VISION_SBU, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.VISION_SBU AND ALPHA_TAB = 3)   VISION_SBU_DESC"
				+ " ,TAppr.CB_ORG_CODE_AT, TAppr.CB_ORG_CODE, TAppr.CB_ECONOMIC_ACT_CODE_AT,"
				+ "TAppr.CB_ECONOMIC_ACT_CODE, TAppr.CB_DOMICILE,"
				+ "TAppr.CB_NATIONALITY, TAppr.CB_RESIDENCE, TAppr.CB_MAJOR_PARTY_INDICATOR,"
				+ "TAppr.CUSTOMER_ID_TYPE_AT, TAppr.CUSTOMER_ID_TYPE,"
				+ "TAppr.ID_DETAILS, TAppr.PRIMARY_CID, TAppr.PARENT_CID,"
				+ "TAppr.ULTIMATE_PARENT, TAppr.CUSTOMER_HIERARCHY_1,"
				+ "TAppr.CUSTOMER_HIERARCHY_2, TAppr.CUSTOMER_HIERARCHY_3,TAppr.ACCOUNT_OFFICER, "
				+ "(SELECT  AO_NAME  AS DESCRIPTION FROM ACCOUNT_OFFICERS WHERE COUNTRY = TAPPR.COUNTRY AND LE_BOOK = TAPPR.LE_BOOK AND ACCOUNT_OFFICER = TAPPR.ACCOUNT_OFFICER ) ACCOUNT_OFFICER_DESC ,"
				+ "TAppr.CREDIT_CLASSIFICATION_AT,TAppr.CREDIT_CLASSIFICATION, TAppr.EXTERNAL_RISK_RATING_AT,"
				+ "TAppr.EXTERNAL_RISK_RATING, TAppr.OBLIGOR_RISK_RATING_AT,"
				+ "TAppr.OBLIGOR_RISK_RATING, TAppr.RELATED_PARTY_AT,"
				+ "TAppr.RELATED_PARTY, TAppr.CUSTOMER_TIERING_AT,"
				+ "TAppr.CUSTOMER_TIERING, TAppr.SALES_ACQUISITION_CHANNEL_AT,"
				+ "TAppr.SALES_ACQUISITION_CHANNEL, TAppr.MARKETING_CAMPAIGN,"
				+ "TAppr.CROSSSALE_REFER_ID, TAppr.CUSTOMER_INDICATOR, TAppr.CUSTOMER_INDICATOR_AT,"
				+ "TAppr.NUM_OF_ACCOUNTS, TAppr.CUSTOMER_ATTRIBUTE_1,"
				+ "TAppr.CUSTOMER_ATTRIBUTE_2, TAppr.CUSTOMER_ATTRIBUTE_3, TAppr.CUSTOMER_SEX_AT, TAppr.CUSTOMER_SEX,TAppr.COMM_ADDRESS_1, TAppr.COMM_ADDRESS_2,TAppr.COMM_ADDRESS_3, TAppr.COMM_CITY, "
				+ "TAppr.COMM_STATE, TAppr.COMM_PIN_CODE, TAppr.PHONE_NUMBER,TAppr.PHONE_NUMBER_02,TAppr.PHONE_NUMBER_03,TAppr.PHONE_NUMBER_04,TAppr.PHONE_NUMBER_05,TAppr.PHONE_NUMBER_06,TAppr.PHONE_NUMBER_07 ,TAppr.COMM_STATE_AT, TAppr.COMM_CITY_AT,"
				+ "TAppr.CUSTOMER_STATUS_NT, TAppr.CUSTOMER_STATUS," + customerStatusNtApprDesc
				+ " ,TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR," + RecordIndicatorNtApprDesc
				+ " ,TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS," + makerApprDesc + " , " + verifierApprDesc
				+ " , " + dbFunctionFormats("TAppr.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED," + " " + dbFunctionFormats("TAppr.DATE_CREATION", "DATETIME_FORMAT", null)
				+ " DATE_CREATION ," + "TAppr.CRS_FLAG,TAppr.FATCA_FLAG"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.CB_NATIONALITY)CB_NATIONALITY_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY =TAppr.CB_RESIDENCE)CB_RESIDENCE_DESC\r\n"
				+ ",(SELECT OUC_DESCRIPTION AS DESCRIPTION FROM   OUC_CODES t1 WHERE T1.COUNTRY = TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK AND t1.VISION_OUC = TAPPR.VISION_OUC AND OUC_STATUS = 0) VISION_OUC_DESC "
				+ ",TAppr.FATCA_OVERRIDE,TAppr.CRS_OVERRIDE, "
				+ "  T1.DUAL_NATIONALITY_1, "
				+ " T1.DUAL_NATIONALITY_2,"
				+ " T1.DUAL_NATIONALITY_3,"
				+ " TAPPR.SSN,TAPPR.CUSTOMER_TIN,TAPPR.SUB_SEGMENT,TAPPR.COMPLIANCE_STATUS ,TAPPR.JOINT_ACCOUNT "
				+ " FROM " + Customers + " TAPPR INNER JOIN " + CustomersExtras
				+ " T1 ON( TAPPR.COUNTRY= T1.COUNTRY AND TAPPR.LE_BOOK= T1.LE_BOOK AND TAPPR.CUSTOMER_ID= T1.CUSTOMER_ID) "
				+ " WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND TAPPR.CUSTOMER_ID = ? ");

		String strQueryPend = new String("Select TPend.COUNTRY,"
				+ "TPend.LE_BOOK, TPend.CUSTOMER_ID, TPend.CUSTOMER_NAME," + "TPend.CUSTOMER_ACRONYM, "
				+ dbFunctionFormats("TPend.CUSTOMER_OPEN_DATE", "DATE_FORMAT", null) + " CUSTOMER_OPEN_DATE," + " "
				+ " TPend.VISION_OUC, TPend.VISION_SBU_AT," + " TPend.GLOBAL_CUSTOMER_ID, TPend.NAICS_CODE,"
				+ " TPend.VISION_SBU, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TPend.VISION_SBU AND ALPHA_TAB = 3)   VISION_SBU_DESC"
				+ " ,TPend.CB_ORG_CODE_AT, TPend.CB_ORG_CODE, TPend.CB_ECONOMIC_ACT_CODE_AT,"
				+ "TPend.CB_ECONOMIC_ACT_CODE, TPend.CB_DOMICILE,"
				+ "TPend.CB_NATIONALITY, TPend.CB_RESIDENCE, TPend.CB_MAJOR_PARTY_INDICATOR,"
				+ "TPend.CUSTOMER_ID_TYPE_AT, TPend.CUSTOMER_ID_TYPE,"
				+ "TPend.ID_DETAILS, TPend.PRIMARY_CID, TPend.PARENT_CID,"
				+ "TPend.ULTIMATE_PARENT, TPend.CUSTOMER_HIERARCHY_1,"
				+ "TPend.CUSTOMER_HIERARCHY_2, TPend.CUSTOMER_HIERARCHY_3,"
				+ "TPend.ACCOUNT_OFFICER, TPend.CREDIT_CLASSIFICATION_AT,"
				+ "TPend.CREDIT_CLASSIFICATION, TPend.EXTERNAL_RISK_RATING_AT,"
				+ "TPend.EXTERNAL_RISK_RATING, TPend.OBLIGOR_RISK_RATING_AT,"
				+ "TPend.OBLIGOR_RISK_RATING, TPend.RELATED_PARTY_AT,"
				+ "TPend.RELATED_PARTY, TPend.CUSTOMER_TIERING_AT,"
				+ "TPend.CUSTOMER_TIERING, TPend.SALES_ACQUISITION_CHANNEL_AT,"
				+ "TPend.SALES_ACQUISITION_CHANNEL, TPend.MARKETING_CAMPAIGN,"
				+ "TPend.CROSSSALE_REFER_ID, TPend.CUSTOMER_INDICATOR, TPend.CUSTOMER_INDICATOR_AT,"
				+ "TPend.NUM_OF_ACCOUNTS, TPend.CUSTOMER_ATTRIBUTE_1,"
				+ "TPend.CUSTOMER_ATTRIBUTE_2, TPend.CUSTOMER_ATTRIBUTE_3, TPend.CUSTOMER_SEX_AT, TPend.CUSTOMER_SEX,TPend.COMM_ADDRESS_1, TPend.COMM_ADDRESS_2,TPend.COMM_ADDRESS_3, TPend.COMM_CITY, TPend.COMM_STATE, "
				+ "TPend.COMM_PIN_CODE, TPend.PHONE_NUMBER,TPend.PHONE_NUMBER_02,TPend.PHONE_NUMBER_03,TPend.PHONE_NUMBER_04,TPend.PHONE_NUMBER_05,TPend.PHONE_NUMBER_06,TPend.PHONE_NUMBER_07, TPend.COMM_STATE_AT, TPend.COMM_CITY_AT,"
				+ "TPend.CUSTOMER_STATUS_NT, TPend.CUSTOMER_STATUS," + customerStatusNtPendDesc
				+ " ,TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR," + RecordIndicatorNtPendDesc
				+ " ,TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS," + makerPendDesc + " , " + verifierPendDesc
				+ " , " + dbFunctionFormats("TPend.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED," + " " + dbFunctionFormats("TPend.DATE_CREATION", "DATETIME_FORMAT", null)
				+ " DATE_CREATION ," + "TPend.CRS_FLAG,TPend.FATCA_FLAG"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = Tpend.CB_NATIONALITY)CB_NATIONALITY_DESC\r\n"
				+ ",(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY =Tpend.CB_RESIDENCE)CB_RESIDENCE_DESC\r\n"
				+ ",(SELECT OUC_DESCRIPTION AS DESCRIPTION FROM   OUC_CODES t1 WHERE T1.COUNTRY = TPend.COUNTRY AND T1.LE_BOOK = TPend.LE_BOOK  AND  t1.VISION_OUC = TPend.VISION_OUC AND OUC_STATUS = 0) VISION_OUC_DESC,"
				+ " Tpend.ACCOUNT_OFFICER, "
				+ "(SELECT  AO_NAME  AS DESCRIPTION FROM ACCOUNT_OFFICERS WHERE COUNTRY = Tpend.COUNTRY AND LE_BOOK = Tpend.LE_BOOK AND ACCOUNT_OFFICER = Tpend.ACCOUNT_OFFICER ) ACCOUNT_OFFICER_DESC "
				+ ",TPend.FATCA_OVERRIDE,TPend.CRS_OVERRIDE,"
				+ "  T2.DUAL_NATIONALITY_1,"
				+ " T2.DUAL_NATIONALITY_2,"
				+ " T2.DUAL_NATIONALITY_3,"
				+ " Tpend.SSN, Tpend.CUSTOMER_TIN, Tpend.SUB_SEGMENT, Tpend.COMPLIANCE_STATUS , Tpend.JOINT_ACCOUNT "
				+ " From " + Customers + "_PEND TPend INNER JOIN " + CustomersExtras
				+ "_PEND T2 ON ( TPend.COUNTRY= T2.COUNTRY AND TPend.LE_BOOK= T2.LE_BOOK AND TPend.CUSTOMER_ID= T2.CUSTOMER_ID) "
				+ " Where TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.CUSTOMER_ID = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry()); // [COUNTRY]
		objParams[1] = new String(dObj.getLeBook()); // [LE_BOOK]
		objParams[2] = new String(dObj.getCustomerId()); // [CUSTOMER_ID]

		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getQueryResultMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getQueryResultMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	@Override
	protected List<CustomersVb> selectApprovedRecord(CustomersVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CustomersVb> doSelectPendingRecord(CustomersVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(CustomersVb records) {
		return records.getCustomerStatus();
	}

	@Override
	protected void setStatus(CustomersVb vObject, int status) {
		vObject.setCustomerStatus(status);
		vObject.setCusExtrasStatus(status);
	}

	@Override
	protected int doInsertionAppr(CustomersVb vObject) {
		if (!ValidationUtil.isValid(vObject.getNaicsCode())) {
			vObject.setNaicsCode("999999");
		}
		String query = "Insert Into " + Customers
				+ " ( COUNTRY, LE_BOOK, CUSTOMER_OPEN_DATE, CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_ACRONYM, "
				+ "VISION_OUC, VISION_SBU_AT, VISION_SBU, GLOBAL_CUSTOMER_ID, NAICS_CODE, CB_ORG_CODE_AT,"
				+ "CB_ORG_CODE, CB_ECONOMIC_ACT_CODE_AT, CB_ECONOMIC_ACT_CODE, CB_DOMICILE, CB_NATIONALITY,"
				+ "CB_RESIDENCE, CB_MAJOR_PARTY_INDICATOR, CUSTOMER_ID_TYPE_AT, CUSTOMER_ID_TYPE, ID_DETAILS,"
				+ "PRIMARY_CID, PARENT_CID, ULTIMATE_PARENT, CUSTOMER_HIERARCHY_1, CUSTOMER_HIERARCHY_2,"
				+ "CUSTOMER_HIERARCHY_3, ACCOUNT_OFFICER, CREDIT_CLASSIFICATION_AT, CREDIT_CLASSIFICATION,"
				+ "EXTERNAL_RISK_RATING_AT, EXTERNAL_RISK_RATING, OBLIGOR_RISK_RATING_AT, OBLIGOR_RISK_RATING,"
				+ "RELATED_PARTY_AT, RELATED_PARTY, CUSTOMER_TIERING_AT, CUSTOMER_TIERING, SALES_ACQUISITION_CHANNEL_AT,"
				+ "SALES_ACQUISITION_CHANNEL, MARKETING_CAMPAIGN, CROSSSALE_REFER_ID, CUSTOMER_INDICATOR_AT, CUSTOMER_INDICATOR,"
				+ "NUM_OF_ACCOUNTS, CUSTOMER_ATTRIBUTE_1, CUSTOMER_ATTRIBUTE_2, CUSTOMER_ATTRIBUTE_3, CUSTOMER_SEX_AT, CUSTOMER_SEX,"
				+ "COMM_ADDRESS_1, COMM_ADDRESS_2, COMM_CITY_AT, COMM_CITY, COMM_STATE_AT, COMM_STATE, COMM_PIN_CODE, PHONE_NUMBER,"
				+ "CUSTOMER_STATUS_NT, CUSTOMER_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,"
				+ "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,CRS_FLAG,FATCA_FLAG,FATCA_OVERRIDE,CRS_OVERRIDE,SSN,CUSTOMER_TIN ,SUB_SEGMENT,COMPLIANCE_STATUS,JOINT_ACCOUNT,"
				+ " PHONE_NUMBER_02,PHONE_NUMBER_03,PHONE_NUMBER_04,PHONE_NUMBER_05,PHONE_NUMBER_06,PHONE_NUMBER_07,COMM_ADDRESS_3)"
				+ "Values (?, ?, " + dateConvert
				+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ systemDate + "," + systemDate + ",?,?,?,?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerOpenDate(),
				vObject.getCustomerId(), vObject.getCustomerName(),
				ValidationUtil.isValid(vObject.getCustomerAcronym()) ? vObject.getCustomerAcronym():"NA",
				vObject.getVisionOuc(), vObject.getVisionSbuAt(), vObject.getVisionSbu(), vObject.getGlobalCustomerId(),
				vObject.getNaicsCode(), vObject.getCbOrgCodeAt(),
				ValidationUtil.isValid(vObject.getCbOrgCode())? vObject.getCbOrgCode():"NA",
				vObject.getCbEconomicActCodeAt(),
				ValidationUtil.isValid( vObject.getCbEconomicActCode())? vObject.getCbEconomicActCode() :"NA",
				ValidationUtil.isValid(vObject.getCbDomicile())? vObject.getCbDomicile():"NA",
				vObject.getCbNationality(), vObject.getCbResidence(), vObject.getCbMajorPartyIndicator(),
				vObject.getCustomerIdTypeAt(),
				ValidationUtil.isValid(vObject.getCustomerIdType())? vObject.getCustomerIdType() :"NA",
				ValidationUtil.isValid( vObject.getIdDetails())? vObject.getIdDetails():"",
				vObject.getPrimaryCid(), vObject.getParentCid(), vObject.getUltimateParent(),
				vObject.getCustomerHierarchy1(), vObject.getCustomerHierarchy2(), vObject.getCustomerHierarchy3(),
				vObject.getAccountOfficer(), vObject.getCreditClassificationAt(), vObject.getCreditClassification(),
				vObject.getExternalRiskRatingAt(), vObject.getExternalRiskRating(), vObject.getObligorRiskRatingAt(),
				vObject.getObligorRiskRating(), vObject.getRelatedPartyAt(), vObject.getRelatedParty(),
				vObject.getCustomerTieringAt(), vObject.getCustomerTiering(), vObject.getSalesAcquisitionChannelAt(),
				vObject.getSalesAcquisitionChannel(), vObject.getMarketingCampaign(), vObject.getCrosssaleReferId(),
				vObject.getCustomerIndicatorAt(), vObject.getCustomerIndicator(), vObject.getNumOfAccounts(),
				vObject.getCustomerAttribute1(), vObject.getCustomerAttribute2(), vObject.getCustomerAttribute3(),
				vObject.getCustomerSexAT(), vObject.getCustomerSex(), vObject.getCommAddress1(),
				vObject.getCommAddress2(), vObject.getCommCityAt(), vObject.getCommCity(), vObject.getCommStateAt(),
				vObject.getCommState(), vObject.getCommPinCode(), vObject.getPhoneNumber(),
				vObject.getCustomerStatusNt(), vObject.getCustomerStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCrsFlag(), vObject.getFatcaFlag(), vObject.getFatcaOverRide(), vObject.getCrsOverRide() ,
				vObject.getSsn(),vObject.getCustomerTin(),vObject.getSubSegment(),vObject.getComplianceStatus(),vObject.getJointAccount(),
				vObject.getPhoneNumber2(),vObject.getPhoneNumber3(),vObject.getPhoneNumber4(),vObject.getPhoneNumber5(),vObject.getPhoneNumber6(),
				vObject.getPhoneNumber7(),vObject.getCommAddress3()};
		
		
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(CustomersVb vObject) {
		if (!ValidationUtil.isValid(vObject.getNaicsCode())) {
			vObject.setNaicsCode("999999");
		}

		String query = "Insert Into " + Customers
				+ "_PEND ( COUNTRY, LE_BOOK, CUSTOMER_OPEN_DATE, CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_ACRONYM, "
				+ "VISION_OUC, VISION_SBU_AT, VISION_SBU, GLOBAL_CUSTOMER_ID, NAICS_CODE, CB_ORG_CODE_AT,"
				+ "CB_ORG_CODE, CB_ECONOMIC_ACT_CODE_AT, CB_ECONOMIC_ACT_CODE, CB_DOMICILE, CB_NATIONALITY,"
				+ "CB_RESIDENCE, CB_MAJOR_PARTY_INDICATOR, CUSTOMER_ID_TYPE_AT, CUSTOMER_ID_TYPE, ID_DETAILS,"
				+ "PRIMARY_CID, PARENT_CID, ULTIMATE_PARENT, CUSTOMER_HIERARCHY_1, CUSTOMER_HIERARCHY_2,"
				+ "CUSTOMER_HIERARCHY_3, ACCOUNT_OFFICER, CREDIT_CLASSIFICATION_AT, CREDIT_CLASSIFICATION,"
				+ "EXTERNAL_RISK_RATING_AT, EXTERNAL_RISK_RATING, OBLIGOR_RISK_RATING_AT, OBLIGOR_RISK_RATING,"
				+ "RELATED_PARTY_AT, RELATED_PARTY, CUSTOMER_TIERING_AT, CUSTOMER_TIERING, SALES_ACQUISITION_CHANNEL_AT,"
				+ "SALES_ACQUISITION_CHANNEL, MARKETING_CAMPAIGN, CROSSSALE_REFER_ID, CUSTOMER_INDICATOR_AT, CUSTOMER_INDICATOR,"
				+ "NUM_OF_ACCOUNTS, CUSTOMER_ATTRIBUTE_1, CUSTOMER_ATTRIBUTE_2, CUSTOMER_ATTRIBUTE_3, CUSTOMER_SEX_AT, CUSTOMER_SEX,"
				+ "COMM_ADDRESS_1, COMM_ADDRESS_2, COMM_CITY_AT, COMM_CITY, COMM_STATE_AT, COMM_STATE, COMM_PIN_CODE, PHONE_NUMBER,"
				+ "CUSTOMER_STATUS_NT, CUSTOMER_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,"
				+ "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,CRS_FLAG,FATCA_FLAG,FATCA_OVERRIDE,CRS_OVERRIDE,SSN,CUSTOMER_TIN ,SUB_SEGMENT,COMPLIANCE_STATUS,JOINT_ACCOUNT,"
				+ "PHONE_NUMBER_02,PHONE_NUMBER_03,PHONE_NUMBER_04,PHONE_NUMBER_05,PHONE_NUMBER_06,PHONE_NUMBER_07,COMM_ADDRESS_3)"
				+ "Values (?, ?, " + dateConvert
				+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
				+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ systemDate + "," + systemDate + ",?,?,?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,?, ? ,? ,?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerOpenDate(),
				vObject.getCustomerId(), vObject.getCustomerName(), 
				ValidationUtil.isValid(vObject.getCustomerAcronym()) ? vObject.getCustomerAcronym():"NA",
				vObject.getVisionOuc(), vObject.getVisionSbuAt(), vObject.getVisionSbu(), vObject.getGlobalCustomerId(),
				vObject.getNaicsCode(), vObject.getCbOrgCodeAt(),
				ValidationUtil.isValid(vObject.getCbOrgCode())? vObject.getCbOrgCode():"NA",
				vObject.getCbEconomicActCodeAt(),
				ValidationUtil.isValid( vObject.getCbEconomicActCode()) ? vObject.getCbEconomicActCode() :"NA",
				ValidationUtil.isValid(vObject.getCbDomicile()) ? vObject.getCbDomicile():"NA",
				vObject.getCbNationality(), vObject.getCbResidence(), vObject.getCbMajorPartyIndicator(),
				vObject.getCustomerIdTypeAt(),
				ValidationUtil.isValid(vObject.getCustomerIdType())? vObject.getCustomerIdType() :"NA",
				ValidationUtil.isValid( vObject.getIdDetails())? vObject.getIdDetails():"",
				vObject.getPrimaryCid(), vObject.getParentCid(), vObject.getUltimateParent(),
				vObject.getCustomerHierarchy1(), vObject.getCustomerHierarchy2(), vObject.getCustomerHierarchy3(),
				vObject.getAccountOfficer(), vObject.getCreditClassificationAt(), vObject.getCreditClassification(),
				vObject.getExternalRiskRatingAt(), vObject.getExternalRiskRating(), vObject.getObligorRiskRatingAt(),
				vObject.getObligorRiskRating(), vObject.getRelatedPartyAt(), vObject.getRelatedParty(),
				vObject.getCustomerTieringAt(), vObject.getCustomerTiering(), vObject.getSalesAcquisitionChannelAt(),
				vObject.getSalesAcquisitionChannel(), vObject.getMarketingCampaign(), vObject.getCrosssaleReferId(),
				vObject.getCustomerIndicatorAt(), vObject.getCustomerIndicator(), vObject.getNumOfAccounts(),
				vObject.getCustomerAttribute1(), vObject.getCustomerAttribute2(), vObject.getCustomerAttribute3(),
				vObject.getCustomerSexAT(), vObject.getCustomerSex(), vObject.getCommAddress1(),
				vObject.getCommAddress2(), vObject.getCommCityAt(), vObject.getCommCity(), vObject.getCommStateAt(),
				vObject.getCommState(), vObject.getCommPinCode(), vObject.getPhoneNumber(),
				vObject.getCustomerStatusNt(), vObject.getCustomerStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCrsFlag(), vObject.getFatcaFlag(), vObject.getFatcaOverRide(), vObject.getCrsOverRide() ,
				vObject.getSsn(),vObject.getCustomerTin(),vObject.getSubSegment(),vObject.getComplianceStatus(),vObject.getJointAccount(),
				vObject.getPhoneNumber2(),vObject.getPhoneNumber3(),vObject.getPhoneNumber4(),vObject.getPhoneNumber5(),vObject.getPhoneNumber6(),
				vObject.getPhoneNumber7(),vObject.getCommAddress3()};
		
		
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(CustomersVb vObject) {
		if (!ValidationUtil.isValid(vObject.getNaicsCode())) {
			vObject.setNaicsCode("999999");
		}
		String query = "Insert Into " + Customers
				+ "_PEND ( COUNTRY, LE_BOOK, CUSTOMER_OPEN_DATE, CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_ACRONYM, "
				+ "VISION_OUC, VISION_SBU_AT, VISION_SBU, GLOBAL_CUSTOMER_ID, NAICS_CODE, CB_ORG_CODE_AT,"
				+ "CB_ORG_CODE, CB_ECONOMIC_ACT_CODE_AT, CB_ECONOMIC_ACT_CODE, CB_DOMICILE, CB_NATIONALITY,"
				+ "CB_RESIDENCE, CB_MAJOR_PARTY_INDICATOR, CUSTOMER_ID_TYPE_AT, CUSTOMER_ID_TYPE, ID_DETAILS,"
				+ "PRIMARY_CID, PARENT_CID, ULTIMATE_PARENT, CUSTOMER_HIERARCHY_1, CUSTOMER_HIERARCHY_2,"
				+ "CUSTOMER_HIERARCHY_3, ACCOUNT_OFFICER, CREDIT_CLASSIFICATION_AT, CREDIT_CLASSIFICATION,"
				+ "EXTERNAL_RISK_RATING_AT, EXTERNAL_RISK_RATING, OBLIGOR_RISK_RATING_AT, OBLIGOR_RISK_RATING,"
				+ "RELATED_PARTY_AT, RELATED_PARTY, CUSTOMER_TIERING_AT, CUSTOMER_TIERING, SALES_ACQUISITION_CHANNEL_AT,"
				+ "SALES_ACQUISITION_CHANNEL, MARKETING_CAMPAIGN, CROSSSALE_REFER_ID, CUSTOMER_INDICATOR,"
				+ "NUM_OF_ACCOUNTS, CUSTOMER_ATTRIBUTE_1, CUSTOMER_ATTRIBUTE_2, CUSTOMER_ATTRIBUTE_3, CUSTOMER_SEX_AT, CUSTOMER_SEX,"
				+ "COMM_ADDRESS_1, COMM_ADDRESS_2, COMM_CITY_AT, COMM_CITY, COMM_STATE_AT, COMM_STATE, COMM_PIN_CODE, PHONE_NUMBER,"
				+ "CUSTOMER_STATUS_NT, CUSTOMER_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER,"
				+ "VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION,CRS_FLAG,FATCA_FLAG,FATCA_OVERRIDE,CRS_OVERRIDE,SSN,CUSTOMER_TIN ,SUB_SEGMENT,COMPLIANCE_STATUS,JOINT_ACCOUNT,"
				+ "PHONE_NUMBER_02,PHONE_NUMBER_03,PHONE_NUMBER_04,PHONE_NUMBER_05,PHONE_NUMBER_06,PHONE_NUMBER_07,COMM_ADDRESS_3)"
				+ "Values (?, ?, " + dateConvert
				+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," + systemDate + ", "
				+ dateTimeConvert + ",?,?,?,?, ?, ?, ?, ?, ? , ? ,? ,? ,?, ?, ?, ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerOpenDate(),
				vObject.getCustomerId(), vObject.getCustomerName(), 
				ValidationUtil.isValid(vObject.getCustomerAcronym()) ? vObject.getCustomerAcronym():"NA",
				vObject.getVisionOuc(), vObject.getVisionSbuAt(), vObject.getVisionSbu(), vObject.getGlobalCustomerId(),
				vObject.getNaicsCode(), vObject.getCbOrgCodeAt(), 
				ValidationUtil.isValid(vObject.getCbOrgCode())? vObject.getCbOrgCode():"NA",
				vObject.getCbEconomicActCodeAt(),
				ValidationUtil.isValid( vObject.getCbEconomicActCode())? vObject.getCbEconomicActCode() :"NA",
				ValidationUtil.isValid(vObject.getCbDomicile())? vObject.getCbDomicile():"NA",
				vObject.getCbNationality(), vObject.getCbResidence(), vObject.getCbMajorPartyIndicator(),
				vObject.getCustomerIdTypeAt(), 
				ValidationUtil.isValid(vObject.getCustomerIdType())? vObject.getCustomerIdType() :"NA",
				ValidationUtil.isValid( vObject.getIdDetails())? vObject.getIdDetails():"",
				vObject.getPrimaryCid(), vObject.getParentCid(), vObject.getUltimateParent(),
				vObject.getCustomerHierarchy1(), vObject.getCustomerHierarchy2(), vObject.getCustomerHierarchy3(),
				vObject.getAccountOfficer(), vObject.getCreditClassificationAt(), vObject.getCreditClassification(),
				vObject.getExternalRiskRatingAt(), vObject.getExternalRiskRating(), vObject.getObligorRiskRatingAt(),
				vObject.getObligorRiskRating(), vObject.getRelatedPartyAt(), vObject.getRelatedParty(),
				vObject.getCustomerTieringAt(), vObject.getCustomerTiering(), vObject.getSalesAcquisitionChannelAt(),
				vObject.getSalesAcquisitionChannel(), vObject.getMarketingCampaign(), vObject.getCrosssaleReferId(),
				vObject.getCustomerIndicator(), vObject.getNumOfAccounts(), vObject.getCustomerAttribute1(),
				vObject.getCustomerAttribute2(), vObject.getCustomerAttribute3(), vObject.getCustomerSexAT(),
				vObject.getCustomerSex(), vObject.getCommAddress1(), vObject.getCommAddress2(), vObject.getCommCityAt(),
				vObject.getCommCity(), vObject.getCommStateAt(), vObject.getCommState(), vObject.getCommPinCode(),
				vObject.getPhoneNumber(), vObject.getCustomerStatusNt(), vObject.getCustomerStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation(), vObject.getCrsFlag(), vObject.getFatcaFlag(),
				vObject.getFatcaOverRide(), vObject.getCrsOverRide() ,
				vObject.getSsn(),vObject.getCustomerTin(),vObject.getSubSegment(),vObject.getComplianceStatus(),vObject.getJointAccount(),
				vObject.getPhoneNumber2(),vObject.getPhoneNumber3(),vObject.getPhoneNumber4(),vObject.getPhoneNumber5(),vObject.getPhoneNumber6(),
				vObject.getPhoneNumber7(),vObject.getCommAddress3()};
		return getJdbcTemplate().update(query, args);
	}

//	@Override
//	protected int doUpdateAppr(CustomersVb vObject) {
//		String query = "Update " + Customers + " set CUSTOMER_OPEN_DATE = " + dateConvert
//				+ ", CUSTOMER_NAME = ?, CUSTOMER_ACRONYM = ?, "
//				+ "VISION_OUC = ?, VISION_SBU_AT = ?, VISION_SBU = ?, GLOBAL_CUSTOMER_ID = ?, NAICS_CODE = ?, CB_ORG_CODE_AT = ?,"
//				+ "CB_ORG_CODE = ?, CB_ECONOMIC_ACT_CODE_AT = ?, CB_ECONOMIC_ACT_CODE = ?, CB_DOMICILE = ?, CB_NATIONALITY = ?,"
//				+ "CB_RESIDENCE = ?, CB_MAJOR_PARTY_INDICATOR = ?, CUSTOMER_ID_TYPE_AT = ?, CUSTOMER_ID_TYPE = ?, ID_DETAILS = ?,"
//				+ "PRIMARY_CID = ?, PARENT_CID = ?, ULTIMATE_PARENT = ?, CUSTOMER_HIERARCHY_1 = ?, CUSTOMER_HIERARCHY_2 = ?,"
//				+ "CUSTOMER_HIERARCHY_3 = ?, ACCOUNT_OFFICER = ?, CREDIT_CLASSIFICATION_AT = ?, CREDIT_CLASSIFICATION = ?,"
//				+ "EXTERNAL_RISK_RATING_AT = ?, EXTERNAL_RISK_RATING = ?, OBLIGOR_RISK_RATING_AT = ?, OBLIGOR_RISK_RATING = ?,"
//				+ "RELATED_PARTY_AT = ?, RELATED_PARTY = ?, CUSTOMER_TIERING_AT = ?, CUSTOMER_TIERING = ?, SALES_ACQUISITION_CHANNEL_AT = ?,"
//				+ "SALES_ACQUISITION_CHANNEL = ?, MARKETING_CAMPAIGN = ?, CROSSSALE_REFER_ID = ?, CUSTOMER_INDICATOR = ?,"
//				+ "NUM_OF_ACCOUNTS = ?, CUSTOMER_ATTRIBUTE_1 = ?, CUSTOMER_ATTRIBUTE_2 = ?, CUSTOMER_ATTRIBUTE_3 = ?, CUSTOMER_SEX_AT = ?, CUSTOMER_SEX = ?,"
//				+ "COMM_ADDRESS_1 = ?, COMM_ADDRESS_2 = ?, COMM_CITY_AT = ?, COMM_CITY = ?, COMM_STATE_AT = ?, COMM_STATE = ?, COMM_PIN_CODE = ?, PHONE_NUMBER = ?,"
//				+ "CUSTOMER_STATUS_NT = ?, CUSTOMER_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?,"
//				+ "VERIFIER = ?, INTERNAL_STATUS = ?, CRS_FLAG = ? ,FATCA_FLAG = ? ,FATCA_OVERRIDE = ?,CRS_OVERRIDE = ? "
//				+ " ,DATE_LAST_MODIFIED =  " + systemDate + " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
//		Object[] args = { vObject.getCustomerOpenDate(), vObject.getCustomerName(), vObject.getCustomerAcronym(),
//				vObject.getVisionOuc(), vObject.getVisionSbuAt(), vObject.getVisionSbu(), vObject.getGlobalCustomerId(),
//				vObject.getNaicsCode(), vObject.getCbOrgCodeAt(), vObject.getCbOrgCode(),
//				vObject.getCbEconomicActCodeAt(), vObject.getCbEconomicActCode(), vObject.getCbDomicile(),
//				vObject.getCbNationality(), vObject.getCbResidence(), vObject.getCbMajorPartyIndicator(),
//				vObject.getCustomerIdTypeAt(), vObject.getCustomerIdType(), vObject.getIdDetails(),
//				vObject.getPrimaryCid(), vObject.getParentCid(), vObject.getUltimateParent(),
//				vObject.getCustomerHierarchy1(), vObject.getCustomerHierarchy2(), vObject.getCustomerHierarchy3(),
//				vObject.getAccountOfficer(), vObject.getCreditClassificationAt(), vObject.getCreditClassification(),
//				vObject.getExternalRiskRatingAt(), vObject.getExternalRiskRating(), vObject.getObligorRiskRatingAt(),
//				vObject.getObligorRiskRating(), vObject.getRelatedPartyAt(), vObject.getRelatedParty(),
//				vObject.getCustomerTieringAt(), vObject.getCustomerTiering(), vObject.getSalesAcquisitionChannelAt(),
//				vObject.getSalesAcquisitionChannel(), vObject.getMarketingCampaign(), vObject.getCrosssaleReferId(),
//				vObject.getCustomerIndicator(), vObject.getNumOfAccounts(), vObject.getCustomerAttribute1(),
//				vObject.getCustomerAttribute2(), vObject.getCustomerAttribute3(), vObject.getCustomerSexAT(),
//				vObject.getCustomerSex(), vObject.getCommAddress1(), vObject.getCommAddress2(), vObject.getCommCityAt(),
//				vObject.getCommCity(), vObject.getCommStateAt(), vObject.getCommState(), vObject.getCommPinCode(),
//				vObject.getPhoneNumber(), vObject.getCustomerStatusNt(), vObject.getCustomerStatus(),
//				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
//				vObject.getInternalStatus(), vObject.getCrsFlag(), vObject.getFatcaFlag(), vObject.getFatcaOverRide(),
//				vObject.getCrsOverRide(), vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
//		return getJdbcTemplate().update(query, args);
//	}
	@Override
	protected int doUpdateAppr(CustomersVb vObject) {
		String query = "Update " + Customers
				+ " set  CUSTOMER_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?,"
				+ "VERIFIER = ?, INTERNAL_STATUS = ?" + " ,DATE_LAST_MODIFIED =  " + systemDate
				+ " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
		Object[] args = { vObject.getCustomerStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(CustomersVb vObject) {
		String query = "Update " + Customers + "_PEND"
				+ " set  CUSTOMER_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?,"
				+ "VERIFIER = ?, INTERNAL_STATUS = ?" + " ,DATE_LAST_MODIFIED =  " + systemDate
				+ " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
		Object[] args = { vObject.getCustomerStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

//	@Override
//	protected int doUpdatePend(CustomersVb vObject) {
//		String query = "Update " + Customers + "_PEND set CUSTOMER_OPEN_DATE = " + dateConvert
//				+ ", CUSTOMER_NAME = ?, CUSTOMER_ACRONYM = ?, "
//				+ "VISION_OUC = ?, VISION_SBU_AT = ?, VISION_SBU = ?, GLOBAL_CUSTOMER_ID = ?, NAICS_CODE = ?, CB_ORG_CODE_AT = ?,"
//				+ "CB_ORG_CODE = ?, CB_ECONOMIC_ACT_CODE_AT = ?, CB_ECONOMIC_ACT_CODE = ?, CB_DOMICILE = ?, CB_NATIONALITY = ?,"
//				+ "CB_RESIDENCE = ?, CB_MAJOR_PARTY_INDICATOR = ?, CUSTOMER_ID_TYPE_AT = ?, CUSTOMER_ID_TYPE = ?, ID_DETAILS = ?,"
//				+ "PRIMARY_CID = ?, PARENT_CID = ?, ULTIMATE_PARENT = ?, CUSTOMER_HIERARCHY_1 = ?, CUSTOMER_HIERARCHY_2 = ?,"
//				+ "CUSTOMER_HIERARCHY_3 = ?, ACCOUNT_OFFICER = ?, CREDIT_CLASSIFICATION_AT = ?, CREDIT_CLASSIFICATION = ?,"
//				+ "EXTERNAL_RISK_RATING_AT = ?, EXTERNAL_RISK_RATING = ?, OBLIGOR_RISK_RATING_AT = ?, OBLIGOR_RISK_RATING = ?,"
//				+ "RELATED_PARTY_AT = ?, RELATED_PARTY = ?, CUSTOMER_TIERING_AT = ?, CUSTOMER_TIERING = ?, SALES_ACQUISITION_CHANNEL_AT = ?,"
//				+ "SALES_ACQUISITION_CHANNEL = ?, MARKETING_CAMPAIGN = ?, CROSSSALE_REFER_ID = ?, CUSTOMER_INDICATOR = ?,"
//				+ "NUM_OF_ACCOUNTS = ?, CUSTOMER_ATTRIBUTE_1 = ?, CUSTOMER_ATTRIBUTE_2 = ?, CUSTOMER_ATTRIBUTE_3 = ?, CUSTOMER_SEX_AT = ?, CUSTOMER_SEX = ?,"
//				+ "COMM_ADDRESS_1 = ?, COMM_ADDRESS_2 = ?, COMM_CITY_AT = ?, COMM_CITY = ?, COMM_STATE_AT = ?, COMM_STATE = ?, COMM_PIN_CODE = ?, PHONE_NUMBER = ?,"
//				+ "CUSTOMER_STATUS_NT = ?, CUSTOMER_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?,"
//				+ "VERIFIER = ?, INTERNAL_STATUS = ?, CRS_FLAG = ? ,FATCA_FLAG = ?  ,FATCA_OVERRIDE = ?,CRS_OVERRIDE = ? ,DATE_LAST_MODIFIED =  "
//				+ systemDate + " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
//		Object[] args = { vObject.getCustomerOpenDate(), vObject.getCustomerName(), vObject.getCustomerAcronym(),
//				vObject.getVisionOuc(), vObject.getVisionSbuAt(), vObject.getVisionSbu(), vObject.getGlobalCustomerId(),
//				vObject.getNaicsCode(), vObject.getCbOrgCodeAt(), vObject.getCbOrgCode(),
//				vObject.getCbEconomicActCodeAt(), vObject.getCbEconomicActCode(), vObject.getCbDomicile(),
//				vObject.getCbNationality(), vObject.getCbResidence(), vObject.getCbMajorPartyIndicator(),
//				vObject.getCustomerIdTypeAt(), vObject.getCustomerIdType(), vObject.getIdDetails(),
//				vObject.getPrimaryCid(), vObject.getParentCid(), vObject.getUltimateParent(),
//				vObject.getCustomerHierarchy1(), vObject.getCustomerHierarchy2(), vObject.getCustomerHierarchy3(),
//				vObject.getAccountOfficer(), vObject.getCreditClassificationAt(), vObject.getCreditClassification(),
//				vObject.getExternalRiskRatingAt(), vObject.getExternalRiskRating(), vObject.getObligorRiskRatingAt(),
//				vObject.getObligorRiskRating(), vObject.getRelatedPartyAt(), vObject.getRelatedParty(),
//				vObject.getCustomerTieringAt(), vObject.getCustomerTiering(), vObject.getSalesAcquisitionChannelAt(),
//				vObject.getSalesAcquisitionChannel(), vObject.getMarketingCampaign(), vObject.getCrosssaleReferId(),
//				vObject.getCustomerIndicator(), vObject.getNumOfAccounts(), vObject.getCustomerAttribute1(),
//				vObject.getCustomerAttribute2(), vObject.getCustomerAttribute3(), vObject.getCustomerSexAT(),
//				vObject.getCustomerSex(), vObject.getCommAddress1(), vObject.getCommAddress2(), vObject.getCommCityAt(),
//				vObject.getCommCity(), vObject.getCommStateAt(), vObject.getCommState(), vObject.getCommPinCode(),
//				vObject.getPhoneNumber(), vObject.getCustomerStatusNt(), vObject.getCustomerStatus(),
//				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
//				vObject.getInternalStatus(), vObject.getCrsFlag(), vObject.getFatcaFlag(), vObject.getFatcaOverRide(),
//				vObject.getCrsOverRide(), vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
//		return getJdbcTemplate().update(query, args);
//	}

	@Override
	protected int doDeleteAppr(CustomersVb vObject) {
		String query = "Delete From " + Customers + " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(CustomersVb vObject) {
		String query = "Delete From " + Customers + "_PEND Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String frameErrorMessage(CustomersVb vObject, String strOperation) {
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg = strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg = strErrMsg + ",LE_BOOK:" + vObject.getLeBook();
			strErrMsg = strErrMsg + ",CUSTOMER_ID:" + vObject.getCustomerId();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation.  Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation.  Bulk Rejection aborted !!";
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}

	@Override
	protected String getAuditString(CustomersVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerId()))
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + vObject.getCustomerId().trim());
			else
				strAudit.append("CUSTOMER_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerName()))
				strAudit.append("CUSTOMER_NAME" + auditDelimiterColVal + vObject.getCustomerName().trim());
			else
				strAudit.append("CUSTOMER_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getCustomerAcronym() != null && !vObject.getCustomerAcronym().equalsIgnoreCase(""))
				strAudit.append("CUSTOMER_ACRONYM" + auditDelimiterColVal + vObject.getCustomerAcronym().trim());
			else
				strAudit.append("CUSTOMER_ACRONYM" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getVisionOuc() != null && !vObject.getVisionOuc().equalsIgnoreCase(""))
				strAudit.append("VISION_OUC" + auditDelimiterColVal + vObject.getVisionOuc().trim());
			else
				strAudit.append("VISION_OUC" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("VISION_SBU_AT" + auditDelimiterColVal + vObject.getVisionSbuAt());
			strAudit.append(auditDelimiter);

			if (vObject.getVisionSbu() != null && !vObject.getVisionSbu().equalsIgnoreCase(""))
				strAudit.append("VISION_SBU" + auditDelimiterColVal + vObject.getVisionSbu().trim());
			else
				strAudit.append("VISION_SBU" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getGlobalCustomerId() != null && !vObject.getGlobalCustomerId().equalsIgnoreCase(""))
				strAudit.append(vObject.getGlobalCustomerId().trim());
			else
				strAudit.append("NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("NAICS_CODE" + auditDelimiterColVal + vObject.getNaicsCode());
			strAudit.append(auditDelimiter);

			strAudit.append("CB_ORG_CODE_AT" + auditDelimiterColVal + vObject.getCbOrgCodeAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCbOrgCode()))
				strAudit.append("CB_ORG_CODE" + auditDelimiterColVal + vObject.getCbOrgCode().trim());
			else
				strAudit.append("CB_ORG_CODE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CB_ECONOMIC_ACT_CODE_AT" + auditDelimiterColVal + vObject.getCbEconomicActCodeAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCbEconomicActCode()))
				strAudit.append("CB_ECONOMIC_ACT_CODE" + auditDelimiterColVal + vObject.getCbEconomicActCode().trim());
			else
				strAudit.append("CB_ECONOMIC_ACT_CODE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCbDomicile()))
				strAudit.append("CB_NATIONALITY" + auditDelimiterColVal + vObject.getCbDomicile().trim());
			else
				strAudit.append("CB_NATIONALITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCbNationality()))
				strAudit.append("CB_NATIONALITY" + auditDelimiterColVal + vObject.getCbNationality().trim());
			else
				strAudit.append("CB_NATIONALITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCbResidence()))
				strAudit.append("CB_RESIDENCE" + auditDelimiterColVal + vObject.getCbResidence().trim());
			else
				strAudit.append("CB_RESIDENCE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getCbMajorPartyIndicator() != null && !vObject.getCbMajorPartyIndicator().equalsIgnoreCase(""))
				strAudit.append(
						"CB_MAJOR_PARTY_INDICATOR" + auditDelimiterColVal + vObject.getCbMajorPartyIndicator().trim());
			else
				strAudit.append("CB_MAJOR_PARTY_INDICATOR" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CUSTOMER_ID_TYPE_AT" + auditDelimiterColVal + vObject.getCustomerIdTypeAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerIdType()))
				strAudit.append("CUSTOMER_ID_TYPE" + auditDelimiterColVal + vObject.getCustomerIdType().trim());
			else
				strAudit.append("CUSTOMER_ID_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getIdDetails()))
				strAudit.append("ID_DETAILS" + auditDelimiterColVal + vObject.getIdDetails().trim());
			else
				strAudit.append("ID_DETAILS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getPrimaryCid()))
				strAudit.append("PRIMARY_CID" + auditDelimiterColVal + vObject.getPrimaryCid().trim());
			else
				strAudit.append("PRIMARY_CID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getParentCid() != null && !vObject.getParentCid().equalsIgnoreCase(""))
				strAudit.append("PARENT_CID" + auditDelimiterColVal + vObject.getParentCid().trim());
			else
				strAudit.append("PARENT_CID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getUltimateParent()))
				strAudit.append("ULTIMATE_PARENT" + auditDelimiterColVal + vObject.getUltimateParent().trim());
			else
				strAudit.append("ULTIMATE_PARENT" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerHierarchy1()))
				strAudit.append("CUSTOMER_HIERARCHY_1" + auditDelimiterColVal + vObject.getCustomerHierarchy1().trim());
			else
				strAudit.append("CUSTOMER_HIERARCHY_1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerHierarchy2()))
				strAudit.append("CUSTOMER_HIERARCHY_2" + auditDelimiterColVal + vObject.getCustomerHierarchy2().trim());
			else
				strAudit.append("CUSTOMER_HIERARCHY_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerHierarchy3()))
				strAudit.append("CUSTOMER_HIERARCHY_3" + auditDelimiterColVal + vObject.getCustomerHierarchy3().trim());
			else
				strAudit.append("CUSTOMER_HIERARCHY_3" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAccountOfficer()))
				strAudit.append("ACCOUNT_OFFICER" + auditDelimiterColVal + vObject.getAccountOfficer().trim());
			else
				strAudit.append("ACCOUNT_OFFICER" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CREDIT_CLASSIFICATION" + auditDelimiterColVal + vObject.getCreditClassificationAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCreditClassification()))
				strAudit.append(
						"CREDIT_CLASSIFICATION" + auditDelimiterColVal + vObject.getCreditClassification().trim());
			else
				strAudit.append("CREDIT_CLASSIFICATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("EXTERNAL_RISK_RATING_AT" + auditDelimiterColVal + vObject.getExternalRiskRatingAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getExternalRiskRating()))
				strAudit.append("EXTERNAL_RISK_RATING" + auditDelimiterColVal + vObject.getExternalRiskRating().trim());
			else
				strAudit.append("EXTERNAL_RISK_RATING" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("OBLIGOR_RISK_RATING_AT" + auditDelimiterColVal + vObject.getObligorRiskRatingAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getObligorRiskRating()))
				strAudit.append("OBLIGOR_RISK_RATING" + auditDelimiterColVal + vObject.getObligorRiskRating().trim());
			else
				strAudit.append("OBLIGOR_RISK_RATING" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RELATED_PARTY_AT" + auditDelimiterColVal + vObject.getRelatedPartyAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getRelatedParty()))
				strAudit.append("RELATED_PARTY" + auditDelimiterColVal + vObject.getRelatedParty().trim());
			else
				strAudit.append("RELATED_PARTY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CUSTOMER_TIERING_AT" + auditDelimiterColVal + vObject.getCustomerTieringAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerTiering()))
				strAudit.append("CUSTOMER_TIERING" + auditDelimiterColVal + vObject.getCustomerTiering().trim());
			else
				strAudit.append("CUSTOMER_TIERING" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append(
					"SALES_ACQUISITION_CHANNEL_AT" + auditDelimiterColVal + vObject.getSalesAcquisitionChannelAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSalesAcquisitionChannel()))
				strAudit.append("SALES_ACQUISITION_CHANNEL" + auditDelimiterColVal
						+ vObject.getSalesAcquisitionChannel().trim());
			else
				strAudit.append("SALES_ACQUISITION_CHANNEL" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getMarketingCampaign()))
				strAudit.append("MARKET_CAMPAIGN" + auditDelimiterColVal + vObject.getMarketingCampaign().trim());
			else
				strAudit.append("MARKET_CAMPAIGN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCrosssaleReferId()))
				strAudit.append("CROSS_SALE_REFER_ID" + auditDelimiterColVal + vObject.getCrosssaleReferId().trim());
			else
				strAudit.append("CROSS_SALE_REFER_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CUSTOMER_INDIACTOR_AT" + auditDelimiterColVal + vObject.getCustomerIndicatorAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerIndicator()))
				strAudit.append("CUSTOMER_INDICATOR" + auditDelimiterColVal + vObject.getCustomerIndicator().trim());
			else
				strAudit.append("CUSTOMER_INDICATOR" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("NUM_OF_ACCOUNTS" + auditDelimiterColVal + vObject.getNumOfAccounts());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerAttribute1()))
				strAudit.append("CUSTOMER_ATTRIBUTE_1" + auditDelimiterColVal + vObject.getCustomerAttribute1().trim());
			else
				strAudit.append("CUSTOMER_ATTRIBUTE_1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerAttribute2()))
				strAudit.append("CUSTOMER_ATTRIBUTE_2" + auditDelimiterColVal + vObject.getCustomerAttribute2().trim());
			else
				strAudit.append("CUSTOMER_ATTRIBUTE_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerAttribute3()))
				strAudit.append("CUSTOMER_ATTRIBUTE_3" + auditDelimiterColVal + vObject.getCustomerAttribute3().trim());
			else
				strAudit.append("CUSTOMER_ATTRIBUTE_3" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerOpenDate()))
				strAudit.append("CUSTOMER_OPEN_DATE" + auditDelimiterColVal + vObject.getCustomerOpenDate().trim());
			else
				strAudit.append("CUSTOMER_OPEN_DATE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCustomerSex()))
				strAudit.append("CUSTOMER_SEX" + auditDelimiterColVal + vObject.getCustomerSex().trim());
			else
				strAudit.append("CUSTOMER_SEX" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCommAddress1()))
				strAudit.append("COMM_ADDRESS" + auditDelimiterColVal + vObject.getCommAddress1().trim());
			else
				strAudit.append("COMM_ADDRESS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCommAddress2()))
				strAudit.append("COMM_ADDRESS_2" + auditDelimiterColVal + vObject.getCommAddress2().trim());
			else
				strAudit.append("COMM_ADDRESS_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("COMM_CITY_AT" + auditDelimiterColVal + vObject.getCommCityAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCommCity()))
				strAudit.append("COMM_CITY" + auditDelimiterColVal + vObject.getCommCity().trim());
			else
				strAudit.append("COMM_CITY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("COMM_STATE_AT" + auditDelimiterColVal + vObject.getCommStateAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCommState()))
				strAudit.append("COMM_STATE" + auditDelimiterColVal + vObject.getCommState().trim());
			else
				strAudit.append("COMM_STATE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCommPinCode()))
				strAudit.append("COMM_PIN_CODE" + auditDelimiterColVal + vObject.getCommPinCode().trim());
			else
				strAudit.append("COMM_PIN_CODE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getPhoneNumber()))
				strAudit.append("PHONE_NUMBER" + auditDelimiterColVal + vObject.getPhoneNumber().trim());
			else
				strAudit.append("PHONE_NUMBER" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDualNationality1()))
				strAudit.append("DUAL_NATIONALITY_1" + auditDelimiterColVal + vObject.getDualNationality1().trim());
			else
				strAudit.append("DUAL_NATIONALITY_1" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDualNationality2()))
				strAudit.append("DUAL_NATIONALITY_2" + auditDelimiterColVal + vObject.getDualNationality2().trim());
			else
				strAudit.append("DUAL_NATIONALITY_2" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDualNationality3()))
				strAudit.append("DUAL_NATIONALITY_3" + auditDelimiterColVal + vObject.getDualNationality3().trim());
			else
				strAudit.append("DUAL_NATIONALITY_3" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CUSTOMER_STATUS_NT" + auditDelimiterColVal + vObject.getCustomerStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("CUSTOMER_STATUS" + auditDelimiterColVal + vObject.getCustomerStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if (vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getCrsFlag() != null && !vObject.getCrsFlag().equalsIgnoreCase(""))
				strAudit.append("CRS_FLAG" + auditDelimiterColVal + vObject.getCrsFlag().trim());
			else
				strAudit.append("CRS_FLAG" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getFatcaFlag() != null && !vObject.getFatcaFlag().equalsIgnoreCase(""))
				strAudit.append("FATCA_FLAG" + auditDelimiterColVal + vObject.getFatcaFlag().trim());
			else
				strAudit.append("FATCA_FLAG" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "Customers";
		serviceDesc = CommonUtils.getResourceManger().getString("customers");
		tableName = "CUSTOMERS";
		childTableName = "CUSTOMERS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	@Override
	protected String getBuildStatus(CustomersVb customersVb) {
		return getBuildModuleStatus(customersVb.getCountry(), customersVb.getLeBook());
	}

	public int validateAccountOfficer(CustomersVb vObDlyVb) {
		int count = 0;
		final int intKeyFieldsCount = 3;
		String strQuery = new String(
				"Select  count(1) From ACCOUNT_OFFICERS Where COUNTRY = ? AND LE_BOOK=? AND ACCOUNT_OFFICER =? AND AO_Status =0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getCountry();
		objParams[1] = vObDlyVb.getLeBook();
		objParams[2] = vObDlyVb.getAccountOfficer();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateNAICS(CustomersVb vObDlyVb) {

		int count = 0;
		final int intKeyFieldsCount = 1;
		String strQuery = new String("Select  count(1) From NAICS_Codes Where NAICS_Code = ? AND NAICS_status =0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getNaicsCode();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateMAXIMOUC(CustomersVb vObDlyVb) {

		int count = 0;
		final int intKeyFieldsCount = 3;
		String strQuery = new String(
				"Select  count(1)  From OUC_CODES Where VISION_OUC = ? AND COUNTRY = ? AND LE_BOOK=? AND OUC_Status=0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getVisionOuc();
		objParams[1] = vObDlyVb.getCountry();
		objParams[2] = vObDlyVb.getLeBook();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateGlobelCustomerId(CustomersVb vObDlyVb) {

		int count = 0;
		final int intKeyFieldsCount = 3;
		String strQuery = new String("Select  count(1) From " + Customers
				+ " Where CUSTOMER_ID = ? AND COUNTRY = ? AND LE_BOOK=? AND CUSTOMER_STATUS = 0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getGlobalCustomerId();
		objParams[1] = vObDlyVb.getCountry();
		objParams[2] = vObDlyVb.getLeBook();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateCBDomicile(String strCountry) {

		int count = 0;
		final int intKeyFieldsCount = 1;
		String strQuery = new String("Select  count(1) From Countries Where Country = ? and Country_Status =0 ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = strCountry;
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validatePrimaryCID(CustomersVb vObDlyVb) {
		int count = 0;
		final int intKeyFieldsCount = 2;
		String strQuery = new String("Select  count(1)  From " + Customers + " Where COUNTRY " + pipeLine + "'-'"
				+ pipeLine + "LE_BOOK = ? AND CUSTOMER_ID = ?");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getEntity();
		objParams[1] = vObDlyVb.getPrimaryCid();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateParentCID(CustomersVb vObDlyVb) {

		int count = 0;
		final int intKeyFieldsCount = 2;
		String strQuery = new String("Select  count(1)  From " + Customers + " Where COUNTRY " + pipeLine + "'-'"
				+ pipeLine + "LE_BOOK = ? AND CUSTOMER_ID = ?");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getEntity();
		objParams[1] = vObDlyVb.getParentCid();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateUltimateParent(CustomersVb vObDlyVb) {

		int count = 0;
		final int intKeyFieldsCount = 2;
		String strQuery = new String("Select  count(1)  From " + Customers + " Where COUNTRY " + pipeLine + "'-'"
				+ pipeLine + "LE_BOOK = ? AND CUSTOMER_ID = ?");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vObDlyVb.getEntity();
		objParams[1] = vObDlyVb.getUltimateParent();
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateCustomerHierarchys(String vCustomerHierarchy) {

		int count = 0;
		final int intKeyFieldsCount = 1;
		String strQuery = new String(
				"Select  count(1) From Customer_Attributes Where Customer_Attribute in (?) and Customer_Att_Status=0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = vCustomerHierarchy;
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int validateCustomerAttributes(String strCustomerAttribute) {

		int count = 0;
		final int intKeyFieldsCount = 1;
		String strQuery = new String(
				"Select  count(1) From Customer_Attributes Where Customer_Attribute in(?) and Customer_Att_Status=0");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = strCustomerAttribute;
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class, objParams);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	protected ExceptionCode doInsertApprRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		List<CustomersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<CustomersVb>) collTemp).get(0));
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
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doInsertionAppr(vObject);
		vObject.setCusExtrasStatus(vObject.getCustomerStatus());
		vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			retVal = doInsertionApprForCustExtras(vObject);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			if (vObject.getManualList() != null && !vObject.getManualList().isEmpty()) {
				vObject.getManualList().forEach(n -> {
					n.setRecordIndicator(vObject.getRecordIndicator());
					customerManualDao.doInsertCustomerManual(n, true);
				});

			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	protected ExceptionCode doUpdateApprRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		List<CustomersVb> collTemp = null;
		CustomersVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
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
		vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateAppr(vObject);

		vObject.setCusExtrasStatus(vObject.getCustomerStatus());
		vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			retVal = doUpdateApprForCustExtras(vObject);
		}

		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			if (vObject.getManualList() != null && !vObject.getManualList().isEmpty()) {
				vObject.getManualList().forEach(n -> {
					retVal = customerManualDao.deleteAndInsertCustomerManualAppr(n);
				});
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	protected ExceptionCode doInsertRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		List<CustomersVb> collTemp = null;
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
		if (collTemp.size() > 0) {
			int staticDeletionFlag = getStatus(((ArrayList<CustomersVb>) collTemp).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE) {
				logger.info("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		collTemp = null;
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			CustomersVb vObjectLocal = ((ArrayList<CustomersVb>) collTemp).get(0);
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
			}
			vObject.setCusExtrasStatus(vObject.getCustomerStatus());
			vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
			retVal = doInsertionPendForCustExtras(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getManualList() != null && !vObject.getManualList().isEmpty()) {
				vObject.getManualList().forEach(n -> {
					n.setRecordIndicator(vObject.getRecordIndicator());
					customerManualDao.doInsertCustomerManual(n, false);
				});

			}
			exceptionCode = getResultObject(retVal);
			return exceptionCode;
		}
	}

	protected ExceptionCode doUpdateRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		List<CustomersVb> collTemp = null;
		CustomersVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) {
				exceptionCode = getResultObject(Constants.RECORD_PENDING_FOR_DELETION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			vObject.setCusExtrasStatus(vObject.getCustomerStatus());
			vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePend(vObject);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = doUpdatePendForCustExtras(vObject);
			} else {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePend(vObject);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = doUpdatePendForCustExtras(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getManualList() != null && !vObject.getManualList().isEmpty()) {
				vObject.getManualList().forEach(n -> {
					n.setRecordIndicator(vObject.getRecordIndicator());
					n.setMaker(vObject.getMaker());
					n.setVerifier(vObject.getVerifier());
					retVal = customerManualDao.deleteAndInsertCustomerManualPend(n);
				});
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} else {
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() > 0) {
				vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			vObject.setCusExtrasStatus(vObject.getCustomerStatus());
			vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
			//
			vObjectlocal.setRecordIndicator(Constants.STATUS_UPDATE);
			vObjectlocal.setMaker(intCurrentUserId);
			retVal = doInsertionPendWithDc(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = doInsertionPendWithDcForCustExtras(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (vObject.getManualList() != null && !vObject.getManualList().isEmpty()) {
				vObject.getManualList().forEach(n -> {
					n.setRecordIndicator(vObject.getRecordIndicator());
					n.setMaker(vObject.getMaker());
					n.setVerifier(vObject.getVerifier());
					retVal = customerManualDao.deleteAndInsertCustomerManualPend(n);
				});
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	protected ExceptionCode doDeleteApprRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		List<CustomersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		CustomersVb vObjectlocal = null;
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
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<CustomersVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		vObject.setCusExtrasStatus(vObject.getCustomerStatus());
		vObject.setCusExtrasStatusNt(vObject.getCustomerStatusNt());
		vObjectlocal.setCusExtrasStatus(vObjectlocal.getCustomerStatus());
		vObjectlocal.setCusExtrasStatusNt(vObjectlocal.getCustomerStatusNt());
		if (vObject.isStaticDelete()) {
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateAppr(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = doUpdateApprForCustExtras(vObjectlocal);
			if (vObjectlocal.getManualList() != null && !vObjectlocal.getManualList().isEmpty()) {
				vObjectlocal.getManualList().forEach(n -> {
					retVal = customerManualDao.deleteAndInsertCustomerManualAppr(n);
				});
			}

		} else {
			retVal = doDeleteAppr(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = doDeleteApprForCustExtras(vObject);
			if (vObjectlocal.getManualList() != null && !vObjectlocal.getManualList().isEmpty()) {
				vObjectlocal.getManualList().forEach(n -> {
					retVal = customerManualDao.doDeleteCustomerManual(n, true);
				});
			}
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.isStaticDelete()) {
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject, vObjectlocal);
		} else {
			exceptionCode = writeAuditLog(null, vObject);
			vObject.setRecordIndicator(-1);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

//	protected ExceptionCode doDeleteRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
//		CustomersVb vObjectlocal = null;
//		List<CustomersVb> collTemp = null;
//		ExceptionCode exceptionCode = null;
//		strApproveOperation = Constants.DELETE;
//		strErrorDesc = "";
//		strCurrentOperation = Constants.DELETE;
//		setServiceDefaults();
//		collTemp = selectApprovedRecord(vObject);
//		if (collTemp == null) {
//			logger.error("Collection is null");
//			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		if (collTemp.size() > 0) {
//			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
//			int intStaticDeletionFlag = getStatus(vObjectlocal);
//			if (intStaticDeletionFlag == Constants.PASSIVATE) {
//				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//		} else {
//			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		collTemp = doSelectPendingRecord(vObject);
//		if (collTemp == null) {
//			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		if (collTemp.size() > 0) {
//			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		if (vObjectlocal == null) {
//			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		if (collTemp.size() > 0) {
//			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
//			vObjectlocal.setDateCreation(vObject.getDateCreation());
//		}
//		vObjectlocal.setMaker(getIntCurrentUserId());
//		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
//		vObjectlocal.setVerifier(0);
//		vObjectlocal.setCusExtrasStatus(vObjectlocal.getCustomerStatus());
//		vObjectlocal.setCusExtrasStatusNt(vObjectlocal.getCustomerStatusNt());
//		retVal = doInsertionPendWithDc(vObjectlocal);
//		if (retVal != Constants.SUCCESSFUL_OPERATION) {
//			exceptionCode = getResultObject(retVal);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		retVal = doInsertionPendWithDcForCustExtras(vObjectlocal);
//		if (retVal != Constants.SUCCESSFUL_OPERATION) {
//			exceptionCode = getResultObject(retVal);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//		vObject.setRecordIndicator(Constants.STATUS_DELETE);
//		vObject.setVerifier(0);
//		return getResultObject(Constants.SUCCESSFUL_OPERATION);
//	}

	protected ExceptionCode doDeleteRecordForNonTrans(CustomersVb vObject) throws RuntimeCustomException {
		CustomersVb vObjectlocal = null;
		List<CustomersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();

		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObjectlocal == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}

		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		vObjectlocal.setCusExtrasStatus(vObjectlocal.getCustomerStatus());
		vObjectlocal.setCusExtrasStatusNt(vObjectlocal.getCustomerStatusNt());

		// 1) Insert delete request into CUSTOMER_PEND
		retVal = doInsertionPendWithDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// 2) Insert delete request into CUSTOMER_EXTRAS_PEND (your existing flow)
		retVal = doInsertionPendWithDcForCustExtras(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// 3) *** NEW *** Stage CUSTOMER_MANUAL rows: MAIN -> PEND as STATUS_DELETE
		int cm = stageCustomerManualDeleteToPend(vObjectlocal);
		if (cm != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(cm);
			throw buildRuntimeCustomException(exceptionCode);
		}

		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected int doInsertionApprForCustExtras(CustomersVb vObject) {
		String query = "INSERT INTO " + CustomersExtras
				+ " (COUNTRY, LE_BOOK, CUSTOMER_ID, CUS_EXTRAS_STATUS_NT, CUS_EXTRAS_STATUS, "
				+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, "
				+ "DATE_CREATION, DATE_LAST_MODIFIED, DUAL_NATIONALITY_1, DUAL_NATIONALITY_2, DUAL_NATIONALITY_3) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ", ?, ?, ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getCusExtrasStatusNt(), vObject.getCusExtrasStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDualNationality1(), vObject.getDualNationality2(), vObject.getDualNationality3() };

		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendForCustExtras(CustomersVb vObject) {
		String query = "INSERT INTO " + CustomersExtras + "_PEND (COUNTRY, LE_BOOK, CUSTOMER_ID, CUS_EXTRAS_STATUS_NT, "
				+ "CUS_EXTRAS_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, "
				+ "DATE_CREATION, DATE_LAST_MODIFIED, DUAL_NATIONALITY_1, DUAL_NATIONALITY_2, DUAL_NATIONALITY_3) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ", ?, ?, ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getCusExtrasStatusNt(), vObject.getCusExtrasStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDualNationality1(), vObject.getDualNationality2(), vObject.getDualNationality3() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendWithDcForCustExtras(CustomersVb vObject) {
		String query = "INSERT INTO " + CustomersExtras + "_PEND (COUNTRY, LE_BOOK, CUSTOMER_ID, CUS_EXTRAS_STATUS_NT, "
				+ "CUS_EXTRAS_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, "
				+ "DATE_CREATION, DATE_LAST_MODIFIED, DUAL_NATIONALITY_1, DUAL_NATIONALITY_2, DUAL_NATIONALITY_3) "
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + dateTimeConvert + ", ?, ?, ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId(),
				vObject.getCusExtrasStatusNt(), vObject.getCusExtrasStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDateCreation(), vObject.getDualNationality1(), vObject.getDualNationality2(),
				vObject.getDualNationality3() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateApprForCustExtras(CustomersVb vObject) {
		String query = "UPDATE " + CustomersExtras + " SET "
				+ " DUAL_NATIONALITY_1 = ?, DUAL_NATIONALITY_2 = ?, DUAL_NATIONALITY_3 = ?, "
				+ " RECORD_INDICATOR = ?, CUS_EXTRAS_STATUS = ?, DATE_LAST_MODIFIED =  " + systemDate
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ?";
		Object[] args = { vObject.getDualNationality1(), vObject.getDualNationality2(), vObject.getDualNationality3(),
				vObject.getRecordIndicator(), vObject.getCusExtrasStatus(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendForCustExtras(CustomersVb vObject) {
		String query = "UPDATE " + CustomersExtras + "_PEND SET "
				+ " DUAL_NATIONALITY_1 = ?, DUAL_NATIONALITY_2 = ?, DUAL_NATIONALITY_3 = ?, "
				+ " RECORD_INDICATOR = ?, CUS_EXTRAS_STATUS = ?, DATE_LAST_MODIFIED =  " + systemDate
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ?";
		Object[] args = { vObject.getDualNationality1(), vObject.getDualNationality2(), vObject.getDualNationality3(),
				vObject.getRecordIndicator(), vObject.getCusExtrasStatus(), vObject.getCountry(), vObject.getLeBook(),
				vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeleteApprForCustExtras(CustomersVb vObject) {
		String query = "Delete From " + CustomersExtras + " Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeletePendForCustExtras(CustomersVb vObject) {
		String query = " Delete From " + CustomersExtras
				+ "_PEND Where COUNTRY = ? AND LE_BOOK = ? AND CUSTOMER_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

//	public ExceptionCode doApproveRecord(CustomersVb vObject, boolean staticDelete) throws RuntimeCustomException {
//		CustomersVb oldContents = null;
//		CustomersVb vObjectlocal = null;
//		List<CustomersVb> collTemp = null;
//		ExceptionCode exceptionCode = null;
//		strCurrentOperation = Constants.APPROVE;
//		setServiceDefaults();
//		try {
//			if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
//				exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING_APPROVE);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//			// See if such a pending request exists in the pending table
//			vObject.setVerifier(getIntCurrentUserId());
//			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			collTemp = doSelectPendingRecord(vObject);
//			if (collTemp == null) {
//				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//
//			if (collTemp.size() == 0) {
//				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//
//			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
//
//			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
//				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//
//			// If it's NOT addition, collect the existing record contents from the
//			// Approved table and keep it aside, for writing audit information later.
//			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
//				collTemp = selectApprovedRecord(vObject);
//				if (collTemp == null || collTemp.isEmpty()) {
//					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//					throw buildRuntimeCustomException(exceptionCode);
//				}
//				oldContents = ((ArrayList<CustomersVb>) collTemp).get(0);
//			}
//
//			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
//				// Write the contents of the Pending table record to the Approved table
//				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
//				vObjectlocal.setVerifier(getIntCurrentUserId());
//				retVal = doInsertionAppr(vObjectlocal);
//				if (retVal != Constants.SUCCESSFUL_OPERATION) {
//					exceptionCode = getResultObject(retVal);
//					throw buildRuntimeCustomException(exceptionCode);
//				} else {
//					retVal = doInsertionApprForCustExtras(vObjectlocal);
//				}
//				if (retVal != Constants.SUCCESSFUL_OPERATION) {
//					exceptionCode = getResultObject(retVal);
//					throw buildRuntimeCustomException(exceptionCode);
//				} else {
//					exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//				}
//
//				String systemDate = getSystemDate();
//				vObject.setDateLastModified(systemDate);
//				vObject.setDateCreation(systemDate);
//				strApproveOperation = Constants.ADD;
//			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization
//
//				collTemp = selectApprovedRecord(vObject);
//				if (collTemp == null || collTemp.isEmpty()) {
//					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
//					throw buildRuntimeCustomException(exceptionCode);
//				}
//
//				// If record already exists in the approved table, reject the addition
//				if (collTemp.size() > 0) {
//					// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
//					vObjectlocal.setVerifier(getIntCurrentUserId());
//					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
//					retVal = doUpdateAppr(vObjectlocal);
//				}
//				// Modify the existing contents of the record in Approved table
//				if (retVal != Constants.SUCCESSFUL_OPERATION) {
//					exceptionCode = getResultObject(retVal);
//					throw buildRuntimeCustomException(exceptionCode);
//				} else {
//					retVal = doUpdateApprForCustExtras(vObjectlocal);
////					retVal = customerManualDao.deleteAndInsertCustomerManualAppr(null)
//				}
//				if (retVal != Constants.SUCCESSFUL_OPERATION) {
//					exceptionCode = getResultObject(retVal);
//					throw buildRuntimeCustomException(exceptionCode);
//				} else {
//					exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//				}
//				String systemDate = getSystemDate();
//				vObject.setDateLastModified(systemDate);
//				// Set the current operation to write to audit log
//				strApproveOperation = Constants.MODIFY;
//			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) { // Delete authorization
//				if (staticDelete) {
//					// Update the existing record status in the Approved table to delete
//					setStatus(vObjectlocal, Constants.PASSIVATE);
//					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
//					vObjectlocal.setVerifier(getIntCurrentUserId());
//					retVal = doUpdateAppr(vObjectlocal);
//					if (retVal != Constants.SUCCESSFUL_OPERATION) {
//						exceptionCode = getResultObject(retVal);
//						throw buildRuntimeCustomException(exceptionCode);
//					} else {
//						retVal = doUpdateApprForCustExtras(vObjectlocal);
//					}
//					if (retVal != Constants.SUCCESSFUL_OPERATION) {
//						exceptionCode = getResultObject(retVal);
//						throw buildRuntimeCustomException(exceptionCode);
//					} else {
//						exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//					}
//					setStatus(vObject, Constants.PASSIVATE);
//					String systemDate = getSystemDate();
//					vObject.setDateLastModified(systemDate);
//
//				} else {
//					// Delete the existing record from the Approved table
//					retVal = doDeleteAppr(vObjectlocal);
//					if (retVal != Constants.SUCCESSFUL_OPERATION) {
//						exceptionCode = getResultObject(retVal);
//						throw buildRuntimeCustomException(exceptionCode);
//					}
//					retVal = doDeleteApprForCustExtras(vObjectlocal);
//					if (retVal != Constants.SUCCESSFUL_OPERATION) {
//						exceptionCode = getResultObject(retVal);
//						throw buildRuntimeCustomException(exceptionCode);
//					}
//					String systemDate = getSystemDate();
//					vObject.setDateLastModified(systemDate);
//				}
//				// Set the current operation to write to audit log
//				strApproveOperation = Constants.DELETE;
//			} else {
//				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//
//			// Delete the record from the Pending table
//			retVal = deletePendingRecord(vObjectlocal);
//
//			if (retVal != Constants.SUCCESSFUL_OPERATION) {
//				exceptionCode = getResultObject(retVal);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//			retVal = doDeletePendForCustExtras(vObject);
//			if (retVal != Constants.SUCCESSFUL_OPERATION) {
//				exceptionCode = getResultObject(retVal);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//			// Set the internal status to Approved
//			vObject.setInternalStatus(0);
//			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
//				exceptionCode = writeAuditLog(null, oldContents);
//				vObject.setRecordIndicator(-1);
//			} else
//				exceptionCode = writeAuditLog(vObjectlocal, oldContents);
//
//			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
//				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
//				throw buildRuntimeCustomException(exceptionCode);
//			}
//			return getResultObject(Constants.SUCCESSFUL_OPERATION);
//		} catch (UncategorizedSQLException uSQLEcxception) {
//			strErrorDesc = parseErrorMsg(uSQLEcxception);
//			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
//			throw buildRuntimeCustomException(exceptionCode);
//		} catch (Exception ex) {
//			logger.error("Error in Approve.", ex);
//			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
//			strErrorDesc = ex.getMessage();
//			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//	}
	public ExceptionCode doApproveRecord(CustomersVb vObject, boolean staticDelete) throws RuntimeCustomException {
		CustomersVb oldContents = null;
		CustomersVb vObjectlocal = null;
		List<CustomersVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
				exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
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

			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// If it's NOT addition, collect the existing record contents from the Approved
			// table
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<CustomersVb>) collTemp).get(0);
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionAppr(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				} else {
					retVal = doInsertionApprForCustExtras(vObjectlocal);
				}
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// ---- CUSTOMER MANUAL: INSERT branch
				int cm = syncCustomerManualForApproval(vObjectlocal, Constants.STATUS_INSERT, false);
				if (cm != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(cm);
					throw buildRuntimeCustomException(exceptionCode);
				}

				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
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

				if (collTemp.size() > 0) {
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateAppr(vObjectlocal);
				}
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				} else {
					retVal = doUpdateApprForCustExtras(vObjectlocal);
				}
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// ---- CUSTOMER MANUAL: UPDATE branch
				int cm = syncCustomerManualForApproval(vObjectlocal, Constants.STATUS_UPDATE, false);
				if (cm != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(cm);
					throw buildRuntimeCustomException(exceptionCode);
				}

				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				strApproveOperation = Constants.MODIFY;

			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) { // Delete authorization
				if (staticDelete) {
					// Soft delete
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doUpdateAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					} else {
						retVal = doUpdateApprForCustExtras(vObjectlocal);
					}
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}

					// ---- CUSTOMER MANUAL: DELETE (soft)
					int cm = syncCustomerManualForApproval(vObjectlocal, Constants.STATUS_DELETE, true);
					if (cm != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(cm);
						throw buildRuntimeCustomException(exceptionCode);
					}

					exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);

				} else {
					// Hard delete
					retVal = doDeleteAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					retVal = doDeleteApprForCustExtras(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}

					// ---- CUSTOMER MANUAL: DELETE (hard)
					int cm = syncCustomerManualForApproval(vObjectlocal, Constants.STATUS_DELETE, false);
					if (cm != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(cm);
						throw buildRuntimeCustomException(exceptionCode);
					}

					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
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
			}
			retVal = doDeletePendForCustExtras(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
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

	public ExceptionCode doRejectRecord(CustomersVb vObject) throws RuntimeCustomException {
		CustomersVb vObjectlocal = null;
		List<CustomersVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<CustomersVb>) collTemp).get(0);
			// Delete the record from the Pending table
			if (deletePendingRecord(vObjectlocal) == 0) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				retVal = doDeletePendForCustExtras(vObjectlocal);
				retVal = customerManualDao.doDeleteCustomerManual(vObjectlocal, false);
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

	private CustomerManualColVb buildManualKeyFromCustomer(CustomersVb cust) {
		CustomerManualColVb cm = new CustomerManualColVb();
		cm.setCountry(cust.getCountry());
		cm.setLeBook(cust.getLeBook());
		cm.setCustomerId(cust.getCustomerId());
		// Map more keys if your schema uses them:
		// cm.setOuCode(cust.getOuCode());
		// cm.setBranchCode(cust.getBranchCode());
		return cm;
	}

	/**
	 * Sync Customer Manual (PEND → APPR, APPR → HIS) during Customers approval.
	 * 
	 * INSERT/UPDATE: 1) Read MANUAL_PEND 2) If MANUAL (approved) has rows → write
	 * them to MANUAL_HIS 3) Delete MANUAL (approved) 4) Insert MANUAL_PEND rows
	 * into MANUAL (approved) 5) Clear MANUAL_PEND
	 *
	 * DELETE (hard): 1) If MANUAL (approved) has rows → write them to MANUAL_HIS 2)
	 * Delete MANUAL (approved) 3) Clear MANUAL_PEND
	 *
	 * DELETE (soft): 1) If MANUAL (approved) has rows → write them to MANUAL_HIS
	 * (as delete) 2) Passivate MANUAL (approved) (or delete if passivation not
	 * supported) 3) Clear MANUAL_PEND
	 */
	/**
	 * Sync Customer Manual data during Customers approval. INSERT/UPDATE: PEND ->
	 * (HIS current APPR) -> APPR; then clear PEND DELETE hard : (HIS current APPR)
	 * -> delete APPR; then clear PEND DELETE soft : (HIS current APPR) ->
	 * passivate/delete APPR (we delete here); then clear PEND
	 */
	private int syncCustomerManualForApproval(CustomersVb custKey, int op, boolean staticDelete) {
		CustomerManualColVb key = buildManualKeyFromCustomer(custKey);

		// fetch current states
		List<CustomerManualColVb> pendRows = customerManualDao.selectPendByKey(key);
		List<CustomerManualColVb> apprRows = customerManualDao.selectApprByKey(key);
		List<String> pendCols = pendRows.stream()
		        .map(CustomerManualColVb::getColumnName)
		        .collect(Collectors.toList());

		List<String> apprCols = apprRows.stream()
		        .map(CustomerManualColVb::getColumnName)
		        .collect(Collectors.toList());

		List<String> commonCols = pendCols.stream()
		        .filter(apprCols::contains)
		        .collect(Collectors.toList());

		int rc;

		if (op == Constants.STATUS_INSERT || op == Constants.STATUS_UPDATE) {
			// Move existing approved rows to history
			if (apprRows != null && !apprRows.isEmpty()) {
				for (CustomerManualColVb row : apprRows) {
					row.setRecordIndicator(Constants.STATUS_UPDATE);
					row.setVerifier(getIntCurrentUserId());
				}
				rc = customerManualDao.insertHistory(apprRows, "BEFORE");
				if (rc == Constants.ERRONEOUS_OPERATION)
					return rc;
				if (pendRows == null && pendRows.isEmpty())
				rc = customerManualDao.deleteApprovedByKey(key);
				if (rc == Constants.ERRONEOUS_OPERATION)
					return rc;
				if(commonCols!=null && !commonCols.isEmpty()) {
					for(String ColumName: commonCols) {
					rc = customerManualDao.deleteApprovedByColumn(key,ColumName);
					if (rc == Constants.ERRONEOUS_OPERATION)
						return rc;
					}
				}
				
			}

			// Insert pend -> approved
			if (pendRows != null && !pendRows.isEmpty()) {
				for (CustomerManualColVb row : pendRows) {
					row.setRecordIndicator(Constants.STATUS_ZERO);
					row.setVerifier(getIntCurrentUserId());
				}
				rc = customerManualDao.insertApproved(pendRows);
				if (rc == Constants.ERRONEOUS_OPERATION)
					return rc;
			}

		} else if (op == Constants.STATUS_DELETE) {
			// Write current approved to history
			if (apprRows != null && !apprRows.isEmpty()) {
				for (CustomerManualColVb row : apprRows) {
					row.setRecordIndicator(Constants.STATUS_DELETE);
					row.setVerifier(getIntCurrentUserId());
				}
				rc = customerManualDao.insertHistory(apprRows, "DELETE");
				if (rc == Constants.ERRONEOUS_OPERATION)
					return rc;
			}

			// Soft delete = passivate (if supported) OR delete; here we delete
			rc = customerManualDao.deleteApprovedByKey(key);
			if (rc == Constants.ERRONEOUS_OPERATION)
				return rc;
			if (staticDelete) {
				// 3) STATIC DELETE: if PENDING exists, promote -> APPROVED, then clear PENDING
				if (pendRows != null && !pendRows.isEmpty()) {
					for (CustomerManualColVb row : pendRows) {
						row.setRecordIndicator(Constants.STATUS_ZERO);
						row.setColumnStatus(Constants.PASSIVATE);
						row.setVerifier(getIntCurrentUserId());
					}
					rc = customerManualDao.insertApproved(pendRows);
					if (rc == Constants.ERRONEOUS_OPERATION)
						return rc;

					
				}
				// If no PENDING, nothing else to do.
			}
		}

		// Always clear pend after finishing
		if(pendRows != null && !pendRows.isEmpty() ||
				apprRows != null && !apprRows.isEmpty()	) {
		rc = customerManualDao.deletePendByKey(key);
		if (rc == Constants.ERRONEOUS_OPERATION)
			return rc;
		}

		return Constants.SUCCESSFUL_OPERATION;
	}

	private int stageCustomerManualDeleteToPend(CustomersVb cust) {
		// Build CustomerManual key
		setServiceDefaults();
		CustomerManualColVb key = new CustomerManualColVb();
		key.setCountry(cust.getCountry());
		key.setLeBook(cust.getLeBook());
		key.setCustomerId(cust.getCustomerId());

		// Fetch all approved manual rows for this customer
		List<CustomerManualColVb> apprRows = customerManualDao.selectApprByKey(key);

		if (apprRows == null || apprRows.isEmpty()) {
			return Constants.SUCCESSFUL_OPERATION; // nothing to stage
		}

		int totalOps = 0;

		for (CustomerManualColVb row : apprRows) {
			// prepare a pending "delete" image
			row.setMaker(intCurrentUserId);
			row.setVerifier(0);
			row.setRecordIndicator(Constants.STATUS_DELETE);
			// keep other values as-is (COLUMN_VALUE etc.)

			// Upsert into PEND (this method: backup existing PEND to HIS, delete it, then
			// insert)
			totalOps += customerManualDao.deleteAndInsertCustomerManualPend(row);
		}

		// If we processed rows without exception, consider it success
		return Constants.SUCCESSFUL_OPERATION;
	}

}
