package com.vision.wb;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CustomerManualColVb;
import com.vision.dao.CustomerManualDao;
import com.vision.dao.CustomersDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.CommonVb;
import com.vision.vb.CustomersVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.VisionUsersVb;
@Component
public class CustomersWb extends AbstractWorkerBean<CustomersVb> {
	@Autowired
	private CustomersDao customersDao;
	@Autowired
	private CustomerManualDao customerManualDao;
	public static Logger logger = LoggerFactory.getLogger(CustomersWb.class);

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(1); // status
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(7); // record_indicator
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(3); // Vision Sbu
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(41, "ALPHA_SUBTAB_DESCRIPTION");
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(42, "ALPHA_SUBTAB_DESCRIPTION");
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(43); // Customer Id Type
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(40); // Customer Sex
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(44); // Credit Classification
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(45); // External Risk rating
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(46); // Obligor Risk Rating
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(49); // Related Party
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(47); // Customer Tiring
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(48); // Sales Acquisiontion Channel
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(51); // Customer Indicator
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(38); // Common city
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(39); // Common state
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().findVerificationRequiredAndStaticDelete("CUSTOMERS");
			arrListLocal.add(collTemp);
			collTemp = getCommonDao().getLegalEntity();
			arrListLocal.add(collTemp);
//			String country = "";
//			String leBook = "";
//			VisionUsersVb visionUsers = SessionContextHolder.getContext();
//			if ("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
//				if (ValidationUtil.isValid(visionUsers.getCountry())) {
//					country = visionUsers.getCountry();
//				}
//				if (ValidationUtil.isValid(visionUsers.getLeBook())) {
//					leBook = visionUsers.getLeBook();
//				}
//			} else {
//				country = getCommonDao().findVisionVariableValue("DEFAULT_COUNTRY");
//				leBook = getCommonDao().findVisionVariableValue("DEFAULT_LE_BOOK");
//			}
//			String countryLeBook = country + "-" + leBook;
			arrListLocal.add("");
			collTemp = customerManualDao.findAllGroupedAsCustomers();
			arrListLocal.add(collTemp);
			collTemp =getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(150);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	@Override
	protected List<ReviewResultVb> transformToReviewResults(List<CustomersVb> approvedCollection,
	        List<CustomersVb> pendingCollection) {

	    ArrayList collTemp = getPageLoadValues();

	    ResourceBundle rsb = CommonUtils.getResourceManger();

	    CustomersVb pending = (pendingCollection != null && !pendingCollection.isEmpty())
	            ? pendingCollection.get(0)
	            : null;
	    CustomersVb approved = (approvedCollection != null && !approvedCollection.isEmpty())
	            ? approvedCollection.get(0)
	            : null;

	    if (pending != null) {
	        getScreenDao().fetchMakerVerifierNames(pending);
	    }
	    if (approved != null) {
	        getScreenDao().fetchMakerVerifierNames(approved);
	    }

	    // Apply manual overrides to pending BEFORE building review results
	    if (pending != null && pending.getManualList() != null && !pending.getManualList().isEmpty()) {
	        applyManualOverridesToPending(pending);
	    }

	    ArrayList<ReviewResultVb> lResult = new ArrayList<>();

	    // helper lambda to safe-get string values
	    java.util.function.Function<Object, String> safeToStr = o -> o == null ? "" : String.valueOf(o);

	    // Example of replacing many repetitive blocks using pending/approved locals.
	    // If pending or approved is null, we fallback to empty strings or default values.

	    lResult.add(new ReviewResultVb(rsb.getString("customerId"),
	            (pending == null ? "" : safeToStr.apply(pending.getCustomerId())),
	            (approved == null ? "" : safeToStr.apply(approved.getCustomerId())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerId())
	                    .equals(safeToStr.apply(approved.getCustomerId()))));

	    lResult.add(new ReviewResultVb(rsb.getString("customerName"),
	            (pending == null ? "" : safeToStr.apply(pending.getCustomerName())),
	            (approved == null ? "" : safeToStr.apply(approved.getCustomerName())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerName())
	                    .equals(safeToStr.apply(approved.getCustomerName()))));

//	    lResult.add(new ReviewResultVb(rsb.getString("customerAccronym"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerAcronym())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerAcronym())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerAcronym())
//	                    .equals(safeToStr.apply(approved.getCustomerAcronym()))));

	    lResult.add(new ReviewResultVb(rsb.getString("visionOUC"),
	            (pending == null ? "" : safeToStr.apply(pending.getVisionOuc())),
	            (approved == null ? "" : safeToStr.apply(approved.getVisionOuc())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getVisionOuc())
	                    .equals(safeToStr.apply(approved.getVisionOuc()))));

	    lResult.add(new ReviewResultVb(rsb.getString("visionSBU"),
	            (pending == null ? ""
	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(2), pending.getVisionSbu())),
	            (approved == null ? ""
	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(2), approved.getVisionSbu())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getVisionSbu())
	                    .equals(safeToStr.apply(approved.getVisionSbu()))));
//
//	    lResult.add(new ReviewResultVb(rsb.getString("globalCustomerId"),
//	            (pending == null ? "" : safeToStr.apply(pending.getGlobalCustomerId())),
//	            (approved == null ? "" : safeToStr.apply(approved.getGlobalCustomerId())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getGlobalCustomerId())
//	                    .equals(safeToStr.apply(approved.getGlobalCustomerId()))));
//
//	    // naicsCode (convert to string safely)
//	    lResult.add(new ReviewResultVb(rsb.getString("naicsCode"),
//	            (pending == null ? "" : safeToStr.apply(pending.getNaicsCode())),
//	            (approved == null ? "" : safeToStr.apply(approved.getNaicsCode())),
//	            !(pending == null || approved == null)
//	                    && !safeToStr.apply(pending.getNaicsCode()).equals(safeToStr.apply(approved.getNaicsCode()))));

	    // cbOrgCode using getAtDescription
//	    lResult.add(new ReviewResultVb(rsb.getString("cbOrgCode"),
//	            (pending == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(3), pending.getCbOrgCode())),
//	            (approved == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(3), approved.getCbOrgCode())),
//	            !(pending == null || approved == null)
//	                    && !safeToStr.apply(pending.getCbOrgCode()).equals(safeToStr.apply(approved.getCbOrgCode()))));

	    // cbEconomicActCode
//	    lResult.add(new ReviewResultVb(rsb.getString("cbEconomicActCode"),
//	            (pending == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
//	                    pending.getCbEconomicActCode())),
//	            (approved == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
//	                    approved.getCbEconomicActCode())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCbEconomicActCode())
//	                    .equals(safeToStr.apply(approved.getCbEconomicActCode()))));

	    // cbDomicile
//	    lResult.add(new ReviewResultVb(rsb.getString("cbDomicile"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCbDomicile())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCbDomicile())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCbDomicile())
//	                    .equals(safeToStr.apply(approved.getCbDomicile()))));

	    // cbNationality
	    lResult.add(new ReviewResultVb(rsb.getString("cbNationality"),
	            (pending == null ? "" : safeToStr.apply(pending.getCbNationality())),
	            (approved == null ? "" : safeToStr.apply(approved.getCbNationality())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCbNationality())
	                    .equals(safeToStr.apply(approved.getCbNationality()))));

	    // cbResidence
	    lResult.add(new ReviewResultVb(rsb.getString("cbResidence"),
	            (pending == null ? "" : safeToStr.apply(pending.getCbResidence())),
	            (approved == null ? "" : safeToStr.apply(approved.getCbResidence())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCbResidence())
	                    .equals(safeToStr.apply(approved.getCbResidence()))));

	    // majorPartyIndicator -> Yes/No
//	    lResult.add(new ReviewResultVb(rsb.getString("majorPartyIndicator"),
//	            (pending == null ? "" : ("Y".equalsIgnoreCase(pending.getCbMajorPartyIndicator()) ? "Yes" : "No")),
//	            (approved == null ? "" : ("Y".equalsIgnoreCase(approved.getCbMajorPartyIndicator()) ? "Yes" : "No")),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCbMajorPartyIndicator())
//	                    .equals(safeToStr.apply(approved.getCbMajorPartyIndicator()))));

	    // customerIdType
//	    lResult.add(new ReviewResultVb(rsb.getString("customerIdType"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(5), pending.getCustomerIdType())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(5), approved.getCustomerIdType())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerIdType())
//	                    .equals(safeToStr.apply(approved.getCustomerIdType()))));
//
//	    // idDetails
//	    lResult.add(new ReviewResultVb(rsb.getString("idDetails"),
//	            (pending == null ? "" : safeToStr.apply(pending.getIdDetails())),
//	            (approved == null ? "" : safeToStr.apply(approved.getIdDetails())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getIdDetails())
//	                    .equals(safeToStr.apply(approved.getIdDetails()))));

	    // primaryCid
//	    lResult.add(new ReviewResultVb(rsb.getString("primaryCid"),
//	            (pending == null ? "" : safeToStr.apply(pending.getPrimaryCid())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPrimaryCid())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPrimaryCid())
//	                    .equals(safeToStr.apply(approved.getPrimaryCid()))));
//
//	    // parentCid
//	    lResult.add(new ReviewResultVb(rsb.getString("parentCid"),
//	            (pending == null ? "" : safeToStr.apply(pending.getParentCid())),
//	            (approved == null ? "" : safeToStr.apply(approved.getParentCid())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getParentCid())
//	                    .equals(safeToStr.apply(approved.getParentCid()))));
//
//	    // ultimateParent
//	    lResult.add(new ReviewResultVb(rsb.getString("ultimateParent"),
//	            (pending == null ? "" : safeToStr.apply(pending.getUltimateParent())),
//	            (approved == null ? "" : safeToStr.apply(approved.getUltimateParent())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getUltimateParent())
//	                    .equals(safeToStr.apply(approved.getUltimateParent()))));
//
//	    // customerHierarchy1/2/3
//	    lResult.add(new ReviewResultVb(rsb.getString("customerHierarchy1"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerHierarchy1())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerHierarchy1())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerHierarchy1())
//	                    .equals(safeToStr.apply(approved.getCustomerHierarchy1()))));
//	    lResult.add(new ReviewResultVb(rsb.getString("customerHierarchy2"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerHierarchy2())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerHierarchy2())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerHierarchy2())
//	                    .equals(safeToStr.apply(approved.getCustomerHierarchy2()))));
//	    lResult.add(new ReviewResultVb(rsb.getString("customerHierarchy3"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerHierarchy3())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerHierarchy3())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerHierarchy3())
//	                    .equals(safeToStr.apply(approved.getCustomerHierarchy3()))));

	    // accountOfficer
	    lResult.add(new ReviewResultVb(rsb.getString("accountOfficer"),
	            (pending == null ? "" : safeToStr.apply(pending.getAccountOfficer())),
	            (approved == null ? "" : safeToStr.apply(approved.getAccountOfficer())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getAccountOfficer())
	                    .equals(safeToStr.apply(approved.getAccountOfficer()))));

	    // creditClassification
//	    lResult.add(new ReviewResultVb(rsb.getString("creditClassification"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(7), pending.getCreditClassification())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(7), approved.getCreditClassification())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCreditClassification())
//	                    .equals(safeToStr.apply(approved.getCreditClassification()))));

	    // externalRiskRating
//	    lResult.add(new ReviewResultVb(rsb.getString("externalRiskRating"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(8), pending.getExternalRiskRating())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(8), approved.getExternalRiskRating())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getExternalRiskRating())
//	                    .equals(safeToStr.apply(approved.getExternalRiskRating()))));
//
//	    // obligorRiskRating
//	    lResult.add(new ReviewResultVb(rsb.getString("obligorRiskRating"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(9), pending.getObligorRiskRating())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(9), approved.getObligorRiskRating())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getObligorRiskRating())
//	                    .equals(safeToStr.apply(approved.getObligorRiskRating()))));
//
//	    // relatedParty
//	    lResult.add(new ReviewResultVb(rsb.getString("relatedParty"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(10), pending.getRelatedParty())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(10), approved.getRelatedParty())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getRelatedParty())
//	                    .equals(safeToStr.apply(approved.getRelatedParty()))));
//
//	    // customerTiering
//	    lResult.add(new ReviewResultVb(rsb.getString("customerTiering"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(11), pending.getCustomerTiering())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(11), approved.getCustomerTiering())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerTiering())
//	                    .equals(safeToStr.apply(approved.getCustomerTiering()))));
//
//	    // salesAcquisitionChannel
//	    lResult.add(new ReviewResultVb(rsb.getString("salesAcquisitionChannel"),
//	            (pending == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(12), pending.getSalesAcquisitionChannel())),
//	            (approved == null ? ""
//	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(12), approved.getSalesAcquisitionChannel())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getSalesAcquisitionChannel())
//	                    .equals(safeToStr.apply(approved.getSalesAcquisitionChannel()))));
//
//	    // marketingCampaign
//	    lResult.add(new ReviewResultVb(rsb.getString("marketingCampaign"),
//	            (pending == null ? "" : safeToStr.apply(pending.getMarketingCampaign())),
//	            (approved == null ? "" : safeToStr.apply(approved.getMarketingCampaign())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getMarketingCampaign())
//	                    .equals(safeToStr.apply(approved.getMarketingCampaign()))));
//
//	    // crosssaleReferId
//	    lResult.add(new ReviewResultVb(rsb.getString("crosssaleReferId"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCrosssaleReferId())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCrosssaleReferId())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCrosssaleReferId())
//	                    .equals(safeToStr.apply(approved.getCrosssaleReferId()))));

	    // customerIndicator
	    lResult.add(new ReviewResultVb(rsb.getString("customerIndicator"),
	            (pending == null ? ""
	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(13), pending.getCustomerIndicator())),
	            (approved == null ? ""
	                    : getAtDescription((List<AlphaSubTabVb>) collTemp.get(13), approved.getCustomerIndicator())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerIndicator())
	                    .equals(safeToStr.apply(approved.getCustomerIndicator()))));

//	    // noOfAccounts
//	    lResult.add(new ReviewResultVb(rsb.getString("noOfAccounts"),
//	            (pending == null ? "" : safeToStr.apply(pending.getNumOfAccounts())),
//	            (approved == null ? "" : safeToStr.apply(approved.getNumOfAccounts())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getNumOfAccounts())
//	                    .equals(safeToStr.apply(approved.getNumOfAccounts()))));

	    // Customer attributes 1..3
//	    lResult.add(new ReviewResultVb(rsb.getString("customerAttribute1"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerAttribute1())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerAttribute1())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerAttribute1())
//	                    .equals(safeToStr.apply(approved.getCustomerAttribute1()))));
//	    lResult.add(new ReviewResultVb(rsb.getString("customerAttribute2"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerAttribute2())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerAttribute2())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerAttribute2())
//	                    .equals(safeToStr.apply(approved.getCustomerAttribute2()))));
//	    lResult.add(new ReviewResultVb(rsb.getString("customerAttribute3"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCustomerAttribute3())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCustomerAttribute3())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerAttribute3())
//	                    .equals(safeToStr.apply(approved.getCustomerAttribute3()))));

	    // openDate, sex, addresses, etc.
	    lResult.add(new ReviewResultVb(rsb.getString("openDate"),
	            (pending == null ? "" : safeToStr.apply(pending.getCustomerOpenDate())),
	            (approved == null ? "" : safeToStr.apply(approved.getCustomerOpenDate())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerOpenDate())
	                    .equals(safeToStr.apply(approved.getCustomerOpenDate()))));

	    lResult.add(new ReviewResultVb(rsb.getString("customerSex"),
	            (pending == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(6), pending.getCustomerSex())),
	            (approved == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(6), approved.getCustomerSex())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerSex())
	                    .equals(safeToStr.apply(approved.getCustomerSex()))));

//	    lResult.add(new ReviewResultVb(rsb.getString("commAddress1"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCommAddress1())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCommAddress1())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommAddress1())
//	                    .equals(safeToStr.apply(approved.getCommAddress1()))));
//
//	    lResult.add(new ReviewResultVb(rsb.getString("commAddress2"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCommAddress2())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCommAddress2())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommAddress2())
//	                    .equals(safeToStr.apply(approved.getCommAddress2()))));
//	    
//	    lResult.add(new ReviewResultVb("Comm Address 3",
//	            (pending == null ? "" : safeToStr.apply(pending.getCommAddress3())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCommAddress3())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommAddress3())
//	                    .equals(safeToStr.apply(approved.getCommAddress3()))));

//	    lResult.add(new ReviewResultVb(rsb.getString("commCity"),
//	            (pending == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(14), pending.getCommCity())),
//	            (approved == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(14), approved.getCommCity())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommCity())
//	                    .equals(safeToStr.apply(approved.getCommCity()))));

//	    lResult.add(new ReviewResultVb(rsb.getString("commState"),
//	            (pending == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(15), pending.getCommState())),
//	            (approved == null ? "" : getAtDescription((List<AlphaSubTabVb>) collTemp.get(15), approved.getCommState())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommState())
//	                    .equals(safeToStr.apply(approved.getCommState()))));
//
//	    lResult.add(new ReviewResultVb(rsb.getString("commPinCode"),
//	            (pending == null ? "" : safeToStr.apply(pending.getCommPinCode())),
//	            (approved == null ? "" : safeToStr.apply(approved.getCommPinCode())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCommPinCode())
//	                    .equals(safeToStr.apply(approved.getCommPinCode()))));
//
//	    lResult.add(new ReviewResultVb(rsb.getString("phoneNumber"),
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber()))));
//	    lResult.add(new ReviewResultVb("Phone Number2",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber2())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber2())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber2())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber2()))));
//	    lResult.add(new ReviewResultVb("Phone Number3",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber3())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber3())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber3())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber3()))));
//	    lResult.add(new ReviewResultVb("Phone Number4",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber4())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber4())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber4())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber4()))));
//	    lResult.add(new ReviewResultVb("Phone Number5",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber5())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber5())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber5())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber5()))));
//	    lResult.add(new ReviewResultVb("Phone Number6",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber6())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber6())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber6())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber6()))));
//	    lResult.add(new ReviewResultVb("Phone Number7",
//	            (pending == null ? "" : safeToStr.apply(pending.getPhoneNumber7())),
//	            (approved == null ? "" : safeToStr.apply(approved.getPhoneNumber7())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getPhoneNumber7())
//	                    .equals(safeToStr.apply(approved.getPhoneNumber7()))));

	    // Dual nationalities (example labels preserved)
//	    lResult.add(new ReviewResultVb("Nationality 2",
//	            (pending == null ? "" : safeToStr.apply(pending.getDualNationality1())),
//	            (approved == null ? "" : safeToStr.apply(approved.getDualNationality1())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getDualNationality1())
//	                    .equals(safeToStr.apply(approved.getDualNationality1()))));
//
//	    lResult.add(new ReviewResultVb("Nationality 3",
//	            (pending == null ? "" : safeToStr.apply(pending.getDualNationality2())),
//	            (approved == null ? "" : safeToStr.apply(approved.getDualNationality2())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getDualNationality2())
//	                    .equals(safeToStr.apply(approved.getDualNationality2()))));
//
//	    lResult.add(new ReviewResultVb("Nationality 4",
//	            (pending == null ? "" : safeToStr.apply(pending.getDualNationality3())),
//	            (approved == null ? "" : safeToStr.apply(approved.getDualNationality3())),
//	            !(pending == null || approved == null) && !safeToStr.apply(pending.getDualNationality3())
//	                    .equals(safeToStr.apply(approved.getDualNationality3()))));
	    
	    
	    lResult.add(new ReviewResultVb("SSN",
	            (pending == null ? "" : safeToStr.apply(pending.getSsn())),
	            (approved == null ? "" : safeToStr.apply(approved.getSsn())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getSsn())
	                    .equals(safeToStr.apply(approved.getSsn()))));
	    lResult.add(new ReviewResultVb("Customer Tin",
	            (pending == null ? "" : safeToStr.apply(pending.getCustomerTin())),
	            (approved == null ? "" : safeToStr.apply(approved.getCustomerTin())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getCustomerTin())
	                    .equals(safeToStr.apply(approved.getCustomerTin()))));
	    
	    lResult.add(new ReviewResultVb("Sub Segment",
	            (pending == null ? "" : safeToStr.apply(pending.getSubSegment())),
	            (approved == null ? "" : safeToStr.apply(approved.getSubSegment())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getSubSegment())
	                    .equals(safeToStr.apply(approved.getSubSegment()))));
	    
	    lResult.add(new ReviewResultVb("Compliance Status",
	            (pending == null ? "" : safeToStr.apply(pending.getComplianceStatus())),
	            (approved == null ? "" : safeToStr.apply(approved.getComplianceStatus())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getComplianceStatus())
	                    .equals(safeToStr.apply(approved.getComplianceStatus()))));
	    
	    lResult.add(new ReviewResultVb("Joint Account",
	            (pending == null ? "" : safeToStr.apply(pending.getJointAccount())),
	            (approved == null ? "" : safeToStr.apply(approved.getJointAccount())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getJointAccount())
	                    .equals(safeToStr.apply(approved.getJointAccount()))));

	    // status (NumSubTab)
	    lResult.add(new ReviewResultVb(rsb.getString("status"),
	            (pending == null ? "" : getNtDescription((List<NumSubTabVb>) collTemp.get(0), pending.getCustomerStatus())),
	            (approved == null ? "" : getNtDescription((List<NumSubTabVb>) collTemp.get(0), approved.getCustomerStatus())),
	            !(pending == null || approved == null) && pending.getCustomerStatus() != approved.getCustomerStatus()));

	    // recordIndicator
	    lResult.add(new ReviewResultVb(rsb.getString("recordIndicator"),
	            (pending == null ? "" : getNtDescription((List<NumSubTabVb>) collTemp.get(1), pending.getRecordIndicator())),
	            (approved == null ? "" : getNtDescription((List<NumSubTabVb>) collTemp.get(1), approved.getRecordIndicator())),
	            !(pending == null || approved == null) && pending.getRecordIndicator() != approved.getRecordIndicator()));

	    // maker / verifier (name fallback)
	    lResult.add(new ReviewResultVb(rsb.getString("maker"),
	            (pending == null ? "" : (pending.getMaker() == 0 ? "" : pending.getMakerName())),
	            (approved == null ? "" : (approved.getMaker() == 0 ? "" : approved.getMakerName())),
	            !(pending == null || approved == null) && pending.getMaker() != approved.getMaker()));

	    lResult.add(new ReviewResultVb(rsb.getString("verifier"),
	            (pending == null ? "" : (pending.getVerifier() == 0 ? "" : pending.getVerifierName())),
	            (approved == null ? "" : (approved.getVerifier() == 0 ? "" : approved.getVerifierName())),
	            !(pending == null || approved == null) && pending.getVerifier() != approved.getVerifier()));

	    // date fields
	    lResult.add(new ReviewResultVb("Date Last Modified",
	            (pending == null ? "" : safeToStr.apply(pending.getDateLastModified())),
	            (approved == null ? "" : safeToStr.apply(approved.getDateLastModified())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getDateLastModified())
	                    .equals(safeToStr.apply(approved.getDateLastModified()))));

	    lResult.add(new ReviewResultVb("Date Creation",
	            (pending == null ? "" : safeToStr.apply(pending.getDateCreation())),
	            (approved == null ? "" : safeToStr.apply(approved.getDateCreation())),
	            !(pending == null || approved == null) && !safeToStr.apply(pending.getDateCreation())
	                    .equals(safeToStr.apply(approved.getDateCreation()))));
	    


	    return lResult;
	}

	/* -------------------------
	   Manual override helpers
	   ------------------------- */

	/**
	 * Applies manualList overrides to the pending CustomersVb instance. 
	 * ManualEntryVb is expected to expose getColumnName(), getVariableName(), getValue() (value optional).
	 */
	private void applyManualOverridesToPending(CustomersVb pending) {
	    if (pending == null) return;
	    List<CustomerManualColVb> manualList = pending.getManualList(); // adapt getter/type if needed
	    if (manualList == null || manualList.isEmpty()) return;

	    for (CustomerManualColVb entry : manualList) {
	        try {
	            String targetProp = entry.getColumnName();
	            if (targetProp == null || targetProp.trim().isEmpty()) continue;

	            String explicitValue = entry.getColumnValue(); // direct override if present
	            String variableName = entry.getVariableName(); // name of other property on pending to copy from

	            Object sourceValue = null;
	            if (explicitValue != null) {
	                sourceValue = explicitValue;
	            } else if (variableName != null && !variableName.trim().isEmpty()) {
	                sourceValue = readProperty(pending, variableName);
	            } else {
	                // nothing to do
	                continue;
	            }

	            // write (with type conversion)
	            writeProperty(pending, variableName, sourceValue);

	        } catch (Exception ex) {
	            // swallow per-entry exceptions to avoid breaking whole transform
	            // Replace with logger.warn(...) if logger available
	            // e.g. logger.warn("Failed manual override for entry: " + entry, ex);
	        }
	    }
	}

	private Object readProperty(Object bean, String propName) throws Exception {
	    for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
	        if (pd.getName().equals(propName)) {
	            Method read = pd.getReadMethod();
	            if (read != null) {
	                return read.invoke(bean);
	            }
	            break;
	        }
	    }
	    return null;
	}

	private void writeProperty(Object bean, String propName, Object value) throws Exception {
	    PropertyDescriptor[] pds = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
	    for (PropertyDescriptor pd : pds) {
	        if (pd.getName().equals(propName)) {
	            Method write = pd.getWriteMethod();
	            if (write == null) return;
	            Class<?> targetType = pd.getPropertyType();
	            Object converted = convertValueToTarget(value, targetType);
	            write.invoke(bean, converted);
	            return;
	        }
	    }
	}

	private Object convertValueToTarget(Object value, Class<?> targetType) throws java.text.ParseException {
	    if (value == null) return null;

	    if (targetType.isInstance(value)) return value;

	    String str = String.valueOf(value);

	    if (targetType == String.class) return str;
	    if (targetType == Integer.class || targetType == int.class) return Integer.valueOf(str);
	    if (targetType == Long.class || targetType == long.class) return Long.valueOf(str);
	    if (targetType == Boolean.class || targetType == boolean.class) return Boolean.valueOf(str);
	    if (targetType == Double.class || targetType == double.class) return Double.valueOf(str);
	    if (targetType == Float.class || targetType == float.class) return Float.valueOf(str);

	    if (java.util.Date.class.isAssignableFrom(targetType)) {
	        java.text.SimpleDateFormat[] formats = new java.text.SimpleDateFormat[] {
	                new java.text.SimpleDateFormat("yyyy-MM-dd"),
	                new java.text.SimpleDateFormat("dd/MM/yyyy"),
	                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss") };
	        for (java.text.SimpleDateFormat fmt : formats) {
	            try {
	                return fmt.parse(str);
	            } catch (java.text.ParseException ignored) {
	            }
	        }
	        throw new java.text.ParseException("Unparseable date: " + str, 0);
	    }

	    // fallback to string
	    return str;
	}

	@Override
	protected void setVerifReqDeleteType(CustomersVb vObject) {
		ArrayList<CommonVb> lCommVbList = (ArrayList<CommonVb>) getCommonDao()
				.findVerificationRequiredAndStaticDelete("CUSTOMERS");
		vObject.setStaticDelete(lCommVbList.get(0).isStaticDelete());
		vObject.setVerificationRequired(lCommVbList.get(0).isVerificationRequired());
//		vObject.setStaticDelete(false);
//		vObject.setVerificationRequired(false);
	}

	@Override
	protected void setAtNtValues(CustomersVb vObject) {
		vObject.setRecordIndicatorNt(7);
		vObject.setCustomerStatusNt(1);
		vObject.setVisionSbuAt(3);
		vObject.setCbOrgCodeAt(41);
		vObject.setCbEconomicActCodeAt(42);
		vObject.setCustomerIdTypeAt(43);
		vObject.setCustomerSexAT(40);
		vObject.setCreditClassificationAt(44);
		vObject.setExternalRiskRatingAt(45);
		vObject.setObligorRiskRatingAt(46);
		vObject.setRelatedPartyAt(49);
		vObject.setCustomerTieringAt(47);
		vObject.setSalesAcquisitionChannelAt(48);
		vObject.setCustomerIndicatorAt(51);
		vObject.setCommCityAt("38");
		vObject.setCommStateAt("39");
	}

	@Override
	protected AbstractDao<CustomersVb> getScreenDao() {
		return customersDao;
	}

	@Override
	protected void doFormateData(CustomersVb vObject) {
		if (ValidationUtil.isValid(vObject.getCommCity()) && "-1".equalsIgnoreCase(vObject.getCommCity()))
			vObject.setCommCity("NA");
		if (ValidationUtil.isValid(vObject.getCommState()) && "-1".equalsIgnoreCase(vObject.getCommState()))
			vObject.setCommState("NA");
		if (ValidationUtil.isValid(vObject.getCbMajorPartyIndicator())
				&& "".equalsIgnoreCase(vObject.getCbMajorPartyIndicator()))
			vObject.setCbMajorPartyIndicator("N");
	}

	protected ExceptionCode doValidate(CustomersVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		if ((ValidationUtil.isValid(vObject.getAccountOfficer())
				&& customersDao.validateAccountOfficer(vObject) == 0)) {
			String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook() + "] Account Officer["
					+ vObject.getAccountOfficer() + "] Combination does not exists in ACCOUNT_OFFICERS table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getVisionOuc()) && (customersDao.validateMAXIMOUC(vObject) == 0)) {
			String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook() + "] Vision OUC["
					+ vObject.getVisionOuc() + "] combination does not exists in OUC_CODES table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;

		}
//		if (ValidationUtil.isValid(vObject.getNaicsCode()) && (customersDao.validateNAICS(vObject) == 0)) {
//			String msg = "NAICS CODE[" + vObject.getNaicsCode() + "] does not exists in NAICS_CODES table.";
//			exceptionCode.setErrorMsg(msg);
//			exceptionCode.setOtherInfo(vObject);
//			return exceptionCode;
//		}
		if (ValidationUtil.isValid(vObject.getCbDomicile())
				&& (customersDao.validateCBDomicile(vObject.getCbDomicile()) == 0)) {
			String msg = "CB Domicile[" + vObject.getCbDomicile() + "] does not exists in COUNTRIES table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getCbNationality())
				&& (customersDao.validateCBDomicile(vObject.getCbNationality()) == 0)) {
			String msg = "CB Nationality[" + vObject.getCbNationality() + "] does not exists in COUNTRIES table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getCbResidence())
				&& (customersDao.validateCBDomicile(vObject.getCbResidence()) == 0)) {
			String msg = "CB Residency[" + vObject.getCbResidence() + "] does not exists in COUNTRIES table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		/*
		 * if(ValidationUtil.isValid(vObject.getGlobalCustomerId())){
		 * if((customersDao.validateGlobelCustomerId(vObject) == 0)){ String msg =
		 * "Country["+vObject.getCountry()+"] LE Book["+vObject.getLeBook()
		 * +"] Global Customer Id["+vObject.getGlobalCustomerId()
		 * +"] combination does not exists in CUSTOMERS table.";
		 * exceptionCode.setErrorMsg(msg); return exceptionCode; } }
		 */
		if (ValidationUtil.isValid(vObject.getPrimaryCid()) && (!vObject.getCustomerId().equals(vObject.getPrimaryCid())
				&& (customersDao.validatePrimaryCID(vObject) == 0))) {
			String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook() + "] Primary CID["
					+ vObject.getPrimaryCid() + "] combination does not exists in Customers table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getParentCid()) && (!vObject.getCustomerId().equals(vObject.getParentCid())
				&& (customersDao.validateParentCID(vObject) == 0))) {
			String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook() + "] Parent CID["
					+ vObject.getParentCid() + "] combination does not exists in Customers table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getParentCid())
				&& (!vObject.getCustomerId().equals(vObject.getUltimateParent())
						&& (customersDao.validateUltimateParent(vObject) == 0))) {
			String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook() + "] Ultimate Parent ["
					+ vObject.getUltimateParent() + "] combination does not exists in Customers table.";
			exceptionCode.setErrorMsg(msg);
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		if (ValidationUtil.isValid(vObject.getCustomerHierarchy1()) && !vObject.getCustomerHierarchy1().isEmpty()) {
			if ((customersDao.validateCustomerHierarchys(vObject.getCustomerHierarchy1()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Hierarchys[" + vObject.getCustomerHierarchy1()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		if (ValidationUtil.isValid(vObject.getCustomerHierarchy2()) && !vObject.getCustomerHierarchy2().isEmpty()) {
			if ((customersDao.validateCustomerHierarchys(vObject.getCustomerHierarchy2()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Hierarchys[" + vObject.getCustomerHierarchy2()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		if (ValidationUtil.isValid(vObject.getCustomerHierarchy3()) && !vObject.getCustomerHierarchy3().isEmpty()) {
			if ((customersDao.validateCustomerHierarchys(vObject.getCustomerHierarchy3()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Hierarchys[" + vObject.getCustomerHierarchy3()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		if (ValidationUtil.isValid(vObject.getCustomerAttribute1()) && !vObject.getCustomerAttribute1().isEmpty()) {
			if ((customersDao.validateCustomerAttributes(vObject.getCustomerAttribute1()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Attributes[" + vObject.getCustomerAttribute1()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		if (ValidationUtil.isValid(vObject.getCustomerAttribute2()) && !vObject.getCustomerAttribute2().isEmpty()) {
			if ((customersDao.validateCustomerAttributes(vObject.getCustomerAttribute2()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Attributes[" + vObject.getCustomerAttribute2()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		if (ValidationUtil.isValid(vObject.getCustomerAttribute3()) && !vObject.getCustomerAttribute3().isEmpty()) {
			if ((customersDao.validateCustomerAttributes(vObject.getCustomerAttribute3()) == 0)) {
				String msg = "Country[" + vObject.getCountry() + "] LE Book[" + vObject.getLeBook()
						+ "] Customer Attributes[" + vObject.getCustomerAttribute3()
						+ "] combination does not exists in CUSTOMER_ATTRIBUTE table.";
				exceptionCode.setErrorMsg(msg);
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}
		}
		return exceptionCode;
	}

	public ExceptionCode reviewRecordNew(CustomersVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReviewResultVb> list = null;
		try {
			List<CustomersVb> approvedCollection = getScreenDao().getQueryResults(vObject, 0);
			List<CustomersVb> pendingCollection = getScreenDao().getQueryResults(vObject, 1);
			list = transformToReviewResults(approvedCollection, pendingCollection);
			exceptionCode.setResponse(list);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getQueryResults(CustomersVb vObject) {
		int intStatus = 1;
		setVerifReqDeleteType(vObject);
		List<CustomersVb> collTemp = getScreenDao().getQueryResults(vObject, intStatus);
		if (collTemp.size() == 0) {
			intStatus = 0;
			collTemp = getScreenDao().getQueryResults(vObject, intStatus);
		}
		if (collTemp.size() == 0) {
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			doSetDesctiptionsAfterQuery(((ArrayList<CustomersVb>) collTemp).get(0));
			ExceptionCode exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceDesc(), 1, "Query", "");
			getScreenDao().fetchMakerVerifierNames(((ArrayList<CustomersVb>) collTemp).get(0));
			((ArrayList<CustomersVb>) collTemp).get(0).setVerificationRequired(vObject.isVerificationRequired());
			((ArrayList<CustomersVb>) collTemp).get(0).setStaticDelete(vObject.isStaticDelete());
			exceptionCode.setResponse(((ArrayList<CustomersVb>) collTemp).get(0));
			return exceptionCode;
		}
	}

}
