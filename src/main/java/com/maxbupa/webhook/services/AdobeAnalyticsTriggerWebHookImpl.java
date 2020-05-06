package com.maxbupa.webhook.services;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.maxbupa.webhook.constants.MongoDBConstant;
import com.maxbupa.webhook.constants.WebHookServiceConstant;
import com.maxbupa.webhook.model.DropOffData;
import com.maxbupa.webhook.utilities.CommonUtility;
import com.maxbupa.webhook.utilities.SoapClient;
import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;


@Service
public class AdobeAnalyticsTriggerWebHookImpl implements AdobeAnalyticsTriggerWebHookService {
    private static final Logger logger = LoggerFactory.getLogger(AdobeAnalyticsTriggerWebHookService.class);
    public static final String TRANSITION = "transition";
    public static final String TRIGGER_PATH = "triggerPath";
    public static final String CRITICAL_ILLNESS_SUM_INSURED = "criticalIllnessSumInsured";
    public static final String PERSONAL_ACCIDENT_SUM_INSURED = "personalAccidentSumInsured";
    public static final String HEALTH_ASSURANCE_105 = "HealthAssurance_105";

    @Autowired
    private Environment env;

    private MongoTemplate mongoTemplate;

    @Autowired
    public AdobeAnalyticsTriggerWebHookImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String insertUpdateData(String triggerData) {
        HashedMap triggerResponse = null;
        try {
            triggerResponse = new HashedMap();
            logger.info("********Webhook Request:: " + triggerData);
            triggerData = triggerData.replaceAll("com.adobe.mcloud.pipeline.pipelineMessage", WebHookServiceConstant.COM_ADOBEM_CLOUD_PIPELINE_PIPELINE_MESSAGE);
            triggerData = triggerData.replaceAll("com.adobe.mcloud.protocol.trigger", WebHookServiceConstant.COM_ADOBE_MCLOUD_PROTOCOL_TRIGGER);
            JSONObject trigJsonObjectData = new JSONObject(triggerData);
            triggerResponse.put(WebHookServiceConstant.EVENT_ID, trigJsonObjectData.get("event_id"));
            JSONObject eventJsonObjectData = new JSONObject(trigJsonObjectData.get(WebHookServiceConstant.EVENT).toString());
            if (null != eventJsonObjectData && eventJsonObjectData.has(WebHookServiceConstant.EVENT)) {
                logger.info(WebHookServiceConstant.REAL_EVENT_TRIGGERED);
            } else {
                logger.info(WebHookServiceConstant.SAMPLE_EVENT_TRIGGERED);
            }
            eventJsonObjectData = (JSONObject) eventJsonObjectData.get(WebHookServiceConstant.COM_ADOBEM_CLOUD_PIPELINE_PIPELINE_MESSAGE);
            eventJsonObjectData = (JSONObject) eventJsonObjectData.get(WebHookServiceConstant.COM_ADOBE_MCLOUD_PROTOCOL_TRIGGER);
            final String dropOffData = createDropOffDataRequest(eventJsonObjectData, trigJsonObjectData.getString("event_id"));
            logger.info(WebHookServiceConstant.ANALYTICS_TRIGGER_DATA_CAPTURED);
            logger.info(String.valueOf(triggerResponse));
            new SoapClient(dropOffData);
        } catch (Exception e) {
            assert triggerResponse != null;
            triggerResponse.put("error", e.getMessage());
            return triggerResponse.toString();
        }
        return triggerResponse.toString();
    }

    private JSONObject fetchLatestEventData(final String mcid, final String analyticsData) {
        JSONObject responseJson = new JSONObject();
        try {
            responseJson = fetchLatestTransactions(mcid, analyticsData);
        } catch (Exception e) {

        }
        return responseJson;
    }

    private String createDropOffDataRequest(final JSONObject eventJsonObjectData, final String eventId) {
        String dropOffRequest = "";
        DropOffData dropOffData = new DropOffData();
        dropOffData = fetchEventDetails(eventJsonObjectData, dropOffData);
        dropOffData.setVisitorId(eventJsonObjectData.getString(WebHookServiceConstant.MC_ID));
        logger.info("******MCID********"+eventJsonObjectData.getString(WebHookServiceConstant.MC_ID));
        dropOffData.setTriggerId(eventJsonObjectData.getString(WebHookServiceConstant.TRIGGER_ID));
        final String analyticsData = fetchAnalyticsData(eventJsonObjectData);
        final JSONObject eventJson = fetchLatestEventData(eventJsonObjectData.getString(WebHookServiceConstant.MC_ID),analyticsData);
        logger.info("******event data********"+eventJson);
        dropOffData = processEventData(eventJson, dropOffData);
        try {
            JacksonXmlModule module = new JacksonXmlModule();
            XmlMapper mapper = new XmlMapper(module);
            dropOffRequest = mapper.writeValueAsString(dropOffData);
            dropOffRequest = processAnalyticsData(dropOffRequest, analyticsData);
            logger.info("******dropOffData********"+dropOffRequest);
        } catch (Exception ex) {
            logger.error("Exception in createDropOffDataRequest");
        }
        return dropOffRequest;
    }

    private String fetchAnalyticsData(JSONObject eventJsonObjectData) {
        String analyticsData = "";
        if (eventJsonObjectData.has(WebHookServiceConstant.ENRICHMENTS)) {
            JSONObject enrichmentJson = eventJsonObjectData.getJSONObject(WebHookServiceConstant.ENRICHMENTS);
            if (enrichmentJson.has(WebHookServiceConstant.ANALYTICS_HIT_SUMMARY) && enrichmentJson.getJSONObject(WebHookServiceConstant.ANALYTICS_HIT_SUMMARY).has(WebHookServiceConstant.DIMENSIONS)) {
                analyticsData = enrichmentJson.getJSONObject(WebHookServiceConstant.ANALYTICS_HIT_SUMMARY).getJSONObject(WebHookServiceConstant.DIMENSIONS).toString();
                logger.info("******analyticsData********"+analyticsData);
            }
        }
        return analyticsData;
    }

    private String processAnalyticsData(String dropOffRequest, String analyticsData) {
        Document dropOffDocument = CommonUtility.convertStringToXMLDocument(dropOffRequest);
        Element dropOffElement = dropOffDocument.getDocumentElement();
        JSONObject analyticsJson = new JSONObject(analyticsData.trim());
        Iterator<String> keys = analyticsJson.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if (analyticsJson.get(key) instanceof JSONObject) {
                final JSONObject data = analyticsJson.getJSONObject(key);
                JSONArray dataArray = data.getJSONArray("data");
                if (dataArray.length() > 0) {
                    dropOffElement.setAttribute(key, String.valueOf(dataArray.get(0)));
                }
            }
        }
        dropOffRequest = CommonUtility.convertXmlDocumentToString(dropOffDocument);
        return  dropOffRequest;
    }

    private DropOffData processEventData(final JSONObject eventJson, DropOffData dropOffData) {
        if (null!=eventJson && eventJson.has(MongoDBConstant.RECENT_PRODUCTS)) {
            final JSONArray recentQuote = eventJson.getJSONArray(MongoDBConstant.RECENT_PRODUCTS);
            if (recentQuote.length() > 0) {
                final JSONObject latestQuote = recentQuote.getJSONObject(0);
                dropOffData = basicQuoteDetails(dropOffData, latestQuote);
            }
        }
        if (null != eventJson && eventJson.has(WebHookServiceConstant.RECENT_PAYMENTS)) {
            if (dropOffData.getEventId().equals(WebHookServiceConstant.EVENT_68) ||
                    dropOffData.getEventId().equals(WebHookServiceConstant.EVENT_CHECKOUT) ||
                    dropOffData.getEventId().equals(WebHookServiceConstant.EVENT_PURCHASE)) {
                final JSONArray recentPayments = eventJson.getJSONArray(WebHookServiceConstant.RECENT_PAYMENTS);
                if (recentPayments.length() > 0) {
                    final JSONObject paymentJson = recentPayments.getJSONObject(0);
                    if (null != paymentJson && paymentJson.has(WebHookServiceConstant.PREMIUM) && paymentJson.getJSONArray(WebHookServiceConstant.PREMIUM).length() > 0) {
                        dropOffData = paymentDetails(dropOffData, paymentJson);
                    }
                }
            }
        }
        return dropOffData;
    }

    private DropOffData paymentDetails(DropOffData dropOffData, JSONObject paymentJson) {
        final JSONObject premiumJson = paymentJson.getJSONArray(WebHookServiceConstant.PREMIUM).getJSONObject(0);
        if (premiumJson.has(WebHookServiceConstant.PREMIUM_AMOUNT)) {
            dropOffData.setPremiumAmount(String.valueOf(premiumJson.get(WebHookServiceConstant.PREMIUM_AMOUNT)));
        }
        if (premiumJson.has(WebHookServiceConstant.APPLICATION_ID)) {
            dropOffData.setApplicationId(premiumJson.getString(WebHookServiceConstant.APPLICATION_ID));
        }
        return dropOffData;
    }

    private DropOffData basicQuoteDetails(DropOffData dropOffData, JSONObject latestQuote) {
        if (latestQuote.has(WebHookServiceConstant.CUSTOMER_ID)) {
            dropOffData.setCustomerId(latestQuote.getString(WebHookServiceConstant.CUSTOMER_ID));
        }
        if (latestQuote.has(WebHookServiceConstant.FORMS)) {
            final JSONObject formJson = latestQuote.getJSONObject(WebHookServiceConstant.FORMS);
            if (formJson.has(WebHookServiceConstant.NAME) ) {
                dropOffData.setName(formJson.getString(WebHookServiceConstant.NAME));
            }
            if (formJson.has(WebHookServiceConstant.EMAIL_ADDRESS) ) {
                dropOffData.setEmailAddress(formJson.getString(WebHookServiceConstant.EMAIL_ADDRESS));
            }
            if (formJson.has(WebHookServiceConstant.PHONE_NUMBER) ) {
                dropOffData.setPhoneNumber(formJson.getString(WebHookServiceConstant.PHONE_NUMBER));
            }
            if (formJson.has(WebHookServiceConstant.DOB)) {
                if (formJson.has(WebHookServiceConstant.DOB)) {
                    final String date = formJson.getString(WebHookServiceConstant.DOB);
                    final DateTimeFormatter inputFormat =
                            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
                    final ZonedDateTime parsed = ZonedDateTime.parse(date, inputFormat);
                    final DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dropOffData.setDateOfBirth(outputFormat.format(parsed));
                }
            }
            if (formJson.has(WebHookServiceConstant.FORMATTED_DOB)) {
                dropOffData.setDateOfBirth(formJson.getString(WebHookServiceConstant.FORMATTED_DOB));
            }
            if (formJson.has(WebHookServiceConstant.QUOTE_ID)) {
                dropOffData.setQuoteId(formJson.getString(WebHookServiceConstant.QUOTE_ID));
            }
            if (formJson.has(WebHookServiceConstant.GET_QUICK_QUOTE_FORM)) {
                final JSONObject quickQuoteFormJson = formJson.getJSONObject(WebHookServiceConstant.GET_QUICK_QUOTE_FORM);
                if (quickQuoteFormJson.has(WebHookServiceConstant.FAMILY_COMBINATION)) {
                    dropOffData.setFamilyCombination(quickQuoteFormJson.getString(WebHookServiceConstant.FAMILY_COMBINATION));
                }
                if (quickQuoteFormJson.has(WebHookServiceConstant.GEOGRAPHY)) {
                    dropOffData.setGeography(quickQuoteFormJson.getString(WebHookServiceConstant.GEOGRAPHY));
                }
                if (quickQuoteFormJson.has(WebHookServiceConstant.PRODUCT_ID)) {
                    dropOffData.setProductId(quickQuoteFormJson.getString(WebHookServiceConstant.PRODUCT_ID));
                    if (quickQuoteFormJson.getString(WebHookServiceConstant.PRODUCT_ID).equals(HEALTH_ASSURANCE_105)) {
                        if (quickQuoteFormJson.has(PERSONAL_ACCIDENT_SUM_INSURED)) {
                            dropOffData.setPersonalAccidentSumInsured(quickQuoteFormJson.getString(PERSONAL_ACCIDENT_SUM_INSURED));
                        }
                        if (quickQuoteFormJson.has(CRITICAL_ILLNESS_SUM_INSURED)) {
                            dropOffData.setCriticalIllnessSumInsured(quickQuoteFormJson.getString(CRITICAL_ILLNESS_SUM_INSURED));
                        }
                    }
                }
                if (quickQuoteFormJson.has(WebHookServiceConstant.SUM_INSURED)) {
                    dropOffData.setSumInsured(quickQuoteFormJson.getString(WebHookServiceConstant.SUM_INSURED));
                }
                if (quickQuoteFormJson.has(WebHookServiceConstant.AGE_OF_ELDEST_MEMBER)) {
                    dropOffData.setAgeOfEldestMember(quickQuoteFormJson.getString(WebHookServiceConstant.AGE_OF_ELDEST_MEMBER));
                }
                if (quickQuoteFormJson.has(WebHookServiceConstant.PRODUCT_NAME)) {
                    dropOffData.setProductName(quickQuoteFormJson.getString(WebHookServiceConstant.PRODUCT_NAME));
                }
            }
            if (formJson.has(WebHookServiceConstant.QUOTE_GENERATED_DETAILS)) {
                final JSONObject quoteGeneratedJson = formJson.getJSONObject(WebHookServiceConstant.QUOTE_GENERATED_DETAILS);
                if (quoteGeneratedJson.has(WebHookServiceConstant.PREMIUM_AFTER_TAX)) {
                    dropOffData.setPremiumAfterTax(quoteGeneratedJson.getString(WebHookServiceConstant.PREMIUM_AFTER_TAX));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.CESS_PERCENTAGE)) {
                    dropOffData.setCessPercentage(quoteGeneratedJson.getString(WebHookServiceConstant.CESS_PERCENTAGE));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.CESS_AMOUNT)) {
                    dropOffData.setCessAmount(quoteGeneratedJson.getString(WebHookServiceConstant.CESS_AMOUNT));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.HOSPITAL_CASH)) {
                    dropOffData.setHospitalCash(quoteGeneratedJson.getString(WebHookServiceConstant.HOSPITAL_CASH));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.TAX_IN_PERCENTAGE)) {
                    dropOffData.setTaxInPercentage(quoteGeneratedJson.getString(WebHookServiceConstant.TAX_IN_PERCENTAGE));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.PREMIUM_BEFORE_TAX)) {
                    dropOffData.setPremiumBeforeTax(quoteGeneratedJson.getString(WebHookServiceConstant.PREMIUM_BEFORE_TAX));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.TAX_AMOUNT)) {
                    dropOffData.setTaxAmount(quoteGeneratedJson.getString(WebHookServiceConstant.TAX_AMOUNT));
                }
                if (quoteGeneratedJson.has(WebHookServiceConstant.BASE_PREMIUM)) {
                    dropOffData.setBasePremium(quoteGeneratedJson.getString(WebHookServiceConstant.BASE_PREMIUM));
                }
            }
        }
        return dropOffData;
    }

    public JSONObject fetchLatestTransactions(String mcid, String analyticsData) {
        String uniquePolicyNumber = null;
        String quoteId = null;
        String productId = null;
        JSONArray recentTransactions = null;
        JSONArray recentQuotes = null;
        JSONArray recentProducts = null;

        Pageable pageable = PageRequest.of(0, 2);
        JSONObject analyticsJson = new JSONObject(analyticsData.trim());
        Iterator<String> keys = analyticsJson.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if (analyticsJson.get(key) instanceof JSONObject) {
                final JSONObject data = analyticsJson.getJSONObject(key);
                JSONArray dataArray = data.getJSONArray("data");
                if (dataArray.length() > 0) {
                    if (key.equals("eVar53")) {
                        uniquePolicyNumber = String.valueOf(dataArray.get(0));
                        if (!StringUtils.isEmpty(uniquePolicyNumber)) {
                            Query paymentDetail = new Query().with(pageable);
                            paymentDetail.addCriteria(Criteria.where(MongoDBConstant.UNIQUE_POLICY_NUMBER).is(uniquePolicyNumber));
                            paymentDetail.with(new Sort(Sort.Direction.DESC, MongoDBConstant.DATE_TIME));
                            paymentDetail.fields().exclude(MongoDBConstant.ID);
                            JSONArray paymentDetailArray = new JSONArray(mongoTemplate
                                    .find(paymentDetail, Object.class, MongoDBConstant.PAYMENT_DETAILS));
                            if (null != paymentDetailArray && paymentDetailArray.length() > 0) {
                                final JSONObject paymentJson = paymentDetailArray.getJSONObject(0);
                                if (null != paymentJson && paymentJson.has(MongoDBConstant.MCID)) {
                                    mcid = paymentJson.getString(MongoDBConstant.MCID);
                                }
                                if (null!= paymentJson && paymentJson.has(MongoDBConstant.QUOTE_ID)) {
                                    quoteId = paymentJson.getString(MongoDBConstant.QUOTE_ID);
                                }
                            }
                        }
                    }
                    if (key.equals("eVar35")) {
                        productId = String.valueOf(dataArray.get(0));
                        logger.info("******Product ID*****"+productId);
                    }
                }
            }
        }
        Query fetchLatestRecords = new Query().with(pageable);
        fetchLatestRecords.addCriteria(Criteria.where(MongoDBConstant.MCID).is(mcid));
        fetchLatestRecords.with(new Sort(Sort.Direction.DESC, MongoDBConstant.DATE_TIME));
        fetchLatestRecords.fields().exclude(MongoDBConstant.ID);

        recentTransactions = new JSONArray(mongoTemplate
                .find(fetchLatestRecords, Object.class, MongoDBConstant.ONLINE_LEADS));

        Query fetchQuoteGenerated = new Query().with(pageable);
        fetchQuoteGenerated.addCriteria(Criteria.where(MongoDBConstant.MCID).is(mcid));
        fetchQuoteGenerated.addCriteria(Criteria.where(MongoDBConstant.FORMS_QUOTE_ID).ne(null).exists(true));
        if (!StringUtils.isEmpty(productId)) {
            fetchQuoteGenerated.addCriteria((Criteria.where(MongoDBConstant.FORMS_GET_QUOTE_PRODUCT_ID).is(productId)));
        }
        fetchQuoteGenerated.addCriteria(Criteria.where(MongoDBConstant.PAYMENT_STATUS).ne(MongoDBConstant.PAYMENT_SUCCESS));
        fetchQuoteGenerated.with(new Sort(Sort.Direction.DESC, MongoDBConstant.DATE_TIME));
        fetchQuoteGenerated.fields().exclude(MongoDBConstant.ID);

        recentQuotes = new JSONArray(mongoTemplate
                .find(fetchQuoteGenerated, Object.class, MongoDBConstant.ONLINE_LEADS));

        Query fetchProductViewed = new Query().with(pageable);
        if (!StringUtils.isEmpty(quoteId)) {
            fetchProductViewed.addCriteria(Criteria.where(MongoDBConstant.FORMS_QUOTE_ID).is(quoteId));
        }
        if (!StringUtils.isEmpty(productId)) {
            fetchProductViewed.addCriteria((Criteria.where(MongoDBConstant.FORMS_GET_QUOTE_PRODUCT_ID).is(productId)));
        }
        fetchProductViewed.addCriteria(Criteria.where(MongoDBConstant.MCID).is(mcid));
        fetchProductViewed.with(new Sort(Sort.Direction.DESC, MongoDBConstant.DATE_TIME));
        fetchQuoteGenerated.fields().exclude(MongoDBConstant.ID);

        recentProducts = new JSONArray(mongoTemplate
                .find(fetchProductViewed, Object.class, MongoDBConstant.ONLINE_LEADS));
        JSONArray recentPayments = fetchRecentPaymentDetails(mcid, uniquePolicyNumber, productId);
        JSONObject response = new JSONObject();
        response.put(MongoDBConstant.RECENT_TRANSACTIONS, recentTransactions);
        response.put(MongoDBConstant.RECENT_QUOTES, recentQuotes);
        response.put(MongoDBConstant.RECENT_PRODUCTS, recentProducts);
        response.put(MongoDBConstant.RECENT_PAYMENTS, recentPayments);

        return response;
    }

    private JSONArray fetchRecentPaymentDetails(String mcid, String uniquePolicyNumber, String productId) {
        JSONArray recentPayments = new JSONArray();
        Pageable pageable = PageRequest.of(0, 10);
        Query paymentDetail = new Query().with(pageable);
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(uniquePolicyNumber)) {
            criteria.and(MongoDBConstant.UNIQUE_POLICY_NUMBER).is(uniquePolicyNumber);
            criteria.and(MongoDBConstant.PAYMENT_STATUS).is(MongoDBConstant.PAYMENT_SUCCESS);
        } else {
            criteria.and(MongoDBConstant.MCID).is(mcid);
            paymentDetail.addCriteria(Criteria.where(MongoDBConstant.PAYMENT_STATUS).ne(MongoDBConstant.PAYMENT_SUCCESS));
            if (!StringUtils.isEmpty(productId)) {
                paymentDetail.addCriteria(Criteria.where(MongoDBConstant.PREMIUM_PRODUCT_ID).is(productId));
            }
        }
        paymentDetail.addCriteria(criteria);
        paymentDetail.fields().exclude(MongoDBConstant.ID);
        paymentDetail.with(new Sort(Sort.Direction.DESC, MongoDBConstant.DATE_TIME));
        paymentDetail.fields().include(MongoDBConstant.CUSTOMER_NAME);
        paymentDetail.fields().include(MongoDBConstant.QUOTE_ID);
        paymentDetail.fields().include(WebHookServiceConstant.PREMIUM);
        paymentDetail.fields().include(MongoDBConstant.UNIQUE_POLICY_NUMBER);
        JSONArray paymentDetailArray = new JSONArray(mongoTemplate
                .find(paymentDetail, Object.class, MongoDBConstant.PAYMENT_DETAILS));

        int recordsCount = 0;
        for (int i = 0; i < paymentDetailArray.length(); i++) {
            if(recordsCount == 2){
                break;
            }
            JSONObject resultantJson = new JSONObject();
            JSONObject paymentDetailJson = paymentDetailArray.getJSONObject(i);
            JSONArray applicationStatusArray = new JSONArray();
            if (paymentDetailJson != null && paymentDetailJson.has(WebHookServiceConstant.PREMIUM)) {
                resultantJson.put(WebHookServiceConstant.PREMIUM, paymentDetailJson.get(WebHookServiceConstant.PREMIUM));
                JSONArray premiumArray = (JSONArray) paymentDetailJson.get(WebHookServiceConstant.PREMIUM);
                JSONObject applicationStatuJson = getApplicationDetails(premiumArray);
                if(applicationStatuJson != null && applicationStatuJson.has(MongoDBConstant.RECORDS_COUNT)){
                    recordsCount = recordsCount + applicationStatuJson.getInt(MongoDBConstant.RECORDS_COUNT);
                    applicationStatusArray.put(applicationStatuJson);
                    if(applicationStatusArray.length() > 0){
                        resultantJson.put(MongoDBConstant.APPLICATION_DETAILS,applicationStatusArray);
                        if (paymentDetailJson != null && paymentDetailJson.has(MongoDBConstant.CUSTOMER_NAME)) {
                            resultantJson.put(MongoDBConstant.CUSTOMER_NAME, paymentDetailJson.getString(MongoDBConstant.CUSTOMER_NAME));
                        }
                        if (paymentDetailJson != null && paymentDetailJson.has(MongoDBConstant.UNIQUE_POLICY_NUMBER)) {
                            resultantJson.put(MongoDBConstant.UNIQUE_POLICY_NUMBER, paymentDetailJson.getString(MongoDBConstant.UNIQUE_POLICY_NUMBER));
                        }
                        if (paymentDetailJson != null && paymentDetailJson.has(MongoDBConstant.QUOTE_ID)) {
                            resultantJson.put(MongoDBConstant.QUOTE_ID, paymentDetailJson.getString(MongoDBConstant.QUOTE_ID));
                            Query quoteDetails = new Query();
                            quoteDetails.addCriteria(Criteria.where(MongoDBConstant.FORMS_QUOTE_ID).is(paymentDetailJson.getString(MongoDBConstant.QUOTE_ID)));
                            quoteDetails.fields().exclude(MongoDBConstant.ID);
                            quoteDetails.fields().include(MongoDBConstant.FORMS);
                            resultantJson.put(MongoDBConstant.FORMS, mongoTemplate.find(quoteDetails, Object.class, MongoDBConstant.ONLINE_LEADS));
                        }
                        recentPayments.put(resultantJson);
                    }
                }
            }
        }
        return recentPayments;
    }

    private JSONObject getApplicationDetails(JSONArray premiumArray){
        JSONObject applicationStatusJson = null;
        if (premiumArray.length() > 0) {
            JSONObject premiumJson = premiumArray.getJSONObject(0);
            if (premiumJson != null && premiumJson.has(MongoDBConstant.APPLICATION_ID)) {
                Query applicationQuery = new Query();
                applicationQuery.addCriteria(
                        Criteria.where(MongoDBConstant.APPLICATION_ID).is(premiumJson.getString(MongoDBConstant.APPLICATION_ID)));
                applicationQuery.fields().exclude(MongoDBConstant.ID);
                applicationQuery.fields().include(MongoDBConstant.PERCENTAGE_COMPLETE);
                applicationQuery.fields().include(MongoDBConstant.JOURNEY_COMPLETE);
                JSONArray applicationArray = new JSONArray(mongoTemplate.find(applicationQuery, Object.class,
                        MongoDBConstant.APPLICATION_FORM_DETAILS));
                if(applicationArray.length() > 0){
                    applicationStatusJson = journeyApplicationStatus(applicationArray.getJSONObject(0));
                    if(applicationStatusJson != null ){
                        applicationStatusJson.put(MongoDBConstant.RECORDS_COUNT,1);
                    }
                }else{
                    applicationStatusJson = setJourneyStatus();
                    applicationStatusJson.put(MongoDBConstant.RECORDS_COUNT,1);
                }
            }else{
                applicationStatusJson = setJourneyStatus();
                applicationStatusJson.put(MongoDBConstant.RECORDS_COUNT,1);
            }
        }
        return applicationStatusJson;
    }

    private JSONObject journeyApplicationStatus(JSONObject applicationJson){
        JSONObject applicationStatusJson = null;
        if(applicationJson != null && applicationJson.has(MongoDBConstant.JOURNEY_COMPLETE)){
            String journeyStatus = applicationJson.getString(MongoDBConstant.JOURNEY_COMPLETE);
            if(journeyStatus != null && journeyStatus.equalsIgnoreCase("true")){
                return applicationStatusJson;
            }else{
                applicationStatusJson = new JSONObject();
                applicationStatusJson.put(MongoDBConstant.JOURNEY_COMPLETE,journeyStatus);
                if(applicationJson != null && applicationJson.has(MongoDBConstant.PERCENTAGE_COMPLETE)){
                    applicationStatusJson.put(MongoDBConstant.PERCENTAGE_COMPLETE,applicationJson.getString(MongoDBConstant.PERCENTAGE_COMPLETE));
                }
            }
        }
        return applicationStatusJson;
    }

    private JSONObject setJourneyStatus(){
        JSONObject applicationStatusJson = new JSONObject();
        applicationStatusJson.put(MongoDBConstant.PERCENTAGE_COMPLETE,MongoDBConstant.PERCENTAGE_OF_JOURNEY);
        applicationStatusJson.put(MongoDBConstant.JOURNEY_COMPLETE,MongoDBConstant.JOURNEY_COMPLETION_FLAG);
        applicationStatusJson.put(MongoDBConstant.RECORDS_COUNT,1);
        return applicationStatusJson;
    }

    private DropOffData fetchEventDetails(JSONObject eventJson, DropOffData dropOffData) {
        if (null != eventJson && eventJson.has(TRIGGER_PATH)) {
            final JSONArray triggerArray = eventJson.getJSONArray(TRIGGER_PATH);
            if (null != triggerArray) {
                for (int eventIndex=0; eventIndex < triggerArray.length(); eventIndex ++) {
                    final JSONObject triggerJson = triggerArray.getJSONObject(eventIndex);
                    if (null != triggerJson && triggerJson.has(TRANSITION)) {
                        String eventId = triggerJson.getString(TRANSITION);
                        if (eventId.contains("conditional") && eventId.contains("select * where events.")) {
                            eventId = eventId.substring(eventId.indexOf(".") + 1);
                            eventId = eventId.substring(0, eventId.indexOf(" "));
                            if (null != eventId) {
                                logger.info("******Event ID******"+eventId);
                                dropOffData.setEventId(eventId);
                                if (WebHookServiceConstant.eventNameConfig.containsKey(eventId)) {
                                    dropOffData.setEventName(WebHookServiceConstant.eventNameConfig.get(eventId));
                                    logger.info("******Event Name******"+dropOffData.getEventName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return dropOffData;
    }
}
