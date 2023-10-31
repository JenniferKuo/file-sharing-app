package org.example.controller;

import org.example.dto.GenerateLinkRequest;
import org.example.dto.GenerateLinkResponse;
import org.example.exception.LinkExpiredException;
import org.example.exception.ResourceNotFoundException;
import org.example.entity.FileInfo;
import org.example.entity.LinkInfo;
import org.example.service.FileService;
import org.example.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private FileService fileService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateLink(@RequestBody GenerateLinkRequest linkRequest) {
        try {
            // Logic to generate link
            GenerateLinkResponse response = linkService.generateLink(linkRequest.getFileId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{link}")
    public ResponseEntity<?>  accessLink(@PathVariable String link) {
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
            return ResponseEntity.status(404).body("Link not found");
        } catch (LinkExpiredException e) {
            return ResponseEntity.status(410).body("Link expired");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}