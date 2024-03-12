package com.learning.awspring.service;

import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileInfoService {
    private final FileInfoRepository fileInfoRepository;

    public List<FileInfo> findAllFiles() {
        log.info("Retrieving all files info");
        return fileInfoRepository.findAll();
    }
}
