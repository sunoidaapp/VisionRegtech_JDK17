package com.vision.vb;

public class GLMappingsVb extends CommonVb{

		private static final long serialVersionUID = 2942783983913844700L;
		private String bsGl = "";
		private String plGl = "";
		private String glEnrichId = "";
		private String productDr = "";
		private String productCr = "";
		private String frlLineBsDr = "";
		private String frlLineBsCr = "";
		private String frlLinePlDr = "";
		private String frlLinePlCr = "";
		private String mrlLineDr = "";
		private String mrlLineCr = "";
		/*private String oucOverride = "";
		private String taxAllowable = "";*/
		private int glMapStatusNt = 0;
		private int glMapStatus = -1;
		private String bsGlDesc = "";//  BSGL Description
		private String plGlDesc = "";//  PlGl Description
		private String frlLineBsDrDes = "";//FRL_LINE_BS_DR  Desc
		private String frlLineBsCrDesc = "";//FRL_LINE_BS_CR Desc
		private String frlLinePlDrDesc = "";//FRL_LINE_PL_DR Desc
		private String frlLinePlCrDesc = "";//FRL_LINE_PL_CR Desc
		private String mrlLineDrDesc = "";//FRL_LINE_BS_DR  Desc
		private String mrlLineCrDesc = "";//FRL_LINE_BS_CR Desc
		private String productDrDesc = "";//PRODUCT_DR
		private String productCrDesc = "";
		private String glEnrichIdDesc = "";//GL_ENRICH_ID  Desc
		/*private String oucOverrideDesc = "";//OUC_OVERRIDE */		
		private String bsPlGlFlag = "";
		
		public String getBsGl() {
			return bsGl;
		}
		public void setBsGl(String bsGl) {
			this.bsGl = bsGl;
		}
		public String getPlGl() {
			return plGl;
		}
		public void setPlGl(String plGl) {
			this.plGl = plGl;
		}
		public String getGlEnrichId() {
			return glEnrichId;
		}
		public void setGlEnrichId(String glEnrichId) {
			this.glEnrichId = glEnrichId;
		}
		public String getProductDr() {
			return productDr;
		}
		public void setProductDr(String productDr) {
			this.productDr = productDr;
		}
		public String getProductCr() {
			return productCr;
		}
		public void setProductCr(String productCr) {
			this.productCr = productCr;
		}
		public String getFrlLineBsDr() {
			return frlLineBsDr;
		}
		public void setFrlLineBsDr(String frlLineBsDr) {
			this.frlLineBsDr = frlLineBsDr;
		}
		public String getFrlLineBsCr() {
			return frlLineBsCr;
		}
		public void setFrlLineBsCr(String frlLineBsCr) {
			this.frlLineBsCr = frlLineBsCr;
		}
		public String getFrlLinePlDr() {
			return frlLinePlDr;
		}
		public void setFrlLinePlDr(String frlLinePlDr) {
			this.frlLinePlDr = frlLinePlDr;
		}
		public String getFrlLinePlCr() {
			return frlLinePlCr;
		}
		public void setFrlLinePlCr(String frlLinePlCr) {
			this.frlLinePlCr = frlLinePlCr;
		}
		public String getMrlLineDr() {
			return mrlLineDr;
		}
		public void setMrlLineDr(String mrlLineDr) {
			this.mrlLineDr = mrlLineDr;
		}
		public String getMrlLineCr() {
			return mrlLineCr;
		}
		public void setMrlLineCr(String mrlLineCr) {
			this.mrlLineCr = mrlLineCr;
		}
		/*public String getOucOverride() {
			return oucOverride;
		}
		public void setOucOverride(String oucOverride) {
			this.oucOverride = oucOverride;
		}
		public String getTaxAllowable() {
			return taxAllowable;
		}
		public void setTaxAllowable(String taxAllowable) {
			this.taxAllowable = taxAllowable;
		}*/
		public int getGlMapStatusNt() {
			return glMapStatusNt;
		}
		public void setGlMapStatusNt(int glMapStatusNt) {
			this.glMapStatusNt = glMapStatusNt;
		}
		public int getGlMapStatus() {
			return glMapStatus;
		}
		public void setGlMapStatus(int glMapStatus) {
			this.glMapStatus = glMapStatus;
		}
		public String getBsGlDesc() {
			return bsGlDesc;
		}
		public void setBsGlDesc(String strBsGlDesc) {
			this.bsGlDesc = strBsGlDesc;
		}
		public String getPlGlDesc() {
			return plGlDesc;
		}
		public void setPlGlDesc(String strPlGlDesc) {
			this.plGlDesc = strPlGlDesc;
		}
		public String getFrlLineBsDrDes() {
			return frlLineBsDrDes;
		}
		public void setFrlLineBsDrDes(String frlLineBsDrDes) {
			this.frlLineBsDrDes = frlLineBsDrDes;
		}
		public String getFrlLineBsCrDesc() {
			return frlLineBsCrDesc;
		}
		public void setFrlLineBsCrDesc(String frlLineBsCrDesc) {
			this.frlLineBsCrDesc = frlLineBsCrDesc;
		}
		public String getFrlLinePlDrDesc() {
			return frlLinePlDrDesc;
		}
		public void setFrlLinePlDrDesc(String frlLinePlDrDesc) {
			this.frlLinePlDrDesc = frlLinePlDrDesc;
		}
		public String getFrlLinePlCrDesc() {
			return frlLinePlCrDesc;
		}
		public void setFrlLinePlCrDesc(String frlLinePlCrDesc) {
			this.frlLinePlCrDesc = frlLinePlCrDesc;
		}
		public String getMrlLineDrDesc() {
			return mrlLineDrDesc;
		}
		public void setMrlLineDrDesc(String mrlLineDrDesc) {
			this.mrlLineDrDesc = mrlLineDrDesc;
		}
		public String getMrlLineCrDesc() {
			return mrlLineCrDesc;
		}
		public void setMrlLineCrDesc(String mrlLineCrDesc) {
			this.mrlLineCrDesc = mrlLineCrDesc;
		}
		public String getProductDrDesc() {
			return productDrDesc;
		}
		public void setProductDrDesc(String productDrDesc) {
			this.productDrDesc = productDrDesc;
		}
		public String getProductCrDesc() {
			return productCrDesc;
		}
		public void setProductCrDesc(String productCrDesc) {
			this.productCrDesc = productCrDesc;
		}
		public String getGlEnrichIdDesc() {
			return glEnrichIdDesc;
		}
		public void setGlEnrichIdDesc(String glEnrichIdDesc) {
			this.glEnrichIdDesc = glEnrichIdDesc;
		}
		/*public String getOucOverrideDesc() {
			return oucOverrideDesc;
		}
		public void setOucOverrideDesc(String oucOverrideDesc) {
			this.oucOverrideDesc = oucOverrideDesc;
		}*/
		public String getBsPlGlFlag() {
			return bsPlGlFlag;
		}
		public void setBsPlGlFlag(String bsPlGlFlag) {
			this.bsPlGlFlag = bsPlGlFlag;
		}
}