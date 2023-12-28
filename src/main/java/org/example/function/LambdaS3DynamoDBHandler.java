package org.example.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import org.example.dao.DynamoDBFileStoreDao;
import org.example.domain.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.List;

public class LambdaS3DynamoDBHandler implements RequestHandler<S3Event, String> {
    private static final Logger logger = LoggerFactory.getLogger(LambdaS3DynamoDBHandler.class);

    DynamoDBFileStoreDao dao = DynamoDBFileStoreDao.getInstance();

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            S3Client s3Client = S3Client.builder().build();
            HeadObjectResponse headObject = getHeadObject(s3Client, srcBucket, srcKey);

            logger.info("Successfully retrieved from bucket: " + srcBucket + " with file: " + srcKey
                    + " of type " + headObject.contentType());

            FileStore fileInfo = new FileStore();
            fileInfo.setFileName(srcKey);
            fileInfo.setFileType(headObject.contentType());

            dao.saveFileInfo(fileInfo);

            // Retrieve the data from FileStore
            List<FileStore> fileStores = dao.getAllFilesInfo();
            long filesCount = fileStores.stream().count();
             FileStore latestFile = fileStores.get((int) (filesCount-1));
            logger.info("File Info has retrived and now file count is "+ filesCount
                    + " and Latest FileName :"+ latestFile.getFileName()
                    +" Latest Type: "+latestFile.getFileType());

            return "Successfully retrieved from bucket: " + srcBucket + " with file: " + srcKey
                    + " of type " + headObject.contentType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HeadObjectResponse getHeadObject(S3Client s3Client, String bucket, String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.headObject(headObjectRequest);
    }
}

