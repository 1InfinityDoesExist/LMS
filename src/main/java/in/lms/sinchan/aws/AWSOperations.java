package in.lms.sinchan.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import in.lms.sinchan.exception.BucketDoesNotExistException;
import lombok.extern.slf4j.Slf4j;


/*
 * Reuseability of code is not taken into account
 */
@Component
@Slf4j
public class AWSOperations {
    @Value("${amazon.aws.access.key}")
    private String accessKey;
    @Value("${amazon.aws.secret.key}")
    private String secretKey;
    @Value("${amazon.aws.lms.bucket.name}")
    private String bucketName;
    @Value("${amazon.aws.region}")
    private String region;

    /*
     * Upload profile image in aws
     */
    public String uploadProfilePic(MultipartFile multipartFile, String folderName, String studentId)
                    throws Exception {
        log.info("----Storing student profile image in aws----");
        String imageUrl = null;
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
        if (!s3Client.doesBucketExist(bucketName)) {
            s3Client.createBucket(bucketName);
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                        folderName + "/" + studentId + "/" + generateFileName(multipartFile),
                        multipartfileToFile(multipartFile))
                                        .withCannedAcl(CannedAccessControlList.PublicReadWrite);
        PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);
        imageUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" +
                        putObjectRequest.getKey();
        return imageUrl;
    }

    private File multipartfileToFile(MultipartFile multipartFile) throws Exception {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    private String generateFileName(MultipartFile multipartFile) {
        return new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
    }


    /*
     * Get all profile images
     */
    public List<String> getAllProfileImages(String email, String folderName) throws Exception {
        List<String> profileImageUrl = new ArrayList<>();
        AWSCredentials awsCredential = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredential)).build();
        if (s3Client.doesBucketExist(bucketName)) {
            ListObjectsRequest listObjectsRequest =
                            new ListObjectsRequest().withBucketName(bucketName)
                                            .withPrefix(folderName + "/" + email + "/");
            ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
            for (;;) {
                List<S3ObjectSummary> s3ObjectSummary = objectListing.getObjectSummaries();
                if (s3ObjectSummary.size() < 1) {
                    break;
                }
                s3ObjectSummary.stream().forEach(s -> {
                    if (!s.getKey().endsWith("/")) {
                        profileImageUrl.add("https://" + bucketName + ".s3." + region
                                        + ".amazonaws.com/"
                                        + s.getKey());
                    }
                });
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            }
        } else {
            throw new BucketDoesNotExistException(
                            "Sorry no bucket with name  : " + bucketName + " exist");
        }
        return profileImageUrl;
    }

    public void deleteProfileImage(String email, String imageUrl, String folder) {
        log.info("-----AWSOperations Class, deleteProfileImage method-----");
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
        imageUrl = imageUrl.replace("https://", "");
        DeleteObjectRequest deleteObjectRequest =
                        new DeleteObjectRequest(imageUrl.substring(0, imageUrl.indexOf(".")),
                                        imageUrl.substring(imageUrl.indexOf("/") + 1));
        s3Client.deleteObject(deleteObjectRequest);
    }
}
