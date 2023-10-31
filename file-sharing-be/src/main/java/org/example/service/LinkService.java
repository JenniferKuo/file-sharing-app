package org.example.service;

import org.example.entity.FileInfo;
import org.example.entity.LinkInfo;
import org.example.exception.LinkExpiredException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.FileInfoRepository;
import org.example.repository.LinkInfoRepository;
import org.example.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class LinkService {

    @Autowired
    private LinkInfoRepository linkInfoRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private RandomStringGenerator randomStringGenerator;

    public FileInfo accessLink(String link) {
        LinkInfo linkInfo = linkInfoRepository.findBySharingLink(link)
                .orElseThrow(() -> new ResourceNotFoundException("Link not found"));

        if (linkInfo.isExpired()) {
            throw new LinkExpiredException("Link expired");
        }

        return fileInfoRepository.findById(linkInfo.getFileId())
                .orElseThrow(() -> new ResourceNotFoundException("Associated file not found"));
    }

    public LinkInfo generateLink(String fileId) {
        // Assuming you have a method to generate a random Base62 string
        String randomString = randomStringGenerator.generateRandomBase62String();
        Date expireTime = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        // Create and save link information in database
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setFileId(fileId);
        linkInfo.setSharingLink(randomString);
        linkInfo.setExpireTime(expireTime);
        linkInfoRepository.save(linkInfo);

        return linkInfo;
    }
}
