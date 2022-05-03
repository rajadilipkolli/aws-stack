package com.learning.awspring.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
