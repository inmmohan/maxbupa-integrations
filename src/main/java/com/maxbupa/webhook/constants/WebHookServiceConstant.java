package com.maxbupa.webhook.constants;

import java.util.HashMap;
import java.util.Map;

public class WebHookServiceConstant {

    private WebHookServiceConstant(){

    }
    public static final String SOAP_ENVELOPE_PART_START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:max:dropOffData\"> <soapenv:Header/>";
    public static final String SOAP_ENVELOPE_PART_END = "</soapenv:Envelope>";
    public static final String SOAP_BODY_PART_START	 =	"<soapenv:Body> <urn:insertDropOffData>  <urn:sessiontoken>maxbupaapiuser/maxbupaapiuser</urn:sessiontoken> <urn:dropOffData> <data>";
    public static final String SOAP_BODY_PART_END = "</data> </urn:dropOffData> </urn:insertDropOffData> </soapenv:Body>";
    public static final String ANALYTICS_TRIGGER_DATA_CAPTURED = "Analytics Trigger Data Captured";
    public static final String SAMPLE_EVENT_TRIGGERED = "Sample Event Triggered";
    public static final String REAL_EVENT_TRIGGERED = "Event Triggered";
    public static final String TRIGGER_ID = "triggerId";
    public static final String EVENT_ID = "eventId";
    public static final String MC_ID = "mcId";
    public static final String COM_ADOBE_MCLOUD_PROTOCOL_TRIGGER = "comAdobeMcloudProtocolTrigger";
    public static final String COM_ADOBEM_CLOUD_PIPELINE_PIPELINE_MESSAGE = "comAdobemCloudPipelinePipelineMessage";
    public static final String EVENT = "event";
    public static final String ENRICHMENTS = "enrichments";
    public static final String ENRICHMENT_DATA_START = "<enrichments>";
    public static final String ENRICHMENT_DATA_END = "</enrichments>";
    public static final String ANALYTICS_HIT_SUMMARY = "analyticsHitSummary";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String EMAIL_ID = "emailId";
    public static final String NAME = "name";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String FORMS = "forms";
    public static final String FORMATTED_DOB = "formattedDob";
    public static final String DOB = "dateOfBirth";
    public static final String CUSTOMER_ID = "customerId";
    public static final String FAMILY_COMBINATION = "familyCombination";
    public static final String GET_QUICK_QUOTE_FORM = "getQuickQuoteForm";
    public static final String GEOGRAPHY = "geography";
    public static final String PRODUCT_ID = "productId";
    public static final String SUM_INSURED = "sumInsured";
    public static final String AGE_OF_ELDEST_MEMBER = "ageOfEldestMember";
    public static final String PRODUCT_NAME = "productName";
    public static final String QUOTE_ID = "quoteId";
    public static final String QUOTE_GENERATED_DETAILS = "quoteGeneratedDetails";
    public static final String PREMIUM_AFTER_TAX = "premiumAfterTax";
    public static final String CESS_PERCENTAGE = "cessPercentage";
    public static final String CESS_AMOUNT = "cessAmount";
    public static final String HOSPITAL_CASH = "hospitalCash";
    public static final String TAX_IN_PERCENTAGE = "taxInPercentage";
    public static final String PREMIUM_BEFORE_TAX = "premiumBeforeTax";
    public static final String TAX_AMOUNT = "taxAmount";
    public static final String RECENT_PAYMENTS = "recentPayments";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_AMOUNT = "premiumAmount";
    public static final String APPLICATION_ID = "applicationId";
    public static final String DIMENSIONS = "dimensions";
    public static final String BASE_PREMIUM = "basePremium";
    public static final String EVENT_CHECKOUT = "checkouts";
    public static final String EVENT_PURCHASE = "purchase";
    public static final String EVENT_85 = "event85";
    public static final String EVENT_88 = "event88";
    public static final String EVENT_68 = "event68";
    public static final String EVENT_85_NAME = "Contact DropOff";
    public static final String EVENT_88_NAME = "Quote DropOff";
    public static final String EVENT_CHECKOUT_NAME = "Purchase DropOff";
    public static final String EVENT_68_NAME = "App Form DropOff";
    public static final String EVENT_PURCHASE_NAME = "Purchase Success";
    public static final Map<String, String> eventNameConfig;
    static {
        eventNameConfig = new HashMap<>();
        eventNameConfig.put(EVENT_85, EVENT_85_NAME);
        eventNameConfig.put(EVENT_88, EVENT_88_NAME);
        eventNameConfig.put(EVENT_CHECKOUT, EVENT_CHECKOUT_NAME);
        eventNameConfig.put(EVENT_68, EVENT_68_NAME);
        eventNameConfig.put(EVENT_PURCHASE, EVENT_PURCHASE_NAME);
    }
}
