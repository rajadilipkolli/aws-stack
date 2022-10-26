package com.learning.awspring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file_info")
@Entity
public class FileInfo {

    @Id
    @SequenceGenerator(allocationSize = 1, name = "sequenceGenerator")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Integer id;

    private String fileName;
    private String fileUrl;
    private boolean isUploadSuccessFull;

    public FileInfo(String fileName, String fileUrl, boolean isUploadSuccessFull) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.isUploadSuccessFull = isUploadSuccessFull;
    }
}
