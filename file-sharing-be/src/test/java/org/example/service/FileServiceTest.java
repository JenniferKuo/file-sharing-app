package org.example.service;

import org.example.config.AWSConfig;
import org.example.entity.FileInfo;
import org.example.repository.FileInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private AWSConfig awsConfig;

    @Mock
    private S3Client s3Client;

    @Mock
    private FileInfoRepository fileInfoRepository;

    @Test
    public void testUploadFile() throws IOException {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());
        FileInfo expectedFileInfo = new FileInfo();
        expectedFileInfo.setFileName("test.txt");
        expectedFileInfo.setSize(9L);

        when(awsConfig.getBucketName()).thenReturn("test-bucket");
        when(fileInfoRepository.save(any(FileInfo.class))).thenReturn(expectedFileInfo);

        FileInfo fileInfo = fileService.uploadFile(mockMultipartFile);

        assertNotNull(fileInfo);
        verify(s3Client, times(1)).putObject((PutObjectRequest) any(PutObjectRequest.class), (RequestBody) any());
        verify(fileInfoRepository, times(1)).save(any(FileInfo.class));
    }

    @Test
    public void testDownloadFileFromS3() throws IOException {
        String fileKey = UUID.randomUUID().toString();
        String bucketName = "test-bucket";
        ByteArrayInputStream bais = new ByteArrayInputStream("test data".getBytes());
        ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(GetObjectResponse.builder().build(), bais);

        when(awsConfig.getBucketName()).thenReturn(bucketName);
        when(s3Client.getObject((GetObjectRequest) any())).thenReturn(responseInputStream);

        InputStreamResource result = fileService.downloadFileFromS3(fileKey);

        assertNotNull(result);
        verify(s3Client, times(1)).getObject((GetObjectRequest) any());
    }
}
