package com.pagamento.aws.sns;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class SnsService {

    private final SnsClient snsClient;
    
    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String publishMessage(String topicArn, String message) {
        return publishMessage(topicArn, message, null);
    }

    public String publishMessage(String topicArn, String message, Map<String, MessageAttributeValue> attributes) {
        PublishRequest.Builder builder = PublishRequest.builder()
            .topicArn(topicArn)
            .message(message);
        
        if (attributes != null) {
            builder.messageAttributes(attributes);
        }
        
        PublishResponse response = snsClient.publish(builder.build());
        return response.messageId();
    }
}
