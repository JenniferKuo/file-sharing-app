package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "files")
public class FileInfo {
    @Id
    private String id;
    private String fileName;
    private Long size;
    private String storagePath;
}
