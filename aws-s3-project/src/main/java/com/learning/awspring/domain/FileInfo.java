package com.learning.awspring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file_info")
@Entity
public class FileInfo {

  @Id private Integer id;
  private String fileName;
  private String fileUrl;
  private boolean isUploadSuccessFull;

  public FileInfo(String fileName, String fileUrl, boolean isUploadSuccessFull) {
    this.fileName = fileName;
    this.fileUrl = fileUrl;
    this.isUploadSuccessFull = isUploadSuccessFull;
  }  
}
