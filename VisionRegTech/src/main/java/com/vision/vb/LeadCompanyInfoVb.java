package com.vision.vb;

import java.io.Serializable;

public class LeadCompanyInfoVb implements Serializable{
	private String leadNo="";
	private String organisationName="";
	private String dateIncorp="";
	private String countryIncorp="";
	private String contactPerson="";
	private String directorName="";
	
	public String getLeadNo() {
		return leadNo;
	}
	public void setLeadNo(String leadNo) {
		this.leadNo = leadNo;
	}
	public String getOrganisationName() {
		return organisationName;
	}
	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}
	public String getDateIncorp() {
		return dateIncorp;
	}
	public void setDateIncorp(String dateIncorp) {
		this.dateIncorp = dateIncorp;
	}
	public String getCountryIncorp() {
		return countryIncorp;
	}
	public void setCountryIncorp(String countryIncorp) {
		this.countryIncorp = countryIncorp;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getDirectorName() {
		return directorName;
	}
	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
}
