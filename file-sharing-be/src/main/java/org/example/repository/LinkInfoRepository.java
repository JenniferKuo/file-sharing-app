package org.example.repository;

import org.example.entity.LinkInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkInfoRepository extends MongoRepository<LinkInfo, String> {
    Optional<LinkInfo>  findBySharingLink(String sharingLink);
}

