package com.maxbupa.webhook.controllers;


import com.maxbupa.webhook.services.AdobeAnalyticsTriggerWebHookService;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maxbupa/v1")
public class AdobeAnalyticsTriggerWebHookController {
    private AdobeAnalyticsTriggerWebHookService analyticsTriggerWebHookService;
    private Integer Count = 0;
    public AdobeAnalyticsTriggerWebHookController(AdobeAnalyticsTriggerWebHookService analyticsTriggerWebHookService) {
        this.analyticsTriggerWebHookService = analyticsTriggerWebHookService;
    }

    @GetMapping("/trigger/webhook")
    public String requestChallengeData(@RequestParam(name = "challenge") String challenge){
        return challenge;
    }

    @PostMapping("/trigger/webhook")
    public String insertUpdateTriggerData(@RequestBody String triggerData) {
        Count += 1;
        LoggerFactory.getLogger(AdobeAnalyticsTriggerWebHookController.class).info(String.valueOf(Count));
        //return triggerData;
        return  analyticsTriggerWebHookService.insertUpdateData(triggerData);
    }

}
