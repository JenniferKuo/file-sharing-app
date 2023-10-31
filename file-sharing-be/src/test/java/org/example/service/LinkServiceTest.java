package org.example.service;

import org.example.entity.FileInfo;
import org.example.entity.LinkInfo;
import org.example.exception.LinkExpiredException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.FileInfoRepository;
import org.example.repository.LinkInfoRepository;
import org.example.utils.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @InjectMocks
    private LinkService linkService;

    @Mock
    private LinkInfoRepository linkInfoRepository;

    @Mock
    private FileInfoRepository fileInfoRepository;

    @Mock
    private RandomStringGenerator randomStringGenerator;

    @Test
    void accessLink_WhenLinkExistsAndNotExpired_ShouldReturnFileInfo() {
        String link = "testLink";
        String fileId = "testFileId";
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setFileId(fileId);
        linkInfo.setSharingLink(link);
        linkInfo.setExpireTime(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));

        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(fileId);

        when(linkInfoRepository.findBySharingLink(link)).thenReturn(Optional.of(linkInfo));
        when(fileInfoRepository.findById(fileId)).thenReturn(Optional.of(fileInfo));

        FileInfo result = linkService.accessLink(link);

        assertEquals(fileInfo, result);
    }

    @Test
    void accessLink_WhenLinkDoesNotExist_ShouldThrowResourceNotFoundException() {
        String link = "testLink";
        when(linkInfoRepository.findBySharingLink(link)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> linkService.accessLink(link));
    }

    @Test
    void accessLink_WhenLinkIsExpired_ShouldThrowLinkExpiredException() {
        String link = "testLink";
        String fileId = "testFileId";
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setFileId(fileId);
        linkInfo.setSharingLink(link);
        linkInfo.setExpireTime(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));

        when(linkInfoRepository.findBySharingLink(link)).thenReturn(Optional.of(linkInfo));

        assertThrows(LinkExpiredException.class, () -> linkService.accessLink(link));
    }

    @Test
    public void testGenerateLink() {
        LinkInfo linkInfo = new LinkInfo();
        linkInfo.setFileId("123");
        linkInfo.setSharingLink("abc");
        linkInfo.setExpireTime(new Date(System.currentTimeMillis() + 100000));

        when(linkInfoRepository.save(any(LinkInfo.class))).thenReturn(linkInfo);
        when(randomStringGenerator.generateRandomBase62String()).thenReturn("abc");

        LinkInfo result = linkService.generateLink("123");

        assertNotNull(result);
        assertEquals("123", result.getFileId());
        assertEquals("abc", result.getSharingLink());
        assertNotNull(result.getExpireTime());

        verify(linkInfoRepository).save(any(LinkInfo.class));
    }
}
