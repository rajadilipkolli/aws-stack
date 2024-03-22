package com.learning.awspring.service;

import com.learning.awspring.entities.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileInfoService {

    private static final Logger log = LoggerFactory.getLogger(FileInfoService.class);

    private final FileInfoRepository fileInfoRepository;

    public FileInfoService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    public List<FileInfo> findAllFiles() {
        log.info("Retrieving all files info");
        return fileInfoRepository.findAll();
    }
}
