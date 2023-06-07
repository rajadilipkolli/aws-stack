#!/bin/bash

# Variables
USER_NAME="localstack"
ROLE_NAME="localstack-role"
POLICY_NAME="kinesis-policy"
STREAM_NAME="my-test-stream"
SHARD_COUNT=1

# Create IAM user
awslocal iam create-user --user-name $USER_NAME

# Create IAM role
awslocal iam create-role --role-name $ROLE_NAME --assume-role-policy-document '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "kinesis.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}'

# Create IAM policy
cat > policy.json <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "kinesis:ListShards",
		        "kinesis:SubscribeToShard",
                "kinesis:DescribeStreamSummary",
                "kinesis:DescribeStreamConsumer",
                "kinesis:GetShardIterator",
                "kinesis:GetRecords",
                "kinesis:PutRecords",
                "kinesis:DescribeStream"
            ],
            "Resource": [
                "arn:aws:kinesis:us-east-1:000000000000:*/*/consumer/*:*",
                "arn:aws:kinesis:us-east-1:000000000000:stream/my-test-stream"
            ]
        },
        {
            "Effect": "Allow",
            "Action": "kinesis:DescribeLimits",
            "Resource": "*"
        },
        {
          "Sid": "DynamoDB",
          "Effect": "Allow",
          "Action": [
            "dynamodb:BatchGetItem",
            "dynamodb:BatchWriteItem",
            "dynamodb:PutItem",
            "dynamodb:GetItem",
            "dynamodb:Scan",
            "dynamodb:Query",
            "dynamodb:UpdateItem",
	        "dynamodb:DescribeTable"
          ],
          "Resource": [
            "arn:aws:dynamodb:us-east-1:000000000000:table/spring-stream-metadata",
            "arn:aws:dynamodb:us-east-1:000000000000:table/spring-stream-lock-registry"
          ]
        }
    ]
}
EOF

awslocal iam create-policy --policy-name $POLICY_NAME --policy-document file://policy.json

# Attach IAM policy to the role
awslocal iam attach-role-policy --role-name $ROLE_NAME --policy-arn $(awslocal iam list-policies --query 'Policies[?PolicyName==`kinesis-policy`].Arn' --output text)

# Tag user with role ARN
ROLE_ARN=$(awslocal iam get-role --role-name $ROLE_NAME --query 'Role.Arn' --output text)
awslocal iam tag-user --user-name $USER_NAME --tags Key=Role,Value=$ROLE_ARN

echo "attached role to user to " $ROLE_ARN

# Create Kinesis Data Stream
awslocal kinesis create-stream --stream-name $STREAM_NAME --shard-count $SHARD_COUNT

awslocal dynamodb create-table \
--table-name spring-stream-lock-registry \
--attribute-definitions AttributeName=lockKey,AttributeType=S AttributeName=sortKey,AttributeType=S \
--key-schema AttributeName=lockKey,KeyType=HASH AttributeName=sortKey,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack

awslocal dynamodb create-table \
--table-name spring-stream-metadata \
--attribute-definitions AttributeName=KEY,AttributeType=S \
--key-schema AttributeName=KEY,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack

awslocal dynamodb list-tables
awslocal kinesis list-streams

# Clean up
rm policy.json

echo "Initialized."
