package com.learning.awspring.repository;

import com.learning.awspring.domain.FileInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Integer> {
    List<FileInfo> findByFileName(String name);

    boolean existsByFileName(String fileName);
}