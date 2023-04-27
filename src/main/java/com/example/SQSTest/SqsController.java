package com.example.SQSTest;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SqsController
{
    private final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                    "AKIATJEGH2EAM2IPZBE2",
                    "ExBpOMHuwdPGfL3UZ61WlKRNliGhmT54vxCqr5YM"
            )))
            .withRegion(Regions.US_EAST_1)
            .build();
    private String standardQueueUrl;

    @PostMapping("/createQueue")
    public String createQueue()
    {

        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");

        CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest("test-queue");
        standardQueueUrl = sqs.createQueue(createStandardQueueRequest)
                .getQueueUrl();

        String message = "Queue created: " + standardQueueUrl;
        System.out.println(message);
        return message;
    }

    @PostMapping("sendMessage")
    public String sendMessage()
    {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        messageAttributes.put("AttributeOne", new MessageAttributeValue().withStringValue("This is an attribute")
                .withDataType("String"));
        messageAttributes.put("Created", new MessageAttributeValue().withStringValue(now.toString()));

        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest().withQueueUrl(standardQueueUrl)
                .withMessageBody("A simple message.")
                //.withDelaySeconds(30) // Message will arrive in the queue after 30 seconds. We can use this only in standard queues
                .withMessageAttributes(messageAttributes);

        SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageStandardQueue);
        return "Message ID: " + sendMessageResult.getMessageId();
    }
}
