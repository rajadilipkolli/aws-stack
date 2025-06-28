#!/bin/sh
awslocal s3 mb s3://testbucket
echo "List of S3 buckets:"
echo "-------------------------------"
awslocal s3 ls

echo "LocalStack initialized successfully"