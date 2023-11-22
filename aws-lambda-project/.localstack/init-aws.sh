#!/usr/bin/env bash

# Set the JAR file path
JAR_FILE="target/aws-lambda-project-0.0.1-SNAPSHOT-aws.jar"

# Check if the JAR file exists
if [ -f "$JAR_FILE" ]; then
    # Zip the JAR file
    zip -j function.zip "$JAR_FILE"

    # Create Lambda function
    awslocal lambda create-function \
        --function-name localstack-lambda-url-example \
        --runtime java17 \
        --zip-file fileb://function.zip \
        --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest \
        --role arn:aws:iam::000000000000:role/lambda-role
        
    echo "created lambda function"
    
    awslocal lambda create-function-url-config \
        --function-name localstack-lambda-url-example \
        --auth-type NONE
    
    echo "created function URL"
    
    awslocal lambda invoke --function-name localstack-lambda-url-example \
        --cli-binary-format raw-in-base64-out \
        --payload '{"body": "{\"name\": \"profile\"}" }' output.txt
    
    echo "invoked lambda function"
else
    echo "Error: JAR file not found at $JAR_FILE"
fi
