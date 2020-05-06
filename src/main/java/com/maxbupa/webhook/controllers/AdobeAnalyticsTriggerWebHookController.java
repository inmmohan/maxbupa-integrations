package com.maxbupa.webhook.controllers;


import com.maxbupa.webhook.services.AdobeAnalyticsTriggerWebHookService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maxbupa/v1")
public class AdobeAnalyticsTriggerWebHookController {
    private AdobeAnalyticsTriggerWebHookService analyticsTriggerWebHookService;

    public AdobeAnalyticsTriggerWebHookController(AdobeAnalyticsTriggerWebHookService analyticsTriggerWebHookService) {
        this.analyticsTriggerWebHookService = analyticsTriggerWebHookService;
    }

    @GetMapping("/trigger/webhook")
    public String requestChallengeData(@RequestParam(name = "challenge") String challenge){
        return challenge;
    }

    @PostMapping("/trigger/webhook")
    public String insertUpdateTriggerData(@RequestBody String triggerData) {
        return  analyticsTriggerWebHookService.insertUpdateData(triggerData);
    }

}
