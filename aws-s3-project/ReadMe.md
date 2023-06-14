# aws-s3-project (using amazon V2)

AWS S3 (Amazon Web Services Simple Storage Service) is a cloud storage service offered by Amazon Web Services (AWS). It allows users to store and retrieve large amounts of data, such as files, images, and videos, in a highly scalable and secure manner. S3 provides various features, such as access control, data encryption, and automatic data replication across multiple data centers, to ensure data durability and availability. It is commonly used by businesses and organizations for data backup, disaster recovery, and data storage for applications and websites.

### Run tests
`$ ./mvnw clean verify`

### Run locally
```
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Using Testcontainers at Development Time
```
`./mvnw spotless:apply spring-boot:test-run
```