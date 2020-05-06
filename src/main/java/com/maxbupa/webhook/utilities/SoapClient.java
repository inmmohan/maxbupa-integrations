package com.maxbupa.webhook.utilities;

import com.maxbupa.webhook.constants.WebHookServiceConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SoapClient {
    private static final Logger logger = LoggerFactory.getLogger(SoapClient.class);

    public SoapClient(String dropOffData){
        try {
            final StringBuilder soapRequest = new StringBuilder();
            soapRequest.append(WebHookServiceConstant.SOAP_ENVELOPE_PART_START);
            soapRequest.append(WebHookServiceConstant.SOAP_BODY_PART_START);
            soapRequest.append(dropOffData);
            //soapRequest.append("\t<dropOffData formId=\"asas\" formName=\""+eventId+"\" triggerId=\""+triggerId+"\" visitorId=\""+visitorId+"\"/>\n");
            soapRequest.append(WebHookServiceConstant.ENRICHMENT_DATA_START);
            //soapRequest.append(analyticsData);
            soapRequest.append(WebHookServiceConstant.ENRICHMENT_DATA_END);
            soapRequest.append(WebHookServiceConstant.SOAP_BODY_PART_END);
            soapRequest.append(WebHookServiceConstant.SOAP_ENVELOPE_PART_END);
            final String soapEnvelope = soapRequest.toString();
            final String url = "https://maxbupahealthinsurance-mkt-stage1.campaign.adobe.com/nl/jsp/soaprouter.jsp";
            final URL obj = new URL(url);
            final HttpURLConnection soapConnection = (HttpURLConnection) obj.openConnection();
            soapConnection.setDoOutput(true);
            logger.info("Connection Set to Campaign");
            soapConnection.setRequestMethod("POST");
            soapConnection.setRequestProperty("SOAPAction","max:dropOffData#insertDropOffData");
            soapConnection.addRequestProperty("Content-Type","text/xml; charset=\"utf-8\"");
            OutputStream reqStream = soapConnection.getOutputStream();
            reqStream.write(soapEnvelope.getBytes());
            logger.info("Soap Request Triggered");
            final InputStream resStream = soapConnection.getInputStream();
            final byte[] byteBuf = new byte[10240];
            final int len = resStream.read(byteBuf);
            logger.info("Data Inserted/Updated Successfully");
            reqStream.close();
            resStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
