package org.example.service;

import org.example.config.AWSConfig;
import org.example.entity.FileInfo;
import org.example.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    AWSConfig awsConfig;
    @Autowired
    private S3Client s3Client;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    public FileInfo uploadFile(MultipartFile multipartFile) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileKey = uploadFileToS3Bucket(file);
//            String publicUrl = setFilePublicAndGetUrl(fileKey);

            file.delete();  // Clean up temporary file

            return saveFileInfo(multipartFile, fileKey);
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading file to S3", ex);
        }
    }

    private String uploadFileToS3Bucket(File file) {
        String fileKey = UUID.randomUUID().toString();
        String bucketName = awsConfig.getBucketName();

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build(),
                RequestBody.fromFile(file));
        return fileKey;
    }

    private FileInfo saveFileInfo(MultipartFile multipartFile, String fileKey) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(multipartFile.getOriginalFilename());
        fileInfo.setSize(multipartFile.getSize());
        fileInfo.setStoragePath(fileKey);

        // Save fileInfo to MongoDB
        fileInfoRepository.save(fileInfo);
        return fileInfo;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    public InputStreamResource downloadFileFromS3(String fileKey) throws IOException {
        String bucketName = awsConfig.getBucketName();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(s3ObjectStream);
    }
}
