package org.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class UploadFileResponse {

    private String id;
    private String fileName;
    private Long size;
}

