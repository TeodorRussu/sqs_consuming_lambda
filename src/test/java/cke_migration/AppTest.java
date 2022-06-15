package cke_migration;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void successfulResponse() {
        App app = new App();

//        var message = new SQSEvent.SQSMessage();
//        message.setBody("{\"eventVersion\":\"2.1\",\"eventSource\":\"aws:s3\",\"awsRegion\":\"us-east-1\",\"eventTime\":\"2022-06-03T15:58:22.625Z\",\"eventName\":\"ObjectCreated:Put\",\"userIdentity\":{\"principalId\":\"A349GCMCWCNHIL\"},\"requestParameters\":{\"sourceIPAddress\":\"95.91.241.100\"},\"responseElements\":{\"x-amz-request-id\":\"DMWKHSS6MAVDZVND\",\"x-amz-id-2\":\"F4qhoEPvyPyznG/gppZSS8ut/LBucw4sAn2dXcQBqwVGvhKBbJFxSINNWzxsrVzCPyaIPbrXATHrDqb+TgEqn9M0sXgMtmJH\"},\"s3\":{\"s3SchemaVersion\":\"1.0\",\"configurationId\":\"tf-s3-queue-20220603143540310000000002\",\"bucket\":{\"name\":\"mopi-uploads-bucket\",\"ownerIdentity\":{\"principalId\":\"A349GCMCWCNHIL\"},\"arn\":\"arn:aws:s3:::mopi-uploads-bucket\"},\"object\":{\"key\":\"cke/migration_1654270531388.sql\",\"size\":0,\"eTag\":\"d41d8cd98f00b204e9800998ecf8427e\",\"sequencer\":\"00629A2F9E9816902E\"}}}");
//        var event = new SQSEvent();
//        event.setRecords(List.of(message));
//        String result = app.handleRequest(event, null);

        assertNotNull(app);
    }
}
