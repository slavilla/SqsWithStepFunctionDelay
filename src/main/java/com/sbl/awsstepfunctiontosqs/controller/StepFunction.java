package com.sbl.awsstepfunctiontosqs.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class StepFunction {

    private final AWSStepFunctions sfn = AWSStepFunctionsClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                    "", //TODO provide access key
                    "" //TODO provide secret key
            )))
            .withRegion(Regions.US_EAST_1)
            .build();

    @PostMapping("sendMessageWithDelay")
    public Object sendMessageWithDelay(@RequestBody String body) {
        String uuid = UUID.randomUUID().toString();
        StartExecutionRequest startExecutionRequest = new StartExecutionRequest();
        startExecutionRequest.setStateMachineArn("arn:aws:states:us-east-1:225767313664:stateMachine:DelayMessageToSqs");
        startExecutionRequest.setInput(body);
        startExecutionRequest.setName(uuid);
        StartExecutionResult startExecutionResult = sfn.startExecution(startExecutionRequest);
        return "Execution ID: " + uuid;
    }
}
