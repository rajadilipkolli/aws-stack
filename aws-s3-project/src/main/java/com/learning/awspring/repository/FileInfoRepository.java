package com.learning.awspring.repository;

import com.learning.awspring.domain.FileInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findByFileName(String name);

    boolean existsByFileName(String fileName);
}
