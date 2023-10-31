package org.example.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Getter
@Setter
@Builder
public class GenerateLinkResponse {

    private String sharingLink;
    private Date expireTime;
}

