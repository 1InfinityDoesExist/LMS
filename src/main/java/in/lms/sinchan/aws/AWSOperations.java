package in.lms.sinchan.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class AWSOperations {
    @Value("${amazon.aws.access.key}")
    private String accessKey;
    @Value("${amazon.aws.secret.key}")
    private String secretKey;
    @Value("${amazon.aws.bucken.name}")
    private String bucketName;
    @Value("${amazon.aws.region}")
    private String region;

    public String uploadProfilePic(MultipartFile multipartFile, String folderName, String studentId)
                    throws Exception {
        String imageUrl = null;
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(region)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
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
}
