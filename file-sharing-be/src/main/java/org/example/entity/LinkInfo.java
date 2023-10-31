package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "links")
public class LinkInfo {
    @Id
    private String id;
    private String fileId;
    private String sharingLink;

    @Indexed(name = "ttl_index", expireAfterSeconds = 60)
    private Date expireTime;

    public boolean isExpired() {
        return new Date().after(expireTime);
    }
}
