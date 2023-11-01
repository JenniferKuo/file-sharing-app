package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.GenerateLinkRequest;
import org.example.dto.GenerateLinkResponse;
import org.example.exception.LinkExpiredException;
import org.example.exception.ResourceNotFoundException;
import org.example.entity.FileInfo;
import org.example.entity.LinkInfo;
import org.example.service.FileService;
import org.example.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@Slf4j
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private FileService fileService;

    @Value("${app.backend-url}")
    private String backendUrl;

    @PostMapping("/generate")
    public ResponseEntity<?> generateLink(@RequestBody GenerateLinkRequest linkRequest) {
        try {
            // Logic to generate link
            LinkInfo linkInfo = linkService.generateLink(linkRequest.getFileId());
            GenerateLinkResponse response = GenerateLinkResponse.builder()
                    .sharingLink(backendUrl + "/api/links/" + linkInfo.getSharingLink())
                    .expireTime(linkInfo.getExpireTime())
                    .build();
            log.info("Generate link: " + linkInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Generate link failed: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{link}")
    public ResponseEntity<?>  accessLink(@PathVariable String link) {
        log.info("Access link: " + link);
        try {
            // Logic to access link
            FileInfo fileInfo = linkService.accessLink(link);
            String fileKey = fileInfo.getStoragePath();
            String fileName = fileInfo.getFileName();
            InputStreamResource resource = fileService.downloadFileFromS3(fileKey);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (ResourceNotFoundException e) {
            log.error("Access link failed: " + e.getMessage());
            return ResponseEntity.status(404).body("Link not found");
        } catch (LinkExpiredException e) {
            log.error("Access link failed: " + e.getMessage());
            return ResponseEntity.status(410).body("Link expired");
        } catch (Exception e) {
            log.error("Access link failed: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}
