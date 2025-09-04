package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class GLCodesVb extends CommonVb{

		private static final long serialVersionUID = 8380991968901326107L;
		private String visionGl = "";
		private String glDescription = "";
		private int glTypeNt = 51;
		private int glType =  -1;
		private int interestBasisNT =  0;
		private int interestBasis = -1;
		private String defaultPoolCode = "";
		private int glSignReversal =  0;
		private String drCrMapInd = "";
		private int glStatusNt =  0;
		private int glStatus =  -1;
		private String glStatusDate = "";
		private int glAttribute1At =  0;
		private String glAttribute1 = "";
		private int glAttribute2At =  0;
		private String glAttribute2 = "";
		private int glAttribute3At =  0;
		private String glAttribute3 = "";
		private String reserveCode = "";
		private String defaultPoolCodeDesc = "";
		private String reserveCodeDesc = "";
		private int dropAccountDetails = -1;
		private int dropAccountDetailsNt = 0;
		private String glTypeDesc = "";
		private List<GLCodesVb> glCodesList;
		private String glStatusDesc =  "";
//		private List<GLCodesVb> childs = new ArrayList<GLCodesVb>(0);
		private List<GLMappingsVb> glMappingsList = new ArrayList<GLMappingsVb>(0);
		private List<AccountsMappingVb> accountsMappingsList = new ArrayList<AccountsMappingVb>(0);
		
		public String getVisionGl() {
			return visionGl;
		}
		public void setVisionGl(String visionGl) {
			this.visionGl = visionGl;
		}
		public String getGlDescription() {
			return glDescription;
		}
		public void setGlDescription(String glDescription) {
			this.glDescription = glDescription;
		}
		public int getGlTypeNt() {
			return glTypeNt;
		}
		public void setGlTypeNt(int glTypeNt) {
			this.glTypeNt = glTypeNt;
		}
		public int getGlType() {
			return glType;
		}
		public void setGlType(int glType) {
			this.glType = glType;
		}
		public int getInterestBasisNT() {
			return interestBasisNT;
		}
		public void setInterestBasisNT(int interestBasisNT) {
			this.interestBasisNT = interestBasisNT;
		}
		public int getInterestBasis() {
			return interestBasis;
		}
		public void setInterestBasis(int interestBasis) {
			this.interestBasis = interestBasis;
		}
		public String getDefaultPoolCode() {
			return defaultPoolCode;
		}
		public void setDefaultPoolCode(String defaultPoolCode) {
			this.defaultPoolCode = defaultPoolCode;
		}
		public int getGlSignReversal() {
			return glSignReversal;
		}
		public void setGlSignReversal(int glSignReversal) {
			this.glSignReversal = glSignReversal;
		}
		public String getDrCrMapInd() {
			return drCrMapInd;
		}
		public void setDrCrMapInd(String drCrMapInd) {
			this.drCrMapInd = drCrMapInd;
		}
		public int getGlStatusNt() {
			return glStatusNt;
		}
		public void setGlStatusNt(int glStatusNt) {
			this.glStatusNt = glStatusNt;
		}
		public int getGlStatus() {
			return glStatus;
		}
		public void setGlStatus(int glStatus) {
			this.glStatus = glStatus;
		}
		public String getGlStatusDate() {
			return glStatusDate;
		}
		public void setGlStatusDate(String glStatusDate) {
			this.glStatusDate = glStatusDate;
		}
		public int getGlAttribute1At() {
			return glAttribute1At;
		}
		public void setGlAttribute1At(int glAttribute1At) {
			this.glAttribute1At = glAttribute1At;
		}
		public String getGlAttribute1() {
			return glAttribute1;
		}
		public void setGlAttribute1(String glAttribute1) {
			this.glAttribute1 = glAttribute1;
		}
		public int getGlAttribute2At() {
			return glAttribute2At;
		}
		public void setGlAttribute2At(int glAttribute2At) {
			this.glAttribute2At = glAttribute2At;
		}
		public String getGlAttribute2() {
			return glAttribute2;
		}
		public void setGlAttribute2(String glAttribute2) {
			this.glAttribute2 = glAttribute2;
		}
		public int getGlAttribute3At() {
			return glAttribute3At;
		}
		public void setGlAttribute3At(int glAttribute3At) {
			this.glAttribute3At = glAttribute3At;
		}
		public String getGlAttribute3() {
			return glAttribute3;
		}
		public void setGlAttribute3(String glAttribute3) {
			this.glAttribute3 = glAttribute3;
		}
		public String getReserveCode() {
			return reserveCode;
		}
		public void setReserveCode(String reserveCode) {
			this.reserveCode = reserveCode;
		}
		public String getDefaultPoolCodeDesc() {
			return defaultPoolCodeDesc;
		}
		public void setDefaultPoolCodeDesc(String defaultPoolCodeDesc) {
			this.defaultPoolCodeDesc = defaultPoolCodeDesc;
		}
		public String getReserveCodeDesc() {
			return reserveCodeDesc;
		}
		public void setReserveCodeDesc(String reserveCodeDesc) {
			this.reserveCodeDesc = reserveCodeDesc;
		}
		public int getDropAccountDetails() {
			return dropAccountDetails;
		}
		public void setDropAccountDetails(int dropAccountDetails) {
			this.dropAccountDetails = dropAccountDetails;
		}
		public int getDropAccountDetailsNt() {
			return dropAccountDetailsNt;
		}
		public void setDropAccountDetailsNt(int dropAccountDetailsNt) {
			this.dropAccountDetailsNt = dropAccountDetailsNt;
		}
		public String getGlTypeDesc() {
			return glTypeDesc;
		}
		public void setGlTypeDesc(String glTypeDesc) {
			this.glTypeDesc = glTypeDesc;
		}
		public List<GLCodesVb> getGlCodesList() {
			return glCodesList;
		}
		public void setGlCodesList(List<GLCodesVb> glCodesList) {
			this.glCodesList = glCodesList;
		}
		public String getGlStatusDesc() {
			return glStatusDesc;
		}
		public void setGlStatusDesc(String glStatusDesc) {
			this.glStatusDesc = glStatusDesc;
		}
		public List<GLMappingsVb> getGlMappingsList() {
			return glMappingsList;
		}
		public void setGlMappingsList(List<GLMappingsVb> glMappingsList) {
			this.glMappingsList = glMappingsList;
		}
		public List<AccountsMappingVb> getAccountsMappingsList() {
			return accountsMappingsList;
		}
		public void setAccountsMappingsList(List<AccountsMappingVb> accountsMappingsList) {
			this.accountsMappingsList = accountsMappingsList;
		}
}