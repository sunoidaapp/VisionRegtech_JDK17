package com.vision.wb;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
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
			String country = "";
			String leBook = "";
			VisionUsersVb visionUsers = SessionContextHolder.getContext();
			if ("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
				if (ValidationUtil.isValid(visionUsers.getCountry())) {
					country = visionUsers.getCountry();
				}
				if (ValidationUtil.isValid(visionUsers.getLeBook())) {
					leBook = visionUsers.getLeBook();
				}
			} else {
				country = getCommonDao().findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = getCommonDao().findVisionVariableValue("DEFAULT_LE_BOOK");
			}
			String countryLeBook = country + "-" + leBook;
			arrListLocal.add(countryLeBook);
			collTemp = customerManualDao.findAllGroupedAsCustomers();
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
		if (pendingCollection != null)
			getScreenDao().fetchMakerVerifierNames(pendingCollection.get(0));
		if (approvedCollection != null)
			getScreenDao().fetchMakerVerifierNames(approvedCollection.get(0));
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();

//		ReviewResultVb lCountry = new ReviewResultVb(rsb.getString("country"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getCountry(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getCountry());
//		lResult.add(lCountry);
//		ReviewResultVb lLeBook = new ReviewResultVb(rsb.getString("leBook"),(pendingCollection == null || pendingCollection.isEmpty())?"":pendingCollection.get(0).getLeBook(),
//				(approvedCollection == null || approvedCollection.isEmpty())?"":approvedCollection.get(0).getLeBook());
//		lResult.add(lLeBook);

		ReviewResultVb lCustomerId = new ReviewResultVb(rsb.getString("customerId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerId(),
				(!pendingCollection.get(0).getCustomerId().equals(approvedCollection.get(0).getCustomerId())));
		lResult.add(lCustomerId);
		ReviewResultVb lCustomerName = new ReviewResultVb(rsb.getString("customerName"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerName(),
				(!pendingCollection.get(0).getCustomerName().equals(approvedCollection.get(0).getCustomerName())));
		lResult.add(lCustomerName);
		ReviewResultVb lCustomerAcronym = new ReviewResultVb(rsb.getString("customerAccronym"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerAcronym(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerAcronym(),
				(!pendingCollection.get(0).getCustomerAcronym()
						.equals(approvedCollection.get(0).getCustomerAcronym())));
		lResult.add(lCustomerAcronym);
		ReviewResultVb lVisionOuc = new ReviewResultVb(rsb.getString("visionOUC"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVisionOuc(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVisionOuc(),
				(!pendingCollection.get(0).getVisionOuc().equals(approvedCollection.get(0).getVisionOuc())));
		lResult.add(lVisionOuc);
		ReviewResultVb lVisionSbu = new ReviewResultVb(rsb.getString("visionSBU"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
								pendingCollection.get(0).getVisionSbu()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(2),
								approvedCollection.get(0).getVisionSbu()),
				(!pendingCollection.get(0).getVisionSbu().equals(approvedCollection.get(0).getVisionSbu())));
		lResult.add(lVisionSbu);
		ReviewResultVb lGlobalCustomerId = new ReviewResultVb(rsb.getString("globalCustomerId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getGlobalCustomerId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getGlobalCustomerId(),
				(!pendingCollection.get(0).getGlobalCustomerId()
						.equals(approvedCollection.get(0).getGlobalCustomerId())));
		lResult.add(lGlobalCustomerId);
		ReviewResultVb lNaicsCode = new ReviewResultVb(rsb.getString("naicsCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: String.valueOf(pendingCollection.get(0).getNaicsCode()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: String.valueOf(approvedCollection.get(0).getNaicsCode()),
				(!pendingCollection.get(0).getNaicsCode().equals(approvedCollection.get(0).getNaicsCode())));
		lResult.add(lNaicsCode);
		ReviewResultVb lCbOrgCode = new ReviewResultVb(rsb.getString("cbOrgCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								pendingCollection.get(0).getCbOrgCode()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(3),
								approvedCollection.get(0).getCbOrgCode()),
				(!pendingCollection.get(0).getCbOrgCode().equals(approvedCollection.get(0).getCbOrgCode())));
		lResult.add(lCbOrgCode);
		ReviewResultVb lCbOEcnomicActCode = new ReviewResultVb(rsb.getString("cbEconomicActCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
								pendingCollection.get(0).getCbEconomicActCode()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(4),
								approvedCollection.get(0).getCbEconomicActCode()),
				(!pendingCollection.get(0).getCbEconomicActCode()
						.equals(approvedCollection.get(0).getCbEconomicActCode())));
		lResult.add(lCbOEcnomicActCode);
		ReviewResultVb lCbDomicel = new ReviewResultVb(rsb.getString("cbDomicile"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCbDomicile(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbDomicile(),
				(!pendingCollection.get(0).getCbDomicile().equals(approvedCollection.get(0).getCbDomicile())));
		lResult.add(lCbDomicel);
		ReviewResultVb lCbNationality = new ReviewResultVb(rsb.getString("cbNationality"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCbNationality(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbNationality(),
				(!pendingCollection.get(0).getCbNationality().equals(approvedCollection.get(0).getCbNationality())));
		lResult.add(lCbNationality);
		ReviewResultVb lCbResidency = new ReviewResultVb(rsb.getString("cbResidence"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCbResidence(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCbResidence(),
				(!pendingCollection.get(0).getCbResidence().equals(approvedCollection.get(0).getCbResidence())));
		lResult.add(lCbResidency);
		ReviewResultVb lCbMajorPartyInd = new ReviewResultVb(rsb.getString("majorPartyIndicator"),
				"Y".equalsIgnoreCase(pendingCollection.get(0).getCbMajorPartyIndicator()) ? "Yes" : "No",
				("Y".equalsIgnoreCase(approvedCollection.get(0).getCbMajorPartyIndicator()) ? "yes" : "no"));
		lResult.add(lCbMajorPartyInd);
		ReviewResultVb lCustomerIdType = new ReviewResultVb(rsb.getString("customerIdType"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
								pendingCollection.get(0).getCustomerIdType()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(5),
								approvedCollection.get(0).getCustomerIdType()),
				(!pendingCollection.get(0).getCustomerIdType().equals(approvedCollection.get(0).getCustomerIdType())));
		lResult.add(lCustomerIdType);
		ReviewResultVb lIdDetails = new ReviewResultVb(rsb.getString("idDetails"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getIdDetails(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getIdDetails(),
				(!pendingCollection.get(0).getIdDetails().equals(approvedCollection.get(0).getIdDetails())));
		lResult.add(lIdDetails);
		ReviewResultVb lPrimaryCid = new ReviewResultVb(rsb.getString("primaryCid"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getPrimaryCid(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getPrimaryCid(),
				(!pendingCollection.get(0).getPrimaryCid().equals(approvedCollection.get(0).getPrimaryCid())));
		lResult.add(lPrimaryCid);
		ReviewResultVb lParentCid = new ReviewResultVb(rsb.getString("parentCid"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getParentCid(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getParentCid(),
				(!pendingCollection.get(0).getParentCid().equals(approvedCollection.get(0).getParentCid())));
		lResult.add(lParentCid);
		ReviewResultVb lUltimateParent = new ReviewResultVb(rsb.getString("ultimateParent"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getUltimateParent(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getUltimateParent(),
				(!pendingCollection.get(0).getUltimateParent().equals(approvedCollection.get(0).getUltimateParent())));
		lResult.add(lUltimateParent);
		ReviewResultVb lCustomerHierarchy1 = new ReviewResultVb(rsb.getString("customerHierarchy1"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerHierarchy1(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerHierarchy1(),
				(!pendingCollection.get(0).getCustomerHierarchy1()
						.equals(approvedCollection.get(0).getCustomerHierarchy1())));
		lResult.add(lCustomerHierarchy1);
		ReviewResultVb lCustomerHierarchy2 = new ReviewResultVb(rsb.getString("customerHierarchy2"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerHierarchy2(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerHierarchy2(),
				(!pendingCollection.get(0).getCustomerHierarchy2()
						.equals(approvedCollection.get(0).getCustomerHierarchy2())));
		lResult.add(lCustomerHierarchy2);
		ReviewResultVb lCustomerHierarchy3 = new ReviewResultVb(rsb.getString("customerHierarchy3"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerHierarchy3(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerHierarchy3(),
				(!pendingCollection.get(0).getCustomerHierarchy3()
						.equals(approvedCollection.get(0).getCustomerHierarchy3())));
		lResult.add(lCustomerHierarchy3);
		ReviewResultVb lAccountOfficer = new ReviewResultVb(rsb.getString("accountOfficer"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getAccountOfficer(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getAccountOfficer(),
				(!pendingCollection.get(0).getAccountOfficer().equals(approvedCollection.get(0).getAccountOfficer())));
		lResult.add(lAccountOfficer);
		ReviewResultVb lCreditClassification = new ReviewResultVb(rsb.getString("creditClassification"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(7),
								pendingCollection.get(0).getCreditClassification()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(7),
								approvedCollection.get(0).getCreditClassification()),
				(!pendingCollection.get(0).getCreditClassification()
						.equals(approvedCollection.get(0).getCreditClassification())));
		lResult.add(lCreditClassification);
		ReviewResultVb lExternalRiskRating = new ReviewResultVb(rsb.getString("externalRiskRating"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(8),
								pendingCollection.get(0).getExternalRiskRating()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(8),
								approvedCollection.get(0).getExternalRiskRating()),
				(!pendingCollection.get(0).getExternalRiskRating()
						.equals(approvedCollection.get(0).getExternalRiskRating())));
		lResult.add(lExternalRiskRating);
		ReviewResultVb lObligorRiskRating = new ReviewResultVb(rsb.getString("obligorRiskRating"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(9),
								pendingCollection.get(0).getObligorRiskRating()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(9),
								approvedCollection.get(0).getObligorRiskRating()),
				(!pendingCollection.get(0).getObligorRiskRating()
						.equals(approvedCollection.get(0).getObligorRiskRating())));
		lResult.add(lObligorRiskRating);
		ReviewResultVb lRelatedParty = new ReviewResultVb(rsb.getString("relatedParty"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(10),
								pendingCollection.get(0).getRelatedParty()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(10),
								approvedCollection.get(0).getRelatedParty()),
				(!pendingCollection.get(0).getRelatedParty().equals(approvedCollection.get(0).getRelatedParty())));
		lResult.add(lRelatedParty);
		ReviewResultVb lCustomerTiring = new ReviewResultVb(rsb.getString("customerTiering"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(11),
								pendingCollection.get(0).getCustomerTiering()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(11),
								approvedCollection.get(0).getCustomerTiering()),
				(!pendingCollection.get(0).getCustomerTiering()
						.equals(approvedCollection.get(0).getCustomerTiering())));
		lResult.add(lCustomerTiring);
		ReviewResultVb lSalesQcquisition = new ReviewResultVb(rsb.getString("salesAcquisitionChannel"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(12),
								pendingCollection.get(0).getSalesAcquisitionChannel()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(12),
								approvedCollection.get(0).getSalesAcquisitionChannel()),
				(!pendingCollection.get(0).getSalesAcquisitionChannel()
						.equals(approvedCollection.get(0).getSalesAcquisitionChannel())));
		lResult.add(lSalesQcquisition);
		ReviewResultVb lMarketingCampaign = new ReviewResultVb(rsb.getString("marketingCampaign"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMarketingCampaign(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMarketingCampaign(),
				(!pendingCollection.get(0).getMarketingCampaign()
						.equals(approvedCollection.get(0).getMarketingCampaign())));
		lResult.add(lMarketingCampaign);
		ReviewResultVb lCrossReferId = new ReviewResultVb(rsb.getString("crosssaleReferId"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCrosssaleReferId(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCrosssaleReferId(),
				(!pendingCollection.get(0).getCrosssaleReferId()
						.equals(approvedCollection.get(0).getCrosssaleReferId())));
		lResult.add(lCrossReferId);
		ReviewResultVb lCustomerInd = new ReviewResultVb(rsb.getString("customerIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(13),
								pendingCollection.get(0).getCustomerIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(13),
								approvedCollection.get(0).getCustomerIndicator()),
				(!pendingCollection.get(0).getCustomerIndicator()
						.equals(approvedCollection.get(0).getCustomerIndicator())));
		lResult.add(lCustomerInd);
		ReviewResultVb lNumOfAccounts = new ReviewResultVb(rsb.getString("noOfAccounts"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: String.valueOf(pendingCollection.get(0).getNumOfAccounts()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: String.valueOf(approvedCollection.get(0).getNumOfAccounts()),
				(!pendingCollection.get(0).getNumOfAccounts().equals(approvedCollection.get(0).getNumOfAccounts())));
		lResult.add(lNumOfAccounts);
		ReviewResultVb lCustomerAttribute1 = new ReviewResultVb(rsb.getString("customerAttribute1"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerAttribute1(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerAttribute1(),
				(!pendingCollection.get(0).getCustomerAttribute1()
						.equals(approvedCollection.get(0).getCustomerAttribute1())));
		lResult.add(lCustomerAttribute1);
		ReviewResultVb lCustomerAttribute2 = new ReviewResultVb(rsb.getString("customerAttribute2"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerAttribute2(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerAttribute2(),
				(!pendingCollection.get(0).getCustomerAttribute2()
						.equals(approvedCollection.get(0).getCustomerAttribute2())));
		lResult.add(lCustomerAttribute2);
		ReviewResultVb lCustomerAttribute3 = new ReviewResultVb(rsb.getString("customerAttribute3"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerAttribute3(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerAttribute3(),
				(!pendingCollection.get(0).getCustomerAttribute3()
						.equals(approvedCollection.get(0).getCustomerAttribute3())));
		lResult.add(lCustomerAttribute3);
		ReviewResultVb lCustomerOpenDate = new ReviewResultVb(rsb.getString("openDate"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCustomerOpenDate(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCustomerOpenDate(),
				(!pendingCollection.get(0).getCustomerOpenDate()
						.equals(approvedCollection.get(0).getCustomerOpenDate())));
		lResult.add(lCustomerOpenDate);
		ReviewResultVb lCustomerSex = new ReviewResultVb(rsb.getString("customerSex"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(6),
								pendingCollection.get(0).getCustomerSex()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(6),
								approvedCollection.get(0).getCustomerSex()),
				(!pendingCollection.get(0).getCustomerSex().equals(approvedCollection.get(0).getCustomerSex())));
		lResult.add(lCustomerSex);
		ReviewResultVb lCommAddress1 = new ReviewResultVb(rsb.getString("commAddress1"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCommAddress1(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCommAddress1(),
				(!pendingCollection.get(0).getCommAddress1().equals(approvedCollection.get(0).getCommAddress1())));
		lResult.add(lCommAddress1);
		ReviewResultVb lCommAddress2 = new ReviewResultVb(rsb.getString("commAddress2"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCommAddress2(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCommAddress2(),
				(!pendingCollection.get(0).getCommAddress2().equals(approvedCollection.get(0).getCommAddress2())));
		lResult.add(lCommAddress2);
		ReviewResultVb lCity = new ReviewResultVb(rsb.getString("commCity"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(14),
								pendingCollection.get(0).getCommCity()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(14),
								approvedCollection.get(0).getCommCity()),
				(!pendingCollection.get(0).getCommCity().equals(approvedCollection.get(0).getCommCity())));
		lResult.add(lCity);
		ReviewResultVb lState = new ReviewResultVb(rsb.getString("commState"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(15),
								pendingCollection.get(0).getCommState()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getAtDescription((List<AlphaSubTabVb>) collTemp.get(15),
								approvedCollection.get(0).getCommState()),
				(!pendingCollection.get(0).getCommState().equals(approvedCollection.get(0).getCommState())));
		lResult.add(lState);
		ReviewResultVb lPinCode = new ReviewResultVb(rsb.getString("commPinCode"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getCommPinCode(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getCommPinCode(),
				(!pendingCollection.get(0).getCommPinCode().equals(approvedCollection.get(0).getCommPinCode())));
		lResult.add(lPinCode);
		ReviewResultVb lPhoneNumber = new ReviewResultVb(rsb.getString("phoneNumber"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getPhoneNumber(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getPhoneNumber(),
				(!pendingCollection.get(0).getPhoneNumber().equals(approvedCollection.get(0).getPhoneNumber())));
		lResult.add(lPhoneNumber);

		ReviewResultVb dualNationality1 = new ReviewResultVb("Nationality 2",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDualNationality1(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDualNationality1(),
				(!pendingCollection.get(0).getDualNationality1()
						.equals(approvedCollection.get(0).getDualNationality1())));
		lResult.add(dualNationality1);

		ReviewResultVb dualNationality2 = new ReviewResultVb("Nationality 3",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDualNationality2(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDualNationality2(),
				(!pendingCollection.get(0).getDualNationality2()
						.equals(approvedCollection.get(0).getDualNationality2())));
		lResult.add(dualNationality2);

		ReviewResultVb dualNationality3 = new ReviewResultVb("Nationality 4",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDualNationality3(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDualNationality3(),
				(!pendingCollection.get(0).getDualNationality3()
						.equals(approvedCollection.get(0).getDualNationality3())));
		lResult.add(dualNationality3);

		ReviewResultVb lCustomerStatus = new ReviewResultVb(rsb.getString("status"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								pendingCollection.get(0).getCustomerStatus()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(0),
								approvedCollection.get(0).getCustomerStatus()),
				(pendingCollection.get(0).getCustomerStatus() != approvedCollection.get(0).getCustomerStatus()));
		lResult.add(lCustomerStatus);
		ReviewResultVb lRecordIndicator = new ReviewResultVb(rsb.getString("recordIndicator"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								pendingCollection.get(0).getRecordIndicator()),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: getNtDescription((List<NumSubTabVb>) collTemp.get(1),
								approvedCollection.get(0).getRecordIndicator()),
				(pendingCollection.get(0).getRecordIndicator() != approvedCollection.get(0).getRecordIndicator()));
		lResult.add(lRecordIndicator);
		ReviewResultVb lMaker = new ReviewResultVb(rsb.getString("maker"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getMaker() == 0 ? "" : pendingCollection.get(0).getMakerName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getMaker() == 0 ? "" : approvedCollection.get(0).getMakerName(),
				(pendingCollection.get(0).getMaker() != approvedCollection.get(0).getMaker()));
		lResult.add(lMaker);
		ReviewResultVb lVerifier = new ReviewResultVb(rsb.getString("verifier"),
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getVerifier() == 0 ? "" : pendingCollection.get(0).getVerifierName(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getVerifier() == 0 ? ""
								: approvedCollection.get(0).getVerifierName(),
				(pendingCollection.get(0).getVerifier() != approvedCollection.get(0).getVerifier()));
		lResult.add(lVerifier);
		ReviewResultVb lDateLastModified = new ReviewResultVb("Date Last Modified",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateLastModified(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateLastModified(),
				(!pendingCollection.get(0).getDateLastModified()
						.equals(approvedCollection.get(0).getDateLastModified())));
		lResult.add(lDateLastModified);
		ReviewResultVb lDateCreation = new ReviewResultVb("Date Creation",
				(pendingCollection == null || pendingCollection.isEmpty()) ? ""
						: pendingCollection.get(0).getDateCreation(),
				(approvedCollection == null || approvedCollection.isEmpty()) ? ""
						: approvedCollection.get(0).getDateCreation(),
				(!pendingCollection.get(0).getDateCreation().equals(approvedCollection.get(0).getDateCreation())));
		lResult.add(lDateCreation);
		return lResult;
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
