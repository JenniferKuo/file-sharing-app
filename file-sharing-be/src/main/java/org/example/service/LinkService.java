package org.example.service;

import org.example.dto.GenerateLinkResponse;
import org.example.exception.LinkExpiredException;
import org.example.exception.ResourceNotFoundException;
import org.example.entity.FileInfo;
import org.example.entity.LinkInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.example.repository.LinkInfoRepository;
import org.example.repository.FileInfoRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.bson.types.ObjectId;

@Service
public class LinkService {

    @Autowired
    private LinkInfoRepository linkInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Value("${app.backend-url}")
    private String backendUrl;


    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public FileInfo accessLink(String link) {
        LinkInfo linkInfo = linkInfoRepository.findBySharingLink(link)
                .orElseThrow(() -> new ResourceNotFoundException("Link not found"));

        if (linkInfo.isExpired()) {
            throw new LinkExpiredException("Link expired");
        }

        return fileInfoRepository.findById(linkInfo.getFileId())
                .orElseThrow(() -> new ResourceNotFoundException("Associated file not found"));
    }

    public GenerateLinkResponse generateLink(String fileId) {
        // Assuming you have a method to generate a random Base62 string
        String randomString = generateRandomBase62String();
        Date expireTime = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        // Create and save link information in database
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setFileId(fileId);
        linkInfo.setSharingLink(randomString);
        linkInfo.setExpireTime(expireTime);
        linkInfoRepository.save(linkInfo);

        return GenerateLinkResponse.builder()
                .sharingLink(backendUrl + "/api/links/" + randomString)
                .expireTime(expireTime)
                .build();
    }

    public String generateRandomBase62String() {
        ObjectId objectId = new ObjectId();
        return base62Encode(objectId.toByteArray());
    }

    private String base62Encode(byte[] input) {
        StringBuilder base62 = new StringBuilder();

        // Convert the string to a big integer
        BigInteger bigInteger = new BigInteger(1, input);

        while (bigInteger.compareTo(BigInteger.ZERO) > 0) {
            int remainder = bigInteger.mod(BigInteger.valueOf(62)).intValue();
            base62.append(BASE62.charAt(remainder));
            bigInteger = bigInteger.divide(BigInteger.valueOf(62));
        }

        return base62.reverse().toString();
    }
}
