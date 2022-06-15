package cke_migration;

import cke_migration.checkout_com.CheckoutComMigrationApp;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<SQSEvent, String> {
    LambdaLogger logger;
    AmazonS3 s3client;

    @SneakyThrows
    @Override
    public String handleRequest(SQSEvent event, Context context) {
        initLambda(context);
        logger.log(event.toString());
        var body = event.getRecords().get(0).getBody();

        S3EventNotification.S3EventNotificationRecord record = S3EventNotification.parseJson(body).getRecords().get(0);

        var s3Key = record.getS3().getObject().getKey();
        var s3Bucket = record.getS3().getBucket().getName();
        logger.log("found id: " + s3Bucket + " " + s3Key);

        File tempInputFile = downloadInputFileFromS3(s3client, s3Key, s3Bucket);

        CheckoutComMigrationApp checkoutComMigrationApp = new CheckoutComMigrationApp();
        var output = checkoutComMigrationApp.startMigration(tempInputFile);

        output.forEach(filename -> {
            logger.log("writing file to s3: " + filename);
            var s3UploadKey = String.format("migrations-scripts/%s/%s", LocalDate.now(), filename.substring(filename.lastIndexOf("/") + 1));
            s3client.putObject(s3Bucket, s3UploadKey, new File(filename));
        });
        return "All good";
    }

    private void initLambda(Context context) {
        logger = context.getLogger();
        s3client = AmazonS3ClientBuilder
                .standard()
                .build();
    }

    private File downloadInputFileFromS3(AmazonS3 s3client, String s3Key, String s3Bucket) throws IOException {
        var s3Object = s3client.getObject(s3Bucket, s3Key);
        InputStream in = s3Object.getObjectContent();

        File tmp = File.createTempFile("migrationFile", "");
        Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tmp;
    }

}
