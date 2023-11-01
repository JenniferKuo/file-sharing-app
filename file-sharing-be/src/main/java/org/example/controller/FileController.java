package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.UploadFileResponse;
import org.example.entity.FileInfo;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Logic to handle file upload
            FileInfo fileInfo = fileService.uploadFile(file);
            UploadFileResponse response = UploadFileResponse.builder()
                    .fileName(fileInfo.getFileName())
                    .id(fileInfo.getId())
                    .size(fileInfo.getSize())
                    .build();
            log.info("Upload file: " + fileInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Upload file failed: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}
