#!/usr/bin/env bash

# Navigate to the directory containing the JAR file
cd /localstackTemp || exit 1

# Set variables
JAR_FILE="aws-lambda-project-0.0.1-SNAPSHOT-aws.jar"
FUNCTION_NAME="localstack-lambda-url-example"
IAM_ROLE_ARN="arn:aws:iam::000000000000:role/lambda-role"

# Function to display an error message and exit
error_exit() {
    echo "Error: $1" >&2
    exit 1
}

# Check if the JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    error_exit "JAR file not found at $JAR_FILE"
fi

# Set environment variables (modify as needed)
ENV_VARS='{"Variables": {"SPRING_DATASOURCE_URL":"jdbc:postgresql://postgresqldb:5432/appdb", "SPRING_DATASOURCE_USERNAME":"appuser", "SPRING_DATASOURCE_PASSWORD":"secret"}}'

# Create Lambda function
awslocal lambda create-function \
    --function-name "$FUNCTION_NAME" \
    --runtime java17 \
    --timeout 10 \
    --handler org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest \
    --role "$IAM_ROLE_ARN" \
    --zip-file "fileb://$JAR_FILE" \
    --environment "$ENV_VARS"

echo -e "Lambda function created successfully!\n"

# Create function URL configuration and capture the output
function_url_response=$(awslocal lambda create-function-url-config --function-name "$FUNCTION_NAME" --auth-type NONE)

# Use jq to extract the FunctionUrl
function_url=$(echo "$function_url_response" | grep -o '"FunctionUrl": "[^"]*' | cut -d'"' -f4)

# Print the extracted FunctionUrl
echo "FunctionUrl: $function_url"
echo -e "Function URL configuration created!\n"

# Wait until the function is active
awslocal lambda wait function-active-v2 --function-name "$FUNCTION_NAME"
echo -e "Lambda function is now active\n"

# Invoke Lambda function or use curl
#awslocal lambda invoke \
#    --function-name "$FUNCTION_NAME" \
#    --payload '{"body": "{\"name\": \"profile\"}" }' \
#    output.txt
#
#echo -e "Lambda function invoked successfully\n"

# Use curl to invoke Lambda function and capture the response
response_from_curl=$(curl -X POST \
    "$function_url" \
    -H 'Content-Type: application/json' \
    -d '{"name": "profile"}')

# Print the response from curl
echo "Response from curl: $response_from_curl"
