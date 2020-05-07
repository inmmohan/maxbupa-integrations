package com.maxbupa.webhook.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "dropOffData")
public class DropOffData {

    @JacksonXmlProperty(isAttribute = true, localName="eventId")
    private String eventId;

    @JacksonXmlProperty(isAttribute = true, localName="requestId")
    private String requestId;

    @JacksonXmlProperty(isAttribute = true, localName = "eventName")
    private String eventName;

    @JacksonXmlProperty(isAttribute = true, localName="triggerId")
    private String triggerId;

    @JacksonXmlProperty(isAttribute = true, localName = "visitorId")
    private String visitorId;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    @JacksonXmlProperty(isAttribute = true, localName = "emailAddress")
    private String emailAddress;

    @JacksonXmlProperty(isAttribute = true, localName = "phoneNumber")
    private String phoneNumber;

    @JacksonXmlProperty(isAttribute = true, localName = "dateOfBirth")
    private String dateOfBirth;

    @JacksonXmlProperty(isAttribute = true, localName = "customerId")
    private String customerId;

    @JacksonXmlProperty(isAttribute = true, localName = "familyCombination")
    private String familyCombination;

    @JacksonXmlProperty(isAttribute = true, localName = "geography")
    private String geography;

    @JacksonXmlProperty(isAttribute = true, localName = "productId")
    private String productId;

    @JacksonXmlProperty(isAttribute = true, localName = "sumInsured")
    private String sumInsured;

    @JacksonXmlProperty(isAttribute = true, localName = "ageOfEldestMember")
    private String ageOfEldestMember;

    @JacksonXmlProperty(isAttribute = true, localName = "productName")
    private String productName;

    @JacksonXmlProperty(isAttribute = true, localName = "quoteId")
    private String quoteId;

    @JacksonXmlProperty(isAttribute = true, localName = "premiumAfterTax")
    private String premiumAfterTax;

    @JacksonXmlProperty(isAttribute = true, localName = "cessPercentage")
    private String cessPercentage;

    @JacksonXmlProperty(isAttribute = true, localName = "cessAmount")
    private String cessAmount;

    @JacksonXmlProperty(isAttribute = true, localName = "hospitalCash")
    private String hospitalCash;

    @JacksonXmlProperty(isAttribute = true, localName = "taxInPercentage")
    private String taxInPercentage;

    @JacksonXmlProperty(isAttribute = true, localName = "premiumBeforeTax")
    private String premiumBeforeTax;

    @JacksonXmlProperty(isAttribute = true, localName = "taxAmount")
    private String taxAmount;

    @JacksonXmlProperty(isAttribute = true, localName = "premiumAmount")
    private String premiumAmount;

    @JacksonXmlProperty(isAttribute = true, localName = "applicationId")
    private String applicationId;

    @JacksonXmlProperty(isAttribute = true, localName = "basePremium")
    private String basePremium;

    @JacksonXmlProperty(isAttribute = true, localName = "personalAccidentSumInsured")
    private String personalAccidentSumInsured;

    @JacksonXmlProperty(isAttribute = true, localName = "criticalIllnessSumInsured")
    private String criticalIllnessSumInsured;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFamilyCombination() {
        return familyCombination;
    }

    public void setFamilyCombination(String familyCombination) {
        this.familyCombination = familyCombination;
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        this.geography = geography;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(String sumInsured) {
        this.sumInsured = sumInsured;
    }

    public String getAgeOfEldestMember() {
        return ageOfEldestMember;
    }

    public void setAgeOfEldestMember(String ageOfEldestMember) {
        this.ageOfEldestMember = ageOfEldestMember;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getPremiumAfterTax() {
        return premiumAfterTax;
    }

    public void setPremiumAfterTax(String premiumAfterTax) {
        this.premiumAfterTax = premiumAfterTax;
    }

    public String getCessAmount() {
        return cessAmount;
    }

    public void setCessAmount(String cessAmount) {
        this.cessAmount = cessAmount;
    }

    public String getHospitalCash() {
        return hospitalCash;
    }

    public void setHospitalCash(String hospitalCash) {
        this.hospitalCash = hospitalCash;
    }

    public String getTaxInPercentage() {
        return taxInPercentage;
    }

    public void setTaxInPercentage(String taxInPercentage) {
        this.taxInPercentage = taxInPercentage;
    }

    public String getPremiumBeforeTax() {
        return premiumBeforeTax;
    }

    public void setPremiumBeforeTax(String premiumBeforeTax) {
        this.premiumBeforeTax = premiumBeforeTax;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCessPercentage() {
        return cessPercentage;
    }

    public void setCessPercentage(String cessPercentage) {
        this.cessPercentage = cessPercentage;
    }

    public String getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(String premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(String basePremium) {
        this.basePremium = basePremium;
    }

    public String getPersonalAccidentSumInsured() {
        return personalAccidentSumInsured;
    }

    public void setPersonalAccidentSumInsured(String personalAccidentSumInsured) {
        this.personalAccidentSumInsured = personalAccidentSumInsured;
    }

    public String getCriticalIllnessSumInsured() {
        return criticalIllnessSumInsured;
    }

    public void setCriticalIllnessSumInsured(String criticalIllnessSumInsured) {
        this.criticalIllnessSumInsured = criticalIllnessSumInsured;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
