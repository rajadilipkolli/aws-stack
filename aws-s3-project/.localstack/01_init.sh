#!/bin/bash
awslocal s3 mb s3://testbucket
echo "List of S3 buckets:"
echo "-------------------------------"
awslocal s3 ls