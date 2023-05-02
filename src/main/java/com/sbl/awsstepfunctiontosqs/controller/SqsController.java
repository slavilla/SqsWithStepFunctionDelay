package com.sbl.awsstepfunctiontosqs.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.sbl.awsstepfunctiontosqs.MyMessage;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SqsController {
    private final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                    "", //TODO provide access key
                    "" //TODO provide secret key
            )))
            .withRegion(Regions.US_EAST_1)
            .build();

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/225767313664/delay-queue";

    @PostMapping("sendMessage")
    public Object sendMessage() {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        messageAttributes.put("Created", new MessageAttributeValue().withStringValue(now.toString())
                .withDataType("String"));

        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(queueUrl)
                .withMessageBody("Message created on: " + now)
                //.withDelaySeconds(30) // Message will arrive in the queue after 30 seconds. We can use this only in standard queues
                .withMessageAttributes(messageAttributes);

        return sqs.sendMessage(sendMessageStandardQueue);
    }

    @GetMapping("getMessages")
    public List<MyMessage> getMessages(@RequestParam Integer waitTime,
                                       @RequestParam Integer messageCount) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(waitTime)
                .withMaxNumberOfMessages(messageCount)
                .withAttributeNames("All")
                .withMessageAttributeNames("All");

        ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);

        List<MyMessage> myMessages = Optional.ofNullable(receiveMessageResult)
                .map(ReceiveMessageResult::getMessages)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(message -> {
                    String sentTimestamp = message.getAttributes().get("SentTimestamp");
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(sentTimestamp));
                    LocalDateTime sentDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    LocalDateTime processOn = sentDateTime.plusSeconds(86400); //process after 24 hours

                    MyMessage myMessage = new MyMessage();
                    myMessage.setReceiptHandle(message.getReceiptHandle());
                    myMessage.setBody(message.getBody());
                    myMessage.setSent(sentDateTime);
                    myMessage.setProcessOn(processOn);
                    return myMessage;
                }).collect(Collectors.toList());
        return myMessages;
    }

    @DeleteMapping("deleteMessage/{receiptHandle}")
    public Object deleteMessage(@PathVariable String receiptHandle) {
        return sqs.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl)
                .withReceiptHandle(receiptHandle));
    }
}
