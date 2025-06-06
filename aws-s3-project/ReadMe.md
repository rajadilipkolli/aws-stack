# aws-s3-project (using AWS SDK V2)

AWS S3 (Amazon Web Services Simple Storage Service) is a cloud storage service that allows users to store and retrieve large amounts of data in a highly scalable and secure manner. This project demonstrates integration with AWS S3 using Spring Cloud AWS and AWS SDK V2.

## Features

- **File Upload/Download**: Upload files to S3 and download them directly
- **Pre-Signed URLs**: Generate pre-signed URLs for secure temporary access to S3 objects
- **S3 Bucket Management**: Create buckets and list objects
- **File Metadata**: Track and manage file metadata in a database
- **Server-Side Encryption**: Secure objects with server-side encryption
- **Object Versioning**: Track multiple versions of objects
- **Object Tagging**: Add custom tags to S3 objects for better organization
- **Storage Metrics**: Track and analyze storage usage patterns

## API Endpoints

### File Operations
- `POST /s3/upload` - Upload a file directly to S3
- `POST /s3/upload/signed/` - Upload a file using a pre-signed URL
- `GET /s3/download/{name}` - Download a file directly from S3
- `GET /s3/download/signed/{bucketName}/{name}` - Get a pre-signed URL to download a file
- `GET /s3/view-all` - List all objects in the default S3 bucket
- `GET /s3/view-all-db` - List all file metadata from the database

### Object Tagging
- `POST /s3/tags` - Add or update tags for an object
- `GET /s3/tags/{fileName}` - Get all tags for an object

### Storage Metrics
- `GET /s3/metrics` - Get overall storage metrics
- `GET /s3/metrics/bucket/{bucketName}` - Get metrics for a specific bucket

## Configuration

The application can be configured using the following properties in `application.yml`:

```yaml
application:
  bucket-name: your-bucket-name
  enable-server-side-encryption: true  # Enable/disable server-side encryption
  server-side-encryption-algorithm: AES256  # Encryption algorithm to use
  enable-versioning: true  # Enable/disable object versioning
```

## Run Tests
```
$ ./mvnw clean verify
```

## Run Locally
```
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Using Testcontainers at Development Time
```shell
./mvnw spotless:apply spring-boot:test-run
```

## Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)

