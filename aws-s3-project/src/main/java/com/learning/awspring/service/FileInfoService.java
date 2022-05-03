package com.learning.awspring.service;

import java.util.List;

import com.learning.awspring.domain.FileInfo;
import com.learning.awspring.repository.FileInfoRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileInfoService {
    private final FileInfoRepository fileInfoRepository;

    public List<FileInfo> saveFilesInfo(List<FileInfo> fileInfo) {
        log.info("Saving file info: '{}'", fileInfo);
        return fileInfoRepository.saveAll(fileInfo);
      }
    
      public List<FileInfo> findAllFiles() {
        log.info("Retrieving all files info");
        return fileInfoRepository.findAll();
      }
    
      public List<FileInfo> getFileByName(String fileName) {
        log.info("Retrieving file by name '{}'", fileName);
        return fileInfoRepository.findByFileName(fileName);
      }
}
